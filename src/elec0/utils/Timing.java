package elec0.utils;

import org.lwjgl.Sys;

public class Timing 
{
	static public long getTime()
	{
		return (Sys.getTime() * 1000) / Sys.getTimerResolution();
	}
}
