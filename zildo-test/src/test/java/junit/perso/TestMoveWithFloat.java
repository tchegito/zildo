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

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyFloat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import junit.framework.TestCase;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import zildo.monde.sprites.elements.Element;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.PersoNJ;
import zildo.monde.util.Pointf;
import zildo.server.EngineZildo;
import zildo.server.MapManagement;

/**
 * @author Tchegito
 *
 */
public class TestMoveWithFloat extends TestCase {

	Perso heros;
	
	@Test
	public void testMoveWithFloat() {
		heros = new PersoNJ();
		heros.setX(20.2f);
		heros.setY(80.75f);
		
		MapManagement fakeMm = mock(MapManagement.class);
		EngineZildo.mapManagement = fakeMm; 
		doAnswer(new Answer<Boolean>() {
			@Override
			public Boolean answer(InvocationOnMock invocation) throws Throwable {
				Object[] args = invocation.getArguments();
				float x = (Float) args[0];
				float y = (Float) args[1];
				return collideWithRound(x, y);
			}
		}).when(fakeMm).collide(anyFloat(), anyFloat(), any(Element.class));
		
		// Loop
		while (heros.getX() < 64*16 && heros.getY() < 64*16) {
			float xx = 1.5f;
			float yy = 0; // + 1.5f;
	        Pointf secureLocation = heros.tryMove(xx, yy);
	        xx = secureLocation.x;
	        yy = secureLocation.y;
			if (heros.x == xx && heros.y == yy) {
				// Obstacle
				Assert.assertTrue(heros.y < 0);
				return;
			} else {
				heros.setX(xx);
				heros.setY(yy);
			}
			//checkDivideBy16(xx);
			//checkDivideBy16(yy);
			System.out.println("location"+heros.getX()+","+heros.getY());
		}
	}
	
	boolean collideWithInt(float x, float y) {
		return collide((int) x, (int) y);
	}

	boolean collideWithRound(float x, float y) {
		return collide(Math.round(x), Math.round(y));
	}

	boolean collideWithDiv16(float x, float y) {
		System.out.println("in with:"+Math.round(x)+" ,"+Math.round(y));
		return collide(16*Math.round(x / 16f), 16 * Math.round(y / 16f));
	}
	/*
	private void checkDivideBy16(float n) {
		int one = Math.round(n) / 16;
		int two = Math.round(n / 16f);
		Assert.assertEquals(one, two);
	}
	*/
	// line equation : y = -x + 150
	boolean collide(int x, int y) {
		//System.out.println("check collide with "+x+","+y);
		int fx = -x + 150;
		return y >= fx;
	}
}
