package zildo.fwk.net.www;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class AlembrumeHttpRequest {

	String requestUrl;
	
	public AlembrumeHttpRequest(String url) {
		this.requestUrl = url;
	}
	
	public int send() {
		try {
			URL objUrl = new URL(requestUrl.toString());
			URLConnection urlConnect = objUrl.openConnection();
			
			// Add server infos
			InputStream in = urlConnect.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			int result = reader.read();
			in.close();
			
			return result;
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		}
	}
}
