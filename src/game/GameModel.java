package game;
import java.util.*;
import java.io.*;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * This is the model for the game. It represents the entire state
 * of the game at the present moment. 
 * In this version, the player is presented with two kinds of fish
 * (good and bad) that come from sides (left or right) and have 
 * certain visual and auditory cues. The player tries to identify the
 * fish using the left or right hand pressing the appropriate key.
 * The system logs each keypress storing info about the press 
 * and the reaction time. If no fish was present it was a miscue
 * and we store the "reaction time" as the time since the last fish appeared
 * The fish can be generated either by reading data from a file or
 * they can be generated randomly with respect to some specification. 
 * @author tim
 *
 */
public class GameModel {
	// this class is too complex, we need to clean it up and refactor!!
	// REFACTOR -- do we need all three of these?
	public double width;
	public double height;
	public double size;
	
	/** flag whether to create a script while generating */
	public boolean createScript = true;
	
	// currently we only ever have one actor at a time ...
	public List<GameActor> actors = new ArrayList<GameActor>();
	
	// we need this when spawning fish ...
	protected Random rand = new Random();
	
	// this is a placeholder, we don't have an avatar yet ..
	//public GameActor avatar;
	
    // this is set to true when the session is over ...
	// we need to add a timer to end the session, or have the 
	// experimenter end the session ...
	public boolean gameOver = false;
	
	// do we really want to pause ... and does this really pause??
	public boolean paused = true;
	
	// we only have one fish at a time anyway ...
	public int numActive;
	
	// we should be more clear about these ...
	private long startTime=System.nanoTime();
	private long lastUpdate=startTime;
	private long nextFishTime=0;
	private long gameStart = startTime;
	
	// mRate is the minimum time between fish, in tenths of a second
	public int mRate = 40;
	// sRate is the maximum time between fish
	public int sRate = 90;
	
	public int score;
	// this is a temporary variable, we'll be reading from a file eventually
	public String typescript = "lb 100 lb 100 rg 100 rb 100 lb 100"; 
	
	// this is true if we are reading from a script
	public boolean scripted=true;
	
	// this is a scanner used to read the fish creation info
	public Scanner scan = new Scanner(typescript);
	
	// this is where the log will be written...
	public BufferedWriter logfile;
	public BufferedWriter scriptfile;

    // we're not using this anymore 
	public String log = "";
	// we should have goodherz and badherz probably ...
	public int goodvisualhz=4;
	public int badvisualhz=9;
	public int badaudiohz=9;
	public int goodaudiohz=4;
	
	// NOT USING ...
	//PrintWriter writer = new PrintWriter("logOLD.txt", "UTF-8");
	
	// HMMMM...
	public int mode = 1;
	// 0 for random, 1 for scripted
	
	
	public GameModel(double size, int numActors) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
		this.width =size;
		this.height = size;
		this.size=size;
		//this.numActors = numActors;
		//this.avatar = new GameActor(0,0);
		//avatar.species = Species.avatar;
		//this.avatar.radius=6;
		
		this.gameOver = false;
		this.nextFishTime = System.nanoTime(); // we skip the first fish!!
		this.nextFishTime = updateNextFishTime(); 
		long now = System.currentTimeMillis();
		String logname = "log"+now+".txt";
		this.logfile = new BufferedWriter(new FileWriter(new File(logname)));
		String scriptname = "script"+now+".txt";
		this.scriptfile = new BufferedWriter(new FileWriter(new File(scriptname)));
		
		
	}
	
	/**
	 * calculates the interval between the current fish and the next fish
	 * using the mRate and sRate parameters (explain more later ...)
	 * @return
	 */
	private long updateNextFishTime(){
		long r = Math.abs(rand.nextLong()) % ((sRate-mRate)*1000000000);
		long m = mRate*100000000L;
		System.out.println("nft:"+ m+","+r);
		return( this.nextFishTime+ m+ r);	
	}
	
	/**
	 * This writes a string to the log file and prefixes it with the
	 * number of milliseconds since the beginning of the session.
	 * @param s the string to be written to the log file
	 */
	public void writeToLog(String s){
		try{
			long theTime = (System.nanoTime()-this.gameStart);
			double theSeconds = (theTime/1000000.0);
			this.logfile.write(theSeconds +" "+s+"\n");
		} catch(Exception e){
			System.out.println("Error writing to log "+e);
		}
	}
	
	private void writeToScript(GameActor fish){
		long now = System.nanoTime() - this.gameStart;
		String side = (fish.fromLeft)?"left":"right";
		String scriptline = "" + now + " " +
		  fish.species + " "+ side +"\n";
        try{
        	this.scriptfile.write(scriptline);
        } catch (Exception e) {
        	System.out.println("Error write to scriptfile "+ e);
        }
	}
	
	public void writeToLog(GameEvent e){
		try{
			long theTime = (System.nanoTime()-this.gameStart);
			double theSeconds = (theTime/1000000.0);
			this.logfile.write(theSeconds+GameEvent.sep+e+"\n");
		} catch(Exception err){
			System.out.println("Error writing GameEvent to log "+err);
		}
	}


	
	/*
	 * spawns an actor from a script
	 * This needs more work so that it can run scripts generated by
	 * this program.....
	 */

	public void scriptSpawnActor(Scanner s) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
		if(s.hasNext()){
		String t=s.next();
		mRate=s.nextInt();

//			startTime=now;
		double x=0,y = height/2;
		Species spec = Species.good;
		int vx=0;
		int or=0;
		if (t.charAt(0)=='r')
		{
			x = width-1;
			vx= -1;
			or=1;
		} else if (t.charAt(0)=='l'){
			x =1;
			vx=1;
			or=0;
		}
		
		if (t.charAt(1) == 'g')
		{
			spec=Species.good;
		}
		else if (t.charAt(1)=='b'){
			spec=Species.bad;
		}
		
		GameActor a = new GameActor(x,y,true,spec);

		if (actors.size()>0){
			this.actors.get(this.actors.size()-1).active=false;
			this.actors.get(this.actors.size()-1).ct.stop();	
		}
			this.actors.add(a);
			a.ct.loop();
			a.speed = 10;
		a.radius = 4;
		a.vx=vx;
		a.vy=0;
		a.species=spec;
		a.origin=or;
		// need to add statement for audiohz
		if (spec==Species.good){
			a.colorHerz=this.goodvisualhz;
		} else if (spec==Species.bad) {
			a.colorHerz=this.badvisualhz;
		}
		
		//log+="Actor event.BirthTime:"+a.birthTime+" Type:"+a.species+" Side:"+a.origin+"\n";
		//writer.println("Actor event.BirthTime:"+a.birthTime+" Type:"+a.species+" Side:"+a.origin+"\n");
		logfile.write("Actor event.BirthTime:"+a.birthTime+" Type:"+a.species+" Side:"+a.origin+"\n");
		logfile.flush();
		}
	}

	
	
	// spawn an actor randomly
	/**
	 * randomly spawns a new fish, good or bad with equal chance,
	 * left or right with equal chance, and resets the time for the
	 * next fish to be spawned...
	 */
	public void randomSpawnActor(){
		// pick left or right		
		Side side;
		if (rand.nextInt(2)==0){
			side = Side.left;
		}else {
			side = Side.right;
		}
		
		// pick good or bad
		Species s;
		if (rand.nextInt(2)==0){
			s = Species.good;
		}else {
			s = Species.bad;
		}
		

		// pick starting location and velocity
		double y = this.height/2;
		double x = (side==Side.left)? 1 : this.width-1;
		double vy=0;
		double vx = (side==Side.left)? 1: -1;
		
		// then make an actor with that position
		GameActor a = new GameActor(x,y,true,s);
		// and fill in all the needed fields...
		// we don't need both fromLeft and origin .... eliminate fromLeft...
		a.fromLeft=(side==Side.left);
		a.origin=(side==Side.left)?0:1;  // we'll covert origin to Side later
		a.radius=4;
		// start playing the music for the fish
		a.ct.loop();
		// set the color herz (these shouldn't be raw numbers!!!
		// we should look them up from a Settings object somewhere...
		a.colorHerz = (s==Species.good)? 2: 4;
		
		// add the fish to the list of actors...
		this.actors.add(a);
		this.writeToScript(a);
	}
	
	public void start(){
		paused = false;
		this.nextFishTime = System.nanoTime();
		this.nextFishTime = updateNextFishTime(); 
	}
	
	public void stop(){
		paused = true;
		gameOver=true;
		try{
			logfile.close();
			scriptfile.close();
		}catch (Exception e){
			System.out.println("Problem closing logfile");
		}

	}
	
	/**
	 * if an actor moves off the board, in the x (or y) direction, 
	 * it is bounced back into the board and its velocity in the
	 * offending direction is reversed
	 * @param a
	 */
	public void keepOnBoard(GameActor a){
		if (a.x<0) {
			a.x = -a.x;a.vx = -a.vx;
		}else if (a.x> width){
			a.x = width - (a.x-width);
			a.vx = -a.vx;
		}
		if (a.y<0) {
			a.y = -a.y;a.vy = -a.vy;
		}else if (a.y > height){
			a.y = height - (a.y-height);
			a.vy=-a.vy;
		}
	}
	
	// here is where we take the fish off the board
	// when the user presses a key!
	// we assume there is only one fish in the actors list ..
	// this is a hack we should clean up or rename...
	public void removeFish(GameActor a){
		this.actors.clear();
	}
	/**
	 * update moves all actors one step 
	 * update will check if the difference between the lastUpdate 
	 * and the current time is greater than the sRate plus a random number 
	 * from 1 to 4, and spawn a fish if so.
	 */
	public void update() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
		if (paused || gameOver) return;
		
		// here is where we decide whether to spawn a fish
		// I'm changing this to not use an input script ...
		// but instead to randomly generate new fish
		// using a nextFishTime variable
		
		// we need to write the code to generate a script from this session
		// so we can randomly generate one script and then use it many times...
		
		long now=System.nanoTime();
		long interval =  (long) ((mRate)*100000000l);
		
		if (now > this.nextFishTime){
			/*
			long duration = now-startTime;
			startTime=now;
			long r = Math.abs(rand.nextLong()) % (sRate*100000000);
			long m = mRate*100000000L;
			this.nextFishTime = startTime+ m+ r;
			*/
			System.out.println("now="+now);
			System.out.println("nft="+this.nextFishTime);
			long tmpzz = this.nextFishTime;
			this.nextFishTime = this.updateNextFishTime();
			double theInterval = (this.nextFishTime-tmpzz)/1000000000.0;
			System.out.println("newnft="+this.nextFishTime);
			System.out.println("interval="+theInterval);
			if (this.actors.size()>0) {
				GameActor lastFish = this.actors.get(this.actors.size()-1);
				lastFish.ct.stop();
				this.writeToScript(lastFish);
				this.actors.clear();
				//System.out.println("missed fish!!");
				//this.writeToLog("missed fish!");
				this.writeToLog(new GameEvent(lastFish));
				//this.writeToLog("- "+duration/1000000.0+" false "+lastFish);
			}
			randomSpawnActor();
	
		}
		
		// here is where we update all of the fish (should only be one now!)
		for(GameActor a:this.actors){
			a.update();
			keepOnBoard(a);
		}
	}

}

