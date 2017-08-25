abstract class Action
{
	public static final int NORMAL = 0;
	public static final int SPECIAL = 1;
	public static final int SUPER = 2;
	public static final int DASH = 3;
	public static final int JUMP = 4;
	
	Puppet target;
	int[] buttonPath, cancelWindow;
	int button, type, cancelType, frames;
	boolean[] isSpecialCancelable, isSuperCancelable, isDashCancelable, isJumpCancelable;
	boolean groundOk, airOk, cLock;
	String hashCounter;
	
	public Action(int t, int ct, int[] b, boolean[] c1, boolean[] c2, boolean[] c3, boolean[] c4, int[] cw, boolean[] ok)
	{
		target = null;
		hashCounter = "";
		button = -1;
		type = t;
		cancelType = ct;	// 0 = on whiff, 1 = on block, 2 = on hit
		frames = 1;
		
		isSpecialCancelable = c1;
		isSuperCancelable =	c2;
		isDashCancelable =	c3;
		isJumpCancelable =	c4;
		
		groundOk = ok[0];
		airOk = ok[1];
		cLock = false;
		
		buttonPath = b;
		cancelWindow = cw;
	}
	
	
	public boolean isCancelable(int c1, int f, int t, int b, boolean g)
	{
		if(cancelType <= c1)
		{
			if((g && (!cLock && f >= cancelWindow[0] && f < cancelWindow[1]) || (cLock && f >= cancelWindow[2] && f < cancelWindow[3])) || (!g && f >= cancelWindow[4] && f < cancelWindow[5]))
			{
				if(t == Action.NORMAL)
				{
					for(int p: buttonPath)
					{
						if(p == b)
							return true;
					}
				}
				else
				{
					int i = (!g)? 2:((!cLock)? 0:1);
					boolean[][] cancel = new boolean[][]{isSpecialCancelable, isSuperCancelable, isDashCancelable, isJumpCancelable};
					if(cancel[t-1][i])
						return true;
				}
			}
		}
		return false;
	}
	
	protected void addPleb(Puppet pu, int hc, int x, int y, int w, int h, int d1, int d2, int s, int hd, int sd, int kx, int ky, double hs, boolean ia, boolean ip, double[][] pr)
	{
		Pleb p = new Pleb(pu,pu.bounds,this,x,y,w,h,d1,d2,s,hd,sd,kx,ky,hs,ia,ip,pr);
		
		if(hashCounter.equals(""))
			hashCounter = p.toString()+hc;
		else if(Integer.parseInt(hashCounter.substring(hashCounter.length()-1)) != hc)
			hashCounter = p.toString()+hc;
		p.hash = hashCounter;
		
		pu.plebsOut.add(p);
	}
	
	abstract void perform(int f);
}