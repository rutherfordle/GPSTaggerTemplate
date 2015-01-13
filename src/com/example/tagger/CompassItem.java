package com.example.tagger;

import java.util.Random;

import com.example.tagger.R;
import com.example.tagger.R.id;
import com.example.tagger.R.layout;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

public class CompassItem extends Activity{
	float[] aValues = new float[3];
	float[] mValues = new float[3];
	private Handler counterHandler = new Handler();
	CompassView compassView;
	SensorManager sensorManager;
	private float d;
	@Override
    public void onCreate(Bundle savedInstanceState) {
		  super.onCreate(savedInstanceState);
		  //super.onCreate(icicle); 
		  this.setContentView(R.layout.compasslayout);

		  compassView = (CompassView)this.findViewById(R.id.compassView);
		  sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
		  updateOrientation(new float[] {0, 0, 0});
        //super.onCreate(savedInstanceState);
       
        
        TextView txtProduct = (TextView) findViewById(R.id.product_label);
        
        Intent i = getIntent();
        // getting attached intent data
        String product = i.getStringExtra("product");
        String destB = i.getStringExtra("Dest");
        Float num = Float.valueOf(destB);
        d = num;
        //compassView.setDest(num);
        // displaying selected product name
        txtProduct.setText(product);
       // Toast.makeText(getApplicationContext(), destB, Toast.LENGTH_SHORT).show();
        
	}
	  public void setMyBearing(View V) {
		  EditText mEdit = (EditText)findViewById(R.id.NorthB);
		  Float setNum = null;
		  int min = 1;
		  int max = 10;

		  Random r = new Random();
		  int i1 = r.nextInt(max - min + 1) + min;
		  //158.34108
		  float b = (float) 720.00/i1;
		  //CompassView compass = (CompassView) findViewById(R.id.compassView);

		  try {
		    setNum = new Float(mEdit.getText().toString());
		  } 
		  catch (Exception ignore) {
		    // ignore
		  }

		  // This happens when we had an exception before
		  if (setNum == null) {
			  Toast.makeText(getApplicationContext(), "Setting...." , Toast.LENGTH_SHORT).show();
			  Float errCode = (float) 666.00;
		    // [...]
			  
	    	 // compass.setBearing((float) b, errCode);
		  }

		  // This happens when the above float was correctly parsed
		  else {
			 // Toast.makeText(getApplicationContext(), "Not NULL" , Toast.LENGTH_SHORT).show();
			  compassView.setBearing((float) setNum);
			  compassView.invalidate();
			  
		  }
	  }
	private void LetsCount(){
	
		counterHandler.postDelayed(TextViewChanger, 10000);
	}
	private Runnable TextViewChanger = new Runnable(){
	    	public void run() {
	    		//CompassView compass = (CompassView) findViewById(R.id.compassView);
	    		//getTags2();
	    		Toast.makeText(getApplicationContext(), "msg msg", Toast.LENGTH_SHORT).show();
	    		LetsCount();
	    	}
	    };
	
	private void updateOrientation(float[] values) {
	      if (compassView!= null) {
	    	  Float setNum = null;
	    	  EditText mEdit = (EditText)findViewById(R.id.NorthB);
			  try {
				    setNum = new Float(mEdit.getText().toString());
				  } 
				  catch (Exception ignore) {
				    // We're ignoring potential exceptions for the example.
				    // Cleaner solutions would treat NumberFormatException
				    // and NullPointerExceptions here
				  }

				  // This happens when we had an exception before
				  if (setNum == null) {
					  compassView.setBearing(values[0]);
				        compassView.setDest(d);
				        compassView.setPitch(values[1]);
				   	    compassView.setRoll(-values[2]);
				    // [...]
					  
			    	 // compass.setBearing((float) b, errCode);
				  }

				  // This happens when the above float was correctly parsed
				  else {
					 // Toast.makeText(getApplicationContext(), "Not NULL" , Toast.LENGTH_SHORT).show();
					 // compassView.setBearing((float) setNum);
					  
				  }
	       

	   	    compassView.invalidate();
	   	  }
	   	}
	    
	    private float[] calculateOrientation() {
	      float[] values = new float[3];
	      float[] R = new float[9];
	      float[] outR = new float[9];

	      SensorManager.getRotationMatrix(R, null, aValues, mValues);
	      SensorManager.remapCoordinateSystem(R, 
	                                          SensorManager.AXIS_X, 
	                                          SensorManager.AXIS_Z, 
	                                          outR);

	      SensorManager.getOrientation(outR, values);

	      // Convert from Radians to Degrees.
	      values[0] = (float) Math.toDegrees(values[0]);
	      values[1] = (float) Math.toDegrees(values[1]);
	      values[2] = (float) Math.toDegrees(values[2]);

	      return values;
	    }
	    
	    private final SensorEventListener sensorEventListener = new SensorEventListener() {
	      public void onSensorChanged(SensorEvent event) {
	        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
	          aValues = event.values;
	        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
	          mValues = event.values;

	        updateOrientation(calculateOrientation());
	      }

	      public void onAccuracyChanged(Sensor sensor, int accuracy) {}
	   	};
	   	
	   	@Override
	   	protected void onResume() {
	   	  super.onResume();

	   	  Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
	   	  Sensor magField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

	   	  sensorManager.registerListener(sensorEventListener, 
	   	                                 accelerometer, 
	   	                                 SensorManager.SENSOR_DELAY_FASTEST);
	   	  sensorManager.registerListener(sensorEventListener, 
	   	                                 magField,
	   	                                 SensorManager.SENSOR_DELAY_FASTEST);
	   	}

	   	@Override
	   	protected void onStop() {
	   	  sensorManager.unregisterListener(sensorEventListener);
	   	  super.onStop();
	   	}
	
	
}
