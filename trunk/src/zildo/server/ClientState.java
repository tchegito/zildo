/**
 *
 */
package zildo.server;

import zildo.fwk.input.KeyboardInstant;
import zildo.fwk.input.KeyboardState;
import zildo.fwk.net.TransferObject;

/**
 * @author tchegito
 *
 */
public class ClientState {

    public TransferObject location;
    public KeyboardInstant keys;
    public KeyboardState keysState;
    public int zildoId;
    
    public ClientState(TransferObject p_location, int p_zildoId) {
        location = p_location;
        keys = null;
        zildoId=p_zildoId;
        keysState=new KeyboardState();
    }
}
