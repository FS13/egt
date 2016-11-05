package org.eclipse.scout.apps.egt.client.graph;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.scout.apps.egt.shared.graph.EgtGraphVertex;
import org.eclipse.scout.apps.egt.shared.graph.EgtGraphWeightedDirectedEdge;
import org.eclipse.scout.apps.egt.shared.graph.EgtSpeciesCodeType;
import org.eclipse.scout.apps.egt.shared.graph.EgtSpeciesCodeType.IEgtSpeciesCode;
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

	public EgtGraphSimulationThread(IClientSession clienSession, EgtGraphSimulationForm simulationForm,
			boolean startNew) {
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

	private class NumberOfColorList {

		private class NumberOfColor {
			private IEgtSpeciesCode m_code;
			private int m_count;

			public NumberOfColor(IEgtSpeciesCode code, int count) {
				m_code = code;
				m_count = count;
			}

			public IEgtSpeciesCode getColor() {
				return m_code;
			}

			public int getCount() {
				return m_count;
			}

			public void setCount(int count) {
				m_count = count;
			}
		}

		List<NumberOfColor> m_numberOfColors = new ArrayList<NumberOfColor>();

		public NumberOfColorList() {
		}

		public void addOneToColor(IEgtSpeciesCode code) {
			NumberOfColor noc = getNumberOfColor(code);
			if (CompareUtility.equals(noc, null)) {
				noc = new NumberOfColor(code, 0);
				m_numberOfColors.add(noc);
			}
			noc.setCount(noc.getCount() + 1);
		}

		public void subOneFromColor(IEgtSpeciesCode code) {
			NumberOfColor noc = getNumberOfColor(code);
			if (CompareUtility.equals(noc, null)) {
				noc = new NumberOfColor(code, 0);
			}
			noc.setCount(noc.getCount() - 1);
			if (CompareUtility.equals(noc.getCount(), 0)) {
				m_numberOfColors.remove(noc);
			}
		}

		public boolean removeNumberOfColor(IEgtSpeciesCode code) {
			NumberOfColor noc = getNumberOfColor(code);
			if (!CompareUtility.equals(noc, null)) {
				return m_numberOfColors.remove(noc);
			}
			return false;
		}

		public NumberOfColor getNumberOfColor(IEgtSpeciesCode code) {
			for (NumberOfColor noc : m_numberOfColors) {
				if (CompareUtility.equals(noc.getColor(), code)) {
					return noc;
				}
			}
			return null;
		}

		public List<NumberOfColor> getNumberOfColorList() {
			return m_numberOfColors;
		}

		public boolean isStationaryState() {
			return CompareUtility.isOneOf(m_numberOfColors.size(), 0, 1);
		}

	}

	private class FitnessOfColorList {

		private class FitnessOfColor {
			private IEgtSpeciesCode m_code;
			private double m_fitness;

			public FitnessOfColor(IEgtSpeciesCode code, double fitness) {
				m_code = code;
				m_fitness = fitness;
			}

			public IEgtSpeciesCode getColor() {
				return m_code;
			}

			public double getFitness() {
				return m_fitness;
			}

			public void setFitness(double fitness) {
				m_fitness = fitness;
			}
		}

		List<FitnessOfColor> m_fitnessOfColors = new ArrayList<FitnessOfColor>();

		public FitnessOfColorList() {
		}

		public void setFitnessForColor(IEgtSpeciesCode code, double fitness) {
			FitnessOfColor foc = getFitnessOfColor(code);
			if (CompareUtility.equals(foc, null)) {
				foc = new FitnessOfColor(code, fitness);
				m_fitnessOfColors.add(foc);
			}
			foc.setFitness(fitness);
		}

		public boolean removeNumberOfColor(IEgtSpeciesCode code) {
			FitnessOfColor foc = getFitnessOfColor(code);
			if (!CompareUtility.equals(foc, null)) {
				return m_fitnessOfColors.remove(foc);
			}
			return false;
		}

		public FitnessOfColor getFitnessOfColor(IEgtSpeciesCode code) {
			for (FitnessOfColor foc : m_fitnessOfColors) {
				if (CompareUtility.equals(foc.getColor(), code)) {
					return foc;
				}
			}
			return null;
		}

	}

	private class LifetimeAnalysisOfColorList {

		private class LifetimeAnalysisOfColor {
			private IEgtSpeciesCode m_code;
			private int m_aliveUntil;
			private int m_max;
			private int m_min;

			public LifetimeAnalysisOfColor(IEgtSpeciesCode code) {
				m_code = code;
				m_aliveUntil = 0;
				m_max = 0;
				m_min = 0;
			}

			public IEgtSpeciesCode getColor() {
				return m_code;
			}

			public int getAliveUntil() {
				return m_aliveUntil;
			}

			public void setAliveUntil(int aliveUntil) {
				m_aliveUntil = aliveUntil;
			}

			public int getMax() {
				return m_max;
			}

			public void setMax(int max) {
				m_max = max;
			}

			public int getMin() {
				return m_min;
			}

			public void setMin(int min) {
				m_min = min;
			}
		}

		List<LifetimeAnalysisOfColor> m_lifetimeAnalysisOfColors = new ArrayList<LifetimeAnalysisOfColor>();

		public LifetimeAnalysisOfColorList() {
		}

		public void updateLifeTimeAnalysisForColor(IEgtSpeciesCode code, int timeStep, int numberOfIndividuals) {
			updateLifeTimeAnalysisTimeStepForColor(code, timeStep);
			updateLifeTimeAnalysisNumberOfIndividualsForColor(code, numberOfIndividuals);
		}

		public void updateLifeTimeAnalysisTimeStepForColor(IEgtSpeciesCode code, int timeStep) {
			LifetimeAnalysisOfColor laoc = getLifetimeAnalysisOfColor(code);
			if (CompareUtility.equals(laoc, null)) {
				laoc = new LifetimeAnalysisOfColor(code);
				m_lifetimeAnalysisOfColors.add(laoc);
			}
			if (!CompareUtility.equals(laoc.getMin(), 0)) {
				laoc.setAliveUntil(timeStep);
			}
		}

		public void updateLifeTimeAnalysisNumberOfIndividualsForColor(IEgtSpeciesCode code, int numberOfIndividuals) {
			LifetimeAnalysisOfColor laoc = getLifetimeAnalysisOfColor(code);
			if (CompareUtility.equals(laoc, null)) {
				laoc = new LifetimeAnalysisOfColor(code);
				m_lifetimeAnalysisOfColors.add(laoc);
			}
			laoc.setMax(Math.max(laoc.getMax(), numberOfIndividuals));
			laoc.setMin(Math.min(laoc.getMin(), numberOfIndividuals));
		}

		public void setInitialLifeTimeAnalysisForColor(IEgtSpeciesCode code, int timeStep, int numberOfIndividuals) {
			setInitialLifeTimeAnalysisForColor(code, timeStep, numberOfIndividuals, numberOfIndividuals);
		}

		public void setInitialLifeTimeAnalysisForColor(IEgtSpeciesCode code, int timeStep, int maxNumberOfIndividuals, int minNumberOfIndividuals) {
			LifetimeAnalysisOfColor laoc = getLifetimeAnalysisOfColor(code);
			if (CompareUtility.equals(laoc, null)) {
				laoc = new LifetimeAnalysisOfColor(code);
				m_lifetimeAnalysisOfColors.add(laoc);
			}
			laoc.setAliveUntil(timeStep);
			laoc.setMax(maxNumberOfIndividuals);
			laoc.setMin(minNumberOfIndividuals);
		}

		public boolean removeNumberOfColor(IEgtSpeciesCode code) {
			LifetimeAnalysisOfColor laoc = getLifetimeAnalysisOfColor(code);
			if (!CompareUtility.equals(laoc, null)) {
				return m_lifetimeAnalysisOfColors.remove(laoc);
			}
			return false;
		}

		public LifetimeAnalysisOfColor getLifetimeAnalysisOfColor(IEgtSpeciesCode code) {
			for (LifetimeAnalysisOfColor laoc : m_lifetimeAnalysisOfColors) {
				if (CompareUtility.equals(laoc.getColor(), code)) {
					return laoc;
				}
			}
			return null;
		}

		public List<LifetimeAnalysisOfColor> getLifetimeAnalysisOfColorList() {
			return m_lifetimeAnalysisOfColors;
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
			for (org.eclipse.scout.apps.egt.client.graph.EgtGraphSimulationThread.NumberOfColorList.NumberOfColor noc : nocl.getNumberOfColorList()) {
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
