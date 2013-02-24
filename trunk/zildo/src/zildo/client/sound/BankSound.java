/**
 * The Land of Alembrum
 * Copyright (C) 2006-2013 Evariste Boussaton
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
	ZildoPlonge("eau"),
	ZildoNage("eau2"),
	ZildoPatauge("eau3"),
	ZildoGadou("vase"),
	ZildoRecupVie("vie"),
	ZildoRecupCoeur("coeur"),
	ZildoRecupItem("item"),
	ZildoGagneArgent("argent"),
	ZildoOuvreCoffre("coffre"),
	ZildoCogne("choc"),
	ZildoPousse("pousse"),
	ZildoMonte("escalmon"),
	ZildoDescend("escaldes"),
	ZildoMontePeu("escalhaut"),
	ZildoDescendPeu("escalbas"),
	ZildoTrouve("trouve"),
	ZildoMoon("moonPiece"),
	ZildoSecret("secret"),
	ZildoKey("getKey"),
	ZildoUnlock("doorUnlock"),
	ZildoUnlockDouble("doorDouble"),
	ZildoAccomplishQuest("queteEpuree"),
	ZildoElectric("electric"),
	ZildoDying("dying"),
	ZildoFall("chute"),
	
	QuadDamage("quaddamage"),
	QuadDamageLeaving("damage2"),
	QuadDamaging("damage3"),
	
	MonstreTouche("touche"),
	MonstreTouche2("touche2"),
	MonstreMeurt("meurt"),
	MonstreTrouve("alerte"),
	MonstreTire("tire2"),
	
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
	
	CasseBuisson("feuilles"),
	CassePierre("brise"),
	OuvrePorte("porte"),

	AfficheTexteSuivant("pageturn"),
	AfficheTexteFin("textefin"),

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
	
	BossHit("bosshit"),
	BossSand1("sand1"),
	BossSand2("sand2"),
	Fuite("fuite"),
	Whip("whip"),
	
	ChestAppears("appear"),
	
	FloorTile("floortile1"),
	Sort("sort"),
	Switch("switch");

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
	
	public BankSound next() {
		return values()[this.ordinal() + 1];
	}
 }