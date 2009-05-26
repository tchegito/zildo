package zildo;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;

import zildo.fwk.awt.AWTOpenGLCanvas;
import zildo.fwk.awt.ZildoCanvas;
import zildo.monde.client.ZildoRenderer;

public class TestFrameZEditor extends JFrame {

	AWTOpenGLCanvas zildoCanvas;
	
	public TestFrameZEditor() {
		// TODO Auto-generated method stub
		super("Hello World!");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	    getContentPane().add((new JLabel("Hello, World!")));
	    
	    // Zildo renderer
        try {
            zildoCanvas = new ZildoCanvas();
            zildoCanvas.setSize(640,480);
        } catch (LWJGLException lwjgle) {
            lwjgle.printStackTrace();
        }
        getContentPane().add(zildoCanvas,BorderLayout.SOUTH);
        
	    pack();
	    setLocationRelativeTo(null);
	    setVisible(true);
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		new TestFrameZEditor();

	}

}
