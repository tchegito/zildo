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

package zildo.fwk.gfx;

import zildo.monde.util.Vector4f;

public enum EngineFX {
	NO_EFFECT,
	GUARD_BLUE(new int[]{80, 112, 200}, new int[]{176, 144, 248}), 
	GUARD_RED(new int[]{184, 64, 112}, new int[]{240, 120, 128}), 
	GUARD_YELLOW(new int[]{208, 192, 64}, new int[]{240, 216, 64}), 
	GUARD_BLACK(new int[]{88, 88, 88}, new int[]{120, 120, 136}),
	GUARD_GREEN(new int[]{40, 120, 56}, new int[]{120, 184, 32}), 
	GUARD_PINK(new int[]{232, 96, 176}, new int[]{248, 128, 176}),
	ROBBER_BLUE(new int[]{81, 105, 170}, new int[]{146, 170, 235}),
	PERSO_HURT, 
	FONT_PEOPLENAME,
	SHINY, QUAD,
	INFO,
	FOCUSED,	// FOCUSED is used when we wants to highlight some entity (inventory, or buying something)
	YELLOW_HALO,	// When hero is invulnerable, and for selected items in inventory
	STAR,
	CLIP,
	FIRE,	// Replace sprite by a flame
	BURNING;	// Add a burning flame to the sprite
	
	public final Vector4f darkColor;
	public final Vector4f brightColor;
	
	public boolean needPixelShader() {
		return !(this==NO_EFFECT || this==SHINY || this==QUAD || this==FOCUSED || this==INFO || this==FONT_PEOPLENAME);
	}
	
	public EngineFX fromInt(int i) {
		return EngineFX.values()[i];
	}
	
	private EngineFX() { 
		darkColor = null;
		brightColor = null;
	}
	private EngineFX(int[] dark, int[] bright) {
		darkColor = GFXBasics.createColor256(dark[0], dark[1], dark[2]);
		brightColor = GFXBasics.createColor256(bright[0], bright[1], bright[2]);
	}
	
	/** Normalized texture means that we need coordinates inside the displayed sprite,
	 * from (0,0) upper left corner to (1,1) bottom right corner. **/
	public boolean isNormalizedTex() {
		return this == STAR || this == FIRE;
	}
}