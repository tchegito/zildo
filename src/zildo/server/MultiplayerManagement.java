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

package zildo.server;

import zildo.monde.sprites.elements.ElementImpact;
import zildo.monde.sprites.elements.ElementQuadDamage;
import zildo.monde.sprites.elements.ElementImpact.ImpactKind;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.PersoZildo;
import zildo.server.state.ClientState;
import zildo.server.state.PlayerState;

/**
 * @author tchegito
 */
public class MultiplayerManagement {

	public static final int QUAD_TIME_DURATION=1500;
	public static final int QUAD_TIME_RESPAWNING=4000;
	
	private boolean needToBroadcast=false;
	
	int quadTimeRemaining=0;
	
	public void render() {
		// Spawn again a quad damage when time is come
		if (quadTimeRemaining > 0) {
			quadTimeRemaining--;
			if (quadTimeRemaining == 0) {
				spawnQuad();
			}
		}
	}
	
	public void pickUpQuad() {
		quadTimeRemaining=QUAD_TIME_RESPAWNING;
	}
	
    public void spawnQuad() {
        int x = 616; // 840;
        int y = 170;
        EngineZildo.spriteManagement.spawnSprite(new ElementImpact(x, y, ImpactKind.SMOKE, null));
        EngineZildo.spriteManagement.spawnSprite(new ElementQuadDamage(x, y));
    }
	
    private void displayDeathMessage(ClientState p_clientKilled, ClientState p_clientKiller) {
        String shooterName = null;
        if (p_clientKiller != null) {
            shooterName = p_clientKiller.playerName;
        } else {
            shooterName = "Neutral units";
        }
        String shootedName = p_clientKilled.playerName;
        EngineZildo.dialogManagement.writeConsole(shooterName + " pawned " + shootedName + " !");
    }
    
    /**
     * People killed another one.<p/>
     * <ul>
     * <li>display message</li>
     * <li>update scores</li>
     * </ul>
     * @param p_zildo
     * @param p_shooter
     */
    public void kill(PersoZildo p_zildo, Perso p_shooter) {
    	ClientState clKilled=Server.getClientFromZildo(p_zildo);
    	ClientState clShooter=null;
        if (p_shooter != null && p_shooter.isZildo()) {
        	clShooter = Server.getClientFromZildo((PersoZildo) p_shooter);
        }
    	displayDeathMessage(clKilled, clShooter);
    	// Update scores
    	clKilled.nDied++;
    	if (clShooter != null) {
    		clShooter.nKill++;
    	}
    	needToBroadcast=true;
    }
    
    public void setNeedToBroadcast(boolean p_active) {
    	needToBroadcast=p_active;
    }
    
    /**
     * @return TRUE if something happened in the game that goes into the score panel.
     */
    public boolean isNeedToBroadcast() {
    	return needToBroadcast;
    }
    
    /**
     * Calculate the player score.
     * @param p_state
     * @return int
     */
    public static int getScore(PlayerState p_state) {
    	return p_state.nKill; // - p_state.nDied;    	
    }
    
    /**
     * Initialize Zildo inventory and so on, with start values.
     * @param p_zildo
     */
    public static void setUpZildo(PersoZildo p_zildo) {
		//p_zildo.addInventory(new Item(ItemKind.BOW));
		//p_zildo.addInventory(new Item(ItemKind.BOOMERANG));
		//p_zildo.addInventory(new Item(ItemKind.BOMB));
		p_zildo.setPv(3);
		
		//p_zildo.setCountArrow(20);
		//p_zildo.setCountBomb(20);

    }
}