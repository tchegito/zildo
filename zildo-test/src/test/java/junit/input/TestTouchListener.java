package junit.input;

import org.junit.Assert;
import org.junit.Test;

import tools.EngineUT;
import zildo.Zildo;
import zildo.client.ClientEngineZildo;
import zildo.client.gui.GUIDisplay;
import zildo.client.gui.menu.SaveGameMenu;
import zildo.fwk.ZUtils;
import zildo.fwk.input.KeyboardHandler.Keys;
import zildo.monde.util.Angle;
import zildo.monde.util.Point;
import zildo.monde.util.Vector2f;
import zildo.monde.util.Zone;
import zildo.platform.input.AndroidKeyboardHandler.KeyLocation;
import zildo.platform.input.TouchPoints;

/** Check several aspects of touch listening:<ul>
 * <li>a crash about concurrent modification</li>
 * <li>mobile cross behavior</li>
 * <li>fixed cross behavior</li>
 * </ul>
 * @author Tchegito
 *
 */
public class TestTouchListener extends EngineUT {

	
	@Test
	public void concurrentModification() {
		GUIDisplay gui = ClientEngineZildo.guiDisplay;
		new Thread() {
			@Override
			public void run() {
				for (int i=0;i<100;i++) {
					gui.getItemOnLocation(160, 100);
					ZUtils.sleep(1);
				}
			}
		}.start();
		gui.displayMenu(new SaveGameMenu(false, null), 255);
		
	}
	
	Zone xButtonLoc = KeyLocation.VP_BUTTON_X.z;
	Zone yButtonLoc = KeyLocation.VP_BUTTON_Y.z;
	Zone padDownLoc = KeyLocation.VP_DOWN.z;
	
	@Test
	public void buttons() {
		TouchPoints tp = enableAndroidTouch();
		
		// 1) check button Q (called 'X' on android screen)
		tp.set(0, new Point(xButtonLoc.x1+8, xButtonLoc.y1+8));
		fakedKbHandler.poll();
		Assert.assertTrue(fakedKbHandler.isKeyDown(Keys.Q));
		
		// 2) check button W (called 'Y' on android screen) in the same time
		tp.set(1, new Point(yButtonLoc.x1+8, yButtonLoc.y1+8));
		fakedKbHandler.poll();
		Assert.assertTrue(fakedKbHandler.isKeyDown(Keys.W));
	}
	
	/** Detected in Issue 82: button press in 'moving cross' **/
	@Test
	public void buttonsDuringMovement() {
		ClientEngineZildo.client.setMovingCross(true);

		// Prepare android touch
		TouchPoints tp = enableAndroidTouch();
		
		// Send basic movements
		// 1) put finger to determine cross center
		tp.set(0, new Point(50, 220));
		fakedKbHandler.poll();
		Assert.assertNull(fakedKbHandler.getDirection());
		// 2) move same finger on the right
		tp.set(0, new Point(50, 200));
		fakedKbHandler.poll();
		Vector2f dir = fakedKbHandler.getDirection();
		Assert.assertEquals(Angle.NORD, Angle.fromDelta(dir.x, dir.y));
		
		// 3) now put another finger on action buttons
		tp.set(1,  new Point(xButtonLoc.x1+8, xButtonLoc.y1+8));
		fakedKbHandler.poll();
		dir = fakedKbHandler.getDirection();
		Assert.assertEquals("Direction shouldn't have been changed !", Angle.NORD, Angle.fromDelta(dir.x, dir.y));
		Assert.assertTrue(fakedKbHandler.isKeyDown(Keys.Q));
	}
	
	@Test
	public void releaseDirectionWhenHoldingButtons() {
		ClientEngineZildo.client.setMovingCross(true);
		
		TouchPoints tp = enableAndroidTouch();
		
		// Move to the south
		tp.set(0, new Point(50, 220));
		fakedKbHandler.poll();
		tp.set(0, new Point(50, 260));
		fakedKbHandler.poll();
		Vector2f dir = fakedKbHandler.getDirection();
		Assert.assertEquals(Angle.SUD, Angle.fromDelta(dir.x, dir.y));
		
		// Press X button
		tp.set(1, new Point(xButtonLoc.x1+8, xButtonLoc.y1+8));
		fakedKbHandler.poll();
		Assert.assertTrue(fakedKbHandler.isKeyDown(Keys.Q));
		
		// Release direction on the cross
		tp.set(0, null);
		fakedKbHandler.poll();
		Assert.assertNull(fakedKbHandler.getDirection());
		
		tp.set(1, null);
		fakedKbHandler.poll();
		Assert.assertFalse(fakedKbHandler.isKeyDown(Keys.Q));
		
		// Press button
		tp.set(2, new Point(xButtonLoc.x1+8, xButtonLoc.y1+8));
		fakedKbHandler.poll();
		Assert.assertTrue(fakedKbHandler.isKeyDown(Keys.Q));
	}
	
	@Test
	public void lockedCross() {
		ClientEngineZildo.client.setMovingCross(false);
		
		TouchPoints tp = enableAndroidTouch();

		// Press DOWN button
		tp.set(0, new Point(padDownLoc.x1+4, padDownLoc.y1+4));
		fakedKbHandler.poll();
		Vector2f dir = fakedKbHandler.getDirection();
		// Now check that direction is accordingly set
		Assert.assertNotNull(dir);
		Assert.assertEquals(Angle.SUD, Angle.fromDelta(dir.x, dir.y));
	}
	
	@Test
	public void holdGearButton() {
		TouchPoints tp = enableAndroidTouch();
		tp.set(0,  new Point(Zildo.viewPortX - 10, 10));
		fakedKbHandler.poll();
		Assert.assertTrue(fakedKbHandler.isKeyPressed(Keys.GEAR));
		fakedKbHandler.poll();
		Assert.assertFalse(fakedKbHandler.isKeyPressed(Keys.GEAR));
	}
}
