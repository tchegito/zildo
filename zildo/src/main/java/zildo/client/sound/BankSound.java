/**
 * The Land of Alembrum
 * Copyright (C) 2006-2016 Evariste Boussaton
 * 
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package zildo.client.sound;

public enum BankSound implements AudioBank {
	ZildoAttaque("epee"),
	
	ZildoTouche("zildoaie"),
	ZildoTombe("tombe"),
	ZildoAtterit("atterit"),
	ZildoLance("lance"),
	ZildoRamasse("ramasse"),
	ZildoPlonge("bigsplash"),
	ZildoNage("eau2"),
	ZildoPatauge("eau"),
	ZildoPatauge2("eau2"),
	ZildoGadou("vase"),
	ZildoRecupCoeur("coeur"),
	ZildoRecupItem("item"),
	ZildoGagneArgent("argent"),
	ZildoOuvreCoffre("coffre"),
	ZildoCogne("choc"),
	ZildoPousse("pousse"),
	ZildoMonte("escalmon"),
	ZildoDescend("escaldes"),
	ZildoTrouve("trouve"),
	ZildoMoon("moonPiece"),
	ZildoOneUp("moon"),
	ZildoSecret("secret"),
	ZildoKey("getKey"),
	ZildoUnlock("doorUnlock"),
	ZildoUnlockDouble("doorDouble"),
	ZildoAccomplishQuest("queteEpuree"),
	ZildoElectric("electric"),	// To replace
	ZildoDying("dying"),
	ZildoFall("chute"),
	
	Flut("flut"),
	
	MoonFusion("moonfusion"),
	
	QuadDamage("quaddamage"),
	QuadDamageLeaving("damage2"),
	QuadDamaging("damage3"),
	
	Invincible("invincible"),
	
	MonstreTouche("touche"),
	MonstreTouche2("touche2"),
	MonstreMeurt("meurt"),
	MonstreTrouve("alerte"),
	
	Poule1("poulea1"),
	Poule2("poulea2"),
	Poule3("poulea3"),
	Poule4("poulea4"),
	Poule5("poulea5"),
	Poule6("pouleb1"),
	Poule7("pouleb2"),
	Poule8("pouleb3"),
	
	Duck1("ducka1"),
	Duck2("ducka2"),
	Duck3("ducka3"),
	Duck4("duckb1"),
	Duck5("duckb2"),
	Duck6("duckb3"),
	
	Cat1("cat1"),
	Cat2("cat2"),
	Cat3("cat3"),
	
	Bat("bat"),
	
	Bee("bee"),	// repeat
	
	Blob("blob1"),	// Green blob
	
	CasseBuisson("feuilles"),
	CassePierre("brise"),
	OuvrePorte("porte"),

	AfficheTexteSuivant("pageturn"),
	//AfficheTexteFin("textefin"),

	FlecheTir("tire"),
	FlechePlante("plante"),
	
	Boomerang("boomeran"),
	BoomerangTape("impact"),
	
	MenuIn("menu1"),
	MenuOut("menu2"),
	MenuMove("bip"),
	MenuSelect("choix"),
	MenuSelectGame("choixok"),
	MenuOutOfOrder("out"),
	
	PlanteBombe("posebomb"),
	Explosion("explose"),
	Hammer("marteau"),
	CannonBall("cannonball"),
	
	Bitey("bitey"),
	BossSand1("sand1"),
	Fuite("fuite"),
	Whip("whip"),
	Wind("windsuck"),
	WindNoLoop("windsuck"),
	ElementalSleep("elementalSleep"),
	
	SlipWater1("slipwater1"),
	SlipWater2("slipwater2"),
	FallWater("eau"),
	Fountain("fountain"),
	Goutte1("goutte1"),
	Goutte2("goutte2"),
	Goutte3("goutte3"),
	
	ChestAppears("appear"),
	
	FloorTile("floortile1"),
	Sort("sort"),
	Switch("switch"),
	LavaDrop("lavadrop"),
	Boiling("boiling"),
	PeebleFloor("peeble"),
	PoisonCloud("poison"),
	
	Squeak1("squeak1"),
	Squeak2("squeak2"),
	
	SerpentSpit("serpentSpit"),
	
	BigRat("bigrat"),
	
	Gas("gas"),
	
	MoleBuried1("moleBuried1"),
	MoleBuried2("moleBuried2"),
	MoleOut("moleOut"),
	MoleCry("molecry"),
	
	Slab1("slabPush"),
	Slab2("slabRelease"),
	
	Lighting("lighting"),	// For dynamite
	
	Chain("chain");
	
	String filename;
	
	private BankSound(String p_filename) {
		filename=p_filename;
	}
	
	public String getFilename() {
		return filename;
	}
	
	public String getSuffix() {
		return "wav";
	}
	
	/** Actually, looping is decided at sound definition itself, and can't be overriden in script **/
	public boolean isLooping() {
		return this == BankSound.Boiling || this == BankSound.Wind || this == BankSound.Fountain ||
				this == BankSound.Chain;
	}
	
	public BankSound next() {
		return values()[this.ordinal() + 1];
	}
 }