package zildo.server;

import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.PersoZildo;
import zildo.server.state.ClientState;

/**
 * @author tchegito
 */
public class MultiplayerManagement {

	private boolean needToBroadcast=false;
	
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
}