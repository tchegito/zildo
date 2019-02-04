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

package zildo.fwk.script.xml.element;

import java.util.List;

import org.xml.sax.Attributes;

import zildo.fwk.ZUtils;

public class SceneElement extends AnyElement {

    public String id;
    public boolean restoreZildo;	// TRUE=at the end of the scene, we should restore the previous zildo's state
    //public boolean locked = true;	// FALSE=game isn't blocked during scene / TRUE=default behavior (blocked)
    public List<LanguageElement> actions = ZUtils.arrayList();

    @Override
    public void parse(Attributes p_elem) {
		xmlElement = p_elem;
		
        id = readAttribute("id");
        restoreZildo = isTrue("restoreZildo");
    }
    
    @Override
    public void add(String node, AnyElement elem) {
    	actions.add((LanguageElement) elem);
    }
    
    @Override
	public String toString() {
    	return id+"\n"+actions.toString();
    }
    
    @Override
    public int hashCode() {
    	return id.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
    	if (obj == null || !(obj instanceof SceneElement)) {
    		return false;
    	}
    	return hashCode() == ((SceneElement)obj).hashCode();
    }
}