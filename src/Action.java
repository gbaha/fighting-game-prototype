abstract class Action
{
	public static final int NORMAL = 0;
	public static final int SPECIAL = 1;
	public static final int SUPER = 2;
	public static final int DASH = 3;
	public static final int JUMP = 4;
	public static final int GRAB = 5;
	public static final int TAUNT = 6;
	
	Puppet target;
	int[][] buttonPath;
	int[] cancelWindow, cost;
	int button, type, cancelType, frames, superflash;
	double scaling;
	boolean[] isSpecialCancelable, isSuperCancelable, isDashCancelable, isJumpCancelable;
	boolean cancelOk, groundOk, airOk, aLock, cLock;
	String hashCounter, guardCounter;
	
	public Action(int t, int ct, int[][] b, boolean[] c1, boolean[] c2, boolean[] c3, boolean[] c4, int[] cw, boolean[] ok)
	{
		target = null;
		hashCounter = "";
		guardCounter = "";
		button = -1;
		type = t;
		cancelType = ct;	// 0 = on whiff, 1 = on block, 2 = on hit
		frames = 1;
		superflash = 0;
		scaling = 0;
		
		isSpecialCancelable = c1;
		isSuperCancelable =	c2;
		isDashCancelable =	c3;
		isJumpCancelable =	c4;
		
		cancelOk = true;
		groundOk = ok[0];
		airOk = ok[1];
		aLock = ok[2];
		cLock = false;
		
		buttonPath = b;
		cancelWindow = cw;
		cost = new int[2];	// [stamina, meter]
	}
	
	
	public boolean isCancelable(int c1, int f, int t, int b, boolean g)
	{
		if(cancelOk)
		{
			if((type == Action.NORMAL || type == Action.DASH || type == Action.JUMP) && t == Action.TAUNT)
				return true;
			else if(type == Action.NORMAL && t == Action.GRAB)
				return (f < 2);
			else
			{
				if(cancelType <= c1)
				{
					if((g && (!cLock && f >= cancelWindow[0] && f < cancelWindow[1]) || (cLock && f >= cancelWindow[2] && f < cancelWindow[3])) || (!g && f >= cancelWindow[4] && f < cancelWindow[5]))
					{
						int i = (!g)? 2:((!cLock)? 0:1);
						if(t == Action.NORMAL)
						{
							for(int p: buttonPath[i])
							{
								if(p == b)
									return true;
							}
						}
						else
						{
							boolean[][] cancel = new boolean[][]{isSpecialCancelable, isSuperCancelable, isDashCancelable, isJumpCancelable, isSpecialCancelable, new boolean[]{false,false,false}};
							if(cancel[t-1][i])
								return true;
						}
					}
				}
			}
		}
		return false;
	}
	
	protected void addPleb(Puppet pu, int hc, int x, int y, int w, int h, int d, int t, int s, int hd, int sd, int kx, int ky, int j, double hs, boolean ia, boolean ip, boolean pb, boolean a, double[][] pr)
	{
		Pleb p = new Pleb(pu,pu.bounds,(a)? this:null,x,y,w,h,d,t,s,hd,sd,kx,ky,j,hs,ia,ip,pb,pr);
		
		if(hashCounter.equals(""))
			hashCounter = p.toString()+"p"+hc;
		else if(Integer.parseInt(hashCounter.substring(hashCounter.lastIndexOf('p')+1)) != hc)
			hashCounter = p.toString()+"p"+hc;
		p.hash = hashCounter;
		
		pu.plebsOut.add(p);
	}
	
	protected void addGrab(Puppet pu, int x, int y, int w, int h, int d, boolean r, boolean ia, boolean a)
	{
		Pleb p = new Pleb(pu,pu.bounds,(a)? this:null,x,y,w,h,d,Pleb.GRAB,r,ia);
		hashCounter = "huggiesp0";
		p.hash = hashCounter;
		pu.plebsOut.add(p);
	}
	
	protected void addGuardTrigger(Puppet pu, int hc, int x, int y, int w, int h, int d, boolean r, boolean ia, boolean a)
	{
		Pleb p = new Pleb(pu,pu.bounds,(a)? this:null,x,y,w,h,d,Pleb.GUARD,r,ia);
		
		if(guardCounter.equals(""))
			guardCounter = p.toString()+"g"+hc;
		else if(Integer.parseInt(guardCounter.substring(guardCounter.lastIndexOf('g')+1)) != hc)
			guardCounter = p.toString()+"g"+hc;
		p.hash = guardCounter;
		
		pu.plebsOut.add(p);
	}
	
	abstract void perform(int f);
}