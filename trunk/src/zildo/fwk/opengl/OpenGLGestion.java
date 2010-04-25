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

package zildo.fwk.opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

import zildo.Zildo;

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
    protected boolean awt=false;	// Default, no awt
    
    public OpenGLGestion() {
    	awt=true;
    }
    
    public OpenGLGestion(boolean fullscreen) {
    	try {
    		this.fullscreen = fullscreen;
    		initDisplay();
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
        List<DisplayMode> selecteds=new ArrayList<DisplayMode>();
        for (int i = 0; i < d.length; i++) {
            if (d[i].getWidth() == Zildo.viewPortX * 2
                && d[i].getHeight() == Zildo.viewPortY * 2
                //&& d[i].getBitsPerPixel() == 32
                ) {
            	selecteds.add(d[i]);
            }
        }
        
        // Sort display modes from best to worse
        Collections.sort(selecteds, new DisplayModeComparator());
        
        boolean success=false;
        for (DisplayMode dm : selecteds) {
            displayMode = dm;
        	showDisplayMode(dm);
            success=initDisplayMode(dm);
        	if (success) {
        		break;
        	}
        }
        if (!success) {
        	throw new RuntimeException("Unable to set up screen !");
        }
    }

    public boolean initDisplayMode(DisplayMode d) {
        
    	try {
	        Display.setDisplayMode(displayMode);
	        Display.setTitle(windowTitle);
	        framerate=Display.getDisplayMode().getFrequency();
	        Display.create();
    	} catch (LWJGLException e) {
    		Display.destroy();
    		return false;
    	}
    	return true;
    }
    
    public void initDisplay() throws Exception {
        createWindow();
    }

    public void init() throws LWJGLException {
        initAppIcon();
        initGL();   
    }
    
    public void initAppIcon() {
        ByteBuffer icon = ByteBuffer.allocate(16 * 16 * 4);
        icon.put(OpenGLZildo.icon);
        icon.flip();
        Display.setIcon(new ByteBuffer[] { icon });
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
        
        Display.setVSyncEnabled(true);

    }

    public void cleanUp() {
    	cleanUpExt();
    	Display.destroy();
    }
    
    protected void cleanUpExt() {
    	
    }
    
    public boolean mainloop() {
    	boolean done=false;
    	if (!awt) {
	        if(Display.isCloseRequested()) {                     // Exit if window is closed
	            done = true;
	        }
	        
	        mainloopExt();
    	}
    	
        return done;
    }
    
    // Defautl main loop extended : nothing. Ready to override
    protected void mainloopExt() {
    	
    }
    
    protected abstract void render(boolean p_clientReady);
    
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
