package com.alembrum;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;
import android.view.InputDevice;
import android.view.MotionEvent;

/** Gamepad are handled since API 12 => HONEYCOMB_MR1 **/
@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
public class GamePadListener {

	TouchListener touchListener;
	
	public GamePadListener(TouchListener touchListener) {
		this.touchListener = touchListener;
	}
	
	public boolean handleMotionEvent(MotionEvent ev) {
	      // Process all historical movement samples in the batch
        final int historySize = ev.getHistorySize();

        // Process the movements starting from the
        // earliest historical position in the batch
        for (int i = 0; i < historySize; i++) {
            // Process the event at historical position i
            processJoystickInput(ev, i);
        }

        // Process the current movement sample in the batch (position -1)
        processJoystickInput(ev, -1);
        return true;
	}
	
    private void processJoystickInput(MotionEvent ev, int historyPos) {
        
    	/*
    	float x = ev.getAxisValue(MotionEvent.AXIS_X);
    	float y = ev.getAxisValue(MotionEvent.AXIS_Y);
    	float hatX = ev.getAxisValue(MotionEvent.AXIS_HAT_X);
    	float hatY = ev.getAxisValue(MotionEvent.AXIS_HAT_Y);
    	*/
        InputDevice mInputDevice = ev.getDevice();

        // Calculate the horizontal distance to move by
        // using the input value from one of these physical controls:
        // the left control stick, hat axis, or the right control stick.
        float x = getCenteredAxis(ev, mInputDevice,
                MotionEvent.AXIS_X, historyPos);
        if (x == 0) {
            x = getCenteredAxis(ev, mInputDevice,
                    MotionEvent.AXIS_HAT_X, historyPos);
        }
        if (x == 0) {
            x = getCenteredAxis(ev, mInputDevice,
                    MotionEvent.AXIS_Z, historyPos);
        }

        // Calculate the vertical distance to move by
        // using the input value from one of these physical controls:
        // the left control stick, hat switch, or the right control stick.
        float y = getCenteredAxis(ev, mInputDevice,
                MotionEvent.AXIS_Y, historyPos);
        if (y == 0) {
            y = getCenteredAxis(ev, mInputDevice,
                    MotionEvent.AXIS_HAT_Y, historyPos);
        }
        if (y == 0) {
            y = getCenteredAxis(ev, mInputDevice,
                    MotionEvent.AXIS_RZ, historyPos);
        }

    	touchListener.setGamePadDirection(x, y);
    }
    
	private static float getCenteredAxis(MotionEvent event,
            InputDevice device, int axis, int historyPos) {
        final InputDevice.MotionRange range =
                device.getMotionRange(axis, event.getSource());

        // A joystick at rest does not always report an absolute position of
        // (0,0). Use the getFlat() method to determine the range of values
        // bounding the joystick axis center.
        if (range != null) {
            final float flat = range.getFlat();
            final float value =
                    historyPos < 0 ? event.getAxisValue(axis):
                    event.getHistoricalAxisValue(axis, historyPos);

            // Ignore axis values that are within the 'flat' region of the
            // joystick axis center.
            if (Math.abs(value) > flat) {
                return value;
            }
        }
        return 0;
    }


}
