import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

public class Pleb extends Hitbox
{
	ArrayList<Force> forceArchiver, appliedForces;
	Puppet puppet;
	Organ bounds;
	Action action;
	String hash;	//action, type;
	int duration, type, strength, hDamage, sDamage, xKnockback, yKnockback, xDist, yDist;
	double hitstunDamp;	//decayRate, piercingRate;
	boolean isAttached, isProjectile, pBreaker;
	double[][] properties;	//[[type, parameters], ...]
	
	public static final int GUARD = -1;
	public static final int MID = 0;
	public static final int LOW = 1;
	public static final int HIGH = 2;
	public static final int GRAB = 3;
	
	public static final int KNOCKDOWN = 0;	//[air only?, kd counter, upward force magnitude, upward force decay, down time]
	public static final int LAUNCH = 1;		//[interruptible?, xforce magnitude, xforce decay, yforce magnitude, yforce decay, hitstun]
	public static final int SPIKE = 2;		//[downward force, ground bounce force, down time]
	public static final int TAUNT = 3;		//[duration, phase, tint pulse]
	public static final int TAYLOR = 99;	//[ITS JOKE]
	
	public Pleb(Puppet p, Organ b, Action a, int x, int y, int w, int h, int d, int t, int s, int hd, int sd, int kx, int ky, double hs, boolean ia, boolean ip, boolean pb, double[][] pr)
	{
		super(x,y,w,h);
		puppet = p;
		bounds = b;
		action = a;
		hash = "";
		duration = d;
		type = t;
		strength = s;
		hDamage = hd;
		sDamage = sd;
		xKnockback = kx;
		yKnockback = ky;
		hitstunDamp = hs;
		isAttached = ia;
		isProjectile = ip;
		pBreaker = pb;
		properties = pr;
		
		forceArchiver = new ArrayList<Force>();
		appliedForces = new ArrayList<Force>();
		
		if(bounds != null)
		{
			if(xKnockback != 0)
				appliedForces.add(new Force("xKnockback",((xKnockback > 0 && puppet.isFacingRight) || (xKnockback < 0 && !puppet.isFacingRight))? 3:1,Math.abs(xKnockback),(Math.abs(xKnockback)/5 > 0)? Math.abs(xKnockback)/5:1));
			if(yKnockback != 0)
				appliedForces.add(new Force("yKnockback",(yKnockback > 0)? 0:2,Math.abs(yKnockback),2));	//(Math.abs(yKnockback)/10 > 0)? Math.abs(yKnockback)/10:1));
			
			xDist = xCoord-bounds.xCoord;
			yDist = yCoord-bounds.yCoord;
			if(!puppet.isFacingRight)
				xCoord = bounds.xCoord+bounds.width-xDist-width;
		}
	}
	
	//FOR GRABS AND GUARD TRIGGERS
	public Pleb(Puppet p, Organ b, Action a, int x, int y, int w, int h, int d, int t, boolean r, boolean ia)
	{
		super(x,y,w,h);
		puppet = p;
		bounds = b;
		action = a;
		hash = "";
		duration = d;
		type = t;
		strength = 0;
		hDamage = 0;
		sDamage = 0;
		xKnockback = 0;
		yKnockback = 0;
		hitstunDamp = 0;
		isAttached = ia;
		isProjectile = false;
		pBreaker = false;
		properties = new double[][]{};
		
		forceArchiver = new ArrayList<Force>();
		appliedForces = new ArrayList<Force>();
		
		if(puppet != null && bounds != null)
		{
			xDist = xCoord-bounds.xCoord;
			yDist = yCoord-bounds.yCoord;
		}
	}
	
	//MIGHT REMOVE LATER
	public Pleb(Puppet p, Organ b, Action a, /*String f, String t,*/ String hc, int x, int y, int w, int h, int d, int t, int s, int hd, int kx, int ky, double hs, boolean ia, boolean ip)	//, int d2, int s, double d3, double p)
	{
		super(x,y,w,h);
		puppet = p;
		bounds = b;
		action = a;
//		faction = f;
//		type = t;
		hash = hc;
		duration = d;
		type = t;
		strength = s;
		hDamage = hd;
		sDamage = (int)(hDamage/4.0+0.5);
		xKnockback = kx;
		yKnockback = ky;
		hitstunDamp = hs;
		isAttached = ia;
		isProjectile = ip;
		pBreaker = false;
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
		if(puppet != null && bounds != null)
		{
			if(xKnockback != 0)
				appliedForces.add(new Force("xKnockback",((xKnockback > 0 && puppet.isFacingRight) || (xKnockback < 0 && !puppet.isFacingRight))? 3:1,Math.abs(xKnockback),(Math.abs(xKnockback)/5 > 0)? Math.abs(xKnockback)/5:1));
			if(yKnockback != 0)
				appliedForces.add(new Force("yKnockback",(yKnockback > 0)? 0:2,Math.abs(yKnockback),(Math.abs(yKnockback)/5 > 0)? Math.abs(yKnockback)/5:1));
			
			xDist = xCoord-bounds.xCoord;
			yDist = yCoord-bounds.yCoord;
			if(!puppet.isFacingRight)
				xCoord = bounds.xCoord+bounds.width-xDist-width;
		}
	}
	
	
	public void draw(Graphics g, double w, double h)
	{
		//TEST
		switch(type)
		{
			case -1:
				g.setColor(Color.YELLOW);
				break;
				
			case 3:
				g.setColor(Color.ORANGE);
				break;
				
			default:
				g.setColor(Color.RED);
				break;
		}
		
		g.setColor(new Color(g.getColor().getRed(),g.getColor().getGreen(),g.getColor().getBlue(),50));
		g.fillRect((int)(xHosh*w/1280),(int)(yHosh*h/720),(int)(width*w/1280),(int)(height*h/720));
		g.setColor(new Color(g.getColor().getRed(),g.getColor().getGreen(),g.getColor().getBlue(),255));
		g.drawRect((int)(xHosh*w/1280),(int)(yHosh*h/720),(int)(width*w/1280),(int)(height*h/720));
		g.drawString(duration+"",(int)((xHosh+width)*w/1280),(int)((yHosh+height*9/10)*h/720));
		
		switch(type)
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
			case 3:
				g.drawString("GRAB",(int)(xHosh*w/1280),(int)(yHosh*h/720));
				break;
		}
		
	/*	double sNum = Math.round(strength*100.0)/100.0;
		g.drawString(sNum+"",(int)((xHosh+width)*w/1280),(int)(yHosh*h/720));*/
		//END OF RINE
		//-----------RINE ENDS HERE
	}
	
/*	public void move()
	{
		if(xDir > 0)
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
			yDrag = 0;
	}
*/	
	public void update()
	{
		super.update(xVel,yVel,xDir,yDir,xDrag,yDrag,speed);
		duration--;
		
		if(puppet != null && bounds != null && isAttached)
		{
			if(puppet.isFacingRight)
				xCoord = bounds.xCoord+xDist;
			else
				xCoord = bounds.xCoord+bounds.width-xDist-width;
			yCoord = bounds.yCoord+yDist;
		}
	}
}