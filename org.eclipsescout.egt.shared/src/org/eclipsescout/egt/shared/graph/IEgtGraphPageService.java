/**
 *
 */
package org.eclipsescout.egt.shared.graph;

import org.eclipse.scout.commons.exception.ProcessingException;
import org.eclipse.scout.service.IService;

/**
 * @author user
 */
public interface IEgtGraphPageService extends IService {

  EgtGraphTablePageData getPageData(GraphSearchFormData searchFormData) throws ProcessingException;
}
