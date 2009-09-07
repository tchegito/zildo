package zildo.fwk.net;

public enum ServerInfo {

	LAN("192.168.0.1", 80),
	Alex("80.9.136.49", 0),
	Antoine("", 0),
	Jerebat("88.242.171.39", 49122),
	Tchegito("82.228.194.234", 1234);
	
	public String ip;
	public int port;
	
	private ServerInfo(String p_ip, int p_port) {
		ip=p_ip;
		port=p_port;
	}
}
