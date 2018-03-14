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
	
	int currButton, fTimer;	//, hugBuffer;
	boolean gamePaused, debugging;
	
	public Hand()
	{
		stickInputs = new LinkedList<int[]>();
		buttonInputs = new LinkedList<int[]>();
		inputOrder = new LinkedList<Boolean>();
		stickBindings = new int[4];
		buttonBindings = new int[12];
		stickArchiver = new boolean[4];
		buttonArchiver = new boolean[14];
		buttonHeld = new boolean[8];
		
		xMouse = 0;
		yMouse = 0;
		winWidth = 0;
		winHeight = 0;
		
		currButton = -1;
		fTimer = 0;
//		hugBuffer = 0;
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
		buttonArchiver = new boolean[14];
		buttonHeld = new boolean[8];
		
		xMouse = MouseInfo.getPointerInfo().getLocation().x-x;
		yMouse = MouseInfo.getPointerInfo().getLocation().y-y;
		winWidth = w;
		winHeight = h;
		
		currButton = -1;
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
			if(player.hitStun == 0 || !player.bounds.isGrounded)
				player.isCrouching = false;
	//		player.isDashing = false;
			player.isBlocking = new boolean[]{false,false};
			player.isRecovering = (buttonHeld[0] || buttonHeld[1] || buttonHeld[2] || buttonHeld[3] || buttonHeld[4] || buttonHeld[5]);
			player.isPushing[0] = (buttonHeld[1] && buttonHeld[4] && player.blockStun > 0);
			player.sInputs = stickArchiver;
			
	/*		for(Force f: player.bounds.forceArchiver)
			{
				if(f.type.equals("dash") && (f.direction == 1 || f.direction == 3))
					player.isDashing = true;
			}
			if(player.isDashing)
			{
				for(Force f: player.bounds.forceArchiver)
				{
					if(f.type.equals("yJump"))
						f.decay = f.magnitude;
				}
			}*/
	//		currButton = -1;
			if(fTimer < 1000)
				fTimer++;
			
			//INPUT CHECKS
			if(stickArchiver[0])
			{
				if((inputOrder.getFirst() || player.bounds.isGrounded) && !player.isAirLocked)
				{
					boolean j = (player.currAction == null);
					if(!j)
					{
						if(player.currAction.isCancelable(player.hitInfo[0],player.fCounter,Action.JUMP,currButton,player.bounds.isGrounded))
						{
							player.currAction = null;
							player.isPerformingAction = false;
							
							if(player.bounds.isGrounded)
								player.bounds.botOffset = 0;
							j = true;
						}
					}
					
					if(j)
					{
						if(/*player.jDirections[2] == 0 &&*/ player.airOptions+player.extraAir > player.aDash+player.jCount && player.jCount < player.jumpLimit+player.extraAir && !player.isBlocking[0] && !player.isBlocking[1])
						{
							if(player.bounds.isGrounded || (!player.bounds.isGrounded && player.jDirections[2] == 0 && player.preFrames == 0))
							{
								if(stickArchiver[1])
									player.jDirections[0] = 1;
								else if(stickArchiver[3])
									player.jDirections[0] = -1;
								else
									player.jDirections[0] = 0;
							}
							if(player.jDirections[2] == 0)
								player.jDirections[2] = 1;
						}
						
						for(Force f: player.bounds.forceArchiver)
						{
							if(f.type.equals("dash"))
							{
								f.decay = f.magnitude;
						/*		if(j && player.jDirections[0] == 0)
									player.jDirections[0] = (f.direction == 3)? 1:-1;*/
							}
						}
						
						/*	if(player.bounds.isGrounded && !player.bounds.isFloating)
						{
							if(stickArchiver[1])
								player.jDirections[0] = 1;
							if(stickArchiver[3])
								player.jDirections[0] = -1;
							if(!player.isDashing && !player.isBlocking[0] && !player.isBlocking[1])
							{
								player.jDirections[1] = 1;
								player.preFrames = 3;
							}
						}*/
					}
				}
				
				if(stickArchiver[1])
					addStickInput(9);
				else if(stickArchiver[3])
					addStickInput(7);
				else
					addStickInput(8);
			}
			else
			{
				if(stickArchiver[2])
				{
					if(player.bounds.isGrounded && !player.isDashing && player.hitStun == 0)
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
				
				if(player.jDirections[2] == -1)	// || (player.jDirections[2] == 2 && player.jDirections[1] != 0))
					player.jDirections[2] = 0;
			}
			
			if(stickArchiver[1])
			{
				if(player.currAction == null && player.bounds.xDrag != 1 && player.bounds.isGrounded && !player.isCrouching && !player.isDashing && !player.isBlocking[0] && !player.isBlocking[1])
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
				
				if(!player.isFacingRight && player.canBlock)
				{
					player.isBlocking[(!player.isCrouching || !player.bounds.isGrounded)? 0:1] = true;
					player.bounds.xDir = 0;
					player.bounds.xDrag = 0;
				}
				
				if(!stickArchiver[0] && !stickArchiver[2])
					addStickInput(6);
			}
			else if(stickArchiver[3])
			{
				if(player.currAction == null && player.bounds.xDrag != -1 && player.bounds.isGrounded && !player.isCrouching && !player.isDashing && !player.isBlocking[0] && !player.isBlocking[1])
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
				
				if(player.isFacingRight && player.canBlock)
				{
					player.isBlocking[(!player.isCrouching || !player.bounds.isGrounded)? 0:1] = true;
					player.bounds.xDir = 0;
					player.bounds.xDrag = 0;
				}
				
				if(!stickArchiver[0] && !stickArchiver[2])
					addStickInput(4);
			}
			
			if(!stickArchiver[0] && !stickArchiver[1] && !stickArchiver[2] && !stickArchiver[3])
				addStickInput(5);
			
			if(currButton != -1 /*&& hugBuffer == 0*/ && ((player.bounds.isGrounded && player.normals[currButton].groundOk) || (!player.bounds.isGrounded && player.normals[currButton].airOk)))
			{//System.out.println("@  "+currButton);
				if(buttonArchiver[currButton] && !buttonHeld[currButton])
				{
					if(player.currAction == null)
						player.setAction(player.normals[currButton]);
					else if(player.currAction.isCancelable(player.hitInfo[0],player.fCounter,player.normals[currButton].type,currButton,player.bounds.isGrounded))
					{
						Puppet t = player.currAction.target;
						player.setAction(player.normals[currButton]);
					//	player.currAction.target = t;
						player.fCounter = 0;
						player.sIndex = player.hitboxArchiver.get(player.currState.getPosition())[0][1];
						
						if(player.bounds.isGrounded)
							player.bounds.botOffset = 0;
					}
					player.currAction.button = currButton;
					currButton = -1;
				}
			}
	/*		else if(hugBuffer > 0)
				hugBuffer--;*/
			
			for(int b = 0; b < 6; b++)
			{
				if(buttonArchiver[b])
				{
					buttonHeld[b] = true;
			//		currButton = b;
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
			LinkedList<Boolean> order = (LinkedList<Boolean>)inputOrder.clone();
			if(sInputs.size() < 2)
				sInputs.addLast(new int[]{5,1});
			if(bInputs.size() < 2)
				bInputs.addLast(new int[]{-1,1});
			
			int i = m[0].length-1;
			int[] j = new int[2];
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
				
				if((s == m[0][i] || m[0][i] == -1 /*|| (s == 5 && m[0][i] != 5 && i > 0)*/) && (bInputs.getFirst()[0] == m[1][i] || m[1][i] < 0))
				{
					if(m[0][i] != -1)
					{
						if(i == m[0].length-1)
						{
							j = new int[]{sInputs.getFirst()[1],0};
							sInputs.removeFirst();
						}
						else if(i == 0 && j[1] == 0)
						{
							j = new int[]{sInputs.getFirst()[1],0};
							sInputs.removeFirst();
						}
						else if((m[2][0] == 0 && (m[2][i+1] >= j[0]+j[1] || m[2][i+1] == -1)) || (m[2][0] == 1 && (m[2][i+1] <= j[0]+j[1] || m[2][i+1] == -1)))
						{
							j = new int[]{sInputs.getFirst()[1],0};
							sInputs.removeFirst();
						}
						else
							c = false;	
					}
					else if(order.getFirst())
					{
						if(i == m[0].length-1)
							c = false;
						j[0] += sInputs.getFirst()[1];
						
					}
					
					if(m[1][i] >= 0)
					{
						if(i == m[0].length-1)
						{
							if(player.actions[player.movelist.indexOf(m)].type != Action.TAUNT || bInputs.getFirst()[1] > 1 || m[2][i] == 1)
							{
								j = new int[]{bInputs.getFirst()[1],0};
								bInputs.removeFirst();
							}
						}
					/*	else if(i == 0 && j[1] == 0)
						{
							if(player.actions[player.movelist.indexOf(m)].type != Action.TAUNT || bInputs.getFirst()[1] > 1 || m[2][i] == 1)
							{
								j = new int[]{sInputs.getFirst()[1],0};
								bInputs.removeFirst();
							}
						}*/
						else if((m[2][0] == 0 && (m[2][i+1] >= j[0]+j[1] || m[2][i+1] == -1)) || (m[2][0] == 1 && (m[2][i+1] <= j[0]+j[1] || m[2][i+1] == -1)))
						{
							if(player.actions[player.movelist.indexOf(m)].type != Action.TAUNT || bInputs.getFirst()[1] > 1 || m[2][i] == 1)
							{
								j = new int[]{bInputs.getFirst()[1],0};
								bInputs.removeFirst();
							}
						}
						else
							c = false;
					}
					else if(!order.getFirst())
					{
						if(m[1][i] == -2)
							c = false;
						j[0] += bInputs.getFirst()[1];
					}
					
					if(sInputs.size() < 2)
						sInputs.addLast(new int[]{5,1});
					if(bInputs.size() < 2)
						bInputs.addLast(new int[]{-1,1});
				}
				else if(s == 5 && sInputs.size() > 1 && order.getFirst())
				{
					j[1] += sInputs.getFirst()[1];
					sInputs.removeFirst();
					i++;
				}
				else
					c = false;
				
				if(!c)
					i = 0;
				else
					order.removeFirst();
				i--;
			}
			
			if(c && player.actions[player.movelist.indexOf(m)].cost[0] <= player.stamina && player.actions[player.movelist.indexOf(m)].cost[1] <= player.meter && ((player.bounds.isGrounded && player.actions[player.movelist.indexOf(m)].groundOk) || (!player.bounds.isGrounded && player.actions[player.movelist.indexOf(m)].airOk)))
			{
				if(player.currAction == null)
				{
					if(m.length < 4)
					{//System.out.println(">> "+player.movelist.indexOf(m)+" "+player.actions[player.movelist.indexOf(m)]);
						player.setAction(player.actions[player.movelist.indexOf(m)]);
						player.stamina -= player.actions[player.movelist.indexOf(m)].cost[0];
						player.meter -= player.actions[player.movelist.indexOf(m)].cost[1];
					}
				}
				else if(player.currAction.cancelOk && (player.currAction.isCancelable(player.hitInfo[0],player.fCounter,player.actions[player.movelist.indexOf(m)].type,currButton,player.bounds.isGrounded) || m.length >= 4))
				{
					boolean n = (m.length < 4);
					if(!n)
					{
						if(m[3][0] == -1)
							n = player.currAction.isCancelable(player.hitInfo[0],player.fCounter,player.actions[player.movelist.indexOf(m)].type,currButton,player.bounds.isGrounded);
						else
						{
							for(int o = 0; o < m[3].length; o += 2)
							{
								boolean[] p = new boolean[]{(player.bounds.isGrounded && !player.currAction.cLock),player.currAction.cLock,!player.bounds.isGrounded};
								if(((m[3][o] < player.normals.length && player.currAction.getClass() == player.normals[m[3][o]].getClass()) || (m[3][o] >= player.normals.length && player.currAction.getClass() == player.actions[m[3][o]].getClass())) && p[m[3][o+1]] && player.hitInfo[0] >= player.actions[player.movelist.indexOf(m)].cancelType)
									n = true;
								
								if(player.movelist.indexOf(m) == 7 || player.movelist.indexOf(m) == 8)System.out.println(p[m[3][o+1]]+" "+player.hitInfo[0]+" >= "+player.actions[player.movelist.indexOf(m)].cancelType+" "+n);
							}
						}
					}
					if(n)
					{//System.out.println(">> "+player.movelist.indexOf(m)+" "+player.actions[player.movelist.indexOf(m)]);
						Puppet t = player.currAction.target;
						player.setAction(player.actions[player.movelist.indexOf(m)]);
						player.stamina -= player.actions[player.movelist.indexOf(m)].cost[0];
						player.meter -= player.actions[player.movelist.indexOf(m)].cost[1];
						
						if(player.currAction.type != Action.SPECIAL && player.currAction.type != Action.SUPER)
							player.currAction.target = t;
						player.fCounter = 0;
						player.sIndex = player.hitboxArchiver.get(player.currState.getPosition())[0][1];
						
						if(player.bounds.isGrounded)
							player.bounds.botOffset = 0;
					}
				}
				player.actions[player.movelist.indexOf(m)].button = currButton;
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
/*			if(hugBuffer == 0)
				hugBuffer = 1;*/
			readInputs();
		}
	}
	
	
	public void keyPressed(KeyEvent e)
	{//System.out.println(e.getKeyCode());
		if(player != null)
		{
			for(int s = 0; s < 4; s++)
			{
				if(e.getKeyCode() == stickBindings[s])
					stickArchiver[s] = true;
			}
			
			for(int b = 0; b < buttonBindings.length; b++)
			{
				if(e.getKeyCode() == buttonBindings[b])
				{
					buttonArchiver[b] = true;
					if(b < 6)
					{
						currButton = b;
						addButtonInput(b);
					}
				}
			}
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
			
			for(int b = 0; b < buttonBindings.length; b++)
			{
				if(e.getKeyCode() == buttonBindings[b])
				{
					buttonArchiver[b] = false;
					if(b < 6)
						buttonHeld[b] = false;
				}
			}
		}
	}
	
	public void keyTyped(KeyEvent e)
	{
		if(player != null){}
	}
}