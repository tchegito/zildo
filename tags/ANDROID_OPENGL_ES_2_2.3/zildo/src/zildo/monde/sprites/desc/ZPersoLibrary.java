package zildo.monde.sprites.desc;

import java.util.ArrayList;

import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.Perso.PersoInfo;

@SuppressWarnings("serial")
public class ZPersoLibrary extends ArrayList<SpriteDescription> {

	public ZPersoLibrary() {
		for (PersoDescription desc : PersoDescription.values()) {
			switch (desc) {
			case ARME_EPEE:
			case ARME_LANCE:
			case ARC:
				break;
			default:
				add(desc);
			}
		}
	}
	
	/**
	 * Initialize perso with default properties.
	 * @param p_perso
	 */
	public void initialize(Perso p_perso) {
		PersoInfo info=PersoInfo.NEUTRAL;
		switch (p_perso.getDesc()) {
		case ZILDO:
			info=PersoInfo.ZILDO;
			p_perso.setName("zildo");
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
