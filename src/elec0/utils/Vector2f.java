package elec0.utils;

public class Vector2f
{
	private float x, y;
	
	public Vector2f() { }
	public Vector2f(float x, float y)
	{this.x = x; this.y = y;}
	public Vector2f(Vector2f vector) // Use this for cloning, as try/catch is slow as fuck
	{this.x = vector.getX(); this.y = vector.getY();}
	
	public float getX()
	{return x;}
	public float getY()
	{return y;}

	
	public void setX(float x)
	{this.x = x;}
	public void setY(float y)
	{this.y = y;}
	public void set(Vector2f toSet)
	{x = toSet.getX();y = toSet.getY();}
	public void set(float x, float y)
	{this.x = x; this.y = y;}
	
	public void addToX(float x)
	{this.x += x;}
	public void addToY(float y)
	{this.y += y;}
	public void addTo(Vector2f addTo)
	{x += addTo.getX();y += addTo.getY();}
	public void addTo(float x, float y)
	{this.x += x; this.y += y;}
	
	static public float dotProduct(Vector2f first, Vector2f second)
	{
		return (first.getX()*second.getX() + first.getY()*second.getY());
	}
	
	public void normalize()
	{
		float fMag = (float)Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
		x = x/fMag;
		y = y/fMag;
	}
	public String toString()
	{return ("x: " + x + ", y: " + y);}
}
