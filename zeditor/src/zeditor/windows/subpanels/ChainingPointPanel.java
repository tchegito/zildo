/**
 * The Land of Alembrum
 * Copyright (C) 2006-2016 Evariste Boussaton
 * 
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package zeditor.windows.subpanels;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import zeditor.core.selection.ChainingPointSelection;
import zeditor.windows.managers.MasterFrameManager;
import zildo.fwk.ZUtils;
import zildo.fwk.ZUtils.Conditioner;
import zildo.fwk.gfx.filter.FilterEffect;
import zildo.monde.map.ChainingPoint;
import zildo.monde.util.Angle;
import zildo.server.EngineZildo;

/**
 * Panel into the tabbed panes : Chaining points.
 * 
 * @author Tchegito
 * 
 */
@SuppressWarnings("serial")
public class ChainingPointPanel extends JPanel {

	JTable pointsList;
	ChainingPointTableModel model;
	private final String[] columnNames = new String[] { "Floor", "Carte", "Vertical",
			"Bord", "Single", "Angle", "Anim", "", "" };
	private final int[] columnSizes = { 20, 70, 30, 30, 30, 50, 60, 40, 45 };
	
	MasterFrameManager manager;

	public ChainingPointPanel(MasterFrameManager p_manager) {
		BorderLayout chainingPointPanelLayout = new BorderLayout();
		setLayout(chainingPointPanelLayout);
		add(getCombo(), BorderLayout.WEST);
		add(getCombo().getTableHeader(), BorderLayout.PAGE_START);

		JButton creer = new JButton(
				new AbstractAction("Créer un nouveau", null) {
					@Override
					public void actionPerformed(ActionEvent actionevent) {
						ChainingPoint ch = new ChainingPoint();
						ch.setMapname("nouveau");
						ch.setComingAngle(Angle.NORD); // Default : north
						ch.setTransitionAnim(FilterEffect.BLEND);
						EngineZildo.mapManagement.getCurrentMap()
								.addChainingPoint(ch);
						manager.updateChainingPoints(null);
						manager.setUnsavedChanges(true);
					}
				});
		add(creer, BorderLayout.SOUTH);

		manager = p_manager;

	}

	public JTable getCombo() {
		if (pointsList == null) {

			pointsList = new JTable(null, columnNames);
			
			pointsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

			pointsList.getSelectionModel().addListSelectionListener(
					new ListSelectionListener() {
						@Override
						public void valueChanged(ListSelectionEvent e) {
							if (e.getValueIsAdjusting() == false) {

								int ind = pointsList.getSelectedRow();
								if (ind != -1) {
									ChainingPointTableModel m = (ChainingPointTableModel) pointsList
											.getModel();
									manager.setChainingPointSelection(new ChainingPointSelection(
											m.getNthRow(ind)));
								}
							}
						}
					});

		}
		return pointsList;
	}

	public void focusPoint(ChainingPoint p_point) {
		// Find the nth line
		for (int i = 0; i < model.getRowCount(); i++) {
			ChainingPoint c = model.getNthRow(i);
			if (c == p_point) {
				pointsList.changeSelection(i, 0, false, false);
				return;
			}
		}
	}

	private ChainingPoint getSelectedPoint() {
		int ind = pointsList.getSelectedRow();
		if (ind != -1) {
			return model.getNthRow(ind);
		}
		return null;
	}

	public void updateList(ChainingPoint[] p_points) {

		// Set the model
		model = new ChainingPointTableModel(manager, p_points, columnNames);
		pointsList.setModel(model);

		// Set angle combo
		TableColumn buttonColumn = pointsList.getColumnModel().getColumn(5);
		JComboBox comboAngle = new JComboBox(ZUtils.getValues(Angle.class, new Conditioner<Angle>() {
			@Override
			public boolean accept(Angle a) {
				return !a.isDiagonal();
			}
		}));
		buttonColumn.setCellEditor(new DefaultCellEditor(comboAngle));		

		// Set transition anim combo
		buttonColumn = pointsList.getColumnModel().getColumn(6);
		JComboBox comboActionsAnim = new JComboBox(ZUtils.getValues(FilterEffect.class, new Conditioner<FilterEffect>() {
			@Override
			public boolean accept(FilterEffect a) {
				return a != FilterEffect.CLOUD;
			}
		}));
		buttonColumn.setCellEditor(new DefaultCellEditor(comboActionsAnim));	
		
		// Set buttons
		buttonColumn = pointsList.getColumnModel().getColumn(7);
		buttonColumn.setCellRenderer(new ChainingPointCellRenderer(
				new AbstractAction("X", null) {
					@Override
					public void actionPerformed(ActionEvent e) {
						// Remove chaining point and update list
						ChainingPoint ch = getSelectedPoint();
						EngineZildo.mapManagement.getCurrentMap()
								.removeChainingPoint(ch);
						manager.updateChainingPoints(null);
						manager.setUnsavedChanges(true);
					}
				}));

		buttonColumn = pointsList.getColumnModel().getColumn(8);
		buttonColumn.setCellRenderer(new ChainingPointCellRenderer(
				new AbstractAction("Go", null) {
					@Override
					public void actionPerformed(ActionEvent e) {
						// Load the map referred from this chaining point
						ChainingPoint ch = getSelectedPoint();
						String mapName = ch.getMapname();
						String currentMapName = EngineZildo.mapManagement
								.getCurrentMap().getName();
						int posSlash = currentMapName.indexOf(System
								.getProperty("file.separator"));
						if (posSlash != -1) {
							mapName = currentMapName.substring(0, posSlash + 1)
									+ mapName;
						}
						manager.loadMap(mapName + ".map", ch);
					}
				}));

		// Set columns sizes
		for (int i = 0; i < columnSizes.length; i++) {
			TableColumn col = pointsList.getColumnModel().getColumn(i);
			col.setPreferredWidth(columnSizes[i]);
		}
	}

	/**
	 * Class handling a cell containing a button.
	 * 
	 * @author Tchegito
	 * 
	 */
	public class ChainingPointCellRenderer extends JButton implements
			TableCellRenderer {

		public ChainingPointCellRenderer(Action p_action) {
			setOpaque(true);
			setAction(p_action);
			setEnabled(true);
		}

		@Override
		public Component getTableCellRendererComponent(JTable jtable,
				Object obj, boolean isSelected, boolean isFocus, int i, int j) {
			if (isSelected && isFocus) {
				doClick();
			}
			return this;
		}
	}

	/**
	 * Class handling the table model : columns descriptions, and event
	 * listeners.
	 * 
	 * @author Tchegito
	 * 
	 */
	public static class ChainingPointTableModel extends DefaultTableModel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 104055182615089183L;

		ChainingPoint[] points;
		
		/**
		 * Very important : to get the boolean value represented par JCheckBox
		 * automatically
		 */
		@Override
		public Class<?> getColumnClass(int c) {
			return getValueAt(0, c).getClass();
		}

		/**
		 * Avoid to edit the buttons ! It would be ugly ...
		 */
		@Override
		public boolean isCellEditable(int i, int j) {
			if (j > 6) {
				return false;
			}
			return super.isCellEditable(i, j);
		}

		public ChainingPointTableModel(final MasterFrameManager manager, ChainingPoint[] data,
				String[] columnNames) {

			super(transformObjectArray(data), columnNames);

			points = data;

			addTableModelListener(new TableModelListener() {
				@Override
				public void tableChanged(TableModelEvent e) {
					int col = e.getColumn();
					int row = e.getFirstRow();
					ChainingPoint ch = points[row];
					Object o = getValueAt(row, col);
					switch (col) {
					case 0: // floor
						ch.setFloor((Short) o); 
						break;
					case 1: // mapname
						ch.setMapname((String) o);
						break;
					case 2: // vertical
						ch.setVertical((Boolean) o);
						break;
					case 3: // bord
						ch.setBorder((Boolean) o);
						break;
					case 4: // single
						ch.setSingle((Boolean) o);
						break;
					case 5: // angle
						ch.setComingAngle(Angle.valueOf((String) o));
						break;
					case 6: // anim
						ch.setTransitionAnim(FilterEffect.valueOf((String) o));
						break;
					}
					manager.setUnsavedChanges(true);

				}
			});

		}

		private static Object[][] transformObjectArray(ChainingPoint[] p_points) {
			Object[][] data = new Object[p_points.length][6];
			for (int i = 0; i < p_points.length; i++) {
				data[i] = getRow(p_points[i]);
			}
			return data;
		}

		private static Object[] getRow(ChainingPoint ch) {
			Object[] obj = new Object[] { ch.getFloor(), ch.getMapname(), ch.isVertical(),
					ch.isBorder(), ch.isSingle(), ch.getComingAngle().toString(), ch.getTransitionAnim().toString(), new JButton("creer"), null };
			return obj;
		}

		public ChainingPoint getNthRow(int p_num) {
			return points[p_num];
		}

		@Override
		public Object getValueAt(int p_row, int p_column) {
			if (p_row >= points.length) {
				return null; // Changing map => transition, so we return NULL
			} else {
				return super.getValueAt(p_row, p_column);
			}
		}
	}
}
