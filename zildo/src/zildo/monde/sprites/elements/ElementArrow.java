/**
 * The Land of Alembrum
 * Copyright (C) 2006-2013 Evariste Boussaton
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
import zildo.fwk.bank.SpriteBank;
import zildo.monde.collision.Collision;
import zildo.monde.collision.DamageType;
import zildo.monde.map.Area;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.desc.EntityType;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.util.Angle;
import zildo.monde.util.Point;
import zildo.server.EngineZildo;

public class ElementArrow extends ElementThrown  {

	final static float arrowSpeed=2.0f;
	
	private final static Point sizeHorizontal = new Point(15,5);
	private final static Point sizeVertical = new Point(5,15);
	
    public ElementArrow(Angle p_angle, int p_startX, int p_startY, int p_startZ, Perso p_shooter) {
        super(p_angle, p_startX, p_startY, p_startZ, arrowSpeed, p_shooter);
        vz = 0.2f;
        az = -0.005f;
        switch (p_angle) {
            case NORD:
                nSpr = ElementDescription.ARROW_UP.ordinal();
                break;
            case EST:
                nSpr = ElementDescription.ARROW_RIGHT.ordinal();
                break;
            case SUD:
                x += 2;
                y += 4;
                nSpr = ElementDescription.ARROW_DOWN.ordinal();
                break;
            case OUEST:
                nSpr = ElementDescription.ARROW_LEFT.ordinal();
            default:
                break;
        }

        z = 6;

        // Add a shadow
        shadow = new Element();
        shadow.x = x;
        shadow.y = y;
        shadow.z = 0;
        shadow.nBank = SpriteBank.BANK_ELEMENTS;
        shadow.nSpr = ElementDescription.SHADOW_SMALL.ordinal();
        shadow.setSprModel(ElementDescription.SHADOW_SMALL);
        EngineZildo.spriteManagement.spawnSprite(shadow);

    }

    @Override
	public void animate() {
    	if (!flying) {
    		animateLanded();
    	} else {
    		animateFlying();
    	}
    }
     
    private void animateFlying() {
        shadow.x = x;
        shadow.y = y;
        switch (angle) {
        	case EST:
        		shadow.x += 5;
        		break;
        	case OUEST:
        		shadow.x -=3;
        		break;
        	default:
        		shadow.x +=1;
        } 
        // Shadow effect
        Area area = EngineZildo.mapManagement.getCurrentMap();
		int altitude=area.readAltitude((int) x/16, (int) y/16);
		boolean highFlying = area.readmap((int)x, (int)y, false, floor) == null;
		if (!highFlying && altitude == relativeZ-1) {
			// The arrow seems to fly under a high place
			int secondAltitude=area.readAltitude((int) x/16,1+(int) (y/16));
			if (secondAltitude == altitude) {
				shadow.y+=10;
				z+=10;
				y+=10;
				relativeZ=altitude;
			}
		}
		super.animate();
		if (highFlying) {
			shadow.y+=10;
		}
    }
    	
    @Override
    public boolean beingCollided(Perso p_perso) {
    	// Arrow just landed : stop move
    	vx=0;
    	vy=0;
    	vz=0;
    	az=0;
    	flying=false;
    	shadow.dying=true;
    	EngineZildo.soundManagement.broadcastSound(BankSound.FlechePlante, this);
    	return true;    	
    }
    
    private final static int[] seqLand={0,1,2,1,2};
    private final static int[] add_landx={0,-1,2, 0,0,0, 0,-1,2, 0,-1,-1};
    private final static int[] add_landy={-1,-1,-1, 0,-1,2, 0,0,0, 0,-1,2};
    
   	private void animateLanded() {
    	// 1: change sprite
    	switch (angle) {
    	case NORD:
        	setSprModel(ElementDescription.ARROW_LAND_UP1);
        	break;
    	case EST:
        	setSprModel(ElementDescription.ARROW_LAND_RIGHT1);
    		break;
    	case SUD:
        	setSprModel(ElementDescription.ARROW_LAND_DOWN1);
    		break;
    	case OUEST:
        	setSprModel(ElementDescription.ARROW_LAND_LEFT1);
        default:
    		break;
    	}
    	int seq=seqLand[(int) vz % 5];
    	addSpr=seq;
    	vz+=0.2;
    	if (vz >= 5) {
    		dying=true;
    	} else {
    		// Adjust sprite position
    		setAjustedX((int) x + add_landx[angle.value* 3 + seq]);
    		setAjustedY((int) y + add_landy[angle.value* 3 + seq]);
    	}
    }
    
    @Override
	public Collision getCollision() {
    	Point pos=new Point(x, y);
    	Perso perso=null;
    	if (EntityType.PERSO == getLinkedPerso().getEntityType()) {
    		perso=(Perso) getLinkedPerso();
    	}
    	if (angle.isHorizontal()) {
    		return new Collision(pos, sizeHorizontal, perso, DamageType.PIERCING, null);
	    } else {
	    	return new Collision(pos, sizeVertical, perso, DamageType.PIERCING, null);
	    }
    }
}
