package org.eclipse.scout.apps.egt.client.ui.desktop.forms;

import org.eclipse.scout.rt.client.ui.form.IForm;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractCloseButton;
import org.eclipse.scout.rt.platform.exception.ProcessingException;

public interface IEgtPageForm extends IForm {

	/**
	 * start the PageFormHandler
	 */
	void startPageForm() throws ProcessingException;

	AbstractCloseButton getCloseButton() throws ProcessingException;

}
