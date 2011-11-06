package zeditor.core.tiles;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import zeditor.core.Options;
import zeditor.core.exceptions.ZeditorException;
import zeditor.core.selection.CaseSelection;
import zeditor.tools.CorrespondanceGifDec;
import zeditor.tools.Transparency;
import zeditor.windows.OptionHelper;
import zeditor.windows.managers.MasterFrameManager;
import zildo.client.ClientEngineZildo;
import zildo.fwk.ZUtils;
import zildo.fwk.bank.MotifBank;
import zildo.fwk.gfx.engine.TileEngine;
import zildo.monde.map.Case;
import zildo.monde.map.Tile;
import zildo.monde.map.TileCollision;
import zildo.monde.map.TileInfo;
import zildo.monde.map.Zone;

/**
 * Classe de gestion des Tuiles
 * 
 * @author Drakulo
 */
@SuppressWarnings("serial")
public class TileSet extends ImageSet {

	private CorrespondanceGifDec bridge;

	boolean blockSet = false; // If we are on a map region selected by user

	/**
	 * Constructeur avec paramètres
	 * 
	 * @param p_tileName
	 *            : Nom du set de tuiles en cours
	 * @author Drakulo
	 */
	public TileSet(String p_tileName, MasterFrameManager p_manager) {
		super(p_tileName, p_manager);

		// Construction du pont de correspondance
		bridge = new CorrespondanceGifDec();

	}

	private Image getTileNamed(String p_name) {

		Image tile = tiles.get(p_name);
		if (tile == null) {
			// Generate image
			if (ClientEngineZildo.tileEngine == null) {
				return null;
			}
			int nBank = TileEngine.getBankFromName(p_name);
			MotifBank bank = ClientEngineZildo.tileEngine.getMotifBank(nBank);
			tile = bridge.generateImg(bank);

			// tile = Transparency.makeColorTransparent(tile,
			// Transparency.BANK_TRANSPARENCY);
			tiles.put(p_name, tile);
		}
		return tile;
	}

	class CallbackImageObserver implements ImageObserver {
		@Override
		public boolean imageUpdate(Image img, int infoflags, int x, int y,
				int width, int height) {
			tileHeight = height;
			tileWidth = width;
			return false;
		}
	}

	ImageObserver imgObserver = new CallbackImageObserver();

	/**
	 * Méthode de changement du tile
	 * 
	 * @param p_url
	 *            chemin de l'image du Tile à charger
	 * @author Drakulo
	 * @throws ZeditorException
	 */
	public void changeTile(String p_name) throws ZeditorException {
		// On renomme le tile
		tileName = p_name;

		if (tileName.indexOf("*") != -1) {
			currentTile = null;
			repaint();
			return;
		}
		// On supprime les points de sélection précédents
		startPoint = null;
		stopPoint = null;

		currentTile = getTileNamed(p_name);

		// Récupération de la taille de l'image
		if (currentTile != null) {
			tileWidth = currentTile.getWidth(imgObserver);
			tileHeight = currentTile.getHeight(imgObserver);
			// Si la hauteur n'est pas un multiple de 16, on tronque la taille
			// au multiple inférieur
			if (tileHeight % 16 != 0) {
				tileHeight -= tileHeight % 16;
			}
		}

		// On repaint pour afficher le résultat
		repaint();

		blockSet = false;
	}

	/**
	 * Return the 16x16 tiled zone.
	 */
	@Override
	protected Zone getObjectOnClick(int p_x, int p_y) {
		int x = 16 * (p_x / 16);
		int y = 16 * (p_y / 16);
		Zone z = null;
		if (x < 0) {
			x = 0;
		} else if (x >= tileWidth) {
			x = tileWidth - 16;
		}
		if (y < 0) {
			y = 0;
		} else if (y >= tileHeight) {
			y = tileHeight - 16;
		}

		// Useful ?
		if ((x >= 0 && x <= tileWidth - 16) && (y >= 0 && y <= tileHeight - 16)) {
			z = new Zone(x, y, 16, 16);
		}

		return z;
	}

	/**
	 * Tiles combo loading.
	 * 
	 * @return Object[] : String array, containing bank names.
	 * @author Drakulo
	 */
	public Object[] getTiles() {
		// Récupération des fichiers du dossier de tiles
		List<String> list = new ArrayList<String>();

		for (String bankName : TileEngine.tileBankNames) {
			list.add(bankName);
		}
		list.add("*block*");
		return list.toArray();
	}

	@Override
	protected void specificPaint(Graphics2D p_g2d) {
		if (currentTile != null && bridge != null && !blockSet) {
			// Selon le paramétrage :
			if (Boolean.parseBoolean(OptionHelper
					.loadOption(Options.SHOW_TILES_UNMAPPED.getValue()))) {
				showUnmappedTiles(p_g2d);
			}
			if (Boolean.parseBoolean(OptionHelper
					.loadOption(Options.SHOW_TILES_GRID.getValue()))) {
				showGrid(p_g2d);
			}
			if (Boolean.parseBoolean(OptionHelper
					.loadOption(Options.SHOW_COLLISION.getValue()))) {
				showCollision(p_g2d);
			}
		}
	}

	/**
	 * Méthode privée d'affichage des tuiles non mappées
	 * 
	 * @param g
	 *            le Graphics concerné
	 * @author Drakulo
	 */
	private void showUnmappedTiles(Graphics g) {
		for (int j = 0; j < currentTile.getHeight(null); j += 16) {
			for (int i = 0; i < currentTile.getWidth(null); i += 16) {
				if (bridge.getMotifParPoint(tileName, i, j) < 0) {
					g.setColor(Color.red);

					// Lignes obliques montantes de gauche à droite (/)
					g.drawLine(i + 4, j, i, j + 4);
					g.drawLine(i + 8, j, i, j + 8);
					g.drawLine(i + 12, j, i, j + 12);
					g.drawLine(i + 16, j, i, j + 16);
					g.drawLine(i + 16, j + 4, i + 4, j + 16);
					g.drawLine(i + 16, j + 8, i + 8, j + 16);
					g.drawLine(i + 16, j + 12, i + 12, j + 16);

					// Lignes obliques descendantes de gauche à droite (\)
					g.drawLine(i, j, i + 16, j + 16);
					g.drawLine(i + 4, j, i + 16, j + 12);
					g.drawLine(i + 8, j, i + 16, j + 8);
					g.drawLine(i + 12, j, i + 16, j + 4);
					g.drawLine(i, j + 4, i + 12, j + 16);
					g.drawLine(i, j + 8, i + 8, j + 16);
					g.drawLine(i, j + 12, i + 4, j + 16);

				}
			}
		}
	}

	/**
	 * Méthode privée d'affichage de la grille sur le TileSet
	 * 
	 * @param g
	 *            le Graphics concerné
	 * @author Drakulo
	 */
	private void showGrid(Graphics g) {
		g.setColor(Color.gray);
		// Grille horitontale
		for (int i = 0; i < currentTile.getHeight(null); i += 16) {
			g.drawLine(0, i, currentTile.getWidth(null), i);
		}

		// Grille verticale
		for (int j = 0; j < currentTile.getWidth(null); j += 16) {
			g.drawLine(j, 0, j, currentTile.getHeight(null));
		}
	}

	/**
	 * Affichage des infos de collisions issues de {@link TileCollision}.
	 * 
	 * @param g
	 */
	private void showCollision(Graphics g) {
		CollisionDrawer collisionDrawer = new CollisionDrawer(g);
		for (int j = 0; j < currentTile.getHeight(null); j += 16) {
			for (int i = 0; i < currentTile.getWidth(null); i += 16) {
				TileInfo tileInfo = bridge.getCollisionParPoint(tileName, i, j);
				if (tileInfo != null) {
					collisionDrawer.drawCollisionTile(i, j, tileInfo, false);
				}
			}
		}
	}

	/**
	 * Méthode privée de construction de la sélection
	 */
	@Override
	protected void buildSelection() {
		if (blockSet) {
			return;
		}
		int startX, startY, stopX, stopY, width, height;
		width = 0;
		height = 0;
		List<Case> list = new ArrayList<Case>();

		startX = startPoint.x;
		startY = startPoint.y;
		stopX = stopPoint.x;
		stopY = stopPoint.y;

		Case c;
		int bank = TileEngine.getBankFromName(tileName);
		int nMotif = 0;
		for (int i = startY; i < stopY; i += 16) {
			for (int j = startX; j < stopX; j += 16) {
				nMotif = bridge.getMotifParPoint(tileName, j, i);
				if (nMotif == -1) {
					list.add(null);
				} else {
					c = new Case();
					c.setBackTile(new Tile(bank, bridge.getMotifParPoint(
							tileName, j, i), c));
					list.add(c);
				}
				// On ne compte la largeur que pour la première ligne
				if (height == 0) {
					width++;
				}
			}
			height++;
		}
		// Display tile number (from 1x1 selection only)
		if (width == 1 && height == 1) {
			MasterFrameManager.display("Tile=" + nMotif,
					MasterFrameManager.MESSAGE_INFO);
		}
		currentSelection = new TileSelection(width, height, list);
		manager.setCaseSelection((CaseSelection) currentSelection);
	}

	/**
	 * Build a selection from a Case's list (when user copy a section of the
	 * map)
	 * 
	 * @param width
	 * @param height
	 * @param p_cases
	 */
	public void buildSelection(int width, int height, List<Case> p_cases) {

		currentSelection = new TileSelection(width, height, p_cases);
		manager.getZildoCanvas().setCursorSize(width, height);

		currentTile = new BufferedImage(width * 16, height * 16,
				BufferedImage.TYPE_INT_RGB);
		// We have to redraw the cases on the image
		Iterator<Case> itCase = p_cases.iterator();
		for (int j = 0; j < height; j++) {
			for (int i = 0; i < width; i++) {
				Case theCase = itCase.next();
				Tile back = theCase.getBackTile();
				Tile fore = theCase.getForeTile();
				drawMotif(i, j, back.bank, back.index, false);
				if (fore != null) {
					drawMotif(i, j, fore.bank, fore.index, true);
				}
			}
		}

		tileWidth = currentTile.getWidth(null);
		tileHeight = currentTile.getHeight(null);
		repaint();
		blockSet = true;
	}

	/**
	 * Display tile, with or without mask
	 * 
	 * @param i
	 * @param j
	 * @param nBank
	 * @param nMotif
	 * @param masque
	 */
	private void drawMotif(int i, int j, int nBank, int nMotif, boolean masque) {
		String bankName = ClientEngineZildo.tileEngine
				.getBankNameFromInt(nBank & 127);
		Image bankTile = getTileNamed(bankName);
		Point p = bridge.getPointParMotif(bankName, nMotif);
		Color col = masque ? null : Transparency.BANK_TRANSPARENCY;
		Graphics g = currentTile.getGraphics();
		while (false == g.drawImage(bankTile, i * 16, j * 16, i * 16 + 16,
				j * 16 + 16, p.x, p.y, p.x + 16, p.y + 16, col, null)) {
			// If image isn't ready yet, we wait for it then retry
			ZUtils.sleep(100);
		}
	}

	/**
	 * Getter de la sélection courante sur le TileSet
	 * 
	 * @return la sélection courante
	 */
	public TileSelection getCurrentSelection() {
		return (TileSelection) currentSelection;
	}

	@Override
	protected void handleSelectionRightClick(MouseEvent e) {
		// We need a selection of a single tile
		TileSelection sel = (TileSelection) currentSelection;
		if (sel.height !=1 || sel.width != 1) {
			return;
		}
		Tile tile = sel.getElement().get(0).getBackTile();
		Image singleTile = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
		singleTile.getGraphics().drawImage(currentTile, 0, 0, 16, 16, 
				startPoint.x, startPoint.y, stopPoint.x, stopPoint.y, null); 
		TilePopupMenu menu = new TilePopupMenu(tile, singleTile);
		menu.show(e.getComponent(), e.getX(), e.getY());
	}
}