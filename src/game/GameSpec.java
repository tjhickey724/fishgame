package game;

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
	
	public boolean stereo = true;
	
	private String sep = ScriptGenerator.SEP;
	
	public String 
	   backgroundImage = "images/stream.jpg",
	   backgroundImageFlipped = "images/streamFlip2.jpg";
	
	
	public String goodSound = "sounds/good.wav";
	public String badSound = "sounds/bad.wav";
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
	
	public GameSpec(){
		// create default GameSpec
	}
	
	private String scriptLine(String prop, String val){
		return "0"+sep+prop+sep+val+"\n";
	}
	
	public String toScript(){
		String s="";
		s += good.toScript("good");
		s += bad.toScript("bad");
		s+= scriptLine("backgroundImage",backgroundImage);
		s+= scriptLine("backgroundImageFlipped",backgroundImageFlipped);
		s+= scriptLine("minFishRelease",""+minFishRelease);
		s+= scriptLine("maxFishRelease",""+maxFishRelease);
		s+= scriptLine("goodSound",""+goodSound);
		s+= scriptLine("badSound",""+badSound);
		s+= scriptLine("stereo",""+stereo);
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
	    }else if (prop.equals("maxFishRelease")) {
			this.maxFishRelease = Integer.parseInt(value);
		}else if (prop.equals("minFishRelease")) {
			this.minFishRelease = Integer.parseInt(value);
		} else if (prop.equals("stereo")){
			this.stereo = "true".equals(value);
		} else if (prop.startsWith("good")) {
			return this.good.update(prop.substring(4), value);
		} else if (prop.startsWith("bad")) {
			return this.bad.update(prop.substring(3),value);
		} else return false;
		return true;
	}


}