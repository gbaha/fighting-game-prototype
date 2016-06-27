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
		spriteParams = new int[]{350,180,290,178};
	}
	
	public void draw(Graphics2D g, ImageObserver i, SpriteReader s, double w, double h, boolean d)
	{
		Image sheet = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resources/roo.png"));
		if(frameIndex*spriteParams[2] >= sheet.getWidth(i))
		 	frameIndex = 0;
		
		s.read(g,i,xHosh,yHosh,800,490,frameIndex,Roo.State.valueOf(currState.toString()).ordinal(),spriteParams,!isFacingRight,sheet);
		super.draw(g,i,s,w,h,d);
	}
}