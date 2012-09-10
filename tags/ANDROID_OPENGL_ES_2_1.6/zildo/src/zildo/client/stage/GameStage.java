/**
 * Legend of Zildo
 * Copyright (C) 2006-2012 Evariste Boussaton
 * Based on original Zelda : link to the past (C) Nintendo 1992
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

/**
 * @author Tchegito
 *
 */
public interface GameStage {

	/**
	 * Called each frame, for updating state.
	 */
	public void updateGame();
	
	/**
	 * Called each frame, to render.
	 */
	public void renderGame();
	
	/**
	 * Called once for starting the stage.
	 */
	public void launchGame();

	public void endGame();
	
	public boolean isDone();
}
