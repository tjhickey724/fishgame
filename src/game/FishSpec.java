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

	public int throbMinSize = 100;
	public int throbMaxSize = 125;
	public int throbRate = 3;
	private String sep = GenerateScript.SEP;
	
	public FishSpec(){
		// creates a default fish whose fields we set directly.
	}
	
	private String scriptLine(String prop, String val){
		return "0"+sep+prop+sep+val+"\n";
	}
	
	public String toScript(){
		String s ="";
		s+= scriptLine("soundFile",soundFile);
		s+= scriptLine("imageFileLeft",imageFileLeft);
		s+= scriptLine("imageFileRight",imageFileRight);
		s+= scriptLine("throbMinSize",""+throbMinSize);
		s+= scriptLine("throbMaxSize",""+throbMaxSize);
		s+= scriptLine("throbRate",""+throbRate);
		return s;
		
	}

}
