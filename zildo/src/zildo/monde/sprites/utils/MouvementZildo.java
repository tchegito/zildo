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

package zildo.monde.sprites.utils;

public enum MouvementZildo {
	
	// Mouvements de Zildo
	VIDE,
	SOULEVE,
	BRAS_LEVES,
	TIRE,			// Pas à l'arc : pousser/tirer
	POUSSE,
	ATTAQUE_EPEE,
	ATTAQUE_ARC,
    ATTAQUE_BOOMERANG,
    TOUCHE,   	// Quand Zildo se fait toucher
	SAUTE,   		// Zildo saute une colline ! -> inactif pendant le saut
	FIERTEOBJET,	// Zildo brandit fièrement un objet
	MORT,
	TOMBE,	// Falling in a pit
	PLAYING_FLUT;

}
