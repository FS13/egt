package org.eclipse.scout.apps.egt.client.graph;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.eclipse.scout.apps.egt.shared.graph.EgtGraph;
import org.eclipse.scout.apps.egt.shared.graph.EgtGraphVertex;
import org.eclipse.scout.apps.egt.shared.graph.EgtGraphWeightedDirectedEdge;
import org.eclipse.scout.apps.egt.shared.graph.EgtSpecies;
import org.eclipse.scout.rt.platform.exception.ProcessingException;
import org.eclipse.scout.rt.platform.util.CompareUtility;
import org.eclipse.scout.rt.platform.util.StringUtility;
import org.eclipse.scout.rt.platform.util.TypeCastUtility;
import org.eclipse.scout.rt.svg.client.SVGUtility;
import org.w3c.dom.NodeList;
import org.w3c.dom.svg.SVGCircleElement;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGMarkerElement;
import org.w3c.dom.svg.SVGPathElement;
import org.w3c.dom.svg.SVGTSpanElement;

public final class GraphUtility {

	private GraphUtility() {
	}

	public static EgtGraph buildGraphFromSvgText(String svgText) throws IOException, ProcessingException {
		if (CompareUtility.equals(svgText, null)) {
			return new EgtGraph();
		}
		SVGDocument document = getDocument(svgText);
		EgtGraph graph = new EgtGraph();
		NodeList circles = document.getElementsByTagName("circle");
		for (int i = 0; i < circles.getLength(); i++) {
			addVertexToGraphFromSvgCircle(graph, (SVGCircleElement) circles.item(i));
		}
		NodeList edges = document.getElementsByTagName("path");
		for (int i = 0; i < edges.getLength(); i++) {
			addEdgeToGraphFromSvgPathAndTSpan(graph, (SVGPathElement) edges.item(i),
					(SVGTSpanElement) document.getElementById(((SVGPathElement) edges.item(i)).getId() + "-weight"));
		}
		return graph;
	}

	public static void addVertexToGraphFromSvgCircle(EgtGraph graph, SVGCircleElement svgCircleElement) {
		EgtGraphVertex vertex = new EgtGraphVertex(TypeCastUtility.castValue(svgCircleElement.getId(), Integer.class),
				TypeCastUtility.castValue(svgCircleElement.getCx().getBaseVal().getValue(), Integer.class),
				TypeCastUtility.castValue(svgCircleElement.getCy().getBaseVal().getValue(), Integer.class),
				TypeCastUtility.castValue(svgCircleElement.getR().getBaseVal().getValue(), Integer.class),
				getSpeciesByColor(svgCircleElement.getAttribute("fill")));
		graph.addVertex(vertex);
	}

	public static void addEdgeToGraphFromSvgPathAndTSpan(EgtGraph graph, SVGPathElement svgPathElement,
			SVGTSpanElement svgTSpanElement) {
		if (!(svgPathElement.getParentNode() instanceof SVGMarkerElement)) {
			String[] fromTo = svgPathElement.getId().split("-");
			EgtGraphWeightedDirectedEdge edge = new EgtGraphWeightedDirectedEdge(
					graph.getVertexById(TypeCastUtility.castValue(fromTo[0], Integer.class)),
					graph.getVertexById(TypeCastUtility.castValue(fromTo[1], Integer.class)),
					TypeCastUtility.castValue(svgTSpanElement.getTextContent().replace("\n", "").trim(), Double.class));
			graph.addEdge(edge);
		}
	}

	public static EgtSpecies getSpeciesByColor(String c) {
		if (CompareUtility.equals(c, EgtSpecies.BLACK.getColor())) {
			return EgtSpecies.BLACK;
		}
		if (CompareUtility.equals(c, EgtSpecies.WHITE.getColor())) {
			return EgtSpecies.WHITE;
		}
		if (CompareUtility.equals(c, EgtSpecies.RED.getColor())) {
			return EgtSpecies.RED;
		}
		if (CompareUtility.equals(c, EgtSpecies.BLUE.getColor())) {
			return EgtSpecies.BLUE;
		}
		if (CompareUtility.equals(c, EgtSpecies.GREEN.getColor())) {
			return EgtSpecies.GREEN;
		}
		if (CompareUtility.equals(c, EgtSpecies.YELLOW.getColor())) {
			return EgtSpecies.YELLOW;
		}
		return null;
	}

	public static SVGDocument getDocument(String content) throws IOException, ProcessingException {
		if (StringUtility.isNullOrEmpty(content)) {
			return null;
		}

		return SVGUtility.readSVGDocument(new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8.name())));
	}

}
