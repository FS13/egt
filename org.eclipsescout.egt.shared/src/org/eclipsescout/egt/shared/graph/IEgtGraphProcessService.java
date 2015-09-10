/**
 *
 */
package org.eclipsescout.egt.shared.graph;

import java.util.List;

import org.eclipse.scout.commons.exception.ProcessingException;
import org.eclipse.scout.service.IService;

/**
 * @author user
 */
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
  EgtGraphFormData load(EgtGraphFormData formData) throws ProcessingException;

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
  EgtGraphFormData store(EgtGraphFormData formData, int numberOfVertices) throws ProcessingException;

  /**
   * @param graphNr
   * @return
   * @throws ProcessingException
   */
  int delete(Long graphNr) throws ProcessingException;

  /**
   * @param graphNrs
   * @return
   * @throws ProcessingException
   */
  int delete(List<Long> graphNrs) throws ProcessingException;

  /**
   * @param graphNr
   * @return
   * @throws ProcessingException
   */
  String getSvgTextForGraph(Long graphNr) throws ProcessingException;

  /**
   * @return
   * @throws ProcessingException
   */
  int getMaxNumberOfVertices() throws ProcessingException;
}
