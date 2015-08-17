/*******************************************************************************
 * Copyright (c) 2010 BSI Business Systems Integration AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BSI Business Systems Integration AG - initial API and implementation
 ******************************************************************************/
package org.eclipsescout.egt.client.graph;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.eclipse.scout.commons.CompareUtility;
import org.eclipse.scout.commons.StringUtility;
import org.eclipse.scout.commons.TypeCastUtility;
import org.eclipse.scout.commons.exception.ProcessingException;
import org.eclipse.scout.svg.client.SVGUtility;
import org.eclipsescout.egt.client.Activator;
import org.eclipsescout.egt.shared.graph.EgtGraph;
import org.eclipsescout.egt.shared.graph.EgtGraphVertex;
import org.eclipsescout.egt.shared.graph.EgtGraphWeightedDirectedEdge;
import org.eclipsescout.egt.shared.graph.EgtSpecies;
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
    SVGDocument document = getDocument(svgText);
    EgtGraph graph = new EgtGraph();
    NodeList circles = document.getElementsByTagName("circle");
    for (int i = 0; i < circles.getLength(); i++) {
      addVertexToGraphFromSvgCircle(graph, (SVGCircleElement) circles.item(i));
    }
    NodeList edges = document.getElementsByTagName("path");
    for (int i = 0; i < edges.getLength(); i++) {
      addEdgeToGraphFromSvgPathAndTSpan(graph, (SVGPathElement) edges.item(i), (SVGTSpanElement) document.getElementById(((SVGPathElement) edges.item(i)).getId() + "-weight"));
    }
    return graph;
  }

  public static void addVertexToGraphFromSvgCircle(EgtGraph graph, SVGCircleElement svgCircleElement) {
    EgtGraphVertex vertex = new EgtGraphVertex(
        TypeCastUtility.castValue(svgCircleElement.getId(), Integer.class),
        TypeCastUtility.castValue(svgCircleElement.getCx().getBaseVal().getValue(), Integer.class),
        TypeCastUtility.castValue(svgCircleElement.getCy().getBaseVal().getValue(), Integer.class),
        TypeCastUtility.castValue(svgCircleElement.getR().getBaseVal().getValue(), Integer.class),
        getSpeciesByColor(svgCircleElement.getAttribute("fill")));
    graph.addVertex(vertex);
  }

  public static void addEdgeToGraphFromSvgPathAndTSpan(EgtGraph graph, SVGPathElement svgPathElement, SVGTSpanElement svgTSpanElement) {
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
    InputStream is = null;

    if (StringUtility.isNullOrEmpty(content)) {
      return null;
    }

    if (content.startsWith("/")) {
      URL url = Activator.getDefault().getBundle().getResource(content);
      is = url.openStream();
    }
    else {
      is = new ByteArrayInputStream(content.getBytes("UTF-8"));
    }

    return SVGUtility.readSVGDocument(is);
  }

}
