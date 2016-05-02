/**
 *
 */
package org.eclipsescout.egt.client.configuration;

import org.eclipse.scout.commons.annotations.FormData;
import org.eclipse.scout.commons.annotations.Order;
import org.eclipse.scout.commons.exception.ProcessingException;
import org.eclipse.scout.rt.client.ui.form.AbstractForm;
import org.eclipse.scout.rt.client.ui.form.AbstractFormHandler;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractCancelButton;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractOkButton;
import org.eclipse.scout.rt.client.ui.form.fields.groupbox.AbstractGroupBox;
import org.eclipse.scout.rt.client.ui.form.fields.stringfield.AbstractStringField;
import org.eclipse.scout.rt.shared.TEXTS;
import org.eclipse.scout.service.SERVICES;
import org.eclipsescout.egt.client.configuration.AbstractConfigurationForm.MainBox.GroupBox;
import org.eclipsescout.egt.client.configuration.AbstractConfigurationForm.MainBox.GroupBox.ParameterNameField;
import org.eclipsescout.egt.client.configuration.AbstractConfigurationForm.MainBox.GroupBox.ParameterValueField;
import org.eclipsescout.egt.client.configuration.AbstractConfigurationForm.MainBox.OkButton;
import org.eclipsescout.egt.shared.configuration.AbstractConfigurationFormData;
import org.eclipsescout.egt.shared.configuration.AbstractParameter;
import org.eclipsescout.egt.shared.configuration.IConfigurationProcessService;

/**
 * @author Fritz Schinkel
 */
@FormData(value = AbstractConfigurationFormData.class, sdkCommand = FormData.SdkCommand.CREATE)
public class AbstractConfigurationForm extends AbstractForm {

  private AbstractParameter m_parameter;

  public AbstractConfigurationForm(AbstractParameter parameter) throws ProcessingException {
    super();
    m_parameter = parameter;
  }

  public AbstractParameter getParameter() {
    return m_parameter;
  }

  public void startModify() throws ProcessingException {
    startInternal(new ModifyHandler());
  }

  public MainBox getMainBox() {
    return getFieldByClass(MainBox.class);
  }

  public GroupBox getGroupBox() {
    return getFieldByClass(GroupBox.class);
  }

  public ParameterNameField getParameterNameField() {
    return getFieldByClass(ParameterNameField.class);
  }

  public ParameterValueField getParameterValueField() {
    return getFieldByClass(ParameterValueField.class);
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
      public class ParameterNameField extends AbstractStringField {

        @Override
        protected String getConfiguredLabel() {
          return TEXTS.get("Name");
        }

        @Override
        protected boolean getConfiguredEnabled() {
          return false;
        }

        @Override
        protected String validateValueInternal(String rawValue) throws ProcessingException {
          return TEXTS.get(rawValue);
        }

      }

      @Order(20.0)
      public class ParameterValueField extends AbstractStringField {

        @Override
        protected String getConfiguredLabel() {
          return TEXTS.get("Value");
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
      IConfigurationProcessService service = SERVICES.getService(IConfigurationProcessService.class);
      AbstractConfigurationFormData formData = new AbstractConfigurationFormData();
      exportFormData(formData);
      formData = service.load(formData, getParameter().getIdentifier());
      importFormData(formData);
    }

    @Override
    protected void execStore() throws ProcessingException {
      IConfigurationProcessService service = SERVICES.getService(IConfigurationProcessService.class);
      AbstractConfigurationFormData formData = new AbstractConfigurationFormData();
      exportFormData(formData);
      formData = service.store(formData, getParameter().getIdentifier());

    }
  }

}
