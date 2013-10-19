package game;

import java.util.ArrayList;

/**
 * A Trial specifies a Fish Event. 
 * 
 * @author tim
 * 
 */
public class Trial {
	/**
	 * when the fish should appear after the beginning of the trial
	 */
	public Long interval;
	
	/*
	 * which sound file should be played
	 */
	public String soundFile;
	
	/*
	 * which visual hertz should be used
	 */
	public int visualHz;
	
	// congruent = 0, incongruent = 1, silent = 3
	public int congruent;
	
	public int trial;
	
	public int block;
	
	public boolean fromLeft;
	
	public Species spec;

	/**
	 * 
	 * @param interval
	 *            time in nanoseconds from beginning of the trial
	 * @param soundFile
	 *            name of the sound file in the sounds/ folder
	 * @param visualHz
	 *            the throb rate
	 * @param congruent
	 *            true if the sound hertz matches the visual hertz
	 * @param fromLeft
	 *            true if fish comes from the left
	 * @param spec
	 *            the Species of fish (i.e. good or bad) correlated with the
	 *            visualHz field sort of ...
	 */
	public Trial(Long interval, String soundFile, int visualHz, int congruent,
			Boolean fromLeft, Species spec) {
		this.interval = interval;
		this.soundFile = soundFile;
		this.visualHz = visualHz;
		this.congruent = congruent;
		this.fromLeft = fromLeft;
		this.spec = spec;
	}
	public void setBlock(int block) {
		this.block = block;
	}
	public String toScriptString() {
		return interval.toString() + " " + soundFile + " " + visualHz
				+ " " + congruent + " " + trial + " " + block + " " + (fromLeft? "left": "right")
				+ " " + spec.toString() + "\n";

	}
}
