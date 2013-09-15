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
 * @author tim
 * 
 */
public class GameView extends JPanel {
	private static final long serialVersionUID = 1L;

	private GameModel gm = null;
	boolean hasAvatar = true;
	boolean flash = false;
	
	public AudioClip bgSound;
	String lastbgSound;

	public AudioClip goodclip, badclip;

	ArrayList<String> keylog = new ArrayList<String>();

	public boolean gameActive = false; // shouldn't this be in the model???


	public BufferedImage streamImage, streamImage2, fish, boat, coin;


	// these arrays store the sprite images used to adjust brightness. the
	// default brightness is in image, 12, accesible using fishL[12] or
	// fishR[12]
	public BufferedImage[] fishL, fishR;
	public long indicatorUpdate;

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
				// when a key is pressed, we send a 'flash' to the indicator in the corner
				flash=true;
				// we set the update time to be 50 ms after the keypress, so the indicator stays lit for 50 ms
				indicatorUpdate=System.nanoTime()+50000000l;
				// play good/bad sounds alone by key press for demo purpose
				if (e.getKeyChar() == 'g') {
					goodclip.play();
					return;
				} else if (e.getKeyChar() == 'b') {
					badclip.play();
					return;
				}

				// first check to see if they pressed
				// when there are no fish!!
				if (gm.getNumFish() == 0) {
					String log = "KeyPress for no fish!";
					// gm.writeToLog(log);
					gm.writeToLog(new GameEvent(e.getKeyChar()));
					badclip.play();
					return;
				}
				// otherwise, remove the last fish (should only be one!)
				GameActor lastFish = gm.removeLastFish();

				GameEvent ge = new GameEvent(e.getKeyChar(), lastFish);

				// get the response time and write it to the log
				long keyPressTime = System.nanoTime();
				long responseTime = keyPressTime - lastFish.birthTime;

				String log = e.getKeyChar() + " " + responseTime / 1000000.0
						+ " " + ge.correctResponse + " " + lastFish;

				System.out.println(log);

				
				gm.writeToLog(ge);


				// play the appropriate sound and modify the score

				if (ge.correctResponse) {

					goodclip.play();
					gm.wealth++;
					gm.setHits(gm.getHits() + 1);
				} else {
					badclip.play();
					gm.wealth--;
					gm.setMisses(gm.getMisses() + 1);
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

		badclip = new AudioClip(gs.badResponseSound);

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
			fishL = spriteImageArray(fish, 5, 5);
			fishR = spriteImageArray(horizontalFlip(fish), 5, 5);
			hasAvatar=gs.hasAvatar;
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
	 * toViewCoords(x) converts from model coordinates to pixels on the screen
	 * so that objects can be drawn to scale, i.e. as the screen is resized the
	 * objects change size proportionately.
	 * 
	 * @param x
	 *            the unit in model coordinates
	 * @return the corresponding value in pixel based on window-size
	 */
	public int toViewCoords(double x) {
		int width = this.getWidth();
		int height = this.getHeight();
		int viewSize = (width < height) ? width : height;
		return (int) Math.round(x / gm.size * viewSize);
	}

	public int toXViewCoords(double x) {
		int width = this.getWidth();
		int height = this.getHeight();
		int viewSize = (width < height) ? width : height;
		return (int) Math.round(x / gm.size * width);
	}

	public int toYViewCoords(double x) {
		int width = this.getWidth();
		int height = this.getHeight();
		int viewSize = (width < height) ? width : height;
		return (int) Math.round(x / gm.size * height);
	}

	/**
	 * toModelCoords(x) is used to convert mouse locations to positions in the
	 * model so that the avatar position in the model can be changed correctly
	 * 
	 * @param x
	 *            position in pixels in view
	 * @return position in model coordinates
	 */

	// bug in this code when the game is scale wider than it is tall
	// BUG
	public double toModelCoords(int x) {
		int width = this.getWidth();
		int height = this.getHeight();
		int viewSize = (width < height) ? width : height;
		return x * gm.size / viewSize;
	}

	/**
	 * paintComponent(g) draws the current state of the model onto the
	 * component. It first repaints it in blue, then draws the scene
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (gm == null)
			return; // this shouldn't ever happen!

		if (gm.gameSpec.requireGameViewUpdate) {
			// if the gameSpec changes, need to update gameState
			this.updateGameState(gm.gameSpec);
			gm.gameSpec.requireGameViewUpdate = false; // DEBUG Threadsafe??
		}

		if (gm.isGameOver()) {
			g.setFont(new Font("Helvetica", Font.BOLD, 50));
			g.drawString("GAME OVER", 100, 100);
			return;
		}

		drawBackground(g);

		drawTimeBar(g);

		drawHud(g);

		drawFish(g);

		if (hasAvatar)
			drawAvatar(g);
		
		drawIndicator(g);


		updateScore(g);

	}


	private void drawIndicator(Graphics g) {
		if (flash){
			g.setColor(Color.white);
			
			if (indicatorUpdate<System.nanoTime())flash=false;
		} else if (gm.flash){
			g.setColor(Color.white);
			if (gm.indicatorUpdate<System.nanoTime())gm.flash=false;
		} else {
			g.setColor(Color.black);
		}
		
		g.fillRect(0, getHeight()-60, 60, 60);
		
		
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
		g.drawString(gm.wealth + "", 55, 65);
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

		header.setText("<html><table style=\"font-size:24pt;\">"
				+ "<tr><td>Right:</td>" + "<td>Wrong:</td>"
				+ "<td>Misses:</td>" + "<td>Total:</td>" + "</tr>" + "<tr><td>"
				+ gm.getHits() + "</td><td>" + gm.getMisses() + "</td><td>"
				+ gm.getNoKeyPress() + "</td>" + +gm.getFishNum() + "</td>"
				+ "</tr></table></html>");

	}

	private void drawFish(Graphics g) {
		java.util.List<GameActor> gaList = gm.getActorList();
		for (GameActor a : gaList) {
			drawActor(g, a, Color.WHITE);
		}
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
				this.bgSound.stop();
			}

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
	 * @param a
	 *            - the Actor to be drawn
	 * @param c
	 *            - the default color for actors of unknown species
	 */
	private void drawActor(Graphics g, GameActor a, Color c) {
		if (!a.active)
			return;
		int theRadius = toViewCoords(a.radius);
		int x = toXViewCoords(a.x);
		int y = toYViewCoords(a.y);
		int visualHz = 1;

		switch (a.species) {
		case good:
			visualHz = gm.gameSpec.good.throbRate;
			break;
		case bad:
			visualHz = gm.gameSpec.bad.throbRate;
			break;
		}

		int theSize = gm.interpolateSize(gm.gameSpec.minThrobSize,
				gm.gameSpec.maxThrobSize, a.birthTime, System.nanoTime(),
				visualHz);

		thisFrame = interpolateBrightness(gm.gameSpec.minBrightness,
				gm.gameSpec.maxBrightness, a.birthTime, System.nanoTime(),
				visualHz);

		double aspectRatio = fishL[12].getHeight()
				/ (1.0 * fishL[12].getWidth());

		int theWidth = gm.gameSpec.minThrobSize; // theSize; //(int) (theSize);
		int theHeight = theSize * fishL[12].getHeight() / fishL[12].getWidth();// (int)
																				// ((theSize
																				// *
																				// aspectRatio)/100);
		// here we send a 'flash' to the corner indicator

		if (a.fromLeft) {
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
