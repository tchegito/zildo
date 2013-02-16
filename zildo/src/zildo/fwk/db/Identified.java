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

package zildo.fwk.db;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import zildo.fwk.collection.IdGenerator;

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
	public static final int DEFAULT_MAX_ID = 512;
	
	protected static Map<Class<? extends Identified>, IdGenerator> idsCounter=
		new HashMap<Class<? extends Identified>, IdGenerator>();

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
	private static Map<Key, Identified> objects = new HashMap<Key, Identified>();
	
	public static void resetCounter(Class<? extends Identified> p_clazz) {
		idsCounter.put(p_clazz, new IdGenerator(retrieveMaxId(p_clazz)));
	}
	
	protected int getCounter(Class<? extends Identified> p_clazz) {
		Class<? extends Identified> refClass=this.getClass();
		if( p_clazz != null) {
			refClass=p_clazz;
		}
		IdGenerator idGen=idsCounter.get(refClass);
		if (idGen== null) {
			idGen = new IdGenerator(retrieveMaxId(refClass));
			idsCounter.put(refClass, idGen);
		}
		return idGen.pop();
	}
	
	protected void initializeId() {
		initializeId(null);
	}

	/**
	 * Allocate an ID and assign it to the current object.
	 * @param p_clazz
	 */
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
	
	/**
	 * Remove an object for the register.
	 * @param <E>
	 * @param clazz
	 * @param p_id
	 */
	public static <E extends Identified> void remove(Class<E> clazz, int p_id) {
		if (p_id != -1) {
			Key p_key=new Key(p_id, clazz);
			objects.remove(p_key);
			IdGenerator idGen=idsCounter.get(clazz);
			if (idGen != null) {	// It should never be null !
				idGen.remove(p_id);
			}
		}
	}
	
	/**
	 * Retrieve the max ID defined by annotation in given class. There's a default value in case
	 * when no annotation is found.
	 * @param p_clazz
	 * @return int
	 */
	private static int retrieveMaxId(Class<? extends Identified> p_clazz) {
		for (Annotation a : p_clazz.getAnnotations()) {
			if (a.annotationType() == MaxId.class) {
				return ((MaxId) a).n();
			}
		}
		return DEFAULT_MAX_ID;
	}

}
