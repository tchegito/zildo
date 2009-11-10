package zildo.server.state;

import zildo.fwk.file.EasyBuffering;

/**
 * Object manipulated by client. It's the simple view of the complete ClientState, used by server only.
 * <p/>
 * Each time a player connects/leaves, or kills another one, server sends to all client the updated object.
 * @author eboussaton
 */
public class PlayerState {

    // Deathmatch
	public int zildoId;	// Player's ID in the game
    public String playerName;
    public int nDied = 0;
    public int nKill = 0;

    public PlayerState(String p_playerName, int p_zildoId) {
    	zildoId = p_zildoId;
    	playerName = p_playerName;
    }
    
    public EasyBuffering serialize(EasyBuffering p_buf) {
        p_buf.put(playerName);
        p_buf.put(zildoId);
        p_buf.put(nDied);
        p_buf.put(nKill);
        return p_buf;
    }

    public static PlayerState deserialize(EasyBuffering p_buffer) {
        PlayerState s = new PlayerState(p_buffer.readString(), p_buffer.readInt());
        s.nDied = p_buffer.readInt();
        s.nKill = p_buffer.readInt();
        return s;
    }
}