/**
 *
 */
package org.eclipsescout.egt.shared.graph;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.scout.commons.CompareUtility;
import org.eclipsescout.egt.shared.graph.EgtSpeciesCodeType.IEgtSpeciesCode;

/**
 * @author user
 */
public class FitnessOfColorList {

  public class FitnessOfColor {
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
      if (CompareUtility.equals(foc.getColor().getId(), code.getId())) {
        return foc;
      }
    }
    return null;
  }

}
