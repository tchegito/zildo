package zeditor.tools;

import java.awt.Point;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import zeditor.tools.banque.Banque;
import zeditor.tools.banque.Foret1;
import zeditor.tools.banque.Foret2;
import zeditor.tools.banque.Foret3;
import zeditor.tools.banque.Foret4;
import zeditor.tools.banque.Grotte;
import zeditor.tools.banque.Maison;
import zeditor.tools.banque.Palais1;
import zeditor.tools.banque.Palais2;
import zeditor.tools.banque.Village;

// Fait la correspondance entre les fichiers PKM et un fichier DEC

public class CorrespondanceGifDec {

	Map<String, Banque> banks;

	public Banque doTheJob(Banque bank) {
		Map<Point, Integer> mapCorrespondance = new HashMap<Point, Integer>();
		Map<Integer, Point> mapCorrespondanceInverse = new HashMap<Integer, Point>();
		List<Point> pkmChanges = bank.getPkmChanges();
		Iterator<Point> it = pkmChanges.iterator();
		Point current = null;
		if (it.hasNext()) {
			current = it.next();
		}
		int addY = 0;
		int nTile = 0;
		int maxY = 0;
		for (Point p : bank.getCoords()) {
			if (current != null && current.x == nTile) {
				// On change de PKM
				addY += current.y;
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

		// On affiche le numéro de la tile pour chaque position:
		if (false) {
			for (int i = 0; i < (maxY / 16) + 1; i++) {
				for (int j = 0; j < 20; j++) {
					Point p = new Point(j * 16, i * 16);
					System.out.println("Pos: x=" + p.x + " y=" + p.y
							+ " ==> tile n°" + mapCorrespondance.get(p));
				}
			}
			System.out.println("Nombre de tile:" + bank.getCoords().length);
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

	public void init() {
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
	}

	public static void main(String[] args) {
		new CorrespondanceGifDec().init();
	}
}
