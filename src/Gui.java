import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.RescaleOp;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.swing.JPanel;

public class Gui extends JPanel
{
	public static final int LEFT = 0;
	public static final int RIGHT = 1;
	public static final int CENTER = 2;
	private final Font FONT;
	
	ArrayList<Splash> splashes;
	Hand hand1, hand2;
	int[][] hitCounter;
	double[] hDamage;
//	double hTick, eTick;
	boolean gamePaused;
	
	public Gui(Hand h1, Hand h2, boolean p)
	{
		FONT = this.getFont();
		splashes = new ArrayList<Splash>();
		hand1 = h1;
		hand2 = h2;
		hitCounter = new int[2][3];	//[[hits, timer, damage], ...]
		hDamage = new double[2];
//		hTick = 0;
//		eTick = 0;
		gamePaused = p;
		
/*		try
		{
			Font sFont = Font.createFont(Font.TRUETYPE_FONT,getClass().getResource("/resources/Prototype.ttf").openStream());
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			ge.registerFont(sFont);
		}
		catch(java.io.IOException e)
		{
		}
		catch(java.awt.FontFormatException e)
		{
		}*/
	}
	
	public void drawFront(Graphics2D g, ImageObserver i, Stage c, double w, double h)
	{
		//METER
		g.setColor(Color.LIGHT_GRAY);
		g.setColor(new Color(g.getColor().getRed(),g.getColor().getGreen(),g.getColor().getBlue(),200));
		g.fillRect((int)((35*w/1280)+0.5),(int)((665*h/720)+0.5),(int)((500*w/1280)+0.5),(int)((35*h/720)+0.5));
		g.fillRect((int)((745*w/1280)+0.5),(int)((665*h/720)+0.5),(int)((500*w/1280)+0.5),(int)((35*h/720)+0.5));
		g.setColor(Color.CYAN);
		g.setColor(new Color(g.getColor().getRed(),g.getColor().getGreen(),g.getColor().getBlue(),50*(c.player1.meter/1000)));
		g.fillRect((int)((35*w/1280)+0.5),(int)((665*h/720)+0.5),(int)((500*w/1280)+0.5),(int)((35*h/720)+0.5));
		g.setColor(new Color(g.getColor().getRed(),g.getColor().getGreen(),g.getColor().getBlue(),50*(c.player2.meter/1000)));
		g.fillRect((int)((745*w/1280)+0.5),(int)((665*h/720)+0.5),(int)((500*w/1280)+0.5),(int)((35*h/720)+0.5));
		g.setColor(Color.BLUE);
		g.setColor(new Color(g.getColor().getRed(),g.getColor().getGreen(),g.getColor().getBlue(),50));
		g.fillRect((int)(((535-500*((c.player1.meter%1000)/1000.0))*w/1280)+0.5),(int)((665*h/720)+0.5),(int)((500*((c.player1.meter%1000)/1000.0)*w/1280)+0.5),(int)((35*h/720)+0.5));
		g.fillRect((int)((745*w/1280)+0.5),(int)((665*h/720)+0.5),(int)((500*((c.player2.meter%1000)/1000.0)*w/1280)+0.5),(int)((35*h/720)+0.5));
		g.setColor(Color.BLUE);
		g.drawString(c.player1.meter/1000+"",(int)((40*w/1280)+0.5),(int)((690*h/720)+0.5));
		g.drawString(c.player2.meter/1000+"",(int)((1230*w/1280)+0.5),(int)((690*h/720)+0.5));
		
		//HIT COUNTER
		if(c.player1.hitStun > 0 || c.player1.isThrown)
		{
	//		if(c.player2.hitInfo[1] > hitCounter[1][0])
				hitCounter[1][0] = c.player2.hitInfo[1];
			hitCounter[1][1] = 60;
		}
		if(c.player2.hitStun > 0 || c.player2.isThrown)
		{
	//		if(c.player1.hitInfo[1] > hitCounter[0][0])
				hitCounter[0][0] = c.player1.hitInfo[1];
			hitCounter[0][1] = 60;
		}
		
		g.setColor(Color.RED);
		if(/*hitCounter[0][0] > 1 &&*/ hitCounter[0][1] > 0)
		{
			if(hitCounter[0][0] > 1)
				g.drawString(hitCounter[0][0]+" HITS",(int)((52*w/1280)+0.5),(int)((166*h/720)+0.5));
			if(hitCounter[0][2] < c.player2.maxHp+1)
				g.drawString(hitCounter[0][2]+" damage",(int)((52*w/1280)+0.5),(int)((191*h/720)+0.5));
			else
				g.drawString("INFINITUUM PLUS DAMAGE!",(int)((52*w/1280)+0.5),(int)((191*h/720)+0.5));
			if(c.player2.isCounterhit)
				g.drawString("COUNTER!",(int)((52*w/1280)+0.5),(int)((216*h/720)+0.5));
		}
		if(/*hitCounter[1][0] > 1 &&*/ hitCounter[1][1] > 0)
		{
			if(hitCounter[1][0] > 1)
				g.drawString(hitCounter[1][0]+" HITS",(int)(((1184-g.getFontMetrics().stringWidth(hitCounter[1][0]+" HITS"))*w/1280)+0.5),(int)((166*h/720)+0.5));
			if(hitCounter[1][2] < c.player1.maxHp+1)
				g.drawString(hitCounter[1][2]+" damage",(int)(((1184-g.getFontMetrics().stringWidth(hitCounter[1][2]+" damage"))*w/1280)+0.5),(int)((191*h/720)+0.5));
			else
				g.drawString("INFINITUUM PLUS DAMAGE!",(int)(((1184-g.getFontMetrics().stringWidth("INFINITUUM PLUS DAMAGE!"))*w/1280)+0.5),(int)((191*h/720)+0.5));
			if(c.player1.isCounterhit)
				g.drawString("COUNTER!",(int)(((1184-g.getFontMetrics().stringWidth("COUNTER!"))*w/1280)+0.5),(int)((216*h/720)+0.5));
		}
		
		//FORCE ARCHIVER
		if(c.settings[1])
		{
			g.setColor(Color.BLUE);
			for(Force f: c.player1.bounds.forceArchiver)
				g.drawString(f.type+"    "+f.direction+"   "+f.magnitude,(int)((12*w/1280)+0.5),(int)(((100+c.player1.bounds.forceArchiver.indexOf(f)*25)*h/720)+0.5));
			for(Force f: c.player2.bounds.forceArchiver)
				g.drawString(f.magnitude+"   "+f.direction+"   "+f.type,(int)(((1188-g.getFontMetrics().stringWidth(f.magnitude+"   "+f.direction+"   "+f.type))*w/1280)+0.5),(int)(((100+c.player2.bounds.forceArchiver.indexOf(f)*25)*h/720)+0.5));
		}
		
		//INPUTS
		if(c.settings[2])
		{
			drawInputs(g,hand1,w,h,true);
			drawInputs(g,hand2,w,h,false);
		}
		
		//SPLASH
		int sLimit = splashes.size();
		for(int s = 0; s < sLimit; s++)
		{
			splashes.get(s).draw(g,w,h);
			if(splashes.get(s).fCounter >= splashes.get(s).length)
			{
				splashes.remove(s);
				sLimit = splashes.size();
				s--;
			}
		}
		
		//PAUSE SCREEN
		if(gamePaused)
		{
			g.setColor(new Color(105,105,105,105));
			g.fillRect(0,0,(int)w,(int)h);
			g.setColor(new Color(255,255,255,255));
			g.fillRect((int)(w-31*w/1280+0.5),(int)(5*h/720+0.5),(int)(10*w/1280+0.5),(int)(25*h/720+0.5));
			g.fillRect((int)(w-15*w/1280+0.5),(int)(5*h/720+0.5),(int)(10*w/1280+0.5),(int)(25*h/720+0.5));
		}
	}
	
	public void drawBack(Graphics2D g, ImageObserver i, Stage c, double w, double h)
	{
		//HEALTH
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect((int)((35*w/1280)+0.5),(int)((20*h/720)+0.5),(int)((500*w/1280)+0.5),(int)((35*h/720)+0.5));
		g.fillRect((int)((745*w/1280)+0.5),(int)((20*h/720)+0.5),(int)((500*w/1280)+0.5),(int)((35*h/720)+0.5));
		g.setColor(Color.RED);
		g.fillRect((int)(((35+500*(c.player1.maxHp-hDamage[0])/c.player1.maxHp)*w/1280)+0.5),(int)((35*h/720)+0.5),(int)((500*(hDamage[0]/c.player1.maxHp)*w/1280)+0.5),(int)((20*h/720)+0.5));
		g.fillRect((int)((745*w/1280)+0.5),(int)((20*h/720)+0.5),(int)((500*(hDamage[1]/c.player2.maxHp)*w/1280)+0.5),(int)((35*h/720)+0.5));
		g.setColor(Color.GREEN);
		g.fillRect((int)(((35+500*(double)(c.player1.maxHp-c.player1.health)/c.player1.maxHp)*w/1280)+0.5),(int)((20*h/720)+0.5),(int)((500*((double)c.player1.health/c.player1.maxHp)*w/1280)+0.5),(int)((35*h/720)+0.5));
		g.fillRect((int)((745*w/1280)+0.5),(int)((20*h/720)+0.5),(int)((500*((double)c.player2.health/c.player2.maxHp)*w/1280)+0.5),(int)((35*h/720)+0.5));
		
		//STAMINA
		for(int s = 0; s < 6; s++)
		{
			g.setColor(Color.LIGHT_GRAY);
			g.fillRect((int)(((495-s*45)*w/1280)+0.5),(int)((60*h/720)+0.5),(int)((40*w/1280)+0.5),(int)((20*h/720)+0.5));
			g.fillRect((int)(((745+s*45)*w/1280)+0.5),(int)((60*h/720)+0.5),(int)((40*w/1280)+0.5),(int)((20*h/720)+0.5));
			
			g.setColor(Color.ORANGE);
			if(c.player1.stamina >= (s+1)*100)
				g.fillRect((int)(((495-s*45)*w/1280)+0.5),(int)((60*h/720)+0.5),(int)((40*w/1280)+0.5),(int)((20*h/720)+0.5));
			else if(c.player1.stamina > s*100)
				g.fillRect((int)(((495-s*45+((100-(c.player1.stamina-s*100.0))/100)*40)*w/1280)+0.5),(int)((60*h/720)+0.5),(int)((40*((c.player1.stamina-s*100.0)/100)*w/1280)+0.5),(int)((20*h/720)+0.5));
			
			if(c.player2.stamina >= (s+1)*100)
				g.fillRect((int)(((745+s*45)*w/1280)+0.5),(int)((60*h/720)+0.5),(int)((40*w/1280)+0.5),(int)((20*h/720)+0.5));
			else if(c.player2.stamina > s*100)
				g.fillRect((int)(((745+s*45)*w/1280)+0.5),(int)((60*h/720)+0.5),(int)((40*((c.player2.stamina-s*100.0)/100)*w/1280)+0.5),(int)((20*h/720)+0.5));
		}
		
		//WINS
		for(int s = 0; s < c.rounds; s++)
		{
			switch(c.wins[0][s+1])
			{
				case 1:
					g.setColor(Color.ORANGE);
					break;
				case 2:
					g.setColor(Color.CYAN);
					break;
				default:
					g.setColor(Color.LIGHT_GRAY);
					break;
			}
			if(c.wins[0][s+1] == 0)
				g.setColor(new Color(g.getColor().getRed(),g.getColor().getGreen(),g.getColor().getBlue(),200));
			g.fillRect((int)(((510-s*45)*w/1280)+0.5),(int)((85*h/720)+0.5),(int)((10*w/1280)+0.5),(int)((20*h/720)+0.5));
			
			switch(c.wins[1][s+1])
			{
				case 1:
					g.setColor(Color.ORANGE);
					break;
				case 2:
					g.setColor(Color.CYAN);
					break;
				default:
					g.setColor(Color.LIGHT_GRAY);
					break;
			}
			if(c.wins[1][s+1] == 0)
				g.setColor(new Color(g.getColor().getRed(),g.getColor().getGreen(),g.getColor().getBlue(),200));
			g.fillRect((int)(((760+s*45)*w/1280)+0.5),(int)((85*h/720)+0.5),(int)((10*w/1280)+0.5),(int)((20*h/720)+0.5));
		}
		
		//TIMER
		BufferedImage timer = new BufferedImage(100,100,BufferedImage.TYPE_INT_ARGB);
		Graphics2D tg = timer.createGraphics();
		tg.setColor(Color.RED);
		tg.setFont(new Font(FONT.getName(),Font.BOLD,75));
		tg.drawString((c.timer[0]/10)+""+(c.timer[0]%10),7,70);
		tg.dispose();
		g.drawImage(timer,(int)((590*w/1280)+0.5),(int)((5*h/720)+0.5),(int)((690*w/1280)+0.5),(int)((115*h/720)+0.5),0,0,100,100,i);
	}
	
	public void drawInputs(Graphics2D g, Hand h1, double w, double h2, boolean d)
	{
		g.setStroke(new BasicStroke((int)(5*w/1280)));
		for(int i = 0; i < 9; i++)
		{
			int c = (i%2 == 0)? 120:200;
			g.setColor(new Color(c,c,c,50));
			g.fillRect((d)? 0:(int)(1120*w/1280+0.5),(int)((240+i*40)*h2/720+0.5),(int)(160*w/1280+0.5),(int)(40*h2/720+0.5));
		}
		
		LinkedList<Boolean> order = (LinkedList<Boolean>)h1.inputOrder.clone();
		int j = 0;
		int k = 0;
		for(int i = 0; i < 9; i++)
		{
			boolean hasDrawn = false;
			boolean hasButtons = false;
			try
			{
				if(order.size() > 0)
				{
					if(k < h1.buttonInputs.size()&& h1.buttonInputs.size() > 0)
					{
						while(!order.getFirst() && h1.buttonInputs.get(k)[1] == 0)
						{
							if(!hasButtons)
							{
								g.setColor(new Color(0,0,0,50));
								g.fillOval((int)(((d)? 52:1184)*w/1280+0.5),(int)((246+i*40)*h2/720+0.5),(int)(12*w/1280+0.5),(int)(12*h2/720+0.5));
								g.fillOval((int)(((d)? 68:1200)*w/1280+0.5),(int)((246+i*40)*h2/720+0.5),(int)(12*w/1280+0.5),(int)(12*h2/720+0.5));
								g.fillOval((int)(((d)? 84:1216)*w/1280+0.5),(int)((246+i*40)*h2/720+0.5),(int)(12*w/1280+0.5),(int)(12*h2/720+0.5));
								g.fillOval((int)(((d)? 52:1184)*w/1280+0.5),(int)((262+i*40)*h2/720+0.5),(int)(12*w/1280+0.5),(int)(12*h2/720+0.5));
								g.fillOval((int)(((d)? 68:1200)*w/1280+0.5),(int)((262+i*40)*h2/720+0.5),(int)(12*w/1280+0.5),(int)(12*h2/720+0.5));
								g.fillOval((int)(((d)? 84:1216)*w/1280+0.5),(int)((262+i*40)*h2/720+0.5),(int)(12*w/1280+0.5),(int)(12*h2/720+0.5));
							}
							
							g.setColor(Color.RED);
							switch(h1.buttonInputs.get(k)[0])
							{
								case 0:
									g.fillOval((int)(((d)? 52:1184)*w/1280+0.5),(int)((246+i*40)*h2/720+0.5),(int)(12*w/1280+0.5),(int)(12*h2/720+0.5));
									break;
									
								case 1:
									g.fillOval((int)(((d)? 68:1200)*w/1280+0.5),(int)((246+i*40)*h2/720+0.5),(int)(12*w/1280+0.5),(int)(12*h2/720+0.5));
									break;
									
								case 2:
									g.fillOval((int)(((d)? 84:1216)*w/1280+0.5),(int)((246+i*40)*h2/720+0.5),(int)(12*w/1280+0.5),(int)(12*h2/720+0.5));
									break;
									
								case 3:
									g.fillOval((int)(((d)? 52:1184)*w/1280+0.5),(int)((262+i*40)*h2/720+0.5),(int)(12*w/1280+0.5),(int)(12*h2/720+0.5));
									break;
									
								case 4:
									g.fillOval((int)(((d)? 68:1200)*w/1280+0.5),(int)((262+i*40)*h2/720+0.5),(int)(12*w/1280+0.5),(int)(12*h2/720+0.5));
									break;
									
								case 5:
									g.fillOval((int)(((d)? 84:1216)*w/1280+0.5),(int)((262+i*40)*h2/720+0.5),(int)(12*w/1280+0.5),(int)(12*h2/720+0.5));
									break;
							}
							
							hasDrawn = true;
							hasButtons = true;
							order.removeFirst();
							k++;
						}
					}
					
					if(order.getFirst())
					{
						if(j < h1.stickInputs.size() && h1.stickInputs.size() > 0)
						{
							if(h1.stickInputs.get(j)[0] != 5)
							{
								g.setColor(Color.RED);
								g.drawString(((h1.stickInputs.get(j)[1] < 1000)? h1.stickInputs.get(j)[1]:999)+"",(int)(((d)? 115:1130)*w/1280+0.5),(int)((265+i*40)*h2/720+0.5));
								
								switch(h1.stickInputs.get(j)[0])
								{
									case 1:
										g.drawLine((int)(((d)? 5:1247)*w/1280+0.5),(int)((272+i*40)*h2/720+0.5),(int)(((d)? 33:1275)*w/1280+0.5),(int)((245+i*40)*h2/720+0.5));
										g.drawLine((int)(((d)? 5:1247)*w/1280+0.5),(int)((272+i*40)*h2/720+0.5),(int)(((d)? 5:1247)*w/1280+0.5),(int)((255+i*40)*h2/720+0.5));
										g.drawLine((int)(((d)? 5:1247)*w/1280+0.5),(int)((272+i*40)*h2/720+0.5),(int)(((d)? 23:1265)*w/1280+0.5),(int)((272+i*40)*h2/720+0.5));
										break;
										
									case 2:
										g.drawLine((int)(((d)? 19:1261)*w/1280+0.5),(int)((245+i*40)*h2/720+0.5),(int)(((d)? 19:1261)*w/1280+0.5),(int)((272+i*40)*h2/720+0.5));
										g.drawLine((int)(((d)? 5:1275)*w/1280+0.5),(int)((263+i*40)*h2/720+0.5),(int)(((d)? 19:1261)*w/1280+0.5),(int)((272+i*40)*h2/720+0.5));
										g.drawLine((int)(((d)? 33:1247)*w/1280+0.5),(int)((263+i*40)*h2/720+0.5),(int)(((d)? 19:1261)*w/1280+0.5),(int)((272+i*40)*h2/720+0.5));
										break;
										
									case 3:
										g.drawLine((int)(((d)? 5:1247)*w/1280+0.5),(int)((245+i*40)*h2/720+0.5),(int)(((d)? 33:1275)*w/1280+0.5),(int)((272+i*40)*h2/720+0.5));
										g.drawLine((int)(((d)? 15:1257)*w/1280+0.5),(int)((272+i*40)*h2/720+0.5),(int)(((d)? 33:1275)*w/1280+0.5),(int)((272+i*40)*h2/720+0.5));
										g.drawLine((int)(((d)? 33:1275)*w/1280+0.5),(int)((255+i*40)*h2/720+0.5),(int)(((d)? 33:1275)*w/1280+0.5),(int)((272+i*40)*h2/720+0.5));
										break;
										
									case 4:
										g.drawLine((int)(((d)? 5:1247)*w/1280+0.5),(int)((258+i*40)*h2/720+0.5),(int)(((d)? 33:1275)*w/1280+0.5),(int)((258+i*40)*h2/720+0.5));
										g.drawLine((int)(((d)? 5:1247)*w/1280+0.5),(int)((258+i*40)*h2/720+0.5),(int)(((d)? 14:1256)*w/1280+0.5),(int)((245+i*40)*h2/720+0.5));
										g.drawLine((int)(((d)? 5:1247)*w/1280+0.5),(int)((258+i*40)*h2/720+0.5),(int)(((d)? 14:1256)*w/1280+0.5),(int)((272+i*40)*h2/720+0.5));
										break;
										
									case 6:
										g.drawLine((int)(((d)? 5:1247)*w/1280+0.5),(int)((258+i*40)*h2/720+0.5),(int)(((d)? 33:1275)*w/1280+0.5),(int)((258+i*40)*h2/720+0.5));
										g.drawLine((int)(((d)? 33:1275)*w/1280+0.5),(int)((258+i*40)*h2/720+0.5),(int)(((d)? 24:1266)*w/1280+0.5),(int)((245+i*40)*h2/720+0.5));
										g.drawLine((int)(((d)? 33:1275)*w/1280+0.5),(int)((258+i*40)*h2/720+0.5),(int)(((d)? 24:1266)*w/1280+0.5),(int)((272+i*40)*h2/720+0.5));
										break;
										
									case 7:
										g.drawLine((int)(((d)? 5:1247)*w/1280+0.5),(int)((245+i*40)*h2/720+0.5),(int)(((d)? 33:1275)*w/1280+0.5),(int)((272+i*40)*h2/720+0.5));
										g.drawLine((int)(((d)? 5:1247)*w/1280+0.5),(int)((245+i*40)*h2/720+0.5),(int)(((d)? 5:1247)*w/1280+0.5),(int)((262+i*40)*h2/720+0.5));
										g.drawLine((int)(((d)? 5:1247)*w/1280+0.5),(int)((245+i*40)*h2/720+0.5),(int)(((d)? 23:1265)*w/1280+0.5),(int)((245+i*40)*h2/720+0.5));
										break;
										
									case 8:
										g.drawLine((int)(((d)? 19:1261)*w/1280+0.5),(int)((245+i*40)*h2/720+0.5),(int)(((d)? 19:1261)*w/1280+0.5),(int)((272+i*40)*h2/720+0.5));
										g.drawLine((int)(((d)? 5:1275)*w/1280+0.5),(int)((254+i*40)*h2/720+0.5),(int)(((d)? 19:1261)*w/1280+0.5),(int)((245+i*40)*h2/720+0.5));
										g.drawLine((int)(((d)? 33:1247)*w/1280+0.5),(int)((254+i*40)*h2/720+0.5),(int)(((d)? 19:1261)*w/1280+0.5),(int)((245+i*40)*h2/720+0.5));
										break;
										
									case 9:
										g.drawLine((int)(((d)? 5:1247)*w/1280+0.5),(int)((272+i*40)*h2/720+0.5),(int)(((d)? 33:1275)*w/1280+0.5),(int)((245+i*40)*h2/720+0.5));
										g.drawLine((int)(((d)? 33:1275)*w/1280+0.5),(int)((262+i*40)*h2/720+0.5),(int)(((d)? 33:1275)*w/1280+0.5),(int)((245+i*40)*h2/720+0.5));
										g.drawLine((int)(((d)? 15:1257)*w/1280+0.5),(int)((245+i*40)*h2/720+0.5),(int)(((d)? 33:1275)*w/1280+0.5),(int)((245+i*40)*h2/720+0.5));
										break;
								}
								hasDrawn = true;
							}
							order.removeFirst();
						}
						j++;
					}
					else
					{
						if(k < h1.buttonInputs.size())
						{
							g.setColor(Color.RED);
							g.drawString(((h1.buttonInputs.get(k)[1] < 1000)? h1.buttonInputs.get(k)[1]:999)+"",(int)(((d)? 115:1130)*w/1280+0.5),(int)((265+i*40)*h2/720+0.5));
							
							if(!hasButtons)
							{
								g.setColor(new Color(0,0,0,50));
								g.fillOval((int)(((d)? 52:1184)*w/1280+0.5),(int)((246+i*40)*h2/720+0.5),(int)(12*w/1280+0.5),(int)(12*h2/720+0.5));
								g.fillOval((int)(((d)? 68:1200)*w/1280+0.5),(int)((246+i*40)*h2/720+0.5),(int)(12*w/1280+0.5),(int)(12*h2/720+0.5));
								g.fillOval((int)(((d)? 84:1216)*w/1280+0.5),(int)((246+i*40)*h2/720+0.5),(int)(12*w/1280+0.5),(int)(12*h2/720+0.5));
								g.fillOval((int)(((d)? 52:1184)*w/1280+0.5),(int)((262+i*40)*h2/720+0.5),(int)(12*w/1280+0.5),(int)(12*h2/720+0.5));
								g.fillOval((int)(((d)? 68:1200)*w/1280+0.5),(int)((262+i*40)*h2/720+0.5),(int)(12*w/1280+0.5),(int)(12*h2/720+0.5));
								g.fillOval((int)(((d)? 84:1216)*w/1280+0.5),(int)((262+i*40)*h2/720+0.5),(int)(12*w/1280+0.5),(int)(12*h2/720+0.5));
							}
							
							g.setColor(Color.RED);
							switch(h1.buttonInputs.get(k)[0])
							{
								case 0:
									g.fillOval((int)(((d)? 52:1184)*w/1280+0.5),(int)((246+i*40)*h2/720+0.5),(int)(12*w/1280+0.5),(int)(12*h2/720+0.5));
									break;
									
								case 1:
									g.fillOval((int)(((d)? 68:1200)*w/1280+0.5),(int)((246+i*40)*h2/720+0.5),(int)(12*w/1280+0.5),(int)(12*h2/720+0.5));
									break;
									
								case 2:
									g.fillOval((int)(((d)? 84:1216)*w/1280+0.5),(int)((246+i*40)*h2/720+0.5),(int)(12*w/1280+0.5),(int)(12*h2/720+0.5));
									break;
									
								case 3:
									g.fillOval((int)(((d)? 52:1184)*w/1280+0.5),(int)((262+i*40)*h2/720+0.5),(int)(12*w/1280+0.5),(int)(12*h2/720+0.5));
									break;
									
								case 4:
									g.fillOval((int)(((d)? 68:1200)*w/1280+0.5),(int)((262+i*40)*h2/720+0.5),(int)(12*w/1280+0.5),(int)(12*h2/720+0.5));
									break;
									
								case 5:
									g.fillOval((int)(((d)? 84:1216)*w/1280+0.5),(int)((262+i*40)*h2/720+0.5),(int)(12*w/1280+0.5),(int)(12*h2/720+0.5));
									break;
							}
							
							hasDrawn = true;
							hasButtons = true;
							order.removeFirst();
						}
						k++;
					}
				}
				
			/*	if(order.size() > 0)
				{
					while(!order.getFirst() && k < h1.buttonInputs.size())
					{System.out.println(order.size()+"   "+k+" "+h1.buttonInputs.size());
						if(h1.buttonInputs.get(k)[1] == 0)
						{
							hasDrawn = true;
							order.removeFirst();
						}
						else
							k = h1.buttonInputs.size();
						k++;
					}
				}*/
			}
			catch(java.lang.NullPointerException e)
			{
				drawInputs(g,h1,w,h2,d);
			}
			
			if(!hasDrawn)
				i--;
			if((j >= h1.stickInputs.size() && k >= h1.buttonInputs.size()) || order.size() == 0)
				i = 9;
		}
		g.setStroke(new BasicStroke((int)(1*w/1280)));
	}
	
	public void displaySplash(String t, int a, int x, int y, int s, int l, int f)
	{
		splashes.add(new Splash(t,a,x,y,s,l,f));
	}
	
	
	public void update(int w, int h, boolean p)
	{
		gamePaused = p;
		
		if(!gamePaused && hand1.player != null && hand2.player != null)
		{
			if(hand1.player.hitInfo[3] != hitCounter[0][2] && hand1.player.hitInfo[3] > 0)
				hitCounter[0][2] = hand1.player.hitInfo[3];
			if(hand2.player.hitInfo[3] != hitCounter[1][2] && hand2.player.hitInfo[3] > 0)
				hitCounter[1][2] = hand2.player.hitInfo[3];
			
			for(int[] i: hitCounter)
			{
				if(i[1] > 0)
					i[1]--;
				else
				{
					i[0] = 0;
					i[2] = 0;
				}
			}
			
			if(hand1.player != null && hitCounter[1][1] == 0)
			{
				if(hDamage[0] > hand1.player.health)
					hDamage[0] -= 10;
				else
					hDamage[0] = hand1.player.health;
			}
			if(hand2.player != null && hitCounter[0][1] == 0)
			{
				if(hDamage[1] > hand2.player.health)
					hDamage[1] -= 10;
				else
					hDamage[1] = hand2.player.health;
			}
			
			for(Splash s: splashes)
				s.fCounter++;
			
/*			if(eTick == 0)
				eTick = 20;
			else
				eTick--;*/
		}
	}
	
	
	private class Splash
	{
		String text;
		int alignment, xCoord, yCoord, size, length, fadePoint, fCounter;
		
		public Splash(String t, int a, int x, int y, int s, int l, int f)
		{
			alignment = a;
			xCoord = x;
			yCoord = y;
			text = t;
			size = s;
			length = l;
			fadePoint = f;
			fCounter = 0;
		}
		
		
		public void draw(Graphics2D g, double w, double h)
		{
			Font sFont = new Font("Fontnamehere",Font.BOLD,(int)(size*w/1280));
			g.setFont(sFont);
			
			float alpha = (fCounter >= length)? 0.0f:((fCounter < fadePoint)? 1.0f:((float)length-fCounter)/(length-fadePoint));
			g.setColor(new Color(1.0f,0.72f,0.07f,alpha));
			
			int sWidth = g.getFontMetrics(sFont).stringWidth(text);
			int sHeight = g.getFontMetrics(sFont).getAscent()-g.getFontMetrics(sFont).getDescent();
			switch(alignment)
			{
				case Gui.LEFT:
					g.drawString(text,(int)(xCoord*w/1280)-sWidth/2,(int)(yCoord*h/720)+sHeight/2);
					break;
					
				case Gui.RIGHT:
					g.drawString(text,(int)(xCoord*w/1280)-sWidth/2,(int)(yCoord*h/720)+sHeight/2);
					break;
					
				case Gui.CENTER:
					g.drawString(text,(int)(xCoord*w/1280)-sWidth/2,(int)(yCoord*h/720)+sHeight/2);
					break;
			}
			g.setFont(FONT);
		}
	}
}