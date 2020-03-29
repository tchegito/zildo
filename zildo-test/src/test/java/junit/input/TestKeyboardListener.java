package junit.input;

import org.junit.Assert;
import org.junit.Test;

import tools.EngineUT;
import tools.annotations.ClientMainLoop;
import zildo.fwk.input.KeyboardHandler.Keys;

public class TestKeyboardListener extends EngineUT {

	@Test @ClientMainLoop
	public void consistentKeyDown() {
		Assert.assertFalse(fakedKbHandler.isKeyDown(Keys.RETURN));
		Assert.assertFalse(fakedKbHandler.isKeyDown(Keys.RETURN));
		
		simulateKeyPressed(Keys.RETURN);
		Assert.assertTrue(fakedKbHandler.isKeyDown(Keys.RETURN));
		// Calling isKeyPressed shouldn't alter the keydown states
		Assert.assertTrue(fakedKbHandler.isKeyPressed(Keys.RETURN));
		Assert.assertTrue(fakedKbHandler.isKeyPressed(Keys.RETURN));
		renderFrames(1);
		Assert.assertTrue(fakedKbHandler.isKeyDown(Keys.RETURN));
		Assert.assertFalse(fakedKbHandler.isKeyPressed(Keys.RETURN));
		simulateKeyPressed();
		renderFrames(1);
		Assert.assertFalse(fakedKbHandler.isKeyPressed(Keys.RETURN));
		Assert.assertFalse(fakedKbHandler.isKeyPressed(Keys.RETURN));
		
	}
	
	@Test 
	public void rightCodes() {
		simulateKeyPressed(Keys.DOWN);
		Assert.assertEquals(208, fakedKbHandler.getCode(Keys.DOWN));
		
		simulateKeyPressed(Keys.RETURN);
		Assert.assertEquals(28, fakedKbHandler.getCode(Keys.RETURN));
	}
}
