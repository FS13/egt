package org.eclipse.scout.apps.egt.client.graph;

import java.util.List;

import org.eclipse.scout.apps.egt.client.graph.EgtGraphCalculationDetailProbabilitiesForm.MainBox.GroupBox.ProbabilityTableField.Table;
import org.eclipse.scout.apps.egt.client.ui.fields.AbstractDoubleColumn;
import org.eclipse.scout.apps.egt.shared.graph.EgtSpeciesCodeType;
import org.eclipse.scout.apps.egt.shared.graph.EgtSpeciesCodeType.IEgtSpeciesCode;
import org.eclipse.scout.apps.egt.shared.graph.IEgtGraphProcessService;
import org.eclipse.scout.apps.egt.shared.graph.IndexMapList.IndexStatePair;
import org.eclipse.scout.rt.client.ui.basic.cell.Cell;
import org.eclipse.scout.rt.client.ui.basic.table.AbstractTable;
import org.eclipse.scout.rt.client.ui.basic.table.ITableRow;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractColumn;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractStringColumn;
import org.eclipse.scout.rt.client.ui.basic.table.columns.IColumn;
import org.eclipse.scout.rt.client.ui.form.AbstractForm;
import org.eclipse.scout.rt.client.ui.form.AbstractFormHandler;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractCancelButton;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractOkButton;
import org.eclipse.scout.rt.client.ui.form.fields.groupbox.AbstractGroupBox;
import org.eclipse.scout.rt.client.ui.form.fields.tablefield.AbstractTableField;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.IOrdered;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.exception.ProcessingException;
import org.eclipse.scout.rt.platform.util.CompareUtility;
import org.eclipse.scout.rt.platform.util.TypeCastUtility;
import org.eclipse.scout.rt.platform.util.collection.OrderedCollection;
import org.eclipse.scout.rt.shared.TEXTS;
import org.eclipse.scout.rt.shared.services.common.code.CODES;

import Jama.Matrix;

public class EgtGraphCalculationDetailProbabilitiesForm extends AbstractForm {

	private int m_numberOfVertices;
	private Matrix m_probabilities;
	private List<Integer> m_stateIndices;
	private List<IndexStatePair> m_isPairList;

	public EgtGraphCalculationDetailProbabilitiesForm(int numberOfVertices, Matrix probabilities, List<Integer> stateIndices, List<IndexStatePair> isPairList) throws ProcessingException {
		m_numberOfVertices = numberOfVertices;
		m_probabilities = probabilities;
		m_stateIndices = stateIndices;
		m_isPairList = isPairList;
	}

	public int getNumberOfVertices() {
		return m_numberOfVertices;
	}

	public void setNumberOfVertices(int numberOfVertices) {
		m_numberOfVertices = numberOfVertices;
	}

	public Matrix getProbabilities() {
		return m_probabilities;
	}

	public void setProbabilities(Matrix probabilities) {
		m_probabilities = probabilities;
	}

	public List<Integer> getStateIndices() {
		return m_stateIndices;
	}

	public void setStateIndices(List<Integer> stateIndices) {
		m_stateIndices = stateIndices;
	}

	public List<IndexStatePair> getISPairList() {
		return m_isPairList;
	}

	public void setISPairList(List<IndexStatePair> isPairList) {
		m_isPairList = isPairList;
	}

	protected void setColumnsVisible() throws ProcessingException {
		for (int i = 1; i <= getNumberOfVertices(); i++) {
			getMainBox().getGroupBox().getProbabilityTableField().getTable().getIndividualColumnByInt(i).setVisible(true);
		}
		int maxNumberOfVertices = BEANS.get(IEgtGraphProcessService.class).getMaxNumberOfVertices();
		for (int i = getNumberOfVertices() + 1; i <= maxNumberOfVertices; i++) {
			getMainBox().getGroupBox().getProbabilityTableField().getTable().getIndividualColumnByInt(i).setVisible(false);
		}
		int order = 1;
		for (int i = 1; i <= getNumberOfVertices(); i++) {
			getMainBox().getGroupBox().getProbabilityTableField().getTable().getIndividualColumnByInt(i).setOrder(order);
			order++;
		}
		for (int i = getNumberOfVertices() + 1; i <= maxNumberOfVertices; i++) {
			getMainBox().getGroupBox().getProbabilityTableField().getTable().getIndividualColumnByInt(i).setOrder(IOrdered.DEFAULT_ORDER);
		}
	}

	public void startDetails() throws ProcessingException {
		startInternal(new DetailHandler());
	}

	public MainBox getMainBox() {
		return getFieldByClass(MainBox.class);
	}

	@Order(10.0)
	public class MainBox extends AbstractGroupBox {

		public GroupBox getGroupBox() {
			return getFieldByClass(GroupBox.class);
		}

		@Override
		protected int getConfiguredHeightInPixel() {
			return 500;
		}

		@Override
		protected int getConfiguredGridColumnCount() {
			return 3;
		}

		@Order(10.0)
		public class GroupBox extends AbstractGroupBox {

			public ProbabilityTableField getProbabilityTableField() {
				return getFieldByClass(ProbabilityTableField.class);
			}

			@Order(10.0)
			public class ProbabilityTableField extends AbstractTableField<ProbabilityTableField.Table> {

				@Override
				protected boolean getConfiguredLabelVisible() {
					return false;
				}

				@Order(10.0)
				public class Table extends AbstractTable {

					@Override
					protected void injectColumnsInternal(OrderedCollection<IColumn<?>> columns) {
						int maxNumberOfVertices = 0;
						try {
							maxNumberOfVertices = BEANS.get(IEgtGraphProcessService.class).getMaxNumberOfVertices();
						} catch (ProcessingException e) {
							e.printStackTrace();
						}
						for (int i = 1; i <= maxNumberOfVertices; i++) {
							columns.addOrdered(new IndividualColumn(i) {
							});
						}
					}

					@Override
					public ITableRow addRow(ITableRow newRow) throws ProcessingException {
						IEgtSpeciesCode[] state = getTable().getStateColumn().getValue(newRow);
						if (!CompareUtility.equals(state, null)) {
							for (int i = 1; i < state.length; i++) {
								getTable().getIndividualColumnByInt(i).setValue(newRow, "" + i);
							}
						}
						return super.addRow(newRow);
					}

					@Override
					protected void execDecorateRow(ITableRow row) throws ProcessingException {
						EgtSpeciesCodeType speciesCodeType = CODES.getCodeType(EgtSpeciesCodeType.class);
						IEgtSpeciesCode[] state = getTable().getStateColumn().getValue(row);
						if (!CompareUtility.equals(state, null)) {
							for (int i = 1; i < state.length; i++) {
								((Cell) getTable().getCell(row, getTable().getIndividualColumnByInt(i))).setBackgroundColor(speciesCodeType.getCode(state[i].getId()).getBackgroundColor());
								((Cell) getTable().getCell(row, getTable().getIndividualColumnByInt(i))).setForegroundColor(speciesCodeType.getCode(state[i].getId()).getForegroundColor());
							}
						}
						super.execDecorateRow(row);
					}

					public StateColumn getStateColumn() {
						return getColumnSet().getColumnByClass(StateColumn.class);
					}

					public IndividualColumn getIndividualColumnByInt(int individual) {
						for (IColumn column : getColumns()) {
							if (column instanceof IndividualColumn && CompareUtility.equals(((IndividualColumn) column).getIndividual(), individual)) {
								return (IndividualColumn) column;
							}
						}
						return null;
					}

					public FixationProbabilityColumn getFixationProbabilityColumn() {
						return getColumnSet().getColumnByClass(FixationProbabilityColumn.class);
					}

					public ExtinctionProbabilityColumn getExtinctionProbabilityColumn() {
						return getColumnSet().getColumnByClass(ExtinctionProbabilityColumn.class);
					}

					@Order(5.0)
					public class StateColumn extends AbstractColumn<IEgtSpeciesCode[]> {

						@Override
						protected boolean getConfiguredDisplayable() {
							return false;
						}

					}

					abstract class IndividualColumn extends AbstractStringColumn {

						private final int m_individual;

						public IndividualColumn(int individual) {
							m_individual = individual;
							initConfig();
						}

						public int getIndividual() {
							return m_individual;
						}

						@Override
						protected String getConfiguredHeaderText() {
							return TypeCastUtility.castValue(m_individual, String.class);
						}

						@Override
						protected int getConfiguredWidth() {
							return 20;
						}

						@Override
						protected int getConfiguredHorizontalAlignment() {
							return 0;
						}

						@Override
						protected String getConfiguredFont() {
							return "BOLD-15";
						}

						@Override
						protected void initConfig() {
							if (!CompareUtility.equals(m_individual, null)) {
								super.initConfig();
							}
						}

					}

					@Order(200.0)
					public class FixationProbabilityColumn extends AbstractDoubleColumn {

						@Override
						protected String getConfiguredHeaderText() {
							return TEXTS.get("FixationProbability");
						}

						@Override
						protected int getConfiguredMaxFractionDigits() {
							return 5;
						}

						@Override
						protected boolean getConfiguredPercent() {
							return true;
						}

						@Override
						protected int getConfiguredMultiplier() {
							return 100;
						}

						@Override
						protected int getConfiguredWidth() {
							return 250;
						}

					}

					@Order(210.0)
					public class ExtinctionProbabilityColumn extends AbstractDoubleColumn {

						@Override
						protected String getConfiguredHeaderText() {
							return TEXTS.get("ExtinctionProbability");
						}

						@Override
						protected int getConfiguredMaxFractionDigits() {
							return 5;
						}

						@Override
						protected boolean getConfiguredPercent() {
							return true;
						}

						@Override
						protected int getConfiguredMultiplier() {
							return 100;
						}

						@Override
						protected int getConfiguredWidth() {
							return 250;
						}

					}

				}

			}
		}

		@Order(100.0)
		public class OkButton extends AbstractOkButton {
		}

		@Order(110.0)
		public class CancelButton extends AbstractCancelButton {
		}
	}

	public class DetailHandler extends AbstractFormHandler {

		@Override
		protected void execLoad() throws ProcessingException {
			Table table = getMainBox().getGroupBox().getProbabilityTableField().getTable();
			int firstStateIndex = getStateIndices().get(0);
			for (int j : getStateIndices()) {
				ITableRow r = table.createRow();
				table.getStateColumn().setValue(r, getISPairList().get(j).getState());
				table.getFixationProbabilityColumn().setValue(r, Math.abs(getProbabilities().get(j - firstStateIndex, 0)));
				table.getExtinctionProbabilityColumn().setValue(r, Math.abs(1 - getProbabilities().get(j - firstStateIndex, 0)));
				table.addRow(r);
			}
			setColumnsVisible();
		}

	}

}
