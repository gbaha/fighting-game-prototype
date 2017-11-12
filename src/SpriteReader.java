import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
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
	double width, height;
	
	public SpriteReader(double w, double h, int t)
	{
//		sprites = new ArrayList<BufferedImage>();
		spriteArchiver = new ArrayList<double[]>();
		width = w;
		height = h;
		timer = t;
	}
	
	
	public void read(Graphics2D g, ImageObserver i, int x1, int y1, int w1, int w2, int h, int x2, int y2, int x3, int y3, int[] p, boolean r, double a, ArrayList<double[]> t, Image s)
	{
		if(s.getWidth(i) > 0 && s.getHeight(i) > 0)
		{
			BufferedImage sprite = new BufferedImage((int)(w2*width/1280),(int)(h*height/720),BufferedImage.TYPE_INT_ARGB);
			Graphics2D sRead = sprite.createGraphics();	//sprites.get(sprites.size()-1).createGraphics();
			
			AffineTransform sTrans = new AffineTransform();
			sTrans.translate((w2/2)*width/1280,(h-p[2]*3/5)*height/720);
			sTrans.rotate(a/180*Math.PI);
			sTrans.translate(-(w2/2)*width/1280,(p[2]*3/5-h)*height/720);
			sRead.setTransform(sTrans);
			
			sRead.drawImage(s,0,0,(int)(w2*width/1280),(int)(h*height/720),x2*p[2],y2*p[3],(x2+1)*p[2],(y2+1)*p[3],i);
			sRead.dispose();
			
			double[] tint = new double[]{t.get(0)[0],t.get(0)[1],t.get(0)[2],t.get(0)[3]};
			int tLimit = t.size();
			for(int u = 1; u < tLimit; u++)
			{
				tint[0] += t.get(u)[0];
				tint[1] += t.get(u)[1];
				tint[2] += t.get(u)[2];
				tint[3] += t.get(u)[3];
				t.get(u)[4]--;
				
				if(t.get(u)[4] <= 0)
				{
					t.remove(u);
					tLimit = t.size();
					u--;
				}
			}
			
			recolor(sprite,tint[0],tint[1],tint[2],tint[3]);
		/*	g.setColor(Color.LIGHT_GRAY);
			g.setColor(new Color(g.getColor().getRed(),g.getColor().getGreen(),g.getColor().getBlue(),100));
			g.fillRect(r?(int)((x1-p[0])*width/1280):(int)((x1+w1+p[0])*width/1280),(int)((y1-p[1])*height/720),(r)?(int)(w2*width/1280):-(int)(w2*width/1280),(int)(h*height/720));*/
			
			g.drawImage(sprite,r?(int)((x1+x3-p[0])*width/1280):(int)((x1+x3+w1+p[0])*width/1280),(int)((y1+y3-p[1])*height/720),r?(int)(w2*width/1280):-(int)(w2*width/1280),(int)(h*height/720),i);
		}
	}
	
	public void backup(Graphics2D g, boolean[] d)
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
	
	private void recolor(BufferedImage s, double r, double g, double b, double a)
	{
		float[] c = new float[]{1f,1f,1f,1f};
		
		if(r > 255)
			r = 255;
		else if(r < 0)
			r = 0;
		
		if(g > 255)
			g = 255;
		else if(g < 0)
			g = 0;
		
		if(b > 255)
			b = 255;
		else if(b < 0)
			b = 0;
		
		c[0] = (float)(r/127.5);
		c[1] = (float)(g/127.5);
		c[2] = (float)(b/127.5);
		c[3] = (float)(a/255);
		
		RescaleOp rec = new RescaleOp(c,new float[]{0,0,0,0},null);
		s = rec.filter(s,s);
	}
}