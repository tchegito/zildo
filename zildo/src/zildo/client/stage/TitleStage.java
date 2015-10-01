/**
 * The Land of Alembrum
 * Copyright (C) 2006-2013 Evariste Boussaton
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package zildo.client.stage;

import zildo.Zildo;
import zildo.client.ClientEngineZildo;
import zildo.client.gui.EntityTransformer;
import zildo.client.gui.GUISequence;
import zildo.fwk.gfx.EngineFX;
import zildo.fwk.ui.UIText;
import zildo.monde.sprites.SpriteEntity;

/**
 * Simple stage making a title, on two lines appearing slowly on the middle of the screen.
 * 
 * @author Tchegito
 *
 */
public class TitleStage implements GameStage {

	boolean done;
	int counter;
	int currentLine;
	int centerY;
	int lineWaiting;
	String[] infosText;
	EntityTransformer action;
	int currentZoom;
	EngineFX currentEffect;
	String wholeInfos;
	
	public TitleStage(String p_text) {
		counter = 0;
		done = false;
		wholeInfos = UIText.getGameText(p_text);
		launchGame();
	}
	
	private class AppearTitle implements EntityTransformer {
		public void transform(SpriteEntity ent) {
			if (!ent.visible) {
				ent.setAlpha(0);
				ent.setVisible(true);
				ent.setSpecialEffect(currentEffect);
				ent.zoom = currentZoom;
			} else if (ent.getAlpha() < 255) {
				ent.setAlpha(ent.getAlpha() + 1);
			}
		}
	}
	
	private static class DisappearTitle implements EntityTransformer {
		public void transform(SpriteEntity ent) {
			if (ent.getAlpha() > 0) {
				ent.setAlpha(Math.max(0, ent.getAlpha() - 2));
			}
		}
	}
	
	@Override
	public void updateGame() {
		lineWaiting--;
		if (lineWaiting == 0) {
			lineWaiting = 255;
			currentLine++;
			if (currentLine == 1) {
				currentZoom = 230;
				currentEffect = EngineFX.FOCUSED;
			}
		}
	}

	@Override
	public void renderGame() {
		String sentence = null;
		if (lineWaiting == 255) {
			if (currentLine < infosText.length) {
				sentence = infosText[currentLine];
			} else if (currentLine == infosText.length) {
				// Nothing
			} else if (currentLine == infosText.length + 1) {
				action = new DisappearTitle();
			} else {
				endGame();
			}
				
		}
		ClientEngineZildo.guiDisplay.displayInfo(currentLine * 16 + centerY, sentence, action);
	}

	@Override
	public void launchGame() {
		infosText = wholeInfos.split("\n");
		
		centerY = (Zildo.viewPortY - (16 * infosText.length) ) / 2;
		lineWaiting = 1;	// Number of frame before first line
		currentLine = -1;
		action = new AppearTitle();
		currentZoom = 255;	// Max for first line
		currentEffect = EngineFX.INFO;
	}

	@Override
	public void endGame() {
		ClientEngineZildo.guiDisplay.clearSequences(GUISequence.INFO);
		done = true;
	}

	@Override
	public boolean isDone() {
		return done;
	}

}
