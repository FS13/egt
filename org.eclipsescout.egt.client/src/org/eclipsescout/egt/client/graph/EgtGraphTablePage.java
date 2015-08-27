/**
 *
 */
package org.eclipsescout.egt.client.graph;

import java.util.Set;

import org.eclipse.scout.commons.CollectionUtility;
import org.eclipse.scout.commons.annotations.Order;
import org.eclipse.scout.commons.annotations.PageData;
import org.eclipse.scout.commons.exception.ProcessingException;
import org.eclipse.scout.rt.client.ui.action.menu.IMenuType;
import org.eclipse.scout.rt.client.ui.action.menu.TableMenuType;
import org.eclipse.scout.rt.client.ui.basic.table.AbstractTable;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractLongColumn;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractStringColumn;
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.AbstractPageWithTable;
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.ISearchForm;
import org.eclipse.scout.rt.client.ui.messagebox.MessageBox;
import org.eclipse.scout.rt.extension.client.ui.action.menu.AbstractExtensibleMenu;
import org.eclipse.scout.rt.shared.TEXTS;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;
import org.eclipse.scout.service.SERVICES;
import org.eclipsescout.egt.client.graph.EgtGraphTablePage.Table;
import org.eclipsescout.egt.shared.graph.EgtGraphTablePageData;
import org.eclipsescout.egt.shared.graph.GraphSearchFormData;
import org.eclipsescout.egt.shared.graph.IEgtGraphPageService;
import org.eclipsescout.egt.shared.graph.IEgtGraphProcessService;

/**
 * @author user
 */
@PageData(value = EgtGraphTablePageData.class)
public class EgtGraphTablePage extends AbstractPageWithTable<Table> {

  @Override
  protected String getConfiguredTitle() {
    return TEXTS.get("Graph");
  }

  @Override
  protected void execLoadData(SearchFilter filter) throws ProcessingException {
    GraphSearchFormData searchFormData = (GraphSearchFormData) filter.getFormData();
    if (searchFormData == null) {
      searchFormData = new GraphSearchFormData();
    }
    EgtGraphTablePageData pageData = SERVICES.getService(IEgtGraphPageService.class).getPageData(searchFormData);
    importPageData(pageData);
  }

  @Order(10.0)
  public class Table extends AbstractTable {

    public GraphNrColumn getGraphNrColumn() {
      return getColumnSet().getColumnByClass(GraphNrColumn.class);
    }

    public GraphNameColumn getGraphNameColumn() {
      return getColumnSet().getColumnByClass(GraphNameColumn.class);
    }

    @Order(10.0)
    public class GraphNrColumn extends AbstractLongColumn {

      @Override
      protected boolean getConfiguredDisplayable() {
        return false;
      }

    }

    @Order(20.0)
    public class GraphNameColumn extends AbstractStringColumn {

      @Override
      protected boolean getConfiguredDisplayable() {
        return true;
      }

      @Override
      protected String getConfiguredHeaderText() {
        return TEXTS.get("Name");
      }

      @Override
      protected int getConfiguredWidth() {
        return 200;
      }

    }

    @Order(210.0)
    public class NewMenu extends AbstractExtensibleMenu {

      @Override
      protected String getConfiguredText() {
        return TEXTS.get("New_");
      }

      @Override
      protected Set<? extends IMenuType> getConfiguredMenuTypes() {
        return CollectionUtility.hashSet(TableMenuType.Header, TableMenuType.EmptySpace);
      }

      @Override
      public void execAction() throws ProcessingException {
        EgtGraphForm form = new EgtGraphForm();
        form.startNew();
        form.waitFor();

        if (form.isFormStored()) {
          reloadPage();
        }
      }

    }

    @Order(210.0)
    public class EditMenu extends AbstractExtensibleMenu {

      @Override
      protected String getConfiguredText() {
        return TEXTS.get("Edit_");
      }

      @Override
      protected Set<? extends IMenuType> getConfiguredMenuTypes() {
        return CollectionUtility.hashSet(TableMenuType.SingleSelection);
      }

      @Override
      public void execAction() throws ProcessingException {
        EgtGraphForm form = new EgtGraphForm();
        form.setGraphNr(getGraphNrColumn().getSelectedValue());
        form.startModify();
        form.waitFor();

        if (form.isFormStored()) {
          reloadPage();
        }
      }

    }

    @Order(220.0)
    public class Seperator1Menu extends AbstractExtensibleMenu {
      @Override
      protected boolean getConfiguredSeparator() {
        return true;
      }

    }

    @Order(230.0)
    public class CopyMenu extends AbstractExtensibleMenu {

      @Override
      protected String getConfiguredText() {
        return TEXTS.get("Copy_");
      }

      @Override
      protected Set<? extends IMenuType> getConfiguredMenuTypes() {
        return CollectionUtility.hashSet(TableMenuType.SingleSelection);
      }

      @Override
      public void execAction() throws ProcessingException {
        EgtGraphForm form = new EgtGraphForm();
        form.setGraphNr(getGraphNrColumn().getSelectedValue());
        form.startCopy();
        form.waitFor();

        if (form.isFormStored()) {
          reloadPage();
        }
      }

    }

    @Order(240.0)
    public class Seperator2Menu extends AbstractExtensibleMenu {
      @Override
      protected boolean getConfiguredSeparator() {
        return true;
      }

    }

    @Order(250.0)
    public class DeleteMenu extends AbstractExtensibleMenu {

      @Override
      protected String getConfiguredText() {
        return TEXTS.get("Delete");
      }

      @Override
      protected Set<? extends IMenuType> getConfiguredMenuTypes() {
        return CollectionUtility.hashSet(TableMenuType.SingleSelection, TableMenuType.MultiSelection);
      }

      @Override
      public void execAction() throws ProcessingException {
        if (MessageBox.showDeleteConfirmationMessage(TEXTS.get("Graphs"), getGraphNameColumn().getSelectedValues())) {
          IEgtGraphProcessService service = SERVICES.getService(IEgtGraphProcessService.class);
          int del = service.delete(getGraphNrColumn().getSelectedValues());
          if (del > 0) {
            reloadPage();
          }
        }
      }
    }

  }

  @Override
  protected Class<? extends ISearchForm> getConfiguredSearchForm() {
    return GraphSearchForm.class;
  }

}
