/**
 *
 */
package org.eclipsescout.egt.server.graph;

import java.util.List;

import org.eclipse.scout.commons.exception.ProcessingException;
import org.eclipse.scout.commons.holders.NVPair;
import org.eclipse.scout.commons.holders.StringHolder;
import org.eclipse.scout.rt.server.services.common.jdbc.SQL;
import org.eclipse.scout.service.AbstractService;
import org.eclipsescout.egt.shared.graph.EgtGraphFormData;
import org.eclipsescout.egt.shared.graph.IEgtGraphProcessService;

/**
 * @author user
 */
public class EgtGraphProcessService extends AbstractService implements IEgtGraphProcessService {

  @Override
  public EgtGraphFormData create(EgtGraphFormData formData) throws ProcessingException {
    SQL.insert(""
        + " INSERT INTO GRAPH "
        + "             (NAME, SVG_TEXT) "
        + " VALUES "
        + "             (:graphName, :svgText) "
        , formData);
    return formData;
  }

  @Override
  public EgtGraphFormData load(EgtGraphFormData formData) throws ProcessingException {
    SQL.selectInto(""
        + " SELECT NAME, "
        + "        SVG_TEXT "
        + " FROM   GRAPH "
        + " WHERE  GRAPH_NR = :graphNr "
        + " INTO   :graphName, "
        + "        :svgText "
        , formData);
    return formData;
  }

  @Override
  public EgtGraphFormData prepareCreate(EgtGraphFormData formData) throws ProcessingException {
    return formData;
  }

  @Override
  public EgtGraphFormData store(EgtGraphFormData formData) throws ProcessingException {
    SQL.update(""
        + " UPDATE GRAPH "
        + " SET    NAME = :graphName, "
        + "        SVG_TEXT = :svgText "
        + " WHERE  GRAPH_NR = :graphNr ", formData);
    return formData;
  }

  @Override
  public int delete(Long graphNr) throws ProcessingException {
    return SQL.delete(""
        + " DELETE FROM GRAPH "
        + " WHERE  GRAPH_NR = :graphNr "
        , new NVPair("graphNr", graphNr));
  }

  @Override
  public int delete(List<Long> graphNrs) throws ProcessingException {
    int del = 0;
    for (Long graphNr : graphNrs) {
      del = del + delete(graphNr);
    }
    return del;
  }

  @Override
  public String getSvgTextForGraph(Long graphNr) throws ProcessingException {
    StringHolder svgText = new StringHolder();
    SQL.selectInto(""
        + " SELECT SVG_TEXT "
        + " FROM   GRAPH "
        + " WHERE  GRAPH_NR = :graphNr "
        + " INTO   :svgText "
        , new NVPair("graphNr", graphNr)
        , new NVPair("svgText", svgText));
    return svgText.getValue();
  }

}
