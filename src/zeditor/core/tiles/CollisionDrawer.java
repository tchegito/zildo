/**
 * Legend of Zildo
 * Copyright (C) 2006-2011 Evariste Boussaton
 * Based on original Zelda : link to the past (C) Nintendo 1992
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package zeditor.core.tiles;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import zildo.monde.map.TileInfo;

/**
 * @author Tchegito
 * 
 */
public class CollisionDrawer {

	final Graphics g;
	static final Map<TileInfo, Image> imagesByTileInfo = new HashMap<TileInfo, Image>();
	final Color collisionColor = new Color(1, 0, 0, 0.5f);

	public CollisionDrawer(Graphics p_graphics) {
		g = p_graphics;
	}

	/**
	 * Draw a masked image on the graphics, with collision information.
	 * 
	 * @param x
	 * @param y
	 * @param info
	 */
	public void drawCollisionTile(int x, int y, TileInfo info) {
		Image img = imagesByTileInfo.get(info);
		if (img == null) {
			img = buildImage(info);
			imagesByTileInfo.put(info, img);
		}
		g.drawImage(img, x, y, null);
	}

	/**
	 * Build an image based on the collision description from TileInfo
	 * 
	 * @param info
	 * @return Image
	 */
	private Image buildImage(TileInfo info) {
		Image img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		Graphics gfx = img.getGraphics();
		gfx.setColor(collisionColor);
		// Draw the tile according to the collision states
		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {
				if (info.collide(j, i)) {
					gfx.drawLine(j, i, j, i);
				}
			}
		}
		return img;
	}
	
	public static Collection<TileInfo> getCollisions() {
		return imagesByTileInfo.keySet();
	}
}