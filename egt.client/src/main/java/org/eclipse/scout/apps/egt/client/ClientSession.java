package org.eclipse.scout.apps.egt.client;

import org.eclipse.scout.apps.egt.client.matlab.MatlabControl;
import org.eclipse.scout.apps.egt.shared.graph.EgtGraphStorage;
import org.eclipse.scout.rt.client.AbstractClientSession;
import org.eclipse.scout.rt.client.IClientSession;
import org.eclipse.scout.rt.client.session.ClientSessionProvider;
import org.eclipse.scout.rt.shared.services.common.code.CODES;

/**
 * <h3>{@link ClientSession}</h3>
 *
 * @author Fritz Schinkel
 */
public class ClientSession extends AbstractClientSession {

	public ClientSession() {
		super(true);
	}

	/**
	 * @return The {@link IClientSession} which is associated with the current
	 *         thread, or <code>null</code> if not found.
	 */
	public static ClientSession get() {
		return ClientSessionProvider.currentSession(ClientSession.class);
	}

	@Override
	protected void execLoadSession() {
		EgtGraphStorage.reloadGraphStorage();

		MatlabControl.requestProxy();

		// pre-load all known code types
		CODES.getAllCodeTypes("org.eclipse.scout.apps.egt.shared");

		setDesktop(new Desktop());
	}

	@Override
	protected void execStoreSession() {
		MatlabControl.disconnectProxy();
	}

}
