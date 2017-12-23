import java.awt.Dimension;
import javax.swing.JFrame;

public class Main
{
	JFrame window;
	Stage stage;
//	Curtains curtains;
//	Beaman geebs;
	Director director;
	Hand p1, p2;
	Gui gui;
	Logic logic;
	Hoshua jas;
	Klamoth klam;
	int xCoord, yCoord, width, height;
	double fps;
	boolean gamePaused, debugging;
	
	public Main(int w, int h)
	{
		window = new JFrame("FUCK A*");
	//	window.setBounds(java.awt.Toolkit.getDefaultToolkit().getScreenSize().width/2-w/2,java.awt.Toolkit.getDefaultToolkit().getScreenSize().height/2-h/2,w,h);
		window.setLocation(java.awt.Toolkit.getDefaultToolkit().getScreenSize().width-w,0);
		window.getContentPane().setPreferredSize(new Dimension(w,h));
		
		xCoord = window.getLocation().x;
		yCoord = window.getLocation().y;
		width = w;
		height = h;
		gamePaused = false;	//Change later
		debugging = false;
		
		stage = new Stage(Stage.TRAINING,2);
//		geebs = new Beaman(stage);
		p1 = new Hand(xCoord,yCoord,width,height,new int[]{87,68,83,65},new int[]{73,79,80,74,75,76,91,59,32,49,50,51,52});
		p2 = new Hand(xCoord,yCoord,width,height,new int[]{38,39,40,37},new int[]{90,88,67,86,66,78,999,999,999,8,999,999,999});
		gui = new Gui(p1,p2,gamePaused);
		
		if(stage.player1 != null)
			p1.player = stage.player1;
		if(stage.player2 != null)
			p2.player = stage.player2;
		
		logic = new Logic(stage,p1,p2,xCoord,yCoord,gamePaused/*,w,h*/);
		jas = new Hoshua(stage,gui,xCoord,yCoord+window.getInsets().top,w,h,fps,6,gamePaused);
		director = new Director(stage,jas);
		klam = new Klamoth();
	}
	
	public void run()
	{
		logic.setFocusTo(1000,4750);
		stage.xFocus = -360;
		
		while(0 < 1)	//TEST -change to while game is running
		{
			double start = System.currentTimeMillis();
			xCoord = window.getLocation().x;
			yCoord = window.getLocation().y;
			
			double fpsLimit = 60;	//test
			if(p1.buttonArchiver[8])
				fpsLimit = 3;
			if(p1.buttonArchiver[9])
			{
				stage.type = (stage.type == Stage.TRAINING)? Stage.VERSUS:Stage.TRAINING;
				stage.settings = (stage.type == Stage.TRAINING)? new boolean[]{true,true,true}:new boolean[]{false,false,false};
				stage.reset(director,p1,p2);
				p1.buttonArchiver[9] = false;
			}
			if(p1.buttonArchiver[10])
			{
				stage.settings[0] = !stage.settings[0];
				p1.buttonArchiver[10] = false;
			}
			if(p1.buttonArchiver[11])
			{
				stage.settings[1] = !stage.settings[1];
				p1.buttonArchiver[11] = false;
			}
			if(p1.buttonArchiver[12])
			{
				stage.settings[2] = !stage.settings[2];
				p1.buttonArchiver[12] = false;
			}
			if(stage.isResetting)
			{
				logic.setFocusTo(1000,4750);
				stage.xFocus = -360;
				stage.yFocus = -4350;
				stage.isResetting = false;
			}
			
			stage.update(director,p1,p2,gui.hDamage);
			director.direct();
			klam.buildQueue(stage,director);
			gui.update(width,height,gamePaused);
			logic.update(director,xCoord,yCoord,gamePaused/*,width,height*/);
			gamePaused = logic.gamePaused;
			
		//	geebs.defyLogic();
			jas.update(xCoord,yCoord+window.getInsets().top,width,height,logic.slip[0],fps,gamePaused);
			klam.play();
			
			double end = System.currentTimeMillis();
			fps = 1000.0/(end-start);
			try
			{
				while(fps > fpsLimit)	//60)
				{
					Thread.sleep(1);
					end = System.currentTimeMillis();
					fps = 1000.0/(end-start);
				}
			}
			catch(java.lang.InterruptedException e){}
		}
	}
	
	public static void main(String[] args)
	{
	//	Main show = new Main(1280,720);
     	Main show = new Main(800,450);
     	show.window.getContentPane().add(show.jas);
   	 	show.window.pack();
   	 	show.window.setSize(new Dimension(show.width+show.window.getInsets().right-4,show.height+show.window.getInsets().top-2));
   	 	
   	 	show.window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	show.window.setResizable(false);
    	show.window.setVisible(true);
    	show.window.addKeyListener(show.p1);
    	show.window.addKeyListener(show.p2);
    	show.run();
	}
}