/**
 * The Land of Alembrum
 * Copyright (C) 2006-2013 Evariste Boussaton
 * 
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

package zildo.client;

import java.io.File;
import java.io.FilenameFilter;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import zildo.Zildo;
import zildo.fwk.Injector;
import zildo.fwk.file.ClassicFileUtil;
import zildo.fwk.file.EasyBuffering;
import zildo.fwk.file.FileUtil;
import zildo.fwk.gfx.GraphicStuff;
import zildo.fwk.gfx.Ortho;
import zildo.fwk.gfx.PixelShaders;
import zildo.fwk.gfx.engine.SpriteEngine;
import zildo.fwk.gfx.engine.TextureEngine;
import zildo.fwk.gfx.engine.TileEngine;
import zildo.fwk.gfx.filter.FilterEffect;
import zildo.fwk.gfx.filter.ScreenFilter;
import zildo.fwk.input.KeyboardHandler;
import zildo.fwk.opengl.OpenGLGestion;
import zildo.fwk.opengl.SoundEngine;

/**
 * Handle all platform-dependent classes.
 *
 * It's a singleton warehouse.
 *
 * @author Tchegito
 *
 */
public class PlatformDependentPlugin {

    private Injector injector = new Injector();
   
    public KeyboardHandler kbHandler;
    public Ortho ortho;
    //public final TextureEngine textureEngine;
    public TileEngine tileEngine;
    public SpriteEngine spriteEngine;
    public PixelShaders pixelShaders;
    public GraphicStuff gfxStuff;
    public SoundEngine soundEngine;
    public OpenGLGestion openGLGestion;
    
    private FileUtil fileUtil = new ClassicFileUtil();
    
    public Map<Class<ScreenFilter>, ScreenFilter> filters;
    
    public enum KnownPlugin { Lwjgl, Android };
   
    public static KnownPlugin currentPlugin = KnownPlugin.Lwjgl;    // Constant for now
   
    final static String PLATFORM_PACKAGE = "zildo.platform.";
    
	public PlatformDependentPlugin() {
        filters = new HashMap<Class<ScreenFilter>, ScreenFilter>();

    }
    
    public void init(boolean p_awt) {
    	
    	if (currentPlugin == KnownPlugin.Android) {
    		fileUtil = injector.createSingleton("com.alembrum.AndroidFileUtil");
    	}
        // Look for existing stuff in the class loader and create all needed singletons
    	if (!p_awt) {
    		kbHandler = createSingleton("input.KeyboardHandler");
    	}
    	if (!p_awt) {
    		openGLGestion = createSingleton("opengl.OpenGLGestion", Zildo.fullScreen);
    	} else {
    		openGLGestion = createSingleton("opengl.OpenGLGestion");
    	}
        ortho = createSingleton("opengl.Ortho", Zildo.viewPortX, Zildo.viewPortY);
        gfxStuff = createSingleton("opengl.GraphicStuff");
        
        pixelShaders = createSingleton("opengl.PixelShaders");

        // Tile and Sprite engine need their own texture engine
        // So we have to create two of them
        TextureEngine textureEngine1 = createSingleton("engine.TextureEngine", gfxStuff);
        tileEngine = createSingleton("engine.TileEngine", textureEngine1);
        TextureEngine textureEngine2 = createSingleton("engine.TextureEngine", gfxStuff);
        spriteEngine = createSingleton("engine.SpriteEngine", textureEngine2);

        if (!p_awt) {
        	soundEngine = createSingleton("opengl.SoundEngine");
        }
    }
   
    /**
     * Create instances of all filters declared in {@link FilterEffect}.
     */
    @SuppressWarnings("unchecked")
    public void initFilters() {
    	filters.clear();
        // Filters
        for (FilterEffect f : FilterEffect.values()) {
        	Class<? extends ScreenFilter>[] classes = f.getFilterClass();
        	for (Class<? extends ScreenFilter> cl : classes) {
        		if (filters.get(cl) == null) {
        	        GraphicStuff gfxStuffFilter = createSingleton("opengl.GraphicStuff");
        			ScreenFilter filter = createSingleton("filter."+cl.getSimpleName(), gfxStuffFilter);
        			filters.put((Class<ScreenFilter>) cl, filter);
        		}
        	}
        }
    }

    /**
     * Open an unmodifiable file.
     * @param path
     * @return EasyBuffering
     */
	public EasyBuffering openFile(String path) {
    	return fileUtil.openFile(path);
    }
    
	public OutputStream prepareSaveFile(String path) {
		return fileUtil.prepareSaveFile(path);
	}
	/**
	 * For savegames (distinction with {@link #openFile(String)} exists only with Android)
	 * @param path
	 * @return EasyBuffering
	 */
	public EasyBuffering openPrivateFile(String path) {
    	return fileUtil.openPrivateFile(path);
    }
	
    public File[] listFiles(String path, FilenameFilter filter) {
    	return fileUtil.listFiles(path, filter);
    }
    
    public Object openFd(String path) {
    	return fileUtil.openFd(path);
    }
    
    @SuppressWarnings("unchecked")
	public <T extends ScreenFilter> T getFilter(Class<T> p_filterClazz) {
    	return (T) filters.get(p_filterClazz);
    }
    
    @SuppressWarnings("unchecked")
    private <T> T createSingleton(String p_className, Object...p_param) {
        // Format the class name with the knonw plugin
        int posPoint = p_className.lastIndexOf(".");
        String formattedClassName = p_className.substring(0, posPoint+1)+
                                    currentPlugin.name()+
                                    p_className.substring(posPoint+1);
        return (T) injector.createSingleton(PLATFORM_PACKAGE + formattedClassName, p_param);
    }
  
   
}