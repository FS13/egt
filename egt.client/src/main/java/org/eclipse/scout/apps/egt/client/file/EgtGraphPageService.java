package org.eclipse.scout.apps.egt.client.file;

import org.eclipse.scout.apps.egt.shared.graph.EgtGraphStorage;
import org.eclipse.scout.apps.egt.shared.graph.EgtGraphStorage.GraphInformation;
import org.eclipse.scout.apps.egt.shared.graph.EgtGraphTablePageData;
import org.eclipse.scout.apps.egt.shared.graph.EgtGraphTablePageData.EgtGraphTableRowData;
import org.eclipse.scout.apps.egt.shared.graph.GraphSearchFormData;
import org.eclipse.scout.apps.egt.shared.graph.IEgtGraphPageService;
import org.eclipse.scout.rt.platform.exception.ProcessingException;
import org.eclipse.scout.rt.platform.util.StringUtility;

public class EgtGraphPageService implements IEgtGraphPageService {

	@Override
	public EgtGraphTablePageData getPageData(GraphSearchFormData searchFormData) throws ProcessingException {
		EgtGraphTablePageData pageData = new EgtGraphTablePageData();

		boolean search = !StringUtility.isNullOrEmpty(searchFormData.getGraphName().getValue());

		for (GraphInformation graphInformation : EgtGraphStorage.getGraphInformation()) {
			String name = graphInformation.getName();
			if (!search || StringUtility.contains(name, searchFormData.getGraphName().getValue())) {
				EgtGraphTableRowData addRow = pageData.addRow();
				addRow.setGraphName(name);
				addRow.setGraphInformation(graphInformation);
			}

		}

		return pageData;
	}

}
