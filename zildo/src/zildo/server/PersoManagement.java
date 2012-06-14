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

package zildo.server;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import zildo.monde.collision.PersoCollision;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.desc.PersoDescription;
import zildo.monde.sprites.elements.Element;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.PersoBat;
import zildo.monde.sprites.persos.PersoFireThing;
import zildo.monde.sprites.persos.PersoGarde;
import zildo.monde.sprites.persos.PersoGardeVert;
import zildo.monde.sprites.persos.PersoHen;
import zildo.monde.sprites.persos.PersoNJ;
import zildo.monde.sprites.persos.PersoRabbit;
import zildo.monde.sprites.persos.PersoRat;
import zildo.monde.sprites.persos.PersoShadowed;
import zildo.monde.sprites.persos.PersoSquirrel;
import zildo.monde.sprites.persos.PersoVolant;
import zildo.monde.sprites.persos.PersoZildo;
import zildo.monde.sprites.persos.ia.PathFinderBee;
import zildo.monde.util.Angle;

//////////////////////////////////////////////////////////////////////
// PersoManagement
//////////////////////////////////////////////////////////////////////
// Class managing characters on a map
//
//////////////////////////////////////////////////////////////////////



public class PersoManagement {


	public List<Perso> tab_perso;
	PersoCollision persoColli;
	
	public PersoManagement()
	{
		tab_perso=new ArrayList<Perso>();
		persoColli = EngineZildo.spriteManagement.persoColli;
	}
	
	public PersoZildo getZildo()
	{
		for (Perso p : tab_perso) {
			if (p.isZildo()) {
				return (PersoZildo) p;
			}
		}
		return null;
	}
	
	public void clearPersos(boolean includingZildo)
	{
		Iterator<Perso> it=tab_perso.iterator();
		if (tab_perso.size() < 1) {
			// We haven't enough characters to process this deletion
			return;
		}
		List<Perso> persoToRemove=new ArrayList<Perso>();
		// Destroy entities
		while (it.hasNext()) {
			Perso perso=it.next();
			if (perso != null && (!perso.isZildo() || includingZildo)) {
				persoToRemove.add(perso);
				it.remove();
			}
		}
		for (Perso p : persoToRemove) {
			EngineZildo.spriteManagement.deleteSprite(p);
		}

	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// collidePerso
	///////////////////////////////////////////////////////////////////////////////////////
	// IN:(x,y) coordinates of character quelPerso
	// OUT:perso colliding
	///////////////////////////////////////////////////////////////////////////////////////
	// Checks wether two characters being collided
	///////////////////////////////////////////////////////////////////////////////////////
    public Perso collidePerso(int x, int y, Element quelElement, int rayon) {
   		return persoColli.checkCollision(x, y, quelElement, rayon);
    }

    public Perso collidePerso(int x, int y, Element quelPerso) {
        return collidePerso(x, y, quelPerso, 7);
    }
	
    public Perso getNamedPerso(String p_name) {
        if (p_name != null && !"".equals(p_name)) {
            for (Perso p : tab_perso) {
                if (p_name.equalsIgnoreCase(p.getName())) {
                    return p;
                }
            }
        }
        return null;
    }
    
	///////////////////////////////////////////////////////////////////////////////////////
	// addPerso
	///////////////////////////////////////////////////////////////////////////////////////
	// Add perso to the list
	///////////////////////////////////////////////////////////////////////////////////////
	public void addPerso(Perso perso)
	{
		tab_perso.add(perso);
	}
	
	public void removePerso(Perso perso) {
		tab_perso.remove(perso);
	}
	
	/**
	 * Create a character onto the map, with basic initializations.
	 * @param p_desc
	 * @param x
	 * @param y
	 * @param z
	 * @param name
	 * @return Perso
	 */
	public Perso createPerso(PersoDescription p_desc, int x, int y, int z, String name, int angle) {
		Perso perso;
		switch (p_desc) {
			case POULE:
	            perso = new PersoHen(x, y);
				break;
			case BAS_GARDEVERT:
				perso = new PersoGardeVert();
				break;
			case GARDE_CANARD:
				perso = new PersoGarde();
				break;
			case CORBEAU:
			case SPECTRE:
			case OISEAU_VERT:
			case VAUTOUR:
				perso = new PersoVolant(p_desc);
				break;
			case LAPIN:
			case PRINCESS_BUNNY:
				perso = new PersoSquirrel(p_desc);
				break;
			case BUCHERON_DEBOUT:
				perso = new PersoShadowed(ElementDescription.SHADOW, 0);
				break;
			case ABEILLE:
				perso = new PersoShadowed(ElementDescription.SHADOW_MINUS, 0);
				perso.setPathFinder(new PathFinderBee(perso));
				break;
			case RABBIT:
				perso = new PersoRabbit();
				break;
			case FIRETHING:
				perso = new PersoFireThing();
				break;
			case CHAUVESOURIS:
				perso = new PersoBat();
				break;
			case RAT:
				perso = new PersoRat();
				break;
			default:
				perso = new PersoNJ();
				break;
		}
		

        perso.setX(x);
        perso.setY(y);
        perso.setZ(z);
        perso.setDesc(p_desc);
        perso.setName(name);
        perso.setAngle(Angle.fromInt(angle));
        perso.setMaxpv(perso.getPv());

        return perso;
    }
	
}