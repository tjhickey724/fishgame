package game;

import java.io.File;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * This class will generate a script based on specifications from the
 * experimenter. For now we just look at min/max spawning intervals.
 * 
 * @author tim
 * 
 */
public class ScriptGenerator {

	private BufferedWriter scriptFile;
	private Random rand = new Random();
	public final static String SEP = "\t";
	public int fishNum = 0;
	public String scriptname;

	public ArrayList<Trial> trials = new ArrayList<Trial>();


	public ScriptGenerator() {
		this(makeScriptFilename());
	}

	public ScriptGenerator(String scriptname) {
		this.scriptname = scriptname;
	}

	private static String makeScriptFilename() {
		return "scripts/scriptv3_" + System.currentTimeMillis();
	}

	/**
	 * the user can specify what to use for the separator for script values
	 */
	public String sep = "\t";

	/**
	 * this generates N script events where the inter-event interval is between
	 * min/10 and max/10 seconds and is expressed in milliseconds
	 * 
	 * @param N
	 */
	public void generate(GameSpec g) {
		try {
			if (this.scriptname == null) {
				this.scriptname = makeScriptFilename();
			}
			if (this.scriptFile == null) {
				try {
					this.scriptFile = new BufferedWriter(new FileWriter(
							new File(this.scriptname + ".txt")));

					this.scriptFile.write("-1" + sep + "version" + sep

							+ RunGame.versionNum + "\n");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					System.out.println("Error Opening Script File");
					e.printStackTrace();
				}
			}

			scriptFile.write(g.toScript());

			int halfTrials = (int) Math.floor(g.totalTrials / 2);
			int block = 1;

			// trial start time and finish time
			Long tstart = (long) 0;
			Long tfinish = tstart + g.trialLength;

			int i = 0;
			// generate good, congruent trials
			while (i <= halfTrials / 2) {

				trials.add(i, new Trial(getInterval(g.ifi), g.good.soundFile,
						g.good.throbRate, true, (rand.nextInt(2) == 1),
						Species.good));

				i++;
			}
			// congruent bad fishes
			while (i <= halfTrials) {

				trials.add(i, new Trial(getInterval(g.ifi), g.bad.soundFile,
						g.bad.throbRate, true, (rand.nextInt(2) == 1),
						Species.bad));

				i++;
			}
			// incongruent bad fishes
			while (i <= halfTrials / 2 + halfTrials) {

				trials.add(i, new Trial(getInterval(g.ifi), g.good.soundFile,
						g.bad.throbRate, false, (rand.nextInt(2) == 1),
						Species.bad));


				i++;
			}
			// incongruent good fishes

			while (i < g.totalTrials) {
				trials.add(i, new Trial(getInterval(g.ifi), g.bad.soundFile,
						g.good.throbRate, true, (rand.nextInt(2) == 1),
						Species.good));

				i++;
			}
			// shuffle

			Collections.shuffle(trials);
			for (int j = 0; j < trials.size(); j++) {
				trials.get(j).trial =  j + 1;
				// set trial start and finish time
				trials.get(j).interval[1] = tstart;
				trials.get(j).interval[2] = tfinish;
				tstart = tfinish;
				tfinish = tfinish + g.trialLength;

				if ((j + 1) % g.trialsPerBlock == 0)
					block++;

				trials.get(j).block = block;

				System.out.print(trials.get(j).toScriptString());
				scriptFile.write(trials.get(j).toScriptString());

			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}


	// chooses randomly between the 3 interfish intervals from gamespec
	public Long getInterval(Long[] ifi) {
		int pick = rand.nextInt(3);
		return ifi[pick];
	}


	public void close() {
		try {
			scriptFile.write("0\tgameover\t1\n ");
			scriptFile.close();
			scriptFile = null;
			scriptname = null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Problems closing scriptfile:" + e);
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		GameSpec g = new GameSpec();
		ScriptGenerator gs = new ScriptGenerator();
		gs.generate(g);
		gs.close();
	}

}
