package zildo.fwk.script.model;

import java.util.ArrayList;
import java.util.List;

public class StringList<T> {

	List<T> elements;
	
	private StringList() {
		elements = new ArrayList<T>();
	}
	
	// Only use this way to create a StringList (just to avoid one redundant type declaration !)
	static public <T> StringList<T> newOne() {
		return new StringList<T>();
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(16);
		for (T elem : elements) {
			sb.append(elem.toString());
			sb.append(",");
		}
		if (sb.length() > 1) {
			sb.setLength(sb.length()-1);
		}
		return sb.toString();
	}
	
	// Only authorized method
	public int size() { return elements.size(); }
	
	public T get(int index) { return elements.get(index); }
	
	public void add(T elem) { elements.add(elem); }
}
