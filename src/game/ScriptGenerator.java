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
	public int mode=0;
	
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
			int sixthTrials;
			mode=g.avmode;
			sixthTrials = (int) Math.floor(g.totalTrials / 6);
			
			int i = 0;
			// generate good, congruent trials
			while (i < sixthTrials) {

				trials.add(i, new Trial(getInterval(g.interval), g.good.soundFile,
						g.good.throbRate, 0, (rand.nextInt(2) == 1),
						Species.good));

				i++;
			}
			// congruent bad fishes
			while (i < sixthTrials * 2) {

				trials.add(i, new Trial(getInterval(g.interval), g.bad.soundFile,
						g.bad.throbRate, 0, (rand.nextInt(2) == 1),
						Species.bad));

				i++;
			}
			// incongruent bad fishes
			while (i < sixthTrials * 3) {
				if (mode==0){
				trials.add(i, new Trial(getInterval(g.interval), g.good.soundFile,
						g.bad.throbRate, 1, (rand.nextInt(2) == 1),
						Species.bad));
				}
				else if (mode==1){
					trials.add(i, new Trial(getInterval(g.interval), g.bad.soundFile,
							g.good.throbRate, 1, (rand.nextInt(2) == 1),
							Species.bad));
				}
				i++;
			}
			// incongruent good fishes
			
			while (i < sixthTrials * 4) {
				if (mode==0){
				trials.add(i, new Trial(getInterval(g.interval), g.bad.soundFile,
						g.good.throbRate, 1, (rand.nextInt(2) == 1),
						Species.good));
				} else if (mode==1){
					trials.add(i, new Trial(getInterval(g.interval), g.good.soundFile,
							g.bad.throbRate, 1, (rand.nextInt(2) == 1),
							Species.good));
				}

				i++;
			}
			//good silent fish
			while (i < sixthTrials * 5) {
				if (mode==0){
				trials.add(i, new Trial(getInterval(g.interval), g.silentResponseSound,
						g.good.throbRate, 2, (rand.nextInt(2) == 1),
						Species.good));
				} else if (mode==1){
					trials.add(i, new Trial(getInterval(g.interval), g.good.soundFile,
							0, 2, (rand.nextInt(2) == 1),
							Species.good));
				}
				i++;
			}
			//bad silent fish
			while (i < sixthTrials * 6) {
				if (mode==0){
				trials.add(i, new Trial(getInterval(g.interval), g.silentResponseSound,
						g.bad.throbRate, 2, (rand.nextInt(2) == 1),
						Species.bad));
				} else if (mode==1){
					trials.add(i, new Trial(getInterval(g.interval), g.bad.soundFile,
							0, 2, (rand.nextInt(2) == 1),
							Species.bad));
				}
				i++;
			}
			
			//here we solve the issue of the remainder by distributing it among the different conditions.
			if(g.totalTrials%6==1){
				//good cong
				trials.add(i, new Trial(getInterval(g.interval), g.good.soundFile,
						g.good.throbRate, 0, (rand.nextInt(2) == 1),
						Species.good));

				i++;
			} else if(g.totalTrials%6==2){
				//good cong

				trials.add(i, new Trial(getInterval(g.interval), g.good.soundFile,
						g.good.throbRate, 0, (rand.nextInt(2) == 1),
						Species.good));

				i++;
				//bad cong
				trials.add(i, new Trial(getInterval(g.interval), g.bad.soundFile,
						g.bad.throbRate, 0, (rand.nextInt(2) == 1),
						Species.bad));
				i++;
			} else if(g.totalTrials%6==3){
				//good cong

				trials.add(i, new Trial(getInterval(g.interval), g.good.soundFile,
						g.good.throbRate, 0, (rand.nextInt(2) == 1),
						Species.good));

				i++;
				//bad cong
				trials.add(i, new Trial(getInterval(g.interval), g.bad.soundFile,
						g.bad.throbRate, 0, (rand.nextInt(2) == 1),
						Species.bad));

				i++;
				//bad incong
				if (mode==0){
				trials.add(i, new Trial(getInterval(g.interval), g.good.soundFile,
						g.bad.throbRate, 1, (rand.nextInt(2) == 1),
						Species.bad));
				} else if (mode==1){
					trials.add(i, new Trial(getInterval(g.interval), g.bad.soundFile,
							g.good.throbRate, 1, (rand.nextInt(2) == 1),
							Species.bad));
				}
				i++;

			} else if(g.totalTrials%6==4){
				//good cong

				trials.add(i, new Trial(getInterval(g.interval), g.good.soundFile,
						g.good.throbRate, 0, (rand.nextInt(2) == 1),
						Species.good));

				i++;
				//bad cong
				trials.add(i, new Trial(getInterval(g.interval), g.bad.soundFile,
						g.bad.throbRate, 0, (rand.nextInt(2) == 1),
						Species.bad));

				i++;
				//bad incong
				if (mode==0){
				trials.add(i, new Trial(getInterval(g.interval), g.good.soundFile,
						g.bad.throbRate, 1, (rand.nextInt(2) == 1),
						Species.bad));
				} else if (mode==1){
					trials.add(i, new Trial(getInterval(g.interval), g.bad.soundFile,
							g.good.throbRate, 1, (rand.nextInt(2) == 1),
							Species.bad));
				}
				i++;
				//good incong
				if (mode==0){
					trials.add(i, new Trial(getInterval(g.interval), g.bad.soundFile,
							g.good.throbRate, 1, (rand.nextInt(2) == 1),
							Species.good));
					} else if (mode==1){
						trials.add(i, new Trial(getInterval(g.interval), g.good.soundFile,
								g.bad.throbRate, 1, (rand.nextInt(2) == 1),
								Species.good));
					}

				i++;
			} else if(g.totalTrials%6==5){
				//good cong
				trials.add(i, new Trial(getInterval(g.interval), g.good.soundFile,
						g.good.throbRate, 0, (rand.nextInt(2) == 1),
						Species.good));

				i++;
				//bad cong
				trials.add(i, new Trial(getInterval(g.interval), g.bad.soundFile,
						g.bad.throbRate, 0, (rand.nextInt(2) == 1),
						Species.bad));

				i++;
				//bad incong
				if (mode==0){
				trials.add(i, new Trial(getInterval(g.interval), g.good.soundFile,
						g.bad.throbRate, 1, (rand.nextInt(2) == 1),
						Species.bad));
				} else if (mode==1){
					trials.add(i, new Trial(getInterval(g.interval), g.bad.soundFile,
							g.good.throbRate, 1, (rand.nextInt(2) == 1),
							Species.bad));
				}
				i++;
				//good incong
				if (mode==0){
					trials.add(i, new Trial(getInterval(g.interval), g.bad.soundFile,
							g.good.throbRate, 1, (rand.nextInt(2) == 1),
							Species.good));
					} else if (mode==1){
						trials.add(i, new Trial(getInterval(g.interval), g.good.soundFile,
								g.bad.throbRate, 1, (rand.nextInt(2) == 1),
								Species.bad));
					}

				i++;
				//good silent
				if (mode==0){
					trials.add(i, new Trial(getInterval(g.interval), g.silentResponseSound,
							g.good.throbRate, 2, (rand.nextInt(2) == 1),
							Species.good));
					} else if (mode==1){
						trials.add(i, new Trial(getInterval(g.interval), g.good.soundFile,
								0, 2, (rand.nextInt(2) == 1),
								Species.good));
					}
				i++;
			}
			// shuffle

			Collections.shuffle(trials);
			for (int j = 0; j < trials.size(); j++) {
				trials.get(j).trial = j + 1;

				System.out.print(trials.get(j).toScriptString());
				scriptFile.write(trials.get(j).toScriptString());

			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	// chooses randomly inbetween min fish release and max
	public Long getInterval( int[] interval) {
		int index = rand.nextInt(3);
		
		long m = interval[index] * 100;
		return m;
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
