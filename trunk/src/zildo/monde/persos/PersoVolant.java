package zildo.monde.persos;

import zildo.fwk.bank.SpriteBank;
import zildo.monde.decors.Element;

public class PersoVolant extends PersoNJ {

	public PersoVolant() {
		super();
		//if (getQuel_deplacement().equals(MouvementPerso.SCRIPT_VOLESPECTRE)) {
			setCptMouvement(100);
			setForeground(true);
			
			Element ombre=new Element();
			ombre.setX(x);
			ombre.setY(y-12);
			ombre.setNBank(SpriteBank.BANK_ELEMENTS);
			ombre.setNSpr(43);
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
