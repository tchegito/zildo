package zeditor.tools.tiles;

import java.awt.Point;


public class PyxelBanque extends Banque {

	public PyxelBanque() {
		coords = new Point[16*16];
		for (int i=0;i<256;i++) {
			coords[i] = new Point(16 * (i % 16), 16 * (i / 16));
		}
	}
}
