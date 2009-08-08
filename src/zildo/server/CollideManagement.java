package zildo.server;

import java.util.ArrayList;
import java.util.List;

import zildo.monde.Collision;
import zildo.monde.map.Angle;
import zildo.monde.persos.Perso;
import zildo.monde.persos.PersoZildo;

public class CollideManagement {
	
    private List<Collision> tab_colli;	// Zones d'aggression des monstres
    private List<Collision> tab_colliz;	// Zones d'aggression de Zildo

    private int nbColli;
	private int nbColliz;
	
//////////////////////////////////////////////////////////////////////
	// Construction/Destruction
	//////////////////////////////////////////////////////////////////////
	
	public CollideManagement()
	{
		nbColli=0;
		nbColliz=0;
		tab_colli=new ArrayList<Collision>();
		tab_colliz=new ArrayList<Collision>();
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// initFrame
	///////////////////////////////////////////////////////////////////////////////////////
	// Initializes collision counters.
	///////////////////////////////////////////////////////////////////////////////////////
	public void initFrame()
	{
		tab_colli.clear();
		tab_colliz.clear();
		nbColli=0;
		nbColliz=0;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// addCollision
	///////////////////////////////////////////////////////////////////////////////////////
	// IN:zildoAttack (TRUE if this collision is FROM Zildo, FALSE otherwise)
	//    x,y,rayon,angle : collision parameters
	///////////////////////////////////////////////////////////////////////////////////////
	public void addCollision(boolean zildoAttack, int x, int y, int rayon, Angle angle, Perso perso)
	{
		Collision colli=new Collision(x,y,rayon,angle,perso);
		if (zildoAttack) {
			tab_colliz.add(colli);
			nbColliz++;
		} else {
			tab_colli.add(colli);
			nbColli++;
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// manageCollisions
	///////////////////////////////////////////////////////////////////////////////////////
	// Here we detect which character is touched and call 'beingWounded' on Perso objects.
	// Zildo or/and PersoNJ can be wounded
	///////////////////////////////////////////////////////////////////////////////////////
	public void manageCollisions()
	{
		Collision enemyCollision;
		Collision zildoCollision;
		PersoZildo zildo= EngineZildo.persoManagement.getZildo();
		float zildoX=zildo.getX();
		float zildoY=zildo.getY();
		boolean died=false;
	
		for (int i=0;i<nbColli;i++) {
			// 1) For each collision, check wether Zildo gets wounded
			enemyCollision=tab_colli.get(i);
			// If he's already wounded, don't check
			if (!zildo.isWounded() && check_colli(enemyCollision.getCx(), enemyCollision.getCy(),
							(int) zildoX, (int) zildoY,
							enemyCollision.getCr(),
							6) ) {
				// Zildo gets wounded
				zildo.beingWounded((float) enemyCollision.getCx(), (float) enemyCollision.getCy());
			}
			// 2) For each collision, check wether a monster gets wounded
			for (int j=0;j<nbColliz;j++) {
				zildoCollision=tab_colliz.get(j);
				if (check_colli(enemyCollision.getCx(), enemyCollision.getCy(),
								zildoCollision.getCx(), zildoCollision.getCy(),
								enemyCollision.getCr(),
								zildoCollision.getCr()) ) {
					// Monster gets wounded, if it isn't yet
					Perso perso=enemyCollision.getPerso();
					if (!perso.isWounded()) {
						died=perso.beingWounded((float) zildoCollision.getCx(), (float) zildoCollision.getCy());
	
						if (died) {
							//EngineZildo.spriteManagement.spawnSpriteGeneric(Element.SPR_MORT,
							//												  (int) perso.getX(),
							//												  (int) perso.getY(),
							//												  0,
							//												  perso);
						}
					}
				}
			}
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// check_colli
	///////////////////////////////////////////////////////////////////////////////////////
	// IN:(x,y) coordinates of the first character
	//	  (a,b) coordinates of the second character
	//	  r : radius of the first character
	//	  rayon: radius of the second character
	///////////////////////////////////////////////////////////////////////////////////////
	// Return true if two characters are colliding.
	// It's called with potential location in a move. Usually, if this method returns true,
	// previous coordinates will be kept.
	///////////////////////////////////////////////////////////////////////////////////////
	public boolean check_colli(int x,int y,int a,int b,int r,int rayon)
	{
		// Juste des maths...
		int c=Math.abs(x-a);
		int d=Math.abs(y-b);
		if (c<50 && d<50) {
			c=c*c;
			c+=d*d;
			c=(int) Math.sqrt((float)c);
			return ( c < (r+rayon) );
		} else {
			return false;
		}
	}
	
	public List<Collision> getTabColliz() {
		return tab_colliz;
	}
	public List<Collision> getTabColli() {
		return tab_colli;
	}
	/*
	  for i:=0 to n_colliseur-1 do begin
	   with tab_colli[i] do begin
	    if (Check_Colli(cx,cy,ax,ay,cr,6)) and (zildo_blesse=False) then begin
	     {Zildo se fait toucher par un ennemi, il est projet‚ dans une direction}
	     with joueur do begin
	      {On saisit la direction de l'ennemi par rapport … Zildo}
	      px:=ax-cx;
	      py:=ay-cy;
	      norme:=round(sqrt(sqr(px)+sqr(py)));
	      if norme=0 then norme:=1;           {Pour ‚viter le 'divide by zero'}
	      {Et on l'envoie !}
	      px:=8*(px/norme);
	      py:=8*(py/norme);
	      mouvement:=MOUVEMENT_TOUCHE;
	      {Il perd un coeur}
	      dec(pv);
	      if pv=1 then begin
	       keys[scEsc]:=True;
	      end;
	     end;
	    end;
	    for j:=0 to n_colliseurz-1 do begin
	     bx:=tab_colliz[j].cx;
	     by:=tab_colliz[j].cy;
	     if Check_Colli(cx,cy,bx,by,cr,tab_colliz[j].cr) then begin
	      {Le monstre se fait toucher}
	      with tab_perso[ctabpnj] do begin
	       if px=0 then begin  {Est-ce qu'il est d‚j… en train de se faire toucher ?}
	        {On extrait la norme du vecteur direction}
	        px:=bx-cx;
	        py:=by-cy;
	        norme:=round(sqrt(sqr(px)+sqr(py)));
	        if norme=0 then norme:=1;
	        {Et on l'envoie}
	        px:=-4*(px/norme);
	        py:=-4*(py/norme);
	        if quel_deplacement<>SCRIPT_VOLESPECTRE then
	         dx:=-1;    {Les volants doivent terminer leur trajectoire}
	        if not (quel_deplacement in [SCRIPT_VOLESPECTRE,SCRIPT_RAT,SCRIPT_ELECTRIQUE,SCRIPT_ABEILLE]) then
	         {Ne se met pas en alerte}
	         alerte:=True;
	       end;
	      end;
	     end;
	    end;
	   end;
	  end;
	
	*/
}