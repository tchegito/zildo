package zildo.monde.sprites.persos;

import java.util.LinkedList;
import java.util.List;

import zildo.monde.sprites.desc.PersoDescription;
import zildo.monde.sprites.elements.Element;
import zildo.monde.util.Point;
import zildo.monde.util.Pointf;
import zildo.server.EngineZildo;

public class PersoFireThing extends PersoNJ {

	final static double eyesDistance = Math.PI / 6;
	
	List<Pointf> positions;
	double direction;	// Angle of direction
	
	final float speed = 1.5f;
	
	Element tail;	// Monster's components will be Element with a PersoDescription (that's weird !)
	Element middle;
	Element eye1;
	Element eye2;
	
	public PersoFireThing() {
		super();
		
		info =PersoInfo.ENEMY;
		
		setDesc(PersoDescription.FIRETHING);
		addSpr = 3;
		
		positions = new LinkedList<Pointf>();
		
		middle = new Element();
		middle.setDesc(PersoDescription.FIRETHING);
		middle.setAddSpr(2);
		addPersoSprites(middle);
		
		tail = new Element();
		tail.setDesc(PersoDescription.FIRETHING);
		addPersoSprites(tail);
		
		eye1 = new Element();
		eye1.setDesc(PersoDescription.FIRETHING);
		eye1.setAddSpr(1);
		addPersoSprites(eye1);
		
		eye2 = new Element();
		eye2.setDesc(PersoDescription.FIRETHING);
		eye2.setAddSpr(1);
		addPersoSprites(eye2);
		
		pv = 2;
	}
	
	float keepy = -999;
	@Override
	public void move() {
		if (keepy == -999) {
			keepy = y;
		}
		// Move
		Pointf pos = new Pointf(x, keepy);
		if (px == 0f && py == 0f) {
			float xx=x + (float) (speed * Math.cos(direction));
			float yy=keepy + (float) (speed * Math.sin(direction));
			pos = new Pointf(xx, yy);
			if (EngineZildo.mapManagement.collide((int) xx, (int) yy, this)) {
				direction+=Math.PI/2 + EngineZildo.hasard.rand()*Math.PI/16;
				pos.x = x;
				pos.y = keepy;
				y = keepy;
			} else {
				x = xx;
				y = yy;
			}
			positions.add(0, pos);
		} else {
			positions.add(0, pos);
			y = keepy;
		}
		if (positions.size() > 6) {
			pos = positions.get(6);
		}
		middle.x = pos.x;
		middle.y = pos.y;
		
		if (positions.size() > 10) {
			pos = positions.get(10);
		}
		tail.x = pos.x;
		tail.y = pos.y;
		
		// Place eyes around the head
		eye1.x = x + (float) (8*Math.cos(direction+eyesDistance));
		eye1.y = y + (float) (8*Math.sin(direction+eyesDistance));
		eye2.x = x + (float) (8*Math.cos(direction-eyesDistance));
		eye2.y = y + (float) (8*Math.sin(direction-eyesDistance));
		
		keepy = y;
		orderElements(tail, middle, this, eye1, eye2);
		
		// Max 12 elements
		if (positions.size() > 12) {
			positions.remove(12);
		}
		direction+=0.01f;
		
		addSpr = 3;
	}
	
	@Override
	public void manageCollision() {
		// Collision with head, middle and tail (not the eyes)
		super.manageCollision();
		tail.manageCollision();
		middle.manageCollision();
	}
	
	/** Order elements so that main one is at z=0 **/
	private void orderElements(Element... elements) {
		int startZ = 10 * - 2;
		for (Element e : elements) {
			e.z = startZ;
			e.y = e.y + startZ;
			startZ+=10;
		}
	}
	
	public void shift(Point p_offset) {
		super.shift(p_offset);
		keepy += p_offset.y;
	}
}
