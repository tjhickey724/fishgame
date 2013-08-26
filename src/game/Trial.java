package game;

import java.util.ArrayList;

public class Trial {
	public ArrayList<Object> specs= new ArrayList<Object>();
	public Trial(String soundFile, int visualHz, boolean congruent,  Boolean fromLeft, Species spec){
		
		specs.add(0, soundFile);
		specs.add(1, visualHz);
		specs.add(2, congruent);
		specs.add(3, 0);
		specs.add(4, 0);
		specs.add(5, fromLeft);
		specs.add(6, spec);
		
	}
	
	public String toScriptString(){
		return specs.get(0) + " " + specs.get(1) + " " + specs.get(2) + " " + specs.get(3) + " " + specs.get(4)
				+ " " + specs.get(5) + " " + specs.get(6).toString() +"\n";
				}
	}


