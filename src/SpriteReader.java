import java.awt.AlphaComposite;
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
	ArrayList<double[]> spriteArchiver;	//[xCoord,yCoord,r,g,b]
	ArrayList<Boolean> isOpen;
	BufferedImage[] spriteCache;
	boolean[] isDrawn;
	int timer;
	double width, height, xZoom, yZoom;
	boolean hasRead;
	
	public SpriteReader( double w, double h, double x, double y, int t)
	{
		spriteArchiver = new ArrayList<double[]>();
		isOpen = new ArrayList<Boolean>();
		spriteCache = new BufferedImage[100];
		isDrawn = new boolean[100];
		for(int i = 0; i < 100; i++)
		{
			spriteCache[i] = new BufferedImage((int)w,(int)h,BufferedImage.TYPE_INT_ARGB);
			isOpen.add(true);
			isDrawn[i] = false;
		}
		
		width = w;
		height = h;
		xZoom = x;
		yZoom = y;
		timer = t;
		hasRead = false;
	}
	
	
	public void read(Graphics2D g, ImageObserver i, Image s1, int s2, int x1, int y1, int w, int x2, int y2, int x3, int y3, int[] p, boolean r, double a, ArrayList<double[]> t)
	{
		if(s1.getWidth(i) > 0 && s1.getHeight(i) > 0)
		{
			Graphics2D sRead = spriteCache[s2].createGraphics();	//sprites.get(sprites.size()-1).createGraphics();
			
			AffineTransform sTrans = new AffineTransform();
			sTrans.translate((p[2]*p[6]/p[4])*width*xZoom/1280,(p[3]*p[7]/p[5])*height*yZoom/720);
			sTrans.rotate(a/180*Math.PI);
			sTrans.translate(-(p[2]*p[6]/p[4])*width*xZoom/1280,-(p[3]*p[7]/p[5])*height*yZoom/720);
			sRead.setTransform(sTrans);
			
			sRead.setComposite(AlphaComposite.Clear);
			sRead.fillRect(0,0,spriteCache[s2].getWidth(),spriteCache[s2].getHeight());
			sRead.setComposite(AlphaComposite.SrcOver);
			
			sRead.drawImage(s1,0,0,(int)(p[2]*width*xZoom/1280),(int)(p[3]*height*yZoom/720),x2*p[4],y2*p[5],(x2+1)*p[4],(y2+1)*p[5],i);
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
			recolor(s2,tint[0],tint[1],tint[2],tint[3]);
		/*	
			g.setColor(Color.LIGHT_GRAY);
			g.setColor(new Color(g.getColor().getRed(),g.getColor().getGreen(),g.getColor().getBlue(),100));
			if(s2 > 1)g.fillRect(r?(int)((x1+x3-p[0])*width*xZoom/1280):(int)((x1+x3+w+p[0])*width*xZoom/1280),(int)((y1+y3-p[1])*height*yZoom/720),r?(int)(p[2]*width*xZoom/1280):-(int)(p[2]*width*xZoom/1280),(int)(p[3]*height*yZoom/720));
		*/	
			g.drawImage(spriteCache[s2],r?(int)((x1+x3-p[0])*width*xZoom/1280):(int)((x1+x3+w+p[0])*width*xZoom/1280),(int)((y1+y3-p[1])*height*yZoom/720),r?spriteCache[s2].getWidth():-spriteCache[s2].getWidth(),spriteCache[s2].getHeight(),i);
			isDrawn[s2] = true;
			hasRead = true;
		}
	}
	
	public int addSprite()
	{
		int s = isOpen.indexOf(true);
		isOpen.set(s,false);
		return s;	
	}
	
	public void reset()
	{
		for(int i = 0; i < 100; i++)
		{
			isOpen.set(i,true);
			isDrawn[i] = false;
		}
	}
	
	public void update(double x, double y)
	{
		xZoom = x;
		yZoom = y;
		if(hasRead)
		{
			for(int i = 0; i < 100; i++)
			{
				if(isDrawn[i])
					isDrawn[i] = false;
				else if(!isOpen.get(i))
					isOpen.set(i,true);
			}
		}
		hasRead = false;
	}
	
	
	private void recolor(int s, double r, double g, double b, double a)
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
		
		if(c[0] != 1f || c[1] != 1f || c[2] != 1f || c[3] != 1f)
		{
			RescaleOp rec = new RescaleOp(c,new float[]{0,0,0,0},null);
			spriteCache[s] = rec.filter(spriteCache[s],spriteCache[s]);
		}
	}
}