import java.awt.MouseInfo;
import java.awt.event.*;
import java.util.LinkedList;

public class Hand implements KeyListener	//, MouseListener
{
	Player player;
	LinkedList<int[]> stickInputs, buttonInputs;
	LinkedList<Boolean> inputOrder;
	int[] stickBindings, buttonBindings;
	boolean[] stickArchiver, buttonArchiver, buttonHeld;
	double winWidth, winHeight;
	double xMouse, yMouse;
	
	int fTimer;
	boolean gamePaused, debugging;
	
	public Hand()
	{
		stickInputs = new LinkedList<int[]>();
		buttonInputs = new LinkedList<int[]>();
		inputOrder = new LinkedList<Boolean>();
		stickBindings = new int[4];
		buttonBindings = new int[10];
		stickArchiver = new boolean[4];
		buttonArchiver = new boolean[10];
		buttonHeld = new boolean[8];
		
		xMouse = 0;
		yMouse = 0;
		winWidth = 0;
		winHeight = 0;
		
		fTimer = 0;
/*		dtapArchiver = -1;
		dtapCheck = new long[2];
		dtapCheck[0] = -1;
		dtapCheck[1] = -1;*/
	}
	
	public Hand(/*Player p,*/ int x, int y, int w, int h, int[] s, int[] b)
	{
	//	player = p;
		stickInputs = new LinkedList<int[]>();
		buttonInputs = new LinkedList<int[]>();
		inputOrder = new LinkedList<Boolean>();
		stickBindings = s;
		buttonBindings = b;
		stickArchiver = new boolean[4];
		buttonArchiver = new boolean[10];
		buttonHeld = new boolean[8];
		
		xMouse = MouseInfo.getPointerInfo().getLocation().x-x;
		yMouse = MouseInfo.getPointerInfo().getLocation().y-y;
		winWidth = w;
		winHeight = h;
		
		fTimer = 0;
/*		dtapArchiver = -1;
		dtapCheck = new long[2];
		dtapCheck[0] = -1;
		dtapCheck[1] = -1;*/
	}
	
	public void pullStrings(int x, int y)
	{
		if(player != null)
		{
			player.isCrouching = false;
			player.isDashing = false;
			for(Force f: player.bounds.forceArchiver)
			{
				if(f.type.equals("dash") && (f.direction == 1 || f.direction == 3))
					player.isDashing = true;
			}
			if(player.isDashing)
			{
				for(Force f: player.bounds.forceArchiver)
				{
					if(f.type.equals("yJump"))
						f.magnitude = 0;
				}
			}
			player.bounds.isFloating = player.isDashing; // || other isFloating checks;
			fTimer++;
			
			//INPUT CHECKS
			if(stickArchiver[0])
			{
				if(player.bounds.isGrounded)
				{
					if(stickArchiver[1])
						player.bounds.forceArchiver.add(new Force("xJump",3,6,0));
					if(stickArchiver[3])
						player.bounds.forceArchiver.add(new Force("xJump",1,6,0));
				}
				
				if(!player.isDashing)
				{
					if(player.bounds.isGrounded && !player.bounds.isFloating)
						player.bounds.forceArchiver.add(new Force("yJump",2,45,1));
					else if(player.bounds.blocked[0] != player.bounds.yCoord+player.bounds.height/2 && player.bounds.forceArchiver.size() > 0)
					{
						boolean j = true;
						for(Force f: player.bounds.forceArchiver)
						{
							if(f.type.equals("headhug"))
								j = false;
						}
						
						if(j)
							player.bounds.forceArchiver.add(new Force("headhug",2,player.jump*0.75,0.8));
					}
				}
				
				if(stickArchiver[1])
					addStickInput(9);
				else if(stickArchiver[3])
					addStickInput(7);
				else
					addStickInput(8);
			}
			else if(stickArchiver[2])
			{
				if(player.bounds.isGrounded)
				{
					player.isCrouching = true;
					player.bounds.xDir = 0;
					player.bounds.xDrag = 0;
				}
				
				if(stickArchiver[1])
					addStickInput(3);
				else if(stickArchiver[3])
					addStickInput(1);
				else
					addStickInput(2);
			}
			
			if(stickArchiver[1])
			{
				if(player.bounds.xDrag != 1 && player.bounds.isGrounded && !player.isCrouching && !player.isDashing)
				{
					if(player.bounds.xDir == -1 || (player.bounds.xDir == 0 && player.bounds.xDrag != 1 && player.bounds.xVel > 0))
					{
						player.bounds.xDir = 0;
						player.bounds.xDrag = -1;
					}
					else
					{
						player.bounds.xDir = 1;
						player.bounds.xDrag = 0;
					}
				}
				
				if(!stickArchiver[0] && !stickArchiver[2])
					addStickInput(6);
			}
			else if(stickArchiver[3])
			{
				if(player.bounds.xDrag != -1 && player.bounds.isGrounded && !player.isCrouching && !player.isDashing)
				{
					if(player.bounds.xDir == 1 || (player.bounds.xDir == 0 && player.bounds.xVel > 0))
					{
						player.bounds.xDir = 0;
						player.bounds.xDrag = 1;
					}
					else
					{
						player.bounds.xDir = -1;
						player.bounds.xDrag = 0;
					}
				}
				
				if(!stickArchiver[0] && !stickArchiver[2])
					addStickInput(4);
			}
			
			if(!stickArchiver[0] && !stickArchiver[1] && !stickArchiver[2] && !stickArchiver[3])
				addStickInput(5);
			
			for(int b = 0; b < 6; b++)
			{
				if(buttonArchiver[b])
				{
					buttonHeld[b] = true;
					addButtonInput(b);
				}
			}
		}
	}
	
	public void readInputs()
	{
		for(int[][] m: player.movelist)
		{
			LinkedList<int[]> sInputs = (LinkedList<int[]>)stickInputs.clone();
			LinkedList<int[]> bInputs = (LinkedList<int[]>)buttonInputs.clone();
			sInputs.addLast(new int[]{5,1});
			bInputs.addLast(new int[]{-1,1});
			
			int i = m[0].length-1;
			boolean c = true;
			while(i >= 0)
			{
				int s = sInputs.getFirst()[0];
				if(!player.isFacingRight)
				{
					switch(sInputs.getFirst()[0])
					{
						case 1:
							s = 3;
							break;
							
						case 3:
							s = 1;
							break;
							
						case 4:
							s = 6;
							break;
							
						case 6:
							s = 4;
							break;
							
						case 7:
							s = 9;
							break;
							
						case 9:
							s = 7;
							break;
					}
				}
				
				if((s == m[0][i] || m[0][i] == -1) && (bInputs.getFirst()[0] == m[1][i] || m[1][i] == -1))
				{
					if(m[0][i] != -1)
					{
						if(((m[2][0] == 0 && m[2][i] >= sInputs.getFirst()[1]) || (m[2][0] == 1 && m[2][i] <= sInputs.getFirst()[1])) || i == 0)
							sInputs.removeFirst();
						else
							c = false;
					}
					if(m[1][i] != -1)
					{
						if(((m[2][0] == 0 && m[2][i] >= bInputs.getFirst()[1]) || (m[2][0] == 1 && m[2][i] <= bInputs.getFirst()[1])) || i == 0)
							bInputs.removeFirst();
						else
							c = false;
					}
					sInputs.addLast(new int[]{5,1});
					bInputs.addLast(new int[]{-1,1});
				}
				else
					c = false;
				
				if(!c)
					i = 0;
				i--;
			}
			
			if(c)
			{//System.out.println(">> "+player.movelist.indexOf(m));
				if(player.currAction == null)
					player.currAction = player.actions[player.movelist.indexOf(m)];
				else if(player.currAction.isCancelable(player.actions[player.movelist.indexOf(m)].type))
					player.currAction = player.actions[player.movelist.indexOf(m)];
			}
		}
	}
	
	public void addStickInput(int i)
	{
		boolean isNew = false;
		if(stickInputs.size() > 0)
		{
			if(stickInputs.getFirst()[0] != i)
				isNew = true;
		}
		else
			isNew = true;
		
		if(isNew)
		{
			stickInputs.addFirst(new int[]{i,fTimer});
			inputOrder.addFirst(true);
			if(i != 5)
			{
				while(stickInputs.size() > 30)
					stickInputs.removeLast();
				while(inputOrder.size() > 60)
					inputOrder.removeLast();
				fTimer = 0;
				readInputs();
			}
		}
	}
	
	public void addButtonInput(int i)
	{
		boolean isNew = false;
		if(buttonInputs.size() > 0)
		{
			if(!buttonHeld[i])
				isNew = true;
		}
		else
			isNew = true;
		
		if(isNew)
		{
			buttonInputs.addFirst(new int[]{i,fTimer});
			inputOrder.addFirst(false);
			
			while(buttonInputs.size() > 30)
				buttonInputs.removeLast();
			while(inputOrder.size() > 60)
				inputOrder.removeLast();
			fTimer = 0;
			readInputs();
		}
	}
	
	
	public void keyPressed(KeyEvent e)
	{
		if(player != null)
		{
		/*	if(e.getKeyCode() == KeyEvent.VK_P)
				debugging = !debugging;*/
			
			if(e.getKeyCode() == stickBindings[0])
				stickArchiver[0] = true;
			if(e.getKeyCode() == stickBindings[1])
				stickArchiver[1] = true;
			if(e.getKeyCode() == stickBindings[2])
				stickArchiver[2] = true;
			if(e.getKeyCode() == stickBindings[3])
				stickArchiver[3] = true;
			
			if(e.getKeyCode() == buttonBindings[0])
			{
				buttonArchiver[0] = true;
				addButtonInput(0);
			}
			if(e.getKeyCode() == buttonBindings[1])
			{
				buttonArchiver[1] = true;
				addButtonInput(1);
			}
			if(e.getKeyCode() == buttonBindings[2])
			{
				buttonArchiver[2] = true;
				addButtonInput(2);
			}
			if(e.getKeyCode() == buttonBindings[3])
			{	
				buttonArchiver[3] = true;
				addButtonInput(3);
			}
			if(e.getKeyCode() == buttonBindings[4])
			{
				buttonArchiver[4] = true;
				addButtonInput(4);
			}
			if(e.getKeyCode() == buttonBindings[5])
			{
				buttonArchiver[5] = true;
				addButtonInput(5);
			}
			
			if(e.getKeyCode() == buttonBindings[8])
				buttonArchiver[8] = true;
			if(e.getKeyCode() == buttonBindings[9])
				buttonArchiver[9] = true;
		}
	}
	
	public void keyReleased(KeyEvent e)
	{
		if(player != null)
		{
			if(e.getKeyCode() == stickBindings[0])
				stickArchiver[0] = false;
			if(e.getKeyCode() == stickBindings[1])
			{
				stickArchiver[1] = false;
				if(player.bounds.xDir == 1)
				{
					player.bounds.xDir = 0;
					player.bounds.xDrag = 1;
				}
			}
			if(e.getKeyCode() == stickBindings[2])
				stickArchiver[2] = false;
			if(e.getKeyCode() == stickBindings[3])
			{
				stickArchiver[3] = false;
				if(player.bounds.xDir == -1)
				{
					player.bounds.xDir = 0;
					player.bounds.xDrag = -1;
				}
			}
			
			if(e.getKeyCode() == buttonBindings[0])
			{
				buttonArchiver[0] = false;
				buttonHeld[0] = false;
			}
			if(e.getKeyCode() == buttonBindings[1])
			{
				buttonArchiver[1] = false;
				buttonHeld[1] = false;
			}
			if(e.getKeyCode() == buttonBindings[2])
			{
				buttonArchiver[2] = false;
				buttonHeld[2] = false;
			}
			if(e.getKeyCode() == buttonBindings[3])
			{	
				buttonArchiver[3] = false;
				buttonHeld[3] = false;
			}
			if(e.getKeyCode() == buttonBindings[4])
			{
				buttonArchiver[4] = false;
				buttonHeld[4] = false;
			}
			if(e.getKeyCode() == buttonBindings[5])
			{
				buttonArchiver[5] = false;
				buttonHeld[5] = false;
			}
			
			if(e.getKeyCode() == buttonBindings[8])
				buttonArchiver[8] = false;
		}
	}
	
	public void keyTyped(KeyEvent e)
	{
		if(player != null)
		{
		}
	}
}