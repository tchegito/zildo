/**
 * Legend of Zildo
 * Copyright (C) 2006-2011 Evariste Boussaton
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

package zildo.fwk.opengl.compatibility;

/**
 * Allows to draw on a texture.
 * 
 * Supports two modes:
 * -software
 * -hardware
 * 
 * @author tchegito
 */
public interface FBO {

    public int create();

    public int generateDepthBuffer();

    public void bindToTextureAndDepth(int myTextureId, int myDepthId, int myFBOId);

    public void startRendering(int myFBOId, int sizeX, int sizeY);

    public void endRendering();

    public void cleanUp(int id);

    public void cleanDepthBuffer(int id);
}
