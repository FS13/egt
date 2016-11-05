package org.eclipse.scout.apps.egt.client.graph;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.scout.apps.egt.client.graph.EgtGraphDetailForm.MainBox.GroupBox.ConfigurationBox.EdgesTabBox.EdgesTableField;
import org.eclipse.scout.apps.egt.client.graph.EgtGraphDetailForm.MainBox.GroupBox.ConfigurationBox.VerticesTabBox.VerticesTableField;
import org.eclipse.scout.apps.egt.client.ui.desktop.forms.IEgtPageForm;
import org.eclipse.scout.apps.egt.client.ui.fields.AbstractDoubleField;
import org.eclipse.scout.apps.egt.shared.graph.EgtGraphLookupCall;
import org.eclipse.scout.apps.egt.shared.graph.EgtGraphStorage.GraphInformation;
import org.eclipse.scout.apps.egt.shared.graph.EgtSpeciesCodeType;
import org.eclipse.scout.apps.egt.shared.graph.EgtSpeciesCodeType.IEgtSpeciesCode;
import org.eclipse.scout.apps.egt.shared.graph.IEgtGraphProcessService;
import org.eclipse.scout.rt.client.session.ClientSessionProvider;
import org.eclipse.scout.rt.client.ui.action.menu.AbstractMenu;
import org.eclipse.scout.rt.client.ui.action.menu.IMenu;
import org.eclipse.scout.rt.client.ui.action.menu.IMenuType;
import org.eclipse.scout.rt.client.ui.action.menu.TableMenuType;
import org.eclipse.scout.rt.client.ui.basic.table.ITableRow;
import org.eclipse.scout.rt.client.ui.form.AbstractFormHandler;
import org.eclipse.scout.rt.client.ui.form.fields.IFormField;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractButton;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractCloseButton;
import org.eclipse.scout.rt.client.ui.form.fields.groupbox.AbstractGroupBox;
import org.eclipse.scout.rt.client.ui.form.fields.integerfield.AbstractIntegerField;
import org.eclipse.scout.rt.client.ui.form.fields.labelfield.AbstractLabelField;
import org.eclipse.scout.rt.client.ui.form.fields.smartfield.AbstractSmartField;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.exception.ProcessingException;
import org.eclipse.scout.rt.platform.extension.InjectFieldTo;
import org.eclipse.scout.rt.platform.util.CollectionUtility;
import org.eclipse.scout.rt.platform.util.CompareUtility;
import org.eclipse.scout.rt.platform.util.NumberUtility;
import org.eclipse.scout.rt.shared.TEXTS;
import org.eclipse.scout.rt.shared.services.common.code.CODES;
import org.eclipse.scout.rt.shared.services.common.code.ICode;
import org.eclipse.scout.rt.shared.services.lookup.ILookupCall;

public class EgtGraphSimulationForm extends EgtGraphForm implements IEgtPageForm {

	private boolean m_paused;
	private boolean m_stopped;

	public EgtGraphSimulationForm() throws ProcessingException {
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

	public boolean isPaused() {
		return m_paused;
	}

	public void setPaused(boolean paused) {
		m_paused = paused;
	}

	public boolean isStopped() {
		return m_stopped;
	}

	public void setStopped(boolean stopped) {
		m_stopped = stopped;
	}

	@Override
	protected void execInitForm() throws ProcessingException {
		super.execInitForm();
		getGraphNameField().setVisible(false);
		getGraphDetailFormField().getInnerForm().getConfigurationBox().setEnabled(false);
		getGraphDetailFormField().getInnerForm().getVerticesTabBox().getVertexConfigurationBox().setVisible(false);

		getGraphDetailFormField().getInnerForm().getVerticesTabBox().getVerticesTableField().getTable()
				.getMenuByClass(VerticesTableField.Table.NewMenu.class).setVisible(false);
		getGraphDetailFormField().getInnerForm().getVerticesTabBox().getVerticesTableField().getTable()
				.getMenuByClass(VerticesTableField.Table.DeleteMenu.class).setVisible(false);
		getGraphDetailFormField().getInnerForm().getVerticesTabBox().getVerticesTableField().getTable()
				.addMenu(new SetInitialSpeciesMenu());
		getGraphDetailFormField().getInnerForm().getVerticesTabBox().getVerticesTableField().getTable()
				.getMenuByClass(SetInitialSpeciesMenu.class).setEnabled(true);
		for (IMenu menu : getGraphDetailFormField().getInnerForm().getVerticesTabBox().getVerticesTableField().getTable()
				.getMenuByClass(SetInitialSpeciesMenu.class).getChildActions()) {
			menu.setEnabled(true);
		}

		getGraphDetailFormField().getInnerForm().getEdgesTabBox().getEdgesTableField().getTable()
				.getMenuByClass(EdgesTableField.Table.NewMenu.class).setVisible(false);
		getGraphDetailFormField().getInnerForm().getEdgesTabBox().getEdgesTableField().getTable()
				.getMenuByClass(EdgesTableField.Table.DeleteMenu.class).setVisible(false);

		for (ICode<Long> c : BEANS.get(EgtSpeciesCodeType.class).getCodes()) {
			getSimulationBox().getFitnessBox().getFitnessColorBoxByCode((IEgtSpeciesCode) c).getScaledFitnessField()
					.setEnabled(false);
		}
		changeVisibilityOfFitnessColors();

		setPaused(false);
		setStopped(true);

		getAnalysisBox().getTimeStepsField().setValue(0);

		getOkButton().setVisible(false);
		getCancelButton().setVisible(false);
	}

	private void changeVisibilityOfFitnessColors() {

		List<IEgtSpeciesCode> species = new ArrayList<IEgtSpeciesCode>();
		for (ITableRow row : getGraphDetailFormField().getInnerForm().getVerticesTabBox().getVerticesTableField().getTable()
				.getRows()) {
			IEgtSpeciesCode code = (IEgtSpeciesCode) BEANS.get(EgtSpeciesCodeType.class).getCode(BEANS.get(EgtSpeciesCodeType.class).getCodeIdByEnum(getGraphDetailFormField().getInnerForm().getVerticesTabBox().getVerticesTableField().getEgtGraphVertecColumn().getValue(row).getSpecies()));
			if (!species.contains(code)) {
				species.add(code);
			}
		}
		for (ICode<Long> c : BEANS.get(EgtSpeciesCodeType.class).getCodes()) {
			if (species.contains(c)) {
				getSimulationBox().getFitnessBox().getFitnessColorBoxByCode((IEgtSpeciesCode) c).setVisible(true);
				double fitness = NumberUtility.nvl(getSimulationBox().getFitnessBox().getFitnessColorBoxByCode((IEgtSpeciesCode) c).getFitnessField().getValue(), 0.0);
				getSimulationBox().getFitnessBox().getFitnessColorBoxByCode((IEgtSpeciesCode) c).getFitnessField().setValue(fitness);
				getSimulationBox().getFitnessBox().getFitnessColorBoxByCode((IEgtSpeciesCode) c).getScaledFitnessField().setEnabled(false);

				getAnalysisBox().getLifetimeAnalysisBox().getLifetimeAnalysisColorBoxByCode((IEgtSpeciesCode) c).setVisible(true);
			} else {
				getSimulationBox().getFitnessBox().getFitnessColorBoxByCode((IEgtSpeciesCode) c).setVisible(false);
				getAnalysisBox().getLifetimeAnalysisBox().getLifetimeAnalysisColorBoxByCode((IEgtSpeciesCode) c).setVisible(false);
			}
		}
		getSimulationBox().getFitnessBox().computeScaledFitness();
	}

	public ChoseGraphField getChoseGraphField() {
		return getFieldByClass(ChoseGraphField.class);
	}

	public SimulationBox getSimulationBox() {
		return getFieldByClass(SimulationBox.class);
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
				for (ITableRow row : getGraphDetailFormField().getInnerForm().getVerticesTabBox().getVerticesTableField()
						.getTable().getRows()) {
					getGraphDetailFormField().getInnerForm().getVerticesTabBox().getVerticesTableField().getSpeciesColumn()
							.setValue(row, EgtSpeciesCodeType.RedCode.ID);
				}

				getSimulationBox().setEnabled(true);
				getSimulationBox().getPauseSimulationButton().setEnabled(false);
				getSimulationBox().getStopSimulationButton().setEnabled(false);

				changeVisibilityOfFitnessColors();

				setPaused(false);
				setStopped(true);

				getAnalysisBox().getTimeStepsField().setValue(0);

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	@Order(220.0)
	public class SetInitialSpeciesMenu extends AbstractMenu {

		@Override
		protected void initConfig() {
			super.initConfig();
			List<IMenu> list = new ArrayList<>();
			for (ICode<Long> c : BEANS.get(EgtSpeciesCodeType.class).getCodes()) {
				list.add(new SetColorMenuInternal((IEgtSpeciesCode) c) {
				});
			}
			addChildActions(list);
		}

		@Override
		protected Set<? extends IMenuType> getConfiguredMenuTypes() {
			return CollectionUtility.hashSet(TableMenuType.SingleSelection, TableMenuType.MultiSelection);
		}

		@Override
		protected String getConfiguredText() {
			return TEXTS.get("SetInitialSpecies");
		}

		private abstract class SetColorMenuInternal extends AbstractMenu {
			private final IEgtSpeciesCode m_code;

			private SetColorMenuInternal(IEgtSpeciesCode code) {
				super(false);
				m_code = code;
				callInitializer();
			}

			@Override
			protected String getConfiguredText() {
				return m_code.getText();
			}

			@Override
			protected Set<? extends IMenuType> getConfiguredMenuTypes() {
				return CollectionUtility.hashSet(TableMenuType.SingleSelection, TableMenuType.MultiSelection);
			}

			@Override
			protected boolean getConfiguredInheritAccessibility() {
				return false;
			}

			@Override
			protected void execAction() throws ProcessingException {
				for (ITableRow row : getGraphDetailFormField().getInnerForm().getVerticesTabBox().getVerticesTableField()
						.getTable().getSelectedRows()) {
					getGraphDetailFormField().getInnerForm().getVerticesTabBox().getVerticesTableField().getSpeciesColumn()
							.setValue(row, m_code.getId());
				}
				changeVisibilityOfFitnessColors();
			}
		}
	}

	@Order(220.0)
	public class SetInitialSpeciesRedMenu extends AbstractMenu {

		@Override
		protected String getConfiguredText() {
			return TEXTS.get("SetInitialSpecies", CODES.getCode(EgtSpeciesCodeType.RedCode.class).getText());
		}

		@Override
		protected Set<? extends IMenuType> getConfiguredMenuTypes() {
			return CollectionUtility.hashSet(TableMenuType.SingleSelection, TableMenuType.MultiSelection);
		}

		@Override
		public void execAction() throws ProcessingException {

			for (ITableRow row : getGraphDetailFormField().getInnerForm().getVerticesTabBox().getVerticesTableField()
					.getTable().getSelectedRows()) {
				getGraphDetailFormField().getInnerForm().getVerticesTabBox().getVerticesTableField().getSpeciesColumn()
						.setValue(row, EgtSpeciesCodeType.RedCode.ID);
			}

		}

	}

	@Order(230.0)
	public class SetInitialSpeciesBlueMenu extends AbstractMenu {

		@Override
		protected String getConfiguredText() {
			return TEXTS.get("SetInitialSpecies", CODES.getCode(EgtSpeciesCodeType.BlueCode.class).getText());
		}

		@Override
		protected Set<? extends IMenuType> getConfiguredMenuTypes() {
			return CollectionUtility.hashSet(TableMenuType.SingleSelection, TableMenuType.MultiSelection);
		}

		@Override
		public void execAction() throws ProcessingException {

			for (ITableRow row : getGraphDetailFormField().getInnerForm().getVerticesTabBox().getVerticesTableField()
					.getTable().getSelectedRows()) {
				getGraphDetailFormField().getInnerForm().getVerticesTabBox().getVerticesTableField().getSpeciesColumn()
						.setValue(row, EgtSpeciesCodeType.BlueCode.ID);
			}

		}

	}

	@InjectFieldTo(EgtGraphDetailForm.MainBox.GroupBox.ConfigurationBox.VerticesTabBox.class)
	@Order(20.0)
	public class SimulationBox extends AbstractGroupBox {

		public StartSimulationButton getStartSimulationButton() {
			return getFieldByClass(StartSimulationButton.class);
		}

		public PauseSimulationButton getPauseSimulationButton() {
			return getFieldByClass(PauseSimulationButton.class);
		}

		public StopSimulationButton getStopSimulationButton() {
			return getFieldByClass(StopSimulationButton.class);
		}

		public FitnessBox getFitnessBox() {
			return getFieldByClass(FitnessBox.class);
		}

		@Override
		protected int getConfiguredGridColumnCount() {
			return 4;
		}

		@Order(10.0)
		public class StartSimulationButton extends AbstractButton {

			@Override
			protected String getConfiguredLabel() {
				return "\u25b6";
			}

			@Override
			protected int getConfiguredHeightInPixel() {
				return 50;
			}

			@Override
			protected int getConfiguredWidthInPixel() {
				return 50;
			}

			@Override
			protected boolean getConfiguredGridUseUiHeight() {
				return true;
			}

			@Override
			protected double getConfiguredGridWeightX() {
				return 0;
			}

			@Override
			protected double getConfiguredGridWeightY() {
				return 0;
			}

			@Override
			protected boolean getConfiguredFillVertical() {
				return false;
			}

			@Override
			protected boolean getConfiguredProcessButton() {
				return false;
			}

			@Override
			protected String getConfiguredFont() {
				return "25";
			}

			@Override
			protected void execClickAction() throws ProcessingException {
				super.execClickAction();
				boolean startNew = !isPaused() && isStopped();
				setEnabled(false);
				setPaused(false);
				getPauseSimulationButton().setEnabled(true);
				setStopped(false);
				getStopSimulationButton().setEnabled(true);
				startSimulation(startNew);
			}

		}

		@Order(20.0)
		public class PauseSimulationButton extends AbstractButton {

			@Override
			protected String getConfiguredLabel() {
				return "\u23f8";
			}

			@Override
			protected int getConfiguredHeightInPixel() {
				return 50;
			}

			@Override
			protected int getConfiguredWidthInPixel() {
				return 50;
			}

			@Override
			protected boolean getConfiguredGridUseUiHeight() {
				return true;
			}

			@Override
			protected double getConfiguredGridWeightX() {
				return 0;
			}

			@Override
			protected double getConfiguredGridWeightY() {
				return 0;
			}

			@Override
			protected boolean getConfiguredFillVertical() {
				return false;
			}

			@Override
			protected boolean getConfiguredProcessButton() {
				return false;
			}

			@Override
			protected String getConfiguredFont() {
				return "25";
			}

			@Override
			protected void execClickAction() throws ProcessingException {
				super.execClickAction();
				getStartSimulationButton().setEnabled(true);
				setPaused(true);
				setEnabled(false);
				setStopped(false);
				getStopSimulationButton().setEnabled(true);
			}

		}

		@Order(30.0)
		public class StopSimulationButton extends AbstractButton {

			@Override
			protected String getConfiguredLabel() {
				return "\u23f9";
			}

			@Override
			protected int getConfiguredHeightInPixel() {
				return 50;
			}

			@Override
			protected int getConfiguredWidthInPixel() {
				return 50;
			}

			@Override
			protected boolean getConfiguredGridUseUiHeight() {
				return true;
			}

			@Override
			protected double getConfiguredGridWeightX() {
				return 0;
			}

			@Override
			protected double getConfiguredGridWeightY() {
				return 0;
			}

			@Override
			protected boolean getConfiguredFillVertical() {
				return false;
			}

			@Override
			protected boolean getConfiguredProcessButton() {
				return false;
			}

			@Override
			protected String getConfiguredFont() {
				return "25";
			}

			@Override
			protected void execClickAction() throws ProcessingException {
				super.execClickAction();
				getStartSimulationButton().setEnabled(true);
				setPaused(false);
				getPauseSimulationButton().setEnabled(false);
				setStopped(true);
				setEnabled(false);
			}

		}

		@Order(40.0)
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
				return 1;
			}

			@Override
			protected boolean getConfiguredBorderVisible() {
				return false;
			}

			public FitnessColorBox getFitnessColorBoxByCode(IEgtSpeciesCode code) {
				for (IFormField field : getFields()) {
					if (field instanceof FitnessColorBox
							&& CompareUtility.equals(((FitnessColorBox) field).getCode().getId(), code.getId())) {
						return (FitnessColorBox) field;
					}
				}
				return null;
			}

			private void computeScaledFitness() {
				Double fitnessSum = 0.0;
				for (ICode<Long> c : BEANS.get(EgtSpeciesCodeType.class).getCodes()) {
					if (getFitnessColorBoxByCode((IEgtSpeciesCode) c).isVisible()) {
						fitnessSum = fitnessSum
								+ NumberUtility.nvl(getFitnessColorBoxByCode((IEgtSpeciesCode) c).getFitnessField().getValue(), 0.0);
					}
				}
				for (ICode<Long> c : BEANS.get(EgtSpeciesCodeType.class).getCodes()) {
					if (getFitnessColorBoxByCode((IEgtSpeciesCode) c).isVisible()) {
						getFitnessColorBoxByCode((IEgtSpeciesCode) c).getScaledFitnessField()
								.setValue(fitnessSum == 0.0 ? 0.0
										: NumberUtility.nvl(getFitnessColorBoxByCode((IEgtSpeciesCode) c).getFitnessField().getValue(), 0.0)
												/ fitnessSum);
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
	}

	@InjectFieldTo(EgtGraphDetailForm.MainBox.GroupBox.ConfigurationBox.VerticesTabBox.class)
	@Order(30.0)
	public class AnalysisBox extends AbstractGroupBox {

		public TimeStepsField getTimeStepsField() {
			return getFieldByClass(TimeStepsField.class);
		}

		public LifetimeAnalysisBox getLifetimeAnalysisBox() {
			return getFieldByClass(LifetimeAnalysisBox.class);
		}

		@Override
		protected int getConfiguredGridColumnCount() {
			return 1;
		}

		@Order(10.0)
		public class TimeStepsField extends AbstractIntegerField {

			@Override
			protected String getConfiguredLabel() {
				return TEXTS.get("Timesteps");
			}

			@Override
			protected boolean getConfiguredEnabled() {
				return false;
			}

		}

		@Order(20.0)
		public class LifetimeAnalysisBox extends AbstractGroupBox {

			@Override
			protected void initConfig() {
				super.initConfig();
				for (ICode<Long> c : BEANS.get(EgtSpeciesCodeType.class).getCodes()) {
					addField(new LifetimeAnalysisColorBox((IEgtSpeciesCode) c) {
					});
				}
			}

			@Override
			protected int getConfiguredGridColumnCount() {
				return 1;
			}

			@Override
			protected int getConfiguredGridW() {
				return 2;
			}

			@Override
			protected boolean getConfiguredBorderVisible() {
				return false;
			}

			public LifetimeAnalysisColorBox getLifetimeAnalysisColorBoxByCode(IEgtSpeciesCode code) {
				for (IFormField field : getAllFields()) {
					if (field instanceof LifetimeAnalysisColorBox && CompareUtility.equals(((LifetimeAnalysisColorBox) field).getCode().getId(), code.getId())) {
						return (LifetimeAnalysisColorBox) field;
					}
				}
				return null;
			}

			abstract class LifetimeAnalysisColorBox extends AbstractGroupBox {
				private final IEgtSpeciesCode m_code;

				public LifetimeAnalysisColorBox(IEgtSpeciesCode code) {
					super(false);
					m_code = code;
					callInitializer();
				}

				public IEgtSpeciesCode getCode() {
					return m_code;
				}

				@Override
				protected int getConfiguredGridColumnCount() {
					return 4;
				}

				@Override
				protected int getConfiguredGridW() {
					return 2;
				}

				@Override
				protected boolean getConfiguredBorderVisible() {
					return false;
				}

				public ColorNameField getColorNameField() {
					return getFieldByClass(ColorNameField.class);
				}

				public AliveUntilField getAliveUntilField() {
					return getFieldByClass(AliveUntilField.class);
				}

				public MaxNumberOfInividualsField getMaxNumberOfIndividualsField() {
					return getFieldByClass(MaxNumberOfInividualsField.class);
				}

				public MinNumberOfIndividualsField getMinNumberOfIndividualsField() {
					return getFieldByClass(MinNumberOfIndividualsField.class);
				}

				@Order(10.0)
				public class ColorNameField extends AbstractLabelField {

					@Override
					protected String getConfiguredLabel() {
						return m_code.getText() + ":";
					}

				}

				@Order(20.0)
				public class AliveUntilField extends AbstractIntegerField {

					@Override
					protected String getConfiguredLabel() {
						return TEXTS.get("AliveUntil");
					}

					@Override
					protected boolean getConfiguredEnabled() {
						return false;
					}

				}

				@Order(30.0)
				public class MaxNumberOfInividualsField extends AbstractIntegerField {

					@Override
					protected String getConfiguredLabel() {
						return TEXTS.get("MaxNumberOfIndividuals");
					}

					@Override
					protected boolean getConfiguredEnabled() {
						return false;
					}

				}

				@Order(40.0)
				public class MinNumberOfIndividualsField extends AbstractIntegerField {

					@Override
					protected String getConfiguredLabel() {
						return TEXTS.get("MinNumberOfIndividuals");
					}

					@Override
					protected boolean getConfiguredEnabled() {
						return false;
					}

				}

			}

		}

	}

	public class PageFormHandler extends AbstractFormHandler {
	}

	private void startSimulation(boolean startNew) throws ProcessingException {
		EgtGraphSimulationThread sim = new EgtGraphSimulationThread(ClientSessionProvider.currentSession(), this, startNew);
		sim.start();
	}

}
