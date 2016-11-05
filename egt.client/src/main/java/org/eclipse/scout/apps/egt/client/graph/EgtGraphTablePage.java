package org.eclipse.scout.apps.egt.client.graph;

import java.util.Set;

import org.eclipse.scout.apps.egt.client.graph.EgtGraphTablePage.Table;
import org.eclipse.scout.apps.egt.shared.graph.EgtGraphStorage.GraphInformation;
import org.eclipse.scout.apps.egt.shared.graph.EgtGraphTablePageData;
import org.eclipse.scout.apps.egt.shared.graph.GraphSearchFormData;
import org.eclipse.scout.apps.egt.shared.graph.IEgtGraphPageService;
import org.eclipse.scout.apps.egt.shared.graph.IEgtGraphProcessService;
import org.eclipse.scout.rt.client.dto.PageData;
import org.eclipse.scout.rt.client.ui.action.menu.AbstractMenu;
import org.eclipse.scout.rt.client.ui.action.menu.IMenuType;
import org.eclipse.scout.rt.client.ui.action.menu.TableMenuType;
import org.eclipse.scout.rt.client.ui.basic.table.AbstractTable;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractColumn;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractStringColumn;
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.AbstractPageWithTable;
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.ISearchForm;
import org.eclipse.scout.rt.client.ui.messagebox.MessageBoxes;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.exception.ProcessingException;
import org.eclipse.scout.rt.platform.util.CollectionUtility;
import org.eclipse.scout.rt.shared.TEXTS;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;

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
		EgtGraphTablePageData pageData = BEANS.get(IEgtGraphPageService.class).getPageData(searchFormData);
		importPageData(pageData);
	}

	@Override
	public boolean isLeaf() {
		return true;
	}

	@Order(10.0)
	public class Table extends AbstractTable {

		public GraphInformationColumn getGraphInformationColumn() {
			return getColumnSet().getColumnByClass(GraphInformationColumn.class);
		}

		public GraphNameColumn getGraphNameColumn() {
			return getColumnSet().getColumnByClass(GraphNameColumn.class);
		}

		@Order(10.0)
		public class GraphInformationColumn extends AbstractColumn<GraphInformation> {

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
		public class NewMenu extends AbstractMenu {

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
		public class EditMenu extends AbstractMenu {

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
				form.setGraphInformation(getGraphInformationColumn().getSelectedValue());
				form.startModify();
				form.waitFor();

				if (form.isFormStored()) {
					reloadPage();
				}
			}

		}

		@Order(220.0)
		public class Seperator1Menu extends AbstractMenu {
			@Override
			protected boolean getConfiguredSeparator() {
				return true;
			}

		}

		@Order(230.0)
		public class CopyMenu extends AbstractMenu {

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
				form.setGraphInformation(getGraphInformationColumn().getSelectedValue());
				form.startCopy();
				form.waitFor();

				if (form.isFormStored()) {
					reloadPage();
				}
			}

		}

		@Order(240.0)
		public class Seperator2Menu extends AbstractMenu {
			@Override
			protected boolean getConfiguredSeparator() {
				return true;
			}

		}

		@Order(250.0)
		public class DeleteMenu extends AbstractMenu {

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
				if (MessageBoxes.showDeleteConfirmationMessage(TEXTS.get("Graphs"), getGraphNameColumn().getSelectedValues())) {
					IEgtGraphProcessService service = BEANS.get(IEgtGraphProcessService.class);
					int del = service.delete(getGraphInformationColumn().getSelectedValues());
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
