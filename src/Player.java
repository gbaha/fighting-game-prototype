import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.ImageObserver;
import java.util.ArrayList;

public class Player extends Puppet
{
	ArrayList<int[][]> movelist;	// [stick inputs, button inputs, input delay, move(int method?)]
	Action[] actions;
	Action currAction;
	State currState, prevState;
	int airDashLimit, aDash, fCounter;
	boolean isDashing;	//ADD isLanding TO MAKE FORWARD/BACK JUMPS PIXEL PERFECT??
	
	public enum State
	{
		IDLE, CROUCH, STANDING, CROUCHING, WALK_FORWARD, WALK_BACKWARD, FALL, LANDING, JUMP_NEUTRAL, JUMP_FORWARD, JUMP_BACKWARD, PERFORM_ACTION
	}
	
	public Player(int x, int y, int w, int h, int c, /*int e,*/ int s, int a, int j, boolean r)
	{
		super(x,y,w,h,c,1000,600,5000,s,a,j,r,false);
		currAction = null;
		currState = State.IDLE;
		prevState = State.IDLE;
		isDashing = false;
		airDashLimit = a;
		aDash = 0;
		meter = 1000;
		
		movelist = new ArrayList<int[][]>();	//PLACE MOVELIST ITEMS IN ORDER OF PRIORITY!!!!
		movelist.add(new int[][]{{6,5,6},{-1,-1,-1},{0,10,10}});
		movelist.add(new int[][]{{9,5,6},{-1,-1,-1},{0,10,10}});
		movelist.add(new int[][]{{4,5,4},{-1,-1,-1},{0,10,10}});
		movelist.add(new int[][]{{7,5,4},{-1,-1,-2},{0,10,10}});
		
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
		currState = State.IDLE;
		isDashing = false;
		aDash = 0;
	}
	
	public void draw(Graphics2D g, ImageObserver i, SpriteReader s, double w, double h, boolean d)
	{
		super.draw(g,i,s,w,h,d);
		g.setColor(Color.BLUE);
		g.drawString(currState+"",(int)((bounds.xHosh+bounds.width+2)*w/1280),(int)((bounds.yHosh)*h/720));
	}
	
	public void checkState()
	{//if(xCoord == 700)System.out.println(jDirections[0]+" "+jDirections[1]+"   "+currState);
		switch(currState)
		{
			case IDLE:
			case FALL:
			case LANDING:
				idle();
				break;
				
			case CROUCH:
			case STANDING:
			case CROUCHING:
				crouch();
				break;
				
			case WALK_FORWARD:
			case WALK_BACKWARD:
				move();
				break;
				
			case JUMP_NEUTRAL:
			case JUMP_FORWARD:
			case JUMP_BACKWARD:
				jump();
				break;
				
			case PERFORM_ACTION:
				performAction();
				break;
				
			//Take damage case
		}
		xCoord = bounds.xCoord;
		if(!isCrouching && currState != State.STANDING)
			yCoord = bounds.yCoord;
		
		if(State.valueOf(currState.toString()).ordinal() < hitboxArchiver.size())
		{
			if(currState != prevState)
			{
				frameIndex = hitboxArchiver.get(State.valueOf(currState.toString()).ordinal())[0][1];
				prevState = currState;
			}
		}
	}
	
	public void performAction()
	{
		currAction.perform(fCounter);
		if(!isPerformingAction)
		{
			currAction = null;
			currState = State.IDLE;
		}
	}
	
	public void idle()
	{
		//TAKE DAMAGE ROUTE SUPERCEDES EVERYTHING
		if(currAction != null)
		{
			currState = State.PERFORM_ACTION;
			return;
		}
		
		//NORMAL ATTACK ROUTE (also read last stick input in case of command normal)
		
		if(jDirections[1] == 1)
		{
			switch(jDirections[0])
			{
				case 0:
					currState = State.JUMP_NEUTRAL;
					return;
					
				case 1:
					currState = (isFacingRight)? State.JUMP_FORWARD:State.JUMP_BACKWARD;
					return;
					
				case -1:
					currState = (isFacingRight)? State.JUMP_BACKWARD:State.JUMP_FORWARD;
					return;
			}
		}
		
		if((!bounds.isGrounded && jDirections[0] == 0 && jDirections[1] == 0) /*!isJumping)*/ || currState == State.FALL || currState == State.LANDING)
		{
			if(currState != State.LANDING)
			{//System.out.println(preFrames+" "+frameIndex+"	"+currState);
				currState = State.FALL;
				if(bounds.isGrounded)
				{
					currState = State.LANDING;
					preFrames = 3;
				}
			}
			else if(preFrames == 0)
				currState = State.IDLE;
			return;
		}
		
		if(isCrouching)
		{
			currState = State.CROUCHING;
			return;
		}
		
		if((isFacingRight && (bounds.xDir > 0 || bounds.xDrag > 0)) || (!isFacingRight && (bounds.xDir < 0  || bounds.xDrag < 0)))
		{
			currState = State.WALK_FORWARD;
			return;
		}
		else if((isFacingRight && (bounds.xDir < 0 || bounds.xDrag < 0)) || (!isFacingRight && (bounds.xDir > 0  || bounds.xDrag > 0)))
		{
			currState = State.WALK_BACKWARD;
			return;
		}
	}
	
	public void move()
	{
		bounds.move();
		if(isCrouching)
			bounds.xVel = 0;
		if(currAction != null)
			currState = State.PERFORM_ACTION;
		if(jDirections[1] == 1)
		{
			switch(jDirections[0])
			{
				case 0:
					currState = State.JUMP_NEUTRAL;
					return;
					
				case 1:
					currState = (isFacingRight)? State.JUMP_FORWARD:State.JUMP_BACKWARD;
					return;
					
				case -1:
					currState = (isFacingRight)? State.JUMP_BACKWARD:State.JUMP_FORWARD;
					return;
			}
		}
		if(!bounds.isGrounded && jDirections[0] == 0 && jDirections[1] == 0) //!isJumping)
			currState = State.FALL;
		if(bounds.xVel == 0)
			currState = State.IDLE;
	}
	
	public void crouch()
	{//System.out.println(preFrames+" "+frameIndex+"	"+currState);
		if(isCrouching)
		{
			if(currState == State.IDLE)
			{
				currState = State.CROUCHING;
				preFrames = 4;
			}
			else if(preFrames == 0)
				currState = State.CROUCH;
		}
		else
		{
			if(currState == State.CROUCH)
			{
				currState = State.STANDING;
				preFrames = 4;
			}
			else if(preFrames == 0)
				currState = State.IDLE;
		}
	}
	
	public void jump()
	{
		if(jDirections[1] == 1)
		{
			switch(jDirections[0])
			{
				case 0:
					currState = State.JUMP_NEUTRAL;
					return;
					
				case 1:
					currState = (isFacingRight)? State.JUMP_FORWARD:State.JUMP_BACKWARD;
					return;
					
				case -1:
					currState = (isFacingRight)? State.JUMP_BACKWARD:State.JUMP_FORWARD;
					return;
			}
		}
		if(jDirections[0] == 0 && jDirections[1] == 0)
			currState = State.FALL;
	}
	
	public void getHitboxes()
	{
		super.getHitboxes(State.valueOf(currState.toString()).ordinal());
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
		
		if(currState == State.CROUCH)
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
	}
	
	
	private class FrontDash extends Action
	{
		public FrontDash()
		{
			super(Action.DASH,new boolean[]{false,false,false,false,false});
		}
		
		public void perform(int f)
		{
			int d = (isFacingRight)? 3:1;
			isPerformingAction = true;
			
			if(!isCrouching && !isDashing && aDash < airDashLimit)
			{
				bounds.forceArchiver.add(new Force("dash",d,35,2));
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
			
			if(!isDashing)
				isPerformingAction = false;
		}
	}

	public class BackDash extends Action
	{
		public BackDash()
		{
			super(Action.DASH,new boolean[]{false,false,false,false,false});
		}
		
		public void perform(int f)
		{
			int d = (isFacingRight)? 1:3;
			isPerformingAction = true;
			
			if(!isCrouching && !isDashing && aDash < airDashLimit)
			{
				bounds.forceArchiver.add(new Force("dash",d,28,2));
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
			
			if(!isDashing)
				isPerformingAction = false;
		}
	}
}