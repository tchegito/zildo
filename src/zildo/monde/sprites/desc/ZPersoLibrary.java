package zildo.monde.sprites.desc;

import java.util.ArrayList;
import java.util.Arrays;

import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.Perso.PersoInfo;

@SuppressWarnings("serial")
public class ZPersoLibrary extends ArrayList<SpriteDescription> {

	public ZPersoLibrary() {
		addAll(Arrays.asList(PersoDescription.values()));
	}
	
	/**
	 * Initialize perso with default properties.
	 * @param p_perso
	 */
	public void initialize(Perso p_perso) {
		PersoDescription desc=p_perso.getQuel_spr();
		PersoInfo info=PersoInfo.NEUTRAL;
		switch (desc) {
		case ZILDO:
			info=PersoInfo.ZILDO;
			p_perso.setNom("zildo");
			break;
		case GARDE_BOUCLIER:
			info=PersoInfo.NEUTRAL;
			break;
		case BAS_GARDEVERT:
		case GARDE_CANARD:
		case CORBEAU:
		case SPECTRE:
		case ABEILLE:
		case CRABE:
		case SQUELETTE:
		case VAUTOUR:
		case ECTOPLASME:
			info=PersoInfo.ENEMY;
			break;
		}
		
		p_perso.setInfo(info);
	}
}
