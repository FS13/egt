package org.eclipse.scout.apps.egt.client.graph;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.scout.apps.egt.client.ui.desktop.forms.IEgtPageForm;
import org.eclipse.scout.apps.egt.client.ui.fields.AbstractDoubleColumn;
import org.eclipse.scout.apps.egt.client.ui.fields.AbstractDoubleField;
import org.eclipse.scout.apps.egt.shared.graph.EgtGraph;
import org.eclipse.scout.apps.egt.shared.graph.EgtGraphLookupCall;
import org.eclipse.scout.apps.egt.shared.graph.EgtGraphStorage.GraphInformation;
import org.eclipse.scout.apps.egt.shared.graph.EgtSpeciesCodeType;
import org.eclipse.scout.apps.egt.shared.graph.EgtSpeciesCodeType.IEgtSpeciesCode;
import org.eclipse.scout.apps.egt.shared.graph.IEgtGraphProcessService;
import org.eclipse.scout.apps.egt.shared.graph.IndexMapList;
import org.eclipse.scout.rt.client.session.ClientSessionProvider;
import org.eclipse.scout.rt.client.ui.action.keystroke.IKeyStroke;
import org.eclipse.scout.rt.client.ui.action.menu.AbstractMenu;
import org.eclipse.scout.rt.client.ui.action.menu.IMenuType;
import org.eclipse.scout.rt.client.ui.action.menu.TableMenuType;
import org.eclipse.scout.rt.client.ui.basic.table.AbstractTable;
import org.eclipse.scout.rt.client.ui.basic.table.ITableRow;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractColumn;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractStringColumn;
import org.eclipse.scout.rt.client.ui.basic.table.columns.IColumn;
import org.eclipse.scout.rt.client.ui.form.AbstractFormHandler;
import org.eclipse.scout.rt.client.ui.form.fields.IFormField;
import org.eclipse.scout.rt.client.ui.form.fields.IValueField;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractButton;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractCloseButton;
import org.eclipse.scout.rt.client.ui.form.fields.groupbox.AbstractGroupBox;
import org.eclipse.scout.rt.client.ui.form.fields.listbox.AbstractListBox;
import org.eclipse.scout.rt.client.ui.form.fields.sequencebox.AbstractSequenceBox;
import org.eclipse.scout.rt.client.ui.form.fields.smartfield.AbstractSmartField;
import org.eclipse.scout.rt.client.ui.form.fields.tablefield.AbstractTableField;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.IOrdered;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.exception.ProcessingException;
import org.eclipse.scout.rt.platform.extension.InjectFieldTo;
import org.eclipse.scout.rt.platform.util.CollectionUtility;
import org.eclipse.scout.rt.platform.util.CompareUtility;
import org.eclipse.scout.rt.platform.util.NumberUtility;
import org.eclipse.scout.rt.platform.util.StringUtility;
import org.eclipse.scout.rt.platform.util.collection.OrderedCollection;
import org.eclipse.scout.rt.shared.TEXTS;
import org.eclipse.scout.rt.shared.services.common.code.ICode;
import org.eclipse.scout.rt.shared.services.common.code.ICodeType;
import org.eclipse.scout.rt.shared.services.lookup.ILookupCall;
import org.eclipse.scout.rt.shared.services.lookup.ILookupRow;

import Jama.Matrix;

public class EgtGraphCalculationForm extends EgtGraphForm implements IEgtPageForm {

	private IndexMapList m_indexMapList;

	private List<IEgtSpeciesCode> m_speciesList;

	private boolean m_calculated = false;
	private boolean m_compared = false;

	public EgtGraphCalculationForm() throws ProcessingException {
		super();
	}

	@Override
	public void startPageForm() throws ProcessingException {
		startInternal(new PageFormHandler());
	}

	@Override
	public AbstractCloseButton getCloseButton() throws ProcessingException {
		return null;
	}

	public IndexMapList getIndexMapList() {
		return m_indexMapList;
	}

	public void setIndexMapList(IndexMapList list) {
		m_indexMapList = list;
	}

	public List<IEgtSpeciesCode> getSpeciesList() {
		return m_speciesList;
	}

	public void setSpeciesList(List<IEgtSpeciesCode> speciesList) {
		m_speciesList = speciesList;
	}

	public boolean getCalculated() {
		return m_calculated;
	}

	public void setCalculated(boolean calculated) {
		m_calculated = calculated;
	}

	public boolean getCompared() {
		return m_compared;
	}

	public void setCompared(boolean compared) {
		m_compared = compared;
	}

	@Override
	protected void execInitForm() throws ProcessingException {
		super.execInitForm();
		getGraphNameField().setVisible(false);
		getGraphDetailFormField().getInnerForm().getConfigurationBox().setVisible(false);

		for (IKeyStroke keyStroke : getGraphDetailFormField().getInnerForm().getMainBox().getKeyStrokes()) {
			keyStroke.setEnabled(false);
		}

		setIndexMapList(new IndexMapList());

		updateStates();

		setCalculated(false);
		setCompared(false);

		getOkButton().setVisible(false);
		getCancelButton().setVisible(false);
	}

	public ChoseGraphField getChoseGraphField() {
		return getFieldByClass(ChoseGraphField.class);
	}

	public ConfigurationBox getConfigurationBox() {
		return getFieldByClass(ConfigurationBox.class);
	}

	public AnalysisBox getAnalysisBox() {
		return getFieldByClass(AnalysisBox.class);
	}

	@InjectFieldTo(EgtGraphForm.MainBox.GroupBox.class)
	@Order(10.0)
	public class ChoseGraphField extends AbstractSmartField<GraphInformation> {

		@Override
		protected String getConfiguredLabel() {
			return TEXTS.get("Graph");
		}

		@Override
		protected Class<? extends ILookupCall<GraphInformation>> getConfiguredLookupCall() {
			return EgtGraphLookupCall.class;
		}

		@Override
		protected void execChangedValue() throws ProcessingException {
			super.execChangedValue();

			IEgtGraphProcessService service = BEANS.get(IEgtGraphProcessService.class);
			String svgText = service.getSvgTextForGraph(getValue());
			setSvgText(svgText);

			try {
				getGraphDetailFormField().getInnerForm().setGraph(GraphUtility.buildGraphFromSvgText(getSvgText()));
				getGraphDetailFormField().getInnerForm().populateGraph();
				for (ITableRow row : getGraphDetailFormField().getInnerForm().getVerticesTabBox().getVerticesTableField().getTable().getRows()) {
					getGraphDetailFormField().getInnerForm().getVerticesTabBox().getVerticesTableField().getSpeciesColumn().setValue(row, EgtSpeciesCodeType.GreyCode.ID);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

			getConfigurationBox().getSpeciesBox().getCalculateForSpeciesField().setValue(null);
			getConfigurationBox().getSpeciesBox().getAdditionalSpeciesField().uncheckAllKeys();

			getConfigurationBox().getCompareSequenceBox().getCompareGraphField().setValue(null);
			getAnalysisBox().getProbabilityTableField().getTable().getComparisonFixationProbabilityColumn().setVisible(false);
			getAnalysisBox().getProbabilityTableField().getTable().getComparisonExtinctionProbabilityColumn().setVisible(false);

			setCalculated(false);
		}
	}

	@InjectFieldTo(EgtGraphDetailForm.MainBox.GroupBox.class)
	@Order(20.0)
	public class ConfigurationBox extends AbstractGroupBox {

		@Override
		protected int getConfiguredGridW() {
			return 1;
		}

		@Override
		protected int getConfiguredGridColumnCount() {
			return 1;
		}

		public FitnessBox getFitnessBox() {
			return getFieldByClass(FitnessBox.class);
		}

		public SpeciesBox getSpeciesBox() {
			return getFieldByClass(SpeciesBox.class);
		}

		public CompareSequenceBox getCompareSequenceBox() {
			return getFieldByClass(CompareSequenceBox.class);
		}

		@Order(10.0)
		public class SpeciesBox extends AbstractGroupBox {

			@Override
			protected int getConfiguredGridW() {
				return 1;
			}

			@Override
			protected int getConfiguredGridColumnCount() {
				return 1;
			}

			@Override
			protected int getConfiguredHeightInPixel() {
				return 200;
			}

			public CalculateForSpeciesField getCalculateForSpeciesField() {
				return getFieldByClass(CalculateForSpeciesField.class);
			}

			public AdditionalSpeciesField getAdditionalSpeciesField() {
				return getFieldByClass(AdditionalSpeciesField.class);
			}

			@Order(10.0)
			public class CalculateForSpeciesField extends AbstractSmartField<Long> {

				@Override
				protected String getConfiguredLabel() {
					return TEXTS.get("CalculateProbabilitiesFor");
				}

				@Override
				protected Class<? extends ICodeType<?, Long>> getConfiguredCodeType() {
					return EgtSpeciesCodeType.class;
				}

				@Override
				protected void execChangedValue() throws ProcessingException {
					super.execChangedValue();
					updateStates();
				}

			}

			@Order(20.0)
			public class AdditionalSpeciesField extends AbstractListBox<Long> {

				@Override
				protected String getConfiguredLabel() {
					return TEXTS.get("AdditionalSpeciesOnGraph");
				}

				@Override
				protected Class<? extends ICodeType<?, Long>> getConfiguredCodeType() {
					return EgtSpeciesCodeType.class;
				}

				@Override
				protected Class<? extends IValueField<?>> getConfiguredMasterField() {
					return CalculateForSpeciesField.class;
				}

				@Override
				protected void execChangedMasterValue(Object newMasterValue) throws ProcessingException {
					Set<Long> checkedKeys = getCheckedKeys();
					super.execChangedMasterValue(newMasterValue);
					loadListBoxData();
					checkedKeys.remove(newMasterValue);
					checkKeys(checkedKeys);
				}

				@Override
				protected void execFilterLookupResult(ILookupCall<Long> call, List<ILookupRow<Long>> result) throws ProcessingException {
					super.execFilterLookupResult(call, result);
					for (ILookupRow<Long> r : result) {
						if (CompareUtility.equals(r.getKey(), getCalculateForSpeciesField().getValue())) {
							result.remove(r);
							return;
						}
					}
				}

				@Override
				protected void execChangedValue() throws ProcessingException {
					super.execChangedValue();
					updateStates();
				}
			}
		}

		@Order(20.0)
		public class FitnessBox extends AbstractGroupBox {

			@Override
			protected void initConfig() {
				super.initConfig();
				for (ICode<Long> c : BEANS.get(EgtSpeciesCodeType.class).getCodes()) {
					addField(new FitnessColorBox((IEgtSpeciesCode) c) {
					});
				}
			}

			@Override
			protected int getConfiguredGridColumnCount() {
				return 2;
			}

			@Override
			protected int getConfiguredGridW() {
				return 2;
			}

			public FitnessColorBox getFitnessColorBoxByCode(IEgtSpeciesCode code) {
				for (IFormField field : getAllFields()) {
					if (field instanceof FitnessColorBox && CompareUtility.equals(((FitnessColorBox) field).getCode().getId(), code.getId())) {
						return (FitnessColorBox) field;
					}
				}
				return null;
			}

			private void computeScaledFitness() {
				Double fitnessSum = 0.0;
				for (ICode<Long> c : BEANS.get(EgtSpeciesCodeType.class).getCodes()) {
					if (getFitnessColorBoxByCode((IEgtSpeciesCode) c).isVisible()) {
						fitnessSum = fitnessSum + NumberUtility.nvl(getFitnessColorBoxByCode((IEgtSpeciesCode) c).getFitnessField().getValue(), 0.0);
					}
				}
				for (ICode<Long> c : BEANS.get(EgtSpeciesCodeType.class).getCodes()) {
					if (getFitnessColorBoxByCode((IEgtSpeciesCode) c).isVisible()) {
						getFitnessColorBoxByCode((IEgtSpeciesCode) c).getScaledFitnessField().setValue(fitnessSum == 0.0 ? 0.0 : NumberUtility.nvl(getFitnessColorBoxByCode((IEgtSpeciesCode) c).getFitnessField().getValue(), 0.0) / fitnessSum);
					}
				}
			}

			abstract class FitnessColorBox extends AbstractGroupBox {
				private final IEgtSpeciesCode m_code;

				public FitnessColorBox(IEgtSpeciesCode code) {
					super(false);
					m_code = code;
					callInitializer();
				}

				public IEgtSpeciesCode getCode() {
					return m_code;
				}

				@Override
				protected int getConfiguredGridColumnCount() {
					return 2;
				}

				@Override
				protected int getConfiguredGridW() {
					return 2;
				}

				@Override
				protected boolean getConfiguredBorderVisible() {
					return false;
				}

				public FitnessField getFitnessField() {
					return getFieldByClass(FitnessField.class);
				}

				public ScaledFitnessField getScaledFitnessField() {
					return getFieldByClass(ScaledFitnessField.class);
				}

				@Order(10.0)
				public class FitnessField extends AbstractDoubleField {

					@Override
					protected String getConfiguredLabel() {
						return TEXTS.get("FitnessValueFor", m_code.getText());
					}

					@Override
					protected void execChangedValue() throws ProcessingException {
						super.execChangedValue();
						computeScaledFitness();
					}
				}

				@Order(30.0)
				public class ScaledFitnessField extends AbstractDoubleField {

					@Override
					protected String getConfiguredLabel() {
						return TEXTS.get("Scaled");
					}

					@Override
					protected boolean getConfiguredEnabled() {
						return false;
					}

				}

			}

		}

		@Order(30.0)
		public class CalculateButton extends AbstractButton {

			@Override
			protected boolean getConfiguredProcessButton() {
				return false;
			}

			@Override
			protected String getConfiguredLabel() {
				return TEXTS.get("Calculate");
			}

			@Override
			protected int getConfiguredHorizontalAlignment() {
				return 0;
			}

			@Override
			protected void execClickAction() throws ProcessingException {
				super.execClickAction();
				startCalculation(getGraphDetailFormField().getInnerForm().getGraph(), false);
			}
		}

		@Order(40.0)
		public class CompareSequenceBox extends AbstractSequenceBox {
			String m_svgText;

			public CompareGraphField getCompareGraphField() {
				return getFieldByClass(CompareGraphField.class);
			}

			@Order(10.0)
			public class CompareGraphField extends AbstractSmartField<GraphInformation> {

				@Override
				public ILookupCall<GraphInformation> getLookupCall() {
					EgtGraphLookupCall call = ((EgtGraphLookupCall) super.getLookupCall());
					try {
						int numberOfVertices = getChoseGraphField().getValue().getNumberOfVertices();
						call.setNumberOfVertices(numberOfVertices);
					} catch (ProcessingException e) {
					}
					return call;
				}

				@Override
				protected Class<? extends ILookupCall<GraphInformation>> getConfiguredLookupCall() {
					return EgtGraphLookupCall.class;
				}

				@Override
				protected void execChangedValue() throws ProcessingException {
					super.execChangedValue();

					IEgtGraphProcessService service = BEANS.get(IEgtGraphProcessService.class);
					m_svgText = service.getSvgTextForGraph(getValue());

					getAnalysisBox().getProbabilityTableField().getTable().getComparisonFixationProbabilityColumn().setVisible(true);
					getAnalysisBox().getProbabilityTableField().getTable().getComparisonExtinctionProbabilityColumn().setVisible(true);

					setCompared(false);
				}

			}

			@Order(20.0)
			public class CompareButton extends AbstractButton {

				@Override
				protected boolean getConfiguredProcessButton() {
					return false;
				}

				@Override
				protected String getConfiguredLabel() {
					return TEXTS.get("Compare");
				}

				@Override
				protected int getConfiguredHorizontalAlignment() {
					return 0;
				}

				@Override
				protected void execClickAction() throws ProcessingException {
					super.execClickAction();
					try {
						startCalculation(GraphUtility.buildGraphFromSvgText(m_svgText), true);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

	}

	@InjectFieldTo(EgtGraphDetailForm.MainBox.GroupBox.class)
	@Order(30.0)
	public class AnalysisBox extends AbstractGroupBox {

		public ProbabilityTableField getProbabilityTableField() {
			return getFieldByClass(ProbabilityTableField.class);
		}

		@Override
		protected int getConfiguredGridColumnCount() {
			return 1;
		}

		@Order(10.0)
		public class ProbabilityTableField extends AbstractTableField<ProbabilityTableField.Table> {

			public void updateStates() throws ProcessingException {
				getTable().deleteAllRows();
				for (IEgtSpeciesCode code : getSpeciesList()) {
					getTable().getColorColumnByCode(code).setVisible(true);
				}
				for (ICode<Long> c : BEANS.get(EgtSpeciesCodeType.class).getCodes()) {
					IEgtSpeciesCode code = (IEgtSpeciesCode) c;
					if (!getSpeciesList().contains(code)) {
						getTable().getColorColumnByCode(code).setVisible(false);
					}
				}
				int order = 1;
				for (IEgtSpeciesCode code : getSpeciesList()) {
					getTable().getColorColumnByCode(code).setOrder(order);
					order++;
				}
				for (ICode<Long> c : BEANS.get(EgtSpeciesCodeType.class).getCodes()) {
					IEgtSpeciesCode code = (IEgtSpeciesCode) c;
					if (!getSpeciesList().contains(code)) {
						getTable().getColorColumnByCode(code).setOrder(IOrdered.DEFAULT_ORDER);
					}
				}

				for (int[] state : getIndexMapList().getAllColorStates()) {
					ITableRow row = getTable().createRow();
					getTable().getStateColumn().setValue(row, state);
					getTable().addRow(row);
				}
			}

			@Override
			protected boolean getConfiguredLabelVisible() {
				return false;
			}

			@Order(10.0)
			public class Table extends AbstractTable {

				@Override
				protected boolean getConfiguredHeaderEnabled() {
					return false;
				}

				@Override
				protected void injectColumnsInternal(OrderedCollection<IColumn<?>> columns) {
					for (ICode<Long> c : BEANS.get(EgtSpeciesCodeType.class).getCodes()) {
						columns.addOrdered(new ColorColumn((IEgtSpeciesCode) c) {
						});
					}
				}

				@Override
				public ITableRow addRow(ITableRow newRow) throws ProcessingException {
					int[] state = getTable().getStateColumn().getValue(newRow);
					for (int i = 0; i < state.length; i++) {
						getColorColumnByCode(getSpeciesList().get(i)).setValue(newRow, "" + state[i]);
					}
					return super.addRow(newRow);
				}

				public StateColumn getStateColumn() {
					return getColumnSet().getColumnByClass(StateColumn.class);
				}

				public ProbabilitiesColumn getProbabilitiesColumn() {
					return getColumnSet().getColumnByClass(ProbabilitiesColumn.class);
				}

				public ComparisonProbabilitiesColumn getComparisonProbabilitiesColumn() {
					return getColumnSet().getColumnByClass(ComparisonProbabilitiesColumn.class);
				}

				public ColorColumn getColorColumnByCode(IEgtSpeciesCode code) {
					for (IColumn<?> column : getColumns()) {
						if (column instanceof ColorColumn && CompareUtility.equals(((ColorColumn) column).getCode().getId(), code.getId())) {
							return (ColorColumn) column;
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

				public ComparisonFixationProbabilityColumn getComparisonFixationProbabilityColumn() {
					return getColumnSet().getColumnByClass(ComparisonFixationProbabilityColumn.class);
				}

				public ComparisonExtinctionProbabilityColumn getComparisonExtinctionProbabilityColumn() {
					return getColumnSet().getColumnByClass(ComparisonExtinctionProbabilityColumn.class);
				}

				@Order(5.0)
				public class StateColumn extends AbstractColumn<int[]> {

					@Override
					protected boolean getConfiguredDisplayable() {
						return false;
					}

				}

				@Order(6.0)
				public class ProbabilitiesColumn extends AbstractColumn<Matrix> {

					@Override
					protected boolean getConfiguredDisplayable() {
						return false;
					}

				}

				@Order(7.0)
				public class ComparisonProbabilitiesColumn extends AbstractColumn<Matrix> {

					@Override
					protected boolean getConfiguredDisplayable() {
						return false;
					}

				}

				abstract class ColorColumn extends AbstractStringColumn {

					private final IEgtSpeciesCode m_code;

					public ColorColumn(IEgtSpeciesCode code) {
						m_code = code;
						initConfig();
					}

					public IEgtSpeciesCode getCode() {
						return m_code;
					}

					@Override
					protected String getConfiguredHeaderText() {
						return m_code.getText();
					}

					@Override
					protected int getConfiguredWidth() {
						return 80;
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
					protected String getConfiguredBackgroundColor() {
						return StringUtility.substring(m_code.getEnum().getColor(), 1);
					}

					@Override
					protected String getConfiguredForegroundColor() {
						if (CompareUtility.equals(m_code.getId(), EgtSpeciesCodeType.BlackCode.ID)) {
							return StringUtility.substring(m_code.getEnum().getComplementaryColor(), 1);
						}
						return "000000";
					}

					@Override
					protected void initConfig() {
						if (!CompareUtility.equals(m_code, null)) {
							super.initConfig();
						}
					}

				}

				@Order(100.0)
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

				@Order(110.0)
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

				@Order(120.0)
				public class ComparisonFixationProbabilityColumn extends AbstractDoubleColumn {

					@Override
					protected String getConfiguredHeaderText() {
						return TEXTS.get("FixationProbabilityComparison");
					}

					@Override
					protected boolean getConfiguredVisible() {
						return false;
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

				@Order(130.0)
				public class ComparisonExtinctionProbabilityColumn extends AbstractDoubleColumn {

					@Override
					protected String getConfiguredHeaderText() {
						return TEXTS.get("ExtinctionProbabilityComparison");
					}

					@Override
					protected boolean getConfiguredVisible() {
						return false;
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
				public class DetailMenu extends AbstractMenu {

					@Override
					protected String getConfiguredText() {
						return TEXTS.get("Details_");
					}

					@Override
					protected Set<? extends IMenuType> getConfiguredMenuTypes() {
						return CollectionUtility.hashSet(TableMenuType.SingleSelection);
					}

					@Override
					protected void execOwnerValueChanged(Object newOwnerValue) {
						super.execOwnerValueChanged(newOwnerValue);
						setEnabled(getCalculated());
					}

					@Override
					public void execAction() throws ProcessingException {
						EgtGraphCalculationDetailProbabilitiesForm form = new EgtGraphCalculationDetailProbabilitiesForm(getGraphDetailFormField().getInnerForm().getGraph().getVertices().size(),
								getProbabilitiesColumn().getSelectedValue(),
								getIndexMapList().getAllStateIndicesForColorState(false, getStateColumn().getSelectedValue()),
								getIndexMapList().getAllIndexStatePairs());
						form.startDetails();
						form.waitFor();
					}

				}

				@Order(220.0)
				public class ComparisonDetailMenu extends AbstractMenu {

					@Override
					protected String getConfiguredText() {
						return TEXTS.get("DetailsComparison_");
					}

					@Override
					protected Set<? extends IMenuType> getConfiguredMenuTypes() {
						return CollectionUtility.hashSet(TableMenuType.SingleSelection);
					}

					@Override
					protected void execOwnerValueChanged(Object newOwnerValue) {
						super.execOwnerValueChanged(newOwnerValue);
						setEnabled(getCompared());
					}

					@Override
					public void execAction() throws ProcessingException {
						EgtGraphCalculationDetailProbabilitiesForm form = new EgtGraphCalculationDetailProbabilitiesForm(getGraphDetailFormField().getInnerForm().getGraph().getVertices().size(),
								getComparisonProbabilitiesColumn().getSelectedValue(),
								getIndexMapList().getAllStateIndicesForColorState(false, getStateColumn().getSelectedValue()),
								getIndexMapList().getAllIndexStatePairs());
						form.startDetails();
						form.waitFor();
					}

				}

			}

		}

	}

	public class PageFormHandler extends AbstractFormHandler {
	}

	private void updateStates() throws ProcessingException {
		if (!CompareUtility.equals(getGraphDetailFormField().getInnerForm().getGraph(), null)) {
			List<IEgtSpeciesCode> list = new ArrayList<IEgtSpeciesCode>();
			if (!CompareUtility.equals(getConfigurationBox().getSpeciesBox().getCalculateForSpeciesField().getValue(), null)) {
				list.add((IEgtSpeciesCode) BEANS.get(EgtSpeciesCodeType.class).getCode(getConfigurationBox().getSpeciesBox().getCalculateForSpeciesField().getValue()));
				for (Long key : getConfigurationBox().getSpeciesBox().getAdditionalSpeciesField().getCheckedKeys()) {
					list.add((IEgtSpeciesCode) BEANS.get(EgtSpeciesCodeType.class).getCode(key));
				}
				getIndexMapList().buildIndexMapList(getGraphDetailFormField().getInnerForm().getGraph().getVertices().size(), list);
			} else {
				setIndexMapList(new IndexMapList());
			}
			setSpeciesList(list);

			for (ICode<Long> c : BEANS.get(EgtSpeciesCodeType.class).getCodes()) {
				if (getSpeciesList().contains(c)) {
					getConfigurationBox().getFitnessBox().getFitnessColorBoxByCode((IEgtSpeciesCode) c).setVisible(true);
					double fitness = NumberUtility.nvl(getConfigurationBox().getFitnessBox().getFitnessColorBoxByCode((IEgtSpeciesCode) c).getFitnessField().getValue(), 0.0);
					getConfigurationBox().getFitnessBox().getFitnessColorBoxByCode((IEgtSpeciesCode) c).getFitnessField().setValue(fitness);
					getConfigurationBox().getFitnessBox().getFitnessColorBoxByCode((IEgtSpeciesCode) c).getScaledFitnessField().setEnabled(false);

				} else {
					getConfigurationBox().getFitnessBox().getFitnessColorBoxByCode((IEgtSpeciesCode) c).setVisible(false);
				}
			}

			getAnalysisBox().getProbabilityTableField().updateStates();
		} else {
			getAnalysisBox().getProbabilityTableField().getTable().deleteAllRows();
			for (ICode<Long> c : BEANS.get(EgtSpeciesCodeType.class).getCodes()) {
				getConfigurationBox().getFitnessBox().getFitnessColorBoxByCode((IEgtSpeciesCode) c).setVisible(false);
			}
		}

		setCalculated(false);
		setCompared(false);

	}

	private void startCalculation(EgtGraph graph, boolean isComparison) throws ProcessingException {
		EgtGraphCalculationThread cal = new EgtGraphCalculationThread(ClientSessionProvider.currentSession(), this, graph, getSpeciesList(), isComparison);
		cal.start();
	}

}
