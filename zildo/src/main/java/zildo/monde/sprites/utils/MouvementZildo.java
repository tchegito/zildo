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

package zildo.monde.sprites.utils;

/**
 * Character's movements (at the beginning, it was only for hero)<br/>
 * <br/>
 * Be careful, because these names are used inside the XML scripts. So it would be dangerous to modify them.
 * 
 * @author tchegito
 *
 */
public enum MouvementZildo {
	
	// 
	VIDE,
	SOULEVE,
	BRAS_LEVES,
	TIRE,			// Not with a bow : push/pull
	POUSSE,
	ATTAQUE_EPEE,
	ATTAQUE_ARC,
    ATTAQUE_BOOMERANG,
    ATTAQUE_ROCKBAG,
    TOUCHE,   	// Character being wounded
	SAUTE,   		// Jump from a higher ground ! -> inactive during jump
	FIERTEOBJET,	// hero proudly brandish proudly an object
	MORT,
	TOMBE,	// Falling in a pit
	PLAYING_FLUT,
	SLEEPING,
	WAKEUP;

}
