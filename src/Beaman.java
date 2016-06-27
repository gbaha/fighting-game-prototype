//A GOD BOUND BY RULES
import java.util.ArrayList;

public class Beaman
{
	public static final int MOVE = 0;
	
	Stage stage;
	ArrayList<Command> commands;
	
	public Beaman(Stage s)
	{
		stage = s;
		commands = new ArrayList<Command>();
	}
	
	
	public void defyLogic()
	{
		int cLimit = commands.size();
		for(int c = 0; c < cLimit; c++)
		{
			commands.get(c).update();
			if(commands.get(c).isCompleted)
			{
				commands.remove(c);
				cLimit = commands.size();
				c--;
			}
		}
	}
	
	public void addComm(int[] c)
	{
		int[] p = new int[c.length-3];
		for(int o = 3; o < c.length; o++)
			p[o-3] = c[o];
		
		commands.add(new Command(c[0],c[1],c[2],p));
	}
	
	
	private class Command
	{
		int action, type, id;
		int[] params;
		boolean isCompleted;
		
		public Command(int a, int t, int i, int[] p)
		{
			action = a;
			type = t;	//PUPP = 0, PROP = 1
			id = i;
			params = p;
			isCompleted = false;
		}
		
		
		public void update()
		{
			switch(action)
			{
				case 0:
					if(params[2] > 0)
					{
						if(type == 0)
						{
							for(Puppet p: stage.puppets)
							{
								if(id == p.id)
								{
									p.bounds.xCoord += params[0];
									p.bounds.yCoord += params[1];
								}
							}
						}
						else if(type == 1)
						{
							for(Prop p: stage.props)
							{
								if(id == p.id)
								{
									p.bounds.xCoord += params[0];
									p.bounds.yCoord += params[1];
								}
							}
						}
						params[2]--;
					}
					else
						isCompleted = true;
					break;
			}
		}
	}
}