/**
 * Legend of Zildo
 * Copyright (C) 2006-2010 Evariste Boussaton
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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;
import org.newdawn.slick.Font;

import zildo.fwk.opengl.OpenGLStuff;
import zildo.prefs.Constantes;


/**
 * Classe qui permet de dessiner sur un buffer.
 * 
 * On ne pourra pas l'utiliser pour dessiner à l'écran directement.
 * Pour ça, voir (@link Ortho).
 * 
 * @author tchegito
 *
 */


public class GFXBasics extends OpenGLStuff {


		//Variables
	private Font pFont;						// Fontes crée en init
	private ByteBuffer pBackBuffer;
	private static final Vector4f[] palette=new Vector4f[256];				// Palette from zildo
	//private D3DLOCKED_RECT rectLock;
	//DWORD* pEcran;
	private int pitch;
	private int width;
	private int height;

	boolean alpha;
	
	
	//////////////////////////////////////////////////////////////////////
	// Construction/Destruction
	//////////////////////////////////////////////////////////////////////
	
	//private static java.awt.Font[] fonts;
	
	static {
		// Load fonts disponibility at startup
		//GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		//fonts = ge.getAllFonts();
		
		// Default palette
		Load_Palette("GAME1.PAL");
	}
	
	public GFXBasics(boolean alpha)
	{
		this.alpha=alpha;
		// On initialise les fontes
		/**
		HFONT* pFont=new HFONT;
	    *pFont         = CreateFont( 8, 0, 0, 0, 0,0,	// les derniers : bold, italic
	                                 FALSE, FALSE, DEFAULT_CHARSET, OUT_DEFAULT_PRECIS,
	                                 CLIP_DEFAULT_PRECIS, DRAFT_QUALITY,
	                                 DEFAULT_PITCH, null); //"Times new roman" );
	
		*/

		// Gourmand : ca rajoute 4 secondes
	    //pFont=new TrueTypeFont(fonts[0], true);

	
		pBackBuffer=null;
	}
	
	void setFonts(Font font)
	{
		pFont=font;
		// On crée la fonte
		/*
		D3DXCreateFont(pD3DDevice9, 8, 0, 0, 0, FALSE,
				DEFAULT_CHARSET, OUT_DEFAULT_PRECIS,
		 DRAFT_QUALITY, DEFAULT_PITCH, "Times new roman", &m_pFont); //"Times new roman" );
		 */
	}
	
	public void SetBackBuffer(ByteBuffer surface, int width, int height)
	{
		pBackBuffer=surface;
		// Get the width/height for displaying right proportions
		this.width=width;
		this.height=height;
		this.pitch=4;	// One pixel size in memory (R, G, B, A)
	}
	
	static void Load_Palette(String fileName)
	{
		// Load the palette
		String filename=Constantes.DATA_PATH;
		filename+=fileName;
		File fi=new File(filename);
		try {
			InputStream stream=new FileInputStream(fi);
			int a,b,c;
			for (int i=0;i<256;i++) {
				a=stream.read();
				b=stream.read();
				c=stream.read();
				palette[i]=new Vector4f(a, b, c, 1);
			}
			stream.close();
		} catch (IOException e) {
			throw new RuntimeException("Unable to read palette");
		}
	}
	
	public void finalize()
	{
		//SafeDelete(m_pFont);
		//SafeRelease(pBackBuffer);
	}
	
	// All these functions assumed that BeginScene has been called
	
	///////////////////////////////////////////////////////////////////////////////////////
	// StartRendering
	///////////////////////////////////////////////////////////////////////////////////////
	public void StartRendering()
	{
		/*
		RECT r={0,0,width,height};
		HRESULT hr=pBackBuffer.LockRect(&rectLock,&r,0);
		pEcran=(DWORD*)rectLock.pBits;
		pitch=rectLock.Pitch /4;
		*/
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// EndRendering
	///////////////////////////////////////////////////////////////////////////////////////
	public void EndRendering() {
		// pBackBuffer.UnlockRect();
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// aff_texte
	///////////////////////////////////////////////////////////////////////////////////////
	public void aff_texte(int afx,int afy,String phrase, long color, boolean palettized) {
		/*
		if (color == -1) {
			color=D3DCOLOR_COLORVALUE(10,10,10,0);
		} else if (palettized) {
			color=palette[color];
			color|=(255<<24);
		}
		RECT rr={afx,afy,afx+80*phrase.length(),afy+80};
	    //STDMETHOD_(INT, DrawTextW)(THIS_ LPD3DXSPRITE pSprite, LPCWSTR pString, INT Count, LPRECT pRect, DWORD Format, D3DCOLOR Color) PURE;
	
		m_pFont.DrawText(null, phrase.Convchar(),phrase.length(),&rr,0,color);
		*/
		//pFont.drawString(afx, afy,phrase);
		//System.out.println(phrase);
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// copyFromRenderedSurface
	///////////////////////////////////////////////////////////////////////////////////////
	// Copy the rendered surface on this one.
	///////////////////////////////////////////////////////////////////////////////////////
	void copyFromRenderedSurface() {
		/*
			IDirect3DSurface9* backBufferForText;
			backBufferForText=dx9Gestion.gfxBasics.pBackBuffer;
	
			D3DXLoadSurfaceFromSurface(pBackBuffer,
					null,null,
					backBufferForText,
					null,null,
					D3DX_FILTER_NONE,D3DCOLOR(palette[0]));
					*/
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// getTextSize
	///////////////////////////////////////////////////////////////////////////////////////
	// Return a RECT structure with 'bottom' and 'right' properties set.
	///////////////////////////////////////////////////////////////////////////////////////
	public Vector2f getTextSize(String phrase) {
		/*
		RECT rr={0,0,1,1};
		m_pFont.DrawText(null, phrase.Convchar(),phrase.length(),&rr,DT_CALCRECT,null);
		return rr;
		*/
		return null;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// box
	///////////////////////////////////////////////////////////////////////////////////////
	// Draw a filled box
	// colorIndex : (index in the palette [0..256] 
	// colorValue : Vector (R, G, B)
	///////////////////////////////////////////////////////////////////////////////////////
	public void box(int xx, int yy, int wx, int wy, int colorIndex, Vector4f colorValue) {
	
		int a=yy*width*pitch + xx * pitch;
		Vector4f color=colorValue;
		if (colorValue == null) {
			color=palette[colorIndex];
		}
		for (int i=0;i<wy;i++)
		{
			for (int j=0;j<wx;j++)
			{
				pBackBuffer.put(a,(byte) color.x);
				pBackBuffer.put(a+1,(byte) color.y);
				pBackBuffer.put(a+2,(byte) color.z);
				if (alpha) {
					pBackBuffer.put(a+3, (byte) color.w);
				}
				a++;
			}
			a+=pitch*(width-wx);
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// boxv
	///////////////////////////////////////////////////////////////////////////////////////
	// Draw an empty box.
	///////////////////////////////////////////////////////////////////////////////////////
	public void boxv(int xx,int yy,int wx,int wy,int colorIndex, Vector4f colorValue) {
	
		int a=yy*width*pitch + xx * pitch;
		Vector4f color=colorValue;
		if (colorValue == null) {
			color=palette[colorIndex];
		}
		for (int i=0;i<wy;i++)
		{
			if (i==0 || i==wy-1)
			{
				for (int j=0;j<wx;j++)
				{
					pBackBuffer.put(a,(byte) color.x);
					pBackBuffer.put(a+1,(byte) color.y);
					pBackBuffer.put(a+2,(byte) color.z);
					if (alpha) {
						pBackBuffer.put(a+3, (byte) colorValue.w);
					}
					a++;
				}
				a+=pitch*(width-wx);
			}
			else {
				pBackBuffer.put(a,(byte) color.x);
				pBackBuffer.put(a+1,(byte) color.y);
				pBackBuffer.put(a+2,(byte) color.z);
				if (alpha) {
					pBackBuffer.put(a+3, (byte) color.w);
				}
				pBackBuffer.put(a + pitch*(wx-1),(byte) color.x);
				pBackBuffer.put(a + pitch*(wx-1)+1,(byte) color.y);
				pBackBuffer.put(a + pitch*(wx-1)+2,(byte) color.z);
				if (alpha) {
					pBackBuffer.put(a + pitch*(wx-1)+3, (byte) color.w);
				}
				a+=pitch;
			}
	
		}
	
	}
	
	public Vector4f getPixel(int x, int y) {
		int a=y*width*pitch + x * pitch;
		byte r=pBackBuffer.get(a);
		byte g=pBackBuffer.get(a+1);
		byte b=pBackBuffer.get(a+2);
		byte w=pBackBuffer.get(a+3);
		Vector4f result=new Vector4f(r, g, b, w);
		return result;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// outlineBox
	///////////////////////////////////////////////////////////////////////////////////////
	// -outline a zone
	///////////////////////////////////////////////////////////////////////////////////////
	public void outlineBox(int xx,int yy,int wx, int wy, int colorIndex1, int colorIndex2, Vector4f colorValue1, Vector4f colorValue2) {
		int a=yy*width*pitch + xx * pitch;
		Vector4f col1;
		Vector4f col2;
		if (colorValue1 == null) {
			col1=palette[colorIndex1];
			col2=palette[colorIndex2]; // + (255 << 24);	==> on met A=255
		} else {
			col1=colorValue1;				// & 0xffffff;   ==> on supprime le membre A
			col2=colorValue2;
		}
		for (int i=0;i<wy;i++)
		{
			if (i>0 && i<(wy-1))
			{
				for (int j=0;j<wx;j++)
				{
					if (j>0 && j<(wx-1)) {
						Vector4f colorPixel=getPixel(xx+j, yy+i);
						if ( colorPixel.equals(col1)) {
	
							if ( !getPixel(xx+j-1, yy+i).equals(col1))
								pset(xx+j-1, yy+i,col2);
							if ( !getPixel(xx+j+1, yy+i).equals(col1))
								pset(xx+j+1, yy+i,col2);
							if ( !getPixel(xx+j, yy+i-1).equals(col1))
								pset(xx+j, yy+i-1,col2);
							if ( !getPixel(xx+j, yy+i+1).equals(col1))
								pset(xx+j, yy+i+1,col2);
						}
					}
					a++;
				}
				a+=pitch-wx;
			}
	
		}
	
	}
	
	// Put a pixel on the screen at desired location, with mask consideration
	// If the fourth parameter is not zero-valued, this is the RGBA color without
	// palettized mode. (useful for blue/green guard)
	public void pset(int xx,int yy,int colorIndex, Vector4f colorValue) {
		// Check that pixel coordinates is inside screen
		if (xx>=0 && xx<=width && yy>=0 && yy<=height)
		{
			Vector4f color=new Vector4f(palette[colorIndex]);
			if (colorValue != null) {
				color=colorValue;
			} else {
				//if (colorIndex!=255)
					// Enable mask display with alpha key = 0
					color.w=255.0f; //color.x; //|=(255 << 24);
			}
			pset(xx,yy, color);
		}
	}
	
	// Basic pset
	public void pset(int xx,int yy,Vector4f colorValue) {
		// Check that pixel coordinates is inside screen
		if (xx>=0 && xx<=width && yy>=0 && yy<=height)
		{
			int a=yy*width*pitch + xx * pitch;
			pBackBuffer.put(a,(byte) colorValue.x);
			pBackBuffer.put(a+1,(byte) colorValue.y);
			pBackBuffer.put(a+2,(byte) colorValue.z);
			if (alpha) {
				pBackBuffer.put(a+3, (byte) colorValue.w);
			}
		}
	}

	public Font getFont() {
		return pFont;
	}

	public void setFont(Font font) {
		pFont = font;
	}
	
	public static Vector4f getColor(int palIndex) {
		return palette[palIndex];
	}
}