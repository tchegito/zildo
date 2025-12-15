package zildo.fwk.file;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import zildo.fwk.script.xml.ScriptReader;

/**
 * Read a 'shader' file and extract 2 string sections : vertex and fragment.<p/>
 * 
 * Extract uniform fields and provide them via a return method : {@link #getUniforms()}.
 * 
 * @author Tchegito
 *
 */
public class ShaderReader {

	String vertexCode;
	String fragmentCode;
	
	final static String delimiterVertex = "[VERTEX]";
	final static String delimiterFragment = "[FRAGMENT]";
	
	Pattern uniformParser = Pattern.compile("uniform .* (.*);(.*)");
	
	List<String> uniforms = new ArrayList<String>();
	
	/**
	 * Constructs the shader reader.
	 * @param filename file name without the extension (.shader)
	 */
	public ShaderReader(String folder, String shadername) {
        String filename = "shader/" + folder + "/"+shadername+".shader";
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
	        		// Look for uniform
	        		parseUniform(strLine);
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
        if (fragmentCode == null && vertexCode == null) {
        	throw new RuntimeException("Shader's code should provide 2 sections : [VERTEX] and [FRAGMENT], or at least [FRAGMENT].");
        }
	}
	
	private void parseUniform(String str) {
		Matcher m = uniformParser.matcher(str);
		if (m.matches()) {
			uniforms.add(m.group(1));
		}
	}
	
	public String getVertexCode() {
		return vertexCode;
	}

	public String getFragmentCode() {
		return fragmentCode;
	}
	
	public String[] getUniforms() {
		return uniforms.toArray(new String[] {});
	}

}
