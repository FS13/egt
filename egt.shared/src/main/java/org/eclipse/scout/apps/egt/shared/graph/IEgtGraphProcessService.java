package org.eclipse.scout.apps.egt.shared.graph;

import java.util.List;

import org.eclipse.scout.apps.egt.shared.graph.EgtGraphStorage.GraphInformation;
import org.eclipse.scout.rt.platform.Bean;
import org.eclipse.scout.rt.platform.exception.ProcessingException;
import org.eclipse.scout.rt.platform.service.IService;

@Bean
public interface IEgtGraphProcessService extends IService {

	/**
	 * @param formData
	 * @return
	 * @throws org.eclipse.scout.commons.exception.ProcessingException
	 */
	EgtGraphFormData create(EgtGraphFormData formData, int numberOfVertices) throws ProcessingException;

	/**
	 * @param formData
	 * @return
	 * @throws org.eclipse.scout.commons.exception.ProcessingException
	 */
	EgtGraphFormData load(GraphInformation graphInformation, EgtGraphFormData formData) throws ProcessingException;

	/**
	 * @param formData
	 * @return
	 * @throws org.eclipse.scout.commons.exception.ProcessingException
	 */
	EgtGraphFormData prepareCreate(EgtGraphFormData formData) throws ProcessingException;

	/**
	 * @param formData
	 * @return
	 * @throws org.eclipse.scout.commons.exception.ProcessingException
	 */
	EgtGraphFormData store(GraphInformation graphInformation, EgtGraphFormData formData, int numberOfVertices) throws ProcessingException;

	/**
	 * @param graphNr
	 * @return
	 * @throws ProcessingException
	 */
	int delete(GraphInformation graphInformation) throws ProcessingException;

	/**
	 * @param graphNrs
	 * @return
	 * @throws ProcessingException
	 */
	int delete(List<GraphInformation> graphInformation) throws ProcessingException;

	/**
	 * @param graphNr
	 * @return
	 * @throws ProcessingException
	 */
	String getSvgTextForGraph(GraphInformation graphInformation) throws ProcessingException;

	/**
	 * @return
	 * @throws ProcessingException
	 */
	int getMaxNumberOfVertices() throws ProcessingException;

}
