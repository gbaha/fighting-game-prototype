import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

public class Roo extends Player
{
	public Roo(int x, int y, boolean r)
	{
		super(x,y,100,250,150,/*100,*/5,1,r);
		spriteParams = new int[]{345,180,290,178};
		hitboxArchiver.add(new int[][]{new int[]{0,0,0,0,5},
			new int[]{42,-23,53,50,	-22,0,150,55,	-22,55,125,100,	-32,155,160,95},
			new int[]{42,-27,53,50,	-22,0,150,55,	-22,55,125,100,	-32,155,160,95},
			new int[]{42,-37,53,50,	-22,-10,150,55,	-22,50,125,100,	-32,155,160,95},
			new int[]{42,-42,53,50,	-22,-15,150,55,	-22,47,125,100,	-32,155,160,95},
			new int[]{42,-42,53,50,	-22,-15,150,55,	-22,47,125,100,	-32,155,160,95},
			new int[]{42,-42,53,50,	-22,-15,150,55,	-22,47,125,100,	-32,155,160,95},
			new int[]{42,-42,53,50,	-22,-15,150,55,	-22,47,125,100,	-32,155,160,95},
			new int[]{42,-42,53,50,	-22,-15,150,55,	-22,47,125,100,	-32,155,160,95},
			new int[]{42,-37,53,50,	-22,-10,150,55,	-22,50,125,100,	-32,155,160,95},
			new int[]{42,-27,53,50,	-22,0,150,55,	-22,55,125,100,	-32,155,160,95}});
		hitboxArchiver.add(new int[][]{new int[]{1,0,0,0,19},
			new int[]{42,-40,53,50,	-22,-5,125,35,	-22,25,175,40,	-35,60,185,90},
			new int[]{42,-40,53,50,	-22,-10,125,35,	-22,20,175,40,	-35,60,185,90},
			new int[]{42,-40,53,50,	-22,-15,125,35,	-22,15,175,40,	-35,60,185,90},
			new int[]{42,-40,53,50,	-22,-10,125,35,	-22,20,175,40,	-35,60,185,90}});
	}
	
	public void draw(Graphics2D g, ImageObserver i, SpriteReader s, double w, double h, boolean d)
	{
		if(Roo.State.valueOf(currState.toString()).ordinal() < hitboxArchiver.size())
		{
			try
			{
				Image sheet = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resources/roo.png"));
				int f = (hitboxArchiver.get(Roo.State.valueOf(currState.toString()).ordinal())[0][3] == 0)? (int)frameIndex:hitboxArchiver.get(Roo.State.valueOf(currState.toString()).ordinal()).length-(int)frameIndex-2;
			//	f = 0; 	//TEST
				s.read(g,i,xHosh,yHosh,800,490,f,hitboxArchiver.get(Roo.State.valueOf(currState.toString()).ordinal())[0][0],spriteParams,!isFacingRight,sheet);
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