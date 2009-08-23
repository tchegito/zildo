package zildo.monde.persos;

import zildo.monde.decors.Element;
import zildo.monde.decors.ElementDescription;

public class PersoVolant extends PersoNJ {

	public PersoVolant() {
		super();
		//if (getQuel_deplacement().equals(MouvementPerso.SCRIPT_VOLESPECTRE)) {
			setCptMouvement(100);
			setForeground(true);
			
			Element ombre=new Element();
			ombre.setX(x);
			ombre.setY(y-12);
			ombre.setSprModel(ElementDescription.SHADOW_SMALL);
			addPersoSprites(ombre);
		//}
	}
	
	public void finaliseComportement(int compteur_animation) {
		// On déplace l'ombre du perso
		if (persoSprites.size() >0) {
			Element ombre=persoSprites.get(0);
			ombre.setX(x);
			ombre.setY(y+6);
			ombre.setVisible(z>0);
		}
		super.finaliseComportement(compteur_animation);
	}
	
}
