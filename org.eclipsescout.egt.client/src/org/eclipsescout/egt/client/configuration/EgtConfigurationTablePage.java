/**
 *
 */
package org.eclipsescout.egt.client.configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.scout.commons.CollectionUtility;
import org.eclipse.scout.commons.annotations.Order;
import org.eclipse.scout.commons.annotations.PageData;
import org.eclipse.scout.commons.exception.ProcessingException;
import org.eclipse.scout.rt.client.ui.action.menu.IMenuType;
import org.eclipse.scout.rt.client.ui.action.menu.TableMenuType;
import org.eclipse.scout.rt.client.ui.basic.table.AbstractTable;
import org.eclipse.scout.rt.client.ui.basic.table.ITableRow;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractColumn;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractStringColumn;
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.AbstractPageWithTable;
import org.eclipse.scout.rt.extension.client.ui.action.menu.AbstractExtensibleMenu;
import org.eclipse.scout.rt.shared.TEXTS;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;
import org.eclipsescout.egt.client.configuration.EgtConfigurationTablePage.Table;
import org.eclipsescout.egt.shared.configuration.AbstractParameter;
import org.eclipsescout.egt.shared.configuration.EgtConfigurationTablePageData;
import org.eclipsescout.egt.shared.configuration.MatlabFunctionsLocationParameter;

/**
 * @author Fritz Schinkel
 */
@PageData(value = EgtConfigurationTablePageData.class)
public class EgtConfigurationTablePage extends AbstractPageWithTable<Table> {

  @Override
  protected String getConfiguredTitle() {
    return TEXTS.get("Configuration");
  }

  private List<AbstractParameter> getParameterList() throws ProcessingException {
    List<AbstractParameter> list = new ArrayList<AbstractParameter>();

    MatlabFunctionsLocationParameter matlabFunctionsLocationParameter = new MatlabFunctionsLocationParameter();
    list.add(matlabFunctionsLocationParameter);

    return list;
  }

  @Override
  protected void execLoadData(SearchFilter filter) throws ProcessingException {
    for (AbstractParameter parameter : getParameterList()) {
      parameter.reloadValue();
      ITableRow row = getTable().createRow();
      getTable().getParameterColumn().setValue(row, parameter);
      getTable().getParameterNameColumn().setValue(row, TEXTS.get(parameter.getIdentifier()));
      getTable().getParameterValueColumn().setValue(row, parameter.getValue());
      getTable().addRow(row);
    }
  }

  @Order(10.0)
  public class Table extends AbstractTable {

    public ParameterColumn getParameterColumn() {
      return getColumnSet().getColumnByClass(ParameterColumn.class);
    }

    public ParameterNameColumn getParameterNameColumn() {
      return getColumnSet().getColumnByClass(ParameterNameColumn.class);
    }

    public ParameterValueColumn getParameterValueColumn() {
      return getColumnSet().getColumnByClass(ParameterValueColumn.class);
    }

    @Order(10.0)
    public class ParameterColumn extends AbstractColumn<AbstractParameter> {

      @Override
      protected boolean getConfiguredDisplayable() {
        return false;
      }

    }

    @Order(10.0)
    public class ParameterNameColumn extends AbstractStringColumn {

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

    @Order(20.0)
    public class ParameterValueColumn extends AbstractStringColumn {

      @Override
      protected boolean getConfiguredDisplayable() {
        return true;
      }

      @Override
      protected String getConfiguredHeaderText() {
        return TEXTS.get("Value");
      }

      @Override
      protected int getConfiguredWidth() {
        return 200;
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
        AbstractConfigurationForm form = new AbstractConfigurationForm(getParameterColumn().getSelectedValue());
        form.startModify();
        form.waitFor();

        if (form.isFormStored()) {
          reloadRow(getSelectedRow());
        }
      }

    }

    private void reloadRow(ITableRow row) throws ProcessingException {
      getParameterColumn().getValue(row).reloadValue();
      getParameterValueColumn().setValue(row, getParameterColumn().getValue(row).getValue());
    }

  }

}
