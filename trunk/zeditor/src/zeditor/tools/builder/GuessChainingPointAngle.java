/**
 * Legend of Zildo
 * Copyright (C) 2006-2012 Evariste Boussaton
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

package zeditor.tools.builder;

import zildo.Zildo;
import zildo.fwk.bank.MotifBank;
import zildo.fwk.gfx.engine.TileEngine;
import zildo.monde.Game;
import zildo.monde.map.Area;
import zildo.monde.map.ChainingPoint;
import zildo.monde.util.Angle;
import zildo.server.EngineZildo;

/**
 * @author Tchegito
 *
 */
public class GuessChainingPointAngle {

	public static void main(String[] args) {
		new GuessChainingPointAngle();
	}
	
	/**
	 * 
	 */
	public GuessChainingPointAngle() {
		Zildo.soundEnabled = false;
		// new Modifier().textureBuilder();
		// if (true) System.exit(0);
		Game g = new Game(null, true);
		new EngineZildo(g);
		
		addAngleInAllChainingPoint();
	}
	
	public void addAngleInAllChainingPoint() {
		for (String bankName : TileEngine.tileBankNames) {
			MotifBank motifBank = new MotifBank();
			motifBank.charge_motifs(bankName.toUpperCase());
		}
		new AllMapProcessor() {
			
			int dimX;
			int dimY;
			
			@Override
			public boolean run() {
				Area map = EngineZildo.mapManagement.getCurrentMap();
				dimX = map.getDim_x();
				dimY = map.getDim_y();
				for (ChainingPoint ch : map.getChainingPoints()) {
					// Guess angle for each chaining point
					Angle a = getAngle(ch);
					ch.setComingAngle(a);
				}
				
				return true;
			}
			
			private Angle getAngle(ChainingPoint ch) {
				if (ch.isBorder()) {
					int x = ch.getPx() & 63;
					int y = ch.getPy() & 63;
					if (x == 0) {
						return Angle.EST;
					} else if (x == dimX - 1) {
							return Angle.OUEST;
					} else if (y == 0) {
							return Angle.SUD;
					} else {
							return Angle.NORD;
					}
				}
				if (!ch.isVertical()) {
					// Ok we got an horizontal one => find the walkable side
					int y = (ch.getPy() & 63) + 2;
					if (!EngineZildo.mapManagement.collide(ch.getPx() * 16 + 8, y * 16 + 8, null)) {
						return Angle.SUD;
					} else {
						return Angle.NORD;
					}				
				} else {
					// Vertical one
					int x = (ch.getPx() & 63) + 2;
					if (!EngineZildo.mapManagement.collide(x * 16 + 8, ch.getPy() * 16 + 8 , null)) {
						return Angle.EST;
					} else {
						return Angle.OUEST;
					}						
				}
			}
		}.modifyAllMaps();
	}
}
