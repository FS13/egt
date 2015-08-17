/**
 *
 */
package org.eclipsescout.egt.shared.graph;

/**
 * @author user
 */
public enum EgtSpecies {

  BLACK("#000000"),
  WHITE("#FFFFFF"),
  RED("#FF0000"),
  BLUE("#0000FF"),
  GREEN("#00FF00"),
  YELLOW("#FFFF00");

  private final String color;

  EgtSpecies(String color) {
    this.color = color;
  }

  public String getColor() {
    return color;
  }

}
