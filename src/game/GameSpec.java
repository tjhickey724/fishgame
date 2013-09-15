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

	// length of the session in milliseconds
	public int runLength = 18 * 60 * 1000;

	// how many blocks in the session. a block is the time that the subject is
	// being scanned.
	public int blocksPerRun = 6;

	// how many trials in a block. a trial is a fish event.
	public int trialsPerBlock = 30;

	// length of a block in milliseconds
	public Long blockLength;

	public Long trialLength;

	public int totalTrials;

	// interfish intervals stored in an array...
	public Long[] ifi = new Long[3];

	public String goodResponseSound = "sounds/good.wav";
	public String badResponseSound = "sounds/bad.wav";

	// we can expand to more sounds later ...
	/*
	 * public String eatGood = goodSound; public String eatBad = badSound;
	 * public String killGood = badSound; public String killBad = goodSound;
	 * public String missGood = badSound; public String missBad = badSound;
	 * public String pushKey = badSound;
	 */

	public int minFishRelease = 30, maxFishRelease = 60;

	public int minThrobSize = 100, maxThrobSize = 125;

	public int minBrightness = 10;
	public int maxBrightness = 14;

	public GameSpec() {
		// create default GameSpec
		

	}

	public void createIntervals(){
				ifi[0] = trialLength / 2;
		ifi[1] = ifi[0] + trialLength / 4;
		ifi[2] = ifi[0] - trialLength / 4;
		
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

		s += scriptLine("totalTrials", "" + totalTrials);
		s += scriptLine("runLength", ""+runLength);
		s += scriptLine("blocksPerRun", ""+blocksPerRun);
		s += scriptLine("blockLength", blockLength.toString()); // milliseconds
		s += scriptLine("trialsPerBlock", ""+trialsPerBlock);
		s += scriptLine("trialLength", trialLength.toString()); // milliseconds

		s += scriptLine("hasAvatar", "" + hasAvatar);

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
		} else if (prop.equals("runLength")){
			this.runLength = Integer.parseInt(value);
		}else if (prop.equals("blocksPerRun")){
			this.blocksPerRun = Integer.parseInt(value);
		} else if (prop.equals("trialsPerBlock")){ 
			this.trialsPerBlock = Integer.parseInt(value);
		} else if (prop.equals("hasAvatar")) {
			this.hasAvatar = (value == "true" ? true : false);

		} else
			return false;
		return true;
	}

}
