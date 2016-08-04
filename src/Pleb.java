import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

public class Pleb extends Hitbox
{
	ArrayList<Force> forceArchiver, appliedForces;
	Puppet puppet;
//	String faction, type;
	int duration, direction, strength, hDamage, sDamage, xKnockback, yKnockback, /*direction, speed,*/ xDist, yDist;
//	double decayRate, piercingRate;
	boolean isAttached;
	
	public Pleb(Puppet p, int x, int y, int w, int h, int d1, int d2, int s, int hd, int sd, int kx, int ky, boolean a)
	{
		super(x,y,w,h);
		puppet = p;
		duration = d1;
		direction = d2;	//[0 = mid, 1 = low, 2 = high]
		strength = s;
		hDamage = hd;
		sDamage = sd;
		xKnockback = kx;
		yKnockback = ky;
		isAttached = a;
		
		forceArchiver = new ArrayList<Force>();
		appliedForces = new ArrayList<Force>();
		
		if(puppet != null)
		{
			if(xKnockback != 0)
				appliedForces.add(new Force("xKnockback",((xKnockback > 0 && puppet.isFacingRight) || (xKnockback < 0 && !puppet.isFacingRight))? 3:1,Math.abs(xKnockback),(Math.abs(xKnockback)/5 > 0)? Math.abs(xKnockback)/5:1));
			if(yKnockback != 0)
				appliedForces.add(new Force("yKnockback",(yKnockback > 0)? 0:2,Math.abs(yKnockback),(Math.abs(yKnockback)/5 > 0)? Math.abs(yKnockback)/5:1));
			
			xDist = xCoord-puppet.xCoord;
			yDist = yCoord-puppet.yCoord;
			if(!puppet.isFacingRight)
				xCoord = puppet.xCoord+puppet.width-xDist-width;
		}
	}
	
	//FOR GUARD TRIGGER
	public Pleb(Puppet p, int x, int y, int w, int h, int d, boolean a)
	{
		super(x,y,w,h);
		puppet = p;
		duration = d;
		direction = -1;
		strength = 0;
		hDamage = 0;
		sDamage = 0;
		xKnockback = 0;
		yKnockback = 0;
		isAttached = a;
		
		forceArchiver = new ArrayList<Force>();
		appliedForces = new ArrayList<Force>();
		
		if(puppet != null)
		{
			xDist = xCoord-puppet.xCoord;
			yDist = yCoord-puppet.yCoord;
		}
	}
	
	//MIGHT REMOVE LATER
	public Pleb(Puppet p, /*String f, String t,*/ int x, int y, int w, int h, int d1, int d2, int s, int hd, int kx, int ky, boolean a)	//, int d2, int s, double d3, double p)
	{
		super(x,y,w,h);
		puppet = p;
//		faction = f;
//		type = t;
		duration = d1;
		direction = d2;
		strength = s;
		hDamage = hd;
		sDamage = (int)(hDamage/4.0+0.5);
		xKnockback = kx;
		yKnockback = ky;
		isAttached = a;
//		direction = d2;
//		speed = s;
//		decayRate = d3;
//		piercingRate = p;
		
/*		if(speed > 0)
		{
			if(direction > 0 && direction < 180)
				forceArchiver.add(new Force("",3,(int)(speed*Math.sin(Math.toRadians(direction))+0.5),0));
			else if(direction > 180)
				forceArchiver.add(new Force("",1,-(int)(speed*Math.sin(Math.toRadians(direction))+0.5),0));
			if(direction < 90 || direction > 270)
				forceArchiver.add(new Force("",2,(int)(speed*Math.cos(Math.toRadians(direction))+0.5),0));
			else if(direction > 90 && direction < 270)
				forceArchiver.add(new Force("",0,-(int)(speed*Math.cos(Math.toRadians(direction))+0.5),0));
		}*/
		
		forceArchiver = new ArrayList<Force>();
		appliedForces = new ArrayList<Force>();
		if(puppet != null)
		{
			if(xKnockback != 0)
				appliedForces.add(new Force("xKnockback",((xKnockback > 0 && puppet.isFacingRight) || (xKnockback < 0 && !puppet.isFacingRight))? 3:1,Math.abs(xKnockback),(Math.abs(xKnockback)/5 > 0)? Math.abs(xKnockback)/5:1));
			if(yKnockback != 0)
				appliedForces.add(new Force("yKnockback",(yKnockback > 0)? 0:2,Math.abs(yKnockback),(Math.abs(yKnockback)/5 > 0)? Math.abs(yKnockback)/5:1));
			
			xDist = xCoord-puppet.xCoord;
			yDist = yCoord-puppet.yCoord;
			if(!puppet.isFacingRight)
				xCoord = puppet.xCoord+puppet.width-xDist-width;
		}
	}
	
	
	public void draw(Graphics g, double w, double h)
	{
		//TEST
		g.setColor((direction != -1)? Color.RED:Color.YELLOW);
		g.setColor(new Color(g.getColor().getRed(),g.getColor().getGreen(),g.getColor().getBlue(),50));
		g.fillRect((int)(xHosh*w/1280),(int)(yHosh*h/720),(int)(width*w/1280),(int)(height*h/720));
		g.setColor((direction != -1)? Color.RED:Color.YELLOW);
		g.drawRect((int)(xHosh*w/1280),(int)(yHosh*h/720),(int)(width*w/1280),(int)(height*h/720));
		switch(direction)
		{
			case 0:
				g.drawString("MID",(int)(xHosh*w/1280),(int)(yHosh*h/720));
				break;
			case 1:
				g.drawString("LOW",(int)(xHosh*w/1280),(int)(yHosh*h/720));
				break;
			case 2:
				g.drawString("HIGH",(int)(xHosh*w/1280),(int)(yHosh*h/720));
				break;
		}
		
	/*	double sNum = Math.round(strength*100.0)/100.0;
		g.drawString(sNum+"",(int)((xHosh+width)*w/1280),(int)(yHosh*h/720));*/
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
		duration--;
		
		if(puppet != null && isAttached)
		{
			if(puppet.isFacingRight)
				xCoord = puppet.xCoord+xDist;
			else
				xCoord = puppet.xCoord+puppet.width-xDist-width;
			yCoord = puppet.yCoord+yDist;
		}
	}
}