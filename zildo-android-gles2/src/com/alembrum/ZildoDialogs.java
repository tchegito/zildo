/**
 * The Land of Alembrum
 * Copyright (C) 2006-2016 Evariste Boussaton
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

package com.alembrum;

import zildo.fwk.ui.EditableItemMenu;
import zildo.fwk.ui.UIText;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

/**
 * @author Tchegito
 *
 */
public class ZildoDialogs {

	public AlertDialog playerNameDialog;
	
	private EditableItemMenu item;
	final EditText editText;
	
	// Those characters would cause a crash because they are used in properties file as well
	String blockCharacterSet = "@#$";
	
	public ZildoDialogs(AlertDialog.Builder builder, Context context) {

        builder.setTitle(UIText.getMenuText("m11.title"));  
        builder.setMessage(UIText.getMenuText("m11.mess"));  
        
        editText = new EditText(context);
        // 10 characters max for name + forbidden characters check
        editText.getText().setFilters(new InputFilter[] {new InputFilter.LengthFilter(10), new FilterForbiddenCharacter() });
        builder.setView(editText);
        
        builder.setCancelable(false)	// No back button
        	.setPositiveButton("Ok", new DialogInterface.OnClickListener() {  
            public void onClick(DialogInterface dialog, int whichButton) {
            	
              }  
            });   
        
        
    	playerNameDialog = builder.create();	

	}
	
	class FilterForbiddenCharacter implements InputFilter {
		@Override
		public CharSequence filter(CharSequence source, int start, int end,
				Spanned dest, int dstart, int dend) {
			if (source != null) {
				// block direct type, and copy/paste (source will be more than 1-sized)
				for (int i=0;i<source.length();i++) {
					if (blockCharacterSet.contains(("" + source.charAt(i)))) {
				         return "";
				    }
				}
			}
		    return null;
		}
	}
	class CustomListener implements OnClickListener {
        private final Dialog dialog;
        public CustomListener(Dialog dialog) {
            this.dialog = dialog;
        }
        @Override
        public void onClick(View v) {
        	String result = editText.getText().toString();
        	result = result.replaceAll(System.getProperty("line.separator"), "");
        	if (result != null && result.length() >= 1) {
            	// Append to the item menu string builder
            	for (int i=0;i<result.length();i++) {
            		item.addText(result.charAt(i));
            	}
            	dialog.dismiss();
        	}
        }
    }
	public void askPlayerName(EditableItemMenu p_item) {
		editText.setText("");
		item = p_item;
		playerNameDialog.show();
    	Button b = playerNameDialog.getButton(DialogInterface.BUTTON_POSITIVE);
  	  	b.setOnClickListener(new CustomListener(playerNameDialog));
	}
}
