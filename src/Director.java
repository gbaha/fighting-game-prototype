import java.util.ArrayList;

public class Director
{
	Stage stage;
	Beaman geebs;
	ArrayList<Task> tasks;
	
	public Director(Stage s, Beaman g)
	{
		stage = s;
		geebs = g;
		tasks = new ArrayList<Task>();
		
		//TEST
		tasks.add(new Task(Task.KILL,new int[]{2,1,2,3,4,5,6,7,8,9,10,11,12,13},new int[]{Beaman.MOVE,1,34,0,10,15}));
	}
	
	
	public void update()
	{
		for(Task t: tasks)
		{
			t.update();
			if(t.isCompleted[0] && !t.isCompleted[1])
			{
				geebs.addComm(t.reaction);
				t.isCompleted[1] = true;
			}
		}
	}
	
	
	private class Task
	{
		private static final int MOVE = 0;
		private static final int KILL = 1;
		private static final int BREAK = 2;
		private static final int OBTAIN = 3;
		
		int action;
		int[] marks, reaction;
		boolean[] isCompleted;
		
		public Task(int a, int[] m, int[] r)
		{
			action = a;
			marks = m;
			reaction = r;
			
			isCompleted = new boolean[2];
			isCompleted[0] = false;
			isCompleted[1] = false;
		}
		
		
		public void update()
		{
			switch(action)
			{
				case 0:
					//MOVE TO (walk into rectangle) {xCoord,yCoord,width,height}
					break;
				
				case 1:
					//KILL (defeat puppets) {lethal/nonlethal/both,marked enemies...}
					// 0 = nonlethal, 1 = lethal, 2 = both
					ArrayList<Puppet> m = new ArrayList<Puppet>();
					for(int n = 1; n < marks.length; n++)
					{
						if(marks[n] != -1)
						{
							boolean idMatched = false;
							for(Puppet p: stage.puppets)
							{
								if(marks[n] == p.id)
								{
									m.add(p);
									idMatched = true;
								}
							}
							if(!idMatched)
								marks[n] = -1;
						}
					}
					
					int mLimit = m.size();
					if(marks[0] == 0)
					{
						for(int o = 0; o < mLimit; o++)
						{
							if(m.get(o).stamina <= 0)
							{
								m.remove(o);
								mLimit = m.size();
								o--;
							}
						}
					}
					else if(marks[0] == 1)
					{
						for(int o = 0; o < mLimit; o++)
						{
							if(m.get(o).health <= 0)
							{
								m.remove(o);
								mLimit = m.size();
								o--;
							}
						}
					}
					else if(marks[0] == 2)
					{
						for(int o = 0; o < mLimit; o++)
						{
							if(m.get(o).health <= 0 || m.get(o).stamina <= 0)
							{
								m.remove(o);
								mLimit = m.size();
								o--;
							}
						}
					}
					
					if(m.size() == 0)
						isCompleted[0] = true;
				break;
				
				case 2:
					//BREAK (defeat props) {marked props...}
					break;
				
				case 3:
					//OBTAIN (pick up items)
					break;
			}
		}
	}
}