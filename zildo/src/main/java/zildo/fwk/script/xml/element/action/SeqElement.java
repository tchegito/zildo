package zildo.fwk.script.xml.element.action;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import zildo.fwk.script.logic.FloatExpression;
import zildo.fwk.script.xml.element.AnyElement;
import zildo.fwk.script.xml.element.LanguageElement;

/**
 * 			<seq who="self" addSpr="7,8,9,10,9,8" value="10"/>
 * @author Tchegito
 *
 */
public class SeqElement extends LanguageElement {

	public SeqElement() {
		//kind = p_kind;
	}

	private List<AnyElement> actions;
	
	@Override
	public void parse(Element p_elem) {
		super.parse(p_elem);
		
		String addSpr = readAttribute("addSpr");
		int wait = readInt("wait");
		String who = readAttribute("who");
		
		boolean error = false;
		int[] sprValues = null;
		if (addSpr != null) {
			String[] vals = addSpr.split(",");
			sprValues = new int[vals.length];
			int idx=0;
			try {
				for (String s : vals) {
					sprValues[idx++] = Integer.parseInt(s);
				}
			} catch (NumberFormatException e) {
				error = true;
			}
		}
		if (sprValues == null || sprValues.length == 0 || error) {
			throw new RuntimeException("<seq> actions MUST have an 'addSpr' attribute describing sprites sequences, separated by commas");
		}
		
		// Create actions following sequence description
		actions = new ArrayList<AnyElement>();
		for (int i : sprValues) {
			// NOTE: ActionElement isn't created the standard way (with XML DOM Element)
			// So we need to pay attention to default values which might not be valued as expected (happened with PV)
			ActionElement persoAction = new ActionElement(ActionKind.perso);
			persoAction.addSpr = new FloatExpression((float) i);
			persoAction.who = who;
			ActionElement waitAction = new ActionElement(ActionKind.wait);
			waitAction.val = wait;
			actions.add(persoAction);
			actions.add(waitAction);
		}
	}
	
	@Override
	public List<AnyElement> addSyntaxicSugarBefore() {
		return actions;
	}
	
	@Override
	public boolean isPlaceHolder() {
		// With this method, the seq element won't be taken into the runtime process. It's just a placeholder
		return true;
	}
	
}
