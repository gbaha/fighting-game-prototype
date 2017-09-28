import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.ImageObserver;
import java.util.ArrayList;

abstract class Projectile extends Prop
{
	Puppet puppet;
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
	
	public void draw(Graphics2D g, ImageObserver i, SpriteReader s, double w, double h, boolean d)
	{
		if(currState.getPosition() < spriteArchiver.size())
		{
			try
			{
				Image sheet = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resources/ninja.png"));
				int f = /*(hitboxArchiver.get(Roo.State.valueOf(currState.toString()).ordinal())[0][3] == 0)?*/ (int)fIndex;	//:hitboxArchiver.get(Roo.State.valueOf(currState.toString()).ordinal()).length-(int)fIndex-2;
				s.read(g,i,bounds.xHosh,bounds.yHosh,bounds.width,1760,1320,f,spriteArchiver.get(currState.getPosition())[0],spriteParams,!isFacingRight,sheet);
			}
			catch(java.lang.IndexOutOfBoundsException e)
			{
				draw(g,i,s,w,h,d);
			}
		}
		super.draw(g,i,s,w,h,d);
	}
	
	public void update()
	{
		super.update();
		health--;
		if(health <= 0 || hits <= 0)
			health = 0;
	}
	
	protected void addPleb(int hc, int x, int y, int w, int h, int d1, int d2, int s, int hd, int sd, int kx, int ky, double hs, boolean ia, boolean ip, double[][] pr)
	{
		Pleb p = new Pleb(puppet,bounds,null,x,y,w,h,d1,d2,s,hd,sd,kx,ky,hs,ia,ip,pr);
		
		if(hashCounter.equals(""))
			hashCounter = this.toString()+hc;
		else if(Integer.parseInt(hashCounter.substring(hashCounter.length()-1)) != hc)
			hashCounter = this.toString()+hc;
		p.hash = hashCounter;
		
		plebsOut.add(p);
	}
	
	protected void addGuardTrigger(int x, int y, int w, int h, int d, boolean r, boolean ia)
	{
		Pleb p = new Pleb(puppet,bounds,null,x,y,w,h,d,-1,r,ia);
		plebsOut.add(p);
	}
}