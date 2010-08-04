/* =============================================================================
 * AWTOpenGLCanvas.java Bubble_Engine 
 * com.evildevil.bubble.core
 * Copyright (c) 2004 - 2005 Benjamin "Evil-Devil" Behrendt
 * All rights reserved
 * -------------------------------------------------------------------------- */
package zildo.fwk.awt;

import java.awt.GraphicsDevice;
import java.awt.Point;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.AWTGLCanvas;
import org.lwjgl.opengl.Drawable;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.vector.Vector4f;

import zildo.client.ClientEngineZildo;
import zildo.client.IRenderable;
import zildo.fwk.gfx.Ortho;
import zildo.monde.map.Area;
import zildo.server.EngineZildo;

/**
 * @author Benjamin "Evil-Devil" Behrendt
 * @version 1.0, 28.12.2005
 */
public class AWTOpenGLCanvas extends AWTGLCanvas implements Runnable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private IRenderable renderer = null;
	private Thread renderThread = null;
	private boolean initialize = false;

	protected ZildoScrollablePanel panel;

	protected Point cursorLocation;
	protected Point cursorSize;

	// Booleans for directional key pressed
	public boolean up;
	public boolean down;
	public boolean left;
	public boolean right;

	private final static Point defaultCursorSize = new Point(16, 16);

	// We have to communicate orders via boolean to this canvas
	// because we can use OpenGL outside of the paint process
	protected boolean changeMap = false;

	private int sizeX;
	private int sizeY;

	private boolean needToResize = false;

	public AWTOpenGLCanvas() throws LWJGLException {
		super();
	}

	public AWTOpenGLCanvas(PixelFormat pixel_format) throws LWJGLException {
		super(pixel_format);
	}

	public AWTOpenGLCanvas(GraphicsDevice device, PixelFormat pixel_format)
			throws LWJGLException {
		super(device, pixel_format);
	}

	public AWTOpenGLCanvas(GraphicsDevice device, PixelFormat pixel_format,
			Drawable drawable) throws LWJGLException {
		super(device, pixel_format, drawable);
	}

	public AWTOpenGLCanvas(IRenderable renderable) throws LWJGLException {
		this();
		setRenderer(renderable);
	}

	public AWTOpenGLCanvas(PixelFormat pixelFormat, IRenderable renderable)
			throws LWJGLException {
		super(pixelFormat);
		setRenderer(renderable);
	}

	public AWTOpenGLCanvas(GraphicsDevice device, PixelFormat pixelFormat,
			IRenderable renderable) throws LWJGLException {
		super(device, pixelFormat);
		setRenderer(renderable);
	}

	public AWTOpenGLCanvas(GraphicsDevice device, PixelFormat pixelFormat,
			Drawable drawable, IRenderable renderable) throws LWJGLException {
		super(device, pixelFormat, drawable);
		setRenderer(renderable);
	}

	public void setRenderer(IRenderable renderable) {
		this.renderer = renderable;
	}

	@Override
	public void paintGL() {
		if (changeMap) {
			Area map = EngineZildo.mapManagement.getCurrentMap();
			ClientEngineZildo.mapDisplay.setCurrentMap(map);
			// And the sprites
			EngineZildo.spriteManagement.updateSprites(false);
			ClientEngineZildo.spriteDisplay
					.setEntities(EngineZildo.spriteManagement
							.getSpriteEntities(null));
			changeMap = false;
		}
		if (!initialize) {
			System.out.println("initializing");
			initRenderThread();
			initOpenGL();
			// give the renderer a note that we have initialized our main render
			// stuff
			// and init the renderer itself
			// renderer.initRenderer();
			renderer.setInitialized(true);
			initialize = true;
		}
		try {
			makeCurrent();
			Ortho ortho = ClientEngineZildo.ortho;
			if (needToResize) {
				if (ortho != null) {
					ortho.setSize(sizeX, sizeY);
					needToResize = false;
				}

			}
			renderer.renderScene();
			// Draw rectangle
			if (cursorLocation != null) {
				Point size = cursorSize;
				if (cursorSize == null) {
					size = defaultCursorSize;
				}
				ortho.boxv(cursorLocation.x, cursorLocation.y, size.x, size.y,
						0, new Vector4f(1, 1, 1, 1));
			}

			swapBuffers();
		} catch (LWJGLException lwjgle) {
			// should not happen
			lwjgle.printStackTrace();
		}
	}

	private final void initRenderThread() {
		if (renderThread == null) {
			renderThread = new Thread(this);
			renderThread.start();
		}
	}

	private void manageKeys() {
		if (left) {
			panel.horizontal.decrease();
		}
		if (right) {
			panel.horizontal.increase();
		}
		if (up) {
			panel.vertical.decrease();
		}
		if (down) {
			panel.vertical.increase();
		}
	}

	public void run() {
		while (renderThread != null && renderThread.isAlive()) {
			synchronized (this) {
				repaint();
				manageKeys();
				try {
					Thread.sleep(16);
				} catch (InterruptedException ie) {
					ie.printStackTrace();
				}
			}
		}
	}

	/**
	 * User ask a resize. We plan it to next rendering.
	 */
	@Override
	public void setSize(int width, int height) {
		super.setSize(width, height);
		sizeX = width;
		sizeY = height;
		needToResize = true;
	}

	/**
	 * If the context is created and a IRenderable object is provided, then
	 * IRenderable.initRenderer() will be called to setup the states etc that
	 * were used.
	 * 
	 * @exception throws LWJGLExeption if there is still no context available
	 * @exception throws NullPointerException if there was no IRenderable object
	 *            provided
	 */
	protected void initOpenGL() {
		if (getContext() == null)
			new LWJGLException(
					"There is no context available that could be used to render something to it.");
		if (renderer == null)
			new NullPointerException(
					"No IRenderable instance found, can't be null. Need one to render a scene.");
		renderer.initRenderer();
	}

	public IRenderable getRenderer() {
		return this.renderer;
	}

	@Override
	public void removeNotify() {
		if (renderThread != null && renderThread.isAlive()) {
			renderThread = null;
		}
		cleanUp();
		super.removeNotify();
	}

	public void cleanUp() {

	}
}