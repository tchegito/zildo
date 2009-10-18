/**
 *
 */
package zildo.server;

import zildo.client.ClientEvent;
import zildo.fwk.Identified;
import zildo.fwk.input.KeyboardInstant;
import zildo.fwk.input.KeyboardState;
import zildo.fwk.net.TransferObject;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.persos.PersoZildo;

/**
 * An object used by server to represent a client state. It consists of :
 * -network adress
 * -keys pressed
 * -client's zildo
 * -events currently active (dialog, map change...)
 * 
 * @author tchegito
 *
 */
public class ClientState {

    public TransferObject location;	// Information on physical location (IP, channel ...)
    public KeyboardInstant keys;	// Keyboard input from client
    public KeyboardState keysState;	// Keypressed (analysis from KeyboardInstant)
    public PersoZildo zildo;		// Client's Zildo
    public int inactivityTime;		// Number of frame where server gets nothing from this client
    public DialogState dialogState;	// Client's dialoguing state
    public String playerName;
    public ClientEvent event;
    
    public ClientState(TransferObject p_location, int p_zildoId) {
        location = p_location;
        keys = null;
		zildo=(PersoZildo) Identified.fromId(SpriteEntity.class, p_zildoId);
        keysState=new KeyboardState();
        inactivityTime=0;
        dialogState=new DialogState();
        if (p_location != null) {
        	playerName=p_location.address.getHostName();
        }
        event=ClientEvent.NOEVENT;
    }
}
