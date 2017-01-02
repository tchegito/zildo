package junit.fwk;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JApplet;

import zildo.monde.util.Zone;

public class OccluderApplet extends JApplet {

	List<Zone> availableZones;
	
	@Override
	public void init() {
		super.init();
		// TODO Auto-generated method stub
		// Build a test case from a real one
		availableZones = new ArrayList<>();
		int[][] toStringAvailable = { { 242, 18, 14, 6 }, { 244, 42, 12, 6 },
				{ 242, 66, 14, 5 }, { 241, 90, 15, 6 }, { 113, 93, 128, 3 },
				{ 50, 95, 63, 1 }, { 255, 96, 1, 142 }, { 254, 115, 1, 123 },
				{ 232, 126, 11, 20 }, { 243, 127, 11, 19 },
				{ 222, 128, 10, 18 }, { 174, 129, 7, 17 }, { 91, 140, 45, 6 },
				{ 208, 140, 14, 6 }, { 195, 141, 13, 5 }, { 161, 142, 13, 4 },
				{ 181, 143, 14, 3 }, { 136, 144, 25, 2 }, { 235, 166, 5, 18 },
				{ 210, 167, 12, 17 }, { 168, 168, 2, 16 }, { 133, 169, 3, 15 },
				{ 75, 170, 7, 14 }, { 240, 178, 14, 6 }, { 222, 179, 13, 5 },
				{ 197, 180, 13, 4 }, { 120, 181, 13, 3 }, { 155, 181, 13, 3 },
				{ 63, 182, 12, 2 }, { 101, 182, 19, 2 }, { 170, 182, 27, 2 },
				{ 50, 183, 13, 1 }, { 82, 183, 19, 1 }, { 136, 183, 19, 1 },
				{ 249, 184, 5, 28 }, { 26, 211, 223, 1 }, { 253, 212, 1, 26 },
				{ 150, 236, 103, 2 }, { 47, 237, 103, 1 }, { 228, 253, 28, 3 },
				{ 86, 254, 142, 2 }, { 14, 255, 72, 1 } };


		availableZones = deserializeZones(toStringAvailable);
	}

	@Override
	public void paint(Graphics g) {
		// TODO Auto-generated method stub
        g.setColor(Color.RED);
		for (Zone z : availableZones) {
			if (z.y2 >= 12 && z.x2 > 2 && z.y1 <160 && z.x1 > 180) {
				g.drawRect(z.x1, z.y1, z.x2, z.y2);
				System.out.println(z);
				System.out.println(z.x2);
			}
		}
		//super.paint(g);
	}

	private List<Zone> deserializeZones(int[][] intZones) {
		List<Zone> zones = new ArrayList<>();
		for (int[] z : intZones) {
			zones.add(new Zone(z[0], z[1], z[2], z[3]));
		}
		return zones;
	}
}
