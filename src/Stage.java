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
		floors.add(new Floor("",0,0,2000,5000));
		player1 = new Roo(1000-200-100,4750,true);
		player2 = new Roo(1000+200,4750,false);
		puppets.add(player1);
		puppets.add(player2);
		player1.target = player2.bounds;
		player2.target = player1.bounds;
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
}