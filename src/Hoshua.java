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
	int xFocus, yFocus, xCoord, yCoord, fSkip, fCounter, sTimer;
	double width, height, xZoom, yZoom, fps;
	boolean gamePaused;
	
	public Hoshua(Stage c, Gui g, int x, int y, double w, double h, double f, int s, boolean p)
	{
		super(true);
		canvas = c;
		sReader = new SpriteReader(w,h,1,1,2);
		gui = g;
		xFocus = c.xFocus;
		yFocus = c.yFocus;
		xCoord = x;
		yCoord = y;
		width = w;
		height = h;
		xZoom = 1;
		yZoom = 1;
		
		fps = f;
		fSkip = s;
		fCounter = 0;
		sTimer = 0;
		
		gamePaused = p;
	//	this.add(gui);
	}
	
	
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		for(Floor f: canvas.floors)
			f.draw(g,this,width*xZoom,height*yZoom,canvas.settings[1]);	
		int a = (sTimer > 0)? ((sTimer < 3)? (int)(105.0*sTimer/3):105):0;
		g.setColor(new Color(6,8,8,a));
		g.fillRect(0,0,(int)width,(int)height);
		
		xFocus = (int)(canvas.xFocus*width/1280);
		yFocus = (int)(canvas.yFocus*height/720);
		if(canvas.zOverride)
		{
			xZoom = canvas.xZoom;
			yZoom = canvas.yZoom;
		}
		else
		{
			canvas.xZoom = xZoom;
			canvas.yZoom = yZoom;
		}
		
		try
		{
			Graphics2D g2 = (Graphics2D) g;
			gui.drawBack(g2,this,canvas,width,height);
			
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
						if(puppets.get(q).hitStun == 0 && !puppets.get(q).isThrown)
						{
							puppets.add(puppets.size(),puppets.get(q));
							puppets.remove(q);
						}
						else
							q++;
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
						if(players.get(q).hitStun == 0 && !players.get(q).isThrown)
						{
							players.add(players.size(),players.get(q));
							players.remove(q);
						}
						else
							q++;
						break;
				}
			}
			
			if(!gamePaused)
			{
				double z = (players.get(0).bounds.xCoord < players.get(1).bounds.xCoord+players.get(1).bounds.width)? players.get(1).bounds.xCoord+players.get(1).bounds.width-players.get(0).bounds.xCoord:players.get(0).bounds.xCoord+players.get(0).bounds.width-players.get(1).bounds.xCoord;
			
				if(Math.abs(1280/(z+100)-xZoom) >= 0.02)	// && xZoom < 1280/(z+100))
					xZoom += 0.02*((1280/(z+100) > xZoom)? 1:-1);
				else
					xZoom = 1280/(z+100);
				
				if(Math.abs(1280/(z+100)-yZoom) >= 0.02)	// && yZoom < 1280/(z+100))
					yZoom += 0.02*((1280/(z+100) > yZoom)? 1:-1);
				else
					yZoom = 1280/(z+100);
				
				if(xZoom > 1)
					xZoom = 1;
				if(yZoom > 1)
					yZoom = 1;
			}
			
			for(Puppet p: puppets)
				p.draw(g2,this,sReader,width*xZoom,height*yZoom,canvas.settings);
			for(Puppet p: players)
				p.draw(g2,this,sReader,width*xZoom,height*yZoom,canvas.settings);
			for(Prop p: canvas.props)
				p.draw(g2,this,sReader,width*xZoom,height*yZoom,canvas.settings);
			
			gui.drawFront(g2,this,canvas,width,height);
			
			for(Pleb p: canvas.plebs)
			{
				if(canvas.settings[0])
					p.draw(g,width*xZoom,height*yZoom);
			}
			
		//	sReader.backup(g2,canvas.settings);
			sReader.update(xZoom,yZoom);
		}
		catch(java.util.ConcurrentModificationException e)
		{
			paintComponent(g);
		}
		
		//TEST
/*		for(BlueFairy b: canvas.fairies)
			b.draw(g,canvas.xFocus,canvas.yFocus,width*xZoom,height*yZoom);*/
		
		if(canvas.settings[1])
		{
			g.setColor(Color.GRAY);
			g.drawString(fps+"",5,20);
			g.drawString(yZoom+"",5,(int)height-10);
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
			
			g.drawString("("+(int)((MouseInfo.getPointerInfo().getLocation().x-xCoord-xFocus)*1280/xZoom/width)+","+(int)((MouseInfo.getPointerInfo().getLocation().y-yCoord-yFocus-25)*720/yZoom/height+40)+")",hqz1,hqz2+15);
			g.setColor(Color.DARK_GRAY);
			g.drawString("("+(MouseInfo.getPointerInfo().getLocation().x-xCoord)+","+(MouseInfo.getPointerInfo().getLocation().y-yCoord)+")",hqz1,hqz2);
		}
	//	repaint();
	}
	
	
	public void update(int x, int y, int w, int h, int s, double f, boolean p)
	{
		xCoord = x;
		yCoord = y;
		width = w;
		height = h;
		sTimer = s;
		fps = f;
		gamePaused = p;
		
		fCounter++;
		if(fCounter != fSkip)
			repaint();
		else
			fCounter = 0;
	}
}