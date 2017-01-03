package zildo.client;
/* =============================================================================
 * IRenderable.java Bubble Engine 
 * Copyright (c) 2004 - 2005 Benjamin "Evil-Devil" Behrendt
 * All rights reserved
 * -----------------------------------------------------------------------------
 * powered by LWJGL - Copyright (c) LWJGL Team (http://www.lwjgl.org)
 * powered by JAVA - Copyright (c) Sun Microsystems (http://java.sun.com)
 * -------------------------------------------------------------------------- */

/**
 * @author Benjamin "Evil-Devil" Behrendt
 * @version 1.0, 09.03.2005
 */
public interface IRenderable {
    
    /**
     * Used to initialize/set states of the render api i.e. openGL
     */
    public void initRenderer();
    
    /**
     * Used to load model, textures and everything that is needed
     * before a scene can be prerendered or plain rendered.
     */
    public void initScene();
    
    /**
     * Used for drawing the scene itself
     */
    public void renderScene();

    /**
     * Used to create things like DisplayLists, VBOs etc
     */
    public void preRenderScene();
    
    public void setInitialized(boolean initialized);
    
    public boolean isInitialized();

}