package zeditor.windows.subpanels;

import java.util.List;

import javax.swing.table.DefaultTableModel;

import zildo.fwk.script.xml.ActionElement;
import zildo.fwk.script.xml.ActionsElement;

public class ScriptTableModel extends DefaultTableModel {

    public final static String[] columnNames = new String[] { "Action", "who",
	    "pos", "what", "value", "text", "angle", "name", "type", "delta", "fx",
	    "speed", "backward", "unblock" };

    public ScriptTableModel(List<ActionElement> p_actions) {
	super();
	setDataVector(transformObjectArray(p_actions), columnNames);

    }

    public final static int findColumnByName(String p_name) {
	for (int i=0;i<columnNames.length;i++) {
	    if (columnNames[i].equals(p_name)) {
		return i;
	    }
	}
	throw new RuntimeException("Unable to find the '"+p_name+"' column");
    }
    
    private Object[][] transformObjectArray(List<ActionElement> p_actions) {
	Object[][] data = new Object[p_actions.size()][5];
	for (int i = 0; i < p_actions.size(); i++) {
	    data[i] = getRow(p_actions.get(i));
	}
	return data;
    }

    private Object[] getRow(ActionElement p_action) {
	if (p_action instanceof ActionsElement) {
	    return null;
	} else {
	    Object[] obj = new Object[columnNames.length];
	    obj[0] = p_action.kind.name();
	    for (int i = 1; i < columnNames.length; i++) {
		obj[i] = p_action.readAttribute(columnNames[i]);
	    }
	    return obj;
	}
    }
    
    @Override
    public boolean isCellEditable(int p_row, int p_column) {
       return true;
    }
}
