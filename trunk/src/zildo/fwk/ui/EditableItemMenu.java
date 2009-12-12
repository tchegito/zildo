package zildo.fwk.ui;


public abstract class EditableItemMenu extends ItemMenu {

	public static String acceptableChar="ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789.,()- ";
	
	StringBuilder text;
	
	public EditableItemMenu(StringBuilder p_text) {
		text=p_text;
	}
	
	/**
	 * Add character to the item's text. Replace space by underscore.
	 * @param p_ch
	 */
	public void addText(char p_ch) {
		if (text.length() < 20) {
			text.append(p_ch);
		}
	}
	
	public void removeLastChar() {
		if (text.length() > 0) {
			text.deleteCharAt(text.length() - 1);
		}
	}
	
	public String getText() {
		return text.toString();
	}

}
