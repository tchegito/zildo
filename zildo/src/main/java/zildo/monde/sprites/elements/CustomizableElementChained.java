package zildo.monde.sprites.elements;

/** Customizable chain of elements.
 * 
 * It's designed to be used in scripts. Technically, it uses abstract class {@link ElementChained}.
 * 
 * To work properly, this element should be moved by two possible means:
 * - by a MoveOrder (if its ElementDescription#isNotFixe is false, like WILL_O_WIST)
 * - by speed (vx/vy/vz) and physicMoveWithCollision() will be called if isNotFixe is true
 * @author Tchegito
 *
 */
public class CustomizableElementChained extends ElementChained {

	Element matrix;
	
	int nbSpawned;
	int nbToSpawn;
	
	public CustomizableElementChained(Element matrix, int chainLong, int delay) {
		super((int) matrix.x, (int) matrix.y);
		this.matrix = matrix;
		this.delay = delay;
		this.nbToSpawn = chainLong;
		follow = true;
		
		// For collision be handled correctly, in case this object should be affected by a mover
		desc = matrix.getDesc();
	}

	protected Element createOne(int p_x, int p_y) {
		Element newOne = new Element(matrix);
		//newOne.setDesc(ElementDescription.fromInt(matrix.getDesc().ordinal()-nbSpawned-1));
		// Report on matrix attributes received by this virtual chain object
		newOne.setSpecialEffect(getSpecialEffect());
		//setSpecialEffect(EngineFX.NO_EFFECT);
		// Customized effect which should be in script (because here we expect behavior to be generic)
		matrix.setAlpha(0.6f * matrix.getAlpha());
		if (nbSpawned == 0) {
			newOne.setForeground(true);
		}
		nbSpawned++;
		if (nbSpawned == nbToSpawn) { // Stop the animation when chain has reach its desired length
			endOfChain = true;
		}
		
		return newOne;
	}

}
