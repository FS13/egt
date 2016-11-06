package org.eclipse.scout.apps.egt.client.graph;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.scout.apps.egt.client.matlab.MatlabControl;
import org.eclipse.scout.apps.egt.shared.graph.EgtGraph;
import org.eclipse.scout.apps.egt.shared.graph.EgtGraphWeightedDirectedEdge;
import org.eclipse.scout.apps.egt.shared.graph.EgtSpeciesCodeType;
import org.eclipse.scout.apps.egt.shared.graph.EgtSpeciesCodeType.IEgtSpeciesCode;
import org.eclipse.scout.apps.egt.shared.graph.FitnessOfColorList;
import org.eclipse.scout.apps.egt.shared.graph.IndexMapList;
import org.eclipse.scout.rt.client.IClientSession;
import org.eclipse.scout.rt.client.context.ClientRunContexts;
import org.eclipse.scout.rt.client.job.ModelJobs;
import org.eclipse.scout.rt.client.ui.basic.table.ITableRow;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.util.TypeCastUtility;
import org.eclipse.scout.rt.platform.util.concurrent.IRunnable;
import org.eclipse.scout.rt.shared.services.common.code.ICode;

import Jama.Matrix;

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

	private class EgtGraphCalculationRunnable implements IRunnable {
		protected Matrix rA;
		protected Matrix r;
		protected IEgtSpeciesCode c;

		public EgtGraphCalculationRunnable(Matrix rhoAdded, Matrix rho, IEgtSpeciesCode code) {
			rA = rhoAdded;
			r = rho;
			c = code;
		}

		@Override
		public void run() throws Exception {
		}

	}

	@Override
	public void run() {

		for (ICode<Long> c : BEANS.get(EgtSpeciesCodeType.class).getCodes()) {
			if (!m_calculationForm.getConfigurationBox().getFitnessBox().getFitnessColorBoxByCode((IEgtSpeciesCode) c).isVisible()) {
				ModelJobs.schedule(new EgtGraphCalculationRunnable(null, null, (IEgtSpeciesCode) c) {
					@Override
					public void run() throws Exception {
						m_calculationForm.getConfigurationBox().getFitnessBox().getFitnessColorBoxByCode((IEgtSpeciesCode) c).getFitnessField().setValue(null);
						m_calculationForm.getConfigurationBox().getFitnessBox().getFitnessColorBoxByCode((IEgtSpeciesCode) c).getScaledFitnessField().setValue(null);
					}
				}, ModelJobs.newInput(ClientRunContexts.copyCurrent().withSession(m_clientSession, true))
						.withName("clearUnusedFitness"));
			} else {
				m_fitnessOfColorList.setFitnessForColor((IEgtSpeciesCode) c, m_calculationForm.getConfigurationBox().getFitnessBox().getFitnessColorBoxByCode((IEgtSpeciesCode) c).getFitnessField().getValue());
			}
		}

		int numberOfIndividuals = m_graph.getVertices().size();

		m_indexMapList.buildIndexMapList(numberOfIndividuals, m_speciesList);

		String statesMatrix = "statesMatrix";
		MatlabControl.createXCorMatrix(statesMatrix, numberOfIndividuals, m_speciesList.size());

		List<Double> fitnessList = new ArrayList<Double>();
		for (int i = 1; i < m_speciesList.size(); i++) {
			fitnessList.add(m_fitnessOfColorList.getFitnessOfColor(m_speciesList.get(i)).getFitness());
		}

		fitnessList.add(m_fitnessOfColorList.getFitnessOfColor(m_speciesList.get(0)).getFitness());

		String fitnessMatrix = "fitnessMatrix";
		MatlabControl.createFitnessMatrix(fitnessMatrix, statesMatrix, fitnessList);

		int numberOfStates = MatlabControl.computeNumberOfStates(statesMatrix);

		MatlabControl.createSquareMatrix(m_pi, numberOfStates);

		IEgtSpeciesCode[] speciesArray = new IEgtSpeciesCode[m_speciesList.size()];
		for (int i = 0; i < speciesArray.length; i++) {
			speciesArray[i] = m_speciesList.get(i);
		}

		for (EgtGraphWeightedDirectedEdge edge : m_graph.getEdges()) {
			MatlabControl.computeEdge(m_pi, edge.getFrom().getId(), edge.getTo().getId(), edge.getWeight(), m_speciesList.size(), statesMatrix, fitnessMatrix);
		}

		String useVector = "useVector";
		MatlabControl.createUseVector(useVector, statesMatrix);

		String subPi = "subPi";
		String b = "b";
		MatlabControl.createSubMatrixAndVector(subPi, b, m_pi, useVector);

		String subRho = "subRho";
		MatlabControl.solveForSub(subRho, subPi, b);

		Matrix rhoMatrix = MatlabControl.buildSolutionVector(subRho, useVector, numberOfStates);

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

		ModelJobs.schedule(new EgtGraphCalculationRunnable(rhoAdded, rhoMatrix, null) {
			@Override
			public void run() throws Exception {
				for (ITableRow row : m_calculationForm.getAnalysisBox().getProbabilityTableField().getTable().getRows()) {
					int[] state = m_calculationForm.getAnalysisBox().getProbabilityTableField().getTable().getStateColumn().getValue(row);
					int stateIndex = m_indexMapList.getColorStateIndexNumber(state);
					List<Integer> allStateIndices = m_indexMapList.getAllStateIndicesForColorState(true, state);
					Matrix probabilities = r.getMatrix(TypeCastUtility.castValue(allStateIndices.toArray(), int[].class), 0, 0);
					if (!m_isComparison) {
						m_calculationForm.getAnalysisBox().getProbabilityTableField().getTable().getProbabilitiesColumn().setValue(row, probabilities);
						m_calculationForm.getAnalysisBox().getProbabilityTableField().getTable().getFixationProbabilityColumn().setValue(row, rA.get(stateIndex, 0));
						m_calculationForm.getAnalysisBox().getProbabilityTableField().getTable().getExtinctionProbabilityColumn().setValue(row, 1 - rA.get(stateIndex, 0));
					} else {
						m_calculationForm.getAnalysisBox().getProbabilityTableField().getTable().getComparisonProbabilitiesColumn().setValue(row, probabilities);
						m_calculationForm.getAnalysisBox().getProbabilityTableField().getTable().getComparisonFixationProbabilityColumn().setValue(row, rA.get(stateIndex, 0));
						m_calculationForm.getAnalysisBox().getProbabilityTableField().getTable().getComparisonExtinctionProbabilityColumn().setValue(row, 1 - rA.get(stateIndex, 0));

					}
				}
				if (!m_isComparison) {
					m_calculationForm.setCalculated(true);
				} else {
					m_calculationForm.setCompared(true);
				}
			}
		}, ModelJobs.newInput(ClientRunContexts.copyCurrent().withSession(m_clientSession, true))
				.withName("transferProbabilities"));

		MatlabControl.resetSession();

	}

}
