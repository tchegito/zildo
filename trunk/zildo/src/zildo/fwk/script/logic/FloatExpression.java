package zildo.fwk.script.logic;



/**
 * Grammar:<br/>
 * <ul>
 * <li>Val = [-] a</li> 
 * <li>Op = [+|-|*|/|=|<|>]</li>
 * <li>Predicate = [(]* [Val | Predicate Op Predicate] [)]*</li>

 * @author evariste.boussaton
 *
 */
public class FloatExpression {

	// Flag to ask optimization or not. It exists just for testing purpose.
	public static boolean OPTIMIZE = true;
	
	protected static final String RESERVED_WORD_RANDOM = "random";
	protected static final String RESERVED_WORD_DICE10 = "dice10";
	
	private FloatASTNode entireExp;
	
	float value;
	
	public FloatExpression(float p_immediateValue) {
		value = p_immediateValue;
		entireExp = null;
	}
	
	public FloatExpression(String p_expression) {
		ExprScanner scan = new ExprScanner(p_expression);
		entireExp = parse(scan, null, false, false);
		
		// Optimization : if all predicates are immediate values (means that no context is necessary to evaluate)
		// So we simplify all predicates into one single value
		if (OPTIMIZE) {
			try {
				value = entireExp.evaluate(null);
				// Look for any reserved word
				if (p_expression.indexOf(RESERVED_WORD_DICE10) == -1 &&
						p_expression.indexOf(RESERVED_WORD_RANDOM) == -1) {
					entireExp = null;
				}
			} catch (NullPointerException e) {
				// We need a context ! So give up this optimization.
			}
		}
		
	}

	@Override
	public String toString() {
		return entireExp == null ? ""+value : entireExp.toString();
	}
	
	private FloatASTNode parse(ExprScanner scan, Operator previous, boolean parenthese, boolean negative) {
		
		FloatASTNode leftNode = null;
		while (scan.hasNext()) {
			int startPos = scan.position();
			String val = scan.next();
			
			// Look for operators
			Operator op = whichOperator(val);
			if (op != null) {
				if (leftNode == null) {
					if (op == Operator.MINUS) {
						negative = !negative;
						continue;
					} else {
						throw new RuntimeException("MISSING_LEFT_OP "+ op.toString()+" at "+ startPos);
					}
				} else {
					// Check operator precedence
					if (previous != null && !op.hasPriority(previous)) {
						scan.goBack();
						return leftNode;
					}
				}
				FloatASTNode rightNode = parse(scan, op, false, negative);
				if (rightNode == null) {
					throw new RuntimeException("MISSING_RIGHT_OP "+op.toString()+" at "+ scan.position());
				}
				leftNode = new FloatOperator(op, leftNode, rightNode);
			// Look for parentheses
			} else if ("(".equals(val)) {
				leftNode = parse(scan, null, true, negative);
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
				if (negative) {
					val = "-"+val;
					negative = !negative;
				}
				leftNode = getFloatValue(val);
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
		if (entireExp == null) {
			return value;
		} else {
			return entireExp.evaluate(c);
		}
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
	
	public boolean isImmediate() {
		return entireExp == null;
	}
}
