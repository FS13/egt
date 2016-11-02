package org.eclipse.scout.apps.egt.client.helloworld;

import org.eclipse.scout.rt.client.ui.desktop.outline.pages.AbstractPageWithNodes;
import org.eclipse.scout.rt.client.ui.form.IForm;
import org.eclipse.scout.rt.shared.TEXTS;

/**
 * <h3>{@link HelloWorldPage}</h3>
 *
 * @author Fritz Schinkel
 */
public class HelloWorldPage extends AbstractPageWithNodes {

	@Override
	protected boolean getConfiguredLeaf() {
		return true;
	}

	@Override
	protected boolean getConfiguredTableVisible() {
		return false;
	}

	@Override
	protected String getConfiguredTitle() {
		return TEXTS.get("HelloWorld");
	}

	@Override
	protected Class<? extends IForm> getConfiguredDetailForm() {
		return HelloWorldForm.class;
	}
}
