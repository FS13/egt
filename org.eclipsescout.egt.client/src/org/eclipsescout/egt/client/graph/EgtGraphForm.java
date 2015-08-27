/**
 *
 */
package org.eclipsescout.egt.client.graph;

import java.io.IOException;

import org.eclipse.scout.commons.annotations.FormData;
import org.eclipse.scout.commons.annotations.Order;
import org.eclipse.scout.commons.exception.ProcessingException;
import org.eclipse.scout.rt.client.ui.form.AbstractForm;
import org.eclipse.scout.rt.client.ui.form.AbstractFormHandler;
import org.eclipse.scout.rt.client.ui.form.IForm;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractCancelButton;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractOkButton;
import org.eclipse.scout.rt.client.ui.form.fields.groupbox.AbstractGroupBox;
import org.eclipse.scout.rt.client.ui.form.fields.stringfield.AbstractStringField;
import org.eclipse.scout.rt.client.ui.form.fields.wrappedform.AbstractWrappedFormField;
import org.eclipse.scout.rt.shared.TEXTS;
import org.eclipse.scout.service.SERVICES;
import org.eclipsescout.egt.client.graph.EgtGraphForm.MainBox.CancelButton;
import org.eclipsescout.egt.client.graph.EgtGraphForm.MainBox.GroupBox;
import org.eclipsescout.egt.client.graph.EgtGraphForm.MainBox.GroupBox.GraphDetailFormField;
import org.eclipsescout.egt.client.graph.EgtGraphForm.MainBox.GroupBox.GraphNameField;
import org.eclipsescout.egt.client.graph.EgtGraphForm.MainBox.OkButton;
import org.eclipsescout.egt.shared.graph.EgtGraphFormData;
import org.eclipsescout.egt.shared.graph.IEgtGraphProcessService;

/**
 * @author user
 */
@FormData(value = EgtGraphFormData.class, sdkCommand = FormData.SdkCommand.CREATE)
public class EgtGraphForm extends AbstractForm {

  private Long m_graphNr;
  private String m_svgText;

  public EgtGraphForm() throws ProcessingException {
    super();
  }

  @FormData
  public Long getGraphNr() {
    return m_graphNr;
  }

  @FormData
  public void setGraphNr(Long graphNr) {
    m_graphNr = graphNr;
  }

  @FormData
  public String getSvgText() {
    return m_svgText;
  }

  @FormData
  public void setSvgText(String svgText) {
    m_svgText = svgText;
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

      @Order(10.0)
      public class GraphNameField extends AbstractStringField {

        @Override
        protected String getConfiguredLabel() {
          return TEXTS.get("Name");
        }

      }

      @Order(20.0)
      public class GraphDetailFormField extends AbstractWrappedFormField<EgtGraphDetailForm> {

        @Override
        protected Class<? extends IForm> getConfiguredInnerForm() {
          return EgtGraphDetailForm.class;
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
      IEgtGraphProcessService service = SERVICES.getService(IEgtGraphProcessService.class);
      EgtGraphFormData formData = new EgtGraphFormData();
      exportFormData(formData);
      formData = service.load(formData);
      importFormData(formData);

      try {
        getGraphDetailFormField().getInnerForm().setGraph(GraphUtility.buildGraphFromSvgText(getSvgText()));
        getGraphDetailFormField().getInnerForm().populateGraph();
      }
      catch (IOException e) {
        e.printStackTrace();
      }
    }

    @Override
    protected void execStore() throws ProcessingException {
      setSvgText(getGraphDetailFormField().getInnerForm().getGraphSvgSourceField().getValue());

      IEgtGraphProcessService service = SERVICES.getService(IEgtGraphProcessService.class);
      EgtGraphFormData formData = new EgtGraphFormData();
      exportFormData(formData);
      formData = service.store(formData);

    }
  }

  public class NewHandler extends AbstractFormHandler {

    @Override
    protected void execLoad() throws ProcessingException {
      IEgtGraphProcessService service = SERVICES.getService(IEgtGraphProcessService.class);
      EgtGraphFormData formData = new EgtGraphFormData();
      exportFormData(formData);
      formData = service.prepareCreate(formData);
      importFormData(formData);

      try {
        getGraphDetailFormField().getInnerForm().setGraph(GraphUtility.buildGraphFromSvgText(getSvgText()));
        getGraphDetailFormField().getInnerForm().populateGraph();
      }
      catch (IOException e) {
        e.printStackTrace();
      }

    }

    @Override
    protected void execStore() throws ProcessingException {
      setSvgText(getGraphDetailFormField().getInnerForm().getGraphSvgSourceField().getValue());

      IEgtGraphProcessService service = SERVICES.getService(IEgtGraphProcessService.class);
      EgtGraphFormData formData = new EgtGraphFormData();
      exportFormData(formData);
      formData = service.create(formData);

    }
  }

  public class CopyHandler extends NewHandler {

    @Override
    protected void execLoad() throws ProcessingException {
      IEgtGraphProcessService service = SERVICES.getService(IEgtGraphProcessService.class);
      EgtGraphFormData formData = new EgtGraphFormData();
      exportFormData(formData);
      formData = service.load(formData);
      formData.getGraphName().setValue(formData.getGraphName().getValue() + " - " + TEXTS.get("Copy0"));
      importFormData(formData);

      try {
        getGraphDetailFormField().getInnerForm().setGraph(GraphUtility.buildGraphFromSvgText(getSvgText()));
        getGraphDetailFormField().getInnerForm().populateGraph();
      }
      catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
