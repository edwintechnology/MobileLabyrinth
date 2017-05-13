/**
 *  Dustin Wolf
 *  Android Mobile Graphics
 *  Labyrith
 *  
 *  This is the Labyrinth game, the purpose is to get to the end of the maze without falling through the holes
 *  
 *  Class LabyrinthActivity, extends Activity, implements SensorEventListener
 *  
 *  # onCreate (Bundle) void - the overridden method that creates the application when it starts
 *  + onSensorChanged(SensorEvent) void - when there is a change detected from the sensor event
 *  	the onSensorChanged method is called and the x y z is grabbed
 *  
 */
package com.edwintechnology.labyrinth;

import java.io.InputStream;
import java.util.ArrayList;
import FileIO.Model;
import FileIO.ObjLoader;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener;
import android.view.View;
import android.widget.Button;

/**
 *   Class LabyrinthActivity
 *   
 *   Extends
 *   	Activity
 *   Implements
 *   	SensorEventListener
 *   
 * @author dustin
 *
 */
public class LabyrinthActivity extends Activity implements SensorEventListener {
	
	/* private variables */
	private MyGLSurfaceView mGLView;
	private MyGLRenderer r;
	private ObjLoader objloader;
	ArrayList<Model> Models;
	private static final float kFilteringFactor = 0.01f;
	private static final float DEG = 57.2957795f;
	private ScaleGestureDetector scaleGestureDetector;
	private float density;
	private final String[] objects= { "holeboard.obj", "ball.obj" };
	private SensorManager mSensorManager;
	private Sensor gSensor, mSensor, aSensor;
	
    static float[] gyro = new float[3];
    static float[] gyroOrientation = new float[3];
    static float[] magnet = new float[3];
    static float[] accel = new float[3];
    static float[] grav = new float[3];
	static float[] orientation = new float[3];
	
	private boolean hasM, hasG, hasA = false;
	
	float[] Rmat = new float[9];
    float[] R2 = new float[9];
    float[] Imat = new float[9];
    
    private float pitch, roll, yaw;
	
	public float Pitch()
	{
		return pitch;
	}
	public float Roll()
	{
		return roll;
	}
	public float Yaw()
	{
		return yaw;
	}
	
	private float x,y,z;
	public float X() { return x; }
	public float Y() { return y; }
	public float Z() { return z; }
	
	private Button update;
	
	/**
	 * onCreate
	 * 
	 * @param Bundle
	 * 		Overridden 
	 */
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); // calls inherited constructor
		
		// sets the content view of the labyrinth activity 
		setContentView(R.layout.activity_labyrinth);
		
		// grab the surface view placed in the layout by the R id
		mGLView = (MyGLSurfaceView)findViewById(R.id.surfaceviewclass);
		
		// finds the button that is placed in the layout by the R.id
		update = (Button)findViewById(R.id.update);
		
		// create an onclick event for the button
		View.OnClickListener updateTheView = new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// when it is clicked, it moves the ball to this position
				MyGLRenderer.ball_x = 22.0f;
				MyGLRenderer.ball_y = 22.0f;
				MyGLRenderer.ball_z = 17.1f;
			}
			
        };
        
        // set the click event to the button via the listener
        update.setOnClickListener(updateTheView);
        
        // gets the renderer from the sub class 
        r = mGLView.getRenderer();
        
        // creates the ObjLoader class with the activity passed in
		objloader = new ObjLoader(this);
		
		// grab all the files in the asset folder
        AssetManager assetMgr = this.getAssets();
        InputStream in = null;
        try{
        	// loop through all the objects defined above
        	for(String o : objects)
        	{
        		in = assetMgr.open(o);
        		// read the files
        		objloader.loadFromStream(in);
        	}
        	// updates models from the object loader
        	Models = objloader.getO();
        }
        catch(Exception e){
        	e.printStackTrace();
        }
        
        //set up gesture detector
        scaleGestureDetector = new ScaleGestureDetector(this, new MySimpleOnScaleGestureListener());
		
        // grab the window metrics
		final DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		density = displayMetrics.density;
		MyGLRenderer.x = displayMetrics.widthPixels;
		MyGLRenderer.y = displayMetrics.heightPixels;
		
		// exit is there is no models
		if(Models.size() <= 0) System.exit(-1);
		
		// creates the Sensor Manager object
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        
		// defines all of the sensors with their specific type
		gSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
		mSensorManager.registerListener(this, gSensor, SensorManager.SENSOR_DELAY_GAME);
        
		aSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mSensorManager.registerListener(this, aSensor, SensorManager.SENSOR_DELAY_GAME);
		
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_GAME);
	}
	
	/**
	 * onSensorChanged
	 * 
	 * @param SensorEvent
	 * 
	 * When there is a change detected from the sensor event, it calls this callback method
	 */
	public void onSensorChanged(SensorEvent event){
		switch( event.sensor.getType() ) {
        case Sensor.TYPE_GRAVITY:
          grav[0] = event.values[0];
          grav[1] = event.values[1];
          grav[2] = event.values[2];
          hasG = true;
          break;
        case Sensor.TYPE_ACCELEROMETER:
        	// grabs accel values
          accel[0] = (event.values[0] * kFilteringFactor + accel[0] * (1.0f - kFilteringFactor));
          accel[1] = (event.values[1] * kFilteringFactor + accel[1] * (1.0f - kFilteringFactor));
          accel[2] = (event.values[2] * kFilteringFactor + accel[2] * (1.0f - kFilteringFactor));
          hasA = true;
          break;
        case Sensor.TYPE_MAGNETIC_FIELD:
        	magnet[0] = event.values[0];
        	magnet[1] = event.values[1];
        	magnet[2] = event.values[2];
          hasM = true;
          break;
        default:
          return;
      }
		// depending on what sensors you have, this will calculate the row/pitch/yaw
		// also no longer using
		if (hasG && hasM) {
            SensorManager.getRotationMatrix(Rmat, Imat, grav, magnet);
            SensorManager.remapCoordinateSystem(Rmat,
                    SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X, R2);
            // Orientation isn't as useful as a rotation matrix, but
            // we'll show it here anyway.
            SensorManager.getOrientation(R2, orientation);
            pitch = orientation[1] * DEG;
            roll = orientation[2] * DEG;
    		yaw = orientation[0] * DEG;
		}
		else if(!hasG && hasA && hasM)
		{
			 SensorManager.getRotationMatrix(Rmat, Imat, accel, magnet);
	            SensorManager.remapCoordinateSystem(Rmat,
	                    SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X, R2);
	            // Orientation isn't as useful as a rotation matrix, but
	            // we'll show it here anyway.
	            SensorManager.getOrientation(R2, orientation);
	            pitch = orientation[1] * DEG;
	    		roll = orientation[2] * DEG;
	    		yaw = orientation[0] * DEG;
		}
		// sets x y and z 
		x = (accel[0] * kFilteringFactor + x * (1.0f - kFilteringFactor)); 
        y = (accel[1] * kFilteringFactor + y * (1.0f - kFilteringFactor));
        z = (accel[2] * kFilteringFactor + z * (1.0f - kFilteringFactor));
        
        // binds the x y within -1 and 1
        if(x >= 1.0f) 
			x = 1.0f;
		if(y >= 1.0f) 
			y = 1.0f;
		if(z >= 0.1f) 
			z = 0.1f;
		if(x <= -1.0f)
			x = -1.0f;
		if(y <= -1.0f)
			y = -1.0f;
		if(z <= 0.0f)
			z = 0.0f; 
	}
	/**
	 * OnAccuracyChanged
	 * 
	 */
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}
	/**
	 * OnTouchEvent
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getPointerCount() > 1) {
			scaleGestureDetector.onTouchEvent(event);
		} 
		return true;
	}
	
	public class MySimpleOnScaleGestureListener extends	SimpleOnScaleGestureListener {

		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			
			float scale = 1/detector.getScaleFactor();
			if(scale >1.0 && MyGLRenderer.scale > .01){
				MyGLRenderer.scale += ((1.0f-scale)/density);
			}
			if(scale<1.0  && MyGLRenderer.scale < 5){
				MyGLRenderer.scale += ((1.0f - scale)/density);
			}
			return true;
		}

		@Override
		public boolean onScaleBegin(ScaleGestureDetector detector) {
			return true;
		}

		@Override
		public void onScaleEnd(ScaleGestureDetector detector) {

		}
	}	
}
