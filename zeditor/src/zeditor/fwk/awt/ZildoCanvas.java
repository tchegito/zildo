/**
 * The Land of Alembrum
 * Copyright (C) 2006-2016 Evariste Boussaton
 * 
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package zeditor.fwk.awt;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JOptionPane;

import org.lwjgl.LWJGLException;

import zeditor.core.selection.ChainingPointSelection;
import zeditor.core.selection.PersoSelection;
import zeditor.core.selection.Selection;
import zeditor.core.selection.SpriteSelection;
import zeditor.core.tiles.TileSelection;
import zeditor.tools.AreaWrapper;
import zeditor.tools.checker.AreaChecker;
import zeditor.tools.checker.ErrorDescription;
import zeditor.windows.managers.MasterFrameManager;
import zeditor.windows.subpanels.SelectionKind;
import zildo.client.ZildoRenderer;
import zildo.monde.collision.Rectangle;
import zildo.monde.map.Area;
import zildo.monde.map.Case;
import zildo.monde.map.ChainingPoint;
import zildo.monde.map.Tile;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.SpriteModel;
import zildo.monde.sprites.desc.EntityType;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.util.Zone;
import zildo.server.EngineZildo;
import zildo.server.MapManagement;

/**
 * Interface class between ZEditor and Zildo platform.
 * 
 * @author tchegito
 * 
 */
public class ZildoCanvas extends AWTOpenGLCanvas implements MapCapturer {

	public enum ZEditMode {
	    NORMAL, COPY, COPY_DRAG, TILE_REVERSE_EDIT, TILE_ROTATE_EDIT, TILE_RAISE_EDIT, TILE_LOWER_EDIT, TILE_REMOVE_EDIT;
	    
	    public boolean isTileAttributeLinked() {
	    	return this == TILE_REVERSE_EDIT || this == TILE_ROTATE_EDIT || this == TILE_RAISE_EDIT ||
	    	this == TILE_LOWER_EDIT || this == TILE_REMOVE_EDIT;
	    }
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	boolean gridSprites=false;
	
	public ZildoCanvas(ZildoScrollablePanel p_panel, String p_mapname)
			throws LWJGLException {
		super();
		panel = p_panel;
		ZildoRenderer renderer = new ZildoRenderer(p_mapname);
		setRenderer(renderer);
	}
	
	@SuppressWarnings("unchecked")
	public void applyBrush(Point p) {
		// Get brush
		Selection sel = manager.getSelection();
		if (sel != null) {
			SelectionKind kind=sel.getKind();
			switch (kind) {
				case TILES:
					if (getMode().isTileAttributeLinked()) {
						switch (getMode()) {
							case TILE_REVERSE_EDIT:
								reverseTile(p);
								break;
							case TILE_ROTATE_EDIT:
								rotateTile(p);
								break;
							case TILE_RAISE_EDIT:
								raiseTile(p);
								break;
							case TILE_LOWER_EDIT:
								lowerTile(p);
								break;
							case TILE_REMOVE_EDIT:
								removeTile(p);
							default:
								break;
							}
						break;
					}
				case PREFETCH:
					drawBrush(p, (TileSelection) sel);
					break;
				case CHAININGPOINT:
					moveChainingPoint(p, (ChainingPointSelection) sel);
					manager.updateChainingPoints(null);
					break;
				case SPRITES:
					placeSprite(p, (SpriteSelection<SpriteEntity>) sel);
					break;
				case PERSOS:
					placePerso(p, (PersoSelection) sel);
					break;
			}
		}
		manager.setUnsavedChanges(true);
	}

	private void drawBrush(Point p, TileSelection p_sel) {
		// Apply selected brush to the map
		if (p_sel.getElement() != null) {
			p_sel.draw(makeAreaWrapper(), new zildo.monde.util.Point(p.x / 16, p.y / 16), mask);
			manager.setUnsavedChanges(true);
		}
	}

	public void endBrush() {
		Selection sel = manager.getSelection();
		if (sel != null && sel instanceof TileSelection) {
			((TileSelection)sel).finalizeDraw();
		}
	}
	
	public void reverseTile(Point p) {
		TileSelection sel = getTileSelection();
		if (sel != null) {
			sel.reverse(makeAreaWrapper(), new zildo.monde.util.Point(p.x, p.y), mask);
		}
	}
	
	public void rotateTile(Point p) {
		TileSelection sel = getTileSelection();
		if (sel != null) {
			sel.rotate(makeAreaWrapper(), new zildo.monde.util.Point(p.x, p.y), mask);
		}
	}
	
	public void raiseTile(Point p) {
		TileSelection sel = getTileSelection();
		if (sel != null) {
			sel.raise(makeAreaWrapper(), new zildo.monde.util.Point(p.x, p.y));
		}
	}
	
	public void lowerTile(Point p) {
		TileSelection sel = getTileSelection();
		if (sel != null) {
			sel.lower(makeAreaWrapper(), new zildo.monde.util.Point(p.x, p.y));
		}
	}
	
	public void removeTile(Point p) {
		TileSelection sel = getTileSelection();
		if (sel != null) {
			sel.remove(makeAreaWrapper(), new zildo.monde.util.Point(p.x, p.y), mask);
		}
	}
	
	private TileSelection getTileSelection() {
		Selection sel = manager.getSelection();
		// Default kind is TILES
		if (sel == null) {
			return null;
		}
		SelectionKind kind=sel.getKind();
		switch (kind) {
		case TILES:
			return (TileSelection)sel;
		default:
			return null;
		}
	}
	
	/**
	 * Action launched by right-clicking on the map. Depends on the kind of selection.<br/>
	 * With Tiles, : lear a region of the map sized by the selected brush.<br/>
	 * Otherwise, remove the focuses element.
	 * @param p
	 */
	public void clearWithBrush(Point p) {
		Selection sel = manager.getSelection();
		// Default kind is TILES
		SelectionKind kind=sel == null ? SelectionKind.TILES : sel.getKind();
		switch (kind) {
			case TILES:
				Point size;
				if (sel != null && sel instanceof TileSelection) {
					TileSelection tileSel=(TileSelection) sel;
					size = new Point(tileSel.width, tileSel.height);
				} else {
					size = new Point(1, 1);
				}
				List<Case> cases=new ArrayList<Case>();
				Case empty=new Case();
				// Get the right empty tile associated to map's "atmosphere"
				int nTile=EngineZildo.mapManagement.getCurrentMap().getAtmosphere().getEmptyTile();
				Tile back = empty.getBackTile();
				back.bank = (byte) (nTile >> 8);
				back.index = nTile % 256;	// Empty in outside
				for (int i=0;i<size.x*size.y;i++) {
				    cases.add(new Case(empty));
				}
				TileSelection emptySel = new TileSelection(size.x, size.y, cases);
				drawBrush(p, emptySel);
				break;
			case CHAININGPOINT:
				ChainingPoint ch=(ChainingPoint) sel.getElement();
				if (ch != null) {
					EngineZildo.mapManagement.getCurrentMap().removeChainingPoint(ch);
					manager.updateChainingPoints(null);
				}
				break;
			case SPRITES:
			case PERSOS:
				@SuppressWarnings("unchecked")
				List<SpriteEntity> entities = ((SpriteSelection<SpriteEntity>) sel).getElement();
				for (SpriteEntity entity : entities) {
        				EngineZildo.spriteManagement.deleteSprite(entity);
        				if (sel.getKind() == SelectionKind.PERSOS) {
        					Perso perso = (Perso) entity;
        					EngineZildo.persoManagement.removePerso(perso);
        					// Remove dialogs too
        					EngineZildo.mapManagement.getCurrentMap().getMapDialog().removePersoDialog(perso.getName());
        					manager.setPersoSelection(null);
        				} else {
        					manager.setSpriteSelection(null);
        				}
				}
			default:
				break;
		}
	}

	/**
	 * Start of the dragging zone
	 * @param p
	 */
	public void startCopy(Point p) {
	    startBlock=p;
	    mode=ZEditMode.COPY_DRAG;
	}
	
	public void switchCopyMode() {
	    mode=ZEditMode.COPY;
	    cursorSize=defaultCursorSize;
	}
	
	/**
	 * End of the dragging zone : user has released the mouse button.<br/>
	 * So we stop the COPY mode, and switch the *block* tile.
	 */
	public void endCopy() {
	    mode=ZEditMode.NORMAL;
	    // Save the desired block from the map
	    Area map=EngineZildo.mapManagement.getCurrentMap();
	    Point camera=panel.getCameraTranslation();
	    Point cameraCorrection=panel.getPosition();
	    int i=(startBlock.x+cameraCorrection.x % 16) / 16;
	    int j=(startBlock.y+cameraCorrection.y % 16) / 16;
	    int w=Math.min(map.getDim_x(), (cursorLocation.x - camera.x) / 16);
	    int h=Math.min(map.getDim_y(), (cursorLocation.y - camera.y) / 16);
	    int width=w-i;
	    int height=h-j;
	    
	    SelectionKind selKind=manager.getSelectionKind();
	    if (selKind != SelectionKind.TILES && selKind != SelectionKind.SPRITES) {
			// Force tiles tab
			selKind = SelectionKind.TILES;
			manager.switchTab(selKind);
	    }
	    switch (selKind) {

	    	case SPRITES:
    		// Get all sprites in the range
    		List<SpriteEntity> entities = findEntity(selKind, null, new Zone(i, j, w-i, h-j));
    		MasterFrameManager.switchCopySprites(entities);
    		break;
    	    default:
    		// Get all tiles
        	    List<Case> cases=new ArrayList<Case>();
        	    for (int y=j;y<h;y++) {
            	    	for (int x=i;x<w;x++) {
            	    		Case c = map.get_mapcase(x, y);
            	    		if (c != null) {
            	    			cases.add(new Case(c));
            	    		}
            	    	}
        	    }
        	    MasterFrameManager.switchCopyTile(width, height, cases);
	    }
	}
	
	public void saveMapFile(String p_mapName) {
		MapManagement map = EngineZildo.mapManagement;
		String fileName = p_mapName;
		Area area = map.getCurrentMap();
		if (p_mapName == null) {
			fileName = area.getName();
		}
		if (fileName.indexOf(".") == -1) {
			fileName += ".map";
		}
		
		// Check for map errors, and ask user for fix, if it's possible
		List<ErrorDescription> errors = new AreaChecker(area).check();
		if (!errors.isEmpty()) {
			for (ErrorDescription error : errors) {
				if (error.autofix) {
					JOptionPane.showMessageDialog(this, "ZEditor : "+error.kind.toString()+"\n\nAutomatically fixed.", error.message, JOptionPane.INFORMATION_MESSAGE);
				} else if (error.fixAction == null) {
					JOptionPane.showMessageDialog(this, "ZEditor : "+error.kind.toString(), error.message, JOptionPane.ERROR_MESSAGE);
				} else {
					if (0 == JOptionPane
							.showConfirmDialog(
									this,
									error.message+"\n\nDo you want ZEditor to fix this ?",
									"ZEditor", JOptionPane.YES_NO_OPTION)) {
						error.fixAction.run();
					}
				}
			}
		}
		map.saveMapFile(fileName);
	}

	/**
	 * Ask the server side to load the given map. If we come from a chaining point, then we return the
	 * target point.
	 * @param p_mapName
	 * @param p_fromChangingPoint (optional)
	 * @return ChainingPoint
	 */
	public ChainingPoint loadMap(String p_mapName, ChainingPoint p_fromChangingPoint) {
		MapManagement mapManagement = EngineZildo.mapManagement;
		String previousMapName=mapManagement.getCurrentMap().getName();
		mapManagement.loadMap(p_mapName, false);
		EngineZildo.spriteManagement.updateSprites(false);
		changeMap = true;
		Point p=new Point(0, 0);
		ChainingPoint ch=null;
		if (p_fromChangingPoint != null) {
			Area map = mapManagement.getCurrentMap();
			ch=map.getTarget(previousMapName, 0, 0);
			if (ch != null) {
			    	// Center view on the chaining point
				Zone z=ch.getZone(map);
				p.x=z.x1 - ZildoScrollablePanel.viewSizeX / 2;
				p.y=z.y1 - ZildoScrollablePanel.viewSizeY / 2;
			}
		}
		panel.setPosition(p);
		
		return ch;
	}
	
	public void clearMap() {
		EngineZildo.mapManagement.clearMap();
		changeMap = true;
	}

	/**
	 * Set cursor size
	 * 
	 * @param x
	 *            number of horizontal tiles
	 * @param y
	 *            number of vertical tiles
	 */
	public void setCursorSize(int x, int y) {
		cursorSize = new Point(x * 16, y * 16);
	}
	
	/**
	 * Change the location of a chaining point
	 * @param p_point
	 * @param p_sel
	 */
	private void moveChainingPoint(Point p_point, ChainingPointSelection p_sel) {
		ChainingPoint ch=p_sel.getElement();
		ch.setPx((short) (p_point.x / 8));
		ch.setPy((short) (p_point.y / 8));
		ch.setFloor(manager.getCurrentFloor());
	}
	
	/**
	 * Detect which object of current kind (sprite, chaining point ...) is under the mouse
	 * @param p
	 */
	public void setObjectOnCursor(Point p) {
	    SelectionKind kind =manager.getSelectionKind();
	    if (kind == null) {
	    	return;
	    }
	    switch (kind) {
	    case CHAININGPOINT:
	    	Area map=EngineZildo.mapManagement.getCurrentMap();
			List<ChainingPoint> points=EngineZildo.mapManagement.getCurrentMap().getChainingPoints();
			for (ChainingPoint ch : points) {
			    Zone z=ch.getZone(map);
			    if (z.isInto(p.x, p.y)) {
			    	manager.setChainingPointSelection(new ChainingPointSelection(ch));
			    	return;
			    }
			}
			break;
	    case SPRITES:
	    case PERSOS:
	    	List<SpriteEntity> entities=findEntity(kind, p, null);
	    	if (entities != null && entities.size() > 0) {
	    	    SpriteEntity entity = entities.get(0);
		    	if (kind == SelectionKind.SPRITES) {
		    		manager.setSpriteSelection(new SpriteSelection<SpriteEntity>(entity));
		    	} else {
			    	manager.setPersoSelection(new PersoSelection((Perso) entity));
		    	}
	    	}
	    	break;
		default:
	    }
	}
	
	/**
	 * Returns a set of entities according to a point location, or inside a zone.<br/>
	 * Result is unique for a point, and multiple for a boundary.
	 * @param p_kind
	 * @param p_point
	 * @param p_zone
	 * @return List<SpriteEntity>
	 */
	private List<SpriteEntity> findEntity(SelectionKind p_kind, Point p_point, Zone p_zone) {
    	List<SpriteEntity> sprites=EngineZildo.spriteManagement.getSpriteEntities(null);
    	Point camera=panel.getPosition();
    	Rectangle filterRect = null;
    	if (p_zone != null) {
    	    filterRect = new Rectangle(p_zone);
    	    filterRect.multiply(16);
    	    filterRect.translate(-camera.x, -camera.y);
    	} else {
        	p_point.x-=camera.x;
            	p_point.y-=camera.y;
    	}
    	List<SpriteEntity> results = new ArrayList<SpriteEntity>();
    	for (SpriteEntity entity : sprites) {
    		EntityType typ=entity.getEntityType();
    		if ((typ == EntityType.PERSO && p_kind == SelectionKind.PERSOS) || 
    			(typ != EntityType.PERSO && p_kind == SelectionKind.SPRITES)) {
	    		Zone z=entity.getZone();
	    		if (p_zone != null) {
	    		    if (filterRect.isCrossing(new Rectangle(z))) {
	    			results.add(entity);
	    		    }
	    		} else {
	    		    if (z.isInto(p_point.x, p_point.y)) {
	    			return Arrays.asList(entity);
	    		    }
	    		}
    		}
    	}
    	return results;
	}
	
	/**
	 * Add a perso to the map
	 * @param p_point
	 * @param p_sel
	 */
	private void placePerso(Point p_point, PersoSelection p_sel) {
		List<Perso> elems=p_sel.getElement();
		boolean first=true;
		Point delta = new Point(0,0);
		int floor = manager.getCurrentFloor();
		for (Perso perso : elems) {
        		if (first) {
        		    delta.x = (int) perso.x - p_point.x;
        		    delta.y = (int) perso.y - p_point.y;
        		    perso.setX(p_point.x);
        		    perso.setY(p_point.y);
        		    first = false;
        		} else {
        		    perso.setX(perso.getX() + delta.x);
        		    perso.setY(perso.getY() + delta.y);
        		}
        		if (!EngineZildo.spriteManagement.isSpawned(perso)) {
        			EngineZildo.spriteManagement.spawnPerso(perso);
        		}
        		perso.setFloor(floor);
		}
		changeSprites=true;	// Ask for sprites updating
	}
	
	private void placeSprite(Point p_point, SpriteSelection<SpriteEntity> p_sel) {
		zildo.monde.util.Point location = new zildo.monde.util.Point(p_point.x, p_point.y);
		if (gridSprites) {
			SpriteModel model = p_sel.getElement().get(0).getSprModel();
			location.x = location.x & (0xffff-7);
			location.y = location.y & (0xffff-7);
			location.x+=model.getTaille_x() / 2;
			location.y+=model.getTaille_y();
		}
		p_sel.place(location, manager.getCurrentFloor());
		manager.setSpriteSelection(p_sel);
		changeSprites=true;
	}

	public void switchTileLayer() {
		this.mask = (mask+1) % 3;
	}
	
	public int getTileLayer() {
		return mask;
	}
	
	public void toggleGrid() {
		this.gridSprites = !gridSprites;
	}
	
	
	public AreaWrapper makeAreaWrapper() {
		return new AreaWrapper(EngineZildo.mapManagement.getCurrentMap(), manager.getCurrentFloor());
	}
}