package FileIO;

import java.nio.FloatBuffer;

public class Face {
	Vertex v[];
	Vertex vt[];
	Vertex vn[];
	int size;
	int count;
	
	public Face(int size)
	{
		this.size = size;
		this.count = 0;
		this.v = new Vertex[size];
		this.vt = new Vertex[size];
		this.vn = new Vertex[size];
	}
	public boolean addVertex(Vertex v, Vertex vt, Vertex vn)
	{
		if(count >= size)
			return false;
		this.v[count] = v;
		this.vt[count] = vt;
		this.vn[count] = vn;
		count++;
		
		return true;
	}
	public Vertex[] getV()
	{
		return v;
	}
	public Vertex[] getVt()
	{
		return vt;
	}
	public Vertex[] getVn()
	{
		return vn;
	}
	
	public void pushOnto(FloatBuffer v_buffer, FloatBuffer vt_buffer, FloatBuffer vn_buffer)
	{
		for(int i = 0; i < size; i++)
		{
			v_buffer.put(v[i].x); v_buffer.put(v[i].y); v_buffer.put(v[i].z);

            if (vt_buffer != null && vt[i] != null) {
                vt_buffer.put(vt[i].x); vt_buffer.put(vt[i].y);
            }

            if (vn_buffer != null && vn[i] != null) {
                vn_buffer.put(vn[i].x); vn_buffer.put(vn[i].y); vn_buffer.put(vn[i].z);
            }
		}
	}
}
