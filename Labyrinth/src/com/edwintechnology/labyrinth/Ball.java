package com.edwintechnology.labyrinth;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;

public class Ball extends Obj{
	RigidBody body;
	float initialOffset = 0;
	boolean ableBody = false;
	
    public Ball(LabyrinthActivity la, int modelIndex) {
    	super(la, modelIndex);
		initialOffset = (float) Math.random() * 2 - 1;
		createCollision();	
    }
    public void createCollision(){
		CollisionShape cubeShape = new BoxShape(new Vector3f(.5f, .5f, .5f));
		Transform cubeTransform = new Transform();
		cubeTransform.setIdentity();
		cubeTransform.origin.set(new Vector3f(initialOffset,3,-7));
		float cubeMass = 3f;
		
		Vector3f localInertia = new Vector3f(0, 0, 0);
		cubeShape.calculateLocalInertia(cubeMass, localInertia);
		
		DefaultMotionState cubeMotionState = new DefaultMotionState(cubeTransform);
		RigidBodyConstructionInfo cubeRBInfo = new RigidBodyConstructionInfo(
				cubeMass, cubeMotionState, cubeShape, localInertia);
		RigidBody cubeBody = new RigidBody(cubeRBInfo);
		MyGLRenderer.dynamicsWorld.addRigidBody(cubeBody);
		body = cubeBody;
    }
}