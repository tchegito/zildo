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

package zildo.monde.sprites.elements;

import zildo.client.sound.BankSound;
import zildo.fwk.ZUtils;
import zildo.fwk.script.logic.FloatExpression;
import zildo.monde.map.Tile.TileNature;
import zildo.monde.sprites.Reverse;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.desc.SpriteDescription;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.PersoPlayer;
import zildo.monde.util.Point;
import zildo.server.EngineZildo;


/** Special element, coming with a shadow, disappearing after a while (given by 'spe' attribute) if 'volatil' field is TRUE **/
public class ElementGoodies extends Element {

	// Coeur : nSpr=40
	// Diamant : nSpr=10
	
	private int timeToAcquire;	// Untakeable. Zildo has just to wait to have it (for chest)
	protected boolean volatil=true;	// TRUE=goodies disappear after a delay
	protected boolean taken = false;
	protected boolean delegateTaken = false;
	
	/**
	 * Common goodies : volatil
	 */
	public ElementGoodies() {
		super();
		spe=540;	// Goodies life duration, in frames (generally about 60FPS : 540==9sec)
		shadow = new Element();
		shadow.setSprModel(ElementDescription.SHADOW);
		EngineZildo.spriteManagement.spawnSprite(shadow);
	}

	@Override
	public void setDesc(SpriteDescription p_desc) {
		super.setDesc(p_desc);
		// TODO: Handle this in a more specific way
		if (p_desc == ElementDescription.PORTAL_KEY) {
			volatil = false;
		} else if (p_desc == ElementDescription.SPADE) {
			shadow = null;
		}
	}
	
	public void setShadowDesc(ElementDescription desc) {
		if (desc == null) {
			shadow = null;
			EngineZildo.spriteManagement.deleteSprite(shadow);
		} else {
			shadow.setDesc(desc);
		}
	}
	/**
	 * Constructor for object coming from a chest. He's designed for Zildo.
	 * @param p_zildo
	 */
	public ElementGoodies(Perso p_zildo) {
		linkedPerso=p_zildo;
		timeToAcquire=60;
		spe = 120;
		volatil = false;
		// Block hero during the item rising
		p_zildo.setAttente(timeToAcquire);
	}
	
	@Override
	public void animate() {
		
		TileNature nature = getCurrentTileNature();
		if (nature == null) {
			// If nature is null (probably out of the map), consider it's bottomless, to make the goodies disappear
			nature = TileNature.BOTTOMLESS;
		}
		if (z < 4) {
			switch (nature) {
				case BOTTOMLESS:
				case WATER:
					fall();
					dying = true;
				default:
					break;
			}
		}
		super.animate();
		
		if (shadow != null) {
			shadow.x = x;
			shadow.y = y-2;
		}
		
		if (volatil && !EngineZildo.scriptManagement.isScripting()) {
			spe--;
		}
		
		ElementDescription spr=ElementDescription.fromInt(nSpr);
		if (spr == ElementDescription.DROP_SMALL || spr == ElementDescription.DROP_MEDIUM) {
			// Coeur voletant vers le sol
			if (vx<=-0.15) {
				ax=0.01f;
				reverse = Reverse.NOTHING;
				addSpr=0;	// Coeur tourné vers la gauche
			} else if (vx>=0.15) {
				ax=-0.01f;
				reverse = Reverse.HORIZONTAL;
				addSpr=1;	// Coeur tourné vers la droite
			}
			if (z<=4) {
				nSpr=ElementDescription.DROP_FLOOR.ordinal();
				addSpr=0;
				vx=0;
				ax=0;
				y=y+3;
			} else if (z<=8) {
				nSpr = ElementDescription.DROP_MEDIUM.ordinal();
			}
		}
		
		if (spr == ElementDescription.PORTAL_KEY) {
			if (z <= 4 && (vx != 0 || vy != 0)) {
				vx = 0;
				vy = 0;
				EngineZildo.soundManagement.broadcastSound(BankSound.ZildoAtterit, this);
			}
		}
		
		if (spr==ElementDescription.DROP_FLOOR || spr.isMoney()) {
			// Il s'agit d'un diamant ou du coeur (10)
			int eff=EngineZildo.nFrame % 100;
			// 1) brillance
			if (eff<33 && spr!=ElementDescription.DROP_FLOOR) {		// Les diamants brillent
				addSpr=(eff / 10) % 3;
			}
			// 2) s'arrête sur le sol
			if (z<4) {
				z=4;
			}
		}
		
		if (timeToAcquire > 0) {
			timeToAcquire--;
			if (timeToAcquire == 0) {
				// Zildo will now have the goodies
				int value = 0;
				if (name != null && name.length() != 0) {
					value = (int) new FloatExpression(name).evaluate(null);
				}
				if (((PersoPlayer)linkedPerso).pickGoodies(this, value)) {
					//dying=true;
				}
			}
		}
		
		if (spe==0) {
			// Le sprite doit mourir
			dying=true;
			shadow.dying = true;
		} else if (spe<120) {
			visible=(spe%4>1);
		} else if (spe<60) {
			visible=(spe%2==0);
		}
		
		setAjustedX((int) x);
		setAjustedY((int) y);
	}
	
	@Override
	public boolean isGoodies() {
		return !taken; // As long as it's not taken, it's a goodies (allows a smooth disappearing phase)
	}
	
	@Override
	public boolean beingCollided(Perso p_perso) {
		return true;
	}

	@Override
	public void beingTaken() {
		taken = true;
		if (!delegateTaken) {
			alphaA = -1;	// Make it disappear smoothly
			alphaV = -2;
			if (questTrigger && desc instanceof ElementDescription) {	// Only for bank ELEMENTS now
				// Activate a quest if we're asked for
				String mapName = EngineZildo.mapManagement.getCurrentMap().getName();
				ElementDescription elemDesc = (ElementDescription) desc;
				if (!ZUtils.isEmpty(name)) {
					EngineZildo.scriptManagement.takeItem(mapName, null, name, elemDesc);
				} else {
					EngineZildo.scriptManagement.takeItem(mapName, new Point(x/16, y/16), null, elemDesc);
				}
			}
		}
	}
	
	public void setDelegateTaken(boolean value) {
		delegateTaken = value;
	}
}
