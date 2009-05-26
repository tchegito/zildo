package zildo.fwk.engine;

import org.lwjgl.util.vector.Vector3f;

import zildo.Zildo;
import zildo.fwk.FilterCommand;
import zildo.fwk.SoundManagement;
import zildo.fwk.bank.SpriteBank;
import zildo.fwk.engine.debug.TileEngineDebug;
import zildo.fwk.filter.BilinearFilter;
import zildo.fwk.filter.BlendFilter;
import zildo.fwk.gfx.Ortho;
import zildo.fwk.gfx.PixelShaders;
import zildo.fwk.gfx.engine.SpriteEngine;
import zildo.fwk.opengl.OpenGLZildo;
import zildo.gui.GUIManagement;
import zildo.monde.Angle;
import zildo.monde.Area;
import zildo.monde.Collision;
import zildo.monde.Game;
import zildo.monde.client.MapDisplay;
import zildo.monde.client.SpriteDisplay;
import zildo.monde.dialog.DialogManagement;
import zildo.monde.persos.Perso;
import zildo.monde.persos.PersoNJ;
import zildo.monde.persos.PersoZildo;
import zildo.monde.persos.utils.PersoDescription;
import zildo.monde.serveur.CollideManagement;
import zildo.monde.serveur.MapManagement;
import zildo.monde.serveur.PersoManagement;
import zildo.monde.serveur.PlayerManagement;
import zildo.monde.serveur.SpriteManagement;
import zildo.prefs.Constantes;

public class EngineZildo {

	// Time left to unblock player's moves
	private int waitingScene;
	private int engineEvent;
	private int a;

	// Link to directX object
	private OpenGLZildo openGLGestion;
	public static Ortho ortho;
	public static FilterCommand filterCommand;
	
	// Server
	public static SpriteManagement spriteManagement;
	public static MapManagement mapManagement;
	public static CollideManagement collideManagement;
	public static PlayerManagement playerManagement;
	public static GUIManagement	guiManagement;
	public static PersoManagement persoManagement;
	public static DialogManagement dialogManagement;
	public static SoundManagement soundManagement;
	public static PixelShaders pixelShaders;
	public static SpriteEngine spriteEngine;
	public static TileEngineDebug tileEngine;
	
	// Client
	public SpriteDisplay spriteDisplay;
	public MapDisplay mapDisplay;
	
	private boolean wantDebug=false;
	
	private static int timeToWait=0;
	private static int nFramesToWait=0;
	
	// For debug
	public static int extraSpeed=1;
	
	public static void freeze() {
		timeToWait=3000;
		nFramesToWait=3;
	}
	
	public void waitIfFreezed() {
		if (nFramesToWait>0) {
			try {
				Thread.sleep(timeToWait);
			} catch (Exception e) {
				
			}
			nFramesToWait--;
		}
	}
	
	private void initializeServer(Game p_game) {
		// Inits de départ
		spriteManagement=new SpriteManagement();
		mapManagement=new MapManagement();
		persoManagement=new PersoManagement();
		dialogManagement=new DialogManagement();
		soundManagement=new SoundManagement();
		collideManagement=new CollideManagement();
	
		a=0;
	
		// Charge une map
		String mapName=p_game.mapName;
	
		// Zildo should be the first perso ( tab_perso[0] )
		PersoZildo zildo=new PersoZildo();
		mapManagement.charge_map(mapName);
		spriteManagement.spawnPerso( zildo );

		// Extra:
		Perso perso=new PersoNJ();
		//perso.setNSpr(0);
		perso.x=45.0f;
		perso.y=280.0f;
		perso.z=0;
		perso.setNom("tigrou");
		perso.setVisible(true);
		perso.setInfo(0);
		perso.setPv(1);
		perso.setQuel_spr(PersoDescription.SORCIER_CAGOULE);
		perso.setNBank(SpriteBank.BANK_PNJ);
		perso.setAngle(Angle.NORD);
		perso.setDx(0);
		perso.setDy(0);
		spriteManagement.spawnPerso(perso);
	
		int r=dialogManagement.getN_phrases()+1;
		dialogManagement.addSentence("Je suis le grand prophete Ben Cristofalizm ! Mon pouvoir est immense !");
		dialogManagement.addSentence("Je fais pleuvoir des avenants ! A chaque saison des pluies, j'arrose l'elite de la societe !");
		dialogManagement.addSentence("Mais il faut etre patient, je sors ma plume en 3 mois et demi.");
		dialogManagement.addSentence("Un conseil, venez me voir au moment du salon de l'auto. Je serai de bonne humeur.");
		dialogManagement.addSentence("Ohh putainnnn ...");
		dialogManagement.addBehavior("tigrou", new short[]{(short)r, (short) (r+1), (short) (r+2), (short) (r+3), (short) (r+4)});
		
		playerManagement=new PlayerManagement(zildo);
	
		/*
		spriteManagement.spawnElement(BANK_ZILDO,4,30,90);
		spriteManagement.spawnSprite(BANK_PNJ,0,30,150);
		spriteManagement.spawnSprite(BANK_PNJ,0,30,140);
		spriteManagement.spawnSprite(BANK_PNJ,0,30,130);
		spriteManagement.spawnSpriteGeneric(SPR_FUMEE);
		*/
	
		waitingScene=0;
		engineEvent=Constantes.ENGINEEVENT_NOEVENT;
	}

	/**
	 * Should be called after {@link #initializeServer}
	 */
	public void initializeClient() {
		
		// Cheat ! We can't have the map at this moment, in real client-server, but this will come later.
		Area map=mapManagement.getCurrentMap();

		guiManagement=new GUIManagement();
		
		spriteEngine = new SpriteEngine();
		tileEngine = new TileEngineDebug();
		tileEngine.prepareTiles(map);
		
		filterCommand = new FilterCommand();
		/*
		filterCommand.addFilter(new BilinearFilter());
		//filterCommand.addFilter(new BlurFilter());
		filterCommand.addFilter(new BlendFilter());
		//filterCommand.addFilter(new FadeFilter());
		filterCommand.active(null, false);
		filterCommand.active(BilinearFilter.class, true);
		*/
		pixelShaders = new PixelShaders();
		if (pixelShaders.canDoPixelShader()) {
			pixelShaders.preparePixelShader();
		}
		ortho=new Ortho(Zildo.viewPortX, Zildo.viewPortY);
		
		spriteEngine.startInitialization();
		
		spriteEngine.endInitialization();

		spriteDisplay=new SpriteDisplay(spriteEngine);
		mapDisplay=new MapDisplay(map);
		
		// GUI
		guiManagement.setToDisplay_generalGui(true);

		ortho.setOrthographicProjection();
		
	}
	

	/**
	 * Server intialization, with real network (doesn't work for now)
	 * @param p_game
	 * @param p_openGLGestion
	 */
	public EngineZildo(OpenGLZildo p_openGLGestion)
	{
		// Lien avec DirectX
		openGLGestion=p_openGLGestion;
	
		//initializeServer(p_game);
	}
	
	public EngineZildo(Game p_game) {
		initializeServer(p_game);
	}
	
	public void cleanUp() {
		filterCommand.cleanUp();
		pixelShaders.cleanUp();
		tileEngine.cleanUp();
		spriteEngine.cleanUp();
	}
	
	public void finalize()
	{
		// L'ordre des suppression est TRES important ! En effet, le vidage des cartes passe
		// par le vidage de tous les sprites/persos qui y sont référencés. Donc on a besoin
		// d'avoir à ce moment là l'objet 'spriteManagement'.
		/*
		delete mapManagement;
		delete spriteManagement;
		delete persoManagement;
		delete playerManagement;
		delete guiManagement;
		delete dialogManagement;
		delete soundManagement;
		delete collideManagement;
	*/
	}	
	
	// Main function : Called by DX9 layer class
	// -Receive a buffer containing keyboard state
	public void render()
	{
		if (waitingScene == 0) {
			// Zildo moves by player
			playerManagement.manageKeyboard();
		} else {
			// Scene is blocked by non-player animation
	
		}
	
		openGLGestion.beginScene();

		// On centre le caméra sur le joueur
		mapDisplay.centerCamera();
	
		// Do sprite's stuff
		// -move camera
		// -animate sprites
		// -fill sort array
		collideManagement.initFrame();
		guiManagement.draw();

		spriteManagement.updateSprites();
		collideManagement.manageCollisions();
	
		// Do map's stuff :
		// -move camera
		// -animate tiles
		mapManagement.updateMap();
	
	
		//// DISPLAY ////
	
		// Display BACKGROUND tiles
		tileEngine.tileRender(true);
	
		// Display BACKGROUND sprites
		spriteEngine.spriteRender(true);
	
		// Display FOREGROUND tiles
		tileEngine.tileRender(false);
	
		// Display FOREGROUND sprites
		spriteEngine.spriteRender(false);
	
		// Is Zildo talking with somebody ?
		if (dialogManagement.isDialoguing()) {
			dialogManagement.manageDialog();
		}
	
		if (Zildo.infoDebug) {
			this.debug();
		}

		filterCommand.doFilter();

		openGLGestion.endScene();
		//gfxBasics.EndRendering();
	
		// Engine event management
		if (engineEvent == Constantes.ENGINEEVENT_NOEVENT && mapManagement.isChangingMap()) {
			// Changing map : 1/3 we launch the fade out
			engineEvent=Constantes.ENGINEEVENT_CHANGINGMAP_FADEOUT;
			waitingScene=1;
			guiManagement.fadeOut();
		} else if (engineEvent == Constantes.ENGINEEVENT_CHANGINGMAP_FADEOUT && guiManagement.isFadeOver()) {
			// Changing map : 2/3 we load the new map and launch the fade in
			engineEvent=Constantes.ENGINEEVENT_CHANGINGMAP_FADEIN;
			mapManagement.processChangingMap();
			guiManagement.fadeIn();
		} else if (engineEvent == Constantes.ENGINEEVENT_CHANGINGMAP_FADEIN && guiManagement.isFadeOver()) {
			// Changing map : 3/3 we unblock the player
			engineEvent=Constantes.ENGINEEVENT_NOEVENT;
			waitingScene=0;
		}
	
		a++;
		if (a>300)
			a=0;
	
	
		if (wantDebug) {
			// On n'enregistre qu'une fois les fichiers
			((TileEngineDebug)tileEngine).writeFile();
			wantDebug=false;
		}
		
		waitIfFreezed();
	}
	
	void debug()
	{
		int fps=(int) openGLGestion.getFPS();
		
		ortho.drawText(1,50,"fps="+fps, new Vector3f(0.0f, 0.0f, 1.0f));
		
		PersoZildo zildo=persoManagement.getZildo();
		ortho.drawText(1,80,"zildo: "+zildo.getX(), new Vector3f(1.0f, 0.0f, 1.0f));
		ortho.drawText(43,86,""+zildo.getY(), new Vector3f(1.0f, 0.0f, 1.0f));
		
		
		// Debug collision
		for (Collision c : EngineZildo.collideManagement.getTabColliz()) {
			if (c != null) {
				int rayon=c.getCr();
				EngineZildo.ortho.box(c.getCx()-rayon/2-mapDisplay.getCamerax(), 
						c.getCy()-rayon/2-mapDisplay.getCameray(), rayon*2, rayon*2,15, null);
			}
		}
		
		for (Collision c : EngineZildo.collideManagement.getTabColli()) {
			if (c != null) {
				int rayon=c.getCr();
				EngineZildo.ortho.box(c.getCx()-rayon/2-mapDisplay.getCamerax(), 
						c.getCy()-rayon/2-mapDisplay.getCameray(), rayon*2, rayon*2,20, null);
			}
		}
		
		/*
		DX9Gestion* dx9Stuff=(DX9Gestion*)dxStuff;
	
		GFXBasics* gfxBasics = dx9Stuff.gfxBasics;
	
		// Display sprite banks information
	
		for (int i=0;i<spriteManagement.n_bankspr;i++)
		{
			SpriteBank* sprBank=spriteManagement.getSpriteBank(i);
			String mess="Bank      ";
			mess.ConvIntToStr(i,5);
			mess.ConvIntToStr(sprBank.nSprite,7);
			gfxBasics.aff_texte(0,i*20,mess.Convchar());
		}
	
		// Display main character informations
		Perso* zildo=this.persoManagement.get_zildo();
		String message="Zildo:x=       y=       dx=    dz=     ";
		message.ConvFloatToStr(zildo.x,8);
		message.ConvFloatToStr(zildo.y,17);
		message.ConvIntToStr(zildo.dx,27);
		message.ConvIntToStr(zildo.dz,34);
		gfxBasics.aff_texte(0,80,message);
		String messageMvt="Mouvement ";
		if (zildo.mouvement==MOUVEMENT_SAUTE)
			messageMvt+="Saute";
		else if (zildo.mouvement==MOUVEMENT_VIDE)
			messageMvt+="Vide";
		else if (zildo.mouvement==MOUVEMENT_ATTAQUE_EPEE)
			messageMvt+="Attaque épée";
		else
			messageMvt+="Indetermine";
		gfxBasics.aff_texte(0,100,messageMvt);
	
		// On calcule les FPS
		if (perf_flag)
			QueryPerformanceCounter((LARGE_INTEGER*) &cur_time);
		else
			cur_time=timeGetTime();
	
		//Calcule le temps écoulé
		time_elapsed=(cur_time-last_time)*time_scale;
	
		//Enregistre l'heure de l'image
		last_time=cur_time;
	
		// On affiche les FPS
		String messageFps="FPS :      ";
		messageFps.ConvFloatToStr(1/time_elapsed,5);
		gfxBasics.aff_texte(0,150,messageFps);
	
		// Infos sur les sprites
		/*SpriteEngine* spriteEngine=(SpriteEngine*)dx9Stuff.spriteEngine;
		int nPoints=spriteEngine.meshSprites[0].nPoints;
		String messageSprites="Nombre de sprites :    ";
		messageSprites.ConvIntToStr(nPoints,20);
		gfxBasics.aff_texte(0,180,messageSprites);
	
		// Infos sur les sprites
		TileEngine* tileEngine=(TileEngine*)dx9Stuff.tileEngine;
		int nPoints0=tileEngine.meshBACK[0].nPoints +
					 tileEngine.meshBACK[1].nPoints +
					 tileEngine.meshBACK[2].nPoints +
					 tileEngine.meshBACK[3].nPoints +
					 tileEngine.meshBACK[4].nPoints +
					 tileEngine.meshBACK[5].nPoints +
					 tileEngine.meshBACK[6].nPoints +
					 tileEngine.meshBACK[7].nPoints;
		String messageTile="Nombre de points :       ";
		messageTile.ConvIntToStr(nPoints0,20);
		gfxBasics.aff_texte(0,190,messageTile);
		// Affiche les coordonnées de chaque sprite
	
		int nPerso=0;
		for (SpriteEntity sprite : spriteManagement.listSprites) {
			int x=sprite.getScrX();
			int y=sprite.getScrY();
			nPerso=0;
			if (sprite.getEntityType()==SpriteEntity.ENTITYTYPE_PERSO) {
				Sprite model=sprite.getSprModel();
				y+=model.getTaille_y() - 3;
				Perso perso=(Perso) sprite;
				nPerso=perso.getQuel_spr();
				if (perso.getNBank()==SpriteBank.BANK_PNJ2) {
					nPerso+=128;
				}
			}
			if (x>0 && x<320 && y>0 && y<240) {
				String coordY="y=   ";
				coordY.ConvIntToStr(y,2);
				//gfxBasics.aff_texte(x,y-15,coordY);
				String nPersoString="n=   ";
				nPersoString.ConvIntToStr(nPerso,2);
				gfxBasics.aff_texte(x,y+10,nPersoString);
			}
		}
	
	*/
	}
	
	public synchronized void serverSide() {
		// Animate the world
		spriteManagement.updateSprites();
		collideManagement.manageCollisions();
	
		// Do map's stuff :
		// -move camera
		// -animate tiles
		mapManagement.updateMap();		
	}
	
	public synchronized void clientSide(boolean p_keyboardManagement) {
		if (waitingScene == 0 && p_keyboardManagement) {
			// Zildo moves by player
			playerManagement.manageKeyboard();
		} else {
			// Scene is blocked by non-player animation
	
		}
	
		// Tile engine
		tileEngine.updateTiles(mapDisplay.getCamerax(),mapDisplay.getCameray(),
				mapDisplay.getCurrentMap(),mapManagement.getCompteur_animation());

		spriteDisplay.updateSpritesClient(spriteManagement.getSpriteEntities(), 
				mapDisplay.getCamerax(),mapDisplay.getCameray());
		
		openGLGestion.beginScene();

		// On centre le caméra sur le joueur
		mapDisplay.centerCamera();
	
		// Do sprite's stuff
		// -move camera
		// -animate sprites
		// -fill sort array
		collideManagement.initFrame();
		guiManagement.draw();
	
	
		//// DISPLAY ////
	
		// Display BACKGROUND tiles
		tileEngine.tileRender(true);
	
		// Display BACKGROUND sprites
		spriteEngine.spriteRender(true);
	
		// Display FOREGROUND tiles
		tileEngine.tileRender(false);
	
		// Display FOREGROUND sprites
		spriteEngine.spriteRender(false);
	
		// Is Zildo talking with somebody ?
		if (dialogManagement.isDialoguing()) {
			dialogManagement.manageDialog();
		}
	
		if (Zildo.infoDebug) {
			this.debug();
		}

		filterCommand.doFilter();

		openGLGestion.endScene();
		//gfxBasics.EndRendering();
	
		// Engine event management
		if (engineEvent == Constantes.ENGINEEVENT_NOEVENT && mapManagement.isChangingMap()) {
			// Changing map : 1/3 we launch the fade out
			engineEvent=Constantes.ENGINEEVENT_CHANGINGMAP_FADEOUT;
			waitingScene=1;
			guiManagement.fadeOut();
		} else if (engineEvent == Constantes.ENGINEEVENT_CHANGINGMAP_FADEOUT && guiManagement.isFadeOver()) {
			// Changing map : 2/3 we load the new map and launch the fade in
			engineEvent=Constantes.ENGINEEVENT_CHANGINGMAP_FADEIN;
			mapManagement.processChangingMap();
			guiManagement.fadeIn();
		} else if (engineEvent == Constantes.ENGINEEVENT_CHANGINGMAP_FADEIN && guiManagement.isFadeOver()) {
			// Changing map : 3/3 we unblock the player
			engineEvent=Constantes.ENGINEEVENT_NOEVENT;
			waitingScene=0;
		}

	}
	
	void loadMap(String mapname)
	{
		// Clear existing entities
		persoManagement.clearPersos();
	
		// Load map
		mapManagement.charge_map(mapname);
	}
	
	public void setWaitingScene(int time) {
		waitingScene=time;
	}

	public OpenGLZildo getOpenGLGestion() {
		return openGLGestion;
	}

	public void setOpenGLGestion(OpenGLZildo p_openGLGestion) {
		openGLGestion = p_openGLGestion;
	}
}