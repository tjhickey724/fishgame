package game;

import java.awt.Color;
import java.io.IOException;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * a GameActor has a position and a velocity and a speed they also have a
 * species and they keep track of whether they are active or not
 * 
 * @author tim
 * 
 */
public class GameActor {
	// this is a bit of a hack, I need to refactor later REFACTOR!!
	public static long GAME_START = 0; // System.nanoTime();

	String soundFolder = "fish_6_8_hz_pan0"; // "fish_3_5_hz_pan50";
	// size
	double radius = 10;
	// position
	double x;
	double y;
	// velocity
	double vx;
	double vy;
	// still on board?
	boolean active;

	// speed
	double speed = 40;
	// species
	boolean fromLeft; // true if fish comes from left
	public int congruent;
	public int trial;
	long birthTime;
	long lastUpdate;
	long deathTime;
	// this is the time it stays on screen, in tenths of a second
	public static double timeOnScreen = 20;
	long lifeSpan;

	long gameStart = GameActor.GAME_START;
	Color color1 = new Color(150, 0, 0), color2 = new Color(200, 0, 0),
			color3 = new Color(100, 100, 100),
			color4 = new Color(250, 250, 250);
	double colorHerz = 4;
	// Origin:
	int origin;
	AudioClip ct, ctL, ctR;
	// AudioClip bt;
	Species species;
	
	/** determines whether visual(0) or audio(1) specifies good or bad  **/
	public int avmode=0;

	public int minBrightness = 10;
	public int maxBrightness = 14;


	private java.util.Random rand = new java.util.Random();

	public GameActor(double x, double y, boolean active, Species spec, int avmode) {
		this(x, y, active, spec, true, "sounds/fish6hz0p", "sounds/fish8hz0p",avmode,0);
	}

	public GameActor(double x, double y, boolean active, Species spec,
			boolean stereo, String goodFishSounds, String badFishSounds,
			int avmode, int congruent) {
		this.congruent = congruent;
		this.avmode = avmode;
		this.x = x;
		this.y = y;
		this.active = active;
		// fish starts off moving forward always
		this.vx = speed * (rand.nextDouble());
		this.vy = speed * (rand.nextDouble() - 0.5);
		this.birthTime = System.nanoTime();
		this.lastUpdate = this.birthTime;
		this.gameStart = GameActor.GAME_START;
		this.species = spec;
		String fishSounds;
		// handles congruence, i.e. if sounds and visuals agree...
		// for incongruence the one the changes depends on the avmode
		// so in avmode=0 the sound is flipped
		// in avmode=1 the visuals are flipped
		// first we give default values for the fishsounds
		if (species.equals(Species.good))
			fishSounds = goodFishSounds;
		else
			fishSounds = badFishSounds;
		
		if (congruent == 0) {
			; // use the default values in all avmodes
		} else if (congruent == 1){
			if (avmode==0){
				if (species.equals(Species.good))
					fishSounds = badFishSounds;
				else
					fishSounds = goodFishSounds;
			} else {
				; // we should switch the visual hz but that will be done in GameView ...
				
			}
		} else{ // congruent = 2 so 
			if (avmode == 0){ // visual mode so the sound is silent, so no change
			   ;}
			else { // avmode == 1, auditory mode so the visual is non-oscillating, and use default sounds
				;
			}
		}

		try {
			this.ct = new AudioClip(fishSounds + "/fish.wav");
			if (stereo) {
				this.ctR = new AudioClip(fishSounds + "/fishR.wav");
				this.ctL = new AudioClip(fishSounds + "/fishL.wav");
				// (fishSounds+"/fishR.wav");
			} else {
				this.ctR = this.ct;
				this.ctL = this.ct;
			}
		} catch (Exception e) {
			System.out.println("Audio problems!!:" + e);
		}
	}

	public GameActor(double x, double y) { // throws
											// UnsupportedAudioFileException,
											// IOException,
											// LineUnavailableException {
		this(x, y, true, Species.good,0);
	}

	public GameActor(int avmode) { // throws UnsupportedAudioFileException, IOException,
							// LineUnavailableException {
		this(0, 0, true, Species.good, avmode);
	}
	

	/** 
	 * actors change their velocity slightly at every step but their speed
	 * remains the same. Update slightly modifies their velocity and uses that
	 * to compute their new position. Note that velocity is in units per update.
	 */
	public void update() {
		long now = System.nanoTime();

		if (now < birthTime + timeOnScreen * 100000000) {
			this.lifeSpan = now - birthTime;
			double dt = (now - this.lastUpdate) / 1000000000.0;
			this.lastUpdate = now;
			double turnspeed = 0.1;
			// vx += rand.nextDouble()*turnspeed -turnspeed/2;
			vy += rand.nextDouble() * turnspeed - turnspeed / 2;
			double tmpSpeed = Math.sqrt(vx * vx + vy * vy);
			// vx /= tmpSpeed;
			vy /= tmpSpeed;
			x += vx * speed * dt;
			y += vy *5 * speed * dt;
		} else {
			this.active = false;
			if(congruent != 2)
				this.ct.stop();
		}

	}

	// set congruent
	public void setCongruent(int congruent) {
		this.congruent = congruent;
	}

	// set block
	public void setTrial(int trial) {
		this.trial = trial;
	}

	public String toString() {
		int ix = (int) x;
		int iy = (int) y;
		// return "["+ix+","+iy+","+active+"]";
		return "[" + this.species + "," + this.origin + "]";
	}

	public String printStream() {
		String output = toString();
		output += "Fish is active: " + active + " Birth Time:"
				+ " last updated: " + lastUpdate;
		return output;
	}

}
