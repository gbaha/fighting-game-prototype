import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.ImageObserver;
import java.util.ArrayList;

public class Player extends Puppet
{
	ArrayList<int[][]> movelist;	//[stick inputs, button inputs, input delay, cancels from?[action, type(standing, crouching, air), starting frame], move(int method?)]
	Action[] actions;
	int airOptions, airDashLimit, jumpLimit, aDash, jCount;
	boolean isDashing, isHoming, isJumping;
	boolean[] sInputs;
	
	public enum PlayerState implements State
	{
		PuppetState, DASH_FORWARD, DASH_BACKWARD, JUMP_HOMING1, JUMP_HOMING2,
		HUG_START, HUG_HOLD, HUG_HIT, HUG_FORWARD, HUG_UPWARD, HUG_DOWNWARD,
		STANDING_LP, STANDING_MP, STANDING_HP, STANDING_LK, STANDING_MK, STANDING_HK, 
		CROUCHING_LP, CROUCHING_MP, CROUCHING_HP, CROUCHING_LK, CROUCHING_MK, CROUCHING_HK, 
		JUMPING_LP, JUMPING_MP, JUMPING_HP, JUMPING_LK, JUMPING_MK, JUMPING_HK;
	
		public String getState()
		{
			return name();
		}
		
		public int getPosition()
		{
			return Puppet.PuppetState.values().length+ordinal()-1;
		}
	}
	
	public Player(int x, int y, int w, int h, int c, int k, /*int e,*/ int s, int a1, int a2, int j1, double j2, boolean r)
	{
		super(x,y,w,h,c,k,1000,600,5000,s,j2,r,false);
//		currState = PuppetState.IDLE;
//		prevState = PuppetState.IDLE;
		isDashing = false;
		airOptions = a1;
		airDashLimit = a2;
		jumpLimit = j1;
		aDash = 0;
		jCount = 0;
		meter = 1000;
		
		movelist = new ArrayList<int[][]>();	//PLACE MOVELIST ITEMS IN ORDER OF PRIORITY!!!!
		movelist.add(new int[][]{{6,5,6},{-1,-1,-1},{0,10,10}});
		movelist.add(new int[][]{{9,5,6},{-1,-1,-1},{0,10,10}});
		movelist.add(new int[][]{{4,5,4},{-1,-1,-1},{0,10,10}});
		movelist.add(new int[][]{{7,5,4},{-1,-1,-1},{0,10,10}});
		movelist.add(new int[][]{{8},{-1},{0},{2,1}});
		movelist.add(new int[][]{{9},{-1},{0},{2,1}});
		movelist.add(new int[][]{{-1,-1},{0,3},{0,1}});
		movelist.add(new int[][]{{-1,-1},{3,0},{0,1}});
		
		actions = new Action[]{new FrontDash(), new FrontDash(), new BackDash(), new BackDash(), new HomingJump(), new HomingJump(), new Hug(10), new Hug(10)};
		sInputs = new boolean[4];
	}
	
	public void reset(int x, int y) //TEST
	{
		isCrouching = false;
		xCoord = x;
		yCoord = y;
		bounds.xCoord = x;
		bounds.yCoord = y;
		bounds.xDir = 0;
		bounds.xDrag = 0;
		bounds.yDir = 0;
		bounds.yDrag = 0;
		bounds.forceArchiver = new ArrayList<Force>();
		isPerformingAction = false;
		currAction = null;
		currState = PuppetState.IDLE;
		isDashing = false;
		isHoming = false;
		aDash = 0;
		jCount = 0;
		meter = 1000;
		sInputs = new boolean[4];
	}
	
	public void draw(Graphics2D g, ImageObserver i, SpriteReader s, double w, double h, boolean d)
	{
		super.draw(g,i,s,w,h,d);
		g.setColor(Color.BLUE);
		g.drawString(currState.getState()+"",(int)((bounds.xHosh+bounds.width+2)*w/1280),(int)((bounds.yHosh)*h/720));
		g.drawString((int)sIndex+"",(int)((bounds.xHosh+bounds.width+2)*w/1280),(int)((bounds.yHosh+bounds.height*3/4)*h/720));
		g.drawString(fCounter+"",(int)((bounds.xHosh+bounds.width+2)*w/1280),(int)((bounds.yHosh+bounds.height*5/6)*h/720));
		g.setColor(Color.RED);
		g.drawString(hitStun+"",(int)(bounds.xHosh*w/1280),(int)((bounds.yHosh+bounds.height+20)*h/720));
		g.drawString(juggleDamp+"",(int)(bounds.xHosh*w/1280),(int)((bounds.yHosh+bounds.height+40)*h/720));
		g.drawString(hitInfo[0]+"",(int)((bounds.xHosh+50)*w/1280),(int)((bounds.yHosh+bounds.height+20)*h/720));
		g.drawString(jDirections[1]+"",(int)((bounds.xHosh+bounds.width+2)*w/1280),(int)((bounds.yHosh+bounds.height+20)*h/720));
		
	}
	
	public void checkState()
	{
		switch(currState.getState())
		{
			case "JUMP_NEUTRAL":
			case "JUMP_FORWARD":
			case "JUMP_BACKWARD":
				jump();
				break;
			
			case "DASH_FORWARD":
			case "DASH_BACKWARD":
			case "JUMP_HOMING1":
			case "JUMP_HOMING2":
			case "STANDING_LP":
			case "STANDING_MP":
			case "STANDING_HP":
			case "STANDING_LK":
			case "STANDING_MK":
			case "STANDING_HK":
			case "CROUCHING_LP":
			case "CROUCHING_MP":
			case "CROUCHING_HP":
			case "CROUCHING_LK":
			case "CROUCHING_MK":
			case "CROUCHING_HK":
			case "JUMPING_LP":
			case "JUMPING_MP":
			case "JUMPING_HP":
			case "JUMPING_LK":
			case "JUMPING_MK":
			case "JUMPING_HK":
			case "HUG_START":
			case "HUG_HOLD":
			case "HUG_HIT":
			case "HUG_FORWARD":
			case "HUG_UPWARD":
			case "HUG_DOWNWARD":
				performAction();
				break;
		}
		super.checkState();
	}
	
	public void setAction(Action a)
	{
		if(currAction == null)
		{
			if((a != actions[0] && a != actions[2]) || aDash < airDashLimit)
			{
				currAction = a;
				a.target = null;
				a.button = -1;
				
				if(!bounds.isGrounded)
					jDirections[1] = 0;
				isHoming = false;
			}
		}
		else //if(currAction.isCancelable(hitInfo[0],fCounter,currAction.type,currAction.button))
		{
			if(currAction.target != null)
			{
				currAction.target.xOffset = 0;
				currAction.target.yOffset = 0;
				currAction.target.sAngle = 0;
			}
			
			bounds.botOffset = 0;
			xOffset = 0;
			yOffset = 0;
			sAngle = 0;
			isThrowing = false;
			
			currAction = a;
			a.target = null;
			a.button = -1;
			fCounter = 0;
	//		sIndex = hitboxArchiver.get(currState.getPosition())[0][1];
			
			if(!bounds.isGrounded)
				jDirections[1] = 0;
			isHoming = false;
		}
	}
	
/*	public void performAction()
	{
		currAction.perform(fCounter);
		if(!isPerformingAction)
		{
			currAction = null;
			currState = PuppetState.IDLE;
			fCounter = 0;
		}
	}*/
	
	public void idle()
	{
		if(currAction != null && currState != PuppetState.PREJUMP)
		{
			performAction();
			return;
		}
		
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
		
		if(jDirections[1] == 1)
		{
			if(preFrames > 0)
			{
				currState = PuppetState.PREJUMP;
				return;
			}
			else
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
		}
		
		if((!bounds.isGrounded /*&& jDirections[0] == 0*/ && jDirections[1] < 1) /*!isJumping)*/ || currState == PuppetState.FALL_NEUTRAL || currState == PuppetState.FALL_FORWARD || currState == PuppetState.FALL_BACKWARD || currState == PuppetState.LANDING)
		{
			if(currState != PuppetState.LANDING)
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
		
		if(jDirections[1] == 1)
		{
			if(preFrames > 0)
			{
				currState = PuppetState.PREJUMP;
				return;
			}
			else
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
		}
		super.crouch();
	}
	
	public void move()
	{
	//	bounds.move();
		if(isCrouching)
			bounds.xVel = 0;
		if(currAction != null)
		{
	//		currState = PuppetState.PERFORM_ACTION;
			performAction();
			return;
		}
		
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
		}
		super.move();
		
/*		if(!bounds.isGrounded && jDirections[1] == 0) //!isJumping)
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
			currState = PuppetState.IDLE;*/
	}
	
	public void jump()
	{
		if(jCount < jumpLimit && airOptions > aDash+jCount && isJumping && !bounds.isFloating)
		{
			int fLimit = bounds.forceArchiver.size();
			for(int f = 0; f < fLimit; f++)
			{
				if(bounds.forceArchiver.get(f).type.equals("yJump") || bounds.forceArchiver.get(f).direction == 1 || bounds.forceArchiver.get(f).direction == 3)
				{
					bounds.forceArchiver.remove(f);
					fLimit = bounds.forceArchiver.size();
					f--;
				}
			}
			
			bounds.forceArchiver.add(new Force("yJump",2,jump,1));
			jDirections[1] = 1;
			isJumping = false;
			jCount++;
		}
		bounds.botOffset = 0;
		
		if(currAction != null)
		{
			bounds.isGrounded = false;
			performAction();
			return;
		}
		
		if(isBlocking[0] || isBlocking[1])
		{
			currState = (bounds.isGrounded)? ((isBlocking[0])? PuppetState.GUARD_STANDING:PuppetState.GUARD_CROUCHING):PuppetState.GUARD_JUMPING;
			return;
		}
		
/*		if(jDirections[1] == 1)
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
		else*/
		if(jDirections[1] < 1)
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
	
	public void getHitboxes()
	{
		super.getHitboxes(currState.getPosition());
	}
	
	public void update()
	{
	//	HITBOX FRAME TEST
	//	bounds.isGrounded = false; bounds.isFloating = true;
	//	currState = PlayerState.JUMPING_MK; setAction(normals[4]); sIndex = 2; fCounter = 0; currAction.frames = 30;
		
	//	super.update();
		if(bounds.isGrounded)
		{
	/*		for(Force f: bounds.forceArchiver)
			{
				if(f.type.equals("xJump"))
					f.magnitude = 0;
			}*/
			if(jDirections[1] == 0)
				jDirections[0] = 0;
			
			aDash = 0;
			jCount = 0;
		}
		else
		{
			if(jDirections[0] != 0 && preFrames == 0)
				bounds.forceArchiver.add(new Force("xJump",(jDirections[0] == 1)? 3:1,6,6));
			bounds.xDir = 0;
			bounds.xDrag = 0;
		}
		
		boolean f = !bounds.isGrounded && (isJuggled || isDashing || isHoming || isThrown || isTeching); // || other isFloating checks;
		bounds.wasFloating = bounds.isFloating  && !f;
		bounds.isFloating = f;
		if(f)
		{
			for(Force j: bounds.forceArchiver)
			{
				if(j.type.equals("yJump"))
					j.magnitude = 0;
			}
		}
		
		super.update();
		bounds.update();
	}
	
	
	private class FrontDash extends Action
	{
		double magnitude, decay;
		
		public FrontDash()
		{
			super(Action.DASH,0,
				new int[][]{new int[]{0,1,2,3,4,5}, new int[]{0,1,2,3,4,5}, new int[]{0,1,2,3,4,5}},
				new boolean[]{true,true,true},
				new boolean[]{true,true,true},
				new boolean[]{true,true,true},
				new boolean[]{true,true,true},
				new int[]{8,99,-1,-1,1,99},
				new boolean[]{true,true,false});
			magnitude = 35;
			decay = 2;
			frames = (int)(magnitude/decay)+((magnitude/decay == (int)(magnitude/decay))? 0:1)+1;
		}
		
		public void perform(int f)
		{
			isPerformingAction = true;
			if(f >= frames)
			{
			//	jDirections[0] = (isFacingRight && jDirections[0] != -1)? 1:-1;
				jDirections[1] = -1;
				isPerformingAction = false;
				target = null;
				return;
			}
			else if(f == 0)
			{
				if(!isCrouching && aDash < airDashLimit && airOptions > aDash+jCount)
				{
					int d = (isFacingRight)? 3:1;
					currState = PlayerState.DASH_FORWARD;
					bounds.forceArchiver.add(new Force("dash",(isFacingRight)? 3:1,magnitude,decay));
					
					if(!bounds.isGrounded)
					{
						boolean j = false;
						for(Force g: bounds.forceArchiver)
						{
							if(g.type.equals("xJump") && g.direction == (d+2)%4)
							{
								g.direction = d;
								j = true;
							}
							if(g.direction == 0 || g.direction == 2)
								g.decay = g.magnitude;
						}
						if(!j)
							bounds.forceArchiver.add(new Force("xJump",d,6,6));
						jDirections[0] = (isFacingRight)? 1:-1;
						aDash++;
					}
				}
				else
					isPerformingAction = false;
			}
		}
	}

	public class BackDash extends Action
	{
		double magnitude, decay;
		
		public BackDash()
		{
			super(Action.DASH,0,
				new int[][]{new int[]{0,1,2,3,4,5}, new int[]{0,1,2,3,4,5}, new int[]{0,1,2,3,4,5}},
				new boolean[]{true,true,true},
				new boolean[]{true,true,true},
				new boolean[]{true,true,true},
				new boolean[]{true,true,true},
				new int[]{8,99,-1,-1,1,99},
				new boolean[]{true,true,false});
			magnitude = 28;
			decay = 2;
			frames = (int)(magnitude/decay)+((magnitude/decay == (int)(magnitude/decay))? 0:1)+1;
		}
		
		public void perform(int f)
		{
			isPerformingAction = true;
			if(f >= frames)
			{
			//	jDirections[0] = (isFacingRight && jDirections[0] != 1)? -1:1;
				jDirections[1] = -1;
				isPerformingAction = false;
				target = null;
				return;
			}
			else if(f == 0)
			{
				if(!isCrouching && aDash < airDashLimit && airOptions > aDash+jCount)
				{
					int d = (isFacingRight)? 1:3;
					currState = PlayerState.DASH_BACKWARD;
					bounds.forceArchiver.add(new Force("dash",d,magnitude,decay));
					
					if(!bounds.isGrounded)
					{
						boolean j = false;
						for(Force g: bounds.forceArchiver)
						{
							if(g.type.equals("xJump") && g.direction == (d+2)%4)
							{
								g.direction = d;
								j = true;
							}
							if(g.direction == 0 || g.direction == 2)
								g.decay = g.magnitude;
						}
						if(!j)
							bounds.forceArchiver.add(new Force("xJump",d,4,4));
						jDirections[0] = (isFacingRight)? -1:1;
						aDash++;
					}
				}
				else
					isPerformingAction = false;
			}
		}
	}
	
	private class HomingJump extends Action
	{
		public HomingJump()
		{
			super(Action.JUMP,0,
				new int[][]{new int[]{0,1,2,3,4,5}, new int[]{0,1,2,3,4,5}, new int[]{0,1,2,3,4,5}},
				new boolean[]{true,true,true},
				new boolean[]{true,true,true},
				new boolean[]{true,true,true},
				new boolean[]{false,false,false},
				new int[]{-1,-1,-1,-1,15,99},
				new boolean[]{true,true,false});
			frames = 30;
		}
		
		public void perform(int f)
		{
			isPerformingAction = true;
			if(f >= frames)
			{
				jDirections[1] = -1;
				isPerformingAction = false;
				isHoming = false;
				target = null;
				return;
			}
			else
			{
				if(target != null)
				{
					if(f < 12)
						currState = PlayerState.JUMP_HOMING1;
					else
					{
						double x = (target.bounds.xCoord-bounds.xCoord)*0.1;
						double y = (target.bounds.yCoord-bounds.yCoord)*0.9;	//*0.2;
						bounds.isGrounded = false;
						isHoming = true;
						if(!bounds.isGrounded)
							jDirections[0] = (isFacingRight)? 1:-1;
						
						currState = PlayerState.JUMP_HOMING2;
						bounds.forceArchiver.add(new Force("xHoming",(x > 0)? 3:1,Math.abs(x),Math.abs(x)));
						bounds.forceArchiver.add(new Force("yHoming",(y > 0)? 0:2,Math.abs(y),Math.abs(y)));
					}
				}
				else
					f = frames;
			}
		}
	}
}