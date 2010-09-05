package zeditor.core.tiles;

import java.util.List;

import zeditor.core.Selection;
import zildo.monde.map.Area;
import zildo.monde.map.Case;
import zildo.monde.map.Point;

/**
 * Cette classe représente une sélection du TileSet. Elle est composée de :
 * <p>
 * <ul>
 * <li>La liste des éléments sélectionnés héritée de la classe {@link Selection}
 * </li>
 * <li>La largeur de la sélection en nombre de cases</li>
 * <li>La hauteur de la sélection en nombre de cases</li>
 * </ul>
 * </p>
 * <p>
 * Un élément de la liste représente l'id de la tuile. La liste <u>doit</u> être
 * remplie de cette manière :
 * </p>
 * <b>TileSet</b>
 * <table border="solid">
 * <tr>
 * <td></td>
 * <td>X</td>
 * <td>X+1</td>
 * <td>X+2</td>
 * </tr>
 * <tr>
 * <td>Y</td>
 * <td>A</td>
 * <td>B</td>
 * <td>C</td>
 * </tr>
 * <tr>
 * <td>Y+1</td>
 * <td>D</td>
 * <td>E</td>
 * <td>F</td>
 * </tr>
 * <tr>
 * <td>Y+2</td>
 * <td>G</td>
 * <td>H</td>
 * <td>I</td>
 * </tr>
 * </table>
 * <p>
 * <b>liste</b><br />
 * {A,B,C,D,E,F,G,H,I}
 * </p>
 * 
 * @author Drakulo
 * 
 */
public class TileSelection extends Selection {
    /**
     * Largeur de la sélection en nombre de cases
     */
    protected Integer width;

    /**
     * Hauteur de la sélection en nombre de cases
     */
    protected Integer height;

    /**
     * Constructeur vide
     */
    public TileSelection() {
	super();
    }

    /**
     * Constructeur
     * 
     * @param w
     *            est la largeur de la sélection (en nombre de cases)
     * @param h
     *            est la hauteur de la sélection (en nombre de cases)
     * @param l
     *            est la liste contenant les éléments
     */
    public TileSelection(Integer w, Integer h, List<Case> l) {
	super(l);
	width = w;
	height = h;
    }

    /**
     * Constructeur
     * 
     * @param w
     *            est la largeur de la sélection (en nombre de cases)
     * @param h
     *            est la hauteur de la sélection (en nombre de cases)
     */
    public TileSelection(Integer w, Integer h) {
	super();
	width = w;
	height = h;
    }

    /**
     * Getter de la largeur de la sélection
     * 
     * @return la largeur de la sélection (en nombre de cases)
     */
    public Integer getWidth() {
	return width;
    }

    /**
     * Setter de la largeur de la séléction
     * 
     * @param width
     *            est la nouvelle largeur à assigner (en nombre de cases)
     */
    public void setWidth(Integer width) {
	this.width = width;
    }

    /**
     * Getter de la hauteur de la sélection
     * 
     * @return la hauteur de la sélection (en nombre de cases)
     */
    public Integer getHeight() {
	return height;
    }

    /**
     * Setter de la hauteur de la séléction
     * 
     * @param height
     *            est la nouvelle hauteur à assigner (en nombre de cases)
     */
    public void setHeight(Integer height) {
	this.height = height;
    }

    /**
     * Surcharge de la méthode toString afin de renvoyer une chaine contenant
     * tous les items séparés par des virgules sans afficher les tiles non
     * mappés qui ont été sélectionnés.
     */
    @Override
    public String toString() {
	String s = null;
	boolean first = true;
	if (items != null && !items.isEmpty()) {
	    s = "[" + width + "," + height + "] >> ";
	    for (int i = 0; i < items.size(); i++) {
		if (first) {
		    first = false;
		} else {
		    s += ", ";
		}
		s += items.get(i);
	    }
	}
	return s;
    }

    /**
     * Draw a set of tiles on the given map at given location
     * @param map
     * @param p (map coordinates, basically 0..63, 0..63)
     */
    public void draw(Area map, Point p) {
    	int dx,dy;
		for (int h = 0; h < height; h++) {
			for (int w = 0; w < width; w++) {
				Case item = items.get(h * width + w);
				if (item != null) {
					dx = p.x + w;
					dy = p.y + h;
					if (map.getDim_x() >= dx && map.getDim_y() > dy) {
						// We know that this is a valid location
						Case c=map.get_mapcase(dx, dy+4);
						// Apply modifications
						int nMotif=item.getN_motif();
						if (nMotif != -1) {	// Smash the previous tile
							c.setN_banque(item.getN_banque());
							c.setN_motif(nMotif);
						} else {
							c.setMasked();
						}
						c.setN_banque_masque(item.getN_banque_masque());
						c.setN_motif_masque(item.getN_motif_masque());
					}
				}
			}
		}
    }
}
