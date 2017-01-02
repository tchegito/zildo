package zildo.fwk.collection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiMap<K, V> {

	Map<K, List<V>> map = new HashMap<K, List<V>>();
	
    public List<V> put(K p_key, List<V> p_value) {
    	List<V> elems = map.get(p_key);
        if (elems == null) {
            elems = new ArrayList<V>();
        }
        elems.addAll(p_value);
        return map.put(p_key, elems);
    }
    
    public List<V> put(K p_key, V p_value) {
    	return put(p_key, Collections.singletonList(p_value));
    }
    
    public List<V> get(K p_key) {
    	return map.get(p_key);
    }
    
}
