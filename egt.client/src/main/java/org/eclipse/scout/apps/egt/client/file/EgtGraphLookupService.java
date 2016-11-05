package org.eclipse.scout.apps.egt.client.file;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.scout.apps.egt.shared.graph.EgtGraphStorage;
import org.eclipse.scout.apps.egt.shared.graph.EgtGraphStorage.GraphInformation;
import org.eclipse.scout.apps.egt.shared.graph.IEgtGraphLookupService;
import org.eclipse.scout.rt.platform.util.CompareUtility;
import org.eclipse.scout.rt.platform.util.StringUtility;
import org.eclipse.scout.rt.shared.services.lookup.ILookupCall;
import org.eclipse.scout.rt.shared.services.lookup.ILookupRow;
import org.eclipse.scout.rt.shared.services.lookup.LookupRow;

public class EgtGraphLookupService implements IEgtGraphLookupService {

	private interface Matcher {
		boolean matches(GraphInformation graphInformation);
	}

	@Override
	public List<? extends ILookupRow<GraphInformation>> getDataByKey(ILookupCall<GraphInformation> call) {
		return getDataByMatcher(new Matcher() {
			@Override
			public boolean matches(GraphInformation graphInformation) {
				return CompareUtility.equals(graphInformation, call.getKey());
			}
		});
	}

	@Override
	public List<? extends ILookupRow<GraphInformation>> getDataByText(ILookupCall<GraphInformation> call) {
		return getDataByMatcher(new Matcher() {
			@Override
			public boolean matches(GraphInformation graphInformation) {
				return StringUtility.contains(graphInformation.getName(), call.getText());
			}
		});
	}

	@Override
	public List<? extends ILookupRow<GraphInformation>> getDataByAll(ILookupCall<GraphInformation> call) {
		return getDataByMatcher(new Matcher() {
			@Override
			public boolean matches(GraphInformation graphInformation) {
				if (call.getKey() != null) {
					int numberOfVertices = call.getKey().getNumberOfVertices();
					if (CompareUtility.equals(numberOfVertices, 0)) {
						return CompareUtility.equals(graphInformation.getNumberOfVertices(), numberOfVertices);
					}
				}
				return true;
			}
		});
	}

	@Override
	public List<? extends ILookupRow<GraphInformation>> getDataByRec(ILookupCall<GraphInformation> call) {
		return getDataByAll(call);
	}

	private List<? extends ILookupRow<GraphInformation>> getDataByMatcher(Matcher matcher) {
		List<ILookupRow<GraphInformation>> list = new ArrayList<>();
		for (GraphInformation graphInformation : EgtGraphStorage.getGraphInformation()) {
			if (matcher.matches(graphInformation)) {
				list.add(new LookupRow<GraphInformation>(graphInformation, graphInformation.getName()));
			}
		}
		return list;
	}
}
