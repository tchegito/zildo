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
import java.nio.IntBuffer;

import org.lwjgl.opengl.ARBFragmentShader;
import static org.lwjgl.opengl.ARBShaderObjects.*;
import org.lwjgl.opengl.ARBVertexShader;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.Util;

import zildo.fwk.ZUtils;
import zildo.fwk.file.ShaderReader;
import zildo.fwk.gfx.PixelShaders;
import zildo.monde.util.Vector4f;

/**
 * @author Tchegito
 *
 */
public class LwjglPixelShaders extends PixelShaders {

	public enum GLShaders {
		// Specific for guards, and wounded enemies
		switchColor, wounded,
		// Gold (for invincibility)
		invincibility,
		// Star
		star,
		fire,
		// Clipping shader
		clip,
		// Fade rotation
		blackBlur,
		// Grid rotating fade
		circular,
		// reflection
		watered,
		// distortion
		underWater;
	}
	
	@Override
	public boolean canDoPixelShader() {
        return GLContext.getCapabilities().GL_ARB_shader_objects &&
        GLContext.getCapabilities().GL_ARB_fragment_shader &&
        GLContext.getCapabilities().GL_ARB_vertex_shader &&
        GLContext.getCapabilities().GL_ARB_shading_language_100;
	}
	
	private ByteBuffer transformStringIntoByteBuffer(String strData) {
		ByteBuffer shaderBuffer = ZUtils.createByteBuffer(strData.length());
		 
		byte[] shaderBytes=new byte[strData.length()];
		for (int i=0;i<strData.length();i++) {
			shaderBytes[i]=(byte) strData.charAt(i);
		}
		shaderBuffer.put(shaderBytes);
		shaderBuffer.flip();
		
		return shaderBuffer;
	}
	
	@Override
	protected int doCreateShader(String strData, boolean pixel) {
		
		ByteBuffer shaderPro = transformStringIntoByteBuffer(strData);
		
		// Create pixel shader
		int type = ARBFragmentShader.GL_FRAGMENT_SHADER_ARB;
		if (!pixel) {
			type = ARBVertexShader.GL_VERTEX_SHADER_ARB;
		}
		int shader = glCreateShaderObjectARB(type);
		glShaderSourceARB(shader, shaderPro);
		
		// Compile, link, validate
		glCompileShaderARB(shader);

		printLogInfo(shader);
		return shader;
	}
	
	
	private void printLogInfo(int obj)
	{
		IntBuffer iVal = ZUtils.createIntBuffer(1);
		glGetObjectParameterARB(obj, GL_OBJECT_INFO_LOG_LENGTH_ARB, iVal);
 
		int length = iVal.get();
		if (length > 0)
		{
			// We have some info we need to output.
			ByteBuffer infoLog = ZUtils.createByteBuffer(length);
			iVal.flip();
			glGetInfoLogARB(obj,  iVal, infoLog);
			byte[] infoBytes = new byte[length];
			infoLog.get(infoBytes);
			String out = new String(infoBytes);
 
			System.out.println("Info log:\n"+out);
		}
 
		Util.checkGLError();
	}
	
	@Override
	protected void doSetParameter(int psId, ByteBuffer name, Vector4f color) {
		int location = glGetUniformLocationARB(psId, name);
		glUniform4fARB(location, color.x, color.y, color.z, color.w);
	}

	@Override
	protected void deletePixelShader(int id) {
		glDeleteObjectARB(id);
	}
	
	// /////////////////////////////////////////////////////////////////////////////////////
	// preparePixelShader
	// /////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void preparePixelShader() {
		for (GLShaders sh : GLShaders.values()) {
			ShaderReader reader = new ShaderReader("glsl", sh.toString());
			addPixelShader(reader);
		}
	}

	// /////////////////////////////////////////////////////////////////////////////////////
	// addPixelShader
	// /////////////////////////////////////////////////////////////////////////////////////
	private void addPixelShader(ShaderReader reader) {
		int vertexShader = -1;
		if (reader.getVertexCode() != null) {
			vertexShader = doCreateShader(reader.getVertexCode(), false);
		}
		int fragmentShader = doCreateShader(reader.getFragmentCode(), true);
		
		int programObject  = glCreateProgramObjectARB();
		if (vertexShader != -1 ) {
			glAttachObjectARB(programObject, vertexShader);
		}
		glAttachObjectARB(programObject, fragmentShader);
		
		glLinkProgramARB(programObject);
		glValidateProgramARB(programObject);

		// Uniform values

		tabPixelShaders[n_PixelShaders] = programObject;
		n_PixelShaders++;
	}
}
