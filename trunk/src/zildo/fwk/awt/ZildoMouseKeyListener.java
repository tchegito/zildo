/**
 * Legend of Zildo
 * Copyright (C) 2006-2010 Evariste Boussaton
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

package zildo.fwk.awt;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import zeditor.windows.managers.MasterFrameManager;

public class ZildoMouseKeyListener implements MouseListener, MouseMotionListener, KeyListener {

	ZildoScrollablePanel panel;

	public ZildoMouseKeyListener(ZildoScrollablePanel p_panel) {
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
		// And apply the brush on it (=selected tiles)
		panel.getZildoCanvas().applyBrush(p);
	}

	public void mouseReleased(MouseEvent mouseevent) {
		// TODO Auto-generated method stub

	}

	public void mouseDragged(MouseEvent mouseevent) {
		mousePressed(mouseevent);

		// Store the cursor location
		Point p = getInsidePosition(mouseevent);
		panel.getZildoCanvas().cursorLocation = p;
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
		p.y += camera.y; // + 8 + 4;

		return p;
	}

	/**
	 * Get pixel-scaled position in window coordinates
	 * @param event
	 * @return
	 */
	private Point getInsidePosition(MouseEvent event) {
		Point p = event.getPoint();
		Point camera = panel.getPosition();
		p.x = (p.x + camera.x % 16) / 16 * 16 - camera.x % 16;
		p.y = (p.y + camera.y % 16) / 16 * 16 - camera.y % 16;

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

		// Store the cursor location
		p = getInsidePosition(mouseevent);
		panel.getZildoCanvas().cursorLocation = p;
	}

	public void keyPressed(KeyEvent p_keyevent) {
		int code = p_keyevent.getKeyCode();
		switch (code) {
			case 37: // left
				panel.zildoCanvas.left = true;
				break;
			case 39: // right
				panel.zildoCanvas.right = true;
				break;
			case 38: // up
				panel.zildoCanvas.up = true;
				break;
			case 40: // down
				panel.zildoCanvas.down = true;
				break;
		}
	}

	public void keyReleased(KeyEvent p_keyevent) {
		int code = p_keyevent.getKeyCode();
		switch (code) {
			case 37: // left
				panel.zildoCanvas.left = false;
				break;
			case 39: // right
				panel.zildoCanvas.right = false;
				break;
			case 38: // up
				panel.zildoCanvas.up = false;
				break;
			case 40: // down
				panel.zildoCanvas.down = false;
				break;
		}
	}

	public void keyTyped(KeyEvent p_keyevent) {
		// TODO Auto-generated method stub

	}
}