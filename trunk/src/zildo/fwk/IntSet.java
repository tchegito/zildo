package zildo.fwk;

import java.util.TreeSet;

public class IntSet extends TreeSet<Integer> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public IntSet(int... startSet) {
		for (int a : startSet) {
			this.add(a);
		}
	}

}
