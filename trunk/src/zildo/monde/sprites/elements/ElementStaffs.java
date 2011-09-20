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

package zildo.monde.sprites.elements;

import zildo.client.sound.BankSound;
import zildo.fwk.bank.SpriteBank;
import zildo.monde.Hasard;
import zildo.monde.map.Angle;
import zildo.monde.map.Point;
import zildo.monde.quest.script.DispatchRoundTrip;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.persos.Perso;
import zildo.server.EngineZildo;


/**
 * @author Tchegito
 *
 */
public class ElementStaffs extends ElementChained {

    DispatchRoundTrip dispatcher;
    
	public ElementStaffs(int p_x, int p_y) {
		super(p_x, p_y);
		
		dispatcher = new DispatchRoundTrip(new Point(p_x, p_y), new Point(0, 100), 16);
	}
	
	boolean left=false;
	
	@Override
	protected Element createOne(int p_x, int p_y) {
	    // Throw a staff from left or right
	    int side = left ? 1 : -1;
	    left=!left;
	    
	    Point p = dispatcher.next();
	    
	    p.x = p_x + 200 * side;

	    Angle a = side == -1 ? Angle.EST : Angle.OUEST;
	    
	    delay = 30+Hasard.intervalle(10);
	    
	    Element staff = new ElementStaff(a, p.x, p.y, 15, 2, null);
	    EngineZildo.soundManagement.broadcastSound(BankSound.FlecheTir, staff);

	    return staff;
	    
	}

	public class ElementStaff extends ElementThrown {

	    public ElementStaff(Angle p_angle, int p_startX, int p_startY,
		    int p_startZ, float p_speed, Perso p_shooter) {
		super(p_angle, p_startX, p_startY, p_startZ, p_speed, p_shooter);
	        setSprModel(ElementDescription.STAFF_POUM);
	        
	        vz = 0.2f;
	        vx = 1.3f*vx;
	        az = -0.005f;
	        z = 8;
	        
	        if (angle == Angle.OUEST) {
	            reverse = REVERSE_HORIZONTAL;
	        }

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
	        super.animate();
	        
	    }
	}
}
