/**
 * Legend of Zildo
 * Copyright (C) 2006-2012 Evariste Boussaton
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

package zeditor.fwk.awt;

import java.awt.BorderLayout;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

import javax.swing.BoundedRangeModel;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;

import org.lwjgl.LWJGLException;

import zildo.Zildo;
import zildo.client.ClientEngineZildo;
import zildo.client.MapDisplay;
import zildo.monde.map.Area;
import zildo.monde.map.ChainingPoint;
import zildo.monde.util.Point;
import zildo.monde.util.Zone;
import zildo.server.EngineZildo;

/**
 * Panel handling map display.<p/>
 * 
 * Provide two scrollbars to navigate through the map. Navigation is done by moving camera in the 
 * {@link MapDisplay} class.
 * 
 * @author tchegito
 *
 */
public class ZildoScrollablePanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final int viewSizeX=Zildo.screenX;
	public static final int viewSizeY=Zildo.screenY+26; // + 32 + 16 - 4 - 2;
	
	ZildoCanvas zildoCanvas;
	MapScrollBar horizontal;
	MapScrollBar vertical;
	
	public ZildoScrollablePanel(String p_defaultMap) throws LWJGLException {
		// Create the panel with two scroll bars, and the canvas
		this.setLayout(new BorderLayout());
		horizontal=new MapScrollBar(JScrollBar.HORIZONTAL);
		vertical=new MapScrollBar(JScrollBar.VERTICAL);
		this.add(horizontal, BorderLayout.SOUTH);
		this.add(vertical, BorderLayout.EAST);
		this.setSize(viewSizeX,viewSizeY);
		zildoCanvas = new ZildoCanvas(this, p_defaultMap);
        zildoCanvas.setSize(viewSizeX,viewSizeY);
		this.add(zildoCanvas);
		
		// Create the mouse listener
		EventListener zildoListener=new ZildoMouseKeyListener(this);
		zildoCanvas.addMouseListener((MouseListener) zildoListener);
		zildoCanvas.addMouseMotionListener((MouseMotionListener) zildoListener);
		zildoCanvas.addKeyListener((KeyListener) zildoListener);
		zildoCanvas.addMouseWheelListener((MouseWheelListener) zildoListener);
	}

	public java.awt.Point getPosition() {
		int x=horizontal.getValue();
		int y=vertical.getValue();
		return new java.awt.Point(x,y);
	}
	
	public void setPosition(java.awt.Point p_point) {
		horizontal.setValue(p_point.x);
		vertical.setValue(p_point.y);
	}

	public java.awt.Point getCameraTranslation() {
		java.awt.Point camera=getPosition();
		camera.x=-16*(camera.x / 16);
		camera.y=-16*(camera.y / 16);
		return camera;
	}
	public ZildoCanvas getZildoCanvas() {
		return zildoCanvas;
	}
	
	public void setZoom(boolean p_zoom) {
		double factor2 = 2f;
		if (!p_zoom) {
			factor2 = 1 / factor2;
		}
		int hv = horizontal.getValue();	// Keep old values
		int vv = vertical.getValue();
		horizontal.setValue(0);	// Identity to avoid getting out of range after changing the extent
		vertical.setValue(0);
		horizontal.getModel().setExtent((int) (horizontal.getModel().getExtent() / factor2));
		vertical.getModel().setExtent((int) (vertical.getModel().getExtent() / factor2));
		// Reset the previous values
		horizontal.setValue(hv);
		vertical.setValue(vv);
	}
	
	public class MapScrollBar extends JScrollBar {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public MapScrollBar(int i) {
			super(i);
			int max;
			int extent;
			if (orientation == JScrollBar.HORIZONTAL) {
				max=64*16;
				extent=viewSizeX;
			} else {
				max=64*16;
				extent=viewSizeY;
			}
			setMinimum(0);
			setMaximum(max);
			BoundedRangeModel m=new DefaultBoundedRangeModel(0, extent, 0, max);
			setModel(m);
			setUnitIncrement(0);
		}
		
		@Override
		public void setValue(int val) {
			int max=getMaximum() - getVisibleAmount();
			if (val > max) {
				val=max;
			}
			if (val < 0) {
				val=0;
			}
			if (val >= 0 && val <= (getMaximum() - getVisibleAmount())) {
				Point p=ClientEngineZildo.mapDisplay.getCamera();
				if (orientation == JScrollBar.HORIZONTAL) {
					p.x=val;
				} else {
					p.y=val;
				}
				ClientEngineZildo.mapDisplay.setCamera(p);
				super.setValue(val);
			}
		}
			
		public void increase() {
			setValue(getValue() + 16);
		}

		public void decrease() {
			setValue(getValue() - 16);
		}
	}
	
	public ZildoCanvas getCanvas() {
		return zildoCanvas;
	}
	
	public List<Zone> getChainingPoints() {
		List<Zone> zones=new ArrayList<Zone>();
		Area map=EngineZildo.mapManagement.getCurrentMap();
		List<ChainingPoint> chaining=map.getChainingPoints();
		for (ChainingPoint c : chaining) {
			Point p1=new Point(c.getPx(), c.getPy());
			Point p2=new Point(32, 16); //c.getPx() & 63, c.getPy() & 63);
			if (c.isBorder()) {
				if (p1.x == 0 || p1.x == map.getDim_x()-1) {
					p1.y=0;
					p2.y=map.getDim_y()*16;
					p2.x=16;
				} else {
					p1.x=0;
					p2.x=map.getDim_x()*16;
				}
			} else if (c.isVertical()) {
				p2.x=16;
				p2.y=32;
			}
			zones.add(new Zone(p1.x, p1.y, p2.x, p2.y));
		}
		return zones;
	}
}
