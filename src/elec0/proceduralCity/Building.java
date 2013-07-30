package elec0.proceduralCity;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_TRIANGLE_FAN;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;

import java.util.ArrayList;
import java.util.Random;

import org.newdawn.slick.Color;

import elec0.utils.Vector3f;

public class Building
{

	// In case I ever need to calculate a circle at render, use this algorithm: http://slabode.exofire.net/circle_draw.shtml

	
	public float fDevLevel;
	private ArrayList<Shape3D> s3Objects;
	private Vector3f v3Origin;
	public int iMaxWidthX, iMaxWidthY;
	private Random rand;
	public static TextureGenerator texGen;
	private Color cColor;
	
	// Two random numbers are picked for the height, with maxes and mins as defined here, then they're added together
	private static final float S1_HEIGHT_MAX_MULTI = 24f; // 12
	private static final float S1_HEIGHT_MIN_MULTI = 16f; // 8
	private static final float S1_WIDTH_MIN_MULTI = 4f; // 2
	private static final float S1_WIDTH_MAX_MULTI = 14f; // 7
	private static final float S1_TILING_HEIGHT = .5f; // 40
	private static final float S1_TILING_HEIGHT_MULTI = .75f; // 3
	private static final float S1_BUFFER = 4;
	
	private static final float S2_CIRCLE_CUT_CH = 0.05f; // Chance of skipping 90 degrees on the 2nd style .1
	private static final int S2_CIRCLE_MAX_CUTS = 1; // 2
	private static final float S2_HEIGHT_MAX_MULTI = 24f; // 12
	private static final float S2_HEIGHT_MIN_MULTI = 16f; // 8
	private static final float S2_WIDTH_MAX_MULTI = 6f; // 3
	private static final float S2_WIDTH_MIN_MULTI = 2f; // 1
	private static final float S2_BUFFER = 4;
	
	// The default cube
	private static final float S3_HEIGHT_MAX_MULTI = 10f; // .5
	private static final float S3_HEIGHT_MIN_MULTI = 4f; // 2
	private static final float S3_WIDTH_MIN_MULTI = 8f; // 2
	private static final float S3_WIDTH_MAX_MULTI = 16f; // 7
	private static final float S3_BUFFER = 4;
	
	private static final int[] STYLE_MIN_LEVEL = new int[] {2, 4, 0}; // Index + 1 = building style
	
	private static final int CIRCLE_SEGMENTS = 36; // 36 10 degree segments
	
	private static final float DEG_RAD = 3.14159f / 180f;
	
	public static final int iBuildTime = 5; // Time in milliseconds allowed per frame to generate buildings
	
	public int iVertsToRender = 0;
	
	/**
	 * 
	 * @param fDevLevel
	 * @param v3Origin
	 * @param iMaxWidthX
	 * @param iMaxWidthY
	 */
	public Building(float fDevLevel, Vector3f v3Origin, int iMaxWidthX, int iMaxWidthY)
	{
		this.fDevLevel = fDevLevel;
		this.v3Origin = v3Origin;
		this.iMaxWidthX = iMaxWidthX;
		this.iMaxWidthY = iMaxWidthY;
		
		init();
		generate();
	}
	
	public void init()
	{
		s3Objects = new ArrayList<Shape3D>();
		rand = new Random();		
	}
	
	private void generate()
	{
		// Only generate one style. Bad things will happen if more than one is used
		int iStyleSel;
		ArrayList<Integer> iStyleAvail = new ArrayList<Integer>();
		
		for(int i = 0; i < STYLE_MIN_LEVEL.length; ++i)
		{
			if(fDevLevel >= STYLE_MIN_LEVEL[i])
			{
				if(i == 2 && fDevLevel < 1)
					iStyleAvail.add(i+1);
				else
					iStyleAvail.add(i+1);
			}
		}
		
		if(iStyleAvail.size() > 1)
		{
			iStyleSel = iStyleAvail.get(rand.nextInt(iStyleAvail.size()));
		}
		else
			iStyleSel = iStyleAvail.get(0);
//		iStyleSel = 1;
		
		switch(iStyleSel)
		{
		case 1:
			genStyle1(); // Multi rectangle
			break;
		case 2:
			genStyle2(); // Circular
			break;
		case 3:
			genStyle3(); // Single square
			break;
		}
		
		cColor = TextureGenerator.pickColor();
		
	}
	
	
	/**
	 * Generates the square base w/ rectangular protrusions
	 */
	private void genStyle1()
	{
		float fTiling;
		int iBaseHeight = (int)(S1_HEIGHT_MIN_MULTI * fDevLevel) + rand.nextInt((int)(fDevLevel * S1_HEIGHT_MAX_MULTI));
		int iBaseWidth = (int)(S1_WIDTH_MIN_MULTI * fDevLevel) + rand.nextInt((int)(S1_WIDTH_MAX_MULTI * fDevLevel));
		
		float fPreviousHeight, fPreviousWidth;
		float fSteps = 0.05f; // Number of steps to take, based on base height
						
		if(iMaxWidthX <= iBaseWidth || iMaxWidthY <= iBaseWidth)
		{
//			System.out.println("Max Width X or Y is less than the base width, setting base width to max x width - 1");
			iBaseWidth = iMaxWidthX - 1;
		}
		
		fTiling = (float)iBaseHeight/S1_TILING_HEIGHT;
		fTiling = (float)Math.ceil(fTiling * S1_TILING_HEIGHT_MULTI);
		
		// If the height isn't divisible by the window height, make it so by getting the divisor and rounding, then multiplying
		if(iBaseHeight % texGen.WIND_HEIGHT != 0)
			iBaseHeight = Math.round(iBaseHeight/texGen.WIND_HEIGHT)*texGen.WIND_HEIGHT;
		
		if(iBaseWidth % texGen.WIND_WIDTH != 0)
			iBaseWidth = Math.round(iBaseWidth / texGen.WIND_WIDTH)*texGen.WIND_WIDTH;
		
		
		fPreviousHeight = iBaseHeight;
		fPreviousWidth = iBaseWidth;

		Shape3D[] s3Shape = new Shape3D[(int)Math.ceil(iBaseHeight * fSteps)+1];
		
//		s3Shape[0] = new Shape3D(GL_QUADS);
//		s3Shape[0].makeBoxUV(iBaseWidth, iBaseWidth, iBaseHeight, null, v3Origin, 0, fTiling, 0, 1);

		for(int i = 1; i < (int)Math.ceil(iBaseHeight * fSteps); ++i)
		{
			float fWidthX;
			float fWidthY;
			float fHeight;
			float fOffsetX;
			float fOffsetY;
			
			// Maximum of 50% of the base width
			int iRnd = 1 + (int)(iBaseWidth * 0.5f);
			int iUnit = 1;
			
			s3Shape[i] = new Shape3D(GL_QUADS);
			
			if(rand.nextBoolean())
			{
				fWidthX = iBaseWidth + iUnit + rand.nextInt(iRnd);
				fWidthY = iBaseWidth + iUnit + rand.nextInt(iRnd);
				
//				fWidthX = (int)randomInRange(iBaseWidth + iUnit, iBaseWidth + iUnit + iRnd)
				
				if(fWidthX > fPreviousWidth || fWidthX > iMaxWidthX)
				{
					while(fWidthX > fPreviousWidth && fWidthX > iMaxWidthX)
					{
						fWidthX = iBaseWidth + iUnit + rand.nextInt(iRnd);
					}
					while(fWidthY > fPreviousWidth && fWidthX > iMaxWidthY)
					{
						fWidthY = iUnit + rand.nextInt(iRnd);
					}
//					fPreviousWidth = fWidthX;
					
				}
				
			}
			else
			{
				fWidthX = iUnit + rand.nextInt(iRnd);
//				fWidthX = (int)randomInRange(iUnit, iMaxWidthX);
				
				fWidthY = iBaseWidth + iUnit + rand.nextInt(iRnd);
//				fWidthY = (int)randomInRange(iBaseWidth + iUnit, iMaxWidthY);
				
				
				if(fWidthY < fPreviousWidth || fWidthY > iMaxWidthY || fWidthX > iMaxWidthX)
				{
					while(fWidthY > fPreviousWidth && fWidthY > iMaxWidthY)
					{	
						fWidthY = iBaseWidth + iUnit + rand.nextInt(iRnd);
					}
					while(fWidthX > iMaxWidthX)
					{
						fWidthX = iUnit + rand.nextInt(iRnd);
					}
//					fPreviousWidth = fWidthY;
				}
				
			}
			
			// Height must be at least 40% of last height
			fHeight = (fPreviousHeight * 0.4f + rand.nextFloat()) + randomInRange(0, fPreviousHeight);
//			fHeight = randomInRange(fPreviousHeight * .4f + rand.nextFloat(), 
			
			// Make sure height isn't 
//			fHeight = (int)randomInRange(1, fPreviousHeight);
			if(fHeight > fPreviousHeight)
			{
				while(fHeight < fPreviousHeight)
					fHeight = (fPreviousHeight * 0.4f + rand.nextFloat()) + randomInRange(0, fPreviousHeight);
			}
			
			
			//Stop if we get too close to the bottom
			if(fHeight < 0.3f * iBaseHeight)
				break;
			
			// Making sure we're at multiples of the window dimensions
			if(fHeight % texGen.WIND_HEIGHT != 0)
				fHeight = (int)Math.floor((float)fHeight / texGen.WIND_HEIGHT) * texGen.WIND_HEIGHT;
			
			if(fWidthX % texGen.WIND_WIDTH != 0)
				fWidthX = (int)Math.floor((float)fWidthX / texGen.WIND_WIDTH) * texGen.WIND_WIDTH;	
			if(fWidthY % texGen.WIND_WIDTH != 0)
				fWidthY = (int)Math.floor((float)fWidthY / texGen.WIND_WIDTH) * texGen.WIND_WIDTH;	
			
			// If we're down too low, things will die
			if(fHeight == 0)
				break;
			
			fPreviousHeight = fHeight;
			
			// Set tiling for height
			fTiling = (float)iBaseHeight/S1_TILING_HEIGHT;
			fTiling = (float)Math.ceil(fTiling * S1_TILING_HEIGHT_MULTI);
			
			fOffsetX = 0;
			fOffsetY = 0;
			
			// Slightly random offsets
			float iRndX = iBaseWidth - fWidthX;
			float iRndY = iBaseWidth - fWidthY;
			
			if(iRndX <= 0)
				fOffsetX = 0;
			else
				fOffsetX = rand.nextFloat()*iRndX;
			
			if(iRndY <= 0)
				fOffsetY = 0;
			else
				fOffsetY = rand.nextFloat()*iRndY;			
			
			// Requires that the center be somewhere in each one
			if(rand.nextBoolean())
				fOffsetX = (int)(0.5f * iBaseWidth);
			else
				fOffsetY = (int)(0.5f * iBaseWidth);
			
			// Making sure we fit in the footprint
			if(fOffsetX + fWidthX > iMaxWidthX)
			{
				fOffsetX = iMaxWidthX - (fOffsetX + fWidthX);
			}
			if(fOffsetY + fWidthY > iMaxWidthY)
			{
				fOffsetY = iMaxWidthY - (fOffsetY + fWidthY);
			}
			
			if(fWidthX > fPreviousWidth)
				break;
			
			// Shouldn't happen, but just in case it does, prevents errors
			if(fWidthX <= 0)
				fWidthX = fPreviousWidth;
			if(fWidthY <= 0)
				fWidthY = fPreviousWidth;
						
			// To prevent texture flickering because planes share same space
			fWidthX -= (float)i*.2f;
			fWidthY -= (float)i*.2f;
			
			fOffsetX += (float)i*.1f;
			fOffsetY += (float)i*.1f;
			
			float fUVBaseX = randomInRange(0, texGen.TEX_WIDTH - fWidthX);
			float fUVBaseY = randomInRange(0, texGen.TEX_WIDTH - fWidthY);
			float fUVBaseHeight = randomInRange(0, texGen.TEX_HEIGHT - fWidthX); // fHeight
			
			s3Shape[i].makeBoxUV(fWidthX, fWidthY, fHeight, new Vector3f(fOffsetX, fOffsetY, 0), v3Origin, fUVBaseX, fUVBaseX + (float)fWidthX*S1_BUFFER/texGen.TEX_WIDTH, fUVBaseY, fUVBaseY + (float)fWidthY*S1_BUFFER/texGen.TEX_WIDTH, fUVBaseHeight, fUVBaseHeight + (float)fHeight*S1_BUFFER/texGen.TEX_HEIGHT);
			
			s3Shape[i].createArray();
			s3Shape[i].setTexture(texGen.tFinalTex);
			s3Objects.add(s3Shape[i]);
			iVertsToRender += 24;
		}
	}
	
	/**
	 * Generates circular buildings
	 */
	private void genStyle2()
	{
		Vector3f v3Offset = new Vector3f(v3Origin);
		int iSegments = CIRCLE_SEGMENTS;
		float fSegmentAngle = 360/iSegments;
		boolean bSkippedI = false;
		
		// The random starting points on the texture, so buildings don't have the same textures
		float fUVStartX, fUVStartY;
		
		
		Shape3D s3BaseC = new Shape3D(GL_TRIANGLE_FAN);
		Shape3D s3TopC = new Shape3D(GL_TRIANGLE_FAN);
		Shape3D s3Cyl = new Shape3D(GL_QUADS);
		
		v3Offset = new Vector3f(v3Offset.getX() + iMaxWidthX*.5f, v3Offset.getY() + iMaxWidthY*.5f, v3Offset.getZ());
		
		int iBaseHeight = (int)(fDevLevel * S2_HEIGHT_MIN_MULTI) + rand.nextInt((int)(fDevLevel * S2_HEIGHT_MAX_MULTI));
		
		float fCircRadX = fDevLevel * S2_WIDTH_MIN_MULTI + rand.nextInt((int)(fDevLevel * S2_WIDTH_MAX_MULTI));
		float fCircRadY = fCircRadX; // In case I want other shapes, but I don't at the moment
		
		float fCircum = (float) (2*Math.PI*fCircRadX);
		
		if(iMaxWidthX <= fDevLevel * S2_WIDTH_MIN_MULTI)
		{
//			System.out.println("Max width is less than minimum width, setting max width to minimum width");
			iMaxWidthX = (int)(fDevLevel * S2_WIDTH_MIN_MULTI);
			iMaxWidthY = (int)(fDevLevel * S2_WIDTH_MIN_MULTI);
		}
		iMaxWidthX *= 0.5f;
		iMaxWidthY *= 0.5f;
		
		// Makes sure that the radius is within the specified width
		if(fCircRadX > iMaxWidthX)
		{
			while(fCircRadX > iMaxWidthX)
			{
				fCircRadX = fDevLevel * S2_WIDTH_MIN_MULTI + rand.nextInt((int)(fDevLevel * S2_WIDTH_MAX_MULTI));
				fCircRadY = fCircRadX;
			}
		}
		
		int iCuts = 0;
		
		// Have the center vert be in the center for triangle fan
		s3BaseC.addPoint(v3Offset.getX(), v3Offset.getY(), v3Offset.getZ());
		
		int[] iCutLoc = new int[CIRCLE_SEGMENTS];
		
		for(int i = 0; i < iSegments + 1; ++i)
		{
			float fCurRadAng = i * fSegmentAngle;
			float ch = rand.nextFloat();
			
			// If we want to, skip forward 90 degrees to vary the look of the buildings, but not if it's the first vert, cause...I'm not sure, but it doesn't work if it happens
			if(ch < S2_CIRCLE_CUT_CH && i != 0 && iCuts < S2_CIRCLE_MAX_CUTS)
			{
				
				if(iSegments - i >= 9 && i != 0)
				{
					iCutLoc[i] = 1;
					i += 9;
					fCurRadAng += 90;
					iCuts++;
				}
			}
			
			s3BaseC.addPoint(v3Offset.getX() + (float)Math.cos(fCurRadAng * DEG_RAD) * fCircRadX, v3Offset.getY() + (float)Math.sin(fCurRadAng * DEG_RAD) * fCircRadY, v3Offset.getZ());
		}
		
		s3BaseC.createArray();
		
		fUVStartX = randomInRange(0, texGen.TEX_WIDTH - fCircum);
		fUVStartY = randomInRange(0, texGen.TEX_HEIGHT - iBaseHeight);
		
		float fCSeg = (fCircum/iSegments)/texGen.TEX_WIDTH;
		
		for(int i = 1; i < s3BaseC.v3VertsA.length; ++i)
		{
			Vector3f v = s3BaseC.v3VertsA[i];
			s3TopC.addPoint(v.getX(), v.getY(), v.getZ() + iBaseHeight);
			
			if(i != 0)
			{
				if(s3BaseC.v3VertsA.length > i+1)
				{
					s3Cyl.addPoint(v.getX(), v.getY(), v.getZ() + iBaseHeight);
					s3Cyl.addPoint(v.getX(), v.getY(), v.getZ());
					s3Cyl.addPoint(s3BaseC.v3VertsA[i+1].getX(), s3BaseC.v3VertsA[i+1].getY(), s3BaseC.v3VertsA[i+1].getZ());
					s3Cyl.addPoint(s3BaseC.v3VertsA[i+1].getX(), s3BaseC.v3VertsA[i+1].getY(), s3BaseC.v3VertsA[i+1].getZ() + iBaseHeight);
					
					iVertsToRender += 4;
					
					// Then we cut 90 degrees here and need to change the textures
					if(i < iCutLoc.length)
					{
						if(iCutLoc[i] == 1)
						{
							bSkippedI = true;
						}
						else
						{
							bSkippedI = false;
						}
					}
										
					if(!bSkippedI) // if we didn't skip 90 degrees
					{								
						s3Cyl.addUVPoint(fUVStartX + (i-1)*fCSeg, fUVStartY);
						s3Cyl.addUVPoint(fUVStartX + (i-1)*fCSeg, fUVStartY + (float)iBaseHeight*S2_BUFFER/texGen.TEX_HEIGHT);
						s3Cyl.addUVPoint(fUVStartX + i*fCSeg, fUVStartY + (float)iBaseHeight*S2_BUFFER/texGen.TEX_HEIGHT);
						s3Cyl.addUVPoint(fUVStartX + i*fCSeg, fUVStartY);
						
//						s3Cyl.addUVPoint(0, 0);
//						s3Cyl.addUVPoint(0, 1);
//						s3Cyl.addUVPoint(1f, 1f);
//						s3Cyl.addUVPoint(1f, 0);
					}
					else
					{						
						s3Cyl.addUVPoint(fUVStartX + (i-1)*fCSeg, fUVStartY);
						s3Cyl.addUVPoint(fUVStartX + (i-1)*fCSeg, fUVStartY + (float)iBaseHeight*S2_BUFFER/texGen.TEX_HEIGHT);
						s3Cyl.addUVPoint(fUVStartX + (i+9)*fCSeg, fUVStartY + (float)iBaseHeight*S2_BUFFER/texGen.TEX_HEIGHT);
						s3Cyl.addUVPoint(fUVStartX + (i+9)*fCSeg, fUVStartY);
					}
					
				}
				
			}
			
		}
		
		s3TopC.createArray();
		s3Cyl.createArray();
		
		s3Cyl.setTexture(texGen.tFinalTex);
		s3TopC.setColor(Color.black);
		s3BaseC.setColor(Color.black);
		
		// Cylinder needs to be added first, or it doesn't use a texture for some stupid reason
		s3Objects.add(s3Cyl);
		s3Objects.add(s3BaseC);
		s3Objects.add(s3TopC);
		
		
	}
	
	/**
	 * Generates simple square
	 */
	private void genStyle3()
	{
		int iBaseHeight = (int)(S3_HEIGHT_MIN_MULTI * fDevLevel) + rand.nextInt((int)(fDevLevel * S3_HEIGHT_MAX_MULTI));
		int iBaseWidth = (int)(S3_WIDTH_MIN_MULTI * fDevLevel) + rand.nextInt((int)(S3_WIDTH_MAX_MULTI * fDevLevel));
		float fUVStartX, fUVStartY;
		Shape3D s3Shape = new Shape3D(GL_QUADS);
		
		// If the height isn't divisible by the window height, make it so by getting the divisor and rounding, then multiplying
		if(iBaseHeight % texGen.WIND_HEIGHT != 0)
			iBaseHeight = Math.round(iBaseHeight/texGen.WIND_HEIGHT)*texGen.WIND_HEIGHT;
			
		if(iBaseWidth % texGen.WIND_WIDTH != 0)
			iBaseWidth = Math.round(iBaseWidth / texGen.WIND_WIDTH)*texGen.WIND_WIDTH;
		
		if(iMaxWidthX <= iBaseWidth || iMaxWidthY <= iBaseWidth)
		{
//			System.out.println("Max Width X or Y is less than the base width, setting base width to max x width - 1");
			iBaseWidth = iMaxWidthX;
		}
		
		fUVStartX = (randomInRange(0, texGen.TEX_WIDTH - iBaseWidth));
		fUVStartY = (randomInRange(0, texGen.TEX_HEIGHT - iBaseHeight));
		
		s3Shape.makeBoxUV(iBaseWidth, iBaseWidth, iBaseHeight, null, v3Origin, fUVStartX, fUVStartX + (float)iBaseWidth*S3_BUFFER/texGen.TEX_WIDTH, fUVStartY, fUVStartY + (float)iBaseHeight*S3_BUFFER/texGen.TEX_HEIGHT);
		
		s3Shape.setTexture(texGen.tFinalTex);
		
		s3Objects.add(s3Shape);
		iVertsToRender += 24;
	}	

	public void render()
	{
		glPushMatrix();
		
		glColor3f(cColor.r, cColor.g, cColor.b);
		
		for(Shape3D s : s3Objects)
		{
			s.render();
		}
		glPopMatrix();
	}
	
	public Vector3f getOrigin()
	{ return v3Origin; }
	
	private float randomInRange(float min, float max)
	{
		return (float)Math.random() * (max-min) + min;
	}	
}