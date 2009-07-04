package zildo.monde;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import zildo.fwk.engine.EngineZildo;
import zildo.monde.decors.Element;


public class Area {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	static public int M_MOTIF_MASQUE=128;
	static private int M_MOTIF_ANIME=64;

	static private int SCROLL_LEFT=0;           // Pour les changements de map
	static private int SCROLL_RIGHT=1;
	static private int SCROLL_UP=2;
	static private int SCROLL_DOWN=3;

	static private int MOTIFS_EXTERIEUR=1;
	static private int MOTIFS_INTERIEUR=0;	

	// For roundAndRange
	static public int ROUND_X=0;
	static public int ROUND_Y=0;
	
	private int dim_x,dim_y;
	private String name;
	private Map<Integer,Case> mapdata;
	private int n_persos,n_sprites,n_pe;
	private List<ChainingPoint> listPointsEnchainement;

	public Area()
	{
		mapdata=new HashMap<Integer, Case>();
		listPointsEnchainement=new ArrayList<ChainingPoint>();
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// get_Areacase
	///////////////////////////////////////////////////////////////////////////////////////
	// IN : coordinates
	// OUT: Case object at the given coordinates
	///////////////////////////////////////////////////////////////////////////////////////
	public Case get_mapcase(int x, int y)
	{
		return mapdata.get(new Integer(y*this.dim_x + x));
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// set_Areacase
	///////////////////////////////////////////////////////////////////////////////////////
	// IN:coordinates, Case object
	///////////////////////////////////////////////////////////////////////////////////////
	public void set_mapcase(int x,int y,Case c)
	{
		mapdata.put(new Integer(y*this.dim_x + x),c);
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// get_animatedAreacase
	///////////////////////////////////////////////////////////////////////////////////////
	// IN:coordinates, frame index
	///////////////////////////////////////////////////////////////////////////////////////
	Case get_animatedAreacase(int x, int y,int compteur_animation)
	{
		Case temp=this.get_mapcase(x,y);
		temp.setN_motif(temp.getAnimatedMotif(compteur_animation));
		return temp;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// readArea
	///////////////////////////////////////////////////////////////////////////////////////
	// IN : coordinates on Area
	// OUT: return motif + bank*256
	///////////////////////////////////////////////////////////////////////////////////////
	// Return n_motif + n_banque*256 from a given position on the Area
	public int readmap(int x,int y)
	{
		Case temp=this.get_mapcase(x,(int) (y+4));
		int a=temp.getN_banque() & 31;
		int b=temp.getN_motif();
		/*
		if (a==2 && b==0)
		{
			a=temp.n_banque_masque & 31;
			b=temp.n_motif_masque;
		}
		*/
		a=a << 8;
		return a + b;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// writeArea
	///////////////////////////////////////////////////////////////////////////////////////
	// IN:x,y (coordinates on Area), quoi =motif + bank*256
	///////////////////////////////////////////////////////////////////////////////////////
	public void writemap(int x,int y,int quoi)
	{
	 Case temp=this.get_mapcase(x,y+4);
	 temp.setN_motif(quoi & 255);
	 temp.setN_banque(quoi >> 8);
	 this.set_mapcase(x,y+4,temp);
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// roundAndRange
	///////////////////////////////////////////////////////////////////////////////////////
	// IN:float to round and range, indicator on which coordinate to compute
	//    ROUND_X(default)  -. x , ROUND_Y -. y
	///////////////////////////////////////////////////////////////////////////////////////
	// Trunc a float, and get it into the Area, with limits considerations.
	///////////////////////////////////////////////////////////////////////////////////////
	public int roundAndRange(float x, int whatToRound)
	{
		int result=(int)x;
		if (x<0)
			x=0;
		int max=dim_x;
		if (whatToRound==ROUND_Y)
			max=dim_y;
		if (x > (max*16 - 16) )
			x=max*16 - 16;
	
		return result;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// isAlongBorder
	///////////////////////////////////////////////////////////////////////////////////////
	public boolean isAlongBorder(int x, int y)
	{
		return (x<4 || x>dim_x*16-8 ||
				y<4 || y>dim_y*16-4);
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	// isChangingArea
	///////////////////////////////////////////////////////////////////////////////////////
	// IN : x,y (pixel coordinates for perso location)
	///////////////////////////////////////////////////////////////////////////////////////
	// Return ChainingPoint if Zildo's crossing one (door, or Area's border)
	///////////////////////////////////////////////////////////////////////////////////////
	public ChainingPoint isChangingMap(float x, float y) {
		// On parcourt les points d'enchainements
		int ax=(int) (x / 16);
		int ay=(int) (y / 16);
		boolean border;
		if (this.n_pe!=0) {
			for (ChainingPoint chPoint : listPointsEnchainement) {
				// Area's borders
				border=isAlongBorder((int) x,(int) y);
				if (chPoint.isCollide(ax,ay,border)) {
					addChainingContextInfos(chPoint);
					return chPoint;
				}
			}
		}
		return null;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// addContextInfos
	///////////////////////////////////////////////////////////////////////////////////////
	// Fill the given ChainingPoint with two extra infos: 'orderX' and 'orderY'
	///////////////////////////////////////////////////////////////////////////////////////
	void addChainingContextInfos(ChainingPoint chPoint) {
		int orderX=0;
		int orderY=0;
		// We're gonna get a sort number in each coordinate for all chaining point
		// referring to the same Area.
		for (ChainingPoint chP : listPointsEnchainement) {
			if (chP.getMapname().equals(chPoint.getMapname())) {
				if (chP.getPx() <= chPoint.getPx()) {
					orderX++;
				}
				if (chP.getPy() <= chPoint.getPy()) {
					orderY++;
				}
			}
		}
		chPoint.setOrderX(orderX);
		chPoint.setOrderY(orderY);
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// getTarget
	///////////////////////////////////////////////////////////////////////////////////////
	// IN : comingArea -. Area's name
	///////////////////////////////////////////////////////////////////////////////////////
	public ChainingPoint getTarget(String comingArea, int orderX, int orderY) {
		if (this.n_pe!=0) {
			for (ChainingPoint chPoint : listPointsEnchainement) {
				if (chPoint.getMapname().equals(comingArea)) {
					if (orderX == 0 && orderY == 0) {
						return chPoint;
					} else {
						// Get the right one, because there is several connections between
						// the two Areas.
						addChainingContextInfos(chPoint);
						if (chPoint.getOrderX() == orderX &&
							chPoint.getOrderY() == orderY) {
							return chPoint;
						}
					}
				}
			}
		}
		return null;
	}
	
		/*
	
				// On place Zildo sur son bon angle si c'est pas le cas}
				int angle
	
	       {On change de Area}
	       temp:=name;              {On sauve l'ancien nom}
	       fade(FALSE);
	       charger_aventure_Area(Area1,tab_pe[i].Areaname);
	       {On cherche le point de r‚apparition de Zildo}
	       if n_pe<>0 then                   {Ce nombre ne PEUT pas ˆtre nul}
	        for j:=0 to n_pe-1 do
	         if tab_pe[j].Areaname=temp then begin
	          x:=(tab_pe[j].px and 127)*16+16;
	          y:=(tab_pe[j].py and 127)*16+8;
	          if (tab_pe[j].px and 128) <> 0 then begin
	           x:=x-8;y:=y+8;
	          end;
	          coming_Area:=1;
	          {On met Zildo un peu en avant}
	          case angle of
	           0:y:=y-16;
	           1:x:=x+16;
	           2:y:=y+16;
	           3:x:=x-16;
	          end;
	          camerax:=round(x)-16*10;
	          cameray:=round(y)-16*6;
	          if camerax>(16*dim_x-16*20) then camerax:=16*dim_x-16*20;
	          if cameray>(16*dim_y-16*13+8)  then cameray:=16*dim_y-16*13+8;
	          if camerax<0 then camerax:=0;
	          if cameray<0 then cameray:=0;
	          exit;
	         end;
	}
	*/
	
	///////////////////////////////////////////////////////////////////////////////////////
	// attackTile
	///////////////////////////////////////////////////////////////////////////////////////
	public void attackTile(Point tileLocation) {
		// On teste si Zildo détruit un buisson
		int on_Area=this.readmap(tileLocation.getX(),tileLocation.getY());
		if (on_Area==165) {
			EngineZildo.spriteManagement.spawnSpriteGeneric(Element.SPR_BUISSON,
															  tileLocation.getX()*16+8,
															  tileLocation.getY()*16+8,0, null);
			EngineZildo.soundManagement.playSoundFX("CasseBuisson");
	
			this.writemap(tileLocation.getX(),tileLocation.getY(),166);
		}
	
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// translatePoints
	///////////////////////////////////////////////////////////////////////////////////////
	// Shift every Area's point by this vector (shiftX, shiftY) to another Area
	///////////////////////////////////////////////////////////////////////////////////////
	public void translatePoints(int shiftX, int shiftY, Area targetArea) {
		Case tempCase;
		for (int i=0;i<dim_y;i++) {
			for (int j=0;j<dim_x;j++) {
				tempCase=get_mapcase(j,i);
				targetArea.set_mapcase(j+shiftX,i+shiftY,tempCase);
			}
		}
	}

	public void addPointEnchainement(ChainingPoint ch) {
		listPointsEnchainement.add(ch);
	}
	
	public int getDim_x() {
		return dim_x;
	}

	public void setDim_x(int dim_x) {
		this.dim_x = dim_x;
	}

	public int getDim_y() {
		return dim_y;
	}

	public void setDim_y(int dim_y) {
		this.dim_y = dim_y;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getN_persos() {
		return n_persos;
	}

	public void setN_persos(int n_persos) {
		this.n_persos = n_persos;
	}

	public int getN_sprites() {
		return n_sprites;
	}

	public void setN_sprites(int n_sprites) {
		this.n_sprites = n_sprites;
	}

	public int getN_pe() {
		return n_pe;
	}

	public void setN_pe(int n_pe) {
		this.n_pe = n_pe;
	}

	public List<ChainingPoint> getListPointsEnchainement() {
		return listPointsEnchainement;
	}

	public void setListPointsEnchainement(List<ChainingPoint> listPointsEnchainement) {
		this.listPointsEnchainement = listPointsEnchainement;
	}
}