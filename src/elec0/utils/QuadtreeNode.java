package elec0.utils;

public class QuadtreeNode
{
	private boolean isDivided = false;
	public QuadtreeNode northWest, northEast, southWest, southEast;
	
	public int size;
	public Quad[][] items;
	
	public QuadtreeNode(int size)
	{
		this.size = size;
		items = new Quad[size][size];
		
		for(int x = 0; x < size; ++x)
			for(int y = 0; y < size; ++y)
				items[x][y] = new Quad();
	}
	
	public Quad[][] getItems()
	{
		Quad[][] returnItems = new Quad[size][size];

		returnItems = InitArrays.init2Array(returnItems);
		
		if(isDivided)
		{
			int sizeToCompare = (size/2);
					
			// start x and y are the size/2 per quadrant. Picking the quadrant
			returnItems = combineQuadArrays(0, 0, returnItems, northWest.getItems());
			returnItems = combineQuadArrays(sizeToCompare, 0, returnItems, northEast.getItems());
			returnItems = combineQuadArrays(0, sizeToCompare, returnItems, southWest.getItems());
			returnItems = combineQuadArrays(sizeToCompare, sizeToCompare, returnItems, southEast.getItems());
				
			return returnItems;
		}
		else
		{
			return items;
		}
	}
	
	private Quad[][] combineQuadArrays(int startX, int startY, Quad[][] primary, Quad[][] secondary)
	{
		// Looping through the secondary array, adding to the primary starting at the start x and y
		
		int curX = startX, curY = startY;
		
		if(secondary != null)
		{
		for(int x = 0; x < secondary.length - 1; ++x)
		{
			for(int y = 0; y < secondary[0].length - 1; ++y)
			{
				primary[curX][curY] = secondary[x][y];
				curY++;
			}
			curX++;
			curY = startY;
		}
		}
		return primary;
	}
	
	public void divide()
	{
		northWest = northEast = southWest = southEast = new QuadtreeNode(size/2+1);
		
		long dtime = Timing.getTime();
		
		// Place data into nodes
		int sizeToCompare = size/2;
		
		for(int x = 0; x < sizeToCompare; ++x)
		{
			for(int y = 0; y < sizeToCompare; ++y)
			{
				if(x <= sizeToCompare && y <= sizeToCompare) // In top-left quadrant
				{
					northWest.items[x][y] = items[x][y];
					
				}
				else if(x >= sizeToCompare && y <= sizeToCompare) // In top-right quadrant
				{
					northEast.items[x][y] = items[x+sizeToCompare][y];
				}
				else if(x <= sizeToCompare && y >= sizeToCompare) // In bottom-left quadrant
				{
					southWest.items[x][y] = items[x][y+sizeToCompare];
				}
				else if(x >= sizeToCompare && y >= sizeToCompare) // In bottom-right quadrant 
				{
					southEast.items[x][y] = items[x+sizeToCompare][y+sizeToCompare];
				}
				else
				{
					System.out.println("divide No criteria met: " + x + ", " + y);
				}
			}
		}
		
		System.out.println("Division finished: " + size + " TIME: " + (Timing.getTime() - dtime));
		isDivided = true;
		
		northWest.shouldDivide();
//		northEast.shouldDivide();
//		southWest.shouldDivide();
//		southEast.shouldDivide();
	}
	
	public void insertQuad(int indexX, int indexY, Quad quad)
	{
		items[indexX][indexY] = quad;
	}
	
	@SuppressWarnings("unused")
	private void optimize()
	{
		
	}
	
	public void shouldDivide() // See if we should divide the quadtree
	{
		
		long stime = Timing.getTime();
		
		float avgHeight = 0;
		int totalItems = (items.length + items[0].length)*4;
		
		for(int x = 0; x < items.length; ++x)
		{
			for(int y = 0; y < items[0].length; ++y)
			{
				if(items[x][y].topL != null)
				{
					avgHeight += items[x][y].topL.getY();
					avgHeight += items[x][y].topR.getY();
					avgHeight += items[x][y].botL.getY();
					avgHeight += items[x][y].botR.getY();
				}
			}
		}
		avgHeight = avgHeight/totalItems;
		System.out.println(size + " Average height: " + avgHeight);
		System.out.println(size + " End division checking TIME: " + (Timing.getTime() - stime));
		
		if(avgHeight > 2000) // 2000 default
		{
			divide();
		}		
	}
	
	public boolean isDivided()
	{return isDivided;}
}
