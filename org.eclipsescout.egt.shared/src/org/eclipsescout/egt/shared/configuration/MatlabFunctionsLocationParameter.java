/**
 *
 */
package org.eclipsescout.egt.shared.configuration;

import org.eclipse.scout.commons.exception.ProcessingException;

/**
 * @author Fritz Schinkel
 */
public class MatlabFunctionsLocationParameter extends AbstractParameter {

  private final static String m_identifier = "matlab_functions_location";

  public MatlabFunctionsLocationParameter() throws ProcessingException {
    super(m_identifier);
  }

}
