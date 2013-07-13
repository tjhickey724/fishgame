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
		left = new FishSpec(),
		right = new FishSpec();
	
	private String sep = ScriptGenerator.SEP;
	
	public String backgroundImage = "images/streamFlip2.jpg";
	
	public int 
	    minFishRelease = 10,
	    maxFishRelease = 40;
	
	public GameSpec(){
		// create default GameSpec
	}
	
	private String scriptLine(String prop, String val){
		return "0"+sep+prop+sep+val+"\n";
	}
	
	public String toScript(){
		String s="";
		s += left.toScript();
		s += right.toScript();
		s+= scriptLine("backgroundImage",backgroundImage);
		s+= scriptLine("minFishRelease",""+minFishRelease);
		s+= scriptLine("maxFishRelease",""+maxFishRelease);
		return(s);
		
	}


}
