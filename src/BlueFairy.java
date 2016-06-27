import java.awt.Graphics;	//TEST
import java.awt.Color;	//TEST
import java.awt.Polygon;
import java.awt.geom.Line2D;
import java.util.ArrayList;


public class BlueFairy
{
//	ArrayList<Integer> path;
	ArrayList<int[]> /*openNodes, closedNodes,*/ pathNodes;
	ArrayList<double[]> navMesh;
	Puppet puppet;
	Hitbox target;
	boolean /*pathFound,*/ inRange, xLocked, yLocked;
	int pNum, tNum;
	int[] waypoint, pNode;

	public BlueFairy()
	{
	/*	openNodes = new ArrayList<int[]>();
		closedNodes = new ArrayList<int[]>();*/
		pathNodes = new ArrayList<int[]>();
	//	path = new ArrayList<Integer>();
		navMesh = new ArrayList<double[]>();
	//	pathFound = false;
		inRange = false;
		xLocked = false;
		yLocked = false;
		pNum = -1;
		tNum = -1;
		waypoint = new int[3];
		pNode = new int[2];
	}
	
	public BlueFairy(Puppet p, int n)
	{
	/*	openNodes = new ArrayList<int[]>();
		closedNodes = new ArrayList<int[]>();*/
		pathNodes = new ArrayList<int[]>();
//		path = new ArrayList<Integer>();
		navMesh = new ArrayList<double[]>();
		puppet = p;
//		pathFound = false;
		inRange = false;
		xLocked = false;
		yLocked = false;
		pNum = n;
		tNum = -1;
		
		waypoint = new int[3];
		waypoint[0] = puppet.xCoord;
		waypoint[1] = puppet.yCoord;
		
		pNode = new int[2];
		pNode[0] = puppet.xCoord;
		pNode[1] = puppet.yCoord;
	}
	
	
	public void draw(Graphics g, int m, int n, double w, double h)
	{
		g.setColor(Color.RED);
		if(inRange)
			g.setColor(Color.MAGENTA);
		int x = puppet.xCoord+m;
		int y = puppet.yCoord+n;
		for(int p = 0; p < pathNodes.size(); p++)
		{
			try
			{
				g.drawLine((int)(x*w/800),(int)(y*h/600),(int)((pathNodes.get(p)[0]+m)*w/800),(int)((pathNodes.get(p)[1]+n)*h/600));
				g.fillOval((int)((x-3)*w/800),(int)((y-3)*h/600),(int)(6*w/800),(int)(6*h/600));
				x = pathNodes.get(p)[0]+m;
				y = pathNodes.get(p)[1]+n;
			}
			catch(java.lang.IndexOutOfBoundsException e)
			{
				draw(g,m,n,w,h);
			}
		}
		
		g.setColor(Color.MAGENTA);
		g.drawLine((int)(puppet.xHosh*w/800),(int)(puppet.yHosh*h/600),(int)((waypoint[0]+puppet.xHosh-puppet.xCoord)*w/800),(int)((waypoint[1]+puppet.yHosh-puppet.yCoord)*h/600));
		g.fillOval((int)((waypoint[0]+puppet.xHosh-puppet.xCoord-3)*w/800),(int)((waypoint[1]+puppet.yHosh-puppet.yCoord-3)*h/600),(int)(6*w/800),(int)(6*h/600));
	}
	
	public void update(Stage s)
	{
		//TEST
	//	System.out.println(puppet.bounds.xDir+" "+puppet.bounds.yDir+"   "+puppet.xCoord+" "+puppet.yCoord+"   "+puppet.xBlocked+" "+puppet.yBlocked);
		
		
		ArrayList<Puppet> p1 = s.puppets;
		ArrayList<Prop> p2 = s.props;
	//	navMesh = s.navMesh;
		
		ArrayList<Hitbox> targets = new ArrayList<Hitbox>();
		for(Puppet p: p1)
			targets.add(new Organ(p.xCoord,p.yCoord,p.width,p.height,0));
	/*	for(Prop p: p2)
			targets.add(p.bottom);	*/
		
		if(puppet != null)
		{
			/*	1) track
			 * 	2) attack position
			 * 	3) if not brave, retreat/grovel 
			 * 	4) if lost target, return to initial position and idle
			 */
			
			target(targets, s.puppets.size());	//<== NEEDS TO BE FIXED ANYWAY
			//TEST
			target = targets.get(0);
			
			if(target != null)
			{
				for(Prop p: p2)
					targets.add(p.bounds);
				
		//		findPath(s,targets);
				
		/*		if(pathFound)
				{
		//			track(/*new int[] {puppet.xCoord,puppet.yCoord-puppet.height},new int[] {target.xCoord,target.yCoord},*/// s,50);
		/*			if(inRange)
						perform(10);
				}*/
			}
		}
	}
	
	public void METHODNAMEHERE(Stage s)
	{
		if(pathNodes.size() > 0)
		{
			track(s,50);
			if(inRange)
				perform(10);
		}
	}
	
	
	private void idle()
	{
		
	}
	
	private void target(ArrayList<Hitbox> h, int s)
	{
		//TEST
		int n = -1;
		double distance = -1;
		
		for(Hitbox t: h)
		{
			if((distance == -1 || distance > Math.sqrt(Math.pow(t.xCoord-puppet.xCoord,2)+Math.pow(t.yCoord-puppet.yCoord,2))) && (t.xCoord != puppet.xCoord || t.yCoord != puppet.yCoord-puppet.height))
			{
				n = h.indexOf(t);
				distance = Math.sqrt(Math.pow(t.xCoord-puppet.xCoord,2)+Math.pow(t.yCoord-puppet.yCoord,2));
			}
		}
		if(n != -1)
		{
			target = h.get(n);
			if(n < s)
				tNum = n;
		}
		else
			target = null;
	}
	
	private void track(Stage s, int m)	//m = marginOfError
	{
		//=================
		//WAYPOINT CREATION
		//=================
		
	/*	int xDir = 0;
		int yDir = 0;*/
		
		if(!pathNodes.isEmpty())
		{
			if(pathNodes.size() > 1)
			{
				if(waypoint[2] != pathNodes.size())
				{
			/*		if(puppet.xCoord < pathNodes.get(0)[0])
						xDir = 1;
					else if(puppet.xCoord > pathNodes.get(0)[0])
						xDir = -1;
					if(puppet.yCoord > pathNodes.get(0)[1])
						yDir = 1;
					else if(puppet.yCoord < pathNodes.get(0)[1])
						yDir = -1;*/
					
					double x1 = navMesh.get(pathNodes.get(0)[5])[0];
					double x2 = navMesh.get(pathNodes.get(0)[5])[2];
					double y1 = navMesh.get(pathNodes.get(0)[5])[1];
					double y2 = navMesh.get(pathNodes.get(0)[5])[3];
					if(x1 > x2)
					{
						x1 = navMesh.get(pathNodes.get(0)[5])[2];
						x2 = navMesh.get(pathNodes.get(0)[5])[0];
					}
					if(y1 > y2)
					{
						y1 = navMesh.get(pathNodes.get(0)[5])[3];
						y2 = navMesh.get(pathNodes.get(0)[5])[1];
					}
					
					double[] d = new double[4];
					d[0] = Math.sqrt(Math.pow(puppet.xCoord-navMesh.get(pathNodes.get(0)[5])[0],2)+Math.pow(puppet.yCoord-navMesh.get(pathNodes.get(0)[5])[1],2));
					d[1] = Math.sqrt(Math.pow(navMesh.get(pathNodes.get(0)[5])[0]-pathNodes.get(1)[0],2)+Math.pow(navMesh.get(pathNodes.get(0)[5])[1]-pathNodes.get(1)[1],2));
					d[2] = Math.sqrt(Math.pow(puppet.xCoord-navMesh.get(pathNodes.get(0)[5])[2],2)+Math.pow(puppet.yCoord-navMesh.get(pathNodes.get(0)[5])[3],2));
					d[3] = Math.sqrt(Math.pow(navMesh.get(pathNodes.get(0)[5])[2]-pathNodes.get(1)[0],2)+Math.pow(navMesh.get(pathNodes.get(0)[5])[3]-pathNodes.get(1)[1],2));
					
					if(d[0]+d[1] > d[2]+d[3] && x2-x1 > puppet.width && y2-y1 > puppet.height)
					{
						if(navMesh.get(pathNodes.get(0)[5])[2] == x1)
							waypoint[0] = (int)(x1+(puppet.width+Math.random()*((x2-x1)/2-puppet.width)+0.5))+1;
						else
							waypoint[0] = (int)(x2-(puppet.width+Math.random()*((x2-x1)/2-puppet.width)+0.5))-1;
					}
					else if(d[0]+d[1] < d[2]+d[3] && x2-x1 > puppet.width && y2-y1 > puppet.height)
					{
						if(navMesh.get(pathNodes.get(0)[5])[0] == x1)
							waypoint[0] = (int)(x1+(puppet.width+Math.random()*((x2-x1)/2-puppet.width)+0.5))+1;
						else
							waypoint[0] = (int)(x2-(puppet.width+Math.random()*((x2-x1)/2-puppet.width)+0.5))-1;
					}
					else
						waypoint[0] = (int)(x1+(x2-x1)/2-puppet.width/2);
					
					waypoint[1] = (int)(y1+(y2-y1)/2+(waypoint[0]-(x1+(x2-x1)/2))*(navMesh.get(pathNodes.get(0)[5])[3]-navMesh.get(pathNodes.get(0)[5])[1])/(navMesh.get(pathNodes.get(0)[5])[2]-navMesh.get(pathNodes.get(0)[5])[0]));
					waypoint[2] = pathNodes.size();
				}
				
				//TEST
			//	System.out.println(waypoint[0]+" "+waypoint[1]);
				
				//===============
				//PUPPET MOVEMENT
				//===============
				
				if(puppet.xCoord < waypoint[0])
				{
					if(puppet.xCoord+puppet.bounds.xForward > waypoint[0])
					{
						puppet.xCoord = waypoint[0];
						puppet.bounds.xDir = 0;
						puppet.bounds.xVel = 0;
					}
					else// if(Math.abs(puppet.xCoord-waypoint[0]) > Math.abs(puppet.yCoord-waypoint[1]))
						puppet.bounds.xDir = 1;
				}
				else if(puppet.xCoord > waypoint[0])
				{
					if(puppet.xCoord-puppet.bounds.xForward < waypoint[0])
					{
						puppet.xCoord = waypoint[0];
						puppet.bounds.xDir = 0;
						puppet.bounds.xVel = 0;
					}
					else// if(Math.abs(puppet.xCoord-waypoint[0]) > Math.abs(puppet.yCoord-waypoint[1]))
						puppet.bounds.xDir = -1;
				}
				else
					puppet.bounds.xDir = 0;
				
				if(puppet.yCoord > waypoint[1])
				{
					if(puppet.yCoord-puppet.bounds.yForward < waypoint[1])
					{
						puppet.yCoord = waypoint[1];
						puppet.bounds.yDir = 0;
						puppet.bounds.yVel = 0;
					}
					else// if(Math.abs(puppet.yCoord-waypoint[1]) > Math.abs(puppet.xCoord-waypoint[0]))
						puppet.bounds.yDir = 1;
				}
				else if(puppet.yCoord < waypoint[1])
				{
					if(puppet.yCoord+puppet.bounds.yForward > waypoint[1])
					{
						puppet.yCoord = waypoint[1];
						puppet.bounds.yDir = 0;
						puppet.bounds.yVel = 0;
					}
					else// if(Math.abs(puppet.yCoord-waypoint[1]) > Math.abs(puppet.xCoord-waypoint[0]))
						puppet.bounds.yDir = -1;
				}
				else
					puppet.bounds.yDir = 0;
				
				//TEST
		//		System.out.println(Math.abs(puppet.xCoord-waypoint[0])+"("+puppet.bounds.xVel+") "+Math.abs(puppet.yCoord-waypoint[1])+"("+puppet.bounds.yVel+")  ["+puppet.direction+"]");
		//		System.out.println(">>   "+puppet.+" "+puppet.yCoord);
				//==
				
				if(puppet.bounds.xDir != 0 && puppet.bounds.yDir != 0)
				{
					int v = puppet.bounds.xVel;
					if(v < puppet.bounds.yVel)
						puppet.bounds.xVel = v;
					else
						puppet.bounds.yVel = v;
					
					if(Math.abs(puppet.xCoord-waypoint[0]) > Math.abs(puppet.yCoord-waypoint[1]) && (puppet.bounds.blocked[1] == puppet.bounds.xCoord+puppet.bounds.width/2 || puppet.bounds.blocked[3] == puppet.bounds.xCoord+puppet.bounds.width/2))
					{
						if(Math.abs(puppet.yCoord-waypoint[1]) > Math.abs(puppet.xCoord-waypoint[0]+puppet.bounds.xVel*puppet.bounds.xDir))
							puppet.xCoord = waypoint[0]+(puppet.yCoord-waypoint[1]);
						else
							puppet.bounds.yDir = 0;
					}
					else if(Math.abs(puppet.yCoord-waypoint[1]) > Math.abs(puppet.xCoord-waypoint[0]) && (puppet.bounds.blocked[0] == puppet.bounds.yCoord+puppet.bounds.height/2 || puppet.bounds.blocked[2] == puppet.bounds.yCoord+puppet.bounds.height/2))
					{
						if(Math.abs(puppet.xCoord-waypoint[0]) > Math.abs(puppet.yCoord-waypoint[1]+puppet.bounds.yVel*puppet.bounds.yDir))
							puppet.yCoord = waypoint[1]+(puppet.xCoord-waypoint[0]);
						else
							puppet.bounds.xDir = 0;
					}
				}
			}
			else
			{
				waypoint[0] = puppet.xCoord;
				waypoint[1] = puppet.yCoord;
				waypoint[2] = 1;
				
				if(puppet.xCoord < target.xCoord-m)
					puppet.bounds.xDir = 1;
				else if(puppet.xCoord > target.xCoord+m)
					puppet.bounds.xDir = -1;
				else
					puppet.bounds.xDir = 0;
				
				if(puppet.yCoord > target.yCoord+m)
					puppet.bounds.yDir = 1;
				else if(puppet.yCoord < target.yCoord-m)
					puppet.bounds.yDir = -1;
				else
					puppet.bounds.yDir = 0;
				
				if(Math.abs(puppet.xCoord-target.xCoord) > Math.abs(puppet.yCoord-target.yCoord) && (puppet.bounds.blocked[1] == puppet.bounds.xCoord || puppet.bounds.blocked[3] == puppet.bounds.xCoord))
				{
					if(Math.abs(puppet.yCoord-target.yCoord) > Math.abs(puppet.xCoord-target.xCoord+puppet.bounds.xVel*puppet.bounds.xDir))
						puppet.xCoord = target.xCoord+(puppet.yCoord-target.yCoord);
					else
						puppet.bounds.yDir = 0;
				}
				else if(Math.abs(puppet.yCoord-target.yCoord) > Math.abs(puppet.xCoord-target.xCoord) && (puppet.bounds.blocked[0] == puppet.bounds.xCoord || puppet.bounds.blocked[2] == puppet.bounds.xCoord))
				{
					if(Math.abs(puppet.xCoord-target.xCoord) > Math.abs(puppet.yCoord-target.yCoord+puppet.bounds.yVel*puppet.bounds.yDir))
						puppet.yCoord = target.yCoord+(puppet.xCoord-target.xCoord);
					else
						puppet.bounds.xDir = 0;
				}
			}
			
			//=================
			//PATH NODE REMOVAL
			//=================
			
			if(pathNodes.size() > 1)
			{
				Line2D.Double l = new Line2D.Double(navMesh.get(pathNodes.get(0)[5])[0],navMesh.get(pathNodes.get(0)[5])[1],navMesh.get(pathNodes.get(0)[5])[2],navMesh.get(pathNodes.get(0)[5])[3]);
				if(l.intersects(puppet.xCoord,puppet.yCoord-puppet.height,puppet.width,puppet.height))
				{
					pNode = new int[]{waypoint[0],waypoint[1]};
					pathNodes.remove(0);
				}
			}
		}
	}
	
	private void perform(int m)
	{
		if(puppet.xCoord < target.xCoord-m)
		{
		/*	if(puppet.yCoord > target.yCoord+m)
				puppet.direction = 1;
			else if(puppet.yCoord < target.yCoord-m)
				puppet.direction = 3;
			else
				puppet.direction = 2;*/
			
			puppet.isFacingRight = true;
		}
		else if(puppet.xCoord > target.xCoord+m)
		{
		/*	if(puppet.yCoord > target.yCoord+m)
				puppet.direction = 7;
			else if(puppet.yCoord < target.yCoord-m)
				puppet.direction = 5;
			else
				puppet.direction = 6;*/
			
			puppet.isFacingRight = false;
		}
	/*	else
		{
			if(puppet.yCoord > target.yCoord+m)
				puppet.direction = 0;
			else if(puppet.yCoord < target.yCoord-m)
				puppet.direction = 4;
		}*/
	}
	
	private void retreat()
	{
	}
}