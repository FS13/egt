/**
 *
 */
package org.eclipsescout.egt.client.ui.desktop.outlines;

import java.util.List;

import org.eclipse.scout.commons.exception.ProcessingException;
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.IPage;
import org.eclipse.scout.rt.extension.client.ui.desktop.outline.AbstractExtensibleOutline;
import org.eclipse.scout.rt.shared.TEXTS;
import org.eclipsescout.egt.client.graph.EgtGraphSimulationForm;
import org.eclipsescout.egt.client.graph.EgtGraphTablePage;
import org.eclipsescout.egt.client.ui.desktop.pages.FormPage;

/**
 * @author user
 */
public class StandardOutline extends AbstractExtensibleOutline {

  @Override
  protected String getConfiguredTitle() {
    return TEXTS.get("StandardOutline");
  }

  @Override
  protected void execCreateChildPages(List<IPage> pageList) throws ProcessingException {
    EgtGraphTablePage egtGraphTablePage = new EgtGraphTablePage();
    pageList.add(egtGraphTablePage);
    FormPage egtGraphSimulationPage = new FormPage(EgtGraphSimulationForm.class);
    pageList.add(egtGraphSimulationPage);
  }
}
