/**
 *
 */
package org.eclipsescout.egt.server.services.common.sql;

import org.eclipse.scout.rt.services.common.jdbc.AbstractDerbySqlService;

/**
 * @author user
 */
public class DerbySqlService extends AbstractDerbySqlService {

  @Override
  protected String getConfiguredJdbcMappingName() {
    return "jdbc:derby:C:\\Users\\user\\Desktop\\Fritz\\Database\\DerbyDB";
  }

  @Override
  protected String getConfiguredPassword() {
    return "taiwan";
  }

  @Override
  protected String getConfiguredUsername() {
    return "egt";
  }
}
