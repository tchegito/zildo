package zeditor.windows.subpanels;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import zeditor.windows.managers.MasterFrameManager;
import zildo.fwk.script.xml.ActionElement;
import zildo.fwk.script.xml.ActionsElement;
import zildo.fwk.script.xml.SceneElement;
import zildo.server.EngineZildo;

public class ScriptPanel extends JPanel {

    final JTable scriptList;
    private final static String[] columnNames=new String[]{"Action", "who", "pos", "what", "value", "angle",
	"name", "type", "delta", "fx", "speed", "text", "backward", "unblock"};

    final MasterFrameManager manager;
    final JComboBox scriptCombo;
    JScrollPane listScroll;
    final List<SceneElement> scenes = EngineZildo.scriptManagement.getAdventure().getScenes();
    SceneElement focused;

    public ScriptPanel(MasterFrameManager p_manager) {

	// Initialize the needed objects
	focused = scenes!= null && scenes.size() > 0 ? scenes.get(0) : null;
	scriptCombo = getCombo();
	scriptList = getScriptList();
	listScroll = getScrollPaneList();
	
	// Layout
	BoxLayout backgroundPanelLayout = new BoxLayout(this, javax.swing.BoxLayout.Y_AXIS);
	setLayout(backgroundPanelLayout);
	
	// Add components
	add(scriptCombo);
	add(scriptList.getTableHeader());
	add(listScroll);

	manager = p_manager;
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
		   scriptList.setModel(new ScriptTableModel(focused.actions));
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
	JTable list = new JTable(null, columnNames);
	list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	list.setModel(new ScriptTableModel(focused.actions));
	
	list.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); //ALL_COLUMNS);
	
	int length=160;
    	for (int i=0;i<columnNames.length;i++) {
    	    TableColumn col = list.getColumnModel().getColumn(i);
    	    //col.setPreferredWidth(length);
    	    length=40;
	}
	return list;
    }

    /**
     * Return the scenes names, designed for a JComboBox
     * @return Object[]
     */
    private Object[] getSceneNames() {
	List<String> sceneNames = new ArrayList();
	for (SceneElement scene : scenes) {
	    sceneNames.add(scene.id);
	}
	return sceneNames.toArray();
    }
    
    public static class ScriptTableModel extends DefaultTableModel {
	
	public ScriptTableModel(List<ActionElement> p_actions) {
	    super(transformObjectArray(p_actions), columnNames);
	}
	
        private static Object[][] transformObjectArray(List<ActionElement> p_actions) {
		Object[][] data=new Object[p_actions.size()][5];
		for (int i=0;i<p_actions.size();i++) {
			data[i]=getRow(p_actions.get(i));
		}
		return data;
        }
        
        private static Object[] getRow(ActionElement p_action) {
            if (p_action instanceof ActionsElement) {
        	return null;
            } else {
		Object[] obj=new Object[columnNames.length];
		obj[0] = p_action.kind.name();
		for (int i=1;i<columnNames.length;i++) {
		    obj[i] = p_action.readAttribute(columnNames[i]);
		}
		return obj;
            }
	}
    }
}
