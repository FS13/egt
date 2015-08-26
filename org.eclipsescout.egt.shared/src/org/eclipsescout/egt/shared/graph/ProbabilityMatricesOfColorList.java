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
public class ProbabilityMatricesOfColorList {

  public class ProbabilityMatrixOfColor {
    private IEgtSpeciesCode m_code;
    private Double[][] m_matrix;

    public ProbabilityMatrixOfColor(IEgtSpeciesCode code, int n) {
      m_code = code;
      m_matrix = new Double[n][n];
    }

    public IEgtSpeciesCode getColor() {
      return m_code;
    }

    public Double[][] getMatrix() {
      return m_matrix;
    }

    public void setMatrixValue(double value, int row, int col) {
      m_matrix[row][col] = value;
    }

    public void addMatrixValue(double value, int row, int col) {
      m_matrix[row][col] = m_matrix[row][col] + value;
    }

  }

  List<ProbabilityMatrixOfColor> m_probabilityMatrixOfColors = new ArrayList<ProbabilityMatrixOfColor>();
  private int m_n;

  public ProbabilityMatricesOfColorList(int n) {
    m_n = n;
  }

  public void addProbabilityEntryToColor(IEgtSpeciesCode code, double value, int row, int col) {
    ProbabilityMatrixOfColor pmoc = getProbabilityMatricesOfColor(code);
    if (CompareUtility.equals(pmoc, null)) {
      pmoc = new ProbabilityMatrixOfColor(code, m_n);
    }
    pmoc.addMatrixValue(value, row, col);
  }

  public ProbabilityMatrixOfColor getProbabilityMatricesOfColor(IEgtSpeciesCode code) {
    for (ProbabilityMatrixOfColor pmoc : m_probabilityMatrixOfColors) {
      if (CompareUtility.equals(pmoc.getColor().getId(), code.getId())) {
        return pmoc;
      }
    }
    return null;
  }

  public List<ProbabilityMatrixOfColor> getProbabilityMatricesOfColorList() {
    return m_probabilityMatrixOfColors;
  }

}
