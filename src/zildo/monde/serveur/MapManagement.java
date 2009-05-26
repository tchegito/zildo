package zildo.monde.serveur;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import zildo.fwk.EasyFile;
import zildo.fwk.IntSet;
import zildo.fwk.bank.MotifBank;
import zildo.fwk.bank.SpriteBank;
import zildo.fwk.engine.EngineZildo;
import zildo.fwk.gfx.engine.TileEngine;
import zildo.monde.Angle;
import zildo.monde.Area;
import zildo.monde.Case;
import zildo.monde.ChainingPoint;
import zildo.monde.Zone;
import zildo.monde.decors.Element;
import zildo.monde.dialog.DialogManagement;
import zildo.monde.persos.Perso;
import zildo.monde.persos.PersoGarde;
import zildo.monde.persos.PersoGardeVert;
import zildo.monde.persos.PersoNJ;
import zildo.monde.persos.PersoVolant;
import zildo.monde.persos.utils.MouvementPerso;
import zildo.monde.persos.utils.MouvementZildo;
import zildo.monde.persos.utils.PersoDescription;
import zildo.prefs.Constantes;

//////////////////////////////////////////////////////////////////////
// MapManagement
//////////////////////////////////////////////////////////////////////
// Class managing low-level problematics about map.
// -Load and clean map
// -Load the 'motif' banks at start
//////////////////////////////////////////////////////////////////////



public class MapManagement {

	protected Logger logger=Logger.getLogger("MapManagement");

	final IntSet walkable =new IntSet(1,6,19,23,27,35,40,41,42,43,49,50,51,52,53,54,55,56,
			57,58,59,60,61,62,63,64,65,66,67,68,69,70,71,72,73,74,75,76,89,90,
			91,99,139,140,141,142,143,144,145,146,
			166,168,170,171,172,174,175,176,177,178,183);

	final IntSet walkable2=new IntSet(22,23,25,34,35,36,37,58,59,61,67,68,71,72,73,  // +256
			77,78,79,80,81,82,83,84,85,86,87,88,89,94,107,108,114,115,116,117,
			118,119,120,121,122,123,124,126,127,128,130,139,140,141,142,143,144,
			145,146,147,168,169,173,174,175,176,177,178);

	final IntSet walkable3=new IntSet(0,33,34,35,36,37,38,39,40,41,42,43,44,45,94,95,96, // +512
			97,98,99,100,101,102,217,218,219,220,221,222,240);

	final IntSet walkable4=new IntSet(9,37,38,39,41,42,43,44,45,46,50,51,52,53,78,79,80,81, // +768
			82,83,84,155,156,157,158,159,160);

	final IntSet walkable5=new IntSet(45,81,82,135,137,147,173,178,210,212,213,227,228,229, // +1024
			230,231,239,240,241);
	
	
    private int n_banquemotif;					// Nombre de banque de motifs en mémoire
	private Area currentMap;

    private boolean phasem;
	private byte compteur_animation;
	
	ChainingPoint changingMapPoint;
	
	List<MotifBank> motifBanks;
	
	static public String[] tileBankNames={"foret1.dec",
			"village.dec",
			"maison.dec",
			"grotte.dec",
			"foret2.dec",
			"foret3.dec",
			"foret4.dec",
			"palais1.dec"};

	public MapManagement()
	{
		compteur_animation=0;
		
		// Init variables
		currentMap=null;
	
		// Load graphs
		motifBanks=new ArrayList<MotifBank>();
		this.charge_tous_les_motifs();
	}
	
	public void finalize()
	{
		this.deleteCurrentMap();
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// charge_tous_les_motifs
	///////////////////////////////////////////////////////////////////////////////////////
	// Load every tile banks
	///////////////////////////////////////////////////////////////////////////////////////
	void charge_tous_les_motifs()
	{
		n_banquemotif=0;
		for (int i=0;i<8;i++)
			this.charge_motifs(tileBankNames[i]);
	
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// charge_motifs
	///////////////////////////////////////////////////////////////////////////////////////
	// IN:filename to load as a tile bank
	///////////////////////////////////////////////////////////////////////////////////////
	void charge_motifs(String filename)
	{
		MotifBank motifBank=new MotifBank();
		String chemin=Constantes.DATA_PATH;
		chemin+=filename;
	
		motifBank.charge_motifs(chemin);
	
		motifBanks.add(motifBank);
		
		// Relase memory allocated for tile graphics, because it's in directX memory now. 
		//delete motifBank;
	
		// Increase number of loaded banks
		n_banquemotif++;
	}
	
	public MotifBank getMotifBank(int n) {
		return motifBanks.get(n);
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// deleteCurrentMap
	///////////////////////////////////////////////////////////////////////////////////////
	void deleteCurrentMap() {
		//delete currentMap;
	
		EngineZildo.guiManagement.clean();
	
		EngineZildo.persoManagement.clearPersosWithoutZildo();
	
		EngineZildo.dialogManagement.clearDialogs();
	
		EngineZildo.spriteManagement.clearSpritesWithoutZildo();
	
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// charge_map
	///////////////////////////////////////////////////////////////////////////////////////
	// IN:filename to load as a map
	///////////////////////////////////////////////////////////////////////////////////////
	// Destroy any data referring to current map, and given one
	///////////////////////////////////////////////////////////////////////////////////////
	public void charge_map(String mapname)
	{
		// Remove previous map
		if (currentMap != null) {
			this.deleteCurrentMap();
		}
	
		// Load a new one
		currentMap=loadMapFile(mapname);
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// linkTwoMaps
	///////////////////////////////////////////////////////////////////////////////////////
	Area linkTwoMaps(String mapname)
	{
		// Load next map
		Area secondMap=loadMapFile(mapname);
		// Put the current map next to the second one, so we have both on a double area
		currentMap.translatePoints(currentMap.getDim_x(),0,secondMap);
		return secondMap;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// loadMapFile
	///////////////////////////////////////////////////////////////////////////////////////
	// IN:filename to load as a map
	///////////////////////////////////////////////////////////////////////////////////////
	Area loadMapFile(String mapname) 
	{ 
		System.out.println("Loading "+mapname);
		SpriteManagement spriteManagement=EngineZildo.spriteManagement;
	
		Area map=new Area();
	
		String chemin=Constantes.DATA_PATH;
		chemin+=mapname;
		chemin+=".MAP";
	
		// Infos de base
		EasyFile file=new EasyFile(chemin);
		map.setDim_x(file.readUnsignedByte());
		map.setDim_y(file.readUnsignedByte());
		map.setN_persos(file.readUnsignedByte());
		map.setN_sprites(file.readUnsignedByte());
		map.setN_pe(file.readUnsignedByte());
		
		// La map
		for (int i=0;i<map.getDim_y();i++)
			for (int j=0;j<map.getDim_x();j++)
			{
				//System.out.println("x="+j+"  y="+i);
				Case temp=new Case();
				temp.setN_motif(file.readUnsignedByte());
				temp.setN_banque(file.readUnsignedByte());
				temp.setN_motif_masque(file.readUnsignedByte());
				temp.setN_banque_masque(file.readUnsignedByte());
			
				map.set_mapcase(j,i+4,temp);
		
				if (temp.getN_motif()==99 && temp.getN_banque()==1) {
					// Fumée de cheminée
					spriteManagement.spawnSpriteGeneric(Element.SPR_FUMEE,j*16,i*16,0, null);
				}
			}
		 /*
	     with spr do begin
	      x:=j*16+16;
	      y:=i*16+28;
	      z:=16;
	      vx:=0.3+random(5)*0.01;vy:=0;vz:=0;
	      ax:=-0.01;ay:=0;az:=0.01+random(5)*0.001;
	      quelspr:=6;
	     end;
	     spawn_sprite(spr);
	    end;*/
	
		// Les P.E
		ChainingPoint pe;
		if (map.getN_pe()!=0) {
			for (int i=0;i<map.getN_pe();i++) {
				pe=new ChainingPoint();
				pe.setPx(file.readUnsignedByte());
				pe.setPy(file.readUnsignedByte());
				pe.setMapname(file.readString(9));
				map.addPointEnchainement(pe);
			}
		}
	
		// Les sprites
		if (map.getN_sprites()!=0) {
			for (int i=0;i<map.getN_sprites();i++) {
				int x=((int)(file.readUnsignedByte()) << 8) + file.readUnsignedByte();
				int y=((int)file.readUnsignedByte() << 8) + file.readUnsignedByte();
				short nSpr;
				nSpr=file.readUnsignedByte();
				spriteManagement.spawnSprite(SpriteBank.BANK_ELEMENTS,nSpr,x,y);
			}
		}
	
		// Les persos
		if (map.getN_persos()!=0) {
			for (int i=0;i<map.getN_persos();i++) {
				PersoNJ perso;
				int x=((int)file.readUnsignedByte() << 8) + file.readUnsignedByte();
				int y=((int)file.readUnsignedByte() << 8) + file.readUnsignedByte();
				int z=((int)file.readUnsignedByte() << 8) + file.readUnsignedByte();
	
				PersoDescription desc=PersoDescription.fromNSpr(file.readUnsignedByte());
				
				switch (desc) {
				case BAS_GARDEVERT:
					perso=new PersoGardeVert();break;
				case GARDE_CANARD:
					perso=new PersoGarde();break;
				case CORBEAU:
				case SPECTRE:
					perso=new PersoVolant();break;
				default:
					perso=new PersoNJ();break;
				}
				perso.setX((float)x);
				perso.setY((float)y);
				perso.setZ((float)z);
				perso.setQuel_spr(desc);
				perso.setInfo(file.readUnsignedByte());
				perso.setEn_bras(file.readUnsignedByte());
				perso.setQuel_deplacement(MouvementPerso.fromInt((int) file.readUnsignedByte()));
				perso.setAngle(Angle.fromInt(file.readUnsignedByte()));
				
				perso.setNBank(SpriteBank.BANK_PNJ);
				perso.setNom(file.readString(9));
				Zone zo=new Zone();
				zo.setX1(map.roundAndRange(perso.getX()-16*5, Area.ROUND_X));
				zo.setY1(map.roundAndRange(perso.getY()-16*5, Area.ROUND_Y));
				zo.setX2(map.roundAndRange(perso.getX()+16*5, Area.ROUND_X));
				zo.setY2(map.roundAndRange(perso.getY()+16*5, Area.ROUND_Y));
				perso.setZone_deplacement(zo);
				perso.setPv(3);
				perso.setDx(-1);
				perso.setMouvement(MouvementZildo.MOUVEMENT_VIDE);
	
				if (perso.getQuel_spr().first() >= 128) {
					perso.setNBank(SpriteBank.BANK_PNJ2);
				}

				perso.initPersoFX();
	
				spriteManagement.spawnPerso(perso);
	
			}
		}
	
		// Les Phrases
		int n_phrases=0;
		if (!file.eof()) {
			n_phrases=file.readUnsignedByte();
			if (n_phrases > 0) {
				DialogManagement dialogManagement=EngineZildo.dialogManagement;
				// On lit les phrases
				for (int i=0;i<n_phrases;i++) {
		
					String phrase=file.readString();
					dialogManagement.addSentence(phrase);
				}
				if (!file.eof()) {
					while (!file.eof()) {
						// On lit le nom
						String nomPerso=file.readString(9);
						// On lit le comportement
						short[] comportement=new short[10];
						file.readUnsignedBytes(comportement, 0, 10);
						dialogManagement.addBehavior(nomPerso,comportement);
					}
				}
			}
		}
	
		map.setName(mapname);
		
		this.logger.info("Map loaded: "+mapname);
		return map;
	}

	///////////////////////////////////////////////////////////////////////////////////////
	// isWalkable
	///////////////////////////////////////////////////////////////////////////////////////
	// IN:motif de map (motif+256*banque)
	// OUT:True si c'est un motif franchissable par les persos
	//     False si c'est un obstacle
	///////////////////////////////////////////////////////////////////////////////////////
	public boolean isWalkable(int on_map)
	{
		if (on_map<256)
			return walkable.contains(on_map);
		else if (on_map<512)
			return walkable2.contains(on_map-256);
		else if (on_map<768)
			return walkable3.contains(on_map-512);
		else if (on_map<1024)
			return walkable4.contains(on_map-768);
		else
			return walkable5.contains(on_map-1024);
	}
	
	// Returns true if given character collides with the map
	/*boolean collide(float tx,float ty,int quelperso)
	{
		int roundx=(int)(tx*10)/10;
		int roundy=(int)(ty*10)/10;
		return this.collide(roundx,roundy,quelperso);
		//return this.collide((int)tx,(int)ty,quelperso);
	}*/
	
	///////////////////////////////////////////////////////////////////////////////////////
	// collide
	///////////////////////////////////////////////////////////////////////////////////////
	// IN:map coordinates and a character to deal with
	// OUT:TRUE if the given character collides with something or somebody
	///////////////////////////////////////////////////////////////////////////////////////
	public boolean collide(int tx,int ty,Element quelElement)
	{
		int mx,my;            // Position map
	    int modx,mody;
	    int on_map;           // La case où se déplace le joueur
	    boolean result;
	
		final int[] tab_add={-1,-1,1,1,-1};
	
		if (tx<0 || ty<0 || 
		    tx>(currentMap.getDim_x()-1)*16 ||
			ty>(currentMap.getDim_y()-1)*16)
			// On empêche la collision sur les bords de cartes
			return false;
	
		// On teste les 4 coins d'un carré de 4x4
		result=false;
		for (int i=0;i<4;i++)
		{
			mx=(tx+4*tab_add[i]);
			my=(ty+2*tab_add[i+1]);
			on_map=currentMap.readmap((mx / 16),(my / 16));
			modx=mx % 16;
			mody=my % 16;
		
			// Les portes sont toujours sauvées selon le même schéma, donc un simple
			// décalage dans le numéro de motif suffit pour toutes les traiter
			if (on_map>=590 && on_map<=605)
				on_map-=16;
		
			switch (on_map)
			{
				// Mi diagonal mi horizontal : collines bord du haut
				case 2:if ((modx<8 && mody>=16-modx) || (modx>7 && mody>7)) return true;
					break;
				case 4:if ((modx>7 && mody>modx) || (modx<8 && mody>7)) return true;
					break;
				case 47:if ((modx>7 && mody>=16-modx) || (modx<8 && mody<8)) return true;
					break;
				case 48:if ((modx<8 && mody>modx) || (modx>7 && mody>7))  return true;
					break;
				// Les bords en diagonal (ex:collines)
				case 5: case 8: case 35: case 79: case 106: case 723: case 787: case 917:
					if (mody > modx) return true;
					break;
				case 7: case 0: case 27: case 77: case 104: case 721: case 788: case 915:
					if (mody >= 16-modx) return true;
					break;
				case 13: case 23: case 84: case 102: case 156: case 719: case 786: case 913:
					if (mody < modx) return true;
					break;
				case 11: case 19: case 85: case 100: case 157: case 717: case 785: case 911:
					if (mody < 16-modx) return true;
					break;
		
					
				// Les parties verticales
				case 101: case 323: case 324: case 327: case 328:
				case 363: case 364: case 385: case 405: case 408:
				case 578: case 579: case 586: case 587:
				case 663: case 664: case 665: case 670: case 671: case 676: case 677:
				case 865: case 870: case 912:
				case 1037: case 1102: case 1103: case 1107: case 1108:
					if (mody <8) return true;
					break;
		
				case 3: case 105: case 329: case 580: case 581: case 588: case 589:
				case 867: case 872: case 916:
				case 1162: case 1163: case 1164: case 1175: case 1192: case 1201: case 1203:
					if (mody>7) return true;
					break;
		
				// Les parties horizontales
				case 39: case 107: case 179: case 185: case 194: case 278: case 314: case 424:
				case 574: case 576: case 582: case 584:
				case 749: /*case 751:*/ case 753: case 755:
				case 667: case 669:
				case 857: case 859: case 861: case 863: case 873: case 879: case 918:
				case 1027: case 1029: case 1031: case 1063: case 1097: case 1213: case 1275:
					if (modx<8) return true;
					break;
		
				case 46: case 103: case 195: case 279: case 315: case 425:
				case 575: case 577: case 583: case 585:
				case 750: /*case 752:*/ case 754: case 756:
				case 858: case 860: case 862: case 864: case 874: case 880: case 914:
				case 1064: case 1098: case 1214: case 1276:
					if (modx>7)	return true;
					break;
		
				// Coins
		
				case 155: case 946: case 954: case 958:
					if (modx>7 && mody<8) return true;
					break;
				case 947:
					if (modx<8 && mody>7) return true;
					break;
				case 948:
					if (modx>7 && mody>7) return true;
					break;
				case 158: case 945: case 953: case 957:
					if (modx<8 && mody<8) return true;
					break;
		
				case 919:
					if (modx<8 && mody<8) return true;
					break;
				case 920:                      
					if (modx>7 && mody<8) return true;
					break;
				case 921: case 1171:                 
					if (modx<8 || mody>7) return true;
					break;
				case 922: case 1172:                 
					if (modx>7 || mody>7) return true;
					break;
		
				default:
				    result=!(isWalkable(on_map));
					if (result)
						return true;
	
			}
	
		};			// for (i)
	
	 
	  // Collision avec les pnj alliés
		if (EngineZildo.persoManagement.collidePerso(tx,ty,quelElement)!=null)
			return true;
	
		if (EngineZildo.spriteManagement.collideSprite(tx,ty,quelElement))
			return true;
			
		
	  /*
	  if (!result)
	  {
	   // Collision avec les sprites fixes impassables (ex:tonneau,rambarde...)
	   i:=0;
	   repeat
	    if tab_elements[i].etat=true then
	     // On teste si le sprite est fixe
	     with tab_elements[i] do begin
	      if quelspr in blockable_sprite+goodies_sprite begin
	       // On teste si Zildo est dedans
	       with banque_spr[BANK_ELEMENTS].tab_sprite[quelspr] do begin
	        sx:=taille_x;
	        sy:=taille_y;
	       end;
	       mx:=round(x)-(sx shr 1);my:=round(y)-(sy shr 1);
	       j:=0;
	       repeat
	        px:=tx+4*tab_add[j];
	        py:=ty+2*tab_add[j+1];
	        if (px>=mx && py>=my && px<=mx+sx && py<=my+sy) return true;
	        j:=j+1;
	       until (bool=true || j=4);
	       // On sauve le sprite si on a Zildo
	       if (bool=true && quelperso=0) begin
	        Sprite_Pousse_par_zildo:=i;
	        sprite_pousse:=true;
	       end;
	       // Peut-être l'obstacle est-il un bonus pour Zildo ?
	       if (bool=true && quelspr in goodies_sprite) begin
	        result=false;
	        if quelperso=0 begin
	         // Il s'agit d'un bonus, et on teste Zildo, donc il le prend
	         beneficie_goodies(quelspr);
	         etat=false;
	        end;
	       end;
	      end;
	     end;
	    inc(i);
	   until (i=MAX_ELEMENTS || bool)
	  end;
	 end;
	}
	*/
	
		// Returns computed result
		return false;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// updateMap
	///////////////////////////////////////////////////////////////////////////////////////
	public void updateMap()
	{
		TileEngine tileEngine=EngineZildo.tileEngine;
	
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// isChangingMap
	///////////////////////////////////////////////////////////////////////////////////////
	public boolean isChangingMap() {
		// Get zildo's position
		Perso zildo=EngineZildo.persoManagement.getZildo();
		float x=zildo.getX();
		float y=zildo.getY();
		// Store the changing point to use it when processing map swap
		changingMapPoint=currentMap.isChangingMap(x,y);
	
		return (changingMapPoint !=null);
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// processChangingMap
	///////////////////////////////////////////////////////////////////////////////////////
	// -load new map
	// -set Zildo's position and angle
	///////////////////////////////////////////////////////////////////////////////////////
	public boolean processChangingMap() {
		if (changingMapPoint != null) {
			// Player is currently on a changing point
			// So we have 3 things to do:
			// 1) turn him on the right angle
			// 2) load new map
			// 3) place zildo at the right location
			Perso zildo=EngineZildo.persoManagement.getZildo();
	
			// 1/3 : angle
			Angle newAngle=changingMapPoint.getAngle((int) zildo.getX(),(int) zildo.getY(),zildo.getAngle());
			zildo.setAngle(newAngle);
			int orderX=changingMapPoint.getOrderX();
			int orderY=changingMapPoint.getOrderY();
	
			// 2/3 : load new map
			String previousMapName=currentMap.getName();
			String newMapName=changingMapPoint.getMapname();
			int previousDimX=currentMap.getDim_x();
			int previousDimY=currentMap.getDim_y();
			boolean isAlongBorder=currentMap.isAlongBorder((int) zildo.getX(), (int) zildo.getY());
			// ATTENTION !
			// Cette ligne ne va pas marcher dans le cas d'un scroll d'une map à l'autre
			// On ne trouvera jamais le point d'enchaînement, et en plus on perdra la map
			// courante.
			// ATTENTION !
			this.charge_map(newMapName);
			
			// 3/3 : location
			// get the target chaining point and place zildo through it
	
			ChainingPoint chPointTarget=currentMap.getTarget(previousMapName, orderX, orderY);
			if (chPointTarget == null || isAlongBorder) {
				// chPointTarget should never be null !
				// But there is a map (polaky, left border) which fails...
				if (zildo.getY() > previousDimY*16-16) {
					zildo.setY(8);
				} else if (zildo.getY() < 4) {
					zildo.setY(currentMap.getDim_y() * 16 - 8);
				} else if (zildo.getX() < 4) {
					zildo.setX(currentMap.getDim_x() * 16 - 16);
				} else if (zildo.getX() > previousDimX*16-16) {
					zildo.setX(8);
				}
				linkTwoMaps(newMapName);
				zildo.finaliseComportement(compteur_animation);
				return true;
			} else {
				zildo.setX((chPointTarget.getPx() & 127) * 16 + 16);
				zildo.setY((chPointTarget.getPy() & 127) * 16 + 8);
			}
			float zx=zildo.getX();
			float zy=zildo.getY();
			if ((chPointTarget.getPx() & 128) != 0) {
				// Vertical chaining point
				zildo.setX(zx-8);
				zildo.setY(zy+8);
			}
			switch (newAngle) {
				case NORD:
					zildo.setY(zy-16);
					break;
				case EST:
					zildo.setX(zx+16);
					break;
				case SUD:
					zildo.setY(zy+16);
					break;
				case OUEST:
					zildo.setX(zx-16);
					break;
			}

			zildo.finaliseComportement(compteur_animation);
			return true;
		}
		return false;
	}

	private int normalizeX(int x) {
		if (x<0) {
			return 0;
		}
		if (x>currentMap.getDim_x() * 16 - 1) {
			return currentMap.getDim_x() * 16 - 1;
		}
		return x;
	}
	
	private int normalizeY(int y) {
		if (y<0) {
			return 0;
		}
		if (y>currentMap.getDim_y() * 16 - 1) {
			return currentMap.getDim_y() * 16 - 1;
		}
		return y;
	}
	
	/**
	 * Create a range inside the map, according to current dimensions.
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return Zone
	 */
	public Zone range(int x1, int y1, int x2, int y2) {
		return new Zone(normalizeX(x1), normalizeY(y1), normalizeX(x2),normalizeY(y2));
	}
	public Zone range(float x1, float y1, float x2, float y2) {
		return new Zone((int) x1, (int) y1, (int) x2, (int) y2);
	}

	public Area getCurrentMap() {
		return currentMap;
	}

	public void setCurrentMap(Area currentMap) {
		this.currentMap = currentMap;
	}
	

    public byte getCompteur_animation() {
		return compteur_animation;
	}

	public void setCompteur_animation(byte compteur_animation) {
		this.compteur_animation = compteur_animation;
	}
}