import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.awt.Toolkit;
import java.net.URL;
import java.util.ArrayList;

abstract class Puppet
{
	private static final int IDLE = 0;
	private static final int FALL = 1;
	private static final int JUMP = 2;
	private static final int MOVE = 3;
	private static final int DASH = 4;
	private static final int FLINCH = 5;
	private static final int DEATH_1 = 7;	//collapse
	private static final int DEATH_2 = 8;	//knocked down
	
	ArrayList<Organ> anatomy;
	ArrayList<Pleb> plebArchiver;
	ArrayList<int[]> touchArchiver;//, actionList, spriteArchiver;
	ArrayList<int[][]> hitboxArchiver;
//	ArrayList<Force> forceArchiver; 
	Organ bounds, grabBox;	//Name subject to change, could use bounds as throwable hitbox
	State currState, prevState;
	int id, xCoord, yCoord, xHosh, yHosh, width, height, crHeight;
	int maxHp, maxSp, maxMp, maxSpd;
	int health, stamina, meter, speed;
	double frameIndex;
	double jForce, jump;
	boolean isFacingRight, isPerformingAction;
	int[] spriteParams;
	boolean[] isBlocking;
	
	public enum State
	{
		IDLE, WALK_FORWARD, WALK_BACKWARD, PERFORM_ACTION
	}
	
	public Puppet(int x, int y, int w, int h, int c, int hp, int sp, int mp, int s, int a, double j, boolean r, boolean f2)
	{
		anatomy = new ArrayList<Organ>();
		plebArchiver = new ArrayList<Pleb>();
		touchArchiver = new ArrayList<int[]>();	//[type, id]
		hitboxArchiver = new ArrayList<int[][]>(); //[sheet.y, sheet.xStart, sheet.xLoop, reversed?, frame delay], [[hitbox.x, hitbox.y, hitbox.w, hitbox.h, ...], ...
	//	actionList = new ArrayList<int[]>();	//[action name, sprites in row, loops?]
	//	spriteArchiver = new ArrayList<int[]>();	//[xMod,yMod,width,height,sWidth,sHeight]
	//	forceArchiver = new ArrayList<Force>();
		
		currState = State.IDLE;
		prevState = State.IDLE;
		id = -1;
		xCoord = x;
		yCoord = y;
		xHosh = xCoord;
		yHosh = yCoord;
		width = w;
		height = h;
		crHeight = c;
		isFacingRight = r;
		isPerformingAction = false;
		isBlocking = new boolean[]{false,false};
		
		maxHp = hp;
		maxSp = sp;
		maxMp = mp;
		maxSpd = s;
		jForce = j;
		
		health = maxHp;
		stamina = maxSp;
		meter = 0;
		speed = maxSpd;
		jump = jForce;
		
		frameIndex = 0;
		
		bounds =  new Organ(x,y,w,h,speed);
		bounds.isFloating = f2;
		bounds.isMovable = true;
		touchArchiver.add(new int[]{-1});
	}
	
	public void draw(Graphics2D g, ImageObserver i, SpriteReader s, double w, double h, boolean d)
	{
	/*	if(d)
		{*/
			try
			{
				g.setColor(Color.BLUE);
				g.setColor(new Color(g.getColor().getRed(),g.getColor().getGreen(),g.getColor().getBlue(),50));
				g.fillRect((int)(bounds.xHosh*w/1280),(int)(bounds.yHosh*h/720),(int)(bounds.width*w/1280),(int)(bounds.height*h/720));
				g.setColor(Color.BLUE);
				g.drawRect((int)(bounds.xHosh*w/1280),(int)(bounds.yHosh*h/720),(int)(bounds.width*w/1280),(int)(bounds.height*h/720));
				
				g.setColor(Color.PINK);
				if(isFacingRight)
					g.drawLine((int)((bounds.xHosh+bounds.width-15)*w/1280),(int)((bounds.yHosh+bounds.height/2)*h/720),(int)((bounds.xHosh+bounds.width+15)*w/1280),(int)((bounds.yHosh+bounds.height/2)*h/720));
				else
					g.drawLine((int)((bounds.xHosh-15)*w/1280),(int)((bounds.yHosh+bounds.height/2)*h/720),(int)((bounds.xHosh+15)*w/1280),(int)((bounds.yHosh+bounds.height/2)*h/720));
				
				g.setColor(Color.BLUE);
				g.drawString((int)frameIndex+"",(int)((bounds.xHosh+bounds.width+2)*w/1280),(int)((bounds.yHosh+bounds.height*3/4)*h/720));
				
				for(Hitbox a: anatomy)
				{
					g.setColor(Color.MAGENTA);
					g.setColor(new Color(g.getColor().getRed(),g.getColor().getGreen(),g.getColor().getBlue(),50));
					g.fillRect((int)(a.xHosh*w/1280),(int)(a.yHosh*h/720),(int)(a.width*w/1280),(int)(a.height*h/720));
					g.setColor(Color.MAGENTA);
					g.drawRect((int)(a.xHosh*w/1280),(int)(a.yHosh*h/720),(int)(a.width*w/1280),(int)(a.height*h/720));
				}
			}
			catch(java.lang.NullPointerException e)
			{
				draw(g,i,s,w,h,d);
			}
	//	}
	}
	
	public void checkState()
	{
		switch(currState)
		{
			case IDLE:
				idle();
				break;
				
			case WALK_FORWARD:
			case WALK_BACKWARD:
				move();
				break;
				
			//Take damage case
		}
		xCoord = bounds.xCoord;
		yCoord = bounds.yCoord;
	}
	
	public void idle()
	{
		//TAKE DAMAGE ROUTE SUPERCEDES EVERYTHING
		if(bounds.xVel > 0)
		{
			if(bounds.xDir > 0)
			{
				currState = State.WALK_FORWARD;
				return;
			}
			else if(bounds.xDir < 0)
			{
				currState = State.WALK_BACKWARD;
				return;
			}
		}	
	}
	
	public void move()
	{
		bounds.move();
		if(isPerformingAction)
			currState = State.PERFORM_ACTION;
		if(bounds.xVel == 0)
			currState = State.IDLE;
	}
	
	public void takeDamage(Pleb p)
	{
		if(health < 0)
			health = 0;
		if(stamina < 0)
			stamina = 0;
		
	/*	boolean isDamaged = true;
		if(plebArchiver.isEmpty())
			plebArchiver.add(p);
		else
		{
			for(int a = 0; a < plebArchiver.size(); a++)
			{
				if(plebArchiver.get(a) == p)
				{
					isDamaged = false;
					if(plebArchiver.get(a).cooldown < plebArchiver.get(a).painThreshold)
						plebArchiver.get(a).cooldown++;
					else
					{
						plebArchiver.get(a).cooldown = 0;
						plebArchiver.remove(a);
						a++;
					}
				}
			}
			if(isDamaged && !p.faction.equals(faction))
				plebArchiver.add(p);
		}
		if(isDamaged && !plebArchiver.isEmpty())
		{
			if(health > 0)
			{
				for(Pleb a: plebArchiver)
				{
					if(a.isLethal)
						health -= a.strength;
					else
						stamina -= a.strength;
				
					if(a.strength > 0)
						a.strength -= a.maxStr*(1-a.lastingRate);
				}
			}
			if(health < 0)
				health = 0;
		}*/
	}
	
	public void getHitboxes()
	{
		getHitboxes(State.valueOf(currState.toString()).ordinal());
	}
	
	public void update()
	{
		bounds.yCoord = yCoord;
		bounds.height = height;
		bounds.update();
	//	grabBox.update();
		
		for(Organ o: anatomy)
		{
			o.update();
			o.xVel = bounds.xVel;
			o.yVel = bounds.yVel;
			o.xDir = bounds.xDir;
			o.yDir = bounds.yDir;
			o.xDrag = bounds.xDrag;
			o.yDrag = bounds.yDrag;
		}
		
		if(!plebArchiver.isEmpty())
		{
			for(int p = 0; p < plebArchiver.size(); p++)
			{
		/*		if(plebArchiver.get(p).cooldown < plebArchiver.get(p).painThreshold)
					plebArchiver.get(p).cooldown++;
				else
				{
					plebArchiver.get(p).cooldown = 0;
					plebArchiver.remove(p);
					p++;
				}*/
			}
		}
	}
	
	protected void getHitboxes(int h)
	{
		anatomy = new ArrayList<Organ>();
		if(h < hitboxArchiver.size())
		{
			if(currState != prevState)
			{
				frameIndex = hitboxArchiver.get(h)[0][1];
				prevState = currState;
			}
			
			int i = (hitboxArchiver.get(h)[0][3] == 0)? (int)frameIndex+1:hitboxArchiver.get(h).length-(int)frameIndex-1;
			if(h < hitboxArchiver.size())	//dont really need in final, good for testing
			{
				if((hitboxArchiver.get(h)[0][3] == 0 && i < hitboxArchiver.get(h).length) || (hitboxArchiver.get(h)[0][3] == 1 && i > 0))
				{
					for(int j = 0; j < hitboxArchiver.get(h)[i].length; j += 4)
						anatomy.add(new Organ(hitboxArchiver.get(h)[i][j]+bounds.xCoord,hitboxArchiver.get(h)[i][j+1]+bounds.yCoord,hitboxArchiver.get(h)[i][j+2],hitboxArchiver.get(h)[i][j+3],speed));
				}
			}
			
		//	frameIndex++;
			int f = (int)frameIndex;
			frameIndex += 1.0/(hitboxArchiver.get(h)[0][4]+1);
			if(frameIndex-f > 1)
				frameIndex = (int)frameIndex;
			if(frameIndex >= hitboxArchiver.get(h).length-1)
				frameIndex = hitboxArchiver.get(h)[0][2];
		}
	}
}