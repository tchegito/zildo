/**
 * The Land of Alembrum
 * Copyright (C) 2006-2016 Evariste Boussaton
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

package zildo.monde.sprites.persos.boss;

import zildo.client.sound.BankSound;
import zildo.fwk.gfx.EngineFX;
import zildo.monde.Bezier3;
import zildo.monde.collision.Collision;
import zildo.monde.sprites.Reverse;
import zildo.monde.sprites.SpriteModel;
import zildo.monde.sprites.desc.PersoDescription;
import zildo.monde.sprites.desc.SpriteAnimation;
import zildo.monde.sprites.elements.Element;
import zildo.monde.sprites.elements.ElementProjectile;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.PersoNJ;
import zildo.monde.sprites.utils.CompositeElement;
import zildo.monde.sprites.utils.MouvementPerso;
import zildo.monde.util.Angle;
import zildo.monde.util.Pointf;
import zildo.monde.util.Vector2f;
import zildo.server.EngineZildo;

/**
 * @author Tchegito
 *
 */
public class PersoDragon extends PersoNJ {

	CompositeElement neck;
	
	double gamma;
	
	int cnt = 1;
	
	// 4, 5, 6, 7 : wing
	int[] seq = {3, 3, 2, 2, 1, 0, 4, 5, 6, 7, 4, 5, 6, 7};
	
	// 7 :arm
	// Interval during dragon wait to look for Zildo again
	final static int FOCUS_TIME = 15;
	// Circle radius around dragon's head can move
	final static int HEAD_ELONGATION = 30;
	
	ElementProjectile fireBalls;
	boolean shapeInitialized = false;
	
	Perso focusedEnemy;
	
	public PersoDragon(int x, int y) {
		//this.x = x;
		//this.y = y;
		pv = 3;
		setInfo(PersoInfo.ENEMY);
		
		desc = PersoDescription.DRAGON;
		neck = new CompositeElement(this);
	}
	
	@Override
	public void animate(int compteur_animation) {
		if (!shapeInitialized) {
			neck.lineShape(seq.length);
			shapeInitialized = true;
			// Make head and upper wing parts higher
			
			neck.elems.get(6).floor++;
			neck.elems.get(7).floor++;
			neck.elems.get(8).floor++;
			neck.elems.get(11).floor++;
			neck.elems.get(12).floor++;
			
			for (Element e : neck.elems) {
				if (e != this) {	// Element 0 is the dragon itself
					addPersoSprites(e);
				}
			}
		}

		super.animate(compteur_animation);
		
		float yy=y; 
		
		float addYWounded = 0f;
		float addXWounded = 0f;
		if (isWounded()) {
			addYWounded = (float) (10+20*Math.cos(gamma*2));
			addXWounded = (float) (10*Math.sin(gamma*2));
			quel_deplacement = MouvementPerso.SPITFIRE;
		}
		Pointf neckPoint = new Pointf((float) (x + 10*Math.cos(gamma/5)), 
										(float) ( 60 + 4*Math.sin(gamma*2)) - addYWounded );
		Pointf headPoint = new Pointf((float) (x + 0*10*Math.sin(gamma)) + addXWounded,
									(float) (neckPoint.y + 5*0 + 0*5*Math.cos(gamma)) +addYWounded*1.5f);
		
		// 1) Detect Zildo every FOCUS_TIME
		Perso zildo = null;
		if (cnt % FOCUS_TIME == 0) {
			zildo = EngineZildo.persoManagement.lookForOne(this, 18, PersoInfo.ZILDO, false);
			if (zildo != null) {
				headPoint.add(new Vector2f(2, 0));
			}
			focusedEnemy = zildo;
		}
		
		Bezier3 bz = new Bezier3(new Pointf(x, 0), neckPoint, headPoint);
		
		vz += az;
		z += vz;
		
		int wingAddX =0;
		int floorHead = 1;
		if (getQuel_deplacement() == MouvementPerso.RETRACTED) {
			wingAddX += 20;
			floorHead = 0;
			setVisible(z > -150);	// Hide dragon when he's really far under the lava
		}
		neck.elems.get(6).floor = floorHead;
		neck.elems.get(7).floor = floorHead;
		neck.elems.get(8).floor = floorHead;
		neck.elems.get(11).floor = floorHead;
		neck.elems.get(12).floor = floorHead;
		
		Angle headAngle = Angle.SUD;
		for (int i=0;i<neck.elems.size()-1;i++) {
			Element e = neck.elems.get(i+1);
			e.setAddSpr(seq[i]);
			if (i >= 6) {	// Wings
				e.x = neck.elems.get(3).x - 40;
				e.y = neck.elems.get(3).y+10-120;// + 100;
				e.z = neck.elems.get(3).z;// + 30; //60;// + 100; // - 40;
				int factor = 1;
				if (i == 10 || i == 11 || i == 12 || i == 13) {	// Reversed wings
					e.x = e.x + 80;
					e.reverse = Reverse.HORIZONTAL;
					factor = -1;
				}
				if (i == 6 || i == 10) {
					e.x += 9;
					if (i == 10) e.x -= 19;
					e.x += wingAddX * factor;
				} else if (i==7 || i == 11) {
					e.y += 50;
					e.x -= 7 * factor;
					e.x += wingAddX * factor;
				} else if (i == 8 || i == 12) {
					e.y += 50+29;
					e.x -= 14 * factor;
					e.x += wingAddX * factor;
				} else if (i==9 || i == 13) {
					e.y += 50 + 29 + 60;
					// Shift Y and Z so that dragon's arm is displayed before head
					e.y -= 30;
					e.z -= 30;
					e.x += wingAddX * factor;
				}
			} else {
				Pointf interpolated = bz.interpol(i / 5f);
				e.x = interpolated.x;
				e.z = interpolated.y + z;
				if (i == 5) {	// Head
					// Mesure distance with center
					float shiftHead = neck.elems.get(2).x - e.x;
					if (focusedEnemy != null) {
						shiftHead = focusedEnemy.x - e.x;
					}
					if (shiftHead < -12) {	// Head looking left
						e.setAddSpr(0);
						e.reverse = Reverse.NOTHING;
						headAngle = Angle.OUEST;
					} else if (shiftHead > 12) {	// Head looking right
						e.setAddSpr(0);
						e.reverse = Reverse.HORIZONTAL; 
						headAngle = Angle.EST;
					} else  {
						e.setAddSpr(9);
						yy+=10;
					}
					switch (quel_deplacement) {
						case SPITFIRE:
							if (e.getAddSpr() == 0) {
								e.setAddSpr(8);
							} else if (e.getAddSpr() == 9) {
								e.setAddSpr(10);
							} else {
								e.setAddSpr(8);
							}
							break;
						case RETRACTED:	// Make wings above the head during the arm's retractation
							yy -= 70;
							e.z -= 70;
						default:
							break;
					}
				}
				e.y = yy;
			}
			yy = e.y;
		}
		neck.elems.get(1).visible = false;
		alpha = 0;
		
		if (cnt++ % 150 == 0) {
			Element h = neck.elems.get(6);
			int deltaX = headAngle.coords.x * 15;
			Element elem = EngineZildo.spriteManagement.spawnSpriteGeneric(SpriteAnimation.SEWER_SMOKE,
					(int) h.x+deltaX, (int) (h.y + z),
					0, 2, this, null);
			elem.z = h.z + 10;
			elem.setLinkedPerso(this);
			elem.setForeground(true);
		}
		gamma += 0.08;
	}
	
	@Override
	public void beingWounded(float cx, float cy, Perso p_shooter, int p_damage) {
		super.beingWounded(cx, cy, p_shooter, p_damage);
		// Cut dragon actions, especially when he spits fire
		EngineZildo.scriptManagement.stopPersoAction(this);
		// Extend wounded duration
		px *= 4000000;
		// Doesn't blink for 'main' invisible sprite
		specialEffect = EngineFX.NO_EFFECT;
		EngineZildo.soundManagement.broadcastSound(BankSound.BigRat, this);
	}
	
	@Override
	public void stopBeingWounded() {
		super.stopBeingWounded();
		// Restart his behavior
		EngineZildo.scriptManagement.runPersoAction(this, "bossDragon", null, false);
		
	}

	@Override
	public void manageCollision() {
		// Manage collision specifically to the dragon, in order to have only a pertinent part hitting hero
		Element body = neck.elems.get(3);
		SpriteModel model = body.getSprModel();
		int radius = (model.getTaille_x() + model.getTaille_y()) / 4;
		Collision collision = new Collision((int) body.x, (int) body.y, radius, Angle.NORD, this, getDamageType(), null);
		
		collision.cy -= model.getTaille_y() / 2;
		collision.cy -= body.z;
		EngineZildo.collideManagement.addCollision(collision);
	}
}
