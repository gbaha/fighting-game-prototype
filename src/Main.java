import java.awt.Dimension;
import javax.swing.JFrame;

public class Main
{
	JFrame window;
	Stage stage;
//	Curtains curtains;
	Beaman geebs;
	Director director;
	Hand p1, p2;
	Gui gui;
	Logic logic;
	Hoshua jas;
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
		
		stage = new Stage();
		geebs = new Beaman(stage);
		director = new Director(stage,geebs);
		p1 = new Hand(xCoord,yCoord,width,height,new int[]{87,68,83,65},new int[]{73,79,80,74,75,76,91,59,10,27});
		p2 = new Hand(xCoord,yCoord,width,height,new int[]{38,39,40,37},new int[]{999,999,999,999,999,999,999,999,999,8});
		gui = new Gui(p1,p2,gamePaused);
		
		if(stage.player1 != null)
			p1.player = stage.player1;
		if(stage.player2 != null)
			p2.player = stage.player2;
		
		logic = new Logic(stage,p1,p2,xCoord,yCoord,gamePaused/*,w,h*/);
		jas = new Hoshua(stage,gui,xCoord,yCoord+window.getInsets().top,w,h,fps,gamePaused,debugging);
	}
	
	public void run()
	{
		logic.setFocusTo(1000,800);
		while(0 < 1)	//TEST -change to while game is running
		{
			double start = System.currentTimeMillis();
			xCoord = window.getLocation().x;
			yCoord = window.getLocation().y;
			
			
			int fpsLimit = 60;	//test
			if(p1.buttonArchiver[8])
			{
				//RESET TEST
		/*		logic.setFocusTo(1000,800);
				stage.player1.reset(1000-200-100,750);
				stage.player2.reset(1000+200,750);
				while(p1.stickInputs.size() > 0)
					p1.stickInputs.remove();
				while(p2.stickInputs.size() > 0)
					p2.stickInputs.remove();*/
					
				//FRAME BY FRAME TEST
				fpsLimit = 1;
			}
			//==
			
			
			director.update();
			gui.update(width,height,gamePaused);
			logic.update(xCoord,yCoord,gamePaused/*,width,height*/);
			gamePaused = logic.gamePaused;
			
			geebs.defyLogic();
			jas.update(xCoord,yCoord+window.getInsets().top,width,height,fps,gamePaused,debugging);
			
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
   	// 	Main show = new Main(1280,720);
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