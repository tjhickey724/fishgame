package game;

import java.util.*;
import java.awt.event.KeyEvent;
import java.io.*;

/**
 * This is the model for the game. It represents the entire state of the game at
 * each moment. In this version, the player is presented with two kinds
 * of fish (good and bad) that come from sides (left or right) and have certain
 * visual and auditory cues. The player tries to identify the fish using the
 * left or right hand pressing the appropriate key.
 * 
 * Keypresses are handled in the GameView class which creates this GameModel.
 * The system logs each fish generation and keypress 
 * storing info about the press and the reaction time.
 * If no fish was present it was a miscue and we store the "reaction time" as
 * the time since the last fish appeared The fish can be generated either by
 * reading data from a file or they can be generated randomly with respect to
 * some specification.
 * 
 * An experiment consists of a sequence of trials, all of which have the same length,
 * which is set in the script.
 * In each trial a fish appears after a delay (also specified in the script and written to the logfile)
 * The user presses a key (or not) to indicate which kind of fish appeared and that key press is logged
 * The fish starts out as active and becomes inactive when a key is pressed (even a wrong key).
 * It also become inactive if it reaches its lifetime without a key press.
 * 
 * 
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
	
	/*
	 * a random number generator used to give the fish a random type of motion
	 */
	private java.util.Random rand = new java.util.Random();

	// these variables store the width and height of the game screen
	// in the current version the width and height have to be equal to the variable size
	// which is set in the GameModel constructor
	// but we use these in case we want to have a rectangular space in the future.
	private double modelWidth;
	private double modelHeight;
	
	/**
	 * the size of the square game space in model units
	 * when the model is drawn to the view, this gamespace is stretched to fit the view
	 * so the size is independent of pixels. Usually we call it with size=100
	 */
	private final double SIZE = 100;
	
	public double getSize() {
		return SIZE;
	}
	
	
	// this is the number of trials that have been started so far
	// this is used to calculate when the next trial begins
	private int numTrials = 0;


	

	/*
	 * this is the current fish on the screen if there is one
	 * it is null if there is no fish on the screen
	 * Be careful!!
	 */
    private Fish currentFish;


	// this is set to true when the session is over ...

	private boolean gameOver = false;
	
	/**
	 *  this checks if the game is over.
	 *  and it is used in the GameLoop to end the GameLoop
	 * @return true if the Game is over
	 */
	public boolean isGameOver() {
		return gameOver;
	}

	
	/**
	 *  this is set to true when the fMRI sends an "=" sign to the game the first time
	 *  and this starts reading the script and generating the fish
	 *  **/
	public boolean started = false;
	

	// do we really want to pause ... and does this really pause??
	private boolean paused = true;

	/**
	 * @return the paused
	 * this is used in the GameLoop
	 */
	public boolean isPaused() {
		return paused;
	}

	


	// this variables stores the time when the next fish should be spawned
	// the time is in nanoseconds and it is set when the game starts and 
	// after each fish is spawned the nextFishTime is recalculated
	// this requires reading the data from the script to find out what kind of
	// fish to spawn ..
	private long nextFishTime = 0;

	// this variable stores the information that we read from the script file 
	// in the updateNextFish method about the features of the next fish to generate, 
	// including the time delay, ...
	// it is used by spawnfish to generate the fish....
	//
	private Fish nextFish = null;
	
	
	// this stores the instant when the "=" is received from the fMRI machines
	// and the game starts. All times are relative to gameStart
	private long gameStart;


	// this is the name of the file that holds the input script we are using
	private String inputScriptFileName;

	/**
	 * set the name of the script file used by the model.
	 * @param fileName
	 */
	public void setInputScript(String fileName) {
		this.inputScriptFileName = fileName;
	}

	
	// this is a scanner used to read the script file
	public Scanner scan; // = new Scanner(typescript);

	// this is where the log is written
	private BufferedWriter logfile;



	/**
	 * create a GameModel with the specified parameters
	 * @param gameSpec
	 */
	public GameModel(GameSpec gameSpec) {
		this.modelWidth = this.SIZE;
		this.modelHeight = this.SIZE;
		this.gameSpec = gameSpec;
		this.gameOver = false;
	}


	/*
	 * this is used in the writeToLog method to find the interval since the
	 * last log event in milliseconds, its good for debugging....
	 */
	private long lastLogEventTimeNano = 0;

	
	/*
	 * Code for handling the Pause/Resume features
	 */
	private long pauseStart = 0;

	public void pause() {
		this.pauseStart = System.nanoTime();
		//this.nextFishTime = Long.MAX_VALUE;
		this.paused = true;
		this.writeToLog("PAUSE");
	}

	private long pauseRestart = 0;
	public void restart() {
		this.pauseRestart = System.nanoTime();
		long pauseDuration = pauseRestart-pauseStart;
		this.gameStart += pauseDuration;
		this.nextFishTime += pauseDuration;
		this.paused = false;
		this.writeToLog("RESTART");
	}

	
	/**
	 * this is called when the user presses the Start button
	 * It starts the background rolling but doesn't start generating the fish
	 * That happens after the "=" is received and the start_fMRI method is called
	 */
	public void start_gameboard() {
		this.paused = false;
		this.gameOver = false;	
		this.started = false;
	}
	
	/**
	 * this is called when the fMRI "=" is received and the experiment should start
	 * this starts reading the script file and generating fish..
	 * It also stores the current time in nanoseconds to be used in all data...
	 */
	public void start_fMRI(){
		Fish.GAME_START = System.nanoTime();
		this.nextFishTime = Fish.GAME_START;
		this.gameStart = Fish.GAME_START;
		this.started = true;
		createNextFish();


	}
	

	/**
	 * this shuts down the gameboard 
	 * it stops any fish sound that maybe playing and the background sound
	 * and causes the GameLoop thread to stop also
	 * it also closes the logfile
	 */
	public void stop() {
		this.paused=true;
		this.gameOver = true;

		try {
			if (currentFish != null) {
				Fish a = currentFish; 
				if(a.congruent != 2)
					a.ct.stop();
			}

		} catch (Exception e) {
			System.out.println("Error while stopping: " + e);
		}
		currentFish = null;
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
	 * if a fish moves off the board, in the x (or y) direction, it is bounced
	 * back into the board and its velocity in the offending direction is
	 * reversed
	 * 
	 * @param a
	 */
	public void keepOnBoard(Fish a) {
		if (a.x < 0) {
			a.x = -a.x;
			a.vx = -a.vx;
		} else if (a.x > modelWidth) {
			a.x = modelWidth - (a.x - modelWidth);
			a.vx = -a.vx;
		}
		if (a.y < 0) {
			a.y = -a.y;
			a.vy = -a.vy;
		} else if (a.y > modelHeight) {
			a.y = modelHeight - (a.y - modelHeight);
			a.vy = -a.vy;
		}
	}

	/**
	 * get the number of fish on the board (currently either 0 or 1)
	 * 
	 * @return number of fish on screen
	 */
	public int getNumFish() {
		if (currentFish==null)
			return 0;
		else
			return 1;
	}

	/**
	 * remove the lastFish from the stream turn off its clip and make it
	 * inactive
	 * 
	 * @return
	 */
	public Fish removeLastFish() {
		if (currentFish != null) { 
			Fish lastFish = currentFish;
			if(lastFish.congruent != 2)
				lastFish.ct.stop();
			lastFish.active = false;
			currentFish = null;
			return lastFish;
		} else {
			System.out.println("trying to get last fish with an empty list!!!");
			return null;
		}
	}

	
	public Fish getCurrentFish(){
		return currentFish;
	}
	
	/**
	 * handle a keypress which is either an "=" or a "p" or "l"
	 * it may have to start playing a sound also ...
	 * It is called from GameView in the EventQueue thread
	 * that's why this needs to be synchronized
	 * @param e
	 * @param goodclip
	 * @param badclip
	 */
	public synchronized void handleKeyPress(KeyEvent e,AudioClip goodclip, AudioClip badclip) {
		if (isGameOver())
			return;


		if ((e.getKeyChar()=='=') && (!started)){
			start_fMRI();
			return;
		}

		// first check to see if they pressed
		// when there are no fish!!
		if (getNumFish() == 0) {
			writeToLog(new GameEvent(e.getKeyChar()));
			badclip.play();
			return;
		}
		// otherwise, see if we've responded already and if so, ignore the keypress???
		Fish lastFish = this.getCurrentFish(); 
		if (lastFish.responded==true){
			return;
		}
		lastFish.responded=true;
		GameEvent ge = new GameEvent(e.getKeyChar(), lastFish);

		// get the response time and write it to the log
		long keyPressTime = ge.when;
		long responseTime = keyPressTime - lastFish.birthTime;

		String log = e.getKeyChar() + " " + responseTime / 1000000.0
				+ " " + ge.correctResponse + " " + lastFish;

		System.out.println(log);

		writeToLog(ge);

		// play the appropriate sound and modify the score

		if (ge.correctResponse) {

			goodclip.play();
			setHits(getHits() + 1);
		} else {
			badclip.play();
			setMisses(getMisses() + 1);
		}
		
		return;
	}
	

	/**
	 * update moves the fish one step 
	 * it is called in the GameLoop thread
	 */
	public synchronized void update() { // throws UnsupportedAudioFileException, IOException,
							// LineUnavailableException {
		if (isPaused() || isGameOver() || !started)
			return;

		long now = System.nanoTime();
		

		if (currentFish == null) {
			if (now > this.nextFishTime) {
				if (this.nextFish==null){
					// no more fish, so the game is over!
					this.gameOver = true;
					return;
				}
				else {
				    spawnFish(now);
				    createNextFish();
				}
			}
		} else  {
			// first we update the fishes position and state
				Fish a = currentFish;
				a.update();
				keepOnBoard(a);
				if (!a.active)  killFish(a);
		}
	}


	private void killFish(Fish a) {
		a.ct.stop();

		currentFish = null;
		
		if(!a.responded) {
			System.out.println("nonresponse:"+a);
			this.writeToLog(new GameEvent(a));
			this.setNoKeyPress(this.getNoKeyPress() + 1);
		} else {
			System.out.println("already responded:"+a);
			// it has been responded to earlier ...
		}
	}
	

	
	/**
	 * this reads the next line in the Script file 
	 *    property/value lines cause the system to update the GameSpec 
	 *    fish launches read the interval and use
	 *    it to compute nextFishTime and read the species and side and store it in
	 *    this.nextFish
	 * 
	 * We should read this right after launching a fish, so we can know when its
	 * time to launch the next fish!
	 * 
	 * @return
	 */

	private void createNextFish() {

		// initialize the scanner if its the first time we're reading a line
		if (scan == null) createScanner();

		if (!scan.hasNext()) {
			// this should never happen as the last script instruction game
			// ends the game
			this.gameOver = true;
			this.nextFishTime = Long.MAX_VALUE;
		}
		
		
		long interval = readNextInterval();
		// process all the 0 interval commands (which set game properties)

		while (interval == -1) {
			interval = updateGameSpec();
		}
		
		if (interval==0){
			this.nextFish = null;
			this.nextFishTime = System.nanoTime() + gameSpec.trialLength*100L*1000L*1000L;
		} else {
			readAndStoreNextFishInfo(interval);
		}
	}


	/*
	 * This is where we read in the rest of the line from the script describing a fish
	 * and we use that information to 
	 * <ul><li> create the next fish with all its characteristics (visual/audio specs)
	 * </li><li> calculate this.nextFishTime that specifies when the fish should be launched
	 * </li></ul>
	 */
	private void readAndStoreNextFishInfo(long interval) {
		
		// first we read in the data on the line and store it in local variables for procession
		String sound = scan.next();
		int visualhz = scan.nextInt();		
		int congruent = scan.nextInt();
		int trialnum = scan.nextInt();
		int block = scan.nextInt();
		boolean fromLeft = (scan.next().equals("left"));
		String species = scan.next();
		scan.nextLine(); // skip over the rest of the line
		
		
		// next we calculate the time the next fish should be launched
		long beginningOfNextTrial = numTrials * gameSpec.trialLength*100000000L + this.gameStart;
		long delayBeforeFishAppears = interval*1000000L;
		this.nextFishTime = beginningOfNextTrial + delayBeforeFishAppears;
		
		// we have completed the calculation for this trial, so we update the trial counter
		numTrials++;
		
		
		
		/*
		 * Now we create the next fish. We can fill in all of the fields except for the birthTime
		 * which will be set in the spawnFish method. We store it in this.nextFish.
		 */

		this.nextFish = new Fish();

		nextFish.fromLeft = fromLeft;
		nextFish.congruent=congruent;
		nextFish.trial = trialnum;
		nextFish.block = block;
		nextFish.species = (species.equals("good")) ? Species.good : Species.bad;
		

		// pick starting location and velocity
		nextFish.y = this.modelHeight / 2;
		nextFish.x = this.nextFish.fromLeft ? 1 : this.modelWidth - 1;
		
	
		// initialize the fish velocity
		nextFish.vx = nextFish.speed * (this.rand.nextDouble()) * (this.nextFish.fromLeft ? 1 : -1);
		nextFish.vy = nextFish.speed * (this.rand.nextDouble() - 0.5);
		
		// initialize so that it is active
		nextFish.active = true;

		// set the sound files
		if (nextFish.species == Species.good)
			nextFish.ct = new AudioClip(gameSpec.good.soundFile+"/fish.wav");
		else
			nextFish.ct = new AudioClip(gameSpec.bad.soundFile+"/fish.wav");
	}


	/**
	 * read in the property/value pair from the script and use it to update the gamespec
	 * then read in the next interval and return it....
	 * @return
	 */
	private long updateGameSpec() {
		long interval;
		String prop = scan.next();
		String value = scan.next();
		scan.nextLine(); // skip over the rest of the line
		writeToLog("0\t" + prop + "\t" + value);
		if (prop.equals("gameover")) {
			this.stop();
			this.gameOver = true;
			this.nextFishTime += 1000000000000L;
		}
		// System.out.println("interval="+interval+" prop="+prop+" value="+value);
		interval = scan.nextLong();

		gameSpec.update(prop, value);
		return interval;
	}


	private long readNextInterval() {
		long interval = -1L;
		try {
			interval = scan.nextLong();
		} catch (Exception e) {
			System.out.println("error with " + scan
					+ " scanning for the first long on a line" + e);
			e.printStackTrace();
		}
		return interval;
	}


	private void createScanner() {
		try {
			scan = new Scanner(new File(this.inputScriptFileName));
		} catch (FileNotFoundException e) {
			System.out.println("Error in reading inputScriptFile:"
					+ this.inputScriptFileName + " " + e);
			e.printStackTrace();
			this.stop();
		}
	}
	
	// spawn an actor randomly
		/**
		 * randomly spawns a new fish,based on the script and resets the time for
		 * the next fish to be spawned...
		 */
		public void spawnFish(long now) {

			nextFish.birthTime = now;
			nextFish.lastUpdate = now;
			currentFish = nextFish;
			if (currentFish.congruent != 2)
				currentFish.ct.loop();
			//nextFish = null;
			writeToLog(currentFish); 
		}



	/*
	 * GUI support --
	 * these methods are used to store data that appears in the GUI,
	 * but are not used to control the model at all ...
	 * 
	 */
	

	/** current fish being processed 
	 *  this might be the same as trialnum ....
	 **/
	private int fishNum;

	/**
	 * this is used in the GUI header 
	 * @return the fishNum
	 */
	public int getFishNum() {
		return fishNum;
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
	
	/*
	 * Methods to handle logging 
	 */
	
	/*
	 * Create a logfile for this session
	 */
	private void createLogfile() {
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

	// private long lastEventTime = System.nanoTime();


	/**
	 * write a logline corresponding to a fish being launched
	 * write out all of the fish properties too
	 * @param f
	 */
	public void writeToLog(Fish f) {

		String logLine = "launch\t" + f.species + "\t" + f.congruent
				+ "\t" + f.trial + "\t" + f.block + "\t" + (f.fromLeft? "left":"right");

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
			

			if (this.logfile == null) createLogfile();
			this.logfile.write(logLine);
			this.logfile.flush();
			System.out.println("log:" + logLine);
		} catch (Exception e) {
			System.out.println("Error writing to log " + e);
		}
	}

	/**
	 * this writes a GameEvent (e.g. key press, missed fish, etc.) see doc for more to the logfile
	 * @param e
	 */
	public void writeToLog(GameEvent e) {
		writeToLog(e.toString());
	}
	
	
	
}
