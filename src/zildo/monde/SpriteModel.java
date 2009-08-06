package zildo.monde;





////////////////////////////////////////////////////////////////////////////////////////
//
// S p r i t e
//
////////////////////////////////////////////////////////////////////////////////////////
// Model class for a graphic sprite from a given bank.
////////////////////////////////////////////////////////////////////////////////////////
//
// Technically:
//-------------
//* Contains informations about sprite location on the bank's texture.

public class SpriteModel extends Identified {
	static public final int TEXTER_BORDGAUCHE = 50;
	static public final int TEXTER_BORDDROIT = 270;

	private	int taille_x,taille_y;
	private int texPos_x,texPos_y;			// Position sur la texture de sprite

	private	int offset;
	
	public SpriteModel(int taille_x, int taille_y, int offset) {
		this.taille_x=taille_x;
		this.taille_y=taille_y;
		this.offset=offset;
		initializeId();
	}
	
	public SpriteModel(int taille_x, int taille_y, int texPos_x, int texPos_y) {
		this.taille_x=taille_x;
		this.taille_y=taille_y;
		this.texPos_x=texPos_x;
		this.texPos_y=texPos_y;
		initializeId();
	}
	
	public int getTaille_x() {
		return taille_x;
	}

	public void setTaille_x(int taille_x) {
		this.taille_x = taille_x;
	}

	public int getTaille_y() {
		return taille_y;
	}

	public void setTaille_y(int taille_y) {
		this.taille_y = taille_y;
	}

	public int getTexPos_x() {
		return texPos_x;
	}

	public void setTexPos_x(int texPos_x) {
		this.texPos_x = texPos_x;
	}

	public int getTexPos_y() {
		return texPos_y;
	}

	public void setTexPos_y(int texPos_y) {
		this.texPos_y = texPos_y;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}
}