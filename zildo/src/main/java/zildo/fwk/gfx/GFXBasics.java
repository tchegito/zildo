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

package zildo.fwk.gfx;

import java.nio.ByteBuffer;

import zildo.Zildo;
import zildo.fwk.file.EasyBuffering;
import zildo.monde.util.Vector4f;

/**
 * Classe qui permet de dessiner sur un buffer.
 * 
 * On ne pourra pas l'utiliser pour dessiner à l'écran directement. Pour ça,
 * voir (@link Ortho).
 * 
 * @author tchegito
 * 
 */

public class GFXBasics {

	// Variables
	private ByteBuffer pBackBuffer;
	
	private static Vector4f[] palette;
	
	private int pitch;
	private int width;
	private int height;

	boolean alpha;
	byte[] bytes;
	
	// ////////////////////////////////////////////////////////////////////
	// Construction/Destruction
	// ////////////////////////////////////////////////////////////////////

	// private static java.awt.Font[] fonts;

	static {
		// Default palette	(Is it really useful on Android to load palettes ???)
		palette = loadPalette("game1.pal");
	}

	public GFXBasics(boolean alpha) {
		this.alpha = alpha;

		pBackBuffer = null;
		bytes = new byte[alpha ? 4 : 3];
	}

	public void setBackBuffer(ByteBuffer surface, int w, int h, boolean alpha) {
		pBackBuffer = surface;
		// Get the width/height for displaying right proportions
		this.width = w;
		this.height = h;
		this.pitch = 4; // One pixel size in memory (R, G, B, A)
		this.alpha = alpha;
	}

	static Vector4f[] loadPalette(String fileName) {
		// Load the palette
		EasyBuffering file = Zildo.pdPlugin.openFile(fileName);
		
		Vector4f[] pal = new Vector4f[256];
		int a, b, c;
		for (int i = 0; i < 256; i++) {
			a = file.readUnsignedByte();
			b = file.readUnsignedByte();
			c = file.readUnsignedByte();
			pal[i] = new Vector4f(a, b, c, 1);
		}
		
		return pal;
	}

	// All these functions assumed that BeginScene has been called

	// /////////////////////////////////////////////////////////////////////////////////////
	// StartRendering
	// /////////////////////////////////////////////////////////////////////////////////////
	public void StartRendering() {
		/*
		 * RECT r={0,0,width,height}; HRESULT
		 * hr=pBackBuffer.LockRect(&rectLock,&r,0);
		 * pEcran=(DWORD*)rectLock.pBits; pitch=rectLock.Pitch /4;
		 */
	}

	// /////////////////////////////////////////////////////////////////////////////////////
	// EndRendering
	// /////////////////////////////////////////////////////////////////////////////////////
	public void EndRendering() {
		// pBackBuffer.UnlockRect();
	}

	// /////////////////////////////////////////////////////////////////////////////////////
	// box
	// /////////////////////////////////////////////////////////////////////////////////////
	// Draw a filled box
	// colorIndex : (index in the palette [0..256]
	// colorValue : Vector (R, G, B)
	// /////////////////////////////////////////////////////////////////////////////////////
	public void box(int xx, int yy, int wx, int wy, int colorIndex, Vector4f colorValue) {

		int a = yy * width * pitch + xx * pitch;
		Vector4f color = colorValue;
		if (colorValue == null) {
			color = palette[colorIndex];
		}
		for (int i = 0; i < wy; i++) {
			for (int j = 0; j < wx; j++) {
				pBackBuffer.put(a, (byte) color.x);
				pBackBuffer.put(a + 1, (byte) color.y);
				pBackBuffer.put(a + 2, (byte) color.z);
				if (alpha) {
					pBackBuffer.put(a + 3, (byte) color.w);
					a++;
				}
				a+=3;
			}
			a += pitch * (width - wx);
		}
	}

	public void clear(Vector4f color) {
		
		for (int a=0;a< 256*256; a++) {
			pBackBuffer.put((byte) color.x);
			pBackBuffer.put((byte) color.y);
			pBackBuffer.put((byte) color.z);
			if (alpha) {
				pBackBuffer.put((byte) color.w);
			}
		}
	}
	// /////////////////////////////////////////////////////////////////////////////////////
	// boxv
	// /////////////////////////////////////////////////////////////////////////////////////
	// Draw an empty box.
	// /////////////////////////////////////////////////////////////////////////////////////
	public void boxv(int xx, int yy, int wx, int wy, int colorIndex, Vector4f colorValue) {

		int a = yy * width * pitch + xx * pitch;
		Vector4f color = colorValue;
		if (colorValue == null) {
			color = palette[colorIndex];
		}
		for (int i = 0; i < wy; i++) {
			if (i == 0 || i == wy - 1) {
				for (int j = 0; j < wx; j++) {
					pBackBuffer.put(a, (byte) color.x);
					pBackBuffer.put(a + 1, (byte) color.y);
					pBackBuffer.put(a + 2, (byte) color.z);
					if (alpha) {
						pBackBuffer.put(a + 3, (byte) colorValue.w);
						a++;
					}
					a+=3;
				}
				a += pitch * (width - wx);
			} else {
				pBackBuffer.put(a, (byte) color.x);
				pBackBuffer.put(a + 1, (byte) color.y);
				pBackBuffer.put(a + 2, (byte) color.z);
				if (alpha) {
					pBackBuffer.put(a + 3, (byte) color.w);
				}
				pBackBuffer.put(a + pitch * (wx - 1), (byte) color.x);
				pBackBuffer.put(a + pitch * (wx - 1) + 1, (byte) color.y);
				pBackBuffer.put(a + pitch * (wx - 1) + 2, (byte) color.z);
				if (alpha) {
					pBackBuffer.put(a + pitch * (wx - 1) + 3, (byte) color.w);
				}
				a += pitch;
			}

		}

	}

	public Vector4f getPixel(int x, int y) {
		int a = y * width * pitch + x * pitch;
		short r = (short) (0xFF & pBackBuffer.get(a));
		short g = (short) (0xFF & pBackBuffer.get(a + 1));
		short b = (short) (0xFF & pBackBuffer.get(a + 2));
		short w = (short) (0xFF & pBackBuffer.get(a + 3));
		Vector4f result = new Vector4f(r, g, b, w);
		return result;
	}

	// /////////////////////////////////////////////////////////////////////////////////////
	// outlineBox
	// /////////////////////////////////////////////////////////////////////////////////////
	// -outline a zone
	// /////////////////////////////////////////////////////////////////////////////////////
	public void outlineBox(int xx, int yy, int wx, int wy, int colorIndex1, int colorIndex2, Vector4f colorValue1,
			Vector4f colorValue2) {
		Vector4f col1;
		Vector4f col2;
		if (colorValue1 == null) {
			col1 = palette[colorIndex1];
			col2 = palette[colorIndex2]; // + (255 << 24); ==> on met A=255
		} else {
			col1 = colorValue1; // & 0xffffff; ==> on supprime le membre A
			col2 = colorValue2;
		}
		for (int i = 0; i < wy; i++) {
			if (i > 0 && i < (wy - 1)) {
				for (int j = 0; j < wx; j++) {
					if (j > 0 && j < (wx - 1)) {
						Vector4f colorPixel = getPixel(xx + j, yy + i);
						if (colorPixel.equals(col1)) {

							if (!getPixel(xx + j - 1, yy + i).equals(col1)) {
								pset(xx + j - 1, yy + i, col2);
							}
							if (!getPixel(xx + j + 1, yy + i).equals(col1)) {
								pset(xx + j + 1, yy + i, col2);
							}
							if (!getPixel(xx + j, yy + i - 1).equals(col1)) {
								pset(xx + j, yy + i - 1, col2);
							}
							if (!getPixel(xx + j, yy + i + 1).equals(col1)) {
								pset(xx + j, yy + i + 1, col2);
							}
						}
					}
				}
			}

		}

	}

	Vector4f colPset = new Vector4f(0, 0, 0, 0);
	
	// Put a pixel on the screen at desired location, with mask consideration
	// If the fourth parameter is not zero-valued, this is the RGBA color
	// without
	// palettized mode. (useful for blue/green guard)
	// NOTE: this method alterates the position in pBackBuffer
	public void pset(int xx, int yy, int colorIndex, Vector4f colorValue) {
		// Check that pixel coordinates is inside screen
		if (xx >= 0 && xx <= width && yy >= 0 && yy <= height) {
			if (colorValue != null) {
				colPset.set(colorValue);
			} else {
				// if (colorIndex!=255)
				// Enable mask display with alpha key = 0
				colPset.set(palette[colorIndex]);
				colPset.w = 255.0f; // color.x; //|=(255 << 24);
			}
			pset(xx, yy, colPset);
		}
	}
	
	// Basic pset
	// NOTE: this method alterates the position in pBackBuffer
	public void pset(int xx, int yy, Vector4f colorValue) {
		// Check that pixel coordinates is inside screen
		if (xx >= 0 && xx <= width && yy >= 0 && yy <= height) {
			int a = yy * width * pitch + xx * pitch;
			if (alpha) {
				putBytes(a, colorValue.x, colorValue.y, colorValue.z, colorValue.w);
			} else {
				putBytes(a, colorValue.x, colorValue.y, colorValue.z);
			}
		}
	}

	private void putBytes(int pos, float... floats) {
		pBackBuffer.position(pos);
		putBytesNext(floats);
	}
	
	private void putBytesNext(float...floats) {
		for (int i=0;i<floats.length;i++) {
			bytes[i] = (byte) floats[i];
		}
		pBackBuffer.put(bytes);
	}
	
	public static Vector4f getColor(int palIndex) {
		return palette[palIndex];
	}

	public static int getIntColor(int palIndex) {
		Vector4f v = getColor(palIndex);
		return (int) v.x << 16 | (int) v.y << 8 | (int) v.z;
	}

	public static int readColor(ByteBuffer buffer, int offset) {
		return (buffer.get(offset) & 0xff) << 16 
			| (buffer.get(offset+1) & 0xff) << 8 
			|  buffer.get(offset+2) & 0xff;
	}

	public static int readAlphaColor(ByteBuffer buffer, int offset) {
		return (buffer.get(offset+3) & 0xff) << 24 
				| (buffer.get(offset) & 0xff) << 16 
				| (buffer.get(offset+1) & 0xff) << 8 
				|  buffer.get(offset+2) & 0xff;
	}
	
	public static Vector4f splitRGBA(int value) {
		int r = (value >> 16) & 0xff;
		int g = (value >> 8) & 0xff;
		int b = (value) & 0xff;
		return new Vector4f(r, g, b, (value >> 24) & 0xff);
	}
	
    public static Vector4f createColor256(float r, float g, float b) {
        return new Vector4f(r / 255.0f, g / 255.0f, b / 255.0f, 1.0f);
    }
}