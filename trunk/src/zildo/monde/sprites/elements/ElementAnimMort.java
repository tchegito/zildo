package zildo.monde.sprites.elements;

import zildo.client.SoundPlay.BankSound;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.persos.Perso;
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

    public void animate() {

        super.animate();
        byte seq_mort[] = { 33, 35, 34, 36, 37, 38, 39, 0 };

        // Animation de la mort d'un perso
        //x = x - vx;
        //y = y - vy;
        SpriteEntity link = getLinkedPerso();
        if (getLinkedPerso() != null) {
            Perso perso = (Perso) link;
            x = perso.getX();
            y = perso.getY();
            if (nSpr == 36) {
                perso.hide();
            } else if (nSpr == 38) {
                link.dying=true;
                setLinkedPerso(null);
            }
        }
        if (vx >= 1.0f && vx < 1.15f) {
            EngineZildo.soundManagement.broadcastSound(BankSound.MonstreMeurt, this);
        }
        nSpr = seq_mort[(byte) vx];
        if (nSpr == 0) {
            dying=true;
        }
    }
}