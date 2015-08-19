/**
 *
 */
package org.eclipsescout.egt.server.graph;

import org.eclipse.scout.rt.server.services.lookup.AbstractSqlLookupService;
import org.eclipsescout.egt.shared.graph.IEgtGraphLookupService;

/**
 * @author user
 */
public class EgtGraphLookupService extends AbstractSqlLookupService<Long> implements IEgtGraphLookupService {

  @Override
  protected String getConfiguredSqlSelect() {
    return "" +
        "SELECT  G.GRAPH_NR, " +
        "        G.NAME " +
        "FROM    GRAPH G " +
        "WHERE   1=1 " +
        "<key>   AND     G.GRAPH_NR = :key </key> " +
        "<text>  AND     UPPER(G.NAME) LIKE UPPER(:text||'%') </text> " +
        "<all> </all> ";

  }
}
