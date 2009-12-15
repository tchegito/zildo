package zildo.server.state;


/**
 *  We need a guide that authorizes actions following the game phase
 *
 * @author tchegito
 */
public enum GamePhase {
	INGAME(true, true, true), 
	DIALOG(false, true, false), 
	MAPCHANGE(false, false, false), 
	SCRIPT(false, true, false);
	
	private GamePhase(boolean p_moves, boolean p_action, boolean p_others) {
		moves=p_moves;
		action=p_action;
		others=p_others;
	}
	
	public boolean moves;	// Zildo moves
	public boolean action;	// Action button
	public boolean others;	// Inventory, weapon
}
