import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

public class Roo extends Player
{
	public Roo(int x, int y, boolean r)
	{
		super(x,y,100,250,150,/*100,*/6,1,r);
		spriteParams = new int[]{345,180,290,178};
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
		hitboxArchiver.add(new int[][]{new int[]{1,4,4,0,19},
			new int[]{32,-40,53,50,	-32,-5,125,35,	-32,25,175,40,	-45,60,185,90},
			new int[]{32,-40,53,50,	-32,-10,125,35,	-32,20,175,40,	-45,60,185,90},
			new int[]{32,-40,53,50,	-32,-15,125,35,	-32,15,175,40,	-45,60,185,90},
			new int[]{32,-40,53,50,	-32,-10,125,35,	-32,20,175,40,	-45,60,185,90}});
		hitboxArchiver.add(new int[][]{new int[]{1,0,4,1,1},
			new int[]{32,-33,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{45,17,53,50,	-27,47,135,45,	-27,80,170,60,	-45,155,185,95},
			new int[]{32,70,53,50,	-32,85,125,45,	-32,125,190,40,	-45,160,185,90},
			new int[]{32,65,53,50,	-32,90,125,35,	-32,120,175,40,	-45,160,185,90}});
		hitboxArchiver.add(new int[][]{new int[]{1,0,4,0,1},
			new int[]{32,-33,53,50,	-32,0,150,55,	-32,55,125,100,	-42,155,160,95},
			new int[]{45,17,53,50,	-27,47,135,45,	-27,80,170,60,	-45,155,185,95},
			new int[]{32,70,53,50,	-32,85,125,45,	-32,125,190,40,	-45,160,185,90},
			new int[]{32,65,53,50,	-32,90,125,35,	-32,120,175,40,	-45,160,185,90}});
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
	}
	
	public void draw(Graphics2D g, ImageObserver i, SpriteReader s, double w, double h, boolean d)
	{
		if(Roo.State.valueOf(currState.toString()).ordinal() < hitboxArchiver.size())
		{
			try
			{
	//			frameIndex = 0; currState = State.WALK_BACKWARD;	//TEST
			
				Image sheet = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resources/roo.png"));
				int f = (hitboxArchiver.get(Roo.State.valueOf(currState.toString()).ordinal())[0][3] == 0)? (int)frameIndex:hitboxArchiver.get(Roo.State.valueOf(currState.toString()).ordinal()).length-(int)frameIndex-2;
				s.read(g,i,xHosh,yHosh,width,800,490,f,hitboxArchiver.get(Roo.State.valueOf(currState.toString()).ordinal())[0][0],spriteParams,!isFacingRight,sheet);
			}
			catch(java.lang.IndexOutOfBoundsException e)
			{
				draw(g,i,s,w,h,d);
			}
		}
		super.draw(g,i,s,w,h,d);
	}
	
	public void getHitboxes()
	{
		super.getHitboxes(Roo.State.valueOf(currState.toString()).ordinal());
	}
}