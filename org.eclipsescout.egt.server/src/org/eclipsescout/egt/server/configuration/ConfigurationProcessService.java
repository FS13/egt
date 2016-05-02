/**
 *
 */
package org.eclipsescout.egt.server.configuration;

import org.eclipse.scout.commons.exception.ProcessingException;
import org.eclipse.scout.commons.holders.NVPair;
import org.eclipse.scout.commons.holders.StringHolder;
import org.eclipse.scout.rt.server.services.common.jdbc.SQL;
import org.eclipse.scout.service.AbstractService;
import org.eclipsescout.egt.shared.configuration.AbstractConfigurationFormData;
import org.eclipsescout.egt.shared.configuration.IConfigurationProcessService;

/**
 * @author user
 */
public class ConfigurationProcessService extends AbstractService implements IConfigurationProcessService {

  @Override
  public AbstractConfigurationFormData load(AbstractConfigurationFormData formData, String identifier) throws ProcessingException {
    SQL.selectInto(""
        + " SELECT IDENTIFIER, "
        + "        VALUE "
        + " FROM   PARAMETER "
        + " WHERE  IDENTIFIER = :identifier "
        + " INTO   :parameterName,"
        + "        :parameterValue "
        , new NVPair("identifier", identifier)
        , formData);
    return formData;
  }

  @Override
  public AbstractConfigurationFormData store(AbstractConfigurationFormData formData, String identifier) throws ProcessingException {
    if (SQL.update(""
        + " UPDATE PARAMETER "
        + " SET    VALUE = :parameterValue "
        + " WHERE  IDENTIFIER = :identifier "
        , new NVPair("identifier", identifier)
        , formData) == 0) {
      SQL.insert(""
          + " INSERT INTO PARAMETER "
          + "             (IDENTIFIER, VALUE) "
          + " VALUES "
          + "             (:identifier, :parameterValue) "
          , new NVPair("identifier", identifier)
          , formData);
    }
    return formData;
  }

  @Override
  public String loadParameterValue(String identifier) throws ProcessingException {
    StringHolder valueHolder = new StringHolder();
    SQL.selectInto(""
        + " SELECT VALUE "
        + " FROM   PARAMETER "
        + " WHERE  IDENTIFIER = :identifier "
        + " INTO   :valueHolder "
        , new NVPair("identifier", identifier)
        , new NVPair("valueHolder", valueHolder));
    return valueHolder.getValue();
  }

}
