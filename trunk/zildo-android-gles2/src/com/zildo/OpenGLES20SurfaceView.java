/**
 * Legend of Zildo
 * Copyright (C) 2006-2012 Evariste Boussaton
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

package com.zildo;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

class OpenGLES20SurfaceView extends GLSurfaceView {
	
    public OpenGLES20SurfaceView(Context context){
        super(context);
    
        // Create an OpenGL ES 2.0 context.
        setEGLContextClientVersion(2);
    }
    

    /**
     * Standard View constructor. In order to render something, you
     * must call {@link #setRenderer} to register a renderer.
     */
    public OpenGLES20SurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setEGLContextClientVersion(2);
    }

	public void setViewRenderer(Renderer renderer) {
    	// Set the Renderer for drawing on the GLSurfaceView
        super.setRenderer(renderer);        	
    }
}