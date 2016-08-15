abstract class Action
{
	public static final int NORMAL = 0;
	public static final int SPECIAL = 1;
	public static final int SUPER = 2;
	public static final int DASH = 3;
	public static final int JUMP = 4;
	
	int[] buttonPath, cancelWindow;
	int button, type, cancelType, frames;
	boolean isSpecialCancelable, isSuperCancelable, isDashCancelable, isJumpCancelable;
	
	public Action(int t, int ct, int f, int[] b, boolean[] c, int[] cw)
	{
		button = -1;
		type = t;
		cancelType = ct;	// 0 = on whiff, 1 = on block, 2 = on hit
		frames = f;
		
		isSpecialCancelable = c[0];
		isSuperCancelable =	c[1];
		isDashCancelable =	c[2];
		isJumpCancelable =	c[3];
		
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
	
	abstract void perform(int f);
}