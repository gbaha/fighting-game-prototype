import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class Stage
{
	public static final int TRAINING = 0;
	public static final int VERSUS = 1;
	
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
	boolean[] openNav, settings;
	int[] timer;
	int[][] wins;
	Player player1, player2;
	int type, rounds, rCounter, xFocus, yFocus;
	double xZoom, yZoom;
	boolean isResetting, zOverride;
	
	public Stage(int t, int r)
	{
		type = t;
		rounds = r;
		rCounter = 0;
		xFocus = 0;
		yFocus = 0;
		xZoom = 1;
		yZoom = 1;
		isResetting = false;
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
		
		switch(type)
		{
			case TRAINING:
				settings = new boolean[]{true,true,true};	//[hitboxes, debugging, inputs]
				break;
				
			case VERSUS:
				settings = new boolean[]{false,false,false};	//[hitboxes, debugging, inputs]
				break;
		}
		timer = new int[]{99,100};
		wins = new int[2][r+1];
		
		//TEST
		floors.add(new Floor("",0,0,2000,5000));
		player1 = new Roo(1000-200-100,4750,true);
		player2 = new Roo(1000+200,4750,false);
		puppets.add(player1);
		puppets.add(player2);
		player1.target = player2;
		player2.target = player1;
		
		
		
		player2.sTint.set(0,new double[]{100,100,255,255});
		//END OF LINE
		//-----------
		//LINE ENDS HERE
		
		for(Puppet p: puppets)
			p.id = puppets.indexOf(p);
		for(Prop p: props)
			p.id = props.indexOf(p);
		for(Floor f: floors)
			f.update(floors);
//		buildFairyTrail();
	}
	
	public void reset(Director d, Hand h1, Hand h2)
	{
		timer = new int[]{99,100};
		puppets.clear();
		props.clear();
		plebs.clear();
		
		player1 = new Roo(1000-200-100,4750,true);
		player2 = new Roo(1000+200,4750,false);
		puppets.add(player1);
		puppets.add(player2);
		player1.target = player2;
		player2.target = player1;
		player2.sTint.set(0,new double[]{100,100,255,255});
		
		for(Puppet p: puppets)
			p.id = puppets.indexOf(p);
		for(Prop p: props)
			p.id = props.indexOf(p);
		
		h1.buttonInputs.clear();
		h2.buttonInputs.clear();
		h1.stickInputs.clear();
		h2.stickInputs.clear();
		h1.player = player1;
		h2.player = player2;
		isResetting = true;
		
		if(type == VERSUS)
			d.addScript(Director.ROUNDSTART);
		
		//TEST
		for(int i = 0; i < 3; i++)
			System.out.println();
	}
	
	public void update(Director d, Hand h1, Hand h2, double hd[])
	{
		switch(type)
		{
			case TRAINING:
				timer = new int[]{99,100};
				wins = new int[2][rounds+1];
				
				if(player1.health < player1.maxHp && hd[0] == player1.health)
					player1.health = player1.maxHp;
				if(player2.health < player2.maxHp && hd[1] == player2.health)
					player2.health = player2.maxHp;
				break;
				
			case VERSUS:
				if((wins[0][0] < rounds && wins[1][0] < rounds) || (wins[0][0] == rounds && wins[1][0] == rounds))
				{
					if(timer[0] == 0)
					{
						if(player1.health >= player2.health)
						{
							wins[0][wins[0][0]+1] = 2;
							wins[0][0]++;
						}
						if(player1.health <= player2.health)
						{
							wins[1][wins[1][0]+1] = 2;
							wins[1][0]++;
						}
						if(wins[0][0] < rounds && wins[1][0] < rounds)
							reset(d,h1,h2);
					}
					else if(player1.health <= 0)
					{
						player1.kdCounter = 0;
						if(wins[1][wins[1][0]+1] == 0)
						{
							d.addScript(Director.ROUNDEND);
							wins[1][wins[1][0]+1] = 1;
						}
						if(d.currScript == null && d.scriptQueue.size() == 0)
						{
							wins[1][0]++;
							if(wins[1][0] < rounds)
								reset(d,h1,h2);
						}
					}
					else if(player2.health <= 0)
					{
						player2.kdCounter = 0;
						if(wins[0][wins[0][0]+1] == 0)
						{
							d.addScript(Director.ROUNDEND);
							wins[0][wins[0][0]+1] = 1;
						}
						if(d.currScript == null && d.scriptQueue.size() == 0)
						{
							wins[0][0]++;
							if(wins[0][0] < rounds)
								reset(d,h1,h2);
						}
					}
					
					if(timer[1] > 0)
						timer[1]--;
					else if(timer[0] > 0)
					{
						timer[0]--;
						timer[1] = 100;
					}
				}
				else
				{
				//	h1.player = null;
					h2.player = null;
				//	h1.buttonInputs.clear();
					h2.buttonInputs.clear();
				//	h1.stickInputs.clear();
					h2.stickInputs.clear();
				}
				break;
		}
	}
}