package elec0.utils;

public class Quad
{
	public Vector3f topL, topR, botL, botR;
	
	public Quad(){ }
	public Quad(Vector3f topL, Vector3f topR, Vector3f botL, Vector3f botR)
	{this.topL = topL; this.topR = topR; this.botL = botL; this.botR = botR;}
	public void blank()
	{
		topL = topR = botL = botR = new Vector3f();
	}
}