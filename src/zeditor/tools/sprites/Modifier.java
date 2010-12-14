/**
 * Legend of Zildo
 * Copyright (C) 2006-2010 Evariste Boussaton
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

package zeditor.tools.sprites;

import zildo.fwk.bank.SpriteBank;
import zildo.monde.Game;
import zildo.monde.sprites.SpriteModel;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.desc.PersoDescription;
import zildo.prefs.Constantes;
import zildo.server.EngineZildo;

/**
 * Test class, doesn't apart to real project.
 *
 * @author Tchegito
 *
 */
public class Modifier {
	 public static void main(String[] args) {
		Game g=new Game(null, true);
		 
		EngineZildo engine=new EngineZildo(g);
		
		EngineZildo.spriteManagement.charge_sprites("PNJ3.SPR");
		
		// Remove spector
		SpriteBankEdit bankIn=new SpriteBankEdit(EngineZildo.spriteManagement.getSpriteBank(SpriteBank.BANK_PNJ));
		SpriteBankEdit bankOut=new SpriteBankEdit(EngineZildo.spriteManagement.getSpriteBank(6));
		int fin=bankOut.getNSprite();
		for (int i=0;i<6;i++) {
			int nSprOriginal=PersoDescription.VOLANT_BLEU.getNSpr() + i;
			SpriteModel model=bankIn.get_sprite(nSprOriginal);
			System.out.println("On copie le sprite no"+nSprOriginal);
			bankOut.addSpr(fin+i, model.getTaille_x(), model.getTaille_y(), bankIn.getSpriteGfx(nSprOriginal));
		}
		bankIn.removeSpr(124);
		bankIn.removeSpr(124);
		bankIn.removeSpr(124);
		bankIn.removeSpr(124);
		bankIn.removeSpr(124);
		bankIn.removeSpr(124);
		bankIn.saveBank();
		bankOut.saveBank();
		// New one
		
		
		//bankOut.removeSpr(19);
		//bankOut.removeSpr(20);
	}
}
