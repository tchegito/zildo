/**
 * Legend of Zildo
 * Copyright (C) 2006-2011 Evariste Boussaton
 * Based on original Zelda : link to the past (C) Nintendo 1992
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

package zildo.fwk.awt;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import org.lwjgl.LWJGLException;

import zeditor.core.selection.ChainingPointSelection;
import zeditor.core.selection.PersoSelection;
import zeditor.core.selection.Selection;
import zeditor.core.selection.SpriteSelection;
import zeditor.core.tiles.TileSelection;
import zeditor.windows.managers.MasterFrameManager;
import zeditor.windows.subpanels.SelectionKind;
import zildo.client.ZildoRenderer;
import zildo.fwk.gfx.PixelShaders.EngineFX;
import zildo.monde.map.Area;
import zildo.monde.map.Case;
import zildo.monde.map.ChainingPoint;
import zildo.monde.map.Tile;
import zildo.monde.map.Zone;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.persos.Perso;
import zildo.server.EngineZildo;
import zildo.server.MapManagement;

/**
 * Interface class between ZEditor and Zildo platform.
 * 
 * @author tchegito
 * 
 */
public class ZildoCanvas extends AWTOpenGLCanvas {

	public enum ZEditMode {
	    NORMAL, COPY, COPY_DRAG;
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	boolean mask=false;
	
	public ZildoCanvas(ZildoScrollablePanel p_panel, String p_mapname)
			throws LWJGLException {
		super();
		panel = p_panel;
		ZildoRenderer renderer = new ZildoRenderer(p_mapname);
		setRenderer(renderer);
	}
	
	public void applyBrush(Point p) {
		// Get brush
		Selection sel = manager.getSelection();
		if (sel != null) {
			SelectionKind kind=sel.getKind();
			switch (kind) {
				case TILES:
				case PREFETCH:
					drawBrush(p, (TileSelection) sel);
					break;
				case CHAININGPOINT:
					moveChainingPoint(p, (ChainingPointSelection) sel);
					break;
				case SPRITES:
					placeSprite(p, (SpriteSelection) sel);
					break;
				case PERSOS:
					placePerso(p, (PersoSelection) sel);
					break;
			}
		}
	}

	private void drawBrush(Point p, TileSelection p_sel) {
		// Apply selected brush to the map
		Area map = EngineZildo.mapManagement.getCurrentMap();
		p_sel.draw(map, new zildo.monde.map.Point(p.x / 16, p.y / 16), mask);
	}

	public void endBrush() {
		Selection sel = manager.getSelection();
		if (sel != null && sel instanceof TileSelection) {
			((TileSelection)sel).finalizeDraw();
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
				back.bank = nTile / 256;
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
				SpriteEntity entity=(SpriteEntity) sel.getElement();
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
	    int w=(cursorLocation.x - camera.x) / 16;
	    int h=(cursorLocation.y - camera.y) / 16;
	    int width=w-i;
	    int height=h-j;
	    List<Case> cases=new ArrayList<Case>();
	    for (int y=j;y<h;y++) {
    	    	for (int x=i;x<w;x++) {
    	    	    cases.add(new Case(map.get_mapcase(x, y + 4 )));
    	    	}
	    }
	    MasterFrameManager.switchCopyTile(width, height, cases);
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

		// Check for sprites out of bound
		List<SpriteEntity> outOfBoundsEntities = area.getOutOfBoundEntities();
		if (outOfBoundsEntities.size() > 0) {
			if (0 == JOptionPane
					.showConfirmDialog(
							this,
							"Some entities are out of bounds. Do you want to remove them ?",
							"ZEditor", JOptionPane.YES_NO_OPTION)) {
				for (SpriteEntity e : outOfBoundsEntities) {
					EngineZildo.spriteManagement.deleteSprite(e);
				}
			}
		}

		String m = area.checkBeforeSave();
		if (m != null) {
			JOptionPane.showMessageDialog(this, m, "ZEditor : error on 'dialog switch'", JOptionPane.ERROR_MESSAGE);
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
		ch.setPx((short) (p_point.x / 16));
		ch.setPy((short) (p_point.y / 16));
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
			List<ChainingPoint> points=EngineZildo.mapManagement.getCurrentMap().getListPointsEnchainement();
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
	    	SpriteEntity entity=findEntity(kind, p);
	    	if (entity != null) {
		    	if (kind == SelectionKind.SPRITES) {
		    		manager.setSpriteSelection(new SpriteSelection(entity));
		    	} else {
			    	manager.setPersoSelection(new PersoSelection((Perso) entity));
		    	}
				entity.setSpecialEffect(EngineFX.PERSO_HURT);
	    	}
	    	break;
		default:
	    }
	}
	
	private SpriteEntity findEntity(SelectionKind p_kind, Point p) {
    	List<SpriteEntity> sprites=EngineZildo.spriteManagement.getSpriteEntities(null);
    	Point camera=panel.getPosition();
    	p.x-=camera.x;
    	p.y-=camera.y;
    	for (SpriteEntity entity : sprites) {
    		int typ=entity.getEntityType();
    		if ((typ == SpriteEntity.ENTITYTYPE_PERSO && p_kind == SelectionKind.PERSOS) || 
    			(typ != SpriteEntity.ENTITYTYPE_PERSO && p_kind == SelectionKind.SPRITES)) {
	    		Zone z=entity.getZone();
	    		if (z.isInto(p.x, p.y)) {
	    			return entity;
	    		}
    		}
    	}
    	return null;
	}
	
	/**
	 * Add a perso to the map
	 * @param p_point
	 * @param p_sel
	 */
	private void placePerso(Point p_point, PersoSelection p_sel) {
		Perso perso=p_sel.getElement();
		perso.setX(p_point.x);
		perso.setY(p_point.y);
		if (!EngineZildo.spriteManagement.isSpawned(perso)) {
			EngineZildo.spriteManagement.spawnPerso(perso);
		}
		changeSprites=true;	// Ask for sprites updating
	}
	
	private void placeSprite(Point p_point, SpriteSelection p_sel) {
		SpriteEntity elem=p_sel.getElement();
		elem.x=p_point.x;
		elem.y=p_point.y;
		elem.setAjustedX(p_point.x);
		elem.setAjustedY(p_point.y);
		if (!EngineZildo.spriteManagement.isSpawned(elem)) {
			EngineZildo.spriteManagement.spawnSprite(elem);
		}
		manager.setSpriteSelection(p_sel);
		changeSprites=true;
	}

	public void toggleMask() {
		this.mask = !mask;
	}
}