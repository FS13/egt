/**
 *
 */
package org.eclipsescout.egt.client.ui.desktop.forms;

import org.eclipse.scout.commons.exception.ProcessingException;
import org.eclipse.scout.rt.client.ui.form.IForm;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractCloseButton;

/**
 * @author user
 */
public interface IEgtPageForm extends IForm {

  /**
   * start the PageFormHandler
   */
  void startPageForm() throws ProcessingException;

  AbstractCloseButton getCloseButton() throws ProcessingException;

}
