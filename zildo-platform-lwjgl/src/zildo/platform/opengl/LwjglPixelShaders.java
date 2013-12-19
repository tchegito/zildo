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

package zildo.platform.opengl;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.opengl.ARBFragmentShader;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.ARBVertexShader;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.Util;

import zildo.fwk.ZUtils;
import zildo.fwk.gfx.PixelShaders;
import zildo.monde.util.Vector4f;

/**
 * @author Tchegito
 *
 */
public class LwjglPixelShaders extends PixelShaders {

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
		int vertexShader= ARBShaderObjects.glCreateShaderObjectARB(type);
		ARBShaderObjects.glShaderSourceARB(vertexShader, shaderPro);
		
		// Compile, link, validate
		ARBShaderObjects.glCompileShaderARB(vertexShader);
		
		int programObject  = ARBShaderObjects.glCreateProgramObjectARB();
		ARBShaderObjects.glAttachObjectARB(programObject, vertexShader);
		
		ARBShaderObjects.glLinkProgramARB(programObject);
		ARBShaderObjects.glValidateProgramARB(programObject);
		
		printLogInfo(vertexShader);
		
		return programObject;
	}
	
	
	private void printLogInfo(int obj)
	{
		IntBuffer iVal = ZUtils.createIntBuffer(1);
		ARBShaderObjects.glGetObjectParameterARB(obj, ARBShaderObjects.GL_OBJECT_INFO_LOG_LENGTH_ARB, iVal);
 
		int length = iVal.get();
		if (length > 0)
		{
			// We have some info we need to output.
			ByteBuffer infoLog = ZUtils.createByteBuffer(length);
			iVal.flip();
			ARBShaderObjects.glGetInfoLogARB(obj,  iVal, infoLog);
			byte[] infoBytes = new byte[length];
			infoLog.get(infoBytes);
			String out = new String(infoBytes);
 
			System.out.println("Info log:\n"+out);
		}
 
		Util.checkGLError();
	}
	
	@Override
	protected void doSetParameter(int psId, ByteBuffer name, Vector4f color) {
		int location = ARBShaderObjects.glGetUniformLocationARB(psId, name);
		ARBShaderObjects.glUniform4fARB(location, color.x, color.y, color.z, color.w);
	}

	@Override
	protected void deletePixelShader(int id) {
		ARBShaderObjects.glDeleteObjectARB(id);
	}
	
	// /////////////////////////////////////////////////////////////////////////////////////
	// preparePixelShader
	// /////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void preparePixelShader() {

		String cPSGuard[] = {
				"uniform vec4 Color1;",
				"uniform vec4 Color2;",
				"uniform vec4 Color3;",
				"uniform vec4 Color4;",
				"uniform vec4 curColor;",
				"uniform sampler2D tex;",
				"void main (void) {",
				"	vec4 texel=texture2D(tex, gl_TexCoord[0].st);",
				"	if (ceil(texel.w*2.0) == 2.0*Color1.w && texel.xyz==Color1.xyz)", // &&
																					// ceil(texel.y*2)==2.0f*Color1.y
																					// &&
																					// ceil(texel.z*2)==2.0f*Color1.z)", //== Color1.w)",
				"		gl_FragColor = Color3;",
				"	else if (ceil(texel.w*2.0) == 2.0*Color2.w && texel.xyz==Color2.xyz)",
				"		gl_FragColor = Color4;",
				"	else gl_FragColor=texel * curColor;", "}" };
		String cPSGuardHurt[] = { "uniform vec4 randomColor;",
				"uniform sampler2D tex;", "void main (void) {",
				"	vec4 texel=texture2D(tex, gl_TexCoord[0].st);",
				"	if (texel.w != 0.0) {",
				"		gl_FragColor = randomColor- texel / 2.0;",
				"		gl_FragColor.w = 1.0;", "	}", "}" };

		String cPSInvincibility[] = { "uniform vec4 factor;",
				"uniform sampler2D tex;", "const float blurSize = 1.0/256.0;",
				"void main (void) {",
				"	vec4 texel=texture2D(tex, gl_TexCoord[0].st);",
				"   float gray = dot(vec3(texel),vec3(0.3, 0.59, 0.11));",
				"   gray = clamp(gray * (factor.x * 4.0), 0.0, 1.0);",
				"   gl_FragColor = vec4(gray ,gray, 0, texel.w);", "}" };
		String shaderCode;
		shaderCode = getShaderCode(cPSGuard);
		addPixelShader(shaderCode);

		shaderCode = getShaderCode(cPSGuardHurt);
		addPixelShader(shaderCode);

		shaderCode = getShaderCode(cPSInvincibility);
		addPixelShader(shaderCode);
	}

	private String getShaderCode(String[] lines) {
		StringBuilder result = new StringBuilder();
		for (String l : lines) {
			result.append(l);
			result.append((char) 13).append((char) 10);
		}
		return result.toString();
	}

	// /////////////////////////////////////////////////////////////////////////////////////
	// addPixelShader
	// /////////////////////////////////////////////////////////////////////////////////////
	private void addPixelShader(String strData) {
		int programObject = doCreateShader(strData, true);
		// Uniform values

		tabPixelShaders[n_PixelShaders] = programObject;
		n_PixelShaders++;
	}
}
