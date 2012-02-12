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

package zildo.fwk.gfx;

import zildo.monde.util.Vector4f;

import zildo.fwk.opengl.OpenGLStuff;

public enum EngineFX {
	NO_EFFECT,
	GUARD_BLUE(new int[]{20, 28, 50}, new int[]{44, 36, 62}), 
	GUARD_RED(new int[]{46, 16, 28}, new int[]{60, 30, 32}), 
	GUARD_YELLOW(new int[]{52, 48, 16}, new int[]{60, 54, 16}), 
	GUARD_BLACK(new int[]{22, 22, 22}, new int[]{30, 30, 34}), 
	GUARD_GREEN(new int[]{10, 30, 14}, new int[]{30, 46, 8}), 
	GUARD_PINK(new int[]{58, 24, 44}, new int[]{62, 32, 44}), 
	PERSO_HURT, 
	FONT_NORMAL(new int[]{0, 0, 28}, new int[]{62, 62, 62}), 
	FONT_HIGHLIGHT(new int[]{8, 16, 28}, new int[]{60, 54, 16}), 
	SHINY, QUAD,
	FOCUSED;	// FOCUSED is used when we wants to highlight some entity (inventory, or buying something)
	
	public final Vector4f darkColor;
	public final Vector4f brightColor;
	
	public boolean needPixelShader() {
		return !(this==NO_EFFECT || this==SHINY || this==QUAD || this==FOCUSED);
	}
	
	public EngineFX fromInt(int i) {
		return EngineFX.values()[i];
	}
	
	private EngineFX() { 
		darkColor = null;
		brightColor = null;
	}
	private EngineFX(int[] dark, int[] bright) {
		darkColor = OpenGLStuff.createColor64(dark[0], dark[1], dark[2]);
		brightColor = OpenGLStuff.createColor64(bright[0], bright[1], bright[2]);
	}
}