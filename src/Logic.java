//import java.awt.Canvas;
import java.awt.geom.Line2D;
import java.util.ArrayList;

public class Logic
{
//	ArrayList<Hitbox> collisionPriority;
	Stage stage;
//	Curtains curtains;
	Hand[] hands;
	Cricket cricket;
	Force gravity;
	int xWindow, yWindow, /*winWidth, winHeight,*/ xFocus, yFocus;
	boolean gamePaused;
	
	public Logic(Stage s, /*Curtains c,*/ Hand h1, Hand h2, int x, int y, boolean p/*, int w, int h2*/)
	{
		stage = s;
	//	curtains = c;
		hands = new Hand[]{h1,h2};
		cricket = new Cricket(s);
		gravity = new Force("gravity",0,15.68,0);
//		collisionPriority = new ArrayList<Hitbox>();
		
		xWindow = x;
		yWindow = y;
	/*	winWidth = w;
		winHeight = h2;*/
		xFocus = 0;
		yFocus = 0;
		gamePaused = p;
	}
	
	public void focus()
	{
		stage.xFocus = xFocus;
		stage.yFocus = yFocus;
	
		for(Floor f: stage.floors)
		{
			f.xHosh = f.xCoord+xFocus;
			f.yHosh = f.yCoord+yFocus;
		}
		for(Puppet p1: stage.puppets)
		{
			p1.xHosh = p1.xCoord+xFocus;
			p1.yHosh = p1.yCoord+yFocus;
			p1.bounds.xHosh = p1.bounds.xCoord+xFocus;
			p1.bounds.yHosh = p1.bounds.yCoord+yFocus;
			
			for(Organ o: p1.anatomy)
			{
				o.xHosh = o.xCoord+xFocus;
				o.yHosh = o.yCoord+yFocus;
			}
		}
		for(Prop p2: stage.props)
		{
			p2.bounds.xHosh = p2.bounds.xCoord+xFocus;
			p2.bounds.yHosh = p2.bounds.yCoord+yFocus;							
		}
		for(Pleb p3: stage.plebs)
		{
			stage.plebs.get(stage.plebs.indexOf(p3)).xHosh = stage.plebs.get(stage.plebs.indexOf(p3)).xCoord+xFocus;
			stage.plebs.get(stage.plebs.indexOf(p3)).yHosh = stage.plebs.get(stage.plebs.indexOf(p3)).yCoord+yFocus;
		}
	}
	
	public void setFocus()
	{
		if(stage.player1 != null && stage.player2 != null)
		{
			Hitbox[] players = new Hitbox[]{stage.player1.bounds,stage.player2.bounds};
			boolean y = false;
			
			for(int p = 0; p < players.length; p++)
			{
				if(players[p].xCoord < -xFocus && players[p].xCoord > stage.floors.get(0).xCoord)
					xFocus = -players[p].xCoord;
				if(players[p].xCoord+players[p].width > 1280-xFocus && players[p].xCoord+players[p].width < stage.floors.get(0).xCoord+stage.floors.get(0).width)
					xFocus = 1280-(players[p].xCoord+players[p].width);
				
				if(players[p].yCoord < -yFocus)
				{
					yFocus = -players[p].yCoord;
					y = true;
				}
			}
			if(!y && yFocus > -400)
				yFocus = (stage.player1.bounds.yCoord < stage.player2.bounds.yCoord)? -stage.player1.bounds.yCoord:-stage.player2.bounds.yCoord;
		}
	}
	
	public void setFocusTo(int x, int y)
	{
		xFocus = 640-x;
		yFocus = 400-y;
	}
	
	public void setFocusTo(Hitbox h)
	{
		xFocus = 640-(h.xCoord+h.width/2);
		yFocus = 600-(h.yCoord+h.height);
	}
	
	public void resetFocus()
	{
	//	xFocus = 0;
//		yFocus = 0;
	}
	
	public void applyForces()
	{
		for(Puppet p: stage.puppets)
		{
			int fLimit = p.bounds.forceArchiver.size();
			if(!p.bounds.isGrounded && !p.bounds.isFloating)
				p.bounds.forceArchiver.add(gravity);
		/*	else
			{
				for(int f = 0; f < fLimit; f++)
				{
					if(p.bounds.forceArchiver.get(f).type.equals("gravity") && p.bounds.forceArchiver.get(f).direction == 0)
					{
						p.bounds.forceArchiver.remove(f);
						fLimit = p.bounds.forceArchiver.size();
						f--;
					}
				}
			}*/
			
			fLimit = p.bounds.forceArchiver.size();
			for(int f = 0; f < fLimit; f++)
			{
				String t = p.bounds.forceArchiver.get(f).type;
				int d = p.bounds.forceArchiver.get(f).direction;
				
				if(f+1 < fLimit)
				{
					for(int g = f+1; g < fLimit; g++)
					{
						if(p.bounds.forceArchiver.get(g).type == t && p.bounds.forceArchiver.get(g).direction == d)
						{
							if((!t.equals("gravity") && d != 0))
							{
								p.bounds.forceArchiver.get(f).magnitude += p.bounds.forceArchiver.get(g).magnitude;
								p.bounds.forceArchiver.get(f).decay += p.bounds.forceArchiver.get(g).decay;
							}
							p.bounds.forceArchiver.remove(g);
							fLimit = p.bounds.forceArchiver.size();
							g--;
						}
					}
				}
			}
		}
		for(Prop p: stage.props)
		{
			int fLimit = p.bounds.forceArchiver.size();
			if(!p.bounds.isGrounded && !p.bounds.isFloating)
				p.bounds.forceArchiver.add(new Force("gravity",0,9.8,0));
		/*	else
			{
				for(int f = 0; f < fLimit; f++)
				{
					if(p.bounds.forceArchiver.get(f).type.equals("gravity") && p.bounds.forceArchiver.get(f).direction == 0)
					{
						p.bounds.forceArchiver.remove(f);
						fLimit = p.bounds.forceArchiver.size();
						f--;
					}
				}
			}*/
			
			fLimit = p.bounds.forceArchiver.size();
			for(int f = 0; f < fLimit; f++)
			{
				String t = p.bounds.forceArchiver.get(f).type;
				int d = p.bounds.forceArchiver.get(f).direction;
				
				if(f+1 < fLimit)
				{
					for(int g = f+1; g < fLimit; g++)
					{
						if(p.bounds.forceArchiver.get(g).type == t && p.bounds.forceArchiver.get(g).direction == d)
						{
							if((!t.equals("gravity") && d != 0))
							{
								p.bounds.forceArchiver.get(f).magnitude += p.bounds.forceArchiver.get(g).magnitude;
								p.bounds.forceArchiver.get(f).decay += p.bounds.forceArchiver.get(g).decay;
							}
							p.bounds.forceArchiver.remove(g);
							fLimit = p.bounds.forceArchiver.size();
							g--;
						}
					}
				}
			}
			
		/*	for(int f = 0; f < fLimit; f++)
			{
				String t = p.bounds.forceArchiver.get(f).type;
				int d = p.bounds.forceArchiver.get(f).direction;
				
				if(f+1 < fLimit)
				{
					for(int g = f+1; g < fLimit; g++)
					{
						if(p.bounds.forceArchiver.get(g).type == t && p.bounds.forceArchiver.get(g).direction == d)
						{
							if((!t.equals("gravity") && d != 0))
								p.bounds.forceArchiver.get(f).magnitude += p.bounds.forceArchiver.get(g).magnitude;
							p.bounds.forceArchiver.remove(g);
							fLimit = p.bounds.forceArchiver.size();
							g--;
						}
					}
				}
			}*/
		}
	/*	for(Pleb p: stage.plebs)
		{
			int fLimit = p.forceArchiver.size();
			if(!p.isFloating)
				p.forceArchiver.add(new Force("gravity",0,9.8,0));
			else
			{
				for(int f = 0; f < fLimit; f++)
				{
					if(p.forceArchiver.get(f).type.equals("gravity") && p.forceArchiver.get(f).direction == 0)
					{
						p.forceArchiver.remove(f);
						fLimit = p.forceArchiver.size();
						f--;
					}
				}
			}
			
			for(int f = 0; f < fLimit; f++)
			{
				String t = p.forceArchiver.get(f).type;
				int d = p.forceArchiver.get(f).direction;
				
				if(f+1 < fLimit)
				{
					for(int g = f+1; g < fLimit; g++)
					{
						if(p.forceArchiver.get(g).type == t && p.forceArchiver.get(g).direction == d)
						{
							if((!t.equals("gravity") && d != 0))
								p.forceArchiver.get(f).magnitude += p.forceArchiver.get(g).magnitude;
							p.forceArchiver.remove(g);
							fLimit = p.forceArchiver.size();
							g--;
						}
					}
				}
			}
		}*/
	}
	
	public void checkCollisions()
	{
		ArrayList<Organ> hitboxes = new ArrayList<Organ>();
		boolean xBlocked = false;
		boolean yBlocked = false;
		
	//	boolean isPrioritized = false;
	//	int cLimit = collisionPriority.size();
		
//		do{
			for(Puppet p: stage.puppets)
			{
				p.touchArchiver = new ArrayList<int[]>();
				p.touchArchiver.add(new int[]{-1});
				p.bounds.isGrounded = false;
				
		/*		cLimit = collisionPriority.size();
				for(int c = 0; c < cLimit; c++)
				{
					if(collisionPriority.get(c) == p.bounds)
					{
						hitboxes.add(0,p.bounds);
						isPrioritized = true;
						
						collisionPriority.remove(c);
						cLimit = collisionPriority.size();
						c--;
					}
				}
				if(!isPrioritized)*/
					hitboxes.add(p.bounds);
			}
	//		isPrioritized = false;
			for(Prop p: stage.props)
			{
				int t = p.touchArchiver.get(0)[0];
				p.touchArchiver = new ArrayList<int[]>();
				p.touchArchiver.add(new int[]{t});
				p.bounds.isGrounded = false;
				
		/*		for(int c = 0; c < cLimit; c++)
				{	
					if(collisionPriority.get(c) == p.bounds)
					{
						hitboxes.add(0,p.bounds);
						isPrioritized = true;
						
						collisionPriority.remove(c);
						cLimit = collisionPriority.size();
						c--;
					}
				}
				if(!isPrioritized)*/
					hitboxes.add(p.bounds);
			}
			
			double[][] forces = new double[hitboxes.size()][4];
			for(Organ h: hitboxes)
			{
				forces[hitboxes.indexOf(h)][0] = 0;
				forces[hitboxes.indexOf(h)][1] = 0;
				forces[hitboxes.indexOf(h)][2] = 0;
				forces[hitboxes.indexOf(h)][3] = 0;
				
				int fLimit = h.forceArchiver.size();
				for(int f = 0; f < fLimit; f++)
				{
					switch(h.forceArchiver.get(f).direction)
					{
						case 0:
							if(!h.forceArchiver.get(f).type.equals("gravity") || (h.forceArchiver.get(f).type.equals("gravity") && !h.isFloating))
								forces[hitboxes.indexOf(h)][0] += h.forceArchiver.get(f).magnitude;
							h.forceArchiver.get(f).magnitude -= h.forceArchiver.get(f).decay;
							break;
							
						case 1:
							forces[hitboxes.indexOf(h)][1] += h.forceArchiver.get(f).magnitude;
							h.forceArchiver.get(f).magnitude -= h.forceArchiver.get(f).decay;
							break;
							
						case 2:
							forces[hitboxes.indexOf(h)][2] += h.forceArchiver.get(f).magnitude;
							h.forceArchiver.get(f).magnitude -= h.forceArchiver.get(f).decay;
							break;
							
						case 3:
							forces[hitboxes.indexOf(h)][3] += h.forceArchiver.get(f).magnitude;
							h.forceArchiver.get(f).magnitude -= h.forceArchiver.get(f).decay;
							break;
					}
					
					if(h.forceArchiver.get(f).magnitude <= 0)
					{
						h.forceArchiver.remove(f);
						fLimit = h.forceArchiver.size();
						f--;
					}
				}
			}
			
			boolean[] cornerOccupied = new boolean[]{false, false};
			for(Organ h1: hitboxes)
			{
				int x1 = h1.xCoord;
				int y1 = h1.yCoord;
				
				x1 += (int)(forces[hitboxes.indexOf(h1)][3]-forces[hitboxes.indexOf(h1)][1]+0.5*((forces[hitboxes.indexOf(h1)][3] > forces[hitboxes.indexOf(h1)][1])? 1:-1));
				y1 += (int)(forces[hitboxes.indexOf(h1)][0]-forces[hitboxes.indexOf(h1)][2]+0.5*((forces[hitboxes.indexOf(h1)][0] > forces[hitboxes.indexOf(h1)][2])? 1:-1));
			
				if(h1.xDir != 0)
					x1 += h1.xForward;
				if(h1.xDrag != 0)
					x1 += h1.xDrift;
				if(h1.yDir != 0)
					y1 -= h1.yForward;
				if(h1.yDrag != 0)
					y1 -= h1.yDrift;
				
				for(Floor f: stage.floors)
				{
					if(h1.xCoord == f.xCoord)
					{
						if(f.cornered[0] == null)
							f.cornered[0] = h1;
						else if(h1 != f.cornered[0] && y1+h1.height > f.cornered[0].yCoord && h1.yCoord <= f.cornered[0].yCoord)
						{
							h1.xCoord = f.cornered[0].xCoord+f.cornered[0].width;
							h1.blocked[1] = f.cornered[0].xCoord+f.cornered[0].width;
						}
						cornerOccupied[0] = true;
					}
					if(h1.xCoord+h1.width == f.xCoord+f.width)
					{
						if(f.cornered[1] == null)
							f.cornered[1] = h1;
						else if(h1 != f.cornered[1] && y1+h1.height > f.cornered[1].yCoord && h1.yCoord <= f.cornered[1].yCoord)
						{
							h1.xCoord = f.cornered[1].xCoord-h1.width;
							h1.blocked[1] = f.cornered[1].xCoord-h1.width;
						}
						cornerOccupied[1] = true;
					}
				}
				
				if(stage.player1 != null && stage.player2 != null)
				{
					if(h1 == stage.player1.bounds || h1 == stage.player2.bounds)
					{
						int p = (h1 == stage.player1.bounds)? hitboxes.indexOf(stage.player2.bounds):hitboxes.indexOf(stage.player1.bounds);
						if(x1 > h1.xCoord || h1.xDir > 0 || h1.xDrag > 0 || h1.blocked[1] != h1.xCoord+h1.width/2)
						{
							for(int x = h1.xCoord; x <= x1; x++)
							{
								if(x+h1.width >= 1280-xFocus && x+h1.width > hitboxes.get(p).xCoord+hitboxes.get(p).width && x+h1.width-hitboxes.get(p).xCoord >= 1280)
								{
						/*			for(Organ h3: stage.puppets.get(hitboxes.indexOf(h1)).anatomy)
										h3.xVel = 0;
						*/			stage.puppets.get(hitboxes.indexOf(h1)).bounds.xVel = 0;
								
									if(h1.blocked[1] > 1280-xFocus  || h1.xCoord+h1.width > 1280-xFocus || h1.blocked[1] == h1.xCoord+h1.width/2)
										h1.xCoord = 1280-xFocus-h1.width;
									x = x1;
								}
								
								if(h1.xCoord == 1280-xFocus-h1.width && x+h1.width > hitboxes.get(p).xCoord+hitboxes.get(p).width && x+h1.width-hitboxes.get(p).xCoord >= 1280)
								{
									h1.blocked[1] = 1280-xFocus;
									xBlocked = true;
								}
							}
						}
						if(x1 < h1.xCoord || h1.xDir < 0 || h1.xDrag < 0 || h1.blocked[3] != h1.xCoord+h1.width/2)
						{
							for(int x = h1.xCoord; x >= x1; x--)
							{
								if(x <= -xFocus && x < hitboxes.get(p).xCoord && hitboxes.get(p).xCoord+hitboxes.get(p).width-x >= 1280)
								{
									if(hitboxes.indexOf(h1) < stage.puppets.size())
									{
								/*		for(Organ h3: stage.puppets.get(hitboxes.indexOf(h1)).anatomy)
											h3.xVel = 0;
								*/		stage.puppets.get(hitboxes.indexOf(h1)).bounds.xVel = 0;
									}
									else
										stage.props.get(hitboxes.indexOf(h1)-stage.puppets.size()).bounds.xVel = 0;
									
									if(h1.blocked[3] < -xFocus || h1.xCoord < -xFocus || h1.blocked[3] == h1.xCoord+h1.width/2)
										h1.xCoord = -xFocus;
									x = x1;
								}
								
								if(h1.xCoord == -xFocus && x < hitboxes.get(p).xCoord && hitboxes.get(p).xCoord+hitboxes.get(p).width-x >= 1280)
								{
									h1.blocked[3] = -xFocus;
									xBlocked = true;
								}
							}
						}
					}
				}
				
				for(Organ h2: hitboxes)
				{
					if(hitboxes.indexOf(h1) != hitboxes.indexOf(h2))
					{
						int x2 = h2.xCoord;
						int y2 = h2.yCoord;
						
						x2 += (int)(forces[hitboxes.indexOf(h2)][3]-forces[hitboxes.indexOf(h2)][1]+0.5*((forces[hitboxes.indexOf(h2)][3] > forces[hitboxes.indexOf(h2)][1])? 1:-1));
						y2 += (int)(forces[hitboxes.indexOf(h2)][0]-forces[hitboxes.indexOf(h2)][2]+0.5*((forces[hitboxes.indexOf(h2)][0] > forces[hitboxes.indexOf(h2)][2])? 1:-1));
						
						if(h2.xDir != 0)
							x2 += h2.xForward;
						if(h2.xDrag != 0)
							x2 += h2.xDrift;
						if(h2.yDir != 0)
							y2 -= h2.yForward;
						if(h2.yDrag != 0)
							y2 -= h2.yDrift;
						
						if(h1.xCoord != h2.xCoord+h2.width && h1.xCoord+h1.width != h2.xCoord && ((h1.xCoord >= h2.xCoord && h1.xCoord < h2.xCoord+h2.width) || (h1.xCoord+h1.width > h2.xCoord && h1.xCoord+h1.width <= h2.xCoord+h2.width) || (h1.xCoord <= h2.xCoord && h1.xCoord+h1.width >= h2.xCoord+h2.width) /*|| (x1 == x2 || x1+h1.height == x2+h2.height)*/))
						{
					/*		if(y1 < h1.yCoord || h1.yDir < 0 || h1.yDrag < 0)
							{
								if((h1.xCoord < h2.xCoord+h2.width && h1.blocked[3] != h2.xCoord+h2.width) || (h1.xCoord+h1.width > h2.xCoord && h1.blocked[1] != h2.xCoord))
								{
									for(int y = h1.yCoord; y >= y1; y--)
									{
										if(y < y2+h2.height && h1.yCoord+h1.height >= y2+h2.height)
										{
											if(hitboxes.indexOf(h1) < stage.puppets.size())
											{
												if(forces[hitboxes.indexOf(h1)][2] > 0)
												{
													int fLimit = stage.puppets.get(hitboxes.indexOf(h1)).bounds.forceArchiver.size();
													for(int g = 0; g < fLimit; g++)
													{
														if(stage.puppets.get(hitboxes.indexOf(h1)).bounds.forceArchiver.get(g).type.equals("jump") && stage.puppets.get(hitboxes.indexOf(h1)).bounds.forceArchiver.get(g).direction == 2)
														{
															stage.puppets.get(hitboxes.indexOf(h1)).jump = stage.puppets.get(hitboxes.indexOf(h1)).bounds.forceArchiver.get(g).magnitude;
															stage.puppets.get(hitboxes.indexOf(h1)).bounds.forceArchiver.remove(g);
															fLimit = stage.puppets.get(hitboxes.indexOf(h1)).bounds.forceArchiver.size();
															g--;
														}
													}
												}
												
												for(Organ h3: stage.puppets.get(hitboxes.indexOf(h1)).anatomy)
													h3.yVel = 0;
												stage.puppets.get(hitboxes.indexOf(h1)).bounds.yVel = 0;
											}
											else
												stage.props.get(hitboxes.indexOf(h1)-stage.puppets.size()).bounds.yVel = 0;
											
											if(h1.blocked[0] < y2+h2.height || h1.yCoord < y2+h2.height || h1.blocked[0] == h1.yCoord+h1.height/2)
												h1.yCoord = y2+h2.height-(int)(gravity.magnitude+0.5)*((h2.isGrounded)? 0:1);
											y = y1;
										}
									}
									
									if(h1.yCoord == y2+h2.height-(int)(gravity.magnitude+0.5)*((h2.isGrounded)? 0:1))
									{
										h1.blocked[0] = y2+h2.height-(int)(gravity.magnitude+0.5)*((h2.isGrounded)? 0:1);
										yBlocked = true;
									}
								}
							}
							else*/ if(y1 > h1.yCoord || h1.yDir > 0 || h1.yDrag > 0)
							{
								if((h1.xCoord < h2.xCoord+h2.width && h1.blocked[3] != h2.xCoord+h2.width) || (h1.xCoord+h1.width > h2.xCoord && h1.blocked[1] != h2.xCoord))
								{
									for(int y = h1.yCoord; y <= y1; y++)
									{
										if(y+h1.height >= y2 && h1.yCoord <= y2)
										{
										/*	if(hitboxes.indexOf(h1) < stage.puppets.size())
											{ 
												for(Organ h3: stage.puppets.get(hitboxes.indexOf(h1)).anatomy)
													h3.yVel = 0;
												stage.puppets.get(hitboxes.indexOf(h1)).bounds.yVel = 0;
												stage.puppets.get(hitboxes.indexOf(h1)).bounds.isGrounded = true;
											}
											else
											{
												stage.props.get(hitboxes.indexOf(h1)-stage.puppets.size()).bounds.yVel = 0;
												stage.props.get(hitboxes.indexOf(h1)-stage.puppets.size()).bounds.isGrounded = true;
											}*/
											
											if(stage.floors.get(0).cornered[0] == h1)
												h2.xCoord = h1.xCoord+h1.width;
											else if(stage.floors.get(0).cornered[1] == h1)
												h2.xCoord = h1.xCoord-h2.width;
											else
											{
												if(x1+h1.width/2 >= x2+h2.width/2)
														h1.xCoord = x2+h2.width;
												else
													h1.xCoord = x2-h1.width;
											}
											
									/*		if(h1.blocked[2] > y2 || h1.yCoord+h1.height > y2 || h1.blocked[2] == h1.yCoord+h1.height/2)
												h1.yCoord = y2-h1.height-(int)(gravity.magnitude+0.5)*((h2.isGrounded)? 0:1);
									*/		y = y1;
										}
									}
									
									if(h1.yCoord == y2-h1.height-(int)(gravity.magnitude+0.5)*((h2.isGrounded)? 0:1))
									{
								//		h1.blocked[2] = y2-(int)(gravity.magnitude+0.5)*((h2.isGrounded)? 0:1);
										yBlocked = true;
									}
								}
							}
							if(y2 > h2.yCoord || h2.yDir > 0 || h2.yDrag > 0)
							{
								if((h2.xCoord < h1.xCoord+h1.width && h2.blocked[3] != h1.xCoord+h1.width) || (h2.xCoord+h2.width > h1.xCoord && h2.blocked[1] != h1.xCoord))
								{
									for(int y = h2.yCoord; y <= y2; y++)
									{
										if(y+h2.height >= y1 && h2.yCoord <= y1)
										{
											if(stage.floors.get(0).cornered[0] == h2)
												h1.xCoord = h2.xCoord+h2.width;
											else if(stage.floors.get(0).cornered[1] == h2)
												h1.xCoord = h2.xCoord-h1.width;
											else
											{
												if(x2+h2.width/2 >= x1+h1.width/2)
														h2.xCoord = x1+h1.width;
												else
													h2.xCoord = x1-h2.width;
											}
											
									/*		if(h1.blocked[2] > y2 || h1.yCoord+h1.height > y2 || h1.blocked[2] == h1.yCoord+h1.height/2)
												h1.yCoord = y2-h1.height-(int)(gravity.magnitude+0.5)*((h2.isGrounded)? 0:1);
									*/		y = y2;
										}
									}
									
									if(h2.yCoord == y1-h2.height-(int)(gravity.magnitude+0.5)*((h1.isGrounded)? 0:1))
									{
								//		h1.blocked[2] = y2-(int)(gravity.magnitude+0.5)*((h2.isGrounded)? 0:1);
										yBlocked = true;
									}
								}
							}
						}
						
						if(h1.yCoord != h2.yCoord+h2.height && h1.yCoord+h1.height != h2.yCoord && ((h1.yCoord >= h2.yCoord && h1.yCoord < h2.yCoord+h2.height) || (h1.yCoord+h1.height > h2.yCoord && h1.yCoord+h1.height <= h2.yCoord+h2.height) || (h1.yCoord <= h2.yCoord && h1.yCoord+h1.height >= h2.yCoord+h2.height) /*|| (y1 == y2 || y1+h1.height == y2+h2.height)*/))
						{
							if(x1 > h1.xCoord || h1.xDir > 0 || h1.xDrag > 0)
							{
								for(int x = h1.xCoord; x <= x1; x++)
								{
									if(x+h1.width > x2 && h1.xCoord <= x2/* && !(h2.isMovable && x2 < 0 && x1+x2 < 0)*/)
									{
								/*		if(hitboxes.indexOf(h1) < stage.puppets.size())
										{
											for(Organ h3: stage.puppets.get(hitboxes.indexOf(h1)).anatomy)
												h3.xVel = 0;
											stage.puppets.get(hitboxes.indexOf(h1)).bounds.xVel = 0;
										}
										else
											stage.props.get(hitboxes.indexOf(h1)-stage.puppets.size()).bounds.xVel = 0;
								*/		
										if(h1.blocked[1] > h2.xCoord || x1+h1.width > h2.xCoord || h1.blocked[1] == h1.xCoord+h1.width/2)
											h1.xCoord = h2.xCoord-h1.width;
										x = x1;
									}
								}
								
								if(h1.xCoord == h2.xCoord-h1.width/*  && !(h2.isMovable && h2.xCoord < 0 && x1+h2.xCoord < 0)*/)
								{
									h1.blocked[1] = h2.xCoord;
									h2.blocked[3] = h1.xCoord+h1.width;
									xBlocked = true;
									
									if(h2.isMovable/* && h2.blocked[1] != h2.xCoord+h2.width*/)
									{
										int m = 0;
										if(h1.xDir > 0)
											m += h1.xForward;
										if(h1.xDrag > 0)
											m += h1.xDrift;
										if(h2.xDir < 0)
											m += h2.xForward;
										if(h2.xDrag < 0)
											m += h2.xDrift;
										
										for(Force f: h1.forceArchiver)
										{
											if(f.direction == 1)
												m -= f.magnitude;
											if(f.direction == 3)
												m += f.magnitude;
										}
										for(Force f: h2.forceArchiver)
										{
											if(f.direction == 1)
												m -= f.magnitude;
											if(f.direction == 3)
												m += f.magnitude;
										}
										if(m > 0)
											applyNewtonsThird(m,0,h1,hitboxes);
									}
								}
							}
							else if(x1 < h1.xCoord || h1.xDir < 0 || h1.xDrag < 0)
							{
								for(int x = h1.xCoord; x >= x1; x--)
								{
									if(x < x2+h2.width && h1.xCoord+h1.width >= x2+h2.width)
									{
								/*		if(hitboxes.indexOf(h1) < stage.puppets.size())
										{
											for(Organ h3: stage.puppets.get(hitboxes.indexOf(h1)).anatomy)
												h3.xVel = 0;
											stage.puppets.get(hitboxes.indexOf(h1)).bounds.xVel = 0;
										}
										else
											stage.props.get(hitboxes.indexOf(h1)-stage.puppets.size()).bounds.xVel = 0;
								*/		
										if(h1.blocked[3] < x2+h2.width || x1 < x2+h2.width || h1.blocked[3] == h1.xCoord+h1.width/2)
											h1.xCoord = h2.xCoord+h2.width;
										x = x1;
									}
								}
								
								if(h1.xCoord == h2.xCoord+h2.width)
								{
									h2.blocked[1] = h1.xCoord;
									h1.blocked[3] = h2.xCoord+h2.width;
									xBlocked = true;
									
									if(h2.isMovable/* && h2.blocked[1] != h2.xCoord+h2.width*/)
									{
										int m = 0;
										if(h1.xDir < 0)
											m += h1.xForward;
										if(h1.xDrag < 0)
											m += h1.xDrift;
										if(h2.xDir > 0)
											m += h2.xForward;
										if(h2.xDrag > 0)
											m += h2.xDrift;
										
										for(Force f: h1.forceArchiver)
										{
											if(f.direction == 1)
												m -= f.magnitude;
											if(f.direction == 3)
												m += f.magnitude;
										}
										for(Force f: h2.forceArchiver)
										{
											if(f.direction == 1)
												m -= f.magnitude;
											if(f.direction == 3)
												m += f.magnitude;
										}
										if(m < 0)
											applyNewtonsThird(m,0,h1,hitboxes);
									}
								}
							}
							if(x2 < h2.xCoord || h2.xDir < 0 || h2.xDrag < 0)
							{
								for(int x = h2.xCoord; x >= x2; x--)
								{
									if(x < x1+h1.width && h2.xCoord >= x1+h1.width)
									{
										if(h1.blocked[1] > h2.xCoord || x1+h1.width > h2.xCoord || h1.blocked[1] == h1.xCoord+h1.width/2)
											h1.xCoord = h2.xCoord-h1.width;
										x = x2;
									}
								}
								
								if(h1.xCoord == h2.xCoord-h1.width)
								{
									h1.blocked[1] = h2.xCoord;
									h2.blocked[3] = h1.xCoord+h1.width;
									xBlocked = true;
									
							/*		if(h2.isMovable && h2.blocked[1] != h2.xCoord+h2.width)
									{
										int m = 0;
										if(h1.xDir != 0)
											m += h1.xForward;
										if(h1.xDrag != 0)
											m += h1.xDrift;
										if(h2.xDir != 0)
											m -= h2.xForward;
										if(h2.xDrag != 0)
											m -= h2.xDrift;
									//	int d = m;
										
										for(Force f: h1.forceArchiver)
										{
											if(f.direction == 3)
											{
												m += f.magnitude;
									//			d += f.decay;
											}
										}
										for(Force f: h2.forceArchiver)
										{
											if(f.direction == 1)
												m -= f.magnitude;
										}
										if(m > 0)
											applyNewtonsThird(m,0,h1,hitboxes);
									}*/
								}
							}
							else if(x2 > h2.xCoord || h2.xDir > 0 || h2.xDrag > 0)
							{
								for(int x = h2.xCoord; x <= x2; x++)
								{
									if(x+h2.width > x1 && h2.xCoord+h2.width <= x1)
									{
										if(h1.blocked[3] < x2+h2.width || x1 < x2+h2.width || h1.blocked[3] == h1.xCoord+h1.width/2)
											h1.xCoord = h2.xCoord+h2.width;
										x = x2;
									}
								}
								
								if(h1.xCoord == h2.xCoord+h2.width)
								{
									h2.blocked[1] = h1.xCoord;
									h1.blocked[3] = h2.xCoord+h2.width;
									xBlocked = true;
									
							/*		if(h2.isMovable && h2.blocked[1] != h2.xCoord+h2.width)
									{
										int m = 0;
										if(h1.xDir != 0)
											m += h1.xForward;
										if(h1.xDrag != 0)
											m += h1.xDrift;
										if(h2.xDir != 0)
											m -= h2.xForward;
										if(h2.xDrag != 0)
											m -= h2.xDrift;
								//		int d = m;
										
										for(Force f: h1.forceArchiver)
										{
											if(f.direction == 1)
											{
												m -= f.magnitude;
								//				d -= f.decay;
											}
										}
										for(Force f: h2.forceArchiver)
										{
											if(f.direction == 3)
												m -= f.magnitude;
										}
										if(m < 0)
											applyNewtonsThird(m,0,h1,hitboxes);
									}*/
								}
							}
						}
					/*	else
						{
							h1.blocked[1] = h1.xCoord+h1.width/2;
							h1.blocked[3] = h1.xCoord+h1.width/2;
						}*/
						
						if((h1.xCoord >= h2.xCoord && h1.xCoord <= h2.xCoord+h2.width) || (h1.xCoord+h1.width >= h2.xCoord && h1.xCoord+h1.width <= h2.xCoord+h2.width) || (h1.xCoord <= h2.xCoord && h1.xCoord+h1.width >= h2.xCoord+h2.width))
						{
							if((h1.yCoord >= h2.yCoord && h1.yCoord <= h2.yCoord+h2.height) || (h1.yCoord+h1.height >= h2.yCoord && h1.yCoord+h1.height <= h2.yCoord+h2.height) || (h1.yCoord <= h2.yCoord && h1.yCoord+h1.height >= h2.yCoord+h2.height))
							{
								int[] t = new int[]{-1,-1};
								if(hitboxes.indexOf(h2) < stage.puppets.size())
									t = new int[]{0,stage.puppets.get(hitboxes.indexOf(h2)).id};
								else
									t = new int[]{1,stage.props.get(hitboxes.indexOf(h2)-stage.puppets.size()).id};
								if(t[0] != -1)
								{
									if(hitboxes.indexOf(h1) < stage.puppets.size())
										stage.puppets.get(hitboxes.indexOf(h1)).touchArchiver.add(t);
									else
										stage.props.get(hitboxes.indexOf(h1)-stage.puppets.size()).touchArchiver.add(t);
								}
							}
						}
						/*	else
						{
							h1.blocked[0] = h1.yCoord+h1.height/2;
							h1.blocked[2] = h1.yCoord+h1.height/2;
						}*/
					}
				}
				
			/*	x1 += (int)(forces[hitboxes.indexOf(h1)][3]-forces[hitboxes.indexOf(h1)][1]+0.5);
				y1 += (int)(forces[hitboxes.indexOf(h1)][0]-forces[hitboxes.indexOf(h1)][2]+0.5);
				if(h1.xDir != 0)
					x1 += h1.xForward;
				else if(h1.xDrag != 0)
					x1 += h1.xDrift;
				if(h1.yDir != 0)
					y1 -= h1.yForward;
				else if(h1.yDrag != 0)
					y1 -= h1.yDrift;	*/
				
				for(Floor f: stage.floors)
				{
			//		if(h1.yCoord != f.yCoord+f.height && h1.yCoord+h1.height != f.yCoord && ((x1 > f.xCoord && x1 < f.xCoord+f.width) || (x1+h1.width > f.xCoord && x1+h1.width < x1+f.width)) && ((y1 > f.yCoord && y1 < f.yCoord+f.height) || (y1+h1.height >= f.yCoord && y1+h1.height < f.yCoord+f.height)))
					if(/*h1.xCoord != f.xCoord+f.width && h1.xCoord+h1.width != f.xCoord &&*/ ((x1+h1.width > f.xCoord && x1 < f.xCoord+f.width) || (h1.xCoord+h1.width > f.xCoord && h1.xCoord < f.xCoord+f.width) /*|| (x1+h1.width > f.xCoord && x1+h1.width < f.xCoord+f.width) /*|| (x1 == f.xCoord || x1+h1.width == f.xCoord+f.width)*/))
					{
						if(/*h1.yCoord != f.yCoord+f.height && h1.yCoord+h1.height != f.yCoord &&*/ ((y1+h1.height > f.yCoord && y1 < f.yCoord+f.height) || (h1.yCoord+h1.height > f.yCoord && h1.yCoord < f.yCoord+f.height) /*|| (y1+h1.height > f.yCoord && y1+h1.height < f.yCoord+f.height) /*|| (y1 == f.yCoord || y1+h1.height == f.yCoord+f.height)*/))
						{
							if(x1 > h1.xCoord || h1.xDir > 0 || h1.xDrag > 0 || h1.blocked[1] != h1.xCoord+h1.width/2)
							{
								for(int x = h1.xCoord; x <= x1; x++)
								{
									for(int[] w: f.walls[1])
									{
										if((y1 > w[0] && y1 < w[1] && (h1.yCoord < w[1] || h1.blocked[0] != w[1])) || (y1+h1.height > w[0] && y1+h1.height <= w[1] && (h1.yCoord+h1.height > w[0] || h1.blocked[2] != w[0])))
										{
											if(x+h1.width >= f.xCoord+f.width && h1.xCoord <= f.xCoord+f.width /*&& !xBlocked*/)
											{
										/*		if(hitboxes.indexOf(h1) < stage.puppets.size())
												{
													for(Organ h3: stage.puppets.get(hitboxes.indexOf(h1)).anatomy)
														h3.xVel = 0;
													
													stage.puppets.get(hitboxes.indexOf(h1)).bounds.xVel = 0;
												}
												else
													stage.props.get(hitboxes.indexOf(h1)-stage.puppets.size()).bounds.xVel = 0;
										*/		
												h1.xVel = 0;
												
												if(h1.blocked[1] > f.xCoord+f.width  || h1.xCoord+h1.width > f.xCoord+f.width || h1.blocked[1] == h1.xCoord+h1.width/2)
													h1.xCoord = f.xCoord+f.width-h1.width;	//f.xCoord+f.width-h1.xCoord-h1.width+h1.xCoord;
												x = x1;
											}
											
											if(h1.xCoord == f.xCoord+f.width-h1.width)
											{
												h1.blocked[1] = f.xCoord+f.width;
												xBlocked = true;
											}
										}
									}
								}
								
							/*	if(!xBlocked)
								{
									x1 += (int)(forces[hitboxes.indexOf(h1)][3]-forces[hitboxes.indexOf(h1)][1]+0.5);
									h1.xCoord += (int)(forces[hitboxes.indexOf(h1)][3]-forces[hitboxes.indexOf(h1)][1]+0.5);
								}*/
							}
							if(x1 < h1.xCoord || h1.xDir < 0 || h1.xDrag < 0 || h1.blocked[3] != h1.xCoord+h1.width/2)
							{
								for(int x = h1.xCoord; x >= x1; x--)
								{
									for(int[] w: f.walls[3])
									{
										if((y1 > w[0] && y1 < w[1] && (h1.yCoord < w[1] || h1.blocked[0] != w[1])) || (y1+h1.height > w[0] && y1+h1.height <= w[1] && h1.blocked[2] != w[0]))
										{
											if(x < f.xCoord && h1.xCoord+h1.width >= f.xCoord /*&& !xBlocked*/)
											{
										/*		if(hitboxes.indexOf(h1) < stage.puppets.size())
												{
													for(Organ h3: stage.puppets.get(hitboxes.indexOf(h1)).anatomy)
														h3.xVel = 0;
													
													stage.puppets.get(hitboxes.indexOf(h1)).bounds.xVel = 0;
												}
												else
													stage.props.get(hitboxes.indexOf(h1)-stage.puppets.size()).bounds.xVel = 0;
										*/		
												h1.xVel = 0;
												
												if(h1.blocked[3] < f.xCoord || h1.xCoord < f.xCoord || h1.blocked[3] == h1.xCoord+h1.width/2)
													h1.xCoord = f.xCoord;	//f.xCoord-h1.xCoord+h1.xCoord;
												x = x1;
											}
											
											if(h1.xCoord == f.xCoord)
											{
												h1.blocked[3] = f.xCoord;
												xBlocked = true;
											}
										}
									}
								}
								
						/*		if(!xBlocked)
								{
									x1 += (int)(forces[hitboxes.indexOf(h1)][3]-forces[hitboxes.indexOf(h1)][1]+0.5);
									h1.xCoord += (int)(forces[hitboxes.indexOf(h1)][3]-forces[hitboxes.indexOf(h1)][1]+0.5);
								}*/
							}
							
							if(y1 < h1.yCoord || h1.yDir < 0 || h1.yDrag < 0)
							{
								for(int y = h1.yCoord; y >= y1; y--)
								{
									for(int[] w: f.walls[0])
									{
										if((x1 > w[0] && x1 < w[1] && (h1.xCoord < w[1] || h1.blocked[3] != w[1])) || (x1+h1.width > w[0] && x1+h1.width < w[1] && (h1.xCoord+h1.width > w[0] || h1.blocked[1] != w[0])))
										{
											if(y < f.yCoord && h1.yCoord+h1.height >= f.yCoord /*&& !yBlocked*/)
											{
												if(hitboxes.indexOf(h1) < stage.puppets.size())
												{
													if(forces[hitboxes.indexOf(h1)][2] > 0)
													{
														int fLimit = stage.puppets.get(hitboxes.indexOf(h1)).bounds.forceArchiver.size();
														for(int g = 0; g < fLimit; g++)
														{
															if(stage.puppets.get(hitboxes.indexOf(h1)).bounds.forceArchiver.get(g).type.equals("jump") && stage.puppets.get(hitboxes.indexOf(h1)).bounds.forceArchiver.get(g).direction == 2)
															{
																stage.puppets.get(hitboxes.indexOf(h1)).jump = stage.puppets.get(hitboxes.indexOf(h1)).bounds.forceArchiver.get(g).magnitude;
																stage.puppets.get(hitboxes.indexOf(h1)).bounds.forceArchiver.remove(g);
																fLimit = stage.puppets.get(hitboxes.indexOf(h1)).bounds.forceArchiver.size();
																g--;
															}
														}
													}
													
											/*		for(Organ h3: stage.puppets.get(hitboxes.indexOf(h1)).anatomy)
														h3.yVel = 0;
													
													stage.puppets.get(hitboxes.indexOf(h1)).bounds.yVel = 0;
											*/	}
											/*	else
													stage.props.get(hitboxes.indexOf(h1)-stage.puppets.size()).bounds.yVel = 0;
											*/	
												h1.yVel = 0;
												
												if(h1.blocked[0] < f.yCoord  || h1.yCoord < f.yCoord || h1.blocked[0] == h1.yCoord+h1.height/2)
													h1.yCoord = f.yCoord;	//f.yCoord-h1.yCoord+h1.yCoord;
												y = y1;
											}
											
											if(h1.yCoord == f.yCoord)
											{
												h1.blocked[0] = f.yCoord;
												yBlocked = true;
											}
										}
									}
								}
								
							/*	if(!yBlocked)
								{
									y1 += (int)(forces[hitboxes.indexOf(h1)][0]-forces[hitboxes.indexOf(h1)][2]+0.5);
									h1.yCoord += (int)(forces[hitboxes.indexOf(h1)][0]-forces[hitboxes.indexOf(h1)][2]+0.5);
								}*/
							}
							if(y1 > h1.yCoord || h1.yDir > 0 || h1.yDrag > 0)
							{
								for(int y = h1.yCoord; y <= y1; y++)
								{
									for(int[] w: f.walls[2])
									{
										if((x1 > w[0] && x1 < w[1] && (h1.xCoord < w[1] || h1.blocked[3] != w[1])) || (x1+h1.width > w[0] && x1+h1.width < w[1] && (h1.xCoord+h1.width > w[0] || h1.blocked[1] != w[0])))
										{
											if(y+h1.height >= f.yCoord+f.height && h1.yCoord <= f.yCoord+f.height /*&& !yBlocked*/)
											{
												if(hitboxes.indexOf(h1) < stage.puppets.size())
												{
										/*			for(Organ h3: stage.puppets.get(hitboxes.indexOf(h1)).anatomy)
														h3.yVel = 0;
										*/			
													stage.puppets.get(hitboxes.indexOf(h1)).bounds.yVel = 0;
													stage.puppets.get(hitboxes.indexOf(h1)).bounds.isGrounded = true;
												}
												else
												{
										//			stage.props.get(hitboxes.indexOf(h1)-stage.puppets.size()).bounds.yVel = 0;
													stage.props.get(hitboxes.indexOf(h1)-stage.puppets.size()).bounds.isGrounded = true;
												}
												h1.yVel = 0;
											
												if(h1.blocked[2] > f.yCoord+f.height || h1.yCoord+h1.height > f.yCoord+f.height || h1.blocked[2] == h1.yCoord+h1.height/2)
													h1.yCoord = f.yCoord+f.height-h1.height;	//f.yCoord+f.height-h1.yCoord-h1.height+stage.puppets.get(hitboxes.indexOf(h1)).bounds.yCoord;
												y = y1;
											}
											
											if(h1.yCoord == f.yCoord+f.height-h1.height)
											{
												h1.blocked[2] = f.yCoord+f.height;
												yBlocked = true;
											}
										}
									}
								}
								
							/*	if(!yBlocked)
								{
									y1 += (int)(forces[hitboxes.indexOf(h1)][0]-forces[hitboxes.indexOf(h1)][2]+0.5);
									h1.yCoord += (int)(forces[hitboxes.indexOf(h1)][0]-forces[hitboxes.indexOf(h1)][2]+0.5);
								}*/
							}
						}
					}
				}
				
				if(hitboxes.indexOf(h1) < stage.puppets.size())
				{
					int fLimit = h1.forceArchiver.size();
					for(int f = 0; f < fLimit; f++)
					{
						if((h1.forceArchiver.get(f).type.equals("jump") || h1.forceArchiver.get(f).type.equals("headhug")) && h1.forceArchiver.get(f).direction == 2 && stage.puppets.get(hitboxes.indexOf(h1)).bounds.isGrounded)
						{
							h1.forceArchiver.remove(f);
							fLimit = h1.forceArchiver.size();
							f--;
						}
					}
					
					//FOCUS WALLS USED TO BE HERE
				}
				
				if(!xBlocked)
				{
					x1 += (int)(forces[hitboxes.indexOf(h1)][3]-forces[hitboxes.indexOf(h1)][1]+0.5*((forces[hitboxes.indexOf(h1)][3] > forces[hitboxes.indexOf(h1)][1])? 1:-1));
					h1.xCoord += (int)(forces[hitboxes.indexOf(h1)][3]-forces[hitboxes.indexOf(h1)][1]+0.5*((forces[hitboxes.indexOf(h1)][3] > forces[hitboxes.indexOf(h1)][1])? 1:-1));
				}
				
				//UPDATE YBLOCKED FOR BOUNDS LATER???
				if(!yBlocked)
				{
					y1 += (int)(forces[hitboxes.indexOf(h1)][0]-forces[hitboxes.indexOf(h1)][2]+0.5*((forces[hitboxes.indexOf(h1)][0] > forces[hitboxes.indexOf(h1)][2])? 1:-1));
					h1.yCoord += (int)(forces[hitboxes.indexOf(h1)][0]-forces[hitboxes.indexOf(h1)][2]+0.5*((forces[hitboxes.indexOf(h1)][0] > forces[hitboxes.indexOf(h1)][2])? 1:-1));
					h1.blocked[0] = h1.yCoord+h1.height/2;
					h1.blocked[2] = h1.yCoord+h1.height/2;
				}
				else
				{
					if(h1.yDir != 0)
						h1.yCoord += h1.yForward;
					if(h1.yDrag != 0)
						h1.yCoord += h1.yDrift;
				}
				
				if(forces[hitboxes.indexOf(h1)][3]-forces[hitboxes.indexOf(h1)][1] == 0 && (forces[hitboxes.indexOf(h1)][0]-forces[hitboxes.indexOf(h1)][2] == 0 || (forces[hitboxes.indexOf(h1)][0]-forces[hitboxes.indexOf(h1)][2] > 0 && (h1.isGrounded || h1.isFloating))))
				{
					if(h1.isMoving)
						h1.wasMoving = true;
					else
						h1.wasMoving = false;
					
					h1.isMoving = false;
				}
				else
					h1.isMoving = true;
				xBlocked = false;
				yBlocked = false;
			}
			if(!cornerOccupied[0])
				stage.floors.get(0).cornered[0] = null;
			if(!cornerOccupied[1])
				stage.floors.get(0).cornered[1] = null;
		
/*		double[][]*/ forces = new double[stage.plebs.size()][4];
		for(Pleb p: stage.plebs)
		{
			forces[stage.plebs.indexOf(p)][0] = 0;
			forces[stage.plebs.indexOf(p)][1] = 0;
			forces[stage.plebs.indexOf(p)][2] = 0;
			forces[stage.plebs.indexOf(p)][3] = 0;
			
			int fLimit = p.forceArchiver.size();
			for(int f = 0; f < fLimit; f++)
			{
				switch(p.forceArchiver.get(f).direction)
				{
					case 0:
						forces[stage.plebs.indexOf(p)][0] += p.forceArchiver.get(f).magnitude;
				//		p.forceArchiver.get(f).magnitude -= 0.5;
						break;
						
					case 1:
						forces[stage.plebs.indexOf(p)][1] += p.forceArchiver.get(f).magnitude;
				//		p.forceArchiver.get(f).magnitude -= 0.5;
						break;
						
					case 2:
						forces[stage.plebs.indexOf(p)][2] += p.forceArchiver.get(f).magnitude;
				//		p.forceArchiver.get(f).magnitude -= 0.5;
						break;
						
					case 3:
						forces[stage.plebs.indexOf(p)][3] += p.forceArchiver.get(f).magnitude;
				//		p.forceArchiver.get(f).magnitude -= 0.5;
						break;
				}
				
				if(p.forceArchiver.get(f).magnitude <= 0)
				{
					p.forceArchiver.remove(f);
					fLimit = p.forceArchiver.size();
					f--;
				}
			}
		}
		
		for(Pleb p: stage.plebs)
		{
			int x1 = p.xCoord+(int)(forces[stage.plebs.indexOf(p)][3]-forces[stage.plebs.indexOf(p)][1]+0.5*((forces[stage.plebs.indexOf(p)][3] > forces[stage.plebs.indexOf(p)][1])? 1:-1));
			int y1 = p.yCoord+(int)(forces[stage.plebs.indexOf(p)][0]-forces[stage.plebs.indexOf(p)][2]+0.5*((forces[stage.plebs.indexOf(p)][0] > forces[stage.plebs.indexOf(p)][2])? 1:-1));
			
			int z = -1;
			for(Prop h: stage.props)
			{
				boolean isEnemy = !(p.faction == h.faction);
				if(isEnemy)
				{
					int x2 = h.bounds.xCoord;
					int y2 = h.bounds.yCoord;	
					if(h.bounds.xDir != 0)
						x2 += h.bounds.xForward;
					else if(h.bounds.xDrag != 0)
						x2 += h.bounds.xDrift;
					if(h.bounds.yDir != 0)
						y2 -= h.bounds.yForward;
					else if(h.bounds.yDrag != 0)
						y2 -= h.bounds.yDrift;
					
					if(p.yCoord != h.bounds.yCoord+h.height && p.yCoord+p.height != h.bounds.yCoord && ((p.yCoord >= h.bounds.yCoord && p.yCoord < h.bounds.yCoord+h.height) || (p.yCoord+p.height > h.bounds.yCoord && p.yCoord+p.height <= h.bounds.yCoord+h.height) || (p.yCoord <= h.bounds.yCoord && p.yCoord+p.height >= h.bounds.yCoord+h.height)))
					{
						int x = p.xCoord;
						if(p.xCoord < x1)
						{
							for(int x0 = p.xCoord; x0 <= x1; x0++)
							{
								if(x1+p.width > x2 && (p.xCoord <= x2 || (p.xCoord+p.width >= x2 && p.xCoord+p.width<= x2+h.bounds.width)) && (x == p.xCoord || x < x2))
								{
									x = x2;
									z = stage.props.indexOf(h)+stage.puppets.size();
								}
							}
						}
						else if(p.xCoord > x1)
						{
							for(int x0 = p.xCoord; x0 >= x1; x0--)
							{
								if(x0 < x2+h.bounds.width && ((p.xCoord >= x2 && p.xCoord <= x2+h.bounds.width) || p.xCoord+p.width >= x2+h.bounds.width) && (x == p.xCoord || x > x2+h.bounds.width))
								{
									x = x2+h.bounds.width;
									z = stage.props.indexOf(h)+stage.puppets.size();
								}
							}
						}
						else
						{
							if((x1+p.width > x2 && p.xCoord <= x2) || (x1 < x2+h.bounds.width && p.xCoord+p.width >= x2+h.bounds.width))
								z = stage.props.indexOf(h)+stage.puppets.size();
						}
					}
					if(p.strength > 0 && p.xCoord != h.bounds.xCoord+h.bounds.width && p.xCoord+p.width != h.bounds.xCoord && ((p.xCoord >= h.bounds.xCoord && p.xCoord < h.bounds.xCoord+h.bounds.width) || (p.xCoord+p.width > h.bounds.xCoord && p.xCoord+p.width <= h.bounds.xCoord+h.bounds.width) || (p.xCoord <= h.bounds.xCoord && p.xCoord+p.width >= h.bounds.xCoord+h.bounds.width)))
					{
						int y = p.yCoord;
						if(p.yCoord > y1)
						{
							for(int y0 = p.yCoord; y0 >= y1; y0--)
							{
								if(y0 < y2+h.height && ((p.yCoord >= y2 && p.yCoord <= y2+h.height) || p.yCoord+p.height >= y2+h.height) && (y == p.yCoord || y > y2+h.height))
								{
									y = y2+h.height;
									z = stage.props.indexOf(h)+stage.puppets.size();
								}
							}
						}
						else if(p.yCoord < y1)
						{
							for(int y0 = p.yCoord; y0 <= y1; y0++)
							{
								if(y1+p.height > y2 && (p.yCoord <= y2 || (p.yCoord+p.height >= y2 && p.yCoord+p.height <= y2+h.height)) && (y == p.yCoord || y > y2))
								{
									y = y2;
									z = stage.props.indexOf(h)+stage.puppets.size();
								}
							}
						}
						else
						{
							if((y1+p.height > y2 && p.yCoord <= y2) || (y1 < y2+h.height && p.yCoord+p.height >= y2+h.height))
								z = stage.props.indexOf(h)+stage.puppets.size();
						}
					}
				}
			}
			
			for(Puppet h: stage.puppets)
			{
				boolean isEnemy = true;//!(p.faction == h.faction);
				if(isEnemy)
				{
					for(Organ o: h.anatomy)
					{
						int x2 = o.xCoord;
						int y2 = o.yCoord;	
						if(o.xDir != 0)
							x2 += o.xForward;
						else if(o.xDrag != 0)
							x2 += o.xDrift;
						if(o.yDir != 0)
							y2 -= o.yForward;
						else if(o.yDrag != 0)
							y2 -= o.yDrift;
						
						if(p.yCoord != o.yCoord+h.height && p.yCoord+p.height != o.yCoord && ((p.yCoord >= o.yCoord && p.yCoord < o.yCoord+h.height) || (p.yCoord+p.height > o.yCoord && p.yCoord+p.height <= o.yCoord+h.height) || (p.yCoord <= o.yCoord && p.yCoord+p.height >= o.yCoord+h.height)))
						{
							int x = p.xCoord;
							if(p.xCoord < x1)
							{
								for(int x0 = p.xCoord; x0 <= x1; x0++)
								{
									if(x1+p.width > x2 && (p.xCoord <= x2 || (p.xCoord+p.width >= x2 && p.xCoord+p.width<= x2+o.width)) && (x == p.xCoord || x < x2))
									{
										x = x2;
										z = stage.puppets.indexOf(h);
									}
								}
							}
							else if(p.xCoord > x1)
							{
								for(int x0 = p.xCoord; x0 >= x1; x0--)
								{
									if(x0 < x2+o.width && ((p.xCoord >= x2 && p.xCoord <= x2+o.width) || p.xCoord+p.width >= x2+o.width) && (x == p.xCoord || x > x2+o.width))
									{
										x = x2+o.width;
										z = stage.puppets.indexOf(h);
									}
								}
							}
							else
							{
								if((x1+p.width > x2 && p.xCoord <= x2) || (x1 < x2+o.width && p.xCoord+p.width >= x2+o.width))
									z = stage.puppets.indexOf(h);
							}
						}
						if(p.strength > 0 && p.xCoord != o.xCoord+o.width && p.xCoord+p.width != o.xCoord && ((p.xCoord >= o.xCoord && p.xCoord < o.xCoord+o.width) || (p.xCoord+p.width > o.xCoord && p.xCoord+p.width <= o.xCoord+o.width) || (p.xCoord <= o.xCoord && p.xCoord+p.width >= o.xCoord+o.width)))
						{
							int y = p.yCoord;
							if(p.yCoord > y1)
							{
								for(int y0 = p.yCoord; y0 >= y1; y0--)
								{
									if(y0 < y2+h.height && ((p.yCoord >= y2 && p.yCoord <= y2+h.height) || p.yCoord+p.height >= y2+h.height) && (y == p.yCoord || y > y2+h.height))
									{
										y = y2+h.height;
										z = stage.puppets.indexOf(h);
									}
								}
							}
							else if(p.yCoord < y1)
							{
								for(int y0 = p.yCoord; y0 <= y1; y0++)
								{
									if(y1+p.height > y2 && (p.yCoord <= y2 || (p.yCoord+p.height >= y2 && p.yCoord+p.height <= y2+h.height)) && (y == p.yCoord || y > y2))
									{
										y = y2;
										z = stage.puppets.indexOf(h);
									}
								}
							}
							else
							{
								if((y1+p.height > y2 && p.yCoord <= y2) || (y1 < y2+h.height && p.yCoord+p.height >= y2+h.height))
									z = stage.puppets.indexOf(h);
							}
						}
					}
				}
			}
			
			if(z != -1)
			{
				if(z < stage.puppets.size())
					stage.puppets.get(z).takeDamage(p);
				else
					stage.props.get(z-stage.puppets.size()).takeDamage(p);
				p.strength = 0;
			}
			
			boolean inBounds = false;
			for(Floor f: stage.floors)
			{
				if(p.xCoord != f.xCoord+f.width && p.xCoord+p.width != f.xCoord && ((p.xCoord+p.width > f.xCoord && p.xCoord < f.xCoord+f.width)))
				{
					if(p.yCoord != f.yCoord+f.height && p.yCoord+p.height != f.yCoord && ((p.yCoord+p.height > f.yCoord && p.yCoord < f.yCoord+f.height)))
					{
						inBounds = true;
					}
				}
			}
			if(!inBounds)
				p.strength = 0;
			
			p.xCoord += (int)(forces[stage.plebs.indexOf(p)][3]-forces[stage.plebs.indexOf(p)][1]+0.5*((forces[stage.plebs.indexOf(p)][3] > forces[stage.plebs.indexOf(p)][1])? 1:-1));
			p.yCoord += (int)(forces[stage.plebs.indexOf(p)][0]-forces[stage.plebs.indexOf(p)][2]+0.5*((forces[stage.plebs.indexOf(p)][0] > forces[stage.plebs.indexOf(p)][2])? 1:-1));
		}
	}
	
	public void applyNewtonsThird(int x, int y, Organ j, ArrayList<Organ> h)
	{
		int xClosest = 0;
		int yClosest = 0;
		int hIndex = -1;
		
		for(Organ i: h)
		{
			if(i != j)
			{
				if(x > 0 && j.xCoord+j.width+x >= i.xCoord && j.xCoord < i.xCoord && (xClosest == 0 || Math.abs(xClosest) > Math.abs(j.xCoord+j.width+x-i.xCoord)))
				{
					xClosest = j.xCoord+j.width+x-i.xCoord;
					hIndex = h.indexOf(i);
				}
				else if(x < 0 && j.xCoord+x <= i.xCoord+i.width && j.xCoord > i.xCoord && (xClosest == 0 || Math.abs(xClosest) > Math.abs(j.xCoord+x-(i.xCoord+i.width))))
				{
					xClosest = j.xCoord+x-(i.xCoord+i.width);
					hIndex = h.indexOf(i);
				}
				
				if(y > 0 && j.yCoord+j.height+y >= i.yCoord && j.yCoord < i.yCoord && (yClosest == 0 || Math.abs(yClosest) > Math.abs(j.yCoord+j.height+y-i.height)))
				{
					yClosest = j.yCoord+j.height+y-i.height;
					hIndex = h.indexOf(i);
				}
				else if(y < 0 && j.yCoord+y <= i.yCoord+i.height && j.yCoord > i.yCoord && (yClosest == 0 || Math.abs(yClosest) > Math.abs(j.yCoord+y-(i.yCoord+i.height))))
				{
					yClosest = j.yCoord+y-(i.yCoord+i.height);
					hIndex = h.indexOf(i);
				}
			}
		}
		
		if(hIndex == -1)
		{
			boolean xBlocked = false;
			boolean yBlocked = false;
			for(Organ o: h)
			{
				if(o != j)
				{
					if(j.yCoord != o.yCoord+o.height && j.yCoord+j.height != o.yCoord && ((j.yCoord >= o.yCoord && j.yCoord < o.yCoord+o.height) || (j.yCoord+j.height > o.yCoord && j.yCoord+j.height <= o.yCoord+o.height) || (j.yCoord <= o.yCoord && j.yCoord+j.height >= o.yCoord+o.height) /*|| (y1 == y2 || y1+j.height == y2+o.height)*/))
					{
						if(x > 0)
						{
							for(int z = j.xCoord; z <= j.xCoord+x; z++)
							{
								if(z+j.width > o.xCoord && j.xCoord <= o.xCoord && !(o.isMovable && o.xCoord < 0 && o.xCoord+x < 0))
								{
									if(j.blocked[1] > o.xCoord || j.xCoord+j.width > o.xCoord || j.blocked[1] == j.xCoord+j.width/2)
										j.xCoord = o.xCoord-j.width;
									z = j.xCoord+x;
								}
							}
							
							if(j.xCoord == o.xCoord-j.width)
							{
								j.blocked[1] = o.xCoord;
								o.blocked[3] = j.xCoord+j.width;
								xBlocked = true;
							}
						}
						else if(x < 0)
						{
							for(int z = j.xCoord; z >= j.xCoord+x; z--)
							{
								if(z < o.xCoord+o.width && j.xCoord+j.width >= o.xCoord+o.width)
								{
									if(j.blocked[3] < o.xCoord+o.width || j.xCoord < o.xCoord+o.width || j.blocked[3] == j.xCoord+j.width/2)
										j.xCoord = o.xCoord+o.width;
									z = j.xCoord+x;
								}
							}
							
							if(j.xCoord == o.xCoord+o.width)
							{
								j.blocked[3] = o.xCoord+o.width;
								o.blocked[1] = j.xCoord;
								xBlocked = true;
							}
						}
					}
				/*	else
					{
						j.blocked[1] = j.xCoord+j.width/2;
						j.blocked[3] = j.xCoord+j.width/2;
					}*/
					
					if(j.xCoord != o.xCoord+o.width && j.xCoord+j.width != o.xCoord && ((j.xCoord >= o.xCoord && j.xCoord < o.xCoord+o.width) || (j.xCoord+j.width > o.xCoord && j.xCoord+j.width <= o.xCoord+o.width) || (j.xCoord <= o.xCoord && j.xCoord+j.width >= o.xCoord+o.width) /*|| (x1 == x2 || x1+j.height == x2+o.height)*/))
					{
						if(y < 0)
						{
							if((j.xCoord < o.xCoord+o.width && j.blocked[3] != o.xCoord+o.width) || (j.xCoord+j.width > o.xCoord && j.blocked[1] != o.xCoord))
							{
								for(int z = j.yCoord; z >= y; z--)
								{
									if(z < o.yCoord+o.height && j.yCoord+j.height >= o.yCoord+o.height)
									{
										if(j.blocked[0] < o.yCoord+o.height || j.yCoord < o.yCoord+o.height || j.blocked[0] == j.yCoord+j.height/2)
											j.yCoord = o.yCoord+o.height-(int)(gravity.magnitude+0.5)*((o.isGrounded)? 0:1);
										z = y;
									}
								}
								
								if(j.yCoord == o.yCoord+o.height-(int)(gravity.magnitude+0.5)*((o.isGrounded)? 0:1))
								{
									j.blocked[0] = o.yCoord+o.height-(int)(gravity.magnitude+0.5)*((o.isGrounded)? 0:1);
									yBlocked = true;
								}
							}
						}
						else if(y > 0)
						{
							if((j.xCoord < o.xCoord+o.width && j.blocked[3] != o.xCoord+o.width) || (j.xCoord+j.width > o.xCoord && j.blocked[1] != o.xCoord))
							{
								for(int z = j.yCoord; z <= y; z++)
								{
									if(z+j.height >= o.yCoord && j.yCoord <= o.yCoord)
									{
										if(j.blocked[2] > o.yCoord || j.yCoord+j.height > o.yCoord || j.blocked[2] == j.yCoord+j.height/2)
											j.yCoord = o.yCoord-j.height-(int)(gravity.magnitude+0.5)*((o.isGrounded)? 0:1);
										z = y;
									}
								}
								
								if(j.yCoord == o.yCoord-j.height-(int)(gravity.magnitude+0.5)*((o.isGrounded)? 0:1))
								{
									j.blocked[2] = o.yCoord-(int)(gravity.magnitude+0.5)*((o.isGrounded)? 0:1);
									yBlocked = true;
								}
							}
						}
					}
					
					if((j.xCoord >= o.xCoord && j.xCoord <= o.xCoord+o.width) || (j.xCoord+j.width >= o.xCoord && j.xCoord+j.width <= o.xCoord+o.width) || (j.xCoord <= o.xCoord && j.xCoord+j.width >= o.xCoord+o.width))
					{
						if((j.yCoord >= o.yCoord && j.yCoord <= o.yCoord+o.height) || (j.yCoord+j.height >= o.yCoord && j.yCoord+j.height <= o.yCoord+o.height) || (j.yCoord <= o.yCoord && j.yCoord+j.height >= o.yCoord+o.height))
						{
							int[] t = new int[]{-1,-1};
							if(h.indexOf(o) < stage.puppets.size())
								t = new int[]{0,stage.puppets.get(h.indexOf(o)).id};
							else
								t = new int[]{1,stage.props.get(h.indexOf(o)-stage.puppets.size()).id};
							if(t[0] != -1)
							{
								if(h.indexOf(j) < stage.puppets.size())
									stage.puppets.get(h.indexOf(j)).touchArchiver.add(t);
								else
									stage.props.get(h.indexOf(j)-stage.puppets.size()).touchArchiver.add(t);
							}
						}
					}
					/*	else
					{
						j.blocked[0] = j.yCoord+j.height/2;
						j.blocked[2] = j.yCoord+j.height/2;
					}*/
				}
			}
			
		/*	x1 += (int)(forces[h.indexOf(j)][3]-forces[h.indexOf(j)][1]+0.5);
			y1 += (int)(forces[h.indexOf(j)][0]-forces[h.indexOf(j)][2]+0.5);
			if(j.xDir != 0)
				x1 += j.xForward;
			else if(j.xDrag != 0)
				x1 += j.xDrift;
			if(j.yDir != 0)
				y1 -= j.yForward;
			else if(j.yDrag != 0)
				y1 -= j.yDrift;	*/
			
			for(Floor f: stage.floors)
			{
				if((j.xCoord+j.width+x > f.xCoord && j.xCoord+x < f.xCoord+f.width) || (j.xCoord+j.width > f.xCoord && j.xCoord < f.xCoord+f.width))
				{
					if((j.yCoord+j.height+y > f.yCoord && j.yCoord+y < f.yCoord+f.height) || (j.yCoord+j.height > f.yCoord && j.yCoord < f.yCoord+f.height))
					{
						if(x > 0)
						{
							for(int z = j.xCoord; z <= j.xCoord+x; z++)
							{
								for(int[] w: f.walls[1])
								{
									if((j.yCoord+y > w[0] && j.yCoord+y < w[1] && (j.yCoord < w[1] || j.blocked[0] != w[1])) || (j.yCoord+j.height+y > w[0] && j.yCoord+j.height+y <= w[1] && (j.yCoord+j.height > w[0] || j.blocked[2] != w[0])))
									{
										if(z+j.width >= f.xCoord+f.width && j.xCoord <= f.xCoord+f.width /*&& !xBlocked*/)
										{
											if(j.blocked[1] > f.xCoord+f.width  || j.xCoord+j.width > f.xCoord+f.width || j.blocked[1] == j.xCoord+j.width/2)
												j.xCoord = f.xCoord+f.width-j.width;	//f.xCoord+f.width-j.xCoord-j.width+j.xCoord;
											j.xVel = 0;
											z = j.xCoord+x;
										}
										
										if(j.xCoord == f.xCoord+f.width-j.width)
										{
											j.blocked[1] = f.xCoord+f.width;
											xBlocked = true;
										}
									}
								}
							}
						/*	if(!xBlocked)
							{
								x1 += (int)(forces[h.indexOf(j)][3]-forces[h.indexOf(j)][1]+0.5);
								j.xCoord += (int)(forces[h.indexOf(j)][3]-forces[h.indexOf(j)][1]+0.5);
							}*/
						}
						
						if(x < 0)
						{
							for(int z = j.xCoord; z >= j.xCoord+x; z--)
							{
								for(int[] w: f.walls[3])
								{
									if((j.yCoord+y > w[0] && j.yCoord+y < w[1] && (j.yCoord < w[1] || j.blocked[0] != w[1])) || (j.yCoord+j.height+y > w[0] && j.yCoord+j.height+y <= w[1] && j.blocked[2] != w[0]))
									{
										if(z < f.xCoord && j.xCoord+j.width >= f.xCoord /*&& !xBlocked*/)
										{
											if(j.blocked[3] < f.xCoord || j.xCoord < f.xCoord || j.blocked[3] == j.xCoord+j.width/2)
												j.xCoord = f.xCoord;	//f.xCoord-j.xCoord+j.xCoord;
											j.xVel = 0;
											z = j.xCoord+x;
										}
										
										if(j.xCoord == f.xCoord)
										{
											j.blocked[3] = f.xCoord;
											xBlocked = true;
										}
									}
								}
							}
					/*		if(!xBlocked)
							{
								x += (int)(forces[h.indexOf(j)][3]-forces[h.indexOf(j)][1]+0.5);
								j.xCoord += (int)(forces[h.indexOf(j)][3]-forces[h.indexOf(j)][1]+0.5);
							}*/
						}
						
						if(y < 0)
						{
							for(int z = j.yCoord; z >= j.yCoord+y; z--)
							{
								for(int[] w: f.walls[0])
								{
									if((j.xCoord+x > w[0] && j.xCoord+x < w[1] && (j.xCoord < w[1] || j.blocked[3] != w[1])) || (j.xCoord+j.width+x > w[0] && j.xCoord+j.width+x < w[1] && (j.xCoord+j.width > w[0] || j.blocked[1] != w[0])))
									{
										if(z < f.yCoord && j.yCoord+j.height >= f.yCoord /*&& !yBlocked*/)
										{
											if(j.blocked[0] < f.yCoord  || j.yCoord < f.yCoord || j.blocked[0] == j.yCoord+j.height/2)
												j.yCoord = f.yCoord;	//f.yCoord-j.yCoord+j.yCoord;
											j.yVel = 0;
											z = j.yCoord+y;
										}
										
										if(j.yCoord == f.yCoord)
										{
											j.blocked[0] = f.yCoord;
											yBlocked = true;
										}
									}
								}
							}
						/*	if(!yBlocked)
							{
								y += (int)(forces[h.indexOf(j)][0]-forces[h.indexOf(j)][2]+0.5);
								j.yCoord += (int)(forces[h.indexOf(j)][0]-forces[h.indexOf(j)][2]+0.5);
							}*/
						}
						
						if(y > 0)
						{
							for(int z = j.yCoord; z <= j.yCoord+y; z++)
							{
								for(int[] w: f.walls[2])
								{
									if((j.xCoord+x > w[0] && j.xCoord+x < w[1] && (j.xCoord < w[1] || j.blocked[3] != w[1])) || (j.xCoord+j.width+x > w[0] && j.xCoord+j.width+x < w[1] && (j.xCoord+j.width > w[0] || j.blocked[1] != w[0])))
									{
										if(z+j.height >= f.yCoord+f.height && j.yCoord <= f.yCoord+f.height /*&& !yBlocked*/)
										{
											if(j.blocked[2] > f.yCoord+f.height || j.yCoord+j.height > f.yCoord+f.height || j.blocked[2] == j.yCoord+j.height/2)
												j.yCoord = f.yCoord+f.height-j.height;	//f.yCoord+f.height-j.yCoord-j.height+stage.puppets.get(h.indexOf(j)).bounds.yCoord;
											j.yVel = 0;
											z = j.yCoord+y;
										}
										
										if(j.yCoord == f.yCoord+f.height-j.height)
										{
											j.blocked[2] = f.yCoord+f.height;
											yBlocked = true;
										}
									}
								}
							}
						/*	if(!yBlocked)
							{
								y += (int)(forces[h.indexOf(j)][0]-forces[h.indexOf(j)][2]+0.5);
								j.yCoord += (int)(forces[h.indexOf(j)][0]-forces[h.indexOf(j)][2]+0.5);
							}*/
						}
					}
				}
			}
			
			if(!xBlocked)
			{
				j.xCoord += x;
				j.blocked[1] = j.xCoord+j.width/2;
				j.blocked[3] = j.xCoord+j.width/2;
			}
			if(!yBlocked)
			{
				j.yCoord += y;
				j.blocked[0] = j.yCoord+j.height/2;
				j.blocked[2] = j.yCoord+j.height/2;

			}
			
		//	xBlocked = false;
		//	yBlocked = false;
		}
		else
		{
			if(Math.abs(xClosest) < Math.abs(yClosest) || yClosest == 0)
			{
				applyNewtonsThird(xClosest,0,h.get(hIndex),h);
				if(xClosest > 0)
					j.xCoord = h.get(hIndex).xCoord-j.width;
				else
					j.xCoord = h.get(hIndex).xCoord+h.get(hIndex).width;
			}
			else
			{
				applyNewtonsThird(0,yClosest,h.get(hIndex),h);
				if(yClosest > 0)
					j.yCoord = h.get(hIndex).yCoord-j.height;
				else
					j.yCoord = h.get(hIndex).yCoord+h.get(hIndex).height;
			}
		}
	}
	
	public void checkDamage()
	{
		ArrayList<Hitbox> hitboxes = new ArrayList<Hitbox>();
		for(Puppet p: stage.puppets)
		{
			for(Hitbox h: p.anatomy)
				hitboxes.add(h);
		}
		for(Prop p: stage.props)
		{
			hitboxes.add(p.bounds);
		}
		
		for(Pleb p1: stage.plebs)
		{
			for(Hitbox h: hitboxes)
			{
				if((p1.xCoord >= h.xCoord && p1.xCoord < h.xCoord+h.width) || (p1.xCoord+p1.width > h.xCoord && p1.xCoord+p1.width <= h.xCoord+h.width))
				{
					if((p1.yCoord >= h.yCoord && p1.yCoord < h.yCoord+h.height) || (p1.yCoord+p1.height > h.yCoord && p1.yCoord+p1.height <= h.yCoord+h.height))
					{
						if(p1.strength < 0)
							p1.strength = 0;
							
						for(Puppet p2: stage.puppets)
						{
		/*					if(!p1.faction.equals(stage.puppets.get(stage.puppets.indexOf(p2)).faction) && p2.anatomy.indexOf(h) != -1)
							{
						//		if(p2.anatomy.get(p2.anatomy.indexOf(h)).vulnerableTo[p1.type])
								stage.puppets.get(stage.puppets.indexOf(p2)).takeDamage(p1);
							}
							*/
						}
						for(Prop p2: stage.props)
						{
							if(!p1.faction.equals(stage.props.get(stage.props.indexOf(p2)).faction)	/* && p2.anatomy.indexOf(h) != -1*/)
							{
						//		if(p2.anatomy.get(p2.anatomy.indexOf(h)).vulnerableTo[p1.type])
								stage.props.get(stage.props.indexOf(p2)).takeDamage(p1);
							}
						}
					}
				}
			}
		}
	}
	
	public void update(int x, int y, boolean g/*, int w, int h*/)
	{
		xWindow = x;
		yWindow = y;
	/*	winWidth = w;
		winHeight = h;*/
		gamePaused = g;
		
		if(!gamePaused)
		{
			for(Floor f: stage.floors)
				f.update(stage.floors);
			for(Puppet p: stage.puppets)
				p.getHitboxes();
			
			applyForces();
			checkCollisions();
			
			for(Puppet p: stage.puppets)
				p.getHitboxes();
	//		checkDamage();	//MIGHT BE UNNECESSARY, might actually need to keep hitboxes aligned if forces push bounds in which case remove getHitboxes() prior to applyForces()
			
			stage.update();
		//	stage.updateTrail();
		//	stage.updatePoints();
			
			int pLimit = stage.props.size();
			for(int p= 0; p < pLimit; p++)
			{
				stage.props.get(p).update();
		/*		if(stage.props.get(p).bounds.isMoving || stage.props.get(p).bounds.wasMoving || stage.props.get(p).health == 0)
					stage.updateTrail(p);
		*/		
				if(stage.props.get(p).health == 0)
				{
					stage.props.remove(p);
					pLimit = stage.props.size();
					p--;
				}
			}
			
			pLimit = stage.puppets.size();
			for(int p = 0; p < pLimit; p++)
			{
				stage.puppets.get(p).checkState();
				stage.puppets.get(p).update();
				
				if(stage.puppets.get(p).health == 0)
				{
					stage.puppets.remove(p);
					pLimit = stage.puppets.size();
					p--;
				}
			}
			
			pLimit = stage.plebs.size();
			for(int p = 0; p < pLimit; p++)
			{
				stage.plebs.get(p).move();
				stage.plebs.get(p).update();
				
				if(stage.plebs.get(p).strength == 0)
				{
					stage.plebs.remove(p);
					pLimit = stage.plebs.size();
					p--;
				}
			/*	if(p.xVel > 0 || p.yVel > 0)
					stage.updateTrail("pleb",stage.plebs.indexOf(p));*/
			}
			
			if(stage.player1 != null && stage.player2 != null)
			{
				if(stage.player1.bounds.isGrounded && stage.player2.bounds.isGrounded)
				{
					stage.player1.isFacingRight = (stage.player1.xCoord+stage.player1.width/2 <= stage.player2.xCoord+stage.player2.width/2)? true:false;
					stage.player2.isFacingRight = (stage.player2.xCoord+stage.player2.width/2 <= stage.player1.xCoord+stage.player1.width/2)? true:false;
				}
				resetFocus();
				setFocus();
			}
			focus();
		}
		
		boolean p = false;
		for(Hand h: hands)
		{
			if(h.player != null)
			{
				h.pullStrings(xWindow,yWindow);
				if(h.buttonArchiver[9])
				{
					p = true;
					h.buttonArchiver[9] = false;
				}
			}
		}
		if(p)
			gamePaused = !gamePaused;
	}
}