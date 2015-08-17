/**
 *
 */
package org.eclipsescout.egt.server.graph;

import org.eclipse.scout.commons.StringUtility;
import org.eclipse.scout.commons.exception.ProcessingException;
import org.eclipse.scout.commons.holders.NVPair;
import org.eclipse.scout.rt.server.services.common.jdbc.SQL;
import org.eclipse.scout.service.AbstractService;
import org.eclipsescout.egt.shared.graph.EgtGraphTablePageData;
import org.eclipsescout.egt.shared.graph.GraphSearchFormData;
import org.eclipsescout.egt.shared.graph.IEgtGraphPageService;

/**
 * @author user
 */
public class EgtGraphPageService extends AbstractService implements IEgtGraphPageService {

  @Override
  public EgtGraphTablePageData getPageData(GraphSearchFormData searchFormData) throws ProcessingException {
    String sql = "";
    sql += " SELECT GRAPH_NR, "
        + "        NAME "
        + " FROM   GRAPH "
        + " WHERE  1=1 ";
    if (!StringUtility.isNullOrEmpty(searchFormData.getGraphName().getValue())) {
      sql += " AND UPPER(NAME) LIKE UPPER(:graphName || '%') ";
    }
    sql += " INTO   :{page.graphNr}, "
        + "        :{page.graphName} ";

    EgtGraphTablePageData pageData = new EgtGraphTablePageData();
    SQL.selectInto(sql
        , searchFormData
        , new NVPair("page", pageData));
    return pageData;
  }
}
