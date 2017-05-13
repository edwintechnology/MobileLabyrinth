package FileIO;

public class Vertex extends Object {
	public float x;
	public float y;
	public float z;

	public Vertex() {
		x = 0.0f;
		y = 0.0f;
		z = 0.0f;
	}

	public Vertex(float x, float y, float z) {
		set(x, y, z);
	}

	public Vertex(Vertex src) {
		set(src.x, src.y, src.z);
	}

	public final boolean equals(float x, float y, float z) {
		return ((this.x == x) && (this.y == y) && (this.z == z));
	}

	public void set(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public void minmax(float minx, float miny, float minz, float maxx,
			float maxy, float maxz) {
		this.x = Math.min(Math.max(this.x, minx), maxx);
		this.y = Math.min(Math.max(this.y, miny), maxy);
		this.z = Math.min(Math.max(this.z, minz), maxz);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;

		if (!(o instanceof Vertex))
			return false;

		Vertex obj = (Vertex) o;
		return this.equals(obj.x, obj.y, obj.z);
	}

	@Override
	public String toString() {
		return "(" + x + "," + y + "," + z + ")";
	}

	@Override
	public int hashCode() {
		throw new UnsupportedOperationException();
	}
}
