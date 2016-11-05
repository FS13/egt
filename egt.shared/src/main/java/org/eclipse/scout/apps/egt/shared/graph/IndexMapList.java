package org.eclipse.scout.apps.egt.shared.graph;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.scout.apps.egt.shared.graph.EgtSpeciesCodeType.IEgtSpeciesCode;
import org.eclipse.scout.rt.platform.util.CompareUtility;

public class IndexMapList {

	private IndexMapListElement m_states;
	private IndexMapListElement m_colorStates;
	private List<IEgtSpeciesCode> m_species;

	public class IndexMapListElement {

		private int m_value;
		private List<IndexMapListElement> m_list;

		public IndexMapListElement() {
			m_value = -1;
			m_list = new ArrayList<IndexMapListElement>();
		}

		public int getValue() {
			return m_value;
		}

		public void setValue(int value) {
			m_value = value;
		}

		public List<IndexMapListElement> getList() {
			return m_list;
		}

		public int buildColorStateIndexMapList(int numberOfSpecies, int numberOfIndividuals, int startingIndex) {
			m_list = new ArrayList<IndexMapListElement>();
			if (CompareUtility.equals(numberOfSpecies, 1)) {
				for (int i = 0; i <= numberOfIndividuals; i++) {
					IndexMapListElement element = new IndexMapListElement();
					element.setValue(i == numberOfIndividuals ? startingIndex : -1);
					m_list.add(element);
				}
				return startingIndex + 1;
			} else {
				for (int i = 0; i <= numberOfIndividuals; i++) {
					IndexMapListElement element = new IndexMapListElement();
					startingIndex = element.buildColorStateIndexMapList(numberOfSpecies - 1, numberOfIndividuals - i, startingIndex);
					m_list.add(element);
				}
				return startingIndex;
			}
		}

		public void buildStateIndexMapList(int numberOfIndividuals, List<IEgtSpeciesCode> species) {
			m_list = new ArrayList<IndexMapListElement>();
			int startingIndex = 0;
			for (int i = 0; i <= numberOfIndividuals; i++) {
				IndexMapListElement element = new IndexMapListElement();
				startingIndex = element.buildStateIndexMapList(numberOfIndividuals, m_species, i, numberOfIndividuals - i, startingIndex);
				m_list.add(element);
			}
		}

		public int buildStateIndexMapList(int numberOfIndividuals, List<IEgtSpeciesCode> species, int numberOfFirstSpecies, int maxNumberOfOtherSpecies, int startingIndex) {
			int numberOfSpecies = species.size();
			if (CompareUtility.equals(numberOfIndividuals, 1)) {
				for (int i = 0; i < numberOfSpecies - 1; i++) {
					IndexMapListElement element = new IndexMapListElement();
					if (!CompareUtility.equals(maxNumberOfOtherSpecies, 0)) {
						element.setValue(startingIndex);
						startingIndex++;
					}
					m_list.add(element);
				}
				if (!CompareUtility.equals(numberOfFirstSpecies, 0)) {
					IndexMapListElement element = new IndexMapListElement();
					element.setValue(startingIndex);
					startingIndex++;
					m_list.add(element);
				}
				return startingIndex;
			} else {
				for (int i = 0; i < numberOfSpecies - 1; i++) {
					IndexMapListElement element = new IndexMapListElement();
					if (!CompareUtility.equals(maxNumberOfOtherSpecies, 0)) {
						startingIndex = element.buildStateIndexMapList(numberOfIndividuals - 1, species, numberOfFirstSpecies, maxNumberOfOtherSpecies - 1, startingIndex);
					}
					m_list.add(element);
				}
				if (!CompareUtility.equals(numberOfFirstSpecies, 0)) {
					IndexMapListElement element = new IndexMapListElement();
					if (!CompareUtility.equals(numberOfFirstSpecies, 0)) {
						startingIndex = element.buildStateIndexMapList(numberOfIndividuals - 1, species, numberOfFirstSpecies - 1, maxNumberOfOtherSpecies, startingIndex);
					}
					m_list.add(element);
				}
				return startingIndex;
			}
		}

		public int getIndexNumber(int... digits) {
			if (digits.length == 1) {
				return getList().get(digits[0]).getValue();
			} else {
				int[] newDigits = new int[digits.length - 1];
				for (int i = 0; i < newDigits.length; i++) {
					newDigits[i] = digits[i + 1];
				}
				return getList().get(digits[0]).getIndexNumber(newDigits);
			}
		}

		public List<int[]> getAllStates(List<int[]> list, int... digits) {
			if (CompareUtility.equals(getList().size(), 0) && !CompareUtility.equals(getValue(), -1)) {
				list.add(digits);
				return list;
			} else if (!CompareUtility.equals(getList(), null)) {
				int[] newDigits = new int[digits.length + 1];
				for (int i = 0; i < digits.length; i++) {
					newDigits[i] = digits[i];
				}
				for (int i = 0; i < getList().size(); i++) {
					newDigits[newDigits.length - 1] = i;
					list = getList().get(i).getAllStates(list, newDigits);
				}
			}
			return list;
		}

		public List<Integer> getAllStateIndicesForColorStateWithNumberOfFirstSpecies(int... digits) {
			return m_list.get(digits[digits.length - 1]).getAllStateIndicesForColorState(digits);
		}

		public List<Integer> getAllStateIndicesForColorState(int... digits) {
			List<Integer> list = new ArrayList<Integer>();
			boolean isEnd = true;
			for (int i = 0; i < digits.length; i++) {
				if (1 <= digits[i]) {
					int[] newDigits = digits.clone();
					newDigits[i] = newDigits[i] - 1;
					list.addAll(m_list.get(i).getAllStateIndicesForColorState(newDigits));
					isEnd = false;
				}
			}
			if (isEnd) {
				list.add(m_value);
			}
			return list;
		}
	}

	public IndexMapList() {
		m_states = new IndexMapListElement();
		m_colorStates = new IndexMapListElement();
	}

	public void buildIndexMapList(int numberOfIndividuals, List<IEgtSpeciesCode> species) {
		buildStateIndexMapList(numberOfIndividuals, species);
		buildColorStateIndexMapList(species.size(), numberOfIndividuals);
	}

	private void buildStateIndexMapList(int numberOfIndividuals, List<IEgtSpeciesCode> species) {
		m_species = new ArrayList<IEgtSpeciesCode>();
		for (int i = 1; i < species.size(); i++) {
			m_species.add(species.get(i));
		}
		m_species.add(species.get(0));
		m_states.buildStateIndexMapList(numberOfIndividuals, m_species);
	}

	private void buildColorStateIndexMapList(int numberOfSpecies, int numberOfIndividuals) {
		m_colorStates.buildColorStateIndexMapList(numberOfSpecies, numberOfIndividuals, 0);
	}

	public int getColorStateIndexNumber(int... digits) {
		return m_colorStates.getIndexNumber(digits);
	}

	public int getStateIndexNumber(IEgtSpeciesCode... species) {
		int[] digits = new int[species.length + 1];
		int numberOfFirstSpecies = 0;
		for (int i = 0; i < species.length; i++) {
			if (CompareUtility.equals(m_species.get(m_species.size() - 1).getId(), species[i].getId())) {
				numberOfFirstSpecies++;
			}
			digits[i + 1] = speciesIndexOf(species[i]);
		}
		digits[0] = numberOfFirstSpecies;
		return m_states.getIndexNumber(digits);
	}

	public int speciesIndexOf(IEgtSpeciesCode species) {
		for (int i = 0; i < m_species.size(); i++) {
			if (CompareUtility.equals(species.getId(), m_species.get(i).getId())) {
				return i;
			}
		}
		return -1;
	}

	public List<int[]> getAllStates() {
		return m_states.getAllStates(new ArrayList<int[]>());
	}

	public List<int[]> getAllColorStates() {
		return m_colorStates.getAllStates(new ArrayList<int[]>());
	}

	public List<Integer> getAllStateIndicesForColorState(int... digits) {
		int[] newDigits = new int[digits.length];
		for (int i = 0; i < digits.length - 1; i++) {
			newDigits[i] = digits[i + 1];
		}
		newDigits[digits.length - 1] = digits[0];
		return m_states.getAllStateIndicesForColorStateWithNumberOfFirstSpecies(newDigits);
	}

}
