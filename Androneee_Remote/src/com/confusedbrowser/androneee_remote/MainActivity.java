package com.confusedbrowser.androneee_remote;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.View;
import android.app.Activity;
import android.view.Menu;
import android.content.Context;
import android.content.Intent;
import android.widget.EditText;
import android.util.Log;
import android.widget.TextView;
import android.widget.SeekBar;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.Sensor;

import java.io.*;
import java.net.*;

public class MainActivity extends Activity implements SeekBar.OnSeekBarChangeListener{
	
	static String ipAddress = "192.168.1.12";
	SeekBar mSeekBar;
    TextView mProgressText;
	InetAddress serverAddr;
	SendControlsThread sendControls;
	VehicleStatusThread vehicleStatus;
	private float[] m_rotationMatrix = new float[16];
	private float[] m_orientation = new float[4];
	
	
	
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Remove title and go full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        // Set the XML view for this activity
        setContentView(R.layout.activity_main);
        
        // Create threads for send and receiving data from the vehicle  
        sendControls = new SendControlsThread(this, ipAddress, 2047, 50);
        sendControls.start();
        
        vehicleStatus = new VehicleStatusThread(ipAddress, 2047);
        vehicleStatus.start();
        
        // Set up slider control to pass to sendControls
        mProgressText = (TextView)findViewById(R.id.serverPos);
        mSeekBar = (SeekBar)findViewById(R.id.servoBar);
        mSeekBar.setOnSeekBarChangeListener(this);
        
        
      //listener for accelerometer, use anonymous class for simplicity
    	((SensorManager)getSystemService(Context.SENSOR_SERVICE)).registerListener(
    	     new SensorEventListener() {    
    	        @Override  
    	        public void onSensorChanged(SensorEvent event) {  
    	           TextView pitch = (TextView) findViewById(R.id.pitch);
    	           TextView roll = (TextView) findViewById(R.id.roll);
    	           TextView yaw = (TextView) findViewById(R.id.yaw);
    	           pitch.setText("pitch: "+event.values[0]);
    	           pitch.append(" | "+event.values[1]);
    	           pitch.append(" | "+event.values[2]);
    	           SensorManager.getRotationMatrixFromVector (m_rotationMatrix, event.values);
    	           SensorManager.getOrientation(m_rotationMatrix, m_orientation);
    	           
    	           float yaw_val = m_orientation[0] * 57.2957795f;
    	           float pitch_val = m_orientation[1] * 57.2957795f;
    	           float roll_val = m_orientation[2] * 57.2957795f;
    	           
    	           pitch.setText("Pitch: "+pitch_val);
    	           roll.setText("Roll: "+roll_val);
    	           yaw.setText("Yaw: "+yaw_val);

    	        }
    	        @Override  
    	        public void onAccuracyChanged(Sensor sensor, int accuracy) {} //ignore

    	    },
    	    ((SensorManager)getSystemService(Context.SENSOR_SERVICE))
    	    .getSensorList(Sensor.TYPE_ROTATION_VECTOR).get(0),   
    	     SensorManager.SENSOR_DELAY_NORMAL);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
    	sendControls.setPosition(progress);
	 	mProgressText.setText(progress+"");
    }
 
    public void onStartTrackingTouch(SeekBar seekBar) {
    	//mProgressText.setText(getString(R.string.seekbar_tracking_on));
    }
 
    public void onStopTrackingTouch(SeekBar seekBar) {
        //mTrackingText.setText(getString(R.string.seekbar_tracking_off));
    }
    
    @Override
	protected void onResume() 
	{
		super.onResume();
	}

	protected void onStop()
	{
		super.onStop();
		sendControls.stop_sending();
		vehicleStatus.stop_listening();
		this.finish();
	}
    
}
