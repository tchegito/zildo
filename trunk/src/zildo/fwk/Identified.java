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

package zildo.fwk;

import java.util.HashMap;
import java.util.Map;

/**
 * Mini-database mechanism.
 * 
 * Provide an ID assignment to an object deriving from this abstract class.
 * It could be retrieved then by the simple key &lt;Class, ID&gt;.<p/>
 * 
 * Very useful to adress a specific object between client and server.<p/>
 * 
 * View an usage example in {@link zildo.monde.sprites.SpriteModel}
 * @author tchegito
 *
 */
public abstract class Identified {

	protected int id=-1;
	
	protected static Map<Class<? extends Identified>, Integer> idsCounter=
		new HashMap<Class<? extends Identified>, Integer>();

	protected static class Key {
		int id;
		Class<? extends Identified> clazz;
		
		public Key(int p_id, Class<? extends Identified> p_clazz) {
			id=p_id;
			clazz=p_clazz;
		}
		
		@Override
		public int hashCode() {
			return id ^2+ 7*clazz.hashCode();
		}
		
		@Override
		public boolean equals(Object o) {
			if (!o.getClass().equals(this.getClass())) {
				return false;
			}
			return this.hashCode() == o.hashCode();
		}
	}
	
	// Database
	private static Map<Key, Identified> objects;
	
	public static void resetCounter(Class<? extends Identified> p_clazz) {
		idsCounter.put(p_clazz, 0);
		if (objects == null) {
			objects=new HashMap<Key, Identified>();
		}
	}
	
	protected int getCounter(Class<? extends Identified> p_clazz) {
		Class<? extends Identified> refClass=this.getClass();
		if( p_clazz != null) {
			refClass=p_clazz;
		}
		Integer i=idsCounter.get(refClass);
		if (i== null) {
			i=0;
		}
		idsCounter.put(refClass, i.intValue()+1);
		return i;
	}
	
	protected void initializeId() {
		initializeId(null);
	}

	protected void initializeId(Class<? extends Identified> p_clazz) {
		Class<? extends Identified> refClass=this.getClass();
		if (p_clazz != null) {
			refClass=p_clazz;
		}
		id=getCounter(refClass);
		objects.put(new Key(id, refClass), this);
	}

	public int getId() {
		return id;
	}
	
	@SuppressWarnings("unchecked")
	public static <E extends Identified> E fromId(Class<E> p_clazz, int p_id) {
		Key p_key=new Key(p_id, p_clazz);
		return (E) objects.get(p_key);
	}
}
