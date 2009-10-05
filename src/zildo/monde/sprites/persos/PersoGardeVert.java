package zildo.monde.sprites.persos;

import java.util.Iterator;

import zildo.fwk.bank.SpriteBank;
import zildo.monde.sprites.desc.PersoDescription;
import zildo.monde.sprites.elements.Element;
import zildo.prefs.Constantes;

/**
 * Perso garde vert.
 * 
 * La particularité est qu'il est constitué de deux sprites:
 * -un pour le corps
 * -un pour la tête
 * 
 * Il est possible d'effectuer des combinaisons, comme par exemple le corps de la princesse et la tête de garde.
 * @author tchegito
 *
 */
public class PersoGardeVert extends PersoNJ {

	public static final int[] mouvetete={0,3,1,0};

	public PersoGardeVert() {
		super();
		// On crée la tête du garde
		Element teteGarde=new Element();
		teteGarde.setX(getX());
		teteGarde.setY(getY()-12);
		teteGarde.setNBank(SpriteBank.BANK_PNJ);
		teteGarde.setNSpr(PersoDescription.HAUT_GARDEVERT.first());
		addPersoSprites(teteGarde);
	}
	
	public void finaliseComportement(int compteur_animation) {
		// On affiche la tête du garde vert
		Iterator<Element> it=this.persoSprites.iterator();
		Element teteGarde=it.next();
		//TODO: found why we use an array 'mouvetete' on the nSpr property. getMouvement() seems to always return 0.
		teteGarde.setNSpr(PersoDescription.HAUT_GARDEVERT.first() + (angle.value + mouvetete[cptMouvement])  % 4);
		teteGarde.setX(x);
		teteGarde.setY(y);
		teteGarde.setZ(9 + (this.getPos_seqsprite() % (4*Constantes.speed)) / (2*Constantes.speed));
		
		int add_spr=angle.value*2 + (getPos_seqsprite() % (4*Constantes.speed)) / (2*Constantes.speed);

		this.setNSpr((this.getQuel_spr().first()+add_spr) % 128);

	}
}
