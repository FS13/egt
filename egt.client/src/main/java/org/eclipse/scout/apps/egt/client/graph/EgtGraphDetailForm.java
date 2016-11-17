package org.eclipse.scout.apps.egt.client.graph;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.scout.apps.egt.client.graph.EgtGraphDetailForm.MainBox.GroupBox;
import org.eclipse.scout.apps.egt.client.graph.EgtGraphDetailForm.MainBox.GroupBox.ConfigurationBox;
import org.eclipse.scout.apps.egt.client.graph.EgtGraphDetailForm.MainBox.GroupBox.ConfigurationBox.EdgesTabBox;
import org.eclipse.scout.apps.egt.client.graph.EgtGraphDetailForm.MainBox.GroupBox.ConfigurationBox.EdgesTabBox.EdgesTableField.Table.EgtGraphWeightedDirectedEdgeColumn;
import org.eclipse.scout.apps.egt.client.graph.EgtGraphDetailForm.MainBox.GroupBox.ConfigurationBox.EdgesTabBox.EdgesTableField.Table.FromColumn;
import org.eclipse.scout.apps.egt.client.graph.EgtGraphDetailForm.MainBox.GroupBox.ConfigurationBox.EdgesTabBox.EdgesTableField.Table.ScaledWeightColumn;
import org.eclipse.scout.apps.egt.client.graph.EgtGraphDetailForm.MainBox.GroupBox.ConfigurationBox.EdgesTabBox.EdgesTableField.Table.ToColumn;
import org.eclipse.scout.apps.egt.client.graph.EgtGraphDetailForm.MainBox.GroupBox.ConfigurationBox.EdgesTabBox.EdgesTableField.Table.WeightColumn;
import org.eclipse.scout.apps.egt.client.graph.EgtGraphDetailForm.MainBox.GroupBox.ConfigurationBox.VerticesTabBox;
import org.eclipse.scout.apps.egt.client.graph.EgtGraphDetailForm.MainBox.GroupBox.ConfigurationBox.VerticesTabBox.VerticesTableField.Table.EgtGraphVertexColumn;
import org.eclipse.scout.apps.egt.client.graph.EgtGraphDetailForm.MainBox.GroupBox.ConfigurationBox.VerticesTabBox.VerticesTableField.Table.IDColumn;
import org.eclipse.scout.apps.egt.client.graph.EgtGraphDetailForm.MainBox.GroupBox.ConfigurationBox.VerticesTabBox.VerticesTableField.Table.SpeciesColumn;
import org.eclipse.scout.apps.egt.client.graph.EgtGraphDetailForm.MainBox.GroupBox.GraphSvgField;
import org.eclipse.scout.apps.egt.client.graph.EgtGraphDetailForm.MainBox.GroupBox.GraphSvgSourceField;
import org.eclipse.scout.apps.egt.client.ui.desktop.forms.IEgtPageForm;
import org.eclipse.scout.apps.egt.client.ui.fields.AbstractDoubleColumn;
import org.eclipse.scout.apps.egt.shared.graph.EgtGraph;
import org.eclipse.scout.apps.egt.shared.graph.EgtGraphVertex;
import org.eclipse.scout.apps.egt.shared.graph.EgtGraphWeightedDirectedEdge;
import org.eclipse.scout.apps.egt.shared.graph.EgtSpeciesCodeType;
import org.eclipse.scout.apps.egt.shared.graph.EgtSpeciesCodeType.IEgtSpeciesCode;
import org.eclipse.scout.rt.client.ui.action.keystroke.AbstractKeyStroke;
import org.eclipse.scout.rt.client.ui.action.menu.AbstractMenu;
import org.eclipse.scout.rt.client.ui.action.menu.IMenuType;
import org.eclipse.scout.rt.client.ui.action.menu.TableMenuType;
import org.eclipse.scout.rt.client.ui.basic.table.AbstractTable;
import org.eclipse.scout.rt.client.ui.basic.table.ITableRow;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractColumn;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractIntegerColumn;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractSmartColumn;
import org.eclipse.scout.rt.client.ui.form.AbstractForm;
import org.eclipse.scout.rt.client.ui.form.AbstractFormHandler;
import org.eclipse.scout.rt.client.ui.form.fields.IFormField;
import org.eclipse.scout.rt.client.ui.form.fields.IValueField;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractButton;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractCloseButton;
import org.eclipse.scout.rt.client.ui.form.fields.groupbox.AbstractGroupBox;
import org.eclipse.scout.rt.client.ui.form.fields.stringfield.AbstractStringField;
import org.eclipse.scout.rt.client.ui.form.fields.tabbox.AbstractTabBox;
import org.eclipse.scout.rt.client.ui.form.fields.tablefield.AbstractTableField;
import org.eclipse.scout.rt.client.ui.messagebox.MessageBoxes;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.exception.ProcessingException;
import org.eclipse.scout.rt.platform.util.CollectionUtility;
import org.eclipse.scout.rt.platform.util.CompareUtility;
import org.eclipse.scout.rt.platform.util.StringUtility;
import org.eclipse.scout.rt.shared.TEXTS;
import org.eclipse.scout.rt.shared.services.common.code.ICodeType;
import org.eclipse.scout.rt.svg.client.SVGUtility;
import org.eclipse.scout.rt.svg.client.svgfield.AbstractSvgField;
import org.w3c.dom.svg.SVGDocument;

public class EgtGraphDetailForm extends AbstractForm implements IEgtPageForm {

	private EgtGraph m_graph;

	public EgtGraphDetailForm() throws ProcessingException {
		super();
	}

	public EgtGraph getGraph() {
		return m_graph;
	}

	public void setGraph(EgtGraph graph) {
		m_graph = graph;
	}

	protected int getGroupBoxColumnCount() {
		return 2;
	}

	protected int getGraphSvgFieldWidth() {
		return 1;
	}

	protected int getConfigurationBoxWidth() {
		return 1;
	}

	@Override
	public void startPageForm() throws ProcessingException {
		startInternal(new PageFormHandler());
	}

	public MainBox getMainBox() {
		return getFieldByClass(MainBox.class);
	}

	public GroupBox getGroupBox() {
		return getFieldByClass(GroupBox.class);
	}

	public GraphSvgField getGraphSvgField() {
		return getFieldByClass(GraphSvgField.class);
	}

	public GraphSvgSourceField getGraphSvgSourceField() {
		return getFieldByClass(GraphSvgSourceField.class);
	}

	public ConfigurationBox getConfigurationBox() {
		return getFieldByClass(ConfigurationBox.class);
	}

	public EdgesTabBox getEdgesTabBox() {
		return getFieldByClass(EdgesTabBox.class);
	}

	public VerticesTabBox getVerticesTabBox() {
		return getFieldByClass(VerticesTabBox.class);
	}

	@Override
	public AbstractCloseButton getCloseButton() throws ProcessingException {
		return null;
	}

	@Order(10.0)
	public class MainBox extends AbstractGroupBox {

		@Order(10.0)
		public class GroupBox extends AbstractGroupBox {

			@Override
			protected int getConfiguredGridColumnCount() {
				return getGroupBoxColumnCount();
			}

			@Order(5.0)
			public class GraphSvgSourceField extends AbstractStringField {

				@Override
				protected boolean getConfiguredVisible() {
					return false;
				}

				@Override
				protected int getConfiguredMaxLength() {
					return Integer.MAX_VALUE;
				}

				@Override
				protected boolean getConfiguredMultilineText() {
					return true;
				}
			}

			@Order(10.0)
			public class GraphSvgField extends AbstractSvgField {

				@Override
				protected int getConfiguredGridW() {
					return getGraphSvgFieldWidth();
				}

				@Override
				protected boolean getConfiguredLabelVisible() {
					return false;
				}

				@Override
				protected Class<? extends IValueField<?>> getConfiguredMasterField() {
					return MainBox.GroupBox.GraphSvgSourceField.class;
				}

				@Override
				protected void execChangedMasterValue(Object newMasterValue) throws ProcessingException {

					getGraphSvgSourceField().clearErrorStatus();

					try {
						getGraphSvgField().setSvgDocument(getDocument((String) newMasterValue));
					} catch (Exception e) {
						e.printStackTrace();
						getGraphSvgSourceField().setErrorStatus(e.getMessage());
					}
				}
			}

			@Order(20.0)
			public class ConfigurationBox extends AbstractTabBox {

				@Override
				protected int getConfiguredGridW() {
					return getConfigurationBoxWidth();
				}

				@Override
				protected double getConfiguredGridWeightY() {
					return 0.5;
				}

				@Override
				protected boolean getConfiguredGridUseUiHeight() {
					return true;
				}

				@Order(10.0)
				public class VerticesTabBox extends AbstractGroupBox {

					@Override
					protected String getConfiguredLabel() {
						return TEXTS.get("Vertices");
					}

					public VerticesTableField getVerticesTableField() {
						return getFieldByClass(VerticesTableField.class);
					}

					public VertexConfigurationBox getVertexConfigurationBox() {
						return getFieldByClass(VertexConfigurationBox.class);
					}

					@Override
					protected int getConfiguredGridColumnCount() {
						return 1;
					}

					@Order(10.0)
					public class VerticesTableField extends AbstractTableField<VerticesTableField.Table> {

						public EgtGraphVertexColumn getEgtGraphVertecColumn() {
							return getTable().getColumnSet().getColumnByClass(EgtGraphVertexColumn.class);
						}

						public IDColumn getIDColumn() {
							return getTable().getColumnSet().getColumnByClass(IDColumn.class);
						}

						public SpeciesColumn getSpeciesColumn() {
							return getTable().getColumnSet().getColumnByClass(SpeciesColumn.class);
						}

						@Override
						protected boolean getConfiguredLabelVisible() {
							return false;
						}

						@Override
						protected int getConfiguredGridH() {
							return 8;
						}

						@Order(10.0)
						public class Table extends AbstractTable {

							@Override
							public ITableRow addRow(ITableRow newRow) throws ProcessingException {
								decorate(newRow);
								return super.addRow(newRow);
							}

							public void decorate(ITableRow newRow) throws ProcessingException {
								getIDColumn().setValue(newRow, getEgtGraphVertecColumn().getValue(newRow).getId());
								getSpeciesColumn().setValue(newRow, BEANS.get(EgtSpeciesCodeType.class)
										.getCodeIdByEnum(getEgtGraphVertecColumn().getValue(newRow).getSpecies()));
							}

							@Order(5.0)
							public class EgtGraphVertexColumn extends AbstractColumn<EgtGraphVertex> {

								@Override
								protected boolean getConfiguredDisplayable() {
									return false;
								}
							}

							@Order(10.0)
							public class IDColumn extends AbstractIntegerColumn {

								@Override
								protected String getConfiguredHeaderText() {
									return TEXTS.get("Vertex");
								}

								@Override
								protected int getConfiguredWidth() {
									return 100;
								}

							}

							@Order(20.0)
							public class SpeciesColumn extends AbstractSmartColumn<Long> {

								@Override
								protected String getConfiguredHeaderText() {
									return TEXTS.get("Species");
								}

								@Override
								protected int getConfiguredWidth() {
									return 200;
								}

								@Override
								protected Class<? extends ICodeType<?, Long>> getConfiguredCodeType() {
									return EgtSpeciesCodeType.class;
								}

								@Override
								protected boolean getConfiguredEditable() {
									return true;
								}

								@Override
								protected Long execValidateValue(ITableRow row, Long rawValue) throws ProcessingException {
									rawValue = super.execValidateValue(row, rawValue);
									if (!CompareUtility.equals(rawValue, null) && !getGraph().isChanging()) {
										IEgtSpeciesCode code = (IEgtSpeciesCode) BEANS.get(EgtSpeciesCodeType.class).getCode(rawValue);
										getEgtGraphVertecColumn().getValue(row).setSpecies(code.getEnum());
										changeVertex(getEgtGraphVertecColumn().getValue(row));
									}
									return rawValue;
								}
							}

							@Order(200.0)
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
									ITableRow row = getTable().createRow();
									int id = 1;
									for (ITableRow r : getTable().getRows()) {
										if (!r.isStatusDeleted() && getIDColumn().getValue(r) >= id) {
											id = getIDColumn().getValue(r) + 1;
										}
									}
									EgtGraphVertex vertex = new EgtGraphVertex(id, 100, 100, 50);
									getEgtGraphVertecColumn().setValue(row, vertex);
									addVertex(vertex);
									getTable().addRow(row);
								}

							}

							@Order(210.0)
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
									if (MessageBoxes.showDeleteConfirmationMessage(TEXTS.get("Vertices"),
											getIDColumn().getSelectedValues())) {
										for (EgtGraphVertex vertex : getEgtGraphVertecColumn().getSelectedValues()) {
											removeVertex(vertex);
											deleteRows(getSelectedRows());
										}
									}
								}
							}

						}
					}

					@Order(20.0)
					public class VertexConfigurationBox extends AbstractGroupBox {

						protected final int height = 50;
						protected final int width = 50;

						protected final int far = 10;
						protected final int close = 1;

						public FarLeftButton getFarLeftButton() {
							return getFieldByClass(FarLeftButton.class);
						}

						public LeftButton getLeftButton() {
							return getFieldByClass(LeftButton.class);
						}

						public FarUpButton getFarUpButton() {
							return getFieldByClass(FarUpButton.class);
						}

						public UpButton getUpButton() {
							return getFieldByClass(UpButton.class);
						}

						public FarDownButton getFarDownButton() {
							return getFieldByClass(FarDownButton.class);
						}

						public DownButton getDownButton() {
							return getFieldByClass(DownButton.class);
						}

						public FarRightButton getFarRightButton() {
							return getFieldByClass(FarRightButton.class);
						}

						public RightButton getRightButton() {
							return getFieldByClass(RightButton.class);
						}

						private class AbstractVertexConfigurationButton extends AbstractButton {

							@Override
							protected int getConfiguredHeightInPixel() {
								return height;
							}

							@Override
							protected int getConfiguredWidthInPixel() {
								return width;
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
							protected int getConfiguredGridX() {
								return getX((int) this.getClass().getAnnotation(Order.class).value());
							}

							@Override
							protected int getConfiguredGridY() {
								return getY((int) this.getClass().getAnnotation(Order.class).value());
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
								if (getVerticesTableField().getTable().getSelectedRowCount() == 0) {
									MessageBoxes.createOk().withHeader(TEXTS.get("EGTFS")).withBody(TEXTS.get("PleaseSelectVertex"));
								} else {
									for (EgtGraphVertex vertex : getVerticesTableField().getEgtGraphVertecColumn().getSelectedValues()) {
										if (isXDirection()) {
											vertex.setX(vertex.getX() + (getOrientation() * getDistance()));
										}
										if (isYDirection()) {
											vertex.setY(vertex.getY() + (getOrientation() * getDistance()));
										}
										changeVertex(vertex);
									}
								}
							}

							protected boolean isXDirection() {
								return false;
							}

							protected boolean isYDirection() {
								return false;
							}

							protected int getOrientation() {
								return 0;
							}

							protected int getDistance() {
								return 0;
							}
						}

						protected int getX(int order) {
							int column = (int) (order % 50 == 0 ? Math.floor(order / 50) - 1 : Math.floor(order / 50));
							return column;
						}

						protected int getY(int order) {
							int row = (int) ((order - 10) / 10) % 5;
							return row;
						}

						@Override
						protected int getConfiguredHeightInPixel() {
							return 7 * height;
						}

						@Override
						protected int getConfiguredWidthInPixel() {
							return 7 * width;
						}

						@Override
						protected int getConfiguredHorizontalAlignment() {
							return 0;
						}

						@Override
						protected boolean getConfiguredFillHorizontal() {
							return false;
						}

						@Order(30.0)
						public class FarLeftButton extends AbstractVertexConfigurationButton {

							@Override
							protected String getConfiguredLabel() {
								return "\u219e";
							}

							@Override
							protected boolean isXDirection() {
								return true;
							}

							@Override
							protected int getOrientation() {
								return -1;
							}

							@Override
							protected int getDistance() {
								return far;
							}
						}

						@Order(80.0)
						public class LeftButton extends AbstractVertexConfigurationButton {

							@Override
							protected String getConfiguredLabel() {
								return "\u2190";
							}

							@Override
							protected boolean isXDirection() {
								return true;
							}

							@Override
							protected int getOrientation() {
								return -1;
							}

							@Override
							protected int getDistance() {
								return close;
							}

							@Override
							protected String getConfiguredKeyStroke() {
								return "a";
							}

						}

						@Order(110.0)
						public class FarUpButton extends AbstractVertexConfigurationButton {

							@Override
							protected String getConfiguredLabel() {
								return "\u219f";
							}

							@Override
							protected boolean isYDirection() {
								return true;
							}

							@Override
							protected int getOrientation() {
								return -1;
							}

							@Override
							protected int getDistance() {
								return far;
							}
						}

						@Order(120.0)
						public class UpButton extends AbstractVertexConfigurationButton {

							@Override
							protected String getConfiguredLabel() {
								return "\u2191";
							}

							@Override
							protected boolean isYDirection() {
								return true;
							}

							@Override
							protected int getOrientation() {
								return -1;
							}

							@Override
							protected int getDistance() {
								return close;
							}

							@Override
							protected String getConfiguredKeyStroke() {
								return "w";
							}
						}

						@Order(140.0)
						public class DownButton extends AbstractVertexConfigurationButton {

							@Override
							protected String getConfiguredLabel() {
								return "\u2193";
							}

							@Override
							protected boolean isYDirection() {
								return true;
							}

							@Override
							protected int getOrientation() {
								return 1;
							}

							@Override
							protected int getDistance() {
								return close;
							}
						}

						@Order(150.0)
						public class FarDownButton extends AbstractVertexConfigurationButton {

							@Override
							protected String getConfiguredLabel() {
								return "\u21a1";
							}

							@Override
							protected boolean isYDirection() {
								return true;
							}

							@Override
							protected int getOrientation() {
								return 1;
							}

							@Override
							protected int getDistance() {
								return far;
							}
						}

						@Order(180.0)
						public class RightButton extends AbstractVertexConfigurationButton {

							@Override
							protected String getConfiguredLabel() {
								return "\u2192";
							}

							@Override
							protected boolean isXDirection() {
								return true;
							}

							@Override
							protected int getOrientation() {
								return 1;
							}

							@Override
							protected int getDistance() {
								return close;
							}

							@Override
							protected String getConfiguredKeyStroke() {
								return "d";
							}
						}

						@Order(230.0)
						public class FarRightButton extends AbstractVertexConfigurationButton {

							@Override
							protected String getConfiguredLabel() {
								return "\u21a0";
							}

							@Override
							protected boolean isXDirection() {
								return true;
							}

							@Override
							protected int getOrientation() {
								return 1;
							}

							@Override
							protected int getDistance() {
								return far;
							}
						}
					}
				}

				@Order(20.0)
				public class EdgesTabBox extends AbstractGroupBox {

					@Override
					protected String getConfiguredLabel() {
						return TEXTS.get("Edges");
					}

					public EdgesTableField getEdgesTableField() {
						return getFieldByClass(EdgesTableField.class);
					}

					// public EdgesConfigurationBox getEdgesConfigurationBox() {
					// return getFieldByClass(EdgesConfigurationBox.class);
					// }

					@Order(10.0)
					public class EdgesTableField extends AbstractTableField<EdgesTableField.Table> {

						@Override
						protected boolean getConfiguredLabelVisible() {
							return false;
						}

						@Override
						protected int getConfiguredGridH() {
							return 15;
						}

						public EgtGraphWeightedDirectedEdgeColumn getEgtGraphWeightedDirectedEdgeColumn() {
							return getTable().getColumnSet().getColumnByClass(EgtGraphWeightedDirectedEdgeColumn.class);
						}

						public FromColumn getFromColumn() {
							return getTable().getColumnSet().getColumnByClass(FromColumn.class);
						}

						public ToColumn getToColumn() {
							return getTable().getColumnSet().getColumnByClass(ToColumn.class);
						}

						public WeightColumn getWeightColumn() {
							return getTable().getColumnSet().getColumnByClass(WeightColumn.class);
						}

						public ScaledWeightColumn getScaledWeightColumn() {
							return getTable().getColumnSet().getColumnByClass(ScaledWeightColumn.class);
						}

						@Order(10.0)
						public class Table extends AbstractTable {

							@Override
							public ITableRow addRow(ITableRow newRow) throws ProcessingException {
								decorate(newRow);
								return super.addRow(newRow);
							}

							public void decorate(ITableRow newRow) throws ProcessingException {
								getFromColumn().setValue(newRow,
										getEgtGraphWeightedDirectedEdgeColumn().getValue(newRow).getFrom().getId());
								getToColumn().setValue(newRow,
										getEgtGraphWeightedDirectedEdgeColumn().getValue(newRow).getTo().getId());
								getWeightColumn().setValue(newRow,
										getEgtGraphWeightedDirectedEdgeColumn().getValue(newRow).getWeight());
							}

							@Order(5.0)
							public class EgtGraphWeightedDirectedEdgeColumn extends AbstractColumn<EgtGraphWeightedDirectedEdge> {

								@Override
								protected boolean getConfiguredDisplayable() {
									return false;
								}

							}

							@Order(10.0)
							public class FromColumn extends AbstractIntegerColumn {

								@Override
								protected String getConfiguredHeaderText() {
									return TEXTS.get("From");
								}

								@Override
								protected int getConfiguredWidth() {
									return 100;
								}

							}

							@Order(20.0)
							public class ToColumn extends AbstractIntegerColumn {

								@Override
								protected String getConfiguredHeaderText() {
									return TEXTS.get("To");
								}

								@Override
								protected int getConfiguredWidth() {
									return 100;
								}
							}

							@Order(30.0)
							public class WeightColumn extends AbstractDoubleColumn {

								@Override
								protected String getConfiguredHeaderText() {
									return TEXTS.get("Weight");
								}

								@Override
								protected int getConfiguredWidth() {
									return 100;
								}

								@Override
								protected boolean getConfiguredEditable() {
									return true;
								}

								@Override
								protected void execCompleteEdit(ITableRow row, IFormField editingField) throws ProcessingException {
									super.execCompleteEdit(row, editingField);
									int fromId = getEdgesTabBox().getEdgesTableField().getFromColumn().getValue(row);
									int toId = getEdgesTabBox().getEdgesTableField().getToColumn().getValue(row);
									if (!CompareUtility.equals(fromId, null) && !CompareUtility.equals(toId, null)) {
										List<ITableRow> list = new ArrayList<ITableRow>();
										double sum = 0;
										for (ITableRow r : getEdgesTabBox().getEdgesTableField().getTable().getRows()) {
											if (CompareUtility.equals(getEdgesTabBox().getEdgesTableField().getFromColumn().getValue(r),
													fromId)) {
												list.add(r);
												sum = sum + getEdgesTabBox().getEdgesTableField().getWeightColumn().getValue(r);
											}
										}
										for (ITableRow r : list) {
											double weight = getEdgesTabBox().getEdgesTableField().getWeightColumn().getValue(r) / sum;
											getEdgesTabBox().getEdgesTableField().getScaledWeightColumn().setValue(r, weight);
											EgtGraphWeightedDirectedEdge edge = getEdgesTableField().getEgtGraphWeightedDirectedEdgeColumn()
													.getValue(r);
											edge.setWeight(weight);
											changeEdge(edge);
										}
									} else {
										MessageBoxes.createOk().withHeader(TEXTS.get("EGTFS")).withBody(TEXTS.get("SelectVerticesFirst"));
									}
								}

								@Override
								protected Double execValidateValue(ITableRow row, Double rawValue) throws ProcessingException {
									rawValue = super.execValidateValue(row, rawValue);
									if (CompareUtility.equals(rawValue, null)) {
										return 0.0;
									}
									return rawValue;
								}
							}

							@Order(40.0)
							public class ScaledWeightColumn extends AbstractDoubleColumn {

								@Override
								protected String getConfiguredHeaderText() {
									return TEXTS.get("ScaledWeight");
								}

								@Override
								protected int getConfiguredWidth() {
									return 150;
								}

							}

							@Order(200.0)
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
									EgtGraphDetailEdgeForm form = new EgtGraphDetailEdgeForm();
									form.setVertexIds(getVerticesTabBox().getVerticesTableField().getIDColumn().getValues());
									form.start();
									form.waitFor();
									if (form.isFormStored()) {
										ITableRow row = getTable().createRow();
										EgtGraphWeightedDirectedEdge edge = new EgtGraphWeightedDirectedEdge(
												getGraph().getVertexById(form.getFromField().getValue()),
												getGraph().getVertexById(form.getToField().getValue()), 0);
										row.setCellValue(getEdgesTableField().getEgtGraphWeightedDirectedEdgeColumn().getColumnIndex(),
												edge);
										addEdge(edge);
										getTable().addRow(row);
									}
								}

							}

							@Order(210.0)
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
									List<String> edges = new ArrayList<String>();
									for (EgtGraphWeightedDirectedEdge edge : getEgtGraphWeightedDirectedEdgeColumn()
											.getSelectedValues()) {
										edges.add(TEXTS.get("From") + " " + edge.getFrom().getId() + " " + TEXTS.get("ToLowerCase") + " "
												+ edge.getTo().getId());
									}
									if (MessageBoxes.showDeleteConfirmationMessage(TEXTS.get("Edges"), edges)) {
										for (EgtGraphWeightedDirectedEdge edge : getEgtGraphWeightedDirectedEdgeColumn()
												.getSelectedValues()) {
											removeEdge(edge);
											deleteRows(getSelectedRows());
										}
									}
								}
							}

						}
					}

				}
			}
		}

		@Order(200.0)
		public class LeftKeyStroke extends AbstractKeyStroke {

			@Override
			protected String getConfiguredKeyStroke() {
				return "a";
			}

			@Override
			protected void execAction() throws ProcessingException {
				getVerticesTabBox().getVertexConfigurationBox().getLeftButton().doClick();
			}
		}

		@Order(200.0)
		public class UpKeyStroke extends AbstractKeyStroke {

			@Override
			protected String getConfiguredKeyStroke() {
				return "w";
			}

			@Override
			protected void execAction() throws ProcessingException {
				getVerticesTabBox().getVertexConfigurationBox().getUpButton().doClick();
			}
		}

		@Order(200.0)
		public class DownKeyStroke extends AbstractKeyStroke {

			@Override
			protected String getConfiguredKeyStroke() {
				return "s";
			}

			@Override
			protected void execAction() throws ProcessingException {
				getVerticesTabBox().getVertexConfigurationBox().getDownButton().doClick();
			}
		}

		@Order(200.0)
		public class RightKeyStroke extends AbstractKeyStroke {

			@Override
			protected String getConfiguredKeyStroke() {
				return "d";
			}

			@Override
			protected void execAction() throws ProcessingException {
				getVerticesTabBox().getVertexConfigurationBox().getRightButton().doClick();
			}
		}

		@Order(200.0)
		public class FarLeftKeyStroke extends AbstractKeyStroke {

			@Override
			protected String getConfiguredKeyStroke() {
				return "shift-a";
			}

			@Override
			protected void execAction() throws ProcessingException {
				getVerticesTabBox().getVertexConfigurationBox().getFarLeftButton().doClick();
			}
		}

		@Order(200.0)
		public class FarUpKeyStroke extends AbstractKeyStroke {

			@Override
			protected String getConfiguredKeyStroke() {
				return "shift-w";
			}

			@Override
			protected void execAction() throws ProcessingException {
				getVerticesTabBox().getVertexConfigurationBox().getFarUpButton().doClick();
			}
		}

		@Order(200.0)
		public class FarDownKeyStroke extends AbstractKeyStroke {

			@Override
			protected String getConfiguredKeyStroke() {
				return "shift-s";
			}

			@Override
			protected void execAction() throws ProcessingException {
				getVerticesTabBox().getVertexConfigurationBox().getFarDownButton().doClick();
			}
		}

		@Order(200.0)
		public class FarRightKeyStroke extends AbstractKeyStroke {

			@Override
			protected String getConfiguredKeyStroke() {
				return "shift-d";
			}

			@Override
			protected void execAction() throws ProcessingException {
				getVerticesTabBox().getVertexConfigurationBox().getFarRightButton().doClick();
			}
		}
	}

	public class PageFormHandler extends AbstractFormHandler {
	}

	public void populateGraph() throws ProcessingException {
		if (!CompareUtility.equals(getGraph(), null)) {
			getVerticesTabBox().getVerticesTableField().getTable().deleteAllRows();
			getEdgesTabBox().getEdgesTableField().getTable().deleteAllRows();

			for (EgtGraphVertex vertex : getGraph().getVertices()) {
				ITableRow row = getVerticesTabBox().getVerticesTableField().getTable().createRow();
				getVerticesTabBox().getVerticesTableField().getEgtGraphVertecColumn().setValue(row, vertex);
				getVerticesTabBox().getVerticesTableField().getTable().addRow(row);
			}
			for (EgtGraphWeightedDirectedEdge edge : getGraph().getEdges()) {
				ITableRow row = getEdgesTabBox().getEdgesTableField().getTable().createRow();
				getEdgesTabBox().getEdgesTableField().getEgtGraphWeightedDirectedEdgeColumn().setValue(row, edge);
				getEdgesTabBox().getEdgesTableField().getTable().addRow(row);
			}
			getGraphSvgSourceField().setValue(getGraph().getSvgString());
		}
	}

	public void populateSimulationUpdateEdge(EgtGraphWeightedDirectedEdge edge) throws ProcessingException {
		if (!CompareUtility.equals(edge, null)) {
			edge.setHighlight(true);
			edge.updateSvgText();
			EgtGraphVertex vertex = edge.getTo();
			for (ITableRow row : getVerticesTabBox().getVerticesTableField().getTable().getRows()) {
				if (CompareUtility.equals(getVerticesTabBox().getVerticesTableField().getEgtGraphVertecColumn().getValue(row).getId(), vertex.getId())) {
					getVerticesTabBox().getVerticesTableField().getEgtGraphVertecColumn().setValue(row, vertex);
					getVerticesTabBox().getVerticesTableField().getTable().decorate(row);
				}
			}
		}
		if (!CompareUtility.equals(getGraph(), null)) {
			getGraphSvgSourceField().setValue(getGraph().getSvgString());
		}
		if (!CompareUtility.equals(edge, null)) {
			edge.setHighlight(false);
			edge.updateSvgText();
		}
	}

	private SVGDocument getDocument(String content) throws IOException, ProcessingException {
		if (StringUtility.isNullOrEmpty(content)) {
			return null;
		}

		return SVGUtility.readSVGDocument(new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8.name())));
	}

	private void addVertex(EgtGraphVertex vertex) {
		getGraph().addVertex(vertex);
		getGraphSvgSourceField().setValue(getGraph().getSvgString());
	}

	private void changeVertex(EgtGraphVertex vertex) {
		getGraph().changeVertex(vertex);
		getGraphSvgSourceField().setValue(getGraph().getSvgString());
	}

	private void removeVertex(EgtGraphVertex vertex) {
		getGraph().removeVertex(vertex);
		getGraphSvgSourceField().setValue(getGraph().getSvgString());
	}

	private void addEdge(EgtGraphWeightedDirectedEdge edge) {
		getGraph().addEdge(edge);
		getGraphSvgSourceField().setValue(getGraph().getSvgString());
	}

	private void changeEdge(EgtGraphWeightedDirectedEdge edge) {
		getGraph().changeEdge(edge);
		getGraphSvgSourceField().setValue(getGraph().getSvgString());
	}

	private void removeEdge(EgtGraphWeightedDirectedEdge edge) {
		getGraph().removeEdge(edge);
		getGraphSvgSourceField().setValue(getGraph().getSvgString());
	}

}
