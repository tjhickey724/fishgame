package game;

import java.util.ArrayList;

/**
 * This class keeps track of all of the state of a game except for the
 * launching of the fish. These parameters will be written to the script file
 * and the log file to make the game play reproducible..
 * @author tim
 *
 */
public class GameSpec {
	public FishSpec 
		good = new FishSpec(),
		bad = new FishSpec();
	
	public boolean changed = true;
	public boolean requireGameViewUpdate = true;
	
	public boolean stereo = true;
	public boolean hasAvatar=true;
	public String bgSound = "sounds/water/mid.wav";
	
	private String sep = ScriptGenerator.SEP;
	
	public String 
	   backgroundImage = "images/stream.jpg";
	
	//length of the session in minutes
	public int runLength=20;
	//how many blocks in the session. a block is the time that the subject is being scanned.
	public int blocksPerRun=5;
	//how many trials in a block. a trial is a fish event. 
	public int trialsPerBlock=50;
	
	public double blockLength = runLength/blocksPerRun;
	public double trialLength = blockLength/trialsPerBlock;
	public int totalTrials = trialsPerBlock*blocksPerRun;
	public ArrayList<Trial> trials = new ArrayList<Trial>();
	
	public String goodResponseSound = "sounds/good.wav";
	public String badResponseSound = "sounds/bad.wav";
	
	// we can expand to more sounds later ...
	/*
	public String eatGood = goodSound;
	public String eatBad = badSound;
	public String killGood = badSound;
	public String killBad = goodSound;
	public String missGood = badSound;
	public String missBad = badSound;
	public String pushKey = badSound;
	*/
	
	public int 
	    minFishRelease = 30,
	    maxFishRelease = 60;
	
	public int
		minThrobSize = 100,
		maxThrobSize = 125;

	public int minBrightness= 10;
	public int maxBrightness= 14;
	
	public GameSpec(){
		// create default GameSpec
	}
	
	private String scriptLine(String prop, String val){
		return "0"+sep+prop+sep+val+"\n";
	}
	
	public String toScript(){
		String s="";


		s+= scriptLine("minFishRelease",""+minFishRelease);
		s+= scriptLine("maxFishRelease",""+maxFishRelease);
		s+= scriptLine("stereo",""+stereo);
		s+= scriptLine("minThrobSize",""+minThrobSize);
		s+= scriptLine("maxThrobSize",""+maxThrobSize);
		s+= scriptLine("minBrightness",""+minBrightness);
		s+= scriptLine("maxBrightness",""+maxBrightness);
		s+= scriptLine("bgSound",""+this.bgSound);
		s += good.toScript("good");
		s += bad.toScript("bad");
		s+= scriptLine("backgroundImage",backgroundImage);
		s+= scriptLine("goodSound",""+goodResponseSound);
		s+= scriptLine("badSound",""+badResponseSound);
		s+= scriptLine("hasAvatar",""+hasAvatar);
		return(s);
		
	}
	
	/**
	 * this will change the value in the specified property, if it exists
	 * We could replace this whole class with a HashMap though....
	 * and maybe we should!
	 * @param prop
	 * @param value
	 * @return
	 */
	public boolean update(String prop, String value){
		this.changed=true;
		if (prop.equals("backgroundImage")){
		    this.backgroundImage = value;
			this.requireGameViewUpdate = true;
	    }else if (prop.equals("maxFishRelease")) {
			this.maxFishRelease = Integer.parseInt(value);
		}else if (prop.equals("minFishRelease")) {
			this.minFishRelease = Integer.parseInt(value);
		} else if (prop.equals("stereo")){
			this.stereo = "true".equals(value);
		} else if (prop.equals("maxThrobSize")){
			this.maxThrobSize = Integer.parseInt(value);
		} else if (prop.equals("minThrobSize")){
			this.minThrobSize = Integer.parseInt(value);
		} else if (prop.startsWith("good")) {
			return this.good.update(prop.substring(4), value);
		} else if (prop.startsWith("bad")) {
			return this.bad.update(prop.substring(3),value);
		} else if (prop.equals("bgSound")) {
			this.bgSound = value;
			this.requireGameViewUpdate = true;
		}else if (prop.equals("minBrightness")){
			this.minBrightness = Integer.parseInt(value);
		}else if (prop.equals("maxBrightness")){
			this.maxBrightness = Integer.parseInt(value);
		
		}else if (prop.equals("hasAvatar")){
			this.hasAvatar = (value == "true" ? true : false);
		} else return false;
		return true;
	}


}
