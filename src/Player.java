import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.ImageObserver;
import java.util.ArrayList;

public class Player extends Puppet
{
	ArrayList<int[][]> movelist;	//[stick inputs, button inputs, input delay, move(int method?)]
	Action[] actions;
	int airDashLimit, aDash;
	boolean isDashing;	//ADD isLanding TO MAKE FORWARD/BACK JUMPS PIXEL PERFECT??
	
	public enum PlayerState implements State
	{
		PuppetState, DASH_FORWARD, DASH_BACKWARD, 
		STANDING_LP, CROUCHING_LP, JUMPING_LP, STANDING_MP, CROUCHING_MP, JUMPING_MP, STANDING_HP, CROUCHING_HP, JUMPING_HP, 
		STANDING_LK, CROUCHING_LK, JUMPING_LK, STANDING_MK, CROUCHING_MK, JUMPING_MK, STANDING_HK, CROUCHING_HK, JUMPING_HK;	//, PERFORM_ACTION
	
		public String getState()
		{
			return name();
		}
		
		public int getPosition()
		{
			return Puppet.PuppetState.values().length+ordinal()-1;
		}
	}
	
	public Player(int x, int y, int w, int h, int c, /*int e,*/ int s, int a, int j, boolean r)
	{
		super(x,y,w,h,c,1000,600,5000,s,a,j,r,false);
//		currState = PuppetState.IDLE;
//		prevState = PuppetState.IDLE;
		isDashing = false;
		airDashLimit = a;
		aDash = 0;
		meter = 1000;
		
		movelist = new ArrayList<int[][]>();	//PLACE MOVELIST ITEMS IN ORDER OF PRIORITY!!!!
		movelist.add(new int[][]{{6,5,6},{-1,-1,-1},{0,10,10}});
		movelist.add(new int[][]{{9,5,6},{-1,-1,-1},{0,10,10}});
		movelist.add(new int[][]{{4,5,4},{-1,-1,-1},{0,10,10}});
		movelist.add(new int[][]{{7,5,4},{-1,-1,-1},{0,10,10}});
		
		actions = new Action[]{new FrontDash(), new FrontDash(), new BackDash(), new BackDash()};
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
		aDash = 0;
	}
	
	public void draw(Graphics2D g, ImageObserver i, SpriteReader s, double w, double h, boolean d)
	{
		super.draw(g,i,s,w,h,d);
		g.setColor(Color.BLUE);
		g.drawString(currState+"",(int)((bounds.xHosh+bounds.width+2)*w/1280),(int)((bounds.yHosh)*h/720));
		g.drawString((int)fIndex+"",(int)((bounds.xHosh+bounds.width+2)*w/1280),(int)((bounds.yHosh+bounds.height*3/4)*h/720));
		g.drawString(fCounter+"",(int)((bounds.xHosh+bounds.width+2)*w/1280),(int)((bounds.yHosh+bounds.height*5/6)*h/720));
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
				
			case "GUARD_STANDING":
				guard();
				break;
				
			case "FLINCH_STANDING0":
			case "FLINCH_STANDING1":
			case "FLINCH_STANDING2":
			case "FLINCH_CROUCHING":
				flinch();
				
			case "JUMP_NEUTRAL":
			case "JUMP_FORWARD":
			case "JUMP_BACKWARD":
				jump();
				break;
				
			case "DASH_FORWARD":
			case "DASH_BACKWARD":
			case "STANDING_LP":
			case "CROUCHING_LP":
			case "JUMPING_LP":
				performAction();
				break;
		}
		xCoord = bounds.xCoord;
		if(!isCrouching && currState != PuppetState.STANDING)
			yCoord = bounds.yCoord;
		
		if(currState.getPosition() < hitboxArchiver.size())
		{
			if(currState != prevState)
			{
				fIndex = hitboxArchiver.get(currState.getPosition())[0][1];
				prevState = currState;
			}
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
		if(currAction != null)
		{
		//	currState = PuppetState.PERFORM_ACTION;
			performAction();
			return;
		}
		
		if(isBlocking[0] || isBlocking[1])
		{
			currState = PuppetState.GUARD_STANDING;
			return;
		}
		
		if(jDirections[1] == 1)
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
		
		if((!bounds.isGrounded /*&& jDirections[0] == 0*/ && jDirections[1] == 0) /*!isJumping)*/ || currState == PuppetState.FALL_NEUTRAL || currState == PuppetState.FALL_FORWARD || currState == PuppetState.FALL_BACKWARD || currState == PuppetState.LANDING)
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
	//		currState = PuppetState.PERFORM_ACTION;
			performAction();
			return;
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
			currState = PuppetState.GUARD_STANDING;
			return;
		}
		
		if(jDirections[1] == 1)
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
		if(currAction != null)
		{
	//		currState = PuppetState.PERFORM_ACTION;
			performAction();
			return;
		}
		
		if(isBlocking[0] || isBlocking[1])
		{
			currState = PuppetState.GUARD_STANDING;
			return;
		}
		
		if(jDirections[1] == 1)
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
	}
	
	public void getHitboxes()
	{
		super.getHitboxes(currState.getPosition());
	}
	
	public void update()
	{
		super.update();
		if(bounds.isGrounded)
		{
			int fLimit = bounds.forceArchiver.size();
			for(int f = 0; f < fLimit; f++)
			{
				if(bounds.forceArchiver.get(f).type.equals("xJump"))
				{
					bounds.forceArchiver.remove(f);
					fLimit = bounds.forceArchiver.size();
					f--;
				}
			}
			aDash = 0;
		}
		else
		{
			bounds.xDir = 0;
			bounds.xDrag = 0;
		}
		
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
		if(isPerformingAction)
			fCounter++;
	}
	
	
	private class FrontDash extends Action
	{
		double magnitude, decay;
		
		public FrontDash()
		{
			super(Action.DASH,1,new boolean[]{false,false,false,false,false});
			magnitude = 35;
			decay = 2;
			frames = (int)(magnitude/decay)+((magnitude/decay == (int)(magnitude/decay))? 0:1);
		}
		
		public void perform(int f)
		{
			int d = (isFacingRight)? 3:1;
			isPerformingAction = true;
			
			if(f >= frames)
			{
				isPerformingAction = false;
				return;
			}
			else
			{
				if(!isCrouching && !isDashing && aDash < airDashLimit)
				{
					currState = PlayerState.DASH_FORWARD;
					bounds.forceArchiver.add(new Force("dash",(isFacingRight)? 3:1,magnitude,decay));
					aDash++;
					
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
						}
						if(!j)
							bounds.forceArchiver.add(new Force("xJump",d,6,0));
					}
				}
			}
		}
	}

	public class BackDash extends Action
	{
		double magnitude, decay;
		
		public BackDash()
		{
			super(Action.DASH,1,new boolean[]{false,false,false,false,false});
			magnitude = 28;
			decay = 2;
			frames = (int)(magnitude/decay)+((magnitude/decay == (int)(magnitude/decay))? 0:1);
		}
		
		public void perform(int f)
		{
			int d = (isFacingRight)? 1:3;
			isPerformingAction = true;
			
			if(f >= frames)
			{
				isPerformingAction = false;
				return;
			}
			else
			{
				if(!isCrouching && !isDashing && aDash < airDashLimit)
				{
					currState = PlayerState.DASH_BACKWARD;
					bounds.forceArchiver.add(new Force("dash",d,magnitude,decay));
					aDash++;
					
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
						}
						if(!j)
							bounds.forceArchiver.add(new Force("xJump",d,4,0));
					}
				}
			}
		}
	}
}