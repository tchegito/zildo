/**
 * The Land of Alembrum
 * Copyright (C) 2006-2016 Evariste Boussaton
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

package zildo.platform.opengl;

import java.nio.ByteBuffer;

import shader.Shaders;
import shader.Shaders.GLShaders;
import zildo.fwk.file.ShaderReader;
import zildo.fwk.gfx.PixelShaders;
import zildo.monde.util.Vector4f;
import android.opengl.GLES20;
import android.util.Log;

/**
 * @author Tchegito
 *
 */
public class AndroidPixelShaders extends PixelShaders {

	public static Shaders shaders;
	
	public AndroidPixelShaders() {
		super();

		shaders = new Shaders(this);
	}
	
	@Override
	public boolean canDoPixelShader() {
        return true;
	}
	
	@Override
	public void preparePixelShader() {
		// Do nothing : to squeeze super method
	}
	
	@Override
	protected int doCreateShader(String shaderPro, boolean pixel) {
        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
		int type = GLES20.GL_VERTEX_SHADER;
		if (pixel) {
			type = GLES20.GL_FRAGMENT_SHADER;
		}
        int shader = GLES20.glCreateShader(type); 
        
        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderPro);
        GLES20.glCompileShader(shader);
        
        final int[] compileStatus = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
        if (compileStatus[0] == 0) {
            String message = GLES20.glGetShaderInfoLog(shader);
        	Log.e("shaders", "can't compile, message="+message+" code="+shaderPro);
        } else {
        	Log.d("shaders", "Compilation ok");
        }
        
        return shader;
	}

	@Override
	protected void doSetParameter(int psId, ByteBuffer name, Vector4f color) {
	}

	@Override
	protected void deletePixelShader(int id) {
	}
	

	public class ShReturn {
		public int programId;
		public String[] uniforms;
	}

	/**
	 * Compile both vertex and fragment shader, and link program reference.<br/>
	 * The uniform field are automatically parsed and set into the given GLShaders.
	 * @param GLShaders
	 */
	public void loadCompleteShader(GLShaders sh) {
		String shaderName = sh.toString();
		ShaderReader sr = new ShaderReader("essl", shaderName);
		
		// Init shaders
        Log.d("shaders", "shader: "+shaderName);
		int vertexShader = doCreateShader(sr.getVertexCode(), false);
        int fragmentShader = doCreateShader(sr.getFragmentCode(), true);
        
        int mProgram = GLES20.glCreateProgram();             // create empty OpenGL Program
        GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
        
        GLES20.glLinkProgram(mProgram);                  // creates OpenGL program executables
        
     // Get the link status.
        final int[] linkStatus = new int[1];
        GLES20.glGetProgramiv(mProgram, GLES20.GL_LINK_STATUS, linkStatus, 0);
        
        // If the link failed, delete the program.
        if (linkStatus[0] == 0)
        {
        	Log.e("shaders", "link failed !");
            GLES20.glDeleteProgram(mProgram);
            mProgram = 0;
        }
        sh.id = mProgram;
        sh.setUniforms(sr.getUniforms());
	}
}
