package org.eclipse.scout.apps.egt.shared.graph;

import javax.annotation.Generated;

import org.eclipse.scout.rt.shared.data.form.AbstractFormData;
import org.eclipse.scout.rt.shared.data.form.fields.AbstractValueFieldData;

/**
 * <b>NOTE:</b><br>
 * This class is auto generated by the Scout SDK. No manual modifications
 * recommended.
 */
@Generated(value = "org.eclipse.scout.apps.egt.client.graph.GraphSearchForm", comments = "This class is auto generated by the Scout SDK. No manual modifications recommended.")
public class GraphSearchFormData extends AbstractFormData {

	private static final long serialVersionUID = 1L;

	public GraphName getGraphName() {
		return getFieldByClass(GraphName.class);
	}

	public static class GraphName extends AbstractValueFieldData<String> {

		private static final long serialVersionUID = 1L;
	}
}
