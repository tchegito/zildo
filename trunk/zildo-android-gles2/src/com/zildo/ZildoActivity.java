package com.zildo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.net.UnknownHostException;

import zildo.Zildo;
import zildo.client.Client;
import zildo.client.PlatformDependentPlugin;
import zildo.client.PlatformDependentPlugin.KnownPlugin;
import zildo.client.gui.menu.StartMenu;
import zildo.fwk.ZUtils;
import zildo.fwk.ui.UIText;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
	static Handler handler;
	OpenGLES20SurfaceView view;
	
	AlertDialog hafWorked;
	AlertDialog hafProblem;
	
	final static int RESET_SPLASHSCREEN = 99;

	static class SplashHandler extends Handler {
			OpenGLES20SurfaceView view;
			
		public SplashHandler(OpenGLES20SurfaceView view) {
			this.view = view;
		}
		
    	@Override
    	public void handleMessage(Message msg) {
    	switch(msg.what){
    	     case RESET_SPLASHSCREEN:
    	            // Remove splashscreen
    	    	 	view.setBackgroundResource(0); 
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
        
        handler = new SplashHandler(view);

        //Log.d("zildo", "trying httpconnection");
        //sendAchievementMessage();
        Log.d("zildo", "trying worldregister");
        
        createDialogs();

        //sendRequest();
    
    }
    

    @Override
    protected void onStop() {
    	super.onStop();
    	view.onPause();
    }
    
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
    
	private final static String url = "http://legendofzildo.appspot.com";
	private final static String serverServlet = "srv";
	private final static String charset = "UTF-8";

    private boolean sendRequest() {
		try {
			 //ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			 //conMgr.get
			 
			 
			StringBuilder request = new StringBuilder();
			request.append(url).append("/").append(serverServlet);
			request.append("?command=CREATE");
			request.append("&name=").append(
					URLEncoder.encode("jeremiade_a_achete_son_tel_a_la", charset));
			request.append("&port=").append("123");
			request.append("&ip=").append("fete_foraine");
			request.append("&nbPlayers=").append(37);
	        Log.d("zildo", "sending message...");

			URL objUrl = new URL(request.toString());
			URLConnection urlConnect = objUrl.openConnection();

	        Log.d("zildo", "awaiting response...");

			// Add server infos
			InputStream in = urlConnect.getInputStream();
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(in));
			int result = reader.read();
			in.close();

			if (result == 48) { // ASCII code of '0') {
				hafWorked.show();
				return true;
			}
			return false;
		} catch (UnknownHostException e) {
			hafProblem.show();
			Log.d("zildo", "No internet connection !");
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
    
    private void createDialogs() {
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(UIText.getMenuText("dialog.haf"))
               .setCancelable(false)
               .setPositiveButton(UIText.getMenuText("dialog.haf.ok"), new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                   }
               });
        hafWorked = builder.create();
        
        builder = new AlertDialog.Builder(this);
        builder.setMessage(UIText.getMenuText("dialog.haf.noInternet"))
               .setCancelable(false)
               .setPositiveButton(UIText.getMenuText("dialog.haf.retry"), new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                   }
               }).setNegativeButton(UIText.getMenuText("dialog.haf.cancel"), new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                   }
               });
        hafProblem = builder.create();        
    	
    }
}