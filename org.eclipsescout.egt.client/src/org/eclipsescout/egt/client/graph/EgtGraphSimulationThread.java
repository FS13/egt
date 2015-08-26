/**
 *
 */
package org.eclipsescout.egt.client.graph;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.scout.commons.CompareUtility;
import org.eclipse.scout.commons.exception.ProcessingException;
import org.eclipse.scout.rt.client.ClientSessionThreadLocal;
import org.eclipse.scout.rt.client.ClientSyncJob;
import org.eclipse.scout.rt.client.IClientSession;
import org.eclipse.scout.rt.shared.services.common.code.CODES;
import org.eclipse.scout.rt.shared.services.common.code.ICode;
import org.eclipsescout.egt.client.ClientSession;
import org.eclipsescout.egt.shared.graph.EgtGraphVertex;
import org.eclipsescout.egt.shared.graph.EgtGraphWeightedDirectedEdge;
import org.eclipsescout.egt.shared.graph.EgtSpeciesCodeType;
import org.eclipsescout.egt.shared.graph.EgtSpeciesCodeType.IEgtSpeciesCode;
import org.eclipsescout.egt.shared.graph.FitnessOfColorList;
import org.eclipsescout.egt.shared.graph.LifetimeAnalysisOfColorList;
import org.eclipsescout.egt.shared.graph.NumberOfColorList;
import org.eclipsescout.egt.shared.graph.NumberOfColorList.NumberOfColor;

/**
 * @author user
 */
public class EgtGraphSimulationThread extends Thread {

  IClientSession m_clientSession;
  EgtGraphSimulationForm m_simulationForm;
  boolean m_startNew;

  public EgtGraphSimulationThread(IClientSession clienSession, EgtGraphSimulationForm simulationForm, boolean startNew) {
    m_clientSession = clienSession;
    m_simulationForm = simulationForm;
    m_startNew = startNew;
  }

  private class EgtGraphSimulationClientSyncJob extends ClientSyncJob {
    protected EgtGraphWeightedDirectedEdge e;
    protected int t;
    protected IEgtSpeciesCode c;
    protected LifetimeAnalysisOfColorList laocl;

    public EgtGraphSimulationClientSyncJob(String name, IClientSession session, EgtGraphWeightedDirectedEdge edge, IEgtSpeciesCode code, int timeSteps, LifetimeAnalysisOfColorList laocl) {
      super(name, session);
      e = edge;
      t = timeSteps;
      c = code;
      this.laocl = laocl;
    }

  }

  @Override
  public void run() {
    ClientSessionThreadLocal.set(m_clientSession);

    double fitnessSum = 0.0;
    NumberOfColorList nocl = new NumberOfColorList();
    FitnessOfColorList focl = new FitnessOfColorList();
    LifetimeAnalysisOfColorList laocl = new LifetimeAnalysisOfColorList();
    int timeSteps = m_startNew ? 0 : m_simulationForm.getAnalysisBox().getTimeStepsField().getValue();

    new EgtGraphSimulationClientSyncJob("resetTimeStep", ClientSyncJob.getCurrentSession(), null, null, timeSteps, null) {
      @Override
      protected void runVoid(IProgressMonitor monitor) throws Throwable {
        m_simulationForm.getAnalysisBox().getTimeStepsField().setValue(t);
      }
    }.schedule();

    for (ICode<Long> c : CODES.getCodeType(EgtSpeciesCodeType.class).getCodes()) {
      if (!m_simulationForm.getSimulationBox().getFitnessBox().getFitnessColorBoxByCode((IEgtSpeciesCode) c).isVisible()) {
        new EgtGraphSimulationClientSyncJob("clearUnusedFitness", ClientSyncJob.getCurrentSession(), null, (IEgtSpeciesCode) c, 0, null) {
          @Override
          protected void runVoid(IProgressMonitor monitor) throws Throwable {
            m_simulationForm.getSimulationBox().getFitnessBox().getFitnessColorBoxByCode((IEgtSpeciesCode) c).getFitnessField().setValue(null);
            m_simulationForm.getSimulationBox().getFitnessBox().getFitnessColorBoxByCode((IEgtSpeciesCode) c).getScaledFitnessField().setValue(null);
          }
        }.schedule();
      }
      else {
        focl.setFitnessForColor((IEgtSpeciesCode) c, m_simulationForm.getSimulationBox().getFitnessBox().getFitnessColorBoxByCode((IEgtSpeciesCode) c).getFitnessField().getValue());
      }
    }

    for (EgtGraphVertex vertex : m_simulationForm.getGraphDetailFormField().getInnerForm().getGraph().getVertices()) {
      vertex.setOldSpecies(vertex.getSpecies());
      IEgtSpeciesCode c = CODES.getCodeType(EgtSpeciesCodeType.class).getCodeByEnum(vertex.getSpecies());

      double f = focl.getFitnessOfColor(c).getFitness();

      m_simulationForm.getGraphDetailFormField().getInnerForm().getGraph().setChanging(true);
      vertex.setFitness(f);
      m_simulationForm.getGraphDetailFormField().getInnerForm().getGraph().setChanging(false);

      fitnessSum = fitnessSum + f;

      nocl.addOneToColor(c);
      if (m_startNew) {
        laocl.setInitialLifeTimeAnalysisForColor(c, timeSteps, nocl.getNumberOfColor(c).getCount());
      }
      else {
        laocl.setInitialLifeTimeAnalysisForColor(c, timeSteps, m_simulationForm.getAnalysisBox().getLifetimeAnalysisBox().getLifetimeAnalysisColorBoxByCode(c).getMaxNumberOfIndividualsField().getValue(), m_simulationForm.getAnalysisBox().getLifetimeAnalysisBox().getLifetimeAnalysisColorBoxByCode(c).getMinNumberOfIndividualsField().getValue());
      }

    }
    while (!nocl.isStationaryState() && !m_simulationForm.isPaused() && !m_simulationForm.isStopped()) {

      double selectVertex = Math.random() * fitnessSum;
      double findVertexByFitness = 0.0;
      EgtGraphVertex selectedVertex = null;
      for (EgtGraphVertex vertex : m_simulationForm.getGraphDetailFormField().getInnerForm().getGraph().getVertices()) {
        findVertexByFitness = findVertexByFitness + vertex.getFitness();
        if (selectVertex <= findVertexByFitness) {
          selectedVertex = vertex;
          break;
        }
      }
      double selectEdge = Math.random();
      double findEdge = 0.0;
      EgtGraphWeightedDirectedEdge selectedEdge = null;
      for (EgtGraphWeightedDirectedEdge edge : m_simulationForm.getGraphDetailFormField().getInnerForm().getGraph().getEdges()) {
        if (CompareUtility.equals(selectedVertex, edge.getFrom())) {
          findEdge = findEdge + edge.getWeight();
          if (selectEdge <= findEdge) {
            selectedEdge = edge;
            break;
          }
        }
      }
      EgtGraphVertex updateVertex = selectedEdge.getTo();
      IEgtSpeciesCode updateVertexSpeciesBefore = CODES.getCodeType(EgtSpeciesCodeType.class).getCodeByEnum(updateVertex.getSpecies());
      fitnessSum = fitnessSum - focl.getFitnessOfColor(updateVertexSpeciesBefore).getFitness();
      nocl.subOneFromColor(updateVertexSpeciesBefore);

      updateVertex.setOldSpecies(updateVertex.getSpecies());

      updateVertex.setSpecies(selectedVertex.getSpecies());
      IEgtSpeciesCode updateVertexSpeciesAfter = CODES.getCodeType(EgtSpeciesCodeType.class).getCodeByEnum(updateVertex.getSpecies());
      updateVertex.setFitness(focl.getFitnessOfColor(updateVertexSpeciesAfter).getFitness());
      fitnessSum = fitnessSum + focl.getFitnessOfColor(updateVertexSpeciesAfter).getFitness();
      nocl.addOneToColor(updateVertexSpeciesAfter);

      timeSteps++;
      if (CompareUtility.equals(nocl.getNumberOfColor(updateVertexSpeciesBefore), null)) {
        laocl.updateLifeTimeAnalysisTimeStepForColor(updateVertexSpeciesBefore, timeSteps);
        laocl.updateLifeTimeAnalysisNumberOfIndividualsForColor(updateVertexSpeciesBefore, 0);
      }
      for (NumberOfColor noc : nocl.getNumberOfColorList()) {
        laocl.updateLifeTimeAnalysisTimeStepForColor(noc.getColor(), timeSteps);
        laocl.updateLifeTimeAnalysisNumberOfIndividualsForColor(noc.getColor(), noc.getCount());
      }

      new EgtGraphSimulationClientSyncJob("updateGraphAfterStep", ClientSyncJob.getCurrentSession(), selectedEdge, null, timeSteps, laocl) {
        @Override
        protected void runVoid(IProgressMonitor monitor) throws Throwable {
          m_simulationForm.getGraphDetailFormField().getInnerForm().getGraph().setChanging(true);
          m_simulationForm.getGraphDetailFormField().getInnerForm().getGraph().changeVertex(e.getTo());
          try {
            m_simulationForm.getGraphDetailFormField().getInnerForm().populateSimulationUpdateEdge(e);
          }
          catch (ProcessingException e) {
            e.printStackTrace();
          }
          m_simulationForm.getGraphDetailFormField().getInnerForm().getGraph().setChanging(false);
          m_simulationForm.getAnalysisBox().getTimeStepsField().setValue(t);

          for (LifetimeAnalysisOfColorList.LifetimeAnalysisOfColor laoc : laocl.getLifetimeAnalysisOfColorList()) {
            m_simulationForm.getAnalysisBox().getLifetimeAnalysisBox().getLifetimeAnalysisColorBoxByCode(laoc.getColor()).getAliveUntilField().setValue(laoc.getAliveUntil());
            m_simulationForm.getAnalysisBox().getLifetimeAnalysisBox().getLifetimeAnalysisColorBoxByCode(laoc.getColor()).getMaxNumberOfIndividualsField().setValue(laoc.getMax());
            m_simulationForm.getAnalysisBox().getLifetimeAnalysisBox().getLifetimeAnalysisColorBoxByCode(laoc.getColor()).getMinNumberOfIndividualsField().setValue(laoc.getMin());
          }
        }
      }.schedule();

      ClientSession.getEnvironment().getDisplay().asyncExec(null);

      try {
        sleep(1500);
      }
      catch (InterruptedException e) {
        e.printStackTrace();
      }

    }

    if (!m_simulationForm.isPaused() && !m_simulationForm.isStopped()) {

      new EgtGraphSimulationClientSyncJob("updateButtonsAfterSimulation", ClientSyncJob.getCurrentSession(), null, null, timeSteps, null) {
        @Override
        protected void runVoid(IProgressMonitor monitor) throws Throwable {
          m_simulationForm.getSimulationBox().getStartSimulationButton().setEnabled(true);
          m_simulationForm.setPaused(false);
          m_simulationForm.getSimulationBox().getPauseSimulationButton().setEnabled(false);
          m_simulationForm.setStopped(true);
          m_simulationForm.getSimulationBox().getStopSimulationButton().setEnabled(false);
          try {
            m_simulationForm.getGraphDetailFormField().getInnerForm().populateSimulationUpdateEdge(null);
          }
          catch (ProcessingException e) {
            e.printStackTrace();
          }
        }
      }.schedule();
    }
  }
}
