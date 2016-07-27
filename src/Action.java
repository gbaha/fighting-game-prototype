abstract class Action
{
	public static final int NORMAL = 0;
	public static final int SPECIAL = 1;
	public static final int SUPER = 2;
	public static final int DASH = 3;
	public static final int JUMP = 4;
	
	int type, frames;
	boolean isNormalCancelable, isSpecialCancelable, isSuperCancelable, isDashCancelable, isJumpCancelable;
	
	public Action(int t, int f, boolean[] c)
	{
		type = t;
		frames = f;
		isNormalCancelable = c[0];
		isSpecialCancelable = c[1];
		isSuperCancelable =	c[2];
		isDashCancelable =	c[3];
		isJumpCancelable =	c[4];
	}
	
	
	public boolean isCancelable(int t)	//Change to accept chain and stamina cancels later
	{
		boolean[] c = new boolean[]{isNormalCancelable, isSpecialCancelable, isSuperCancelable, isDashCancelable, isJumpCancelable};
		if(c[t])
			return true;
		return false;
	}
	
	abstract void perform(int f);
}