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
//	ArrayList<int[]> touchArchiver, actionList, spriteArchiver;
	ArrayList<int[][]> hitboxArchiver;
	ArrayList<Pleb> plebsIn, plebsOut;
	ArrayList<Prop> propArchiver;
	ArrayList<String> plebArchiver;
	Action[] normals;
	
//	ArrayList<Force> forceArchiver;
	Organ bounds, grabBox;	//Name subject to change, could use bounds as throwable hitbox
	State currState, prevState;
	Action currAction;
	int id, xCoord, yCoord, xHosh, yHosh, width, height, crHeight, kdHeight;
	int maxHp, maxSp, maxMp, maxSpd;
	int health, stamina, meter, speed;
	int preFrames, fCounter, hitStun, hitStop, sCooldown;
	int kdCounter, kdLimit, kdStun;
	double sIndex, jForce, jump, hitstunDamp;
	boolean isFacingRight, isPerformingAction, isCrouching, canBlock, isGuardBroken, isLaunched, isUnstoppable;	//, isJumping;
	int[] hitInfo, flinchPoints, jDirections, spriteParams;	//Add tint later
	boolean[] isBlocking;
	
	public enum PuppetState implements State
	{
		IDLE, CROUCH, STANDING, CROUCHING, WALK_FORWARD, WALK_BACKWARD, FALL_NEUTRAL, FALL_FORWARD, FALL_BACKWARD, LANDING, PREJUMP, JUMP_NEUTRAL, JUMP_FORWARD, JUMP_BACKWARD, 	//, PERFORM_ACTION
		GUARD_STANDING, GUARD_CROUCHING, GUARD_JUMPING, FLINCH_STANDING0, FLINCH_STANDING1, FLINCH_STANDING2, FLINCH_CROUCHING, FLINCH_TRIP0, FLINCH_TRIP1, FLINCH_AERIAL0, FLINCH_AERIAL1, FLINCH_AERIAL2, KNOCKDOWN, FALLING, BREAK_GROUND, BREAK_AIR;
		
		public String getState()
		{
			return name();
		}
		
		public int getPosition()
		{
			return ordinal();
		}
	}
	
	public Puppet(int x, int y, int w, int h, int c, int k, int hp, int sp, int mp, int s, double j, boolean r, boolean f2)
	{
		anatomy = new ArrayList<Organ>();
	//	plebArchiver = new ArrayList<Pleb>();
	//	touchArchiver = new ArrayList<int[]>();	//[type, id]
		hitboxArchiver = new ArrayList<int[][]>(); //[sheet.y, sheet.xStart, sheet.xLoop, reversed?, frame delay], [[hitbox.x, hitbox.y, hitbox.w, hitbox.h, ...], ...
	//	actionList = new ArrayList<int[]>();	//[action name, sprites in row, loops?]
	//	spriteArchiver = new ArrayList<int[]>();	//[xMod,yMod,width,height,sWidth,sHeight]
		plebsIn = new ArrayList<Pleb>();
		plebsOut = new ArrayList<Pleb>();
		propArchiver = new ArrayList<Prop>();
		plebArchiver = new ArrayList<String>();
		
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
		kdHeight = k;
		isFacingRight = r;
		isPerformingAction = false;
		canBlock = false;
		isGuardBroken = false;
		isLaunched = false;
		isUnstoppable = false;	// Unaffected by new forces through damage?
	//	isJumping = false;
		
		hitInfo = new int[]{0,0,0};	//[type, number of hits, enemy hitstun]
		flinchPoints = new int[]{0,0,0,0,0,0,0,0,0,0}; 	//marks points where sprite freezes during hitstun
		jDirections = new int[]{0,0};
		isBlocking = new boolean[]{false,false};
		
		maxHp = hp;
		maxSp = sp;
		maxMp = mp;
		maxSpd = s;
		jForce = j;
		
		health = maxHp;
		stamina = maxSp;	//0;
		meter = 0;
		speed = maxSpd;
		jump = jForce;
		
		preFrames = 0;
		fCounter = 0;
		sIndex = 0;
		hitStun = 0;
		hitStop = 0;
		hitstunDamp = 0;
		sCooldown = 0;		//999999;
		
		kdCounter = 0;
		kdLimit = 2;	// if counter >= limit, cannot be knocked down in combo
		kdStun = 0;
		
		bounds =  new Organ(x,y,w,h,speed);
		bounds.isFloating = f2;
		bounds.isMovable = true;
	//	touchArchiver.add(new int[]{-1});
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
				g.fillRect((int)(xHosh*w/1280),(int)(yHosh*h/720),(int)(10*w/1280),(int)(10*h/720));
				
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
			case "PREJUMP":
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
			case "BREAK_GROUND":
			case "BREAK_AIR":
				flinch();
				break;
				
			case "KNOCKDOWN":
				knockdown();
				break;
		}
		
		xCoord = bounds.xCoord;
		if(bounds.height == height)	//((!bounds.isGrounded || currState == PuppetState.LANDING) && currState != PuppetState.GUARD_CROUCHING && currState != PuppetState.FLINCH_CROUCHING)
			yCoord = bounds.yCoord;
		
		if(currState.getPosition() < hitboxArchiver.size())
		{
			if(currState != prevState)
			{
				sIndex = hitboxArchiver.get(currState.getPosition())[0][1];
				prevState = currState;
			}
		}
	}
	
	public void setAction(Action a)
	{
		if(currAction == null)
		{
			currAction = a;
			a.button = -1;
		}
		else if(currAction.isCancelable(hitInfo[0],fCounter,currAction.type,currAction.button,bounds.isGrounded))
		{
			currAction = a;
			a.button = -1;
			fCounter = 0;
			sIndex = hitboxArchiver.get(currState.getPosition())[0][1];
		}
	}
	
	public void performAction()
	{
		currAction.perform(fCounter);
		bounds.xDir = 0;
		bounds.xDrag = 0;
		
		if(!isPerformingAction)
		{
			currAction = null;
			currState = (isCrouching)? PuppetState.CROUCH:PuppetState.IDLE;
			fCounter = 0;
		}
	}
	
	public void idle()
	{
		if(isBlocking[0] || isBlocking[1])
		{
			currState = (bounds.isGrounded)? ((isBlocking[0])? PuppetState.GUARD_STANDING:PuppetState.GUARD_CROUCHING):PuppetState.GUARD_JUMPING;
			return;
		}
		
		if(jDirections[1] == 1)
		{
			if(preFrames > 0)
			{
				currState = PuppetState.PREJUMP;
				return;
			}
			else
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
	}
	
	public void move()
	{
		bounds.move();
		if(isBlocking[0] || isBlocking[1])
		{
			currState = (isBlocking[0])? PuppetState.GUARD_STANDING:PuppetState.GUARD_CROUCHING;
			return;
		}
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
	
	public void takeDamage(Pleb p, Hitbox[] c)
	{
		boolean hitSuccessful = false;
		bounds.forceArchiver = new ArrayList<Force>();
		switch(p.direction)
		{
			case 0:
				if(!isBlocking[0] && !isBlocking[1])
					hitSuccessful = true;
				break;
				
			case 1:
				if(!isBlocking[1])
					hitSuccessful = true;
				break;
				
			case 2:
				if(!isBlocking[0])
					hitSuccessful = true;
				break;
		}
		
		if(hitSuccessful && kdCounter < kdLimit)
		{
			health -= (!isGuardBroken)? p.hDamage:(int)((double)p.hDamage*2.5+0.5);
			if(health < 0)
				health = 0;
			
			if(!bounds.isGrounded)
				currState = PuppetState.FLINCH_AERIAL0;
			else if(isCrouching)
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
			
			switch(p.strength)
			{
				case 0:
					hitStun = 10;
					hitStop = 5;
					break;
				case 1:
					hitStun = 15;
					hitStop = 6;
					break;
				case 2:
					hitStun = 22;
					hitStop = 7;
					break;
				case 3:	//Change later
					hitStun = 10;
					hitStop = 10;
					break;
			}
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
								currState = (bounds.isGrounded)? PuppetState.FLINCH_TRIP0:PuppetState.FLINCH_TRIP1;
								p.appliedForces.add(new Force("knockdown",2,t[3],t[4]));
								bounds.yCoord = yCoord;
								bounds.height = height;
								kdCounter += t[2];
								kdStun = (int)t[5];
								isCrouching = false;
							}
							break;
							
						case Pleb.LAUNCH:
							currState = PuppetState.FLINCH_AERIAL1;
							for(Force f: bounds.forceArchiver)
								f.magnitude = 0;
							for(Force f: p.appliedForces)
								f.magnitude = 0;
							bounds.forceArchiver.add(new Force("xLaunch",(p.puppet.isFacingRight)? 3:1,t[3],t[4]));
							bounds.forceArchiver.add(new Force("yLaunch",2,t[5],t[6]));
							hitStun = (int)t[7];
				//			isLaunched = true;
							isUnstoppable = (t[1] == 0)? false:true;
							break;
					}
				}
			}
			sIndex = hitboxArchiver.get(currState.getPosition())[0][1];
			
			p.puppet.hitInfo[0] = 2;
			p.puppet.hitInfo[2] = hitStun;
			p.puppet.hitInfo[1]++;
			p.puppet.stamina = p.puppet.maxSp;
			if(p.puppet.currAction != null)
				p.puppet.currAction.target = this;
		}
		else
		{
			stamina -= p.sDamage;
			if(stamina <= 0)
			{
				stamina = 0;
				currState = (bounds.isGrounded)? PuppetState.BREAK_GROUND:PuppetState.BREAK_AIR;
				isGuardBroken = true;
				hitStun = 90;
				hitStop = 30;
			}
			else
			{
				switch(p.strength)
				{
					case 0:
						hitStun = 10;
						hitStop = 5;
						break;
					case 1:
						hitStun = 10;
						hitStop = 6;
						break;
					case 2:
						hitStun = 10;
						hitStop = 7;
						break;
					case 3:
						hitStun = 10;
						hitStop = 10;
						break;
				}
			}
			
			p.puppet.hitInfo[0] = 1;
			p.puppet.hitInfo[2] = hitStun;
		//	p.puppet.hitInfo[1]++;
		}
		sCooldown = 120;
		
		if(!isUnstoppable)
		{
			for(Force f: p.appliedForces)
			{
				if(f.type.equals("xKnockback") && !p.isProjectile && ((f.direction == 1 && bounds == c[0]) || (f.direction == 3 && bounds == c[1])))
				{
					f.direction = (f.direction+2)%4;
					p.puppet.bounds.forceArchiver.add(f);
				}
				else
					bounds.forceArchiver.add(f);
				
				if(!bounds.isGrounded)
					bounds.forceArchiver.add(new Force("juggle",2,24,2));
			}
			
			if(p.puppet.hitInfo[0] == 2)
				p.puppet.stamina = p.puppet.maxSp;
		}
	}
	
	public void guard()
	{
		if(hitStun > 0)
			hitStun--;
		else if(!isBlocking[0] && !isBlocking[1] && hitStun == 0)
		{
			plebArchiver.clear();
			currState = (!isCrouching)? PuppetState.IDLE:PuppetState.CROUCH;
		}
	}
	
	public void flinch()
	{
	/*	if(hitStun > 0)
			hitStun--;
		else*/
		if(hitStun <= 0)
		{
			isUnstoppable = false;
			if(kdStun > 0)
			{
				if(bounds.isGrounded)
				{
					currState = PuppetState.KNOCKDOWN;
					hitStun = kdStun;
				}
			/*	else
					hitInfo[2] = 1;*/
			}
			else
			{
				kdCounter = 0;
				plebArchiver.clear();
				currState = (!isCrouching)? PuppetState.IDLE:PuppetState.CROUCH;
			}
		}
		jDirections = new int[]{0,0};
	}
	
	public void knockdown()
	{
		if(kdStun > 0)
			kdStun--;
		else
		{
			kdCounter = 0;
			plebArchiver.clear();
			currState = (!isCrouching)? PuppetState.IDLE:PuppetState.CROUCH;
		}
	}
	
	public void getHitboxes()
	{
		getHitboxes(currState.getPosition());
	}
	
	public void update()
	{
		bounds.yCoord = yCoord;
		bounds.height = height;
		if(currState == PuppetState.KNOCKDOWN && bounds.isGrounded)
		{
			bounds.yCoord += height-kdHeight;
			bounds.height = kdHeight;
		}
		else if((isCrouching && jDirections[0] == 0 && jDirections[1] == 0 && currState != PuppetState.LANDING && currState != PuppetState.PREJUMP && currState != PuppetState.FLINCH_TRIP0 && currState != PuppetState.FLINCH_TRIP1) || currState == PuppetState.CROUCHING)
		{
			bounds.yCoord += height-crHeight;
			bounds.height = crHeight;
		}
		bounds.update();
		
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
		
		if(isGuardBroken && hitStun == 0)
		{
			stamina = maxSp;
			isGuardBroken = false;
		}
		
		if(hitStun > 0)
			hitStun--;
		if(hitInfo[2] > 0)
			hitInfo[2]--;
		else
		{
			hitInfo[0] = 0;
			hitInfo[1] = 0;
		}
		
		if(isPerformingAction)
			fCounter++;
		
		if(sCooldown == 0)
		{
			if(stamina < maxSp && hitInfo[1] == 0)
				stamina++;
		}
		else
			sCooldown--;
	}
	
	protected void getHitboxes(int h)
	{
		anatomy = new ArrayList<Organ>();
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
				anatomy.add(new Organ((isFacingRight)? hitboxArchiver.get(h)[i][j]+bounds.xCoord:bounds.xCoord+bounds.width-hitboxArchiver.get(h)[i][j]-hitboxArchiver.get(h)[i][j+2],hitboxArchiver.get(h)[i][j+1]+bounds.yCoord,hitboxArchiver.get(h)[i][j+2],hitboxArchiver.get(h)[i][j+3],speed));
				
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
	
	
	public class LightPunch extends Action
	{
		public LightPunch()
		{
			super(Action.NORMAL,1,new int[]{},new boolean[]{false,false,false},new boolean[]{false,false,false},new boolean[]{false,false,false},new boolean[]{false,false,false},new int[]{0,0,0,0,0,0},new boolean[]{true,true});
		}
		
		public void perform(int f){}
	}
	
	public class MediumPunch extends Action
	{
		public MediumPunch()
		{
			super(Action.NORMAL,1,new int[]{},new boolean[]{false,false,false},new boolean[]{false,false,false},new boolean[]{false,false,false},new boolean[]{false,false,false},new int[]{0,0,0,0,0,0},new boolean[]{true,true});
		}
		
		public void perform(int f){}
	}
	
	public class HeavyPunch extends Action
	{
		public HeavyPunch()
		{
			super(Action.NORMAL,1,new int[]{},new boolean[]{false,false,false},new boolean[]{false,false,false},new boolean[]{false,false,false},new boolean[]{false,false,false},new int[]{0,0,0,0,0,0},new boolean[]{true,true});
		}
		
		public void perform(int f){}
	}
	
	public class LightKick extends Action
	{
		public LightKick()
		{
			super(Action.NORMAL,1,new int[]{},new boolean[]{false,false,false},new boolean[]{false,false,false},new boolean[]{false,false,false},new boolean[]{false,false,false},new int[]{0,0,0,0,0,0},new boolean[]{true,true});
		}
		
		public void perform(int f){}
	}
	
	public class MediumKick extends Action
	{
		public MediumKick()
		{
			super(Action.NORMAL,1,new int[]{},new boolean[]{false,false,false},new boolean[]{false,false,false},new boolean[]{false,false,false},new boolean[]{false,false,false},new int[]{0,0,0,0,0,0},new boolean[]{true,true});
		}
		
		public void perform(int f){}
	}
	
	public class HeavyKick extends Action
	{
		public HeavyKick()
		{
			super(Action.NORMAL,1,new int[]{},new boolean[]{false,false,false},new boolean[]{false,false,false},new boolean[]{false,false,false},new boolean[]{false,false,false},new int[]{0,0,0,0,0,0},new boolean[]{true,true});
		}
		
		public void perform(int f){}
	}
}