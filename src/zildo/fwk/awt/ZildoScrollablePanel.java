package zildo.fwk.awt;

import java.awt.BorderLayout;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.EventListener;

import javax.swing.BoundedRangeModel;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;

import org.lwjgl.LWJGLException;

import zildo.client.ClientEngineZildo;
import zildo.monde.map.Point;

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

	public static final int viewSizeX=640;
	public static final int viewSizeY=600;
	
	ZildoCanvas zildoCanvas;
	JScrollBar horizontal;
	JScrollBar vertical;
	
	public ZildoScrollablePanel(String p_defaultMap) throws LWJGLException {
		// Create the panel with two scroll bars, and the canvas
		this.setLayout(new BorderLayout());
		horizontal=new MapScrollBar(JScrollBar.HORIZONTAL);
		vertical=new MapScrollBar(JScrollBar.VERTICAL);
		this.add(horizontal, BorderLayout.SOUTH);
		this.add(vertical, BorderLayout.EAST);
		this.setSize(viewSizeX,viewSizeY);
		zildoCanvas = new ZildoCanvas(p_defaultMap);
        zildoCanvas.setSize(viewSizeX,viewSizeY);
		this.add(zildoCanvas);
		
		// Create the mouse listener
		EventListener zildoListener=new ZildoMouseListener(this);
		zildoCanvas.addMouseListener((MouseListener) zildoListener);
		zildoCanvas.addMouseMotionListener((MouseMotionListener) zildoListener);
	}

	public java.awt.Point getPosition() {
		int x=horizontal.getValue();
		int y=vertical.getValue();
		return new java.awt.Point(x,y);
	}

	public ZildoCanvas getZildoCanvas() {
		return zildoCanvas;
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
			setMaximum(max);
			BoundedRangeModel model=new DefaultBoundedRangeModel(0, extent, 0, max);
			setModel(model);
			setUnitIncrement(16);
		}
		
		@Override
		public void setValue(int val) {
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
}
