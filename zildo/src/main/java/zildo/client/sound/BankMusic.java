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

public enum BankMusic implements AudioBank {
	 Village("ZildoVillage"),
	 Angoisse("Angoisse"),
	 Grotte("Grotte"),
	 Triste("Triste"),
	 Surprise("Surprise"),
	 PianoBar("PianoBar"),
	 Chateau("Chateau"),
	 ChateauAttaque("ChateauAttack"),
	 Vitesse("Vitesse"),
	 ZildoMort("ZildoMort"),
	 Nuit("Nuit"),
	 Falcor("Falcor"),
	 Voleurs("Voleurs"),
	 Attaque("Attaque"),
	 Squirrel("Roxyforest"),
	 Isidore("Squirrel"),
	 Story("Story"),
	 Nature("Nature"),
	 Valori("Valori"),
	 Poulpa("Poulpa");
	 
	 private String filename;
	 
	 private BankMusic(String p_filename) {
		 filename=p_filename;
	 }

	public String getFilename() {
		return filename;
	}
	
	public static BankMusic forName(String p_name) {
		for (BankMusic b : values()) {
			if (b.name().equals(p_name)) {
				return b;
			}
		}
		throw new RuntimeException("The music '+p_name+' doesn't exist !");
	}
	
	public String getSuffix() {
		return "ogg";
	}
	
	@Override
	public boolean isLooping() {
		return true;
	}
}