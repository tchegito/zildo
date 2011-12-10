/**
 * Legend of Zildo
 * Copyright (C) 2006-2011 Evariste Boussaton
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

package zildo.fwk.net;

public class ServerInfo {

	public String name;
	public String ip;
	public int port;
	public int nbPlayers;
	
	public ServerInfo(String p_name, String p_ip, int p_port) {
		name=p_name;
		ip=p_ip;
		port=p_port;
	}
	
	@Override
	public String toString() {
		return name+"\nIP="+ip+"\nport="+port;
	}
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}
	
	@Override
	public boolean equals(Object p_obj) {
		if (p_obj == null || !p_obj.getClass().equals(ServerInfo.class)) {
			return false;
		}
		return this.hashCode() == p_obj.hashCode();
	}
}
