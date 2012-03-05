package zeditor.core.selection;

import java.util.ArrayList;
import java.util.List;

import zeditor.core.tiles.TileSelection;
import zildo.monde.map.Case;
/**
 * Cette classe représente une sélection dans Zeditor. Elle est abstraite et ne
 * peut pas être utilisée comme telle. Il faut utiliser l'une des classes filles
 * suivantes :
 * <p>
 * <ul>
 * <li>{@link TileSelection}</li>
 * </ul>
 * </p>
 * 
 * @author Drakulo
 * 
 */
public abstract class CaseSelection extends Selection {
	/**
	 * Liste des éléments de la sélection
	 */
	protected List<Case> items;

	/**
	 * Constructeur vide
	 */
	public CaseSelection() {
		items=new ArrayList<Case>();
	}

	/**
	 * Constructeur à partir d'une liste
	 * 
	 * @param l
	 *            est la liste des éléments de la sélection
	 */
	public CaseSelection(List<Case> l) {
		items = l;
	}

	/**
	 * Setter de la liste d'éléments
	 * 
	 * @param l
	 *            est la liste des éléments
	 */
	public void setItems(List<Case> l) {
		items = l;
	}


}
