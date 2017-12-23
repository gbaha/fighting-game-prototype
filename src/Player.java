import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.ImageObserver;
import java.util.ArrayList;

public class Player extends Puppet
{
	ArrayList<int[][]> movelist;	//[stick inputs, button inputs, input delay, cancels from?[action, type(standing, crouching, air), starting frame], move(int method?)]
	Action[] actions;
	boolean[] sInputs;
	
	public enum PlayerState implements State
	{
		PuppetState, DASH_FORWARD, DASH_BACKWARD, JUMP_HOMING1, JUMP_HOMING2,
		HUG_START, HUG_HOLD, HUG_HIT, HUG_FORWARD, HUG_UPWARD, HUG_DOWNWARD,
		SLIP1, SLIP2A, SLIP2B, SLIP2C, SLIP2D, SLIP2E, TAUNT,
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
		super(x,y,w,h,c,k,1000,600,5000,s,a1,a2,j1,j2,r,false);
//		currState = PuppetState.IDLE;
//		prevState = PuppetState.IDLE;
		meter = 1000;
		
		movelist = new ArrayList<int[][]>();	//PLACE MOVELIST ITEMS IN ORDER OF PRIORITY!!!!
		movelist.add(new int[][]{{7},{-1},{0},{-1}});
		movelist.add(new int[][]{{8},{-1},{0},{-1}});
		movelist.add(new int[][]{{9},{-1},{0},{-1}});
		movelist.add(new int[][]{{6,5,6},{-2,-2,-2},{0,12,12}});
		movelist.add(new int[][]{{9,5,6},{-2,-2,-2},{0,12,12}});
		movelist.add(new int[][]{{4,5,4},{-2,-2,-2},{0,12,12}});
		movelist.add(new int[][]{{7,5,4},{-2,-2,-2},{0,12,12}});
		movelist.add(new int[][]{{8},{-1},{0},{2,1}});
		movelist.add(new int[][]{{9},{-1},{0},{2,1}});
		movelist.add(new int[][]{{-1,-1},{1,4},{0,1}});
		movelist.add(new int[][]{{-1,-1},{4,1},{0,1}});
		movelist.add(new int[][]{{-1,-1},{2,5},{0,1}});
		movelist.add(new int[][]{{-1,-1},{5,2},{0,1}});
		movelist.add(new int[][]{{-1,-1},{0,3},{0,1}});
		movelist.add(new int[][]{{-1,-1},{3,0},{0,1}});
		
		actions = new Action[]{new Jump(-1), new Jump(0), new Jump(1),
				new FrontDash(), new FrontDash(), new BackDash(), new BackDash(),
				new HomingJump(), new HomingJump(), new Parry(), new Parry(),
				new Taunt(this), new Taunt(this), new Hug(10), new Hug(10)};
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
		isSlipping = false;
		floatOverride = false;
		aDash = 0;
		jCount = 0;
		meter = 1000;
		sInputs = new boolean[4];
	}
	
	public void draw(Graphics2D g, ImageObserver i, SpriteReader s, double w, double h, boolean[] d)
	{
		super.draw(g,i,s,w,h,d);
		if(d[1])
		{
			g.setColor(Color.BLUE);
			g.drawString(currState.getState()+"",(int)((bounds.xHosh+bounds.width+2)*w/1280),(int)((bounds.yHosh)*h/720));
			g.drawString((int)sIndex+"",(int)((bounds.xHosh+bounds.width+2)*w/1280),(int)((bounds.yHosh+bounds.height*3/4)*h/720));
			g.drawString(fCounter+"",(int)((bounds.xHosh+bounds.width+2)*w/1280),(int)((bounds.yHosh+bounds.height*5/6)*h/720));
			g.setColor(Color.RED);
			g.drawString(((hitStun > 0)? hitStun:blockStun)+"",(int)(bounds.xHosh*w/1280),(int)((bounds.yHosh+bounds.height+20)*h/720));
			g.drawString(juggleDamp+"",(int)(bounds.xHosh*w/1280),(int)((bounds.yHosh+bounds.height+40)*h/720));
			g.drawString(hitInfo[0]+"",(int)((bounds.xHosh+50)*w/1280),(int)((bounds.yHosh+bounds.height+20)*h/720));
			g.drawString(jDirections[1]+"",(int)((bounds.xHosh+bounds.width+2)*w/1280),(int)((bounds.yHosh+bounds.height+20)*h/720));
		}
	}
	
	public void checkState()
	{
		switch(currState.getState())
		{
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
			case "SLIP1":
			case "SLIP2A":
			case "SLIP2B":
			case "SLIP2C":
			case "SLIP2D":
			case "SLIP2E":
			case "TAUNT":
				performAction();
				break;
		}
		super.checkState();
	}
	
	public void setAction(Action a)
	{
		if(currAction == null)
		{
			if((a != actions[3] && a != actions[5]) || aDash < airDashLimit)
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
				if(currAction.type == Action.GRAB && a.type != Action.GRAB && currAction.target.damageDamp < 0.4)
					currAction.target.damageDamp = 0.4;
			}
			
			bounds.botOffset = 0;
			xOffset = 0;
			yOffset = 0;
			sAngle = 0;
			isThrowing = false;
			
			currAction = a;
			currAction.target = null;
			currAction.button = -1;
			fCounter = 0;
	//		sIndex = hitboxArchiver.get(currState.getPosition())[0][1];
			
			if(!bounds.isGrounded)
				jDirections[1] = 0;
			isHoming = false;
		}
		if(slipFloat == -1)
			slipFloat = 5;
	}
	
	public void getHitboxes()
	{
		super.getHitboxes(currState.getPosition());
	}
	
	public void update()
	{
		if(bounds.isGrounded && jDirections[1] == 0)
		{
	//		if(jDirections[1] == 0)
			jDirections[0] = 0;
			aDash = 0;
			jCount = 0;
		}
		else
		{
			if(jDirections[0] != 0 && preFrames == 0)
				bounds.forceArchiver.add(new Force("xJump",(jDirections[0] == 1)? 3:1,(jCount+aDash == 1)? 6:9,(jCount+aDash == 1)? 6:9));
			bounds.xDir = 0;
			bounds.xDrag = 0;
		}
		
		boolean f = !bounds.isGrounded && (isJuggled || isDashing || isHoming || isThrown || isTeching || slipFloat > 0); // || other isFloating checks;
		bounds.wasFloating = bounds.isFloating  && !f;
		bounds.isFloating = f;
		if(f)
		{
			for(Force j: bounds.forceArchiver)
			{
				if(j.type.equals("yJump"))
					j.magnitude = 0;
			}
		}//if(otgDamp > 0)System.out.println(bounds.isGrounded+" "+isJuggled+" "+f);
		
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
		double speedup;
		
		public HomingJump()
		{
			super(Action.JUMP,2,
				new int[][]{new int[]{0,1,2,3,4,5}, new int[]{0,1,2,3,4,5}, new int[]{0,1,2,3,4,5}},
				new boolean[]{true,true,true},
				new boolean[]{true,true,true},
				new boolean[]{true,true,true},
				new boolean[]{false,false,false},
				new int[]{-1,-1,-1,-1,15,99},
				new boolean[]{true,true,false});
			frames = 30;
			speedup = 0.3;
		}
		
		public void perform(int f)
		{
			isPerformingAction = true;
			if(f >= frames)
			{
				jDirections[1] = -1;
				jDirections[2] = -1;
				isPerformingAction = false;
				isHoming = false;
				target = null;
				speedup = 0.3;
				return;
			}
			else
			{
				if(target != null)
				{
					if(target.launchPoint > 0)
					{
						if(f < 12)
							currState = PlayerState.JUMP_HOMING1;
						else
						{
							double x = (target.bounds.xCoord-bounds.xCoord)*0.1*speedup;
							double y = (target.bounds.yCoord-bounds.yCoord)*0.9*speedup;	//*0.2;
							bounds.isGrounded = false;
							isHoming = true;
							if(!bounds.isGrounded)
								jDirections[0] = (isFacingRight)? 1:-1;
							if(f == 12)
							{
								addSound("bamf.wav",new float[]{0.0f});
								jCount++;
							}
							
							currState = PlayerState.JUMP_HOMING2;
							bounds.forceArchiver.add(new Force("xHoming",(x > 0)? 3:1,Math.abs(x),Math.abs(x)));
							bounds.forceArchiver.add(new Force("yHoming",(y > 0)? 0:2,Math.abs(y),Math.abs(y)));
							if(speedup < 1)
							speedup += 0.1;
						}
					}
					else
						fCounter = frames;
				}
				else
					fCounter = frames;
			}
		}
	}
	
	public class Parry extends Action
	{
		public Parry()
		{
			super(Action.TAUNT,0,
					new int[][]{new int[]{}, new int[]{}, new int[]{}},
					new boolean[]{false,false,false},
					new boolean[]{false,false,false},
					new boolean[]{false,false,false},
					new boolean[]{false,false,false},
					new int[]{-1,-1,-1,-1,-1,-1},
					new boolean[]{true,true,true});
			frames = 12;
		}
		
		public void perform(int f)
		{
			isPerformingAction = (stamina >= 100);
			if(target != null)
				f = frames;
			
			if(f >= frames)
			{
				if(target == null)
				{
					stamina -= 100;
					sCooldown = 60;
					isPerformingAction = false;
				}
				else
				{
					setAction(new Dodge());
					stamina = maxSp;
					for(Organ a: anatomy)
					{
						a.hInvul = true;
						a.pInvul = true;
					}
					throwInvul = true;
				}
				return;
			}
			else
			{
				currState = PlayerState.SLIP1;
				isSlipping = true;
			}
		}
	}
	
	public class Dodge extends Action
	{
		PlayerState state;
		int xSlip, ySlip;
		boolean hasTilted;
		
		public Dodge()
		{
			super(Action.SPECIAL,0,
					new int[][]{new int[]{0,1,2,3,4,5}, new int[]{0,1,2,3,4,5}, new int[]{0,1,2,3,4,5}},
					new boolean[]{true,true,true},
					new boolean[]{true,true,true},
					new boolean[]{true,true,true},
					new boolean[]{false,false,false},
					new int[]{0,30,0,30,0,30},
					new boolean[]{true,true,true});
			frames = 30;
			
			switch((int)(Math.random()*5))
			{
				case 0:
					state = PlayerState.SLIP2A;
					break;
				case 1:
					state = PlayerState.SLIP2B;
					break;
				case 2:
					state = PlayerState.SLIP2C;
					break;
				case 3:
					state = PlayerState.SLIP2D;
					break;
				case 4:
					state = PlayerState.SLIP2E;
					break;
			}
			xSlip = 0;
			ySlip = 0;
			hasTilted = false;
		}
		
		public void perform(int f)
		{
			isPerformingAction = true;
			isCrouching = false;
			
			if(f >= frames)
			{
				if(xSlip != 0)
				{
					jDirections[0] = xSlip/Math.abs(xSlip);
					jDirections[1] = -1;
					slipFloat = 5;
				}
				
				isPerformingAction = false;
				floatOverride = false;
				bounds.isGhost = false;
				hasTilted = false;
				return;
			}
			else
			{
				currState = state;
				if(f == 1)
				{
					bounds.forceArchiver.clear();
					jDirections = new int[]{0,0,0};
				}
				if(f > 0 && f < 10 && !hasTilted)
				{
					if(sInputs[0] || sInputs[1] || sInputs[2] || sInputs[3])
					{
						xSlip = ((sInputs[1])? 6:0)+((sInputs[3])? -6:0);
						ySlip = ((sInputs[0])? 6:0)+((sInputs[2])? -6:0);
						hasTilted = true;
					}
				}
				
				for(Organ a: anatomy)
				{
					a.hInvul = true;
					a.pInvul = true;
				}
				throwInvul = true;
				floatOverride = true;
				bounds.isGhost = true;
				bounds.isGrounded = false;
				slipFloat = -1;
				
				if(xSlip != 0)
					bounds.forceArchiver.add(new Force("xSlip",(xSlip > 0)? 3:1,Math.abs(xSlip),Math.abs(xSlip)));
				if(ySlip != 0)
					bounds.forceArchiver.add(new Force("ySlip",(ySlip > 0)? 2:0,Math.abs(ySlip),Math.abs(ySlip)));
				
			}
		}
	}
	
	public class Taunt extends Action
	{
		Player player;
		
		public Taunt(Player p)
		{
			super(Action.TAUNT,0,
					new int[][]{new int[]{}, new int[]{}, new int[]{}},
					new boolean[]{false,false,false},
					new boolean[]{false,false,false},
					new boolean[]{false,false,false},
					new boolean[]{false,false,false},
					new int[]{-1,-1,-1,-1,-1,-1},
					new boolean[]{true,false,true});
			player = p;
		}
		
		public void perform(int f)
		{
			isPerformingAction = true;
			if(f >= frames)
			{
				isPerformingAction = false;
				return;
			}
			else
			{
				switch(f)
				{
					case 0:
						currState = PlayerState.TAUNT;
						frames = 60;
						break;
						
					case 3:
						if(player.target != null)
						{
							if(player.target instanceof Puppet)
							{
								Puppet t = (Puppet)player.target;
								if(t.health > 0)
								{
									t.propertyArchiver.add(new double[]{Pleb.TAUNT,600,0,0});
									if(t.kdStun > 0 && t.bounds.isGrounded)
										t.stamina -= 150;
								}
							}
						}
						break;
				}
			}
		}
	}
}