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
		hitboxArchiver.add(new int[][]{new int[]{0},
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
	}
	
	public void draw(Graphics2D g, ImageObserver i, SpriteReader s, double w, double h, boolean d)
	{//frameIndex = 0; 	//TEST
		Image sheet = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resources/roo.png"));
		if(frameIndex*spriteParams[2] >= sheet.getWidth(i))
		 	frameIndex = 0;
		
		s.read(g,i,xHosh,yHosh,800,490,(int)frameIndex,Roo.State.valueOf(currState.toString()).ordinal(),spriteParams,!isFacingRight,sheet);
		super.draw(g,i,s,w,h,d);
	}
}