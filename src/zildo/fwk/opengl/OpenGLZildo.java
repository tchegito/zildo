package zildo.fwk.opengl;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import zildo.fwk.engine.EngineZildo;
import zildo.monde.Point;

public class OpenGLZildo extends OpenGLGestion {

	String windowTitle="Zildo OpenGL";

    private EngineZildo engineZildo;

    private float z;
    private float xx;
    private Point zoomPosition;
    private boolean pressed=false;

	public OpenGLZildo(boolean fullscreen) {
		super(fullscreen);
		
		z=0.0f;
	}
	
    public void setEngineZildo(EngineZildo p_engineZildo) {
    	engineZildo=p_engineZildo;
    }
    
    protected void mainloopExt() {

        // Pour test
        if(Keyboard.isKeyDown(Keyboard.KEY_ADD)) {       // '+'
            z+=0.1f;
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_SUBTRACT)) {       // '-'
            z-=0.1f;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_MULTIPLY)) {
        	pressed=true;
        }
        if (!Keyboard.isKeyDown(Keyboard.KEY_MULTIPLY) && pressed) {
        	pressed=false;
        }
        xx+=0.5f / 8.0f;
    }
    
    public void render() {

   		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT); // Clear The Screen And The Depth Buffer

    	GL11.glLoadIdentity(); // Reset The Projection Matrix

    	// invert the y axis, down is positive
        float zz=(float) (z *5.0f);
        if (zz != 0.0f) {
        	GL11.glTranslatef(-zoomPosition.getX()*zz, zoomPosition.getY()*zz,0.0f);
        }
    	GL11.glScalef(1+zz , -1-zz, 1);
    	EngineZildo.filterCommand.doPreFilter();
    	
    	
		//GL11.glMatrixMode(GL11.GL_MODELVIEW);

       // GL11.glLight(GL11.GL_LIGHT1, GL11.GL_POSITION,(FloatBuffer)temp.asFloatBuffer().put(lightPosition).flip());         // Position The Light

        //lightPosition[2]+=0.1f;

        engineZildo.render();

        EngineZildo.filterCommand.doPostFilter();
        
       	Display.sync(framerate);
        Display.update();
        
        //camera.place(new Vector3f(xx,0,z)); //EngineZildo.mapManagement.getCamerax(), 
//            			EngineZildo.mapManagement.getCameray(),
        			//80.0f));
        //camera.rotateZ(0.01f);
        //camera.rotateX(0.1f);
        //camera.rotateY(-0.2f);
        //camera.rotateZ(0.3f);
    	
    }
    
    /**
     * Set orthographic projection, run.
     */
    public void run() {
    	EngineZildo.ortho.setOrthographicProjection();
    	super.run();
    }
    
    public void setZ(float p_z) {
    	z=p_z;
    }

	public void setZoomPosition(Point zoomPosition) {
		this.zoomPosition = zoomPosition;
	}
	
    protected void cleanUpExt() {
    	engineZildo.cleanUp();
    }
}
