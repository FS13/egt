/**
 *
 */
package org.eclipsescout.egt.shared.graph;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.scout.commons.CompareUtility;
import org.eclipse.scout.commons.TypeCastUtility;
import org.eclipsescout.egt.shared.graph.EgtSpeciesCodeType.IEgtSpeciesCode;

/**
 * @author user
 */
public class IndexMapList {

  private IndexMapListElement m_states;
  private IndexMapListElement m_colorStates;
  private List<IEgtSpeciesCode> m_species;

  public class IndexMapListElement {

    private int m_value;
    private int m_speciesBasedValue;
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

    public int getSpeciesBasedValue() {
      return m_speciesBasedValue;
    }

    public void setSpeciesBasedValue(int speciesBasedValue) {
      m_speciesBasedValue = speciesBasedValue;
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
      }
      else {
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
        startingIndex = element.buildStateIndexMapList(numberOfIndividuals, m_species, i, numberOfIndividuals - i, startingIndex, 0);
        m_list.add(element);
      }
    }

    public int buildStateIndexMapList(int numberOfIndividuals, List<IEgtSpeciesCode> species, int numberOfFirstSpecies, int maxNumberOfOtherSpecies, int startingIndex, int speciesBasedIndex) {
      int numberOfSpecies = species.size();
      int power = numberOfIndividuals - 1;
      if (CompareUtility.equals(numberOfIndividuals, 1)) {
        for (int i = 0; i < numberOfSpecies - 1; i++) {
          IndexMapListElement element = new IndexMapListElement();
          if (!CompareUtility.equals(maxNumberOfOtherSpecies, 0)) {
            element.setValue(startingIndex);
            element.setSpeciesBasedValue(speciesBasedIndex + i);
            startingIndex++;
          }
          m_list.add(element);
        }
        if (!CompareUtility.equals(numberOfFirstSpecies, 0)) {
          IndexMapListElement element = new IndexMapListElement();
          element.setValue(startingIndex);
          element.setSpeciesBasedValue(speciesBasedIndex + (numberOfSpecies - 1));
          startingIndex++;
          m_list.add(element);
        }
        return startingIndex;
      }
      else {
        for (int i = 0; i < numberOfSpecies - 1; i++) {
          IndexMapListElement element = new IndexMapListElement();
          if (!CompareUtility.equals(maxNumberOfOtherSpecies, 0)) {
            int newSpeciesBasedIndex = speciesBasedIndex + (i * TypeCastUtility.castValue(Math.pow(numberOfSpecies, power), int.class));
            startingIndex = element.buildStateIndexMapList(numberOfIndividuals - 1, species, numberOfFirstSpecies, maxNumberOfOtherSpecies - 1, startingIndex, newSpeciesBasedIndex);
          }
          m_list.add(element);
        }
        if (!CompareUtility.equals(numberOfFirstSpecies, 0)) {
          IndexMapListElement element = new IndexMapListElement();
          if (!CompareUtility.equals(numberOfFirstSpecies, 0)) {
            int newSpeciesBasedIndex = speciesBasedIndex + ((numberOfSpecies - 1) * TypeCastUtility.castValue(Math.pow(numberOfSpecies, power), int.class));
            startingIndex = element.buildStateIndexMapList(numberOfIndividuals - 1, species, numberOfFirstSpecies - 1, maxNumberOfOtherSpecies, startingIndex, newSpeciesBasedIndex);
          }
          m_list.add(element);
        }
        return startingIndex;
      }
    }

    public int getIndexNumber(int... digits) {
      if (digits.length == 1) {
        return getList().get(digits[0]).getValue();
      }
      else {
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
      }
      else if (!CompareUtility.equals(getList(), null)) {
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

    public List<Integer> getAllStateIndicesForColorStateWithNumberOfFirstSpecies(boolean speciesBased, int... digits) {
      return m_list.get(digits[digits.length - 1]).getAllStateIndicesForColorState(speciesBased, digits);
    }

    public List<IndexStatePair> getAllIndexStatePairs(List<IndexStatePair> list, IEgtSpeciesCode... species) {
      if (!CompareUtility.equals(getList(), null)) {
        IEgtSpeciesCode[] newDigits = new IEgtSpeciesCode[species.length + 1];
        for (int i = 0; i < species.length; i++) {
          newDigits[i] = species[i];
        }
        for (int i = 0; i < getList().size(); i++) {
          list = getList().get(i).getAllIndexStatePairs(list, i, newDigits);
        }
      }
      return list;
    }

    private List<IndexStatePair> getAllIndexStatePairs(List<IndexStatePair> list, int numberOfFirstSpecies, IEgtSpeciesCode... species) {
      if (CompareUtility.equals(getList().size(), 0) && !CompareUtility.equals(getValue(), -1)) {
        IndexStatePair isPair = new IndexStatePair();
        isPair.setIndex(getValue());
        isPair.setNumberOfFirstSpecies(numberOfFirstSpecies);
        for (int i = 0; i < species.length; i++) {
          isPair.addSpecies(species[i]);
        }
        list.add(isPair);
        return list;
      }
      else if (!CompareUtility.equals(getList(), null)) {
        IEgtSpeciesCode[] newDigits = new IEgtSpeciesCode[species.length + 1];
        for (int i = 0; i < species.length; i++) {
          newDigits[i] = species[i];
        }
        for (int i = 0; i < getList().size(); i++) {
          newDigits[newDigits.length - 1] = speciesOfIndex(i);
          list = getList().get(i).getAllIndexStatePairs(list, numberOfFirstSpecies, newDigits);
        }
      }
      return list;
    }

    public List<Integer> getAllStateIndicesForColorState(boolean speciesBased, int... digits) {
      List<Integer> list = new ArrayList<Integer>();
      boolean isEnd = true;
      for (int i = 0; i < digits.length; i++) {
        if (1 <= digits[i]) {
          int[] newDigits = digits.clone();
          newDigits[i] = newDigits[i] - 1;
          list.addAll(m_list.get(i).getAllStateIndicesForColorState(speciesBased, newDigits));
          isEnd = false;
        }
      }
      if (isEnd) {
        if (speciesBased) {
          list.add(m_speciesBasedValue);
        }
        else {
          list.add(m_value);
        }
      }
      return list;
    }
  }

  public class IndexStatePair {

    private int m_index;
    private int m_numberOfFirstSpecies;
    private IEgtSpeciesCode[] m_state;

    public IndexStatePair() {
    }

    public void setIndex(int index) {
      m_index = index;
    }

    public int getIndex() {
      return m_index;
    }

    public void setNumberOfFirstSpecies(int numberOfFirstSpecies) {
      m_numberOfFirstSpecies = numberOfFirstSpecies;
    }

    public int getNumberOfFirstSpecies() {
      return m_numberOfFirstSpecies;
    }

    public void addSpecies(IEgtSpeciesCode species) {
      if (CompareUtility.equals(m_state, null)) {
        m_state = new IEgtSpeciesCode[]{species};
      }
      else {
        IEgtSpeciesCode[] state = new IEgtSpeciesCode[m_state.length + 1];
        for (int i = 0; i < m_state.length; i++) {
          state[i] = m_state[i];
        }
        state[m_state.length] = species;
        m_state = state;
      }
    }

    public IEgtSpeciesCode[] getState() {
      return m_state;
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
      digits[i + 1] = indexOfSpecies(species[i]);
    }
    digits[0] = numberOfFirstSpecies;
    return m_states.getIndexNumber(digits);
  }

  public int indexOfSpecies(IEgtSpeciesCode species) {
    for (int i = 0; i < m_species.size(); i++) {
      if (CompareUtility.equals(species.getId(), m_species.get(i).getId())) {
        return i;
      }
    }
    return -1;
  }

  public IEgtSpeciesCode speciesOfIndex(int i) {
    return m_species.get(i);
  }

  public List<int[]> getAllStates() {
    return m_states.getAllStates(new ArrayList<int[]>());
  }

  public List<int[]> getAllColorStates() {
    return m_colorStates.getAllStates(new ArrayList<int[]>());
  }

  public List<IndexStatePair> getAllIndexStatePairs() {
    return m_states.getAllIndexStatePairs(new ArrayList<IndexStatePair>());
  }

  public List<Integer> getAllStateIndicesForColorState(boolean speciesBased, int... digits) {
    int[] newDigits = new int[digits.length];
    for (int i = 0; i < digits.length - 1; i++) {
      newDigits[i] = digits[i + 1];
    }
    newDigits[digits.length - 1] = digits[0];
    return m_states.getAllStateIndicesForColorStateWithNumberOfFirstSpecies(speciesBased, newDigits);
  }

}
