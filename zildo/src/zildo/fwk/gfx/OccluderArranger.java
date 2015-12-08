package zildo.fwk.gfx;

import java.util.ArrayList;
import java.util.List;

import zildo.monde.util.Point;
import zildo.monde.util.Zone;

/**
 * Class which reallocates areas when we can't find a sufficient zone for our texture.
 * @author tchegito
 *
 */
public class OccluderArranger {

	int width,height;
	int[][] surface;
	Occluder occ;
	
	public OccluderArranger(Occluder occluder) {
		this.occ = occluder;
		width = occ.width;
		height = occ.height;
		// Fill a 2-dimension array with zone IDs
		surface = new int[width][height];

		int i;
		for (i=0;i<width*height;i++) {
			surface[i % width][i/width]=0;
		}
		i=0;
		Zone max = new Zone();
		for (Zone z : occ.available) {
			i++;
			fillZone(z, i);
			int size = z.x2 * z.y2;
			if (size > (max.x2 * max.y2)) {
				max = z;
			}
		}
	
		//stats();
	}
	
	private void fillZone(Zone z, int id) {
		for (int x=0;x<z.x2;x++) {
			for (int y=0;y<z.y2;y++) {
				surface[z.x1 + x][z.y1 + y] = id;
			}
		}
	}

	public List<Zone> recut() {
		List<Zone> zones = new ArrayList<>();
		// Find a filled pixel
		while (true) {
			FindResult left = find(0, 0, true);
			if (left != null) {
				// Find the rightest point
				FindResult right = find(left.p.x, left.p.y, false, true);
				// Find the biggest zone with given width
				int zoneWidth = right.p.x - left.p.x;
				int zoneHeight = findHeight(left.p.x, left.p.y+1, zoneWidth) + 1;
				System.out.println("On a trouvé une zone en "+left.p+" de taille "+zoneWidth+"x"+zoneHeight);
				// Empty zone and continue
				Zone z = new Zone(left.p.x, left.p.y, zoneWidth, zoneHeight);
				zones.add(z);
				fillZone(z, 0);
			} else {
				break;
			}
		}
		return zones;
	}

	class FindResult {
		Point p;
		int idZone;
		
		public FindResult(Point p, int idZone) {
			this.p = p;
			this.idZone = idZone;
		}
	}
	
	private FindResult find(int posX, int posY, boolean filled) {
		return find(posX, posY, filled, false);
	}
	
	/** Returns the next point fill/empty from the given position.
	 * 
	 * @param posX
	 * @param posY
	 * @param filled
	 * @param oneLine TRUE=search only on the current line
	 * @return
	 */
	private FindResult find(int posX, int posY, boolean filled, boolean oneLine) {
		
		int startX = posX;
		int startY = posY;
		int idZone = 0;
		
		for (int j=startY;j<height;j++) {
			for (int i=startX;i<width;i++) {
				idZone = surface[i][j];
				if ((filled && idZone != 0) || (!filled && idZone == 0)) {
					return new FindResult(new Point(i, j), idZone);
				}
			}
			if (oneLine) {
				return new FindResult(new Point(width, j), idZone);
			}
		}
		return null;
	}
	
	private int findHeight(int posX, int posY, int width) {
		// Iterate over surface to find the highest zone with given width
		int h=0;
		int y = posY;
		while ((y+h) < height) {
			FindResult r = find(posX, y + h, false, true);
			if (r.p.x < posX+width) {
				break;
			}
			h++;
		}
		return h;
	}
	
	public void stats() {
		Zone max = new Zone();
		for (Zone z : occ.available) {
			int size = z.x2 * z.y2;
			if (size > (max.x2 * max.y2)) {
				max = z;
			}
		}
		int count = 0;
		for (int i=0;i<width*height;i++) {
			if (surface[i % width][i/width] != 0) count++;
		}
		
		System.out.println("ca y est "+count+" pixels libres = " +((double)count/(width*height)) *100d+"%");
		System.out.println(occ.available.size()+" zones, dont la plus grande allouable="+max.x2+"x"+max.y2);	
	}
}
