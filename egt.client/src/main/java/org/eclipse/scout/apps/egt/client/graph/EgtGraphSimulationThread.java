package org.eclipse.scout.apps.egt.client.graph;

import org.eclipse.scout.apps.egt.shared.graph.EgtGraphVertex;
import org.eclipse.scout.apps.egt.shared.graph.EgtGraphWeightedDirectedEdge;
import org.eclipse.scout.apps.egt.shared.graph.EgtSpeciesCodeType;
import org.eclipse.scout.apps.egt.shared.graph.EgtSpeciesCodeType.IEgtSpeciesCode;
import org.eclipse.scout.apps.egt.shared.graph.FitnessOfColorList;
import org.eclipse.scout.apps.egt.shared.graph.LifetimeAnalysisOfColorList;
import org.eclipse.scout.apps.egt.shared.graph.NumberOfColorList;
import org.eclipse.scout.apps.egt.shared.graph.NumberOfColorList.NumberOfColor;
import org.eclipse.scout.rt.client.IClientSession;
import org.eclipse.scout.rt.client.context.ClientRunContexts;
import org.eclipse.scout.rt.client.job.ModelJobs;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.exception.ProcessingException;
import org.eclipse.scout.rt.platform.util.CompareUtility;
import org.eclipse.scout.rt.platform.util.concurrent.IRunnable;
import org.eclipse.scout.rt.shared.services.common.code.ICode;

public class EgtGraphSimulationThread extends Thread {

	IClientSession m_clientSession;
	EgtGraphSimulationForm m_simulationForm;
	boolean m_startNew;

	public EgtGraphSimulationThread(IClientSession clienSession, EgtGraphSimulationForm simulationForm, boolean startNew) {
		m_clientSession = clienSession;
		m_simulationForm = simulationForm;
		m_startNew = startNew;
	}

	private class EgtGraphSimulationRunnable implements IRunnable {
		protected EgtGraphWeightedDirectedEdge e;
		protected int t;
		protected IEgtSpeciesCode c;
		protected LifetimeAnalysisOfColorList laocl;

		public EgtGraphSimulationRunnable(EgtGraphWeightedDirectedEdge edge, IEgtSpeciesCode code, int timeSteps, LifetimeAnalysisOfColorList laocl) {
			e = edge;
			t = timeSteps;
			c = code;
			this.laocl = laocl;
		}

		@Override
		public void run() throws Exception {
		}

	}

	@Override
	public void run() {

		double fitnessSum = 0.0;
		NumberOfColorList nocl = new NumberOfColorList();
		FitnessOfColorList focl = new FitnessOfColorList();
		LifetimeAnalysisOfColorList laocl = new LifetimeAnalysisOfColorList();
		int timeSteps = m_startNew ? 0 : m_simulationForm.getAnalysisBox().getTimeStepsField().getValue();

		ModelJobs.schedule(new EgtGraphSimulationRunnable(null, null, timeSteps, null) {
			@Override
			public void run() throws Exception {
				m_simulationForm.getAnalysisBox().getTimeStepsField().setValue(t);
			}
		}, ModelJobs.newInput(ClientRunContexts.copyCurrent().withSession(m_clientSession, true))
				.withName("resetTimeStep"));

		for (ICode<Long> c : BEANS.get(EgtSpeciesCodeType.class).getCodes()) {
			if (!m_simulationForm.getSimulationBox().getFitnessBox().getFitnessColorBoxByCode((IEgtSpeciesCode) c)
					.isVisible()) {

				ModelJobs.schedule(new EgtGraphSimulationRunnable(null, (IEgtSpeciesCode) c, 0, null) {
					@Override
					public void run() throws Exception {
						m_simulationForm.getSimulationBox().getFitnessBox().getFitnessColorBoxByCode((IEgtSpeciesCode) c)
								.getFitnessField().setValue(null);
						m_simulationForm.getSimulationBox().getFitnessBox().getFitnessColorBoxByCode((IEgtSpeciesCode) c)
								.getScaledFitnessField().setValue(null);
					}
				}, ModelJobs.newInput(ClientRunContexts.copyCurrent().withSession(m_clientSession, true))
						.withName("clearUnusedFitness"));
			} else {
				focl.setFitnessForColor((IEgtSpeciesCode) c, m_simulationForm.getSimulationBox().getFitnessBox()
						.getFitnessColorBoxByCode((IEgtSpeciesCode) c).getFitnessField().getValue());
			}
		}

		for (EgtGraphVertex vertex : m_simulationForm.getGraphDetailFormField().getInnerForm().getGraph().getVertices()) {
			vertex.setOldSpecies(vertex.getSpecies());
			IEgtSpeciesCode c = BEANS.get(EgtSpeciesCodeType.class).getCodeByEnum(vertex.getSpecies());

			double f = focl.getFitnessOfColor(c).getFitness();

			m_simulationForm.getGraphDetailFormField().getInnerForm().getGraph().setChanging(true);
			vertex.setFitness(f);
			m_simulationForm.getGraphDetailFormField().getInnerForm().getGraph().setChanging(false);

			fitnessSum = fitnessSum + f;

			nocl.addOneToColor(c);
			if (m_startNew) {
				laocl.setInitialLifeTimeAnalysisForColor(c, timeSteps, nocl.getNumberOfColor(c).getCount());
			} else {
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
			for (EgtGraphWeightedDirectedEdge edge : m_simulationForm.getGraphDetailFormField().getInnerForm().getGraph()
					.getEdges()) {
				if (CompareUtility.equals(selectedVertex, edge.getFrom())) {
					findEdge = findEdge + edge.getWeight();
					if (selectEdge <= findEdge) {
						selectedEdge = edge;
						break;
					}
				}
			}
			EgtGraphVertex updateVertex = selectedEdge.getTo();
			IEgtSpeciesCode updateVertexSpeciesBefore = BEANS.get(EgtSpeciesCodeType.class)
					.getCodeByEnum(updateVertex.getSpecies());
			fitnessSum = fitnessSum - focl.getFitnessOfColor(updateVertexSpeciesBefore).getFitness();
			nocl.subOneFromColor(updateVertexSpeciesBefore);

			updateVertex.setOldSpecies(updateVertex.getSpecies());

			updateVertex.setSpecies(selectedVertex.getSpecies());
			IEgtSpeciesCode updateVertexSpeciesAfter = BEANS.get(EgtSpeciesCodeType.class)
					.getCodeByEnum(updateVertex.getSpecies());
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

			ModelJobs.schedule(new EgtGraphSimulationRunnable(selectedEdge, null, timeSteps, laocl) {
				@Override
				public void run() throws Exception {
					m_simulationForm.getGraphDetailFormField().getInnerForm().getGraph().setChanging(true);
					m_simulationForm.getGraphDetailFormField().getInnerForm().getGraph().changeVertex(e.getTo());
					try {
						m_simulationForm.getGraphDetailFormField().getInnerForm().populateSimulationUpdateEdge(e);
					} catch (ProcessingException e) {
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
			}, ModelJobs.newInput(ClientRunContexts.copyCurrent().withSession(m_clientSession, true))
					.withName("updateGraphAfterStep"));

			try {
				sleep(1500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}

		if (!m_simulationForm.isPaused() && !m_simulationForm.isStopped()) {

			ModelJobs.schedule(new EgtGraphSimulationRunnable(null, null, timeSteps, null) {
				@Override
				public void run() throws Exception {
					m_simulationForm.getSimulationBox().getStartSimulationButton().setEnabled(true);
					m_simulationForm.setPaused(false);
					m_simulationForm.getSimulationBox().getPauseSimulationButton().setEnabled(false);
					m_simulationForm.setStopped(true);
					m_simulationForm.getSimulationBox().getStopSimulationButton().setEnabled(false);
					try {
						m_simulationForm.getGraphDetailFormField().getInnerForm().populateSimulationUpdateEdge(null);
					} catch (ProcessingException e) {
						e.printStackTrace();
					}
				}
			}, ModelJobs.newInput(ClientRunContexts.copyCurrent().withSession(m_clientSession, true))
					.withName("updateButtonsAfterSimulation"));
		}
	}

}
