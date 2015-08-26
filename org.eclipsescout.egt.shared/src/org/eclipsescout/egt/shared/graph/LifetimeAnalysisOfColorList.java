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
public class LifetimeAnalysisOfColorList {

  public class LifetimeAnalysisOfColor {
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
      if (CompareUtility.equals(laoc.getColor().getId(), code.getId())) {
        return laoc;
      }
    }
    return null;
  }

  public List<LifetimeAnalysisOfColor> getLifetimeAnalysisOfColorList() {
    return m_lifetimeAnalysisOfColors;
  }

}
