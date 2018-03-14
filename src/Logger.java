import java.io.*;
import java.util.Date;
import java.text.DateFormat;

public class Logger
{
	String nl;
	public Logger()
	{
		nl = "\r\n";
	}
	
	public void log(Stage s, Logic l, Hand h1, Hand h2, Klamoth k, Exception b) throws IOException
	{
		File bLog = new File("BugLog.txt");
		if(!bLog.exists())
			bLog.createNewFile();
		
		FileWriter logWriter = new FileWriter(bLog.getAbsoluteFile(),true);
		StringWriter error = new StringWriter();
		logWriter.write(DateFormat.getDateTimeInstance().format(new Date())+nl);
		b.printStackTrace(new PrintWriter(error));
		logWriter.write(error.toString()+nl);
		logStats(logWriter,s,l,h1,h2,k);
		logWriter.write(nl);
		logWriter.close();
	}
	
	public void logTest(Stage s, Logic l, Hand h1, Hand h2, Klamoth k) throws IOException
	{
		File bLog = new File("BugLog.txt");
		if(!bLog.exists())
			bLog.createNewFile();
		
		FileWriter logWriter = new FileWriter(bLog.getAbsoluteFile(),true);
		logWriter.write(DateFormat.getDateTimeInstance().format(new Date())+nl);
		logWriter.write("BUG LOG TEST"+nl+nl);
		logStats(logWriter,s,l,h1,h2,k);
		logWriter.write(nl);
		logWriter.close();
	}
	
	private void logStats(FileWriter w, Stage s, Logic l, Hand h1, Hand h2, Klamoth k) throws IOException
	{
		//STAGE
		w.write("STAGE:"+nl);
		w.write(s.type+" ["+s.settings[0]+" "+s.settings[1]+" "+s.settings[2]+"]"+nl);
		w.write(s.rCounter+" out of "+s.rounds+" ("+s.timer[0]+")"+nl);
		w.write("p1 wins "+s.wins[0][0]+" [");
		for(int i = 0; i < s.rCounter; i++)
		{
			w.write(s.wins[0][i+1]);
			if(i < s.wins[0][0])
				w.write(" ");
		}
		w.write("]"+nl);
		w.write("p2 wins "+s.wins[1][0]+" [");
		for(int i = 0; i < s.rCounter; i++)
		{
			w.write(s.wins[1][i+1]);
			if(i < s.wins[1][0])
				w.write(" ");
		}
		w.write("]"+nl);
		for(int i = 8; i < h1.buttonHeld.length; i++)
			w.write(h1.buttonHeld[i]+" ");
		w.write(s.xFocus+" "+s.yFocus+nl);
		w.write(l.xWindow+" "+l.yWindow+nl);
		w.write(l.lastHit+" "+l.hitStop+nl);
		w.write("SLIP  ["+l.slipStop[0]+" "+l.slipStop[1]+" "+l.slipStop[2]+" "+l.slipStop[3]+"]"+nl);
		w.write("SUPER ["+l.superStop[0]+" "+l.superStop[1]+" "+l.superStop[2]+" "+l.superStop[3]+"]"+nl);
		w.write((l.gamePaused)? "PAUSED"+nl+nl:nl+nl);
		
		//PLAYER 1
		w.write("PLAYER 1:"+nl);
		w.write("S	"+h1.player.sInputs[0]+" "+h1.player.sInputs[1]+" "+h1.player.sInputs[2]+" "+h1.player.sInputs[3]+nl);
		w.write("	");
		for(int[] i: h1.stickInputs)
			w.write("("+i[0]+","+i[1]+") ");
		w.write(nl);
		w.write("B	"+h1.buttonHeld[0]+" "+h1.buttonHeld[1]+" "+h1.buttonHeld[2]+" "+h1.buttonHeld[3]+" "+h1.buttonHeld[4]+" "+h1.buttonHeld[5]+" "+h1.buttonHeld[6]+" "+h1.buttonHeld[7]+nl);
		w.write("	");
		for(int[] i: h1.buttonInputs)
			w.write("("+i[0]+","+i[1]+") ");
		w.write(nl);
		logPuppet(w,s.player1);
		w.write(nl);
		
		//PLAYER 2
		w.write("PLAYER 2:"+nl);
		w.write("S	"+h2.player.sInputs[0]+" "+h2.player.sInputs[1]+" "+h2.player.sInputs[2]+" "+h2.player.sInputs[3]+nl);
		w.write("	");
		for(int[] i: h2.stickInputs)
			w.write("("+i[0]+","+i[1]+") ");
		w.write(nl);
		w.write("B	"+h2.buttonHeld[0]+" "+h2.buttonHeld[1]+" "+h2.buttonHeld[2]+" "+h2.buttonHeld[3]+" "+h2.buttonHeld[4]+" "+h2.buttonHeld[5]+" "+h2.buttonHeld[6]+" "+h2.buttonHeld[7]+nl);
		w.write("	");
		for(int[] i: h2.buttonInputs)
			w.write("("+i[0]+","+i[1]+") ");
		w.write(nl);
		logPuppet(w,s.player2);
		w.write(nl);
		
		//PUPPETS
		w.write("PUPPETS:"+nl);
		if(s.puppets.size() > 2)
		{
			for(Puppet p: s.puppets)
			{
				if(p != s.player1 && p != s.player2)
				{
					logPuppet(w,p);
					w.write(nl);
				}
			}
		}
		w.write(nl);
		
		//PROPS
		w.write("PROPS:"+nl);
		for(Prop p: s.props)
		{
			logProps(w,p);
			w.write(nl);
		}
		w.write(nl);
		
		//HITBOXES
		w.write("PLEBS:"+nl);
		for(Pleb p: s.plebs)
		{
			w.write(p.type+" "+p.hash+" "+p.puppet+" "+p.bounds+" "+p.action+nl);
			w.write(p.xCoord+" "+p.yCoord+" ("+p.xHosh+" "+p.yHosh+")"+nl);
			w.write(p.hDamage+" "+p.sDamage+" "+p.xKnockback+" "+p.yKnockback+nl);
			w.write(p.duration+" "+p.strength+" "+p.juggleHeight+" "+p.xDist+" "+p.yDist+" "+p.hitstunDamp+nl);
			w.write(p.isAttached+" "+p.isProjectile+" "+p.pBreaker+nl);
			for(double[] i: p.properties)
			{
				w.write("[");
				for(int j = 0; j < i.length; j++)
				{
					w.write((Math.floor(i[j]*1000)/1000)+"");
					w.write((j < i.length-1)? " ":"]"+nl);
				}
			}
			logForces(w,p.bounds);
		}
		w.write(nl);
		
		//SOUNDS
		w.write("SOUNDS:"+nl);
		for(int a = 0; a < k.queue.size(); a++)
		{
			w.write(k.queue.get(a));
			for(float[] b: k.beatInfo)
			{
				w.write(" [");
				for(int c = 0; c < b.length; c++)
				{
					w.write((Math.floor(b[c]*1000)/1000)+"");
					w.write((c < b.length-1)? " ":"]"+nl);
				}
			}
		}
		w.write(nl);
	}
	
	private void logPuppet(FileWriter w, Puppet p) throws IOException
	{
		w.write(p+" "+p.id+" ("+p.currState.getState()+")"+nl);
		w.write(p.currAction+" "+p.fCounter+nl);
		w.write(p.pIndex+" "+p.spriteIndex+" ("+p.sIndex+")"+nl);
		w.write(p.bounds.xCoord+" "+p.bounds.yCoord+" ("+p.bounds.xHosh+" "+p.bounds.yHosh+")"+nl);
		w.write(p.xOffset+" "+p.yOffset+" "+p.sAngle+nl);
		w.write("H "+p.health+" "+p.maxHp+nl);
		w.write("M "+p.meter+" "+p.maxMp+nl);
		w.write("S "+p.stamina+" "+p.maxSp+nl);
		w.write(p.hitStop+" "+p.hitStun+" "+p.blockStun+nl);
		w.write("["+p.hitInfo[0]+" "+p.hitInfo[1]+" "+p.hitInfo[2]+" "+p.hitInfo[3]+"]"+nl);
		w.write("KD "+p.kdCounter+" "+p.kdLimit+" "+p.kdStun+nl);
		w.write("WB ("+p.bounces[0]+" "+p.bounces[1]+") "+p.bounceLimit+" "+p.bDirection+" "+p.bounceStun+nl);
		w.write("U "+p.ukemi[0]+" "+p.ukemi[1]+nl);
		w.write("A "+p.armor[0]+" "+p.armor[1]+nl);
		w.write("J "+p.jump+" "+p.jumpLimit+" ["+p.jDirections[0]+" "+p.jDirections[1]+" "+p.jDirections[2]+"]"+nl);
		w.write("D "+p.aDash+" "+p.airDashLimit+nl);
		w.write(p.airOptions+" "+p.extraAir+nl);
		w.write(p.damageDamp+" "+p.hitstunDamp+" "+p.otgDamp+" "+p.juggleDamp+" "+p.gMagnitude+nl);
		
		boolean h = (p.anatomy.size() > 0);
		if(h)
			h = p.anatomy.get(0).hInvul;
		w.write(h+" "+p.throwInvul+" "+p.bounds.isGhost+nl);
		w.write(p.canBlock+" "+p.isGuardBroken+" ["+p.isBlocking[0]+" "+p.isBlocking[1]+"]"+nl);
		w.write(p.isFacingRight+" "+p.isPerformingAction+" "+p.isCrouching+" "+p.isCounterhit+" "+p.isTaunted+nl);
		w.write(p.isDashing+" "+p.isHoming+" "+p.isJumping+" "+p.isSlipping+nl);
		w.write(p.isJuggled+" "+p.isRecovering+" "+p.isAirLocked+" "+p.bounds.isFloating+" "+p.floatOverride+nl);
		w.write(p.throwInvul+" "+p.isThrowing+" "+p.isThrown+" "+p.isTeching+nl);
		w.write((p.isBella)? "IS BELLA":"IS NOT BELLA"+nl+nl);
		
		for(double[] i: p.propertyArchiver)
		{
			w.write("[");
			for(int j = 0; j < i.length; j++)
			{
				w.write((Math.floor(i[j]*1000)/1000)+"");
				w.write((j < i.length-1)? " ":"]"+nl);
			}
		}
		logForces(w,p.bounds);
	}
	
	private void logProps(FileWriter w, Prop p) throws IOException
	{
		w.write(p+" "+p.id+" ("+p.currState.getState()+")"+nl);
		w.write(p.puppet+" "+p.fCounter+nl);
		w.write(p.bounds.xCoord+" "+p.bounds.yCoord+" ("+p.bounds.xHosh+" "+p.bounds.yHosh+")"+nl);
		w.write(p.xOffset+" "+p.yOffset+" "+p.sAngle+nl);
		w.write(p.health+" "+p.maxHp+" ("+p.hits+")"+nl);
		w.write("F "+p.fCounter+" "+p.fIndex+nl);
		w.write("S "+p.spriteIndex+" "+p.sAngle+nl);
		w.write(p.isFacingRight+" "+p.isHit+nl);
		logForces(w,p.bounds);
	}
	
	private void logForces(FileWriter w, Organ h) throws IOException
	{
		for(Force f: h.forceArchiver)
			w.write("<"+f.type+" "+f.direction+" "+(Math.floor(f.magnitude*1000)/1000)+" "+(Math.floor(f.decay*1000)/1000)+">"+nl);
	}
}