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

package zildo.monde.sprites.magic;

import java.util.ArrayList;
import java.util.List;

import zildo.fwk.bank.SpriteBank;
import zildo.fwk.gfx.EngineFX;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.elements.Element;
import zildo.monde.sprites.utils.CompositeElement;
import zildo.server.EngineZildo;

public class ShieldEffect {

    public enum ShieldType {
        REDBALL
    }

    float alpha = 0.0f;
    float gamma = 0.0f;
    Element affected; // Shield is affected to this element
    ShieldType shieldType;
    List<CompositeElement> composites=new ArrayList<CompositeElement>();

    public ShieldEffect(Element p_linked, ShieldType p_shieldType) {
        affected = p_linked;
        shieldType = p_shieldType;
        switch (shieldType) {
            case REDBALL:
            	for (int i = 0;i<4;i++) {
	            	Element ball=new Element();
	                ball.setNSpr(ElementDescription.REDBALL3.ordinal());
	                ball.setNBank(SpriteBank.BANK_ELEMENTS);
	                ball.setSpecialEffect(EngineFX.SHINY);
	        		EngineZildo.spriteManagement.spawnSprite(ball);
	                CompositeElement composite=new CompositeElement(ball);
	                composite.followShape();
	                composites.add(composite);
	                ball.x = affected.x;
	                ball.y = affected.y;
	                ball.z = affected.z;
	                ball.setLinkedPerso(affected);
	                //ball.zoom = 200;
            	}
                break;
        }
    }

    public void animate() {

        switch (shieldType) {
            case REDBALL:
            	float beta=0.0f;
            	float coeff=1.0f;
            	for (CompositeElement composite : composites) {
	                composite.animate();
	                Element elem=composite.getRefElement();
	                elem.x = (int) (affected.x + 5 * Math.sin((alpha*alpha+beta) * coeff));
	                elem.y = (int) (affected.y + 10 + 2 * Math.cos((alpha+beta) * coeff));
	                elem.z = 20; // - beta * 10;
	                
	                elem.y -= elem.getSprModel().getTaille_y() / 2;
	
	                elem.setAjustedX((int) elem.x);
	                elem.setAjustedY((int) elem.y);
	                
	                beta += 0.8f; //gamma; //0.9;//Math.PI / 4;
	                coeff *= -1f;
            	}
                alpha += 0.1;
                gamma += 0.2;
                break;
        }
    }
    
    public void kill() {
		for (CompositeElement composite : composites) {
			composite.die(false);
		}    	
    }
}