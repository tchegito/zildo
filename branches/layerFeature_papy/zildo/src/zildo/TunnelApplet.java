package zildo;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JApplet;

import zildo.monde.util.Point;

public class TunnelApplet extends JApplet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4712846039893965845L;
	Tunnel tunnel;

	@Override
	public void init() {
		super.init();
		setSize(320, 240);
	}
	public TunnelApplet() {
		tunnel = new Tunnel();
	}
	
	@Override
	public void paint(Graphics g) {
		long t1 = System.nanoTime();
		//super.paint(g);
		tunnel.update();
		long t2= System.nanoTime();
		
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, 320,240);
		
		g.setColor(Color.WHITE);
		
		// Draw tunnel points
		for (int i=0;i<Tunnel.DEEPNESS;i++) {
			for (int j=0;j<Tunnel.WIDTH;j++) {
				Point p = tunnel.screenPoints[i][j];
				if (p != null) {
					Point p2 = tunnel.screenPoints[i][(j+1) % Tunnel.WIDTH];
					g.drawLine(p.x, p.y, p2.x, p2.y);
					if (i != ((Tunnel.lastIndex + 1) % Tunnel.DEEPNESS)) { //< (Tunnel.DEEPNESS - 1)) {
						Point p3 = tunnel.screenPoints[(i+Tunnel.DEEPNESS-1) % Tunnel.DEEPNESS][j];
						g.drawLine(p.x, p.y, p3.x, p3.y);
					}
				}
			}
		}
		long t3 = System.nanoTime();
		
		//System.out.println("temps:" + ((t2-t1) / 1000000) + "ms / "+((t3-t2)/1000000)+"ms");
	    try {
	        //Thread.sleep(60);
	      } catch (Exception e) {
	      }
	    repaint();
	}
}
