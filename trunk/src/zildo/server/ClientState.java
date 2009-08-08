/**
 *
 */
package zildo.server;

import zildo.fwk.Identified;
import zildo.fwk.input.KeyboardInstant;
import zildo.fwk.input.KeyboardState;
import zildo.fwk.net.TransferObject;
import zildo.monde.decors.SpriteEntity;
import zildo.monde.persos.PersoZildo;

/**
 * An object used by server to represent a client state. It consists of :
 * -network adress
 * -keys pressed
 * -client's zildo
 * 
 * @author tchegito
 *
 */
public class ClientState {

    public TransferObject location;
    public KeyboardInstant keys;
    public KeyboardState keysState;
    public PersoZildo zildo;
    public int inactivityTime;
    public boolean dialoguing;
    
    public ClientState(TransferObject p_location, int p_zildoId) {
        location = p_location;
        keys = null;
		zildo=(PersoZildo) Identified.fromId(SpriteEntity.class, p_zildoId);
        keysState=new KeyboardState();
        inactivityTime=0;
        dialoguing=false;
    }
}
