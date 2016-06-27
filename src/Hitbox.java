abstract class Hitbox
{
	int /*group,*/ xCoord, yCoord, xHosh, yHosh, width, height;
	int xVel, yVel, xDir, yDir, xDrag, yDrag, speed;
	boolean isFloating, isGrounded, isMovable, isMoving, wasMoving;
	
	public Hitbox()
	{
	//	group = -1;
		xCoord = 0;
		yCoord = 0;
		xHosh = xCoord;
		yHosh = yCoord;
		width = 0;
		height = 0;
		
		xVel = 0;
		yVel = 0;
		xDir = 0;
		yDir = 0;
		xDrag = 0;
		yDrag = 0;
		speed = 0;
		
		isFloating = false;
		isGrounded = false;
		isMovable = false;
		isMoving = false;
		wasMoving = false;
	}
	
	public Hitbox(int x, int y, int w, int h)
	{
	//	group = -1;
		xCoord = x;
		yCoord = y;
		xHosh = xCoord;
		yHosh = yCoord;
		width = w;
		height = h;
		
		xVel = 0;
		yVel = 0;
		xDir = 0;
		yDir = 0;
		xDrag = 0;
		yDrag = 0;
		speed = 0;
		
		isFloating = false;
		isGrounded = false;
		isMovable = false;
		isMoving = false;
		wasMoving = false;
	}
	
	public void update(int x1, int y1, int x2, int y2, int x3, int y3, int s)
	{
		xVel = x1;
		yVel = y1;
		xDir = x2;
		yDir = y2;
		xDrag = x3;
		yDrag = y3;
		speed = s;
		
		if(xVel == 0)
			xDrag = 0;
		if(yVel == 0)
			yDrag = 0;
	}
}