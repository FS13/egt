package org.eclipse.scout.apps.egt.client.ui.desktop.pages;

import org.eclipse.scout.apps.egt.client.ui.desktop.forms.IEgtPageForm;
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.AbstractPageWithNodes;
import org.eclipse.scout.rt.platform.exception.ProcessingException;
import org.eclipse.scout.rt.platform.util.CompareUtility;
import org.eclipse.scout.rt.shared.TEXTS;

public class FormPage extends AbstractPageWithNodes {

	private Class<? extends IEgtPageForm> m_formType;
	private boolean m_enabled = true;

	public FormPage(Class<? extends IEgtPageForm> formType) {
		super(false, formType.getName());
		m_formType = formType;
		callInitializer();
	}

	public FormPage(Class<? extends IEgtPageForm> c, boolean enabled) {
		super(false, c.getName());
		m_formType = c;
		m_enabled = enabled;
		callInitializer();
	}

	@Override
	protected boolean getConfiguredEnabled() {
		return m_enabled;
	}

	// @Override
	// protected String getConfiguredIconId() {
	// return AbstractIcons.TreeNode;
	// }

	@Override
	protected boolean getConfiguredLeaf() {
		return true;
	}

	@Override
	protected String getConfiguredTitle() {
		return TEXTS.get("Labels");
	}

	@Override
	protected void execInitPage() throws ProcessingException {
		String s = m_formType.getSimpleName();
		s = s.substring(8, s.length() - 4); // "EgtGraph".length() is 8
		getCellForUpdate().setText(TEXTS.get(s));
		setTableVisible(false);
	}

	@Override
	protected void execPageActivated() throws ProcessingException {
		if (getDetailForm() == null && m_enabled) {
			IEgtPageForm form = execCreateDetailForm();
			setDetailForm(form);
			if (!CompareUtility.equals(form.getCloseButton(), null)) {
				form.getCloseButton().setVisible(false);
			}
			form.startPageForm();
		}
	}

	protected IEgtPageForm execCreateDetailForm() throws ProcessingException {
		try {
			return m_formType.newInstance();
		} catch (Exception e) {
			throw new ProcessingException("create " + m_formType, e);
		}
	}

	@Override
	protected void execPageDeactivated() throws ProcessingException {
		if (getDetailForm() != null) {
			getDetailForm().doClose();
			setDetailForm(null);
		}
	}
}
