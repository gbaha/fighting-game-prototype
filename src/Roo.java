import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

public class Roo extends Player
{
	public enum RooState implements State
	{
		PlayerState;
		
		public String getState()
		{
			return name();
		}
		
		public int getPosition()
		{
			return ordinal();
		}
	}
	
	public Roo(int x, int y, boolean r)
	{
		super(x,y,100,250,150,/*100,*/6,1,40,r);
		flinchPoints = new int[]{1,0,0,0};
		spriteParams = new int[]{345,180,290,178};
		
		//IDLE
		hitboxArchiver.add(new int[][]{new int[]{0,0,0,0,5},
			new int[]{32,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{32,-27,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{32,-37,53,50,	-32,-10,150,55,	-32,50,125,100,	-42,155,160,95},
			new int[]{32,-42,53,50,	-32,-15,150,55,	-32,47,125,100,	-42,155,160,95},
			new int[]{32,-42,53,50,	-32,-15,150,55,	-32,47,125,100,	-42,155,160,95},
			new int[]{32,-42,53,50,	-32,-15,150,55,	-32,47,125,100,	-42,155,160,95},
			new int[]{32,-42,53,50,	-32,-15,150,55,	-32,47,125,100,	-42,155,160,95},
			new int[]{32,-42,53,50,	-32,-15,150,55,	-32,47,125,100,	-42,155,160,95},
			new int[]{32,-37,53,50,	-32,-10,150,55,	-32,50,125,100,	-42,155,160,95},
			new int[]{32,-27,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95}});
		//CROUCH
		hitboxArchiver.add(new int[][]{new int[]{1,3,3,0,19},
			new int[]{32,-40,53,50,	-32,-5,125,35,	-32,25,175,40,	-45,60,185,90},
			new int[]{32,-40,53,50,	-32,-10,125,35,	-32,20,175,40,	-45,60,185,90},
			new int[]{32,-40,53,50,	-32,-15,125,35,	-32,15,175,40,	-45,60,185,90},
			new int[]{32,-40,53,50,	-32,-10,125,35,	-32,20,175,40,	-45,60,185,90}});
		//STANDING
		hitboxArchiver.add(new int[][]{new int[]{1,3,0,1,1},
			new int[]{32,-33,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{45,17,53,50,	-27,47,135,45,	-27,80,170,60,	-45,155,185,95},
			new int[]{32,70,53,50,	-32,85,125,45,	-32,125,190,40,	-45,160,185,90},
			new int[]{32,65,53,50,	-32,90,125,35,	-32,120,175,40,	-45,160,185,90}});
		//CROUCHING
		hitboxArchiver.add(new int[][]{new int[]{1,0,3,0,1},
			new int[]{32,-33,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{45,17,53,50,	-27,47,135,45,	-27,80,170,60,	-45,155,185,95},
			new int[]{32,70,53,50,	-32,85,125,45,	-32,125,190,40,	-45,160,185,90},
			new int[]{32,65,53,50,	-32,90,125,35,	-32,120,175,40,	-45,160,185,90}});
		//WALK FORWARD
		hitboxArchiver.add(new int[][]{new int[]{2,0,0,0,5},
			new int[]{35,-32,53,50,	-32,0,155,55,	-32,55,125,90,	-82,145,200,105},
			new int[]{35,-32,53,50,	-32,0,155,55,	-32,55,125,90,	-92,145,200,105},
			new int[]{35,-42,53,50,	-32,-12,155,55,	-32,49,125,90,	-72,145,160,105},
			new int[]{35,-42,53,50,	-32,-20,155,55,	-32,45,125,90,	-72,145,160,105},
			new int[]{35,-47,53,50,	-32,-17,155,55,	-32,47,125,90,	-42,145,140,105},
			new int[]{35,-47,53,50,	-32,-17,155,55,	-32,47,125,90,	-32,145,150,105},
			new int[]{35,-50,53,50,	-32,-17,155,55,	-32,47,125,90,	-32,145,150,105},
			new int[]{35,-50,53,50,	-32,-17,155,55,	-32,47,125,90,	-32,145,175,105},
			new int[]{35,-45,53,50,	-32,-12,155,55,	-32,49,125,90,	-52,145,200,105},
			new int[]{35,-37,53,50,	-32,0,155,55,	-32,55,125,90,	-52,145,200,105}});
		//WALK BACKWARD
		hitboxArchiver.add(new int[][]{new int[]{3,0,0,0,5},
			new int[]{35,-32,53,50,	-32,0,155,55,	-32,55,125,90,	-32,145,190,105},
			new int[]{35,-32,53,50,	-32,0,155,55,	-32,55,125,90,	-32,145,200,105},
			new int[]{35,-42,53,50,	-32,-17,155,55,	-32,47,125,90,	-32,145,180,105},
			new int[]{35,-42,53,50,	-32,-20,155,55,	-32,45,125,90,	-32,145,160,105},
			new int[]{35,-47,53,50,	-32,-17,155,55,	-32,47,125,90,	-32,145,150,105},
			new int[]{35,-47,53,50,	-32,-17,155,55,	-32,47,125,90,	-32,145,135,105},
			new int[]{35,-50,53,50,	-32,-17,155,55,	-32,47,125,90,	-32,145,135,105},
			new int[]{35,-50,53,50,	-32,-20,155,55,	-32,45,125,90,	-62,145,185,105},
			new int[]{35,-45,53,50,	-32,-17,155,55,	-32,47,125,90,	-62,145,185,105},
			new int[]{35,-37,53,50,	-32,0,155,55,	-32,55,125,90,	-62,145,200,105}});
		//FALL NEUTRAL
		hitboxArchiver.add(new int[][]{new int[]{4,5,9,0,3},
			new int[]{47,-47,53,50,	-48,-25,160,86,	-10,25,152,90,	-10,115,120,66},
			new int[]{47,-47,53,50,	-48,-25,150,86,	-10,47,152,80,	-10,80,100,105},
			new int[]{40,-50,53,50,	-48,-25,150,86,	-10,45,152,80,	-10,125,108,95},
			new int[]{35,-56,53,50,	-48,-25,136,80,	-10,38,150,80,	-10,118,103,125},
			new int[]{22,-66,53,50,	-48,-25,136,70,	0,30,145,80,	0,110,100,155},
			new int[]{22,-66,53,50,	-48,-25,136,70,	0,30,145,80,	0,110,100,155}});
		//FALL FORWARD
		hitboxArchiver.add(new int[][]{new int[]{5,1,10,0,3},
			new int[]{111,-11,53,50,	-20,-20,100,85,	25,-8,115,125,	-20,65,140,75,	-35,140,90,85},
			new int[]{95,50,58,45,	-36,0,160,80,	0,0,150,75,	-32,75,150,70},
			new int[]{63,85,58,45,	85,20,55,80,	-15,0,122,90,	-56,56,120,70},
			new int[]{5,92,52,45,	42,32,90,90,	42,5,65,90,	-35,-15,88,116},
			new int[]{-42,66,42,45,	0,58,110,68,	-33,9,145,60,	5,-35,70,45},
			new int[]{-60,42,50,45,	-30,45,142,75,	-43,12,105,75,	-10,-15,140,65},
			new int[]{-60,-30,53,50,	-60,15,115,90,	-44,-15,90,85,	55,6,105,182},
			new int[]{22,-60,53,50,	-55,-25,135,70,	-5,45,140,70,	45,115,75,130},
			new int[]{40,-58,53,50,	-40,-28,128,74,	0,46,132,44,	20,90,100,140},
			new int[]{42,-65,53,50,	-35,-30,128,75,	5,45,136,100,	5,145,100,110}});
		//FALL BACKWARD
		hitboxArchiver.add(new int[][]{new int[]{5,7,0,1,3},
			new int[]{51,-53,53,50,	-36,-30,126,80,	-10,52,150,90,	-20,142,100,103},
			new int[]{111,-11,53,50,	-20,-20,100,85,	25,-8,115,125,	-20,65,140,75,	-35,140,90,85},
			new int[]{95,50,58,45,	-36,0,160,80,	0,0,150,75,	-32,75,150,70},
			new int[]{63,85,58,45,	85,20,55,80,	-15,0,122,90,	-56,56,120,70},
			new int[]{5,92,52,45,	42,32,90,90,	42,5,65,90,	-35,-15,88,116},
			new int[]{-42,66,42,45,	0,58,110,68,	-33,9,145,60,	5,-35,70,45},
			new int[]{-60,42,50,45,	-30,45,142,75,	-43,12,105,75,	-10,-15,140,65},
			new int[]{-60,-30,53,50,	-60,15,115,90,	-44,-15,90,85,	55,6,105,182}});
		//LANDING
		hitboxArchiver.add(new int[][]{new int[]{1,2,0,1,5},
			new int[]{45,17,53,50,	-27,47,135,45,	-27,80,170,60,	-45,155,185,95},
			new int[]{32,70,53,50,	-32,85,125,45,	-32,125,190,40,	-45,160,185,90},
			new int[]{32,65,53,50,	-32,90,125,35,	-32,120,175,40,	-45,160,185,90}});
		//JUMP NEUTRAL
		hitboxArchiver.add(new int[][]{new int[]{4,0,4,0,3},
			new int[]{22,-66,53,50,	-62,-66,200,66,	-17,0,100,60,	-12,60,90,175},
			new int[]{22,-66,53,50,	-62,-66,200,66,	-17,0,100,60,	-12,60,90,175},
			new int[]{29,-60,53,50,	-52,-60,200,70,	-10,10,100,60,	-10,70,140,150},
			new int[]{40,-56,53,50,	-38,-25,180,70,	-10,10,100,60,	-20,70,155,136},
			new int[]{50,-42,53,50,	-48,-25,170,86,	-10,25,160,60,	-10,70,150,101}});
		//JUMP FORWARD
		hitboxArchiver.add(new int[][]{new int[]{5,0,0,0,3},
			new int[]{51,-53,53,50,	-36,-30,126,80,	-10,52,150,90,	-20,142,100,103}});
		//JUMP BACKWARD
		hitboxArchiver.add(new int[][]{new int[]{5,8,8,0,3},
			new int[]{22,-60,53,50,	-55,-25,135,70,	-5,45,140,70,	45,115,75,130}});
		//STANDING GUARD
		hitboxArchiver.add(new int[][]{new int[]{6,0,0,0,3},
			new int[]{32,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95}});
		//STANDING FLINCH(MID)
		hitboxArchiver.add(new int[][]{new int[]{9,0,4,0,2},
				new int[]{32,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
				new int[]{32,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
				new int[]{32,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
				new int[]{32,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
				new int[]{32,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95}});
		//STANDING FLINCH(LOW)
		hitboxArchiver.add(new int[][]{new int[]{10,0,5,0,2},
				new int[]{32,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
				new int[]{32,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
				new int[]{32,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
				new int[]{32,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
				new int[]{32,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
				new int[]{32,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95}});
		//STANDING FLINCH(HIGH)
		hitboxArchiver.add(new int[][]{new int[]{11,0,3,0,2},
				new int[]{32,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
				new int[]{32,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
				new int[]{32,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
				new int[]{32,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95}});
		//CROUCHING FLINCH
		hitboxArchiver.add(new int[][]{new int[]{12,0,3,0,2},
			new int[]{32,-33,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{32,-33,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{32,-33,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{32,-33,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95}});
		//DASH FORWARD
		hitboxArchiver.add(new int[][]{new int[]{13,0,5,0,4},
			new int[]{32,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{32,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{32,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{32,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{32,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{32,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95}});
		//DASH BACKWARD
		hitboxArchiver.add(new int[][]{new int[]{14,0,5,0,4},
			new int[]{32,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{32,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{32,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{32,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{32,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{32,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95}});
		//STANDING LP
		hitboxArchiver.add(new int[][]{new int[]{15,0,4,0,2},
			new int[]{32,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{32,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95, 125,0,120,45},
			new int[]{32,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95, 125,0,120,45},
			new int[]{32,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{32,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95}});
		//STANDING MP
		hitboxArchiver.add(new int[][]{new int[]{16,0,8,0,2},
			new int[]{32,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{32,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{32,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{32,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95, 125,10,100,42},
			new int[]{32,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95, 125,10,100,42},
			new int[]{32,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95, 125,10,100,42},
			new int[]{32,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95, 125,10,100,42},
			new int[]{32,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{32,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95}});
		//STANDING HP
		hitboxArchiver.add(new int[][]{new int[]{17,0,13,0,2},
			new int[]{32,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{32,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{32,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{32,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{32,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{32,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{32,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{32,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{32,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{32,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{32,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{32,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{32,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{32,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95}});
		
		normals = new Action[]{new LightPunch(this), new MediumPunch(this), new HeavyPunch(this), new LightKick(this), new MediumKick(this), new HeavyKick(this)};
	}
	
	public void draw(Graphics2D g, ImageObserver i, SpriteReader s, double w, double h, boolean d)
	{
		if(currState.getPosition() < hitboxArchiver.size())
		{
			try
			{
		//		fIndex = 4; fCounter = 4; currAction = new MediumPunch(this); currState = PlayerState.STANDING_MP;	//TEST
				
				Image sheet = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resources/roo.png"));
				int f = /*(hitboxArchiver.get(Roo.State.valueOf(currState.toString()).ordinal())[0][3] == 0)?*/ (int)fIndex;	//:hitboxArchiver.get(Roo.State.valueOf(currState.toString()).ordinal()).length-(int)fIndex-2;
				s.read(g,i,xHosh,yHosh,width,800,490,f,hitboxArchiver.get(currState.getPosition())[0][0],spriteParams,!isFacingRight,sheet);
			}
			catch(java.lang.IndexOutOfBoundsException e)
			{
				draw(g,i,s,w,h,d);
			}
		}
		super.draw(g,i,s,w,h,d);
	}
	
	
	public class LightPunch extends Action
	{
		Roo roo;
		
		public LightPunch(Roo r)
		{
			super(Action.NORMAL,1,10,new int[]{0,1,4},new boolean[]{false,false,false,false},new int[]{3,10});
			roo = r;
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
						currState = (!bounds.isGrounded)? PlayerState.JUMPING_LP:((isCrouching)? PlayerState.CROUCHING_LP:PlayerState.STANDING_LP);
						hashCounter = "";
						hits = 1;
						
						if(!bounds.isGrounded){}
						else if(isCrouching){}
						else
							plebsOut.add(new Pleb(roo,bounds.xCoord+130,bounds.yCoord+10,180,35,5,true));
						break;
						
					case 2:
						if(!bounds.isGrounded){}
						else if(isCrouching){}
						else
							addPleb(roo,0,bounds.xCoord+130,bounds.yCoord+25,140,20,3,0,0,12,1,12,0,true);
						break;
				}
			}
		}
	}
	
	public class MediumPunch extends Action
	{
		Roo roo;
		
		public MediumPunch(Roo r)
		{
			super(Action.NORMAL,1,18,new int[]{2,5},new boolean[]{false,false,false,false},new int[]{4,18});
			roo = r;
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
						currState = (!bounds.isGrounded)? PlayerState.JUMPING_MP:((isCrouching)? PlayerState.CROUCHING_MP:PlayerState.STANDING_MP);
						hashCounter = "";
						hits = 1;
						
						if(!bounds.isGrounded){}
						else if(isCrouching){}
						else
							plebsOut.add(new Pleb(roo,bounds.xCoord+130,bounds.yCoord+10,180,35,8,true));
						break;
					case 3:
						if(!bounds.isGrounded){}
						else if(isCrouching){}
						else
						{
							addPleb(roo,0,bounds.xCoord+110,bounds.yCoord+30,85,20,1,0,1,30,10,16,0,true);
							addPleb(roo,0,bounds.xCoord+200,bounds.yCoord+30,50,30,1,0,1,30,10,16,0,true);
						}
						break;
					case 4:
						if(!bounds.isGrounded){}
						else if(isCrouching){}
						else
							addPleb(roo,0,bounds.xCoord+125,bounds.yCoord+30,130,20,3,0,1,30,10,16,0,true);
						break;
				}
			}
		}
	}
	
	public class HeavyPunch extends Action
	{
		Roo roo;
		
		public HeavyPunch(Roo r)
		{
			super(Action.NORMAL,1,28,new int[]{},new boolean[]{false,false,false,false},new int[]{0,0});
			roo = r;
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
						currState = (!bounds.isGrounded)? PlayerState.JUMPING_HP:((isCrouching)? PlayerState.CROUCHING_HP:PlayerState.STANDING_HP);
						hashCounter = "";
						hits = 1;
						
						if(!bounds.isGrounded){}
						else if(isCrouching){}
						else
							plebsOut.add(new Pleb(roo,bounds.xCoord+130,bounds.yCoord+10,180,35,10,true));
						break;
					case 6:
						if(!bounds.isGrounded){}
						else if(isCrouching){}
						else
							addPleb(roo,0,bounds.xCoord+130,bounds.yCoord+25,140,20,4,0,2,5,25,18,0,true);
						break;
				}
			}
		}
	}
	
	public class LightKick extends Action
	{
		Roo roo;
		
		public LightKick(Roo r)
		{
			super(Action.NORMAL,1,1,new int[]{},new boolean[]{false,false,false,false},new int[]{0,0});
			roo = r;
		}
		
		public void perform(int f)
		{
		}
	}
	
	public class MediumKick extends Action
	{
		Roo roo;
		
		public MediumKick(Roo r)
		{
			super(Action.NORMAL,1,1,new int[]{},new boolean[]{false,false,false,false},new int[]{0,0});
			roo = r;
		}
		
		public void perform(int f)
		{
		}
	}
	
	public class HeavyKick extends Action
	{
		Roo roo;
		
		public HeavyKick(Roo r)
		{
			super(Action.NORMAL,1,1,new int[]{},new boolean[]{false,false,false,false},new int[]{0,0});
			roo = r;
		}
		
		public void perform(int f)
		{
		}
	}
}