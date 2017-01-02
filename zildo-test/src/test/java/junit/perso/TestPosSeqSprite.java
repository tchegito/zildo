/**
 * The Land of Alembrum
 * Copyright (C) 2006-2016 Evariste Boussaton
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

package junit.perso;

import org.junit.Assert;
import org.junit.Test;

import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.PersoPlayer;
import zildo.resource.Constantes;

/**
 * Class made to validate the refactor about pos_seqsprite calculation.
 * 
 * @author Tchegito
 *
 */
public class TestPosSeqSprite {

	@Test
	public void checkNonReg() {
		Perso p = new PersoPlayer(0);
		for (int i=0;i<1000;i++) {
		
			p.setPos_seqsprite(i);
			int funcPerso = p.computeSeq(1) % 8;
			int funcBefore = (i % (8 * Constantes.speed)) / Constantes.speed;
			
			Assert.assertEquals("Non ! ", funcBefore, funcPerso);
		}
	}
	
	@Test
	public void checkMoreComplex() {
		Perso p = new PersoPlayer(0);
		for (int i=0;i<1000;i++) {
			p.setPos_seqsprite(i);
			int funcPerso = p.computeSeq(3) % 2;
			int funcBefore = (i % (6 * Constantes.speed)) / (3 * Constantes.speed);

			Assert.assertEquals("No at "+i+"-nth element ! ", funcBefore, funcPerso);
		}
	}
}
