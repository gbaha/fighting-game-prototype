import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

public class Roo extends Player
{
	public enum RooState implements State
	{
		PlayerState, FIREBALL_LAUNCH, FIREBALL_RECOVER;
		
		public String getState()
		{
			return name();
		}
		
		public int getPosition()
		{
			return Player.PlayerState.values().length+Puppet.PuppetState.values().length+ordinal()-2;
		}
	}
	
	public Roo(int x, int y, boolean r)
	{
		super(x,y,100,250,150,/*100,*/6,1,40,r);
		flinchPoints = new int[]{1,0,0,0,1,1,1};
		spriteParams = new int[]{345,180,290,178};
		
		//IDLE
		hitboxArchiver.add(new int[][]{new int[]{0,0,0,0,4},
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
		hitboxArchiver.add(new int[][]{new int[]{1,4,4,0,9},
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
			new int[]{32,-133,53,50,	-32,-100,150,55,	-32,-45,125,100,	-42,55,160,95},
			new int[]{45,-83,53,50,	-27,-53,135,45,	-27,-20,170,60,	-45,55,185,95},
			new int[]{32,-30,53,50,	-32,-15,125,45,	-32,25,190,40,	-45,60,185,90},
			new int[]{32,-35,53,50,	-32,-10,125,35,	-32,20,175,40,	-45,60,185,90}});
		//WALK FORWARD
		hitboxArchiver.add(new int[][]{new int[]{2,0,0,0,4},
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
		hitboxArchiver.add(new int[][]{new int[]{3,0,0,0,4},
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
		hitboxArchiver.add(new int[][]{new int[]{4,5,9,0,2},
			new int[]{47,-47,53,50,	-48,-25,160,86,	-10,25,152,90,	-10,115,120,66},
			new int[]{47,-47,53,50,	-48,-25,150,86,	-10,47,152,80,	-10,80,100,105},
			new int[]{40,-50,53,50,	-48,-25,150,86,	-10,45,152,80,	-10,125,108,95},
			new int[]{35,-56,53,50,	-48,-25,136,80,	-10,38,150,80,	-10,118,103,125},
			new int[]{22,-66,53,50,	-48,-25,136,70,	0,30,145,80,	0,110,100,155},
			new int[]{22,-66,53,50,	-48,-25,136,70,	0,30,145,80,	0,110,100,155}});
		//FALL FORWARD
		hitboxArchiver.add(new int[][]{new int[]{5,1,10,0,2},
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
		hitboxArchiver.add(new int[][]{new int[]{5,7,0,1,2},
			new int[]{51,-53,53,50,	-36,-30,126,80,	-10,52,150,90,	-20,142,100,103},
			new int[]{111,-11,53,50,	-20,-20,100,85,	25,-8,115,125,	-20,65,140,75,	-35,140,90,85},
			new int[]{95,50,58,45,	-36,0,160,80,	0,0,150,75,	-32,75,150,70},
			new int[]{63,85,58,45,	85,20,55,80,	-15,0,122,90,	-56,56,120,70},
			new int[]{5,92,52,45,	42,32,90,90,	42,5,65,90,	-35,-15,88,116},
			new int[]{-42,66,42,45,	0,58,110,68,	-33,9,145,60,	5,-35,70,45},
			new int[]{-60,42,50,45,	-30,45,142,75,	-43,12,105,75,	-10,-15,140,65},
			new int[]{-60,-30,53,50,	-60,15,115,90,	-44,-15,90,85,	55,6,105,182}});
		//LANDING
		hitboxArchiver.add(new int[][]{new int[]{1,2,0,1,4},
			new int[]{45,17,53,50,	-27,47,135,45,	-27,80,170,60,	-45,155,185,95},
			new int[]{32,70,53,50,	-32,85,125,45,	-32,125,190,40,	-45,160,185,90},
			new int[]{32,65,53,50,	-32,90,125,35,	-32,120,175,40,	-45,160,185,90}});
		//JUMP NEUTRAL
		hitboxArchiver.add(new int[][]{new int[]{4,0,4,0,2},
			new int[]{22,-66,53,50,	-62,-66,200,66,	-17,0,100,60,	-12,60,90,175},
			new int[]{22,-66,53,50,	-62,-66,200,66,	-17,0,100,60,	-12,60,90,175},
			new int[]{29,-60,53,50,	-52,-60,200,70,	-10,10,100,60,	-10,70,140,150},
			new int[]{40,-56,53,50,	-38,-25,180,70,	-10,10,100,60,	-20,70,155,136},
			new int[]{50,-42,53,50,	-48,-25,170,86,	-10,25,160,60,	-10,70,150,101}});
		//JUMP FORWARD
		hitboxArchiver.add(new int[][]{new int[]{5,0,0,0,1},
			new int[]{51,-53,53,50,	-36,-30,126,80,	-10,52,150,90,	-20,142,100,103}});
		//JUMP BACKWARD
		hitboxArchiver.add(new int[][]{new int[]{5,8,8,0,1},
			new int[]{22,-60,53,50,	-55,-25,135,70,	-5,45,140,70,	45,115,75,130}});
		//STANDING GUARD
		hitboxArchiver.add(new int[][]{new int[]{6,0,0,0,1},
			new int[]{32,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95}});
		//CROUCHING GUARD
		hitboxArchiver.add(new int[][]{new int[]{7,0,0,0,1},
			new int[]{32,-40,53,50,	-32,-5,125,35,	-32,25,175,40,	-45,60,185,90}});
		//JUMPING GUARD
		hitboxArchiver.add(new int[][]{new int[]{8,0,0,0,1},
			new int[]{22,-70,100,68,	-20,-40,100,70,	-20,30,152,72,	25,95,78,50}});
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
			new int[]{32,-40,53,50,	-32,-5,125,35,	-32,25,175,40,	-45,60,185,90},
			new int[]{32,-40,53,50,	-32,-5,125,35,	-32,25,175,40,	-45,60,185,90},
			new int[]{32,-40,53,50,	-32,-5,125,35,	-32,25,175,40,	-45,60,185,90},
			new int[]{32,-40,53,50,	-32,-5,125,35,	-32,25,175,40,	-45,60,185,90}});
		//MIDAIR FLINCH(STANDARD)
		hitboxArchiver.add(new int[][]{new int[]{10,0,5,0,2},
			new int[]{32,-33,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{32,-33,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{32,-33,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{32,-33,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{32,-33,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95}});
		//MIDAIR FLINCH(LAUNCH)
		hitboxArchiver.add(new int[][]{new int[]{13,0,1,0,2},
			new int[]{32,-33,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{32,-33,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95}});
		//MIDAIR FLINCH(SPIKE)
		hitboxArchiver.add(new int[][]{new int[]{14,0,5,0,2},
			new int[]{32,-33,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{32,-33,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{32,-33,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{32,-33,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{32,-33,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95}});
		//FALLING
		hitboxArchiver.add(new int[][]{new int[]{13,2,7,0,2},
			new int[]{32,-33,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{32,-33,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{32,-33,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{32,-33,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{32,-33,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{32,-33,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95}});
		//GUARD BREAK(GROUND)
		hitboxArchiver.add(new int[][]{new int[]{15,0,7,0,2},
			new int[]{32,-33,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{32,-33,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{32,-33,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{32,-33,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{32,-33,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{32,-33,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{32,-33,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{32,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95}});
		//GUARD BREAK(AIR)
		hitboxArchiver.add(new int[][]{new int[]{16,0,8,0,3},
			new int[]{32,-33,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{32,-33,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{32,-33,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{32,-33,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{32,-33,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{32,-33,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{32,-33,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{32,-33,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{32,-33,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{32,-33,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{32,-33,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{32,-33,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{32,-33,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{32,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95}});
		
		//DASH FORWARD
		hitboxArchiver.add(new int[][]{new int[]{17,0,5,0,3},
			new int[]{32,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{32,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{32,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{32,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{32,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{32,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95}});
		//DASH BACKWARD
		hitboxArchiver.add(new int[][]{new int[]{18,0,5,0,3},
			new int[]{32,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{32,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{32,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{32,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{32,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{32,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95}});
		
		//STANDING LP
		hitboxArchiver.add(new int[][]{new int[]{19,0,4,0,2},
			new int[]{32,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{32,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95, 125,0,120,45},
			new int[]{32,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95, 125,0,120,45},
			new int[]{32,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{32,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95}});
		//STANDING MP
		hitboxArchiver.add(new int[][]{new int[]{20,0,8,0,2},
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
		hitboxArchiver.add(new int[][]{new int[]{21,0,13,0,2},
			new int[]{32,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{32,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{52,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{52,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{52,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{52,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{52,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{52,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{42,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{32,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{32,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{32,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{32,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{32,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95}});
		//STANDING LK
		hitboxArchiver.add(new int[][]{new int[]{22,0,6,0,2},
			new int[]{32,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{32,-23,60,50,	-32,0,150,70,	-32,75,125,75,	-42,155,160,95},
			new int[]{32,-23,60,50,	-32,0,150,90,	-32,100,250,75,	-42,165,290,85},
			new int[]{32,-23,60,50,	-32,0,150,90,	-32,100,250,75,	-42,165,290,85},
			new int[]{32,-23,60,50,	-32,0,150,70,	-32,75,125,75,	-42,155,160,95},
			new int[]{32,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{32,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95}});
		//STANDING MK
		hitboxArchiver.add(new int[][]{new int[]{23,0,9,0,2},
			new int[]{50,-28,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{58,-28,63,50,	-24,0,160,55,	-24,55,170,100,	-34,155,190,95},
			new int[]{58,-28,63,50,	-24,0,160,55,	-24,55,170,100,	-34,155,190,95},
			new int[]{58,-40,63,50,	-24,0,160,55,	-24,55,170,100,	-34,155,190,95},
			new int[]{58,-40,63,50,	-24,0,160,55,	-24,55,170,100,	-34,155,190,95},
			new int[]{58,-40,63,50,	-24,0,160,55,	-24,55,250,100,	-34,155,190,95},
			new int[]{58,-40,63,50,	-24,0,160,55,	-24,55,250,100,	-34,155,190,95},
			new int[]{58,-40,63,50,	-24,0,160,55,	-24,55,170,100,	-34,155,190,95},
			new int[]{58,-28,63,50,	-24,0,160,55,	-24,55,170,100,	-34,155,190,95},
			new int[]{32,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95}});
		//STANDING HK
		hitboxArchiver.add(new int[][]{new int[]{24,0,24,0,1},
			new int[]{0,-23,58,50,	-32,20,150,55,	-22,55,160,110,	-22,175,160,75},
			new int[]{0,-23,58,50,	-32,20,150,55,	-22,55,160,110,	-22,175,160,75},
			new int[]{0,-23,58,50,	-32,20,150,55,	-22,55,160,110,	-22,175,160,75},
			new int[]{0,-20,58,50,	-32,8,150,55,	-22,55,180,110,	-22,175,160,75},
			new int[]{0,-20,58,50,	-32,8,150,55,	-22,55,180,110,	-22,175,160,75},
			new int[]{0,-20,58,50,	-32,8,150,55,	-22,55,180,110,	-22,175,160,75},
			new int[]{-22,-20,58,50,	-22,8,150,55,	-22,50,180,110,	-22,175,160,75},
			new int[]{-22,-20,58,50,	-22,8,150,55,	-22,50,180,110,	-22,175,160,75,	120,-10,80,80},
			new int[]{-22,-20,58,50,	-22,8,150,55,	-22,50,180,110,	-22,175,160,75,	120,-10,80,80},
			new int[]{-22,-20,58,50,	-22,8,150,55,	-22,50,180,110,	-22,175,160,75,	120,-10,80,80},
			new int[]{-22,-20,58,50,	-22,8,150,55,	-22,50,180,110,	-22,175,160,75,	120,-10,80,80},
			new int[]{-22,-20,58,50,	-22,8,150,55,	-22,50,180,110,	-22,175,160,75,	120,-10,80,80},
			new int[]{-22,-20,58,50,	-22,8,150,55,	-22,50,180,110,	-22,175,160,75,	120,-10,80,80},
			new int[]{-22,-20,58,50,	-22,8,150,55,	-22,50,180,110,	-22,175,160,75,	120,-10,80,80},
			new int[]{-22,-20,58,50,	-22,8,150,55,	-22,50,180,110,	-22,175,160,75,	120,-10,80,80},
			new int[]{22,-20,58,50,	-22,8,150,55,	-22,50,180,110,	-22,175,160,75},
			new int[]{22,-20,58,50,	-22,8,150,55,	-22,50,180,110,	-22,175,160,75},
			new int[]{22,-20,58,50,	-22,8,150,55,	-22,50,180,110,	-22,175,160,75},
			new int[]{22,-23,58,50,	-22,8,150,55,	-22,50,180,110,	-22,175,160,75},
			new int[]{22,-23,58,50,	-22,8,150,55,	-22,50,180,110,	-22,175,160,75},
			new int[]{22,-23,58,50,	-22,8,150,55,	-22,50,180,110,	-22,175,160,75},
			new int[]{50,-28,58,50,	-32,8,150,55,	-22,55,160,110,	-22,175,160,75},
			new int[]{50,-28,58,50,	-32,8,150,55,	-22,55,160,110,	-22,175,160,75},
			new int[]{50,-28,58,50,	-32,8,150,55,	-22,55,160,110,	-22,175,160,75},
			new int[]{50,-28,58,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95}});
			
			
		//CROUCHING LP
		hitboxArchiver.add(new int[][]{new int[]{25,0,4,0,2},
			new int[]{32,-40,53,50,	-32,-5,125,35,	-32,25,135,40,	-45,60,185,90},
			new int[]{32,-40,53,50,	-32,-5,125,35,	-32,25,135,40,	-45,60,185,90,	125,-5,80,40},
			new int[]{32,-40,53,50,	-32,-5,125,35,	-32,25,135,40,	-45,60,185,90,	125,-5,80,40},
			new int[]{32,-40,53,50,	-32,-5,125,35,	-32,25,135,40,	-45,60,185,90,	125,-5,80,40},
			new int[]{32,-40,53,50,	-32,-5,125,35,	-32,25,135,40,	-45,60,185,90}});
		//CROUCHING MP
		hitboxArchiver.add(new int[][]{new int[]{18,0,1,0,2},
			new int[]{32,-40,53,50,	-32,-5,125,35,	-32,25,175,40,	-45,60,185,90}});
		//CROUCHING HP
		hitboxArchiver.add(new int[][]{new int[]{18,0,1,0,2},
			new int[]{32,-40,53,50,	-32,-5,125,35,	-32,25,175,40,	-45,60,185,90}});
		//CROUCHING LK
		hitboxArchiver.add(new int[][]{new int[]{18,0,1,0,2},
			new int[]{32,-40,53,50,	-32,-5,125,35,	-32,25,175,40,	-45,60,185,90}});
		//CROUCHING MK
		hitboxArchiver.add(new int[][]{new int[]{18,0,1,0,2},
			new int[]{32,-40,53,50,	-32,-5,125,35,	-32,25,175,40,	-45,60,185,90}});
		//CROUCHING HK
		hitboxArchiver.add(new int[][]{new int[]{18,0,1,0,2},
			new int[]{32,-40,53,50,	-32,-5,125,35,	-32,25,175,40,	-45,60,185,90}});
		
		//JUMPING LP
		hitboxArchiver.add(new int[][]{new int[]{18,0,0,0,2},
			new int[]{32,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95}});
		//JUMPING MP
		hitboxArchiver.add(new int[][]{new int[]{18,0,1,0,2},
			new int[]{32,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95}});
		//JUMPING HP
		hitboxArchiver.add(new int[][]{new int[]{18,0,1,0,2},
			new int[]{32,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95}});
		//JUMPING LK
		hitboxArchiver.add(new int[][]{new int[]{18,0,1,0,2},
			new int[]{32,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95}});
		//JUMPING MK
		hitboxArchiver.add(new int[][]{new int[]{18,0,1,0,2},
			new int[]{32,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95}});
		//JUMPING HK
		hitboxArchiver.add(new int[][]{new int[]{18,0,1,0,3},
			new int[]{32,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95}});
		
		//FIREBALL LAUNCH
		hitboxArchiver.add(new int[][]{new int[]{26,0,9,0,2},
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
		//FIREBALL LAUNCH
		hitboxArchiver.add(new int[][]{new int[]{27,0,2,0,2},
			new int[]{32,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{32,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{32,-23,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95}});
		
		movelist.add(new int[][]{{2,3,6,-1},{-1,-1,-1,0},{0,-1,10,10}});	//{0,7,4,3}});
		movelist.add(new int[][]{{2,3,6,-1},{-1,-1,-1,1},{0,-1,10,10}});
		movelist.add(new int[][]{{2,3,6,-1},{-1,-1,-1,2},{0,-1,10,10}});
		
		actions = new Action[]{actions[0], actions[1], actions[2], actions[3], new FireBall(this,0), new FireBall(this,1), new FireBall(this,2)};
		normals = new Action[]{new LightPunch(this), new MediumPunch(this), new HeavyPunch(this), new LightKick(this), new MediumKick(this), new HeavyKick(this)};
	}
	
	public void draw(Graphics2D g, ImageObserver i, SpriteReader s, double w, double h, boolean d)
	{
		if(currState.getPosition() < hitboxArchiver.size())
		{
			try
			{
	//			fIndex = 2; fCounter = 1; currState = PlayerState.CROUCHING_LP; currAction = new LightPunch(this);	//TEST
				
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
	
	public void checkState()
	{
		switch(currState.getState())
		{
			case "FIREBALL_LAUNCH":
			case "FIREBALL_RECOVER":
				performAction();
				break;
		}
		super.checkState();
	}
	
	
	public class LightPunch extends Action
	{
		Roo roo;
		boolean cLock;
		
		public LightPunch(Roo r)
		{
			super(Action.NORMAL,1,10,new int[]{0,1,4},new boolean[]{true,true,false,false},new int[]{3,10},new boolean[]{true,true});
			roo = r;
			cLock = false;
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
				if(f == 0)
					cLock = isCrouching;
				else
					isCrouching = cLock;
				
				switch(f)
				{
					case 0:
						currState = (!bounds.isGrounded)? PlayerState.JUMPING_LP:((isCrouching)? PlayerState.CROUCHING_LP:PlayerState.STANDING_LP);
						hashCounter = "";
						
						if(!bounds.isGrounded)
						{
							frames = 1;
						}
						else if(isCrouching)
						{
							plebsOut.add(new Pleb(roo,roo.bounds,bounds.xCoord+110,bounds.yCoord-10,200,35,5,roo.isFacingRight,true));
							frames = 10;
						}
						else
						{
							plebsOut.add(new Pleb(roo,roo.bounds,bounds.xCoord+130,bounds.yCoord+10,180,35,5,roo.isFacingRight,true));
							frames = 10;
						}
						break;
						
					case 2:
						if(!bounds.isGrounded){}
						else if(isCrouching)
							addPleb(roo,0,bounds.xCoord+110,bounds.yCoord+5,125,20,3,0,0,12,1,16,0,0.8,true,false,new int[]{});
						else
							addPleb(roo,0,bounds.xCoord+130,bounds.yCoord+25,140,20,3,0,0,12,1,16,0,0.8,true,false,new int[]{});
						break;
				}
			}
		}
	}
	
	public class MediumPunch extends Action
	{
		Roo roo;
		boolean cLock;
		
		public MediumPunch(Roo r)
		{
			super(Action.NORMAL,1,18,new int[]{2,5},new boolean[]{true,true,false,false},new int[]{5,18},new boolean[]{true,true});
			roo = r;
			cLock = false;
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
				if(f == 0)
					cLock = isCrouching;
				else
					isCrouching = cLock;
				
				switch(f)
				{
					case 0:
						currState = (!bounds.isGrounded)? PlayerState.JUMPING_MP:((isCrouching)? PlayerState.CROUCHING_MP:PlayerState.STANDING_MP);
						hashCounter = "";
						
						if(!bounds.isGrounded)
						{
							frames = 1;
						}
						else if(isCrouching)
						{
							frames = 1;
						}
						else
						{
							plebsOut.add(new Pleb(roo,roo.bounds,bounds.xCoord+130,bounds.yCoord+10,210,35,9,roo.isFacingRight,true));
							frames = 18;
						}
						break;
					case 5:
						if(!bounds.isGrounded){}
						else if(isCrouching){}
						else
						{
							addPleb(roo,0,bounds.xCoord+110,bounds.yCoord+30,85,20,1,0,1,30,10,20,0,0.1,true,false,new int[]{});
							addPleb(roo,0,bounds.xCoord+200,bounds.yCoord+30,50,30,1,0,1,30,10,20,0,0.1,true,false,new int[]{});
						}
						break;
					case 6:
						if(!bounds.isGrounded){}
						else if(isCrouching){}
						else
							addPleb(roo,0,bounds.xCoord+125,bounds.yCoord+30,130,20,3,0,1,30,10,20,0,0.1,true,false,new int[]{});
						break;
				}
			}
		}
	}
	
	public class HeavyPunch extends Action
	{
		Roo roo;
		boolean cLock;
		
		public HeavyPunch(Roo r)
		{
			super(Action.NORMAL,1,28,new int[]{},new boolean[]{true,true,false,false},new int[]{4,28},new boolean[]{true,true});
			roo = r;
			cLock = false;
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
				if(f == 0)
					cLock = isCrouching;
				else
					isCrouching = cLock;
				
				switch(f)
				{
					case 0:
						currState = (!bounds.isGrounded)? PlayerState.JUMPING_HP:((isCrouching)? PlayerState.CROUCHING_HP:PlayerState.STANDING_HP);
						hashCounter = "";
						
						if(!bounds.isGrounded)
						{
							frames = 1;
						}
						else if(isCrouching)
						{
							frames = 1;
						}
						else
						{
							plebsOut.add(new Pleb(roo,roo.bounds,bounds.xCoord+130,bounds.yCoord+6,165,135,10,roo.isFacingRight,true));
							plebsOut.add(new Pleb(roo,roo.bounds,bounds.xCoord+150,bounds.yCoord-150,80,195,10,roo.isFacingRight,true));
							frames = 28;
						}
						break;
					case 3:
						if(!bounds.isGrounded){}
						else if(isCrouching){}
						else
						{
							addPleb(roo,0,bounds.xCoord+138,bounds.yCoord+62,72,52,4,0,2,45,25,18,0,0.75,true,false,new int[]{});
							addPleb(roo,0,bounds.xCoord+142,bounds.yCoord+6,82,62,4,0,2,45,25,18,0,0.75,true,false,new int[]{});
						}
						break;
					case 5:
						if(!bounds.isGrounded){}
						else if(isCrouching){}
						else
						{
							addPleb(roo,0,bounds.xCoord+148,bounds.yCoord-38,55,40,4,0,2,45,25,18,0,0.75,true,false,new int[]{});
							addPleb(roo,0,bounds.xCoord+123,bounds.yCoord-73,55,40,4,0,2,45,25,18,0,0.75,true,false,new int[]{});
						}
						break;
				}
			}
		}
	}
	
	public class LightKick extends Action
	{
		Roo roo;
		boolean cLock;
		
		public LightKick(Roo r)
		{
			super(Action.NORMAL,1,15,new int[]{1,4},new boolean[]{true,true,false,false},new int[]{5,15},new boolean[]{true,true});
			roo = r;
			cLock = false;
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
				if(f == 0)
					cLock = isCrouching;
				else
					isCrouching = cLock;
				
				switch(f)
				{
					case 0:
						currState = (!bounds.isGrounded)? PlayerState.JUMPING_LK:((isCrouching)? PlayerState.CROUCHING_LK:PlayerState.STANDING_LK);
						hashCounter = "";
						
						if(!bounds.isGrounded)
						{
							frames = 1;
						}
						else if(isCrouching)
						{
							frames = 1;
						}
						else
						{
							plebsOut.add(new Pleb(roo,roo.bounds,bounds.xCoord+130,bounds.yCoord+175,195,75,7,roo.isFacingRight,true));
							frames = 15;
						}
						break;
						
					case 4:
						if(!bounds.isGrounded){}
						else if(isCrouching){}
						else
							addPleb(roo,0,bounds.xCoord+130,bounds.yCoord+185,118,35,3,0,1,18,1,17,0,0.8,true,false,new int[]{});
						break;
				}
			}
		}
	}
	
	public class MediumKick extends Action
	{
		Roo roo;
		boolean cLock;
		
		public MediumKick(Roo r)
		{
			super(Action.NORMAL,1,20,new int[]{2,5},new boolean[]{true,true,false,false},new int[]{6,20},new boolean[]{true,true});
			roo = r;
			cLock = false;
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
				if(f == 0)
					cLock = isCrouching;
				else
					isCrouching = cLock;
				
				switch(f)
				{
					case 0:
						currState = (!bounds.isGrounded)? PlayerState.JUMPING_MK:((isCrouching)? PlayerState.CROUCHING_MK:PlayerState.STANDING_MK);
						hashCounter = "";
						
						if(!bounds.isGrounded)
						{
							frames = 1;
						}
						else if(isCrouching)
						{
							frames = 1;
						}
						else
						{
							plebsOut.add(new Pleb(roo,roo.bounds,bounds.xCoord+153,bounds.yCoord+10,168,135,10,roo.isFacingRight,true));
							frames = 20;
						}
						break;
						
					case 2:
						if(!bounds.isGrounded){}
						else if(isCrouching){}
						else
						{
							bounds.forceArchiver.add(new Force("mkStep",(isFacingRight)? 3:1,15,15));
						}
						break;
						
					case 5:
						if(!bounds.isGrounded){}
						else if(isCrouching){}
						else
						{
							addPleb(roo,0,bounds.xCoord+176,bounds.yCoord+50,55,40,4,0,1,22,10,19,0,0.75,true,false,new int[]{});
							addPleb(roo,0,bounds.xCoord+153,bounds.yCoord+90,55,40,4,0,1,22,10,19,0,0.75,true,false,new int[]{});
						}
						break;
				}
			}
		}
	}
	
	public class HeavyKick extends Action
	{
		Roo roo;
		boolean cLock;
		
		public HeavyKick(Roo r)
		{
			super(Action.NORMAL,1,26,new int[]{},new boolean[]{true,true,false,false},new int[]{0,0},new boolean[]{true,true});
			roo = r;
			cLock = false;
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
				if(f == 0)
					cLock = isCrouching;
				else
					isCrouching = cLock;
				
				switch(f)
				{
					case 0:
						currState = (!bounds.isGrounded)? PlayerState.JUMPING_HK:((isCrouching)? PlayerState.CROUCHING_HK:PlayerState.STANDING_HK);
						hashCounter = "";
						
						if(!bounds.isGrounded)
						{
							frames = 1;
						}
						else if(isCrouching)
						{
							frames = 1;
						}
						else
						{
							plebsOut.add(new Pleb(roo,roo.bounds,bounds.xCoord+125,bounds.yCoord-130,130,210,14,roo.isFacingRight,true));
							plebsOut.add(new Pleb(roo,roo.bounds,bounds.xCoord+125,bounds.yCoord-50,210,150,14,roo.isFacingRight,true));
							frames = 26;
						}
						break;
						
					case 8:
						if(!bounds.isGrounded){}
						else if(isCrouching){}
						else
						{
							addPleb(roo,0,bounds.xCoord+140,bounds.yCoord+10,60,40,5,0,2,49,25,24,0,0.45,true,false,new int[]{});
							addPleb(roo,0,bounds.xCoord+160,bounds.yCoord-20,60,40,5,0,2,49,25,24,0,0.45,true,false,new int[]{});
							addPleb(roo,0,bounds.xCoord+180,bounds.yCoord-50,60,40,5,0,2,49,25,24,0,0.45,true,false,new int[]{});
						}
						break;
				}
			}
		}
	}
	
	public class FireBall extends Action
	{
		Roo roo;
		int strength;
		
		public FireBall(Roo r, int s)
		{
			super(Action.SPECIAL,1,38,new int[]{},new boolean[]{false,false,false,false},new int[]{0,0},new boolean[]{true,false});
			roo = r;
			strength = s;
		}
		
		public void perform(int f)
		{
			isPerformingAction = true;
			isCrouching = false;
			bounds.yCoord = yCoord;
			bounds.height = height;
			
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
						if(bounds.isGrounded)
						{
							currState = RooState.FIREBALL_LAUNCH;
							hashCounter = "";
							frames = 38;
						}
						else
							frames = 1;
						break;
						
					case 9:
						propArchiver.add(new Ninja(roo,bounds.xCoord+((isFacingRight)? bounds.width+105:-105),bounds.yCoord+42,120,50,999,1,strength));
						break;
					
					case 29:
			 			currState = RooState.FIREBALL_RECOVER;
			 			fIndex = 0;
						break;
				}
			}
		}
	}
	
	public class Ninja extends Projectile
	{
		Roo roo;
		int speed;
		
		public Ninja(Roo r, int x, int y, int w1, int h1, int h2, int h3, int s)
		{
			super(r,x,y,w1,h1,h2,h3,s);
			roo = r;
			hDamage = 100;
			sDamage = 40;
			
			switch(strength)
			{
				case 0:
					speed = 5; 
					break;
				
				case 1:
					speed = 7; 
					break;
					
				case 2:
					speed = 9; 
					break;
			}
			
			spriteParams = new int[]{940,653,640,480};
			spriteArchiver.add(new int[]{0,0,0,0,2});
		}
		
		public void move()
		{
			if(fCounter == 0)
			{
				plebsOut.add(new Pleb(puppet,bounds,bounds.xCoord-155,bounds.yCoord-35,365,120,3,roo.isFacingRight,true));
				addPleb(0,bounds.xCoord-155,bounds.yCoord-20,260,90,2,0,1,40,40,20,0,1,true,false,new int[]{Pleb.LAUNCH});
			}
			else if(fCounter > 1)
			{
				if(isFacingRight)
					bounds.xCoord += speed;
				else
					bounds.xCoord -= speed;
				if(fCounter >= 2)
					plebsOut.add(new Pleb(puppet,bounds,bounds.xCoord+25,bounds.yCoord-35,130,120,2,roo.isFacingRight,true));
				if(fCounter == 2)
					addPleb(0,bounds.xCoord+25,bounds.yCoord-20,85,90,999,0,1,40,40,20,0,1,true,true,new int[]{});
			}
		}
	}
}