package zildo.fwk.gfx;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import zildo.fwk.GFXBasics;
import zildo.fwk.opengl.OpenGLStuff;

/**
 * Classe qui permet de dessiner directement sur l'écran de rendu dans un contexte orthographique.
 * 
 * C'est-à-dire qu'il n'y a pas de projection. La coordonnée en Z n'est utilisée que pour savoir si un objet est
 * devant ou derrière un autre, mais elle ne met plus les objets en perspective.
 * 
 * 
 * @author tchegito
 *
 */
public class Ortho extends OpenGLStuff {

	static char[][][] fonts=new char[][][]{	//63 fonte
		      {{0,1,1,1,0},{1,0,0,0,1},{1,1,1,1,1},{1,0,0,0,1},{1,0,0,0,1}}, // A
		      {{1,1,1,1,0},{1,0,0,0,1},{1,1,1,1,0},{1,0,0,0,1},{1,1,1,1,0}},
		      {{0,1,1,1,1},{1,0,0,0,0},{1,0,0,0,0},{1,0,0,0,0},{0,1,1,1,1}},
		      {{1,1,1,1,0},{1,0,0,0,1},{1,0,0,0,1},{1,0,0,0,1},{1,1,1,1,0}},
		      {{1,1,1,1,1},{1,0,0,0,0},{1,1,1,0,0},{1,0,0,0,0},{1,1,1,1,1}},
		      {{1,1,1,1,1},{1,0,0,0,0},{1,1,1,0,0},{1,0,0,0,0},{1,0,0,0,0}},
		      {{0,1,1,1,0},{1,0,0,0,0},{1,0,1,1,1},{1,0,0,0,1},{0,1,1,1,0}},
		      {{1,0,0,0,1},{1,0,0,0,1},{1,1,1,1,1},{1,0,0,0,1},{1,0,0,0,1}},
		      {{0,1,1,1,0},{0,0,1,0,0},{0,0,1,0,0},{0,0,1,0,0},{0,1,1,1,0}}, // I
		      {{0,0,0,1,0},{0,0,0,1,0},{1,0,0,1,0},{1,0,0,1,0},{0,1,1,0,0}},
		      {{1,0,0,1,0},{1,0,1,0,0},{1,1,0,0,0},{1,0,1,0,0},{1,0,0,1,0}},
		      {{1,0,0,0,0},{1,0,0,0,0},{1,0,0,0,0},{1,0,0,0,0},{1,1,1,1,1}},
		      {{1,0,0,0,1},{1,1,0,1,1},{1,0,1,0,1},{1,0,0,0,1},{1,0,0,0,1}},
		      {{1,0,0,0,1},{1,1,0,0,1},{1,0,1,0,1},{1,0,0,1,1},{1,0,0,0,1}},
		      {{0,1,1,1,0},{1,0,0,0,1},{1,0,0,0,1},{1,0,0,0,1},{0,1,1,1,0}},
		      {{1,1,1,1,0},{1,0,0,0,1},{1,1,1,1,0},{1,0,0,0,0},{1,0,0,0,0}},
		      {{0,1,1,1,0},{1,0,0,0,1},{1,0,0,0,1},{1,0,0,1,1},{0,1,1,1,1}},
		      {{1,1,1,1,0},{1,0,0,0,1},{1,1,1,1,0},{1,0,0,1,0},{1,0,0,0,1}},
		      {{0,1,1,1,1},{1,0,0,0,0},{0,1,1,1,0},{0,0,0,0,1},{1,1,1,1,0}},
		      {{1,1,1,1,1},{0,0,1,0,0},{0,0,1,0,0},{0,0,1,0,0},{0,0,1,0,0}}, // T
		      {{1,0,0,0,1},{1,0,0,0,1},{1,0,0,0,1},{1,0,0,0,1},{0,1,1,1,0}},
		      {{1,0,0,0,1},{1,0,0,0,1},{0,1,0,1,0},{0,1,0,1,0},{0,0,1,0,0}},
		      {{1,0,0,0,1},{1,0,0,0,1},{1,0,1,0,1},{1,1,0,1,1},{1,0,0,0,1}},
		      {{1,0,0,0,1},{0,1,0,1,0},{0,0,1,0,0},{0,1,0,1,0},{1,0,0,0,1}},
		      {{1,0,0,0,1},{0,1,0,1,0},{0,0,1,0,0},{0,0,1,0,0},{0,0,1,0,0}},
		      {{1,1,1,1,1},{0,0,0,1,0},{0,0,1,0,0},{0,1,0,0,0},{1,1,1,1,1}},
		      {{0,0,0,0,0},{0,0,0,0,0},{0,0,0,0,0},{0,0,0,0,0},{0,0,1,0,0}}, // .
		      {{0,0,0,0,0},{0,0,0,1,0},{0,0,1,0,0},{0,1,0,0,0},{1,0,0,0,0}}, // /
		      {{0,1,1,1,0},{1,0,0,1,1},{1,0,1,0,1},{1,1,0,0,1},{0,1,1,1,0}}, // 0
		      {{0,0,1,0,0},{0,1,1,0,0},{0,0,1,0,0},{0,0,1,0,0},{1,1,1,1,1}},
		      {{0,1,1,1,0},{1,0,0,0,1},{0,0,1,1,0},{0,1,0,0,0},{1,1,1,1,1}},
		      {{1,1,1,1,0},{0,0,0,0,1},{0,1,1,1,0},{0,0,0,0,1},{1,1,1,1,0}},
		      {{1,0,0,1,0},{1,0,0,1,0},{1,1,1,1,1},{0,0,0,1,0},{0,0,0,1,0}},
		      {{1,1,1,1,1},{1,0,0,0,0},{0,1,1,1,0},{0,0,0,0,1},{1,1,1,1,0}},
		      {{0,0,1,1,0},{0,1,0,0,0},{1,1,1,1,0},{1,0,0,0,1},{0,1,1,1,0}},
		      {{1,1,1,1,1},{0,0,0,1,0},{0,0,1,0,0},{0,1,0,0,0},{0,1,0,0,0}},
		      {{0,1,1,1,0},{1,0,0,0,1},{0,1,1,1,0},{1,0,0,0,1},{0,1,1,1,0}},
		      {{0,1,1,1,0},{1,0,0,0,1},{0,1,1,1,1},{0,0,0,1,0},{0,1,1,0,0}}, // 9
		      {{0,0,1,0,0},{0,1,0,0,0},{1,1,1,1,1},{0,1,0,0,0},{0,0,1,0,0}}, // Flèche gauche
		      {{0,0,1,0,0},{0,0,0,1,0},{1,1,1,1,1},{0,0,0,1,0},{0,0,1,0,0}}, // Droite
		      {{0,0,0,0,0},{0,0,1,0,0},{0,0,0,0,0},{0,0,1,0,0},{0,0,0,0,0}},
		      {{0,0,1,0,0},{0,1,0,1,0},{1,0,0,0,1},{0,1,0,1,0},{0,0,1,0,0}},
		      {{1,1,0,0,0},{0,0,1,0,0},{0,0,1,1,1},{0,0,1,0,0},{1,1,0,0,0}},
		      {{0,0,0,0,1},{0,0,0,1,1},{0,0,1,0,1},{0,1,0,0,1},{1,1,1,1,1}}, //p=6
		      {{1,1,1,1,1},{1,0,1,0,1},{1,1,1,1,1},{1,0,1,0,1},{1,1,1,1,1}},
		      {{0,1,1,0,1},{1,0,0,1,0},{0,0,0,0,0},{0,1,1,0,1},{1,0,0,1,0}},
		      {{0,0,1,0,0},{0,0,0,0,0},{1,0,0,0,1},{0,0,0,0,0},{0,0,1,0,0}},
		      {{0,1,0,0,1},{1,0,0,1,1},{1,0,1,0,1},{1,1,0,0,1},{1,0,0,1,0}},
		      {{0,1,0,0,0},{1,0,1,0,0},{0,1,0,0,0},{0,0,0,0,0},{1,1,1,1,1}},
		      {{0,1,1,1,0},{1,0,1,0,1},{0,1,1,1,0},{0,1,0,1,0},{0,0,1,0,0}},
		      {{1,1,1,1,1},{0,1,0,1,0},{0,0,1,0,0},{0,1,0,1,0},{1,1,1,1,1}},
		      {{1,1,1,0,0},{1,0,1,0,0},{0,0,1,1,1},{0,0,1,0,1},{0,0,1,1,1}},
		      {{1,1,1,1,1},{0,0,0,0,1},{1,1,1,0,1},{0,0,1,0,1},{1,0,1,0,1}},
		      {{0,0,1,1,1},{0,1,0,0,0},{0,1,1,1,0},{1,0,0,0,0},{0,1,1,0,0}},
		      {{1,1,1,1,0},{0,0,0,0,1},{1,0,0,0,1},{1,0,0,0,0},{0,1,1,1,1}},
		      {{0,0,1,0,0},{0,0,0,1,0},{1,1,1,1,1},{0,1,0,0,0},{0,0,1,0,0}},
		      {{0,0,0,0,1},{0,0,0,1,0},{1,0,1,0,0},{0,1,0,0,0},{1,0,1,0,0}},
		      {{0,0,1,0,0},{0,0,0,0,0},{1,0,0,0,1},{1,0,0,0,1},{0,1,1,1,0}},
		      {{1,0,0,1,0},{0,1,0,0,0},{0,1,1,0,0},{1,0,0,1,0},{1,0,0,0,0}},
		      {{0,0,1,0,0},{0,1,1,1,0},{0,0,1,0,0},{0,0,1,0,0},{1,1,1,1,1}},
		      {{1,0,1,0,1},{0,1,1,1,0},{1,1,1,1,1},{0,1,1,1,0},{1,0,1,0,1}},
		      {{1,0,0,0,1},{1,0,0,0,1},{0,1,1,1,0},{1,0,0,0,1},{0,1,1,1,0}},
		      {{0,0,0,1,0},{0,0,0,1,0},{0,0,0,0,0},{0,0,0,0,0},{0,0,0,0,0}}}; // '

	int w, h;
	boolean orthoSetUp;
	
	public Ortho(int width, int height) {
		w=width;
		h=height;
		orthoSetUp=false;
	}
	
	public void setOrthographicProjection() {
		if (!orthoSetUp) {
			// switch to projection mode
			GL11.glMatrixMode(GL11.GL_PROJECTION);
			// save previous matrix which contains the 
			//settings for the perspective projection
			GL11.glPushMatrix();
			// reset matrix
			GL11.glLoadIdentity();
			// set a 2D orthographic projection
			GL11.glOrtho(0, w, 0, h, -99999, 99999);
	
			// invert the y axis, down is positive
			//GL11.glScalef(1, -1, 1);
			//GL11.glDisable(GL11.GL_DEPTH_TEST);
			GL11.glDisable(GL11.GL_CULL_FACE);
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glDisable(GL11.GL_ALPHA_TEST);
			// mover the origin from the bottom left corner
			// to the upper left corner
			GL11.glTranslatef(0, h, 0);
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
			
			orthoSetUp=true;
		}
	}

	public void resetPerspectiveProjection() {
		if (orthoSetUp) {
			GL11.glMatrixMode(GL11.GL_PROJECTION);
			GL11.glPopMatrix();
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
			
			orthoSetUp=false;
		}
	}

	public void drawChar(int x, int y, char a) {
		int aa=a;
		if (aa!=32) {
			if (a=='.') {
				aa=26;
			} else if (a=='\'') {
				aa=62;
			} else if (aa>=48 && aa<=57) {
				aa-=48-28;
			} else {
				if (aa>='a' && aa<='z') {
					aa-='a'; //-1;
				} else if (aa>199) {
					aa-=161;
				} else {
					aa-=64;
				}
			}
			if (aa>=0 && aa<fonts.length) {
				GL11.glBegin(GL11.GL_POINTS);
				for (int i=0;i<5;i++) {
					for (int j=0;j<5;j++) {
						char pixel=fonts[aa][j][i];
						if (pixel==1) {
							GL11.glVertex2d(x+i, y+j);
							GL11.glVertex2d(x+i+0.5f, y+j);
							GL11.glVertex2d(x+i+0.5f, y+j+0.5f);
							GL11.glVertex2d(x+i, y+j+0.5f);
						}
					}
				}
				GL11.glEnd();
			}
		}
	}
	
	public void drawText(int x, int y, String txt) {
		for (int i=0;i<txt.length();i++) {
			drawChar(x+6*i, y, txt.toLowerCase().charAt(i));
		}
	}
	public void drawText(int x, int y, String txt, Vector3f color) {
		GL11.glColor3f(color.x, color.y, color.z);
		drawText(x,y,txt);
		GL11.glColor3f(1.0f, 1.0f, 1.0f);
	}
	
	/**
	 * Draw a box on foreground (z=1) with desired color (palettized or not).
	 * This is not the right way for many boxes. Prefer (@link #boxOpti).
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @param palColor index for palettized color
	 * @param color real color
	 */
	public void box(int x,int y, int w, int h, int palColor, Vector4f color) {
		initDrawBox(false);
		boxOpti(x, y, w, h, palColor, color);
		endDraw();
	}
	
	/**
	 * Just draw the colored box, without managing glBegin/glEnd
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @param palColor
	 * @param color
	 */
	public void boxOpti(int x, int y, int w, int h, int palColor, Vector4f color) {
		Vector4f col=color;
		if (color == null) {
			col=new Vector4f(GFXBasics.getColor(palColor));
			col.scale(1.0f/256.0f);
		}
		GL11.glColor4f(col.x, col.y, col.z, col.w);
		GL11.glVertex2d(x, y);
		GL11.glVertex2d(x+w, y);
		GL11.glVertex2d(x+w, y+h);
		GL11.glVertex2d(x, y+h);
	}
	
	/**
	 * Just draw a textured box, without managing glBegin/glEnd
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @param u
	 * @param v
	 * @param uw
	 * @param vh
	 */
	public void boxTexturedOpti(int x, int y, int w, int h, float u, float v, float uw, float vh) {
		GL11.glTexCoord2f(u, v);
		GL11.glVertex2d(x, y);
		GL11.glTexCoord2f(u+uw, v);
		GL11.glVertex2d(x+w, y);
		GL11.glTexCoord2f(u+uw, v+vh);
		GL11.glVertex2d(x+w, y+h);
		GL11.glTexCoord2f(u, v+vh);
		GL11.glVertex2d(x, y+h);
	}
	
	/**
	 * Initialize the right matrix to draw quads, and do a glBegin.
	 * @param withTexture
	 */
	public void initDrawBox(boolean withTexture) {
		// On se met au premier plan et on annule le texturing
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glPushMatrix();
		GL11.glTranslatef(0,0,1);
		if (!withTexture) {
			GL11.glDisable(GL11.GL_TEXTURE_2D);
		}
		GL11.glBegin(GL11.GL_QUADS); 
	}
	
	/**
	 * Get back the original matrix, and go a glEnd.
	 */
	public void endDraw() {
		GL11.glEnd();
		// On se remet où on était et on réactive le texturing
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glColor3f(1.0f, 1.0f, 1.0f); //, 1.0f);
		GL11.glPopMatrix();
	}
	
	/**
	 * Draw an empty box on foreground (z=1), same way that box.
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @param palColor
	 * @param color
	 */
	public void boxv(int x, int y, int w, int h, int palColor, Vector4f color) {
		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
		box(x ,y ,w ,h ,palColor, color);
		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
		
	}
}
