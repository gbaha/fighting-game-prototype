import java.awt.Color;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.image.ImageObserver;
import java.util.ArrayList;

public class Floor
{
	Hitbox[] cornered;
	String type;
	int[][][] walls;
	int xCoord, yCoord, xHosh, yHosh, width, height;
	
	public Floor(String t, int x, int y, int w, int h)
	{
		cornered = new Hitbox[2];
		type = t;
		
		walls = new int[4][][];
		walls[0] = new int[][]{};
		walls[1] = new int[][]{};
		walls[2] = new int[][]{};
		walls[3] = new int[][]{};
		
		xCoord = x;
		yCoord = y;
		xHosh = xCoord;
		yHosh = yCoord;
		width = w;
		height = h;
	}
	
	public void draw(Graphics g, ImageObserver i, double w, double h, boolean d)
	{
	//	g.drawImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resources/fetusofgrah.jpg")),(int)((xHosh-50)*w/1280),(int)((yHosh+4400)*h/720),(int)((width+100)*w/1280),(int)((height/5)*h/720),i);
		if(d)
		{
			g.setColor(new Color(255,255,255,35));
			g.fillRect((int)((xHosh-50)*w/1280),(int)((yHosh+400)*h/720),(int)((width+100)*w/1280),(int)(height*h/720));
			
			g.setColor(Color.LIGHT_GRAY);
			for(int y = 0; y < height; y += 200)
			{
				for(int x = 0; x < width; x += 200)
					g.drawRect((int)((xHosh+x)*w/1280),(int)((yHosh+y)*h/720),(int)(200*w/1280),(int)(200*h/720));
			}
		}
		
		//TEST
		g.setColor(Color.DARK_GRAY);
		g.drawRect((int)(xHosh*w/1280),(int)(yHosh*h/720),(int)(width*w/1280),(int)(height*h/720));
	}
	
	public void update(ArrayList<Floor> f)
	{
		ArrayList<ArrayList<Integer>> linkedFloors = new ArrayList<ArrayList<Integer>>();
		for(int n = 0; n < 4; n++)
			linkedFloors.add(new ArrayList<Integer>());
		for(Floor w: f)
		{
			if(yCoord == w.yCoord+w.height)
				linkedFloors.get(0).add(f.indexOf(w));
			if(xCoord+width == w.xCoord)
				linkedFloors.get(1).add(f.indexOf(w));
			if(yCoord+height == w.yCoord)
				linkedFloors.get(2).add(f.indexOf(w));
			if(xCoord == w.xCoord+w.width)
				linkedFloors.get(3).add(f.indexOf(w));
		}
		
		ArrayList<ArrayList<Integer>> sortedFloors = new ArrayList<ArrayList<Integer>>();
		for(int n = 0; n < 4; n++)
			sortedFloors.add(new ArrayList<Integer>());
		for(int n = 0; n < 4; n++)
		{
			for(int l: linkedFloors.get(n))
			{
				if(sortedFloors.get(n).isEmpty())
					sortedFloors.get(n).add(l);
				else
				{
					int s = 0;
					for(int w = 0; w < sortedFloors.get(n).size(); w++)
					{
						if(f.get(l).xCoord > f.get(sortedFloors.get(n).get(w)).xCoord && (n == 0 || n == 2))
							s++;
						if(f.get(l).yCoord > f.get(sortedFloors.get(n).get(w)).yCoord && (n == 1 || n == 3))
							s++;
					}
					sortedFloors.get(n).add(s,l);
				}
				
				if(n == 0 || n == 2)
				{
					if(sortedFloors.get(n).indexOf(l) == 0 && f.get(sortedFloors.get(n).get(0)).xCoord+f.get(sortedFloors.get(n).get(0)).width < xCoord+1)
						sortedFloors.get(n).remove(0);
					if(!sortedFloors.get(n).isEmpty())
					{
						if(sortedFloors.get(n).indexOf(l) == sortedFloors.get(n).size()-1 && f.get(sortedFloors.get(n).get(sortedFloors.get(n).size()-1)).xCoord > xCoord+width-1)
							sortedFloors.get(n).remove(sortedFloors.get(n).size()-1);
					}
				}
				else if(n == 1 || n == 3)
				{
					if(sortedFloors.get(n).indexOf(l) == 0 && f.get(sortedFloors.get(n).get(0)).yCoord+f.get(sortedFloors.get(n).get(0)).height < yCoord+1)
						sortedFloors.get(n).remove(0);
					if(!sortedFloors.get(n).isEmpty())
					{
						if(sortedFloors.get(n).indexOf(l) == sortedFloors.get(n).size()-1 && f.get(sortedFloors.get(n).get(sortedFloors.get(n).size()-1)).yCoord > yCoord+height-1)
							sortedFloors.get(n).remove(sortedFloors.get(n).size()-1);
					}
				}
			}
		}
		
		ArrayList<ArrayList<int[]>> w0 = new ArrayList<ArrayList<int[]>>();
		for(int w1 = 0; w1 < walls.length; w1++)
		{
			w0.add(new ArrayList<int[]>());
			if(w1 == 0 || w1 == 2)
			{
				if(sortedFloors.get(w1).isEmpty())
				{
					walls[w1] = new int[1][];
					walls[w1][0] = new int[]{xCoord,xCoord+width};
				}
				else
				{
					int x1 = xCoord;
					int x2 = f.get(sortedFloors.get(w1).get(0)).xCoord;
					for(int w2 = 0; x2 < xCoord+width; w2++)
					{
						if(w2 > 0)
							x1 = f.get(sortedFloors.get(w1).get(w2-1)).xCoord+f.get(sortedFloors.get(w1).get(w2-1)).width;
						if(w2 >= sortedFloors.get(w1).size())
							x2 = xCoord+width;
						else
							x2 = f.get(sortedFloors.get(w1).get(w2)).xCoord;
						
						if(x1 <= xCoord+width && x2 >= xCoord && x1 != x2)
							w0.get(w1).add(new int[]{x1,x2});
					}
					
					//TEST
				/*	if(w1 == 0 && !sortedFloors.get(w1).isEmpty())
					{
					for(int w2 = 0; w2 < w0.get(w1).size(); w2++)
						System.out.println("^"+w2+"("+w0.get(w1).size()+")   "+(yCoord)+"("+xCoord+" "+(xCoord+width)+") "+w0.get(w1).get(w2)[0]+" "+w0.get(w1).get(w2)[1]);
					System.out.println();
					}
					if(w1 == 2 && !sortedFloors.get(w1).isEmpty())
					{
					for(int w2 = 0; w2 < w0.get(w1).size(); w2++)
						System.out.println("v"+w2+"("+w0.get(w1).size()+")   "+(yCoord+height)+"("+xCoord+" "+(xCoord+width)+") "+w0.get(w1).get(w2)[0]+" "+w0.get(w1).get(w2)[1]);
					System.out.println();
					}
					//END OF RINE
					//-----------RINE ENDS HERE	*/
					
					
					walls[w1] = new int[w0.get(w1).size()][];
					for(int w2 = 0; w2 < w0.get(w1).size(); w2++)
						walls[w1][w2] = w0.get(w1).get(w2);
				}
			}
			else if(w1 == 1 || w1 == 3)
			{
				if(sortedFloors.get(w1).isEmpty())
				{
					walls[w1] = new int[1][];
					walls[w1][0] = new int[] {yCoord,yCoord+height};
				}
				else
				{
					int y1 = yCoord;
					int y2 = f.get(sortedFloors.get(w1).get(0)).yCoord;
					for(int w2 = 0; y2 < yCoord+height; w2++)
					{
						if(w2 > 0)
							y1 = f.get(sortedFloors.get(w1).get(w2-1)).yCoord+f.get(sortedFloors.get(w1).get(w2-1)).height;
						if(w2 >= sortedFloors.get(w1).size())
							y2 = yCoord+height;
						else
							y2 = f.get(sortedFloors.get(w1).get(w2)).yCoord;
						
						if(y1 <= yCoord+height && y2 >= yCoord && y1 != y2)
							w0.get(w1).add(new int[]{y1,y2});
					}
					
					walls[w1] = new int[w0.get(w1).size()][];
					for(int w2 = 0; w2 < w0.get(w1).size(); w2++)
						walls[w1][w2] = w0.get(w1).get(w2);
				}
			}
		}
	}
}