/**
 *
 */
package org.eclipsescout.egt.shared.graph;

import org.eclipse.scout.commons.CompareUtility;

/**
 * @author user
 */
public class EgtGraphVertex {

  private int m_id;
  private int m_x;
  private int m_y;
  private int m_radius;
  private EgtSpecies m_species;
  private double m_fitness;

  private String m_svgText;

  public EgtGraphVertex(int id, int x, int y, int radius) {
    this(id, x, y, radius, EgtSpecies.WHITE);
  }

  public EgtGraphVertex(int id, int x, int y, int radius, EgtSpecies species) {
    m_id = id;
    m_x = x;
    m_y = y;
    m_radius = radius;
    m_species = species;
    updateSvgText();

  }

  public int getId() {
    return m_id;
  }

  public int getX() {
    return m_x;
  }

  public void setX(int x) {
    boolean changed = !CompareUtility.equals(m_x, x);
    m_x = x;
    if (changed) {
      updateSvgText();
    }
  }

  public int getY() {
    return m_y;
  }

  public void setY(int y) {
    boolean changed = !CompareUtility.equals(m_y, y);
    m_y = y;
    if (changed) {
      updateSvgText();
    }
  }

  public int getRadius() {
    return m_radius;
  }

  public void setRadius(int radius) {
    boolean changed = !CompareUtility.equals(m_radius, radius);
    m_radius = radius;
    if (changed) {
      updateSvgText();
    }
  }

  public EgtSpecies getSpecies() {
    return m_species;
  }

  public void setSpecies(EgtSpecies species) {
    boolean changed = !CompareUtility.equals(m_species, species);
    m_species = species;
    if (changed) {
      updateSvgText();
    }
  }

  public double getFitness() {
    return m_fitness;
  }

  public void setFitness(double fitness) {
    m_fitness = fitness;
  }

  public String getSvgText() {
    return m_svgText;
  }

  public void updateSvgText() {
    m_svgText = buildSvgText();
  }

  private String buildSvgText() {
    return "<g>"
        + "  <circle "
        + "id= \"" + getId() + "\" "
        + "cx=\"" + getX() + "\" "
        + "cy=\"" + getY() + "\" "
        + "r=\"" + getRadius() + "\" "
        + "stroke=\"#000000\" "
        + "stroke-width=\"" + (getRadius() * 0.1) + "\" "
        + "fill=\"" + getSpecies().getColor() + "\" "
        + "/>" + "\n"
        + "  <text x=\"" + getX() + "\" y=\"" + (getY() + (getRadius() * 0.3)) + "\" style=\"font-family: Arial; font-size: " + (getRadius() * 0.8) + "; " + (getSpecies() == EgtSpecies.BLACK ? "fill: #FFFFFF; " : "") + "text-anchor: middle\">" + getId() + "</text>" + "\n"
        + "</g>";
  }

}
