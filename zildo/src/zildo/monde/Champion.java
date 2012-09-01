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

package zildo.monde;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Tchegito
 *
 */
public class Champion {

	public final String playerName;
	public final int heartQuarter;
	public final String episodeName;
	public final Date finishDate;
	
	private static DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
	
	private String ret = null;
	
	public Champion(String name, int heartQuarter, String episodeName, Date finishDate) {
		this.playerName = name;
		this.heartQuarter = heartQuarter;
		this.episodeName = episodeName;
		this.finishDate = finishDate;
	}
	
	@Override
	public String toString() {
		if (ret == null) {
			ret = playerName + " - "+df.format(finishDate)+" : "+heartQuarter+" piece";
			if (heartQuarter > 1) {
				ret += "s";
			}
		}
		return ret;
	}
}
