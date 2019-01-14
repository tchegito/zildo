package zeditor.tools.builder;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.imageio.ImageIO;

import org.json.JSONObject;
import org.json.JSONTokener;

import zeditor.tools.tiles.TileBankEdit;
import zildo.client.Client;

public class PyxelReader {

	public static void main(String[] args) {
		String filename="src/main/resources/DemoDoc.pyxel";
		
        // Init OpenGL in order to save texture
		new Client(false);

		new PyxelReader().importPyxel(filename);
	}
	
	JSONObject root;
	
	@SuppressWarnings("resource")
	public void importPyxel(String filename) {
		// Load and unzip file
		TileBankEdit bankEdit = new TileBankEdit(null);
		bankEdit.setName("pyxel");
		int pos = 0;
		
		try {
	        ZipInputStream zis = new ZipInputStream(new FileInputStream(filename));
	        ZipEntry zipEntry = zis.getNextEntry();
	        while (zipEntry != null) {
	            // Look for JSON desc
	            if (zipEntry.getName().endsWith("json")) {
	            	// Read JSON (without closing inputstream, otherwise ZIP reading is over !)
	            	String json = new BufferedReader(new InputStreamReader(zis))
	            	  .lines().collect(Collectors.joining("\n"));
	            	extractNumTiles(json);
	            } else {
	            	// Store texture
		           	BufferedImage pngTexture = ImageIO.read(zis);
		           	bankEdit.addSpr(pos++, pngTexture.getRGB(0,  0,  16,  16, null, 0, 16));
	            }
	            zipEntry = zis.getNextEntry();
	        }
	        zis.closeEntry();
	        zis.close();

			bankEdit.saveBank();

		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}
	
	private int extractNumTiles(String jsonPyxelDesc) {
		JSONTokener tokener = new JSONTokener(jsonPyxelDesc);
		root = new JSONObject(tokener);
		int numTiles =  root.getJSONObject("tileset").getInt("numTiles");
		return numTiles;
	}
}
