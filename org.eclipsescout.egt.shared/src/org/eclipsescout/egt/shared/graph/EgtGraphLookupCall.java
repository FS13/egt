/**
 * 
 */
package org.eclipsescout.egt.shared.graph;

import org.eclipse.scout.rt.shared.services.lookup.ILookupService;
import org.eclipse.scout.rt.shared.services.lookup.LookupCall;

/**
 * @author user
 */
public class EgtGraphLookupCall extends LookupCall<Long> {

  private static final long serialVersionUID = 1L;

  @Override
  protected Class<? extends ILookupService<Long>> getConfiguredService() {
    return IEgtGraphLookupService.class;
  }
}
