package zeditor.core.exceptions;

public class TileSetException extends ZeditorException {
	private static final long serialVersionUID = -6599731056904859722L;

	/**
	 * Constructeur vide
	 * 
	 * @author Drakulo
	 */
	public TileSetException() {
		super();
	}

	/**
	 * Constructeur avec paramètre (le message d'erreur)
	 * 
	 * @param s
	 *            String : message d'erreur
	 * @author Drakulo
	 */
	public TileSetException(String s) {
		super("[TileSet] " + s);
	}
}
