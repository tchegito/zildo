/**
 * Legend of Zildo
 * Copyright (C) 2006-2012 Evariste Boussaton
 * Based on original Zelda : link to the past (C) Nintendo 1992
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
	ZildoRecupArgent("argent3"),
	ZildoGagneArgent("argent"),
	ZildoOuvreCoffre("coffre"),
	ZildoCogne("choc"),
	ZildoPousse("pousse"),
	ZildoMonte("escalmon"),
	ZildoDescend("escaldes"),
	ZildoMontePeu("escalhaut"),
	ZildoDescendPeu("escalbas"),
	ZildoTrouve("trouve"),
	ZildoCoeur("heartPiece"),
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
	
	Poule1("poule1"),
	Poule2("poule2"),
	
	Bee("bee"),	// repeat
	
	CasseBuisson("feuilles"),
	CassePierre("brise"),
	OuvrePorte("porte"),

	AfficheTexte("texte1"),
	AfficheTexteFin("textefin"),

	FlecheTir("tire"),
	FlechePlante("plante"),
	
	Boomerang("boomeran"),
	BoomerangTape("argent2"),
	
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
 }