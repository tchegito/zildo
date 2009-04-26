package zildo.fwk.opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.Sys;
import org.lwjgl.devil.IL;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

import zildo.Zildo;
import zildo.prefs.Constantes;

/**
 * Classe qui regroupe tous les comportements d'initialisation généraux d'OpenGL:
 * -énumération des modes de rendu
 * -création du contexte
 * -chargement de texture
 * -boucle principale
 * 
 * Nécessite l'implémentation de la méthode 
 * @see #render()
 * 
 * Permet de surcharger la méthode
 * @see #mainloopExt()
 * 
 * @author Tchegito
 *
 */
public abstract class OpenGLGestion {

    private DisplayMode displayMode;
    String windowTitle; //="(To override) Window OpenGL";
    private float lightAmbient[] = { 0.5f, 0.5f, 0.5f, 1.0f };  // Ambient Light Values ( NEW )
    private float lightDiffuse[] = { 1.0f, 1.0f, 1.0f, 1.0f };      // Diffuse Light Values ( NEW )
    private float lightPosition[] = { 0.0f, 0.0f, 2.0f, 1.0f }; // Light Position ( NEW )

    protected int framerate;
    private boolean fullscreen;
    private boolean done;
    
    public OpenGLGestion(boolean fullscreen) {
    	try {
    		this.fullscreen = fullscreen;
    		init();
    	} catch (Exception e) {
    		throw new RuntimeException(e);
    	}

    }
    
    private void showDisplayMode(DisplayMode d) {
    	System.out.println("mode: "+d.getWidth()+"x"+d.getHeight()+" "+d.getBitsPerPixel()+"bpp "+d.getFrequency()+"Hz");
    }
    private void createWindow() throws Exception {
        Display.setFullscreen(fullscreen);
        DisplayMode d[] = Display.getAvailableDisplayModes();
        for (int i = 0; i < d.length; i++) {
            if (d[i].getWidth() == Zildo.viewPortX * 2
                && d[i].getHeight() == Zildo.viewPortY * 2
                && d[i].getBitsPerPixel() == 32) {
                displayMode = d[i];
            	showDisplayMode(d[i]);
                break;
            }
        }
        Display.setDisplayMode(displayMode);
        Display.setTitle(windowTitle);
        framerate=Display.getDisplayMode().getFrequency();
        Display.create();
    }

    public void init() throws Exception {
        createWindow();
        IL.create();

        loadTextures();
        initGL();
    }
    
    private void loadTextures() {
        loadTexture(Constantes.DATA_PATH+"Marie.bmp");
    }
    
    private void initGL() {
        GL11.glEnable(GL11.GL_TEXTURE_2D); // Enable Texture Mapping
        GL11.glShadeModel(GL11.GL_SMOOTH); // Enable Smooth Shading
        GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f); // Black Background
        GL11.glClearDepth(1.0f); // Depth Buffer Setup
        GL11.glEnable(GL11.GL_DEPTH_TEST); // Enables Depth Testing
        GL11.glDepthFunc(GL11.GL_LEQUAL); // The Type Of Depth Testing To Do

        //initProjectionScene();
        
        GL11.glEnable(GL11.GL_CULL_FACE);

        ByteBuffer temp = ByteBuffer.allocateDirect(16);
        temp.order(ByteOrder.nativeOrder());
        GL11.glLight(GL11.GL_LIGHT1, GL11.GL_AMBIENT, (FloatBuffer)temp.asFloatBuffer().put(lightAmbient).flip());              // Setup The Ambient Light
        GL11.glLight(GL11.GL_LIGHT1, GL11.GL_DIFFUSE, (FloatBuffer)temp.asFloatBuffer().put(lightDiffuse).flip());              // Setup The Diffuse Light
        GL11.glLight(GL11.GL_LIGHT1, GL11.GL_POSITION,(FloatBuffer)temp.asFloatBuffer().put(lightPosition).flip());         // Position The Light
        GL11.glEnable(GL11.GL_LIGHT1);                          // Enable Light One
        
        //GL11.glEnable(GL11.GL_LIGHTING);
        
        //Display.setVSyncEnabled(true);

    }
    
    /*
    private void initProjectionScene() {
        GL11.glMatrixMode(GL11.GL_PROJECTION); // Select The Projection Matrix
        GL11.glLoadIdentity(); // Reset The Projection Matrix

        // Calculate The Aspect Ratio Of The Window
        GLU.gluPerspective(45.0f, (float) displayMode.getWidth() / (float) displayMode.getWidth(),0.1f,100.0f);
        GL11.glMatrixMode(GL11.GL_MODELVIEW); // Select The Modelview Matrix
        
        // Really Nice Perspective Calculations
        GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST);
    }
*/    
    
    /**
     * Texture loading directly from LWJGL examples
     */
    private int[] loadTexture(String path) {
    	
        //IntBuffer image = ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder()).asIntBuffer();
        //IL.ilGenImages(image);
        //IL.ilBindImage(image.get(0));
        IL.ilLoadImage(path);
        //IL.ilConvertImage(IL.IL_RGB, IL.IL_BYTE);
        //ByteBuffer scratch = ByteBuffer.allocateDirect(IL.ilGetInteger(IL.IL_IMAGE_WIDTH) * IL.ilGetInteger(IL.IL_IMAGE_HEIGHT) * 3);
        //IL.ilCopyPixels(0, 0, 0, IL.ilGetInteger(IL.IL_IMAGE_WIDTH), IL.ilGetInteger(IL.IL_IMAGE_HEIGHT), 1, IL.IL_RGB, IL.IL_BYTE, scratch);

        // Create A IntBuffer For Image Address In Memory
        IntBuffer buf = ByteBuffer.allocateDirect(12).order(ByteOrder.nativeOrder()).asIntBuffer();
        //GL11.glGenTextures(buf); // Create Texture In OpenGL
/*
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, buf.get(0));
        // Typical Texture Generation Using Data From The Image

        // Create Nearest Filtered Texture
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, buf.get(0));
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, IL.ilGetInteger(IL.IL_IMAGE_WIDTH), 
                IL.ilGetInteger(IL.IL_IMAGE_HEIGHT), 0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, scratch);

        // Create Linear Filtered Texture
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, buf.get(1));
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, IL.ilGetInteger(IL.IL_IMAGE_WIDTH), 
                IL.ilGetInteger(IL.IL_IMAGE_HEIGHT), 0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, scratch);

        // Create MipMapped Texture
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, buf.get(2));
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_NEAREST);
        GLU.gluBuild2DMipmaps(GL11.GL_TEXTURE_2D, 3, IL.ilGetInteger(IL.IL_IMAGE_WIDTH), 
                IL.ilGetInteger(IL.IL_IMAGE_HEIGHT), GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, scratch);
*/
      return new int[]{ buf.get(0), buf.get(1), buf.get(2) };     // Return Image Addresses In Memory
    }    

    public void run() {
        try {
            while (!done) {
                mainloop();

                this.render();

            }
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
        cleanUp();
    }

    public void cleanUp() {
    	cleanUpExt();
    	Display.destroy();
    }
    
    protected void cleanUpExt() {
    	
    }
    
    protected void mainloop() {
    	if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {       // Exit if Escape is pressed
            done = true;
        }
        if(Display.isCloseRequested()) {                     // Exit if window is closed
            done = true;
        }
        
        mainloopExt();
    }
    
    // Defautl main loop extended : nothing. Ready to override
    protected void mainloopExt() {
    	
    }
    
    protected abstract void render();
    
    private double simulationTime=0;
    private double fps;
    
    public void beginScene() {
    	// time elapsed since we last rendered
    	double secondsSinceLastFrame = getTimeInSeconds() - simulationTime;

    	// update the simulation current time
    	simulationTime += secondsSinceLastFrame;

    	fps=1 / secondsSinceLastFrame;
    	//long toWait=(long) (2/(framerate*1000) - secondsSinceLastFrame*1000.0f);
    	/*
    	if (toWait < 1000) {
	    	try {
	    		Thread.sleep(toWait);
	    	}catch (Exception e) {
	    		
	    	}
    	}
    	*/
    }
    
    public void endScene() {
    	
    }   
    
    static long ticksPerSecond;
    
    public double getFPS() {
    	return fps;
    }
    
    public static double getTimeInSeconds()
    {
        if (ticksPerSecond == 0) { // initialize ticksPerSecond
            ticksPerSecond = Sys.getTimerResolution();
        }
        return (((double)Sys.getTime())/(double)ticksPerSecond);
    }

}
