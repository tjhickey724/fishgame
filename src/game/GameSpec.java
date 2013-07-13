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
	
	public String backgroundImage = "images/streamFlip2.jpg";
	
	public int 
	    minFishRelease = 10,
	    maxFishRelease = 40;
	
	public GameSpec(){
		// create default GameSpec
	}


}
