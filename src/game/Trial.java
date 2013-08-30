package game;

import java.util.ArrayList;

public class Trial {
	public ArrayList<Object> specs = new ArrayList<Object>();

	public Trial(Long interval, String soundFile, int visualHz,
			boolean congruent, Boolean fromLeft, Species spec) {
		specs.add(0, interval);
		specs.add(1, 0);
		specs.add(2, 0);
		specs.add(3, soundFile);
		specs.add(4, visualHz);
		specs.add(5, congruent);
		specs.add(6, 0);
		specs.add(7, 0);
		specs.add(8, fromLeft);
		specs.add(9, spec);

	}

	public String toScriptString() {
		return specs.get(0).toString() + " " + specs.get(1).toString() + " "
				+ specs.get(2).toString() + " " + specs.get(3) + " " + specs.get(4) + " "
				+ specs.get(5) + " " + specs.get(6) + " " + specs.get(7) + " " + specs.get(8) + " "
				+ specs.get(9).toString() + "\n";
	}
}
