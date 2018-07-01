/**
 * The Land of Alembrum
 * Copyright (C) 2006-2016 Evariste Boussaton
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

package zildo.fwk.script.xml.element;

import org.xml.sax.Attributes;

/**
 * @author Tchegito
 *
 */
public abstract class LanguageElement extends AnyElement {

	public boolean unblock; // Default: FALSE meaning action is waiting to be over before next one

	@Override
	public void parse(Attributes p_elem) {
		xmlElement = p_elem;
		unblock = isTrue("unblock");
	}
}
