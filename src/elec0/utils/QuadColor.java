package elec0.utils;

import org.newdawn.slick.Color;

public class QuadColor
{
	public Vector3f topL, topR, botL, botR;
	public Color cTopL, cTopR, cBotL, cBotR;
	
	public QuadColor(){ }
	public QuadColor(Vector3f topL, Vector3f topR, Vector3f botL, Vector3f botR)
	{this.topL = topL; this.topR = topR; this.botL = botL; this.botR = botR;}
	public QuadColor(Vector3f topL, Vector3f topR, Vector3f botL, Vector3f botR, Color cTopL, Color cTopR, Color cBotL, Color cBotR)
	{this.topL = topL; this.topR = topR; this.botL = botL; this.botR = botR; this.cTopL = cTopL; this.cTopR = cTopR; this.cBotL = cBotL; this.cBotR = cBotR;}
	public void blank()
	{
		topL = topR = botL = botR = new Vector3f();
		cTopL = cTopR = cBotL = cBotR = new Color(Color.white);
	}
}