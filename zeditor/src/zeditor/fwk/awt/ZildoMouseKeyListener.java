/**
 * The Land of Alembrum
 * Copyright (C) 2006-2016 Evariste Boussaton
 * 
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

package zeditor.fwk.awt;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import zeditor.fwk.awt.ZildoCanvas.ZEditMode;
import zeditor.windows.managers.MasterFrameManager;
import zildo.fwk.gfx.engine.TileEngine;
import zildo.monde.map.Case;
import zildo.monde.map.Tile;
import zildo.monde.sprites.Reverse;
import zildo.monde.sprites.Rotation;

public class ZildoMouseKeyListener implements MouseListener,
		MouseMotionListener, KeyListener, MouseWheelListener {

	ZildoScrollablePanel panel;
	ZildoCanvas canvas;

	// When user press SHIFT, we set this to TRUE, and user gain focus on what
	// is under the mouse cursor
	private boolean focusOnCursor;

	public ZildoMouseKeyListener(ZildoScrollablePanel p_panel) {
		panel = p_panel;
		canvas = panel.getZildoCanvas();
	}

	// Drop selected tile on map
	@Override
	public void mouseClicked(MouseEvent mouseevent) {
	}

	@Override
	public void mouseEntered(MouseEvent mouseevent) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseExited(MouseEvent mouseevent) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent mouseevent) {

		// Get position
		Point p = getPosition(mouseevent);
		int but = mouseevent.getModifiers();
		switch (but) {
		case 16: // Left click
			// Copy and apply the brush on it (=selected tiles)
			if (canvas.getMode() != ZEditMode.COPY && canvas.getMode() != ZEditMode.COPY_DRAG) {
				canvas.applyBrush(p);
			}
			break;
		case 4: // Right click
		case 5: // Right click with LEFT SHIFT
			canvas.clearWithBrush(p);
			break;
		}
	}

	@Override
	public void mouseReleased(MouseEvent mouseevent) {
		switch (canvas.getMode()) {
		case NORMAL:
			canvas.endBrush();
			break;
		case COPY_DRAG:
			canvas.endCopy();
			break;
		}
	}

	@Override
	public void mouseDragged(MouseEvent mouseevent) {
		mousePressed(mouseevent);
		Point p;

		switch (canvas.getMode()) {
		case COPY:
			p = getInsideCameraPosition(mouseevent);
			canvas.startCopy(p);
			break;
		case NORMAL:
		case TILE_RAISE_EDIT:
		case TILE_LOWER_EDIT:
			// Store the cursor location
			p = getInsidePosition(mouseevent);
			canvas.cursorLocation = p;
			break;
		case COPY_DRAG:
			mouseMoved(mouseevent);
			break;
		}
	}

	public Point getAdjustedPoint(MouseEvent p_event) {
		Point p = p_event.getPoint();
		if (canvas.isZoom()) {
			p.x /= 2;
			p.y /= 2;
		}
		return p;
	}

	/**
	 * Get pixel-scaled position
	 * 
	 * @param event
	 * @return Point
	 */
	private Point getPosition(MouseEvent event) {
		Point p = getAdjustedPoint(event);
		Point camera = panel.getPosition();
		p.x += camera.x;
		p.y += camera.y; // + 8 + 4;

		return p;
	}

	/**
	 * Get pixel-scaled position in window coordinates
	 * 
	 * @param event
	 * @return
	 */
	private Point getInsidePosition(MouseEvent event) {
		Point p = getAdjustedPoint(event);
		Point camera = panel.getPosition();
		p.x = (p.x + camera.x % 16) / 16 * 16 - camera.x % 16;
		p.y = (p.y + camera.y % 16) / 16 * 16 - camera.y % 16;

		return p;
	}

	private Point getInsideCameraPosition(MouseEvent event) {
		Point p = getInsidePosition(event);
		Point camera = panel.getPosition();
		p.x += 16 * (camera.x / 16);
		p.y += 16 * (camera.y / 16);
		return p;
	}

	@Override
	public void mouseMoved(MouseEvent mouseevent) {
		Point p = getPosition(mouseevent);
		
		// Display location in system message
		StringBuilder message = new StringBuilder();
		message.append("X: ");
		message.append(p.x / 16);
		for (int i = message.length(); i < 16; i++) {
			message.append(" ");
		}
		message.append("Y: ");
		message.append(p.y / 16);

		message.append("         ");
		message.append("x: ");
		message.append(p.x);
		message.append("    y: ");
		message.append(p.y);

		MasterFrameManager.display(message.toString(),
				MasterFrameManager.MESSAGE_INFO);

		// Display case info
		Case c = canvas.makeAreaWrapper().get_mapcase(p.x/16, p.y/16);
		message.setLength(0);
		if (c != null) {
			caseToString(message, c);
		}
		MasterFrameManager.displayCaseInfo(message.toString());
		if (focusOnCursor) {
			canvas.setObjectOnCursor(p);
		}

		// Store the cursor location
		p = getInsidePosition(mouseevent);
		canvas.cursorLocation = p;

		// In order to user can press keys when mouse is under the canvas
		canvas.requestFocusInWindow();
	}

	@Override
	public void keyPressed(KeyEvent p_keyevent) {
		int code = p_keyevent.getKeyCode();
		switch (code) {
		case 37: // left
			canvas.left = true;
			break;
		case 39: // right
			canvas.right = true;
			break;
		case 38: // up
			canvas.up = true;
			break;
		case 40: // down
			canvas.down = true;
			break;
		case 16: // shift
			focusOnCursor = true;
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent p_keyevent) {
		int code = p_keyevent.getKeyCode();
		switch (code) {
		case 37: // left
			canvas.left = false;
			break;
		case 39: // right
			canvas.right = false;
			break;
		case 38: // up
			canvas.up = false;
			break;
		case 40: // down
			canvas.down = false;
			break;
		case 16: // shift
			focusOnCursor = false;
			break;
		}
	}

	@Override
	public void keyTyped(KeyEvent p_keyevent) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		int rot = e.getWheelRotation();
		if (rot == 1) {
			canvas.setZoom(false);
		} else {
			canvas.setZoom(true);
		}
	}
	
	private void caseToString(StringBuilder sb, Case c) {
		sb.append("back:");
		tileToString(sb, c.getBackTile());
		if (c.getBackTile2() != null) {
			sb.append(" - back2:");
			tileToString(sb, c.getBackTile2());
		}
		if (c.getForeTile() != null) {
			sb.append(" - fore:");
			tileToString(sb,c.getForeTile());
		}
	}
	
	private void tileToString(StringBuilder sb, Tile t) {
		sb.append(TileEngine.tileBankNames[t.bank]).append("(").append(t.index+")");
		if (t.reverse != Reverse.NOTHING) {
			sb.append(t.reverse);
		}
		if (t.rotation != Rotation.NOTHING) {
			sb.append(t.rotation);
		}
	}
}