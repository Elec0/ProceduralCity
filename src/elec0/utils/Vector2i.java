package elec0.utils;

public class Vector2i
{
	private int x, y;
	
	public Vector2i() { }
	public Vector2i(int x, int y)
	{this.x = x; this.y = y;}
	public Vector2i(Vector2i vector) // Use this for cloning, as try/catch is slow as fuck
	{this.x = vector.getX(); this.y = vector.getY(); }
	
	public int getX()
	{return x;}
	public int getY()
	{return y;}

	
	public void setX(int x)
	{this.x = x;}
	public void setY(int y)
	{this.y = y;}
	public void set(Vector2i toSet)
	{x = toSet.getX();y = toSet.getY();}
	public void set(int x, int y)
	{this.x = x; this.y = y;}
	
	public void addToX(int x)
	{this.x += x;}
	public void addToY(int y)
	{this.y += y;}
	public void addTo(Vector2i addTo)
	{x += addTo.getX();y += addTo.getY();}
	public void addTo(int x, int y)
	{this.x += x; this.y += y;}
	
	public String toString()
	{return ("x: " + x + ", y: " + y);}
}
