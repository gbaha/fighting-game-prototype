import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MouseInfo; //TEST
import java.awt.Toolkit;
import java.util.ArrayList;

import javax.swing.JPanel;

public class Hoshua extends JPanel
{
	Stage canvas;
	SpriteReader sReader;
	Gui gui;
	int xFocus, yFocus, xCoord, yCoord, fSkip, fCounter;
	double width, height, fps;
	boolean gamePaused, debugging;
	
	public Hoshua(Stage c, Gui g, int x, int y, double w, double h, double f, int s, boolean p, boolean b)
	{
		super(true);
		canvas = c;
		sReader = new SpriteReader(w,h,2);
		gui = g;
		xFocus = c.xFocus;
		yFocus = c.yFocus;
		xCoord = x;
		yCoord = y;
		width = w;
		height = h;
		
		fps = f;
		fSkip = s;
		fCounter = 0;
		
		gamePaused = p;
		debugging = b;
	//	this.add(gui);
	}
	
	
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		for(Floor f: canvas.floors)
			f.draw(g,this,width,height,debugging);	
		
		xFocus = (int)(canvas.xFocus*width/1280);
		yFocus = (int)(canvas.yFocus*height/720);
		
		try
		{
			Graphics2D g2 = (Graphics2D) g;
			gui.drawTop(g2,this,canvas.player1,canvas.player2,width,height);
			
			ArrayList<Puppet> puppets = new ArrayList<Puppet>();
			ArrayList<Puppet> players = new ArrayList<Puppet>();
			for(Puppet p: canvas.puppets)
			{
				if(p instanceof Player)
					players.add(0,p);
				else
					puppets.add(0,p);
			}
			
			int q = 0;
			for(int p = 0; p < puppets.size(); p++)
			{
				switch(puppets.get(q).currState.getState())
				{
					case "IDLE":
					case "CROUCH":
					case "STANDING":
					case "CROUCHING":
						q++;
						break;
					default:
						puppets.add(puppets.size(),puppets.get(q));
						puppets.remove(q);
						break;
				}
			}
			for(int p = 0; p < players.size(); p++)
			{
				switch(players.get(q).currState.getState())
				{
					case "IDLE":
					case "CROUCH":
					case "STANDING":
					case "CROUCHING":
						q++;
						break;
					default:
						players.add(players.size(),players.get(q));
						players.remove(q);
						break;
				}
			}
			
			for(Puppet p: puppets)
					p.draw(g2,this,sReader,width,height,debugging);
			for(Puppet p: players)
				p.draw(g2,this,sReader,width,height,debugging);
			for(Prop p: canvas.props)
				p.draw(g2,this,sReader,width,height,debugging);
			
			gui.drawBot(g2,this,canvas.player1,canvas.player2,width,height);
			
			for(Pleb p: canvas.plebs)
			{
		//		if(p.xCoord <= width*21/20-xFocus && p.xCoord+p.width >= -(xFocus+width/20) && p.yCoord <= height*21/20-yFocus && p.yCoord+p.height >= -(yFocus+height/20))
					p.draw(g,width,height);
			}
			sReader.backup(g2,debugging);
		}
		catch(java.util.ConcurrentModificationException e)
		{
			paintComponent(g);
		}
		
		//TEST
/*		for(BlueFairy b: canvas.fairies)
			b.draw(g,canvas.xFocus,canvas.yFocus,width,height);*/
		
		//TEST
		g.setColor(Color.GRAY);
		g.drawString(fps+"",5,20);
	//	g.setColor(new Color((int)(Math.random()*255),(int)(Math.random()*255),(int)(Math.random()*255)));
	//	g.fillRect((int)(width-20*width/1280),0,(int)(20*width/1280),(int)(20*height/720));
		
		int hqz1 = MouseInfo.getPointerInfo().getLocation().x-xCoord+(int)(10*width/1280);
		int hqz2 = MouseInfo.getPointerInfo().getLocation().y-yCoord;
		if(hqz1 < 0)
			hqz1 = 0;
		else if(hqz1 > width)
			hqz1 = (int)width-65;
		if(hqz2 < 0)
			hqz2 = 10;
		else if(hqz2 > height)
			hqz2 = (int)height-45;
		
		g.setColor(Color.DARK_GRAY);
		g.drawString("("+(MouseInfo.getPointerInfo().getLocation().x-xCoord)+","+(MouseInfo.getPointerInfo().getLocation().y-yCoord)+")",hqz1,hqz2);
		g.setColor(Color.GRAY);
		g.drawString("("+(int)((MouseInfo.getPointerInfo().getLocation().x-xCoord-xFocus)*1280/width)+","+(int)((MouseInfo.getPointerInfo().getLocation().y-yCoord-yFocus-25)*720/height+40)+")",hqz1,hqz2+15);
		
	/*	g.setColor(Color.LIGHT_GRAY);
		if(canvas.player != null)
			g.drawLine((int)((canvas.player.xHosh+canvas.player.width/2)*width/1280),(int)((canvas.player.yHosh+canvas.player.height/2)*height/720),(MouseInfo.getPointerInfo().getLocation().x-xCoord),(MouseInfo.getPointerInfo().getLocation().y-yCoord-25));*/
		
	//	repaint();
	}
	
	
	public void update(int x, int y, int w, int h, double f, boolean p, boolean b)
	{
		xCoord = x;
		yCoord = y;
		width = w;
		height = h;
		fps = f;
		gamePaused = p;
		debugging = b;
		
		fCounter++;
		if(fCounter != fSkip)
			repaint();
		else
			fCounter = 0;
	}
}