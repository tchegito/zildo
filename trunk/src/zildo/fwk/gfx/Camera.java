package zildo.fwk.gfx;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

public class Camera {
	
	Vector3f Position;
	Vector3f ViewDir;		/*Not used for rendering the camera, but for "moveforwards"
							So it is not necessary to "actualize" it always. It is only
							actualized when ViewDirChanged is true and moveforwards is called*/
	boolean ViewDirChanged;
	float RotatedX, RotatedY, RotatedZ;	

	public Vector3f AddF3dVectors (Vector3f u, Vector3f v)
	{
		Vector3f result=new Vector3f(u.x + v.x, u.y + v.y, u.z + v.z);
		return result;
	}
	void AddF3dVectorToVector ( Vector3f Dst, Vector3f V2)
	{
		Dst.x += V2.x;
		Dst.y += V2.y;
		Dst.z += V2.z;
	}
	
	
	/***************************************************************************************/
	
	public Camera()
	{
		//Init with standard OGL values:
		Position = new Vector3f (	0.0f, 
								0.0f,
								0.0f);
		ViewDir = new Vector3f(	0.0f,
								0.0f,
								1.0f);
		ViewDirChanged = false;
		//Only to be sure:
		RotatedX = RotatedY = RotatedZ = 0.0f;
	}
	
	void GetViewDir()
	{
		Vector3f Step1=new Vector3f(0,0,0);
		Vector3f Step2=new Vector3f(0,0,0);
		//Rotate around Y-axis:
		Step1.x = (float) Math.cos( (RotatedY + 90.0f) * Math.PI / 180.0f);
		Step1.z = (float) -Math.sin( (RotatedY + 90.0f) * Math.PI / 180.0f);
		//Rotate around X-axis:
		float cosX = (float) Math.cos (RotatedX * Math.PI / 180.0f);
		Step2.x = Step1.x * cosX;
		Step2.z = Step1.z * cosX;
		Step2.y = (float) Math.sin(RotatedX * Math.PI / 180.0f);
		//Rotation around Z-axis not yet implemented, so:
		ViewDir = Step2;
	}
	public void move (Vector3f Direction)
	{
		AddF3dVectorToVector(Position, Direction );
		
	}
	
	public void place(Vector3f direction) {
		Position = direction;
	}
	
	public void rotateY (float Angle)
	{
		RotatedY += Angle;
		ViewDirChanged = true;
	}
	
	public void rotateX (float Angle)
	{
		RotatedX += Angle;
		ViewDirChanged = true;
	}
	
	public void rotateZ (float Angle)
	{
		RotatedZ += Angle;
		ViewDirChanged = true;
	}
	
	public void render()
	{
		GL11.glRotatef(-RotatedX , 1.0f, 0.0f, 0.0f);
		GL11.glRotatef(-RotatedY , 0.0f, 1.0f, 0.0f);
		GL11.glRotatef(-RotatedZ , 0.0f, 0.0f, 1.0f);
		GL11.glTranslatef( -Position.x, -Position.y, -Position.z );
	}
	
	public void moveForwards( float Distance )
	{
		if (ViewDirChanged) GetViewDir();
		Vector3f MoveVector=new Vector3f(0,0,0);
		MoveVector.x = ViewDir.x * -Distance;
		MoveVector.y = ViewDir.y * -Distance;
		MoveVector.z = ViewDir.z * -Distance;
		AddF3dVectorToVector(Position, MoveVector );
	}
	
	public void strafeRight ( float Distance )
	{
		if (ViewDirChanged) GetViewDir();
		Vector3f MoveVector=new Vector3f(0,0,0);
		MoveVector.z = -ViewDir.x * -Distance;
		MoveVector.y = 0.0f;
		MoveVector.x = ViewDir.z * -Distance;
		AddF3dVectorToVector(Position, MoveVector );
	}
}