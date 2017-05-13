package com.edwintechnology.labyrinth;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class MyGLSurfaceView extends GLSurfaceView {
		private MyGLRenderer mRenderer;
		
	    public MyGLSurfaceView(Context context){
	        super(context);
	        setEGLContextClientVersion(2);
	        // Set the Renderer for drawing on the GLSurfaceView
	        mRenderer = new MyGLRenderer((LabyrinthActivity)context);
	        setRenderer(mRenderer);
	      //  setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
	    }
	    public MyGLSurfaceView(Context context, AttributeSet attrs) {
	        super(context, attrs);

	        // Create an OpenGL ES 2.0 context.
	        setEGLContextClientVersion(2);
	        mRenderer = new MyGLRenderer((LabyrinthActivity)context);
	        // Set the Renderer for drawing on the GLSurfaceView
	        setRenderer(mRenderer);

	        // Render the view only when there is a change in the drawing data
	        //setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
	    }
	   
	    
	    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
	    private float mPreviousX;
	    private float mPreviousY;
	    
	    @Override
	    public boolean onTouchEvent(MotionEvent e) {
	        float x = e.getX();
	        float y = e.getY();

	        switch (e.getAction()) {
	            case MotionEvent.ACTION_MOVE:

	                float dx = x - mPreviousX;
	                float dy = y - mPreviousY;

	                // reverse direction of rotation above the mid-line
	                if (y > getHeight() / 2) {
	                    dx = dx * -1 ;
	                }

	                // reverse direction of rotation to left of the mid-line
	                if (x < getWidth() / 2) {
	                    dy = dy * -1 ;
	                }

	                mRenderer.setAngle(
	                        mRenderer.getAngle() +
	                        ((dx + dy) * TOUCH_SCALE_FACTOR));  // = 180.0f / 320
	                requestRender();
	        }

	        mPreviousX = x;
	        mPreviousY = y;
	        return true; 
	    }
	    
	    public MyGLRenderer getRenderer()
	    {
	    	return mRenderer;
	    }
	}
