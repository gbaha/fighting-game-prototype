import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class Stage
{
	ArrayList<Floor> floors;
	ArrayList<Puppet> puppets;
	ArrayList<Prop> props;
	ArrayList<Pleb> plebs;
	ArrayList<BlueFairy> fairies;	//<==
	ArrayList<Polygon> mapPolys;
	ArrayList<ArrayList<Prop>> obsPolys;
	ArrayList<ArrayList<int[]>> mapArchiver;
	ArrayList<ArrayList<Integer>> navArchiver, propArchiver;
	ArrayList<int[]> points;
	ArrayList<double[]> navMesh;
	boolean[] openNav;
	Player player1, player2;
	int xFocus, yFocus;
	
	public Stage()
	{
		xFocus = 0;
		yFocus = 0;
		floors = new ArrayList<Floor>();
		
		puppets = new ArrayList<Puppet>();
		props = new ArrayList<Prop>();
		plebs = new ArrayList<Pleb>();
		fairies = new ArrayList<BlueFairy>();
		mapPolys = new ArrayList<Polygon>();
		obsPolys = new ArrayList<ArrayList<Prop>>();
		mapArchiver = new ArrayList<ArrayList<int[]>>();
		navArchiver = new ArrayList<ArrayList<Integer>>();	//Records props intersecting with map navmesh
		propArchiver = new ArrayList<ArrayList<Integer>>();	//Sorts props by their map placement
		points = new ArrayList<int[]>();	//[type, xCoord, yCoord, id1, id2 (prop = -1)]
		navMesh = new ArrayList<double[]>();
		openNav = new boolean[0];
		
		//TEST
		floors.add(new Floor("",0,0,2000,1000));
		player1 = new Roo(1000-200-100,750,true);
		player2 = new Bunny(1000+200,750,false);
		puppets.add(player1);
		puppets.add(player2);
		
		//END OF LINE
		//-----------
		//LINE ENDS HERE
		
		simplifyFloors();	//MUST PLACE BEFORE PROP AND FLOOR UPDATE
		for(Puppet p: puppets)
			p.id = puppets.indexOf(p);
		for(Prop p: props)
			p.id = props.indexOf(p);
		for(Floor f: floors)
			f.update(floors);
//		buildFairyTrail();
	}
	
	
	public void drawMesh(Graphics g, double w, double h, boolean d)
	{
		ArrayList<double[]> mapMesh = getMapMesh();
	//	g.setColor(new Color((int)(Math.random()*255),(int)(Math.random()*255),(int)(Math.random()*255)));
		g.setColor(Color.GREEN);
		int x1 = 0;
		int y1 = 0;
		int x2 = 0;
		int y2 = 0;
		
		for(int n = 0; n < mapMesh.size(); n++)
		{
			try
			{
				x1 = (int)mapMesh.get(n)[0]+xFocus;
				y1 = (int)mapMesh.get(n)[1]+yFocus;
				x2 = (int)mapMesh.get(n)[2]+xFocus;
				y2 = (int)mapMesh.get(n)[3]+yFocus;
			}
			catch(java.lang.IndexOutOfBoundsException e)
			{
				drawMesh(g,w,h,d);
			}
			g.drawLine((int)(x1*w/1280),(int)(y1*h/720),(int)(x2*w/1280),(int)(y2*h/720));
		}
		
		if(d)
		{
			for(int n = 0; n < navMesh.size(); n++)
			{
				try
				{
					if(navMesh.get(n)[4] == 0)
						g.setColor(Color.YELLOW);
					else //if(navMesh.get(n)[4] == 1)
						g.setColor(Color.PINK);
				/*	else
						g.setColor(Color.ORANGE);*/
				}
				catch(java.lang.NullPointerException e)
				{
					drawMesh(g,w,h,d);
				}
				catch(java.lang.IndexOutOfBoundsException e)
				{
					drawMesh(g,w,h,d);
				}
						
				try
				{
					x1 = (int)navMesh.get(n)[0]+xFocus;
					y1 = (int)navMesh.get(n)[1]+yFocus;
					x2 = (int)navMesh.get(n)[2]+xFocus;
					y2 = (int)navMesh.get(n)[3]+yFocus;
				}
				catch(java.lang.NullPointerException e)
				{
					drawMesh(g,w,h,d);
				}
				catch(java.lang.IndexOutOfBoundsException e)
				{
					drawMesh(g,w,h,d);
				}
				if(navMesh.get(n)[4] > 0)
					g.drawLine((int)(x1*w/1280),(int)(y1*h/720),(int)(x2*w/1280),(int)(y2*h/720));
				else if(openNav[n])
					g.drawLine((int)(x1*w/1280),(int)(y1*h/720),(int)(x2*w/1280),(int)(y2*h/720));
			}
			
			g.setColor(Color.PINK);
			try
			{
				for(Prop p: props)
				{
					for(int q = 0; q < p.pointArchiver.size(); q++)
					{
						if(p.pointArchiver.get(q).get(1)[0] != -1)
						g.fillOval((int)((p.pointArchiver.get(q).get(0)[0]+xFocus-4)*w/1280),(int)((p.pointArchiver.get(q).get(0)[1]+yFocus-4)*h/720),(int)(8*w/1280),(int)(8*h/720));
					}
				}
			}
			catch(java.util.ConcurrentModificationException e)
			{
				drawMesh(g,w,h,d);
			}
		}
	}
	
	public void update()
	{
		propArchiver = new ArrayList<ArrayList<Integer>>();
		for(int f = 0; f < floors.size(); f++)
		{
			propArchiver.add(new ArrayList<Integer>());
			for(Prop p: props)
			{
				for(ArrayList<int[]> q: p.pointArchiver)
				{
					if(q.get(1)[0] == f)
						propArchiver.get(f).add(props.indexOf(p));
				}
			}
		}
	}
	
	public void updateTrail()
	{
		for(int n = 0; n < openNav.length; n++)
		{
			double x1 = navMesh.get(n)[0];
			double y1 = navMesh.get(n)[1];
			double x2 = navMesh.get(n)[2];
			double y2 = navMesh.get(n)[3];
			
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
			
			for(Prop p: props)
			{
				if(l.intersects(p.bounds.xCoord,p.bounds.yCoord,p.bounds.width,p.bounds.height))
					openNav[n] = false;
			}
		}
	}
	
	public void updateTrail(int t)
	{
		for(ArrayList<int[]> m: props.get(t).pointArchiver)
		{
			int nLimit = navMesh.size();
			for(int n = 0; n < nLimit; n++)
			{
				if((m.get(0)[0] == navMesh.get(n)[0] && m.get(0)[1] == navMesh.get(n)[1]) || (m.get(0)[0] == navMesh.get(n)[2] && m.get(0)[1] == navMesh.get(n)[3]))
				{
					navMesh.remove(n);
					nLimit = navMesh.size();
					n--;
				}
			}
		}
		
		for(int n = 0; n < 4; n++)
		{
			switch(n)
			{
				case 0:
					props.get(t).pointArchiver.get(0).set(0, new int[]{props.get(t).bounds.xCoord,props.get(t).bounds.yCoord});
					break;
				case 1:
					props.get(t).pointArchiver.get(1).set(0, new int[]{props.get(t).bounds.xCoord+props.get(t).bounds.width,props.get(t).bounds.yCoord});
					break;
				case 2:
					props.get(t).pointArchiver.get(2).set(0, new int[]{props.get(t).bounds.xCoord+props.get(t).bounds.width,props.get(t).bounds.yCoord+props.get(t).bounds.height});
					break;
				case 3:
					props.get(t).pointArchiver.get(3).set(0, new int[]{props.get(t).bounds.xCoord,props.get(t).bounds.yCoord+props.get(t).bounds.height});
					break;
			}
		}
		
		for(int b = 0; b < openNav.length; b++)
		{
			double x1 = navMesh.get(b)[0];
			double y1 = navMesh.get(b)[1];
			double x2 = navMesh.get(b)[2];
			double y2 = navMesh.get(b)[3];
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
			if(openNav[b] && l.intersects(props.get(t).bounds.xCoord,props.get(t).bounds.yCoord,props.get(t).bounds.width,props.get(t).bounds.height))
			{
				navArchiver.get(b).add(t);
				openNav[b] = false;
			}
			else if(!openNav[b])
			{
				for(int n = 0; n < navArchiver.get(b).size(); n++)
				{
					if(navArchiver.get(b).get(n) == t)
					{
						navArchiver.get(b).remove(n);
						n = navArchiver.get(b).size();
					}
				}
				
				if(navArchiver.get(b).size() == 0)
					openNav[b] = true;
			}
		}
		
		ArrayList<Prop> uProps = new ArrayList<Prop>();
		ArrayList<Integer> uArchiver = new ArrayList<Integer>();
		uProps.add(props.get(t));
		uArchiver.add(props.get(t).id);
		for(Prop p: props)
		{
			boolean pointsMatch = false;
			for(ArrayList<int[]> q1: p.pointArchiver)
			{
				if(p != props.get(t) && q1.get(1)[0] != -1)
				{
					for(ArrayList<int[]> q2: props.get(t).pointArchiver)
					{
						for(int r1: q1.get(1))
						{
							for(int r2: q2.get(1))
							{
								if(!pointsMatch && r1 == r2)
									pointsMatch = true;
							}
						}
					}
				}
			}
			if(pointsMatch)
			{
				uProps.add(p);
				uArchiver.add(p.id);
			}
		}
		
		ArrayList<Prop> oPolys = new ArrayList<Prop>();
		for(Prop u: uProps)
			oPolys.add(u);
		int uCount = 0;
		while(oPolys.size() > 0)
		{
			obsPolys.add(new ArrayList<Prop>());
			buildObsPolys(oPolys,0);
			uCount++;
		}
		
		int oCount = 0;
		int oLimit = obsPolys.size();
		for(int o = 0; o < oLimit; o++)
		{
			if(obsPolys.get(o).size() == 0)
			{
				obsPolys.remove(o);
				oCount++;
				oLimit = obsPolys.size();
				o--;
			}
		}
		for(Prop p: props)
		{
			if(p.touchArchiver.get(0)[0] > 0)
			{
				p.touchArchiver.get(0)[0] -= oCount;
				if(p.touchArchiver.get(0)[0] < 0)
					p.touchArchiver.get(0)[0] = 0;
			}
		}
		
	//	int uLimit = uProps.size();
	//	for(int u = 0; u < uLimit; u++)
		
		ArrayList<Prop> vProps = new ArrayList<Prop>();
		vProps.add(uProps.get(0));
		for(Prop u: uProps)
		{
			if(uProps.indexOf(u) > 0)	//?
			{
				boolean pointsMatch = false;
				for(ArrayList<int[]> p: props.get(t).pointArchiver)
				{
					if(p.size() > 2)
					{
						if(p.get(2)[0] == 1 && p.get(2)[1] == u.id)	//?
							pointsMatch = true;
					}
				}
				if(!pointsMatch)
					vProps.add(u);	//?
			/*	{
					uProps.remove(u);
					uLimit = uProps.size();
					u--;
				}*/
			}
		}
			
		for(Prop v: vProps)	//?
		{
			for(ArrayList<int[]> q1: v.pointArchiver)
			{
				for(Floor f: floors)
				{
					if(q1.get(0)[0] > f.xCoord && q1.get(0)[0] < f.xCoord+f.width && q1.get(0)[1] > f.yCoord && q1.get(0)[1] < f.yCoord+f.height)
					{
						if(q1.get(1)[0] == -1)
							q1.get(1)[0] = floors.indexOf(f);
						else
						{
							int[] a = q1.get(1);
							q1.set(1,new int[a.length+1]);
							q1.get(1)[0] = floors.indexOf(f);
							for(int b = 1; b < q1.get(1).length; b++)
								q1.get(1)[b] = a[b-1];
						}
					}
					else
					{
						if(q1.get(0)[1] == f.yCoord)
						{
							if(q1.get(0)[0] == f.xCoord || q1.get(0)[0] == f.xCoord+f.width)
								q1.set(1,new int[]{-1});
							else if(q1.get(0)[0] > f.xCoord && q1.get(0)[0] < f.xCoord+f.width)
							{
								if(q1.get(1)[0] == -1)
									q1.get(1)[0] = floors.indexOf(f);
								else
								{
									int[] a = q1.get(1);
									q1.set(1,new int[a.length+1]);
									q1.get(1)[0] = floors.indexOf(f);
									for(int b = 1; b < q1.get(1).length; b++)
										q1.get(1)[b] = a[b-1];
								}
								
								for(int[] w: f.walls[0])
								{
									if(q1.get(0)[0] >= w[0] && q1.get(0)[0] <= w[1])
										q1.set(1,new int[]{-1});
								}
							}
						}
						else if(q1.get(0)[0] == f.xCoord+f.width)
						{
							if(q1.get(0)[1] == f.yCoord || q1.get(0)[1] == f.yCoord+f.height)
								q1.set(1,new int[]{-1});
							else if(q1.get(0)[1] > f.yCoord && q1.get(0)[1] < f.yCoord+f.height)
							{
								if(q1.get(1)[0] == -1)
									q1.get(1)[0] = floors.indexOf(f);
								else
								{
									int[] a = q1.get(1);
									q1.set(1,new int[a.length+1]);
									q1.get(1)[0] = floors.indexOf(f);
									for(int b = 1; b < q1.get(1).length; b++)
										q1.get(1)[b] = a[b-1];
								}
								
								for(int[] w: f.walls[1])
								{
									if(q1.get(0)[1] >= w[0] && q1.get(0)[1] <= w[1])
										q1.set(1,new int[]{-1});
								}
							}
						}
						else if(q1.get(0)[1] == f.yCoord+f.height)
						{
							if(q1.get(0)[0] == f.xCoord || q1.get(0)[0] == f.xCoord+f.width)
								q1.set(1,new int[]{-1});
							else if(q1.get(0)[0] > f.xCoord && q1.get(0)[0] < f.xCoord+f.width)
							{
								if(q1.get(1)[0] == -1)
									q1.get(1)[0] = floors.indexOf(f);
								else
								{
									int[] a = q1.get(1);
									q1.set(1,new int[a.length+1]);
									q1.get(1)[0] = floors.indexOf(f);
									for(int b = 1; b < q1.get(1).length; b++)
										q1.get(1)[b] = a[b-1];
								}
								
								for(int[] w: f.walls[2])
								{
									if(q1.get(0)[0] >= w[0] && q1.get(0)[0] <= w[1])
										q1.set(1,new int[]{-1});
								}
							}
						}
						else if(q1.get(0)[0] == f.xCoord)
						{
							if(q1.get(0)[1] == f.yCoord || q1.get(0)[1] == f.yCoord+f.height)
								q1.set(1,new int[]{-1});
							else if(q1.get(0)[1] > f.yCoord && q1.get(0)[1] < f.yCoord+f.height)
							{
								if(q1.get(1)[0] == -1)
									q1.get(1)[0] = floors.indexOf(f);
								else
								{
									int[] a = q1.get(1);
									q1.set(1,new int[a.length+1]);
									q1.get(1)[0] = floors.indexOf(f);
									for(int b = 1; b < q1.get(1).length; b++)
										q1.get(1)[b] = a[b-1];
								}
								
								for(int[] w: f.walls[3])
								{
									if(q1.get(0)[1] >= w[0] && q1.get(0)[1] <= w[1])
										q1.set(1,new int[]{-1});
								}
							}
						}
					}
				}
				if(q1.get(1)[0] != -1)
				{
					int blockCount = 0;
					for(int b = 1; b < v.touchArchiver.size(); b++)
					{System.out.println(v.id+"   "+uProps.get(uProps.size()-1).id+" "+uArchiver.get(uArchiver.size()-1)+"   "+uProps.size()+" "+uArchiver.size()+"   "+v.touchArchiver.get(b)[1]+" "+uArchiver.indexOf(v.touchArchiver.get(b)[1]));
						
						if(uArchiver.indexOf(v.touchArchiver.get(b)[1]) != -1){
						
						if(q1.get(0)[0] >= uProps.get(uArchiver.indexOf(v.touchArchiver.get(b)[1])).bounds.xCoord && q1.get(0)[0] <= uProps.get(uArchiver.indexOf(v.touchArchiver.get(b)[1])).bounds.xCoord+uProps.get(uArchiver.indexOf(v.touchArchiver.get(b)[1])).bounds.width && q1.get(0)[1] >= uProps.get(uArchiver.indexOf(v.touchArchiver.get(b)[1])).bounds.yCoord && q1.get(0)[1] <= uProps.get(uArchiver.indexOf(v.touchArchiver.get(b)[1])).bounds.yCoord+uProps.get(uArchiver.indexOf(v.touchArchiver.get(b)[1])).bounds.height)
							blockCount++;
						if((q1.get(0)[0] == uProps.get(uArchiver.indexOf(v.touchArchiver.get(b)[1])).bounds.xCoord || q1.get(0)[0] == uProps.get(uArchiver.indexOf(v.touchArchiver.get(b)[1])).bounds.xCoord+uProps.get(uArchiver.indexOf(v.touchArchiver.get(b)[1])).bounds.width) && (q1.get(0)[1] == uProps.get(uArchiver.indexOf(v.touchArchiver.get(b)[1])).bounds.yCoord || q1.get(0)[1] == uProps.get(uArchiver.indexOf(v.touchArchiver.get(b)[1])).bounds.yCoord+uProps.get(uArchiver.indexOf(v.touchArchiver.get(b)[1])).bounds.height))
						{
							if(v.bounds.xCoord == uProps.get(uArchiver.indexOf(v.touchArchiver.get(b)[1])).bounds.xCoord || v.bounds.xCoord+v.bounds.width == uProps.get(uArchiver.indexOf(v.touchArchiver.get(b)[1])).bounds.xCoord+uProps.get(uArchiver.indexOf(v.touchArchiver.get(b)[1])).bounds.width || v.bounds.yCoord == uProps.get(uArchiver.indexOf(v.touchArchiver.get(b)[1])).bounds.yCoord || v.bounds.yCoord+v.bounds.height == uProps.get(uArchiver.indexOf(v.touchArchiver.get(b)[1])).bounds.yCoord+uProps.get(uArchiver.indexOf(v.touchArchiver.get(b)[1])).bounds.height)
								blockCount++;
						}
						
						}
					
					}
					if(blockCount > 1)
					{
						q1.set(1,new int[]{-1});
					}
				}
				
				//bruh
				
				if(q1.get(1)[0] != -1)
				{
					double d = -1;
					int[] e = new int[2];
					
					for(Polygon m: mapPolys)
					{
						boolean isIntersecting = false;
						
						double x1 = q1.get(0)[0];
						double y1 = q1.get(0)[1];
						double x2 = m.xpoints[m.npoints];
						double y2 = m.ypoints[m.npoints];
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
						for(Prop u: uProps)
						{
							if(l.intersects(u.bounds.xCoord,u.bounds.yCoord,u.bounds.width,u.bounds.height))
								isIntersecting = true;
						}
						
						if(!isIntersecting && (d == -1 || d > Math.sqrt(Math.pow(x1-x2,2)+Math.pow(y1-y2,2))))
						{
							d = Math.sqrt(Math.pow(x1-x2,2)+Math.pow(y1-y2,2));
							e = new int[]{0,mapPolys.indexOf(m),m.npoints};
						}
					}
					
					for(int o = obsPolys.size()-uCount; o < obsPolys.size(); o++)
					{
						if(v.touchArchiver.get(0)[0] != o)
						{
							for(Prop p: obsPolys.get(o))
							{
								for(ArrayList<int[]> q2: p.pointArchiver)
								{
									if(q2.get(1)[0] != -1)
									{
										int blockCount = 0;
										
										
										
										if(q2.get(0)[0] >= props.get(t).bounds.xCoord && q2.get(0)[0] <= props.get(t).bounds.xCoord+props.get(t).bounds.width && q2.get(0)[1] >= props.get(t).bounds.yCoord && q2.get(0)[1] <= props.get(t).bounds.yCoord+props.get(t).bounds.height)
											blockCount++;
										if((q2.get(0)[0] == props.get(t).bounds.xCoord || q2.get(0)[0] == props.get(t).bounds.xCoord+props.get(t).bounds.width) && (q2.get(0)[1] == props.get(t).bounds.yCoord || q2.get(0)[1] == props.get(t).bounds.yCoord+props.get(t).bounds.height))
										{
											if(p.bounds.xCoord == props.get(t).bounds.xCoord || p.bounds.xCoord+p.bounds.width == props.get(t).bounds.xCoord+props.get(t).bounds.width || p.bounds.yCoord == props.get(t).bounds.yCoord || p.bounds.yCoord+p.bounds.height == props.get(t).bounds.yCoord+props.get(t).bounds.height)
												blockCount++;
										}
										//if(p.id == 1 && p.pointArchiver.indexOf(q2) == 0)System.out.println(props.get(t).bounds.xCoord+" "+(props.get(t).bounds.yCoord+props.get(t).bounds.height)+"   "+p.touchArchiver.size()+" "+blockCount+" <<");
										
										
										
										for(int b = 1; b < p.touchArchiver.size(); b++)
										{
											
											if(uArchiver.indexOf(p.touchArchiver.get(b)[1]) != -1){
											
											if(q2.get(0)[0] >= uProps.get(uArchiver.indexOf(p.touchArchiver.get(b)[1])).bounds.xCoord && q2.get(0)[0] <= uProps.get(uArchiver.indexOf(p.touchArchiver.get(b)[1])).bounds.xCoord+uProps.get(uArchiver.indexOf(p.touchArchiver.get(b)[1])).bounds.width && q2.get(0)[1] >= uProps.get(uArchiver.indexOf(p.touchArchiver.get(b)[1])).bounds.yCoord && q2.get(0)[1] <= uProps.get(uArchiver.indexOf(p.touchArchiver.get(b)[1])).bounds.yCoord+uProps.get(uArchiver.indexOf(p.touchArchiver.get(b)[1])).bounds.height)
												blockCount++;
											if((q2.get(0)[0] == uProps.get(uArchiver.indexOf(p.touchArchiver.get(b)[1])).bounds.xCoord || q2.get(0)[0] == uProps.get(uArchiver.indexOf(p.touchArchiver.get(b)[1])).bounds.xCoord+uProps.get(uArchiver.indexOf(p.touchArchiver.get(b)[1])).bounds.width) && (q2.get(0)[1] == uProps.get(uArchiver.indexOf(p.touchArchiver.get(b)[1])).bounds.yCoord || q2.get(0)[1] == uProps.get(uArchiver.indexOf(p.touchArchiver.get(b)[1])).bounds.yCoord+uProps.get(uArchiver.indexOf(p.touchArchiver.get(b)[1])).bounds.height))
											{
												if(p.bounds.xCoord == uProps.get(uArchiver.indexOf(p.touchArchiver.get(b)[1])).bounds.xCoord || p.bounds.xCoord+p.bounds.width == uProps.get(uArchiver.indexOf(p.touchArchiver.get(b)[1])).bounds.xCoord+uProps.get(uArchiver.indexOf(p.touchArchiver.get(b)[1])).bounds.width || p.bounds.yCoord == uProps.get(uArchiver.indexOf(p.touchArchiver.get(b)[1])).bounds.yCoord || p.bounds.yCoord+p.bounds.height == uProps.get(uArchiver.indexOf(p.touchArchiver.get(b)[1])).bounds.yCoord+uProps.get(uArchiver.indexOf(p.touchArchiver.get(b)[1])).bounds.height)
													blockCount++;
											}
											
											}
											
										}//if(p.id == 1 && p.pointArchiver.indexOf(q2) == 0)System.out.println(q2.get(0)[0]+" "+q2.get(0)[1]+"   "+p.touchArchiver.size()+" "+blockCount);
										if(blockCount > 1)
										{
											q2.set(1,new int[]{-1});
										}
									}
									
									if(q2.get(1)[0] != -1)
									{
										boolean isIntersecting = false;
										
										double x1 = q1.get(0)[0];
										double y1 = q1.get(0)[1];
										double x2 = q2.get(0)[0];
										double y2 = q2.get(0)[1];
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
										for(Prop u: uProps)
										{
											if(l.intersects(u.bounds.xCoord,u.bounds.yCoord,u.bounds.width,u.bounds.height))
												isIntersecting = true;
										}
										
										if(!isIntersecting)
										{
											if(d == -1 || d > Math.sqrt(Math.pow(x1-x2,2)+Math.pow(y1-y2,2)))
											{
												if(q2.size() < 2)
												{
													d = Math.sqrt(Math.pow(x1-x2,2)+Math.pow(y1-y2,2));
													e = new int[]{1,props.indexOf(p),p.pointArchiver.indexOf(q2)};
												//	e = new int[]{1,p2.id,p2.pointArchiver.indexOf(q2)};
												}
												else
												{
													boolean isNew = true;
													for(int f = 2; f < q2.size(); f++)
													{
														if(q2.get(f)[0] == v.id && q2.get(f)[1] == v.pointArchiver.indexOf(q1))
															isNew = false;
													}
													if(isNew)
													{
														d = Math.sqrt(Math.pow(x1-x2,2)+Math.pow(y1-y2,2));
														e = new int[]{1,props.indexOf(p),p.pointArchiver.indexOf(q2)};
													//	e = new int[]{1,p2.id,p2.pointArchiver.indexOf(q2)};
													}
												}
											}
										}
									}
								}
							}
						}
					}
					
					if(d != -1)
					{
						q1.add(e);
			//			props.get(e[0]).pointArchiver.get(e[2]).add(new int[]{p1.id,p1.pointArchiver.indexOf(q1)});
					}
					
					//TEST
				/*	System.out.println(">> "+v.id+" "+v.pointArchiver.indexOf(q1));
					for(int f = 2; f < q1.size(); f++)
						System.out.println("   "+q1.get(f)[0]+" "+q1.get(f)[1]);*/
					//==
				}
			}
		}
		
	//	navMesh = new ArrayList<double[]>();
		for(ArrayList<Prop> o: obsPolys)
		{
			for(Prop p: o)
			{
				for(ArrayList<int[]> q: p.pointArchiver)
				{
					for(int n = 2; n < q.size(); n++)
					{
						if(q.get(n)[0] == 0)
							navMesh.add(new double[]{q.get(0)[0],q.get(0)[1],mapPolys.get(q.get(n)[1]).xpoints[q.get(n)[2]],mapPolys.get(q.get(n)[1]).ypoints[q.get(n)[2]],1});
						else if(q.get(n)[0] == 1)
							navMesh.add(new double[]{q.get(0)[0],q.get(0)[1],props.get(q.get(n)[1]).pointArchiver.get(q.get(n)[2]).get(0)[0],props.get(q.get(n)[1]).pointArchiver.get(q.get(n)[2]).get(0)[1],1});
					}
				}
			}
		}//System.out.println(props.get(2).pointArchiver.get(1).size()+" "+props.get(2).pointArchiver.get(1).get(props.get(2).pointArchiver.get(1).size()-1)[0]+" "+props.get(2).pointArchiver.get(1).get(props.get(2).pointArchiver.get(1).size()-1)[1]+"   "+navMesh.size());
	}
	
	public void updateTrail(String p, int t)
	{
		int p1 = 0;
		int p2 = 0;
		int p3 = 0;
		int p4 = 0;
		
		if(p.equals("prop"))
		{
			p1 = props.get(t).bounds.xCoord;
			p2 = props.get(t).bounds.yCoord;
			p3 = props.get(t).bounds.xCoord+props.get(t).bounds.width;
			p4 = props.get(t).bounds.yCoord+props.get(t).bounds.height;
		}
		else if(p.equals("pleb"))
		{
			p1 = plebs.get(t).xCoord;
			p2 = plebs.get(t).yCoord;
			p3 = plebs.get(t).xCoord+plebs.get(t).width;
			p4 = plebs.get(t).yCoord+plebs.get(t).height;
		}
		else if(p.equals("pupp"))
		{
			p1 = puppets.get(t).xCoord;
			p2 = puppets.get(t).yCoord;
			p3 = puppets.get(t).xCoord+puppets.get(t).width;
			p4 = puppets.get(t).yCoord+puppets.get(t).height;
		}
		else
			return;
		System.out.println("==");	//BITCH
		ArrayList<double[]> mapMesh = getMapMesh();
		mapArchiver = new ArrayList<ArrayList<int[]>>();
		ArrayList<Polygon> mPolys = getmapPolys(mapMesh,mapMesh.size());
		mapPolys = new ArrayList<Polygon>();
		for(int m = 0; m < mPolys.size(); m++)
			mapPolys.add(mPolys.get(m));
		ArrayList<Integer> a = new ArrayList<Integer>();
		int[][] q = new int[2][3];
		ArrayList<ArrayList<Integer>> points = getAllPoints(mapPolys,/*mapMesh,*/a,q);
	/*	int m = 0;
		for(Polygon o: mapPolys)
			m += o.npoints;
		
		int c = m;*/
		
		int c = 0;
		for(Polygon m: mapPolys)
			c += m.npoints;
		int u = 0;
		int w = -1;
		if(p.equals("prop"))
		{
			for(int v = u+q[0][0]; u < v; u++)
			{
				boolean pointsMatch = true;
				for(int b = 0; b < a.get(u); b++)
				{
					if(points.get(c+b).get(3) != p1 && points.get(c+b).get(3) != p3 && points.get(c+b).get(4) != p2 && points.get(c+b).get(4) != p4)
						pointsMatch = false;
				}
				if(pointsMatch)
				{
					w = u;
					u = v;
				}
				else
					c += a.get(u);
			}
		}
		else if(p.equals("pleb"))
		{
			c += q[1][0];
			u += q[0][0];
			for(int v = u+q[0][1]; u < v; u++)
			{
				boolean pointsMatch = true;
				for(int b = 0; b < a.get(u); b++)
				{
					if(points.get(c+b).get(3) != p1 && points.get(c+b).get(3) != p3 && points.get(c+b).get(4) != p2 && points.get(c+b).get(4) != p4)
						pointsMatch = false;
				}
				if(pointsMatch)
				{
					w = u;
					u = v;
				}
				else
					c += a.get(u);
			}
		}
		else if(p.equals("pupp"))
		{
			c += q[1][0]+q[1][1];
			u += q[0][0]+q[0][1];
			for(int v = u+q[0][2]; u < v; u++)
			{
				boolean pointsMatch = true;
				for(int b = 0; b < a.get(u); b++)
				{
					if(points.get(c+b).get(3) != p1 && points.get(c+b).get(3) != p3 && points.get(c+b).get(4) != p2 && points.get(c+b).get(4) != p4)
						pointsMatch = false;
				}
				if(pointsMatch)
				{
					w = u;
					u = v;
				}
				else
					c += a.get(u);
			}
		}
		
		if(w != -1)
		{double aa = System.currentTimeMillis(); System.out.println(a.get(w)+" "+points.size()+" "+mapMesh.size()+" "+props.size()+"   ("+(a.get(w)*points.size()*mapMesh.size()*props.size())+")");	//BITCH
			ArrayList<ArrayList<int[]>> blackList = new ArrayList<ArrayList<int[]>>();
			for(int b = 0; b < a.get(w); b++)
				blackList.add(new ArrayList<int[]>());
			
			int[] i = new int[a.get(w)];
			ArrayList<int[]> j = new ArrayList<int[]>();
			
			for(int r1 = c; r1 < c+a.get(w); r1++)
			{
				for(int r2 = 0; r2 < points.size(); r2++)
				{
					if(points.get(r2).get(0) != 3 && (r2 < c || r2 >= c+a.get(w)))
					{
						boolean blackListed = false;
						for(int[] b: blackList.get(r1-c))
						{
							if(b[0] == points.get(r2).get(3) && b[1] == points.get(r2).get(4))
								blackListed = true;
						}
						
						boolean withinMain = true;
						boolean isObstructed = false;
						double x1 = points.get(r1).get(3);
						double y1 = points.get(r1).get(4);
						double x2 = points.get(r2).get(3);
						double y2 = points.get(r2).get(4);
						int u1 = 0;
						int u2 = 0;
						int v1 = 0;
						int v2 = 0;
						
						i[r1-c] = points.get(r1).get(2);
						j.add(new int[4]);
						j.get(j.size()-1)[0] = points.get(r1).get(1);
						j.get(j.size()-1)[1] = points.get(r1).get(2);
						j.get(j.size()-1)[2] = points.get(r2).get(1);
						j.get(j.size()-1)[3] = points.get(r2).get(2);
						
						if(x1 > x2)
						{
					/*		x1 = points.get(r2).get(3);
							y1 = points.get(r2).get(4);
							x2 = points.get(r1).get(3);
							y2 = points.get(r1).get(4);
							j.get(j.size()-1)[0] = points.get(r2).get(1);
							j.get(j.size()-1)[1] = points.get(r2).get(2);
							j.get(j.size()-1)[2] = points.get(r1).get(1);
							j.get(j.size()-1)[3] = points.get(r1).get(2);*/
							
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
						double bb = System.currentTimeMillis(); System.out.println(bb-aa);	//BITCH
						for(double[] o1: mapMesh)
						{
							double l1 = o1[0];
							double l2 = o1[2];
							if(l1 > l2)
							{
								l1 = o1[2];
								l2 = o1[0];
							}
							
							if((x1 >= l1 && x1 <= l2) || (x2 >= l2 && l2 <= x2))
							{
								if(Line2D.linesIntersect(x1+u1,y1+v1,x2+u2,y2+v2,o1[0],o1[1],o1[2],o1[3]))
									withinMain = false;
								for(Polygon o2: mapPolys)
								{
									if(mapPolys.indexOf(o2) == 0 && (!o2.contains(x1+u1,y1+v1) || !o2.contains(x2+u2,y2+v2)))
										withinMain = false;
									if(mapPolys.indexOf(o2) > 0 && (o2.contains(x1+u1,y1+v1) || o2.contains(x2+u2,y2+v2)))
										withinMain = false;
								}
								
								if(points.get(r1).get(0) == 3 && points.get(r2).get(0) == 3)
									isObstructed = true;
								Line2D.Double l = new Line2D.Double(x1+u1,y1+v1,x2+u2,y2+v2);
								for(Prop o2: props)
								{
									if(l.intersects(o2.bounds.xCoord,o2.bounds.yCoord,o2.bounds.width,o2.bounds.height))
										isObstructed = true;
								}
							/*	for(Puppet o2: puppets)
								{
									if(l.intersects(o2.xCoord,o2.yCoord,o2.width,o2.height))
										isObstructed = true;
								}*/
							}
						}
						
						if(withinMain && !isObstructed && !blackListed)
						{
							if(points.get(r1).size() <= 5)
							{
								points.get(r1).add(5,points.get(r2).get(3));
								points.get(r1).add(6,points.get(r2).get(4));
							}	
							else if(Math.sqrt(Math.pow(points.get(r1).get(3)-points.get(r2).get(3),2)+Math.pow(points.get(r1).get(4)-points.get(r2).get(4),2)) < Math.sqrt(Math.pow(points.get(r1).get(3)-points.get(r1).get(5),2)+Math.pow(points.get(r1).get(4)-points.get(r1).get(6),2)))
							{
								points.get(r1).set(5,points.get(r2).get(3));
								points.get(r1).set(6,points.get(r2).get(4));
							}
						}
					}
				}
				
				for(int r2 = c; r2 < c+a.get(w); r2++)
				{
					if(r1 != r2 && points.get(r1).size() > 5 && points.get(r2).size() > 5)
					{
						if(points.get(r1).get(5) == points.get(r2).get(5) && points.get(r1).get(6) == points.get(r2).get(6))
						{
							if(Math.sqrt(Math.pow(points.get(r1).get(3)-points.get(r1).get(5),2)+Math.pow(points.get(r1).get(4)-points.get(r1).get(6),2)) < Math.sqrt(Math.pow(points.get(r2).get(3)-points.get(r2).get(5),2)+Math.pow(points.get(r2).get(4)-points.get(r2).get(6),2)))
							{
								points.add(c+a.get(w),points.get(r2));
								points.get(r2).remove(6);
								points.get(r2).remove(5);
								blackList.add(blackList.get(r2-c));
								blackList.remove(r2-c);
								blackList.get(a.get(w)-1).add(new int[]{points.get(r1).get(5),points.get(r1).get(6)});
								points.remove(r2);
								if(points.size()-(blackList.get(a.get(w)-1).size()+4) > 0)
									r1--;
							}
							else if(Math.sqrt(Math.pow(points.get(r1).get(3)-points.get(r1).get(5),2)+Math.pow(points.get(r1).get(4)-points.get(r1).get(6),2)) > Math.sqrt(Math.pow(points.get(r2).get(3)-points.get(r2).get(5),2)+Math.pow(points.get(r2).get(4)-points.get(r2).get(6),2)))
							{
								points.add(c+a.get(w),points.get(r1));
								points.get(r1).remove(6);
								points.get(r1).remove(5);
								blackList.add(blackList.get(r1-c));
								blackList.remove(r1-c);
								blackList.get(a.get(w)-1).add(new int[]{points.get(r2).get(5),points.get(r2).get(6)});
								points.remove(r1);
								if(points.size()-(blackList.get(a.get(w)-1).size()+4) > 0)
									r1--;
							}
						}
					}
				}
			}
			
			for(int r3 = 0; r3 < 4; r3++)
			{
				boolean dne = true;
				if(points.get(c+r3).size() > 5)
				{
					switch(points.get(c+r3).get(2))
					{
						case 0:
							if((p1 == points.get(c+r3).get(3) && p2 == points.get(c+r3).get(4)) || (p1 == points.get(c+r3).get(5) && p2 == points.get(c+r3).get(6)))
								dne = false;
							break;
							
						case 1:
							if((p3 == points.get(c+r3).get(3) && p2 == points.get(c+r3).get(4)) || (p3 == points.get(c+r3).get(5) && p2 == points.get(c+r3).get(6)))
								dne = false;
							break;
							
						case 2:
							if((p1 == points.get(c+r3).get(3) && p4 == points.get(c+r3).get(4)) || (p1 == points.get(c+r3).get(5) && p4 == points.get(c+r3).get(6)))
								dne = false;
							break;
							
						case 3:
							if((p3 == points.get(c+r3).get(3) && p4 == points.get(c+r3).get(4)) || (p3 == points.get(c+r3).get(5) && p4 == points.get(c+r3).get(6)))
								dne = false;
							break;
					}
				}
				
		//COMMENTED OUT ON 8/25
		/*		for(int r4 = 0; r4 < a.get(w); r4++)
				{
					int n = navMesh.size()-(points.size()-(c+r4));
					if(navMesh.get(n)[5] == points.get(c+r3).get(1) && navMesh.get(n)[6] == points.get(c+r3).get(2))
					{	
						if(dne)
						{
							int q1 = 0;
							int q2 = 0;
							switch((int)navMesh.get(n)[6])
							{
								case 0:
									q1 = p1;
									q2 = p2;
									break;
								case 1:
									q1 = p3;
									q2 = p2;
									break;
								case 2:
									q1 = p1;
									q2 = p4;
									break;
								case 3:
									q1 = p3;
									q2 = p4;
									break;
							}
							
							navMesh.get(n)[0] = q1;
							navMesh.get(n)[1] = q2;
							navMesh.get(n)[2] = q1;
							navMesh.get(n)[3] = q2;
						}
						else
						{
							if(Math.sqrt(Math.pow(points.get(c+r3).get(3)-points.get(c+r3).get(5),2)+Math.pow(points.get(c+r3).get(4)-points.get(c+r3).get(6),2)) > 0)
							{
								navMesh.get(n)[0] = points.get(c+r3).get(3);
								navMesh.get(n)[1] = points.get(c+r3).get(4);
								navMesh.get(n)[2] = points.get(c+r3).get(5);
								navMesh.get(n)[3] = points.get(c+r3).get(6);
								
								navMesh.get(n)[5] = points.get(c+r3).get(1);
								navMesh.get(n)[6] = points.get(c+r3).get(2);
								
								if(p.equals("prop"))
									navMesh.get(n)[4] = 1;
								else if(p.equals("pleb"))
									navMesh.get(n)[4] = 2;
								else if(p.equals("pupp"))
									navMesh.get(n)[4] = 3;
							}
						}
					}
				}
			*/	
				
				
				
		/*		if(dne)
				{
					//TEST
					System.out.println();
					for(int f = 0; f < a.get(w); f++)
						System.out.println(f+"   "+points.get(c+f).get(3)+" "+points.get(c+f).get(4)+"   "+i[f]);
					//==
					
					for(int r4 = c; r4 < c+a.get(w); r4++)
					{
						if(r3 == i[r4-c] && points.get(r4).size() > 5)
						{
							dne = false;
							
							//TEST
							System.out.println(">>    "+r3+"   "+(r4-c)+" "+points.get(c+r3+d).get(2));
							System.out.println();
						/*	System.out.println((navMesh.size()-points.size()+r4+2)+" "+navMesh.size());
							System.out.println(navMesh.size()+" "+points.size()+" "+r4+" ("+c+" "+(c+a.get(w))+")");
							System.out.println();*/
							
				/*			if(Math.sqrt(Math.pow(points.get(r4).get(3)-points.get(r4).get(5),2)+Math.pow(points.get(r4).get(4)-points.get(r4).get(6),2)) > 0)
							{
				/*				navMesh.add(r4,new double[]{points.get(r4).get(3),points.get(r4).get(4),points.get(r4).get(5),points.get(r4).get(6),-1,points.get(r4).get(1),points.get(r4).get(2)});
								if(p.equals("prop"))
									navMesh.get(r4)[4] = 1;
								else if(p.equals("pleb"))
									navMesh.get(r4)[4] = 2;
								else if(p.equals("pupp"))
									navMesh.get(r4)[4] = 3;*/
								
							/*	navMesh.get(r4)[0] = points.get(r4).get(3);
								navMesh.get(r4)[1] = points.get(r4).get(4);
								navMesh.get(r4)[2] = points.get(r4).get(5);
								navMesh.get(r4)[3] = points.get(r4).get(6);
								
								navMesh.get(r4)[5] = points.get(r4).get(1);
								navMesh.get(r4)[6] = points.get(r4).get(2);*/	
								
						/*		navMesh.get(navMesh.size()-points.size()+r4+2)[0] = points.get(r4).get(3);
								navMesh.get(navMesh.size()-points.size()+r4+2)[1] = points.get(r4).get(4);
								navMesh.get(navMesh.size()-points.size()+r4+2)[2] = points.get(r4).get(5);
								navMesh.get(navMesh.size()-points.size()+r4+2)[3] = points.get(r4).get(6);
								
								navMesh.get(navMesh.size()-points.size()+r4+2)[5] = points.get(r4).get(1);
								navMesh.get(navMesh.size()-points.size()+r4+2)[6] = points.get(r4).get(2);*/
						//		navMesh.get(navMesh.size()-points.size()+r4+2)[7] = points.get(r4).get(1);
						//		navMesh.get(navMesh.size()-points.size()+r4+2)[8] = points.get(r4).get(2);
								
						/*		if(p.equals("prop"))
									navMesh.get(navMesh.size()-points.size()+r4+2)[4] = 1;
								else if(p.equals("pleb"))
									navMesh.get(navMesh.size()-points.size()+r4+2)[4] = 2;
								else if(p.equals("pupp"))
									navMesh.get(navMesh.size()-points.size()+r4+2)[4] = 3;*/
		/*					}
						}
					}
			/*		if(dne)
					{
						int q1 = 0;
						int q2 = 0;
						switch((int)navMesh.get(navMesh.size()-points.size()+r3+c+2)[6])
						{
							case 0:
								q1 = p1;
								q2 = p2;
								break;
							case 1:
								q1 = p3;
								q2 = p2;
								break;
							case 2:
								q1 = p1;
								q2 = p4;
								break;
							case 3:
								q1 = p3;
								q2 = p4;
								break;
						}
						
						navMesh.get(navMesh.size()-points.size()+r3+c+2)[0] = q1;
						navMesh.get(navMesh.size()-points.size()+r3+c+2)[1] = q2;
						navMesh.get(navMesh.size()-points.size()+r3+c+2)[2] = q1;
						navMesh.get(navMesh.size()-points.size()+r3+c+2)[3] = q2;
					}*/
		/*		}
				else
				{
					r3--;
					d++;
				}*/
			}
		}
	}
	
	public void updatePoints(/*int f*/)
	{
		points = new ArrayList<int[]>();
		for(int m = 0; m < mapPolys.size(); m++)
		{
			for(int n = 0; n < mapPolys.get(m).npoints; n++)
				points.add(new int[]{0,mapPolys.get(m).xpoints[n],mapPolys.get(m).ypoints[n],mapArchiver.get(m).get(n)[0],mapArchiver.get(m).get(n)[1]});
		}
		
		int pStart = points.size();
		for(Prop p: props)
		{
			points.add(new int[]{1,p.bounds.xCoord,p.bounds.yCoord,p.id,-1});
			points.add(new int[]{1,p.bounds.xCoord+p.bounds.width,p.bounds.yCoord,p.id,-1});
			points.add(new int[]{1,p.bounds.xCoord,p.bounds.yCoord+p.bounds.height,p.id,-1});
			points.add(new int[]{1,p.bounds.xCoord+p.bounds.width,p.bounds.yCoord+p.bounds.height,p.id,-1});
		}
	/*	for(Plebs p: plebs)
		{
			points.add(new int[]{1,p.xCoord,p.yCoord,p.id,-1});
			points.add(new int[]{1,p.xCoord+p.width,p.yCoord,p.id,-1});
			points.add(new int[]{1,p.xCoord,p.yCoord+p.height,p.id,-1});
			points.add(new int[]{1,p.xCoord+p.width,p.yCoord+p.height,p.id,-1});
		}*/
		
		int pLimit = points.size();
		for(int p1 = pStart; p1 < pLimit; p1++)
		{
			for(int p2 = 0; p2 < pLimit; p2++)
			{
				if(p1 > pStart && (p2 > 0 || (p2 > pStart && points.get(p1)[3] != points.get(p2)[3])))
				{
					if(points.get(p1)[1] == (int)points.get(p2)[1] && (int)points.get(p1)[2] == (int)points.get(p2)[2])
					{
						
					}
				}
			}
		}
		
	/*					if(b.get(p1-o) != b.get(p2-o))
						{
							int a3 = b.get(p1-o);
							int a4 = b.get(p2-o);
							
							if(points.get(p1).get(0) != 3)
							{
								a.set(a3,a.get(a3)-1);
								if(a.get(a3) <= 0)
								{
									a.remove(a3);
									q[0][points.get(p1).get(0)-1]--;
									for(int c = a3; c < b.size(); c++)
										b.set(c,b.get(c)-1);
									if(a4 > a3)
										a4--;
								}
							}
							
							if(points.get(p2).get(0) != 3)
							{
							a.set(a4,a.get(a4)-1);
								if(a.get(a4) <= 0)
								{
									a.remove(a4);
									q[0][points.get(p2).get(0)-1]--;
									for(int c = a4; c < b.size(); c++)
										b.set(c,b.get(c)-1);
								}
							}
							
							if(p1 > p2)
							{
								int r1 = points.get(p1).get(0)-1;
								int r2 = points.get(p2).get(0)-1;
								
								if(points.get(p1).get(0) != 3)
								{
								points.remove(p1);
								b.remove(p1-o);
								q[1][r1]--;
								}
								
								if(points.get(p2).get(0) != 3)
								{
								points.remove(p2);
								b.remove(p2-o);
								q[1][r2]--;
								}
							}
							else
							{
								int r1 = points.get(p1).get(0)-1;
								int r2 = points.get(p2).get(0)-1;
								
								if(points.get(p2).get(0) != 3)
								{
									points.remove(p2);
									b.remove(p2-o);
									q[1][r2]--;
								}
								
								if(points.get(p1).get(0) != 3)
								{
									points.remove(p1);
									b.remove(p1-o);
									q[1][r1]--;
								}
							}
							
							pLimit = points.size();
							if(p1 < pLimit && p2 < pLimit)
							{
								if(points.get(p1).get(0) != 3)
								{
									p1--;
									if(p1 < 0)
										p1 = 0;
								}
								if(points.get(p2).get(0) != 3)
									p2 = 0;
							}
						}
					}
				}
			}
		}*/
	/*	for(int p1 = 0; p1 < points.size(); p1++)
		{
			if(points.get(p1).get(0) > 0)
			{
				int[][]p2 = new int[][]{{points.get(p1).get(1),points.get(p1).get(2)},{points.get(p1+1).get(1),points.get(p1+1).get(2)},{points.get(p1+2).get(1),points.get(p1+2).get(2)},{points.get(p1+3).get(1),points.get(p1+3).get(2)}};			
				for(double[] p: o2)
				{
					double x1 = p[0];
					double y1 = p[1];
					double x2 = p[2];
					double y2 = p[3];
					if(x1 > x2)
					{
						x1 = p[2];
						x2 = p[0];
					}
					if(y1 > y2)
					{
						y1 = p[3];
						y2 = p[1];
					}
					
					for(int q = 0; q < 4; q++)
					{
						if((p2[q][0] >= x1 && p2[q][0] <= x2) && (p2[q][1] >= y1 && p2[q][1] <= y2))
						{
							points.get(p1+q).add(3,p2[q][0]);
							points.get(p1+q).add(4,p2[q][1]);
						}
					}
				}
				
				for(Prop p: props)
				{
					double x1 = p.bounds.xCoord;
					double y1 = p.bounds.yCoord;
					double x2 = p.bounds.xCoord+p.bounds.width;
					double y2 = p.bounds.yCoord+p.bounds.height;
					int q = 0;
					if(p2[q][0] != x1 && p2[q+1][0] != x2 && p2[q][1] != y1 && p2[q+2][1] != y2)
					{
						while(q < 4)
						{
							if((p2[q][0] >= x1 && p2[q][0] <= x2) && (p2[q][1] >= y1 && p2[q][1] <= y2))
							{
								points.get(p1+q).add(3,p2[q][0]);
								points.get(p1+q).add(4,p2[q][1]);
							}
							q++;
						}
					}
				}
				
				for(Pleb p: plebs)
				{
					double x1 = p.xCoord;
					double y1 = p.yCoord;
					double x2 = p.xCoord+p.width;
					double y2 = p.yCoord+p.height;
					int q = 0;
					if(p2[q][0] != x1 && p2[q+1][0] != x2 && p2[q][1] != y1 && p2[q+2][1] != y2)
					{
						while(q < 4)
						{
							if((p2[q][0] >= x1 && p2[q][0] <= x2) && (p2[q][1] >= y1 && p2[q][1] <= y2))
							{
								points.get(p1+q).add(3,p2[q][0]);
								points.get(p1+q).add(4,p2[q][1]);
							}
							q++;
						}
					}
				}
				
				for(Puppet p: puppets)
				{
					double x1 = p.xCoord;
					double y1 = p.yCoord-p.height;
					double x2 = p.xCoord+p.width;
					double y2 = p.yCoord+p.height/4;
					int q = 0;
					if(p2[q][0] != x1 && p2[q+1][0] != x2 && p2[q][1] != y1 && p2[q+2][1] != y2)
					{
						while(q < 4)
						{
							if((p2[q][0] >= x1 && p2[q][0] <= x2) && (p2[q][1] >= y1 && p2[q][1] <= y2))
							{
								points.get(p1+q).add(3,p2[q][0]);
								points.get(p1+q).add(4,p2[q][1]);
							}
							q++;
						}
					}
				}	
				p1+=3;
			}
		}*/
		
		//TEST
/*		for(int[]z: points)
		System.out.println(z[0]+" "+z[1]+"   "+z[2]+" "+z[3]);
		System.out.println();*/
	}
	
	
	private void buildFairyTrail()
	{
		ArrayList<double[]> mapMesh = getMapMesh();
		mapArchiver = new ArrayList<ArrayList<int[]>>();
		ArrayList<Polygon> mPolys = getmapPolys(mapMesh,mapMesh.size());
		mapPolys = new ArrayList<Polygon>();
		for(int m = 0; m < mPolys.size(); m++)
			mapPolys.add(mPolys.get(m));
		navMesh = new ArrayList<double[]>();
		
		//============================
		//NAVMESH AND NAVPOLY CREATION
		//============================
		
		ArrayList<Polygon> navPolys = new ArrayList<Polygon>();
		ArrayList<double[]> partitions = new ArrayList<double[]>();
		for(int p = 0; p < mapPolys.get(0).npoints; p++)
			partitions.add(new double[]{mapPolys.get(0).xpoints[p],mapPolys.get(0).ypoints[p]});
		
		boolean parting = true;
		while(parting)
		{
			ArrayList<double[]> parts = partitions;
			int pLimit = parts.size();
			parting = false;
			for(int p1 = 0; p1 < pLimit; p1++)
			{
				int p2 = p1+2;
				if(p2 >= parts.size())
					p2 -= parts.size();
				
				boolean withinMain = true;
				boolean isIntersecting = false;
				double x1 = parts.get(p1)[0];
				double y1 = parts.get(p1)[1];
				double x2 = parts.get(p2)[0];
				double y2 = parts.get(p2)[1];
				if(x1 > x2)
				{
					x1 = parts.get(p2)[0];
					y1 = parts.get(p2)[1];
					x2 = parts.get(p1)[0];
					y2 = parts.get(p1)[1];
				}
				for(double[] o: mapMesh)
				{
					double l1 = o[0];
					double l2 = o[2];
					int z1 = 0;
					int z2 = 0;
					if(l1 > l2)
					{
						l1 = o[2];
						l2 = o[0];
					}
					if(y1 > y2)
					{
						z1 = -1;
						z2 = 1;
					}
					else if(y1 < y2)
					{
						z1 = 1;
						z2 = -1;
					}
					
					if((x1 >= l1 && x1 <= l2) || (x2 >= l2 && l2 <= x2))
					{
						if(Line2D.linesIntersect(x1+1,y1+z1,x2-1,y2+z2,o[0],o[1],o[2],o[3]))
							withinMain = false;
						for(Polygon p: mapPolys)
						{
							if(mapPolys.indexOf(p) == 0 && (!p.contains(x1+1,y1+z1) || !p.contains(x2-1,y2+z2)))
								withinMain = false;
							if(mapPolys.indexOf(p) > 0 && (p.contains(x1+1,y1+z1) || p.contains(x2-1,y2+z2)))
								withinMain = false;
						}
					}
				}
				
				for(int n = 0; n < navMesh.size(); n++)
				{
					int z1 = 0;
					int z2 = 0;
					if(y1 > y2)
					{
						z1 = -1;
						z2 = 1;
					}
					else if(y1 < y2)
					{
						z1 = 1;
						z2 = -1;
					}
					if(Line2D.linesIntersect(x1+1,y1+z1,x2-1,y2+z2,(int)navMesh.get(n)[0],(int)navMesh.get(n)[1],(int)navMesh.get(n)[2],(int)navMesh.get(n)[3]))
						isIntersecting = true;
				}
				
				if(withinMain && !isIntersecting)
				{
					navMesh.add(new double[]{parts.get(p1)[0],parts.get(p1)[1],parts.get(p2)[0],parts.get(p2)[1],0,-1,-1,-1,-1});
					int p3 = p1+1;
					if(p3 >= parts.size())
						p3 -= parts.size();
					navPolys.add(new Polygon(new int[]{(int)parts.get(p1)[0],(int)parts.get(p2)[0],(int)parts.get(p3)[0]},new int[]{(int)parts.get(p1)[1],(int)parts.get(p2)[1],(int)parts.get(p3)[1]},3));
					parts.remove(p3);
					pLimit = parts.size();
					parting = true;
				}
			}
		}
		navPolys.add(new Polygon());
		for(double[] p: partitions)
			navPolys.get(navPolys.size()-1).addPoint((int)p[0],(int)p[1]);
		
		//==============================
		//ADDITIONAL MAPPOLY INTEGRATION
		//==============================
		
		ArrayList<int[]> mapPoints = new ArrayList<int[]>();
		for(Polygon m1: mapPolys)
		{
			for(int m2 = 0; m2 < m1.npoints; m2++)
				mapPoints.add(new int[]{m1.xpoints[m2],m1.ypoints[m2]});
		}
		
		for(Polygon m1: mapPolys)
		{
			if(mapPolys.indexOf(m1) > 0)
			{
				for(int m2 = 0; m2 < m1.npoints; m2++)
				{
					double a = -1;
					int b = -1;
					
					for(int m3 = 0; m3 < mapPoints.size(); m3++)
					{
						boolean withinMain = true;
						boolean isIntersecting = false;
						if(m2 < m1.npoints)
						{
							if(m1.xpoints[m2] != mapPoints.get(m3)[0] && m1.ypoints[m2] != mapPoints.get(m3)[1])
							{
								double x1 = m1.xpoints[m2];
								double y1 = m1.ypoints[m2];
								double x2 = mapPoints.get(m3)[0];
								double y2 = mapPoints.get(m3)[1];
								if(x1 > x2)
								{
									x1 = mapPoints.get(m3)[0];
									y1 = mapPoints.get(m3)[1];
									x2 = m1.xpoints[m2];
									y2 = m1.ypoints[m2];
								}
								
								for(double[] o: mapMesh)
								{
									double l1 = o[0];
									double l2 = o[2];
									int z1 = 0;
									int z2 = 0;
									if(l1 > l2)
									{
										l1 = o[2];
										l2 = o[0];
									}
									if(y1 > y2)
									{
										z1 = -1;
										z2 = 1;
									}
									else if(y1 < y2)
									{
										z1 = 1;
										z2 = -1;
									}
									
									if((x1 >= l1 && x1 <= l2) || (x2 >= l2 && l2 <= x2))
									{
										if(Line2D.linesIntersect(x1+1,y1+z1,x2-1,y2+z2,o[0],o[1],o[2],o[3]))
											withinMain = false;
										for(Polygon p: mapPolys)
										{
											if(mapPolys.indexOf(p) == 0 && (!p.contains(x1+1,y1+z1) || !p.contains(x2-1,y2+z2)))
												withinMain = false;
											if(mapPolys.indexOf(p) > 0 && (p.contains(x1+1,y1+z1) || p.contains(x2-1,y2+z2)))
												withinMain = false;
										}
									}
								}
								
								for(int n = 0; n < navMesh.size(); n++)
								{
									int z1 = 0;
									int z2 = 0;
									if(y1 > y2)
									{
										z1 = -1;
										z2 = 1;
									}
									else if(y1 < y2)
									{
										z1 = 1;
										z2 = -1;
									}
									if(Line2D.linesIntersect(x1+1,y1+z1,x2-1,y2+z2,(int)navMesh.get(n)[0],(int)navMesh.get(n)[1],(int)navMesh.get(n)[2],(int)navMesh.get(n)[3]))
										isIntersecting = true;
								}
								
								if(withinMain && !isIntersecting)
								{
									if(a == -1 || a > Math.sqrt(Math.pow(x2-x1,2)+Math.pow(y2-y1,2)))
									{
										a = Math.sqrt(Math.pow(x2-x1,2)+Math.pow(y2-y1,2));
										b = m3;
									}
								}
							}
						}
					}
					
					if(b >= 0)
						navMesh.add(new double[]{m1.xpoints[m2],m1.ypoints[m2],mapPoints.get(b)[0],mapPoints.get(b)[1],0,-1,-1/*,-1,-1*/});
				}
			}
		}
		
		//===================================
		//NAVMESH AND NAVPOLYS SIMPLIFICATION
		//===================================
		
		ArrayList<Polygon> nPolys = new ArrayList<Polygon>();
		nPolys = navPolys;
		int pLimit = nPolys.size();
		for(int p1 = 0; p1 < pLimit; p1++)
		{
			for(int p2 = 0; p2 < pLimit; p2++)
			{
				if(p1 != p2)
				{
					int v = 0;
					int[][] vertices = new int[2][2];
					for(int n1 = 0; n1 < nPolys.get(p1).npoints; n1++)
					{
						for(int n2 = 0; n2 < nPolys.get(p2).npoints; n2++)
						{
							if(nPolys.get(p1).xpoints[n1] == nPolys.get(p2).xpoints[n2] && nPolys.get(p1).ypoints[n1] == nPolys.get(p2).ypoints[n2])
							{
								if(v < 2)
									vertices[v] = new int[]{n1,n2};
								v++;
							}
						}
					}
					
					if(v == 2)
					{
						int[] v1 = new int[]{vertices[0][0]-1,vertices[0][0]+1};
						int[] v2 = new int[]{vertices[0][1]-1,vertices[0][1]+1};
						if(v1[0] < 0)
							v1[0] += nPolys.get(p1).npoints;
						if(v1[1] >= nPolys.get(p1).npoints)
							v1[1] -= nPolys.get(p1).npoints;
						if(v2[0] < 0)
							v2[0] += nPolys.get(p2).npoints;
						if(v2[1] >= nPolys.get(p2).npoints)
							v2[1] -= nPolys.get(p2).npoints;
						
						int[] a = new int[3];
						if(nPolys.get(p1).xpoints[v1[0]] == nPolys.get(p2).xpoints[v2[0]] && nPolys.get(p1).ypoints[v1[0]] == nPolys.get(p2).ypoints[v2[0]])
						{
							vertices[1][0] = v1[0];
							vertices[1][1] = v2[0];
							a[0] = v1[1];
							a[1] = v2[1];
						}
						else if(nPolys.get(p1).xpoints[v1[0]] == nPolys.get(p2).xpoints[v2[1]] && nPolys.get(p1).ypoints[v1[0]] == nPolys.get(p2).ypoints[v2[1]])
						{
							vertices[1][0] = v1[0];
							vertices[1][1] = v2[1];
							a[0] = v1[1];
							a[1] = v2[0];
						}
						else if(nPolys.get(p1).xpoints[v1[1]] == nPolys.get(p2).xpoints[v2[0]] && nPolys.get(p1).ypoints[v1[1]] == nPolys.get(p2).ypoints[v2[0]])
						{
							vertices[1][0] = v1[1];
							vertices[1][1] = v2[0];
							a[0] = v1[0];
							a[1] = v2[1];
						}
						else if(nPolys.get(p1).xpoints[v1[1]] == nPolys.get(p2).xpoints[v2[1]] && nPolys.get(p1).ypoints[v1[1]] == nPolys.get(p2).ypoints[v2[1]])
						{
							vertices[1][0] = v1[1];
							vertices[1][1] = v2[1];
							a[0] = v1[0];
							a[1] = v2[0];
						}
						
						if(Line2D.linesIntersect(nPolys.get(p1).xpoints[vertices[0][0]],nPolys.get(p1).ypoints[vertices[0][0]],nPolys.get(p2).xpoints[vertices[1][1]],nPolys.get(p2).ypoints[vertices[1][1]],nPolys.get(p1).xpoints[a[0]],nPolys.get(p1).ypoints[a[0]],nPolys.get(p2).xpoints[a[1]],nPolys.get(p2).ypoints[a[1]]))
						{
							v1 = new int[]{vertices[1][0]-1,vertices[1][0]+1};
							v2 = new int[]{vertices[1][1]-1,vertices[1][1]+1};
							if(v1[0] < 0)
								v1[0] += nPolys.get(p1).npoints;
							if(v1[1] >= nPolys.get(p1).npoints)
								v1[1] -= nPolys.get(p1).npoints;
							if(v2[0] < 0)
								v2[0] += nPolys.get(p2).npoints;
							if(v2[1] >= nPolys.get(p2).npoints)
								v2[1] -= nPolys.get(p2).npoints;
							
							if(nPolys.get(p1).xpoints[v1[0]] == nPolys.get(p2).xpoints[v2[0]] && nPolys.get(p1).ypoints[v1[0]] == nPolys.get(p2).ypoints[v2[0]])
							{
								a[0] = v1[1];
								a[1] = v2[1];
							}
							else if(nPolys.get(p1).xpoints[v1[0]] == nPolys.get(p2).xpoints[v2[1]] && nPolys.get(p1).ypoints[v1[0]] == nPolys.get(p2).ypoints[v2[1]])
							{
								a[0] = v1[1];
								a[1] = v2[0];
							}
							else if(nPolys.get(p1).xpoints[v1[1]] == nPolys.get(p2).xpoints[v2[0]] && nPolys.get(p1).ypoints[v1[1]] == nPolys.get(p2).ypoints[v2[0]])
							{
								a[0] = v1[0];
								a[1] = v2[1];
							}
							else if(nPolys.get(p1).xpoints[v1[1]] == nPolys.get(p2).xpoints[v2[1]] && nPolys.get(p1).ypoints[v1[1]] == nPolys.get(p2).ypoints[v2[1]])
							{
								a[0] = v1[0];
								a[1] = v2[0];
							}
							
							if(Line2D.linesIntersect(nPolys.get(p1).xpoints[vertices[0][0]],nPolys.get(p1).ypoints[vertices[0][0]],nPolys.get(p2).xpoints[vertices[1][1]],nPolys.get(p2).ypoints[vertices[1][1]],nPolys.get(p1).xpoints[a[0]],nPolys.get(p1).ypoints[a[0]],nPolys.get(p2).xpoints[a[1]],nPolys.get(p2).ypoints[a[1]]))
							{
								ArrayList<double[]> nMesh = navMesh;
								int nLimit = nMesh.size();
								for(int n3 = 0; n3 < nLimit; n3++)
								{
									if((nMesh.get(n3)[0] == nPolys.get(p1).xpoints[vertices[0][0]] && nMesh.get(n3)[1] == nPolys.get(p1).ypoints[vertices[0][0]] && nMesh.get(n3)[2] == nPolys.get(p2).xpoints[vertices[1][1]] && nMesh.get(n3)[3] == nPolys.get(p2).ypoints[vertices[1][1]]) || (nMesh.get(n3)[0] == nPolys.get(p1).xpoints[vertices[1][0]] && nMesh.get(n3)[1] == nPolys.get(p1).ypoints[vertices[1][0]] && nMesh.get(n3)[2] == nPolys.get(p2).xpoints[vertices[0][1]] && nMesh.get(n3)[3] == nPolys.get(p2).ypoints[vertices[0][1]]))
									{
										nMesh.remove(n3);
										nLimit--;
									}
								}
								nMesh = navMesh;
								fusePolygons(nPolys,p1,p2,vertices);
								p1 -= 2;
								p2 -= 2;
								if(p1 < 0)
									p1 = 0;
								if(p2 < 0)
									p2 = 0;
								pLimit = nPolys.size();
							}
						}
					}
				}
			}
		}
		navPolys = nPolys;
		
		//==========================
		//PUPP PROP PLEB INTEGRATION
		//==========================
		
	/*	ArrayList<double[]> nMesh = new ArrayList<double[]>();
		for(int n = 0; n < navMesh.size(); n++)
			nMesh.add(navMesh.get(n));
	//	int nLimit = nMesh.size();*/
		
		openNav = new boolean[navMesh.size()];
		navArchiver = new ArrayList<ArrayList<Integer>>();
		for(int b = 0; b < openNav.length; b++)
		{
			openNav[b] = true;
			navArchiver.add(new ArrayList<Integer>());
		}
		
		for(int n = 0; n < navMesh.size(); n++)
		{
	//		boolean isIntersecting = false;
			double x1 = navMesh.get(n)[0];
			double y1 = navMesh.get(n)[1];
			double x2 = navMesh.get(n)[2];
			double y2 = navMesh.get(n)[3];
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
			
			for(Prop p: props)
			{
				if(l.intersects(p.bounds.xCoord,p.bounds.yCoord,p.bounds.width,p.bounds.height))
					navArchiver.get(n).add(props.indexOf(p));
					
			}
			if(navArchiver.get(n).size() > 0)
				openNav[n] = false;
		}
		
	/*	for(int n = 0; n < nLimit; n++)
		{
			boolean isIntersecting = false;
			double x1 = nMesh.get(n)[0];
			double y1 = nMesh.get(n)[1];
			double x2 = nMesh.get(n)[2];
			double y2 = nMesh.get(n)[3];
			if(x1 > x2)
			{
				x1 = nMesh.get(n)[2];
				y1 = nMesh.get(n)[3];
				x2 = nMesh.get(n)[0];
				y2 = nMesh.get(n)[1];
			}
			
			int z1 = 0;
			int z2 = 0;
			if(y1 > y2)
			{
				z1 = -1;
				z2 = 1;
			}
			else
			{
				z1 = 1;
				z2 = -1;
			}
			Line2D.Double l = new Line2D.Double(x1+1,y1+z1,x2-1,y2+z2);
			
			for(Prop p: props)
			{
				if(l.intersects(p.bounds.xCoord,p.bounds.yCoord,p.bounds.width,p.bounds.height))
					isIntersecting = true;
			}
			for(Pleb p: plebs)
			{
				if(l.intersects(p.xCoord,p.yCoord,p.width,p.height))
					isIntersecting = true;
			}	
			if(isIntersecting)
			{
				nMesh.remove(n);
				nLimit--;
				n--;
			}
		}
		navMesh = nMesh;*/
		
		
		for(Prop p: props)
		{
			for(int o = 0; o < 4; o++)
			{
			//	boolean foundLocation = false;
				p.pointArchiver.get(o).set(1,new int[]{-1});
				
				for(Floor f: floors)
				{
					if(p.pointArchiver.get(o).get(0)[0] > f.xCoord && p.pointArchiver.get(o).get(0)[0] < f.xCoord+f.width && p.pointArchiver.get(o).get(0)[1] > f.yCoord && p.pointArchiver.get(o).get(0)[1] < f.yCoord+f.height)
					{
						if(p.pointArchiver.get(o).get(1)[0] == -1)
							p.pointArchiver.get(o).get(1)[0] = floors.indexOf(f);
						else
						{
							int[] a = p.pointArchiver.get(o).get(1);
							p.pointArchiver.get(o).set(1,new int[a.length+1]);
							p.pointArchiver.get(o).get(1)[0] = floors.indexOf(f);
							for(int b = 1; b < p.pointArchiver.get(o).get(1).length; b++)
								p.pointArchiver.get(o).get(1)[b] = a[b-1];
						}
				//		foundLocation = true;
					}
					else
					{
						if(/*!foundLocation &&*/ p.pointArchiver.get(o).get(0)[1] == f.yCoord)
						{
							if(p.pointArchiver.get(o).get(0)[0] == f.xCoord || p.pointArchiver.get(o).get(0)[0] == f.xCoord+f.width)
								p.pointArchiver.get(o).set(1,new int[]{-1});
							else if(p.pointArchiver.get(o).get(0)[0] > f.xCoord && p.pointArchiver.get(o).get(0)[0] < f.xCoord+f.width)
							{
								if(p.pointArchiver.get(o).get(1)[0] == -1)
									p.pointArchiver.get(o).get(1)[0] = floors.indexOf(f);
								else
								{
									int[] a = p.pointArchiver.get(o).get(1);
									p.pointArchiver.get(o).set(1,new int[a.length+1]);
									p.pointArchiver.get(o).get(1)[0] = floors.indexOf(f);
									for(int b = 1; b < p.pointArchiver.get(o).get(1).length; b++)
										p.pointArchiver.get(o).get(1)[b] = a[b-1];
								}
								
								for(int[] w: f.walls[0])
								{
									if(p.pointArchiver.get(o).get(0)[0] >= w[0] && p.pointArchiver.get(o).get(0)[0] <= w[1])
										p.pointArchiver.get(o).set(1,new int[]{-1});
								}
							}
							
						/*	if(p.pointArchiver.get(o).get(1)[0] == -1 || p.pointArchiver.get(o).get(1).length > 1)
								foundLocation = true;*/
						}
						else if(/*!foundLocation &&*/ p.pointArchiver.get(o).get(0)[0] == f.xCoord+f.width)
						{
							if(p.pointArchiver.get(o).get(0)[1] == f.yCoord || p.pointArchiver.get(o).get(0)[1] == f.yCoord+f.height)
								p.pointArchiver.get(o).set(1,new int[]{-1});
							else if(p.pointArchiver.get(o).get(0)[1] > f.yCoord && p.pointArchiver.get(o).get(0)[1] < f.yCoord+f.height)
							{
								if(p.pointArchiver.get(o).get(1)[0] == -1)
									p.pointArchiver.get(o).get(1)[0] = floors.indexOf(f);
								else
								{
									int[] a = p.pointArchiver.get(o).get(1);
									p.pointArchiver.get(o).set(1,new int[a.length+1]);
									p.pointArchiver.get(o).get(1)[0] = floors.indexOf(f);
									for(int b = 1; b < p.pointArchiver.get(o).get(1).length; b++)
										p.pointArchiver.get(o).get(1)[b] = a[b-1];
								}
								
								for(int[] w: f.walls[1])
								{
									if(p.pointArchiver.get(o).get(0)[1] >= w[0] && p.pointArchiver.get(o).get(0)[1] <= w[1])
										p.pointArchiver.get(o).set(1,new int[]{-1});
								}
							}
							
						/*	if(p.pointArchiver.get(o).get(1)[0] == -1 || p.pointArchiver.get(o).get(1).length > 1)
								foundLocation = true;*/
						}
						else if(/*!foundLocation &&*/ p.pointArchiver.get(o).get(0)[1] == f.yCoord+f.height)
						{
							if(p.pointArchiver.get(o).get(0)[0] == f.xCoord || p.pointArchiver.get(o).get(0)[0] == f.xCoord+f.width)
								p.pointArchiver.get(o).set(1,new int[]{-1});
							else if(p.pointArchiver.get(o).get(0)[0] > f.xCoord && p.pointArchiver.get(o).get(0)[0] < f.xCoord+f.width)
							{
								if(p.pointArchiver.get(o).get(1)[0] == -1)
									p.pointArchiver.get(o).get(1)[0] = floors.indexOf(f);
								else
								{
									int[] a = p.pointArchiver.get(o).get(1);
									p.pointArchiver.get(o).set(1,new int[a.length+1]);
									p.pointArchiver.get(o).get(1)[0] = floors.indexOf(f);
									for(int b = 1; b < p.pointArchiver.get(o).get(1).length; b++)
										p.pointArchiver.get(o).get(1)[b] = a[b-1];
								}
								
								for(int[] w: f.walls[2])
								{
									if(p.pointArchiver.get(o).get(0)[0] >= w[0] && p.pointArchiver.get(o).get(0)[0] <= w[1])
										p.pointArchiver.get(o).set(1,new int[]{-1});
								}
							}
							
						/*	if(p.pointArchiver.get(o).get(1)[0] == -1 || p.pointArchiver.get(o).get(1).length > 1)
								foundLocation = true;*/
						}
						else if(/*!foundLocation &&*/ p.pointArchiver.get(o).get(0)[0] == f.xCoord)
						{
							if(p.pointArchiver.get(o).get(0)[1] == f.yCoord || p.pointArchiver.get(o).get(0)[1] == f.yCoord+f.height)
								p.pointArchiver.get(o).set(1,new int[]{-1});
							else if(p.pointArchiver.get(o).get(0)[1] > f.yCoord && p.pointArchiver.get(o).get(0)[1] < f.yCoord+f.height)
							{
								if(p.pointArchiver.get(o).get(1)[0] == -1)
									p.pointArchiver.get(o).get(1)[0] = floors.indexOf(f);
								else
								{
									int[] a = p.pointArchiver.get(o).get(1);
									p.pointArchiver.get(o).set(1,new int[a.length+1]);
									p.pointArchiver.get(o).get(1)[0] = floors.indexOf(f);
									for(int b = 1; b < p.pointArchiver.get(o).get(1).length; b++)
										p.pointArchiver.get(o).get(1)[b] = a[b-1];
								}
								
								for(int[] w: f.walls[3])
								{
									if(p.pointArchiver.get(o).get(0)[1] >= w[0] && p.pointArchiver.get(o).get(0)[1] <= w[1])
										p.pointArchiver.get(o).set(1,new int[]{-1});
								}
							}
							
						/*	if(p.pointArchiver.get(o).get(1)[0] == -1 || p.pointArchiver.get(o).get(1).length > 1)
								foundLocation = true;*/
						}
					}
				}
				
				if(p.pointArchiver.get(o).get(1)[0] != -1/*foundLocation*/)
				{
					int blockCount = 0;
					for(Prop q: props)
					{
						if(props.indexOf(p) != props.indexOf(q))
						{
							if(p.pointArchiver.get(o).get(0)[0] >= q.bounds.xCoord && p.pointArchiver.get(o).get(0)[0] <= q.bounds.xCoord+q.bounds.width && p.pointArchiver.get(o).get(0)[1] >= q.bounds.yCoord && p.pointArchiver.get(o).get(0)[1] <= q.bounds.yCoord+q.bounds.height)
								blockCount++;
							if((p.pointArchiver.get(o).get(0)[0] == q.bounds.xCoord || p.pointArchiver.get(o).get(0)[0] == q.bounds.xCoord+q.bounds.width) && (p.pointArchiver.get(o).get(0)[1] == q.bounds.yCoord || p.pointArchiver.get(o).get(0)[1] == q.bounds.yCoord+q.bounds.height))
							{
								if(p.bounds.xCoord == q.bounds.xCoord || p.bounds.xCoord+p.bounds.width == q.bounds.xCoord+q.bounds.width || p.bounds.yCoord == q.bounds.yCoord || p.bounds.yCoord+p.bounds.height == q.bounds.yCoord+q.bounds.height)
									blockCount++;
							}
						}
					}
					
					if(blockCount > 1)
					{
						p.pointArchiver.get(o).set(1,new int[]{-1});
				//		foundLocation = false;
					}
				}
			}
		}
		
		for(Prop p1: props)
		{
			for(Prop p2: props)
			{
				if(p1 != p2)
				{
					if((p1.bounds.xCoord >= p2.bounds.xCoord && p1.bounds.xCoord <= p2.bounds.xCoord+p2.bounds.width) || (p1.bounds.xCoord+p1.bounds.width >= p2.bounds.xCoord && p1.bounds.xCoord+p1.bounds.width <= p2.bounds.xCoord+p2.bounds.width) || (p1.bounds.xCoord <= p2.bounds.xCoord && p1.bounds.xCoord+p1.bounds.width >= p2.bounds.xCoord+p2.bounds.width))
					{
						if((p1.bounds.yCoord >= p2.bounds.yCoord && p1.bounds.yCoord <= p2.bounds.yCoord+p2.bounds.height) || (p1.bounds.yCoord+p1.bounds.height >= p2.bounds.yCoord && p1.bounds.yCoord+p1.bounds.height <= p2.bounds.yCoord+p2.bounds.height) || (p1.bounds.yCoord <= p2.bounds.yCoord && p1.bounds.yCoord+p1.bounds.height >= p2.bounds.yCoord+p2.bounds.height))
							p1.touchArchiver.add(new int[]{1,p2.id});
					}
				}
			}
		}
		
		obsPolys = new ArrayList<ArrayList<Prop>>();
		ArrayList<Prop> oPolys = new ArrayList<Prop>();
		for(Prop p: props)
			oPolys.add(p);
		
		while(oPolys.size() > 0)
		{
			obsPolys.add(new ArrayList<Prop>());
			buildObsPolys(oPolys,0);
		}
		
		for(ArrayList<Prop> o1: obsPolys)
		{
			for(Prop p1: o1)
			{
				for(ArrayList<int[]> q1: p1.pointArchiver)
				{
					if(q1.get(1)[0] != -1)
					{
						double d = -1;
						int[] e = new int[2];
						
						for(Polygon m: mapPolys)
						{
							boolean isIntersecting = false;
							
							double x1 = q1.get(0)[0];
							double y1 = q1.get(0)[1];
							double x2 = m.xpoints[m.npoints];
							double y2 = m.ypoints[m.npoints];
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
							for(Prop p3: props)
							{
								if(l.intersects(p3.bounds.xCoord,p3.bounds.yCoord,p3.bounds.width,p3.bounds.height))
									isIntersecting = true;
							}
							
							if(!isIntersecting && (d == -1 || d > Math.sqrt(Math.pow(x1-x2,2)+Math.pow(y1-y2,2))))
							{
								d = Math.sqrt(Math.pow(x1-x2,2)+Math.pow(y1-y2,2));
								e = new int[]{0,mapPolys.indexOf(m),m.npoints};
							}
						}
						
						for(ArrayList<Prop> o2: obsPolys)
						{
							if(o1 != o2)
							{
								for(Prop p2: o2)
								{ 
									for(ArrayList<int[]> q2: p2.pointArchiver)
									{
										if(q2.get(1)[0] != -1)
										{
											boolean isIntersecting = false;
											
											double x1 = q1.get(0)[0];
											double y1 = q1.get(0)[1];
											double x2 = q2.get(0)[0];
											double y2 = q2.get(0)[1];
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
											for(Prop p3: props)
											{
												if(l.intersects(p3.bounds.xCoord,p3.bounds.yCoord,p3.bounds.width,p3.bounds.height))
													isIntersecting = true;
											}
											
											if(!isIntersecting)
											{
												if(d == -1 || d > Math.sqrt(Math.pow(x1-x2,2)+Math.pow(y1-y2,2)))
												{
													if(q2.size() < 2)
													{
														d = Math.sqrt(Math.pow(x1-x2,2)+Math.pow(y1-y2,2));
														e = new int[]{1,props.indexOf(p2),p2.pointArchiver.indexOf(q2)};
													//	e = new int[]{1,p2.id,p2.pointArchiver.indexOf(q2)};
													}
													else
													{
														boolean isNew = true;
														for(int f = 2; f < q2.size(); f++)
														{
															if(q2.get(f)[0] == p1.id && q2.get(f)[1] == p1.pointArchiver.indexOf(q1))
																isNew = false;
														}
														if(isNew)
														{
															d = Math.sqrt(Math.pow(x1-x2,2)+Math.pow(y1-y2,2));
															e = new int[]{1,props.indexOf(p2),p2.pointArchiver.indexOf(q2)};
														//	e = new int[]{1,p2.id,p2.pointArchiver.indexOf(q2)};
														}
													}
												}
											}
										}
									}
								}
							}
						}
						
						if(d != -1)
						{
							q1.add(e);
				//			props.get(e[0]).pointArchiver.get(e[2]).add(new int[]{p1.id,p1.pointArchiver.indexOf(q1)});
						}
						
						//TEST
					/*	System.out.println(">> "+p1.id+" "+p1.pointArchiver.indexOf(q1));
						for(int f = 2; f < q1.size(); f++)
							System.out.println("   "+q1.get(f)[0]+" "+q1.get(f)[1]);*/
						//==
					}
				}
			}
		}
		
		
		for(ArrayList<Prop> o: obsPolys)
		{
			for(Prop p: o)
			{
				for(ArrayList<int[]> q: p.pointArchiver)
				{
					for(int n = 2; n < q.size(); n++)
					{
						if(q.get(n)[0] == 0)
							navMesh.add(new double[]{q.get(0)[0],q.get(0)[1],mapPolys.get(q.get(n)[1]).xpoints[q.get(n)[2]],mapPolys.get(q.get(n)[1]).ypoints[q.get(n)[2]],1});
						else if(q.get(n)[0] == 1)
							navMesh.add(new double[]{q.get(0)[0],q.get(0)[1],props.get(q.get(n)[1]).pointArchiver.get(q.get(n)[2]).get(0)[0],props.get(q.get(n)[1]).pointArchiver.get(q.get(n)[2]).get(0)[1],1});
					}
				}
			}
		}
	}
	
	private void simplifyFloors()
	{
		boolean isSimplified = false;
		while(!isSimplified)
		{
			isSimplified = true;
			int fLimit = floors.size();
			for(int f1 = 0; f1 < fLimit; f1++)
			{
				for(int f2 = 0; f2 < fLimit; f2++)
				{
					if(f1 != f2 && floors.get(f1).type == floors.get(f2).type)
					{
						if(floors.get(f1).xCoord == floors.get(f2).xCoord+floors.get(f2).width || floors.get(f1).xCoord+floors.get(f1).width == floors.get(f2).xCoord)
						{
							if(floors.get(f1).height == floors.get(f2).height)
							{
								int x = floors.get(f1).xCoord;
								int y = floors.get(f1).yCoord;
								if(x > floors.get(f2).xCoord)
									x = floors.get(f2).xCoord;
								if(y > floors.get(f2).yCoord)
									y = floors.get(f2).yCoord;
								
								floors.add(new Floor(floors.get(f1).type,x,y,floors.get(f1).width+floors.get(f2).width,floors.get(f1).height));
								floors.remove(f1);
								if(f1 > 0)
									f1--;
								if(f2 > 0)
									f2--;
								floors.remove(f2);
								if(f1 > 0)
									f1--;
								if(f2 > 0)
									f2--;
								fLimit = floors.size();
								isSimplified = false;
							}
						}
						else if(floors.get(f1).yCoord == floors.get(f2).yCoord+floors.get(f2).height || floors.get(f1).yCoord+floors.get(f1).height == floors.get(f2).yCoord)
						{
							if(floors.get(f1).width == floors.get(f2).width)
							{
								int x = floors.get(f1).xCoord;
								int y = floors.get(f1).yCoord;
								if(x > floors.get(f2).xCoord)
									x = floors.get(f2).xCoord;
								if(y > floors.get(f2).yCoord)
									y = floors.get(f2).yCoord;
								
								floors.add(new Floor(floors.get(f1).type,x,y,floors.get(f1).width,floors.get(f1).height+floors.get(f2).height));
								floors.remove(f1);
								if(f1 > 0)
									f1--;
								if(f2 > 0)
									f2--;
								floors.remove(f2);
								if(f1 > 0)
									f1--;
								if(f2 > 0)
									f2--;
								fLimit = floors.size();
								isSimplified = false;
							}
						}
					}
				}
			}
		}
	}
	
	private void fusePolygons(ArrayList<Polygon> p, int p1, int p2, int[][] v)
	{
		Polygon p3 = new Polygon();
		int n = v[0][0];
		while(n != v[1][0])
		{
			p3.addPoint(p.get(p1).xpoints[n],p.get(p1).ypoints[n]);
			n += v[0][0]-v[1][0];
			if(n < 0)
				n += p.get(p1).npoints;
			if(n >= p.get(p1).npoints)
				n -= p.get(p1).npoints;
		}
		n = v[1][1];
		while(n != v[0][1])
		{
			p3.addPoint(p.get(p2).xpoints[n],p.get(p2).ypoints[n]);
			n += v[1][1]-v[0][1];
			if(n < 0)
				n += p.get(p2).npoints;
			if(n >= p.get(p2).npoints)
				n -= p.get(p2).npoints;
		}
		
		p.remove(p1);
		p.remove(p2);
		p.add(p3);
	}
	
	private void buildObsPolys(ArrayList<Prop> p, int n)
	{
		obsPolys.get(obsPolys.size()-1).add(p.get(n));
		for(int q = 0; q < props.size(); q++)
		{
			if(p.get(n).id == props.get(q).id)
			{
				if(props.get(props.indexOf(props.get(q))).touchArchiver.get(0)[0] != -1)
				{
					int oLimit = obsPolys.get(props.get(props.indexOf(props.get(q))).touchArchiver.get(0)[0]).size();
					for(int o = 0; o < oLimit; o++)
					{
						if(obsPolys.get(props.get(props.indexOf(props.get(q))).touchArchiver.get(0)[0]).get(o).id == p.get(n).id)
						{
							obsPolys.get(props.get(props.indexOf(props.get(q))).touchArchiver.get(0)[0]).remove(o);
							oLimit = obsPolys.get(props.get(props.indexOf(props.get(q))).touchArchiver.get(0)[0]).size();
							o--;
						}
					}
				}
				props.get(props.indexOf(props.get(q))).touchArchiver.get(0)[0] = obsPolys.size()-1;
			}
		}
		p.remove(n);
		if(n > 0)
			n--;
		
		for(int o = 1; o < obsPolys.get(obsPolys.size()-1).get(obsPolys.get(obsPolys.size()-1).size()-1).touchArchiver.size(); o++)
		{
			if(obsPolys.get(obsPolys.size()-1).get(obsPolys.get(obsPolys.size()-1).size()-1).touchArchiver.get(o)[0] == 1)
			{
				int t = -1;
				for(int q = 0; q < p.size(); q++)
				{
					if(obsPolys.get(obsPolys.size()-1).get(obsPolys.get(obsPolys.size()-1).size()-1).touchArchiver.get(o)[1] == p.get(q).id)
						t = q;
				}
				
				if(t != -1)
					buildObsPolys(p,t);
			}
		}
	}
	
	private ArrayList<double[]> getMapMesh()
	{
		//================
		//MAPMESH CREATION
		//================
		
		ArrayList<double[]> mapMesh = new ArrayList<double[]>();
		for(Floor f: floors)
		{
			for(int n = 0; n < 4; n++)
			{
				try
				{
					switch(n)
					{
						case 0:
							for(int[] w: f.walls[n])
								mapMesh.add(new double[]{w[0],f.yCoord,w[1],f.yCoord,floors.indexOf(f),-1});
							break;
						case 1:
							for(int[] w: f.walls[n])
								mapMesh.add(new double[]{f.xCoord+f.width,w[0],f.xCoord+f.width,w[1],floors.indexOf(f),-1});
							break;
						case 2:
							for(int[] w: f.walls[n])
								mapMesh.add(new double[]{w[0],f.yCoord+f.height,w[1],f.yCoord+f.height,floors.indexOf(f),-1});
							break;
						case 3:
							for(int[] w: f.walls[n])
								mapMesh.add(new double[]{f.xCoord,w[0],f.xCoord,w[1],floors.indexOf(f),-1});
							break;
					}
				}
				catch(java.lang.NullPointerException e)
				{
			//		System.out.println("HAH GAY");
					return getMapMesh();
				}
			}
		}
		
		//======================
		//MAPMESH SIMPLIFICATION
		//======================
		
		ArrayList<double[]> mMesh = new ArrayList<double[]>();
		for(int n = 0; n < mapMesh.size(); n++)
			mMesh.add(mapMesh.get(n));
		
		int oLimit = mapMesh.size();
		for(int o1 = 0; o1 < oLimit; o1++)
		{
			for(int o2 = 0; o2 < oLimit; o2++)
			{
				if(o1 != o2)
				{
					if(Line2D.linesIntersect(mMesh.get(o1)[0],mMesh.get(o1)[1],mMesh.get(o1)[2],mMesh.get(o1)[3],mMesh.get(o2)[0],mMesh.get(o2)[1],mMesh.get(o2)[2],mMesh.get(o2)[3]))
					{	
						if(mMesh.get(o1)[0] == mMesh.get(o1)[2] && mMesh.get(o2)[0] == mMesh.get(o2)[2] && mMesh.get(o1)[0] == mMesh.get(o2)[0])
						{
							Double[] points = new Double[4];
							points[0] = mMesh.get(o1)[1];
							points[1] = mMesh.get(o1)[3];
							points[2] = mMesh.get(o2)[1];
							points[3] = mMesh.get(o2)[3];
							
							int q = 0;
							ArrayList<double[]> sortedPoints = new ArrayList<double[]>();
							for(double p: points)
							{
								if(sortedPoints.isEmpty())
								{
									if(q == 0 || q == 1)
										sortedPoints.add(new double[]{p,1});
									else if(q == 2 || q == 3)
										sortedPoints.add(new double[]{p,2});
								}
								else
								{
									int s = 0;
									for(int n = 0; n < sortedPoints.size(); n++)
									{
										if(p > sortedPoints.get(n)[0])
											s++;
									}
									if(q == 0 || q == 1)
										sortedPoints.add(s,new double[]{p,1});
									else if(q == 2 || q == 3)
										sortedPoints.add(s,new double[]{p,2});
								}
								q++;
							}
							
							double x = mMesh.get(o1)[0];
							boolean connected = false;
							if(sortedPoints.get(1)[0] == sortedPoints.get(2)[0] && sortedPoints.get(1)[1] != sortedPoints.get(2)[1])
							{
								int n = 0;
								connected = true;
								for(int o3 = 0; o3 < oLimit; o3++)
								{
									if(mapMesh.get(o3) != mMesh.get(o1) && mapMesh.get(o3) != mMesh.get(o2))
									{
										if(mapMesh.get(o3)[1] == sortedPoints.get(1)[0] && mapMesh.get(o3)[3] == sortedPoints.get(2)[0] && (mapMesh.get(o3)[0] == x || mapMesh.get(o3)[2] == x))
											n++;
									}
								}
								if(n >= 2)
									connected = false;
							}
							
							if(connected)
							{
								mMesh.add(new double[]{x,sortedPoints.get(0)[0],x,sortedPoints.get(3)[0],mMesh.get(o1)[4],mMesh.get(o2)[4]});
								
								if(o1 > o2)
								{
									mMesh.remove(mMesh.get(o1));
									mMesh.remove(mMesh.get(o2));
								}
								else
								{
									mMesh.remove(mMesh.get(o2));
									mMesh.remove(mMesh.get(o1));
								}
								oLimit = mMesh.size();
								
								if(o1 > 0)
									o1--;
								if(o2 > 0)
									o2--;
							}
						}
						else if(mMesh.get(o1)[1] == mMesh.get(o1)[3] && mMesh.get(o2)[1] == mMesh.get(o2)[3] && mMesh.get(o1)[1] == mMesh.get(o2)[1])
						{
							Double[] points = new Double[4];
							points[0] = mMesh.get(o1)[0];
							points[1] = mMesh.get(o1)[2];
							points[2] = mMesh.get(o2)[0];
							points[3] = mMesh.get(o2)[2]; 
							
							int q = 0;
							ArrayList<double[]> sortedPoints = new ArrayList<double[]>();
							for(double p: points)
							{
								if(sortedPoints.isEmpty())
								{
									if(q == 0 || q == 1)
										sortedPoints.add(new double[]{p,1});
									else if(q == 2 || q == 3)
										sortedPoints.add(new double[]{p,2});
								}
								else
								{
									int s = 0;
									for(int n = 0; n < sortedPoints.size(); n++)
									{
										if(p > sortedPoints.get(n)[0])
											s++;
									}
									if(q == 0 || q == 1)
										sortedPoints.add(s,new double[]{p,1});
									else if(q == 2 || q == 3)
										sortedPoints.add(s,new double[]{p,2});
								}
								q++;
							}
							
							double y = mMesh.get(o1)[1];
							boolean connected = false;
							if(sortedPoints.get(1)[0] == sortedPoints.get(2)[0] && sortedPoints.get(1)[1] != sortedPoints.get(2)[1])
							{
								int n = 0;
								connected = true;
								for(int o3 = 0; o3 < mapMesh.size(); o3++)
								{
									if(mapMesh.get(o3) != mMesh.get(o1) && mapMesh.get(o3) != mMesh.get(o2))
									{
										if(mapMesh.get(o3)[0] == sortedPoints.get(1)[0] && mapMesh.get(o3)[2] == sortedPoints.get(2)[0] && (mapMesh.get(o3)[1] == y || mapMesh.get(o3)[3] == y))
											n++;
									}
								}
								if(n >= 2)
									connected = false;
							}
							
							if(connected)
							{
								mMesh.add(new double[]{sortedPoints.get(0)[0],y,sortedPoints.get(3)[0],y,mMesh.get(o1)[4],mMesh.get(o2)[4]});
								
								if(o1 > o2)
								{
									mMesh.remove(mMesh.get(o1));
									mMesh.remove(mMesh.get(o2));
								}
								else
								{
									mMesh.remove(mMesh.get(o2));
									mMesh.remove(mMesh.get(o1));
								}
								oLimit = mMesh.size();
								
								if(o1 > 0)
									o1--;
								if(o2 > 0)
									o2--;
							}
							
							/*		if(o1 > o2)
							{
								mMesh.remove(mMesh.get(o1));
								mMesh.remove(mMesh.get(o2));
							}
							else
							{
								mMesh.remove(mMesh.get(o2));
								mMesh.remove(mMesh.get(o1));
							}
							oLimit = mMesh.size();
							
							if(connected)
								mMesh.add(new double[]{sortedPoints.get(0)[0],y,sortedPoints.get(3)[0],y});
							else
							{
								mMesh.add(new double[]{sortedPoints.get(0)[0],y,sortedPoints.get(1)[0],y});
								mMesh.add(new double[]{sortedPoints.get(2)[0],y,sortedPoints.get(3)[0],y});
							}
							if(o1 > 0)
								o1--;
							if(o2 > 0)
								o2--;	*/
						}
					}
				}
			}
			mapMesh = mMesh;
		}
		mMesh = new ArrayList<double[]>();
		for(int n = 0; n < mapMesh.size(); n++)
			mMesh.add(mapMesh.get(n));
		oLimit = mMesh.size();
		return mapMesh;
	}
	
	private ArrayList<Polygon> getmapPolys(ArrayList<double[]> o, int l)
	{
		ArrayList<Polygon> mPolys = new ArrayList<Polygon>();
		ArrayList<double[]> mMesh = new ArrayList<double[]>();
		for(int n = 0; n < l; n++)
			mMesh.add(o.get(n));
		
		int q = 0;
		mPolys.add(new Polygon());
		mPolys.get(0).addPoint((int)mMesh.get(0)[0],(int)mMesh.get(0)[1]);
		double x1 = (int)mMesh.get(0)[0];
		double y1 = (int)mMesh.get(0)[1];
		double x2 = (int)mMesh.get(0)[2];
		double y2 = (int)mMesh.get(0)[3];
		mMesh.remove(mMesh.get(0));
		l = mMesh.size();
		
		mapArchiver.add(new ArrayList<int[]>());
		mapArchiver.get(0).add(new int[]{(int)mMesh.get(0)[4],(int)mMesh.get(0)[5]});
		
		while(l > 0)
		{
			while(x1 != x2 || y1 != y2)
			{
				for(int p1 = 0; p1 < l; p1++)
				{
					if(mMesh.get(p1)[0] == x2 && mMesh.get(p1)[1] == y2)
					{
						mPolys.get(q).addPoint((int)mMesh.get(p1)[0],(int)mMesh.get(p1)[1]);
						mapArchiver.get(mapArchiver.size()-1).add(new int[]{(int)mMesh.get(p1)[4],(int)mMesh.get(p1)[5]});
						
						x2 = mMesh.get(p1)[2];
						y2 = mMesh.get(p1)[3];
						mMesh.remove(mMesh.get(p1));
						l = mMesh.size();
						p1--;
					}
					else if(mMesh.get(p1)[2] == x2 && mMesh.get(p1)[3] == y2)
					{
						mPolys.get(q).addPoint((int)mMesh.get(p1)[2],(int)mMesh.get(p1)[3]);
						mapArchiver.get(mapArchiver.size()-1).add(new int[]{(int)mMesh.get(p1)[4],(int)mMesh.get(p1)[5]});
						
						x2 = mMesh.get(p1)[0];
						y2 = mMesh.get(p1)[1];
						mMesh.remove(mMesh.get(p1));
						l = mMesh.size();
						p1--;
					}
					
					for(int p2 = mPolys.get(q).npoints-2; p2 > 0; p2--)
					{
						if(mPolys.get(q).xpoints[p2] == mPolys.get(q).xpoints[mPolys.get(q).npoints-1] && mPolys.get(q).ypoints[p2] == mPolys.get(q).ypoints[mPolys.get(q).npoints-1])
						{
							mPolys.add(new Polygon());
							mapArchiver.add(new ArrayList<int[]>());
							for(int p3 = p2+1; p3 < mPolys.get(q).npoints; p3++)
							{
								mPolys.get(q+1).addPoint(mPolys.get(q).xpoints[p3],mPolys.get(q).ypoints[p3]);
								mapArchiver.get(q+1).add(new int[]{(int)mMesh.get(p3)[4],(int)mMesh.get(p3)[5]});
							}
							
							mPolys.add(new Polygon());
							mapArchiver.add(new ArrayList<int[]>());
							for(int p3 = 0; p3 <= p2; p3++)
							{
								mPolys.get(q+2).addPoint(mPolys.get(q).xpoints[p3],mPolys.get(q).ypoints[p3]);
								mapArchiver.get(q+2).add(new int[]{(int)mMesh.get(p3)[4],(int)mMesh.get(p3)[5]});
							}
							
							mPolys.remove(q);
							mapArchiver.remove(q);
							q++;
							p2 = 0;
						}
					}
				}
			}
			
			if(l > 0)
			{
				mPolys.add(new Polygon());
				mapArchiver.add(new ArrayList<int[]>());
				q++;
				
				mPolys.get(q).addPoint((int)mMesh.get(0)[0],(int)mMesh.get(0)[1]);
				x1 = mMesh.get(0)[0];
				y1 = mMesh.get(0)[1];
				x2 = mMesh.get(0)[2];
				y2 = mMesh.get(0)[3];
				mMesh.remove(mMesh.get(0));
				l = mMesh.size();
				
				mapArchiver.get(q).add(new int[]{(int)mMesh.get(0)[4],(int)mMesh.get(0)[5]});
			}
		}
		
		int[] p0 = new int[mPolys.size()];
		for(int p1 = 0; p1 < mPolys.size(); p1++)
		{
			for(int p2 = 0; p2 < mPolys.size(); p2++)
			{
				if(p1 != p2)
				{
					boolean containsPoly = true;
					for(int p3 = 0; p3 < mPolys.get(p2).npoints; p3++)
					{
						if(!mPolys.get(p1).contains(mPolys.get(p2).xpoints[p3],mPolys.get(p2).ypoints[p3]))
							containsPoly = false;
					}
					if(containsPoly)
						p0[p1]++;
				}
			}
		}
		
		q = 0;
		for(int p = 1; p < p0.length; p++)
		{
			if(p0[p] > p0[q])
				q = p;
		}
		mPolys.add(0,mPolys.get(q));
		mPolys.remove(q+1);
		mapArchiver.add(0,mapArchiver.get(q));
		mapArchiver.remove(q+1);
		return mPolys;
	}
	
	private ArrayList<ArrayList<Integer>> getAllPoints(ArrayList<Polygon> o1,/* ArrayList<double[]> o2,*/ ArrayList<Integer> a, int[][] q)
	{
		ArrayList<ArrayList<Integer>> points = new ArrayList<ArrayList<Integer>>();
		int o = 0;
		for(int o2 = 0; o2 < o1.size(); o2++)
		{
			o += o1.get(o2).npoints;
			for(int p = 0; p < o1.get(o2).npoints; p++)
			{
				points.add(new ArrayList<Integer>());
				points.get(points.size()-1).add(0);
				points.get(points.size()-1).add(-1);
				points.get(points.size()-1).add(-1);
				points.get(points.size()-1).add(o1.get(o2).xpoints[p]);
				points.get(points.size()-1).add(o1.get(o2).ypoints[p]);
			}
		}
		
		ArrayList<Integer> b = new ArrayList<Integer>();
		for(Prop p: props)
		{
			points.add(new ArrayList<Integer>());
			points.get(points.size()-1).add(1);
			points.get(points.size()-1).add(props.indexOf(p));
			points.get(points.size()-1).add(0);
			points.get(points.size()-1).add(p.bounds.xCoord);
			points.get(points.size()-1).add(p.bounds.yCoord);
			b.add(props.indexOf(p));
			
			points.add(new ArrayList<Integer>());
			points.get(points.size()-1).add(1);
			points.get(points.size()-1).add(props.indexOf(p));
			points.get(points.size()-1).add(1);
			points.get(points.size()-1).add(p.bounds.xCoord+p.bounds.width);
			points.get(points.size()-1).add(p.bounds.yCoord);
			b.add(props.indexOf(p));
			
			points.add(new ArrayList<Integer>());
			points.get(points.size()-1).add(1);
			points.get(points.size()-1).add(props.indexOf(p));
			points.get(points.size()-1).add(2);
			points.get(points.size()-1).add(p.bounds.xCoord);
			points.get(points.size()-1).add(p.bounds.yCoord+p.bounds.height);
			
			b.add(props.indexOf(p));
			
			points.add(new ArrayList<Integer>());
			points.get(points.size()-1).add(1);
			points.get(points.size()-1).add(props.indexOf(p));
			points.get(points.size()-1).add(3);
			points.get(points.size()-1).add(p.bounds.xCoord+p.bounds.width);
			points.get(points.size()-1).add(p.bounds.yCoord+p.bounds.height);
			b.add(props.indexOf(p));
			a.add(4);
		}
		for(Pleb p: plebs)
		{
			points.add(new ArrayList<Integer>());
			points.get(points.size()-1).add(2);
			points.get(points.size()-1).add(plebs.indexOf(p));
			points.get(points.size()-1).add(0);
			points.get(points.size()-1).add(p.xCoord);
			points.get(points.size()-1).add(p.yCoord);
			b.add(props.size()+plebs.indexOf(p));
			
			points.add(new ArrayList<Integer>());
			points.get(points.size()-1).add(2);
			points.get(points.size()-1).add(plebs.indexOf(p));
			points.get(points.size()-1).add(1);
			points.get(points.size()-1).add(p.xCoord+p.width);
			points.get(points.size()-1).add(p.yCoord);
			b.add(props.size()+plebs.indexOf(p));
			
			points.add(new ArrayList<Integer>());
			points.get(points.size()-1).add(2);
			points.get(points.size()-1).add(plebs.indexOf(p));
			points.get(points.size()-1).add(2);
			points.get(points.size()-1).add(p.xCoord);
			points.get(points.size()-1).add(p.yCoord+p.height);
			b.add(props.size()+plebs.indexOf(p));
			
			points.add(new ArrayList<Integer>());
			points.get(points.size()-1).add(2);
			points.get(points.size()-1).add(plebs.indexOf(p));
			points.get(points.size()-1).add(3);
			points.get(points.size()-1).add(p.xCoord+p.width);
			points.get(points.size()-1).add(p.yCoord+p.height);
			b.add(props.size()+plebs.indexOf(p));
			a.add(4);
		}
	/*	for(Puppet p: puppets)
		{
			points.add(new ArrayList<Integer>());
			points.get(points.size()-1).add(3);
			points.get(points.size()-1).add(puppets.indexOf(p));
			points.get(points.size()-1).add(0);
			points.get(points.size()-1).add(p.xCoord);
			points.get(points.size()-1).add(p.yCoord);
			b.add(props.size()+plebs.size()+puppets.indexOf(p));
			
			points.add(new ArrayList<Integer>());
			points.get(points.size()-1).add(3);
			points.get(points.size()-1).add(puppets.indexOf(p));
			points.get(points.size()-1).add(1);
			points.get(points.size()-1).add(p.xCoord+p.width);
			points.get(points.size()-1).add(p.yCoord);
			b.add(props.size()+plebs.size()+puppets.indexOf(p));
			
			points.add(new ArrayList<Integer>());
			points.get(points.size()-1).add(3);
			points.get(points.size()-1).add(puppets.indexOf(p));
			points.get(points.size()-1).add(2);
			points.get(points.size()-1).add(p.xCoord);
			points.get(points.size()-1).add(p.yCoord+p.height);
			b.add(props.size()+plebs.size()+puppets.indexOf(p));
			
			points.add(new ArrayList<Integer>());
			points.get(points.size()-1).add(3);
			points.get(points.size()-1).add(puppets.indexOf(p));
			points.get(points.size()-1).add(3);
			points.get(points.size()-1).add(p.xCoord+p.width);
			points.get(points.size()-1).add(p.yCoord+p.height);
			b.add(props.size()+plebs.size()+puppets.indexOf(p));
			a.add(4);
		}	*/
		q[0][0] = props.size();
		q[0][1] = plebs.size();
		q[0][2] = puppets.size();
		q[1][0] = props.size()*4;
		q[1][1] = plebs.size()*4;
		q[1][2] = puppets.size()*4;
		
		int pLimit = points.size();
		for(int p1 = o; p1 < pLimit; p1++)
		{
			for(int p2 = o; p2 < pLimit; p2++)
			{
				if(p1 != p2 && p1 > o && p2 > o)
				{
					if((int)points.get(p1).get(3) == (int)points.get(p2).get(3) && (int)points.get(p1).get(4) == (int)points.get(p2).get(4))
					{
						if(b.get(p1-o) != b.get(p2-o))
						{
							int a3 = b.get(p1-o);
							int a4 = b.get(p2-o);
							
							if(points.get(p1).get(0) != 3)
							{
								a.set(a3,a.get(a3)-1);
								if(a.get(a3) <= 0)
								{
									a.remove(a3);
									q[0][points.get(p1).get(0)-1]--;
									for(int c = a3; c < b.size(); c++)
										b.set(c,b.get(c)-1);
									if(a4 > a3)
										a4--;
								}
							}
							
							if(points.get(p2).get(0) != 3)
							{
							a.set(a4,a.get(a4)-1);
								if(a.get(a4) <= 0)
								{
									a.remove(a4);
									q[0][points.get(p2).get(0)-1]--;
									for(int c = a4; c < b.size(); c++)
										b.set(c,b.get(c)-1);
								}
							}
							
							if(p1 > p2)
							{
								int r1 = points.get(p1).get(0)-1;
								int r2 = points.get(p2).get(0)-1;
								
								if(points.get(p1).get(0) != 3)
								{
								points.remove(p1);
								b.remove(p1-o);
								q[1][r1]--;
								}
								
								if(points.get(p2).get(0) != 3)
								{
								points.remove(p2);
								b.remove(p2-o);
								q[1][r2]--;
								}
							}
							else
							{
								int r1 = points.get(p1).get(0)-1;
								int r2 = points.get(p2).get(0)-1;
								
								if(points.get(p2).get(0) != 3)
								{
									points.remove(p2);
									b.remove(p2-o);
									q[1][r2]--;
								}
								
								if(points.get(p1).get(0) != 3)
								{
									points.remove(p1);
									b.remove(p1-o);
									q[1][r1]--;
								}
							}
							
							pLimit = points.size();
							if(p1 < pLimit && p2 < pLimit)
							{
								if(points.get(p1).get(0) != 3)
								{
									p1--;
									if(p1 < 0)
										p1 = 0;
								}
								if(points.get(p2).get(0) != 3)
									p2 = 0;
							}
						}
					}
				}
			}
		}
	/*	for(int p1 = 0; p1 < points.size(); p1++)
		{
			if(points.get(p1).get(0) > 0)
			{
				int[][]p2 = new int[][]{{points.get(p1).get(1),points.get(p1).get(2)},{points.get(p1+1).get(1),points.get(p1+1).get(2)},{points.get(p1+2).get(1),points.get(p1+2).get(2)},{points.get(p1+3).get(1),points.get(p1+3).get(2)}};			
				for(double[] p: o2)
				{
					double x1 = p[0];
					double y1 = p[1];
					double x2 = p[2];
					double y2 = p[3];
					if(x1 > x2)
					{
						x1 = p[2];
						x2 = p[0];
					}
					if(y1 > y2)
					{
						y1 = p[3];
						y2 = p[1];
					}
					
					for(int q = 0; q < 4; q++)
					{
						if((p2[q][0] >= x1 && p2[q][0] <= x2) && (p2[q][1] >= y1 && p2[q][1] <= y2))
						{
							points.get(p1+q).add(3,p2[q][0]);
							points.get(p1+q).add(4,p2[q][1]);
						}
					}
				}
				
				for(Prop p: props)
				{
					double x1 = p.bounds.xCoord;
					double y1 = p.bounds.yCoord;
					double x2 = p.bounds.xCoord+p.bounds.width;
					double y2 = p.bounds.yCoord+p.bounds.height;
					int q = 0;
					if(p2[q][0] != x1 && p2[q+1][0] != x2 && p2[q][1] != y1 && p2[q+2][1] != y2)
					{
						while(q < 4)
						{
							if((p2[q][0] >= x1 && p2[q][0] <= x2) && (p2[q][1] >= y1 && p2[q][1] <= y2))
							{
								points.get(p1+q).add(3,p2[q][0]);
								points.get(p1+q).add(4,p2[q][1]);
							}
							q++;
						}
					}
				}
				
				for(Pleb p: plebs)
				{
					double x1 = p.xCoord;
					double y1 = p.yCoord;
					double x2 = p.xCoord+p.width;
					double y2 = p.yCoord+p.height;
					int q = 0;
					if(p2[q][0] != x1 && p2[q+1][0] != x2 && p2[q][1] != y1 && p2[q+2][1] != y2)
					{
						while(q < 4)
						{
							if((p2[q][0] >= x1 && p2[q][0] <= x2) && (p2[q][1] >= y1 && p2[q][1] <= y2))
							{
								points.get(p1+q).add(3,p2[q][0]);
								points.get(p1+q).add(4,p2[q][1]);
							}
							q++;
						}
					}
				}
				
				for(Puppet p: puppets)
				{
					double x1 = p.xCoord;
					double y1 = p.yCoord-p.height;
					double x2 = p.xCoord+p.width;
					double y2 = p.yCoord+p.height/4;
					int q = 0;
					if(p2[q][0] != x1 && p2[q+1][0] != x2 && p2[q][1] != y1 && p2[q+2][1] != y2)
					{
						while(q < 4)
						{
							if((p2[q][0] >= x1 && p2[q][0] <= x2) && (p2[q][1] >= y1 && p2[q][1] <= y2))
							{
								points.get(p1+q).add(3,p2[q][0]);
								points.get(p1+q).add(4,p2[q][1]);
							}
							q++;
						}
					}
				}	
				p1+=3;
			}
		}*/
		return points;
	}
}