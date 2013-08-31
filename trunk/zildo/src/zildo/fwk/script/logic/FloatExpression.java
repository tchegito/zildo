package zildo.fwk.script.logic;

import zildo.fwk.script.logic.FloatOperator.Operator;


/**
 * Grammar:<br/>
 * <ul>
 * <li>Val = [NOT] a</li> 
 * <li>Op = [AND|OR]</li>
 * <li>Predicate = [NOT] [(]* [Val | Predicate Op Predicate] [)]*</li>

 * @author evariste.boussaton
 *
 */
public class FloatExpression {

	private FloatASTNode entireExp;
	
	public FloatExpression(String p_expression) {
		ExprScanner scan = new ExprScanner(p_expression);
		entireExp = parse(scan, false, false);
	}

	@Override
	public String toString() {
		return entireExp.toString();
	}
	
	private FloatASTNode parse(ExprScanner scan, boolean priority, boolean parenthese) {
		
		FloatASTNode leftNode = null;
		while (scan.hasNext()) {
			int startPos = scan.position();
			String val = scan.next();
			
			// Look for operators
			Operator op = whichOperator(val);
			if (op != null) {
				if (leftNode == null) {
					throw new RuntimeException("MISSING_LEFT_OP "+ op.toString()+" at "+ startPos);
				}
				FloatASTNode rightNode = parse(scan, op.isPriority(), false);
				if (rightNode == null) {
					throw new RuntimeException("MISSING_RIGHT_OP "+op.toString()+" at "+ scan.position());
				}
				leftNode = new FloatOperator(op, leftNode, rightNode);
			// Look for parentheses
			} else if ("(".equals(val)) {
				leftNode = parse(scan, false, true);
			} else if (")".equals(val)) {
				if (!parenthese) {
					scan.goBack();
				}
				return leftNode;
			} else {
				// Not an operator ==> consider it as a value
				if (leftNode != null) {
					throw new RuntimeException("MISSING_OPERATOR at "+startPos);
				}
				leftNode = getFloatValue(val);
			}
			if (priority) { // || (op != null && op.isPriority())) {
				return leftNode;
			}
		}
		return leftNode;
	}
	
	/**
	 * Returns an operator enum, if given string is one of them.
	 * @param s
	 * @return Operator
	 */
	private Operator whichOperator(String s) {
		if (s != null && s.length() == 1) {
			for (Operator op : Operator.values()) {
				if (s.charAt(0) == op.getChar()) {
					return op;
				}
			}
		}
		return null;
	}
	
	/**
	 * Evaluate parsed expression with given context.<br/>
	 * @param c context for evaluation
	 * @return boolean
	 */
	public float evaluate(IEvaluationContext c) {
		return entireExp.evaluate(c);
	}

	/**
	 * Returns the float value of the parsed predicate. Check is done about integrity, and throw exception if needed.
	 * @param val
	 * @return float
	 * @throws LogicParseException
	 */
	private FloatASTNode getFloatValue(String val) {
		try {
			Float i = Float.parseFloat(val);
			return new FloatValue(i);
		} catch (NumberFormatException e) {
			// It must be a variable
			return new FloatVariable(val);
		}
	}
}
