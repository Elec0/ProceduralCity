package elec0.proceduralCity;

import java.util.Random;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.opengl.Texture;

import elec0.utils.Timing;

public class TextureGenerator 
{
	public static Texture tFinalTex;
	private Image tTex;
	private Graphics g;
	private Random rand;
	private Color cBaseColor;
	
	private int iStart = 2;
	private int iStopX = iStart, iStopY = iStart;
	private int iMilliseconds;
	private long lEndTime;
	private byte bDoneGen = 0, bStarted = 0;
	
	public int WIND_WIDTH = 8; // 8*64 = 512, so if we have 64x64 8x8 windows we fill the square
	public int WIND_HEIGHT = 8;
	public int TEX_HEIGHT = 512;
	public int TEX_WIDTH = 512;
	private final int BUFFER_X = WIND_WIDTH * 1;
	private final int BUFFER_Y = WIND_WIDTH * 1;
	private final int BUFFER_MAX_X = TEX_WIDTH - WIND_WIDTH * 2;
	private final int BUFFER_MAX_Y = TEX_HEIGHT - WIND_HEIGHT * 2;
	
	public TextureGenerator()
	{
		iMilliseconds = 10000; // Default, give it 10 seconds to generate a texture, which should be more than enough
		generate();
	}
	public TextureGenerator(int iTime)
	{
		iMilliseconds = iTime;
		generate();
	}
	
	public static Color pickColor()
	{
		Color cColor = Color.white;
		Random r = new Random();
		int colors = 3;
		int pick = r.nextInt(colors);
		
		// Pick default color, either blue, white, or orange
		
		
		if(pick == 0) // White
		{
			cColor = Color.white;
		}
		else if(pick == 1) // Orange
		{
//			cBaseColor = new Color(100 + rand.nextInt(55), 60 + rand.nextInt(155), 0);
			cColor = new Color(220 + r.nextInt(35), 215 + r.nextInt(35), 175 + r.nextInt(35));
		}
		else if(pick == 2)
		{
//			cBaseColor = new Color(0, 60 + rand.nextInt(75), 60 + rand.nextInt(75));
			cColor = new Color(200 + r.nextInt(30), 180 + r.nextInt(50), 230 + r.nextInt(25));
		
		}
		
		return cColor;
	}
	
	public void generate()
	{
		lEndTime = Timing.getTime() + iMilliseconds;
		
		// If we haven't started generating a texture yet, create it and suchlike.
		if(bStarted == 0)
		{
			rand = new Random();
			try {
				tTex = new Image(TEX_WIDTH, TEX_HEIGHT);
				g = tTex.getGraphics();
			} catch (SlickException e) {
				e.printStackTrace();
			}
			
			cBaseColor = Color.white;
			
			g.setColor(Color.black);
			g.fillRect(0, 0, TEX_WIDTH, TEX_HEIGHT);
			g.flush();
			
			bStarted = 1;
		}
		createWindows();
	}
	private void createWindows()
	{
		// Start at 1 or 2 to give black room at top left of texture for tops of buildings
		for(int y = iStopY; y < TEX_HEIGHT; y += WIND_HEIGHT)
		{
			if(Timing.getTime() > lEndTime)
				return;
			
			for(int x = iStopX; x < TEX_WIDTH; x += WIND_WIDTH)
			{
				if(Timing.getTime() > lEndTime)
					return;
				
				float fPercent = randomInRange(0, .1f);
				
				if(rand.nextFloat() < fPercent)
				{
					
					genWindow(x, y, true);
				}
				else
					genWindow(x, y, false);
				iStopX = x;
			}
			iStopX = 0;
			iStopY = y;
		}
		tFinalTex = tTex.getTexture();
		bDoneGen = 1;
	}
	
	private void genWindow(int x, int y, boolean lit)
	{
		if(lit)
		{
			float percent = randomInRange(0, 0.8f);
			
			if(rand.nextBoolean()) // Subtract or add
				percent *= -1;
			
			float fR = cBaseColor.r*percent + cBaseColor.r;
			float fG = cBaseColor.g*percent + cBaseColor.g;
			float fB = cBaseColor.b*percent + cBaseColor.b;
			
			if(fR > 255)
				fR = 255;
			if(fG > 255)
				fG = 255;
			if(fB > 255)
				fB = 255;
			
			Color col = new Color(fR, fG, fB);
			
			g.setColor(col);
			
			g.fillRect(x, y, WIND_WIDTH, WIND_HEIGHT);
			g.flush();
		}
		else
		{
			int col = 0;
			if(rand.nextBoolean())
				col = rand.nextInt(10);
			g.setColor(new Color(col, col, col));
			
			g.fillRect(x, y, WIND_WIDTH, WIND_HEIGHT);
			g.flush();
		}
	}
	
	public static Texture generateSky(Color botStart, Color botEnd, Color midEnd, Color topEnd)
	{
		Image iSky = null;
		Graphics g = null;
		try {
			iSky = new Image(128,128);
			g = iSky.getGraphics();
		} catch (SlickException e) {
			e.printStackTrace();
		}
		
		int iMax = iSky.getHeight();
		int iTime = iMax/4;
		
		g.setColor(Color.black);
		g.fillRect(0, 0, iSky.getWidth(), iSky.getHeight());
		g.flush();
		
		// Really bottom
//		for(int i = 0; i < iTime; ++i)
//		{
//			float fTime = (float)(i) / (float)iTime;
//			
//			Color cN = new Color(lerp(0, botStart.r, fTime), lerp(0, botStart.g, fTime), lerp(0, botStart.b, fTime));
//			g.setColor(cN);
//			g.fillRect(0, i, iSky.getWidth(), i+1);
//		}
		// Bottom
		for(int i = 0; i < iTime; ++i)
		{
			float fTime = (float)(i) / (float)iTime;
			
			Color cN = new Color(lerp(botStart.r, botEnd.r, fTime), lerp(botStart.g, botEnd.g, fTime), lerp(botStart.b, botEnd.b, fTime));
			g.setColor(cN);
			g.fillRect(0, i, iSky.getWidth(), i+1);
		}
		
		for(int i = iTime; i < 2*iTime; ++i)
		{
			float fTime = (float)(i-iTime) / (float)iTime;
			Color cN = new Color(lerp(botEnd.r, midEnd.r, fTime), lerp(botEnd.g, midEnd.g, fTime), lerp(botEnd.b, midEnd.b, fTime));
			g.setColor(cN);
			g.fillRect(0, i, iSky.getWidth(), i+1);
		}
		
		for(int i = 2*iTime; i < iMax; ++i)
		{
			float fTime = (float)(i-2*iTime) / (float)iTime;
			Color cN = new Color(lerp(midEnd.r, topEnd.r, fTime), lerp(midEnd.g, topEnd.g, fTime), lerp(midEnd.b, topEnd.b, fTime));
			g.setColor(cN);
			g.fillRect(0, i, iSky.getWidth(), i+1);
		}
		g.flush();
		
		return iSky.getTexture();
	}
	
	private static float lerp(float v0, float v1, float time)
	{
		return v0+(v1-v0)*time;
	}
	
	/**
	 * Call during render to generate texture in real-time
	 */
	public void update()
	{
		if(bDoneGen == 0)
			generate();
	}
	
	public boolean isGenerated()
	{
		if(bDoneGen == 1)
			return true;
		return false;
	}
	/**
	 * Generates a 5x5 black texture
	 * @return
	 */
	public static Texture generateBlack()
	{
		Image iBlack = null;
		Graphics g = null;
		try {
			iBlack = new Image(5, 5);
			g = iBlack.getGraphics();
		} catch (SlickException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		g.setColor(Color.black);
		g.drawRect(0, 0, 5, 5);
		g.flush();
		
		return iBlack.getTexture();
	}
	
	private float randomInRange(float min, float max) 
	{
		return (float)Math.random() * (max-min) + min;
	}
}
