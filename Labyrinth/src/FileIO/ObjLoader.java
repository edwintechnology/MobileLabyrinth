package FileIO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.StringTokenizer;

import android.content.res.AssetManager;

import com.edwintechnology.labyrinth.LabyrinthActivity;

public class ObjLoader {

	private ArrayList<Vertex> v;
	private ArrayList<Vertex> vt;
	private ArrayList<Vertex> vn;
	private ArrayList<Face> f;
	private ArrayList<Model> o;
	
	private float minX =  10000000, minY =  10000000, minZ =  10000000; 
	private float maxX = -10000000, maxY = -10000000, maxZ = -10000000;

	private ArrayList<Integer> inds= new ArrayList<Integer>();
	private ArrayList<Float> verts= new ArrayList<Float>();
	
	public int getIndSize()
	{
		return inds.size();
	}
	public int getVertSize()
	{
		return verts.size();
	}
	public int[] getIndices()
	{
		int i = 0;
		int[] something = new int[inds.size()];
		for(int f : inds)
		{
			something[i++] = f;
		}
		return something;
	}
	public float[] getVertices()
	{
		int i = 0;
		float[] something = new float[verts.size()];
		for(float f : verts)
		{
			something[i++] = f;
		}
		return something;
	}
	public ByteBuffer getVertexBuffer() {
		ByteBuffer buf = ByteBuffer.allocateDirect(getVertices().length*4).order(ByteOrder.nativeOrder());
		for (int i=0; i<getVertices().length; i++) {
			buf.putFloat(getVertices()[i]);
		}
		buf.flip();
		return buf;
	}

	public ByteBuffer getIndexBuffer() {
		ByteBuffer buf = ByteBuffer.allocateDirect(getIndices().length*4).order(ByteOrder.nativeOrder());
		for (int i=0; i<getIndices().length; i++) {
			buf.putInt(getIndices()[i]);
		}
		buf.flip();
		return buf;
	}
	
	private Material mtl;
	
	private LabyrinthActivity LA;
	
	public ObjLoader(LabyrinthActivity LA)
	{
		v = new ArrayList<Vertex>();
		vt = new ArrayList<Vertex>();
		vn = new ArrayList<Vertex>();
		f = new ArrayList<Face>();
		o = new ArrayList<Model>();
		this.LA = LA;
	}
	
	public ArrayList<Model> getO()
	{
		return o;
	}
	
	private void clear()
	{
		v = null;
		v = new ArrayList<Vertex>();
		vt = null;
		vt = new ArrayList<Vertex>();
		vn = null;
		vn = new ArrayList<Vertex>();
		f = null;
		f = new ArrayList<Face>();
		
	}
	public void loadFromStream(InputStream in) throws IOException
	{
		clear();
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		
		boolean pending = false;
		
		while(reader.ready())
		{
			String line = reader.readLine();
			if(line == null) break;
			if(line.equals("")) continue;
			if(line.equals(" ")) continue;
			StringTokenizer st = new StringTokenizer(line);
			String command = st.nextToken();
			
			if(command.equals("#")) continue;
			
			if(command.equals("mtllib")){
				mtl = new Material();
				
				AssetManager assetMgr = LA.getAssets();
				InputStream mtlin = assetMgr.open(st.nextToken());
				
				BufferedReader mtlreader = new BufferedReader(new InputStreamReader(mtlin));
				
				while(mtlreader.ready())
				{
					String mtlline = mtlreader.readLine();
					if(mtlline == null) break;
					if(mtlline.equals("")) continue;
					if(mtlline.equals(" ")) continue;
					StringTokenizer mtlst = new StringTokenizer(mtlline);
					
					String mtlCmd = mtlst.nextToken();
					
					if(mtlCmd.equals("#")) continue;
					else if(mtlCmd.equals("newmtl")){
						mtl.setName(mtlst.nextToken());
					}
					else if(mtlCmd.equals("Ns")){
						mtl.setSpecularCoefficient(Float.parseFloat(mtlst.nextToken()));
					}
					else if(mtlCmd.equals("Ka")){
						mtl.setAmbient(read_light(mtlst));
					}
					else if(mtlCmd.equals("Kd")){
						mtl.setDiffuse(read_light(mtlst));			
					}
					else if(mtlCmd.equals("Ks")){
						mtl.setSpecular(read_light(mtlst));
					}
					else if(mtlCmd.equals("Ni")){
						mtl.setIndexOfRefraction(Float.parseFloat(mtlst.nextToken()));
					}
					else if(mtlCmd.equals("d")){
						mtl.setTransparency(Float.parseFloat(mtlst.nextToken()));
					}
					else if(mtlCmd.equals("illum")){
						mtl.setIllum(Float.parseFloat(mtlst.nextToken()));
					}
				}
				
			}
			else if(command.equals("o"))
			{
				
				if(pending)
				{
					Model model = new Model();
					model.fill(f, vt.size() > 0, vn.size() > 0);
					model.addMinMax(maxX, maxY, maxZ, minX, minY, minZ);
					o.add(model);
				}
				else
					pending = true;
			}else
                if (command.equals("v")) {
                    v.add(read_point(st));
                }
                else
                if (command.equals("vn")) {
                    vn.add(read_point(st));
                }
                else
                if (command.equals("vt")) {
                    vt.add(read_point(st));
                }
                else
                if (command.equals("f")) {
                    if (st.countTokens() != 3 && st.countTokens() != 4)
                        throw new UnsupportedOperationException("Only triangles supported");

                    if(st.countTokens() == 3)
                    {
                    Face face = new Face(st.countTokens());
                    while (st.hasMoreTokens()) {
                        String[] face_tok = st.nextToken().split("/");

                        int v_idx = -1;
                        int vt_idx = -1;
                        int vn_idx = -1;
                        
                        if(face_tok.length > 2){
                        	v_idx = Integer.parseInt(face_tok[0]) - 1;
                        if (face_tok[1] != null && !face_tok[1].equals("")) vt_idx = Integer.parseInt(face_tok[1])-1;
                        if (face_tok[2] != null && !face_tok[2].equals("")) vn_idx = Integer.parseInt(face_tok[2])-1;
                        //Log.v("objmodel", "face: "+v_idx+"/"+vt_idx+"/"+vn_idx);
                        }
                        face.addVertex(
                            v.get(v_idx),
                            vt_idx == -1 ? null : vt.get(vt_idx),
                            vn_idx == -1 ? null : vn.get(vn_idx)
                        );
                    }
                    f.add(face);
                    }
                    else if(st.countTokens() == 4)
                    {                    	
                    	//1 2 3 4 == 1 2 3 and 2 3 4
                    	Vertex[] v_index = new Vertex[st.countTokens()];
                    	Vertex[] vt_index = new Vertex[st.countTokens()];
                    	Vertex[] vn_index = new Vertex[st.countTokens()];
                    	
                    	Face face = new Face(st.countTokens()-1);
                    	Face secondFace = new Face(st.countTokens()-1);
                    	
                    	int fcount = 0;
                    	while (st.hasMoreTokens()) {
                            StringTokenizer face_tok = new StringTokenizer(st.nextToken(), "/");
                            
                            int v_idx = -1;
                            int vt_idx = -1;
                            int vn_idx = -1;
                            v_idx = Integer.parseInt(face_tok.nextToken());
                            if (face_tok.hasMoreTokens()) vn_idx = Integer.parseInt(face_tok.nextToken());
                            if (face_tok.hasMoreTokens()) vt_idx = Integer.parseInt(face_tok.nextToken());
                            
                            v_index[fcount] = v.get(v_idx-1);
                            vt_index[fcount] = vt_idx == -1 ? null : vt.get(vt_idx-1);
                            vn_index[fcount] = vn_idx == -1 ? null : vn.get(vn_idx-1);
                            //Log.v("objmodel", "face: "+v_idx+"/"+vt_idx+"/"+vn_idx);

                            //face.addVertex(
                            //    v.get(v_idx-1),
                            //    vt_idx == -1 ? null : vt.get(vt_idx-1),
                            //    vn_idx == -1 ? null : vn.get(vn_idx-1)
                            //);
                            fcount++;
                        }
                    	for(int i = 0; i < 3; i ++)
                    	{
                    		face.addVertex(
                    				v_index[i],
                    				vt_index[i],
                    				vn_index[i]);  
                    	}
                    	for(int j = 1; j < 4; j ++)
                    	{
                    		secondFace.addVertex(v_index[j], vt_index[j], vn_index[j]);
                    	}
                    	f.add(face);
                    	f.add(secondFace);
                    }
                }
                /*
                else
                if (cmd.equals("usemtl")) {
                    // lets not bother parsing material file
                    // just use the name as an asset path
                    obj.mTextureName = tok.nextToken();
                }
                */
            }

            if (pending) {
                Model model = new Model();
                model.fill(f, vt.size() > 0, vn.size() > 0);
                model.addMinMax(maxX, maxY, maxZ, minX, minY, minZ);
                o.add(model);
            }

            //obj.mModels = new Model[o.size()];
            //o.toArray(obj.mModels);
            
            //return obj;
        }
	private float[] read_light(StringTokenizer tok)
	{
		float[] lights = {1.0f, 1.0f, 1.0f, 1.0f};
		
		if(tok.hasMoreTokens()){
			lights[0] = Float.parseFloat(tok.nextToken());
			 if (tok.hasMoreTokens()) {
				 lights[1] = Float.parseFloat(tok.nextToken());
	                if (tok.hasMoreTokens()) {
	                	lights[2] = Float.parseFloat(tok.nextToken());
	                }
	            }
		}
		return lights;
	}
	private Vertex read_point(StringTokenizer tok)
    {
		Vertex ret = new Vertex();
        if (tok.hasMoreTokens()) {
            ret.x = Float.parseFloat(tok.nextToken());
            if (tok.hasMoreTokens()) {
                ret.y = Float.parseFloat(tok.nextToken());
                if (tok.hasMoreTokens()) {
                    ret.z = Float.parseFloat(tok.nextToken());
                }
            }
        }
        if(ret.x > maxX){
        	maxX = ret.x;
        }
        if(ret.x < minX){
        	minX = ret.x;
        }
        if(ret.y > maxY){
        	maxY = ret.y;
        }
        if(ret.y < minY){
        	minY = ret.y;
        }
        if(ret.z > maxZ){
        	maxZ = ret.z;
        }
        if(ret.z < minZ){
        	minZ = ret.z;
        }
        return ret;
    }
	public ArrayList<Vertex> GetVFromF()
	{
		ArrayList<Vertex> fromF = new ArrayList<Vertex>();
		for(Face face : f)
		{
			Vertex[] v = face.getV();
			for(int vindex = 0; vindex < v.length; vindex++)
			{
				fromF.add(v[vindex]);
			}
		}
		return fromF;
	}
	public ArrayList<Vertex> GetVtFromF()
	{
		ArrayList<Vertex> fromF = new ArrayList<Vertex>();
		for(Face face : f)
		{
			Vertex[] v = face.getVt();
			for(int vindex = 0; vindex < v.length; vindex++)
			{
				fromF.add(v[vindex]);
			}
		}
		return fromF;
	}
	public ArrayList<Vertex> GetVnFromF()
	{
		ArrayList<Vertex> fromF = new ArrayList<Vertex>();
		for(Face face : f)
		{
			Vertex[] v = face.getVn();
			for(int vindex = 0; vindex < v.length; vindex++)
			{
				fromF.add(v[vindex]);
			}
		}
		return fromF;
	}
}
