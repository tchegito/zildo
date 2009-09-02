package zildo.client;

import org.lwjgl.util.vector.Vector3f;

import zildo.Zildo;
import zildo.client.gui.DialogDisplay;
import zildo.client.gui.GUIDisplay;
import zildo.fwk.FilterCommand;
import zildo.fwk.filter.BilinearFilter;
import zildo.fwk.filter.BlendFilter;
import zildo.fwk.gfx.Ortho;
import zildo.fwk.gfx.PixelShaders;
import zildo.fwk.gfx.engine.SpriteEngine;
import zildo.fwk.gfx.engine.TileEngine;
import zildo.fwk.opengl.OpenGLZildo;
import zildo.monde.Collision;
import zildo.monde.map.Case;
import zildo.monde.map.Point;
import zildo.monde.map.Rectangle;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.persos.Perso;
import zildo.prefs.Constantes;
import zildo.server.EngineZildo;

public class ClientEngineZildo {

	// Link to directX object
	public static OpenGLZildo openGLGestion;
	public static Ortho ortho;
	public static FilterCommand filterCommand;
	
	public static SpriteDisplay spriteDisplay;
	public static MapDisplay mapDisplay;
	
	public static GUIDisplay guiDisplay;
	public static DialogDisplay dialogDisplay;
	
	public static SpriteEngine spriteEngine;
	public static TileEngine tileEngine;
	public static SoundPlay soundPlay;
	public static PixelShaders pixelShaders;

	// Time left to unblock player's moves
	private int waitingScene;
	private int engineEvent;
	
	/**
	 * Should be called after {@link #initializeServer}
	 */
	public void initializeClient(boolean p_awt) {
		
		filterCommand = new FilterCommand();
		guiDisplay=new GUIDisplay();
		dialogDisplay=new DialogDisplay();
		soundPlay=new SoundPlay();

		ortho=new Ortho(Zildo.viewPortX, Zildo.viewPortY);
		
		if (!p_awt) {

			if (ortho.isFBOSupported()) {
				filterCommand.addFilter(new BilinearFilter());
				//filterCommand.addFilter(new BlurFilter());
				filterCommand.addFilter(new BlendFilter());
				//filterCommand.addFilter(new FadeFilter());
				filterCommand.active(null, false);
				filterCommand.active(BilinearFilter.class, true);
			}

			pixelShaders = new PixelShaders();
			if (pixelShaders.canDoPixelShader()) {
				pixelShaders.preparePixelShader();
			}
		}

		spriteEngine = new SpriteEngine();
		tileEngine = new TileEngine();

		spriteDisplay=new SpriteDisplay(spriteEngine);
		mapDisplay=new MapDisplay(null);
		spriteEngine.init(spriteDisplay);

		// GUI
		guiDisplay.setToDisplay_generalGui(true);

		ortho.setOrthographicProjection();
		
	}
	

	/**
	 * Client intialization, with real network
	 * @param p_game
	 * @param p_openGLGestion
	 */
	public ClientEngineZildo(OpenGLZildo p_openGLGestion, boolean p_awt)
	{
		// Lien avec DirectX
		ClientEngineZildo.openGLGestion=p_openGLGestion;
	
		if (!p_awt) {
			initializeClient(p_awt);
		}
		
		waitingScene=0;
		engineEvent=Constantes.ENGINEEVENT_NOEVENT;
	}
	
	
	public void renderFrame(boolean p_editor) {
		if (waitingScene == 0 && !p_editor) {
			// Zildo moves by player
			//playerManagement.manageKeyboard();
		} else {
			// Scene is blocked by non-player animation
	
		}
	

		// Focus camera on player
		if (!p_editor) {
			mapDisplay.centerCamera();
			
			// Is Zildo talking with somebody ?
			if (dialogDisplay.isDialoguing()) {
				dialogDisplay.manageDialog();
			}

		}
		
		// Tile engine
		//TODO: turn on the compteur_animation
		tileEngine.updateTiles(mapDisplay.getCamerax(),mapDisplay.getCameray(),
				mapDisplay.getCurrentMap(),mapDisplay.getCompteur_animation());

		spriteDisplay.updateSpritesClient(mapDisplay.getCamerax(),mapDisplay.getCameray());
		
		ClientEngineZildo.openGLGestion.beginScene();

	
		//// DISPLAY ////
	
		// Display BACKGROUND tiles
		tileEngine.tileRender(true);
	
		// Display BACKGROUND sprites
		spriteEngine.spriteRender(true);
	
		// Display FOREGROUND tiles
		tileEngine.tileRender(false);
	
		// Display FOREGROUND sprites
		spriteEngine.spriteRender(false);
	
        if (Zildo.infoDebug && !p_editor) {
			this.debug();
		}

		guiDisplay.draw();

		openGLGestion.endScene();
		//gfxBasics.EndRendering();
	
		// Engine event management
		//TODO: turn on the events management
		/*
		if (engineEvent == Constantes.ENGINEEVENT_NOEVENT && mapManagement.isChangingMap()) {
			// Changing map : 1/3 we launch the fade out
			engineEvent=Constantes.ENGINEEVENT_CHANGINGMAP_FADEOUT;
			waitingScene=1;
			guiManagement.fadeOut();
		} else if (engineEvent == Constantes.ENGINEEVENT_CHANGINGMAP_FADEOUT && guiManagement.isFadeOver()) {
            // Changing map : 2/3 we load the new map and launch the fade in
            engineEvent = Constantes.ENGINEEVENT_CHANGINGMAP_FADEIN;
            mapManagement.processChangingMap();
            Area map = mapManagement.getCurrentMap();
            mapDisplay.setCurrentMap(map);
			spriteDisplay.initCamera();
            guiManagement.fadeIn();
        } else if (engineEvent == Constantes.ENGINEEVENT_CHANGINGMAP_FADEIN && guiManagement.isFadeOver()) {
			// Changing map : 3/3 we unblock the player
			engineEvent=Constantes.ENGINEEVENT_NOEVENT;
			waitingScene=0;
		}
		*/

	}
	
	public void cleanUp() {
		filterCommand.cleanUp();
		pixelShaders.cleanUp();
		tileEngine.cleanUp();
		spriteEngine.cleanUp();
	}
	
	
	void debug()
	{
		int fps=(int) openGLGestion.getFPS();
		
		ortho.drawText(1,50,"fps="+fps, new Vector3f(0.0f, 0.0f, 1.0f));
		
		SpriteEntity zildo=spriteDisplay.getZildo();
		ortho.drawText(1,80,"zildo: "+zildo.x, new Vector3f(1.0f, 0.0f, 1.0f));
		ortho.drawText(43,86,""+zildo.y, new Vector3f(1.0f, 0.0f, 1.0f));
		
		// Debug collision
		if (EngineZildo.collideManagement != null && Zildo.infoDebugCollision) {
			for (Collision c : EngineZildo.collideManagement.getTabColli()) {
				if (c != null) {
					int rayon=c.getCr();
					int color=15;
					Perso damager=c.getPerso();
					if (damager != null && damager.getInfo() == 1) {
						color=20;
					}
					if (c.size==null) {
						ortho.box(c.getCx()-rayon/2-mapDisplay.getCamerax(), 
								c.getCy()-rayon/2-mapDisplay.getCameray(), rayon*2, rayon*2,color, null);
					} else {
						Point center=new Point(c.cx-mapDisplay.getCamerax(), c.cy-mapDisplay.getCameray());
						Rectangle rect=new Rectangle(center, c.size);
						ortho.box(rect, color, null);
					}
				}
			}
			int x=(int) zildo.x-4;
			int y=(int) zildo.y-10;
			ortho.box(x-3-mapDisplay.getCamerax(), y-3-mapDisplay.getCameray(), 16, 16, 12, null);
		}
		
		if (Zildo.infoDebugCase) {
			for (int y=0;y<20;y++) {
				for (int x=0;x<20;x++) {
					int cx=x+mapDisplay.getCamerax() / 16;
					int cy=y+mapDisplay.getCameray() / 16;
					int px=x*16 - mapDisplay.getCamerax() % 16;
					int py=y*16 - mapDisplay.getCameray() % 16;
					if (cy < 64 && cx < 64) {
						Case c=EngineZildo.mapManagement.getCurrentMap().get_mapcase(cx, cy+4);
						int onmap=EngineZildo.mapManagement.getCurrentMap().readmap(cx, cy);
						ortho.drawText(px, py+4, ""+c.getZ(), new Vector3f(0,1,0));
						ortho.drawText(px, py, ""+onmap, new Vector3f(1,0,0));
					}
				}
			}
		}
	}

    public void setOpenGLGestion(OpenGLZildo p_openGLGestion) {
        	openGLGestion = p_openGLGestion;
    }

}
