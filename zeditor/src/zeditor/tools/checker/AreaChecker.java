package zeditor.tools.checker;

import java.util.ArrayList;
import java.util.List;

import zeditor.tools.checker.ErrorDescription.Action;
import zildo.client.sound.Ambient.Atmosphere;
import zildo.fwk.collection.IntSet;
import zildo.fwk.script.model.ZSSwitch;
import zildo.monde.map.AnimatedTiles;
import zildo.monde.map.Area;
import zildo.monde.map.Case;
import zildo.monde.map.Case.TileLevel;
import zildo.monde.map.ChainingPoint;
import zildo.monde.map.Tile;
import zildo.monde.sprites.Rotation;
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
		checkUnexistentFloor();
		
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
		List<String> messageLines=new ArrayList<String>();
		final List<Case> errorCases = new ArrayList<Case>();
		for (ChainingPoint ch : area.getChainingPoints()) {
			Point[] mustBeMasked = null;
			int x = ch.getPx() / 2;
			int y = ch.getPy() / 2;
			if (ch.isBorder()) {
				// Check doors on border chaining point
				if (ch.isVertical()) {
					IntSet doorHorizontalRight = new IntSet(192 + 256*3);
					IntSet doorHorizontalLeft = new IntSet(197 + 256*3);
					for (int xx = x;xx < x+3;xx++) {
						for (int i=0;i<area.getDim_y();i++) {
							Case c = area.get_mapcase(xx,  i);
							if (c != null) {
								Tile t = c.getBackTile();
								if (doorHorizontalRight.contains(t.getValue()) || doorHorizontalLeft.contains(t.getValue())) {
									mustBeMasked = horizontal_mustBeMasked;
									y = i;
									break;
								}
							}
						}
					}
				} else {
					continue;
				}
			} else if (ch.isVertical()) {
				mustBeMasked = horizontal_mustBeMasked;
				x -= 3;
			} else if (!ch.isSingle()) {
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
			
			if (mustBeMasked != null) {
				for (Point p : mustBeMasked) {
					Point toCheck = new Point(x + p.x, y + p.y
							* factor);
					if (!area.isOutside(toCheck.x << 4, toCheck.y << 4)) {
						Case c = area.get_mapcase(toCheck.x, toCheck.y);
						Tile t = c.getForeTile();
						if (t == null) {
							errorCases.add(c);
							messageLines.add("ERROR: tile at " + toCheck+ " should be masked !");
						}
					}
				}
			}
		}
		



		
		if (!messageLines.isEmpty()) {
			int size = messageLines.size();
			if (size > 20) {
				messageLines = messageLines.subList(0, 20);
				messageLines.add("( still "+(size - 20)+" same kind)");
			}
			String mess = join(messageLines, "\n");
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
	
	private String join(List<String> list, String character) {
		StringBuilder s = new StringBuilder("");
		for (String elem : list) {
			s.append(elem).append(character);
		}
		return s.toString();
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
								TileLevel level = at.isBack2() ? TileLevel.BACK2 : TileLevel.BACK;
								area.writemap(x, y, at.reference + rep + addBank, level, Rotation.NOTHING);
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
	
	private void checkUnexistentFloor() {
		boolean found = false;
		for (int floor = 0;floor<=2;floor++) {
			for (int y = 0; y<area.getDim_y(); y++) {
				for (int x = 0; x<area.getDim_x(); x++) {
					Case c = area.get_mapcase(x, y, floor);
					if (c != null) {
						Tile back = c.getBackTile();
						if (floor > 0 && back.getValue() == 256 * 6 + 35) {
							Case c2 = area.get_mapcase(x, y, floor-1);
							if (c2 == null) {		
								found = true;
								area.set_mapcase(x, y, (byte) (floor-1), new Case()); 
							}
						}
					}
				}
			}
		}
		if (found) {
			String message="Tile with special value needed a tile at lower floor !";
			addList(new ErrorDescription(CheckError.FLOOR_UNEXISTING, message, true));
		}
	}
}
