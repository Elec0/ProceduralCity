package elec0.utils;

import static org.lwjgl.opengl.GL11.*;
public class BezierCurve
{
	private double[] lx, ly;
	private float resolution = .005f;
	 
	public BezierCurve()
	{}
	public BezierCurve(int[] x, int[] y)
	{
		initCurve(x, y);
	}
	
	/**
	 * Takes (I think) up to but no more than 4 control poins
	 * @param x int array of x-coord control points
	 * @param y int array of y-coord control points
	 */
	public void initCurve(int[] x, int[] y)
	{
	   
	    lx = new double[(int) ((1/resolution)+1)];
	    ly = new double[lx.length];
	    
	 
	    int curPos = 0;
	    
	    for (double t = 0; t < 1; t += resolution)
	    {
			double xt = Math.pow(1-t, 3) * x[0] + 3 * t * Math.pow (1-t, 2) * x[1] +
				    3 * Math.pow (t, 2) * (1-t) * x[2] + Math.pow (t, 3) * x[3];
		 
			double yt = Math.pow (1-t, 3) * y[0] + 3 * t * Math.pow (1-t, 2) * y[1] +
				    3 * Math.pow (t, 2) * (1-t) * y[2] + Math.pow (t, 3) * y[3];
			
			lx[curPos] = xt;
			ly[curPos] = yt;
			
			++curPos;
	    }
	}
	
	public void drawCurve(float base)
	{
		glColor3f(1f, 1f, 1f);
		glBegin(GL_QUADS);
		
		    for(int i = 0; i < lx.length-1; i+=1)
		    {
		    	// For use in drawing the curve at the top, then drawing a solid polygon to make it look good
		    	
	    		glVertex3d(lx[i], ly[i], 0);
	    		glVertex3d(lx[i+1], ly[i+1], 0);
	    		
	    		glVertex3d(lx[i+1], base, 0);
	    		glVertex3d(lx[i], base, 0);
	    	
		    }
	    
	    glEnd();
	}
}