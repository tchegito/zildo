package zeditor.tools.checker;

import java.util.ArrayList;
import java.util.List;

import zeditor.tools.checker.ErrorDescription.Action;
import zildo.client.sound.Ambient.Atmosphere;
import zildo.fwk.script.model.ZSSwitch;
import zildo.monde.map.AnimatedTiles;
import zildo.monde.map.Area;
import zildo.monde.map.Case;
import zildo.monde.map.ChainingPoint;
import zildo.monde.map.Tile;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.util.Point;
import zildo.server.EngineZildo;
import zildo.server.MapManagement;

public class AreaChecker {

	List<ErrorDescription> errors;
	Area area;

	public AreaChecker(Area p_map) {
		area = p_map;
		errors = new ArrayList<ErrorDescription>();
	}

	/**
	 * Returns an error message, designed for ZEditor. Called before save.
	 * 
	 * @return List<ErrorDescription>
	 */
	public List<ErrorDescription> check() {
		checkDialogSwitch();
		checkOutOfBoundsEntities();
		checkChainingPointsUncovered();
		checkWrongAnimatedTiles();
		
		return errors;
	}

	private void addList(ErrorDescription p_desc) {
		if (p_desc != null) {
			errors.add(p_desc);
		}
	}

	private void checkDialogSwitch() {
		String result = null;
		// 1) dialog switch
		for (Perso p : EngineZildo.persoManagement.tab_perso) {
			String swi = p.getDialogSwitch();
			if (swi != null && swi.length() > 0) {
				try {
					ZSSwitch.parseForDialog(swi);
				} catch (RuntimeException e) {
					result = "Perso " + p.getName() + " : " + e.getMessage();
					addList(new ErrorDescription(
							CheckError.DIALOG_SWITCH_MISMATCH, result));
				}
			}
		}
	}

	private void checkOutOfBoundsEntities() {
		// Check for sprites out of bound
		final List<SpriteEntity> outOfBoundsEntities = area
				.getOutOfBoundEntities();
		if (outOfBoundsEntities.size() > 0) {
			String str = "";
			for (SpriteEntity entity : outOfBoundsEntities) {
				str+=entity.getDesc()+", ";
			}
			str = str.substring(0, str.length() - 2);	// Remove the last ", "
			addList(new ErrorDescription(
					CheckError.SPRITES_OUT_OF_BOUNDS,
					"Some entities are out of bounds ("+str+"). Do you want to remove them ?",
					new Action() {

						@Override
						public void run() {
							for (SpriteEntity e : outOfBoundsEntities) {
								EngineZildo.spriteManagement.deleteSprite(e);
							}
						}
					}));
		}
	}

	final static Point[] vertical_mustBeMasked = new Point[] { new Point(-1, 0), new Point(0, 0), new Point(1, 0), new Point(2, 0),
			new Point(0, 1), new Point(1, 1),
			new Point(0, 2), new Point(1, 2) };
	
	final static Point[] horizontal_mustBeMasked = new Point[] { new Point(0, -1), new Point(1, -1), new Point(0, 0), new Point(1, 0),
		new Point(0, 1), new Point(1, 1) };
	
	private void checkChainingPointsUncovered() {
		MapManagement mapManagement = EngineZildo.mapManagement;
		// Chaining points
		String mess="";
		final List<Case> errorCases = new ArrayList<Case>();
		for (ChainingPoint ch : area.getChainingPoints()) {
			Point[] mustBeMasked;
			int x = ch.getPx();
			int y = ch.getPy();
			if (ch.isBorder()) {
				continue;
			} else if (ch.isVertical()) {
				mustBeMasked = horizontal_mustBeMasked;
				x -= 3;
			} else {
				mustBeMasked = vertical_mustBeMasked;
				// Ok we got an horizontal one => find the walkable side
				y += 2;
			}
			int factor = 1;
			
			if (!area.isOutside(x, y) && !mapManagement.collide(x * 16, y * 16, null)) {
				//System.out.print("Chaining point haut=>bas");
			} else {
				//System.out.print("Chaining point bas=>haut");
				factor = -1;
			}
			//System.out.println(" " + ch.getPx() + "," + ch.getPy());

			// Check all expected tiles
			
			for (Point p : vertical_mustBeMasked) {
				Point toCheck = new Point(ch.getPx() + p.x, ch.getPy() + p.y
						* factor);
				if (!area.isOutside(toCheck.x << 4, toCheck.y << 4)) {
					Case c = area.get_mapcase(toCheck.x, toCheck.y);
					Tile t = c.getForeTile();
					if (t == null) {
						errorCases.add(c);
						mess+="ERROR: tile at " + toCheck+ " should be masked !\n";
					}
				}
			}
		}
		if (mess.length() != 0) {
			addList(new ErrorDescription(CheckError.CHAINING_POINT_UNCOVERED, mess, new Action() {
				@Override
				public void run() {
					int empty = Atmosphere.HOUSE.getEmptyTile();
					for (Case c : errorCases) {
						// We know that the case has no fore tile => copy back on fore
						Tile back = c.getBackTile();
						c.setForeTile(back.clone());
						// Put an empty one on back tile
						back.index = empty & 255;
						back.bank = (byte) (empty >> 8);
					}
				}
			}));
		}
	}
	
	private void checkWrongAnimatedTiles() {
		boolean found = false;
		for (int y = 0; y<area.getDim_y(); y++) {
			for (int x = 0; x<area.getDim_x(); x++) {
				int v = area.readmap(x,  y);
				for (AnimatedTiles at : AnimatedTiles.values()) {
					int addBank = at.bank.getOffset();
					int nbRepeat = 1;
					if (at.repeat != null) {
						nbRepeat = at.repeat.until - at.reference + 1;
					}
					for (int rep = 0 ; rep < nbRepeat; rep++) {
						for (int i : at.others) {
							if (v == ( i + addBank + rep)) {
								found = true;
								area.writemap(x, y, at.reference + rep + addBank);
							}
						}
					}
				}
			}
		}
		if (found) {
			String message="Wrong animated tiles !";
			addList(new ErrorDescription(CheckError.WRONG_ANIMATED_TILE, message, true));
		}
	}
}
