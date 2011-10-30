/**
 * Legend of Zildo
 * Copyright (C) 2006-2011 Evariste Boussaton
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
import zildo.fwk.file.EasySerializable;
import zildo.fwk.net.TransferObject;

public class WaitingDialog implements EasySerializable {

	public enum CommandDialog {
		ACTION, // End dialog
		UP, DOWN, // Line selection
		BUYING, STOP, // Brutal end
		CONTINUE; // continue to next sentence
	}

	public String sentence;
	public CommandDialog action;
	public boolean console; // TRUE=message should be displayed in the console
	public TransferObject client; // Only used for sending dialog to the right
									// client. Unused by client side.

	public WaitingDialog(String p_sentence, CommandDialog p_action,
			boolean p_console, TransferObject p_client) {
		sentence = p_sentence;
		action = p_action;
		console = p_console;
		client = p_client;
	}

	@Override
	public void serialize(EasyBuffering p_buffer) {
		p_buffer.put(sentence);
		p_buffer.put((byte) (action == null ? -1 : action.ordinal()));
		p_buffer.put(console);
	}

	public static WaitingDialog deserialize(EasyBuffering p_buffer) {
		String s = p_buffer.readString();
		int act = p_buffer.readByte();
		boolean console = p_buffer.readBoolean();
		CommandDialog actDialog = (act == -1 ? null
				: CommandDialog.values()[act]);
		return new WaitingDialog(s, actDialog, console, null);
	}
}
