package zildo.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import zildo.monde.Collision;
import zildo.monde.map.Angle;
import zildo.monde.persos.Perso;
import zildo.monde.persos.PersoZildo;

public class CollideManagement {
	
    private List<Collision> tab_colli;	// Zones d'aggression des monstres
	
    //////////////////////////////////////////////////////////////////////
	// Construction/Destruction
	//////////////////////////////////////////////////////////////////////
	
	public CollideManagement()
	{
		tab_colli=new ArrayList<Collision>();
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// initFrame
	///////////////////////////////////////////////////////////////////////////////////////
	// Initializes collision counters.
	///////////////////////////////////////////////////////////////////////////////////////
	public void initFrame()
	{
		tab_colli.clear();
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// addCollision
	///////////////////////////////////////////////////////////////////////////////////////
	// IN:zildoAttack (TRUE if this collision is FROM Zildo, FALSE otherwise)
	//    x,y,rayon,angle : collision parameters
	//    perso : perso who create this collision
	///////////////////////////////////////////////////////////////////////////////////////
	public void addCollision(boolean zildoAttack, int x, int y, int rayon, Angle angle, Perso perso)
	{
		Collision colli=new Collision(x,y,rayon,angle,perso);
		tab_colli.add(colli);
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// manageCollisions
	///////////////////////////////////////////////////////////////////////////////////////
	// Here we detect which character is touched and call 'beingWounded' on Perso objects.
	// Zildo or/and PersoNJ can be wounded
	///////////////////////////////////////////////////////////////////////////////////////
	public void manageCollisions(Collection<ClientState> p_states)
	{
		for (int i=0;i<tab_colli.size();i++) {
			// 1) For each collision, check wether Zildo gets wounded
			Collision collider=tab_colli.get(i);
			
			Perso damager=collider.getPerso();
			int infoDamager=damager == null ? 2 : damager.getInfo();	// If no one, consider it's from a Zildo
			
			if (infoDamager == 1) {	// PNJ -> they attack zildo
				checkAllZildoWound(p_states, collider);
			} else if (infoDamager == 2) {	// ZILDO -> he attacks PNJ or another Zildo
				// 2) For each collision, check wether a monster/zildo gets wounded
				for (int j=0;j<tab_colli.size();j++) {
					Collision collided=tab_colli.get(j);
					Perso damaged=collided.getPerso();
					if (damaged != null) {	// No one to damage : it's a bushes or rock
						int infoDamaged=damaged.getInfo();
						
						if (j != i && !damaged.equals(damager)) {
							if (infoDamaged == 1) {	// Zildo hit an enemy
								checkEnemyWound(collider, collided);
							} else if (infoDamaged == 2) {
								checkZildoWound((PersoZildo) damaged, collider);
							}
						}
					}
				}
				checkAllZildoWound(p_states, collider);
			}
		}
	}
	
	public void checkAllZildoWound(Collection<ClientState> p_states, Collision p_colli) {
		for (ClientState state : p_states) {
			PersoZildo zildo=state.zildo;
			Perso damager=p_colli.getPerso();
			if (damager!=null && !damager.equals(zildo)) {
				checkZildoWound(state.zildo, p_colli);
			}
		}
	}
	
	/**
	 * Check wether the given collision hit the given Zildo. Wound if needed.
	 * @param p_zildo
	 * @param p_colli
	 */
	public void checkZildoWound(PersoZildo p_zildo, Collision p_colli) {
		float zildoX=p_zildo.getX() - 4;
		float zildoY=p_zildo.getY() - 10;
		// If he's already wounded, don't check
		if (!p_zildo.isWounded() && check_colli(p_colli.getCx(), p_colli.getCy(),
						(int) zildoX, (int) zildoY,
						p_colli.getCr(),
						8) ) {
			// Zildo gets wounded
			p_zildo.beingWounded((float) p_colli.getCx(), (float) p_colli.getCy());
		}		
	}
	
	/**
	 * Check wether the given collision hit the another one. Wound if needed.
	 * @param p_collider
	 * @param p_collided
	 */
	public void checkEnemyWound(Collision p_collider, Collision p_collided) {
		if (check_colli(p_collided.getCx(), p_collided.getCy(),
				p_collider.getCx(), p_collider.getCy(),
				p_collided.getCr(),
				p_collider.getCr()) ) {
			// Monster gets wounded, if it isn't yet
			Perso perso=p_collided.getPerso();
		
			if (!perso.isWounded()) {
				perso.beingWounded((float) p_collider.getCx(), (float) p_collider.getCy());
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