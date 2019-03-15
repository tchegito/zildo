package zildo.monde.map;

import zildo.fwk.bank.TileGFXBank;

public enum AnimatedTiles {

	FLOWERS1(TileGFXBank.Foret1, 52, 55, 163), 
	FLOWERS2(TileGFXBank.Foret1, 53, 56, 164),
	FLOWERSBLUE(TileGFXBank.Foret4, 215, 216, 217),
	FLOWERSBLUE_PACK(TileGFXBank.Foret4, 218, 219, 220),
	FLOWERSRED(TileGFXBank.Foret4, 221, 222, 223),
	FLOWERSORANGE(TileGFXBank.Foret4, 0, 1, 2),
	WATER(TileGFXBank.Foret1, 108, new Repeat(128), 208, 231),
	WATER_BIS(TileGFXBank.Foret1, 130, 230, 253),
	// Blacksmith hoven
	HOVEN1(TileGFXBank.Maison, 142, 178, 194), 
	HOVEN2(TileGFXBank.Maison, 144, 179, 195),
	BIG_CHANDLER(TileGFXBank.Maison, 174, 176, 177),
	CANDLE(TileGFXBank.Maison, 235, 236, 237),
	CAVE_WATER(TileGFXBank.Grotte, 78, 79, 80),
	//WATER_TER(TileGFXBank.Foret3, 96, new Repeat(98), 99, 102),
	LAVA(TileGFXBank.Grotte, 217, 218, 219),
	TORCH(TileGFXBank.Grotte, 230, 231, 232);
	
	public boolean isBack2() {
		switch (this) {
			case FLOWERSBLUE:
			case FLOWERSBLUE_PACK:
			case FLOWERSRED:
			case FLOWERSORANGE:
				return true;
			default:
				return false;
		}
	}
	
	public final TileGFXBank bank;
	public final int reference;
	public final int[] others;
	public final Repeat repeat;
	
	/**
	 * Class allowing to repeat the same pattern along many tiles.
	 */
	public static class Repeat {
		public int until;
		
		public Repeat(int u) {
			until = u;
		}
	}
	
	private AnimatedTiles(TileGFXBank bank, int reference, int... others) {
		this.bank = bank;
		this.reference = reference;
		this.others = others;
		this.repeat = null;
	}
	
	private AnimatedTiles(TileGFXBank bank, int reference, Repeat repeat, int... others) {
		this.bank = bank;
		this.reference = reference;
		this.others = others;
		this.repeat = repeat;
	}
}
