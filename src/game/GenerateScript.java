package game;

import java.io.File;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;


/**
 * This class will generate a script based on specifications from the
 * experimenter. For now we just look at min/max spawning intervals.
 * @author tim
 *
 */
public class GenerateScript {
	
	private BufferedWriter scriptFile;
	private Random rand = new Random();
	
	public GenerateScript() {
		this("scripts/script"+System.currentTimeMillis()+".txt");
	}
	public GenerateScript(String scriptname){
		try {
			this.scriptFile = new BufferedWriter(new FileWriter(new File(scriptname+".txt")));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Error Opening Script File");
			e.printStackTrace();
		}
	}
	
	/**
	 * the user can specify what to use for the separator for script values
	 */
	public String sep = "\t";
	
	/**
	 * this generates N script events where the inter-event interval is
	 * between min/10 and max/10 seconds and is expressed in milliseconds
	 * @param N
	 */
	public void generate(GameSpec g, int N){
		int nextInterval = 0;
		int minFish = g.minFishRelease;
		int maxFish = g.maxFishRelease;
		for(int i=0;i<N;i++){
			nextInterval = 1;
			int r = rand.nextInt((maxFish-minFish)*100);
			long m = minFish*100;
			long theInterval =  m+ r;	
			boolean goodFish = rand.nextInt(2)==1;
			boolean fromLeft = rand.nextInt(2)==1;
			String scriptLine =
					""+theInterval+
					sep+(goodFish?"good":"bad ")+
					sep+(fromLeft?"left ":"right")+
					sep+(i+1)+
					"\n";
			System.out.print(scriptLine);
			try {
				scriptFile.write(scriptLine);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Error writing to script file:"+e);
				e.printStackTrace();
			}
		}
		
	}
	
	public void close(){
		try {
			scriptFile.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Problems closing scriptfile:"+e);
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args){
		GameSpec g = new GameSpec();
		GenerateScript gs = new GenerateScript();
		gs.generate(g,100);
		gs.close();
	}

}
