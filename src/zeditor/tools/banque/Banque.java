package zeditor.tools.banque;

import java.awt.Point;
import java.util.List;
import java.util.Map;

//Regexps à appliquer:
///////////////////////
//1) commentaires
//find:    \{(.*)\}
//replace: /*$1*/
//2) les points
//find:     \(([0-9]*),([0-9]*)\)
//replace: new Point($1, $2)

public abstract class Banque {

	// Données d'entrée
	Point[] coords;
	List<Point> pkmChanges;
	// Données construites par {@link GenereCorrespondanceDec#doTheJob()}
	Map<Point, Integer> mapCorrespondance;

	// Ensemble des points correspondant à la position haute-gauche de chaque
	// tile
	public Point[] getCoords() {
		return coords;
	}

	// List des numéros de tile où on change de PKM
	// Dans point on a: x=numéro de tile / y=offset Y pour la page suivante
	public List<Point> getPkmChanges() {
		return pkmChanges;
	}

	public void setMapCorrespondance(Map<Point, Integer> map) {
		mapCorrespondance = map;
	}

	/**
	 * Renvoie le numéro de la tile à la position donnée. Renvoie -1 si il n'y a
	 * pas de tile à cet endroit.
	 * 
	 * @param x
	 * @param y
	 * @return int
	 */
	public int getNumTile(int x, int y) {
		Point p = new Point(x, y);
		Integer i = mapCorrespondance.get(p);
		return i == null ? -1 : i.intValue();
	}
}
