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
public class NumberOfColorList {

  public class NumberOfColor {
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
      if (CompareUtility.equals(noc.getColor().getId(), code.getId())) {
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
