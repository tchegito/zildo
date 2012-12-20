/**
 * Legend of Zildo
 * Copyright (C) 2006-2012 Evariste Boussaton
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

package zildo.monde.sprites.desc;

import zildo.fwk.bank.SpriteBank;
import zildo.monde.sprites.Reverse;
import zildo.monde.sprites.Rotation;
import zildo.monde.sprites.SpriteModel;
import zildo.monde.sprites.utils.Sprite;
import zildo.monde.util.Angle;
import zildo.monde.util.Point;
import zildo.server.EngineZildo;

/**
 * @author Tchegito
 *
 */
public class ZildoSprSequence {

	
	// Sword attack sequence
	// UP
	Sprite[] seqUp = new Sprite[]{spr(3, Rotation.COUNTERCLOCKWISE, Reverse.HORIZONTAL),
								  spr(1, Rotation.COUNTERCLOCKWISE, null),
								  spr(0, Rotation.CLOCKWISE, null),
								  spr(3, null, Reverse.VERTICAL),
								  spr(2, Rotation.CLOCKWISE, null),
								  spr(1, Rotation.UPSIDEDOWN, null)
								  }; 
	Sprite[] seqRight = new Sprite[]{spr(3, Rotation.UPSIDEDOWN, null),
			  spr(1, null, Reverse.VERTICAL),
			  spr(0, null, Reverse.HORIZONTAL),
			  spr(3, Rotation.COUNTERCLOCKWISE, null),
			  spr(2, null, Reverse.HORIZONTAL),
			  spr(1, Rotation.CLOCKWISE, Reverse.VERTICAL)
			  }; 
	
	Sprite[] seqLeft = null;
	
	Sprite[] seqDown = new Sprite[]{spr(0, null, null),
			  spr(2, null, null),
			  spr(3, null, null),
			  spr(0, Rotation.COUNTERCLOCKWISE, null),
			  spr(2, Rotation.COUNTERCLOCKWISE, null),
			  spr(1, null, null)
			  }; 
	
	Point[] seqUpPts = new Point[]{new Point(14, 9), new Point(15, 17), new Point(0, 22),
			new Point(-3, 18), new Point(-5, 12), new Point(-9, 8)};
	
	//Point[] seqRightPts = new Point[]{new Point(9, 12), new Point(11, 10), new Point(15, 4), 
	//		new Point(14, 0), new Point(11, -6), new Point(6, -9)};
	Point[] seqRightPts = new Point[]{new Point(12, 12), new Point(17, 10), new Point(20, 4), 
			new Point(20, 0), new Point(18, -6), new Point(13, -9)};
	
	//Point[] seqDownPts = new Point[]{new Point(-5, 11), new Point(-6, 10), new Point(6, 19),
		//	new Point(9, 19), new Point(10, 15), new Point(16, 13)};
	
	//Point[] seqDownPts = new Point[]{new Point(-9, 11), new Point(-8, 10), new Point(4, 2),
	//		new Point(9, 0), new Point(8, 7), new Point(14, 9)};
	Point[] seqDownPts = new Point[]{new Point(-3, 5), new Point(-1, -4), new Point(7, -10),
			new Point(9, -12), new Point(15, -7), new Point(20, -5)};
	Point[] seqLeftPts;
	
	public Sprite getSpr(Angle angle, int pos) {
		if (seqLeft == null) {
			seqLeft = new Sprite[6];
			int i=0;
			for (Sprite spr : seqRight) {
				Reverse rev = spr.reverse;
				Rotation rot = spr.rotate;
				if (rot == Rotation.NOTHING || rot == Rotation.UPSIDEDOWN) {
					rev = rev.flipHorizontal();
				} else if (rot == Rotation.CLOCKWISE || rot == Rotation.COUNTERCLOCKWISE) {
					rev = rev.flipVertical();
				}
				seqLeft[i++] = new Sprite(spr.nSpr, spr.nBank, rev, rot);
			}
		}
		switch (angle) {
		case NORD:
			return seqUp[pos];
		case SUD:
			return seqDown[pos];
		case OUEST:
			return seqLeft[pos];
		case EST:
			default:
			return seqRight[pos];
		}
	}
	
	public Point getOffset(Angle angle, int pos) {
		if (seqLeftPts == null) {
			seqLeftPts = new Point[6];
			int i=0;
			for (Point p : seqRightPts) {
				// Not so simple ! Position in seqRightPts takes into account "x + spriteSizeX / 2.
				// So to get a correct reverse, we must remove the "spriteSizeX /2" component,
				// and add the new "spriteSizeX / 2", because it could be different.
				/*
				SpriteModel model = EngineZildo.spriteManagement.getSpriteBank(SpriteBank.BANK_ZILDO).get_sprite(seqRight[i].nSpr);
				int addX = 0;
				int tx = model.getTaille_x();
				if (seqLeft[i].rotate.isWidthHeightSwitched()) {
					tx = model.getTaille_y();
				}
				addX = -tx/2;
				System.out.println("remove "+addX);
				model = EngineZildo.spriteManagement.getSpriteBank(SpriteBank.BANK_ZILDO).get_sprite(seqLeft[i].nSpr);
				tx = model.getTaille_x();
				if (seqLeft[i].rotate.isWidthHeightSwitched()) {
					tx = model.getTaille_y();
				}
				addX += tx/2;
				System.out.println("add "+addX);
				*/
				seqLeftPts[i++] = new Point(9-p.x, p.y);
			}
		}
		switch (angle) {
		case NORD:
			return seqUpPts[pos];
		case SUD:
			return seqDownPts[pos];
		case OUEST:
			return seqLeftPts[pos];
		case EST:
			default:
			return seqRightPts[pos];
		}
	}
	private Sprite spr(int sword, Rotation rot, Reverse rev) {
		return new Sprite(ZildoDescription.SWORD0.getNSpr() + sword, 
				SpriteBank.BANK_ZILDO, rev, rot);
	}
}
