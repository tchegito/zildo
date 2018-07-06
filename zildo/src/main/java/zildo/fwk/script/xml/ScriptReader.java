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

package zildo.fwk.script.xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import zildo.fwk.script.xml.element.AnyElement;

public class ScriptReader {

	static SAXParserFactory factory;
	
    /**
     * Return a script element containing all the hierarchy from a given XML file.
     * @param p_scriptName
     * @return AnyElement
     */
    public static AnyElement loadScript(String... p_scriptNames) {
    	List<AnyElement> parsedXmls = new ArrayList<AnyElement>();
        ExecutorService es = Executors.newFixedThreadPool(p_scriptNames.length);
    	for (String scriptName : p_scriptNames) {
    		es.execute(new ThreadLoadScript(scriptName, parsedXmls));
    	}
    	es.shutdown();
    	try {
			es.awaitTermination(1, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			throw new RuntimeException("Unable to parse script !",e);
		}
    	AnyElement ret = null;
    	for (AnyElement parsedXml : parsedXmls) {
	    	 if (ret == null) {
	    		 ret = parsedXml;
	         } else {
	         	// Append read nodes to the previous one
	        	 ret.merge(parsedXml);
	         }
    	}
    	return ret;
    }

    static class ThreadLoadScript extends Thread {
    	
    	final String scriptName;
    	final List<AnyElement> parsedXmls;
    	
    	public ThreadLoadScript(String scriptName, List<AnyElement> parsedXmls) {
    		this.scriptName = scriptName;
    		this.parsedXmls = parsedXmls;
    	}
    	
    	public void run() {
    		try {
	            // Load the stream
	            String filename = "zildo/resource/script/"+scriptName+".xml";
	            InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream(filename);
	            if (stream == null) {
	            	stream = ScriptReader.class.getClassLoader().getResourceAsStream(filename);
	            	if (stream == null) { // Ultimate check: used for Unit Test
	            		stream = ScriptReader.class.getClassLoader().getResourceAsStream(scriptName+".xml");
	            	}
	            }
	            parsedXmls.add(loadStream(stream));
	           
	        } catch (Exception e) {
	            throw new RuntimeException("Unable to parse " + scriptName, e);
	        }
    	}
    }
    
	public static AnyElement loadStream(InputStream p_stream) throws ParserConfigurationException, IOException, SAXException {
		if (factory == null) {
			factory = SAXParserFactory.newInstance();
		}
		SAXParser parser = factory.newSAXParser();
		AdventureHandler handler = new AdventureHandler();
		parser.parse(p_stream, handler);
		AnyElement root = handler.getRoot();
		return root;
	}

}