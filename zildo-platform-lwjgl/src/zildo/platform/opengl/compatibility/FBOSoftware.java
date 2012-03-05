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

package zildo.platform.opengl.compatibility;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.Util;

import zildo.fwk.opengl.compatibility.FBO;
import zildo.platform.opengl.GLUtils;

/**
 * @author eboussaton
 */
public class FBOSoftware implements FBO {

	private int texRendered;
	private static int cnt = 0;
	private static final Map<Integer, Integer> texFboID = new HashMap<Integer, Integer>();

	@Override
	public int create() {
		return cnt++;

	}

	@Override
	public int generateDepthBuffer() {
		return 0;
	}

	@Override
	public void bindToTextureAndDepth(int myTextureId, int myDepthId,
			int myFBOId) {
		// Keep a link between myTextureId and myFBOId
		texFboID.put(myFBOId, myTextureId);
	}

	@Override
	public void startRendering(int myFBOId, int sizeX, int sizeY) {
		texRendered = texFboID.get(myFBOId);
	}

	@Override
	public void endRendering() {
		GLUtils.copyScreenToTexture(texRendered, 1024, 512);
		Util.checkGLError();
	}

	@Override
	public void cleanUp(int id) {
		GLUtils.cleanTexture(id);
	}

	@Override
	public void cleanDepthBuffer(int id) {

	}
}