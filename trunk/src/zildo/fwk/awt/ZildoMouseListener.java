/**
 * Legend of Zildo Copyright (C) 2006-2010 Evariste Boussaton Based on original Zelda : link to the past (C) Nintendo 1992 This program is
 * free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any later version. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this
 * program. If not, see <http://www.gnu.org/licenses/>.
 */

package zildo.fwk.awt;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import zeditor.core.TileSelection;
import zeditor.windows.managers.MasterFrameManager;
import zildo.monde.map.Area;
import zildo.server.EngineZildo;

public class ZildoMouseListener implements MouseListener, MouseMotionListener {

    ZildoScrollablePanel panel;

    public ZildoMouseListener(ZildoScrollablePanel p_panel) {
        panel = p_panel;
    }

    // Drop selected tile on map
    public void mouseClicked(MouseEvent mouseevent) {

    }

    public void mouseEntered(MouseEvent mouseevent) {
        // TODO Auto-generated method stub
    }

    public void mouseExited(MouseEvent mouseevent) {
        // TODO Auto-generated method stub

    }

    public void mousePressed(MouseEvent mouseevent) {

        // Get position
        Point p = getPosition(mouseevent);
        // Get brush
        TileSelection sel = MasterFrameManager.getTileSelection();
        if (sel != null) {
            // Apply selected brush to the map
	        Area map = EngineZildo.mapManagement.getCurrentMap();
	        for (int h = 0; h < sel.getHeight(); h++) {
	            for (int w = 0; w < sel.getWidth(); w++) {
	                int item = sel.getItem(h * w + w);
	                if (item != -1) {
	                    map.writemap(p.x / 16 + w, p.y / 16 + h + 4, item);
	                }
	            }
	        }
        }
    }

    public void mouseReleased(MouseEvent mouseevent) {
        // TODO Auto-generated method stub

    }

    public void mouseDragged(MouseEvent mouseevent) {
        mousePressed(mouseevent);
    }

    /**
     * Get pixel-scaled position
     * @param event
     * @return Point
     */
    private Point getPosition(MouseEvent event) {
        Point p = event.getPoint();
        Point camera = panel.getPosition();
        p.x += camera.x;
        p.y += camera.y + 8;

        return p;
    }

    public void mouseMoved(MouseEvent mouseevent) {
        Point p = getPosition(mouseevent);
        StringBuilder message = new StringBuilder();
        message.append("X: ");
        message.append(p.x / 16);
        for (int i = message.length(); i < 16; i++) {
            message.append(" ");
        }
        message.append("Y: ");
        message.append(p.y / 16);
        MasterFrameManager.display(message.toString(), MasterFrameManager.MESSAGE_INFO);
    }
}