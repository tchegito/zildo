package com.alembrum;

import zildo.Zildo;
import zildo.client.Client;
import zildo.client.PlatformDependentPlugin;
import zildo.client.PlatformDependentPlugin.KnownPlugin;
import zildo.client.gui.menu.StartMenu;
import zildo.fwk.ZUtils;
import zildo.fwk.net.www.CrashReporter;
import zildo.fwk.ui.EditableItemMenu;
import zildo.platform.input.AndroidKeyboardHandler.KeyLocation;
import zildo.platform.opengl.AndroidSoundEngine;
import zildo.resource.Constantes;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

/**
 * About splashscreen:
 *
 * we use a Thread for initializing all the long stuffs. It could be done differently using 2 activities
 * probably.
 *
 * Anywway, there is special notes:
 * 1) it doesn't work to set GL view as invisibile waiting for initialization to be done,
 *    because the OpenGLRenderer#onSurfaceCreated has to be triggered to finish the job. And if
 *    the view is invisible, this method is never called
 * 2) the ZildoDialogs initialization made our splash screen hidden during its initialization. So
 *    I delayed it with a provider pattern, to create it when needed (new game)
 *
 */
@SuppressLint("ClickableViewAccessibility")
public class ZildoActivity extends Activity {

	static TouchListener touchListener = null;
	GamePadListener gamePadListener;
	static Handler handler = null;
	OpenGLES20SurfaceView view;
	static ClientThread clientThread;
	static OpenGLRenderer renderer;

	final static int RESET_SPLASHSCREEN = 99;
	final static int PLAYERNAME_DIALOG = 98;
	final static int TOAST = 97;

	final static String PARAM_LEFTHANDED = "leftHanded";
	final static String PARAM_MOVINGCROSS = "movingCross";

	interface ActionRunner {
		void run();
	}

	interface Provider<T> {
		T make();
	}

	static class SplashHandler extends Handler {
		OpenGLES20SurfaceView view;
		ZildoDialogs zd;
		Context appContext;	// This context is global to the whole application, depends not on activity lifecycle
		ActionRunner removeSplashAction;
		Provider<ZildoDialogs> createZdAction;

		public SplashHandler(OpenGLES20SurfaceView view, Context ctx,
							 ActionRunner splashAction,
							 Provider<ZildoDialogs> createZdAction) {
			this.view = view;
			this.appContext = ctx;
			this.removeSplashAction = splashAction;
			this.createZdAction = createZdAction;
		}

		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
				case RESET_SPLASHSCREEN:
					// Remove splashscreen
					removeSplashAction.run();
					break;
				case PLAYERNAME_DIALOG:
					if (zd == null) {
						zd = createZdAction.make();
					}
					zd.askPlayerName((EditableItemMenu) msg.obj);
					break;
				case TOAST:
					int slot = msg.arg1;
					Toast.makeText(appContext, "Game has been saved before crash on "+slot, Toast.LENGTH_LONG).show();
					break;
			}
		}
	}


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);

			// Enable fullscreen
			this.requestWindowFeature(Window.FEATURE_NO_TITLE);
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
			//getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);



			if (clientThread == null) {
				clientThread = new ClientThread();
				// Display splash screen
				getWindow().setBackgroundDrawableResource(R.drawable.splash480320_fr);
			}

			String versionName = "0.00";
			try {
				versionName = getPackageManager().getPackageInfo("com.alembrum", 0).versionName;
			} catch (NameNotFoundException e) {

			}
			Constantes.CURRENT_VERSION_DISPLAYED = versionName;

			// Initialize platform dependent
			PlatformDependentPlugin.currentPlugin = KnownPlugin.Android;

			//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

			AssetManager assetManager = getAssets();
			AndroidReadingFile.assetManager = assetManager;
			AndroidReadingFile.context = getBaseContext();

			// Get phone resolution
			DisplayMetrics metrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(metrics);
			Zildo.screenX = metrics.widthPixels;
			Zildo.screenY = metrics.heightPixels;

			if (handler == null) {
				handler = new SplashHandler(view, getApplicationContext(), new ActionRunner() {
					public void run() {
						setContentView(com.alembrum.R.layout.main);

						view = findViewById(R.id.glsurfaceview); // Nul si on a pas fait le setContentView avant

						view.setViewRenderer(renderer);
						view.setOnTouchListener(touchListener);

						client.setLeftHanded(getPreferences(MODE_PRIVATE).getBoolean(PARAM_LEFTHANDED, false));
						client.setMovingCross(getPreferences(MODE_PRIVATE).getBoolean(PARAM_MOVINGCROSS, false));

					}
				}, () -> new ZildoDialogs(new AlertDialog.Builder(ZildoActivity.this), getBaseContext()));
			}

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
				gamePadListener = new GamePadListener(touchListener);
			}

			if (!clientThread.isAlive()) {
				clientThread.start();
			}

			this.setVolumeControlStream(AudioManager.STREAM_MUSIC);

		} catch (RuntimeException t) {
			// Send a more detailed report than Google one
			new CrashReporter(t).addContext().sendReport();
			// Throw exception even though
			throw t;
		}
	}

	/*
    private void placeSavegame() {
        OutputStream fileOut = Zildo.pdPlugin.prepareSaveFile(Constantes.SAVEGAME_DIR + Constantes.SAVEGAME_FILE + "2");
        byte[] saveGame1 = {0, 9, 12, 115, 116, 97, 114, 116, 95, 118, 105, 115, 105, 116, 50, 1, 12, 116, 114, 105, 103, 95, 101, 99, 104, 97, 110, 103, 101, 1, 11, 102, 117, 105, 116, 101, 95, 116, 111, 110, 121, 49, 1, 9, 118, 101, 114, 116, 95, 115, 101, 101, 110, 1, 16, 118, 111, 108, 101, 117, 114, 115, 109, 52, 40, 49, 51, 44, 32, 51, 41, 1, 11, 116, 114, 105, 103, 95, 102, 97, 108, 99, 111, 114, 1, 18, 105, 103, 111, 114, 95, 112, 114, 111, 109, 105, 115, 101, 95, 115, 119, 111, 114, 100, 1, 9, 102, 114, 101, 101, 100, 73, 103, 111, 114, 1, 16, 118, 111, 108, 101, 117, 114, 115, 103, 49, 40, 49, 56, 44, 32, 52, 41, 1, 3, 0, 6, 0, 0, 0, 0, 0, 0, 3, 2, 113, 101, 2, 0, 3, 8, 78, 69, 67, 75, 76, 65, 67, 69, 0, 0, 8, 82, 79, 67, 75, 95, 66, 65, 71, 0, 0, 5, 83, 87, 79, 82, 68, 0, 0, 7, 118, 111, 108, 101, 117, 114, 115, 1, 1, 2, -113, 2, 115, 0, -32, 2, 88, 2, 6, 68, 105, 122, 122, 105, 101, 23, 91, 91, 68, 89, 78, 65, 77, 73, 84, 69, 44, 32, 49, 93, 44, 32, 49, 53, 44, 32, 50, 48, 93, 16, 66, 105, 108, 101, 108, 73, 103, 111, 114, 86, 105, 108, 108, 97, 103, 101, 74, 91, 91, 69, 77, 80, 84, 89, 95, 66, 65, 71, 44, 32, 49, 93, 44, 32, 49, 48, 48, 44, 32, 50, 93, 44, 32, 91, 91, 66, 76, 85, 69, 68, 82, 79, 80, 44, 32, 49, 93, 44, 32, 49, 53, 44, 32, 45, 49, 93, 44, 32, 91, 91, 68, 89, 78, 65, 77, 73, 84, 69, 44, 32, 49, 93, 44, 32, 49, 48, 48, 44, 32, 50, 93};
        byte[] saveGame28 = {0, 22, 8, 104, 101, 99, 116, 111, 114, 95, 49, 1, 10, 115, 116, 111, 112, 95, 100, 101, 102, 105, 49, 1, 12, 115, 116, 97, 114, 116, 95, 118, 105, 115, 105, 116, 49, 1, 19, 115, 117, 105, 116, 101, 95, 118, 105, 115, 105, 116, 49, 95, 119, 101, 97, 112, 111, 110, 1, 12, 115, 116, 97, 114, 116, 95, 118, 105, 115, 105, 116, 50, 1, 12, 116, 114, 105, 103, 95, 101, 99, 104, 97, 110, 103, 101, 1, 11, 102, 117, 105, 116, 101, 95, 116, 111, 110, 121, 49, 1, 9, 118, 101, 114, 116, 95, 115, 101, 101, 110, 1, 15, 97, 116, 116, 97, 113, 117, 101, 95, 118, 111, 108, 101, 117, 114, 115, 1, 16, 118, 111, 108, 101, 117, 114, 115, 109, 52, 40, 49, 51, 44, 32, 51, 41, 1, 11, 116, 114, 105, 103, 95, 102, 97, 108, 99, 111, 114, 1, 8, 118, 51, 95, 99, 114, 97, 116, 101, 1, 16, 118, 111, 108, 101, 117, 114, 115, 109, 52, 95, 98, 117, 116, 116, 111, 110, 1, 10, 98, 97, 99, 107, 67, 111, 117, 99, 111, 117, 1, 12, 114, 101, 116, 111, 117, 114, 95, 116, 114, 105, 111, 110, 1, 12, 104, 101, 99, 116, 111, 114, 95, 99, 97, 108, 108, 49, 1, 12, 104, 101, 99, 116, 111, 114, 95, 99, 97, 108, 108, 50, 1, 12, 104, 101, 99, 116, 111, 114, 95, 99, 97, 108, 108, 51, 1, 17, 118, 111, 108, 101, 117, 114, 115, 109, 50, 117, 40, 49, 48, 44, 32, 55, 41, 1, 16, 118, 111, 108, 101, 117, 114, 115, 103, 49, 40, 49, 56, 44, 32, 52, 41, 1, 15, 118, 111, 108, 101, 117, 114, 115, 109, 51, 40, 56, 44, 32, 56, 41, 1, 7, 101, 99, 104, 97, 110, 103, 101, 1, 3, 1, 6, 0, 0, 0, 0, 0, 0, 26, 2, 113, 101, 2, 0, 4, 8, 78, 69, 67, 75, 76, 65, 67, 69, 0, 0, 8, 82, 79, 67, 75, 95, 66, 65, 71, 0, 0, 5, 83, 87, 79, 82, 68, 0, 0, 5, 83, 87, 79, 82, 68, 0, 0, 9, 112, 114, 105, 115, 111, 110, 101, 120, 116, 2, -19, 1, 112, 5, 6, 1, 105, 0, 8, 2, 6, 68, 105, 122, 122, 105, 101, 23, 91, 91, 68, 89, 78, 65, 77, 73, 84, 69, 44, 32, 49, 93, 44, 32, 49, 53, 44, 32, 50, 48, 93, 16, 66, 105, 108, 101, 108, 73, 103, 111, 114, 86, 105, 108, 108, 97, 103, 101, 74, 91, 91, 69, 77, 80, 84, 89, 95, 66, 65, 71, 44, 32, 49, 93, 44, 32, 49, 48, 48, 44, 32, 50, 93, 44, 32, 91, 91, 66, 76, 85, 69, 68, 82, 79, 80, 44, 32, 49, 93, 44, 32, 49, 53, 44, 32, 45, 49, 93, 44, 32, 91, 91, 68, 89, 78, 65, 77, 73, 84, 69, 44, 32, 49, 93, 44, 32, 49, 48, 48, 44, 32, 50, 93};

        byte[] data = saveGame28;

        Log.d("file", "creating savegame");

        try {
            fileOut.write(data, 0, data.length);
            fileOut.close();
            Log.d("file", "savegame successfully created !");
        } catch (IOException e) {
            Log.e("file", "unable to create savegame !");
            e.printStackTrace();
        }

    }
*/
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
		AndroidSoundEngine.pauseAll();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (findViewById(R.id.glsurfaceview) == null && renderer != null) {
			// Our view has been disposed, so we have to recreate it and attach it to our renderer and touch listener
			setContentView(com.alembrum.R.layout.main);
			view = findViewById(R.id.glsurfaceview);
			view.setViewRenderer(renderer);
			view.setOnTouchListener(touchListener);
		}
		if (view != null) {
			view.onResume();

		}
		AndroidSoundEngine.resumeAll();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (handleKeys(keyCode, event, true)) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (handleKeys(keyCode, event, false)) {
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}

	private boolean handleKeys(int keyCode, KeyEvent event, boolean press) {
		if ((event.getSource() & InputDevice.SOURCE_GAMEPAD)
				== InputDevice.SOURCE_GAMEPAD) {
			// If user press A or B, we ALWAYS got to return TRUE. Else, Android considers it's back button and handles it not the Zildo's way
			if (event.getRepeatCount() == 0) {
				switch (keyCode) {
					case KeyEvent.KEYCODE_BUTTON_X:
						touchListener.pressGameButton(KeyLocation.VP_BUTTON_Y, press);
						return true;
					case KeyEvent.KEYCODE_BUTTON_Y:
						touchListener.pressGameButton(KeyLocation.VP_INVENTORY, press);
						return true;
					case KeyEvent.KEYCODE_BUTTON_A:
						touchListener.pressGameButton(KeyLocation.VP_BUTTON_X, press);
						return true;
					case KeyEvent.KEYCODE_BUTTON_B:
						touchListener.pressBackButton(press);
						return true;
					case KeyEvent.KEYCODE_BUTTON_SELECT:
						touchListener.pressGameButton(KeyLocation.VP_COMPASS, press);
						return true;
					case KeyEvent.KEYCODE_BUTTON_START:
						touchListener.pressMenuButton(press);
						return true;
				}

			}
			return true;
		}
		switch (keyCode) {
			case KeyEvent.KEYCODE_MENU:
				//do your work
				touchListener.pressMenuButton(press);
				return true;
			case KeyEvent.KEYCODE_BACK:
				touchListener.pressBackButton(press);
				return true;
		}
		return false;
	}

	// Since HONEYCOMB, we can use motion events
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
	@Override
	public boolean dispatchGenericMotionEvent(MotionEvent event) {
		if (gamePadListener != null && (event.getSource() & InputDevice.SOURCE_JOYSTICK) ==
				InputDevice.SOURCE_JOYSTICK &&
				event.getAction() == MotionEvent.ACTION_MOVE) {
			return gamePadListener.handleMotionEvent(event);
		}
		return super.dispatchGenericMotionEvent(event);
	}

	static Client client;

	class ClientThread extends Thread {


		boolean ready = false;

		public ClientThread() {
		}

		@Override
		public void run() {

			if (client == null) {
				client = new Client(true);
			}

			if (touchListener == null) {
				touchListener = new TouchListener(client);
			}
			if (renderer == null) {
				renderer = new OpenGLRenderer(client, touchListener, handler);
			}

			// Main objects are created => trigger the surface creation by removing splash screen
			handler.sendEmptyMessage(RESET_SPLASHSCREEN);

			while (!client.isReady() || handler == null) {
				ZUtils.sleep(100);
			}



			//placeSavegame();

			Log.d("client", "Client runs !");
			client.handleMenu(new StartMenu());

			while (!client.isDone()) {
				ZUtils.sleep(500);
			}

			// End of the application : save preferences
			getPreferences(MODE_PRIVATE).edit().putBoolean(PARAM_LEFTHANDED, client.isLeftHanded()).commit();
			getPreferences(MODE_PRIVATE).edit().putBoolean(PARAM_MOVINGCROSS, client.isMovingCross()).commit();
			// And quit
			if(android.os.Build.VERSION.SDK_INT >= 21) {
				finishAndRemoveTask();
			} else {
				finish();
			}

		}

		public Client getClient() {
			return client;
		}
	}
}