package zildo.monde.decors;

import java.util.List;

import zildo.monde.persos.Perso;
import zildo.server.EngineZildo;

public class ElementAnimMort extends Element {

	/**
	 * Crée un sprite de mort lié au personnage
	 * @param perso
	 */
    public ElementAnimMort(Perso perso) {
        super();
        z = 8.0f;
        ax = 0.15f;
        vx = 0.0f;
        nSpr = 33;
        setLinkedPerso(perso);
    }

    public List<SpriteEntity> animate() {

        List<SpriteEntity> deads = super.animate();
        byte seq_mort[] = { 33, 35, 34, 36, 37, 38, 39, 0 };

        // Animation de la mort d'un perso
        x = x - vx;
        y = y - vy;
        SpriteEntity link = getLinkedPerso();
        if (getLinkedPerso() != null) {
            Perso perso = (Perso) link;
            x = perso.getX();
            y = perso.getY();
            if (nSpr == 36) {
                perso.hide();
            } else if (nSpr == 38) {
                deads.add(link);
                setLinkedPerso(null);
            }
        }
        if (vx >= 1.0f && vx < 1.15f) {
            EngineZildo.soundManagement.broadcastSound("MonstreMeurt", this);
        }
        nSpr = seq_mort[(byte) vx];
        if (nSpr == 0) {
            deads.add(this);
        }

        return deads;
    }
}