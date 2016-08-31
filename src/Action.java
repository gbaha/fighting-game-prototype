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
	boolean isSpecialCancelable, isSuperCancelable, isDashCancelable, isJumpCancelable, groundOk, airOk;
	String hashCounter;
	
	public Action(int t, int ct, int f, int[] b, boolean[] c, int[] cw, boolean[] ok)
	{
		target = null;
		hashCounter = "";
		button = -1;
		type = t;
		cancelType = ct;	// 0 = on whiff, 1 = on block, 2 = on hit
		frames = f;
		
		isSpecialCancelable = c[0];
		isSuperCancelable =	c[1];
		isDashCancelable =	c[2];
		isJumpCancelable =	c[3];
		
		groundOk = ok[0];
		airOk = ok[1];
		
		buttonPath = b;
		cancelWindow = cw;
	}
	
	
	public boolean isCancelable(int c, int f, int t, int b)	//Change to stamina cancels later
	{
		if(cancelType <= c && f >= cancelWindow[0] && f < cancelWindow[1])
		{
			if(t == 0)
			{
				for(int p: buttonPath)
				{
					if(p == b)
						return true;
				}
			}
			else
			{
				boolean[] cancel = new boolean[]{isSpecialCancelable, isSuperCancelable, isDashCancelable, isJumpCancelable};
				if(cancel[t-1])
					return true;
			}
		}
		return false;
	}
	
	protected void addPleb(Puppet pu, int hc, int x, int y, int w, int h, int d1, int d2, int s, int hd, int sd, int kx, int ky, double hs, boolean ia, boolean ip, int[] pr)
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