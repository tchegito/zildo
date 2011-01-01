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

package zeditor.core.selection;

import zeditor.windows.subpanels.SelectionKind;

/**
 * @author Tchegito
 *
 */
public abstract class Selection {

	public abstract SelectionKind getKind();
	
	public abstract Object getElement();
	
	public void unfocus() {}
	
    /**
     * Consider that :<ul>
     * <li>Selection has ALWAYS an element</li>
     * <li>Two selection are equals if and only if its have the same element</li>
     * </ul>
     * {@inheritDoc}
     */
    public boolean equals(Object p_obj) {
    	Selection sel=(Selection) p_obj;
    	if (sel.getKind() != getKind()) {
    		return false;
    	}
    	return sel.getElement().equals(getElement());
    }
}
