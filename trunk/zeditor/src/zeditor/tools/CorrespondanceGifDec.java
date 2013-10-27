package zeditor.tools;

import java.awt.Image;
import java.awt.Point;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import zeditor.tools.banque.Foret1;
import zeditor.tools.banque.Foret2;
import zeditor.tools.banque.Foret3;
import zeditor.tools.banque.Foret4;
import zeditor.tools.banque.Grotte;
import zeditor.tools.banque.Maison;
import zeditor.tools.banque.Palais1;
import zeditor.tools.banque.Palais2;
import zeditor.tools.banque.Palais3;
import zeditor.tools.banque.Village;
import zeditor.tools.tiles.Banque;
import zeditor.tools.tiles.GraphChange;
import zeditor.tools.tiles.TileBankEdit;
import zildo.fwk.bank.TileBank;
import zildo.fwk.gfx.engine.TileEngine;
import zildo.monde.map.TileCollision;
import zildo.monde.map.TileInfo;

// Fait la correspondance entre les fichiers PKM et un fichier DEC

public class CorrespondanceGifDec {

	final Map<String, Banque> banks;
	final TileCollision tileCollision = TileCollision.getInstance();
	
	public Banque doTheJob(Banque bank) {
		Map<Point, Integer> mapCorrespondance = new HashMap<Point, Integer>();
		Map<Integer, Point> mapCorrespondanceInverse = new HashMap<Integer, Point>();
		List<GraphChange> pkmChanges = bank.getPkmChanges();
		Iterator<GraphChange> it = pkmChanges.iterator();
		GraphChange current = null;
		if (it.hasNext()) {
			current = it.next();
		}
		int addY = 0;
		int nTile = 0;
		int maxY = 0;
		for (Point p : bank.getCoords()) {
			if (current != null && current.nTile == nTile) {
				// On change de PKM
				addY += current.shiftY;
				if (it.hasNext()) {
					current = it.next();
				}
			}
			p.y += addY;
			if (p.y > maxY) {
				maxY = p.y;
			}
			// On range le point dans la map
			mapCorrespondance.put(p, nTile);
			mapCorrespondanceInverse.put(nTile, p);
			nTile++;
		}

		bank.setMapCorrespondance(mapCorrespondance, mapCorrespondanceInverse);
		return bank;
	}

	private Banque getBanque(String bankName) {
		Banque bank = banks.get(bankName.toUpperCase());
		if (bank == null) {
			throw new RuntimeException("La banque " + bankName
					+ " n'existe pas !");
		}
		return bank;
	}
	
	public Image generateImg(TileBank bank) {
		Banque b=getBanque(bank.getName());
		return new TileBankEdit(bank, b).generateImg();
	}
	
	/**
	 * Renvoie le numéro de la tile correspondant à la banque et à la position
	 * passées en paramètre
	 * 
	 * @param bankName
	 *            Nom de la banque (correspond aux noms des fichiers .DEC)
	 * @param x
	 * @param y
	 * @return int (-1 si aucune tile trouvée à cet endroit)
	 */
	public int getMotifParPoint(String bankName, int x, int y) {
		return getBanque(bankName).getNumTile(x, y);
	}
	
	public Point getPointParMotif(String bankName, int nMotif) {
		return getBanque(bankName).getCoordsTile(nMotif);
	}
	
	public TileInfo getCollisionParPoint(String bankName, int x, int y) {
		int nTile=getMotifParPoint(bankName, x, y);
		if (nTile == -1) {
			return null;
		}
		int i=0;
        for (String name : TileEngine.tileBankNames) {
        	if (name.startsWith(bankName)) {
        		break;
        	}
        	i+=256;
        }
        i+=nTile;
        return tileCollision.getTileInfo(i);
	}

	public CorrespondanceGifDec() {
		banks = new HashMap<String, Banque>();
		banks.put("FORET1", doTheJob(new Foret1()));
		banks.put("FORET2", doTheJob(new Foret2()));
		banks.put("FORET3", doTheJob(new Foret3()));
		banks.put("FORET4", doTheJob(new Foret4()));
		banks.put("MAISON", doTheJob(new Maison()));
		banks.put("VILLAGE", doTheJob(new Village()));
		banks.put("GROTTE", doTheJob(new Grotte()));
		banks.put("PALAIS1", doTheJob(new Palais1()));
		banks.put("PALAIS2", doTheJob(new Palais2()));
		banks.put("PALAIS3", doTheJob(new Palais3()));
	}
}
