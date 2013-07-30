package elec0.proceduralCity;

import static org.lwjgl.opengl.GL11.*;

import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureImpl;

import elec0.utils.Camera;
import elec0.utils.Timing;
import elec0.utils.Vector2f;
import elec0.utils.Vector3f;

public class City
{
	private Grid gGrid;
	private Building[][] buildings;
	private byte bDone = 0;
	// Generating a warm color, fades to blue, fades to dark blue, fades to black
	private Color cSkyBotStart, cSkyBotEnd, cSkyMidEnd, cSkyTopEnd;
	private Texture tSky;
	
	public static boolean bDevColors = false;
	
	public static Camera camParentCamera;
	
	public City()
	{
		Building.texGen = new TextureGenerator(30);
		
		// Generate a warm color to start the sky with, warm is red and green less than the red
		cSkyBotStart = new Color(randomInRange(.3f, 1), 0, 0);
		cSkyBotStart = new Color(cSkyBotStart.r, randomInRange(0, cSkyBotStart.r), 0);
		// A blue
		cSkyBotEnd = new Color(0, 0, randomInRange(.2f, .3f));
		// Dark blue
		cSkyMidEnd = new Color(0, 0, randomInRange(.1f, cSkyBotEnd.b-0.05f));
		cSkyTopEnd = Color.black;
		
		tSky = TextureGenerator.generateSky(cSkyBotStart, cSkyBotEnd, cSkyMidEnd, cSkyTopEnd);
		
		gGrid = new Grid();

		gGrid.generate();
		buildings = new Building[gGrid.iGridSize][gGrid.iGridSize];
	}
	float max = 1, min = 0;
	public void render()
	{
		// Real-time texture generate
		Building.texGen.update();
		update();
		
		renderSky();
		
		renderGrid();
				
		renderBuildings();
		glColor3f(0, 0, 0);	

	}
	
	private int iStopX = 0, iStopY = 0;
	private void update()
	{
		// We haven't generated the buildings yet, and the texture is created
		if(bDone == 0 && Building.texGen.isGenerated())
		{
			long lStart = Timing.getTime();
			int iVerts = 0;
			
			for(int x = iStopX; x < gGrid.iGridSize; ++x)
			{
				if(Timing.getTime() > lStart+Building.iBuildTime)
					return;
				for(int y = iStopY; y < gGrid.iGridSize; ++y)
				{
					if(Timing.getTime() > lStart+Building.iBuildTime)
						return;
					
					if(gGrid.getLevel(x, y) != 0)
					{
						int fBaseX = x * gGrid.getSquareSize();
						int fBaseY = y * gGrid.getSquareSize();
						
						buildings[x][y] = new Building(gGrid.getLevel(x, y), new Vector3f(fBaseX, fBaseY, 0), gGrid.getSquareSize(), gGrid.getSquareSize());
						iVerts += buildings[x][y].iVertsToRender;
					}
					iStopY = y;
				}
				iStopY = 0;
				iStopX = x;
			}
			System.out.println("Total Verts: " + iVerts); 
			bDone = 1;
		}
	}
	
	long lTimeTest;
	private void renderBuildings()
	{
		for(int x = 0; x < buildings.length; ++x)
		{
			for(int y = 0; y < buildings[0].length; ++y)
			{
				if(buildings[x][y] != null)
				{						
					// Checking to see if the section is in 45 degrees either way
					if(!camParentCamera.pointInAngle(new Vector2f(buildings[x][y].getOrigin().getX(), buildings[x][y].getOrigin().getY())))
					{
						// If the point isn't in view
//							x+= 1;
						y+= 1;
					}
					else
					{
						buildings[x][y].render();
					}
				}
			}
		}
	}
	
	public void renderSky()
	{
		float fStartX = .5f, fStartY = .5f, fStartZ = .5f;
		float iWidth = 1, iHeight = 1f;
		
		glPushMatrix();
		
		glLoadIdentity();
		glRotatef(-camParentCamera.getRotateX(), 1.0f, 0.0f, 0.0f);
        glRotatef(-camParentCamera.getRotateY(), 0.0f, 1.0f, 0.0f);
//		
		glPushAttrib(GL_ENABLE_BIT);

		glDisable(GL_DEPTH_TEST);
		glEnable(GL_TEXTURE_2D);
		glColor3f(1, 1, 1);
		// create a texture with this instead of three quads
		tSky.bind();
		glTranslatef(0, .3f, 0);
		
		glBegin(GL_QUADS);
			// Front
			glTexCoord2f(1, 0); glVertex3f(fStartX, -fStartY, -fStartZ);
			glTexCoord2f(0, 0); glVertex3f(-fStartX, -fStartY, -fStartZ);
			glTexCoord2f(0, .9f); glVertex3f(-fStartX, fStartY, -fStartZ);
			glTexCoord2f(1, .9f); glVertex3f(fStartX, fStartY, -fStartZ);
			// left
			glTexCoord2f(1, 0); glVertex3f(fStartX, -fStartY, fStartZ);
			glTexCoord2f(0, 0); glVertex3f(fStartX, -fStartY, -fStartZ);
			glTexCoord2f(0, .9f); glVertex3f(fStartX, fStartY, -fStartZ);
			glTexCoord2f(1, .9f); glVertex3f(fStartX, fStartY, fStartZ);
			// back
			glTexCoord2f(1, 0); glVertex3f(-fStartX, -fStartY, fStartZ);
			glTexCoord2f(0, 0); glVertex3f(fStartX, -fStartY, fStartZ);
			glTexCoord2f(0, .9f); glVertex3f(fStartX, fStartY, fStartZ);
			glTexCoord2f(1, .9f); glVertex3f(-fStartX, fStartY, fStartZ);
			//right
			glTexCoord2f(1, 0); glVertex3f(-fStartX, -fStartY, -fStartZ);
			glTexCoord2f(0, 0); glVertex3f(-fStartX, -fStartY, fStartZ);
			glTexCoord2f(0, .9f); glVertex3f(-fStartX, fStartY, fStartZ);
			glTexCoord2f(1, .9f); glVertex3f(-fStartX, fStartY, -fStartZ);
		glEnd();

		glPopAttrib();
		glPopMatrix();
		
	}
	
	private void renderGrid()
	{
		Color one = Color.red;
		Color two = Color.green;
		Color three = Color.blue;
		Color four = new Color(0, 255, 255);
		
		glDisable(GL_TEXTURE_2D);
		glBegin(GL_QUADS);
		
		for(int y = 0; y < gGrid.iGridSize; ++y)
		{
			for(int x = 0; x < gGrid.iGridSize; ++x)
			{
				float lv = gGrid.getLevel(x, y);

				if(bDevColors == false)
				{
					if(lv == 0) // If the square is road
						glColor3f(.1f, .1f, .1f);
					else	
					{
						glColor3f(.01f, .01f, .01f);
					}
				}
				else
				{
					if(lv == 0)
						glColor3f(1, 1, 1);
					else if(lv == 1)
						glColor3f(1, 0, 0);
					else if(lv == 2)
						glColor3f(0, 1, 0);
					else if(lv == 3)
						glColor3f(0, 0, 1);
					else if(lv == 4)
						glColor3f(0, 1, 1);
					else if(lv > 0) // Is a value, and needs interpolation
					{
						
						// Linear interpolation to get gradient
						int num1 = (int)Math.floor(lv);
						int num2 = (int)Math.ceil(lv);
						float t = num1 - lv;
						Color col1 = Color.black;
						Color col2 = Color.black;
						
						// To make sure we actually get a color, as 1 is the lowest the development level can go
						if(num1 < 1)
							num1 = 1;
						if(num2 < 1)
							num2 = 1;
						
						// Set the colors to their whole values
						if(num1 == 1)
							col1 = one;
						else if(num1 == 2)
							col1 = two;
						else if(num1 == 3)
							col1 = three;
						else if(num1 >= 4)
							col1 = four;
						
						if(num2 == 1)
							col2 = one;
						else if(num2 == 2)
							col2 = two;
						else if(num2 == 3)
							col2 = three;
						else if(num2 >= 4)
							col2 = four;
						
						// Interpolate colors
						Color newCol = new Color(0);
						newCol.r = col1.r + (col2.r - col1.r) * t;
						newCol.g = col1.g + (col2.g - col1.g) * t;
						newCol.b = col1.b + (col2.b - col1.b) * t;
						
						glColor3f(newCol.r, newCol.g, newCol.b);	
					}
				}
				float fBaseX = x * gGrid.getSquareSize();
				float fBaseY = y * gGrid.getSquareSize();
				
				
				glVertex3f(fBaseX, 0, fBaseY);
				glVertex3f(fBaseX + gGrid.getSquareSize(), 0, fBaseY);
				glVertex3f(fBaseX + gGrid.getSquareSize(), 0, fBaseY + gGrid.getSquareSize());
				glVertex3f(fBaseX, 0, fBaseY + gGrid.getSquareSize());
				
			}
		}

		glEnd();
		glColor3f(0, 0, 0);
		glBegin(GL_QUADS);
			glVertex3f(-10000, -1f, 0);
			glVertex3f(-10000, -1f, -10000);
			glVertex3f(10000, -1f, -10000);
			glVertex3f(10000, -1f, 10000);
		glEnd();
		glEnable(GL_TEXTURE_2D);
	}
	
	private float randomInRange(float min, float max) 
	{
		return (float)Math.random() * (max-min) + min;
	}
}