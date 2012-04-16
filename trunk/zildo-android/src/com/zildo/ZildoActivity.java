package com.zildo;

import zildo.client.Client;
import zildo.client.PlatformDependentPlugin;
import zildo.client.PlatformDependentPlugin.KnownPlugin;
import zildo.client.gui.menu.StartMenu;
import zildo.fwk.ZUtils;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public class ZildoActivity extends Activity {
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
        
        TouchListener touchListener = new TouchListener(client);
        
   		view.setRenderer(new OpenGLRenderer(client, touchListener));
   		view.setOnTouchListener(touchListener);
   		
   		clientThread.start();
   		
        setContentView(view);
        

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
            
    	}
    	
    	public Client getClient() {
    		return client;
    	}
    }
}