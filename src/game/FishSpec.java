package game;


/**
 * this stores a complete specification of how the fish are to be rendered
 * @author tim
 *
 */
public class FishSpec {
	public String soundFile = "sounds/fish6hz0p/fish.wav";
	public String imageFileLeft = "images/fishLeft.png";
	public String imageFileRight = "images/fishRight.png";
	public String rightImageFile = "images/fishRight.png";
	public int throbMinSize = 100;
	public int thromMaxSize = 125;
	public int throbRate = 3;
	
	public FishSpec(){
		// creates a default fish whose fields we set directly.
	}

}
