package zildo.fwk.script.logic;


/**
 * Simple string bufferizer, with operators as token.<br/>
 * 
 * String as in-parameter is converted to lower case and all spaces are removed.<br/>
 * @author evariste.boussaton
 *
 */
public class ExprScanner {

	final String exp;
	int cursor;
	StringBuilder value;
	
	Character nextValue = null;
	
	public ExprScanner(String expression) {
		if (expression == null) {
			throw new RuntimeException("NULL can't be parsed");
		}
		cursor = 0;
		value = new StringBuilder();
		// Remove spaces and converts to lower
		exp = expression.replace(" ", "").toLowerCase().replace("[^\\+]\\-", "+-");
	}
	
	public String next() {
		value.setLength(0);
		
		if (nextValue != null) {
			value.append(nextValue);
			if (nextValue != ')') {
				nextValue = null;
			}
			return value.toString();
		}
		
		while (exp.length() > cursor) {
			char a = exp.charAt(cursor);
			cursor++;
			if ( isDelimiter(a)) {
				if (value.length() > 0) {
					nextValue = a;
					break;
				}
				return ""+a;
			} else {
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
	
	private boolean isDelimiter(char a) {
		return (a == '+' || a=='-' || a=='*' || a=='/');
	}
	
	public void goBack() {
		cursor--;
	}

}
