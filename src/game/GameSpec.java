package game;

import java.util.ArrayList;

/**
 * This class keeps track of all of the state of a game except for the launching
 * of the fish. These parameters will be written to the script file and the log
 * file to make the game play reproducible..
 * 
 * @author tim
 * 
 */
public class GameSpec {
	public FishSpec good = new FishSpec(), bad = new FishSpec();

	public boolean changed = true;
	public boolean requireGameViewUpdate = true;

	public boolean stereo = true;

	public boolean hasAvatar = true;

	public String bgSound = "sounds/water/mid.wav";

	private String sep = ScriptGenerator.SEP;

	public String backgroundImage = "images/streamB.jpg";

	public int numcongruent = 10;
	public int numincongruent = 10;
	public int numMissing = 10;
	public int blocks = 5;
	

	public String goodResponseSound = "sounds/good.wav";
	public String badResponseSound = "sounds/bad.wav";
	public String neutralResponseSound = "sounds/neutral.wav";
	public String silence = "sounds/silence.wav";
	// we can expand to more sounds later ...
	/*
	 * public String eatGood = goodSound; public String eatBad = badSound;
	 * public String killGood = badSound; public String killBad = goodSound;
	 * public String missGood = badSound; public String missBad = badSound;
	 * public String pushKey = badSound;
	 */

	public int minFishRelease = 30, maxFishRelease = 60;
	
	/**  length of a trial in tenths of a second **/
	public int trialLength = 30;
	
	public int minThrobSize = 100, maxThrobSize = 125;
	public int interval[] = {25, 35, 45}; // fish entrance times in tenths of a second
	public int minBrightness = 10;
	public int maxBrightness = 14;
	public int mode = 1;
	public GameSpec() {

	}
	private String scriptLine(String prop, String val) {

		return "-1" + sep + prop + sep + val + "\n";

	}

	public String toScript() {
		String s = "";

		s += scriptLine("stereo", "" + stereo);
		s += scriptLine("minThrobSize", "" + minThrobSize);
		s += scriptLine("maxThrobSize", "" + maxThrobSize);
		s += scriptLine("minBrightness", "" + minBrightness);
		s += scriptLine("maxBrightness", "" + maxBrightness);
		s += scriptLine("bgSound", "" + this.bgSound);
		s += good.toScript("good");
		s += bad.toScript("bad");
		s += scriptLine("backgroundImage", backgroundImage);
		s += scriptLine("goodSound", "" + goodResponseSound);
		s += scriptLine("badSound", "" + badResponseSound);

		s += scriptLine("Congruent Trials", "" + numcongruent);
		s += scriptLine("Incongruent Trials", "" + numincongruent);
		s += scriptLine("Missing Stimulus Trials", "" + numMissing);
		s += scriptLine("trialLength", "" + trialLength);
		s += scriptLine("hasAvatar", "" + hasAvatar);
		s += scriptLine("mode", "" + mode);
		return (s);

	}

	/**
	 * this will change the value in the specified property, if it exists We
	 * could replace this whole class with a HashMap though.... and maybe we
	 * should!
	 * 
	 * @param prop
	 * @param value
	 * @return
	 */
	public boolean update(String prop, String value) {
		this.changed = true;
		if (prop.equals("backgroundImage")) {
			this.backgroundImage = value;
			this.requireGameViewUpdate = true;
		} else if (prop.equals("maxFishRelease")) {
			this.maxFishRelease = Integer.parseInt(value);
		} else if (prop.equals("minFishRelease")) {
			this.minFishRelease = Integer.parseInt(value);
		} else if (prop.equals("stereo")) {
			this.stereo = "true".equals(value);
		} else if (prop.equals("maxThrobSize")) {
			this.maxThrobSize = Integer.parseInt(value);
		} else if (prop.equals("minThrobSize")) {
			this.minThrobSize = Integer.parseInt(value);
		} else if (prop.startsWith("good")) {
			return this.good.update(prop.substring(4), value);
		} else if (prop.startsWith("bad")) {
			return this.bad.update(prop.substring(3), value);
		} else if (prop.equals("bgSound")) {
			this.bgSound = value;
			this.requireGameViewUpdate = true;
		} else if (prop.equals("minBrightness")) {
			this.minBrightness = Integer.parseInt(value);
		} else if (prop.equals("maxBrightness")) {
			this.maxBrightness = Integer.parseInt(value);
		} else if (prop.equals("hasAvatar")) {
			this.hasAvatar = (value == "true" ? true : false);
		} else if (prop.equals("congruentTrials")){
			this.numcongruent = Integer.parseInt(value);
		}
		else if (prop.equals("incongruentTrials")){
			this.numincongruent = Integer.parseInt(value);
		}
		else if (prop.equals("missingTrials")){
			this.numMissing = Integer.parseInt(value);
		}
		else if(prop.equals("trialLength")){
			this.trialLength = Integer.parseInt(value);
		} else if (prop.equals("mode")){
			this.mode = Integer.parseInt(value);
		} else
			return false;
		return true;
	}

}
