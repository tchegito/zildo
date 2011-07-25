package zeditor.windows.subpanels;

import java.util.List;

import javax.swing.table.DefaultTableModel;

import zildo.fwk.ZUtils;
import zildo.fwk.gfx.filter.FilterEffect;
import zildo.fwk.script.xml.ActionElement;
import zildo.fwk.script.xml.ActionsElement;
import zildo.monde.map.Angle;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.desc.PersoDescription;
import zildo.monde.sprites.utils.MouvementPerso;

public class ScriptTableModel extends DefaultTableModel {

    public final static String[] columnNames = new String[] { "Action", "who",
	    "pos", "what", "value", "text", "angle", "name", "type", "delta", "fx",
	    "speed", "backward", "unblock" };

    public ScriptTableModel(List<ActionElement> p_actions) {
	super();
	// 1st pass : set values
	setDataVector(transformObjectArray(p_actions), columnNames);

	// 2nd pass : adjust enum values
	for (int i = 0; i < p_actions.size(); i++) {
	    for (int j = 0 ; j<columnNames.length ;j ++) {
		Class clazz = getClassCell(i, j);
		if (clazz != String.class) {
		    Object val = getValueAt(i, j);
		    if (val != null) {
			try {
			    int n = Integer.parseInt(val.toString());
			    setValueAt(ZUtils.getValues(clazz)[n], i, j);
			} catch (NumberFormatException e) {
			    // Nothing to do
			}
		    }
		}
	    }
	}
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
	Object[][] data = new Object[p_actions.size()][columnNames.length];
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
    
    public Class getClassCell(int p_row, int p_column) {
	String titleColumn = getColumnName(p_column);
	Object actionKindStr = getValueAt(p_row, 0);
	if (actionKindStr != null) {
	    if ("value".equals(titleColumn)) {
		if ("angle".equals(actionKindStr)) {
		    return Angle.class;
		} else if ("script".equals(actionKindStr)) {
		    return MouvementPerso.class;
		}
		// "Type" column
	    } else if ("type".equals(titleColumn)) {
		if ("spawn".equals(actionKindStr)) {
		    Object whoColumn = getValueAt(p_row, ScriptTableModel
			    .findColumnByName("who"));
		    if (whoColumn == null) {
			return ElementDescription.class;
		    } else {
			return PersoDescription.class;
		    }
		} else if (actionKindStr.toString().startsWith("fade")) {
		    return FilterEffect.class;
		}
	    }
	}
	return String.class;
    }
}
