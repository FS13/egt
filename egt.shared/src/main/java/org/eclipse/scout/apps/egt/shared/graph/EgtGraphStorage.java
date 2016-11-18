package org.eclipse.scout.apps.egt.shared.graph;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.scout.apps.egt.shared.file.EgtGraphFileConfigProperties.EgtGraphsFileProperty;
import org.eclipse.scout.rt.platform.config.CONFIG;
import org.eclipse.scout.rt.platform.util.TypeCastUtility;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public final class EgtGraphStorage {

	private static List<GraphInformation> m_graphs = new ArrayList<>();

	private static String m_graphsFile = CONFIG.getPropertyValue(EgtGraphsFileProperty.class);

	public static class GraphInformation {

		private String m_name;
		private String m_fileName;
		private int m_numberOfVertices;

		public GraphInformation(String name, String fileName, int numberOfVertices) {
			m_name = name;
			m_fileName = fileName;
			m_numberOfVertices = numberOfVertices;
		}

		public String getName() {
			return m_name;
		}

		public String getFileName() {
			return m_fileName;
		}

		public int getNumberOfVertices() {
			return m_numberOfVertices;
		}

	}

	public static List<GraphInformation> getGraphInformation() {
		return m_graphs;
	}

	public static void reloadGraphStorage() {
		getGraphInformation().clear();

		JSONParser parser = new JSONParser();

		try {
			Object obj = parser.parse(new FileReader(m_graphsFile));

			JSONArray jsonArray = (JSONArray) obj;
			for (Object o : jsonArray) {
				JSONObject jsonObject = (JSONObject) o;
				String name = (String) jsonObject.get("name");
				String file = (String) jsonObject.get("file");
				int numberOfVertices = TypeCastUtility.castValue(jsonObject.get("numberOfVertices"), int.class);

				getGraphInformation().add(new GraphInformation(name, file, numberOfVertices));
			}
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public static void storeGraphStorage() {

		JSONArray jsonArray = new JSONArray();
		for (GraphInformation graphInformation : getGraphInformation()) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("name", graphInformation.getName());
			jsonObject.put("file", graphInformation.getFileName());
			jsonObject.put("numberOfVertices", graphInformation.getNumberOfVertices());
			jsonArray.add(jsonObject);
		}

		try {
			FileWriter file = new FileWriter(m_graphsFile);
			file.write(jsonArray.toJSONString());
			file.flush();
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void changeGraphInformation(GraphInformation oldGraphInformation,
			GraphInformation newGraphInformation) {
		if (oldGraphInformation != null) {
			getGraphInformation().remove(oldGraphInformation);
		}
		if (newGraphInformation != null) {
			getGraphInformation().add(newGraphInformation);
		}
		storeGraphStorage();
	}

}
