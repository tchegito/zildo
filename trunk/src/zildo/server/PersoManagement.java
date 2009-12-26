package zildo.server;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import zildo.fwk.bank.SpriteBank;
import zildo.monde.map.Angle;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.desc.PersoDescription;
import zildo.monde.sprites.elements.Element;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.PersoGarde;
import zildo.monde.sprites.persos.PersoGardeVert;
import zildo.monde.sprites.persos.PersoHen;
import zildo.monde.sprites.persos.PersoNJ;
import zildo.monde.sprites.persos.PersoVolant;
import zildo.monde.sprites.persos.PersoZildo;

//////////////////////////////////////////////////////////////////////
// PersoManagement
//////////////////////////////////////////////////////////////////////
// Class managing characters on a map
//
//////////////////////////////////////////////////////////////////////



public class PersoManagement {


	public List<Perso> tab_perso;
	
	public PersoManagement()
	{
		tab_perso=new ArrayList<Perso>();
	}
	
	public PersoZildo getZildo()
	{
		for (Perso p : tab_perso) {
			if (p.isZildo()) {
				return (PersoZildo) p;
			}
		}
		return null;
	}
	
	public void clearPersos()
	{
		tab_perso.clear();
	}
	
	public void clearPersosWithoutZildo()
	{
		Iterator<Perso> it=tab_perso.iterator();
		if (tab_perso.size() <= 1) {
			// We haven't enough characters to process this deletion
			return;
		}
		// Destroy entities
		while (it.hasNext()) {
			Perso perso=it.next();
			if (perso != null && !perso.isZildo()) {
				EngineZildo.spriteManagement.deleteSprite(perso);
				it.remove();
			}
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
    public Perso collidePerso(int x, int y, Element quelElement, int rayon) {
        Perso perso = null;
        if (quelElement != null && quelElement.getEntityType() == SpriteEntity.ENTITYTYPE_PERSO) {
            perso = (Perso) quelElement;
        }

        for (Perso persoToCompare : tab_perso) {
            if (persoToCompare.getPv() > 0 && persoToCompare != quelElement) {
                int tx = (int) persoToCompare.getX();
                int ty = (int) persoToCompare.getY();
                if (EngineZildo.collideManagement.checkCollisionCircles(x, y, tx, ty, rayon, rayon)) {
                    if (perso != null && perso.isZildo() && perso.linkedSpritesContains(persoToCompare)) {
                        // Collision entre Zildo et l'objet qu'il porte dans les mains => on laisse
                    } else if (quelElement == null || quelElement.getLinkedPerso() != persoToCompare) {
                        return persoToCompare;
                    }
                }
            }
        }
        return null;
    }

    public Perso collidePerso(int x, int y, Element quelPerso) {
        return collidePerso(x, y, quelPerso, 5);
    }
	
    public Perso getNamedPerso(String p_name) {
        if (p_name != null && !"".equals(p_name)) {
            for (Perso p : tab_perso) {
                if (p_name.equalsIgnoreCase(p.getNom())) {
                    return p;
                }
            }
        }
        return null;
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
	
	public void removePerso(Perso perso) {
		tab_perso.remove(perso);
	}
	
	/**
	 * Create a character onto the map, with basic initializations.
	 * @param p_desc
	 * @param x
	 * @param y
	 * @param z
	 * @param name
	 * @return Perso
	 */
	public Perso createPerso(PersoDescription p_desc, int x, int y, int z, String name, int angle) {
		Perso perso;
		switch (p_desc) {
			case POULE:
	            perso = new PersoHen(x, y);
				break;
			case BAS_GARDEVERT:
				perso = new PersoGardeVert();
				break;
			case GARDE_CANARD:
				perso = new PersoGarde();
				break;
			case CORBEAU:
			case SPECTRE:
				perso = new PersoVolant();
				break;
			default:
				perso = new PersoNJ();
				break;
		}

        perso.setX(x);
        perso.setY(y);
        perso.setZ(z);
        perso.setQuel_spr(p_desc);
        perso.setNBank(SpriteBank.BANK_PNJ);
        perso.setNom(name);
        if (perso.getQuel_spr().first() >= 128) {
            perso.setNBank(SpriteBank.BANK_PNJ2);
        }
        perso.setPv(1);
        perso.setAngle(Angle.fromInt(angle));

        return perso;
    }
	
}