/**
 *
 */
package zildo.server;

import zildo.fwk.KeyboardInstant;
import zildo.fwk.net.TransferObject;

/**
 * @author tchegito
 *
 */
public class ClientState {

    public TransferObject location;
    public KeyboardInstant keys;
    public int zildoId;
    
    public ClientState(TransferObject p_location, int p_zildoId) {
        location = p_location;
        keys = null;
        zildoId=p_zildoId;
    }
}
