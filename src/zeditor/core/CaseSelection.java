package zeditor.core;

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
	 * Getter de la liste d'éléments
	 * 
	 * @return La liste des items
	 */
	public List<Case> getItems() {
		return items;
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

	/**
	 * Récupère un élément de la sélection
	 * 
	 * @param Index
	 *            est l'index de l'élément à récupérer dans la liste
	 * @return la valeur de l'élément
	 */
	public Case getItem(Integer index) {
		if (index > items.size()) {
			return null;
		} else {
			return items.get(index);
		}
	}

}
