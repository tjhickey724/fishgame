package game;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.event.*;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
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
	public AudioClip goodclip,badclip;
	private GameModel gm = null;
	ArrayList<String> keylog = new ArrayList<String>();
	public boolean gameActive = false;
	private BufferedImage streamImage, streamImage2,fishL,fishR;
	
	public GameView(final GameModel gm) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
		super();
		this.gm = gm;
		goodclip = new AudioClip("sounds/good.wav");
		badclip = new AudioClip("sounds/bad.wav");
		System.out.println("hello");
		this.requestFocus();
		
		//here we read in the background image which tiles the scene
		try {
			streamImage = ImageIO.read(new File("images/stream.jpg")); 
			streamImage2 = ImageIO.read(new File("images/streamFlip2.jpg")); 
			fishL = ImageIO.read(new File("images/fishLeft.png"));
			fishR = ImageIO.read(new File("images/fishRight.png"));
		}catch(Exception e){
			System.out.println("can't find background images"+e);
		}
		
		KeyListener kl = new KeyListener()
		{
			@Override
			public void keyPressed(KeyEvent e)
				{
				
			//	feedBackText.append("Key Pressed: " + e.getKeyChar() + "\n");
			}
			
			@Override
			public void keyReleased(KeyEvent e)
			{
				//feedBackText.append("Key Released: " + e.getKeyChar() + "\n");
			}
			
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
				//gm.writeToLog(log);
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
			for(GameActor a:gm.actors){
				a.ct.stop();
			}
		}
		g.drawImage(streamImage,0,y_offset-height,width,height/2,null);
		g.drawImage(streamImage2,0,y_offset-height/2,width,height/2,null);
		g.drawImage(streamImage,0,y_offset,width,height/2,null);
		g.drawImage(streamImage2,0,y_offset+height/2,width,height/2,null);
		//g.drawImage(streamImage,0,y_offset+2*291,null);
		
	//	drawActor(g,gm.avatar,Color.GREEN);
		java.util.List<GameActor> gaList = new ArrayList<GameActor>(gm.actors);
		for(GameActor a:gaList){
			drawActor(g,a,Color.WHITE);
		}
		g.setFont(new Font("Helvetica",Font.BOLD,20));
		g.setColor(Color.WHITE);
		//g.drawString("Score:"+gm.score, width/10, height/10);
		g.drawString("Right:"+gm.hits+"   Wrong:"+gm.misses + "  Misses:"+gm.noKeyPress, 0, 20);
		g.drawString(""+gm.hits+"     "+gm.misses + "     "+gm.noKeyPress, 0, 40);

		

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
		int x = toViewCoords(a.x);
		int y = toViewCoords(a.y);
		int visualHz=1;
		
		switch (a.species){
		case good:
			visualHz = gm.goodvisualhz;
			c = interpolate(a.color3, a.color4,a.birthTime,System.nanoTime(),a.colorHerz);break;
		case bad:
			visualHz = gm.badvisualhz;
			c = interpolate(a.color3, a.color4,a.birthTime,System.nanoTime(),a.colorHerz);break;
		
		case avatar: c=Color.BLACK; break;
		}
		g.setColor(c);
		/*
		if (a.species==Species.avatar){
			g.drawOval(x-theRadius, y-theRadius, 2*theRadius, 2*theRadius);
		} else
			g.fillOval(x-theRadius, y-theRadius, 2*theRadius, 2*theRadius);
	
		*/
		int theSize = interpolateSize(gm.visualMin,gm.visualMax,a.birthTime,System.nanoTime(),visualHz);
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
		double y = 0.5*(Math.sin(Math.PI*2*t)+1);
		double s = min*y + max*(1-y);
		int size = (int)Math.round(s);
		return size;
}

}
