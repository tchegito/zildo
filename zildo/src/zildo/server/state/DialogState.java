/**
 * The Land of Alembrum
 * Copyright (C) 2006-2013 Evariste Boussaton
 * 
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

import zildo.monde.dialog.ActionDialog;

public class DialogState {

    boolean dialoguing;
    public ActionDialog actionDialog;
    public boolean continuing;	// TRUE=character has another sentence to come
    
	public DialogState() {
		dialoguing=false;
	}
	
	public boolean isDialoguing() {
		return dialoguing;
	}
	
	public void setDialoguing(boolean value) {
		dialoguing = value;
	}
}
