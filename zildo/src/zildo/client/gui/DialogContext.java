/**
 * Legend of Zildo
 * Copyright (C) 2006-2012 Evariste Boussaton
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

package zildo.client.gui;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tchegito
 *
 */
public class DialogContext {
	public boolean visibleMessageDisplay; // FALSE=Visible text isn't display
	// yet at screen
	public boolean entireMessageDisplay; // FALSE=Entire sentence aren't
	// display yet at screen.
	public boolean fullSentenceDisplayed;

	public String sentence;
	public int pos;	// position in sentence
	public int numToScroll;
	
	public List<Integer> startLineIndexes = new ArrayList<Integer>();
	
	public void setSentence(String sent) {
		sentence = sent;
		fullSentenceDisplayed = false;
		pos=0;

		startLineIndexes.clear();
		startLineIndexes.add(0);
	}
	
	public void add(int index) {
		startLineIndexes.add(index);
	}
	
	public void setLine(int numLine) {
		if (startLineIndexes.size() > numLine) {
			pos = startLineIndexes.get(numLine);
		} else {
			System.out.println("strange ! ask "+numLine+" but max is "+startLineIndexes.size());
		}
	}
}
