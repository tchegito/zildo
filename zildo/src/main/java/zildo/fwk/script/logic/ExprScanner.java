package zildo.fwk.script.logic;


/**
 * Simple string bufferizer, with operators as token.<br/>
 * 
 * String as in-parameter has all its spaces removed.<br/>
 * @author evariste.boussaton
 *
 */
public class ExprScanner {

	final String exp;
	int cursor;
	StringBuilder value;
	
	String nextValue = null;
	
	public ExprScanner(String expression) {
		if (expression == null) {
			throw new RuntimeException("NULL can't be parsed");
		}
		cursor = 0;
		value = new StringBuilder();
		// Remove spaces
		exp = expression.replace(" ", "").replace("[^\\+]\\-", "+-");
	}
	
	public String next() {
		value.setLength(0);
		
		if (nextValue != null) {
			value.append(nextValue);
			if (!nextValue.equals(")")) {
				nextValue = null;
			}
			return value.toString();
		}
		
		while (exp.length() > cursor) {
			char a = exp.charAt(cursor);
			Operator o = isDelimiter(exp, cursor);
			if ( o != null) {
				lastPas = o.symbol.length();	// Remember for goBack method
				cursor += lastPas;
				if (o.symbol.length() > 1) {
					if (value.length() == 0) {
						value.append(o.symbol);
					} else {
						nextValue = o.symbol;
					}
					break;
				} else if (value.length() > 0) {
					nextValue = ""+a;
					break;
				}
				return ""+a;
			} else {
				cursor++;
				lastPas = 1;
				boolean parenthese = (a == '(' || a ==')');
				if (parenthese && value.length() > 0) {	// Parenthese after
//					nextValue = a;
					cursor--;
					break;
				}
				value.append(a);
				if (parenthese) {	// Parenthese before
					break;
				}
			}
		}
		return value.toString();
	}
	
	public boolean hasNext() {
		return exp.length() > cursor;
	}
	
	/**
	 * Returns position in the provided string.
	 * @return int
	 */
	public int position() {
		return cursor;
	}
	
	// Return an operator found in given string at given position
	private Operator isDelimiter(String exp, int cursor) {
		for (Operator o: Operator.values()) {
			String s = o.symbol;
			int idx = 0;
			for ( ; idx < s.length() && cursor+idx < exp.length() && s.charAt(idx) == exp.charAt(cursor+idx); idx++) {
				
			}
			if (idx == s.length()) {
				return o;
			}
		}
		return null;
	}
	
	int lastPas;
	
	public void goBack() {
		cursor -= lastPas;
	}

}
