import java.awt.Graphics;	//TEST
import java.awt.Color;	//TEST
import java.awt.Polygon;
import java.awt.geom.Line2D;
import java.util.ArrayList;


public class Cricket
{
//	ArrayList<Integer> path;
	ArrayList<int[]> openNodes, closedNodes/*, f.pathNodes*/;
//	ArrayList<double[]> navMesh;
	Stage stage;
/*	Puppet f.puppet;
	Hitbox f.target;*/
	boolean pathFound, inRange, xLocked, yLocked;
/*	int f.pNum, f.tNum;
	int[] waypoint, pNode;*/

	public Cricket()
	{
		openNodes = new ArrayList<int[]>();
		closedNodes = new ArrayList<int[]>();
	//	f.pathNodes = new ArrayList<int[]>();
	//	path = new ArrayList<Integer>();
//		navMesh = new ArrayList<double[]>();
		pathFound = false;
		inRange = false;
		xLocked = false;
		yLocked = false;
/*		f.pNum = -1;
		f.tNum = -1;
		waypoint = new int[3];
		pNode = new int[2];*/
	}
	
	public Cricket(Stage s)
	{
		openNodes = new ArrayList<int[]>();
		closedNodes = new ArrayList<int[]>();
//		f.pathNodes = new ArrayList<int[]>();
//		path = new ArrayList<Integer>();
//		navMesh = new ArrayList<double[]>();
		stage = s;
//		f.puppet = p;
		pathFound = false;
		inRange = false;
		xLocked = false;
		yLocked = false;
//		f.pNum = n;
//		f.tNum = -1;
		
	/*	waypoint = new int[3];
		waypoint[0] = f.puppet.xCoord;
		waypoint[1] = f.puppet.yCoord;
		
		pNode = new int[2];
		pNode[0] = f.puppet.xCoord;
		pNode[1] = f.puppet.yCoord;*/
	}
	
	
/*	public void draw(Graphics g, int m, int n, double w, double h)
	{
		g.setColor(Color.RED);
		if(inRange)
			g.setColor(Color.MAGENTA);
		int x = f.puppet.xCoord+m;
		int y = f.puppet.yCoord+n;
		for(int p = 0; p < f.pathNodes.size(); p++)
		{
			try
			{
				g.drawLine((int)(x*w/800),(int)(y*h/600),(int)((f.pathNodes.get(p)[0]+m)*w/800),(int)((f.pathNodes.get(p)[1]+n)*h/600));
				g.fillOval((int)((x-3)*w/800),(int)((y-3)*h/600),(int)(6*w/800),(int)(6*h/600));
				x = f.pathNodes.get(p)[0]+m;
				y = f.pathNodes.get(p)[1]+n;
			}
			catch(java.lang.IndexOutOfBoundsException e)
			{
				draw(g,m,n,w,h);
			}
		}
		
		g.setColor(Color.MAGENTA);
		g.drawLine((int)(f.puppet.xHosh*w/800),(int)(f.puppet.yHosh*h/600),(int)((waypoint[0]+f.puppet.xHosh-f.puppet.xCoord)*w/800),(int)((waypoint[1]+f.puppet.yHosh-f.puppet.yCoord)*h/600));
		g.fillOval((int)((waypoint[0]+f.puppet.xHosh-f.puppet.xCoord-3)*w/800),(int)((waypoint[1]+f.puppet.yHosh-f.puppet.yCoord-3)*h/600),(int)(6*w/800),(int)(6*h/600));
	}*/
	
/*	private void f.target(ArrayList<Hitbox> h, int s)
	{
		//TEST
		int n = -1;
		double distance = -1;
		
		for(Hitbox t: h)
		{
			if((distance == -1 || distance > Math.sqrt(Math.pow(t.xCoord-f.puppet.xCoord,2)+Math.pow(t.yCoord-f.puppet.yCoord,2))) && (t.xCoord != f.puppet.xCoord || t.yCoord != f.puppet.yCoord-f.puppet.height))
			{
				n = h.indexOf(t);
				distance = Math.sqrt(Math.pow(t.xCoord-f.puppet.xCoord,2)+Math.pow(t.yCoord-f.puppet.yCoord,2));
			}
		}
		if(n != -1)
		{
			f.target = h.get(n);
			if(n < s)
				f.tNum = n;
		}
		else
			f.target = null;
	}*/
	
	public void findPath(BlueFairy f)
	{
		int distance = -1;
		if(f.pathNodes.isEmpty())
		{
			int x = f.puppet.xCoord;
			int y = f.puppet.yCoord;
			int gScore = 0;	//(int)(Math.sqrt(Math.pow(x-p[0],2)+Math.pow(y-p[1],2))+0.5);
			int hScore = Math.abs(x-f.target.xCoord)+Math.abs(y-f.target.yCoord);	//(int)(Math.sqrt(Math.pow(x-t[0],2)+Math.pow(y-t[1],2))+0.5);
			
			pathFound = true;
			double x1 = x;
			double y1 = y;
			double x2 = f.target.xCoord;
			double y2 = f.target.yCoord;
			int u1 = 0;
			int u2 = 0;
			int v1 = 0;
			int v2 = 0;
			
			if(x1 > x2)
			{
				u1 = -1;
				u2 = 1;
			}
			else if(x1 < x2)
			{
				u1 = 1;
				u2 = -1;
			}
			
			if(y1 > y2)
			{
				v1 = -1;
				v2 = 1;
			}
			else if(y1 < y2)
			{
				v1 = 1;
				v2 = -1;
			}
			Line2D.Double l = new Line2D.Double(x1+u1,y1+v1,x2+u2,y2+v2);
			
			for(Polygon o: stage.mapPolys)
			{
				if(stage.mapPolys.indexOf(o) == 0 && (!o.contains(x1+u1,y1+v1) || !o.contains(x2+u2,y2+v2)))
					pathFound = false;
				if(stage.mapPolys.indexOf(o) > 0 && (o.contains(x1+u1,y1+v1) || o.contains(x2+u2,y2+v2)))
					pathFound = false;
				
				for(int o1 = 0; o1 < o.npoints; o1++)
				{
					int o2 = o1+1;
					if(o2 >= o.npoints)
						o2 -= o.npoints;
					if(l.intersectsLine(o.xpoints[o1],o.ypoints[o1],o.xpoints[o2],o.ypoints[o2]))
						pathFound = false;
				}
			}
			
			for(Puppet p: stage.puppets)
			{
				if(l.intersects(p.xCoord,p.yCoord,p.width,p.height) && p != f.puppet && (p.xCoord != f.target.xCoord && p.yCoord-p.height != f.target.yCoord))
					pathFound = false;
			}
			for(Prop p: stage.props)
			{
				if(l.intersects(p.bounds.xCoord,p.bounds.yCoord,p.bounds.width,p.bounds.height))
					pathFound = false;
			}
			
			if(pathFound)
			{
				f.pathNodes = new ArrayList<int[]>();
				f.pathNodes.add(new int[]{f.target.xCoord,f.target.yCoord,Math.abs(x-f.target.xCoord)+Math.abs(y-f.target.yCoord),0,0,-1});
			}
			else
			/*	boolean isClosed = false;
			if(openNodes.isEmpty())
			{
				for(int[] c: closedNodes)
				{
					if(c == new int[]{x,y,gScore,hScore,0})
						isClosed = true;
				}
				if(!isClosed)*/
					openNodes.add(new int[]{x,y,gScore,hScore,0,-1});
		//	}
		}
		else
		{
			//==================
			//INITIAL PATH CHECK
			//==================
			
			if(f.pathNodes.get(f.pathNodes.size()-1)[0] != f.target.xCoord || f.pathNodes.get(f.pathNodes.size()-1)[1] != f.target.yCoord)
			{
				double x1 = f.target.xCoord;
				double y1 = f.target.yCoord;
				double x2 = f.puppet.xCoord;
				double y2 = f.puppet.yCoord;
				int u1 = 0;
				int u2 = 0;
				int v1 = 0;
				int v2 = 0;
				
				if(f.pathNodes.size() > 1)
				{
					x2 = f.pathNodes.get(f.pathNodes.size()-2)[0];
					y2 = f.pathNodes.get(f.pathNodes.size()-2)[1];
				}
				if(x1 > x2)
				{
					u1 = -1;
					u2 = 1;
				}
				else if(x1 < x2)
				{
					u1 = 1;
					u2 = -1;
				}
				
				if(y1 > y2)
				{
					v1 = -1;
					v2 = 1;
				}
				else if(y1 < y2)
				{
					v1 = 1;
					v2 = -1;
				}
				Line2D.Double l = new Line2D.Double(x1+u1,y1+v1,x2+u2,y2+v2);
				
				for(Polygon o: stage.mapPolys)
				{
					if(stage.mapPolys.indexOf(o) == 0 && (!o.contains(x1+u1,y1+v1) || !o.contains(x2+u2,y2+v2)))
						pathFound = false;
					if(stage.mapPolys.indexOf(o) > 0 && (o.contains(x1+u1,y1+v1) || o.contains(x2+u2,y2+v2)))
						pathFound = false;
					
					for(int o1 = 0; o1 < o.npoints; o1++)
					{
						int o2 = o1+1;
						if(o2 >= o.npoints)
							o2 -= o.npoints;
						if(l.intersectsLine(o.xpoints[o1],o.ypoints[o1],o.xpoints[o2],o.ypoints[o2]))
							pathFound = false;
					}
				}
				
				for(Puppet p: stage.puppets)
				{
					if(l.intersects(p.xCoord,p.yCoord,p.width,p.height) && p != f.puppet)
						pathFound = false;
				}
				for(Prop p: stage.props)
				{
					if(l.intersects(p.bounds.xCoord,p.bounds.yCoord,p.bounds.width,p.bounds.height))
						pathFound = false;
				}
				
				//TEST
		//		System.out.println((int)x1+" "+(int)y1+"   "+(int)x2+" "+(int)y2+"   "+pathFound);
				
				if(pathFound)
				{
					f.pathNodes.get(f.pathNodes.size()-1)[0] = f.target.xCoord;
					f.pathNodes.get(f.pathNodes.size()-1)[1] = f.target.yCoord;
				}
				else
					f.pathNodes = new ArrayList<int[]>();
			}
		}
		
		//==============================
		//PUPPET AND TARGET NODE REMOVAL
		//==============================
		
		f.navMesh = new ArrayList<double[]>();
		for(double[] n: stage.navMesh)
			f.navMesh.add(n);
		int nLimit = f.navMesh.size();
		
		for(int n = 0; n < nLimit; n++)
		{
			if(f.navMesh.get(n)[4] == 3 && (f.navMesh.get(n)[5] == f.pNum || f.navMesh.get(n)[5] == f.tNum))
			{
				f.navMesh.remove(n);
				nLimit--;
				n--;
			}
		}
		
		while(!pathFound)
		{
			//=======================
			//SORTED NODE ARRANGEMENT
			//=======================
			
			ArrayList<int[]> sortedNodes = new ArrayList<int[]>();
			for(int[] n: openNodes)
			{
				if(sortedNodes.isEmpty())
					sortedNodes.add(n);
				else
				{
					int s = 0;
					for(int o = 0; o < sortedNodes.size(); o++)
					{
						if(n[2]+n[3] > sortedNodes.get(o)[2]+sortedNodes.get(o)[3])
							s++;
					}
					sortedNodes.add(s,n);
				}
			}
			
			for(int[] s: sortedNodes)
			{
				//======================
				//ADJACENT NODE CREATION
				//======================
				
				openNodes.remove(s);
				closedNodes.add(s);
				
				ArrayList<int[]> adjacentNodes = getAdjacentNodes(s,f.navMesh,f.puppet,f.target,closedNodes.indexOf(s));
				ArrayList<int[]> sortedAdjacentNodes = new ArrayList<int[]>();
				for(int[] a: adjacentNodes)
				{
					if(sortedAdjacentNodes.isEmpty())
						sortedAdjacentNodes.add(a);
					else
					{
						int b = 0;
						for(int c = 0; c < sortedAdjacentNodes.size(); c++)
						{
							if(a[2]+a[3] > sortedAdjacentNodes.get(c)[2]+sortedAdjacentNodes.get(c)[3])
								b++;
						}
						sortedAdjacentNodes.add(b,a);
					}
				}
				
				//=================
				//LOOPED PATH CHECK
				//=================
				
				for(int a = 0; a < sortedAdjacentNodes.size(); a++)
				{
					if(!pathFound)
					{
						pathFound = true;
						double x1 = f.target.xCoord;
						double y1 = f.target.yCoord;
						double x2 = sortedAdjacentNodes.get(a)[0];
						double y2 = sortedAdjacentNodes.get(a)[1];
						int u1 = 0;
						int u2 = 0;
						int v1 = 0;
						int v2 = 0;
						
						if(x1 > x2)
						{
							u1 = -1;
							u2 = 1;
						}
						else if(x1 < x2)
						{
							u1 = 1;
							u2 = -1;
						}
						
						if(y1 > y2)
						{
							v1 = -1;
							v2 = 1;
						}
						else if(y1 < y2)
						{
							v1 = 1;
							v2 = -1;
						}
						Line2D.Double l = new Line2D.Double(x1+u1,y1+v1,x2+u2,y2+v2);
						
						for(Polygon o: stage.mapPolys)
						{
							if(stage.mapPolys.indexOf(o) == 0 && (!o.contains(x1+u1,y1+v1) || !o.contains(x2+u2,y2+v2)))
								pathFound = false;
							if(stage.mapPolys.indexOf(o) > 0 && (o.contains(x1+u1,y1+v1) || o.contains(x2+u2,y2+v2)))
								pathFound = false;
							
							for(int o1 = 0; o1 < o.npoints; o1++)
							{
								int o2 = o1+1;
								if(o2 >= o.npoints)
									o2 -= o.npoints;
								if(l.intersectsLine(o.xpoints[o1],o.ypoints[o1],o.xpoints[o2],o.ypoints[o2]))
									pathFound = false;
							}
						}
						
						for(Puppet p: stage.puppets)
						{
							if(f.target.xCoord != p.xCoord && f.target.yCoord != p.yCoord)
							{
								if(l.intersects(p.xCoord,p.yCoord,p.width,p.height) && p != f.puppet)
									pathFound = false;
							}
						}
						
						for(Prop p: stage.props)
						{
							if(f.target.xCoord != p.bounds.xCoord && f.target.yCoord != p.bounds.yCoord)
							{
								if(l.intersects(p.bounds.xCoord,p.bounds.yCoord,p.bounds.width,p.bounds.height))
									pathFound = false;
							}
						}
						
						//==================
						//PATH NODE CREATION
						//==================
						
						if(pathFound)
						{
							ArrayList<int[]> pNodes = new ArrayList<int[]>();
							pNodes.add(sortedAdjacentNodes.get(a));
							
							int d = 0;
							while(pNodes.get(0)[0] != f.puppet.xCoord || pNodes.get(0)[1] != f.puppet.yCoord)
							{
								pNodes.add(0,closedNodes.get(pNodes.get(0)[4]));
								d += (int)(Math.sqrt(Math.pow(pNodes.get(0)[0]-pNodes.get(1)[0],2)+Math.pow(pNodes.get(0)[1]-pNodes.get(1)[1],2))+0.5);
							}
							d += (int)(Math.sqrt(Math.pow(pNodes.get(pNodes.size()-1)[0]-f.target.xCoord,2)+Math.pow(pNodes.get(pNodes.size()-1)[1]-f.target.yCoord,2))+0.5);
							pNodes.remove(0);
							
							if(distance == -1 || (d < distance))
							{
								f.pathNodes = new ArrayList<int[]>();
								for(int[] p: pNodes)
									f.pathNodes.add(p);
								f.pathNodes.add(new int[]{f.target.xCoord,f.target.yCoord,Math.abs(f.puppet.xCoord-f.target.xCoord)+Math.abs(f.puppet.yCoord-f.target.yCoord),0,0,-1});
								distance = d;
							}
							pathFound = false;
						}
					}
					
					//==============================
					//SORTED ADJACENT NODE PLACEMENT
					//==============================
					
					boolean inBounds = false;
					for(Floor b: stage.floors)
					{
						if(sortedAdjacentNodes.get(a)[0] >= b.xCoord && sortedAdjacentNodes.get(a)[0] <= b.xCoord+b.width && sortedAdjacentNodes.get(a)[1] >= b.yCoord && sortedAdjacentNodes.get(a)[1] <= b.yCoord+b.height)
							inBounds = true;
					}
					if(!inBounds)
						closedNodes.add(sortedAdjacentNodes.get(a));
					
					boolean isOpen = false;
					for(int[] o: openNodes)
					{
						if(o[0] == sortedAdjacentNodes.get(a)[0] && o[1] == sortedAdjacentNodes.get(a)[1])
							isOpen = true;
					}
					
					boolean isClosed = false;
					for(int[] c: closedNodes)
					{
						if(c[0] == sortedAdjacentNodes.get(a)[0] && c[1] == sortedAdjacentNodes.get(a)[1])
							isClosed = true;
					}
					
					if(sortedAdjacentNodes.get(a)[2] > s[2]+(int)(Math.sqrt(Math.pow(f.puppet.xCoord-s[0],2)+Math.pow(f.puppet.yCoord-s[1],2))+0.5) || !isOpen)
					{
						sortedAdjacentNodes.get(a)[2] = s[2]+(int)(Math.sqrt(Math.pow(f.puppet.xCoord-s[0],2)+Math.pow(f.puppet.xCoord-s[1],2))+0.5);
						if(!isOpen && !isClosed)
							openNodes.add(sortedAdjacentNodes.get(a));
					}
				}
				
				if(distance != -1 && f.pathNodes.size() > 0)
				{
					int oLimit = openNodes.size();
					for(int o = 0; o < oLimit; o++)
					{
						int d = 0;
						int[] n = openNodes.get(o);
						while(n[4] != 0)
						{
							d += (int)(Math.sqrt(Math.pow(n[0]-closedNodes.get(n[4])[0],2)+Math.pow(n[1]-closedNodes.get(n[4])[1],2))+0.5);
							n = closedNodes.get(n[4]);
						}
						if(n[4] == 0)
							d += (int)(Math.sqrt(Math.pow(n[0]-f.puppet.xCoord,2)+Math.pow(n[1]-f.puppet.yCoord,2))+0.5);
						
						if( d >= distance)
						{
							closedNodes.add(openNodes.get(o));
							openNodes.remove(o);
							oLimit--;
							o--;
							if(o < 0)
								o = 0;
						}
					}
				}
			}

			sortedNodes = new ArrayList<int[]>();
			for(int[] n: openNodes)
			{
				if(sortedNodes.isEmpty())
					sortedNodes.add(n);
				else
				{
					int s = 0;
					for(int o = 0; o < sortedNodes.size(); o++)
					{
						if(n[2]+n[3] > sortedNodes.get(o)[2]+sortedNodes.get(o)[3])
							s++;
					}
					sortedNodes.add(s,n);
				}
			}
			
			//============
			//LOOP BREAKER
			//============
			
		/*	if(sortedNodes.isEmpty())
			{
				pathFound = true;
			}
			else if(!f.pathNodes.isEmpty())
			{
				if(sortedNodes.get(0)[3]+sortedNodes.get(0)[4] >= f.pathNodes.get(f.pathNodes.size()-1)[3]+f.pathNodes.get(f.pathNodes.size()-1)[4])
					pathFound = true;
			}*/
			
			if(openNodes.isEmpty())
				pathFound = true;
		}
		
		openNodes = new ArrayList<int[]>();	//REMOVE LATER... MAYBE
		closedNodes = new ArrayList<int[]>();
		
		//TEST
	//	System.out.println("))<>((");
	}
	
	public void condenseMinds(ArrayList<BlueFairy> f)
	{
		ArrayList<BlueFairy> fairiesWithACause = new ArrayList<BlueFairy>();
		for(BlueFairy a: f)
		{
			if(a.target != null)
				fairiesWithACause.add(a);
		}
		
		for(BlueFairy b1: fairiesWithACause)
		{
			for(BlueFairy b2: fairiesWithACause)
			{
				if(f.indexOf(b1) != f.indexOf(b2))
				{
					if((b1.target.xCoord == b2.target.xCoord && b1.target.yCoord == b2.target.yCoord) && b1.pathNodes.size() > 0 && b2.pathNodes.size() > 0)
					{
		//				if(b1.pathNodes.get(0)[0])
					}
				}
			}
		}
	}
	
	
	private ArrayList<int[]> getAdjacentNodes(int[] s, ArrayList<double[]> n, Puppet p, Hitbox t, int i)
	{
		ArrayList<int[]> aNodes = new ArrayList<int[]>();
		double x0 = s[0];
		double y0 = s[1];
		
		if(x0 == p.xCoord)
			x0 += p.width;
		if(y0 == p.yCoord-p.height)
			y0 += p.height*5/4;
		
		for(double[] m1: n)
		{
	//		ArrayList<double[]> dNodes = new ArrayList<double[]>();
			double x1 = m1[0];
			double y1 = m1[1];
			double x2 = m1[2];
			double y2 = m1[3];
	//		double e = 0;
			
	/*		if(x1 > x2)
			{
				x1 = m1[2];
				x2 = m1[0];
			}
			if(y1 > y2)
			{
				y1 = m1[3];
				y2 = m1[1];
			}
			
			for(int d = 0; d < 8; d++)
			{
				switch(d)
				{
					case 0:
						if(x0 >= x1 && x0 <= x2 && y0 > y2)
						{
							double x = x0;
							double y = m1[1]+(m1[0]-x0)*(m1[3]-m1[1])/(m1[2]-m1[0]);
							double gScore = Math.sqrt(Math.pow(x-p.xCoord,2)+Math.pow(y-p.yCoord,2))+0.5;
							double hScore = Math.sqrt(Math.pow(x-t.xCoord,2)+Math.pow(y-t.yCoord,2))+0.5;
							dNodes.add(new double[]{x,y,gScore,hScore});
						}
						break;
						
					case 1:
						e = 0;
						if(Math.abs(x0-x2) > Math.abs(y0-y1))
							e = Math.abs(y0-y1);
						else
							e = Math.abs(x0-x2);
						
						if(Line2D.linesIntersect(x0,y0,x0+e,y0-e,m1[0],m1[1],m1[2],m1[3]))
						{
							double x = x0+Line2D.ptLineDist(m1[0],m1[1],m1[2],m1[3],x0,y0);
							double y = y0+Line2D.ptLineDist(m1[0],m1[1],m1[2],m1[3],x0,y0);
							double gScore = Math.sqrt(Math.pow(x-p.xCoord,2)+Math.pow(y-p.yCoord,2))+0.5;
							double hScore = Math.sqrt(Math.pow(x-t.xCoord,2)+Math.pow(y-t.yCoord,2))+0.5;
							dNodes.add(new double[]{x,y,gScore,hScore});
						}
						break;
						
					case 2:
						if(y0 >= y1 && y0 <= y2 && x0 < x1)
						{
							double x = m1[0]+(m1[1]-y0)*(m1[3]-m1[1])/(m1[2]-m1[0]);
							double y = y0;
							double gScore = Math.sqrt(Math.pow(x-p.xCoord,2)+Math.pow(y-p.yCoord,2))+0.5;
							double hScore = Math.sqrt(Math.pow(x-t.xCoord,2)+Math.pow(y-t.yCoord,2))+0.5;
							dNodes.add(new double[]{x,y,gScore,hScore});
						}
						break;
						
					case 3:
						e = 0;
						if(Math.abs(x0-x2) > Math.abs(y0-y2))
							e = Math.abs(y0-y2);
						else
							e = Math.abs(x0-x2);
						
						if(Line2D.linesIntersect(x0,y0,x0+e,y0+e,m1[0],m1[1],m1[2],m1[3]))
						{
							double x = x0+Line2D.ptLineDist(m1[0],m1[1],m1[2],m1[3],x0,y0);
							double y = y0+Line2D.ptLineDist(m1[0],m1[1],m1[2],m1[3],x0,y0);
							double gScore = Math.sqrt(Math.pow(x-p.xCoord,2)+Math.pow(y-p.yCoord,2))+0.5;
							double hScore = Math.sqrt(Math.pow(x-t.xCoord,2)+Math.pow(y-t.yCoord,2))+0.5;
							dNodes.add(new double[]{x,y,gScore,hScore});
						}
						break;
						
					case 4:
						if(x0 >= x1 && x0 <= x2 && y0 < y1)
						{
							double x = x0;
							double y = m1[1]+(m1[0]-x0)*(m1[3]-m1[1])/(m1[2]-m1[0]);
							double gScore = Math.sqrt(Math.pow(x-p.xCoord,2)+Math.pow(y-p.yCoord,2))+0.5;
							double hScore = Math.sqrt(Math.pow(x-t.xCoord,2)+Math.pow(y-t.yCoord,2))+0.5;
							dNodes.add(new double[]{x,y,gScore,hScore});
						}
						break;
						
					case 5:
						e = 0;
						if(Math.abs(x0-x1) > Math.abs(y0-y2))
							e = Math.abs(y0-y2);
						else
							e = Math.abs(x0-x1);
						
						if(Line2D.linesIntersect(x0,y0,x0-e,y0+e,m1[0],m1[1],m1[2],m1[3]))
						{
							double x = x0+Line2D.ptLineDist(m1[0],m1[1],m1[2],m1[3],x0,y0);
							double y = y0+Line2D.ptLineDist(m1[0],m1[1],m1[2],m1[3],x0,y0);
							double gScore = Math.sqrt(Math.pow(x-p.xCoord,2)+Math.pow(y-p.yCoord,2))+0.5;
							double hScore = Math.sqrt(Math.pow(x-t.xCoord,2)+Math.pow(y-t.yCoord,2))+0.5;
							dNodes.add(new double[]{x,y,gScore,hScore});
						}
						break;
						
					case 6:
						if(y0 >= y1 && y0 <= y2 && x0 > x2)
						{
							double x = m1[0]+(m1[1]-y0)*(m1[3]-m1[1])/(m1[2]-m1[0]);
							double y = y0;
							double gScore = Math.sqrt(Math.pow(x-p.xCoord,2)+Math.pow(y-p.yCoord,2))+0.5;
							double hScore = Math.sqrt(Math.pow(x-t.xCoord,2)+Math.pow(y-t.yCoord,2))+0.5;
							dNodes.add(new double[]{x,y,gScore,hScore});
						}
						break;
						
					case 7:
						e = 0;
						if(Math.abs(x0-x1) > Math.abs(y0-y1))
							e = Math.abs(y0-y1);
						else
							e = Math.abs(x0-x1);
						
						if(Line2D.linesIntersect(x0,y0,x0-e,y0-e,m1[0],m1[1],m1[2],m1[3]))
						{
							double x = x0+Line2D.ptLineDist(m1[0],m1[1],m1[2],m1[3],x0,y0);
							double y = y0+Line2D.ptLineDist(m1[0],m1[1],m1[2],m1[3],x0,y0);
							double gScore = Math.sqrt(Math.pow(x-p.xCoord,2)+Math.pow(y-p.yCoord,2))+0.5;
							double hScore = Math.sqrt(Math.pow(x-t.xCoord,2)+Math.pow(y-t.yCoord,2))+0.5;
							dNodes.add(new double[]{x,y,gScore,hScore});
						}
						break;
				}
			}
			
			ArrayList<double[]> dSorted = new ArrayList<double[]>();
			if(!dNodes.isEmpty())
			{
				for(double[] d: dNodes)
				{
					if(dSorted.isEmpty())
						dSorted.add(d);
					else
					{
						int b = 0;
						for(int c = 0; c < dSorted.size(); c++)
						{
							if(d[2]+d[3] > dSorted.get(c)[2]+dSorted.get(c)[3])
								b++;
						}
						dSorted.add(b,d);
					}
				}
				
				for(double[] d: dSorted)
				{*/
			x1 = s[0];
			y1 = s[1];
			x2 = (m1[0]+m1[2])/2;	//d[0];
			y2 = (m1[1]+m1[3])/2;	//d[1];
			int u1 = 0;
			int u2 = 0;
			int v1 = 0;
			int v2 = 0;
			
			if(x1 > x2)
			{
				u1 = -1;
				u2 = 1;
			}
			else if(x1 < x2)
			{
				u1 = 1;
				u2 = -1;
			}
			
			if(y1 > y2)
			{
				v1 = -1;
				v2 = 1;
			}
			else if(y1 < y2)
			{
				v1 = 1;
				v2 = -1;
			}
			Line2D.Double l = new Line2D.Double(x1+u1,y1+v1,x2+u2,y2+v2);
			
			if(m1[4] != 3 || (m1[5] != stage.puppets.indexOf(p)/* && m1[7] != stage.puppets.indexOf(p)*/))
			{
				boolean withinMain = true;
				boolean isIntersecting = false;
				boolean tooNarrow = false;
				boolean isClosed = false;
				
				if(Math.abs(m1[0]-m1[2]) >= p.width || Math.abs(m1[1]-m1[3]) >= p.height)
				{
					for(Polygon o: stage.mapPolys)
					{
						if(stage.mapPolys.indexOf(o) == 0 && (!o.contains(x1+u1,y1+v1) || !o.contains(x2+u2,y2+v2)))
							withinMain = false;
						if(stage.mapPolys.indexOf(o) > 0 && (o.contains(x1+u1,y1+v1) || o.contains(x2+u2,y2+v2)))
							withinMain = false;
						
						for(int o1 = 0; o1 < o.npoints; o1++)
						{
							int o2 = o1+1;
							if(o2 >= o.npoints)
								o2 -= o.npoints;
							
							if(l.intersectsLine(o.xpoints[o1],o.ypoints[o1],o.xpoints[o2],o.ypoints[o2]))
								withinMain = false;
						}
					}
					
					if(withinMain)
					{
						if(n.indexOf(m1) < stage.openNav.length)
							isIntersecting = !stage.openNav[n.indexOf(m1)];
						
						if(!isIntersecting)
						{
							for(Puppet q: stage.puppets)
							{
								if(l.intersects(q.xCoord,q.yCoord,q.width,q.height) && q != p)
									isIntersecting = true;
							}
							
							if(!isIntersecting)
							{
								for(Prop q: stage.props)
								{
									if(l.intersects(q.bounds.xCoord,q.bounds.yCoord,q.bounds.width,q.bounds.height))
										isIntersecting = true;
								}
							}
					/*		if(!isIntersecting)
							{
								for(double[] m2: n)
								{
									if(m2[4] != 3 || (m2[5] != stage.puppets.indexOf(p)/* && m2[7] != stage.puppets.indexOf(p)*/ //))
					/*				{
										if(l.intersectsLine(m2[0],m2[1],m2[2],m2[3]))
											isIntersecting = true;
									}
								}
							}*/
							
							if(!isIntersecting)
							{
								for(double[] m2: n)
								{
									if(l.intersectsLine(m2[0],m2[1],m2[2],m2[3]) && (Math.abs(m2[2]-m2[0]) < p.width || Math.abs(m2[3]-m2[1]) < p.height))
										tooNarrow = true;
								}
								
								if(!tooNarrow)
								{
									for(int[] c: closedNodes)
									{
										if(c[0] == (int)((m1[0]+m1[2])/2+0.5)/*(int)(d[0]+0.5)*/ && c[1] == (int)((m1[1]+m1[3])/2+0.5)/*(int)(d[1]+0.5)*/)
											isClosed = true;
									}
									
									if(!isClosed)
									{
										int x = (int)((m1[0]+m1[2])/2+0.5);	//(int)(d[0]+0.5);
										int y = (int)((m1[1]+m1[3])/2+0.5);	//(int)(d[1]+0.5);
										int gScore = (int)(Math.sqrt(Math.pow(x-p.xCoord,2)+Math.pow(y-p.yCoord,2))+0.5);
										int hScore = (int)(Math.sqrt(Math.pow(x-t.xCoord,2)+Math.pow(y-t.yCoord,2))+0.5);
										aNodes.add(new int[]{x,y,gScore,hScore,i,n.indexOf(m1)});
									}
								}
							}
						}
					}
				}
			}
		}
	//		}
	//	}
		return aNodes;
	}
}