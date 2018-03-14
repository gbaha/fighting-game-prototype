import java.util.LinkedList;

public class Director
{
	public static final int ROUNDSTART = 0;
	public static final int ROUNDEND = 1;
	
	LinkedList<Script> scriptQueue;
	LinkedList<String> sounds;
	Script[] scriptList;
	Stage stage;
	Hoshua jas;
	Script currScript;
	int fCounter;
	boolean handLock;
	
	public Director(Stage s, Hoshua j)
	{
		scriptQueue = new LinkedList<Script>();
		sounds = new LinkedList<String>();
		stage = s;
		jas = j;
		fCounter = 0;
		
		scriptList = new Script[]
		{
			new RoundStart(stage),
			new RoundEnd(stage)
		};
	}
	
	public void direct()
	{
		handLock = false;
		if(currScript == null && scriptQueue.size() > 0)
		{
			currScript = scriptQueue.removeFirst();
			fCounter = 0;
		}
		if(currScript != null)
		{
			currScript.perform(fCounter);
			handLock = currScript.isLocked;
			fCounter++;
			
			if(fCounter >= currScript.length)
				currScript = null;
		}
	}
	
	public void addScript(int l)
	{
		scriptQueue.addLast(scriptList[l]);
	}
	
	
	public class RoundStart extends Script
	{
		public RoundStart(Stage s)
		{
			super(s,90,true);
		}
		
		public void perform(int f)
		{
			switch(f)
			{
				case 0:
					jas.gui.displaySplash("ROUND "+(stage.wins[0][0]+stage.wins[1][0]+1),Gui.CENTER,640,200,80,90,80);
					sounds.add("roundstart1"+(char)(Math.random()*2+97)+".wav");
					break;
					
				case 60:
					jas.gui.displaySplash("FIGHT",Gui.CENTER,640,300,100,30,20);
					sounds.add("roundstart2"+(char)(Math.random()*2+97)+".wav");
					break;
			}
		}
	}
	
	public class RoundEnd extends Script
	{
		public RoundEnd(Stage s)
		{
			super(s,240,false);
		}
		
		public void perform(int f)
		{
			switch(f)
			{
				case 0:
					jas.gui.displaySplash("KO",Gui.CENTER,640,360,200,190,180);
					sounds.add("roundfinish"+(int)(Math.random()*2+1)+".wav");
					break;
					
				case 180:
					if(stage.player1.health == 0 && stage.player2.health > 0)
					{
						stage.player2.currAction = stage.player2.actions[9];
						stage.player2.fCounter = 0;
						stage.player2.sIndex = stage.player2.hitboxArchiver.get(stage.player2.currState.getPosition())[0][1];
					}
					else if(stage.player2.health == 0 && stage.player1.health > 0)
					{
						stage.player1.currAction = stage.player1.actions[9];
						stage.player1.fCounter = 0;
						stage.player1.sIndex = stage.player1.hitboxArchiver.get(stage.player1.currState.getPosition())[0][1];
					}
			}
		}
	}
}