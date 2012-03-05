package zeditor.windows.subpanels;

import java.awt.Color;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import zildo.fwk.ZUtils;
import zildo.fwk.script.xml.ScriptWriter;
import zildo.fwk.script.xml.element.ActionElement;
import zildo.fwk.script.xml.element.ActionsElement;
import zildo.fwk.script.xml.element.ActionElement.ActionKind;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.desc.PersoDescription;
import zildo.monde.sprites.utils.MouvementPerso;
import zildo.monde.util.Angle;
import zildo.fwk.gfx.filter.FilterEffect;

@SuppressWarnings("serial")
public class ScriptTableModel extends DefaultTableModel {

	List<ActionElement> actions;

	Map<String, Color> colorByPerso = new HashMap<String, Color>();
	
	final static Color[] colors = new Color[] {Color.CYAN, Color.RED, Color.YELLOW, Color.PINK, Color.ORANGE, Color.LIGHT_GRAY, Color.MAGENTA};
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ScriptTableModel(List<ActionElement> p_actions) {
		super();
		// 1st pass : set values
		setDataVector(transformObjectArray(p_actions), ScriptWriter.columnNames);

		actions = p_actions;

		// 2nd pass : adjust enum values
		for (int i = 0; i < p_actions.size(); i++) {
			for (int j = 0; j < ScriptWriter.columnNames.length; j++) {
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

		addTableModelListener(new TableModelListener() {

			@Override
			public void tableChanged(TableModelEvent p_e) {
				int col = p_e.getColumn();
				int row = p_e.getFirstRow();
				if (col == -1) { // Sometimes we got here with -1
					return;
				}
				String attr = ScriptWriter.columnNames[col];
				ActionElement action = actions.get(row);
				String value = (String) getValueAt(row, col);

				if (col == 0) {
					// Kind of action
					ActionKind kind = ZUtils.getField(value, ActionKind.class);
					if (action.kind != kind) { // New action ?
						actions.set(row, new ActionElement(kind));
					}
				} else {
					Class clazz = getClassCell(row, col);
					if (clazz != String.class && clazz != PersoDescription.class && clazz != ElementDescription.class) {
						// Special cells
						int intValue = ZUtils.getField(value, clazz).ordinal();
						value = String.valueOf(intValue);
					}
					action.setAttribute(attr, value);
				}
			}
		});
		
		// Colors
		for (ActionElement action : actions) {
			String whowhat = action.who == null ? action.what : action.who;
			if (whowhat != null) {
				Color c = colorByPerso.get(whowhat);
				if (c == null) {
					int nbColors = colorByPerso.values().size();
					if (nbColors < colors.length) {
						c = colors[nbColors];
					} else {
						c = Color.white;
					}
					colorByPerso.put(whowhat, c);
				}
			}
		}

	}

	public final static int findColumnByName(String p_name) {
		for (int i = 0; i < ScriptWriter.columnNames.length; i++) {
			if (ScriptWriter.columnNames[i].equals(p_name)) {
				return i;
			}
		}
		throw new RuntimeException("Unable to find the '" + p_name + "' column");
	}

	private Object[][] transformObjectArray(List<ActionElement> p_actions) {
		Object[][] data = new Object[p_actions.size()][ScriptWriter.columnNames.length];
		for (int i = 0; i < p_actions.size(); i++) {
			data[i] = getRow(p_actions.get(i));
		}
		return data;
	}

	private Object[] getRow(ActionElement p_action) {
		if (p_action instanceof ActionsElement) {
			return null;
		} else {
			Object[] obj = new Object[ScriptWriter.columnNames.length];
			obj[0] = p_action.kind.name();
			for (int i = 1; i < ScriptWriter.columnNames.length; i++) {
				obj[i] = p_action.readAttribute(ScriptWriter.columnNames[i]);
			}
			return obj;
		}
	}

	@Override
	public boolean isCellEditable(int p_row, int p_column) {
		return true;
	}

	@SuppressWarnings("rawtypes")
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
					Object whoColumn = getValueAt(p_row, ScriptTableModel.findColumnByName("who"));
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
	
	/** Returns the right color this line (based on the "who"/"what" field of the action).
	 * @param p_row
	 * @return Color
	 */
	public Color getLineColor(int p_row) {
		ActionElement action = actions.get(p_row);
		String whowhat = action.who == null ? action.what : action.who;
		if (whowhat != null) {
			return colorByPerso.get(whowhat);
		}
		return Color.white;
	}

}
