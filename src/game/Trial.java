package game;

import java.util.ArrayList;


/**
 * A Trial is a Fish Event, e.g. Good Fish from Right Bad Fish from Left and it
 * has a fixed amount of time. In fMRI mode, the previous trial ends with the
 * fish disappearing from the screen The new trial begins with no fish, and the
 * after an inter-fish interval the fish appears
 * 
 * @author tim
 * 
 */
public class Trial {
	/**
	 * an ArrayList storing the properties of the trial
	 */
	public ArrayList<Object> specs = new ArrayList<Object>();

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
	public Trial(Long interval, String soundFile, int visualHz,
			boolean congruent, Boolean fromLeft, Species spec) {
		specs.add(0, interval);
		// trial start
		specs.add(1, 0);
		// trial end
		specs.add(2, 0);
		specs.add(3, soundFile);
		specs.add(4, visualHz);
		specs.add(5, congruent);
		// block
		specs.add(6, 0);
		// trial number
		specs.add(7, 0);
		specs.add(8, fromLeft);
		specs.add(9, spec);
	}

	public String toScriptString() {
		return specs.get(0).toString() + " " + specs.get(1).toString() + " "
				+ specs.get(2).toString() + " " + specs.get(3) + " "
				+ specs.get(4) + " " + specs.get(5) + " " + specs.get(6) + " "
				+ specs.get(7) + " " + specs.get(8) + " "
				+ specs.get(9).toString() + "\n";

	}
}
