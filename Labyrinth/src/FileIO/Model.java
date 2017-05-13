package FileIO;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

public class Model {
	private String name;
	private FloatBuffer v;
	private FloatBuffer vt;
	private FloatBuffer vn;
	private float minX, minY, minZ, maxX, maxY, maxZ;
	private FloatBuffer indices;
	private FloatBuffer vertexs;
	
	private int v_size;
	public float getMaxX() { return maxX; }
	public float getMaxY() { return maxY; }
	public float getMaxZ() { return maxZ; }
	public float getMinX() { return minX; }
	public float getMinY() { return minY; }
	public float getMinZ() { return minZ; }
	public void addMinMax(float xx, float xy, float xz, float mx, float my, float mz)
	{
		minX = mx;
		minY = my;
		minZ = mz;
		maxX = xx;
		maxY = xy;
		maxZ = xz;
	}
	public void fill(ArrayList<Face> faces, boolean has_tex, boolean has_normals)
	{
		int f_len = faces.size();
		
		this.v_size = f_len * 3;
		this.v = FloatBuffer.allocate(v_size * 3);
		if(has_tex)
			this.vt = FloatBuffer.allocate(v_size * 2);
		if(has_normals)
			this.vn = FloatBuffer.allocate(v_size * 3);
		
		for(int i = 0; i < f_len; i++){
			Face face = faces.get(i);
			face.pushOnto(this.v, this.vt, this.vn);
		}
		
		 this.v.rewind();
         if (this.vt != null)
             this.vt.rewind();
         if (this.vn != null)
             this.vn.rewind();
	}
	
	public FloatBuffer getV()
	{
		return this.v;
	}
	public FloatBuffer getVN()
	{
		return this.vn;
	}
	public FloatBuffer getVT()
	{
		return this.vt;
	}
	public int getSize()
	{
		return v_size;
	}
}
