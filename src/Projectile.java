import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.ArrayList;

abstract class Projectile extends Prop
{
	String hashCounter;
	int strength, hDamage, sDamage, speed;
	
	public Projectile(Puppet p, int x, int y, int w1, int h1, int h2, int h3, int s)
	{
		super(x,y,w1,h1,h2,h3);
		puppet = p;
		hashCounter = "";
		
		strength = s;
		isFacingRight = puppet.isFacingRight;
		bounds.isFloating = true;
	}
	
	
	public void draw(Graphics2D g, ImageObserver i, SpriteReader s, double w, double h, boolean[] d)
	{
		if(spriteIndex == -1)
			spriteIndex = s.addSprite();
		if(spriteIndex != -1 && currState.getPosition() < spriteArchiver.size())
		{
			try
			{
				int f = (int)fIndex;
				if(puppet.palettes.get(puppet.pIndex).length > 1)
					sTint.set(0,puppet.palettes.get(puppet.pIndex)[puppet.palettes.get(puppet.pIndex).length-1]);
				s.read(g,i,sheet,spriteIndex,bounds.xHosh,bounds.yHosh,bounds.width,f,spriteArchiver.get(currState.getPosition())[0],xOffset,yOffset,spriteParams,!isFacingRight,sAngle,sTint);
			}
			catch(java.lang.IndexOutOfBoundsException e)
			{
				draw(g,i,s,w,h,d);
			}
		}
		super.draw(g,i,s,w,h,d);
	}
	
	public void move()
	{
		if(!isHit)
			super.move();
	}
	
	public void update()
	{
		super.update();
		isHit = false;
		health--;
		if(health <= 0 || hits <= 0)
			health = 0;
	}
	
	protected void addPleb(int hc, int x, int y, int w, int h, int d1, int d2, int s, int hd, int sd, int kx, int ky, int j, double hs, boolean ia, boolean ip, boolean pb, double[][] pr)
	{
		Pleb p = new Pleb(puppet,bounds,null,x,y,w,h,d1,d2,s,hd,sd,kx,ky,j,hs,ia,ip,pb,pr);
		
		if(hashCounter.equals(""))
			hashCounter = this.toString()+hc;
		else if(Integer.parseInt(hashCounter.substring(hashCounter.length()-1)) != hc)
			hashCounter = this.toString()+hc;
		p.hash = hashCounter;
		
		plebsOut.add(p);
	}
	
	protected void addGuardTrigger(int hc, int x, int y, int w, int h, int d, boolean ia)
	{
		Pleb p = new Pleb(puppet,bounds,null,x,y,w,h,d,-1,isFacingRight,ia);
		plebsOut.add(p);
	}
}