package zeditor.core.exceptions;

/**
 * <h1>Exception Zeditor</h1>
 * 
 * @author Drakulo
 */
public class ZeditorException extends Exception {
	private static final long serialVersionUID = 6304086926579848971L;

	/**
	 * Constructeur vide
	 * 
	 * @author Drakulo
	 */
	public ZeditorException() {
		super();
	}

	/**
	 * Constructeur avec paramètre (le message d'erreur)
	 * 
	 * @param s
	 *            String : message d'erreur
	 * @author Drakulo
	 */
	public ZeditorException(String s) {
		super(s);
	}
}
