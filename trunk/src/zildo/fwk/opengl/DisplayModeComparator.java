package zildo.fwk.opengl;

import java.util.Comparator;

import org.lwjgl.opengl.DisplayMode;

public class DisplayModeComparator implements Comparator<DisplayMode> {

	/**
	 * Returns -1 if o1 is better than o2
	 */
	public int compare(DisplayMode o1, DisplayMode o2) {
		if (o1.getBitsPerPixel() >= o2.getBitsPerPixel()) {
			return -1;
		} else {
			return 1;
		}
	}
}
