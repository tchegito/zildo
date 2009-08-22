package zildo.monde;

import zildo.monde.map.Angle;
import zildo.monde.map.Point;
import zildo.monde.persos.Perso;

public class Collision {

    public int cx, cy;
    public int cr;
    public Angle cangle; // Rayon et angle du monstre
    public Point size; // Exact object's size
    public Perso perso; // Position dans la table de pnj

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

    public Collision(int x, int y, int cr, Point size, Angle angle, Perso perso) {
        this.cx = x;
        this.cy = y;
        this.cr = cr;
        this.size = size;
        this.cangle = angle;
        this.perso = perso;
    }
}