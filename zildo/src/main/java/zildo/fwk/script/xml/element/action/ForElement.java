package zildo.fwk.script.xml.element.action;

import java.util.Arrays;
import java.util.List;

import org.w3c.dom.Element;

import zildo.fwk.script.logic.FloatExpression;
import zildo.fwk.script.xml.ScriptReader;
import zildo.fwk.script.xml.element.AnyElement;
import zildo.fwk.script.xml.element.LanguageElement;
import zildo.fwk.script.xml.element.logic.VarElement;

public class ForElement extends LoopElement {

	public String varName;
	public int nbIterations;

	LanguageElement incrementation;
	
	public ForElement() {
		super(ActionKind._for);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void parse(Element p_elem) {
		xmlElement = p_elem;

		actions = (List<LanguageElement>) ScriptReader.parseNodes(xmlElement);

		varName = readAttribute("var");
		nbIterations = readInt("value");
		
		if (actions.isEmpty()) {
			throw new RuntimeException("For is empty !");
		}
		if (varName == null || nbIterations == 0) {
			throw new RuntimeException("A variable name and a number of iterations must be provided in For element !");
		}
		
		whileCondition = new FloatExpression(varName+"!="+nbIterations);

		// Create a language element incrementing the variable
		incrementation = VarElement.createVarAction(varName, varName+"+1");
		actions.add(incrementation);
	}
	
	@Override
	public List<AnyElement> addSyntaxicSugarBefore() {
		// Create a variable before the 'for' statement
		return Arrays.asList((AnyElement) VarElement.createVarAction(varName, "0")); 
	}
}
