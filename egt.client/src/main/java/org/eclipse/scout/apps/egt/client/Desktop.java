package org.eclipse.scout.apps.egt.client;

import java.util.List;

import org.eclipse.scout.apps.egt.client.work.EgtGraphOutline;
import org.eclipse.scout.apps.egt.client.work.EgtSimulationAndCalculationOutline;
import org.eclipse.scout.apps.egt.shared.Icons;
import org.eclipse.scout.rt.client.ui.action.keystroke.IKeyStroke;
import org.eclipse.scout.rt.client.ui.desktop.AbstractDesktop;
import org.eclipse.scout.rt.client.ui.desktop.outline.AbstractOutlineViewButton;
import org.eclipse.scout.rt.client.ui.desktop.outline.IOutline;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.util.CollectionUtility;
import org.eclipse.scout.rt.shared.TEXTS;

/**
 * <h3>{@link Desktop}</h3>
 *
 * @author Fritz Schinkel
 */
public class Desktop extends AbstractDesktop {
	@Override
	protected String getConfiguredTitle() {
		return TEXTS.get("ApplicationTitle");
	}

	@Override
	protected String getConfiguredLogoId() {
		return Icons.AppLogo;
	}

	@Override
	protected List<Class<? extends IOutline>> getConfiguredOutlines() {
		return CollectionUtility.<Class<? extends IOutline>>arrayList(EgtGraphOutline.class,
				EgtSimulationAndCalculationOutline.class);
	}

	@Override
	protected void execGuiAttached() {
		super.execGuiAttached();
		selectFirstVisibleOutline();
	}

	protected void selectFirstVisibleOutline() {
		for (IOutline outline : getAvailableOutlines()) {
			if (outline.isEnabled() && outline.isVisible()) {
				setOutline(outline);
				break;
			}
		}
	}

	// @Order(1000)
	// public class FileMenu extends AbstractMenu {
	//
	// @Override
	// protected String getConfiguredText() {
	// return TEXTS.get("File");
	// }
	//
	// @Order(1000)
	// public class ExitMenu extends AbstractMenu {
	//
	// @Override
	// protected String getConfiguredText() {
	// return TEXTS.get("Exit");
	// }
	//
	// @Override
	// protected void execAction() {
	// ClientSessionProvider.currentSession(ClientSession.class).stop();
	// }
	// }
	// }

	@Order(1000)
	public class EgtGraphOutlineViewButton extends AbstractOutlineViewButton {

		public EgtGraphOutlineViewButton() {
			this(EgtGraphOutline.class);
		}

		protected EgtGraphOutlineViewButton(Class<? extends EgtGraphOutline> outlineClass) {
			super(Desktop.this, outlineClass);
		}

		@Override
		protected DisplayStyle getConfiguredDisplayStyle() {
			return DisplayStyle.TAB;
		}

		@Override
		protected String getConfiguredKeyStroke() {
			return IKeyStroke.F2;
		}
	}

	@Order(2000)
	public class EgtSimulationAndCalculationOutlineViewButton extends AbstractOutlineViewButton {

		public EgtSimulationAndCalculationOutlineViewButton() {
			this(EgtSimulationAndCalculationOutline.class);
		}

		protected EgtSimulationAndCalculationOutlineViewButton(
				Class<? extends EgtSimulationAndCalculationOutline> outlineClass) {
			super(Desktop.this, outlineClass);
		}

		@Override
		protected DisplayStyle getConfiguredDisplayStyle() {
			return DisplayStyle.TAB;
		}

		@Override
		protected String getConfiguredKeyStroke() {
			return IKeyStroke.F3;
		}
	}

}
