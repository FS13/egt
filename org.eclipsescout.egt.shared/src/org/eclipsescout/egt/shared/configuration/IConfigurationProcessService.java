/**
 *
 */
package org.eclipsescout.egt.shared.configuration;

import org.eclipse.scout.commons.exception.ProcessingException;
import org.eclipse.scout.service.IService;

/**
 * @author user
 */
public interface IConfigurationProcessService extends IService {

  /**
   * @param formData
   * @return
   * @throws org.eclipse.scout.commons.exception.ProcessingException
   */
  AbstractConfigurationFormData load(AbstractConfigurationFormData formData, String identifier) throws ProcessingException;

  /**
   * @param formData
   * @return
   * @throws org.eclipse.scout.commons.exception.ProcessingException
   */
  AbstractConfigurationFormData store(AbstractConfigurationFormData formData, String identifier) throws ProcessingException;

  /**
   * @param identifier
   * @return
   * @throws ProcessingException
   */
  String loadParameterValue(String identifier) throws ProcessingException;

}
