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

package junit.area;

import org.junit.Assert;
import org.junit.Test;

import zeditor.tools.builder.AllMapProcessor;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.desc.PersoDescription;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.Perso.PersoInfo;
import zildo.server.EngineZildo;

/**
 * @author Tchegito
 *
 */
public class CheckConsistentMap {

	class EnemyException {
		final PersoDescription desc;
		final String name;
		final String mapName;
		
		public EnemyException(PersoDescription p_desc, String p_name, String p_mapName) {
			desc = p_desc;
			name = p_name;
			mapName = p_mapName;
		}
	}
	
	// Exceptions to the rules: character usually considered as enemies, but Zildo need to talk with.
	EnemyException[] exceptions = {
			new EnemyException(PersoDescription.GARDE_CANARD, "gard2", "prison"),
			new EnemyException(PersoDescription.GARDE_CANARD, "jaune", "prison"),
			new EnemyException(PersoDescription.FALCOR, "falcor", "voleursm4b")
	};
	
	@Test
	public void testEnemies() {
		new AllMapProcessor() {
			
			@Override
			public boolean run() {
				//for (Perso p : EngineZildo.persoManagement.tab_perso) {
				for (SpriteEntity entity : EngineZildo.spriteManagement.getSpriteEntities(null)) {
					if (!entity.getEntityType().isPerso()) {
						continue;
					}
					Perso p = (Perso) entity;
					boolean realEnemy = p.getInfo() == PersoInfo.ENEMY;
					boolean shouldBeEnemy = false;
					
					switch (p.getDesc()) {
					case ABEILLE:
						continue;	// Both are tolerated
					case BAS_GARDEVERT:
					case HAUT_GARDEVERT:
					case CHAUVESOURIS:
					case CORBEAU:
					case CRABE:
					case CREATURE:
					case ECTOPLASME:
					case GREEN_BLOB:
					case FIRETHING:
					case GARDE_CANARD:
					case RABBIT:
					case RAT:
					case SPECTRE:
					case SQUELETTE:
					case VAUTOUR:
					case VOLANT_BLEU:
					case FOX:
					case STONE_SPIDER:
					case FLYINGSERPENT:
					case BRAMBLE:
					case BIG_RAT:
					case DRAGON:
					case FIRE_ELEMENTAL:
					case BITEY:
					case CACTUS:
					case SCORPION:
					case MOLE:
					case BUTCHER:
					case HOODED:
					case DARKGUY:
						shouldBeEnemy = true;
					default:
						break;
					}
					// Check if this is a known exception
					if (realEnemy != shouldBeEnemy) {
						for (EnemyException e : exceptions) {
							if (e.desc == p.getDesc() && e.name.equals(p.getName()) && (e.mapName+".map").equals(mapName)) {
								shouldBeEnemy = !shouldBeEnemy;
								break;
							}
						}
					}
					Assert.assertTrue("Aaaaie ! "+p.getName()+" ("+p.getDesc()+") at ("+(int) p.getX()+","+(int) p.getY()+") should be enemy !", realEnemy == shouldBeEnemy);
				}
				return false;
			}
		}.modifyAllMaps();
	}
}
