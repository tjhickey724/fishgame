package game;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.event.*;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.*;
import java.io.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import javax.swing.*;
import javax.swing.event.*;
import java.util.ArrayList;


/**
 * a GameView is a 2D view of a GameModel
 * the aspect of the view and model may be different
 * Since the GameModel is a square space, the GameView takes
 * the minimum of the width and height of the JPanel and uses
 * that to scale the GameModel to the Viewing window.
 * Calling repaint() on the GameView will cause it to render
 * the current state of the Model to the JPanel canvas...
 * 
 * @author tim
 *
 */
public class GameView extends JPanel{
	private static final long serialVersionUID = 1L;
	
	private GameModel gm = null;
	private GameSpec gs;
	public AudioClip bgsound = new AudioClip("sounds/background/water6s.wav");

	
	public AudioClip goodclip,badclip;

	ArrayList<String> keylog = new ArrayList<String>();
	
	public boolean gameActive = false; // shouldn't this be in the model???
	
	public BufferedImage streamImage, streamImage2,fishL,fishR;
	
	public JLabel header = new JLabel("Right: Wrong:");
	
	
	/**
	 * create a gameview panel
	 * and add listeners to implement the user interaction with the model
	 * @param gm
	 */
	public GameView(final GameModel gm) {
		super();
		this.gm = gm;
		this.updateGameState(gm.gameSpec);
		this.bgsound.loop();

		this.requestFocus();
		
		KeyAdapter kl = new KeyAdapter()
		{
			
			/**
			 * returns true if the key press was correct
			 * @param c
			 * @param lastFish
			 * @return
			 */
			private boolean hitCorrectKey(char c,GameActor lastFish){
				Species s = lastFish.species;
				boolean onLeft = lastFish.origin==0;
				if (s==Species.good) 
					if (onLeft)
					   return c =='q';
					else
						return c=='p';
				else
					if (onLeft)
						return c=='a';
					else
						return c=='l';
			}
			
			@Override
			public void keyTyped(KeyEvent e)
			{
				if (gm.gameOver)
					return;
				
				// play good/bad sounds alone by key press for demo purpose
				if (e.getKeyChar()=='g') {
					goodclip.play(); return;
				} else if (e.getKeyChar() == 'b'){
					badclip.play(); return;
				}
				
				// first check to see if they pressed
				// when there are no fish!!
				if (gm.actors.size()==0) {
					String log="KeyPress for no fish!" ;
					//gm.writeToLog(log);
					gm.writeToLog(new GameEvent(e.getKeyChar()));
					badclip.play();
					return;
				}
				//otherwise, get the last fish (should only be one!)
				GameActor lastFish = gm.actors.get(gm.actors.size()-1);
				// turn off the fish sound and make fish inactive
				// and remove it from the model
				lastFish.ct.stop();
				lastFish.active = false;
				gm.removeFish(lastFish);
				
				// get the response time and write it to the log
				long keyPressTime = System.nanoTime();
				long responseTime = keyPressTime - lastFish.birthTime;
				boolean correctResponse = hitCorrectKey(e.getKeyChar(),lastFish);
				String log = e.getKeyChar()+" "+responseTime/1000000.0+" "
					      +correctResponse+" "+lastFish;
				System.out.println(log);
				
				gm.writeToLog(new GameEvent(e.getKeyChar(),lastFish));
				
				// play the appropriate sound and modify the score
				if (correctResponse){
					goodclip.play();
					gm.score += 2;
					gm.hits++;
				}else {
					badclip.play();
					gm.score -= 1;
					gm.misses++;
				}
				

			}
		};
		this.addKeyListener(kl);
	}
	
	/*
	 * this goes through the GameSpec and updates all of the local state,
	 * e.g. the image and sound files, throb rate, etc. ...
	 */
	private void updateGameState(GameSpec gs){
		goodclip = new AudioClip(gs.goodSound);
		badclip = new AudioClip(gs.badSound);

		//here we read in the background image which tiles the scene
		try {
			streamImage = ImageIO.read(new File(gs.backgroundImage)); 
			streamImage2 = ImageIO.read(new File(gs.backgroundImageFlipped)); 
			fishL = ImageIO.read(new File("images/fishLeft.png"));
			fishR = ImageIO.read(new File("images/fishRight.png"));
		}catch(Exception e){
			System.out.println("can't find background images"+e);
		}
		
	}
	
	/**
	 * toViewCoords(x) converts from model coordinates to pixels
	 * on the screen so that objects can be drawn to scale, i.e.
	 * as the screen is resized the objects change
	 * size proportionately.  
	 * @param x the unit in model coordinates 
	 * @return the corresponding value in pixel based on window-size
	 */
	public int toViewCoords(double x){
		int width = this.getWidth();
 		int height = this.getHeight();
		int viewSize = (width<height)?width:height;
		return (int) Math.round(x/gm.size*viewSize);
	}
	public int toXViewCoords(double x){
		int width = this.getWidth();
 		int height = this.getHeight();
		int viewSize = (width<height)?width:height;
		return (int) Math.round(x/gm.size*width);
	}
	public int toYViewCoords(double x){
		int width = this.getWidth();
 		int height = this.getHeight();
		int viewSize = (width<height)?width:height;
		return (int) Math.round(x/gm.size*height);
	}
	
	/**
	 * toModelCoords(x) is used to convert mouse locations
	 * to positions in the model so that the avatar position
	 * in the model can be changed correctly
	 * @param x position in pixels in view
	 * @return position in model coordinates
	 */
	
	// bug in this code when the game is scale wider than it is tall
	// BUG
	public double toModelCoords(int x){
		int width = this.getWidth();
		int height = this.getHeight();
		int viewSize = (width<height)?width:height;
		return x*gm.size/viewSize;
	}

	/**
	 * paintComponent(g) draws the current state of the model
	 * onto the component. It first repaints it in blue, 
	 * then draws the avatar,
	 * then draws each of the other actors, i.e. fireflies and wasps...
	 */
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		if (gm==null) return;
		int width = this.getWidth();
		int height = this.getHeight();
		g.setColor(Color.BLUE);
		g.fillRect(0,0,width,height);
		double seconds = System.nanoTime()/1000000000.0;
		double frames = seconds*0.1;
		double framePart = frames -Math.floor(frames);
		int y_offset = 
				(int) Math.round(framePart*height);
		//System.out.println(y_offset);
		//g.drawImage(streamImage,0,0,null);
		//g.drawImage(streamImage2,0,y_offset-3*291,null); 
		if (gm.paused || gm.gameOver){
			y_offset=0;
			if (gm.actors.size()> 0){
				GameActor a = gm.actors.get(0);
				a.ct.stop();
			}

		}

		g.drawImage(streamImage,0,y_offset-height,width,height/2,null);
		g.drawImage(streamImage2,0,y_offset-height/2,width,height/2,null);
		g.drawImage(streamImage,0,y_offset,width,height/2,null);
		g.drawImage(streamImage2,0,y_offset+height/2,width,height/2,null);
		
		if (gm.gameOver){
			g.drawString("GAME OVER",100,100);
			return;
		}
		
		
		//g.drawImage(streamImage,0,y_offset+2*291,null);
		
	//	drawActor(g,gm.avatar,Color.GREEN);
		java.util.List<GameActor> gaList = new ArrayList<GameActor>(gm.actors);
		for(GameActor a:gaList){
			drawActor(g,a,Color.WHITE);
		}
		g.setFont(new Font("Helvetica",Font.BOLD,20));
		g.setColor(Color.WHITE);
		//g.drawString("Score:"+gm.score, width/10, height/10);
		header.setText("<html><table style=\"font-size:24pt;\"><tr><td>Right:</td><td>Wrong:</td><td>Misses:</td></tr><tr><td>"+gm.hits+"</td><td>"+gm.misses+"</td><td>"+gm.noKeyPress+"</td></tr></table></html>");

		

	}
	
	/**
	 * drawActor(g,a,c) - draws a single actor a 
	 * using the Graphics object g. The color c is the
	 * default color used for new species, but is ignored
	 * for avatars, wasps, and fireflies
	 * 
	 * @param g - the Graphics object used for drawing
	 * @param a - the Actor to be drawn
	 * @param c - the default color for actors of unknown species
	 */
	private void drawActor(Graphics g, GameActor a,Color c){
		if (!a.active) return;
		int theRadius = toViewCoords(a.radius);
		int x = toXViewCoords(a.x);
		int y = toYViewCoords(a.y);
		int visualHz=1;
		
		switch (a.species){
			case good:
				visualHz = gm.gameSpec.good.throbRate; break;
			case bad:
				visualHz = gm.gameSpec.bad.throbRate;	break;
		}


		int theSize = interpolateSize(
				gm.gameSpec.minThrobSize,
				gm.gameSpec.maxThrobSize,
				a.birthTime,
				System.nanoTime(),
				visualHz);
		
		int theWidth  = (int) ((theSize * 97)/100);
		int theHeight = (int) ((theSize * 32)/100);
		if (a.fromLeft){
			g.drawImage(fishL,x-theWidth/2,y-theHeight/2,theWidth,theHeight,null);
		} else {
			g.drawImage(fishR,x-theWidth/2,y-theHeight/2,theWidth,theHeight,null);
		}

	}
	
	private Color interpolate(Color c1, Color c2, long birth, long now, double freq){
		double t = ((now-birth)/1000000000.0)*freq;
		double y = 0.5*(Math.sin(Math.PI*2*t)+1);
		float red = (float) (c1.getRed()/255.0*y + c2.getRed()/255.0*(1-y));
		float green = (float) (c1.getGreen()/255.0*y + c2.getGreen()/255.0*(1-y));
		float blue = (float) (c1.getBlue()/255.0*y + c2.getBlue()/255.0*(1-y));
		return new Color(red,green,blue);
	}
	
	private int interpolateSize(double min, double max, long birth, long now, double freq){
		double t = ((now-birth)/1000000000.0)*freq;
		double y = 0.5*(Math.sin(Math.PI*t)+1);
		double s = min*y + max*(1-y);
		int size = (int)Math.round(s);
		return size;
}

}
