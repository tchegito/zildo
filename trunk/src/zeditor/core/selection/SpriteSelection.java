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

package zeditor.core.selection;

import zeditor.windows.subpanels.SelectionKind;
import zildo.fwk.gfx.PixelShaders.EngineFX;
import zildo.monde.sprites.SpriteEntity;

/**
 * @author Tchegito
 *
 */
public class SpriteSelection extends Selection {

	SpriteEntity sprite;
	EngineFX initial;
	
	@Override
	public SelectionKind getKind() {
		return SelectionKind.SPRITES;
	}
	
	public SpriteSelection(SpriteEntity p_entity) {
		sprite=p_entity;
		initial=sprite.getSpecialEffect();
	}
	
	public SpriteEntity getElement() {
		return sprite;
	}
	
	public void unfocus() {
		sprite.setSpecialEffect(initial);
	}

}
