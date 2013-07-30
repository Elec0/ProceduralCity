package elec0.utils;

import static org.lwjgl.opengl.GL11.*;

import java.awt.Font;

import org.newdawn.slick.Color;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;

public class Text 
{
	public UnicodeFont font;
	
	public Text(String fontName, int fontStyle, int size)
	{
		// load a default java font
		Font awtFont = new Font(fontName, fontStyle, size);
		font = new UnicodeFont(awtFont);
	    font.getEffects().add(new ColorEffect(java.awt.Color.white));
	    font.addAsciiGlyphs();
	    try {
	        font.loadGlyphs();
	    } catch (SlickException ex) {
	       // Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
	    }

		/*
		// load font from a .ttf file
		try 
		{
			InputStream inputStream	= ResourceLoader.getResourceAsStream("res\\fonts\\courier.ttf");
			
			Font awtFont2 = Font.createFont(Font.TRUETYPE_FONT, inputStream);
			awtFont2 = awtFont2.deriveFont(24f); // set font size
			font2 = new UnicodeFont(awtFont2);
			font2.getEffects().add(new ColorEffect(java.awt.Color.white));
			font2.addAsciiGlyphs();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		*/
	}
	
	public void drawString(float x, float y, String text, Color color)
	{
		// Make sure alpha blending is enabled, it might not be
    	glEnable(GL_BLEND);
    	glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		
		font.drawString(x, y, text, color);
		
	}
    
}