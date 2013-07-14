package game;


/**
 * this stores a complete specification of how the fish are to be rendered
 * @author tim
 *
 */
public class FishSpec {
	public String soundFile = "sounds/fish6hz0p";
	public String imageFile = "images/fish1";
	public String imageFileLeft = "images/fishLeft.png";
	public String imageFileRight = "images/fishRight.png";

	public int throbMinSize = 100;
	public int throbMaxSize = 125;
	public int throbRate = 3;
	private String sep = ScriptGenerator.SEP;
	
	public FishSpec(){
		// creates a default fish whose fields we set directly.
	}
	
	private String scriptLine(String prop, String val){
		return "0"+sep+prop+sep+val+"\n";
	}
	
	public String toScript(String type){
		String s ="";
		s+= scriptLine(type+"soundFile",soundFile);
		s += scriptLine(type+"imageFile",imageFile);
		s+= scriptLine(type+"imageFileLeft",imageFileLeft);
		s+= scriptLine(type+"imageFileRight",imageFileRight);
		s+= scriptLine(type+"throbMinSize",""+throbMinSize);
		s+= scriptLine(type+"throbMaxSize",""+throbMaxSize);
		s+= scriptLine(type+"throbRate",""+throbRate);
		return s;		
	}
	
	public boolean update(String prop, String val){

		if (prop.equals("soundFile")) {
			this.soundFile = val;
		} else if (prop.equals("imageFile")){
			this.imageFile = val;
		} else if (prop.equals("imageFileLeft")){
			this.imageFileLeft = val;
		} else if (prop.equals("imageFileRight")){
			this.imageFileRight = val;
		} else if (prop.equals("throbMinSize")){
			throbMinSize = Integer.parseInt(val);
		} else if (prop.equals("throbMaxSize")){
			throbMaxSize = Integer.parseInt(val);
		} else if (prop.equals("tbrobRate")) {
			throbRate = Integer.parseInt(val);
		} else return false;

		return true;
	}

}
