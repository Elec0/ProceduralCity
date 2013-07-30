package elec0.proceduralCity;

import static org.lwjgl.opengl.GL11.*;

import java.util.ArrayList;

import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.Texture;

import elec0.utils.Timing;
import elec0.utils.Vector2f;
import elec0.utils.Vector3f;

@SuppressWarnings("unchecked")
public class Shape3D 
{
	public int iRenderType;
	public ArrayList<Vector3f> v3Verts;
	public ArrayList<Vector2f> v2UV;
	
	public Vector3f[] v3VertsA;
	public Vector2f[] v2UVA;
	
	private Texture tTex;
	private static Texture tBlack;
	private boolean bUseTexture = false;
	private Color cCol;
	
	private boolean bArrayUpdated = false;
	
	public Shape3D()
	{ init(); }
	/**
	 * For use in cloning, prove a Shape3D and it will copy everything over
	 * @param s3Shape
	 */
	public Shape3D(Shape3D s3Shape)
	{
		init();
		this.iRenderType = s3Shape.iRenderType;
		this.v3Verts = s3Shape.v3Verts;
		this.v2UV = s3Shape.v2UV;
		this.v3VertsA = s3Shape.v3VertsA;
		this.v2UVA = s3Shape.v2UVA;
	}
	public Shape3D(int iRenderType)
	{ 
		init();
		this.iRenderType = iRenderType; 
	}
	/**
	 * If you provide Shape3D with an ArrayList of points, it assumes that it is the completed list. If you edit it, you must recall createArray
	 * @param iRenderType
	 * @param v3Verts
	 */
	public Shape3D(int iRenderType, ArrayList<Vector3f> v3Verts)
	{ 
		init();
		this.iRenderType = iRenderType; 
		this.v3Verts = (ArrayList<Vector3f>) v3Verts.clone();
		createArray();
	}
	public Shape3D(int iRenderType, ArrayList<Vector3f> v3Verts, ArrayList<Vector2f> v2UV)
	{ 
		init();
		this.iRenderType = iRenderType; 
		this.v3Verts = (ArrayList<Vector3f>) v3Verts.clone(); 
		this.v2UV = (ArrayList<Vector2f>) v2UV.clone(); 
		createArray();
	}
	
	private void init()
	{
		if(tBlack == null)
			tBlack = TextureGenerator.generateBlack();
		v3Verts = new ArrayList<Vector3f>();
		v2UV = new ArrayList<Vector2f>();
		cCol = Color.black; // Default color
	}
	
	/**
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	public void addPoint(float x, float y, float z)
	{
		bArrayUpdated = false;
		v3Verts.add(new Vector3f(x, y, z));
	}
	public void addPoint(Vector3f point)
	{
		bArrayUpdated = false;
		v3Verts.add(point);
	}
	
	/**
	 * 
	 * @param u
	 * @param v
	 */
	public void addUVPoint(float u, float v)
	{
		bArrayUpdated = false;
		v2UV.add(new Vector2f(u, v));
	}
	public void addUVPoint(Vector2f point)
	{
		bArrayUpdated = false;
		v2UV.add(point);
	}
	

	
	public void createArray()
	{
		int i = 0;
		bArrayUpdated = true;
		
		if(v3Verts != null)
		{
			v3VertsA = new Vector3f[v3Verts.size()];
			
			for(Vector3f v : v3Verts)
			{
				v3VertsA[i] = new Vector3f(v);
				i++;
			}
		}
		else
			System.out.println("Shape3D error: You must pass vertices into the Shape3D");
		
		i = 0;
		if(v2UV != null)
		{
			v2UVA = new Vector2f[v2UV.size()];
			
			for(Vector2f v : v2UV)
			{
				v2UVA[i] = new Vector2f(v);
				i++;
			}
		}
		
	}
	long lTimeTest;
	public void render()
	{
		if(!bArrayUpdated)
			createArray();
		
		glPushMatrix();
		
		
		if(bUseTexture)
		{
			glEnable(GL_TEXTURE_2D);
			if(tTex != null)
				tTex.bind();
			else
				System.out.println("Shape3D: Trying to use texture, but tex is null");
		}
		else
		{
			glDisable(GL_TEXTURE_2D);
			glColor3f(cCol.r, cCol.g, cCol.b);
			
		}
		
		
		glBegin(iRenderType);
		for(int i = 0; i < v3VertsA.length; ++i)
		{
			if(v2UVA.length > 0)
			{
				if(i < v2UVA.length)
				{
					if(bUseTexture)
					{
						glTexCoord2f(v2UVA[i].getX(), v2UVA[i].getY());
					}
				}
				else
				{
					if(bUseTexture)
					{
						tBlack.bind();
						glTexCoord2f(0, 0);
					}
				}
			}
			
			// Opengl uses a different coordinate system than I'm used to, so I switch Y and Z when I render.
			glVertex3f(v3VertsA[i].getX(), v3VertsA[i].getZ(), v3VertsA[i].getY());
			
		}
		glEnd();
		glDisable(GL_TEXTURE_2D);
		
		glPopMatrix();
	}
	
	
	/**
	 * Generates a cube/rectangle with given parameters. Requires GL_QUADS to be rendermode
	 * @param fXWidth
	 * @param fYWidth
	 * @param fHeight
	 * @param v3Start
	 */
	public void makeBox(float fXWidth, float fYWidth, float fHeight, Vector3f v3Start, Vector3f v3Origin)
	{
		Vector3f v3Offset = new Vector3f(v3Origin);
		if(v3Start != null)
			v3Offset.addTo(v3Start);
		
		// Bottom
		v3Verts.add(new Vector3f(v3Offset.getX(), v3Offset.getY(), v3Offset.getZ()));
		v3Verts.add(new Vector3f(v3Offset.getX() + fXWidth, v3Offset.getY(), v3Offset.getZ()));
		v3Verts.add(new Vector3f(v3Offset.getX() + fXWidth, v3Offset.getY() + fYWidth, v3Offset.getZ()));
		v3Verts.add(new Vector3f(v3Offset.getX(), v3Offset.getY() + fYWidth, v3Offset.getZ()));
		
		
		v3Verts.add(new Vector3f(v3Offset.getX(), v3Offset.getY(), v3Offset.getZ()));
		v3Verts.add(new Vector3f(v3Offset.getX() + fXWidth, v3Offset.getY(), v3Offset.getZ()));
		v3Verts.add(new Vector3f(v3Offset.getX() + fXWidth, v3Offset.getY(), v3Offset.getZ() + fHeight));
		v3Verts.add(new Vector3f(v3Offset.getX(), v3Offset.getY(), v3Offset.getZ() + fHeight));
		
		v3Verts.add(new Vector3f(v3Offset.getX(), v3Offset.getY() + fYWidth, v3Offset.getZ()));
		v3Verts.add(new Vector3f(v3Offset.getX() + fXWidth, v3Offset.getY() + fYWidth, v3Offset.getZ()));
		v3Verts.add(new Vector3f(v3Offset.getX() + fXWidth, v3Offset.getY() + fYWidth, v3Offset.getZ() + fHeight));
		v3Verts.add(new Vector3f(v3Offset.getX(), v3Offset.getY() + fYWidth, v3Offset.getZ() + fHeight));
		
		v3Verts.add(new Vector3f(v3Offset.getX(), v3Offset.getY(), v3Offset.getZ()));
		v3Verts.add(new Vector3f(v3Offset.getX(), v3Offset.getY() + fYWidth, v3Offset.getZ()));
		v3Verts.add(new Vector3f(v3Offset.getX(), v3Offset.getY() + fYWidth, v3Offset.getZ() + fHeight));
		v3Verts.add(new Vector3f(v3Offset.getX(), v3Offset.getY(), v3Offset.getZ() + fHeight));
		
		v3Verts.add(new Vector3f(v3Offset.getX() + fXWidth, v3Offset.getY(), v3Offset.getZ()));
		v3Verts.add(new Vector3f(v3Offset.getX() + fXWidth, v3Offset.getY() + fYWidth, v3Offset.getZ()));
		v3Verts.add(new Vector3f(v3Offset.getX() + fXWidth, v3Offset.getY() + fYWidth, v3Offset.getZ() + fHeight));
		v3Verts.add(new Vector3f(v3Offset.getX() + fXWidth, v3Offset.getY(), v3Offset.getZ() + fHeight));
		
		// Top
		v3Verts.add(new Vector3f(v3Offset.getX(), v3Offset.getY(), v3Offset.getZ() + fHeight));
		v3Verts.add(new Vector3f(v3Offset.getX() + fXWidth, v3Offset.getY(), v3Offset.getZ() + fHeight));
		v3Verts.add(new Vector3f(v3Offset.getX() + fXWidth, v3Offset.getY() + fYWidth, v3Offset.getZ() + fHeight));
		v3Verts.add(new Vector3f(v3Offset.getX(), v3Offset.getY() + fYWidth, v3Offset.getZ() + fHeight));
	}
	
	
	public void makeBoxUV(float fXWidth, float fYWidth, float fHeight, Vector3f v3Start, Vector3f v3Origin, float fMinUVX, float fMaxUVX, float fMinUVY, float fMaxUVY)
	{
		Vector3f v3Offset = new Vector3f(v3Origin);
		if(v3Start != null)
			v3Offset.addTo(v3Start);
		
		// Front
		v3Verts.add(new Vector3f(v3Offset.getX(), v3Offset.getY(), v3Offset.getZ()));
		v3Verts.add(new Vector3f(v3Offset.getX() + fXWidth, v3Offset.getY(), v3Offset.getZ()));
		v3Verts.add(new Vector3f(v3Offset.getX() + fXWidth, v3Offset.getY(), v3Offset.getZ() + fHeight));
		v3Verts.add(new Vector3f(v3Offset.getX(), v3Offset.getY(), v3Offset.getZ() + fHeight));
		
			v2UV.add(new Vector2f(fMinUVX, fMinUVY));
			v2UV.add(new Vector2f(fMaxUVX, fMinUVY));
			v2UV.add(new Vector2f(fMaxUVX, fMaxUVY));
			v2UV.add(new Vector2f(fMinUVX, fMaxUVY));
		
		// Back
		v3Verts.add(new Vector3f(v3Offset.getX(), v3Offset.getY() + fYWidth, v3Offset.getZ()));
		v3Verts.add(new Vector3f(v3Offset.getX() + fXWidth, v3Offset.getY() + fYWidth, v3Offset.getZ()));
		v3Verts.add(new Vector3f(v3Offset.getX() + fXWidth, v3Offset.getY() + fYWidth, v3Offset.getZ() + fHeight));
		v3Verts.add(new Vector3f(v3Offset.getX(), v3Offset.getY() + fYWidth, v3Offset.getZ() + fHeight));
		
			v2UV.add(new Vector2f(fMinUVX, fMinUVY));
			v2UV.add(new Vector2f(fMaxUVX, fMinUVY));
			v2UV.add(new Vector2f(fMaxUVX, fMaxUVY));
			v2UV.add(new Vector2f(fMinUVX, fMaxUVY));
		
		// Left
		v3Verts.add(new Vector3f(v3Offset.getX(), v3Offset.getY(), v3Offset.getZ()));
		v3Verts.add(new Vector3f(v3Offset.getX(), v3Offset.getY() + fYWidth, v3Offset.getZ()));
		v3Verts.add(new Vector3f(v3Offset.getX(), v3Offset.getY() + fYWidth, v3Offset.getZ() + fHeight));
		v3Verts.add(new Vector3f(v3Offset.getX(), v3Offset.getY(), v3Offset.getZ() + fHeight));
		
			v2UV.add(new Vector2f(fMinUVX, fMinUVY));
			v2UV.add(new Vector2f(fMaxUVX, fMinUVY));
			v2UV.add(new Vector2f(fMaxUVX, fMaxUVY));
			v2UV.add(new Vector2f(fMinUVX, fMaxUVY));
		
		// Right
		v3Verts.add(new Vector3f(v3Offset.getX() + fXWidth, v3Offset.getY(), v3Offset.getZ()));
		v3Verts.add(new Vector3f(v3Offset.getX() + fXWidth, v3Offset.getY() + fYWidth, v3Offset.getZ()));
		v3Verts.add(new Vector3f(v3Offset.getX() + fXWidth, v3Offset.getY() + fYWidth, v3Offset.getZ() + fHeight));
		v3Verts.add(new Vector3f(v3Offset.getX() + fXWidth, v3Offset.getY(), v3Offset.getZ() + fHeight));
		
			v2UV.add(new Vector2f(fMinUVX, fMinUVY));
			v2UV.add(new Vector2f(fMaxUVX, fMinUVY));
			v2UV.add(new Vector2f(fMaxUVX, fMaxUVY));
			v2UV.add(new Vector2f(fMinUVX, fMaxUVY));
		
		/* For some strange reason, when I choose random texture coords for the top and bottom everything freaks out, so if I just don't specify them, but have them last
		* it just doesn't assign texcoords to them, which is what I want, mainly
		* This might be because of how I assigned texture coords, which I fixed.
		*/
		// Top
		v3Verts.add(new Vector3f(v3Offset.getX(), v3Offset.getY(), v3Offset.getZ() + fHeight));
		v3Verts.add(new Vector3f(v3Offset.getX() + fXWidth, v3Offset.getY(), v3Offset.getZ() + fHeight));
		v3Verts.add(new Vector3f(v3Offset.getX() + fXWidth, v3Offset.getY() + fYWidth, v3Offset.getZ() + fHeight));
		v3Verts.add(new Vector3f(v3Offset.getX(), v3Offset.getY() + fYWidth, v3Offset.getZ() + fHeight));
		
			v2UV.add(new Vector2f(0, 0));
			v2UV.add(new Vector2f(0, 0));
			v2UV.add(new Vector2f(0, 0));
			v2UV.add(new Vector2f(0, 0));
			
		// Bottom
		v3Verts.add(new Vector3f(v3Offset.getX(), v3Offset.getY(), v3Offset.getZ()));
		v3Verts.add(new Vector3f(v3Offset.getX() + fXWidth, v3Offset.getY(), v3Offset.getZ()));
		v3Verts.add(new Vector3f(v3Offset.getX() + fXWidth, v3Offset.getY() + fYWidth, v3Offset.getZ()));
		v3Verts.add(new Vector3f(v3Offset.getX(), v3Offset.getY() + fYWidth, v3Offset.getZ()));
		
			v2UV.add(new Vector2f(0, 0));
			v2UV.add(new Vector2f(0, 0));
			v2UV.add(new Vector2f(0, 0));
			v2UV.add(new Vector2f(0, 0));
	}
	
	public void makeBoxUV(float fXWidth, float fYWidth, float fHeight, Vector3f v3Start, Vector3f v3Origin, float fMinUVX, float fMaxUVX, float fMinUVY, float fMaxUVY, float fMinUVHeight, float fMaxUVHeight)
	{
		Vector3f v3Offset = new Vector3f(v3Origin);
		if(v3Start != null)
			v3Offset.addTo(v3Start);
		
		// Front
		v3Verts.add(new Vector3f(v3Offset.getX(), v3Offset.getY(), v3Offset.getZ()));
		v3Verts.add(new Vector3f(v3Offset.getX() + fXWidth, v3Offset.getY(), v3Offset.getZ()));
		v3Verts.add(new Vector3f(v3Offset.getX() + fXWidth, v3Offset.getY(), v3Offset.getZ() + fHeight));
		v3Verts.add(new Vector3f(v3Offset.getX(), v3Offset.getY(), v3Offset.getZ() + fHeight));
		
			v2UV.add(new Vector2f(fMinUVX, fMinUVHeight));
			v2UV.add(new Vector2f(fMaxUVX, fMinUVHeight));
			v2UV.add(new Vector2f(fMaxUVX, fMaxUVHeight));
			v2UV.add(new Vector2f(fMinUVX, fMaxUVHeight));
		
		// Back
		v3Verts.add(new Vector3f(v3Offset.getX(), v3Offset.getY() + fYWidth, v3Offset.getZ()));
		v3Verts.add(new Vector3f(v3Offset.getX() + fXWidth, v3Offset.getY() + fYWidth, v3Offset.getZ()));
		v3Verts.add(new Vector3f(v3Offset.getX() + fXWidth, v3Offset.getY() + fYWidth, v3Offset.getZ() + fHeight));
		v3Verts.add(new Vector3f(v3Offset.getX(), v3Offset.getY() + fYWidth, v3Offset.getZ() + fHeight));
		
			v2UV.add(new Vector2f(fMinUVX, fMinUVHeight));
			v2UV.add(new Vector2f(fMaxUVX, fMinUVHeight));
			v2UV.add(new Vector2f(fMaxUVX, fMaxUVHeight));
			v2UV.add(new Vector2f(fMinUVX, fMaxUVHeight));
		
		// Left
		v3Verts.add(new Vector3f(v3Offset.getX(), v3Offset.getY(), v3Offset.getZ()));
		v3Verts.add(new Vector3f(v3Offset.getX(), v3Offset.getY() + fYWidth, v3Offset.getZ()));
		v3Verts.add(new Vector3f(v3Offset.getX(), v3Offset.getY() + fYWidth, v3Offset.getZ() + fHeight));
		v3Verts.add(new Vector3f(v3Offset.getX(), v3Offset.getY(), v3Offset.getZ() + fHeight));
		
			v2UV.add(new Vector2f(fMinUVY, fMinUVHeight));
			v2UV.add(new Vector2f(fMaxUVY, fMinUVHeight));
			v2UV.add(new Vector2f(fMaxUVY, fMaxUVHeight));
			v2UV.add(new Vector2f(fMinUVY, fMaxUVHeight));
		
		// Right
		v3Verts.add(new Vector3f(v3Offset.getX() + fXWidth, v3Offset.getY(), v3Offset.getZ()));
		v3Verts.add(new Vector3f(v3Offset.getX() + fXWidth, v3Offset.getY() + fYWidth, v3Offset.getZ()));
		v3Verts.add(new Vector3f(v3Offset.getX() + fXWidth, v3Offset.getY() + fYWidth, v3Offset.getZ() + fHeight));
		v3Verts.add(new Vector3f(v3Offset.getX() + fXWidth, v3Offset.getY(), v3Offset.getZ() + fHeight));
		
			v2UV.add(new Vector2f(fMinUVY, fMinUVHeight));
			v2UV.add(new Vector2f(fMaxUVY, fMinUVHeight));
			v2UV.add(new Vector2f(fMaxUVY, fMaxUVHeight));
			v2UV.add(new Vector2f(fMinUVY, fMaxUVHeight));
		
		/* For some strange reason, when I choose random texture coords for the top and bottom everything freaks out, so if I just don't specify them, but have them last
		* it just doesn't assign texcoords to them, which is what I want, mainly
		* This might be because of how I assigned texture coords, which I fixed.
		*/
		// Top
		v3Verts.add(new Vector3f(v3Offset.getX(), v3Offset.getY(), v3Offset.getZ() + fHeight));
		v3Verts.add(new Vector3f(v3Offset.getX() + fXWidth, v3Offset.getY(), v3Offset.getZ() + fHeight));
		v3Verts.add(new Vector3f(v3Offset.getX() + fXWidth, v3Offset.getY() + fYWidth, v3Offset.getZ() + fHeight));
		v3Verts.add(new Vector3f(v3Offset.getX(), v3Offset.getY() + fYWidth, v3Offset.getZ() + fHeight));
		
			v2UV.add(new Vector2f(0, 0));
			v2UV.add(new Vector2f(0, 0));
			v2UV.add(new Vector2f(0, 0));
			v2UV.add(new Vector2f(0, 0));
			
		// Bottom
		v3Verts.add(new Vector3f(v3Offset.getX(), v3Offset.getY(), v3Offset.getZ()));
		v3Verts.add(new Vector3f(v3Offset.getX() + fXWidth, v3Offset.getY(), v3Offset.getZ()));
		v3Verts.add(new Vector3f(v3Offset.getX() + fXWidth, v3Offset.getY() + fYWidth, v3Offset.getZ()));
		v3Verts.add(new Vector3f(v3Offset.getX(), v3Offset.getY() + fYWidth, v3Offset.getZ()));
		
			v2UV.add(new Vector2f(0, 0));
			v2UV.add(new Vector2f(0, 0));
			v2UV.add(new Vector2f(0, 0));
			v2UV.add(new Vector2f(0, 0));
	}
	
	public void setTexture(Texture tex)
	{
		tTex = tex;
		bUseTexture = true;
	}
	public void setColor(Color newCol)
	{
		cCol = newCol;
	}
}
