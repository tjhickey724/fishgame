package game;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.event.*;
import java.awt.geom.AffineTransform;


import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.*;
import java.io.*;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

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

	public AudioClip bgSound;
	String lastbgSound;

	
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
				if (gm.isGameOver())
					return;
				
				// play good/bad sounds alone by key press for demo purpose
				if (e.getKeyChar()=='g') {
					goodclip.play(); return;
				} else if (e.getKeyChar() == 'b'){
					badclip.play(); return;
				}
				
				// first check to see if they pressed
				// when there are no fish!!
				if (gm.getNumFish()==0) {
					String log="KeyPress for no fish!" ;
					//gm.writeToLog(log);
					gm.writeToLog(new GameEvent(e.getKeyChar()));
					badclip.play();
					return;
				}
				//otherwise, remove the last fish (should only be one!)
				GameActor lastFish = gm.removeLastFish();
				
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
					gm.setHits(gm.getHits() + 1);
				}else {
					badclip.play();
					gm.score -= 1;
					gm.setMisses(gm.getMisses() + 1);
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
		
		if (gs==null) {
			System.out.println("gs is null!!!");
			return;
		}
			
		
		goodclip = new AudioClip(gs.goodSound);
		
		badclip = new AudioClip(gs.badSound);



		//here we read in the background image which tiles the scene
		try {
			streamImage = ImageIO.read(new File(gs.backgroundImage)); 
			
			AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
			tx.translate(0, -streamImage.getHeight(null));
			AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
			streamImage2 = op.filter(streamImage, null);


			fishL = ImageIO.read(new File("images/fish/fishL.png"));
			fishR = ImageIO.read(new File("images/fish/fishR.png"));
			if (!gs.bgSound.equals(this.lastbgSound)){	
				this.lastbgSound = gs.bgSound;
				if (bgSound != null) 
					bgSound.stop();

				bgSound = new AudioClip(gs.bgSound);
				bgSound.loop();	
				
			}
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
	 * then draws the scene
	 */
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		if (gm==null) return;  // this shouldn't ever happen!
		
		if (gm.gameSpec.requireGameViewUpdate) {
			// if the gameSpec changes, need to update gameState
			this.updateGameState(gm.gameSpec);
			gm.gameSpec.requireGameViewUpdate = false; // DEBUG Threadsafe??
		}
			
		if (gm.isGameOver()){
			g.setFont(new Font("Helvetica",Font.BOLD,50));
			g.drawString("GAME OVER",100,100);
			return;
		}
		
		drawBackground(g);
		
		drawFish(g); 

		updateScore(g);
		

		

	}
	
	private void updateScore(Graphics g){
		g.setFont(new Font("Helvetica",Font.BOLD,20));
		g.setColor(Color.WHITE);
		
		header.setText("<html><table style=\"font-size:24pt;\">"+
				"<tr><td>Right:</td>"+
					"<td>Wrong:</td>"+
					"<td>Misses:</td>"+
					"<td>Total:</td>"+
				"</tr>"+
				"<tr><td>"+gm.getHits()+"</td><td>"
					+gm.getMisses()+"</td><td>"
					+gm.getNoKeyPress()+"</td>"+
					+gm.getFishNum()+"</td>"+
				"</tr></table></html>");

	}
	
	private void drawFish(Graphics g){
		java.util.List<GameActor> gaList = gm.getActorList();
		for(GameActor a:gaList){
			drawActor(g,a,Color.WHITE);
		}
	}
	
	private void drawBackground(Graphics g){
		int width = this.getWidth();
		int height = this.getHeight();
		g.setColor(Color.BLUE);
		g.fillRect(0,0,width,height);
		double seconds = System.nanoTime()/1000000000.0;
		double frames = seconds*0.1;
		double framePart = frames -Math.floor(frames);
		int y_offset = 
				(int) Math.round(framePart*height);

		if (gm.isPaused() || gm.isGameOver()){
			y_offset=0;
			if (gm.getNumFish()> 0){
				gm.removeLastFish();
				this.bgSound.stop();
			}

		}

		// draw image on screen tiled
		g.drawImage(streamImage,0,y_offset-height,width,height/2+2,null);
		g.drawImage(streamImage2,0,y_offset-height/2,width,height/2+2,null);
		g.drawImage(streamImage,0,y_offset,width,height/2+2,null);
		g.drawImage(streamImage2,0,y_offset+height/2,width,height/2+2,null);
		
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


		int theSize = gm.interpolateSize(
				gm.gameSpec.minThrobSize,
				gm.gameSpec.maxThrobSize,
				a.birthTime,
				System.nanoTime(),
				visualHz);
		
		double aspectRatio = fishL.getHeight()/(1.0*fishL.getWidth());
 
		int theWidth  = gm.gameSpec.minThrobSize; //theSize; //(int) (theSize);
		int theHeight = theSize*fishL.getHeight()/fishL.getWidth(); //(int) ((theSize * aspectRatio)/100);
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
	


}
