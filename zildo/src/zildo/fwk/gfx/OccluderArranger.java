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
	
	// Fills a 2-dimension array with all free areas
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
	
	}
	/** Fill an area in the 2-dimension array. Note that 0 means occupied, and 1..n means the ID of free area **/
	private void fillZone(Zone z, int id) {
		for (int x=0;x<z.x2;x++) {
			for (int y=0;y<z.y2;y++) {
				surface[z.x1 + x][z.y1 + y] = id;
			}
		}
	}
	
	// Clean that part :
	// IF wishedWidth/height are provided, this is what we should do:
	// Special process, where we request for specific width/height area.
	// Once we found it, go to the regular process, where wishes are nonsense
	
	// Try to do a better cut to obtain larger area
	public List<Zone> recut(Zone... zs) {
		List<Zone> zones = new ArrayList<Zone>();
		if (zs != null) {
			for (Zone z : zs) {
				zones.add(z);
				// Declare that zone occupied
				fillZone(z, 0);
			}
		}
		// Find a filled pixel
		while (true) {
			FindResult left = find(0, 0, true);
			if (left != null) {
				// Find the rightest point
				FindResult right = find(left.p.x, left.p.y, false, true);
				// Find the biggest zone with given width
				int zoneWidth = right.p.x - left.p.x;
				int zoneHeight = findHeight(left.p.x, left.p.y+1, zoneWidth) + 1;
				// Empty zone and continue
				Zone z = new Zone(left.p.x, left.p.y, zoneWidth, zoneHeight);
				zones.add(z);
				fillZone(z, 0);

			} else {
				break;
			}
		}
		//stats(zones);

		return zones;
	}
	
	/** Find an available area with desired size, based on the 2-dimension array, not the array of Zone **/
	public Zone cutSpecificArea(int wishedWidth, int wishedHeight) {
		FindResult left = new FindResult(new Point(0, 0), 0);
		// Find a filled pixel
		while (true) {
			boolean wishFulfill = true;
			left = find(left.p.x, left.p.y, true);
			if (left != null) {
				// Find the rightest point
				int maxX = left.p.x + wishedWidth - 1;
				FindResult right = find(left.p.x, left.p.y, false, true, maxX);
				// Find the biggest zone with given width
				int zoneWidth = right.p.x - left.p.x;
				if (zoneWidth == wishedWidth) {
					int zoneHeight = findHeight(left.p.x, left.p.y+1, zoneWidth) + 1;
					if (zoneHeight < wishedHeight) {
						wishFulfill = false;
					} else {
						zoneHeight = wishedHeight;
						wishedWidth = -1;
						wishedHeight = -1;
						// Empty zone and continue
						return new Zone(left.p.x, left.p.y, zoneWidth, zoneHeight);
					}
				} else {
					wishFulfill = false;
				}
				if (!wishFulfill) {
					left.p.x++; //=zoneWidth;
					if (left.p.x == 256) {	// Carriage return
						left.p.x = 0;
						left.p.y++;
					}
				}

			} else {
				break;
			}
		}
		return null;
	}

	class FindResult {
		Point p;
		int idZone;
		
		public FindResult(Point p, int idZone) {
			this.p = p;
			this.idZone = idZone;
		}
		@Override
		public String toString() {
			return p+", idZone="+idZone;
		}
	}
	
	private FindResult find(int posX, int posY, boolean filled) {
		return find(posX, posY, filled, false);
	}
	
	private FindResult find(int posX, int posY, boolean filled, boolean oneLine) {
		return find(posX, posY, filled, oneLine, 999);
	}
	/** Returns the next point filled/empty from the given position.
	 * 
	 * @param posX
	 * @param posY
	 * @param filled
	 * @param oneLine TRUE=search only on the current line
	 * @return
	 */
	private FindResult find(int posX, int posY, boolean filled, boolean oneLine, int maxX) {
		
		int startX = posX;
		int startY = posY;
		int idZone = 0;
		
		for (int j=startY;j<height;j++) {
			for (int i=startX;i<width;i++) {
				idZone = surface[i][j];
				if ((filled && idZone != 0) || (!filled && idZone == 0) || i>maxX) {
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
	
	public void stats(List<Zone> zones) {
		Zone max = new Zone();
		for (Zone z : zones) {
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
