package junit.sprites;

import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import tools.EngineWithMenuUT;
import tools.annotations.ClientMainLoop;
import zildo.client.ClientEngineZildo;
import zildo.client.stage.SinglePlayer;
import zildo.fwk.FilterCommand;
import zildo.fwk.gfx.filter.FitToScreenFilter;
import zildo.fwk.input.KeyboardHandler.Keys;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.desc.FontDescription;
import zildo.server.EngineZildo;

public class CheckGUI extends EngineWithMenuUT {

	class FilterMonitoring {
		final boolean enableFitScreen;
		final int fadeLevel;
		public FilterMonitoring(boolean enableFitScreen, int fadeLevel) {
			this.enableFitScreen = enableFitScreen;
			this.fadeLevel = fadeLevel;
		}
	}
	
	// We observed a blink when leaving ingame menu. It was because of an error in MenuTransitionProgress
	@Test
	@ClientMainLoop
	public void FilterMonitoring() {
		SinglePlayer singlePlayerStage = mock(SinglePlayer.class);
		when(singlePlayerStage.isDone()).thenReturn(false);
		ClientEngineZildo.getClientForGame().getMenuTransition().askForStage(singlePlayerStage);

		List<FilterMonitoring> values = new ArrayList<>();
		waitEndOfScripting();
		ClientEngineZildo.filterCommand = mock(FilterCommand.class);
		doAnswer(i -> {
			boolean enable = i.getArgument(1);
			int fadeLevel = ClientEngineZildo.getClientForGame().getMenuTransition().getFadeLevel();
			values.add(new FilterMonitoring(enable, fadeLevel));
			return null;
		}).when(ClientEngineZildo.filterCommand)
				.active(eq(FitToScreenFilter.class), any(Boolean.class),  eq(null));
		simulatePressButton(Keys.ESCAPE, 2);
		renderFrames(5);
		Assert.assertTrue(ClientEngineZildo.getClientForMenu().isIngameMenu());


		System.out.println("On sort");
		pickItem("m7.continue");
		Assert.assertFalse(ClientEngineZildo.getClientForMenu().isIngameMenu());

		// Check last entry
		FilterMonitoring fm = values.get(values.size() - 1);
		Assert.assertTrue("A blink occured with FitScreenFilter !", fm.enableFitScreen && fm.fadeLevel == 0);
	}
	
	@Test 	@ClientMainLoop
	public void displayDialogCorner() {
		// We trigger a dialog, then press button to make frame disappear
		// After one frame, we should notice that the bars are gone, but corners still there.
		spawnZildo(160, 100);
		waitEndOfScripting();
		EngineZildo.scriptManagement.execute("preintro", true);
		while (!clientState.dialogState.isDialoguing()) {
			renderFrames(1);
		}
		renderFrames(50);
		simulatePressButton(Keys.Q, 1);
		simulatePressButton(Keys.Q, 0);
		SpriteDisplayMocked sd = (SpriteDisplayMocked) ClientEngineZildo.spriteDisplay;
		SpriteEntity[][] entities = sd.getTabTri();
		FontDescription expected = FontDescription.FRAME_CORNER_LEFT;
		for (int i=0;i<entities.length;i++) {
			for (int j=0;j<entities[i].length;j++) {
				SpriteEntity entity = entities[i][j];
				if (entity != null && entity.nSpr == expected.getNSpr() && entity.nBank == expected.getBank()) {
					// && entities[i][j].getDesc() == FontDescription.FRAME_CORNER_LEFT) {
					fail("Corner sprite shouldn't have been there !"); //.out.println(entities[i][j].getDesc());
				}
			}
		}
	}
}
