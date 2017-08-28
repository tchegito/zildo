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

package junit.area;

import org.junit.Assert;
import org.junit.Test;

import zildo.fwk.ui.UIText;
import zildo.monde.map.Region;

/**
 * @author Tchegito
 *
 */
public class CheckAreaRegions {

	@Test
	public void regionMatcher() {
		Assert.assertSame(Region.Lugdunia, Region.fromMapName("coucou"));
		Assert.assertSame(Region.Lugdunia, Region.fromMapName("d4m8"));
		Assert.assertSame(Region.LugduniaCastle, Region.fromMapName("prisonext"));
		Assert.assertSame(Region.LugduniaForest, Region.fromMapName("bosquet"));
		Assert.assertSame(Region.LugduniaPrison, Region.fromMapName("prison4"));
		Assert.assertSame(Region.ThievesCamp, Region.fromMapName("voleursm2"));
		Assert.assertSame(Region.LugduniaForest, Region.fromMapName("igorlily"));
		Assert.assertSame(Region.ClearingOaks, Region.fromMapName("promenade2"));
		Assert.assertSame(Region.ThievesCamp, Region.fromMapName("voleurs"));
		Assert.assertSame(Region.CaveFlames, Region.fromMapName("voleursg5"));
		Assert.assertSame(Region.Fishermen, Region.fromMapName("igorv3b"));
		Assert.assertSame(Region.CaveFlames, Region.fromMapName("voleursg1"));
		Assert.assertSame(Region.Sousbois, Region.fromMapName("sousbois3"));
	}
	
	@Test
	public void labels() {
		for (Region r : Region.values()) {
			Assert.assertNotNull(UIText.getGameText(r.getName()));
		}
	}
}
