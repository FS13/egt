package org.eclipse.scout.apps.egt.shared.graph;

import org.eclipse.scout.rt.platform.util.StringUtility;

public enum EgtSpecies {

	BLACK("#000000"), WHITE("#FFFFFF"), RED("#FF0000"), BLUE("#0000FF"), GREEN("#00FF00"), YELLOW("#FFFF00");

	private final String color;
	private final String complementaryColor;

	EgtSpecies(String color) {
		this.color = color;
		String comp = StringUtility.replace(color, "0", "x");
		comp = StringUtility.replace(comp, "F", "0");
		this.complementaryColor = StringUtility.replace(comp, "x", "F");
	}

	public String getColor() {
		return color;
	}

	public String getComplementaryColor() {
		return complementaryColor;
	}

}
