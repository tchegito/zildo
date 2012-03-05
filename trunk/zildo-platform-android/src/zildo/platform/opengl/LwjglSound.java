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

package zildo.platform.opengl;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;
import org.lwjgl.util.WaveData;
import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.SoundStore;

import zildo.fwk.file.EasyReadingFile;
import zildo.fwk.opengl.Sound;
import zildo.resource.Constantes;

public class LwjglSound extends Sound {

	/** Buffers hold sound data. */
	IntBuffer buffer = BufferUtils.createIntBuffer(1);

	/** Sources are points emitting sound. */
	IntBuffer source = BufferUtils.createIntBuffer(1);

	/** Position of the source sound. */
	FloatBuffer sourcePos = BufferUtils.createFloatBuffer(3).put(
			new float[] { 0.0f, 0.0f, 0.0f });

	/** Velocity of the source sound. */
	FloatBuffer sourceVel = BufferUtils.createFloatBuffer(3).put(
			new float[] { 0.0f, 0.0f, 0.0f });

	/** Position of the listener. */
	FloatBuffer listenerPos = BufferUtils.createFloatBuffer(3).put(
			new float[] { 0.0f, 0.0f, 0.0f });

	/** Velocity of the listener. */
	FloatBuffer listenerVel = BufferUtils.createFloatBuffer(3).put(
			new float[] { 0.0f, 0.0f, 0.0f });

	/**
	 * Orientation of the listener. (first 3 elements are "at", second 3 are
	 * "up")
	 */
	FloatBuffer listenerOri = BufferUtils.createFloatBuffer(6).put(
			new float[] { 0.0f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f });

	Audio snd = null;

	@Override
	public void finalize() {
		killALData();
	}
	
	public LwjglSound(String p_filename) {
		sourcePos.flip();
		sourceVel.flip();
		listenerPos.flip();
		listenerVel.flip();
		listenerOri.flip();

		loadALData(p_filename);

		setListenerValues();
	}


	private int loadALData(String p_filename) {
		// Load wav data into a buffer.
		AL10.alGenBuffers(buffer);

		if (AL10.alGetError() != AL10.AL_NO_ERROR)
			return AL10.AL_FALSE;

		EasyReadingFile file = new EasyReadingFile(p_filename);

		String lower = p_filename.toLowerCase();
		if (lower.endsWith("wav")) {
			WaveData waveFile = WaveData.create(file.getAll());
			AL10.alBufferData(buffer.get(0), waveFile.format, waveFile.data,
					waveFile.samplerate);
			waveFile.dispose();

			// Bind the buffer with the source.
			AL10.alGenSources(source);

			if (AL10.alGetError() != AL10.AL_NO_ERROR)
				return AL10.AL_FALSE;

			AL10.alSourcei(source.get(0), AL10.AL_BUFFER, buffer.get(0));
			AL10.alSourcef(source.get(0), AL10.AL_PITCH, 1.0f);
			AL10.alSourcef(source.get(0), AL10.AL_GAIN, 1.0f);
			AL10.alSource(source.get(0), AL10.AL_POSITION, sourcePos);
			AL10.alSource(source.get(0), AL10.AL_VELOCITY, sourceVel);

		} else if (lower.endsWith("ogg")) {
			try {
				snd = SoundStore.get().getOggStream(Constantes.DATA_PATH+p_filename);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		// Do another error check and return.
		if (AL10.alGetError() == AL10.AL_NO_ERROR)
			return AL10.AL_TRUE;

		return AL10.AL_FALSE;

	}

	/**
	 * void setListenerValues()
	 * 
	 * We already defined certain values for the Listener, but we need to tell
	 * OpenAL to use that data. This function does just that.
	 */
	void setListenerValues() {
		AL10.alListener(AL10.AL_POSITION, listenerPos);
		AL10.alListener(AL10.AL_VELOCITY, listenerVel);
		AL10.alListener(AL10.AL_ORIENTATION, listenerOri);
	}

	/**
	 * void killALData()
	 * 
	 * We have allocated memory for our buffers and sources which needs to be
	 * returned to the system. This function frees that memory.
	 */
	void killALData() {
		if (snd == null) {
			AL10.alDeleteSources(source);
			AL10.alDeleteBuffers(buffer);
		}
	}

	@Override
	public void play() {
		if (snd != null) {
			snd.playAsMusic(1.0f, 1.0f, true);
		} else {
			AL10.alSourcePlay(source.get(0));
		}
	}

	@Override
	public void stop() {
		if (snd != null) {
			snd.stop();
		} else {
			AL10.alSourceStop(source.get(0));
		}
	}

	public void pause() {
		if (snd != null) {
			snd.stop();
		} else {
			AL10.alSourcePause(source.get(0));
		}
	}
}