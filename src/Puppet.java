import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.awt.Toolkit;
import java.net.URL;
import java.util.ArrayList;

abstract class Puppet
{
	ArrayList<Organ> anatomy;
	ArrayList<int[]> touchArchiver;	//, actionList, spriteArchiver;
	ArrayList<int[][]> hitboxArchiver;
	ArrayList<Pleb> plebsIn, plebsOut;	//plebArchiver;
	Action[] normals;
	
//	ArrayList<Force> forceArchiver;
	Organ bounds, grabBox;	//Name subject to change, could use bounds as throwable hitbox
	State currState, prevState;
	Action currAction;
	int id, xCoord, yCoord, xHosh, yHosh, width, height, crHeight;
	int maxHp, maxSp, maxMp, maxSpd;
	int health, stamina, meter, speed;
	int preFrames, fCounter, hitStop, hitStun;
	double fIndex, jForce, jump;
	boolean isFacingRight, isPerformingAction, isCrouching;//, isJumping;
	int[] jDirections, spriteParams;
	boolean[] isBlocking;
	
	public enum PuppetState implements State
	{
		IDLE, CROUCH, STANDING, CROUCHING, WALK_FORWARD, WALK_BACKWARD, FALL_NEUTRAL, FALL_FORWARD, FALL_BACKWARD, LANDING, JUMP_NEUTRAL, JUMP_FORWARD, JUMP_BACKWARD,	//, PERFORM_ACTION
		FLINCH_STANDING0, FLINCH_STANDING1, FLINCH_STANDING2, FLINCH_CROUCHING;
		
		public String getState()
		{
			return name();
		}
		
		public int getPosition()
		{
			return ordinal();
		}
	}
	
	public Puppet(int x, int y, int w, int h, int c, int hp, int sp, int mp, int s, int a, double j, boolean r, boolean f2)
	{
		anatomy = new ArrayList<Organ>();
	//	plebArchiver = new ArrayList<Pleb>();
		touchArchiver = new ArrayList<int[]>();	//[type, id]
		hitboxArchiver = new ArrayList<int[][]>(); //[sheet.y, sheet.xStart, sheet.xLoop, reversed?, frame delay], [[hitbox.x, hitbox.y, hitbox.w, hitbox.h, ...], ...
	//	actionList = new ArrayList<int[]>();	//[action name, sprites in row, loops?]
	//	spriteArchiver = new ArrayList<int[]>();	//[xMod,yMod,width,height,sWidth,sHeight]
		plebsIn = new ArrayList<Pleb>();
		plebsOut = new ArrayList<Pleb>();
		
	//	forceArchiver = new ArrayList<Force>();
		normals = new Action[]{new LightPunch(), new MediumPunch(), new HeavyPunch(), new LightKick(), new MediumKick(), new HeavyKick()};
		
		currState = PuppetState.IDLE;
		prevState = PuppetState.IDLE;
		currAction = null;
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
	//	isJumping = false;
		
		jDirections = new int[]{0,0};
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
		
		preFrames = 0;
		fCounter = 0;
		fIndex = 0;
		hitStop = 0;
		hitStun = 0;
		
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
				
				for(Hitbox a: anatomy)
				{
					g.setColor(Color.GREEN);
					g.setColor(new Color(g.getColor().getRed(),g.getColor().getGreen(),g.getColor().getBlue(),50));
					g.fillRect((int)(a.xHosh*w/1280),(int)(a.yHosh*h/720),(int)(a.width*w/1280),(int)(a.height*h/720));
					g.setColor(Color.GREEN);
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
		switch(currState.getState())
		{
			case "IDLE":
			case "FALL_NEUTRAL":
			case "FALL_FORWARD":
			case "FALL_BACKWARD":
			case "LANDING":
				idle();
				break;
				
			case "CROUCH":
			case "STANDING":
			case "CROUCHING":
				crouch();
				break;
				
			case "WALK_FORWARD":
			case "WALK_BACKWARD":
				move();
				break;
				
			case "FLINCH_STANDING0":
			case "FLINCH_STANDING1":
			case "FLINCH_STANDING2":
			case "FLINCH_CROUCHING":
				flinch();
		}
		xCoord = bounds.xCoord;
		yCoord = bounds.yCoord;
	}
	
	public void setAction(Action a)
	{
		if(currAction == null)
			currAction = a;
		else if(currAction.isCancelable(currAction.type))
			currAction = a;
	}
	
	public void performAction()
	{
		currAction.perform(fCounter);
		if(!isPerformingAction)
		{
			currAction = null;
			currState = PuppetState.IDLE;
			fCounter = 0;
		}
	}
	
	public void idle()
	{
		if(!bounds.isGrounded && jDirections[1] == 0) //!isJumping)
		{
			switch(jDirections[0])
			{
				case 0:
					currState = PuppetState.FALL_NEUTRAL;
					return;
				case 1:
					currState = (isFacingRight)? PuppetState.FALL_FORWARD:PuppetState.FALL_BACKWARD;
					return;
				case -1:
					currState = (isFacingRight)? PuppetState.FALL_BACKWARD:PuppetState.FALL_FORWARD;
					return;
			}
		}
		if(bounds.xVel > 0)
		{
			if((isFacingRight && bounds.xDir > 0) || (!isFacingRight && bounds.xDir < 0))
			{
				currState = PuppetState.WALK_FORWARD;
				return;
			}
			else if((isFacingRight && bounds.xDir < 0) || (!isFacingRight && bounds.xDir > 0))
			{
				currState = PuppetState.WALK_BACKWARD;
				return;
			}
		}
	}
	
	public void crouch()
	{
		if(isCrouching)
		{
			if(preFrames == 0)
				currState = PuppetState.CROUCH;
		}
		else
		{
			if(currState == PuppetState.CROUCH)
			{
				currState = PuppetState.STANDING;
				preFrames = 4;
			}
			else if(preFrames == 0)
				currState = PuppetState.IDLE;
		}
	}
	
	public void move()
	{
		bounds.move();
		if(!bounds.isGrounded && jDirections[1] == 0) //!isJumping)
		{
			switch(jDirections[0])
			{
				case 0:
					currState = PuppetState.FALL_NEUTRAL;
					return;
				case 1:
					currState = (isFacingRight)? PuppetState.FALL_FORWARD:PuppetState.FALL_BACKWARD;
					return;
				case -1:
					currState = (isFacingRight)? PuppetState.FALL_BACKWARD:PuppetState.FALL_FORWARD;
					return;
			}
		}
		if(bounds.xVel == 0)
			currState = PuppetState.IDLE;
	}
	
	public void takeDamage(Pleb p)
	{
		health -= p.hDamage;
	//	stamina -= p.sDamage;
		if(health < 0)
			health = 0;
		if(stamina < 0)
			stamina = 0;
		for(Force f: p.appliedForces)
			bounds.forceArchiver.add(f);
		
		if(isCrouching)
			currState = PuppetState.FLINCH_CROUCHING;
		else
		{
			switch(p.direction)
			{
				case 0:
					currState = PuppetState.FLINCH_STANDING0;
					break;
				case 1:
					currState = PuppetState.FLINCH_STANDING1;
					break;
				case 2:
					currState = PuppetState.FLINCH_STANDING2;
					break;
			}
		}
		fIndex = hitboxArchiver.get(currState.getPosition())[0][1];
		
		switch(p.strength)
		{
			case 0:
				hitStun = 10;
				hitStop = 3;
				break;
			case 1:
				hitStun = 10;
				hitStop = 3;
				break;
			case 2:
				hitStun = 10;
				hitStop = 3;
				break;
			case 3:
				hitStun = 10;
				hitStop = 3;
				break;
		}
	}
	
	public void flinch()
	{
		if(hitStun > 0)
			hitStun--;
		else
			currState = (!isCrouching)? PuppetState.IDLE:PuppetState.CROUCH;
	}
	
	public void getHitboxes()
	{
		getHitboxes(currState.getPosition());
	}
	
	public void update()
	{
		if(currState == PuppetState.CROUCH)
		{
			bounds.yCoord = yCoord+height-crHeight;
			bounds.height = crHeight;
		}
		else
		{
			bounds.yCoord = yCoord;
			bounds.height = height;
		}
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
		
		if(!plebsIn.isEmpty())
		{
			for(int p = 0; p < plebsIn.size(); p++)
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
			//MIGHT REMOVE AGAIN, COULD BE PLACED IN PUBLIC METHOD
		/*	if(currState != prevState)
			{
				fIndex = hitboxArchiver.get(PuppetState.valueOf(currState.toString()).ordinal())[0][1];
				prevState = currState;
			}*/
			//===
			
		//	fIndex = 0;	//TEST
			
			int i = (int)fIndex+1-((hitboxArchiver.get(h)[0][3] == 0)? hitboxArchiver.get(h)[0][1]:0);
		/*	if(hitboxArchiver.get(h)[0][3] == 0) //&& fIndex >= hitboxArchiver.get(h)[0][2])
				i -= hitboxArchiver.get(h)[0][1];
		/*	else if(hitboxArchiver.get(h)[0][3] == 1 && fIndex <= hitboxArchiver.get(h)[0][2])
				i += hitboxArchiver.get(h)[0][1];*/
			
			for(int j = 0; j < hitboxArchiver.get(h)[i].length; j += 4)
				anatomy.add(new Organ((isFacingRight)? hitboxArchiver.get(h)[i][j]+bounds.xCoord:bounds.xCoord+bounds.width-hitboxArchiver.get(h)[i][j]-hitboxArchiver.get(h)[i][j+2],hitboxArchiver.get(h)[i][j+1]+bounds.yCoord,hitboxArchiver.get(h)[i][j+2],hitboxArchiver.get(h)[i][j+3],speed));
			
			int f = (int)fIndex+((hitboxArchiver.get(h)[0][3] == 1 && fIndex != (int)fIndex)? 1:0);
			fIndex += (hitboxArchiver.get(h)[0][3] == 0)? 1.0/(hitboxArchiver.get(h)[0][4]+1):-1.0/(hitboxArchiver.get(h)[0][4]+1);
			if(Math.abs(fIndex-f) >= 1)
			{
				fIndex = (int)fIndex;
				i += (hitboxArchiver.get(h)[0][3] == 0)? 1:-1;
				
				if(preFrames > 0)
					preFrames--;
			}
			if((hitboxArchiver.get(h)[0][3] == 0 && i >= hitboxArchiver.get(h).length) || (hitboxArchiver.get(h)[0][3] == 1 && i <= 0))
				fIndex = hitboxArchiver.get(h)[0][2];
		}
	}
	
	
	public interface State
	{
		public String getState();
		
		public int getPosition();
	}
	
	
	public class LightPunch extends Action
	{
		public LightPunch()
		{
			super(Action.NORMAL,1,new boolean[]{false,false,false,false,false});
		}
		
		public void perform(int f){}
	}
	
	public class MediumPunch extends Action
	{
		public MediumPunch()
		{
			super(Action.NORMAL,1,new boolean[]{false,false,false,false,false});
		}
		
		public void perform(int f){}
	}
	
	public class HeavyPunch extends Action
	{
		public HeavyPunch()
		{
			super(Action.NORMAL,1,new boolean[]{false,false,false,false,false});
		}
		
		public void perform(int f){}
	}
	
	public class LightKick extends Action
	{
		public LightKick()
		{
			super(Action.NORMAL,1,new boolean[]{false,false,false,false,false});
		}
		
		public void perform(int f){}
	}
	
	public class MediumKick extends Action
	{
		public MediumKick()
		{
			super(Action.NORMAL,1,new boolean[]{false,false,false,false,false});
		}
		
		public void perform(int f){}
	}
	
	public class HeavyKick extends Action
	{
		public HeavyKick()
		{
			super(Action.NORMAL,1,new boolean[]{false,false,false,false,false});
		}
		
		public void perform(int f){}
	}
}