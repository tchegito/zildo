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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;

import zildo.Zildo;
import zildo.fwk.opengl.Utils;

/**
 * @author tchegito
 */
public class FBOHardware implements FBO {

    public int create() {
        if (GLContext.getCapabilities().GL_EXT_framebuffer_object) {
            IntBuffer buffer = ByteBuffer.allocateDirect(1 * 4).order(ByteOrder.nativeOrder()).asIntBuffer(); // allocate a 1 int byte
            // buffer
            EXTFramebufferObject.glGenFramebuffersEXT(buffer); // generate
            int fboId=buffer.get();
            
            checkCompleteness(fboId);
            
            return fboId;
        } else {
            throw new RuntimeException("Unable to create FBO");
        }
    }

    public void bindToTextureAndDepth(int myTextureId, int myDepthId, int myFBOId) {
        // On bind le FBO à la texture
        EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, myFBOId);
        if (myDepthId > 0) {
            EXTFramebufferObject.glFramebufferRenderbufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT,
                    EXTFramebufferObject.GL_DEPTH_ATTACHMENT_EXT, EXTFramebufferObject.GL_RENDERBUFFER_EXT, myDepthId);
        }
        EXTFramebufferObject.glFramebufferTexture2DEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT,
                EXTFramebufferObject.GL_COLOR_ATTACHMENT0_EXT, GL11.GL_TEXTURE_2D, myTextureId, 0);

        // Puis on détache la texture de la vue
        EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, 0);
    }

    public int generateDepthBuffer() {
        IntBuffer buf = ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder()).asIntBuffer();
        EXTFramebufferObject.glGenRenderbuffersEXT(buf); // Create Texture In OpenGL
        int depthID = buf.get(0);

        EXTFramebufferObject.glBindRenderbufferEXT(EXTFramebufferObject.GL_RENDERBUFFER_EXT, depthID);
        EXTFramebufferObject.glRenderbufferStorageEXT(EXTFramebufferObject.GL_RENDERBUFFER_EXT, GL11.GL_DEPTH_COMPONENT, Utils
                .adjustTexSize(Zildo.viewPortX), Utils.adjustTexSize(Zildo.viewPortY));

        return depthID;
    }

    public void startRendering(int myFBOId, int sizeX, int sizeY) {
        EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, myFBOId);

        GL11.glPushAttrib(GL11.GL_VIEWPORT_BIT);
        GL11.glViewport(0, 0, Zildo.viewPortX, Zildo.viewPortY);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
    }

    public void endRendering() {
        EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, 0);
        GL11.glPopAttrib();
    }

    public void cleanUp(int id) {
        EXTFramebufferObject.glDeleteFramebuffersEXT(Utils.getBufferWithId(id));
    }

    public void cleanDepthBuffer(int id) {
        EXTFramebufferObject.glDeleteRenderbuffersEXT(Utils.getBufferWithId(id));
    }

    public void checkCompleteness(int myFBOId) {
        int framebuffer = EXTFramebufferObject.glCheckFramebufferStatusEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT);
        switch (framebuffer) {
            case EXTFramebufferObject.GL_FRAMEBUFFER_COMPLETE_EXT:
                break;
            case EXTFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT_EXT:
                throw new RuntimeException("FrameBuffer: " + myFBOId + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT_EXT exception");
            case EXTFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT_EXT:
                throw new RuntimeException("FrameBuffer: " + myFBOId
                        + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT_EXT exception");
            case EXTFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS_EXT:
                throw new RuntimeException("FrameBuffer: " + myFBOId + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS_EXT exception");
            case EXTFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER_EXT:
                throw new RuntimeException("FrameBuffer: " + myFBOId + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER_EXT exception");
            case EXTFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_FORMATS_EXT:
                throw new RuntimeException("FrameBuffer: " + myFBOId + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_FORMATS_EXT exception");
            case EXTFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER_EXT:
                throw new RuntimeException("FrameBuffer: " + myFBOId + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER_EXT exception");
            default:
                throw new RuntimeException("Unexpected reply from glCheckFramebufferStatusEXT: " + framebuffer);
        }
    }
}