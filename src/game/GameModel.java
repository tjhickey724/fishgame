package game;

import java.util.*;
import java.io.*;

/**
 * This is the model for the game. It represents the entire state of the game at
 * the present moment. In this version, the player is presented with two kinds
 * of fish (good and bad) that come from sides (left or right) and have certain
 * visual and auditory cues. The player tries to identify the fish using the
 * left or right hand pressing the appropriate key.
 * 
 * Keypresses are handled in the GameView class which creates this GameModel The
 * system logs each keypress storing info about the press and the reaction time.
 * If no fish was present it was a miscue and we store the "reaction time" as
 * the time since the last fish appeared The fish can be generated either by
 * reading data from a file or they can be generated randomly with respect to
 * some specification.
 * 
 * @author tim
 * 
 */
public class GameModel {

	/**
	 * The gameSpec is a set of property/value pairs that describes everything
	 * about the current game aesthetics and mechanics.
	 */
	public GameSpec gameSpec, lastGameSpec;

	// this class is too complex, we need to clean it up and refactor!!
	// REFACTOR -- do we need all three of these?
	public double width;
	public double height;
	public double size;

	private long lastLogEventTimeNano1 = 0;
	// time per trial is represented as tenths of seconds
	public double timePerTrial = 20;
	// time remaining is represented as a percentage
	public double timeRemaining = 100;
	// total actor time is the
	public double totalActorTime = 0;
	public double currentActorTime = 0;
	public double previousActorTime = 0;

	public int health = 10;
	public int wealth = 0;

	// currently we only ever have one actor at a time ...
	private List<GameActor> actors = new ArrayList<GameActor>();

	// we need this when spawning fish ...
	protected Random rand = new Random();

	// this is set to true when the session is over ...
	// we need to add a timer to end the session, or have the
	// experimenter end the session ...
	private boolean gameOver = false;
	
	// next interfish interval time in milliseconds
	long nextFishInterval=0; 

	// this checks if the game is over.
	public boolean isGameOver() {
		return gameOver;
	}

	// this sets gameOver to true or false
	public void setGameOver(boolean gameOver) {
		this.gameOver = gameOver;
	}

	// do we really want to pause ... and does this really pause??
	private boolean paused = true;

	/**
	 * @return the paused
	 */
	public boolean isPaused() {
		return paused;
	}

	/**
	 * @param paused
	 *            the paused to set
	 */
	public void setPaused(boolean paused) {
		this.paused = paused;
	}

	// we should be more clear about these ...
	private long startTime = System.nanoTime();

	private long nextFishTime = 0;

	private long nextEventTime = 0;
	private long lastEventTime = 0;

	private GameActor nextFish = null;
	private long gameStart = startTime;
	

	public int score;

	// this is true if we are reading from a script
	private String inputScriptFileName;

	public void setInputScript(String fileName) {
		this.inputScriptFileName = fileName;
	}

	// this is a scanner used to read the fish creation info
	public Scanner scan; // = new Scanner(typescript);

	// this is where the log will be written...
	public BufferedWriter logfile;

	public boolean stereo = true;

	/** current fish being processed **/
	private int fishNum;

	/**
	 * @return the fishNum
	 */
	public int getFishNum() {
		return fishNum;
	}

	/**
	 * @param fishNum
	 *            the fishNum to set
	 */
	public void setFishNum(int fishNum) {
		this.fishNum = fishNum;
	}

	/** these variables record good/bad hits */
	private int hits, misses, noKeyPress;

	public int pressWithNoFish;

	/**
	 * @return the hits
	 */
	public int getHits() {
		return hits;
	}

	/**
	 * @param hits
	 *            the hits to set
	 */
	public void setHits(int hits) {
		this.hits = hits;
	}

	/**
	 * @return the misses
	 */
	public int getMisses() {
		return misses;
	}

	/**
	 * @param misses
	 *            the misses to set
	 */
	public void setMisses(int misses) {
		this.misses = misses;
	}

	/**
	 * @return the noKeyPress
	 */
	public int getNoKeyPress() {
		return noKeyPress;
	}

	/**
	 * @param noKeyPress
	 *            the noKeyPress to set
	 */
	public void setNoKeyPress(int noKeyPress) {
		this.noKeyPress = noKeyPress;
	}

	public GameModel(double size, int numActors, GameSpec gameSpec) {
		this.width = size;
		this.height = size;
		this.size = size;
		this.gameSpec = gameSpec;

		this.setGameOver(false);

	}

	private void getLogFile() {
		if (this.logfile == null) {
			// open the logfile
			long now = System.currentTimeMillis();
			String logname = "logs/log" + now + ".txt";
			try {
				this.logfile = new BufferedWriter(new FileWriter(new File(
						logname)));
			} catch (IOException e) {
				System.out.println("Problems opening logfile:" + logname + ": "
						+ e);
				e.printStackTrace();
			}
		}
	}

	// private long lastEventTime = System.nanoTime();

	/**
	 * this reads the next line in the Script file property/value lines cause
	 * the system to update the GameSpec fish launches read the interval and use
	 * it to compute nextFishTime and read the species and side and store it in
	 * this.nextFish
	 * 
	 * We should read this right after launching a fish, so we can know when its
	 * time to launch the next fish!
	 * 
	 * @return
	 */

	public void updateNextFishTime(long now) {

		// initialize the scanner if its the first time we're reading a line
		if (scan == null) {
			try {
				scan = new Scanner(new File(this.inputScriptFileName));
			} catch (FileNotFoundException e) {
				System.out.println("Error in reading inputScriptFile:"
						+ this.inputScriptFileName + " " + e);
				e.printStackTrace();
				this.stop();
				return;
			}
		}
		if (!scan.hasNext()) {
			this.setGameOver(true);

			this.nextFishTime =  this.nextFishTime + 10 * 1000000000L;
			return;

		}
		long interval = -1;
		try {
			interval = scan.nextLong();
		} catch (Exception e) {
			System.out.println("error with " + scan
					+ " scanning for the first long on a line" + e);
			e.printStackTrace();
		}
		// process all the 0 interval commands (which set game properties)

		while (interval == -1) {

			String prop = scan.next();
			String value = scan.next();
			scan.nextLine(); // skip over the rest of the line
			writeToLog("0\t" + prop + "\t" + value);
			if (prop.equals("gameover")) {
				this.stop();
				this.setGameOver(true);
				this.nextFishTime = System.nanoTime() + 10*1000000000L;
				return;
			}
			// System.out.println("interval="+interval+" prop="+prop+" value="+value);
			interval = scan.nextLong();

			gameSpec.update(prop, value);
		}

		// calculate the next FishTime and the basic characteristics of the
		// nextFish (species and side)
		if (interval==0){
			setGameOver(true);
			return;
		}
		nextFishTime = interval *1000000 + now;
		String sound = scan.next();
		int visualhz = scan.nextInt();
		int congruent = scan.nextInt();
		int trialnum = scan.nextInt();
		String fromLeft = scan.next();

		String species = scan.next();

		scan.nextLine(); // skip over the rest of the line

		// create the next Fish to be launched
		GameActor a = new GameActor(gameSpec.avmode);
		//System.out.println("launching new fish: "+a+" interval:"+interval+" nFT:"+ (nextFishTime-System.nanoTime())/1000000.0+" trial:"+trialnum);

		//a.avmode = this.gameSpec.avmode;
		a.fromLeft = fromLeft.equals("left");
		a.setCongruent(congruent);
		a.setTrial(trialnum);
		a.species = (species.equals("good")) ? Species.good : Species.bad;
		this.nextFish = a;
		//System.out.println("nextFishTime - gameStart="+(nextFishTime-this.gameStart)/1000000000.0);
		this.nextFishTime = nextFishTime;

	}

	public void writeToLog(GameActor f) {

		String logLine = "launch\t" + f.species + "\t" + f.congruent
				+ "\t" + f.trial + "\t" + (f.fromLeft?"left":"right");

		writeToLog(logLine);
	}

	/**
	 * This writes a string to the log file and prefixes it with the number of
	 * milliseconds since the beginning of the session.
	 * 
	 * @param s
	 *            the string to be written to the log file
	 */
	public void writeToLog(String s) {
		try {
			long theTime = (System.nanoTime() - this.gameStart);

			long theInterval = theTime - lastLogEventTimeNano;
			lastLogEventTimeNano = theTime;

			int theSeconds = (int) Math.round(theInterval / 1000000.0);
			String logLine = theSeconds + GameEvent.sep + theTime / 1000000
					+ " " + s + "\n";
			getLogFile(); // make sure the logfile is open!
			this.logfile.write(logLine);
			System.out.println("log:" + logLine);
		} catch (Exception e) {
			System.out.println("Error writing to log " + e);
		}
	}

	public void writeToLog(GameEvent e) {
		writeToLog(e.toString());
	}

	private long lastLogEventTimeNano = 0;

	public boolean flash;

	public long indicatorUpdate;

	public boolean soundflash;
	
	public long soundIndicatorUpdate;

	public void pause() {

		this.nextFishTime = Long.MAX_VALUE;
		setPaused(true);
		this.writeToLog("PAUSE");
	}

	public void restart() {

		this.nextFishTime = System.nanoTime() + 2 * 1000000000L;
		setPaused(false);
		this.writeToLog("RESTART");
	}

	// spawn an actor randomly
	/**
	 * randomly spawns a new fish,based on the script and resets the time for
	 * the next fish to be spawned...
	 */
	public void spawnFish() {
		if (this.isGameOver())
			return;
		
		Side side = (this.nextFish.fromLeft) ? Side.left : Side.right;
		Species s = this.nextFish.species;
		// System.out.println("spawning "+s+" "+side);

		// pick starting location and velocity
		double y = this.height / 2;

		double x = (side == Side.left) ? 1 : this.width - 1;

		// then make an actor with that position
		GameActor a = new GameActor(x, y, true, s, gameSpec.stereo,
				gameSpec.good.soundFile, gameSpec.bad.soundFile,gameSpec.avmode,nextFish.congruent);
		
		
		// and fill in all the needed fields...
		// we don't need both fromLeft and origin .... eliminate fromLeft...
		a.fromLeft = (side == Side.left);
		a.origin = (side == Side.left) ? 0 : 1; // we'll convert origin to Side
												// later

		a.setCongruent(nextFish.congruent);
		a.setTrial(nextFish.trial);
		// make sure it is moving inward if it comes from the right
		if (!a.fromLeft)
			a.vy = -a.vy;
		// a.radius=4;
		// start playing the music for the fish
		if (a.fromLeft)
			a.ct = a.ctL;
		else
			a.ct = a.ctR;
		//if fish is not silent play sound
		if (a.congruent != 2 && gameSpec.avmode != 1) {
			a.ct.loop();
			soundflash=true;
			soundIndicatorUpdate=System.nanoTime()+50000000l;
		} else if (gameSpec.avmode == 1){
			a.ct.loop();
			soundflash=true;
			soundIndicatorUpdate=System.nanoTime()+50000000l;
		}
		a.vx = (side == Side.left) ? 1 : -1;

		// add the fish to the list of actors...
		this.actors.add(a);
		//send a flash to the indicator
		flash=true;
		indicatorUpdate=System.nanoTime()+50000000l;
		writeToLog(a); // indicate that a was spawned
	}

	public void start() {
		this.setPaused(false);
		this.setGameOver(false);
		this.nextFishTime = System.nanoTime();
		this.gameStart = nextFishTime;
		GameActor.GAME_START = this.gameStart;
		updateNextFishTime(this.gameStart);

	}

	public void stop() {
		this.setPaused(true);
		this.setGameOver(true);

		/*
		 * java.util.Iterator<GameActor> iter =this.actors.iterator(); while
		 * (iter.hasNext()){ GameActor a = (GameActor) iter.next(); a.ct.stop();
		 * }
		 */
		try {
			if (actors.size() > 0) {
				GameActor a = actors.get(0);
				if(a.congruent != 2)
					a.ct.stop();
			}

		} catch (Exception e) {
			System.out.println("Error while stopping: " + e);
		}
		this.actors.clear();
		try {
			if (logfile != null)
				logfile.close();
			logfile = null;
			System.out.println("closing log/script files");
		} catch (Exception e) {
			System.out.println("Problem closing logfile");
		}

	}

	/**
	 * if an actor moves off the board, in the x (or y) direction, it is bounced
	 * back into the board and its velocity in the offending direction is
	 * reversed
	 * 
	 * @param a
	 */
	public void keepOnBoard(GameActor a) {
		if (a.x < 0) {
			a.x = -a.x;
			a.vx = -a.vx;
		} else if (a.x > width) {
			a.x = width - (a.x - width);
			a.vx = -a.vx;
		}
		if (a.y < 0) {
			a.y = -a.y;
			a.vy = -a.vy;
		} else if (a.y > height) {
			a.y = height - (a.y - height);
			a.vy = -a.vy;
		}
	}

	/**
	 * get the number of fish on the board (currently either 0 or 1)
	 * 
	 * @return number of fish on screen
	 */
	public int getNumFish() {
		return this.actors.size();
	}

	/**
	 * remove the lastFish from the stream turn off its clip and make it
	 * inactive
	 * 
	 * @return
	 */
	public GameActor removeLastFish() {
		if (this.actors.size() > 0) {
			GameActor lastFish = this.actors.get(0);
			lastFish.ct.stop();  // fixed bug 10/3/2013
			lastFish.active = false;
			this.actors.clear();
			return lastFish;
		} else {
			System.out.println("trying to get last fish with an empty list!!!");
			return null;
		}
	}

	public List<GameActor> getActorList() {
		return new ArrayList<GameActor>(this.actors);
	}

	/**
	 * update moves all actors one step update will check if the difference
	 * between the lastUpdate and the current time is greater than the sRate
	 * plus a random number from 1 to 4, and spawn a fish if so.
	 */
	public void update() { 
		long now = System.nanoTime(); 
		
		if (isPaused() || isGameOver())
			return;


		// these are for the GUI
		totalActorTime = (currentActorTime) / 1000000;
		timeRemaining = 100 - (totalActorTime / timePerTrial);

		
		if ((now > this.nextFishTime) && (actors.size()==0)) { // this means we need to launch a fish!!
			currentActorTime = 0;		
			spawnFish();
		}
		
		/*
		 *  Finally, we update  the fish 
		 */

		try {
			if (actors.size() > 0) {
				GameActor a = (GameActor) actors.get(0);
				a.update();
				keepOnBoard(a);

				if (!a.active) { // this is where we remove the fish if the user didn't press a key and the lifespan has been reached...
					a.ct.stop();
					previousActorTime += a.lifeSpan;
					currentActorTime = 0;
					this.setNoKeyPress(this.getNoKeyPress() + 1);
					GameEvent missedFishEvent = new GameEvent(a);
					updateNextFishTime(missedFishEvent.when);
					this.writeToLog(missedFishEvent);
					this.actors.clear();
				} else {
					this.currentActorTime = a.lifeSpan;
				}

			}
		} catch (Exception e) {
			System.out.println("Exception on update: " + e);
		}

	}

	public int interpolateSize(double min, double max, long birth, long now,
			double freq) {
		double t = ((now - birth) / 1000000000.0) * freq;
		double y = 1 - 0.5 * (Math.sin(Math.PI * 2 * t) + 1);
		double s = min * y + max * (1 - y);
		int size = (int) Math.round(s);
		return size;
	}

	public int getVisualHZ(FishSpec fs) {
		return fs.throbRate;
	}

}
