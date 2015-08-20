package org.eclipsescout.egt.client;

import org.eclipse.rap.rwt.lifecycle.UICallBack;
import org.eclipse.scout.commons.UriUtility;
import org.eclipse.scout.commons.exception.ProcessingException;
import org.eclipse.scout.commons.logger.IScoutLogger;
import org.eclipse.scout.commons.logger.ScoutLogManager;
import org.eclipse.scout.rt.client.AbstractClientSession;
import org.eclipse.scout.rt.client.ClientJob;
import org.eclipse.scout.rt.client.servicetunnel.http.ClientHttpServiceTunnel;
import org.eclipse.scout.rt.shared.services.common.code.CODES;
import org.eclipse.scout.rt.ui.rap.IRwtEnvironment;
import org.eclipsescout.egt.client.ui.desktop.Desktop;

public class ClientSession extends AbstractClientSession {
  private static IScoutLogger logger = ScoutLogManager.getLogger(ClientSession.class);

  private static IRwtEnvironment m_uiEnvironment;

  public ClientSession() {
    super(true);
  }

  /**
   * @return session in current ThreadContext
   */
  public static ClientSession get() {
    return ClientJob.getCurrentSession(ClientSession.class);
  }

  @Override
  public void execLoadSession() throws ProcessingException {

    UICallBack.activate("callback id");

    setServiceTunnel(new ClientHttpServiceTunnel(this, UriUtility.toUrl(getBundle().getBundleContext().getProperty("server.url"))));

    //pre-load all known code types
    CODES.getAllCodeTypes(org.eclipsescout.egt.shared.Activator.PLUGIN_ID);

    setDesktop(new Desktop());

    // turn client notification polling on
    // getServiceTunnel().setClientNotificationPollInterval(2000L);
  }

  @Override
  public void execStoreSession() throws ProcessingException {
    UICallBack.deactivate("callback id");
  }

  public static IRwtEnvironment getEnvironment() {
    return m_uiEnvironment;
  }

  public static void setEnvironment(IRwtEnvironment uiEnvironment) {
    m_uiEnvironment = uiEnvironment;
  }
}
