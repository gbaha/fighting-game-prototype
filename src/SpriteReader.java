import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.RescaleOp;
import java.util.ArrayList;

//import javax.swing.JPanel;

public class SpriteReader// extends JPanel
{
//	ArrayList<BufferedImage> sprites;
	ArrayList<double[]> spriteArchiver;	//[xCoord,yCoord,r,g,b]
	int timer;
//	int[] xFocus, yFocus;
	double width, height;
	
	public SpriteReader(double w, double h, int t)
	{
//		sprites = new ArrayList<BufferedImage>();
		spriteArchiver = new ArrayList<double[]>();
		width = w;
		height = h;
		timer = t;
	}
	
	
	public void read(Graphics2D g, ImageObserver i, int x1, int y1, int w, int h, int x2, int y2, int[] p, boolean r, Image s)
	{
		if(s.getWidth(i) > 0 && s.getHeight(i) > 0)
		{
			BufferedImage sprite = new BufferedImage((int)(w*width/1280),(int)(h*height/720),BufferedImage.TYPE_INT_ARGB);
			Graphics2D sRead = sprite.createGraphics();	//sprites.get(sprites.size()-1).createGraphics();
			sRead.drawImage(s,0,0,(int)(w*width/1280),(int)(h*height/720),x2*p[2],y2*p[3],(x2+1)*p[2],(y2+1)*p[3],i);
			sRead.dispose();
			
		/*	double[] c = new double[]{127.5,127.5,127.5,255};
			if(p.hit > 0 || p.stun > 0)
			{
				c = new double[]{255,255,255,255};
				if(p.hit > 0)
					t *= 2;
			}
			else if(p.mercy > 0)
			{
				if((p.action[2]/2)%2 == 0)
					c = new double[]{45,45,45,135};
			}
			recolor(sprites.get(sprites.size()-1),c[0],c[1],c[2],c[3]);*/
		//	g.setColor(Color.LIGHT_GRAY);
		//	g.fillRect(r?(int)((x1-p[0])*width/1280):(int)((x1+w-p[0])*width/1280),(int)((y1-p[1])*height/720),(r)?(int)(w*width/1280):-(int)(w*width/1280),(int)(h*height/720));
			g.drawImage(sprite,r?(int)((x1-p[0])*width/1280):(int)((x1+w-p[0])*width/1280),(int)((y1-p[1])*height/720),r?(int)(w*width/1280):-(int)(w*width/1280),(int)(h*height/720),i);
		}
	}
	
	public void recolor(BufferedImage s, double r, double g, double b, double a)
	{
		float[] c = new float[]{1f,1f,1f,1f};
		c[0] = (float)(r/127.5);
		c[1] = (float)(g/127.5);
		c[2] = (float)(b/127.5);
		c[3] = (float)(a/255);
		
		RescaleOp rec = new RescaleOp(c,new float[]{0,0,0,0},null);
		s = rec.filter(s,s);
	}
	
	public void backup(Graphics2D g, boolean d)
	{
	/*	if(width > 0 && height > 0)
		{
			BufferedImage back = new BufferedImage((int)width,(int)height,BufferedImage.TYPE_INT_ARGB);
			if(!d)
			{
				Graphics2D sBack = back.createGraphics();
				for(BufferedImage s: sprites)
				{
					sBack.drawImage(s,null,(int)spriteArchiver.get(sprites.indexOf(s))[0],(int)spriteArchiver.get(sprites.indexOf(s))[1]);
					recolor(s,spriteArchiver.get(sprites.indexOf(s))[2],spriteArchiver.get(sprites.indexOf(s))[3],spriteArchiver.get(sprites.indexOf(s))[4],spriteArchiver.get(sprites.indexOf(s))[5]);
				}
				sBack.dispose();
			}
			
			sprites = new ArrayList<BufferedImage>();
			spriteArchiver = new ArrayList<double[]>();
			
			g.drawImage(back,null,0,0);
		}*/
	}
}