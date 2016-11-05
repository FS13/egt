package org.eclipse.scout.apps.egt.client.graph;

import java.util.List;

import org.eclipse.scout.apps.egt.shared.graph.EgtGraph;
import org.eclipse.scout.apps.egt.shared.graph.EgtGraphVertex;
import org.eclipse.scout.apps.egt.shared.graph.EgtGraphWeightedDirectedEdge;
import org.eclipse.scout.apps.egt.shared.graph.EgtSpeciesCodeType;
import org.eclipse.scout.apps.egt.shared.graph.EgtSpeciesCodeType.IEgtSpeciesCode;
import org.eclipse.scout.apps.egt.shared.graph.FitnessOfColorList;
import org.eclipse.scout.apps.egt.shared.graph.IndexMapList;
import org.eclipse.scout.apps.egt.shared.graph.NumberOfColorList;
import org.eclipse.scout.apps.egt.shared.math.MathUtility;
import org.eclipse.scout.rt.client.IClientSession;
import org.eclipse.scout.rt.client.context.ClientRunContexts;
import org.eclipse.scout.rt.client.job.ModelJobs;
import org.eclipse.scout.rt.client.ui.basic.table.ITableRow;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.util.CompareUtility;
import org.eclipse.scout.rt.platform.util.concurrent.IRunnable;
import org.eclipse.scout.rt.shared.services.common.code.ICode;

import Jama.Matrix;

public class EgtGraphCalculationThread extends Thread {

	IClientSession m_clientSession;
	EgtGraphCalculationForm m_calculationForm;
	List<IEgtSpeciesCode> m_speciesList;

	FitnessOfColorList m_fitnessOfColorList;
	IndexMapList m_indexMapList;
	Matrix m_pi;

	public EgtGraphCalculationThread(IClientSession clienSession, EgtGraphCalculationForm calculationForm, List<IEgtSpeciesCode> speciesList) {
		m_clientSession = clienSession;
		m_calculationForm = calculationForm;
		m_speciesList = speciesList;

		m_fitnessOfColorList = new FitnessOfColorList();
		m_indexMapList = new IndexMapList();
	}

	private class EgtGraphCalculationRunnable implements IRunnable {
		protected Matrix r;
		protected IEgtSpeciesCode c;

		public EgtGraphCalculationRunnable(Matrix rho, IEgtSpeciesCode code) {
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
				ModelJobs.schedule(new EgtGraphCalculationRunnable(null, (IEgtSpeciesCode) c) {
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

		int numberOfSpecies = m_speciesList.size();
		int numberOfIndividuals = m_calculationForm.getGraphDetailFormField().getInnerForm().getGraph().getVertices().size();

		m_indexMapList.buildIndexMapList(numberOfSpecies, numberOfIndividuals);

		int[] firstSpeciesFixationState = new int[numberOfSpecies];
		firstSpeciesFixationState[0] = numberOfIndividuals;
		for (int i = 1; i < firstSpeciesFixationState.length; i++) {
			firstSpeciesFixationState[i] = 0;
		}

		int matrixSize = m_indexMapList.getIndexNumber(firstSpeciesFixationState) + 1;

		m_pi = new Matrix(matrixSize, matrixSize);

		IEgtSpeciesCode[] speciesArray = new IEgtSpeciesCode[m_speciesList.size()];
		for (int i = 0; i < speciesArray.length; i++) {
			speciesArray[i] = m_speciesList.get(i);
		}

		calculateGraph(m_calculationForm.getGraphDetailFormField().getInnerForm().getGraph(), speciesArray);

		int[] firstSpeciesOneState = new int[numberOfSpecies];
		firstSpeciesOneState[0] = 1;
		firstSpeciesOneState[firstSpeciesOneState.length - 1] = numberOfIndividuals - 1;
		for (int i = 1; i < firstSpeciesOneState.length - 1; i++) {
			firstSpeciesFixationState[i] = 0;
		}

		int subMatrixStartIndex = m_indexMapList.getIndexNumber(firstSpeciesOneState);

		List<int[]> states = m_indexMapList.getAllStates();
		for (int i = subMatrixStartIndex; i < m_pi.getRowDimension() - 1; i++) {
			int[] state = states.get(i);
			long binomial = calculateBinomialForState(numberOfSpecies, numberOfIndividuals, state);
			m_pi.setMatrix(i, i, 0, matrixSize - 1, m_pi.getMatrix(i, i, 0, matrixSize - 1).times((double) 1 / binomial));
		}

		Matrix subPi = m_pi.getMatrix(subMatrixStartIndex, matrixSize - 2, subMatrixStartIndex, matrixSize - 2);

		Matrix b = m_pi.getMatrix(subMatrixStartIndex, matrixSize - 2, matrixSize - 1, matrixSize - 1);

		Matrix identity = new Matrix(subPi.getRowDimension(), subPi.getColumnDimension());
		for (int i = 0; i < identity.getRowDimension(); i++) {
			identity.set(i, i, 1);
		}

		Matrix subRho = identity.minus(subPi).solve(b);

		Matrix rho = new Matrix(matrixSize, 1);
		rho.setMatrix(subMatrixStartIndex, matrixSize - 2, 0, 0, subRho);
		rho.set(matrixSize - 1, 0, 1);

		ModelJobs.schedule(new EgtGraphCalculationRunnable(rho, null) {
			@Override
			public void run() throws Exception {
				for (ITableRow row : m_calculationForm.getAnalysisBox().getProbabilityTableField().getTable().getRows()) {
					int[] state = m_calculationForm.getAnalysisBox().getProbabilityTableField().getTable().getStateColumn().getValue(row);
					int stateIndex = m_indexMapList.getIndexNumber(state);
					m_calculationForm.getAnalysisBox().getProbabilityTableField().getTable().getFixationProbabilityColumn().setValue(row, r.get(stateIndex, 0));
					m_calculationForm.getAnalysisBox().getProbabilityTableField().getTable().getExtinctionProbabilityColumn().setValue(row, 1 - r.get(stateIndex, 0));
				}
			}
		}, ModelJobs.newInput(ClientRunContexts.copyCurrent().withSession(m_clientSession, true))
				.withName("transferProbabilities"));

	}

	protected void calculateGraph(EgtGraph graph, IEgtSpeciesCode... codes) {
		calculateFixedUpToVertex(graph, 0, codes);
	}

	protected void calculateFixedUpToVertex(EgtGraph graph, int l, IEgtSpeciesCode... codes) {
		if (!CompareUtility.equals(l, graph.getVertices().size())) {

			for (IEgtSpeciesCode code : codes) {
				graph.getVertices().get(l).setSpecies(code.getEnum());
				calculateFixedUpToVertex(graph, l + 1, codes);
			}
		} else {
			calculateState(graph, codes);
		}
	}

	protected void calculateState(EgtGraph graph, IEgtSpeciesCode... codes) {
		NumberOfColorList nocl = new NumberOfColorList();
		Double fitnessSum = 0.0;
		for (EgtGraphVertex vertex : graph.getVertices()) {
			IEgtSpeciesCode code = BEANS.get(EgtSpeciesCodeType.class).getCodeByEnum(vertex.getSpecies());
			nocl.addOneToColor(code);
			fitnessSum = fitnessSum + m_fitnessOfColorList.getFitnessOfColor(code).getFitness();
		}
		for (EgtGraphWeightedDirectedEdge edge : graph.getEdges()) {
			Double probabilityFrom = m_fitnessOfColorList.getFitnessOfColor(BEANS.get(EgtSpeciesCodeType.class).getCodeByEnum(edge.getFrom().getSpecies())).getFitness() / fitnessSum;
			Double probabilityEdge = edge.getWeight();
			Double probability = probabilityFrom * probabilityEdge;
			IEgtSpeciesCode codeFrom = (IEgtSpeciesCode) BEANS.get(EgtSpeciesCodeType.class).getCodeByEnum(edge.getFrom().getSpecies());
			IEgtSpeciesCode codeTo = (IEgtSpeciesCode) BEANS.get(EgtSpeciesCodeType.class).getCodeByEnum(edge.getTo().getSpecies());
			int fromIndex = 0;
			int toIndex = 0;
			int[] stateFrom = new int[codes.length];
			int[] stateTo = new int[codes.length];
			for (int i = 0; i < codes.length; i++) {
				int from = codes[i].getId() == codeFrom.getId() ? 1 : 0;
				int to = codes[i].getId() == codeTo.getId() ? -1 : 0;
				stateFrom[i] = nocl.getNumberOfColor(codes[i]) == null ? 0 : nocl.getNumberOfColor(codes[i]).getCount();
				stateTo[i] = nocl.getNumberOfColor(codes[i]) == null ? 0 : nocl.getNumberOfColor(codes[i]).getCount() + from + to;
			}
			fromIndex = m_indexMapList.getIndexNumber(stateFrom);
			toIndex = m_indexMapList.getIndexNumber(stateTo);
			m_pi.set(fromIndex, toIndex, m_pi.get(fromIndex, toIndex) + probability);
		}
	}

	protected long calculateBinomialForState(int numberOfSpecies, int numberOfIndividuals, int... state) {
		long binomial = 1;
		for (int i = 0; i < numberOfSpecies; i++) {
			int numberOfSpeciesI = state[i];
			binomial = binomial * MathUtility.binom(numberOfIndividuals, numberOfSpeciesI);
			numberOfIndividuals = numberOfIndividuals - numberOfSpeciesI;
		}
		return binomial;
	}

}
