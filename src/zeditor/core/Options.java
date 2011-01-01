package zeditor.core;

/**
 * Enumération des options de Zeditor
 * @author Drakulo
 *
 */
public enum Options {
	SHOW_TILES_UNMAPPED("showTilesUnmapped"),
	SHOW_TILES_GRID("showTilesGrid");
	
	/** L'attribut qui contient la valeur associé à l'enum */
	private final String value;
	
	/** Le constructeur qui associe une valeur à l'enum */
	private Options(String value) {
		this.value = value;
	}
	
	/** La méthode accesseur qui renvoit la valeur de l'enum */
	public String getValue() {
		return this.value;
	}
}
