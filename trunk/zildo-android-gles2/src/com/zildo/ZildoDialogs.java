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

package com.zildo;

import zildo.fwk.ui.EditableItemMenu;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputFilter;
import android.widget.EditText;

/**
 * @author Tchegito
 *
 */
public class ZildoDialogs {

	public AlertDialog playerNameDialog;
	
	private EditableItemMenu item;
	
	public ZildoDialogs(AlertDialog.Builder builder, Context context) {

        builder.setTitle("New game");  
        builder.setMessage("Register your name");  
        
        final EditText editText = new EditText(context);
        // 10 characters max for name
        editText.getText().setFilters(new InputFilter[] {new InputFilter.LengthFilter(10) });
        builder.setView(editText);
        
        builder.setCancelable(false)	// No back button
        	.setPositiveButton("Ok", new DialogInterface.OnClickListener() {  
            public void onClick(DialogInterface dialog, int whichButton) {
            	String result = editText.getText().toString();
            	// Append to the item menu string builder
            	for (int i=0;i<result.length();i++) {
            		item.addText(result.charAt(i));
            	}
            	playerNameDialog.dismiss();
              }  
            });         
    	playerNameDialog = builder.create();		
	}
	
	public void askPlayerName(EditableItemMenu p_item) {
		item = p_item;
		playerNameDialog.show();
	}
}
