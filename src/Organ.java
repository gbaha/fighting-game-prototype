/*import java.awt.Color;
import java.awt.Graphics;*/
import java.util.ArrayList;

public class Organ extends Hitbox
{
	ArrayList<Pleb> plebArchiver;
	ArrayList<Force> forceArchiver;
	int xForward, yForward, xDrift, yDrift;
	int[] blocked;
//	boolean xBlocked, yBlocked;
	
	//================================================
	// ADD UPPER AND LOWER BODY VARIABLE TO DIFFERENTIATE
	//================================================
	
	public Organ()
	{
		super(0,0,0,0);
		plebArchiver = new ArrayList<Pleb>();
		forceArchiver = new ArrayList<Force>();
		
		speed = 1;
		xForward = xVel*xDir;
		yForward = yVel*yDir;
		xDrift = xVel*xDrag;
		yDrift = yVel*yDrag;
		
		blocked = new int[4];
		blocked[0] = yCoord+height/2;
		blocked[1] = xCoord+width/2;
		blocked[2] = yCoord+height/2;
		blocked[3] = xCoord+width/2;
	/*	xBlocked = false;
		yBlocked = false;*/
	}
	
	public Organ(int x, int y, int w, int h, int s /*, int[] v*/)
	{
		super(x,y,w,h);
		plebArchiver = new ArrayList<Pleb>();
		forceArchiver = new ArrayList<Force>();
/*		for(int t = 0; t < 3; t++)
			vulnerableTo[t] = false;
		for(int t: v)
			vulnerableTo[t] = true;*/
		
		speed = s;
		xForward = xVel*xDir;
		yForward = yVel*yDir;
		xDrift = xVel*xDrag;
		yDrift = yVel*yDrag;
		
		blocked = new int[4];
		blocked[0] = yCoord+height/2;
		blocked[1] = xCoord+width/2;
		blocked[2] = yCoord+height/2;
		blocked[3] = xCoord+width/2;
	/*	xBlocked = false;
		yBlocked = false;*/
	}
	
	public void move()
	{
		if(Math.abs(xDir) > 0/* && xDir != -xDrag && xVel < speed*/)
			xVel = speed;	//xVel++;
		if(Math.abs(xDrag) > 0 && xVel > 0)
			xVel = 0;	//xVel--;
		if(Math.abs(yDir) > 0 /*&& yDir != -yDrag && yVel < speed*/)
			yVel = speed;	//yVel++;
		if(Math.abs(yDrag) > 0 /*&& yVel > 0*/)
			yVel = 0;	//yVel--;
		
	/*	if((xDir > 0 && blocked[1]) || (xDir < 0 && blocked[3]))
			xVel = 0;
		if((yDir > 0 && blocked[2]) || (yDir < 0 && blocked[0]))
			yVel = 0;
		if(xVel == 0)
			xDrag = 0;
		if(yVel == 0)
			yDrag = 0;*/
		
		xForward = xVel*xDir;
		yForward = yVel*yDir;
		xDrift = xVel*xDrag;
		yDrift = yVel*yDrag;		
		
		if((xDir > 0 && blocked[1] == xCoord+width/2) || (xDir < 0 && blocked[3] == xCoord+width/2))
		{
			if(blocked[1] == xCoord+width/2)
				blocked[1] += xForward;
			if(blocked[3] == xCoord+width/2)
				blocked[3] += xForward;
			xCoord += xForward;
		}
		else if((xDrag > 0 && blocked[1] == xCoord+width/2) || (xDrag < 0 && blocked[3] == xCoord+width/2))
		{
			if(blocked[1] == xCoord+width/2)
				blocked[1] += xDrift;
			if(blocked[3] == xCoord+width/2)
				blocked[3] += xDrift;
			xCoord += xDrift;
		}
		if(yDir != 0)
		{
			if(blocked[0] == yCoord+height/2)
				blocked[0] -= yForward;
			if(blocked[2] == yCoord+height/2)
				blocked[2] -= yForward;
			yCoord -= yForward;
		}
		else if(yDrag != 0)
		{
			if(blocked[0] == yCoord+height/2)
				blocked[0] -= yDrift;
			if(blocked[2] == yCoord+height/2)
				blocked[2] -= yDrift;
			yCoord -= yDrift;
		}
	}
	
	public void update()
	{
		super.update(xVel,yVel,xDir,yDir,xDrag,yDrag,speed);
		xForward = xVel*xDir;
		yForward = yVel*yDir;
		xDrift = xVel*xDrag;
		yDrift = yVel*yDrag;
		
		blocked[1] = xCoord+width/2;
		blocked[3] = xCoord+width/2;
	}
}