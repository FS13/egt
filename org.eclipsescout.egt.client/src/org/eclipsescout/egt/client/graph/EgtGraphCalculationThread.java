/**
 *
 */
package org.eclipsescout.egt.client.graph;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.scout.commons.TypeCastUtility;
import org.eclipse.scout.rt.client.ClientSessionThreadLocal;
import org.eclipse.scout.rt.client.ClientSyncJob;
import org.eclipse.scout.rt.client.IClientSession;
import org.eclipse.scout.rt.client.ui.basic.table.ITableRow;
import org.eclipse.scout.rt.shared.services.common.code.CODES;
import org.eclipse.scout.rt.shared.services.common.code.ICode;
import org.eclipsescout.egt.client.ClientSession;
import org.eclipsescout.egt.shared.graph.EgtGraph;
import org.eclipsescout.egt.shared.graph.EgtGraphWeightedDirectedEdge;
import org.eclipsescout.egt.shared.graph.EgtSpeciesCodeType;
import org.eclipsescout.egt.shared.graph.EgtSpeciesCodeType.IEgtSpeciesCode;
import org.eclipsescout.egt.shared.graph.FitnessOfColorList;
import org.eclipsescout.egt.shared.graph.IndexMapList;

import Jama.Matrix;

/**
 * @author user
 */
public class EgtGraphCalculationThread extends Thread {

  IClientSession m_clientSession;
  EgtGraphCalculationForm m_calculationForm;
  EgtGraph m_graph;
  List<IEgtSpeciesCode> m_speciesList;

  boolean m_isComparison;

  FitnessOfColorList m_fitnessOfColorList;
  IndexMapList m_indexMapList;
  String m_pi = "matrixPi";

  public EgtGraphCalculationThread(IClientSession clienSession, EgtGraphCalculationForm calculationForm, EgtGraph graph, List<IEgtSpeciesCode> speciesList, boolean isComparison) {
    m_clientSession = clienSession;
    m_calculationForm = calculationForm;
    m_graph = graph;
    m_speciesList = speciesList;
    m_isComparison = isComparison;

    m_fitnessOfColorList = new FitnessOfColorList();
    m_indexMapList = new IndexMapList();
  }

  private class EgtGraphCalculationClientSyncJob extends ClientSyncJob {
    protected Matrix rA;
    protected Matrix r;
    protected IEgtSpeciesCode c;

    public EgtGraphCalculationClientSyncJob(String name, IClientSession session, Matrix rhoAdded, Matrix rho, IEgtSpeciesCode code) {
      super(name, session);
      rA = rhoAdded;
      r = rho;
      c = code;
    }

  }

  @Override
  public void run() {
    ClientSessionThreadLocal.set(m_clientSession);

    for (ICode<Long> c : CODES.getCodeType(EgtSpeciesCodeType.class).getCodes()) {
      if (!m_calculationForm.getConfigurationBox().getFitnessBox().getFitnessColorBoxByCode((IEgtSpeciesCode) c).isVisible()) {
        new EgtGraphCalculationClientSyncJob("clearUnusedFitness", ClientSyncJob.getCurrentSession(), null, null, (IEgtSpeciesCode) c) {
          @Override
          protected void runVoid(IProgressMonitor monitor) throws Throwable {
            m_calculationForm.getConfigurationBox().getFitnessBox().getFitnessColorBoxByCode((IEgtSpeciesCode) c).getFitnessField().setValue(null);
            m_calculationForm.getConfigurationBox().getFitnessBox().getFitnessColorBoxByCode((IEgtSpeciesCode) c).getScaledFitnessField().setValue(null);
          }
        }.schedule();
      }
      else {
        m_fitnessOfColorList.setFitnessForColor((IEgtSpeciesCode) c, m_calculationForm.getConfigurationBox().getFitnessBox().getFitnessColorBoxByCode((IEgtSpeciesCode) c).getFitnessField().getValue());
      }
    }

    int numberOfIndividuals = m_graph.getVertices().size();

    m_indexMapList.buildIndexMapList(numberOfIndividuals, m_speciesList);

    String statesMatrix = "statesMatrix";
    ClientSession.getMatlabControl().createXCorMatrix(statesMatrix, numberOfIndividuals, m_speciesList.size());

    List<Double> fitnessList = new ArrayList<Double>();
    for (int i = 1; i < m_speciesList.size(); i++) {
      fitnessList.add(m_fitnessOfColorList.getFitnessOfColor(m_speciesList.get(i)).getFitness());
    }
    fitnessList.add(m_fitnessOfColorList.getFitnessOfColor(m_speciesList.get(0)).getFitness());

    String fitnessMatrix = "fitnessMatrix";
    ClientSession.getMatlabControl().createFitnessMatrix(fitnessMatrix, statesMatrix, fitnessList);

    int numberOfStates = ClientSession.getMatlabControl().computeNumberOfStates(statesMatrix);

    ClientSession.getMatlabControl().createSquareMatrix(m_pi, numberOfStates);

    IEgtSpeciesCode[] speciesArray = new IEgtSpeciesCode[m_speciesList.size()];
    for (int i = 0; i < speciesArray.length; i++) {
      speciesArray[i] = m_speciesList.get(i);
    }

    for (EgtGraphWeightedDirectedEdge edge : m_graph.getEdges()) {
      ClientSession.getMatlabControl().computeEdge(m_pi, edge.getFrom().getId(), edge.getTo().getId(), edge.getWeight(), m_speciesList.size(), statesMatrix, fitnessMatrix);
    }

    String useVector = "useVector";
    ClientSession.getMatlabControl().createUseVector(useVector, statesMatrix);

    String subPi = "subPi";
    String b = "b";
    ClientSession.getMatlabControl().createSubMatrixAndVector(subPi, b, m_pi, useVector);

    String subRho = "subRho";
    ClientSession.getMatlabControl().solveForSub(subRho, subPi, b);

    Matrix rhoMatrix = ClientSession.getMatlabControl().buildSolutionVector(subRho, useVector, numberOfStates);

    List<int[]> allColorStates = m_indexMapList.getAllColorStates();
    Matrix rhoAdded = new Matrix(allColorStates.size(), 1);
    for (int[] state : allColorStates) {
      int stateIndex = m_indexMapList.getColorStateIndexNumber(state);
      List<Integer> allStateIndicesForColorState = m_indexMapList.getAllStateIndicesForColorState(true, state);
      for (int index : allStateIndicesForColorState) {
        rhoAdded.set(stateIndex, 0, rhoAdded.get(stateIndex, 0) + rhoMatrix.get(index, 0));
      }
      rhoAdded.set(stateIndex, 0, rhoAdded.get(stateIndex, 0) / allStateIndicesForColorState.size());
    }

    new EgtGraphCalculationClientSyncJob("transferProbabilities", ClientSyncJob.getCurrentSession(), rhoAdded, rhoMatrix, null) {
      @Override
      protected void runVoid(IProgressMonitor monitor) throws Throwable {
        for (ITableRow row : m_calculationForm.getAnalysisBox().getProbabilityTableField().getTable().getRows()) {
          int[] state = m_calculationForm.getAnalysisBox().getProbabilityTableField().getTable().getStateColumn().getValue(row);
          int stateIndex = m_indexMapList.getColorStateIndexNumber(state);
          List<Integer> allStateIndices = m_indexMapList.getAllStateIndicesForColorState(true, state);
          Matrix probabilities = r.getMatrix(TypeCastUtility.castValue(allStateIndices.toArray(), int[].class), 0, 0);
          if (!m_isComparison) {
            m_calculationForm.getAnalysisBox().getProbabilityTableField().getTable().getProbabilitiesColumn().setValue(row, probabilities);
            m_calculationForm.getAnalysisBox().getProbabilityTableField().getTable().getFixationProbabilityColumn().setValue(row, rA.get(stateIndex, 0));
            m_calculationForm.getAnalysisBox().getProbabilityTableField().getTable().getExtinctionProbabilityColumn().setValue(row, 1 - rA.get(stateIndex, 0));
          }
          else {
            m_calculationForm.getAnalysisBox().getProbabilityTableField().getTable().getComparisonProbabilitiesColumn().setValue(row, probabilities);
            m_calculationForm.getAnalysisBox().getProbabilityTableField().getTable().getComparisonFixationProbabilityColumn().setValue(row, rA.get(stateIndex, 0));
            m_calculationForm.getAnalysisBox().getProbabilityTableField().getTable().getComparisonExtinctionProbabilityColumn().setValue(row, 1 - rA.get(stateIndex, 0));
          }
        }
        if (!m_isComparison) {
          m_calculationForm.setCalculated(true);
        }
        else {
          m_calculationForm.setCompared(true);
        }
      }
    }.schedule();

    ClientSession.getMatlabControl().resetSession();

    ClientSession.getEnvironment().getDisplay().asyncExec(null);

  }
}
