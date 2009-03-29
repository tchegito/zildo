package zildo.gui;

import java.util.ArrayList;

import zildo.fwk.engine.EngineZildo;
import zildo.monde.decors.SpriteEntity;
import zildo.monde.serveur.SpriteManagement;


public class GUISpriteSequence extends ArrayList<SpriteEntity> {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private boolean sequenceDrawn;
//////////////////////////////////////////////////////////////////////
// Construction/Destruction
//////////////////////////////////////////////////////////////////////

	
	public GUISpriteSequence()
	{
		sequenceDrawn=false;
		this.clear();
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// isDrawn
	///////////////////////////////////////////////////////////////////////////////////////
	public boolean isDrawn()
	{
		return sequenceDrawn;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// addSprite
	///////////////////////////////////////////////////////////////////////////////////////
	// -ask sprite management to add sprite in the engine with given parameters
	///////////////////////////////////////////////////////////////////////////////////////
	public SpriteEntity addSprite(int nBank, int nSpr, int x, int y, boolean visible) {
		SpriteManagement spriteManagement=EngineZildo.spriteManagement;
		SpriteEntity entity = spriteManagement.spawnFont(nBank, nSpr, x, y, visible);
		
		this.add(entity);
	
		sequenceDrawn=true;

		return entity;
	}
	public SpriteEntity addSprite(int nBank, int nSpr, int x, int y) {
		return addSprite(nBank, nSpr, x, y, true);
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// clear
	///////////////////////////////////////////////////////////////////////////////////////
	// -ask sprite management to remove each sprites added in this sequence
	///////////////////////////////////////////////////////////////////////////////////////
	public void clear()
	{
		SpriteManagement spriteManagement=EngineZildo.spriteManagement;
		for (SpriteEntity entity : this) {
			spriteManagement.deleteSprite(entity);
		}
		super.clear();
	
		sequenceDrawn=false;
	}

}