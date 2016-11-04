package org.eclipse.scout.apps.egt.client.work;

import java.util.List;

import org.eclipse.scout.apps.egt.client.graph.EgtGraphTablePage;
import org.eclipse.scout.apps.egt.shared.FontAwesomeIcons;
import org.eclipse.scout.rt.client.ui.desktop.outline.AbstractOutline;
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.IPage;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.shared.TEXTS;

/**
 * <h3>{@link WorkOutline}</h3>
 *
 * @author Fritz Schinkel
 */
@Order(1000)
public class WorkOutline extends AbstractOutline {

	@Override
	protected void execCreateChildPages(List<IPage<?>> pageList) {
		super.execCreateChildPages(pageList);
		EgtGraphTablePage egtGraphTablePage = new EgtGraphTablePage();
		pageList.add(egtGraphTablePage);
	}

	@Override
	protected String getConfiguredTitle() {
		return TEXTS.get("Work");
	}

	@Override
	protected String getConfiguredIconId() {
		return FontAwesomeIcons.Wrench;
	}
}
