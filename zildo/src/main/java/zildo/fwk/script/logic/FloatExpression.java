package zildo.fwk.script.logic;

import zildo.fwk.ZUtils;
import zildo.fwk.script.context.IEvaluationContext;
import zildo.fwk.script.logic.FloatVariable.NoContextException;



/**
 * Grammar:<br/>
 * <ul>
 * <li>Val = [-] a</li> 
 * <li>Op = [+|-|*|/|=|<|>]</li>
 * <li>Predicate = [(]* [Val | Predicate Op Predicate] [)]*</li>
 *
 * Handles only binary operators. Ternary are not supported. But it is with {@link ZSSwitch}.
 *
 * @author evariste.boussaton
 *
 */
public class FloatExpression {

	// Flag to ask optimization or not. It exists just for testing purpose.
	public static boolean OPTIMIZE = true;
	
	protected static final String RESERVED_WORD_BELL = "bell";
	protected static final String RESERVED_WORD_RANDOM = "random";
	protected static final String RESERVED_WORD_DICE10 = "dice10";	// number between 1 and 10
	protected static final String RESERVED_WORD_ZILDO = "zildo";
	protected static final String RESERVED_WORD_ZILDOX = "zildo.x";
	protected static final String RESERVED_WORD_ZILDOY = "zildo.y";
	protected static final String RESERVED_WORD_ZILDOZ = "zildo.z";
	protected static final String RESERVED_WORD_ZILDOVX = "zildo.vx";
	protected static final String RESERVED_WORD_ZILDOVY = "zildo.vy";
	protected static final String RESERVED_WORD_ZILDOLOC = "zildo.loc";
	protected static final String RESERVED_WORD_ZILDOSCRX = "zildo.scrX";
	protected static final String RESERVED_WORD_ZILDOSCRY = "zildo.scrY";
	protected static final String RESERVED_WORD_ZILDOFLOOR = "zildo.floor";
	protected static final String RESERVED_WORD_ZILDOANGLEX = "zildo.angle.x";
	protected static final String RESERVED_WORD_ZILDOANGLEY = "zildo.angle.y";
	protected static final String RESERVED_WORD_ZILDOMONEY = "zildo.money";
	protected static final String RESERVED_WORD_ZILDONETTLE = "zildo.nettle";
	protected static final String RESERVED_WORD_FUN = "fun:";
	
	private FloatASTNode entireExp;
	
	float value;
	
	public FloatExpression(float p_immediateValue) {
		value = p_immediateValue;
		entireExp = null;
	}
	
	public FloatExpression(String p_expression) {
		ExprScanner scan = new ExprScanner(p_expression);
		entireExp = parse(scan, null, false);
		
		// Optimization : if all predicates are immediate values (means that no context is necessary to evaluate)
		// So we simplify all predicates into one single value
		if (OPTIMIZE) {
			try {
				value = entireExp.evaluate(null);
				// Look for any reserved word
				if (p_expression.indexOf(RESERVED_WORD_DICE10) == -1 &&
						p_expression.indexOf(RESERVED_WORD_RANDOM) == -1 &&
						p_expression.indexOf(RESERVED_WORD_BELL) == -1) {
					entireExp = null;
				}
			} catch (NoContextException e) {
				// We need a context ! So give up this optimization.
			}
		}
		
	}

	@Override
	public String toString() {
		return entireExp == null ? ""+value : entireExp.toString();
	}
	
	private FloatASTNode parse(ExprScanner scan, Operator previous, boolean parenthese) {
		
		FloatASTNode leftNode = null;
		while (scan.hasNext()) {
			int startPos = scan.position();
			String val = scan.next();
			
			// Look for operators
			Operator op = whichOperator(val);
			if (op != null) {
				if (leftNode == null) {
					if (op == Operator.MINUS) {
						FloatASTNode rightNode = parse(scan, op, false);
						leftNode = new FloatOperator(op, new FloatValue(0f), rightNode);
						continue;
					} else if (previous == Operator.NOT_EQUALS) {
						return parse(scan, previous, false);
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
				FloatASTNode rightNode = parse(scan, op, false);
				if (rightNode == null) {
					throw new RuntimeException("MISSING_RIGHT_OP "+op.toString()+" at "+ scan.position());
				}
				leftNode = new FloatOperator(op, leftNode, rightNode);
			// Look for parentheses
			} else if ("(".equals(val)) {
				FloatASTNode temp = parse(scan, null, true);
				if (leftNode == null) 
					leftNode = temp;
				else 
					leftNode = new FloatBuiltIn(leftNode.toString(), temp.toString());
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
		}
		return leftNode;
	}
	
	/**
	 * Returns an operator enum, if given string is one of them.
	 * @param s
	 * @return Operator
	 */
	private Operator whichOperator(String s) {
		if (s != null) {
			for (Operator op : Operator.values()) {
				if (s.equals(op.getSymbol())) {
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
		if (ZUtils.isNumeric(val)) {
			Float i = Float.parseFloat(val);
			return new FloatValue(i);
		} else {
			return new FloatVariable(val);
		}
	}
	
	public boolean isImmediate() {
		return entireExp == null;
	}
}
