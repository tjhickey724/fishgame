package game;

import java.awt.Color;
import java.io.IOException;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * a Fish has a position and a velocity and a speed they also have a
 * species and they keep track of whether they are active or not
 * 
 * @author tim
 * 
 */
public class Fish {
	public static long GAME_START = 0; // System.nanoTime();
	
	/**
	 * this indicates whether the user has pressed an appropriate key after seeing the fish
	 * it starts out false and becomes true when a key is pressed
	 */
	public boolean responded=false;
	
	
	/**
	 * x position of the fish in model coordinates
	 */
	public double x;
	
	/**
	 * y position of the fish in model coordinates
	 */
	public double y;
	
	/**
	 * velocity of fish in the x direction in model coordinates
	 */
	public double vx;
	
	/**
	 * velocity of fish in the y direction in model coordinates
	 */
	public double vy;
	
	/**
	 * true if fish should still be displayed on the screen
	 */
	public boolean active;

	/*
	 * speed in model coordinates/sec that the fish moves across the screen
	 */
	public double speed = 40;
	
	/**
	 * true if fish is launched from the left side of the screen
	 */
	public boolean fromLeft; 
	
	/**
	 * true if the fish audio and video have the same hertz
	 */
	public int congruent;
	
	/**
	 * the trial in which this fish appears
	 */
	public int trial;
	
	/**
	 * the block in which this fish appears
	 */
	public int block;
	
	/**
	 * the time this fish was launched (using System.nanoTime())
	 */
	public long birthTime;
	
	/**
	 * the last time the fish was updated (using System.nanoTime())
	 * this is used to calculate how far this fish should be moved
	 * using the velocity and the time since last update
	 */
	public long lastUpdate;
	
	/**
	 * this is the lifetime of the fish in tenths of a second.
	 * After this time has elapsed, the fish will be set as inactive
	 * and removed from the screen.
	 */
	public static double fishLifetime = 20;
	
	/**
	 * the clip of the fish sound that should be played
	 */
	public AudioClip ct;
	
	/**
	 * fish sounds depending on if the fish comes from the left or the right
	 * we shouldn't have to have both in a single fish!
	 */
	public AudioClip ctL, ctR;
	
	/**
	 * the species of the fish (i.e. good or bad)
	 */
	public Species species;

	/*
	 * a random number generator used to give the fish a random type of motion
	 */
	private java.util.Random rand = new java.util.Random();

	/** create a fish with default values **/
	public Fish(){
	}
	
	/**
	 * actors change their velocity slightly at every step but their speed
	 * remains the same. Update slightly modifies their velocity and uses that
	 * to compute their new position. Note that velocity is in units per update.
	 */
	public void update() {
		long now = System.nanoTime();

		if (now < birthTime + fishLifetime * 100*1000*1000) {
			double dt = (now - this.lastUpdate) / (1000*1000*1000.0);
			this.lastUpdate = now;
			
			// modify the vertical component of the fish velocity
			double turnspeed = 0.1;
			vy += rand.nextDouble() * turnspeed - turnspeed / 2;
			
			// normalize so the fish has constant speed
			double tmpSpeed = Math.sqrt(vx * vx + vy * vy);
			vy /= tmpSpeed;
			vx /= tmpSpeed;

			
			// update the position of the fish
			x += vx * speed * dt;
			y += vy * speed * dt;
			
		} else {
			this.active = false;
			this.ct.stop();
		}

	}




	public String toString() {
		return "[" + this.species + "," + this.fromLeft + "]";
	}


}
