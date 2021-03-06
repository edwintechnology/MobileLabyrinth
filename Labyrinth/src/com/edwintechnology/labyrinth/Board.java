package com.edwintechnology.labyrinth;

import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;

import FileIO.Model;
import FileIO.ObjLoader;
import FileIO.ResourceReader;
import FileIO.Textures;
import android.content.res.AssetManager;
import android.opengl.GLES20;

import com.bulletphysics.collision.shapes.TriangleIndexVertexArray;

public class Board {
    private FloatBuffer vBuffer;
    private FloatBuffer vnBuffer;
    private FloatBuffer vtBuffer;
    private int buf = 0;
    private final int SIZE_OF_FLOAT = 4;
    
    private int mVertexBufferIndex;
    private int mNormalBufferIndex;
    private int mTextureBufferIndex;
    
    private LabyrinthActivity labyrinth;
    
	private int mProgram;
	private int objTextureHandle;
	private int mModelMatrixHandle;
	private int mProjectionMatrixHandle;
	private int mViewMatrixHandle;
	private int mVertexHandle;
	private int mNormalHandle;
	private int mTextureHandle;
	private int mLightPosHandle1;
	private int mLightPosHandle2;
	private int mLightPosHandle3;
	private int mLightPosHandle4;
	private int mTextureUniformHandle;
    private int mNormalMatrixHandle;
    private int mKaHandle;
    private int mKdHandle;
    private int mKsHandle;
    private int mShineHandle;
    
	float initialOffset = 0;
	boolean ableBody = false;
	

    public int getNormalHandle()
    {
    	return mNormalMatrixHandle;
    }
	public int getTextureHandle()
	{
		return objTextureHandle;
	}
	public int getModelHandle()
	{
		return mModelMatrixHandle;
	}
	public int getViewHandle()
	{
		return mViewMatrixHandle;
	}
	public int getProjectionHandle()
	{
		return mProjectionMatrixHandle;
	}
	public int getLightHandle1()
	{
		return mLightPosHandle1;
	}
	public int getLightHandle2()
	{
		return mLightPosHandle2;
	}
	public int getLightHandle3()
	{
		return mLightPosHandle3;
	}
	public int getLightHandle4()
	{
		return mLightPosHandle4;
	}
	public int getTextureUniformHandle()
	{
		return mTextureUniformHandle;
	}
	 
    public Board(LabyrinthActivity la, int modelIndex) {
    	
    	labyrinth = la;
    	
    	Model model = la.Models.get(modelIndex);
        vBuffer = model.getV();
    	vtBuffer = model.getVT();
    	vnBuffer = model.getVN();
    	buf = model.getSize();
    	
        final int buffers[] = new int[3];
        GLES20.glGenBuffers(3, buffers, 0);
        
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[0]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vBuffer.capacity() * SIZE_OF_FLOAT, vBuffer, GLES20.GL_STATIC_DRAW);
        
        if(vnBuffer != null){
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[1]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vnBuffer.capacity() * SIZE_OF_FLOAT, vnBuffer, GLES20.GL_STATIC_DRAW);
        }
        if(vtBuffer != null){
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[2]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vtBuffer.capacity() * SIZE_OF_FLOAT, vtBuffer, GLES20.GL_STATIC_DRAW);
        }
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        
        mVertexBufferIndex = buffers[0];
        mNormalBufferIndex = buffers[1];
        mTextureBufferIndex = buffers[2];

        vBuffer.limit(0);
        vBuffer = null;
        if(vnBuffer != null){
        	vnBuffer.limit(0);
        	vnBuffer = null;
        }
    	if(vtBuffer != null){
    		vtBuffer.limit(0);
    		vtBuffer = null;
    	}
    	// prepare shaders and OpenGL program
        int vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, ResourceReader.readText(labyrinth, R.raw.vertex_shader));
        int fragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, ResourceReader.readText(labyrinth, R.raw.fragment_shader));
        
        mProgram = GLES20.glCreateProgram();             // create empty OpenGL Program
        GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(mProgram);                  // create OpenGL program
        
        // since I only have 2 
        switch(modelIndex)
        {
        case 0:
        case 1:
        case 2:
        case 3:
        case 4:
        	objTextureHandle = Textures.load(labyrinth, R.raw.black);
        	break;
        case 5:
        	objTextureHandle = Textures.load(labyrinth, R.raw.wood);
        	break;
        case 6:
        	objTextureHandle = Textures.load(labyrinth, R.raw.chrome);
        	ableBody = true;
        	default:
        		objTextureHandle = Textures.load(labyrinth, R.raw.black);
        }
			GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);			

			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, objTextureHandle);		
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);		

			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, objTextureHandle);		
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
			
			ObjLoader objloader = new ObjLoader(la);
			AssetManager assetMgr = la.getAssets();
	        InputStream in = null;
	        			try {
				in = assetMgr.open("holeboard.txt");
				//objloader.loadFromStreamIndexVertexOnly(in);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    }
    public void draw(float[] M, float[] V, float[] P, float[] N, float[] L1, float[] L2, float[] L3, float[] L4) {
    	GLES20.glUseProgram(mProgram);
    	
    	// Set program handles for cube drawing.
    	mNormalMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uNormalMatrix");
		mModelMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uModelMatrix");
		mProjectionMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uProjectionMatrix");
		mViewMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uViewMatrix");
        mLightPosHandle1 = GLES20.glGetUniformLocation(mProgram, "u_LightPos1");
        mLightPosHandle2 = GLES20.glGetUniformLocation(mProgram, "u_LightPos2");
        mLightPosHandle3 = GLES20.glGetUniformLocation(mProgram, "u_LightPos3");
        mLightPosHandle4 = GLES20.glGetUniformLocation(mProgram, "u_LightPos4");
        mTextureUniformHandle = GLES20.glGetUniformLocation(mProgram, "u_Texture");
        mVertexHandle = GLES20.glGetAttribLocation(mProgram, "a_vertex");        
        mNormalHandle = GLES20.glGetAttribLocation(mProgram, "a_normal"); 
        mTextureHandle = GLES20.glGetAttribLocation(mProgram, "a_texture");
        mKaHandle = GLES20.glGetAttribLocation(mProgram, "a");        
        mKdHandle = GLES20.glGetAttribLocation(mProgram, "d"); 
        mKsHandle = GLES20.glGetAttribLocation(mProgram, "s");
        mShineHandle= GLES20.glGetAttribLocation(mProgram, "shine");
        
        GLES20.glUniform3f(getLightHandle1(), L1[0], L1[1], L1[2]);
        GLES20.glUniform3f(getLightHandle2(), L2[0], L2[1], L2[2]);
        GLES20.glUniform3f(getLightHandle3(), L3[0], L3[1], L3[2]);
        GLES20.glUniform3f(getLightHandle4(), L4[0], L4[1], L4[2]);
        
        GLES20.glUniformMatrix4fv(getModelHandle(), 1, false, M, 0);
        GLES20.glUniformMatrix4fv(getViewHandle(), 1, false, V, 0);
        GLES20.glUniformMatrix4fv(getProjectionHandle(), 1, false, P, 0);
        GLES20.glUniformMatrix4fv(getNormalHandle(), 1, false, N, 0);
        
		// Pass in vertex information
    	GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mVertexBufferIndex);
        GLES20.glEnableVertexAttribArray(mVertexHandle);
		GLES20.glVertexAttribPointer(mVertexHandle, MyGLRenderer.VERTEX_DATA_SIZE, GLES20.GL_FLOAT, false, 0, 0);

		// Pass in the normal information
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mNormalBufferIndex);
		GLES20.glEnableVertexAttribArray(mNormalHandle);
		GLES20.glVertexAttribPointer(mNormalHandle, MyGLRenderer.NORMAL_DATA_SIZE, GLES20.GL_FLOAT, false, 0, 0);

		//if(vtBuffer != null){
		// Pass in the texture information
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mTextureBufferIndex);
		GLES20.glEnableVertexAttribArray(mTextureHandle);
		GLES20.glVertexAttribPointer(mTextureHandle, MyGLRenderer.TEXTURE_DATA_SIZE, GLES20.GL_FLOAT, false, 0, 0);
		//}
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
		
        // Draw the face
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, buf*(SIZE_OF_FLOAT-1)*3);
        

        GLES20.glDisableVertexAttribArray(mVertexHandle);
        GLES20.glDisableVertexAttribArray(mNormalHandle);
        GLES20.glDisableVertexAttribArray(mTextureHandle);
    }
}
