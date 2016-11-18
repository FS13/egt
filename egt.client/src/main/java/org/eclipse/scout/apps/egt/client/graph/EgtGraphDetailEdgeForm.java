package org.eclipse.scout.apps.egt.client.graph;

import java.util.List;

import org.eclipse.scout.apps.egt.client.graph.EgtGraphDetailEdgeForm.MainBox.CancelButton;
import org.eclipse.scout.apps.egt.client.graph.EgtGraphDetailEdgeForm.MainBox.GroupBox;
import org.eclipse.scout.apps.egt.client.graph.EgtGraphDetailEdgeForm.MainBox.GroupBox.FromField;
import org.eclipse.scout.apps.egt.client.graph.EgtGraphDetailEdgeForm.MainBox.GroupBox.ToField;
import org.eclipse.scout.apps.egt.client.graph.EgtGraphDetailEdgeForm.MainBox.OkButton;
import org.eclipse.scout.rt.client.ui.form.AbstractForm;
import org.eclipse.scout.rt.client.ui.form.AbstractFormHandler;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractCancelButton;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractOkButton;
import org.eclipse.scout.rt.client.ui.form.fields.groupbox.AbstractGroupBox;
import org.eclipse.scout.rt.client.ui.form.fields.integerfield.AbstractIntegerField;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.exception.ProcessingException;
import org.eclipse.scout.rt.platform.exception.VetoException;
import org.eclipse.scout.rt.shared.TEXTS;

public class EgtGraphDetailEdgeForm extends AbstractForm {

	private List<Integer> m_vertexIds;

	/**
	 * @throws org.eclipse.scout.commons.exception.ProcessingException
	 */
	public EgtGraphDetailEdgeForm() throws ProcessingException {
		super();
	}

	public List<Integer> getVertexIds() {
		return m_vertexIds;
	}

	public void setVertexIds(List<Integer> vertexIds) {
		m_vertexIds = vertexIds;
	}

	@Override
	public void validateForm() throws ProcessingException {
		super.validateForm();

		int from = getFromField().getValue();
		int to = getToField().getValue();

		boolean problems = false;
		String problemsWith = TEXTS.get("ProblemsWithTheFollowingValues");

		if (!getVertexIds().contains(from)) {
			problems = true;
			problemsWith += "\n" + " - " + TEXTS.get("From");
		}

		if (!getVertexIds().contains(to)) {
			problems = true;
			problemsWith += "\n" + " - " + TEXTS.get("To");
		}

		if (problems) {
			problemsWith += "\n" + " - " + TEXTS.get("VertexDoesNotExist");
			throw new VetoException(problemsWith).withTitle(TEXTS.get("EGTFS"));
		}
	}

	public void start() throws ProcessingException {
		startInternal(new Handler());
	}

	public MainBox getMainBox() {
		return getFieldByClass(MainBox.class);
	}

	public GroupBox getGroupBox() {
		return getFieldByClass(GroupBox.class);
	}

	public FromField getFromField() {
		return getFieldByClass(FromField.class);
	}

	public ToField getToField() {
		return getFieldByClass(ToField.class);
	}

	public OkButton getOkButton() {
		return getFieldByClass(OkButton.class);
	}

	public CancelButton getCancelButton() {
		return getFieldByClass(CancelButton.class);
	}

	@Order(10.0)
	public class MainBox extends AbstractGroupBox {

		@Order(10.0)
		public class GroupBox extends AbstractGroupBox {

			@Order(10.0)
			public class FromField extends AbstractIntegerField {

				@Override
				protected String getConfiguredLabel() {
					return TEXTS.get("From");
				}

				@Override
				protected boolean getConfiguredMandatory() {
					return true;
				}

			}

			@Order(20.0)
			public class ToField extends AbstractIntegerField {

				@Override
				protected String getConfiguredLabel() {
					return TEXTS.get("To");
				}

				@Override
				protected boolean getConfiguredMandatory() {
					return true;
				}

			}

		}

		@Order(10.0)
		public class OkButton extends AbstractOkButton {
		}

		@Order(20.0)
		public class CancelButton extends AbstractCancelButton {
		}
	}

	public class Handler extends AbstractFormHandler {
	}
}
