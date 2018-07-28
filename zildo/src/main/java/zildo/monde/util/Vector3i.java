package zildo.monde.util;

public class Vector3i {

	public final int x;
	public final int y;
	public final int z;
	
	public Vector3i(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Vector4f normalize() {
		return new Vector4f(x / 256f, y / 256f, z / 256f, 1f);
	}
}
