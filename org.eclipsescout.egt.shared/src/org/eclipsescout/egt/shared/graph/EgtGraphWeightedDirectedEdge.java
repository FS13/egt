/**
 *
 */
package org.eclipsescout.egt.shared.graph;

import org.eclipse.scout.commons.CompareUtility;

/**
 * @author user
 */
public class EgtGraphWeightedDirectedEdge {

  private EgtGraphVertex m_from;
  private EgtGraphVertex m_to;
  private double m_weight;

  private String m_svgText;

  private boolean m_highlight;

  public EgtGraphWeightedDirectedEdge(EgtGraphVertex from, EgtGraphVertex to, double weight) {
    m_from = from;
    m_to = to;
    m_weight = weight;
    updateSvgText();
  }

  public EgtGraphVertex getFrom() {
    return m_from;
  }

  public EgtGraphVertex getTo() {
    return m_to;
  }

  public double getWeight() {
    return m_weight;
  }

  public void setWeight(double weight) {
    boolean changed = !CompareUtility.equals(m_weight, weight);
    m_weight = weight;
    if (changed) {
      updateSvgText();
    }
  }

  public String getSvgText() {
    return m_svgText;
  }

  public void updateSvgText() {
    m_svgText = buildSvgText();
  }

  public boolean isHighlight() {
    return m_highlight;
  }

  public void setHighlight(boolean highlight) {
    m_highlight = highlight;
  }

  private String buildSvgText() {
    boolean fromTo = CompareUtility.equals(getFrom(), getTo());
    int r = getFrom().getRadius();
    String svgText = "  <path id=\"" + getFrom().getId() + "-" + getTo().getId() + "\"" + "\n";
    if (fromTo) {
      svgText += "         d=\"M" + getFrom().getX() + "," + getFrom().getY() + " " + "\n"
          + "             m" + (r * 0.3) + "," + (r * 0.3) + " " + "\n"
          //          + "             l" + (r * 0.6) + "," + (r * 1.6) + " " + "\n"
          + "             l" + (r * -1.6) + "," + (r * -0.6) + " " + "\n"
          + "             a" + (r * 0.8) + "," + r + " 135 1,1 " + r + "," + (-r) + "\" " + "\n";
    }
    else {
      svgText += "         d=\"M" + getFrom().getX() + "," + getFrom().getY() + " " + "\n"
          + "             L" + ((getFrom().getX() * 0.7 + getTo().getX() * 0.3)) + "," + ((getFrom().getY() * 0.7 + getTo().getY() * 0.3)) + " " + "\n"
          + "             L" + getTo().getX() + "," + getTo().getY() + "\" " + "\n";
    }
    svgText += "   style=\"stroke: #0000cc; stroke-width: 1px; fill: none; marker-" + (fromTo ? "end" : "mid") + ": url(#markerArrow);\" />" + "\n"
        + "  <text x=\"" + (fromTo ? (r * 3.2) : (r * 0.6)) + "\" y=\"0\" style=\"font-family: Arial;\"> " + "\n"
        + "    <textPath xlink:href=\"#" + getFrom().getId() + "-" + getTo().getId() + "\" > " + "\n"
        + "        <tspan id=\"" + getFrom().getId() + "-" + getTo().getId() + "-weight" + "\" dy=\"-10\">" + "\n"
        + "          " + getWeight() + "" + "\n"
        + "        </tspan>" + "\n"
        + "    </textPath>" + "\n"
        + "  </text>" + "\n";

    if (isHighlight()) {
      svgText += "  <defs>"
          + "  <path id=\"" + getFrom().getId() + "-" + getTo().getId() + "motion" + "\"" + "\n";
      if (fromTo) {
        svgText += "         d=\"M" + 0 + "," + 0 + " " + "\n"
            + "             m" + (r * 0.3) + "," + (r * 0.3) + " " + "\n"
            //          + "             l" + (r * 0.6) + "," + (r * 1.6) + " " + "\n"
            + "             l" + (r * -1.6) + "," + (r * -0.6) + " " + "\n"
            + "             a" + (r * 0.8) + "," + r + " 135 1,1 " + r + "," + (-r) + "\" />" + "\n";
      }
      else {
        svgText += "           d=\"M" + 0 + "," + 0 + " " + "\n"
            + "               L" + ((getFrom().getX() * -0.3 + getTo().getX() * 0.3)) + "," + ((getFrom().getY() * -0.3 + getTo().getY() * 0.3)) + " " + "\n"
            + "               L" + (getTo().getX() - getFrom().getX()) + "," + (getTo().getY() - getFrom().getY()) + "\" />" + "\n";
      }
      svgText += "  </defs>" + "\n"
          + "  <circle id=\"" + getFrom().getId() + "motion" + "\" cx=\"" + getFrom().getX() + "\" cy=\"" + getFrom().getY() + "\" r=\"" + (getFrom().getRadius() * 0.2) + "\" stroke=\"#000000\" stroke-width=\"" + (getFrom().getRadius() * 0.02) + "\" fill=\"" + getFrom().getSpecies().getColor() + "\" />" + "\n"
          + "  <animate xlink:href=\"#" + getTo().getId() + "\" attributeName=\"fill\" begin=\"0\" dur=\"0.5\" values=\"" + getTo().getOldSpecies().getColor() + "\" fill=\"freeze\" />" + "\n"
          + "  <animate xlink:href=\"#" + getTo().getId() + "text" + "\" attributeName=\"fill\" begin=\"0\" dur=\"0.5\" values=\"" + (getTo().getOldSpecies() == EgtSpecies.BLACK ? "#FFFFFF" : "#000000") + "\" fill=\"freeze\" />" + "\n"
          + "  <animate xlink:href=\"#" + getFrom().getId() + "\" attributeName=\"fill\" begin=\"0.25\" dur=\"0.25\" values=\"" + getFrom().getSpecies().getColor() + ";" + getFrom().getSpecies().getComplementaryColor() + ";" + getFrom().getSpecies().getColor() + "\" fill=\"freeze\" />" + "\n"
          + "  <animateMotion xlink:href=\"#" + getFrom().getId() + "motion" + "\" begin=\"0.25\" dur=\"0.25\">" + "\n"
          + "        <mpath xlink:href=\"#" + getFrom().getId() + "-" + getTo().getId() + "motion\" />" + "\n"
          + "  </animateMotion>" + "\n"
          + "  <animate xlink:href=\"#" + getTo().getId() + "\" attributeName=\"fill\" begin=\"0.5\" dur=\"0.25\" values=\"#" + getTo().getOldSpecies().getColor() + ";" + getTo().getSpecies().getColor() + "\" fill=\"freeze\" />" + "\n"
          + "  <animate xlink:href=\"#" + getTo().getId() + "text" + "\" attributeName=\"fill\" begin=\"0.5\" dur=\"0.25\" values=\"" + (getTo().getOldSpecies() == EgtSpecies.BLACK ? "#FFFFFF" : "#000000") + ";" + (getTo().getSpecies() == EgtSpecies.BLACK ? "#FFFFFF" : "#000000") + "\" fill=\"freeze\" />" + "\n";
    }

    return svgText;
  }
}
