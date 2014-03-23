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

package zildo.server;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import zildo.monde.collision.PersoCollision;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.desc.PersoDescription;
import zildo.monde.sprites.elements.Element;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.Perso.PersoInfo;
import zildo.monde.sprites.persos.PersoBat;
import zildo.monde.sprites.persos.PersoFireThing;
import zildo.monde.sprites.persos.PersoFish;
import zildo.monde.sprites.persos.PersoFlyingSerpent;
import zildo.monde.sprites.persos.PersoFox;
import zildo.monde.sprites.persos.PersoGarde;
import zildo.monde.sprites.persos.PersoGardeVert;
import zildo.monde.sprites.persos.PersoGreenBlob;
import zildo.monde.sprites.persos.PersoNJ;
import zildo.monde.sprites.persos.PersoPoultry;
import zildo.monde.sprites.persos.PersoRabbit;
import zildo.monde.sprites.persos.PersoRat;
import zildo.monde.sprites.persos.PersoShadowed;
import zildo.monde.sprites.persos.PersoSpider;
import zildo.monde.sprites.persos.PersoSquirrel;
import zildo.monde.sprites.persos.PersoVolant;
import zildo.monde.sprites.persos.PersoZildo;
import zildo.monde.sprites.persos.ia.PathFinderBee;
import zildo.monde.sprites.utils.MouvementPerso;
import zildo.monde.util.Angle;
import zildo.monde.util.Point;

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
        	// Iterate in reverse order => useful when map is scrolling
        	// In this case, we have two maps in memory, and potentially two characters
        	// with the same name. So, the one on the new map is more interesting.          
        	for (int i = tab_perso.size() - 1; i>=0; i--) {
        		Perso p = tab_perso.get(i);
                if (p_name.equalsIgnoreCase(p.getName())) {
                    return p;
                }
            }
        }
        return null;
    }
    
    public Perso getFollower(Perso p_perso) {
    	for (Perso p : tab_perso) {
    		if (p.getFollowing() == p_perso) {
    			return p;
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
			case CANARD:
	            perso = new PersoPoultry(p_desc, x, y);
	            break;
			case BROWN_CAT:
			case GREY_CAT:
	            perso = new PersoPoultry(p_desc, x, y);
	            ((PersoPoultry)perso).getShadow().setDesc(ElementDescription.SHADOW_SMALL);
	            perso.setQuel_deplacement(MouvementPerso.CAT, true);
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
			case PRINCESSE:
			case VIEUX_SAGE:
			case VIEUX_SAGE2:
				perso = new PersoShadowed(ElementDescription.SHADOW, 2);
				break;
			case ABEILLE:
				perso = new PersoShadowed(ElementDescription.SHADOW_MINUS, 0);
				perso.setPathFinder(new PathFinderBee(perso));
				break;
			case GREEN_BLOB:
				perso = new PersoGreenBlob();
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
			case BIG_RAT:
				perso = new PersoRat(p_desc);
				break;
			case FOX:
			case FALCOR:
				perso = new PersoFox();
				break;
			case ZILDO:
				perso = new PersoZildo(x, y, null);
				break;
			case STONE_SPIDER:
				perso = new PersoSpider(x, y);
				break;
			case FLYINGSERPENT:
				perso = new PersoFlyingSerpent();
				break;
			case FISH:
				perso = new PersoFish();
				break;
			case LOUISE:
				perso = new PersoShadowed(ElementDescription.SHADOW_SMALL, 2);
				break;
			case IGOR:
				perso = new PersoShadowed();
				break;
			default:
				perso = new PersoNJ();
				break;
		}
		
		switch (p_desc) {
			case SQUELETTE:
				perso.setPv(2);
				break;
			case CREATURE:
				perso.setPv(2);
				perso.setSpeed(1.5f);
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

	/**
	 * Return the first discoverd character inside a circular zone around a given character.
	 * @param p_looker Looking character
	 * @param radius radius of the circular zone
	 * @return Perso
	 */
	public Perso lookFor(Perso p_looker, int radius, PersoInfo p_info) {
		if (p_looker != null) {
			for (Perso p : tab_perso) {
				if (p != p_looker && (p_info == null || p.getInfo() == p_info) && p.visible && p.getPv()>0) {
					double distance = Point.distance(p_looker.x,  p_looker.y, p.x, p.y);
					if (distance < radius * 16) {
						return p;
					}
				}
			}
		}
		return null;
	}
}