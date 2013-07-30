package elec0.utils;

public class InitArrays 
{
	
	public static Quad[][] init2Array(Quad[][] array)
	{
		for(int x = 0; x < array.length; ++x)
			for(int y = 0; y < array[0].length; ++y)
				array[x][y] = new Quad();
		
		return array;
	}
}
