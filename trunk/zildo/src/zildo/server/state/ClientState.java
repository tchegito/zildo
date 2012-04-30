/**
 * Legend of Zildo
 * Copyright (C) 2006-2012 Evariste Boussaton
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


package zildo.server.state;

import zildo.client.ClientEvent;
import zildo.client.ClientEventNature;
import zildo.fwk.db.Identified;
import zildo.fwk.input.KeyboardInstant;
import zildo.fwk.input.KeyboardState;
import zildo.fwk.net.TransferObject;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.persos.PersoZildo;

/**
 * An object used by server to represent a client state. It consists of :<br/>
 * <ul>
 * <li>network adress</li>
 * <li>keys pressed</li>
 * <li>client's zildo</li>
 * <li>events currently active (dialog, map change...)</li>
 * </ul>
 * @author tchegito
 *
 */
public class ClientState extends PlayerState {

    public TransferObject location;	// Information on physical location (IP, channel ...)
    public KeyboardInstant keys;	// Keyboard input from client
    public KeyboardState keysState;	// Keypressed (analysis from KeyboardInstant)
    public PersoZildo zildo;		// Client's Zildo
    public int inactivityTime;		// Number of frame where server gets nothing from this client
    public DialogState dialogState;	// Client's dialoguing state
    public ClientEvent event;
    public boolean gameOver;
    
    public ClientState(TransferObject p_location, int p_zildoId) {
    	super(null, p_zildoId);
        location = p_location;
        keys = null;
		zildo=(PersoZildo) Identified.fromId(SpriteEntity.class, p_zildoId);
        keysState=new KeyboardState();
        inactivityTime=0;
        dialogState=new DialogState();
        if (p_location != null) {
        	playerName=p_location.address.getHostName();
        }
        event=new ClientEvent(ClientEventNature.NOEVENT);
        gameOver=false;
    }
}
