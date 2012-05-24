package zildo.fwk.bank;

public enum TileGFXBank {
	Foret1,
	Village,
	Maison,
	Grotte,
	Foret2,
	Foret3,
	Foret4,
	Palais1,
	Palais2;
	
	int offset;

	public int getOffset() {
		return ordinal() << 8;	// * 256
	}
	
}
