package org.eclipse.scout.apps.egt.shared.graph;

import org.eclipse.scout.rt.platform.Bean;
import org.eclipse.scout.rt.platform.exception.ProcessingException;
import org.eclipse.scout.rt.platform.service.IService;

@Bean
public interface IEgtGraphPageService extends IService {

	EgtGraphTablePageData getPageData(GraphSearchFormData searchFormData) throws ProcessingException;
}
