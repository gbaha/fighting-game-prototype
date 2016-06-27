import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

public class Pleb extends Hitbox
{
	ArrayList<Force> forceArchiver;
	Puppet puppet;
	String faction, type;
	int direction, speed, /*cooldown,*/ xDist, yDist;
	double strength, maxStr, decayRate, piercingRate;
//	boolean isLethal;
	
	public Pleb(Puppet p, String f, String t, int x, int y, int w, int h, int d1, int s1, double s2, double d2/*, double p, boolean l*/)
	{
		super(x,y,w,h);
		puppet = p;
		faction = f;
		type = t;
		direction = d1;
		speed = s1;
		maxStr = s2;
		strength = maxStr;
		decayRate = d2;
	//	piercingRate = p;
	//	isLethal = l;
		
		forceArchiver = new ArrayList<Force>();
		if(speed > 0)
		{
			if(direction > 0 && direction < 180)
				forceArchiver.add(new Force("",3,(int)(speed*Math.sin(Math.toRadians(direction))+0.5),0));
			else if(direction > 180)
				forceArchiver.add(new Force("",1,-(int)(speed*Math.sin(Math.toRadians(direction))+0.5),0));
			if(direction < 90 || direction > 270)
				forceArchiver.add(new Force("",2,(int)(speed*Math.cos(Math.toRadians(direction))+0.5),0));
			else if(direction > 90 && direction < 270)
				forceArchiver.add(new Force("",0,-(int)(speed*Math.cos(Math.toRadians(direction))+0.5),0));
		}
		
		if(puppet != null)
		{
			xDist = xCoord-puppet.xCoord;
			yDist = yCoord-puppet.yCoord;
		}
	}
	
	
	public void draw(Graphics g, double w, double h)
	{
		//TEST
		g.setColor(Color.RED);
		g.drawRect((int)(xHosh*w/1280),(int)(yHosh*h/720),(int)(width*w/1280),(int)(height*h/720));
		
		double sNum = Math.round(strength*100.0)/100.0;
		g.drawString(sNum+"",(int)((xHosh+width)*w/1280),(int)(yHosh*h/720));
		//END OF RINE
		//-----------RINE ENDS HERE
	}
	
	public void move()
	{
	/*	if(xDir > 0)
		{
			if(yDir > 0)
				direction = 1;
			else if(yDir < 0)
				direction = 3;
			else
				direction = 2;
		}
		else if(xDir < 0)
		{
			if(yDir > 0)
				direction = 7;
			else if(yDir < 0)
				direction = 5;
			else
				direction = 6;
		}
		else
		{
			if(yDir > 0)
				direction = 0;
			else if(yDir < 0)
				direction = 4;
		}
		
		if(xDrag == 0)
			xCoord += (int)((double)(xVel*xDir)+(speed*xDir)/2+0.5);
		else
			xCoord += (int)((double)(xVel*xDrag)+(speed*xDrag)/2+0.5);
		if(yDrag == 0)
			yCoord -= (int)((double)(yVel*yDir)+(speed*yDir)/2+0.5);
		else
			yCoord -= (int)((double)(yVel*yDrag)+(speed*yDrag)/2+0.5);
		
		if(Math.abs(xDir) > 0 && xDir != -xDrag && xVel < 20)
			xVel++;
		if(Math.abs(xDrag) > 0 && xVel > 0)
			xVel--;
		if(Math.abs(yDir) > 0 && yDir != -yDrag && yVel < 20)
			yVel++;
		if(Math.abs(yDrag) > 0 && yVel > 0)
			yVel--;
		
		if(xVel == 0)
			xDrag = 0;
		if(yVel == 0)
			yDrag = 0;	*/
	}
	
	public void update()
	{
		super.update(xVel,yVel,xDir,yDir,xDrag,yDrag,speed);
		if(strength > 0)
			strength -= maxStr*decayRate;
		else
			strength = 0;
		
		if(puppet != null && speed == -1)
		{
			xCoord = puppet.xCoord+xDist;
			yCoord = puppet.yCoord+yDist;
		}
	}
}