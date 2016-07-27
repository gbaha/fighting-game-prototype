import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

public class Prop
{
	ArrayList<Pleb> plebArchiver;
//	ArrayList<Force> forceArchiver;
	ArrayList<ArrayList<int[]>> pointArchiver;	//Records where prop's navmesh points are connected
	ArrayList<int[]> touchArchiver;
	Organ bounds;
	String faction;
	int id, height, weight, maxHp, health, hit;
//	int[][] navLines;
	boolean isSturdy;
	
	public Prop(String f1, int x, int y, int w1, int h1, int w2, int h2, boolean f2, boolean s)
	{
		plebArchiver = new ArrayList<Pleb>();
//		forceArchiver = new ArrayList<Force>();
		touchArchiver = new ArrayList<int[]>();	//[{obsPolyNum}, {type, id}, ...]
		pointArchiver = new ArrayList<ArrayList<int[]>>();
		
		touchArchiver.add(new int[]{-1});
		for(int n = 0; n < 4; n++)
		{
			pointArchiver.add(new ArrayList<int[]>());	//[{xCoord, yCoord}, {floorIds (or -1)}, {type, id, pointNum}, ...]
			switch(n)
			{
				case 0:
					pointArchiver.get(0).add(new int[]{x,y});
					pointArchiver.get(0).add(new int[]{-1});
					break;
				case 1:
					pointArchiver.get(1).add(new int[]{x+w1,y});
					pointArchiver.get(1).add(new int[]{-1});
					break;
				case 2:
					pointArchiver.get(2).add(new int[]{x+w1,y+h1});
					pointArchiver.get(2).add(new int[]{-1});
					break;
				case 3:
					pointArchiver.get(3).add(new int[]{x,y+h1});
					pointArchiver.get(3).add(new int[]{-1});
					break;
			}
		}
		
		faction = f1;
		id = -1;
		height = h1;
		weight = w2;
		maxHp = h2;
		health = maxHp;
		hit = 0;
	//	navLines = new int[4][5];
		
	/*	xBlocked = false;
		yBlocked = false;*/
		
		bounds = new Organ(x,y,w1,h1,0);
		bounds.isFloating = f2;
		bounds.isMovable = true;
		isSturdy = s;
	}
	
	
	public void draw(Graphics2D g, double w, double h, boolean d)
	{
		//TEST
		if(isSturdy)
			g.setColor(new Color(255,125,0));
		else
		{
			g.setColor(Color.ORANGE);
			g.drawString(health+"",(int)((bounds.xHosh)*w/1280)-15,(int)(bounds.yHosh*h/720));
		}
		g.drawRect((int)(bounds.xHosh*w/1280),(int)(bounds.yHosh*h/720),(int)(bounds.width*w/1280),(int)(bounds.height*h/720));
		
		if(d)
			g.drawString(id+"",(int)((bounds.xHosh+bounds.width+2)*w/1280),(int)((bounds.yHosh+bounds.height)*h/720));
		//END OF RINE
		//-----------RINE ENDS HERE
	}
	
	public void move()
	{
		bounds.move();
	}
	
	public void takeDamage(Pleb p)
	{
		if(!isSturdy)
		{
			hit = 2;
			health -= p.hDamage;
		}
		if(health < 0)
			health = 0;
		
	/*	boolean isDamaged = true;
		if(plebArchiver.isEmpty())
			plebArchiver.add(p);
		else
		{
			for(int a = 0; a < plebArchiver.size(); a++)
			{
				if(plebArchiver.get(a) == p)
				{
					isDamaged = false;
					if(plebArchiver.get(a).cooldown < plebArchiver.get(a).painThreshold)
						plebArchiver.get(a).cooldown++;
					else
					{
						plebArchiver.get(a).cooldown = 0;
						plebArchiver.remove(a);
						a++;
					}
				}
			}
			if(isDamaged && !p.faction.equals(faction))
				plebArchiver.add(p);
		}
		if(isDamaged && !plebArchiver.isEmpty())
		{
			if(health > 0)
			{
				for(Pleb a: plebArchiver)
				{
					health -= a.strength;
					if(a.strength > 0)
						a.strength -= a.maxStr*(1-a.lastingRate);
				}
			}
			else if(health < 0)
				health = 0;
		}*/
	}
	
	public void update()
	{
		bounds.update();
		bounds.xDir = bounds.xDir;
		bounds.yDir = bounds.yDir;
		bounds.xVel = bounds.xVel;
		bounds.yVel = bounds.yVel;
		bounds.xDrag = bounds.xDrag;
		bounds.yDrag = bounds.yDrag;
		
		if(hit > 0)
			hit--;
		
		if(!plebArchiver.isEmpty())
		{
			for(int a = 0; a < plebArchiver.size(); a++)
			{
		/*		if(plebArchiver.get(a).cooldown < plebArchiver.get(a).painThreshold)
					plebArchiver.get(a).cooldown++;
				else
				{
					plebArchiver.get(a).cooldown = 0;
					plebArchiver.remove(a);
					a++;
				}*/
			}
		}
	}
	
/*	public boolean isFloating()	//STILL DONT KNOW IF WE ARE USING THIS
	{
		return bounds.isFloating;
	}*/
}