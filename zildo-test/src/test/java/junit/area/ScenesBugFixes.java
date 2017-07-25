package junit.area;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
}
