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
 * a GameView is a 2D view of a GameModel the aspect of the view and model may
 * be different Since the GameModel is a square space, the GameView takes the
 * minimum of the width and height of the JPanel and uses that to scale the
 * GameModel to the Viewing window. Calling repaint() on the GameView will cause
 * it to render the current state of the Model to the JPanel canvas...
 * 
 * The keyListener created for the GameView will be called when ever the user
 * hits a key This code will be run in parallel with the GameLoop, so you have
 * to be careful in the update method of GameModel to assume that a key could be
 * pressed anytime while the update is being modified.... This means we really
 * should be synchronizing on the model when it is modified from the key
 * listener.
 * 
 * @author tim
 * 
 */

// REFACTOR:  this needs more refactoring. In particular, the wins/losses/etc
// fields of the GameModel are really statistics that are only used for the GUI here
// and so they should be abstracted to their own GameStats class which will clean up
// the GameModel code and make this code easier to maintain...

public class GameView extends JPanel {
	private static final long serialVersionUID = 1L;

	public GameModel gm = null;
	boolean hasAvatar = true;
	boolean flash = false;
	boolean soundflash = false;
    boolean blank=true;
	public AudioClip bgSound;
	String lastbgSound;

	public AudioClip goodclip, badclip;

	ArrayList<String> keylog = new ArrayList<String>();

	public boolean gameActive = false; // shouldn't this be in the model???

	public BufferedImage streamImage, streamImage2, fish, boat, coin,fixationMark,bubble;

	// these arrays store the sprite images used to adjust brightness. the
	// default brightness is in image, 12, accesible using fishL[12] or
	// fishR[12]
	public BufferedImage[] fishL, fishR;
	public long indicatorUpdate;
	public long soundIndicatorUpdate;

	public JLabel header = new JLabel("Right: Wrong:");
	public int thisFrame = 12;

	/**
	 * create a gameview panel and add listeners to implement the user
	 * interaction with the model
	 * 
	 * @param gm
	 */
	public GameView(final GameModel gm) {
		super();
		this.gm = gm;
		this.updateGameState(gm.gameSpec);
		this.requestFocus();

		KeyAdapter kl = new KeyAdapter() {

			@Override
			public void keyTyped(KeyEvent e) {
				if (gm.isGameOver())
					return;
				// when a key is pressed, we send a 'flash' to the indicator in
				// the corner
				flash = true;
				// we set the update time to be 50 ms after the keypress, so the
				// indicator stays lit for 50 ms
				indicatorUpdate = System.nanoTime() + 50000000l;

				// first check to see if they pressed
				// when there are no fish!!
				if (gm.getNumFish() == 0) {
					long keyPressTime = e.getWhen() * 1000000L;
					
					badclip.playDelayed(gm.EEG, gm.gameSpec.audioDelay);
					return;
				}

				// this is a synchronized call to the GameModel
				// and will delay if gm.update is being called from the GameLoop
				// Also, once it is called here, gm.update must delay if called
				// from the GameLoop
				boolean correctResponse = gm.handleKeyPress(e);

				if (correctResponse) {
					goodclip.playDelayed(gm.EEG, gm.gameSpec.audioDelay);
				} else {
					badclip.playDelayed(gm.EEG, gm.gameSpec.audioDelay);
				}

			}

		};
		this.addKeyListener(kl);
	}

	/*
	 * this goes through the GameSpec and updates all of the local state, e.g.
	 * the image and sound files, throb rate, etc. ...
	 */
	private void updateGameState(GameSpec gs) {

		if (gs == null) {
			System.out.println("gs is null!!!");
			return;
		}

		goodclip = new AudioClip(gs.goodResponseSound);
		goodclip.gm = this.gm;
		goodclip.codeForEEG = "FPOS";

		badclip = new AudioClip(gs.badResponseSound);
		badclip.gm = this.gm;
		badclip.codeForEEG = "FNEG";

		// here we read in the background image which tiles the scene
		try {
			streamImage = ImageIO.read(new File(gs.backgroundImage));

			AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
			tx.translate(0, -streamImage.getHeight(null));
			AffineTransformOp op = new AffineTransformOp(tx,
					AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
			streamImage2 = op.filter(streamImage, null);

			boat = ImageIO.read(new File("images/boat1.png"));

			fish = ImageIO.read(new File("images/fish/fish.png"));
			coin = ImageIO.read(new File("images/wealth.png"));
			fixationMark=ImageIO.read(new File("images/FixationMark3.png"));
			bubble=ImageIO.read(new File("images/bubble2.png"));
			fishL = spriteImageArray(fish, 5, 5);
			fishR = spriteImageArray(horizontalFlip(fish), 5, 5);
			hasAvatar = gs.hasAvatar;
			if (!gs.bgSound.equals(this.lastbgSound)) {
				this.lastbgSound = gs.bgSound;
				if (bgSound != null)
					bgSound.stop();

				bgSound = new AudioClip(gs.bgSound);
				bgSound.loop();

			}
		} catch (Exception e) {
			System.out.println("can't find background images" + e);
		}

	}

	/**
	 * toXViewCoords(x) converts from model coordinates to pixels on the screen
	 * in the horizontal direction so that objects can be drawn to scale, i.e.
	 * as the screen is resized the objects change size proportionately.
	 * 
	 * @param x
	 *            the unit in model coordinates
	 * @return the corresponding value in pixel based on window-size
	 */

	public int toXViewCoords(double x) {
		int width = this.getWidth();
		return (int) Math.round(x / GameModel.SIZE * width);
	}

	public int toYViewCoords(double x) {
		int height = this.getHeight();
		return (int) Math.round(x / GameModel.SIZE * height);
	}

	/**
	 * paintComponent(g) draws the current state of the model onto the
	 * component. It first repaints it in blue, then draws the scene
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (gm == null)
			return; // this shouldn't ever happen!
		
		if (gm.firstBlankScreen || gm.secondBlankScreen){
			drawBlankScreen(g);
			return;
		}

		if (gm.gameSpec.requireGameViewUpdate) {
			// if the gameSpec changes, need to update gameState
			this.updateGameState(gm.gameSpec);
			gm.gameSpec.requireGameViewUpdate = false; // DEBUG Threadsafe??
		}

		if (gm.isGameOver()) {
			int hits = gm.getHits();
			int misses = gm.getMisses();
			int nokey = gm.getNoKeyPress();
			int total=hits-misses-nokey;
			g.setFont(new Font("Helvetica", Font.BOLD, 50));
			g.drawString("GAME OVER", 100, 100);
			g.setFont(new Font("Helvetica", Font.BOLD, 25));
			g.drawString("Right: " + gm.getHits(), 100, 130);
			g.drawString("Wrong: " + gm.getMisses(), 100, 160);
			g.drawString("Missed: " + gm.getNoKeyPress(), 100, 190);
			g.drawString("Total: " + total, 100, 210);
			return;
		}
		drawBackground(g);
		if (gm.usingEEG){
			
			drawFixationMark(g);	
		}

		drawTimeBar(g);

		drawHud(g);

		drawFish(g);

		if (hasAvatar)
			drawAvatar(g);

		drawIndicator(g);
		drawSoundIndicator(g);

		updateScore(g);
		
		
        

	}
	
	private void drawFixationMark(Graphics g){
		int x = (this.getWidth()- fixationMark.getWidth()/4)/2;
		int y = (this.getHeight()-fixationMark.getHeight()/4)/2;
		g.drawImage(fixationMark, x, y, fixationMark.getWidth()/4, fixationMark.getHeight()/4, null);
	}
	private void drowbubble(Graphics g){
		int x = (this.getWidth()- bubble.getWidth()/4)/2;
		int y = (this.getHeight()-bubble.getHeight()/4)/2;
		g.drawImage(bubble, x, y, bubble.getWidth(), bubble.getHeight(), null);
	}

	private void drawIndicator(Graphics g) {
		if (flash) {
			g.setColor(Color.white);

			if (indicatorUpdate < System.nanoTime())
				flash = false;
		} else if (gm.flash) {
			g.setColor(Color.white);
			if (gm.indicatorUpdate < System.nanoTime())
				gm.flash = false;
		} else {
			g.setColor(Color.black);
		}

		g.fillRect(0, getHeight() - 60, 60, 60);

	}

	private void drawSoundIndicator(Graphics g) {
		if (soundflash) {
			g.setColor(Color.white);
			if (soundIndicatorUpdate < System.nanoTime())
				soundflash = false;
		} else if (gm.soundflash) {
			g.setColor(Color.white);
			if (gm.soundIndicatorUpdate < System.nanoTime())
				gm.soundflash = false;
		} else {
			g.setColor(Color.black);
		}

		g.fillRect(getWidth() - 60, getHeight() - 60, 60, 60);

	}

	private void drawTimeBar(Graphics g) {
		// TODO Auto-generated method stub
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, getWidth(), 20);
		g.setColor(Color.GREEN);
		// this is supposed to reference the timePerTrial rather than timeLimit.
		g.fillRect(0, 0, toXViewCoords(gm.timeRemaining), 20);
		// System.out.println(gm.timeRemaining);
		// g.drawString("", toViewCoords(50), 20);

	}

	public void drawHud(Graphics g) {

		g.drawImage(coin, 2, 20, 50, 50, null);
		g.setColor(Color.BLACK);
		g.setFont(new Font("Helvetica", Font.BOLD, 50));
		int hits = gm.getHits();
		int misses = gm.getMisses();
		int nokey = gm.getNoKeyPress();
		int total=hits-misses-nokey;
		//g.drawString(gm.wealth + "", 55, 65);
		g.drawString(total + "", 55, 65);
		
	}

	// method to draw the boat avatar
	private void drawAvatar(Graphics g) {
		// This draws the boat in the middle
		int x = (this.getWidth() - boat.getWidth(null)) / 2;
		int y = (this.getHeight() - boat.getHeight(null) / 4);
		g.drawImage(boat, x, y, boat.getWidth(), boat.getHeight(), null);
	}

	private void updateScore(Graphics g) {
		g.setFont(new Font("Helvetica", Font.BOLD, 20));
		g.setColor(Color.WHITE);

	}

	private void drawFish(Graphics g) {
		Fish f = gm.getCurrentFish();
		// if (f!= null) System.out.println("drawing fish: "+f);
		if (f != null) {
			drawActor(g, f, Color.WHITE);
		}
		// if (f!= null) System.out.println("drew fish: "+f);
	}

	private void drawBlankScreen(Graphics g) {
		int width = this.getWidth();
		int height = this.getHeight();
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, width, height);	
	}
	
	private void drawBackground(Graphics g) {
		int width = this.getWidth();
		int height = this.getHeight();
		g.setColor(Color.BLUE);
		g.fillRect(0, 0, width, height);
		double seconds = System.nanoTime() / 1000000000.0;
		double frames = seconds * 0.1;
		double framePart = frames - Math.floor(frames);
		int y_offset = (int) Math.round(framePart * height);

		if (gm.isPaused() || gm.isGameOver()) {
			y_offset = 0;
			if (gm.getNumFish() > 0) {
				gm.removeLastFish();
			}

		}
		if (gm.isGameOver()){
			this.bgSound.stop();
		}

		// draw image on screen tiled
		g.drawImage(streamImage, 0, y_offset - height, width, height / 2 + 2,
				null);
		g.drawImage(streamImage2, 0, y_offset - height / 2, width,
				height / 2 + 2, null);
		g.drawImage(streamImage, 0, y_offset, width, height / 2 + 2, null);
		g.drawImage(streamImage2, 0, y_offset + height / 2, width,
				height / 2 + 2, null);

	}

	/**
	 * drawActor(g,a,c) - draws a single actor a using the Graphics object g.
	 * The color c is the default color used for new species, but is ignored for
	 * avatars, wasps, and fireflies
	 * 
	 * @param g
	 *            - the Graphics object used for drawing
	 * @param aFish
	 *            - the Actor to be drawn
	 * @param c
	 *            - the default color for actors of unknown species
	 */
	private void drawActor(Graphics g, Fish aFish, Color c) {
		if (!aFish.active)
			return;
		double fx = gm.getCurrentFishX();
		double fy = gm.getCurrentFishY();
		int x = toXViewCoords(aFish.x);
		int y = toYViewCoords(aFish.y);
		int visualHz = 1;
		// System.out.println("+++ fx="+fx+" gm.x="+aFish.x+" w ="+this.getWidth()+" gv.x="+x);
		// System.out.println("+++ fy="+fy+" gm.y="+aFish.y+" w ="+this.getHeight()+" gv.y="+y);

		// set the default visual hertz for the fish
		// this should be done when the fish is created!
		// the visualhz should be a field of the fish...
		switch (aFish.species) {
		case good:
			visualHz = gm.gameSpec.good.throbRate;
			break;
		case bad:
			visualHz = gm.gameSpec.bad.throbRate;
			break;
		}

		// handle the exception conditions...
		if (gm.gameSpec.avmode == 1) { // auditory determines good/bad, so
										// switch the visual hertz in
										// incongruent case
			if (aFish.congruent == 1) {
				switch (aFish.species) {
				case bad:
					visualHz = gm.gameSpec.good.throbRate;
					break;
				case good:
					visualHz = gm.gameSpec.bad.throbRate;
					break;
				}
			} else if (aFish.congruent == 2) {
				visualHz = 0;
			}
		}

		int theSize = interpolateSize(gm.gameSpec.minThrobSize,
				gm.gameSpec.maxThrobSize, aFish.birthTime, System.nanoTime(),
				visualHz);

		thisFrame = interpolateBrightness(gm.gameSpec.minBrightness,
				gm.gameSpec.maxBrightness, aFish.birthTime, System.nanoTime(),
				visualHz);

		// double aspectRatio = fishL[12].getHeight() / (1.0 *
		// fishL[12].getWidth());

		int theWidth = gm.gameSpec.minThrobSize; // theSize; //(int) (theSize);
		int theHeight = theSize * fishL[12].getHeight() / fishL[12].getWidth();// (int)
																				// ((theSize
																				// *
																				// aspectRatio)/100);

		if (aFish.fromLeft) {
			g.drawImage(fishL[thisFrame], x - theWidth / 2, y - theHeight / 2,
					theWidth, theHeight, null);
		} else {
			g.drawImage(fishR[thisFrame], x - theWidth / 2, y - theHeight / 2,
					theWidth, theHeight, null);
		}

	}

	// brightness works by cycling through a sprite image that has 25 different
	// levels of brightness.
	private int interpolateBrightness(int min, int max, long birth, long now,
			double freq) {
		// t is the number of cycles so far; take the time in seconds that the
		// actor has been active, multiply by the frequency
		double t = ((now - birth) / 1000000000.0) * freq;
		// y is the sinusoidal position of the cycle
		double y = 0.5 * (Math.sin(Math.PI * 2 * t) + 1);
		// frame oscillates between min and max, as y oscillates from 0 to 1.
		int range = (int) (max - min);
		// int segment = (int) range/25;
		int frame = (int) (range * (y - (y % 0.04)));
		return frame;
	}

	private int interpolateSize(double min, double max, long birth, long now,
			double freq) {
		double t = ((now - birth) / 1000000000.0) * freq;
		double y = 1 - 0.5 * (Math.sin(Math.PI * 2 * t) + 1);
		double s = min * y + max * (1 - y);
		int size = (int) Math.round(s);
		return size;
	}

	// code from http://www.javalobby.org/articles/ultimate-image/
	public static BufferedImage[] spriteImageArray(BufferedImage img, int cols,
			int rows) {
		int w = img.getWidth() / cols;
		int h = img.getHeight() / rows;
		int num = 0;
		BufferedImage imgs[] = new BufferedImage[w * h];
		for (int y = 0; y < rows; y++) {
			for (int x = 0; x < cols; x++) {
				imgs[num] = new BufferedImage(w, h, img.getType());
				// Tell the graphics to draw only one block of the image
				Graphics2D sg = imgs[num].createGraphics();
				sg.drawImage(img, 0, 0, w, h, w * x, h * y, w * x + w, h * y
						+ h, null);
				sg.dispose();
				num++;
			}
		}
		return imgs;
	}

	public static BufferedImage horizontalFlip(BufferedImage img) {
		int w = img.getWidth();
		int h = img.getHeight();
		BufferedImage dimg = new BufferedImage(w, h, img.getType());
		Graphics2D g = dimg.createGraphics();
		g.drawImage(img, 0, 0, w, h, w, 0, 0, h, null);
		g.dispose();
		return dimg;
	}

}
