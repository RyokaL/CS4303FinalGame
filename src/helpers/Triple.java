package helpers;

public class Triple {

	public int x, y, z;
	public float X, Y, Z;
	
	public Triple(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Triple(float X, float Y, float Z) {
		this.X = X;
		this.Y = Y;
		this.Z = Z;
	}
	
	@Override
	public String toString() {
		return "x: " + x + " y: " + y + " z: " + z;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj) {
			return true;
		}
		if(obj == null) {
			return false;
		}
		if(getClass() != obj.getClass()) {
			return false;
		}
		Triple other = (Triple)obj;
		if(other.x == this.x && other.y == this.y && other.z == this.z) {
			return true;
		}
		else if(other.X == this.X && other.Y == this.Y && other.Z == this.Z) {
			return true;
		}
		return false;
	}
}
