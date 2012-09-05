package zildo.fwk.collection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("serial")
public class MultiMap<K, V> extends HashMap<K, List<V>> {

    @Override
    public List<V> put(K p_key, List<V> p_value) {
	List<V> elems = get(p_key);
        if (elems == null) {
            elems = new ArrayList<V>();
        }
        elems.addAll(p_value);
        return super.put(p_key, elems);
    }
    
    public List<V> put(K p_key, V p_value) {
	return put(p_key, Collections.singletonList(p_value));
    }
    
}
