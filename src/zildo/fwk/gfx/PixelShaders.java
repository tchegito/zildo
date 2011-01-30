/**
 * Legend of Zildo
 * Copyright (C) 2006-2011 Evariste Boussaton
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

package zildo.fwk.gfx;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBFragmentShader;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.Util;
import org.lwjgl.util.vector.Vector4f;

import zildo.fwk.opengl.OpenGLStuff;


public class PixelShaders extends OpenGLStuff {
	
	//////////////////////////////////////////////////////////////////////
	// Construction/Destruction
	//////////////////////////////////////////////////////////////////////
	
	public enum EngineFX {
		NO_EFFECT,
		GUARD_BLUE,GUARD_RED, GUARD_YELLOW, GUARD_BLACK, GUARD_GREEN, GUARD_PINK,
		PERSO_HURT, 
		FONT_NORMAL, FONT_HIGHLIGHT,
		SHINY, QUAD,
		FOCUSED;	// FOCUSED is used when we wants to highlight some entity (inventory, or buying something)
		
		public boolean needPixelShader() {
			return !(this==NO_EFFECT || this==SHINY || this==QUAD || this==FOCUSED);
		}
		
		public EngineFX fromInt(int i) {
			return EngineFX.values()[i];
		}
	}
    
	private int n_PixelShaders;
	private int[] tabPixelShaders;
	private ByteBuffer buff=BufferUtils.createByteBuffer(256);	// Used for setParameter
	
	public PixelShaders()
	{
		n_PixelShaders=0;
		tabPixelShaders=new int[8];
	}
	
	public void cleanUp() {
		for (int i=0;i<n_PixelShaders;i++) {
			int id=tabPixelShaders[i];
			ARBShaderObjects.glDeleteObjectARB(id);
		}
	}
	
	public int getPixelShader(int n) {
		return tabPixelShaders[n];
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// canDoPixelShader
	///////////////////////////////////////////////////////////////////////////////////////
	public boolean canDoPixelShader() {
        return GLContext.getCapabilities().GL_ARB_shader_objects &&
        GLContext.getCapabilities().GL_ARB_fragment_shader &&
        GLContext.getCapabilities().GL_ARB_vertex_shader &&
        GLContext.getCapabilities().GL_ARB_shading_language_100;

	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// preparePixelShader
	///////////////////////////////////////////////////////////////////////////////////////
	public boolean preparePixelShader() {
	
			String cPSGuard[] = {
			"uniform vec4 Color1;",
			"uniform vec4 Color2;",
			"uniform vec4 Color3;",
			"uniform vec4 Color4;",
			"uniform sampler2D tex;",
			"void main (void) {",
			"	vec4 texel=texture2D(tex, gl_TexCoord[0].st);",
			"	if (ceil(texel.w*2.0) == 2.0*Color1.w && texel.xyz==Color1.xyz)", // && ceil(texel.y*2)==2.0f*Color1.y && ceil(texel.z*2)==2.0f*Color1.z)", //== Color1.w)",
			"		gl_FragColor = Color3;",
			"	else if (ceil(texel.w*2.0) == 2.0*Color2.w && texel.xyz==Color2.xyz)",
			"		gl_FragColor = Color4;",
			"	else gl_FragColor=texel;",
			"}"};
/*
			{"!!ARBfp1.0",
							"dcl t0.xy",
							"dcl_2d s0",
							//"def c1, 0.0, 0.0, -1.0, -0.5",	// Couleur qu'on cherche (*-1)
							//"def c2, 0.6, 0.6, 0.6, 1.0",	// Couleur à mettre à la place
							//"def c3, 0.0, -1.0, 0.0, -0.5",	// Couleur qu'on cherche (*-1)
							//"def c4, 0.3, 0.3, 0.3, 1.0",	// Couleur à mettre à la place
							"texld r0, t0, s0",		// On a le pixel de la texture en r0
							"mov r2, r0",			// On sauve cette valeur
							"add r1, r0, c1",		// On prend la différence
							"dp3 r1, r1, r1",		// On somme RGB sur une seule composante
							"cmp r0, r1, c2, r2",
							"cmp r0, -r1, c2, r2",
							"mov r2, r0",			// On resauve au cas où la 1ère substitution a été faite
							"add r1, r0, c3",
							"dp3 r1, r1, r1",
							"cmp r0, r1, c4, r2",
							"cmp r0, -r1, c4, r2",
							"mov oC0, r0"};
	*/
			String cPSGuardHurt[] = {
					"uniform vec4 randomColor;",
					"uniform sampler2D tex;",
					"void main (void) {",
					"	vec4 texel=texture2D(tex, gl_TexCoord[0].st);",
					"	if (texel.w != 0.0) {",
					"		gl_FragColor = randomColor- texel / 2.0;",
					"		gl_FragColor.w = 1.0;",
					"	}",
					"}"};
			/*
					"ps_2_0",
							"dcl t0.xy",
							"dcl_2d s0",
							"def c2, 0.1, 0.0, 0.0, 0.0",
							"texld r0, t0, s0",		// On a le pixel de la texture en r0
							"add r1, r0, c1",		// C1 doit contenir un random (R,G,B,0)
							//"sub r2, r2.a, c2.x",
							"mov r2.rgba, r0.aaaa",
							"cmp r0, r2, r1, r0",
							"mov oC0, r0"};
	
							
		/*
			(a,b,c,1) comparé à (0,0,1,0)
			x=y si (x>=y && y>=x)
				   (x-y>=0 && y-x>=0)
	
			cmp a,b,c,d ==> a= c si b>=0
							   d si b<0
	
				   Ce qu'on veut :
					-en entrée on a 'c' = couleur du pixel à afficher
					-s'il est égal à 'v', on le remplace par c2
	
					1) c==v ssi c>=v && c<=v
					   c!=v ssi c<v || c>v
					2) on va poser r1=c-v et r0=c
					3) on va faire 2 comparaisons
						a/ cmp r0,r1,c1,r0
							  r0 reste inchangé si r1<0
												== (c-v)<0
												== c<v
						b/ cmp r0,-r1,c1,r0
							  r0 reste inchangé si -r1<0
											    == v-c<0
												== v<c
	
			cmp r0,r1,c1,r2
			cmp r0,-r1,c1,r2
	*/
		String shaderCode;
		shaderCode=getShaderCode(cPSGuard);
		addPixelShader(shaderCode);
		
		shaderCode=getShaderCode(cPSGuardHurt);
		addPixelShader(shaderCode);
	
		return true;
	}
	
	public String getShaderCode(String[] lines) {
		StringBuilder result=new StringBuilder();
		for (String l : lines) {
			result.append(l);
			result.append((char)13).append((char)10);
		}
		return result.toString();
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// addPixelShader
	///////////////////////////////////////////////////////////////////////////////////////
	void addPixelShader(String strData)
	{
		ByteBuffer shaderPro = BufferUtils.createByteBuffer(strData.length());
		 
		byte[] shaderBytes=new byte[strData.length()];
		for (int i=0;i<strData.length();i++) {
			shaderBytes[i]=(byte) strData.charAt(i);
		}
		shaderPro.put(shaderBytes);
		shaderPro.flip();

		// Create pixel shader
		int vertexShader= ARBShaderObjects.glCreateShaderObjectARB(ARBFragmentShader.GL_FRAGMENT_SHADER_ARB);
		ARBShaderObjects.glShaderSourceARB(vertexShader, shaderPro);
		
		// Compile, link, validate
		ARBShaderObjects.glCompileShaderARB(vertexShader);
		
		int programObject  = ARBShaderObjects.glCreateProgramObjectARB();
		ARBShaderObjects.glAttachObjectARB(programObject, vertexShader);
		
		ARBShaderObjects.glLinkProgramARB(programObject);
		ARBShaderObjects.glValidateProgramARB(programObject);
		
		// Uniform values
		
		printLogInfo(vertexShader);
		tabPixelShaders[n_PixelShaders]=programObject;
		n_PixelShaders++;
	}
	
	private ByteBuffer toByteString(String str, boolean isNullTerminated)
	{
		int length = str.length();
		if (isNullTerminated)
			length++;
		// Reuse the same buffer
		buff.position(0);
		buff.limit(buff.capacity());
		buff.put( str.getBytes() );
 
		if (isNullTerminated)
			buff.put( (byte)0 );
 
		buff.flip();
		return buff;
	}
	
	private void printLogInfo(int obj)
	{
		IntBuffer iVal = BufferUtils.createIntBuffer(1);
		ARBShaderObjects.glGetObjectParameterARB(obj, ARBShaderObjects.GL_OBJECT_INFO_LOG_LENGTH_ARB, iVal);
 
		int length = iVal.get();
		if (length > 0)
		{
			// We have some info we need to output.
			ByteBuffer infoLog = BufferUtils.createByteBuffer(length);
			iVal.flip();
			ARBShaderObjects.glGetInfoLogARB(obj,  iVal, infoLog);
			byte[] infoBytes = new byte[length];
			infoLog.get(infoBytes);
			String out = new String(infoBytes);
 
			System.out.println("Info log:\n"+out);
		}
 
		Util.checkGLError();
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// getConstantsForSpecialEffect
	///////////////////////////////////////////////////////////////////////////////////////
	// -used for color replacement on the guard
	//  *he can be red, yellow, black, green, pink or blue.
	//  *just with one texture we draw all these colors ! Marvellous pixel shaders !
	// How does it works :
	// colorReplace1 will be replaced by brightColor
	// colorReplace2 will be replaced by darkColor
	///////////////////////////////////////////////////////////////////////////////////////

	public Vector4f[] getConstantsForSpecialEffect(EngineFX specialEffect)
	{
		Vector4f darkColor=new Vector4f(0,0,0,0);
		Vector4f brightColor=new Vector4f(0,0,0,0);
		Vector4f colorReplace1=new Vector4f(1,0,0.0f,0.5f);
		Vector4f colorReplace2=new Vector4f(0,1.0f,0,0.5f);
	
		switch (specialEffect) {
			case GUARD_RED:
				darkColor=  createColor64(46,16,28);
				brightColor=createColor64(60,30,32);
				break;
			case GUARD_YELLOW:
				darkColor=  createColor64(52,48,16);
				brightColor=createColor64(60,54,16);
				break;
			case GUARD_BLACK:
				darkColor=  createColor64(22,22,22);
				brightColor=createColor64(30,30,34);
				break;
			case GUARD_GREEN:
				darkColor=  createColor64(10,30,14);
				brightColor=createColor64(30,46,8);
				break;
			case GUARD_PINK:
				darkColor=  createColor64(58,24,44);
				brightColor=createColor64(62,32,44);
				break;
			case GUARD_BLUE:
				darkColor=  createColor64(20,28,50);
				brightColor=createColor64(44,36,62);
				break;
			case FONT_NORMAL:	// Blue and white
				darkColor=  createColor64(0,0,28);
				brightColor=createColor64(62,62,62);
				break;
			case FONT_HIGHLIGHT:	// Yellow set
				darkColor=  createColor64(8,16,28);
				brightColor=createColor64(60,54,16);
				break;
			default:
			case NO_EFFECT:
				;
		}
	
		Vector4f[] tab={brightColor, darkColor, colorReplace1, colorReplace2};

		return tab;
	}

	/**
	 * Set pixel shader parameter.
	 * @param pixelShaderId
	 * @param uniformName
	 * @param color
	 */
	public void setParameter(int pixelShaderIndex, String uniformName, Vector4f color) {
		ByteBuffer name = toByteString(uniformName, true);
		int psId=tabPixelShaders[pixelShaderIndex];
		int location = ARBShaderObjects.glGetUniformLocationARB(psId, name);
		ARBShaderObjects.glUniform4fARB(location, color.x, color.y, color.z, color.w);
	}
}