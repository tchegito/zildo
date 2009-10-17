package zildo.monde.sprites.persos;

import zildo.monde.collision.Collision;
import zildo.monde.collision.DamageType;
import zildo.monde.sprites.elements.ElementGuardWeapon;
import zildo.prefs.Constantes;

/**
 * Perso garde "canard"
 * 
 * Plusieurs particularités:
 * -il peut changer de couleurs à l'aide d'un pixel shader, qui se base sur son nom.
 * -il peut avoir plusieurs armes différente: épée, lance et arc
 * @author tchegito
 *
 */
public class PersoGarde extends PersoNJ {
	
	final int[][] seq_gbleu={
		{0,1,4,1,0,2,3,2},{5,6,7,6,5,6,7,6},
		{8,9,10,11,8,9,10,11},{12,13,14,13,12,13,14,13}};
	
	ElementGuardWeapon weapon;
	
	public PersoGarde() {
		super();
		weapon=new ElementGuardWeapon(this);
		addPersoSprites(weapon);		
	}
	
	public void finaliseComportement(int compteur_animation) {
	   //Garde bleu
	   int add_spr=seq_gbleu[angle.value][(getPos_seqsprite() % (16*Constantes.speed)) / (2*Constantes.speed)];
       this.setNSpr((this.getQuel_spr().first() + add_spr) % 128);
	}
	
	public Collision getCollision() {
		return new Collision((int) x, (int) y, 10, null, this, DamageType.BLUNT, null);
	}
}
