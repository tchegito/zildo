package zeditor.core.tiles;

import java.util.List;

import zeditor.core.prefetch.complex.DropDelegateDraw;
import zeditor.core.selection.CaseSelection;
import zeditor.tools.AreaWrapper;
import zeditor.windows.managers.MasterFrameManager;
import zeditor.windows.subpanels.SelectionKind;
import zildo.monde.map.Case;
import zildo.monde.map.Tile;
import zildo.monde.sprites.Reverse;
import zildo.monde.sprites.Rotation;
import zildo.monde.util.Point;
import zildo.resource.Constantes;

/**
 * Cette classe représente une sélection du TileSet. Elle est composée de :
 * <p>
 * <ul>
 * <li>La liste des éléments sélectionnés héritée de la classe
 * {@link CaseSelection}</li>
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
public class TileSelection extends CaseSelection {
	/**
	 * Largeur de la sélection en nombre de cases
	 */
	public int width;

	/**
	 * Hauteur de la sélection en nombre de cases
	 */
	public int height;

	/**
	 * Constructeur vide
	 */
	public TileSelection() {
		super();
	}

	protected DropDelegateDraw drawer; // Class that handles the drawing of
										// TileSelection

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

		// Default renderer for TileSelection. This could be overriden in
		// subclasses
		// (For example : PrefetchSelection)
		drawer = new DropDelegateDraw();
	}

	@Override
	public SelectionKind getKind() {
		return SelectionKind.TILES;
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
	 * 
	 * @param map
	 * @param p
	 *            (map coordinates, basically 0..63, 0..63)
	 * @param p_mask
	 *            TRUE=user is in 'masked edit' mode
	 */
	public void draw(AreaWrapper map, Point p, int p_mask) {
		int dx, dy;
		for (int h = 0; h < height; h++) {
			for (int w = 0; w < width; w++) {
				Case item = items.get(h * width + w);
				dx = p.x + w;
				dy = p.y + h;
				if (item != null) {
					if (map.getDim_x() > dx && map.getDim_y() > dy && dy >= 0
							&& dx >= 0) {
						// We know that this is a valid location
						Case c = map.get_mapcase(dx, dy);
						if (c == null) {
							c = new Case();
							map.set_mapcase(dx, dy, c);
						}

						// Calls the drawer to do the job
						drawer.draw(c, item, p_mask);
					}
				} else {
					map.set_mapcase(dx, dy, null);
				}
			}
		}
	}

	private Reverse reverseTile(Case item, int p_mask, Reverse p_ref) {
		Tile tile;
		Reverse ref = p_ref;
		switch (p_mask) {
		case 0:
			tile = item.getBackTile();
			break;
		case 1:
			tile = item.getBackTile2();
			break;
		case 2:
			tile = item.getForeTile();
			break;
		default:
			throw new RuntimeException("Value "+p_mask+" is wrong for mask !");
		}
		if (tile != null) {
			if (tile.reverse == null) {
				tile.reverse = Reverse.NOTHING;
			}
			if (p_ref == null) {
				ref = tile.reverse; // Keep a reference reverse value
			}
			boolean flipH = false;
			if (ref == Reverse.HORIZONTAL || ref == Reverse.NOTHING) {
				flipH = true;
			}
			if (p_ref == null && flipH) {
				ref = Reverse.HORIZONTAL;
			}
			if (tile.rotation.isWidthHeightSwitched()) {
				flipH = !flipH;
			}
			if (flipH) {
				tile.reverse = tile.reverse.flipHorizontal();
			} else {
				tile.reverse = tile.reverse.flipVertical();
			}


		}
		item.setModified(true);
		return ref;
	}
	/**
	 * Modifiy 'reverse' attribute for a set of tile, at 'p' location, with the current size.
	 * @param map
	 * @param p
	 * @param p_mask
	 */
	public void reverse(AreaWrapper map, Point p, int p_mask) {
		Reverse ref = null;
			for (int h = 0; h < height; h++) {
				for (int w = 0; w < width; w++) {
					Case item =  map.get_mapcase(p.x/16 + w, p.y/16 + h);
					if (item != null) {
						switch (p_mask) {
						case 0:
							ref = reverseTile(item, 0, ref);
							ref = reverseTile(item, 1, ref);
							ref = reverseTile(item, 2, ref);
							break;
						case 1:
							ref = reverseTile(item, 1, ref);
							break;
						case 2:
							ref = reverseTile(item, 2, ref);
							break;
						default:
							throw new RuntimeException("Value "+p_mask+" is wrong for mask !");
						}
					}
				}
			}
			// Reverse all tiles inside the zone
			if (width > 1 || height > 1) {
				if (ref == null) {
					ref = Reverse.NOTHING;
				}
				int revH=0, revW=0;
				Case[] keepCase = new Case[width * height];
				for (int h = 0; h < height; h++) {
					for (int w = 0; w < width; w++) {
						switch (ref) {
						case ALL: case HORIZONTAL:
							revW = width - w - 1; revH = h; break;	// Symetry around the Y axis
						case VERTICAL:
							revW = w; revH = height - h - 1; break;	// Symetry around the X axis
						case NOTHING:
							revW = width - w - 1; revH = height - h - 1;break;
						}
						keepCase[h * width + w] = map.get_mapcase(p.x/16 + revW, p.y/16 + revH);
					}
				}
				for (int h = 0; h < height; h++) {
					for (int w = 0; w < width; w++) {
						map.set_mapcase(p.x/16 + w, p.y/16 + h, keepCase[h * width + w]);
					}
				}
			}
	}
	
	private void rotateTile(Case item, int p_mask) {
		Tile tile;
		switch (p_mask) {
		case 0:
			tile = item.getBackTile();
			break;
		case 1:
			tile = item.getBackTile2();
			break;
		case 2:
			tile = item.getForeTile();
			break;
		default:
			throw new RuntimeException("Value "+p_mask+" is wrong for mask !");
		}
		if (tile != null) {
			if (tile.rotation == null) {
				tile.rotation = Rotation.NOTHING;
			}
			tile.rotation = tile.rotation.succ();
		}
		item.setModified(true);
	}
	
	/**
	 * Modifiy 'rotate' attribute for a set of tile, at 'p' location, with the current size.
	 * @param map
	 * @param p
	 * @param p_mask
	 */
	public void rotate(AreaWrapper map, Point p, int p_mask) {
			for (int h = 0; h < height; h++) {
				for (int w = 0; w < width; w++) {
					Case item =  map.get_mapcase(p.x/16 + w, p.y/16 + h);
					if (item != null) {
						switch (p_mask) {
						case 0:
							rotateTile(item, 0);
							rotateTile(item, 1);
							rotateTile(item, 2);
							break;
						case 1:
							rotateTile(item, 1);
							break;
						case 2:
							rotateTile(item, 2);
							break;
						default:
							throw new RuntimeException("Value "+p_mask+" is wrong for mask !");
						}
					}
				}
			}
			// Rotate all tiles inside the zone
			if ((width > 1 || height > 1) && width == height) {
				int rotH=0, rotW=0;
				Case[] keepCase = new Case[width * height];
				for (int h = 0; h < height; h++) {
					for (int w = 0; w < width; w++) {
						rotW = h; rotH = width - w - 1;
						keepCase[h * width + w] = map.get_mapcase(p.x/16 + rotW, p.y/16 + rotH);
					}
				}
				for (int h = 0; h < height; h++) {
					for (int w = 0; w < width; w++) {
						map.set_mapcase(p.x/16 + w, p.y/16 + h, keepCase[h * width + w]);
					}
				}
			}
	}
	
	/**
	 * Switch a Case object from one floor to upper one. Case is replaced by an empty tile.
	 * @param map
	 * @param p
	 */
	public void raise(AreaWrapper map, Point p) {
		int px = p.x / 16;
		int py = p.y / 16;
		Case c = map.get_mapcase(px, py);
		if (c != null && (map.floor+1) < Constantes.TILEENGINE_FLOOR) {
			Case existingUpperCase = map.area.get_mapcase(px, py, (byte) (map.floor + 1));
			if (existingUpperCase == null) {
				map.area.set_mapcase(px, py, (byte) (map.floor + 1), c);
				Case empty = new Case();
				int emptyTileValue = map.area.getAtmosphere().getEmptyTile();
				empty.setBackTile(new Tile(emptyTileValue, empty));
				map.set_mapcase(px, py, empty);
			} else {
				MasterFrameManager.display("A tile already exists above !", MasterFrameManager.MESSAGE_ERROR);
			}
		}
	}
	
	/**
	 * Switch a Case object from one floor to lower one. Case is nullified.
	 * @param map
	 * @param p
	 */
	public void lower(AreaWrapper map, Point p) {
		int px = p.x / 16;
		int py = p.y / 16;
		Case c = map.get_mapcase(px, py);
		if (c != null) {
			Case existingLowerCase = map.area.get_mapcase(px, py, (byte) (map.floor - 1));
			if (existingLowerCase == null) {
				map.area.set_mapcase(px, py, (byte) (map.floor - 1), c);
				map.set_mapcase(px, py, null);
			}
		}
	}
	
	/** Remove a complete case, or only tile, when mask is different than 0. **/
	public void remove(AreaWrapper map, Point p, int p_mask) {
		int px = p.x / 16;
		int py = p.y / 16;
		Case c = map.get_mapcase(px, py);
		if (c != null) {
			switch (p_mask) {
			case 0:
			default:
				map.set_mapcase(px, py, null);
				break;
			case 1:
				c.setBackTile2(null);
				break;
			case 2:
				c.setForeTile(null);
				break;
			}
		}
	}
	
	@Override
	public List<Case> getElement() {
		return items;
	}

	public void finalizeDraw() {
	}
}
