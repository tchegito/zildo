package com.zildo;

import zildo.client.Client;
import zildo.client.PlatformDependentPlugin;
import zildo.client.PlatformDependentPlugin.KnownPlugin;
import zildo.client.gui.menu.StartMenu;
import zildo.fwk.ZUtils;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public class ZildoActivity extends Activity {
	
	TouchListener touchListener;
	public static AudioManager mgr;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Enable fullscreen
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);

        GLSurfaceView view = new GLSurfaceView(this);
        //view.setBackgroundColor(Color.BLUE);

        // Initialize platform dependent
        
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        PlatformDependentPlugin.currentPlugin = KnownPlugin.Android;
        
        AssetManager assetManager = getAssets();
        AndroidReadingFile.assetManager = assetManager;

        ClientThread clientThread = new ClientThread();
        
        Client client = clientThread.getClient();
        
        touchListener = new TouchListener(client);
        
   		view.setRenderer(new OpenGLRenderer(client, touchListener));
   		view.setOnTouchListener(touchListener);
   		
   		clientThread.start();
   		
   		mgr = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
	    float streamVolumeCurrent = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
	    float streamVolumeMax = mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC); 
	    //System.out.println("Volumes are "+streamVolumeCurrent+" and "+streamVolumeMax);
	    
        setContentView(view);
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);

    }
    

	
    @Override
    public void onBackPressed() {
    	touchListener.pressBackButton();
    }
    
    class ClientThread extends Thread {
    	
    	Client client;
    	
    	boolean ready = false;
    	
    	public ClientThread() {
    		client = new Client(true);
    	}
    	
    	@Override
		public void run() {
    		while (!client.isReady()) {
    			ZUtils.sleep(500);
    		}
    		Log.d("client", "Client runs !");
            client.handleMenu(new StartMenu());
           	//client.run();

            
            while (!client.isDone()) {
            	ZUtils.sleep(500);
            }
            System.out.println("ask finish");
            finish();
            System.out.println("still there");
            android.os.Process.killProcess(android.os.Process.myPid());

    	}
    	
    	public Client getClient() {
    		return client;
    	}
    }
}