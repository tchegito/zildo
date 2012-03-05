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

package zildo.fwk.gfx;

import java.nio.ByteBuffer;

import zildo.fwk.ZUtils;
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
		ByteBuffer shaderPro = ZUtils.createByteBuffer(strData.length());
		 
		byte[] shaderBytes=new byte[strData.length()];
		for (int i=0;i<strData.length();i++) {
			shaderBytes[i]=(byte) strData.charAt(i);
		}
		shaderPro.put(shaderBytes);
		shaderPro.flip();

		int programObject = doCreatePixelShader(shaderPro);
		// Uniform values
		

		tabPixelShaders[n_PixelShaders]=programObject;
		n_PixelShaders++;
	}
	
	protected abstract int doCreatePixelShader(ByteBuffer shaderPro);
	
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

	public Vector4f[] getConstantsForSpecialEffect(EngineFX specialEffect)
	{
		Vector4f darkColor=new Vector4f(0,0,0,0);
		Vector4f brightColor=new Vector4f(0,0,0,0);
		Vector4f colorReplace1=new Vector4f(1,0,0.0f,0.5f);
		Vector4f colorReplace2=new Vector4f(0,1.0f,0,0.5f);
	
		if (specialEffect.darkColor != null) {
			darkColor = specialEffect.darkColor;
			brightColor = specialEffect.brightColor;
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
		doSetParameter(psId, name, color);
	}
	
	protected abstract void doSetParameter(int psId, ByteBuffer name, Vector4f color);
}