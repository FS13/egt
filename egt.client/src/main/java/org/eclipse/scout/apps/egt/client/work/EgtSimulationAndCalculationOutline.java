package org.eclipse.scout.apps.egt.client.work;

import java.util.List;

import org.eclipse.scout.apps.egt.client.graph.EgtGraphSimulationForm;
import org.eclipse.scout.apps.egt.client.ui.desktop.pages.FormPage;
import org.eclipse.scout.apps.egt.shared.FontAwesomeIcons;
import org.eclipse.scout.rt.client.ui.desktop.outline.AbstractOutline;
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.IPage;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.shared.TEXTS;

/**
 * <h3>{@link EgtSimulationAndCalculationOutline}</h3>
 *
 * @author Fritz Schinkel
 */
@Order(2000)
public class EgtSimulationAndCalculationOutline extends AbstractOutline {

	@Override
	protected void execCreateChildPages(List<IPage<?>> pageList) {
		super.execCreateChildPages(pageList);
		FormPage egtGraphSimulationPage = new FormPage(EgtGraphSimulationForm.class);
		pageList.add(egtGraphSimulationPage);
	}

	@Override
	protected String getConfiguredTitle() {
		return TEXTS.get("Work");
	}

	@Override
	protected String getConfiguredIconId() {
		return FontAwesomeIcons.XSquared;
	}
}
