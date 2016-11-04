package org.eclipse.scout.apps.egt.shared.graph;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.scout.rt.platform.util.CompareUtility;

public class EgtGraph {

	private List<EgtGraphVertex> m_vertices;
	private List<EgtGraphWeightedDirectedEdge> m_edges;

	private int m_viewBoxRange;
	private int m_viewBoxStartX;
	private int m_viewBoxStartY;

	private int m_boundary = 30;

	public EgtGraph() {
		m_vertices = new ArrayList<EgtGraphVertex>();
		m_edges = new ArrayList<EgtGraphWeightedDirectedEdge>();
		m_viewBoxRange = 1000;
		m_viewBoxStartX = 0;
		m_viewBoxStartY = 0;
	}

	public EgtGraph(String svgText) {
		buildGraphFromSvgText(svgText);
	}

	public int getViewBoxRange() {
		return m_viewBoxRange;
	}

	public void setViewBoxRange(int range) {
		m_viewBoxRange = range;
	}

	public int getViewBoxStartX() {
		return m_viewBoxStartX;
	}

	public void setViewBoxStartX(int startX) {
		m_viewBoxStartX = startX;
	}

	public int getViewBoxStartY() {
		return m_viewBoxStartY;
	}

	public void setViewBoxStartY(int startY) {
		m_viewBoxStartY = startY;
	}

	public int getBoundary() {
		return m_boundary;
	}

	public void setBoundary(int boundary) {
		m_boundary = boundary;
	}

	public List<EgtGraphVertex> getVertices() {
		return m_vertices;
	}

	public EgtGraphVertex getVertexById(int id) {
		for (EgtGraphVertex vertex : getVertices()) {
			if (CompareUtility.equals(vertex.getId(), id)) {
				return vertex;
			}
		}
		return null;
	}

	public boolean idExists(int id) {
		return !CompareUtility.equals(getVertexById(id), null);
	}

	public void addVertex(EgtGraphVertex vertex) {
		if (!idExists(vertex.getId())) {
			getVertices().add(vertex);
			updateViewBox(vertex);
		}
	}

	public void changeVertex(EgtGraphVertex vertex) {
		EgtGraphVertex v = getVertexById(vertex.getId());
		v.setX(vertex.getX());
		v.setY(vertex.getY());
		v.setRadius(vertex.getRadius());
		v.setSpecies(vertex.getSpecies());
		for (EgtGraphWeightedDirectedEdge edge : getEdgesByFrom(vertex)) {
			edge.updateSvgText();
		}
		for (EgtGraphWeightedDirectedEdge edge : getEdgesByTo(vertex)) {
			edge.updateSvgText();
		}
		updateViewBox(vertex);
	}

	public boolean removeVertex(EgtGraphVertex vertex) {
		boolean removed = false;
		removed = getVertices().remove(vertex);
		if (removed) {
			for (EgtGraphWeightedDirectedEdge edge : getEdges()) {
				if (CompareUtility.equals(edge.getFrom(), vertex) || CompareUtility.equals(edge.getTo(), vertex)) {
					getEdges().remove(edge);
				}
			}
			updateViewBox();
		}
		return removed;
	}

	public List<EgtGraphWeightedDirectedEdge> getEdges() {
		return m_edges;
	}

	public EgtGraphWeightedDirectedEdge getEdgeByFromTo(EgtGraphVertex from, EgtGraphVertex to) {
		for (EgtGraphWeightedDirectedEdge edge : getEdges()) {
			if (CompareUtility.equals(edge.getFrom(), from) && CompareUtility.equals(edge.getTo(), to)) {
				return edge;
			}
		}
		return null;
	}

	public List<EgtGraphWeightedDirectedEdge> getEdgesByFrom(EgtGraphVertex from) {
		List<EgtGraphWeightedDirectedEdge> list = new ArrayList<EgtGraphWeightedDirectedEdge>();
		for (EgtGraphWeightedDirectedEdge edge : getEdges()) {
			if (CompareUtility.equals(edge.getFrom(), from)) {
				list.add(edge);
			}
		}
		return list;
	}

	public List<EgtGraphWeightedDirectedEdge> getEdgesByTo(EgtGraphVertex to) {
		List<EgtGraphWeightedDirectedEdge> list = new ArrayList<EgtGraphWeightedDirectedEdge>();
		for (EgtGraphWeightedDirectedEdge edge : getEdges()) {
			if (CompareUtility.equals(edge.getTo(), to)) {
				list.add(edge);
			}
		}
		return list;
	}

	public void addEdge(EgtGraphWeightedDirectedEdge edge) {
		if (idExists(edge.getFrom().getId()) && idExists(edge.getTo().getId())) {
			getEdges().add(edge);
		}
	}

	public void changeEdge(EgtGraphWeightedDirectedEdge edge) {
		EgtGraphWeightedDirectedEdge e = getEdgeByFromTo(edge.getFrom(), edge.getTo());
		e.setWeight(edge.getWeight());
	}

	public boolean removeEdge(EgtGraphWeightedDirectedEdge edge) {
		return getEdges().remove(edge);
	}

	private void updateViewBox() {
		setViewBoxRange(1000);
		setViewBoxStartX(0);
		setViewBoxStartY(0);
		for (EgtGraphVertex vertex : getVertices()) {
			updateViewBox(vertex);
		}
	}

	private void updateViewBox(EgtGraphVertex vertex) {
		int x = vertex.getX();
		int y = vertex.getY();
		int radiusPlus = 3 * vertex.getRadius();
		setViewBoxStartX(Math.min(getViewBoxStartX(), x - radiusPlus - getBoundary()));
		setViewBoxStartY(Math.min(getViewBoxStartY(), y - radiusPlus - getBoundary()));
		setViewBoxRange(Math.max(getViewBoxRange(), Math.max(x + radiusPlus + getBoundary(), y + radiusPlus + getBoundary())
				- Math.min(getViewBoxStartX(), getViewBoxStartY())));

	}

	public String getSvgString() {
		String svgText = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" + "\n"
				+ "<svg width=\"500\" height=\"500\" viewBox=\"" + getViewBoxStartX() + " " + getViewBoxStartY() + " "
				+ getViewBoxRange() + " " + getViewBoxRange()
				+ "\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" contentScriptType=\"text/ecmascript\" zoomAndPan=\"magnify\" contentStyleType=\"text/css\" version=\"1.1\" xml:space=\"preserve\" preserveAspectRatio=\"xMidYMid meet\">"
				+ "\n"
				+ "  <marker id=\"markerArrow\" markerWidth=\"20\" markerHeight=\"20\" refX=\"0\" refY=\"10\" orient=\"auto\">"
				+ "\n" + "    <path d=\"M0,0 L0,20 L14,10 L0,0\" style=\"fill: #000000;\" />" + "\n" + "  </marker>" + "\n";
		for (EgtGraphWeightedDirectedEdge edge : getEdges()) {
			svgText += edge.getSvgText() + "\n";
		}
		for (EgtGraphVertex vertex : getVertices()) {
			svgText += vertex.getSvgText() + "\n";
		}
		svgText += "</svg>";
		return svgText;
	}

	public void buildGraphFromSvgText(String svgText) {
	}

}
