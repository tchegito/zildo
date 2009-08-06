package zildo.gui;

import java.util.ArrayList;

import zildo.client.ClientEngineZildo;
import zildo.client.SpriteDisplay;
import zildo.monde.decors.SpriteEntity;


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
		SpriteDisplay spriteDisplay=ClientEngineZildo.spriteDisplay;
		SpriteEntity entity = spriteDisplay.spawnFont(nBank, nSpr, x, y, visible);
		
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
		SpriteDisplay spriteDisplay=ClientEngineZildo.spriteDisplay;
		for (SpriteEntity entity : this) {
			spriteDisplay.deleteSprite(entity);
		}
		super.clear();
	
		sequenceDrawn=false;
	}

}