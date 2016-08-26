import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.ImageObserver;
import java.util.ArrayList;

abstract class Prop
{
	ArrayList<Pleb> plebsIn, plebsOut;
	ArrayList<int[]> spriteArchiver;
//	ArrayList<Force> forceArchiver;
	Organ bounds;
	State currState;
	int id, xCoord, yCoord, xHosh, yHosh, width, height;
	int maxHp, health, hits, fCounter;
	double fIndex;
	boolean isFacingRight;
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
		
		id = -1;
		xCoord = x;
		yCoord = y;
		xHosh = xCoord;
		yHosh = yCoord;
		width = w1;
		height = h1;
		maxHp = h2;
		health = maxHp;
		hits = h3;
		fCounter = 0;
		fIndex = 0;
		isFacingRight = true;
		
	/*	xBlocked = false;
		yBlocked = false;*/
		
		bounds = new Organ(x,y,w1,h1,0);
	//	bounds.isFloating = f;
		bounds.isMovable = true;
	}
	
	
	public void draw(Graphics2D g, ImageObserver i, SpriteReader s, double w, double h, boolean d)
	{
//		if(d)
//		{
			g.setColor(Color.CYAN);
			g.setColor(new Color(g.getColor().getRed(),g.getColor().getGreen(),g.getColor().getBlue(),50));
			g.fillRect((int)(bounds.xHosh*w/1280),(int)(bounds.yHosh*h/720),(int)(bounds.width*w/1280),(int)(bounds.height*h/720));
			g.setColor(Color.CYAN);
			g.drawRect((int)(bounds.xHosh*w/1280),(int)(bounds.yHosh*h/720),(int)(bounds.width*w/1280),(int)(bounds.height*h/720));
			g.drawString(health+"",(int)(bounds.xHosh*w/1280)-15,(int)(bounds.yHosh*h/720));
//		}
	}
	
	public void move()
	{
		bounds.move();
	}
	
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
}