import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.Toolkit;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;

abstract class Puppet implements Punchable
{
	ArrayList<Organ> anatomy;
//	ArrayList<int[]> touchArchiver, actionList, spriteArchiver;
	ArrayList<int[][]> hitboxArchiver;
	ArrayList<double[]> propertyArchiver, sTint;
	LinkedList<String> soundArchiver;
	LinkedList<float[]> soundInfo;
	
	ArrayList<Pleb> plebsIn, plebsOut;
	ArrayList<Prop> propArchiver;
	ArrayList<String> plebArchiver;
	Action[] normals;
	
	Punchable target;
	Organ bounds;
	State currState, prevState;
	Action currAction;
	Image sheet;
	int id, xCoord, yCoord, xHosh, yHosh, xOffset, yOffset, width, height, crHeight, kdHeight;
	int maxHp, maxSp, maxMp, maxSpd;
	int health, stamina, meter, speed;
	int spriteIndex, preFrames, fCounter, blockStun, hitStun, hitStop, sCooldown;
	int kdCounter, kdLimit, kdStun, bDirection, bounceLimit, bounceStun, launchPoint, slipFloat;
	int airOptions, airDashLimit, jumpLimit, aDash, jCount;
	double sIndex, sAngle, jForce, jump, juggleDamp, otgDamp, hitstunDamp, damageDamp;
	boolean isFacingRight, isPerformingAction, isCrouching, canBlock, isGuardBroken, isCounterhit, isTaunted;
	boolean throwInvul, isThrowing, isThrown, isTeching, isJuggled, isAirLocked, floatOverride;	//, isUnstoppable, isJumping;
	boolean isDashing, isHoming, isJumping, isSlipping;
	boolean isBella;	// its joke
	
	int[] hitInfo, flinchPoints, ukemi, bounces, jDirections, armor, spriteParams;
	boolean[] isBlocking;
	
	public enum PuppetState implements State
	{
		IDLE, CROUCH, STANDING, CROUCHING, WALK_FORWARD, WALK_BACKWARD, FALL_NEUTRAL, FALL_FORWARD, FALL_BACKWARD, LANDING, PREJUMP, JUMP_NEUTRAL, JUMP_FORWARD, JUMP_BACKWARD,
		GUARD_STANDING, GUARD_CROUCHING, GUARD_JUMPING, FLINCH_STANDING0, FLINCH_STANDING1, FLINCH_STANDING2, FLINCH_CROUCHING, FLINCH_TRIP0, FLINCH_TRIP1, FLINCH_AERIAL0, FLINCH_AERIAL1, FLINCH_AERIAL2,
		KNOCKDOWN, FALLING, WALL_BOUNCE, WALL_SPLAT, UKEMI_STAND, UKEMI_ROLL, UKEMI_REBOUND, BREAK_GROUND, BREAK_AIR, HUG_BREAK, HUGGED0, HUGGED1, HUGGED2, HUGGED3;
		
		public String getState()
		{
			return name();
		}
		
		public int getPosition()
		{
			return ordinal();
		}
	}
	
	public Puppet(int x, int y, int w, int h, int c, int k, int hp, int sp, int mp, int s, int a1, int a2, int j1, double j2, boolean r, boolean f2)
	{
		anatomy = new ArrayList<Organ>();
		hitboxArchiver = new ArrayList<int[][]>(); //[sheet.y, sheet.xStart, sheet.xLoop, reversed?, frame delay], [[hitbox.x, hitbox.y, hitbox.w, hitbox.h, ...], ...
		propertyArchiver = new ArrayList<double[]>();
		
		plebsIn = new ArrayList<Pleb>();
		plebsOut = new ArrayList<Pleb>();
		propArchiver = new ArrayList<Prop>();
		plebArchiver = new ArrayList<String>();
		
		sTint = new ArrayList<double[]>();	//[[default tint], [r,g,b,a,duration], ...] might add priority
		sTint.add(new double[]{127.5,127.5,127.5,255});
		
		soundArchiver = new LinkedList<String>();
		soundInfo = new LinkedList<float[]>();
		
		normals = new Action[]{new LightPunch(), new MediumPunch(), new HeavyPunch(), new LightKick(), new MediumKick(), new HeavyKick()};
		
		currState = PuppetState.IDLE;
		prevState = PuppetState.IDLE;
		currAction = null;
		id = -1;
		xCoord = x;
		yCoord = y;
		xHosh = xCoord;
		yHosh = yCoord;
		xOffset = 0;
		yOffset = 0;
		width = w;
		height = h;
		crHeight = c;
		kdHeight = k;
		isFacingRight = r;
		isPerformingAction = false;
		canBlock = false;
		isGuardBroken = false;
		throwInvul = false;
		isThrowing = false;
		isThrown = false;
		isTeching = false;
		isJuggled = false;
		isCounterhit = false;
		isTaunted = false;
		floatOverride = false;
		isDashing = false;
		isHoming = false;
		isSlipping = false;
	//	isUnstoppable = false;	// Unaffected by new forces through damage?
	//	isJumping = false;
		
		hitInfo = new int[4];	//[type, number of hits, enemy hitstun, enemy damage]
		flinchPoints = new int[]{0,0,0,0,0,0,0,0,0,0,0}; 	//marks points where sprite freezes during hitstun
		ukemi = new int[2];	//[frames, state]
		bounces = new int[2];
		jDirections = new int[3];
		armor = new int[2];	//[hits, duration]
		isBlocking = new boolean[]{false,false};
		
		maxHp = hp;
		maxSp = sp;
		maxMp = mp;
		maxSpd = s;
		jForce = j2;
		
		health = maxHp;
		stamina = maxSp;	//0;
		meter = 0;
		speed = maxSpd;
		jump = jForce;
		
		spriteIndex = -1;
		preFrames = 0;
		fCounter = 0;
		sIndex = 0;
		sAngle = 0;
		blockStun = 0;
		hitStun = 0;
		hitStop = 0;
		hitstunDamp = 0;
		juggleDamp = 0;
		otgDamp = 0;
		damageDamp = 0;
		sCooldown = 0;		//999999;
		
		kdCounter = 0;
		kdLimit = 2;	// if counter >= limit, cannot be knocked down in combo
		kdStun = 0;
		bDirection = 0;
		bounceLimit = 1;
		bounceStun = 0;
		launchPoint = 0;
		slipFloat = 0;
		airOptions = a1;
		airDashLimit = a2;
		jumpLimit = j1;
		aDash = 0;
		jCount = 0;
		
		bounds =  new Organ(x,y,w,h,speed);
		bounds.isFloating = f2;
		bounds.isMovable = true;
		target = null;
	//	touchArchiver.add(new int[]{-1});
	}
	
	public void draw(Graphics2D g, ImageObserver i, SpriteReader s, double w, double h, boolean[] d)
	{
		if(d[0])
		{
			try
			{
				if(!isThrown)
				{
					Color c = (bounds.isGhost)? Color.WHITE:Color.BLUE;
					g.setColor(c);
					g.setColor(new Color(g.getColor().getRed(),g.getColor().getGreen(),g.getColor().getBlue(),50));
					g.fillRect((int)(bounds.xHosh*w/1280),(int)(bounds.yHosh*h/720),(int)(bounds.width*w/1280),(int)(bounds.height*h/720));
					g.setColor(c);
					g.drawRect((int)(bounds.xHosh*w/1280),(int)(bounds.yHosh*h/720),(int)(bounds.width*w/1280),(int)(bounds.height*h/720));
				}
				
				g.setColor(Color.PINK);
				if(isFacingRight)
					g.drawLine((int)((bounds.xHosh+bounds.width-15)*w/1280),(int)((bounds.yHosh+bounds.height/2)*h/720),(int)((bounds.xHosh+bounds.width+15)*w/1280),(int)((bounds.yHosh+bounds.height/2)*h/720));
				else
					g.drawLine((int)((bounds.xHosh-15)*w/1280),(int)((bounds.yHosh+bounds.height/2)*h/720),(int)((bounds.xHosh+15)*w/1280),(int)((bounds.yHosh+bounds.height/2)*h/720));
				g.fillRect((int)((xHosh+xOffset)*w/1280),(int)((yHosh+yOffset)*h/720),(int)(10*w/1280),(int)(10*h/720));
				g.fillRect((int)((bounds.xHosh+bounds.width-10)*w/1280),(int)((bounds.yHosh+bounds.height+bounds.botOffset-10)*h/720),(int)(10*w/1280),(int)(10*h/720));
				
				for(Organ a: anatomy)
				{
					Color c = (a.hInvul)? Color.WHITE:((throwInvul)? Color.MAGENTA:((a.pInvul)? Color.CYAN: ((isSlipping)? Color.ORANGE:Color.GREEN)));
					g.setColor(c);
					g.setColor(new Color(g.getColor().getRed(),g.getColor().getGreen(),g.getColor().getBlue(),50));
					g.fillRect((int)(a.xHosh*w/1280),(int)(a.yHosh*h/720),(int)(a.width*w/1280),(int)(a.height*h/720));
					g.setColor(c);
					g.drawRect((int)(a.xHosh*w/1280),(int)(a.yHosh*h/720),(int)(a.width*w/1280),(int)(a.height*h/720));
				}
				
				if(anatomy.size() > 0)
				{
					if(anatomy.get(0).hInvul)
					{
						g.setColor(Color.WHITE);
						g.fillRect((int)((bounds.xHosh+bounds.width-60)*w/1280),(int)((bounds.yHosh+bounds.height+20)*h/720),(int)(20*w/1280),(int)(20*h/720));
					}
					if(anatomy.get(0).pInvul)
					{
						g.setColor(Color.CYAN);
						g.fillRect((int)((bounds.xHosh+bounds.width-20)*w/1280),(int)((bounds.yHosh+bounds.height+20)*h/720),(int)(20*w/1280),(int)(20*h/720));
					}
				}
				if(throwInvul)
				{
					g.setColor(Color.MAGENTA);
					g.fillRect((int)((bounds.xHosh+bounds.width-40)*w/1280),(int)((bounds.yHosh+bounds.height+20)*h/720),(int)(20*w/1280),(int)(20*h/720));
				}
			}
			catch(java.lang.NullPointerException e)
			{
				System.out.println(anatomy.isEmpty()+" "+anatomy.size());
				draw(g,i,s,w,h,d);
			}
		}
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
			case "PREJUMP":
			case "UKEMI_STAND":
			case "UKEMI_ROLL":
			case "UKEMI_REBOUND":
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
			
			case "JUMP_NEUTRAL":
			case "JUMP_FORWARD":
			case "JUMP_BACKWARD":
				jump();
				break;
				
			case "GUARD_STANDING":
			case "GUARD_CROUCHING":
			case "GUARD_JUMPING":
				guard();
				break;
			
			case "FLINCH_STANDING0":
			case "FLINCH_STANDING1":
			case "FLINCH_STANDING2":
			case "FLINCH_CROUCHING":
			case "FLINCH_TRIP0":
			case "FLINCH_TRIP1":
			case "FLINCH_AERIAL0":
			case "FLINCH_AERIAL1":
			case "FLINCH_AERIAL2":
			case "WALL_BOUNCE":
			case "WALL_SPLAT":
			case "BREAK_GROUND":
			case "BREAK_AIR":
			case "HUG_BREAK":
			case "HUGGED0":
			case "HUGGED1":
			case "HUGGED2":
			case "HUGGED3":
				flinch();
				break;
				
			case "KNOCKDOWN":
				knockdown();
				break;
		}
		
		xCoord = bounds.xCoord;
		if(bounds.height == height)	//((!bounds.isGrounded || currState == PuppetState.LANDING) && currState != PuppetState.GUARD_CROUCHING && currState != PuppetState.FLINCH_CROUCHING)
			yCoord = bounds.yCoord;
		
	/*	if(currState.getPosition() < hitboxArchiver.size())
		{
			if(currState != prevState)
			{
				sIndex = hitboxArchiver.get(currState.getPosition())[0][1];
				prevState = currState;
			}
		}*/
	}
	
	public void setAction(Action a)
	{
		if(currAction == null)
		{
			currAction = a;
			a.target = null;
			a.button = -1;
		}
		else //if(currAction.isCancelable(hitInfo[0],fCounter,currAction.type,currAction.button,bounds.isGrounded))
		{
			if(currAction.target != null)
			{
				currAction.target.xOffset = 0;
				currAction.target.yOffset = 0;
				currAction.target.sAngle = 0;
				if(currAction.type == Action.GRAB && a.type != Action.GRAB && currAction.target.damageDamp < 0.4)
					currAction.target.damageDamp = 0.4;
			}
			
			bounds.botOffset = 0;
			xOffset = 0;
			yOffset = 0;
			isThrowing = false;
			
			currAction = a;
			a.target = null;
			a.button = -1;
			fCounter = 0;
			sIndex = hitboxArchiver.get(currState.getPosition())[0][1];
		}
		if(slipFloat == -1)
			slipFloat = 5;
	}
	
	public void performAction()
	{//System.out.println(fCounter+" "+currState.getState()+" "+currAction+" "+jDirections[2]);
		if(currAction != null)
		{
			if(fCounter == 0)
				bounds.botOffset = 0;
			currAction.perform(fCounter);
		}
		bounds.xDir = 0;
		bounds.xDrag = 0;
		
		if(!isPerformingAction)
		{
			if(currAction != null)
			{
				if(currAction.type != Action.JUMP)
					currState = (isCrouching)? PuppetState.CROUCH:PuppetState.IDLE;
				if(currAction.target != null)
				{
					if(currAction.type == Action.GRAB && currAction.target.damageDamp < 0.4)
						currAction.target.damageDamp = 0.4;
				}
				currAction.target = null;
			}
			else
				currState = (isCrouching)? PuppetState.CROUCH:PuppetState.IDLE;
			
			currAction = null;
			fCounter = 0;
			sAngle = 0;
			bounds.botOffset = 0;
		}
		
		if(bounds.isGrounded && bounds.botOffset != 0)
		{
			if(currAction != null)
			{
				if(currAction.target != null)
				{
					if(currAction.type == Action.GRAB && currAction.target.damageDamp < 0.4)
						currAction.target.damageDamp = 0.4;
				}
				currAction.target = null;
			}
			
			currAction = null;
			currState = PuppetState.LANDING;
			fCounter = 0;
			sAngle = 0;
			
			isPerformingAction = false;
	//		preFrames = 3;
			bounds.botOffset = 0;
		}
	}
	
	public void idle()
	{if(bDirection != 0)System.out.println(ukemi[0]+" "+ukemi[1]);
		if(currAction != null && currState != PuppetState.PREJUMP && ukemi[1] == 0)
		{
			performAction();
			return;
		}
		else
			currAction = null;
		
		if(isCrouching && currState == PuppetState.LANDING)
		{
			currState = PuppetState.CROUCHING;
			return;
		}
		if(isBlocking[0] || isBlocking[1])
		{
			currState = (bounds.isGrounded)? ((isBlocking[0])? PuppetState.GUARD_STANDING:PuppetState.GUARD_CROUCHING):PuppetState.GUARD_JUMPING;
			return;
		}
		
		if(preFrames == 0)
		{
			if(jDirections[1] == 1 || (!bounds.isGrounded && jDirections[1] == -1))
			{
				isJumping = true;
				switch(jDirections[0])
				{
					case 0:
						currState = PuppetState.JUMP_NEUTRAL;
						return;
					case 1:
						currState = (isFacingRight)? PuppetState.JUMP_FORWARD:PuppetState.JUMP_BACKWARD;
						return;
					case -1:
						currState = (isFacingRight)? PuppetState.JUMP_BACKWARD:PuppetState.JUMP_FORWARD;
						return;
				}
			}
			else if(jDirections[2] > 0)
			{
				if(bounds.isGrounded)
				{
					currState = PuppetState.PREJUMP;
					preFrames = 2;
				}
				jDirections[1] = 1;
				return;
			}
		}
		
		if((!bounds.isGrounded /*&& jDirections[0] == 0*/ && jDirections[1] < 1) /*!isJumping)*/ || currState == PuppetState.FALL_NEUTRAL || currState == PuppetState.FALL_FORWARD || currState == PuppetState.FALL_BACKWARD || currState == PuppetState.LANDING || currState == PuppetState.UKEMI_STAND || currState == PuppetState.UKEMI_ROLL || currState == PuppetState.UKEMI_REBOUND)
		{
			if(currState != PuppetState.LANDING)
			{
				if(ukemi[1] == 0)
				{
					switch(jDirections[0])
					{
						case 0:
							currState = PuppetState.FALL_NEUTRAL;
							break;
						case 1:
							currState = (isFacingRight)? PuppetState.FALL_FORWARD:PuppetState.FALL_BACKWARD;
							break;
						case -1:
							currState = (isFacingRight)? PuppetState.FALL_BACKWARD:PuppetState.FALL_FORWARD;
							break;
					}
				}
				if(bounds.isGrounded)
				{
					currState = PuppetState.LANDING;
					preFrames = 3;
				}
			}
			else if(preFrames == 0)
				currState = PuppetState.IDLE;
			return;
		}
		
		if(isCrouching)
		{
			currState = PuppetState.CROUCHING;
			preFrames = 4;
			return;
		}
		
		if(preFrames == 0)
		{
			if((isFacingRight && (bounds.xDir > 0 || bounds.xDrag > 0)) || (!isFacingRight && (bounds.xDir < 0  || bounds.xDrag < 0)))
			{
				currState = PuppetState.WALK_FORWARD;
				return;
			}
			else if((isFacingRight && (bounds.xDir < 0 || bounds.xDrag < 0)) || (!isFacingRight && (bounds.xDir > 0  || bounds.xDrag > 0)))
			{
				currState = PuppetState.WALK_BACKWARD;
				return;
			}
		}
	}
	
	public void crouch()
	{
		if(currAction != null)
		{
			performAction();
			return;
		}
		
		if(isBlocking[0] || isBlocking[1])
		{
			currState = (bounds.isGrounded)? ((isBlocking[0])? PuppetState.GUARD_STANDING:PuppetState.GUARD_CROUCHING):PuppetState.GUARD_JUMPING;
			return;
		}
		
		if(preFrames == 0)
		{
			if(jDirections[1] == 1 || (!bounds.isGrounded && jDirections[1] == -1))
			{
				isJumping = true;
				switch(jDirections[0])
				{
					case 0:
						currState = PuppetState.JUMP_NEUTRAL;
						return;
					case 1:
						currState = (isFacingRight)? PuppetState.JUMP_FORWARD:PuppetState.JUMP_BACKWARD;
						return;
					case -1:
						currState = (isFacingRight)? PuppetState.JUMP_BACKWARD:PuppetState.JUMP_FORWARD;
						return;
				}
			}
			else if(jDirections[2] > 0)
			{
				if(bounds.isGrounded)
				{
					currState = PuppetState.PREJUMP;
					preFrames = 2;
				}
				jDirections[1] = 1;
				return;
			}
		}
		
		if(isBlocking[0] || isBlocking[1])
		{
			currState = (isBlocking[0])? PuppetState.GUARD_STANDING:PuppetState.GUARD_CROUCHING;
			return;
		}
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
		
		if(jDirections[2] > 0)
		{
			if(preFrames == 0)
			{
				switch(jDirections[0])
				{
					case 0:
						currState = PuppetState.JUMP_NEUTRAL;
						return;
					case 1:
						currState = (isFacingRight)? PuppetState.JUMP_FORWARD:PuppetState.JUMP_BACKWARD;
						return;
					case -1:
						currState = (isFacingRight)? PuppetState.JUMP_BACKWARD:PuppetState.JUMP_FORWARD;
						return;
				}
			}
		}
		if(jDirections[1] == 0 && !bounds.isGrounded)
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
	}
	
	public void move()
	{
		if(isCrouching)
			bounds.xVel = 0;
		if(currAction != null)
		{
			performAction();
			return;
		}
		
		if(isBlocking[0] || isBlocking[1])
		{
			currState = (bounds.isGrounded)? ((isBlocking[0])? PuppetState.GUARD_STANDING:PuppetState.GUARD_CROUCHING):PuppetState.GUARD_JUMPING;
			return;
		}
		
		if(preFrames == 0)
		{
			if(jDirections[1] == 1 || (!bounds.isGrounded && jDirections[1] == -1))
			{
				isJumping = true;
				switch(jDirections[0])
				{
					case 0:
						currState = PuppetState.JUMP_NEUTRAL;
						return;
					case 1:
						currState = (isFacingRight)? PuppetState.JUMP_FORWARD:PuppetState.JUMP_BACKWARD;
						return;
					case -1:
						currState = (isFacingRight)? PuppetState.JUMP_BACKWARD:PuppetState.JUMP_FORWARD;
						return;
				}
			}
			else if(jDirections[2] > 0)
			{
				if(bounds.isGrounded)
				{
					currState = PuppetState.PREJUMP;
					preFrames = 2;
				}
				jDirections[1] = 1;
				return;
			}
		}
		
		bounds.move();
		if(isBlocking[0] || isBlocking[1])
		{
			currState = (isBlocking[0])? PuppetState.GUARD_STANDING:PuppetState.GUARD_CROUCHING;
			return;
		}
		
		if(jDirections[2] > 0)
		{
			if(preFrames == 0)
			{
				switch(jDirections[0])
				{
					case 0:
						currState = PuppetState.JUMP_NEUTRAL;
						return;
					case 1:
						currState = (isFacingRight)? PuppetState.JUMP_FORWARD:PuppetState.JUMP_BACKWARD;
						return;
					case -1:
						currState = (isFacingRight)? PuppetState.JUMP_BACKWARD:PuppetState.JUMP_FORWARD;
						return;
				}
			}
		}
		if(jDirections[1] == 0 && !bounds.isGrounded)
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
	
	public void jump()
	{
		if(jCount < jumpLimit && airOptions > aDash+jCount && !bounds.isFloating && jDirections[2] > 0 && preFrames == 0)
		{
			switch(jDirections[0])
			{
				case 0:
					currState = PuppetState.JUMP_NEUTRAL;
					break;
				case 1:
					currState = (isFacingRight)? PuppetState.JUMP_FORWARD:PuppetState.JUMP_BACKWARD;
					break;
				case -1:
					currState = (isFacingRight)? PuppetState.JUMP_BACKWARD:PuppetState.JUMP_FORWARD;
					break;
			}
			
			if(jDirections[2] >= 2)
			{
				int fLimit = bounds.forceArchiver.size();
				for(int f = 0; f < fLimit; f++)
				{
					if(bounds.forceArchiver.get(f).type.equals("yJump") || bounds.forceArchiver.get(f).type.equals("xKnockback"))	//|| bounds.forceArchiver.get(f).direction == 1 || bounds.forceArchiver.get(f).direction == 3)
					{
						bounds.forceArchiver.remove(f);
						fLimit = bounds.forceArchiver.size();
						f--;
					}
				}
				bounds.forceArchiver.add(new Force("yJump",2,(aDash+jCount == 0)? jump:jump*9/10,1));
				jDirections[1] = 1;
				jDirections[2] = -1;
				isJumping = false;
				jCount++;
				preFrames = 5;
				sIndex = hitboxArchiver.get(currState.getPosition())[0][1];
			}
		}
		bounds.botOffset = 0;
		if(jDirections[2] > 0)
			jDirections[2]++;
		
		if(currAction != null && jDirections[2] <= 0)// && preFrames == 0)
		{
			bounds.isGrounded = false;
			performAction();
			return;
		}
		
		if(isBlocking[0] || isBlocking[1])
		{
			currState = (bounds.isGrounded)? ((isBlocking[0])? PuppetState.GUARD_STANDING:PuppetState.GUARD_CROUCHING):PuppetState.GUARD_JUMPING;
			return;
		}//System.out.println(jDirections[1]+" "+bounds.isGrounded);
		
		if(jDirections[1] == 0 && !bounds.isGrounded)
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
	}
	
	public void takeDamage(Pleb p, Hitbox[] c)
	{
		boolean hitSuccessful = false;
		bounds.forceArchiver = new ArrayList<Force>();
		switch(p.type)
		{
			case Pleb.MID:
				if(!isBlocking[0] && !isBlocking[1])
					hitSuccessful = true;
				else
					addSound("hit_block.wav",new float[]{-7.0f});
				break;
				
			case Pleb.LOW:
				if(!isBlocking[1])
					hitSuccessful = true;
				else
					addSound("hit_block.wav",new float[]{-7.0f});
				break;
				
			case Pleb.HIGH:
				if(!isBlocking[0])
					hitSuccessful = true;
				else
					addSound("hit_block.wav",new float[]{-7.0f});
				break;
				
			case Pleb.GRAB:
				if(blockStun == 0 && hitStun == 0 && kdStun == 0 && bounds.isGrounded == p.puppet.bounds.isGrounded && !isBlocking[0] && !isBlocking[1])
				{
					p.puppet.currAction.target = this;
					p.puppet.fCounter = 0;
					p.puppet.hitInfo[0] = 2;
					isThrown = true;
				}
				break;
		}
		if(hitSuccessful)
			hitSuccessful = ((kdCounter < kdLimit || !bounds.isGrounded) && p.type != Pleb.GRAB);
			
		if(hitSuccessful && armor[0] > 0)
		{
			boolean a = (p.strength < 3);
			for(double[] b: p.properties)
			{
				if(b[0] == Pleb.KNOCKDOWN)
					a = false;
			}
			if(a)
			{
				health -= p.hDamage/2;
				sTint.add(new double[]{255,255,255,0,5});
				hitSuccessful = false;
			}
			else
				armor[0] = 0;
		}
		
		if(hitSuccessful)
		{
			if(p.puppet.currAction != null)
			{
				if(p.puppet.currAction.target == null && (p.puppet.hitInfo[1] > 2 || damageDamp > 0))
					damageDamp += p.puppet.currAction.scaling;
				p.puppet.currAction.target = this;
			}
			if(damageDamp > 0.8)
				damageDamp = 0.8;
			
			if(isCounterhit)
				isCounterhit = false;
			else if(p.puppet.slipFloat > 0)
				isCounterhit = true;
			else if(currAction != null)
				isCounterhit = (currAction.type != Action.DASH || currAction.type != Action.JUMP);
			if(stamina < 100)
			{
				isGuardBroken = true;
				stamina = 0;
			}
			
			int damage = p.hDamage;
			damage *= (isCounterhit)? 1.5:1;
			damage *= (p.puppet.isTaunted)? 1.15:1;
			damage *= (isGuardBroken)? 2.5:1;
			damage *= (p.puppet.isBella && (p.puppet.isThrowing || isThrown))? 999:1;	// its joke
			damage = (int)(damage*(1-damageDamp)+0.5);
			
			health -= damage;
			if(health < 0)
				health = 0;
			
			if(isGuardBroken && hitStun == 0)
			{
				currState = (bounds.isGrounded)? PuppetState.BREAK_GROUND:PuppetState.BREAK_AIR;
				if(!bounds.isGrounded)
					propertyArchiver.add(new double[]{Pleb.KNOCKDOWN,1,1,21,7,60});
				hitStun = 60;
				hitStop = 15;
				stamina = 0;
			}
			else
			{
				if(otgDamp > 0)
					currState = PuppetState.FLINCH_TRIP1;
				else if(!bounds.isGrounded)
					currState = PuppetState.FLINCH_AERIAL0;
				else if(isCrouching) //&& currState != PuppetState.FLINCH_CROUCHING)
					currState = PuppetState.FLINCH_CROUCHING;
				else
				{
					switch(p.type)
					{
						case Pleb.MID:
							currState = PuppetState.FLINCH_STANDING0;
							break;
						case Pleb.LOW:
							currState = PuppetState.FLINCH_STANDING1;
							break;
						case Pleb.HIGH:
							currState = PuppetState.FLINCH_STANDING2;
							break;
					}
			//		isCrouching = false;
				}
			
				switch(p.strength)
				{
					case 0:
						hitStun = 10;
						hitStop = 5;
						addSound("hit_light.wav",new float[]{-3.0f});
						break;
					case 1:
						hitStun = 15;
						hitStop = 7;
						addSound("hit_light.wav",new float[]{-3.0f});
						break;
					case 2:
						hitStun = 22;
						hitStop = 9;
						addSound("hit_light.wav",new float[]{-3.0f});
						break;
					case 3:	//Change later
						hitStun = 10;
						hitStop = 10;
						addSound("hit_light.wav",new float[]{-3.0f});
						break;
				}
			}
			if(isCounterhit)
				hitStun *= 1.5;
			
			p.puppet.hitInfo[0] = 2;
			p.puppet.hitInfo[1]++;
			p.puppet.hitInfo[3] += damage;
			
			bounds.forceArchiver.clear();
			if(p.properties.length > 0)
			{
				for(double[] t: p.properties)
				{
					switch((int)t[0])
					{
						case Pleb.KNOCKDOWN:
							if(t[1] == 0 || !bounds.isGrounded)
							{
								if(kdStun <= 0)
								{
									currState = (bounds.isGrounded)? ((t[3] == 0)? PuppetState.KNOCKDOWN:PuppetState.FLINCH_TRIP0):PuppetState.FLINCH_TRIP1;
									bounds.yCoord = yCoord;
									bounds.height = height;
									sIndex = hitboxArchiver.get(currState.getPosition())[0][1];
								}
								p.puppet.hitInfo[2] += (int)t[5];
							}
							break;
							
						case Pleb.LAUNCH:
							if((kdStun == 0 || !bounds.isGrounded) && launchPoint == 0)
							{
								currState = PuppetState.FLINCH_AERIAL1;
								if((isFacingRight && bounds == c[0]) || (!isFacingRight && bounds == c[1]))
									t[2] = 0;
								
								hitStun = (int)t[6];
								launchPoint = p.puppet.hitInfo[1];
								isJuggled = true;
							}
							break;
							
						case Pleb.SPIKE:
							currState = PuppetState.FLINCH_AERIAL2;
							break;
							
						case Pleb.WALLBOUNCE:
							if(t[1] == 0 || !bounds.isGrounded)
							{
								hitStun = (int)t[7];
								isFacingRight = !p.puppet.isFacingRight;
								if(t[4] > 0)
									isJuggled = true;
							}
							break;
					}
					propertyArchiver.add(t);
				}
			}
			sIndex = hitboxArchiver.get(currState.getPosition())[0][1];
			bounds.botOffset = 0;
			yOffset = 0;
			sAngle = 0;
			
			p.puppet.hitInfo[2] = hitStun;	// Must be after properties for launch to work
			p.puppet.stamina = p.puppet.maxSp;
		}
		else if(!isThrown && p.type != Pleb.GRAB)
		{
			stamina -= p.sDamage;
			switch(p.strength)
			{
				case 0:
					blockStun = 10;
					hitStop = 5;
					break;
				case 1:
					blockStun = 10;
					hitStop = 7;
					break;
				case 2:
					blockStun = 10;
					hitStop = 9;
					break;
				case 3:
					blockStun = 10;
					hitStop = 10;
					break;
			}
			isFacingRight = !p.puppet.isFacingRight;
			
			p.puppet.hitInfo[0] = 1;
			p.puppet.hitInfo[2] = blockStun;
			if(p.puppet.currAction != null)
				p.puppet.currAction.target = this;
		//	p.puppet.hitInfo[1]++;
		}
		sCooldown = 120;
		
		if(armor[0] <= 0)
		{
			for(Force f: p.appliedForces)
			{
				if(f.type.equals("xKnockback") && p.puppet.bounds.isGrounded && !p.isProjectile && ((f.direction == 1 && bounds == c[0]) || (f.direction == 3 && bounds == c[1])))
				{
					f.direction = (f.direction+2)%4;
					p.puppet.bounds.forceArchiver.add(f);
				}
				else if(!f.type.equals("yKnockback") || hitSuccessful)
					bounds.forceArchiver.add(f);
			}
		}
		else
			armor[0]--;
		
		if(!bounds.isGrounded || kdCounter > 0)
			juggleCheck(p.puppet,hitSuccessful);
		if(isJuggled )//&& otgDamp == 0)
		{
			currState = PuppetState.FLINCH_AERIAL1;
			bounds.isGrounded = false;
		}
		
		if(p.action != null)
		{
			if(p.action.aLock)
				p.puppet.isAirLocked = false;
		}
		
		if(p.puppet.hitInfo[0] == 2)
			p.puppet.stamina = p.puppet.maxSp;
	}
	
	public void guard()
	{
		isPerformingAction = false;
		floatOverride = false;
		currAction = null;
		jDirections = new int[]{0,0,0};
		
		if(!isBlocking[0] && !isBlocking[1] && blockStun <= 0)
		{
			plebArchiver.clear();
			currState = (!isCrouching)? PuppetState.IDLE:PuppetState.CROUCH;
		}
	}
	
	public void flinch()
	{
		if(hitStun <= 0)
		{
			isPerformingAction = false;
			floatOverride = false;
			currAction = null;
			jDirections = new int[3];
			
			if(kdStun > 0)
			{
				if(bounds.isGrounded)
				{
					currState = PuppetState.KNOCKDOWN;
					hitStun = kdStun;
					if(kdCounter >= kdLimit)
						ukemi[0] = kdStun;
				}
			}
			else if(!isThrown)
			{
				fCounter = 0;
				kdCounter = 0;
				damageDamp = 0;
				otgDamp = 0;
				ukemi[0] = 0;
				bounces = new int[2];
				isCounterhit = false;
				plebArchiver.clear();
				currState = (!isCrouching)? PuppetState.IDLE:PuppetState.CROUCH;
			}
			else if(isThrown && currAction != null)	// Probably gonna need
			{
				if(currAction.type == Action.GRAB)
					isTeching = true;
			}
		}
	}
	
	public void knockdown()
	{
		if(kdStun > 0)
		{
			yOffset = 25;
			kdStun--;
		}
		else if(health > 0)
		{
			isPerformingAction = false;
			floatOverride = false;
			isCounterhit = false;
			bounds.isGrounded = false;
			currAction = null;
			
			yOffset = 0;
			fCounter = 0;
			kdCounter = 0;
			damageDamp = 0;
			otgDamp = 0;
			bounces = new int[2];
			plebArchiver.clear();
			
			ukemi[0] = 0;
			if(ukemi[1] > 2)
				bounds.forceArchiver.add(new Force("xUkemi",(isFacingRight)? 1:3,20,1));
			bounds.forceArchiver.add(new Force("yUkemi",2,40,2));
			currState = (ukemi[1] < 3)? PuppetState.UKEMI_STAND:PuppetState.UKEMI_ROLL;	//(!isCrouching)? PuppetState.IDLE:PuppetState.CROUCH;
		}
	}
	
	public void applyProperties()
	{
		int pLimit = propertyArchiver.size();
		for(int p = 0; p < pLimit; p++)
		{
			double[] t = propertyArchiver.get(p);
			boolean pCleared = false;
			
			switch((int)t[0])
			{
				case Pleb.KNOCKDOWN:
					if(t[1] == 0 || !bounds.isGrounded)
					{
						if(launchPoint == 0)
							bounds.forceArchiver.add(new Force("knockdown",(t[3] > 0)? 2:0,Math.abs(t[3]),t[4]));
					//	launchPoint = 0;
						isCrouching = false;
						
						kdStun = (int)t[5];
						if(bounds.isGrounded)
						{
							kdCounter += t[2];
							otgDamp = 0.2;
						}
					}
					pCleared = true;
					break;
				
				case Pleb.LAUNCH:
					if(kdStun == 0 || !bounds.isGrounded)
					{
						int fLimit = bounds.forceArchiver.size();
						for(int f = 0; f < fLimit; f++)
						{
							if(bounds.forceArchiver.get(f).direction == 2)
							{
								bounds.forceArchiver.remove(f);
								fLimit = bounds.forceArchiver.size();
								f--;
							}
						}
						bounds.forceArchiver.add(new Force("xLaunch",((isFacingRight && t[2] > 0) || (!isFacingRight && t[2] < 0))? 1:3,Math.abs(t[2]),t[3]));
						bounds.forceArchiver.add(new Force("yLaunch",2,t[4],t[5]));
					}
					pCleared = true;
					break;
					
				case Pleb.SPIKE:
					if(!bounds.isGrounded)
					{
						bounds.forceArchiver.add(new Force("yLaunch",0,t[1],t[1]));
						sIndex = hitboxArchiver.get(currState.getPosition())[0][1];
						hitStun++;
					}
					else
					{
						currState = PuppetState.KNOCKDOWN;
						propertyArchiver.add(new double[]{Pleb.KNOCKDOWN,0,1,t[2],t[2]/5,t[3]});
						pCleared = true;
					}
					break;
					
				case Pleb.WALLBOUNCE:
					if(t[7] > 0)
					{
						t[7]--;
						if(bDirection == 0 || (bDirection != 0 && bounceStun > 0))
						{
							bounds.forceArchiver.add(new Force("xLaunch",((isFacingRight && t[2] > 0) || (!isFacingRight && t[2] < 0))? 1:3,Math.abs(t[2]),(t[7] > 0)? t[2]:t[3]));
							bounds.forceArchiver.add(new Force("yLaunch",2,t[4],(t[7] > 0)? t[4]:t[5]));
						}
						else
						{
							currState = PuppetState.WALL_SPLAT;
							int fLimit = bounds.forceArchiver.size();
							for(int f = 0; f < fLimit; f++)
							{
								bounds.forceArchiver.remove(f);
								fLimit = bounds.forceArchiver.size();
								f--;
							}System.out.println(bDirection+" "+bounces[0]+" "+bounces[1]+" "+bounceLimit);
							if((bDirection == -1 && bounces[0] < bounceLimit) || (bDirection == 1 && bounces[1] < bounceLimit))
							{
								currState = PuppetState.WALL_BOUNCE;
								bounds.forceArchiver.add(new Force("xBounce",(bDirection == 1)? 1:3,t[6],-30));
								bounds.forceArchiver.add(new Force("yBounce",2,50,4));
							}
							else
							{
								bounds.forceArchiver.add(new Force("wallsplat",0,3,-30));
								ukemi[0] = 30;
							}
							hitStun = 30;
							bounceStun = 30;
							bounces[(bDirection == 1)? 1:0]++;
							isJuggled = true;
							t[7] = 0;
						}
					}
					else
						pCleared = true;
					break;
					
				case Pleb.TAUNT:
					pCleared = (t[2] == 0 && isTaunted);
					if(t[1] > 0 && !pCleared)
					{
						t[1]--;
						if(t[2] == 0)
							t[2] = ((int)(t[1]/60)%2 == 0)? -1:1;
						t[3] += ((int)(t[1]/60)%2 == 0)? t[2]:-t[2];
						
						if(sTint.size() == 1)
							sTint.add(new double[]{t[3]*-3,t[3],t[3],0,1});
						isTaunted = true;
					}
					else
					{
						pCleared = true;
						isTaunted = false;
					}
					break;
					
				case Pleb.TAYLOR:
					if(t[2] == 0 && t[4] == 0)
					{
						pCleared = isTaunted;
						if(!pCleared)
							propArchiver.add(new Taylor(this,(int)t[1]));
					}
					if(t[1] > 0 && !pCleared)
					{
						t[1]--;
						if(t[2] == 0)
							t[2] = ((int)(t[1]/60)%2 == 0)? -1:1;
						switch((int)t[4])
						{
							case 0:
								t[3] += ((int)(t[1]/60)%2 == 0)? t[2]:-t[2];
								
								if(sTint.size() == 1)
									sTint.add(new double[]{t[3]*-3,t[3],t[3],0,1});
								isTaunted = true;
								break;
								
							case 1:
								if(currAction != null)
								{
									if(currAction.type != Action.DASH && currAction.type != Action.JUMP && fCounter == 0)
										armor = new int[]{2,currAction.frames};
								}
								isBella = true;
								break;
						}
					}
					else
					{
						pCleared = true;
						isTaunted = false;
						isBella = false;
					}
					break;
			}
			
			if(pCleared)
			{
				propertyArchiver.remove(p);
				pLimit = propertyArchiver.size();
				p--;
			}
		}
	}
	
	public void directTo(int x, int y)
	{
		xOffset = x-bounds.xCoord;
		yOffset = y-bounds.yCoord;
	}
	
	public void getHitboxes()
	{
		getHitboxes(currState.getPosition());
	}
	
	public void addSound(String s, float[] i)
	{
		soundArchiver.addLast(s);
		soundInfo.addLast(i);
	}
	
	public void update()
	{
		bounds.yCoord = yCoord;
		bounds.height = height;
		if(currState == PuppetState.KNOCKDOWN)	// && bounds.isGrounded)
		{
			bounds.yCoord += height-kdHeight;
			bounds.height = kdHeight;
		}
		else if(((isCrouching && jDirections[0] == 0 && jDirections[1] == 0 && currState != PuppetState.LANDING && currState != PuppetState.PREJUMP && currState != PuppetState.FLINCH_TRIP0 && currState != PuppetState.FLINCH_TRIP1) || currState == PuppetState.CROUCHING) && !isThrown)
		{
			bounds.yCoord += height-crHeight;
			bounds.height = crHeight;
		}
		if(bounds.isGrounded)
			isAirLocked = false;
		bounds.update();
		
		if(armor[1] > 0)
			armor[1]--;
		if(armor[0] == 0 || armor[1] == 0)
			armor = new int[2];
		
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
		
		isDashing = false;
		for(Force f: bounds.forceArchiver)
		{
			if(f.type.equals("dash") && (f.direction == 1 || f.direction == 3))
				isDashing = true;
		}
		if(isDashing)
		{
			for(Force f: bounds.forceArchiver)
			{
				if(f.type.equals("yJump"))
					f.decay = f.magnitude;
			}
		}
		
		if(blockStun > 0)
			blockStun--;
		
		if(isGuardBroken && hitStun == 0)
		{
			stamina = maxSp;
			isGuardBroken = false;
		}
		
		if(kdStun > 0 && !bounds.isGrounded)
			hitStun = 1;
		else if(hitStun > 0)
			hitStun--;
		else
		{
			isJuggled = false;
			if(bounds.isGrounded)
			{
				juggleDamp = 0;
				launchPoint = 0;
			}
		}
		
		if(bounceStun > 0)
		{
			if(ukemi[1] <= 1)
			{
				if((bDirection == -1 && bounces[0] <= bounceLimit) || (bDirection == 1 && bounces[1] <= bounceLimit))
				{
					for(Organ a: anatomy)
						a.hInvul = true;
					bounds.isGhost = true;
				}
				else
				{
					int fLimit = bounds.forceArchiver.size();
					for(int f = 0; f < fLimit; f++)
					{
						if(bounds.forceArchiver.get(f).type != "wallsplat")
						{
							bounds.forceArchiver.remove(f);
							fLimit = bounds.forceArchiver.size();
							f--;
						}
					}
					floatOverride = true;
				}
			}
			bounceStun--;
		}
		
		if(ukemi[0] > 0)
		{
			if((kdCounter >= kdLimit && bounds.isGrounded) || (bDirection != 0 && (bounces[0] >= bounceLimit || bounces[1] >= bounceLimit)))
				ukemi[0]--;
		}
		else if(kdCounter == 0 && bounceStun == 0 && bounds.isGrounded)
			ukemi[1] = 0;
		if(ukemi[1] > 0)
			throwInvul = true;
		
		if(slipFloat > 0)
			slipFloat--;
		
		if(!isThrowing)
		{
			if(hitInfo[2] > 0)
				hitInfo[2]--;
			else
			{
				hitInfo[1] = 0;
				hitInfo[3] = 0;
			}
		}
		
		if(isPerformingAction)
			fCounter++;
		else
			hitInfo[0] = 0;
		
		if(sCooldown == 0)
		{
			if(stamina < maxSp && hitInfo[1] == 0)
				stamina++;
		}
		else
			sCooldown--;
		
		if(sAngle < 0)
			sAngle += 360;
		if(sAngle >= 360)
			sAngle %= 360;
	}
	
	
	public Organ getBounds()
	{
		return bounds;
	}
	
	protected void getHitboxes(int h)
	{
		anatomy = new ArrayList<Organ>();
		if(currState.getPosition() < hitboxArchiver.size())
		{
			if(currState != prevState)
			{
				sIndex = hitboxArchiver.get(currState.getPosition())[0][1];
				prevState = currState;
			}
		}
		
		if(h < hitboxArchiver.size())
		{
			//MIGHT REMOVE AGAIN, COULD BE PLACED IN PUBLIC METHOD
		/*	if(currState != prevState)
			{
				sIndex = hitboxArchiver.get(PuppetState.valueOf(currState.toString()).ordinal())[0][1];
				prevState = currState;
			}*/
			//===
			
		//	sIndex = 0;	//TEST
			
			int i = (int)sIndex+1-((hitboxArchiver.get(h)[0][3] == 0)? hitboxArchiver.get(h)[0][1]:0);
		/*	if(hitboxArchiver.get(h)[0][3] == 0) //&& sIndex >= hitboxArchiver.get(h)[0][2])
				i -= hitboxArchiver.get(h)[0][1];
		/*	else if(hitboxArchiver.get(h)[0][3] == 1 && sIndex <= hitboxArchiver.get(h)[0][2])
				i += hitboxArchiver.get(h)[0][1];*/
			
			for(int j = 0; j < hitboxArchiver.get(h)[i].length; j += 4)
			{
				int x = (isFacingRight)? hitboxArchiver.get(h)[i][j]+bounds.xCoord+xOffset:bounds.xCoord+bounds.width+xOffset-hitboxArchiver.get(h)[i][j]-hitboxArchiver.get(h)[i][j+2];
				int y = hitboxArchiver.get(h)[i][j+1]+bounds.yCoord+yOffset;
				if(sAngle != 0 && j != 4 && isThrown)
				{
					double d = Math.sqrt(Math.pow(hitboxArchiver.get(h)[i][j]+hitboxArchiver.get(h)[i][j+2]/2-(hitboxArchiver.get(h)[i][4]+hitboxArchiver.get(h)[i][6]/2),2)+Math.pow(hitboxArchiver.get(h)[i][j+1]+hitboxArchiver.get(h)[i][j+3]/2-(hitboxArchiver.get(h)[i][5]+hitboxArchiver.get(h)[i][7]/2),2));
					x = ((isFacingRight)? hitboxArchiver.get(h)[i][4]+bounds.xCoord+xOffset:bounds.xCoord+bounds.width+xOffset-hitboxArchiver.get(h)[i][4]-hitboxArchiver.get(h)[i][6])+(int)(d*Math.cos(Math.toRadians(sAngle+((j == 0 || (j == 2 && isFacingRight))? -90:90)))+0.5);
					y = hitboxArchiver.get(h)[i][5]+(int)(d*Math.sin(Math.toRadians(sAngle+((j == 0 ^ isFacingRight)? -90:90)))+0.5)+bounds.yCoord+yOffset;
				}
				anatomy.add(new Organ(x,y,hitboxArchiver.get(h)[i][j+2],hitboxArchiver.get(h)[i][j+3],speed));
			}
				
			int f = (int)sIndex+((hitboxArchiver.get(h)[0][3] == 1 && sIndex != (int)sIndex)? 1:0);
			
			boolean isFlinching = false;
			if(hitStun > 0 && currState.getState().length() >= 6 && currState.getState().substring(0,6).equals("FLINCH"))	//4-(PuppetState.values().length-currState.getPosition()) >= 0)
			{
				if(hitStun > hitboxArchiver.get(h).length-flinchPoints[currState.getPosition()-PuppetState.FLINCH_STANDING0.getPosition()]+1 && sIndex == flinchPoints[currState.getPosition()-PuppetState.FLINCH_STANDING0.getPosition()])
					isFlinching = true;
			}
			if(!isFlinching)
				sIndex += (hitboxArchiver.get(h)[0][3] == 0)? (1.0/hitboxArchiver.get(h)[0][4]):(-1.0/hitboxArchiver.get(h)[0][4]);
			
			if(Math.abs(sIndex-f) >= 1)
			{
				sIndex = (int)sIndex;
				i += (hitboxArchiver.get(h)[0][3] == 0)? 1:-1;
				
				if(preFrames > 0)
					preFrames--;
			}
			if((hitboxArchiver.get(h)[0][3] == 0 && i >= hitboxArchiver.get(h).length) || (hitboxArchiver.get(h)[0][3] == 1 && i <= 0))
				sIndex = hitboxArchiver.get(h)[0][2];
		}
	}
	
	private void juggleCheck(Puppet p, boolean h)
	{
		double[] y = new double[]{(h)? ((p.bounds.isGrounded)? 50:30):25,2};
		for(Force j: p.bounds.forceArchiver)
		{
			if(j.direction == 0 && !p.bounds.isGrounded)
			{
				y[0] -= j.magnitude;
				y[1] -= j.decay;
			}
			if(j.direction == 2 && !j.type.equals("yPursuit") && !j.type.equals("yJump"))
			{
				y[0] += j.magnitude;
				y[1] += j.decay;
			}
		}
		
		if(y[0] > 0 && y[1] > 0)
		{
			double[] x = new double[]{0,0,0};
			boolean yExists = false;
			for(Force j: bounds.forceArchiver)
			{
				if(j.type.equals("xKnockback"))
				{
					if(otgDamp > 0)
						j.magnitude *= 0.2;
					x[0] = j.magnitude*0.8;
					x[1] = j.decay;
					x[2] = j.direction;
				}
				
				if(j.type.equals("yKnockback") && launchPoint > 0)
					j.magnitude -= y[0]*(1+juggleDamp-otgDamp);
				if(j.type.equals("juggle"))
				{
					j.magnitude = y[0]*(1+juggleDamp-otgDamp);
					j.decay = y[1];
					yExists = true;
				}
			}
			if(!yExists)
				bounds.forceArchiver.add(new Force("juggle",2,y[0]*(1+juggleDamp-otgDamp),y[1]));
			
			if(!p.bounds.isGrounded && p.hitInfo[1] > 1 && (launchPoint == 0 || launchPoint < p.hitInfo[1]))
			{
				isJuggled = true;
				yExists = false;
				for(Force j: p.bounds.forceArchiver)
				{
					if((j.direction == 1 || j.direction == 2) && x[2] != -1)
						j.magnitude = 0;
					if(j.type.equals("yPursuit"))
					{
						j.magnitude = ((h)? 50:25);
						yExists = true;
					}
				}
				if(x[2] != -1 && ((p.isFacingRight && p.jDirections[0] == 1) || (!p.isFacingRight && p.jDirections[0] == -1)))
					p.bounds.forceArchiver.add(new Force("xPursuit",(int)x[2],x[0]*0.9,x[1]));
				if(!yExists && p.bounds.yCoord+p.bounds.height >= bounds.yCoord)
					p.bounds.forceArchiver.add(new Force("yPursuit",2,((h)? 50:25),2));
				
				kdStun = 0;
				otgDamp = 0;
				juggleDamp += 0.08;
			}
			else if(otgDamp > 0)
			{
				kdCounter++;
				otgDamp -= 0.04;
			}
		}
	}
	
	
	public class LightPunch extends Action
	{
		public LightPunch()
		{
			super(Action.NORMAL,1,new int[][]{new int[]{},new int[]{},new int[]{}},new boolean[]{false,false,false},new boolean[]{false,false,false},new boolean[]{false,false,false},new boolean[]{false,false,false},new int[]{0,0,0,0,0,0},new boolean[]{true,true,true});
		}
		
		public void perform(int f){}
	}
	
	public class MediumPunch extends Action
	{
		public MediumPunch()
		{
			super(Action.NORMAL,1,new int[][]{new int[]{},new int[]{},new int[]{}},new boolean[]{false,false,false},new boolean[]{false,false,false},new boolean[]{false,false,false},new boolean[]{false,false,false},new int[]{0,0,0,0,0,0},new boolean[]{true,true,true});
		}
		
		public void perform(int f){}
	}
	
	public class HeavyPunch extends Action
	{
		public HeavyPunch()
		{
			super(Action.NORMAL,1,new int[][]{new int[]{},new int[]{},new int[]{}},new boolean[]{false,false,false},new boolean[]{false,false,false},new boolean[]{false,false,false},new boolean[]{false,false,false},new int[]{0,0,0,0,0,0},new boolean[]{true,true,true});
		}
		
		public void perform(int f){}
	}
	
	public class LightKick extends Action
	{
		public LightKick()
		{
			super(Action.NORMAL,1,new int[][]{new int[]{},new int[]{},new int[]{}},new boolean[]{false,false,false},new boolean[]{false,false,false},new boolean[]{false,false,false},new boolean[]{false,false,false},new int[]{0,0,0,0,0,0},new boolean[]{true,true,true});
		}
		
		public void perform(int f){}
	}
	
	public class MediumKick extends Action
	{
		public MediumKick()
		{
			super(Action.NORMAL,1,new int[][]{new int[]{},new int[]{},new int[]{}},new boolean[]{false,false,false},new boolean[]{false,false,false},new boolean[]{false,false,false},new boolean[]{false,false,false},new int[]{0,0,0,0,0,0},new boolean[]{true,true,true});
		}
		
		public void perform(int f){}
	}
	
	public class HeavyKick extends Action
	{
		public HeavyKick()
		{
			super(Action.NORMAL,1,new int[][]{new int[]{},new int[]{},new int[]{}},new boolean[]{false,false,false},new boolean[]{false,false,false},new boolean[]{false,false,false},new boolean[]{false,false,false},new int[]{0,0,0,0,0,0},new boolean[]{true,true,true});
		}
		
		public void perform(int f){}
	}
	
	public class Hug extends Action
	{
		int techWindow, tCounter;
		
		public Hug(int t)
		{
			super(Action.GRAB,2,
					new int[][]{new int[]{}, new int[]{}, new int[]{}},
					new boolean[]{false,false,false},
					new boolean[]{false,false,false},
					new boolean[]{false,false,false},
					new boolean[]{false,false,false},
					new int[]{10,120,-1,-1,-1,-1},
					new boolean[]{true,true,true});
			techWindow = t;
			tCounter = 0;
		}
		
		public void perform(int f)
		{
			if(target != null)
			{
				if(target.isTeching && tCounter < techWindow)
				{
					target.currState = PuppetState.HUG_BREAK;
					target.sIndex = target.hitboxArchiver.get(target.currState.getPosition())[0][1];
					target.hitStun = 15;
					target.xOffset = 0;
					target.yOffset = 0;
					target.sAngle = 0;
					target.isThrown = false;
					
					currState = PuppetState.HUG_BREAK;
					sIndex = hitboxArchiver.get(currState.getPosition())[0][1];
					hitStun = 15;
					xOffset = 0;
					yOffset = 0;
					sAngle = 0;
					isTeching = true;
				}
				else
					target.isTeching = false;
				tCounter++;
			}
			jDirections[1] = 0;
		}
	}
	
	
	public class Jump extends Action
	{
		int direction;
		public Jump(int d)
		{
			super(Action.JUMP,0,
				new int[][]{new int[]{}, new int[]{}, new int[]{}},
				new boolean[]{false,false,false},
				new boolean[]{false,false,false},
				new boolean[]{false,false,false},
				new boolean[]{false,false,false},
				new int[]{-1,-1,-1,-1,-1,-1},
				new boolean[]{true,true,false});
			direction = d;
			frames = 3;
		}
		
		public void perform(int f)
		{
		/*	if(airOptions > aDash+jCount && jCount < jumpLimit && !isBlocking[0] && !isBlocking[1])
			{
				if(bounds.isGrounded || (!bounds.isGrounded && jDirections[2] == 0 && preFrames == 0))
				{
					jDirections[0] = direction;
					if(jDirections[2] == 0)
						jDirections[2] = 1;
					jump();
				}
			}*/
			
			isPerformingAction = true;
			if(f >= frames)
			{
				isPerformingAction = false;
				target = null;
				return;
			}
			else if(f == 0)
			{
				if(airOptions > aDash+jCount && jCount < jumpLimit && !isBlocking[0] && !isBlocking[1] && (bounds.isGrounded || (!bounds.isGrounded && jDirections[2] == 0 && preFrames == 0)))
				{
			//		if(direction != 0)
						jDirections[0] = direction;
					if(jDirections[2] == 0)
						jDirections[2] = 1;
					jump();
				}
				else
					isPerformingAction = false;
			}
		}
	}
	
	
	public class Taylor extends Prop	// ITS JOKE
	{
		public Taylor(Puppet p, int t)
		{
			super(0,0,0,0,t,9999);
			puppet = p;
			spriteParams = new int[]{-70,-100,140,163};
			spriteArchiver.add(new int[]{0,0,0,0,2});
		}
		
		public void draw(Graphics2D g, ImageObserver i, SpriteReader s, double w, double h, boolean[] d)
		{
			try
			{
			//	Image taylor = Toolkit.getDefaultToolkit().getImage(System.getProperty("user.dir")+"\\resources\\lithead.png");
				Image taylor = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resources/lithead.png"));
				g.drawImage(taylor,(int)((anatomy.get(0).xHosh+anatomy.get(0).width/2+spriteParams[0])*w/1280),(int)((anatomy.get(0).yHosh+anatomy.get(0).height/2+spriteParams[1])*h/720),(int)(spriteParams[2]*w/1280),(int)(spriteParams[3]*h/720),i);
			}
			catch(java.lang.IndexOutOfBoundsException e)
			{
				draw(g,i,s,w,h,d);
			}
		}
		
		public void update()
		{
			super.update();
			health--;
		}
	}
}