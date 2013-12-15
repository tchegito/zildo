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

package zildo.fwk.gfx;

import java.nio.ByteBuffer;

import zildo.client.ClientEngineZildo;
import zildo.fwk.ZUtils;
import zildo.monde.util.Vector3f;
import zildo.monde.util.Vector4f;


public abstract class PixelShaders {
	
	//////////////////////////////////////////////////////////////////////
	// Construction/Destruction
	//////////////////////////////////////////////////////////////////////
	
	protected int n_PixelShaders;
	protected int[] tabPixelShaders;
	private ByteBuffer buff=ZUtils.createByteBuffer(256);	// Used for setParameter
	
	public PixelShaders()
	{
		n_PixelShaders=0;
		tabPixelShaders=new int[8];
	}
	
	public void cleanUp() {
		for (int i=0;i<n_PixelShaders;i++) {
			int id=tabPixelShaders[i];
			deletePixelShader(id);
		}
	}
	
	protected abstract void deletePixelShader(int id);
	
	public int getPixelShader(int n) {
		return tabPixelShaders[n];
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// canDoPixelShader
	///////////////////////////////////////////////////////////////////////////////////////
	public abstract boolean canDoPixelShader();
	
	///////////////////////////////////////////////////////////////////////////////////////
	// preparePixelShader
	///////////////////////////////////////////////////////////////////////////////////////
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
			"	if (ceil(texel.w*2.0) == 2.0*Color1.w && texel.xyz==Color1.xyz)", // && ceil(texel.y*2)==2.0f*Color1.y && ceil(texel.z*2)==2.0f*Color1.z)", //== Color1.w)",
			"		gl_FragColor = Color3;",
			"	else if (ceil(texel.w*2.0) == 2.0*Color2.w && texel.xyz==Color2.xyz)",
			"		gl_FragColor = Color4;",
			"	else gl_FragColor=texel * curColor;",
			"}"};
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
			
			String cPSInvincibility[] = {
					"uniform vec4 factor;",
					"uniform sampler2D tex;",
					"const float blurSize = 1.0/256.0;",
					"void main (void) {",
					"	vec4 texel=texture2D(tex, gl_TexCoord[0].st);",
					"   float gray = dot(vec3(texel),vec3(0.3, 0.59, 0.11));",
					"   gray = clamp(gray * (factor.x * 4.0), 0.0, 1.0);",
					"   gl_FragColor = vec4(gray ,gray, 0, texel.w);",
					"}"};
		String shaderCode;
		shaderCode=getShaderCode(cPSGuard);
		addPixelShader(shaderCode);
		
		shaderCode=getShaderCode(cPSGuardHurt);
		addPixelShader(shaderCode);
		
		shaderCode=getShaderCode(cPSInvincibility);
		addPixelShader(shaderCode);
	}
	
	private String getShaderCode(String[] lines) {
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
	private void addPixelShader(String strData)
	{
		int programObject = doCreateShader(strData, true);
		// Uniform values
		

		tabPixelShaders[n_PixelShaders]=programObject;
		n_PixelShaders++;
	}
	
	/**
	 * Create and compile a shader, from kind depending of the given boolean.
	 * @param strData shader's code
	 * @param pixel TRUE=fragment (pixel) / FALSE=vertex
	 * @return int
	 */
	protected abstract int doCreateShader(String strData, boolean pixel);
	
	private ByteBuffer toByteString(String str, boolean isNullTerminated)
	{
		// Reuse the same buffer
		buff.position(0);
		buff.limit(buff.capacity());
		buff.put( str.getBytes() );
 
		if (isNullTerminated)
			buff.put( (byte)0 );
 
		buff.flip();
		return buff;
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

	private static Vector4f darkColor=new Vector4f(0,0,0,0);
	private static Vector4f brightColor=new Vector4f(0,0,0,0);
	private static Vector4f colorReplace1=new Vector4f(1,0,0.0f,0.5f);
	private static Vector4f colorReplace2=new Vector4f(0,1.0f,0,0.5f);

	Vector4f coeffedDark = new Vector4f(1, 1, 1, 1);
	Vector4f coeffedBright = new Vector4f(1, 1, 1, 1);
	
	public Vector4f[] getConstantsForSpecialEffect(EngineFX specialEffect)
	{
		if (specialEffect.darkColor != null) {
			darkColor = specialEffect.darkColor;
			brightColor = specialEffect.brightColor;
		}
		Vector3f coeff = ClientEngineZildo.ortho.getAmbientColor();
		coeffedDark.setAndScale3(darkColor, coeff);
		coeffedBright.setAndScale3(brightColor, coeff);
		
		Vector4f[] tab={coeffedBright, coeffedDark, colorReplace1, colorReplace2};

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
		doSetParameter(psId, name, color);
	}
	
	protected abstract void doSetParameter(int psId, ByteBuffer name, Vector4f color);
}