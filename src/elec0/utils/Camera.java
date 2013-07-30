package elec0.utils;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.*;

public class Camera 
{
    public Vector3f Position = new Vector3f();
    private Vector3f ViewDir = new Vector3f();
    
    private float RotatedX, RotatedY;
    private boolean ViewDirChanged;
    
    public float fov, aspectRatio, nearPlane, farPlane;
    private float fRenderFOV;
    private float fCosFOV;

    public void initCamera()
    {
        Position = new Vector3f(0, 5, 5);
        ViewDir = new Vector3f(0, 0, -1);
        
        RotatedX = RotatedY = 0.0f;
        ViewDirChanged = true;
        
        fov = 90f;
        fRenderFOV = 200f;
        aspectRatio = 1.0f;
        nearPlane = 0.2f;
        farPlane = 200.0f;
        
        fCosFOV= (float)Math.cos(Math.toRadians(fRenderFOV/2f));

    }

    public void initPerspective()
    {
        glMatrixMode(GL_PROJECTION); // Select The Projection Matrix
        glLoadIdentity(); // Reset The Projection Matrix
        gluPerspective(fov, aspectRatio, nearPlane, farPlane);
    }
    
    public void init2DPerspective()
    {
    	 glMatrixMode(GL_PROJECTION);
    	 glLoadIdentity();
    	 glOrtho(0, 800, 600, 0, 0, 1);
    	 glMatrixMode(GL_MODELVIEW);
    }
    
    public void render()
    {
        glRotatef(-RotatedX, 1.0f, 0.0f, 0.0f);
        glRotatef(-RotatedY, 0.0f, 1.0f, 0.0f);
        glTranslatef(-Position.getX(), -Position.getY(), -Position.getZ());
        
    }
    
    private void GetViewDir()
    {
        Vector3f Step1 = new Vector3f(), Step2 = new Vector3f();

        //Rotate around Y-axis:
        Step1.setX((float)Math.cos((RotatedY + 90.0) * (Math.PI / 180f)));
        Step1.setZ((float)-Math.sin((RotatedY + 90.0) * (Math.PI / 180f)));

        //Rotate around X-axis:
        double cosX = (float)Math.cos(RotatedX * (Math.PI / 180f));
        Step2.setX((float)(Step1.getX() * cosX));
        Step2.setZ((float)(Step1.getZ() * cosX));
        Step2.setY((float)Math.sin((RotatedX) * (Math.PI / 180f))); // Limit rotating to X and Y planes, no flying around

        //Rotation around Z-axis not yet implemented, so:
        ViewDir = Step2;
    }
    
    public Vector3f AddVector3fs(Vector3f first, Vector3f second)
    {
        Vector3f result = new Vector3f();
        result.setX(first.getX() + second.getX());
        result.setY(first.getY() + second.getY());
        result.setZ(first.getZ() + second.getZ());
        return result;
    }

    public Vector3f AddToVector3f(Vector3f first, Vector3f second)
    {
        first.addTo(second);
        return first;
    }
    
    public void Move(Vector3f Direction)
    {Position = AddToVector3f(Position, Direction);}
    
    public void Move(float x, float y, float z)
    {
        Vector3f Dir = new Vector3f(x, y, z);
        Position = AddToVector3f(Position,  Dir);
    }

    public void MoveTo(Vector3f Pos)
    {Position = Pos;}

    public void MoveTo(float x, float y, float z)
    {
        Vector3f pos = new Vector3f(x, y, z);
        Position = pos;
    }

    public void Rotate(float aX, float aY, float aZ)
    {
    	RotatedX += aX;
    	RotatedY += aY;
    	
    	if(Math.abs(RotatedX) > 90)
    		RotatedX -= aX;
    	ViewDirChanged = true;
    	GetViewDir();
    }
    public void addRotate(float aX, float aY)
    {
    	RotatedX += aX;
    	RotatedY += aY;
    }
    public float getRotateX()
    {return RotatedX;}
    public float getRotateY()
    {return RotatedY;}
    
    public void MoveForwards(float Distance)
    {
        if (ViewDirChanged) GetViewDir();
        
        Vector3f MoveVector = new Vector3f();
        MoveVector.setX(ViewDir.getX() * Distance);
        MoveVector.setY(ViewDir.getY() * Distance);
        MoveVector.setZ(ViewDir.getZ() * Distance);
        Move(MoveVector);        
    }

    public void MoveRight(float Distance)
    {
        if (ViewDirChanged) GetViewDir();
        Vector3f MoveVector = new Vector3f();
        MoveVector.setZ(-ViewDir.getX() * -Distance);
        MoveVector.setY(0.0f);
        MoveVector.setX(ViewDir.getZ() * -Distance);
//        Position = AddToVector3f(Position,  MoveVector);
        Move(MoveVector);
    }
    
    public void moveUp(float distance)
    {
    	if(ViewDirChanged) GetViewDir();
    	Vector3f moveVector = new Vector3f(0, distance, 0);
    	Move(moveVector);
    }
    
    /**
     * Tests a point in view of the camera
     * @param v2Point Vector2f Location of the point to test
     * @param angle half of FOV
     * @return
     */
    public boolean pointInAngle(Vector2f v2Point)
    {
    	// use viewdir instead of computing it myself
    	Vector2f v2View = new Vector2f(ViewDir.getX(), ViewDir.getZ());
    	Vector2f v2ToPoint = new Vector2f(v2Point.getX()-Position.getX(), v2Point.getY() - Position.getZ());
    	
    	v2View.normalize();
    	v2ToPoint.normalize();
    	
    	float fAngle = Vector2f.dotProduct(v2View, v2ToPoint);
    	
    	if(fAngle >= fCosFOV)
    		return true;
    	else
    		return false;
    }
}
