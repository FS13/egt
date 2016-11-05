package org.eclipse.scout.apps.egt.shared.graph;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.scout.rt.platform.util.CompareUtility;

public class IndexMapList {

	private IndexMapListElement m_start;

	public class IndexMapListElement {

		private int m_value;
		private List<IndexMapListElement> m_list;

		public IndexMapListElement() {
			m_value = -1;
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

		public int buildIndexMapList(int numberOfSpecies, int numberOfIndividuals, int startingIndex) {
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
					startingIndex = element.buildIndexMapList(numberOfSpecies - 1, numberOfIndividuals - i, startingIndex);
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
			if (CompareUtility.equals(getList(), null) && !CompareUtility.equals(getValue(), -1)) {
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
	}

	public IndexMapList() {
		m_start = new IndexMapListElement();
	}

	public void buildIndexMapList(int numberOfSpecies, int numberOfIndividuals) {
		m_start.buildIndexMapList(numberOfSpecies, numberOfIndividuals, 0);
	}

	public int getIndexNumber(int... digits) {
		return m_start.getIndexNumber(digits);
	}

	public List<int[]> getAllStates() {
		return m_start.getAllStates(new ArrayList<int[]>());
	}

}
