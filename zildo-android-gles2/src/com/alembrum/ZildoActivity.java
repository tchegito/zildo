package com.alembrum;

import java.util.Locale;

import zildo.Zildo;
import zildo.client.Client;
import zildo.client.PlatformDependentPlugin;
import zildo.client.PlatformDependentPlugin.KnownPlugin;
import zildo.client.gui.menu.StartMenu;
import zildo.fwk.ZUtils;
import zildo.fwk.ui.EditableItemMenu;
import zildo.platform.opengl.AndroidSoundEngine;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public class ZildoActivity extends Activity {
	
	static TouchListener touchListener = null;
	static Handler handler = null;
	OpenGLES20SurfaceView view;
	static ClientThread clientThread;
	static OpenGLRenderer renderer;
	
	static ZildoDialogs zd;
	
	final static int RESET_SPLASHSCREEN = 99;
	final static int PLAYERNAME_DIALOG = 98;

	static class SplashHandler extends Handler {
			OpenGLES20SurfaceView view;
			ZildoDialogs zds;
			
		public SplashHandler(OpenGLES20SurfaceView view, ZildoDialogs zd) {
			this.view = view;
			this.zds = zd;
		}
		
    	@Override
    	public void handleMessage(Message msg) {
    	switch(msg.what){
    	     case RESET_SPLASHSCREEN:
    	            // Remove splashscreen
    	    	 	view.setBackgroundResource(0); 
    	            break;
    	     case PLAYERNAME_DIALOG:
    	    	 zd.askPlayerName((EditableItemMenu) msg.obj);
    	    	 break;
    	   }
    	}
	}


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		
        // Enable fullscreen
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.main);
       	view = (OpenGLES20SurfaceView) findViewById(R.id.glsurfaceview);
	    
        if (clientThread == null) {
        	clientThread = new ClientThread();
	    	// Display splash screen
        	if ("fr".equals(Locale.getDefault().getLanguage())) {
        		view.setBackgroundResource(R.drawable.splash480320_fr);
        	} else {
        		view.setBackgroundResource(R.drawable.splash480320_en);
        	}
        }

        // Initialize platform dependent
        PlatformDependentPlugin.currentPlugin = KnownPlugin.Android;
        
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        
        AssetManager assetManager = getAssets();
        AndroidReadingFile.assetManager = assetManager;
        AndroidReadingFile.context = getBaseContext();

    	client = clientThread.getClient();
    
    	if (touchListener == null) {
    		touchListener = new TouchListener(client);
    	}
    	
     // Get phone resolution
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        Zildo.screenX = metrics.widthPixels;
        Zildo.screenY = metrics.heightPixels;
        
        if (renderer == null) {
        	renderer = new OpenGLRenderer(client, touchListener);
        }
        
   		view.setViewRenderer(renderer);
   		view.setOnTouchListener(touchListener);
   		
   		if (!clientThread.isAlive()) {
   			clientThread.start();
   		}	    
        
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        
        
        if (zd == null) {
        	createDialogs();
        }
        if (handler == null) {
        	handler = new SplashHandler(view, zd);
        }
        
    }
    

    @Override
    protected void onStop() {
    	super.onStop();
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	if (view != null) {
    		view.onPause();
    	}
    	Log.d("zildo", "onPause");
       	Log.d("zildo", "pause sounds");
   		AndroidSoundEngine.pauseAll();
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	if (view != null) {
    		view.onResume();
    	}
    	Log.d("zildo", "onResume");
       	Log.d("zildo", "resume sounds");
   		AndroidSoundEngine.resumeAll();
    }
    
    @Override
    public void onBackPressed() {
    	touchListener.pressBackButton();
    }
    
	static Client client;

	class ClientThread extends Thread {
    	
    	
    	boolean ready = false;
    	
    	public ClientThread() {
    		if (client == null) {
    			client = new Client(true);
    		}
    	}
    	
    	@Override
		public void run() {
    		while (!client.isReady() || handler == null) {
    			ZUtils.sleep(500);
    		}
    		
    		// Game is loaded => ask to remove the splashscreen
    		handler.sendEmptyMessage(RESET_SPLASHSCREEN);
    		
    		Log.d("client", "Client runs !");
            client.handleMenu(new StartMenu());
            
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
    
    private void createDialogs() {
        // 3 : player name
        zd = new ZildoDialogs(new AlertDialog.Builder(this), getBaseContext());
   }
}