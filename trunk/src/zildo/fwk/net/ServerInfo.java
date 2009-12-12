package zildo.fwk.net;

public class ServerInfo {

	public String name;
	public String ip;
	public int port;
	
	public ServerInfo(String p_name, String p_ip, int p_port) {
		name=p_name;
		ip=p_ip;
		port=p_port;
	}
	
	public String toString() {
		return name+"\nIP="+ip+"\nport="+port;
	}
	
	public int hashCode() {
		return name.hashCode();
	}
	
	public boolean equals(Object p_obj) {
		if (p_obj == null || !p_obj.getClass().equals(ServerInfo.class)) {
			return false;
		}
		return this.hashCode() == p_obj.hashCode();
	}
}
