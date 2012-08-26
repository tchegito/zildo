package com.zildo;

import zildo.Zildo;
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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public class ZildoActivity extends Activity {
	
	TouchListener touchListener;
	public static AudioManager mgr;
	Handler handler;
	OpenGLES20SurfaceView view;
	
	final int RESET_SPLASHSCREEN = 99;
	
	
	interface SimpleCallback {
		public void doIt();
	}
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable fullscreen
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        view = new OpenGLES20SurfaceView(this);

        // Display splash screen
        view.setBackgroundResource(R.drawable.splashscreen480320);
        
        // Initialize platform dependent
        PlatformDependentPlugin.currentPlugin = KnownPlugin.Android;
        
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        
        AssetManager assetManager = getAssets();
        AndroidReadingFile.assetManager = assetManager;
        AndroidReadingFile.context = getBaseContext();
        
        ClientThread clientThread = new ClientThread();
        
        Client client = clientThread.getClient();
        
        touchListener = new TouchListener(client);
        
     // Get phone resolution
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        Zildo.screenX = metrics.widthPixels;
        Zildo.screenY = metrics.heightPixels;
        
   		view.setViewRenderer(new OpenGLRenderer(client, touchListener));
   		view.setOnTouchListener(touchListener);
   		
   		clientThread.start();
   		
   		mgr = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
	    //float streamVolumeCurrent = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
	    //float streamVolumeMax = mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC); 
	    //System.out.println("Volumes are "+streamVolumeCurrent+" and "+streamVolumeMax);
	    
        setContentView(view);
        
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        
        handler = new Handler(){
        	@Override
        	public void handleMessage(Message msg) {
        	switch(msg.what){
        	     case RESET_SPLASHSCREEN:
        	            // Remove splashscreen
        	    	 	view.setBackgroundResource(0); 
        	            break;
        	   }
        	}
        };

    }
    
/*
    @Override
    protected void onStop() {
    	super.onStop();
    	view.onPause();
    }
  */  
    @Override
    protected void onPause() {
    	super.onPause();
    	view.onPause();
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	if (view != null) {
    		view.onResume();
    	}
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
}