package org.eclipse.scout.apps.egt.client.file;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.eclipse.scout.apps.egt.shared.file.EgtGraphFileConfigProperties.EgtGraphsDirectoryProperty;
import org.eclipse.scout.apps.egt.shared.graph.EgtGraphFormData;
import org.eclipse.scout.apps.egt.shared.graph.EgtGraphStorage;
import org.eclipse.scout.apps.egt.shared.graph.EgtGraphStorage.GraphInformation;
import org.eclipse.scout.apps.egt.shared.graph.IEgtGraphProcessService;
import org.eclipse.scout.rt.platform.config.CONFIG;
import org.eclipse.scout.rt.platform.exception.ProcessingException;
import org.eclipse.scout.rt.platform.util.IOUtility;

public class EgtGraphProcessService implements IEgtGraphProcessService {

	private String m_directory = CONFIG.getPropertyValue(EgtGraphsDirectoryProperty.class) + "\\";

	@Override
	public EgtGraphFormData create(EgtGraphFormData formData) throws ProcessingException {
		String svg = formData.getSvgText();
		String fileName = "";

		try {
			File file = File.createTempFile("graph", ".xml", new File(m_directory));
			fileName = file.getName();
			FileWriter writer = new FileWriter(file.getAbsolutePath());
			writer.write(svg);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		GraphInformation graphInformation = new GraphInformation(formData.getGraphName().getValue(), fileName, 0);
		EgtGraphStorage.changeGraphInformation(null, graphInformation);

		return formData;
	}

	@Override
	public EgtGraphFormData load(GraphInformation graphInformation, EgtGraphFormData formData)
			throws ProcessingException {
		String filePath = m_directory + graphInformation.getFileName();

		String svg = "";
		try {
			FileReader reader = new FileReader(filePath);
			svg = IOUtility.readString(reader);
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		formData.getGraphName().setValue(graphInformation.getName());
		formData.setSvgText(svg);

		return formData;
	}

	@Override
	public EgtGraphFormData prepareCreate(EgtGraphFormData formData) throws ProcessingException {
		return formData;
	}

	@Override
	public EgtGraphFormData store(GraphInformation graphInformation, EgtGraphFormData formData)
			throws ProcessingException {
		String svg = formData.getSvgText();

		String filePath = m_directory + graphInformation.getFileName();

		try {
			FileWriter writer = new FileWriter(filePath);
			writer.write(svg);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		GraphInformation newGraphInformation = new GraphInformation(formData.getGraphName().getValue(),
				graphInformation.getFileName(), 0);
		EgtGraphStorage.changeGraphInformation(graphInformation, newGraphInformation);

		return formData;
	}

	@Override
	public int delete(GraphInformation graphInformation) throws ProcessingException {
		boolean success = IOUtility.deleteFile(m_directory + graphInformation.getFileName());
		EgtGraphStorage.changeGraphInformation(graphInformation, null);
		return success ? 1 : 0;
	}

	@Override
	public int delete(List<GraphInformation> graphInformation) throws ProcessingException {
		int del = 0;
		for (GraphInformation graphInfo : graphInformation) {
			del = del + delete(graphInfo);
		}
		return del;
	}

}
