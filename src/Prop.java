import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import java.util.LinkedList;

abstract class Prop implements Punchable	//, Audible
{
	ArrayList<Pleb> plebsIn, plebsOut;
	ArrayList<int[]> spriteArchiver;
	ArrayList<double[]> sTint;
//	ArrayList<Force> forceArchiver;
	Puppet puppet;
	Organ bounds;
	State currState;
	Image sheet;
	int id, xCoord, yCoord, xHosh, yHosh, xOffset, yOffset, width, height;
	int maxHp, health, hits, fCounter, spriteIndex;
	double fIndex, sAngle;
	boolean isFacingRight, isHit;
	int[] spriteParams;
	
	public enum PropState implements State
	{
		IDLE;
		
		public String getState()
		{
			return name();
		}
		
		public int getPosition()
		{
			return ordinal();
		}
	}
	
	public Prop(int x, int y, int w1, int h1, int h2, int h3)	//, boolean f, boolean s)
	{
		plebsIn = new ArrayList<Pleb>();
		plebsOut = new ArrayList<Pleb>();
		spriteArchiver = new ArrayList<int[]>();
		currState = PropState.IDLE;
		
		sTint = new ArrayList<double[]>();
		sTint.add(new double[]{127.5,127.5,127.5,255});
		
		id = -1;
		xCoord = x;
		yCoord = y;
		xHosh = xCoord;
		yHosh = yCoord;
		xOffset = 0;
		yOffset = 0;
		width = w1;
		height = h1;
		maxHp = h2;
		health = maxHp;
		hits = h3;
		fCounter = 0;
		spriteIndex = -1;
		fIndex = 0;
		sAngle = 0;
		isFacingRight = true;
		isHit = false;
		
		puppet = null;
		bounds = new Organ(x,y,w1,h1,0);
	//	bounds.isFloating = f;
		bounds.isMovable = true;
	}
	
	
	public void draw(Graphics2D g, ImageObserver i, SpriteReader s, double w, double h, boolean[] d)
	{
		if(d[0])
		{
			g.setColor(Color.CYAN);
			g.setColor(new Color(g.getColor().getRed(),g.getColor().getGreen(),g.getColor().getBlue(),50));
			g.fillRect((int)(bounds.xHosh*w/1280),(int)(bounds.yHosh*h/720),(int)(bounds.width*w/1280),(int)(bounds.height*h/720));
			g.setColor(Color.CYAN);
			g.drawRect((int)(bounds.xHosh*w/1280),(int)(bounds.yHosh*h/720),(int)(bounds.width*w/1280),(int)(bounds.height*h/720));
		}
		if(d[1])
		{
			g.setColor(Color.CYAN);
			g.drawString(hits+"",(int)(bounds.xHosh*w/1280)-15,(int)((bounds.yHosh+bounds.height)*h/720));
			g.drawString(health+"",(int)(bounds.xHosh*w/1280)-15,(int)(bounds.yHosh*h/720));
		}
	}
	
	public void move()
	{
		bounds.move();
	}
	
/*	public void addSound(String s, float[] i)
	{
		soundArchiver.addLast(s);
		soundInfo.addLast(i);
	}*/
	
	public void update()
	{
		int i = (int)fIndex+1-((spriteArchiver.get(currState.getPosition())[3] == 0)? spriteArchiver.get(currState.getPosition())[1]:0);
		int f = (int)fIndex+((spriteArchiver.get(currState.getPosition())[3] == 1 && fIndex != (int)fIndex)? 1:0);
		fIndex += (spriteArchiver.get(currState.getPosition())[3] == 0)? 1.0/(spriteArchiver.get(currState.getPosition())[4]+1):-1.0/(spriteArchiver.get(currState.getPosition())[4]+1);
		if(Math.abs(fIndex-f) >= 1)
		{
			fIndex = (int)fIndex;
			i += (spriteArchiver.get(currState.getPosition())[3] == 0)? 1:-1;
		}
		if((spriteArchiver.get(currState.getPosition())[3] == 0 && i >= spriteArchiver.get(currState.getPosition()).length) || (spriteArchiver.get(currState.getPosition())[3] == 1 && i <= 0))
			fIndex = spriteArchiver.get(currState.getPosition())[2];
		fCounter++;
		
		bounds.update();
		if(!plebsIn.isEmpty())
		{
			for(int a = 0; a < plebsIn.size(); a++)
			{
			}
		}
	}
	
	public Organ getBounds()
	{
		return bounds;
	}
}