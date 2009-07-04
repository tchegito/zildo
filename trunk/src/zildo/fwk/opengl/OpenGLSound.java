package zildo.fwk.opengl;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.util.WaveData;

import zildo.fwk.file.EasyReadingFile;

public class OpenGLSound {

	  /** Buffers hold sound data. */
	  IntBuffer buffer = BufferUtils.createIntBuffer(1);
	
	  /** Sources are points emitting sound. */
	  IntBuffer source = BufferUtils.createIntBuffer(1);
	
	  /** Position of the source sound. */
	  FloatBuffer sourcePos = BufferUtils.createFloatBuffer(3).put(new float[] { 0.0f, 0.0f, 0.0f });
	
	  /** Velocity of the source sound. */
	  FloatBuffer sourceVel = BufferUtils.createFloatBuffer(3).put(new float[] { 0.0f, 0.0f, 0.0f });
	
	  /** Position of the listener. */
	  FloatBuffer listenerPos = BufferUtils.createFloatBuffer(3).put(new float[] { 0.0f, 0.0f, 0.0f });
	
	  /** Velocity of the listener. */
	  FloatBuffer listenerVel = BufferUtils.createFloatBuffer(3).put(new float[] { 0.0f, 0.0f, 0.0f });
	
	  /** Orientation of the listener. (first 3 elements are "at", second 3 are "up") */
	  FloatBuffer listenerOri =
	      BufferUtils.createFloatBuffer(6).put(new float[] { 0.0f, 0.0f, -1.0f,  0.0f, 1.0f, 0.0f });
	
	  static {
			try {
				AL.create();
			} catch (LWJGLException e) {
				
			}
	  }
	  
	public OpenGLSound(String p_filename) {
		sourcePos.flip();
		sourceVel.flip();
		listenerPos.flip();
		listenerVel.flip();
		listenerOri.flip();
		
		loadALData(p_filename);
		
	    setListenerValues();
	}
	
	public void finalize() {
		killALData();
	}
	private int loadALData(String p_filename) {
	    // Load wav data into a buffer.
	    AL10.alGenBuffers(buffer);

	    if(AL10.alGetError() != AL10.AL_NO_ERROR)
	      return AL10.AL_FALSE;

	    EasyReadingFile file=new EasyReadingFile(p_filename);
	    
	    WaveData waveFile = WaveData.create(file.getAll());
	    AL10.alBufferData(buffer.get(0), waveFile.format, waveFile.data, waveFile.samplerate);
	    waveFile.dispose();

	    // Bind the buffer with the source.
	    AL10.alGenSources(source);

	    if (AL10.alGetError() != AL10.AL_NO_ERROR)
	      return AL10.AL_FALSE;

	    AL10.alSourcei(source.get(0), AL10.AL_BUFFER,   buffer.get(0) );
	    AL10.alSourcef(source.get(0), AL10.AL_PITCH,    1.0f          );
	    AL10.alSourcef(source.get(0), AL10.AL_GAIN,     1.0f          );
	    AL10.alSource (source.get(0), AL10.AL_POSITION, sourcePos     );
	    AL10.alSource (source.get(0), AL10.AL_VELOCITY, sourceVel     );

	    // Do another error check and return.
	    if (AL10.alGetError() == AL10.AL_NO_ERROR)
	      return AL10.AL_TRUE;

	    return AL10.AL_FALSE;

	}
	
	  /**
	   * void setListenerValues()
	   *
	   *  We already defined certain values for the Listener, but we need
	   *  to tell OpenAL to use that data. This function does just that.
	   */
	  void setListenerValues() {
	    AL10.alListener(AL10.AL_POSITION,    listenerPos);
	    AL10.alListener(AL10.AL_VELOCITY,    listenerVel);
	    AL10.alListener(AL10.AL_ORIENTATION, listenerOri);
	  }

	  /**
	   * void killALData()
	   *
	   *  We have allocated memory for our buffers and sources which needs
	   *  to be returned to the system. This function frees that memory.
	   */
	  void killALData() {
	    AL10.alDeleteSources(source);
	    AL10.alDeleteBuffers(buffer);
	  }

	  public void play() {
		    AL10.alSourcePlay(source.get(0));
	  }

	  public void stop() {
		    AL10.alSourceStop(source.get(0));
	  }
	  
	  public void pause() {
		  AL10.alSourcePause(source.get(0));
	  }
}