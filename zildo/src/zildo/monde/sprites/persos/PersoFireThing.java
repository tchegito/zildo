package zildo.monde.sprites.persos;

import java.util.LinkedList;
import java.util.List;

import zildo.client.sound.BankSound;
import zildo.monde.sprites.desc.PersoDescription;
import zildo.monde.sprites.elements.Element;
import zildo.monde.util.Pointf;
import zildo.server.EngineZildo;

public class PersoFireThing extends PersoNJ {

	final static double eyesDistance = Math.PI / 6;
	
	List<Pointf> positions;
	double direction;	// Angle of direction
	
	final float speed = 1.5f;
	
	Element tail;
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
	}
	
	float keepy = -999;
	@Override
	public void animate(int compteur_animation) {
		//super.animate(compteur_animation);
		
		if (keepy == -999) {
			keepy = y;
		}
		// Move
		float xx=x + (float) (speed * Math.cos(direction));
		float yy=keepy + (float) (speed * Math.sin(direction));
		Pointf pos = new Pointf(xx, yy);
		if (EngineZildo.mapManagement.collide((int) xx, (int) yy, this)) {
			EngineZildo.soundManagement.broadcastSound(BankSound.BoomerangTape, this);
			direction+=Math.PI/2;
			pos.x = x;
			pos.y = keepy;
			y = keepy;
		} else {
			x = xx;
			y = yy;
		}
		positions.add(0, pos);
		
		if (positions.size() > 4) {
			pos = positions.get(4);
		}
		middle.x = pos.x;
		middle.y = pos.y;
		
		if (positions.size() > 8) {
			pos = positions.get(8);
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
		
		// Max 10 elements
		if (positions.size() > 10) {
			positions.remove(10);
		}
		direction+=0.01f;
	}
	
	private void orderElements(Element... elements) {
		int startZ = 0;
		for (Element e : elements) {
			e.z = startZ;
			e.y = e.y + startZ;
			startZ+=10;
		}
	}
}
