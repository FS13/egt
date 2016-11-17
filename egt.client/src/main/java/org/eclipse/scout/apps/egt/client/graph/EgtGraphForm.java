package org.eclipse.scout.apps.egt.client.graph;

import java.io.IOException;

import org.eclipse.scout.apps.egt.client.graph.EgtGraphForm.MainBox.CancelButton;
import org.eclipse.scout.apps.egt.client.graph.EgtGraphForm.MainBox.GroupBox;
import org.eclipse.scout.apps.egt.client.graph.EgtGraphForm.MainBox.GroupBox.GraphDetailFormField;
import org.eclipse.scout.apps.egt.client.graph.EgtGraphForm.MainBox.GroupBox.GraphNameField;
import org.eclipse.scout.apps.egt.client.graph.EgtGraphForm.MainBox.OkButton;
import org.eclipse.scout.apps.egt.shared.graph.EgtGraphFormData;
import org.eclipse.scout.apps.egt.shared.graph.EgtGraphStorage.GraphInformation;
import org.eclipse.scout.apps.egt.shared.graph.IEgtGraphProcessService;
import org.eclipse.scout.rt.client.dto.FormData;
import org.eclipse.scout.rt.client.ui.form.AbstractForm;
import org.eclipse.scout.rt.client.ui.form.AbstractFormHandler;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractCancelButton;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractOkButton;
import org.eclipse.scout.rt.client.ui.form.fields.groupbox.AbstractGroupBox;
import org.eclipse.scout.rt.client.ui.form.fields.stringfield.AbstractStringField;
import org.eclipse.scout.rt.client.ui.form.fields.wrappedform.AbstractWrappedFormField;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.exception.ExceptionHandler;
import org.eclipse.scout.rt.platform.exception.ProcessingException;
import org.eclipse.scout.rt.shared.TEXTS;

@FormData(value = EgtGraphFormData.class, sdkCommand = FormData.SdkCommand.CREATE)
public class EgtGraphForm extends AbstractForm {

	private GraphInformation m_graphInformation;
	private String m_svgText;

	public EgtGraphForm() throws ProcessingException {
		super();
	}

	@FormData
	public GraphInformation getGraphInformation() {
		return m_graphInformation;
	}

	@FormData
	public void setGraphInformation(GraphInformation graphInformation) {
		m_graphInformation = graphInformation;
	}

	@FormData
	public String getSvgText() {
		return m_svgText;
	}

	@FormData
	public void setSvgText(String svgText) {
		m_svgText = svgText;
	}

	protected int getGraphDetailFormGroupBoxColumnCount() {
		return 2;
	}

	protected int getGraphDetailFormGraphSvgFieldWidth() {
		return 1;
	}

	protected int getGraphDetailFormConfigurationBoxWidth() {
		return 1;
	}

	public void startModify() throws ProcessingException {
		startInternal(new ModifyHandler());
	}

	public void startNew() throws ProcessingException {
		startInternal(new NewHandler());
	}

	public void startCopy() throws ProcessingException {
		startInternal(new CopyHandler());
	}

	public CancelButton getCancelButton() {
		return getFieldByClass(CancelButton.class);
	}

	public MainBox getMainBox() {
		return getFieldByClass(MainBox.class);
	}

	public GroupBox getGroupBox() {
		return getFieldByClass(GroupBox.class);
	}

	public GraphNameField getGraphNameField() {
		return getFieldByClass(GraphNameField.class);
	}

	public GraphDetailFormField getGraphDetailFormField() {
		return getFieldByClass(GraphDetailFormField.class);
	}

	public OkButton getOkButton() {
		return getFieldByClass(OkButton.class);
	}

	@Order(10.0)
	public class MainBox extends AbstractGroupBox {

		@Order(10.0)
		public class GroupBox extends AbstractGroupBox {

			@Override
			protected int getConfiguredGridColumnCount() {
				return 1;
			}

			@Override
			protected int getConfiguredWidthInPixel() {
				return 1100;
			}

			@Override
			protected int getConfiguredHeightInPixel() {
				return 850;
			}

			@Order(10.0)
			public class GraphNameField extends AbstractStringField {

				@Override
				protected String getConfiguredLabel() {
					return TEXTS.get("Name");
				}

				@Override
				protected boolean getConfiguredMandatory() {
					return true;
				}

			}

			@Order(20.0)
			public class GraphDetailFormField extends AbstractWrappedFormField<EgtGraphDetailForm> {

				@Override
				protected void initConfig() {
					super.initConfig();
					try {
						EgtGraphDetailForm form = new EgtGraphDetailForm() {

							@Override
							protected int getGroupBoxColumnCount() {
								return getGraphDetailFormGroupBoxColumnCount();
							}

							@Override
							protected int getGraphSvgFieldWidth() {
								return getGraphDetailFormGraphSvgFieldWidth();
							}

							@Override
							protected int getConfigurationBoxWidth() {
								return getGraphDetailFormConfigurationBoxWidth();
							}

						};
						setInnerForm(form, true);
					} catch (Exception e) {
						BEANS.get(ExceptionHandler.class).handle(new ProcessingException("error creating instance of class '" + getConfiguredInnerForm().getName() + "'.", e));
					}
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

	public class ModifyHandler extends AbstractFormHandler {

		@Override
		protected void execLoad() throws ProcessingException {
			IEgtGraphProcessService service = BEANS.get(IEgtGraphProcessService.class);
			EgtGraphFormData formData = new EgtGraphFormData();
			exportFormData(formData);
			formData = service.load(getGraphInformation(), formData);
			importFormData(formData);

			try {
				getGraphDetailFormField().getInnerForm().setGraph(GraphUtility.buildGraphFromSvgText(getSvgText()));
				getGraphDetailFormField().getInnerForm().populateGraph();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		protected void execStore() throws ProcessingException {
			setSvgText(getGraphDetailFormField().getInnerForm().getGraphSvgSourceField().getValue());

			IEgtGraphProcessService service = BEANS.get(IEgtGraphProcessService.class);
			EgtGraphFormData formData = new EgtGraphFormData();
			exportFormData(formData);
			formData = service.store(getGraphInformation(), formData, getGraphDetailFormField().getInnerForm().getGraph().getVertices().size());

		}
	}

	public class NewHandler extends AbstractFormHandler {

		@Override
		protected void execLoad() throws ProcessingException {
			IEgtGraphProcessService service = BEANS.get(IEgtGraphProcessService.class);
			EgtGraphFormData formData = new EgtGraphFormData();
			exportFormData(formData);
			formData = service.prepareCreate(formData);
			importFormData(formData);

			try {
				getGraphDetailFormField().getInnerForm().setGraph(GraphUtility.buildGraphFromSvgText(getSvgText()));
				getGraphDetailFormField().getInnerForm().populateGraph();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		protected void execStore() throws ProcessingException {
			setSvgText(getGraphDetailFormField().getInnerForm().getGraphSvgSourceField().getValue());

			IEgtGraphProcessService service = BEANS.get(IEgtGraphProcessService.class);
			EgtGraphFormData formData = new EgtGraphFormData();
			exportFormData(formData);
			formData = service.create(formData, getGraphDetailFormField().getInnerForm().getGraph().getVertices().size());

		}
	}

	public class CopyHandler extends NewHandler {

		@Override
		protected void execLoad() throws ProcessingException {
			IEgtGraphProcessService service = BEANS.get(IEgtGraphProcessService.class);
			EgtGraphFormData formData = new EgtGraphFormData();
			exportFormData(formData);
			formData = service.load(getGraphInformation(), formData);
			formData.getGraphName().setValue(formData.getGraphName().getValue() + " - " + TEXTS.get("Copy0"));
			importFormData(formData);

			try {
				getGraphDetailFormField().getInnerForm().setGraph(GraphUtility.buildGraphFromSvgText(getSvgText()));
				getGraphDetailFormField().getInnerForm().populateGraph();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
