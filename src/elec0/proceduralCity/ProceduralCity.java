package elec0.proceduralCity;

import static org.lwjgl.opengl.GL11.*;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import elec0.utils.Camera;
import elec0.utils.Vector2f;

public class ProceduralCity
{
	private Camera camera = new Camera();
	private boolean wireframe = false, fullscreen = true;
	private long lastFPS;
	private int fps;
	
	private City city;
	
	public void start()
	{
		initGL();
		init();
		
		lastFPS = getTime();
		
		while(!Display.isCloseRequested())
		{
			updateFPS();
			pollInput();
			
			
			render();
			
			Display.update();
			Display.sync(60); // limit FPS to 60
		}
		Display.destroy();
		System.exit(0);
	}

	private void render()
	{
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        
        /*gluLookAt(0, 0, 5, // Set up the LookAt
                0, 0, 0,
                0, 1, 0);*/
        
        camera.render();
        
//        drawGrid(); // Render this fill, it's lines
        
		
		// Render
        if(wireframe)
        	glPolygonMode( GL_FRONT_AND_BACK, GL_LINE );
        else
        	glPolygonMode( GL_FRONT_AND_BACK, GL_FILL );
        
        city.render();
		
	}

	private void init()
	{
		city = new City();
		city.camParentCamera = camera;
		
		Mouse.setGrabbed(true);
		camera.initCamera();
		camera.farPlane = 1000;
		camera.initPerspective();
		
	}
	
	private void initGL()
	{	
		
		try {
//			Display.setDisplayMode(new DisplayMode(800, 600));
			
			DisplayMode displayMode = null;
	        DisplayMode[] modes = Display.getAvailableDisplayModes();

	         for (int i = 0; i < modes.length; i++)
	         {
	             if (modes[i].getWidth() == 800
	             && modes[i].getHeight() == 600
	             && modes[i].isFullscreenCapable())
	               {
	                    displayMode = modes[i];
	               }
	         }
	        Display.setDisplayMode(displayMode); 
			Display.setFullscreen(true);
			Display.create();
			Display.setVSyncEnabled(true);
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(0);
		}
		
		glViewport(0, 0, Display.getWidth(), Display.getHeight());
		
		glEnable(GL_TEXTURE_2D); // Enable Texture Mapping
        glShadeModel(GL_SMOOTH); // Enable Smooth Shading
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f); // Black Background
        glClearDepth(1.0); // Depth Buffer Setup
        glEnable(GL_DEPTH_TEST); // Enables Depth Testing
        glDepthFunc(GL_LEQUAL); // The Type Of Depth Testing To Do
        
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
		
		glEnable(GL_FOG); 
			FloatBuffer fogColor = BufferUtils.createFloatBuffer(4);
			fogColor.put(0f).put(0f).put(0f).put(1f).flip();
			
			int fogMode = GL_LINEAR;
			glFogi(GL_FOG_MODE, fogMode);
			glFog(GL_FOG_COLOR, fogColor);
			glFogf(GL_FOG_DENSITY, 0.35f);
			glHint(GL_FOG_HINT, GL_DONT_CARE);
			glFogf(GL_FOG_START, 100.0f);
			glFogf(GL_FOG_END, 500.0f);
        
        // Alpha blending
    	glEnable(GL_BLEND);
    	glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        
        glMatrixMode(GL_MODELVIEW); // Select The Modelview Matrix
        glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
        

        
	}
	
	private long lastF;

	
	private void pollInput()
	{
		int x = Mouse.getX();
		int y = Display.getHeight() - Mouse.getY();
		float speed = .1f;
		int iKeypressSpeed = 50;
		int iDelta = getDelta();
		speed *= iDelta;
		
		if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
			speed *= 4;
		
		if(Keyboard.isKeyDown(Keyboard.KEY_W))
		{
			camera.MoveForwards(speed);
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_S))
		{
			camera.MoveForwards(-speed);
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_A))
		{
			camera.MoveRight(-speed);
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_D))
		{
			camera.MoveRight(speed);
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_SPACE))
		{
			camera.moveUp(speed);
		}
		
		if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE))
		{
			Display.destroy();
			System.exit(0);
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_P))
		{
			if(getTime() - lastF > iKeypressSpeed)
				wireframe = !wireframe;
			lastF = getTime();
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_N))
		{
			if(getTime() - lastF > iKeypressSpeed)
				city = new City();
			lastF = getTime();
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_C))
		{
			if(getTime() - lastF > iKeypressSpeed)
				city.bDevColors = !city.bDevColors;
			lastF = getTime();
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_F11))
		{
			if(getTime() - lastF > iKeypressSpeed)
			{
				fullscreen = !fullscreen;
				try {
					Display.setFullscreen(fullscreen);
				} catch (LWJGLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		checkMouseMove(x, y);
	}
	
	Vector2f previous = new Vector2f(), mousePos = new Vector2f();
	private float MOUSE_SENSITIVITY = 4.0f;
	
	private void checkMouseMove(float x, float y)
	{
	    float DeltX, DeltY;
	    int centX = Display.getWidth()/2, centY = Display.getHeight()/2;
	    
	    DeltX = (float)(centX - x) / MOUSE_SENSITIVITY;
	    DeltY = (float)(centY - y) / MOUSE_SENSITIVITY;
	    
	    camera.Rotate(DeltY, DeltX, 0);

	    Mouse.setCursorPosition(centX, centY);
	}
     	
	public long getTime() 
	{
		return (Sys.getTime() * 1000) / Sys.getTimerResolution();
	}
	
	public void updateFPS() 
	{
	    if (getTime() - lastFPS > 1000) {
	        Display.setTitle("FPS: " + fps); 
	        fps = 0; //reset the FPS counter
	        lastFPS += 1000; //add one second
	    }
	    fps++;
	}
	
	long lastFrame;
	public int getDelta() 
	{
		long time = getTime();
		
		int delta = (int) (time - lastFrame);
		
		lastFrame = time;	
		return delta;	
	}
	
	private void drawGrid()
	{		
        glPushMatrix();
        
        glDisable(GL_TEXTURE_2D);
        
        glColor3f(0.25f, 0.25f, 0.25f);
        
        float gridSize = 30;
        int mX = 64, mZ = 64;
        
        glTranslatef(-(mX+gridSize)/2, -5, -(mZ+gridSize)/2);
        
        
        for(int x = 0; x < mX; ++x)
        {
        	for(int z = 0; z < mZ; ++z)
        	{
        		glBegin(GL_LINE_LOOP);
        			glVertex3f(x, 0, z);
        			glVertex3f(x+gridSize, 0, z);
        			glVertex3f(x+gridSize, 0, z+gridSize);
        			glVertex3f(x, 0, z+gridSize);
        		glEnd();
        	}
        }
        
        glEnable(GL_TEXTURE_2D);
        glPopMatrix();
	}
	
	public static void main(String[] args)
	{
		ProceduralCity city = new ProceduralCity();
		city.start();

	}
}