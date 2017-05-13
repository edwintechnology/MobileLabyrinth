package com.edwintechnology.labyrinth;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.vecmath.Vector3f;

import android.annotation.SuppressLint;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;

public class MyGLRenderer implements GLSurfaceView.Renderer {
	
	final static float[] mModelMatrix = new float[16];
	final float[] mNormalMatrix = new float[16];
	final float[] mProjectionMatrix = new float[16];
	final float[] mViewMatrix = new float[16];
	final float[] mTransformationMatrix = new float[16];
	final float[] mRotationMatrix = new float[16];
	final float[] tempMatrix = new float[16];
	
	/*light1*/
	final float[] mLightModelMatrix1 = new float[16];
	final float[] mLightPosInWorldSpace1 = new float[4];
	final float[] mLightPosInEyeSpace1 = new float[4];
	float[] mLightPosInModelSpace1 = null;
	
	/*light2*/
	final float[] mLightModelMatrix2 = new float[16];
	final float[] mLightPosInWorldSpace2 = new float[4];
	final float[] mLightPosInEyeSpace2 = new float[4];
	float[] mLightPosInModelSpace2 = null;
	
	/*light3*/
	final float[] mLightModelMatrix3 = new float[16];
	final float[] mLightPosInWorldSpace3 = new float[4];
	final float[] mLightPosInEyeSpace3 = new float[4];
	float[] mLightPosInModelSpace3 = null;
	
	/*light4*/
	final float[] mLightModelMatrix4 = new float[16];
	final float[] mLightPosInWorldSpace4 = new float[4];
	final float[] mLightPosInEyeSpace4 = new float[4];
	float[] mLightPosInModelSpace4 = null;
	
	private float mAngle;
	public static float scale = 1.0f;
	public static float scaleb = 1.0f;
	public static float x = 1;
	public static float y = 1;
	public static float camX = 0.0f, camY = 0.0f, camZ = -3.0f;
    public static float lookX = 0.0f, lookY = 0.0f, lookZ = 0.0f;
    public static float upX = 0.0f, upY = 1.0f, upZ = 0.0f;
    public static float lightX = 1.75f, lightY = 1.75f, lightZ = 1.48f;
	public static float ball_x = 19.75f, ball_y = 8.58f, ball_z = 17.1f;
	public static float prev_ball_x, prev_ball_y, prev_ball_z;
	public static float minX = -1f, minY = -1f, minZ = -1f;
	public static float maxX = 1f, maxY = 1f, maxZ = 1f;
	
	private float speed = 2.0f;
    public boolean viewMode = true;
    private LabyrinthActivity LabyrinthActivity;
    
    static final int VERTEX_DATA_SIZE = 3;	
	static final int NORMAL_DATA_SIZE = 3;
	static final int TEXTURE_DATA_SIZE = 2;
	static final int BYTES_PER_FLOAT = 4;
	
	private Obj Board;
	//private Board MaskBoard;
	private Obj Ball;
	private Obj Cylinder1;
	private Obj Cylinder2;
	private Obj Cylinder3;
	private Obj Cylinder4;
	private Obj Cylinder5;
	
	private ConstraintSolver solver;
	private BroadphaseInterface broadphase;
	private DefaultCollisionConfiguration collisionConfiguration;
	private CollisionDispatcher dispatcher;
	static DynamicsWorld dynamicsWorld;
	
	float BlockZ = 0.1f;
	private Block b1;
	private Block b2;
	private Block b3;
	private Block b4;
	private Block b5;
	private Block b6;
	private Block b7;
	private Block b8;
	private Block b9;
	private Block b10;
	private Block b11;
	
	private Block circle1;
	private Block circle2;
	private Block circle3;
	private Block circle4;
	private Block circle5;
	
	public MyGLRenderer(LabyrinthActivity labyrinthActivity)
	{
		LabyrinthActivity = labyrinthActivity;
	}

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
    	
    	//MaskBoard = new Board(LabyrinthActivity, 5);
    	SetUpWorld();
    	// creates object
    	Cylinder1 = new Obj(LabyrinthActivity, 0);
    	Cylinder2 = new Obj(LabyrinthActivity, 1);
    	Cylinder3 = new Obj(LabyrinthActivity, 2);
    	Cylinder4 = new Obj(LabyrinthActivity, 3);
    	Cylinder5 = new Obj(LabyrinthActivity, 4);
    	
    	Board = new Obj(LabyrinthActivity,5);
    	Ball = new Obj(LabyrinthActivity, 6);
    	
    	minX = -26.07f;
    	maxX = 26.07f;
    	minY = -27.0f;
    	maxY = 27.0f;
    	minZ = -29.0f;
    	maxZ = 23.0f;
    	
        // Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
                
        // Enable depth test for HSR
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glEnable(GLES20.GL_TEXTURE_2D);
        GLES20.glEnable(GLES20.GL_TEXTURE0);
        GLES20.glEnable(GLES20.GL_TEXTURE1);
        GLES20.glEnable(GLES20.GL_TEXTURE2);
        
        mLightPosInModelSpace1 = new float[]{-lightX, -lightY, lightZ, 1.0f};
        mLightPosInModelSpace2 = new float[]{-lightX,  lightY, lightZ, 1.0f};
        mLightPosInModelSpace3 = new float[]{ lightX, -lightY, lightZ, 1.0f};
        mLightPosInModelSpace4 = new float[]{ lightX,  lightY, lightZ, 1.0f};
        
        // 00 [] 01 //
        // 10 [] 11 //
        
        // -- [] -+ //
        // +- [] ++ //

    	b1 = new Block(20.00f, 23.00f, BlockZ, 13.5f, 23.00f, BlockZ, 20.00f, 11.65f, BlockZ, 13.50f, 11.65f, BlockZ);   
    	b2 = new Block(20.00f, 16.18f, BlockZ, -20.29f, 16.18f, BlockZ, 20.00f, 11.26f, BlockZ, -20.29f, 11.26f, BlockZ);
    	b3 = new Block(-5.8f, 16.18f, BlockZ, -11.75f, 16.18f, BlockZ, -5.8f, -11.00f, BlockZ, -11.75f, -11.00f, BlockZ);
    	b4 = new Block(-5.8f, -4.0f, BlockZ, -21.0f, -4.0f, BlockZ, -5.8f, -11.19f, BlockZ, -21.0f, -11.19f, BlockZ);
    	b5 = new Block(-13.00f, -5.6f, BlockZ, -21.0f, -5.6f, BlockZ, -13.00f, -23.02f, BlockZ, -17.0f, -23.02f, BlockZ);
    	b6 = new Block(2.74f, -16.97f, BlockZ, -17.0f, -16.97f, BlockZ, 2.74f, -23.02f, BlockZ, -17.0f, -23.02f, BlockZ);
    	b7 = new Block(26.07f, 4.09f, BlockZ, 2.7f, 4.09f, BlockZ, 26.07f, -1.51f, BlockZ, 2.7f, -1.51f, BlockZ);
    	b8 = new Block(19.69f, -4.68f, BlockZ, 13.99f, -4.68f, BlockZ, 19.69f, -13.5f, BlockZ, 13.99f, -13.5f, BlockZ);
    	b9 = new Block(6.6f, -8.88f, BlockZ, -2.03f, -8.88f, BlockZ, 6.6f, -13.45f, BlockZ, -2.03f, -13.45f, BlockZ);
    	b10 = new Block(26.07f, -17.23f, BlockZ, 9.82f, -17.23f, BlockZ, 26.07f, -23.02f, BlockZ, 9.82f, -23.02f, BlockZ);
    	b11 = new Block(-20.29f, 4.11f, BlockZ, -26.07f, 4.11f, BlockZ, -20.29f, -2.53f, BlockZ, -26.07f, -2.53f, BlockZ);

    	// center
    	// 13.75f, 8.58f
    	circle1 = new Block(15.0f, 10.0f, BlockZ, 12.0f, 10.0f, BlockZ, 15.0f, 7.0f, BlockZ, 12.0f, 7.0f, BlockZ);
    	// 6.63f, -4.09f
    	circle2 = new Block(8.0f, -2.5f, BlockZ, 5.0f, -2.5f, BlockZ, 8.0f, -5.5f, BlockZ, 5.0f, -5.5f, BlockZ);
    	// 12.42f, -25.46f 
    	circle3 = new Block(14.0f, -23.0f, BlockZ, 11.0f, -23.0f, BlockZ, 14.0f, -27.0f, BlockZ, 11.0f, -27.0f, BlockZ);
    	// -19.7f, -27.55
    	circle4 = new Block(-17.5f, -25.5f, BlockZ, -21.0f, -25.5f, BlockZ, -17.5f, -29.0f, BlockZ, -21.0f, -29.0f, BlockZ);
    	// -23.3, 19.9
    	circle5 = new Block(-22.0f, 21f, BlockZ, -26.0f, 21f, BlockZ, -22.0f, 18.5f, BlockZ, -26.0f, 18.0f, BlockZ);
    	
    	prev_ball_x = 0; 
    	prev_ball_y = 0; 
    	prev_ball_z = 0;
    }

    private float getRatio(float width, float height)
    {
    	return (float) width/height;
    }
    
    @SuppressLint("NewApi")
	public void onDrawFrame(GL10 unused) {
    	GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
    	//dynamicsWorld.stepSimulation(1.f / 120.f, 1);
    	
    	/* view */
     	Matrix.setIdentityM(mViewMatrix, 0);
     	Matrix.setLookAtM(mViewMatrix, 0, camX, camY, camZ, lookX, lookY, lookZ, upX, upY, upZ);
    	
     	/* frustum Projection */
        final float ratio = getRatio(x, y);
		final float left = -ratio;
		final float right = ratio;
		final float bottom = -1.0f;
		final float top = 1.0f;
		final float near = 2.0f;
		final float far = 7.0f;
		
		Matrix.setIdentityM(mProjectionMatrix, 0);
		Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
    	
		/* attempt at light 1 */
        // Calculate position of the light. Push into the distance.
        Matrix.setIdentityM(mLightModelMatrix1, 0);
        Matrix.translateM(mLightModelMatrix1, 0, 0.0f, 0.0f, -1.0f);
        Matrix.multiplyMV(mLightPosInWorldSpace1, 0, mLightModelMatrix1, 0, mLightPosInModelSpace1, 0);
        Matrix.multiplyMV(mLightPosInEyeSpace1, 0, mViewMatrix, 0, mLightPosInWorldSpace1, 0);
        
        /* attempt at light 2 */
        // Calculate position of the light. Push into the distance.
        Matrix.setIdentityM(mLightModelMatrix2, 0); 
        Matrix.translateM(mLightModelMatrix2, 0, 0.0f, 0.0f, -1.0f);
        Matrix.multiplyMV(mLightPosInWorldSpace2, 0, mLightModelMatrix2, 0, mLightPosInModelSpace2, 0);
        Matrix.multiplyMV(mLightPosInEyeSpace2, 0, mViewMatrix, 0, mLightPosInWorldSpace2, 0);
        
        /* attempt at light 3 */
        // Calculate position of the light. Push into the distance.
        Matrix.setIdentityM(mLightModelMatrix3, 0); 
        Matrix.translateM(mLightModelMatrix3, 0, 0.0f, 0.0f, -1.0f);
        Matrix.multiplyMV(mLightPosInWorldSpace3, 0, mLightModelMatrix3, 0, mLightPosInModelSpace3, 0);
        Matrix.multiplyMV(mLightPosInEyeSpace3, 0, mViewMatrix, 0, mLightPosInWorldSpace3, 0);
        
        /* attempt at light 4 */
        // Calculate position of the light. Push into the distance.
        Matrix.setIdentityM(mLightModelMatrix4, 0); 
        Matrix.translateM(mLightModelMatrix4, 0, 0.0f, 0.0f, -1.0f);
        Matrix.multiplyMV(mLightPosInWorldSpace4, 0, mLightModelMatrix4, 0, mLightPosInModelSpace4, 0);
        Matrix.multiplyMV(mLightPosInEyeSpace4, 0, mViewMatrix, 0, mLightPosInWorldSpace4, 0);
        
        /* model */
    	Matrix.setIdentityM(tempMatrix, 0);
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.setIdentityM(mRotationMatrix, 0);
        Matrix.scaleM(mModelMatrix, 0, (1/(scale*9)), (1/(scale*9)), (1/(scale*7)));
        Matrix.rotateM(mModelMatrix, 0, -90, 1.0f, 0.0f, 0.0f);
       // Matrix.rotateM(mRotationMatrix, 0, mAngle, 1.0f, 0.0f, 0.0f);
        //Matrix.rotateM(mRotationMatrix, 0, (-1 * LabyrinthActivity.Roll()) % 360, 1.0f, 0.0f, 0.0f);
       // Matrix.rotateM(mRotationMatrix, 0, (-1 * LabyrinthActivity.Pitch()) % 360, 0.0f, 0.0f, 1.0f);
        
        Matrix.multiplyMM(tempMatrix, 0, mModelMatrix, 0, mRotationMatrix, 0);
        System.arraycopy(tempMatrix, 0, mModelMatrix, 0, tempMatrix.length);
        
        Matrix.setIdentityM(mNormalMatrix, 0);
        Matrix.setIdentityM(tempMatrix, 0);
        Matrix.invertM(mNormalMatrix, 0, mModelMatrix, 0);
        Matrix.transposeM(tempMatrix, 0, mNormalMatrix, 0);
        System.arraycopy(tempMatrix, 0, mNormalMatrix, 0, tempMatrix.length);
       
        
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, Board.getTextureHandle());
   		GLES20.glUniform1i(Board.getTextureUniformHandle(), 0);
   	    Board.draw(mModelMatrix, mViewMatrix, mProjectionMatrix, mNormalMatrix, mLightPosInEyeSpace1, mLightPosInEyeSpace2, mLightPosInEyeSpace3, mLightPosInEyeSpace4);
    	GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        
    	// cylinder 1
    	GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, Cylinder1.getTextureHandle());
    	GLES20.glUniform1i(Cylinder1.getTextureUniformHandle(), 0);
    	Cylinder1.draw(mModelMatrix, mViewMatrix, mProjectionMatrix, mNormalMatrix, mLightPosInEyeSpace1, mLightPosInEyeSpace2, mLightPosInEyeSpace3, mLightPosInEyeSpace4);
	    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
	      	    
	    // cylinder 2
	    GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, Cylinder2.getTextureHandle());
    	GLES20.glUniform1i(Cylinder2.getTextureUniformHandle(), 0);
    	Cylinder2.draw(mModelMatrix, mViewMatrix, mProjectionMatrix, mNormalMatrix, mLightPosInEyeSpace1, mLightPosInEyeSpace2, mLightPosInEyeSpace3, mLightPosInEyeSpace4);
	    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
	    
	    GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, Cylinder3.getTextureHandle());
    	GLES20.glUniform1i(Cylinder3.getTextureUniformHandle(), 0);
    	Cylinder3.draw(mModelMatrix, mViewMatrix, mProjectionMatrix, mNormalMatrix, mLightPosInEyeSpace1, mLightPosInEyeSpace2, mLightPosInEyeSpace3, mLightPosInEyeSpace4);
	    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
	    
	    GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, Cylinder4.getTextureHandle());
    	GLES20.glUniform1i(Cylinder4.getTextureUniformHandle(), 0);
    	Cylinder4.draw(mModelMatrix, mViewMatrix, mProjectionMatrix, mNormalMatrix, mLightPosInEyeSpace1, mLightPosInEyeSpace2, mLightPosInEyeSpace3, mLightPosInEyeSpace4);
	    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
	    
	    GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, Cylinder5.getTextureHandle());
    	GLES20.glUniform1i(Cylinder5.getTextureUniformHandle(), 0);
    	Cylinder5.draw(mModelMatrix, mViewMatrix, mProjectionMatrix, mNormalMatrix, mLightPosInEyeSpace1, mLightPosInEyeSpace2, mLightPosInEyeSpace3, mLightPosInEyeSpace4);
	    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
	    
    	/* view */
     	Matrix.setIdentityM(mViewMatrix, 0);
     	Matrix.setLookAtM(mViewMatrix, 0, camX, camY, camZ, lookX, lookY, lookZ, upX, upY, upZ);
    	
     	/* frustum Projection */
		Matrix.setIdentityM(mProjectionMatrix, 0);
		Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
    	
        Matrix.setIdentityM(tempMatrix, 0);
		Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.setIdentityM(mTransformationMatrix, 0);

        ball_x += (float)(LabyrinthActivity.X()*(5/2)) * speed/15f;
        ball_y += (float)(LabyrinthActivity.Y()*-(5/2)) * speed/15f;
        //ball_z += (float)(LabyrinthActivity.Z() / -15);
        
        if(ball_x <= minX)
        {
        	ball_x = minX;
        }
        if(ball_y <= minZ)
        {
        	ball_y = minZ;
        }
        if(ball_z <= minZ)
        {
        	ball_z = minZ;
        }
        if(ball_x >= maxX)
        {
        	ball_x = maxX;
        }
        if(ball_y >= maxZ)
        {
        	ball_y = maxZ;
        }
        if(ball_z >= maxZ)
        {
        	ball_z = maxZ;
        }
        
        // tl 22.0 19.95
        // bl 22.0 -25.15
        // tr -22.8 19.95
        // br -22.8 -25.15
        
        // helpers for finding direction in respect to a block
        float ballToEdgeDistanceForL;
        float ballToEdgeDistanceForR;
        
        // checks against block1
        if(ball_x < b1.TL.x && ball_y > b2.BL.y && ball_x > b1.TR.x && LabyrinthActivity.X() < 0)
        {
        	ball_x = b1.TL.x;
        }
        else if(ball_x < b1.TL.x && ball_x > b1.TR.x && ball_y > b2.TR.y)
        {
        	ball_x = b1.TR.x;
        }
        
        // checks against block 2
        ballToEdgeDistanceForL = Math.abs(ball_y - b2.TL.y);
        ballToEdgeDistanceForR = Math.abs(ball_y - b2.BL.y);
        if(ball_y > b2.BL.y && ball_y < b2.TL.y && ball_x > b2.TR.x && ball_x < b2.TL.x && ballToEdgeDistanceForL > ballToEdgeDistanceForR)
        {
        	ball_y = b2.BL.y;
        }
        if(ball_y < b2.TL.y && ball_x < b2.TL.x && ball_x > b2.TR.x && ball_y > b2.BL.y && ballToEdgeDistanceForL < ballToEdgeDistanceForR)
        {
        	if(ball_x > b1.TR.x)
        		ball_x = b1.TR.x;
        	ball_y = b2.TL.y;
        }
        
        // block 3
        ballToEdgeDistanceForL = Math.abs(ball_x - b3.TL.x);
        ballToEdgeDistanceForR = Math.abs(ball_x - b3.TR.x);
        
        if(ball_x < b3.TL.x && ball_y > b3.BL.y && ball_x > b3.TR.x && LabyrinthActivity.X() < 0)
        {
        	if(ball_y >= b2.BL.y)
        		ball_y = b2.BL.y;
        	ball_x = b3.TL.x;
        }    
        if(ball_x > b3.TR.x && ball_x < b3.TL.x && ball_y > b3.BR.y && ball_y < b3.TR.y && LabyrinthActivity.X() > 0)
        {
        		ball_x = b3.TR.x;
        }
        	
        // block 4
        ballToEdgeDistanceForL = Math.abs(ball_y - b4.TL.y);
        ballToEdgeDistanceForR = Math.abs(ball_y - b4.BL.y);

        if(ball_y < b4.TL.y && ball_x < b4.TL.x && ball_x > b4.TR.x && ball_y > b4.BL.y && ballToEdgeDistanceForL < ballToEdgeDistanceForR)
        {
        	ball_y = b4.TL.y;
        }
        if(ball_y > b4.BL.y && ball_y < b4.TL.y && ball_x > b4.TR.x && ball_x < b4.TL.x && ballToEdgeDistanceForL > ballToEdgeDistanceForR)
        {
        	if(ball_x < b5.TL.x)
        		ball_x = b5.TL.x;
        	ball_y = b4.BL.y;
        }
        
        // block 5
        ballToEdgeDistanceForL = Math.abs(ball_x - b5.TL.x);
        ballToEdgeDistanceForR = Math.abs(ball_x - b5.TR.x);
        if(ball_x < b5.TL.x && ball_x > b5.TR.x && ball_y < b4.BL.y && ball_y > b6.TL.y && ballToEdgeDistanceForL < ballToEdgeDistanceForR)
        {
        	ball_x = b5.TL.x;
        }
        
        	if(ball_x > b5.TR.x && ball_x < b5.TL.x && ball_y < b4.BL.y && ball_y > b6.TL.y && ballToEdgeDistanceForR < ballToEdgeDistanceForL)
        	{
        		ball_x = b5.TR.x;
        	}
        	
        // Block 6
        ballToEdgeDistanceForL = Math.abs(ball_y - b6.TL.y);
        ballToEdgeDistanceForR = Math.abs(ball_y - b6.BL.y);
        if(ball_y < b6.TL.y && ball_y > b6.BL.y && ball_x < b6.TL.x && ball_x > b6.TR.x && ballToEdgeDistanceForL < ballToEdgeDistanceForR)
        {
        	if(ball_x < b5.TL.x)
        		ball_x = b5.TL.x;
        	ball_y = b6.TL.y;
        }
        if(ball_y > b6.BL.y && ball_y < b6.TL.y && ball_x < b6.TL.x && ball_x > b6.TR.x && ballToEdgeDistanceForL > ballToEdgeDistanceForR)
        {
        	ball_y = b6.BL.y;
        }
        
        //border 7
        ballToEdgeDistanceForL = Math.abs(ball_y - b7.TL.y);
        ballToEdgeDistanceForR = Math.abs(ball_y - b7.BL.y);
        if(ball_y < b7.TL.y && ball_y > b7.BL.y && ball_x > b7.TR.y && ballToEdgeDistanceForL < ballToEdgeDistanceForR)
        {
        	ball_y = b7.TL.y;
        }
        if(ball_y > b7.BL.y && ball_y < b7.TL.y && ball_x > b7.TR.y && ballToEdgeDistanceForL > ballToEdgeDistanceForR)
        {
        	ball_y = b7.BL.y;
        }
        
        //Block 8
        ballToEdgeDistanceForL = Math.abs(ball_y - b8.TL.y);
        ballToEdgeDistanceForR = Math.abs(ball_y - b8.BL.y);
        if(ball_y < b8.TL.y && ball_y > b8.BL.y && ball_x > b8.TR.x && ball_x < b8.TL.x && ballToEdgeDistanceForL < ballToEdgeDistanceForR)
        {
        	ball_y = b8.TL.y;
        }
        if(ball_y > b8.BL.y && ball_y < b8.TL.y && ball_x > b8.TR.x && ball_x < b8.TL.x && ballToEdgeDistanceForL > ballToEdgeDistanceForR){
        	ball_y = b8.BL.y;
        }
        ballToEdgeDistanceForL = Math.abs(ball_x - b8.TL.x);
        ballToEdgeDistanceForR = Math.abs(ball_x - b8.TR.x);
        if(ball_x < b8.TL.x && ball_x > b8.TR.x && ball_y <= b8.TL.y && ball_y >= b8.BL.y && ballToEdgeDistanceForL < ballToEdgeDistanceForR)
        {
        	ball_x = b8.TL.x;
        }
        if(ball_x > b8.TR.x && ball_x < b8.TL.x && ball_y <= b8.TL.y && ball_y >= b8.BL.y && ballToEdgeDistanceForL > ballToEdgeDistanceForR)
        {
        	ball_x = b8.TR.x;
        }
        
        // block 9
        ballToEdgeDistanceForL = Math.abs(ball_y - b9.TL.y);
        ballToEdgeDistanceForR = Math.abs(ball_y - b9.BL.y);
        if(ball_y < b9.TL.y && ball_y > b9.BL.y && ball_x < b9.TL.x && ball_x > b9.TR.x && ballToEdgeDistanceForL < ballToEdgeDistanceForR)
        {
        	ball_y = b9.TL.y;
        }
        if(ball_y > b9.BL.y && ball_y < b9.TL.y && ball_x < b9.TL.x && ball_x > b9.TR.x && ballToEdgeDistanceForL > ballToEdgeDistanceForR)
        {
        	ball_y = b9.BL.y;
        }
        ballToEdgeDistanceForL = Math.abs(ball_x - b9.TL.x);
        ballToEdgeDistanceForR = Math.abs(ball_x - b9.TR.x);
        /*if(ball_x < b9.TL.x && ball_x > b9.TR.x && ball_y <= b9.TL.y && ball_y >= b9.BL.y && ballToEdgeDistanceForL < ballToEdgeDistanceForR)
        {
        	ball_x = b9.TL.x;
        }
        if(ball_x > b9.TR.x && ball_x < b9.TL.x && ball_y <= b9.TL.y && ball_y >= b9.BL.y && ballToEdgeDistanceForL > ballToEdgeDistanceForR)
        {
        	ball_x = b9.TR.x;
        }*/
        
        // block 10
        ballToEdgeDistanceForL = Math.abs(ball_y - b10.TL.y);
        ballToEdgeDistanceForR = Math.abs(ball_y - b10.BL.y);
        if(ball_y < b10.TL.y && ball_y > b10.BL.y && ball_x > b10.TR.x && ballToEdgeDistanceForL < ballToEdgeDistanceForR)
        {
        	ball_y = b10.TL.y;
        }
        if(ball_y > b10.BL.y && ball_y < b10.TL.y && ball_x > b10.TR.x && ballToEdgeDistanceForL > ballToEdgeDistanceForR)
        {
        	ball_y = b10.BL.y;
        }
        
        // block 11
        ballToEdgeDistanceForL = Math.abs(ball_y - b11.TL.y);
        ballToEdgeDistanceForR = Math.abs(ball_y - b11.BL.y);
        if(ball_y < b11.TL.y && ball_y > b11.BL.y  && ball_x < b11.TL.x && ballToEdgeDistanceForL < ballToEdgeDistanceForR)
        {
        	ball_y = b11.TL.y;
        }
        if(ball_y > b11.BL.y && ball_y < b11.TL.y && ball_x < b11.TL.x && ballToEdgeDistanceForL > ballToEdgeDistanceForR)
        {
        	ball_y = b11.BL.y;
        }
        
        // circle 1
        if(ball_x < circle1.TL.x && ball_x > circle1.TR.x && ball_y < circle1.TL.y && ball_y > circle1.BL.y)
        {
        	ball_z = 200.0f ;
        }
     // circle 2
        if(ball_x < circle2.TL.x && ball_x > circle2.TR.x && ball_y < circle2.TL.y && ball_y > circle2.BL.y)
        {
        	ball_z = 200.0f ;
        }
     // circle 3
        if(ball_x < circle3.TL.x && ball_x > circle3.TR.x && ball_y < circle3.TL.y && ball_y > circle3.BL.y)
        {
        	ball_z = 200.0f ;
        }
     // circle 4
        if(ball_x < circle4.TL.x && ball_x > circle4.TR.x && ball_y < circle4.TL.y && ball_y > circle4.BL.y)
        {
        	ball_z = 200.0f ;
        }
     // circle 5
        if(ball_x < circle5.TL.x && ball_x > circle5.TR.x && ball_y < circle5.TL.y && ball_y > circle5.BL.y)
        {
        	ball_z = 200.0f ;
        }
        
        Matrix.setIdentityM(mRotationMatrix, 0);
        
        //Matrix.translateM(mTransformationMatrix, 0, -prev_ball_x, -prev_ball_y, -prev_ball_z);
        //Matrix.rotateM(mRotationMatrix, 0, (30 * ball_x) % 360, 0.0f, 1.0f, 0.0f);
        //Matrix.translateM(mTransformationMatrix, 0, prev_ball_x, prev_ball_y, prev_ball_z);
        Matrix.scaleM(mModelMatrix, 0, (1/(scale*35)), (1/(scale*35)), (1/(scale*35)));
        Matrix.translateM(mTransformationMatrix, 0, 0.0f, 0.0f, -10.0f);
        Matrix.translateM(mTransformationMatrix, 0, ball_x, ball_y, ball_z);
        Matrix.multiplyMM(tempMatrix, 0, mModelMatrix, 0, mTransformationMatrix, 0);
        System.arraycopy(tempMatrix, 0, mModelMatrix, 0, tempMatrix.length);
        
        Matrix.multiplyMM(tempMatrix, 0, mModelMatrix, 0, mRotationMatrix, 0);
        System.arraycopy(tempMatrix, 0, mModelMatrix, 0, tempMatrix.length);
        
        Matrix.setIdentityM(mNormalMatrix, 0);
        Matrix.setIdentityM(tempMatrix, 0);
        Matrix.invertM(mNormalMatrix, 0, mModelMatrix, 0);
        Matrix.transposeM(tempMatrix, 0, mNormalMatrix, 0);
        System.arraycopy(tempMatrix, 0, mNormalMatrix, 0, tempMatrix.length);
        
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, Ball.getTextureHandle());
        
   		GLES20.glUniform1i(Board.getTextureUniformHandle(), 0);
	    Ball.draw(mModelMatrix, mViewMatrix, mProjectionMatrix, mNormalMatrix, mLightPosInEyeSpace1, mLightPosInEyeSpace2, mLightPosInEyeSpace3, mLightPosInEyeSpace4);
	    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
	    
	    prev_ball_x = ball_x; 
	    prev_ball_y = ball_y; 
	    prev_ball_z = ball_z;
    }
    
    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
    	
		GLES20.glViewport(0, 0, width, height);
		final float ratio = (float) width / height;
		final float left = -ratio;
		final float right = ratio;
		final float bottom = -1.0f;
		final float top = 1.0f;
		final float near = 2.0f;
		final float far = 7.0f;

		Matrix.setIdentityM(mProjectionMatrix, 0);
		Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
    }

    public static int loadShader(int type, String shaderCode){
        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }
    /**
     * Returns the rotation angle of the triangle shape (mTriangle).
     *
     * @return - A float representing the rotation angle.
     */
    public float getAngle() {
        return mAngle;
    }

    /**
     * Sets the rotation angle of the triangle shape (mTriangle).
     */
    public void setAngle(float angle) {
        mAngle = angle;
    }
    
    public void SetUpWorld()
    {
    	collisionConfiguration = new DefaultCollisionConfiguration();
		dispatcher = new CollisionDispatcher(collisionConfiguration);
		//Vector3f worldMin = new Vector3f(-1000f, -1000f, -1000f);
		//Vector3f worldMax = new Vector3f(1000f, 1000f, 1000f);
		broadphase = new DbvtBroadphase();
		solver = new SequentialImpulseConstraintSolver();
		
		dynamicsWorld = new DiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfiguration);
		 
		dynamicsWorld.setGravity(new Vector3f(0.0f,0.0f, 10.0f));
		dynamicsWorld.getDispatchInfo().allowedCcdPenetration = 0f;
		
		
		
		
		//GImpactMeshShape trimesh = new GImpactMeshShape(MaskBoard.getShape());
		//trimesh.setLocalScaling(new Vector3f(4f, 4f, 4f));
		//trimesh.updateBound();
		CollisionShape trimeshShape = new BoxShape(new Vector3f(0.0f,0.0f,0.0f));
		// register algorithm
		//GImpactCollisionAlgorithm.registerAlgorithm(dispatcher);
		
		Transform groundTransform = new Transform();
		groundTransform.setIdentity();
		groundTransform.origin.set(new Vector3f(0.f, -35.0f, 10.f));
		float mass = 0f;
		
		boolean isDynamic = (mass != 0f);

		Vector3f localInertia = new Vector3f(0, 0, 0);
		if (isDynamic) {
			trimeshShape.calculateLocalInertia(mass, localInertia);
		}

		DefaultMotionState myMotionState = new DefaultMotionState(groundTransform);
		RigidBodyConstructionInfo rbInfo = new RigidBodyConstructionInfo(
				mass, myMotionState, trimeshShape, localInertia);
		RigidBody body = new RigidBody(rbInfo);

		// add the body to the world
		dynamicsWorld.addRigidBody(body);
    
    }
}
//		   ___________________________________
//        | ________________________________ |
//        | |  |b|_______________________ O| |
// 	      | |  |1|b2____________________|  | |
//		  | |

