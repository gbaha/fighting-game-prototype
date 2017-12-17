import java.util.ArrayList;
import java.util.LinkedList;
import javax.sound.sampled.*;
 
public class Klamoth	//SOUND AND SHIT
{
	LinkedList<Clip> beats;
	LinkedList<String> queue;
	LinkedList<float[]> beatInfo;	//[Volume, balance]
	Clip fire;
	
	public Klamoth()
	{
		beats = new LinkedList<Clip>();
		queue = new LinkedList<String>();
		beatInfo = new LinkedList<float[]>();
		
	/*	try
		{
			fire = AudioSystem.getClip();			
			fire.open(AudioSystem.getAudioInputStream(getClass().getResource("/resources/firetest.wav")));
			fire.loop(Clip.LOOP_CONTINUOUSLY);
			
			FloatControl volume = (FloatControl)fire.getControl(FloatControl.Type.MASTER_GAIN);
			volume.setValue(-15.0f);
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
		}*/
	}
	
	public void buildQueue(Stage s, Director d)
	{
		for(Pleb p: s.plebs)
		{
			while(p.soundArchiver.size() > 0 && p.soundInfo.size() > 0)
			{
				float[] i = p.soundInfo.removeFirst();
				int x = (p.xHosh+p.width/2 < 0)? 0:((p.xHosh+p.width/2 > 1280)? 1280:p.xHosh+p.width/2);
				beatInfo.add(new float[]{i[0],(x-640)/640.0f});
				queue.add(p.soundArchiver.removeFirst());
			}
		}
	/*	for(Prop p: s.props)
		{
			while(p.soundArchiver.size() > 0 && p.soundInfo.size() > 0)
			{
				float[] i = p.soundInfo.removeFirst();
				int x = (p.xHosh+p.width/2 < 0)? 0:((p.xHosh+p.width/2 > 1280)? 1280:p.xHosh+p.width/2);
				beatInfo.add(new float[]{i[0],(x-640)/640.0f});
				queue.add(p.soundArchiver.removeFirst());
			}
		}*/
		for(Puppet p: s.puppets)
		{
			while(p.soundArchiver.size() > 0 && p.soundInfo.size() > 0)
			{
				float[] i = p.soundInfo.removeFirst();
				int x = (p.xHosh+p.width/2 < 0)? 0:((p.xHosh+p.width/2 > 1280)? 1280:p.xHosh+p.width/2);
				beatInfo.add(new float[]{i[0],(x-640)/640.0f});
				queue.add(p.soundArchiver.removeFirst());
			}
		}
		while(d.sounds.size() > 0)
		{
			beatInfo.add(new float[]{0.0f,0.0f});
			queue.add(d.sounds.removeFirst());
			
		}
	}
	
	public void play()
	{
		while(queue.size() > 0)
		{
			try
			{
				beats.add(AudioSystem.getClip());
				beats.getLast().open(AudioSystem.getAudioInputStream(getClass().getResource("/resources/"+queue.getFirst())));
				beats.getLast().start();
				
				FloatControl volume = (FloatControl)beats.getLast().getControl(FloatControl.Type.MASTER_GAIN);
				volume.setValue(beatInfo.getFirst()[0]);
				FloatControl balance = (FloatControl)beats.getLast().getControl(FloatControl.Type.BALANCE);
				balance.setValue(beatInfo.getFirst()[1]);
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
			catch(java.lang.NullPointerException e4)
			{
				 e4.printStackTrace();
			}
			queue.removeFirst();
			beatInfo.removeFirst();
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