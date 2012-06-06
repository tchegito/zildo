package zildo.platform.opengl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import zildo.fwk.script.xml.ScriptReader;

public class ShaderReader {

	String vertexCode;
	String fragmentCode;
	
	final static String delimiterVertex = "[VERTEX]";
	final static String delimiterFragment = "[FRAGMENT]";
	
	/**
	 * Constructs the shader reader.
	 * @param filename file name without the extension (.shader)
	 */
	public ShaderReader(String shadername) {
        String filename = "shaders/"+shadername+".shader";
        InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream(filename);
        if (stream == null) {
        	stream = ScriptReader.class.getClassLoader().getResourceAsStream(filename);
        }
        BufferedReader buf = new BufferedReader(new InputStreamReader(stream));
        StringBuilder sb = new StringBuilder();
        int check = 2;	// Should be zero at end
        try {
        	String strLine;
	        while ((strLine = buf.readLine()) != null)   {
	        	if (strLine.startsWith(delimiterVertex)) {
	        		// Nothing to do
	        		check--;
	        	} else if (strLine.startsWith(delimiterFragment)) {
	        		// All precedent is vertex shader
	        		vertexCode = sb.toString();
	        		sb.setLength(0);
	        		check--;
	        	} else {
	        		sb.append(strLine).append("\n");
	        	}
	        }
	        fragmentCode = sb.toString();
	        
        } catch (IOException e) {
        	throw new RuntimeException("Can't read shader file :"+filename);
        } finally {
        	try {
        		buf.close();
        		stream.close();
        	} catch (IOException e) {
            	throw new RuntimeException("Can't close shader file :"+filename);
        	}
        }
        if (check != 0) {
        	throw new RuntimeException("Shader's code should provide 2 sections : [VERTEX] and [FRAGMENT].");
        }
	}
	
	public String getVertexCode() {
		return vertexCode;
	}

	public String getFragmentCode() {
		return fragmentCode;
	}
}
