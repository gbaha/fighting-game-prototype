import java.util.ArrayList;
import java.util.LinkedList;
import javax.sound.sampled.*;
 
public class Klamoth	//SOUND AND SHIT
{
	LinkedList<Clip> beats;
	LinkedList<String> queue;
	Clip fire;
	
	public Klamoth()
	{
		beats = new LinkedList<Clip>();
		queue = new LinkedList<String>();
	}
	
	public void buildQueue(Stage s)
	{
		if(fire != null)
		{
			
		}
	}
	
	public void play()
	{
		while(queue.size() > 0)
		{
			try
			{
				beats.add(AudioSystem.getClip());
				beats.get(beats.size()-1).open(AudioSystem.getAudioInputStream(getClass().getResource("/resources/"+queue.getFirst())));
				beats.get(beats.size()-1).start();
			}
			catch(java.io.IOException e1)
			{
				e1.printStackTrace();
			}
			catch(LineUnavailableException e2)
			{
				 e2.printStackTrace();
		    }
			catch(UnsupportedAudioFileException  e3)
			{
				 e3.printStackTrace();
			}
			queue.removeFirst();
		}
		
		int bLimit = beats.size();
		for(int b = 0; b < bLimit; b++)
		{
			if(beats.get(b).getFramePosition() >= beats.get(b).getFrameLength())
			{
				beats.get(b).stop();
				beats.remove(b);
				bLimit = beats.size();
				b--;
			}
				
		}
	}
}