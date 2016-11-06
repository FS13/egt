package org.eclipse.scout.apps.egt.client.graph;

import java.util.List;

import org.eclipse.scout.apps.egt.shared.graph.EgtGraph;
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
	EgtGraph m_graph;
	List<IEgtSpeciesCode> m_speciesList;

	boolean m_isComparison;

	FitnessOfColorList m_fitnessOfColorList;
	IndexMapList m_indexMapList;
	Matrix m_pi;

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

		IEgtSpeciesCode[] firstSpeciesFixationState = new IEgtSpeciesCode[numberOfIndividuals];
		for (int i = 0; i < firstSpeciesFixationState.length; i++) {
			firstSpeciesFixationState[i] = m_speciesList.get(0);
		}

		int matrixSize = m_indexMapList.getStateIndexNumber(firstSpeciesFixationState) + 1;

		m_pi = new Matrix(matrixSize, matrixSize);

		IEgtSpeciesCode[] speciesArray = new IEgtSpeciesCode[m_speciesList.size()];
		for (int i = 0; i < speciesArray.length; i++) {
			speciesArray[i] = m_speciesList.get(i);
		}

		calculateGraph(m_graph, speciesArray);

		IEgtSpeciesCode[] firstSpeciesOneState = new IEgtSpeciesCode[numberOfIndividuals];
		firstSpeciesOneState[firstSpeciesOneState.length - 1] = m_speciesList.get(0);
		for (int i = 0; i < firstSpeciesOneState.length - 1; i++) {
			firstSpeciesOneState[i] = m_speciesList.get(1);
		}

		int subMatrixStartIndex = m_indexMapList.getStateIndexNumber(firstSpeciesOneState);

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

		List<int[]> allColorStates = m_indexMapList.getAllColorStates();
		Matrix rhoAdded = new Matrix(allColorStates.size(), 1);
		for (int[] state : allColorStates) {
			int stateIndex = m_indexMapList.getColorStateIndexNumber(state);
			List<Integer> allStateIndicesForColorState = m_indexMapList.getAllStateIndicesForColorState(state);
			for (int index : allStateIndicesForColorState) {
				rhoAdded.set(stateIndex, 0, rhoAdded.get(stateIndex, 0) + rho.get(index, 0));
			}
			rhoAdded.set(stateIndex, 0, rhoAdded.get(stateIndex, 0) / allStateIndicesForColorState.size());
		}

		ModelJobs.schedule(new EgtGraphCalculationRunnable(rhoAdded, rho, null) {
			@Override
			public void run() throws Exception {
				for (ITableRow row : m_calculationForm.getAnalysisBox().getProbabilityTableField().getTable().getRows()) {
					int[] state = m_calculationForm.getAnalysisBox().getProbabilityTableField().getTable().getStateColumn().getValue(row);
					int stateIndex = m_indexMapList.getColorStateIndexNumber(state);
					List<Integer> allStateIndices = m_indexMapList.getAllStateIndicesForColorState(state);
					int firstStateIndex = allStateIndices.get(0);
					int lastStateIndex = allStateIndices.get(allStateIndices.size() - 1);
					Matrix probabilities = r.getMatrix(firstStateIndex, lastStateIndex, 0, 0);
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
		IEgtSpeciesCode[] stateFrom = new IEgtSpeciesCode[graph.getVertices().size()];
		for (int i = 0; i < graph.getVertices().size(); i++) {
			IEgtSpeciesCode code = BEANS.get(EgtSpeciesCodeType.class).getCodeByEnum(graph.getVertices().get(i).getSpecies());
			nocl.addOneToColor(code);
			fitnessSum = fitnessSum + m_fitnessOfColorList.getFitnessOfColor(code).getFitness();
			stateFrom[i] = code;
		}
		int fromIndex = m_indexMapList.getStateIndexNumber(stateFrom);
		for (EgtGraphWeightedDirectedEdge edge : graph.getEdges()) {
			IEgtSpeciesCode[] stateTo = stateFrom.clone();
			Double probabilityFrom = m_fitnessOfColorList.getFitnessOfColor(BEANS.get(EgtSpeciesCodeType.class).getCodeByEnum(edge.getFrom().getSpecies())).getFitness() / fitnessSum;
			Double probabilityEdge = edge.getWeight();
			Double probability = probabilityFrom * probabilityEdge;
			stateTo[graph.getVertices().indexOf(edge.getTo())] = (IEgtSpeciesCode) BEANS.get(EgtSpeciesCodeType.class).getCodeByEnum(edge.getFrom().getSpecies());
			int toIndex = m_indexMapList.getStateIndexNumber(stateTo);
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
