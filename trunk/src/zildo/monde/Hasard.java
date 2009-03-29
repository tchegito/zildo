package zildo.monde;

public class Hasard {

	/**
	 * Renvoie VRAI si un lancer de dé à 10 faces fait plus de 'p_number'
	 * @param p_number
	 */
	static public boolean lanceDes(int p_number) {
		return Math.random()*10 > p_number;
	}
}
