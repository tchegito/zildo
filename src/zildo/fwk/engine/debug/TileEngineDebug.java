package zildo.fwk.engine.debug;

import zildo.fwk.gfx.engine.TileEngine;
import zildo.prefs.Constantes;

public class TileEngineDebug extends TileEngine {

	public TileEngineDebug() {
		super();
		
		meshBACK=new TilePrimitiveDebug[Constantes.NB_MOTIFBANK];
		meshFORE=new TilePrimitiveDebug[Constantes.NB_MOTIFBANK];

	}
	
	public void writeFile() {
		for (TilePrimitiveDebug mesh:(TilePrimitiveDebug[]) meshBACK) {
			mesh.writeFile(false, true);
		}
	}
}
