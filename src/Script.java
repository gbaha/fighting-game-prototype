abstract class Script
{
	Stage stage;
	int length;
	boolean isLocked;
	
	public Script(Stage s, int l1, boolean l2)
	{
		stage = s;
		length = l1;
		isLocked = l2;
	}
	
	abstract void perform(int f);
}