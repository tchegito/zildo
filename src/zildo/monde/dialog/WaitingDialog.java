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

package zildo.monde.dialog;

import zildo.fwk.file.EasyBuffering;
import zildo.fwk.net.TransferObject;

public class WaitingDialog {

	public String sentence;
	public int action;
	public boolean console;			// TRUE=message should be displayed in the console
	public TransferObject client;	// Only used for sending dialog to the right client. Unused by client side.
	
	public WaitingDialog(String p_sentence, int p_action, boolean p_console, TransferObject p_client) {
		sentence=p_sentence;
		action=p_action;
		console=p_console;
		client=p_client;
	}
	
	public void serialize(EasyBuffering p_buffer) {
		p_buffer.put(sentence);
		p_buffer.put(action);
		p_buffer.put(console);
	}
	
	public static WaitingDialog deserialize(EasyBuffering p_buffer) {
		String s=p_buffer.readString();
		int act=p_buffer.readInt();
		boolean console=p_buffer.readBoolean();
		return new WaitingDialog(s, act, console, null);
	}
}
