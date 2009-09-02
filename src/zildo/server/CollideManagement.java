package zildo.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import zildo.monde.Collision;
import zildo.monde.map.Point;
import zildo.monde.map.Rectangle;
import zildo.monde.map.Angle;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.PersoZildo;

public class CollideManagement {

    private List<Collision> tab_colli; // Zones d'aggression des monstres

    // ////////////////////////////////////////////////////////////////////
    // Construction/Destruction
    // ////////////////////////////////////////////////////////////////////

    public CollideManagement() {
        tab_colli = new ArrayList<Collision>();
    }

    // /////////////////////////////////////////////////////////////////////////////////////
    // initFrame
    // /////////////////////////////////////////////////////////////////////////////////////
    // Initializes collision counters.
    // /////////////////////////////////////////////////////////////////////////////////////
    public void initFrame() {
        tab_colli.clear();
    }

    // /////////////////////////////////////////////////////////////////////////////////////
    // addCollision
    // /////////////////////////////////////////////////////////////////////////////////////
    // IN:zildoAttack (TRUE if this collision is FROM Zildo, FALSE otherwise)
    // x,y,rayon,angle : collision parameters
    // perso : perso who create this collision
    // /////////////////////////////////////////////////////////////////////////////////////
    public void addCollision(int x, int y, int rayon, Point size, Angle angle, Perso perso) {
        Collision colli = new Collision(x, y, rayon, size, angle, perso);
        addCollision(colli);
    }
    
    public void addCollision(Collision p_colli) {
    	tab_colli.add(p_colli);
    }

    // /////////////////////////////////////////////////////////////////////////////////////
    // manageCollisions
    // /////////////////////////////////////////////////////////////////////////////////////
    // Here we detect which character is touched and call 'beingWounded' on Perso objects.
    // Zildo or/and PersoNJ can be wounded
    // /////////////////////////////////////////////////////////////////////////////////////
    public void manageCollisions(Collection<ClientState> p_states) {
        for (int i = 0; i < tab_colli.size(); i++) {
            // 1) For each collision, check wether Zildo gets wounded
            Collision collider = tab_colli.get(i);

            Perso damager = collider.getPerso();
            int infoDamager = damager == null ? 2 : damager.getInfo(); // If no one, consider it's from a Zildo

            if (infoDamager == 1) { // PNJ -> they attack zildo
                checkAllZildoWound(p_states, collider);
            } else if (infoDamager == 2) { // ZILDO -> he attacks PNJ or another Zildo
                // 2) For each collision, check wether a monster/zildo gets wounded
                for (int j = 0; j < tab_colli.size(); j++) {
                    Collision collided = tab_colli.get(j);
                    Perso damaged = collided.getPerso();
                    if (damaged != null) { // No one to damage : it's a bushes or rock
                        int infoDamaged = damaged.getInfo();

                        if (j != i && !damaged.equals(damager)) {
                            if (infoDamaged == 1) { // Zildo hit an enemy
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
            PersoZildo zildo = state.zildo;
            Perso damager = p_colli.getPerso();
            if (damager == null || !damager.equals(zildo)) {
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
        float zildoX = p_zildo.getX() - 4;
        float zildoY = p_zildo.getY() - 10;
        // If he's already wounded, don't check
        Collision zildoCollision = new Collision((int) zildoX, (int) zildoY, 8, null, null, p_zildo);

        if (!p_zildo.isWounded() && checkColli(p_colli, zildoCollision)) {
            // Zildo gets wounded
            p_zildo.beingWounded((float) p_colli.getCx(), (float) p_colli.getCy(), p_colli.getPerso());
        }
    }

    /**
     * Check wether the given collision hit the another one. Wound if needed.
     * @param p_collider
     * @param p_collided
     */
    public void checkEnemyWound(Collision p_collider, Collision p_collided) {
        if (checkColli(p_collided, p_collider)) {
            // Monster gets wounded, if it isn't yet
            Perso perso = p_collided.getPerso();

            if (!perso.isWounded()) {
                perso.beingWounded((float) p_collider.getCx(), (float) p_collider.getCy(), p_collider.getPerso());
            }
        }
    }

    // /////////////////////////////////////////////////////////////////////////////////////
    // check_colli
    // /////////////////////////////////////////////////////////////////////////////////////
    // IN:(x,y) coordinates of the first character
    // (a,b) coordinates of the second character
    // r : radius of the first character
    // rayon: radius of the second character
    // /////////////////////////////////////////////////////////////////////////////////////
    // Return true if two characters are colliding.
    // It's called with potential location in a move. Usually, if this method returns true,
    // previous coordinates will be kept.
    // /////////////////////////////////////////////////////////////////////////////////////
    public boolean check_colli(int x, int y, int a, int b, int r, int rayon) {
        // Juste des maths...
        int c = Math.abs(x - a);
        int d = Math.abs(y - b);
        if (c < 50 && d < 50) {
            c = c * c;
            c += d * d;
            c = (int) Math.sqrt((float) c);
            return (c < (r + rayon));
        } else {
            return false;
        }
    }

    public boolean checkColli(Collision p_collider, Collision p_collided) {
        return check_colli(p_collider.cx, p_collider.cy, p_collided.cx, p_collided.cy, p_collider.cr, p_collided.cr, p_collider.size,
                p_collided.size);
    }

    public boolean check_colli(int x1, int y1, int x2, int y2, int radius1, int radius2, Point size1, Point size2) {
        // Check for each
        if (size1 == null && size2 == null) {
            // Collision between 2 circles
            return check_colli(x1, y1, x2, y2, radius1, radius2);
        } else if (size2 == null) {
            // Collision between 1 rectangle and 1 circle
            return new Rectangle(new Point(x1, y1), size1).isCrossingCircle(new Point(x2, y2), radius2);
        } else if (size1 == null && size2 != null) {
            // Idem
            return new Rectangle(new Point(x2, y2), size2).isCrossingCircle(new Point(x1, y1), radius1);
        } else {
            // Collision between 2 rectangles
            return new Rectangle(new Point(x1, y1), size1).isCrossing(new Rectangle(new Point(x2, y2), size2));
        }
    }

    public List<Collision> getTabColli() {
        return tab_colli;
    }
}