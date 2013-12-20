package shader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ShaderReader {

	String fragmentCode;
	
	public ShaderReader(String filename) {
		java.io.InputStream stream = getClass().getClassLoader().getResourceAsStream("shader/glsl/"+filename+".shader");
		if (stream != null) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
			StringBuilder sb = new StringBuilder();
			while (true) {
				String line = null;
				try {
					line = reader.readLine();
				} catch (IOException e) {
					throw new RuntimeException("Can't read "+filename+" shader !");
				}
				if (line == null) {
					break;
				}
        		sb.append(line).append("\n");

			}
			fragmentCode = sb.toString();
		} else {
			throw new RuntimeException("Can't find "+filename+" shader !");
		}
	}
	
	public String getFragmentCode() {
		return fragmentCode;
	}
	
}
