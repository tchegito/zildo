package zeditor.windows.subpanels;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import zeditor.windows.managers.MasterFrameManager;
import zildo.fwk.ZUtils;
import zildo.fwk.gfx.filter.FilterEffect;
import zildo.fwk.script.xml.ActionElement;
import zildo.fwk.script.xml.ActionElement.ActionKind;
import zildo.fwk.script.xml.SceneElement;
import zildo.fwk.script.xml.ScriptWriter;
import zildo.monde.map.Angle;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.desc.PersoDescription;
import zildo.monde.sprites.utils.MouvementPerso;
import zildo.server.EngineZildo;

@SuppressWarnings("serial")
public class ScriptPanel extends JPanel {

	final JTable scriptList;
	ScriptTableModel model;
	
	final MasterFrameManager manager;
	final JComboBox scriptCombo;
	final JButton buttonPlus;
	final JButton buttonSave;
	
	// Specific combos based on enumerated types
	final Map<Class<?>, JComboBox> combosByEnumClass;
	final static Class<?>[] managedEnums = {Angle.class, ElementDescription.class, PersoDescription.class, FilterEffect.class, MouvementPerso.class};
	
	JScrollPane listScroll;
	final List<SceneElement> scenes = EngineZildo.scriptManagement
			.getAdventure().getScenes();
	SceneElement focused;

	@SuppressWarnings("unchecked")
	public <T extends Enum<T>> ScriptPanel(MasterFrameManager p_manager) {

		// Initialize the needed objects
		focused = scenes != null && scenes.size() > 0 ? scenes.get(0) : null;
		scriptCombo = getCombo();
		scriptList = getScriptList();
		listScroll = getScrollPaneList();
		buttonPlus = new JButton(new AbstractAction("Ajouter", null) {
			@Override
			public void actionPerformed(ActionEvent e) {
				addLine();
			}
		});
		buttonSave = new JButton(new AbstractAction("Sauvegarder", null) {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveScript();
			}
		});
		JPanel panelButtons = new JPanel();
		panelButtons.add(buttonPlus);
		panelButtons.add(buttonSave);

		// Specific combos for the list
		combosByEnumClass = new HashMap<Class<?>, JComboBox>();
		for (Class<?> e : managedEnums) {
		    // Create a combo which resize itself after item changed
			Class<T> enumClazz = (Class<T>) e;
		    JComboBox combo = new UpdateComboBox(ZUtils.getValues(enumClazz));
		    combosByEnumClass.put(e, combo); 
		}
		
		updateList();

		// Layout
		BoxLayout backgroundPanelLayout = new BoxLayout(this,
				javax.swing.BoxLayout.Y_AXIS);
		setLayout(backgroundPanelLayout);

		// Add components
		add(scriptCombo);
		add(scriptList.getTableHeader());
		add(panelButtons);
		add(listScroll);
		
		manager = p_manager;
	}

	private void addLine() {
		focused.actions.add(new ActionElement(ActionKind.pos));
		updateList();
	}
	
	private void saveScript() {
		// Save the XML script
	    	new ScriptWriter("kikoo").create(scenes);
	}
	
	private JComboBox getCombo() {
		ComboBoxModel backgroundComboModel = new DefaultComboBoxModel(
				getSceneNames());
		JComboBox combo = new JComboBox();
		combo.setModel(backgroundComboModel);
		combo.setSize(339, 21);
		combo.setMaximumSize(new java.awt.Dimension(32767, 21));
		combo.setAction(new AbstractAction("Changer le Script", null) {
			public void actionPerformed(ActionEvent evt) {
				String sceneName = scriptCombo.getSelectedItem().toString();
				focusNamedScene(sceneName);
				updateList();
			}
		});
		return combo;
	}

	private void focusNamedScene(String p_name) {
		for (SceneElement scene : scenes) {
			if (scene.id.equals(p_name)) {
				focused = scene;
			}
		}
	}

	private JScrollPane getScrollPaneList() {
		JScrollPane backgroundScroll = new JScrollPane();
		backgroundScroll
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		backgroundScroll
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		backgroundScroll.setViewportView(scriptList);
		return backgroundScroll;
	}

	private JTable getScriptList() {
		JTable list = new JTable(null, ScriptTableModel.columnNames) {
		    @Override
		    public TableCellEditor getCellEditor(int p_row, int p_column) {
		        if (p_column >= 1) {
		            Class<?> c = model.getClassCell(p_row, p_column);
		            if (c.isEnum()) {
		        	return new DefaultCellEditor(combosByEnumClass.get(c));
		            }
		        }
		        return super.getCellEditor(p_row, p_column);
		    }
		    @Override
		    public void tableChanged(TableModelEvent p_e) {
		        super.tableChanged(p_e);
			if (p_e.getColumn() == 0) {
			    updateList();
			}
		    }
		};
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		return list;
	}

	private void updateList() {
	    model = new ScriptTableModel(focused.actions);
	    scriptList.setModel(model);

	    enhanceListWithCombo();

	    autoResizeColWidth(scriptList);
	}

	/**
	 * Add combobox to cases which are needing one.
	 */
	private void enhanceListWithCombo() {
		// Action kind
		TableColumn buttonColumn = scriptList.getColumnModel().getColumn(0);
		JComboBox comboActions = new JComboBox(
				ZUtils.getValues(ActionKind.class));
		buttonColumn.setCellEditor(new DefaultCellEditor(comboActions));

		// Angle
		int nthColumn = ScriptTableModel.findColumnByName("angle");
		buttonColumn = scriptList.getColumnModel().getColumn(nthColumn);
		comboActions = new UpdateComboBox(ZUtils.getValues(Angle.class));
		buttonColumn.setCellEditor(new DefaultCellEditor(comboActions));

		// Boolean columns
		String[] booleanColumns = { "delta", "backward", "unblock" };
		for (String s : booleanColumns) {
			nthColumn = ScriptTableModel.findColumnByName(s);
			buttonColumn = scriptList.getColumnModel().getColumn(nthColumn);
			comboActions = new UpdateComboBox(new Object[] { "", "true" });
			buttonColumn.setCellEditor(new DefaultCellEditor(comboActions));
		}
	}

	/**
	 * Return the scenes names, designed for a JComboBox
	 * 
	 * @return Object[]
	 */
	private Object[] getSceneNames() {
		List<String> sceneNames = new ArrayList<String>();
		for (SceneElement scene : scenes) {
			sceneNames.add(scene.id);
		}
		return sceneNames.toArray();
	}

	/**
	 * Auto adjust column size with content.<br/>
	 * Found at
	 * 'http://www.pikopong.com/blog/2008/08/13/auto-resize-jtable-column-width/'
	 * .
	 * 
	 * @param table
	 */
	private void autoResizeColWidth(JTable table) {
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		int margin = 5;

		for (int i = 0; i < table.getColumnCount(); i++) {
			int vColIndex = i;
			DefaultTableColumnModel colModel = (DefaultTableColumnModel) table
					.getColumnModel();
			TableColumn col = colModel.getColumn(vColIndex);
			int width = 0;

			// Get width of column header
			TableCellRenderer renderer = col.getHeaderRenderer();

			if (renderer == null) {
				renderer = table.getTableHeader().getDefaultRenderer();
			}

			Component comp = renderer.getTableCellRendererComponent(table,
					col.getHeaderValue(), false, false, 0, 0);

			width = comp.getPreferredSize().width;

			// Get maximum width of column data
			for (int r = 0; r < table.getRowCount(); r++) {
				renderer = table.getCellRenderer(r, vColIndex);
				comp = renderer.getTableCellRendererComponent(table,
						table.getValueAt(r, vColIndex), false, false, r,
						vColIndex);
				width = Math.max(width, comp.getPreferredSize().width);
			}

			// Add margin
			width += 2 * margin;

			// Set the width
			col.setPreferredWidth(width);
		}

		((DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer())
				.setHorizontalAlignment(SwingConstants.LEFT);

		table.getTableHeader().setReorderingAllowed(false);
	}
	
	/**
	 * Simple combo which updates the list, as soon as the selected item changes.
	 * @author Tchegito
	 *
	 */
    class UpdateComboBox extends JComboBox {
    	
    	public UpdateComboBox(Object[] p_items) {
    		super(p_items);
    	    this.addActionListener(new ActionListener() {
    	        @Override
    	        public void actionPerformed(ActionEvent p_e) {
    		    autoResizeColWidth(scriptList);
    	        }
    	    });
    	}
    }

}
