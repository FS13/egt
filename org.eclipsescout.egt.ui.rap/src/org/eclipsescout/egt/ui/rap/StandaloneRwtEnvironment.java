package org.eclipsescout.egt.ui.rap;

import org.eclipse.scout.rt.ui.rap.AbstractStandaloneRwtEnvironment;
import org.eclipsescout.egt.client.ClientSession;

public class StandaloneRwtEnvironment extends AbstractStandaloneRwtEnvironment {

  public StandaloneRwtEnvironment() {
    super(Activator.getDefault().getBundle(), ClientSession.class);
    ClientSession.setEnvironment(this);
  }
}
