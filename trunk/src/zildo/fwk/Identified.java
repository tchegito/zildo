package zildo.fwk;

import java.util.HashMap;
import java.util.Map;

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
		
		public int hashCode() {
			return id ^2+ 7*clazz.hashCode();
		}
		
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
