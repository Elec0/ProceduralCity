package elec0.proceduralCity;

import elec0.utils.*;
import java.util.Random;

public class Grid 
{
	/* The grid is broken down into sections, which are broken into squares
	 * Grid has iGridSize^2 sections
	 * There are iSquareSize^2 squares in a section
	 * Grid -> Section -> Square
	 * 
	 * Grid size is irrelevant, sqrt(iSectionSize) will determine one side of the grid square
	 * 
	 * Square must follow (iSectionSize % iSquareSize == 0 && iSquareSize > iSectionSize)
	 * Which is to say all the things must fit equally and you're getting more as you go in farther
	 * 
	 * The grid doesn't store what buildings are where, only the development levels of each square.
	*/
	
	private int iSectionSize; // Number of sections in one axis of the grid
	private int iSquareSection; // Number of squares in a section
	private int iSquareSize; // Number of units in square
	
	private float[][] fLevels; // Levels of development
	private float fResIndex; // Residental index. The higher an index is the more that type of building shows up and the bigger it is
	private float fCorpIndex; // Index in range of 0..1
	private int iMaxCorpRad; // Max corp level radius
	private int iMaxResRad; // Max residental level radius
	
	public int iGridSize; // Total size, in squares, of one side of the grid
	
	private Random rand;
	
	// Constants
	private static final int LV_ROAD = 0; // Roads, which is where nothing is placed
	private static final int LV_MID = 1; // Small buildings 1 story
	private static final int LV_MIDH = 2; // Medium-high buildings 1-2 stories
	private static final int LV_HIGH = 3; // Larger buildings 1-4 stories
	private static final int LV_CORP = 4; // Skyscrapers
	
	private static final float RAD_CORP = 0.3f; // Set maximum amount of map allowed for corp and residental buildings
	private static final float RAD_RES = 0.5f;
	private int RAD_FALLOFF_CORP_MULT;
	private int RAD_FALLOFF_RES_MULT;
	
	
	// Defaults
	private static final int SIZE_SEC = 4;
	private static final int SIZE_SQR = 20; // Increases grid size. Larger numbers do not increase lag significantly
	private static final int SIZE_SQR_SEC = 20; // Num * 4 = sections per one side. Larger numbers lag more 
	private static final float INDX_RES = 0.4f; 
	private static final float INDX_CORP = 0.2f; 
	
	/**
	 * Default grid
	 */
	public Grid()
	{
		iSectionSize = SIZE_SEC;
		iSquareSize = SIZE_SQR;
		iSquareSection = SIZE_SQR_SEC;
		fResIndex = INDX_RES;
		fCorpIndex = INDX_CORP;
		
		init();
	}
	public Grid(int iSectionSize, int iSquareSection, int iSquareSize, float fResIndex, float fCorpIndex)
	{
		this.iSectionSize = iSectionSize;
		this.iSquareSection = iSquareSection;
		this.iSquareSize = iSquareSize;
		this.fResIndex = fResIndex;
		this.fCorpIndex = fCorpIndex;
		
		init();
	}
	
	private void init()
	{
		rand = new Random();
		fLevels = new float[iSquareSection*iSectionSize][iSquareSection*iSectionSize];
		iMaxCorpRad = (int)(RAD_CORP * (iSquareSection * iSectionSize)); // 30% of max map is largest possible corp radius
		iMaxResRad = (int)(RAD_RES * (iSquareSection * iSectionSize)); // 50% of max map is largest possible residental radius
		
		RAD_FALLOFF_CORP_MULT = (int) (iSquareSection * iSectionSize * INDX_CORP);
		RAD_FALLOFF_RES_MULT = (int) (iSquareSection * iSectionSize * INDX_RES);
		iGridSize = iSquareSection * iSectionSize;
	}
	
	public void generate()
	{
		Vector2i v2CorpCenter = new Vector2i((int)(fLevels.length * ((float)nextInt(4, 6) / 10)), (int)(fLevels[0].length * ((float)nextInt(4, 6) / 10))); // Picks a decimal x and y between .4 and .6 to be the center
		Vector2i v2ResidentalCenter = new Vector2i((int)(fLevels.length * ((float)nextIntNotInRange(0, 10, 4, 6) / 10)), (int)(fLevels[0].length * ((float)nextIntNotInRange(0, 10, 4, 6) / 10))); // Gets the center for residental, but not possible center in corp area
		
//		System.out.println(fLevels.length + " * " + ((float)nextInt(4, 6) / 10));
		
		for(int y = 0; y < fLevels[0].length; ++y)
		{
			for(int x = 0; x < fLevels.length; ++x)
			{
				// Set level of square, using linear falloff
				float fDistCorpCent = dist(x, y, v2CorpCenter.getX(), v2CorpCenter.getY());
				float fDistResCent = dist(x, y, v2ResidentalCenter.getX(), v2ResidentalCenter.getY());
				
				// Linear radial falloff
				float fCorpFalloffRate = LV_CORP * RAD_FALLOFF_CORP_MULT / fDistCorpCent;
				float fResFalloffRate = LV_HIGH * RAD_FALLOFF_RES_MULT / fDistResCent;
				
				// The distance is 0 if it's right on top of it, which causes a divide by zero
				if(fDistCorpCent == 0)
					fCorpFalloffRate = LV_CORP * RAD_FALLOFF_CORP_MULT;
				if(fDistResCent == 0)
					fDistResCent = LV_HIGH * RAD_FALLOFF_RES_MULT;
				
				// Limiting corp ranges
				if(fCorpFalloffRate < 1)
					fCorpFalloffRate = 1;
				if(fCorpFalloffRate > 4)
					fCorpFalloffRate = 4;
				// Limiting residential ranges
				if(fResFalloffRate < 1)
					fResFalloffRate = 1;
				if(fResFalloffRate > 3)
					fResFalloffRate = 3;
				
				// Picking which ever value is higher for the dev value. Might possibly want to interpolate these later, but it seems to be working fine for now
				if(fCorpFalloffRate > fResFalloffRate)
					fLevels[x][y] = fCorpFalloffRate;
				else
					fLevels[x][y] = fResFalloffRate;

				// Set roads
				// Have roads every 5 x and 8 y so we have close to the golden ratio
				float fRoadSkipX = 5;
				float fRoadSkipY = 8;

				if((x % fRoadSkipX == 0 || y % fRoadSkipY == 0) || (y == fLevels[0].length - 1 || x == fLevels.length - 1)) // If we need to place a road, and making sure there is a road on the end of the grid
				{
					fLevels[x][y] = 0;
				}				
			}
		}
		
	}
	
	/**
	 * Gets the construction level of index x and y
	 * @param x
	 * @param y
	 * @return 0 if x or y not valid.
	 */
	public float getLevel(int x, int y)
	{
		if(x < fLevels.length && y < fLevels[0].length)
				return fLevels[x][y];
		
		System.out.println("ERROR: " + x + ", " + y + " (" + fLevels.length + ", " + fLevels[0].length + ")");
		return 0;
	}
	
	public int getSquareSize()
	{ return iSquareSize; }
	public int getSquareSection()
	{ return iSquareSection; }
	public int getSectionSize()
	{ return iSectionSize; }
	
	/**
	 * Gets a random number between a and b
	 * @param a
	 * @param b
	 * @return
	 */
	private int nextInt(int a, int b)
	{
		return a + rand.nextInt(b);
	}
	
	/**
	 * Returns random number between a and b but not between la and lb
	 * @param a
	 * @param b
	 * @param la
	 * @param lb
	 * @return
	 */
	private int nextIntNotInRange(int a, int b, int la, int lb)
	{
		while(true)
		{
			int temp = nextInt(a, b);
			if(temp < la || temp > lb)
				return temp;
		}
	}
	
	/**
	 * Returns the distance between the two points
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return
	 */
	private float dist(int x1, int y1, int x2, int y2)
	{
		return (float)Math.sqrt((x2 - x1)*(x2 - x1) + (y2 - y1) * (y2 - y1));
	}
}
