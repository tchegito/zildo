package zildo.monde.persos;

import java.util.Iterator;

import zildo.fwk.bank.SpriteBank;
import zildo.monde.decors.Element;
import zildo.monde.persos.utils.PersoDescription;
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
	
	public PersoGarde() {
		super();
		Element armeGarde=new Element();
		armeGarde.setX(getX());
		armeGarde.setY(getY()-12);
		armeGarde.setNBank(SpriteBank.BANK_PNJ);
		armeGarde.setNSpr(9);
		addPersoSprites(armeGarde);		
	}
	
	public void finaliseComportement(int compteur_animation) {
	   //Garde bleu
	   int add_spr=seq_gbleu[angle.value][(getPos_seqsprite() % (16*Constantes.speed)) / (2*Constantes.speed)];
	   // Arme du garde
       int j=(getPos_seqsprite() / (2*Constantes.speed)) % 2;
       //else j:=0;
       int yy=(int)y,xx=(int)x,zz=0;
       switch (angle) {
       	case NORD:yy=(int)y-8-3*j;xx=(int)x+8;break;
       	case EST:yy=(int)y;		xx=(int)x+6+3*j;zz=4;break;
       	case SUD:yy=(int)y+6+3*j;	xx=(int)x-9;break;
       	case OUEST:yy=(int)y;		xx=(int)x-6-3*j;zz=4;break;
       }
       Iterator<Element> it=this.persoSprites.iterator();
       Element armeGarde=it.next();
       armeGarde.setNSpr(PersoDescription.ARME_EPEE.first() + angle.value);
       armeGarde.setNBank(SpriteBank.BANK_PNJ);
       armeGarde.setX((float)xx);
       armeGarde.setY((float)yy);
       armeGarde.setZ((float)zz);	
       
       this.setNSpr((this.getQuel_spr().first() + add_spr) % 128);
	}
}
