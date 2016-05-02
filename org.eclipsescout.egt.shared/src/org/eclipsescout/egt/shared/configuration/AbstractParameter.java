/**
 *
 */
package org.eclipsescout.egt.shared.configuration;

import org.eclipse.scout.commons.exception.ProcessingException;
import org.eclipse.scout.service.SERVICES;

/**
 * @author Fritz Schinkel
 */
public class AbstractParameter {

  private static String m_identifier;
  private static String m_value;

  protected AbstractParameter(String identifier) throws ProcessingException {
    m_identifier = identifier;
    reloadValue();
  }

  public String getIdentifier() {
    return m_identifier;
  }

  public String getValue() {
    return m_value;
  }

  public void reloadValue() throws ProcessingException {
    m_value = SERVICES.getService(IConfigurationProcessService.class).loadParameterValue(getIdentifier());
  }

}
