package zildo.fwk.awt;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import zeditor.windows.managers.MasterFrameManager;

public class ZildoMouseListener implements MouseListener, MouseMotionListener {

	ZildoScrollablePanel panel;
	
	public ZildoMouseListener(ZildoScrollablePanel p_panel) {
		panel=p_panel;
	}
	
	public void mouseClicked(MouseEvent mouseevent) {
		// Drop selected tile on map
		
	}

	public void mouseEntered(MouseEvent mouseevent) {
		// TODO Auto-generated method stub
		
	}

	public void mouseExited(MouseEvent mouseevent) {
		// TODO Auto-generated method stub
		
	}

	public void mousePressed(MouseEvent mouseevent) {
		// TODO Auto-generated method stub
		
	}

	public void mouseReleased(MouseEvent mouseevent) {
		// TODO Auto-generated method stub
		
	}

	public void mouseDragged(MouseEvent mouseevent) {
		// TODO Auto-generated method stub

	}

	public void mouseMoved(MouseEvent mouseevent) {
		// TODO Auto-generated method stub
		Point p=mouseevent.getPoint();
		Point camera=panel.getPosition();
		p.x+=camera.x;
		p.y+=camera.y;
		StringBuilder message=new StringBuilder();
		message.append("X: ");
		message.append(p.x / 16);
		for (int i=message.length();i<16;i++) {
			message.append(" ");
		}
		message.append("Y: ");
		message.append(p.y / 16);
		MasterFrameManager.display(message.toString(), MasterFrameManager.MESSAGE_INFO);
	}
}
