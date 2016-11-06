package org.eclipse.scout.apps.egt.shared.graph;

import org.eclipse.scout.apps.egt.shared.graph.EgtGraphStorage.GraphInformation;
import org.eclipse.scout.rt.shared.services.lookup.ILookupService;
import org.eclipse.scout.rt.shared.services.lookup.LookupCall;

public class EgtGraphLookupCall extends LookupCall<GraphInformation> {

	private static final long serialVersionUID = 1L;
	private int m_numberOfVertices;

	@Override
	protected Class<? extends ILookupService<GraphInformation>> getConfiguredService() {
		return IEgtGraphLookupService.class;
	}

	public void setNumberOfVertices(int numberOfVertices) {
		m_numberOfVertices = numberOfVertices;
	}

	public int getNumberOfVertices() {
		return m_numberOfVertices;
	}
}
