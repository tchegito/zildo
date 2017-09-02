package zildo.fwk.net.www;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class CrashReporter {

	String fullStack;
	
	public CrashReporter(Throwable t) {
		// Try to send report if player has any communication enabled (3G or Wifi)
		StringWriter errorMessage = new StringWriter();
		PrintWriter pw = new PrintWriter(errorMessage);
		t.printStackTrace(pw);
		fullStack = errorMessage.toString();
	}
	
	public String getMessage() {
		return fullStack;
	}
	
	public void sendReport() {
		StringBuilder sb = new StringBuilder(WorldRegister.url);
		sb.append("?command=STACK");
		try {
			String encoded = URLEncoder.encode(getMessage(), WorldRegister.charset);
			sb.append("&message=").append(encoded);
			System.out.println(encoded);
		} catch (UnsupportedEncodingException e) {
			
		}
		new AlembrumeHttpRequest(sb.toString()).send();
	}
}
