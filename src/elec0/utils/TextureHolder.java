package elec0.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

/** TextureHolder.java
 * Easy method for storing and recalling textures with Slick and LWJGL. 
 * Call loadTexture with the path for image and the name you want to store it with.
 * Then use getTexture with the name you stored it with, couldn't be simpler. 
 */

public class TextureHolder 
{
	private Map<String, Texture> textures = new HashMap<String, Texture>();
	
	public void loadTexture(String path, String name) // Full path, including extension
	{
		long ltime = Timing.getTime();
		
		if(path == null || name == null)
			return;

		if(new File(System.getProperty("user.dir") + path).exists())
		{
			Texture tempTexture;
			String[] ext = path.split("\\.");
			
			try 
			{
				tempTexture = TextureLoader.getTexture(ext[ext.length-1], ResourceLoader.getResourceAsStream(System.getProperty("user.dir") +path));
				
				textures.put(name, tempTexture);
				
				System.out.println("Texture " + name + " Loaded TIME: " + (Timing.getTime() - ltime));
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
			catch(RuntimeException e)
			{
				e.printStackTrace();
			}
			
		}
		else
		{
			System.out.println(path + " not found.");
		}
	}
	
	public boolean textureExists(String name)
	{
		if(textures.containsKey(name))
			return true;
		return false;
	}
	
	public Texture getTexture(String name)
	{
		Texture toReturn = null;
		
		if(textures.containsKey(name))
		{
			toReturn = textures.get(name);
		}
		else
		{
			System.out.println("ERROR getting texture with name " + name + ", does not exist.");
		}
		
		return toReturn;
	}
}
