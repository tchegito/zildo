package zildo.monde;

import zildo.monde.map.Angle;
import zildo.monde.persos.Perso;

public class Collision {

	private int cx,cy;
	private int cr;
	private Angle cangle;	// Rayon et angle du monstre
    private Perso perso;	// Position dans la table de pnj
	private int ctabpnj;
	
	public Collision() {
	}
	
	public int getCx() {
		return cx;
	}

	public void setCx(int cx) {
		this.cx = cx;
	}

	public int getCy() {
		return cy;
	}

	public void setCy(int cy) {
		this.cy = cy;
	}

	public int getCr() {
		return cr;
	}

	public void setCr(int cr) {
		this.cr = cr;
	}

	public Angle getCangle() {
		return cangle;
	}

	public void setCangle(Angle cangle) {
		this.cangle = cangle;
	}

	public Perso getPerso() {
		return perso;
	}

	public void setPerso(Perso perso) {
		this.perso = perso;
	}

	public int getCtabpnj() {
		return ctabpnj;
	}

	public void setCtabpnj(int ctabpnj) {
		this.ctabpnj = ctabpnj;
	}

	public Collision(int x, int y, int cr, Angle angle, Perso perso) {
		this.cx=x;
		this.cy=y;
		this.cr=cr;
		this.cangle=angle;
		this.perso=perso;
	}
	
	public Collision(int x, int y, int cr, Angle angle, Perso perso, int ctabpnj) {
		this.cx=x;
		this.cy=y;
		this.cr=cr;
		this.cangle=angle;
		this.perso=perso;
		this.ctabpnj=ctabpnj;
	}	
}