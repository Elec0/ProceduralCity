package elec0.utils;

public class Vector3f
{
	private float x, y, z;
	
	public Vector3f() { }
	public Vector3f(Vector3f vector) // Use this for cloning, as try/catch is slow as fuck
	{this.x = vector.getX(); this.y = vector.getY(); this.z = vector.getZ();}
	public Vector3f(float x, float y, float z)
	{this.x = x; this.y = y; this.z = z;}
	
	public float getX()
	{return x;}
	public float getY()
	{return y;}
	public float getZ()
	{return z;}
	
	public void setX(float x)
	{this.x = x;}
	public void setY(float y)
	{this.y = y;}
	public void setZ(float z)
	{this.z = z;}
	public void set(Vector3f toSet)
	{x = toSet.getX();y = toSet.getY();z = toSet.getZ();}
	public void set(float x, float y, float z)
	{this.x = x; this.y = y; this.z = z;}
	
	public void addToX(float x)
	{this.x += x;}
	public void addToY(float y)
	{this.y += y;}
	public void addToZ(float z)
	{this.z += z;}
	public void addTo(Vector3f addTo)
	{x += addTo.getX();y += addTo.getY();z += addTo.getZ();}
	public void addTo(float x, float y, float z)
	{this.x += x; this.y += y; this.z += z;}
	
	static public Vector3f subtractVector3f(Vector3f first, Vector3f second)
	{return new Vector3f(first.getX() - second.getX(), first.getY() - second.getY(), first.getZ() - second.getZ());}
	
	static public float dotProduct(Vector3f first, Vector3f second)
	{ return (first.getX()*second.getX() + first.getY()*second.getY() + first.getZ()*second.getZ()); }
	
	public void normalize()
	{
		float fMag = (float)Math.sqrt(x*x + y*y + z*z);
		x = x/fMag;
		y = y/fMag;
		z = z/fMag;
	}
	
	public String toString()
	{return ("x: " + x + ", y: " + y + ", z: " + z);}
}
