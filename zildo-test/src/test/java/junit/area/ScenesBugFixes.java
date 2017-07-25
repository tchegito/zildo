package junit.area;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.Assert;
import org.junit.Test;

import tools.EngineUT;
import tools.annotations.SoundEnabled;
import zildo.client.sound.BankSound;
import zildo.monde.sprites.persos.PersoPlayer;
import zildo.server.EngineZildo;

public class ScenesBugFixes extends EngineUT {

	@Test @SoundEnabled
	public void secretSoundInLibrary() {
		EngineZildo.scriptManagement.accomplishQuest("secretDoorRevealed", false);
		mapUtils.loadMap("d4m2");
		waitEndOfScripting();
		// Ruben noticed sound was played each time => that was a bug !
		verify(EngineZildo.soundManagement, never()).playSound(eq(BankSound.ZildoSecret), any(PersoPlayer.class), eq(false));
	}
	
	@Test
	public void nailsInForest() {
		mapUtils.loadMap("sousbois3");
		int nails = 1277;
		// Check that nails are in place
		Assert.assertEquals(nails, mapUtils.area.readmap(11, 28));
		EngineZildo.scriptManagement.accomplishQuest("removeNailForMoonStone", true);
		waitEndOfScripting();
		// After button is presed, check that nails are removed
		Assert.assertEquals(54, mapUtils.area.readmap(11, 28));
		
		// Reload map, then check again
		mapUtils.loadMap("sousbois3");
		waitEndOfScripting();
		Assert.assertEquals("Nails should have been removed !", 54, mapUtils.area.readmap(11, 28));
	}
}
