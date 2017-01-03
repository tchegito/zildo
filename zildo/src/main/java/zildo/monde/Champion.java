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

package zildo.monde;

import java.util.Date;

/**
 * @author Tchegito
 *
 */
public class Champion {

	public String playerName;
	public final int moonHalf;	// PV * 4
	public final String episodeName;
	public final int money;
	public final long timeSpent;
	public final Date finishDate;
	
	private String ret = null;
	
	public Champion(String name, int heartQuarter, String episodeName, Date finishDate, int money, long timeSpent) {
		this.playerName = name;
		this.moonHalf = heartQuarter;
		this.episodeName = episodeName;
		this.finishDate = finishDate;
		this.money = money;
		this.timeSpent = timeSpent;
	}
	
	@Override
	public String toString() {
		if (ret == null) {
			String strHq = "" + moonHalf / 4;
			int half = moonHalf % 4;
			if (half > 0) {
				strHq+= "." +
						"" + 25 * half;
			}
			ret = playerName + " - ";
			ret += getTimeSpentToString((int) timeSpent) + " - "+strHq+"^ "+money+"¤";
		}
		return ret;
	}
	
	public static String getTimeSpentToString(int timeSpent) {
		int nbMinutes = timeSpent / 60;
		return nbMinutes+"min"+timeSpent % 60;
	}
}
