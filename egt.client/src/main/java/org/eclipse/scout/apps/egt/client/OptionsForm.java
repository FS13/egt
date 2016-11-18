package org.eclipse.scout.apps.egt.client;

import org.eclipse.scout.apps.egt.client.OptionsForm.MainBox.GroupBox.UiThemeField;
import org.eclipse.scout.apps.egt.shared.UiThemeCodeType;
import org.eclipse.scout.rt.client.ui.form.AbstractForm;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractOkButton;
import org.eclipse.scout.rt.client.ui.form.fields.groupbox.AbstractGroupBox;
import org.eclipse.scout.rt.client.ui.form.fields.smartfield.AbstractSmartField;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.util.StringUtility;
import org.eclipse.scout.rt.shared.TEXTS;
import org.eclipse.scout.rt.shared.services.common.code.ICodeType;

public class OptionsForm extends AbstractForm {

	@Override
	protected String getConfiguredTitle() {
		return TEXTS.get("Options");
	}

	@Override
	protected void execInitForm() {
		String theme = StringUtility.nvl(getDesktop().getTheme(), UiThemeCodeType.RedCode.ID);
		getUiThemeField().setValue(theme);
	}

	public MainBox getMainBox() {
		return getFieldByClass(MainBox.class);
	}

	public UiThemeField getUiThemeField() {
		return getFieldByClass(UiThemeField.class);
	}

	protected void storeOptions() {
		// Not inside form handler, because the form is used in a FormToolButton
		// without a handler
		getDesktop().setTheme(getUiThemeField().getValue());
	}

	@Order(10)
	public class MainBox extends AbstractGroupBox {

		@Override
		protected int getConfiguredGridW() {
			return 1;
		}

		@Order(10)
		public class GroupBox extends AbstractGroupBox {

			@Override
			protected int getConfiguredGridW() {
				return 1;
			}

			@Order(10)
			public class UiThemeField extends AbstractSmartField<String> {

				@Override
				protected String getConfiguredLabel() {
					return TEXTS.get("UiTheme");
				}

				@Override
				protected Class<? extends ICodeType<?, String>> getConfiguredCodeType() {
					return UiThemeCodeType.class;
				}

				@Override
				protected boolean getConfiguredMandatory() {
					return true;
				}
			}
		}

		@Order(10)
		public class ApplyButton extends AbstractOkButton {

			@Override
			protected String getConfiguredLabel() {
				return TEXTS.get("Apply");
			}

			@Override
			protected void execClickAction() {
				storeOptions();
			}
		}
	}
}
