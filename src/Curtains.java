import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.geom.Line2D;
import java.util.ArrayList;

public class Curtains
{
	Stage stage;
//	ArrayList borders;
//	ArrayList
	ArrayList<Prop> props;
	ArrayList<Polygon> drapes, obsPolys;
	int width, height, layers;
	
	public Curtains()
	{
	//	borders = new ArrayList();
		drapes = new ArrayList<Polygon>();//new ArrayList>();
		width = 0;
		height = 0;
		layers = 1;
	}
	
	public Curtains(Stage s, int w, int h, int l)
	{
		stage = s;
		obsPolys = stage.mapPolys;
	//	borders = new ArrayList();
		drapes = new ArrayList<Polygon>();//new ArrayList>();
		props = stage.props;
		width = w;
		height = h;
		layers = l;
	}
	
	public void draw(Graphics g, double w, double h)	//MAY ALSO DRAW BACKGROUNDS AND WALLS (BACKDROP)obss
	{
		int a = 0;
		if(layers > 1)
			a = 10;
		
		//TEST
		layers = 1;
		
		g.setColor(new Color(0,0,0,120/layers+a));
		g.fillRect(100,100,20,20);
	}
	
	public void drop(Puppet p)
	{
		/*	ArrayList<int[]> d = new ArrayList<int[]>();	//{#,x,y1,y2}
			d.add(new int[]{0,puppet.yCoord,puppet.xCoord,puppet.xCoord});
			d.add(new int[]{1,puppet.xCoord,puppet.yCoord,puppet.yCoord});
			d.add(new int[]{2,puppet.yCoord,puppet.xCoord,puppet.xCoord});
			d.add(new int[]{3,puppet.xCoord,puppet.yCoord,puppet.yCoord});*/
		
		ArrayList<int[]> d = new ArrayList<int[]>();
		ArrayList<int[]> q = new ArrayList<int[]>();
		ArrayList<ArrayList<Integer>> l = new ArrayList<ArrayList<Integer>>();
		ArrayList<int[]> obsPoints = new ArrayList<int[]>();
		
		for(Polygon n: obsPolys)
		{
			l.add(new ArrayList<Integer>());
			for(int o = 0; o < n.npoints; o++)
				obsPoints.add(new int[]{n.xpoints[o],n.ypoints[o],obsPolys.indexOf(n)});
		}
		
		for(int n = 0; n < obsPoints.size(); n++)
		{
			int x = obsPoints.get(n)[0];
			int y = obsPoints.get(n)[1];
			if(x <= width*21/20-stage.xFocus && x >= -(stage.xFocus+width/20) && y <= height*21/20-stage.yFocus && y >= -(stage.yFocus+height/20))
			{
				q.add(new int[]{x,y,n});
				l.get(obsPoints.get(n)[2]).add(n);
			}
		}
		
	/*	for(int n = 0; n < l.size(); n++)
		{
			int m = l.get(n)-1;
			if(m < 0)
				m += obsPoly.npoints;
			
			for(int o = 0; o < l.size(); o++)
			{
				if(m > 0)
				{
					if(m == l.get(o))
						m = -1;
				}
			}
			
			if(m != -1)
				l.add(m,n);
			n++;
		}*/

		int qLimit = q.size();
		for(int n = 0; n < qLimit; n++)
		{
			boolean isBlocked = false;
			int x1 = p.xCoord+p.width/2;
			int y1 = p.yCoord-p.height/8;
			int x2 = q.get(n)[0];
			int y2 = q.get(n)[1];
			
			for(int k = 0; k < l.size(); k++)
			{
				for(int o = 0; o < l.get(k).size(); o++)
				{
					int u = l.get(k).get(o)-1;
					if(u < l.get(k).get(0))
						u += l.get(k).size();
					
					int x3 = obsPoints.get(u)[0];
					int y3 = obsPoints.get(u)[1];
					int x4 = obsPoints.get(l.get(k).get(o))[0];
					int y4 = obsPoints.get(l.get(k).get(o))[1];
					
					if((x2 != x3 || y2 != y3) && (x2 != x4 || y2 != y4))
					{
						if(Line2D.linesIntersect(x1,y1,x2,y2,x3,y3,x4,y4))
							isBlocked = true;
					}
				}
			}
			
			if(isBlocked)
			{
				q.remove(n);
				qLimit--;
				n--;
			}
		}
		
		//TEST
	/*	for(int s = 0; s < q.size(); s++)
			System.out.println(q.get(s)[0]+" "+q.get(s)[1]);
	*/	//==

		qLimit = q.size();		
		for(int n = 0; n < q.size(); n++)
		{
			boolean inBounds = true;
			int x = q.get(n)[0];
			int y = q.get(n)[1];
			
			if(y != p.yCoord-p.height/8 && Math.abs(q.get(n)[1]-p.yCoord-p.height/8) != 0)
			{
				double m = (double)(p.yCoord-p.height/8-y)/(p.xCoord+p.width/2-x);
				int b = (int)(y-x*m+0.5);
				y += (q.get(n)[1]-p.yCoord-p.height/8)/Math.abs(q.get(n)[1]-p.yCoord-p.height/8);
				x = (int)((double)(y-b)/m+0.5);
			}
			else if(Math.abs(q.get(n)[0]-p.xCoord+p.width/2) != 0)
				x += (q.get(n)[0]-p.xCoord+p.width/2)/Math.abs(q.get(n)[0]-p.xCoord+p.width/2);
			else
				inBounds = false;
			
			if(inBounds)
			{
				if(obsPolys.get(0).contains(x,y))
				{
					if(obsPolys.size() > 1)
					{
						for(int m = 1; m < obsPolys.size(); m++)
						{
							if(obsPolys.get(m).contains(x,y))
								inBounds = false;
						}
					}
				}
				else
					inBounds = false;
			}
			
			if(!inBounds)
			{
				q.remove(n);
				qLimit--;
				n--;
			}
		}
		
		//TEST
	/*	for(int s = 0; s < q.size(); s++)
			System.out.println(q.get(s)[0]+" "+q.get(s)[1]);
	*/	//==
		
		ArrayList<int[]> c = new ArrayList<int[]>();
		for(int n = 0; n < q.size(); n++)
		{
			int x1 = q.get(n)[0];
			int y1 = q.get(n)[1];
			
			if(p.xCoord+p.width/2 != x1 && p.yCoord+p.height/8 != y1)
			{
				double m = 0;
				int x2 = 0;
				int y2 = 0;
				
				if(p.xCoord+p.width/2 == x1)
				{
					x2 = x1;
					y2 = (y1-p.yCoord-p.height/8)/Math.abs(y1-p.yCoord-p.height/8)*width;
				}
				else
				{
					m = (double)(p.yCoord-p.height/8-y1)/(p.xCoord+p.width/2-x1);
					x2 = x1+(int)(m*width+0.5)*(y1-p.yCoord-p.height/8)/Math.abs(y1-p.yCoord-p.height/8);
					y2 = y1+(int)(m*height+0.5)*(x1-p.xCoord-p.width/2)/Math.abs(x1-p.xCoord-p.width/2);
				}
				
				c.add(new int[]{x1,y1,n});
				double a = -1;
				int e = c.size()-1;
				
				for(int k = 0; k < l.size(); k++)
				{
					for(int o = 0; o < l.get(k).size(); o++)
					{
						int u = l.get(k).get(o)-1;
						if(u < l.get(k).get(0))
							u += l.get(k).size();
			/*	for(int o = 0; o < obsPoints.size(); o++)
				{
					int u = o-1;
					if(u < 0)
						u += obsPoints.size();
			*/	
						int x3 = obsPoints.get(u)[0];
						int y3 = obsPoints.get(u)[1];
						int x4 = obsPoints.get(l.get(k).get(o))[0];
						int y4 = obsPoints.get(l.get(k).get(o))[1];
						
					/*	int x4 = obsPoints.get(o)[0];
						int y4 = obsPoints.get(o)[1];*/
						
						if((x1 != x3 || y1 != y3) && (x1 != x4 || y1 != y4))
						{
							if(Line2D.linesIntersect(x1,y1,x2,y2,x3,y3,x4,y4))
							{
							/*	double m2 = (y4-y3)/(x4-x3);
								int b1 = (int)(y1-x1*m1+0.5);
								int b2 = (int)(y3-x3*m2+0.5);
								
								int x = (int)((b2-b1)/(m1-m2)+0.5);
								int y = (int)(m1*x1+b1+0.5);*/
								
								//TEST
						//		System.out.println(x1+" "+y1+"   "+x2+" "+y2+"   "+x3+" "+y3+"   "+x4+" "+y4);
								
								int b = (int)(y1-x1*m+0.5);
								int x = 0;
								int y = 0;
								
								if(x3 == x4)
								{
									x = x3;
									y = (int)(m*x3+b);
								}
								else if(y3 == y4)
								{
									x = (int)((y3-b)/m+0.5);
									y = y3;
								}
								
						//		c.add(new int[]{x,y,o});
								if(a == -1)
								{
									c.add(new int[]{x,y,n/*l.get(k).get(o)*/});
									a = Math.sqrt(Math.pow(x-p.xCoord,2)+Math.pow(y-p.yCoord,2));
								}
								else if(a > Math.sqrt(Math.pow(x-p.xCoord,2)+Math.pow(y-p.yCoord,2)))
								{
									c.add(e,new int[]{x,y,n/*l.get(k).get(o)*/});
									a = Math.sqrt(Math.pow(x-p.xCoord,2)+Math.pow(y-p.yCoord,2));
								}
							}
						}
					}
				}
				if(e+2 == c.size())
					c.remove(c.size()-1);
				
				//TEST
			/*	for(int[]t: c)
					System.out.println(t[0]+" "+t[1]);
				System.out.println();*/
				
				if(c.size() > 1)
				{
					int[] b = c.get(0);
					for(int o = 1; o < obsPoints.size(); o++)
					{
						
					}
					
				/*	for(int o = 1; o < c.size(); o++)
					{
						if(Math.sqrt(Math.pow(c.get(o)[0]-x1,2)+Math.pow(c.get(o)[1]-y1,2)) < Math.sqrt(Math.pow(b[0]-x1,2)+Math.pow(b[1]-y1,2)))
							b = c.get(o);
					}*/
					
				/*	if(n > b[2])
					{*/
						d.add(b);
						d.add(new int[]{x1,y1,n});
				/*	}
					else
					{
						d.add(new int[]{x1,y1,n});
						d.add(b);
					}*/
				}
			}
		}
		
		//TEST
	/*	for(int n = 0; n < obsPoly.npoints; n++)
			System.out.println(n+"   "+obsPoly.xpoints[n]+" "+obsPoly.ypoints[n]);
		System.out.println();*/
		
	// <<================
	/*	System.out.println("["+d.size()+"]");
		for(int[] e: d)
			System.out.println(e[0]+" "+e[1]+"   "+e[2]);
		System.out.println("))<>((");*/
		
		//CONTINUE HERE
		/*
		if(!d.isEmpty())
		{
			drapes.add(new Polygon());
			drapes.get(0).addPoint(d.get(0)[0],d.get(0)[1]);
			int e0 = d.get(0)[2];
			d.remove(0);
			int eLimit = d.size();
			
			for(int e1 = 0; e1 < eLimit; e1++)
			{
				int f = 0;
				
				for(int e2 
				{
				}
			}
		}*/
//##
	}
	
	public void update()
	{
		drop(stage.puppets.get(0));
	}

	private int gcd(int a, int b)
	{
		if(b == 0)
			return a;
		else
			return gcd(b,a%b);
	}
}
