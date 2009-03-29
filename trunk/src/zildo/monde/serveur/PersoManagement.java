package zildo.monde.serveur;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import zildo.fwk.engine.EngineZildo;
import zildo.monde.decors.Element;
import zildo.monde.decors.SpriteEntity;
import zildo.monde.persos.Perso;
import zildo.monde.persos.PersoZildo;

//////////////////////////////////////////////////////////////////////
// PersoManagement
//////////////////////////////////////////////////////////////////////
// Class managing characters on a map
//
//////////////////////////////////////////////////////////////////////



public class PersoManagement {


	List<Perso> tab_perso;
	
	public PersoManagement()
	{
		tab_perso=new ArrayList<Perso>();
	}
	
	public PersoZildo getZildo()
	{
		return (PersoZildo) tab_perso.get(0);
	}
	
	public void clearPersos()
	{
		tab_perso.clear();
	}
	
	public void clearPersosWithoutZildo()
	{
		Iterator<Perso> it=tab_perso.iterator();
		if (tab_perso.size() > 1) {
			it.next();
		} else {
			// We haven't enough characters to process this deletion
			return;
		}
		// Destroy entities
		while (it.hasNext()) {
			Perso perso=it.next();
			if (perso != null) {
				EngineZildo.spriteManagement.deleteSprite(perso);
			}
		}
		// Clear perso's list except Zildo
		while (tab_perso.size()!=1) {
			tab_perso.remove(1);
		}
	}
	
	Perso get_perso(int nperso)
	{
		// Get the right sprite
		return tab_perso.get(nperso);

	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// collidePerso
	///////////////////////////////////////////////////////////////////////////////////////
	// IN:(x,y) coordinates of character quelPerso
	// OUT:perso colliding
	///////////////////////////////////////////////////////////////////////////////////////
	// Checks wether two characters being collided
	///////////////////////////////////////////////////////////////////////////////////////
	public Perso collidePerso(int x,int y,Element quelElement,int rayon)
	{
		Perso perso=null;
		if (quelElement.getEntityType() == SpriteEntity.ENTITYTYPE_PERSO) {
			perso=(Perso) quelElement;
		}
		
		for (Perso persoToCompare : tab_perso) {
			if (persoToCompare.getPv()>0 && persoToCompare != quelElement) {
				int tx=(int)persoToCompare.getX();
				int ty=(int)persoToCompare.getY();
				if (EngineZildo.collideManagement.check_colli((int) x,(int) y,tx,ty,rayon,rayon)) {
					if (perso != null && perso.isZildo() && perso.linkedSpritesContains(persoToCompare)) {
						// Collision entre Zildo et l'objet qu'il porte dans les mains => on laisse
					} else {
						return persoToCompare;
					}
				}
			}
		}
		return null;
	}
	public Perso collidePerso(int x,int y,Element quelPerso) {
		if (quelPerso == null) {
			return null;
		} else {
			return collidePerso(x, y, quelPerso, 6);
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// addPerso
	///////////////////////////////////////////////////////////////////////////////////////
	// Add perso to the list
	///////////////////////////////////////////////////////////////////////////////////////
	public void addPerso(Perso perso)
	{
		tab_perso.add(perso);
	}
	
}