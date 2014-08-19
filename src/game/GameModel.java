package game;

import java.util.*;
import java.awt.event.KeyEvent;
import java.io.*;

/**
 * This is the model for the game. It represents the entire state of the game at
 * the present moment. In this version, the player is presented with two kinds
 * of fish (good and bad) that come from sides (left or right) and have certain
 * visual and auditory cues. The player tries to identify the fish by pressing
 * the appropriate key.
 * 
 * Keypresses are handled in the GameView class which creates this GameModel The
 * system logs each keypress storing info about the press and the reaction time.
 * If no fish was present it was a miscue and we store the "reaction time" as
 * the time since the last fish appeared The fish are generated by reading data
 * from a file created by the ScriptGenerator class.
 * 
 * 
 */

// REFACTOR: much refactoring has been completed but there is still some to do!

public class GameModel {

	/**
	 * Create a new GameModel based on the GameSpec parameter
	 * 
	 * @param gameSpec
	 */
	public GameModel(GameSpec gameSpec) {
		this.gameSpec = gameSpec;
	}

	/**
	 * The gameSpec is a set of property/value pairs that describes everything
	 * about the current game aesthetics and mechanics except for the actual
	 * sequence of fish that should be generated. So the background image and
	 * sound, the sound files for the good and bad fish, etc.
	 */
	public GameSpec gameSpec;
    public String[] minBrightness=new String[5];
    public String [] maxBrightness=new String[5];
    public String[]minSize=new String[5];
    public String[] maxSize=new String[5];
    public int[]goodEquity=new int[5];
    public int[] badEquity=new int[5];
	// here are some convenient constants for converting between nanoseconds
	// and milliseconds and seconds
	private static long million = 1000000L;
	
	private static long billion = 1000000000L;

	
	public int systimeToMillis(long t){
		return (int) ((t - this.gameStart)/million);
	}
	
	/**
	 * this is the connection to the EEG recording device
	 * It must first be initialize, synchronized, and have the recording started
	 * before events will be recorded...
	 * In this application we use the EEG.eventNS0(CODE,TIME,DURATION) method
	 * which is ignored if recording has not been turned on...
	 */
	public NetStation EEG = new NetStation(this);
	public boolean usingEEG = false;
	public boolean usingDebgEgg=false;
	public boolean firstBlankScreen = false;
	public boolean secondBlankScreen = false;
	public boolean midBlankScreen = false;
	private long blankScreenTimeout = 0L;
	private static long BLANK_SCREEN_DELAY = 3*60;  // in seconds 
	private static long DEBUG_BLANK_DELAY=3;
	
	
	
	private int fishSoFar = 0;
	
	

	/**
	 * the SIZE of the GameBoard is 100x100 in model units when it is drawn to a
	 * screen it is stretched in the x and y directions to fit the screen. The x
	 * and y positions can be thought of as percentages of the screen, however
	 * it is scaled.
	 */
	public static final double SIZE = 100;
	// these are convenience variables to make the code easier to read
	private static double WIDTH = SIZE;
	private static double HEIGHT = SIZE;

	/*
	 * methods related to the current Fish
	 */
	private Fish currentFish = null;

	public synchronized double getCurrentFishX() {
		if (currentFish != null)
			return currentFish.x;
		else
			return 0;
	}

	public synchronized double getCurrentFishY() {
		if (currentFish != null)
			return currentFish.y;
		else
			return 0;
	}

	public synchronized Fish getCurrentFish() {
		return this.currentFish;
	}

	/**
	 * get the number of fish on the board (currently either 0 or 1)
	 * 
	 * @return number of fish on screen
	 */
	public int getNumFish() {
		return (currentFish == null ? 0 : 1);
	}

	/**
	 * remove the lastFish from the stream turn off its clip and make it
	 * inactive
	 * 
	 * @return
	 */
	public void removeLastFish() {
		System.out.println("currentFish = "+currentFish);
		if (currentFish != null){ // DEBUG - FIND OUT WHY THIS HAPPENS!!!
			currentFish.ct.stop();
		}
		currentFish = null;
		long now = System.nanoTime();
		sendEEGMarker(now,"EFIS");
		flash = true;
		indicatorUpdate = System.nanoTime() + 50000000l;
		
		fishSoFar++;
		boolean timeForPause = checkForPause();
		if(!timeForPause) {
			this.createNextFish(now);
		}
	}
	

	/**
	 * Check if it's time for the mid-game pause, and if so, pause the game.
	 */
	public boolean checkForPause() {
		if(fishSoFar == gameSpec.numTrialsBeforePause) {
			midBlankScreen = true;
			return true;
		}
		return false;
	}

	/*
	 * methods related to the gameOver field which is true when the game is over
	 */
	private boolean gameOver = false;

	/**
	 * this checks if the game is over.
	 * 
	 * @return true if the game is over
	 */
	public boolean isGameOver() {
		return gameOver;
	}

	/**
	 * sets gameOver to true or false
	 * 
	 * @param gameOver
	 */
	public void setGameOver(boolean gameOver) {
		this.gameOver = gameOver;
	}

	/*
	 * methods related to the paused field
	 */

	// pause the game, in case the subject needs to go to the bathroom!
	private boolean paused = true;

	/**
	 * returns true if the game is paused
	 * 
	 * @return the paused
	 */
	public boolean isPaused() {
		return paused;
	}

	/*
	 * pauses or resumes the game
	 * 
	 * @param paused the paused to set
	 */
	private void setPaused(boolean paused) {
		this.paused = paused;
	}

	/*
	 * this is the time, in nanoseconds (relative to System.nanoTime) that the
	 * next fish should be launched. We initialize it to the maximum time so
	 * that no fish will be launched unless this variable is reset to a smaller
	 * value
	 */
	private long nextFishTime = Long.MAX_VALUE;

	/*
	 * this variable holds the information about the next fish to be released!
	 */
	private Fish nextFish = new Fish();

	/*
	 * this stores the time, in nanoseconds, at which the game part of the
	 * experiment started it is reset when the game actually does start, but we
	 * give it a default time for when the class is loaded...
	 */
	private long gameStart = System.nanoTime();

	/*
	 * this is the name of the input script file used when creating the scanner
	 * and set from the ScriptWindow
	 */
	private String inputScriptFileName;

	/**
	 * sets the input script to the specified filename
	 * 
	 * @param fileName
	 */
	public void setInputScript(String fileName) {
		this.inputScriptFileName = fileName;
	}

	/*
	 * this is a scanner used to read the input script
	 */
	private Scanner scan; // = new Scanner(typescript);

	/*
	 * this holds the logfile for the experiment
	 */
	private BufferedWriter logfile;

	/*
	 * if there is no logfile yet, it creates one with a unique name otherwise
	 * it returns the logfile. REFACTOR: Exceptions are caught in this method,
	 * but should be thrown and caught at a higher level
	 */
	private void getLogFile() {
		if (this.logfile == null) {
			// open the logfile
			File logsFolder = new File("logs");
			logsFolder.mkdirs();
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

	/*
	 * this is used so that we can calculate a field in each logfile entry which
	 * shows the time since the last log element was written
	 */
	private long lastLogEventTimeNano = 0;

	/**
	 * this starts the game by initializing the GAME_START variable and reading
	 * the script to set the properties and create the next fish and set its
	 * launch time.
	 */
	public void start() {
		this.paused = false;
		this.gameOver = false;
		this.nextFishTime = System.nanoTime();
		this.gameStart = nextFishTime;
		Fish.GAME_START = this.gameStart;
		long delay = 0;  
		long now = System.nanoTime();
		
		if (this.usingEEG || this.usingDebgEgg  ){
			writeToLog(now,"FirstBlankScreen");
			initEEG();
			if (this.usingEEG)
				delay = BLANK_SCREEN_DELAY*billion;
			else 
				delay=DEBUG_BLANK_DELAY*billion;
			this.firstBlankScreen = true;
			this.blankScreenTimeout = now + delay; // 3 minutes from now ...
			//send SRES indicating that the initial resting period has started
			sendEEGMarker(now,"SRES");
		}


		// I think we need to send a "new trial" marker to the EEG
		// and update the time in the call to createNextFish accordingly...
		//createNextFish(System.nanoTime()+delay);
		//this is now done in update so that it happens after the resting period

	}

	private void initEEG() {
		System.out.println("initEEG");


			try {
				this.EEG.connectNS();
				this.EEG.startTime = this.gameStart;
				this.EEG.synchronizeNS();
				Thread.sleep(1000L);
				this.EEG.startRecordingNS();
				
			} catch (NetStationError e) {
				e.printStackTrace();
				System.out.println("Error in NetStation initialization"+e);
				System.exit(0);
			} catch (InterruptedException e) {
				e.printStackTrace();
				System.out.println("Error in NetStation init:"+e);
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Error in Netstation init:"+e);
			}
			

	}

	/**
	 * this stops the game and closes the output log file This catches I/O
	 * errors if it can't close the logfile and prints and error message.
	 * REFACTOR: make sure the input script is closed too!
	 */
	public void stop() {
		this.scan = null;
		

		if (currentFish != null) {
			currentFish.ct.stop();
		}
		currentFish = null;


		long now = System.nanoTime();
		
		
		if (usingEEG || usingDebgEgg){
			this.secondBlankScreen = true;
		    if (usingEEG)
		    	this.blankScreenTimeout = now + BLANK_SCREEN_DELAY*billion;
		    else
		    	this.blankScreenTimeout = now + DEBUG_BLANK_DELAY*billion;
			this.writeToLog(now,"SecondBlankScreen");
		} else {
			this.paused = true;
			this.gameOver = true;
		}

		

	}

	private void closeLogfile() {
		try {
			if (logfile != null)
				logfile.close();
			logfile = null;
			System.out.println("closing log/script files");
		} catch (Exception e) {
			System.out.println("Problem closing logfile");
		}
	}
	
	

	private void stopEEG() {

		if (this.usingDebgEgg)
			return;
		
		
		
		// turn off EEG recording....

		try {
			// we are not stopping the recording
			// or disconnecting at this point
			EEG.stopRecordingNS();
			EEG.disconnectNS();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Error trying to stop EEG recording "+e);
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.out.println("Error trying to stop EEG recording "+e);
		}

	}

	private long pauseTime = 0;

	/**
	 * temporarily pause the game (incase subject has to go to the bathroom!)
	 * QUESTION: do we need to pause the EEG also and then resynchronize when
	 * resuming?
	 */
	public void pause() {
		pauseTime = System.nanoTime();
		this.nextFishTime = Long.MAX_VALUE;
		setPaused(true);
		this.writeToLog(pauseTime, "PAUSE");
	}

	/**
	 * resumes from a paus REFACTOR: make sure this handles the log correctly
	 * QUESTION: does the EEG stop, how should that effect the log timing data?
	 */
	public void resume() {
		long now = System.nanoTime();

		this.nextFishTime += (now - pauseTime);
		setPaused(false);
		this.writeToLog(now, "RESUME");
	}

	public void resumeMidGame() {
		if(midBlankScreen) {
			midBlankScreen = false;
			long now = System.nanoTime();
			this.createNextFish(now);
		}
	}
	
	
	/**
	 * if an actor moves off the board, in the x (or y) direction, it is bounced
	 * back into the board and its velocity in the offending direction is
	 * reversed. REFACTOR: move this to the update method of the fish class.
	 * 
	 * @param a
	 */
	public void keepOnBoard(Fish a) {
		double tmpY = a.y;
		if (a.x < 0) {
			a.x = 0;
			a.vx = -a.vx;
		} else if (a.x > WIDTH) {
			a.x = WIDTH;
			a.vx = -a.vx;
		}
		// here we keep the fish from dropping too low in the frame
		if (a.y < 0.2*HEIGHT) {
			a.y = 0.2*HEIGHT;
			a.vy = Math.abs(a.vy);
		// we also keep it from getting too high..
		} else if (a.y > 0.6*HEIGHT) {
			a.y = 0.6*HEIGHT;
			a.vy = -Math.abs(a.vy);
		}
		//System.out.println("@@@ "+tmpY+"-->"+a.y+"..."+a.vy);
	}

	/**
	 * This method is called from GameView when a clip needs to be handled it
	 * handles the keypress event and creates and logs a GameEvent
	 * 
	 * @param e
	 * @param goodclip
	 * @param badclip
	 */
	public synchronized boolean handleKeyPress(KeyEvent e) {

		Fish lastFish = this.getCurrentFish();


		// create a GameEvent for logging purposes
		// this also determines if it was a correct or incorrect key press
		GameEvent ge = new GameEvent(e, lastFish);

		// then we update the NextFishTime which means reading in another line
		// and
		// storing the info about that fish in this.nextFish
		// and setting this.nextFishTime
		// Note: this does not launch the fish!
        if( gameSpec.Equity && ge.correctResponse)
        {
        	if(lastFish.species.equals(Species.bad))
        		badEquity[Integer.parseInt(lastFish.equityN)-1]+=1;
        	else
        		goodEquity[Integer.parseInt(lastFish.equityN)-1]+=1;
        	
        }
        //this.createNextFish(ge.when); //

		// get the response time, in nanoseconds, and write it to the log
		// long responseTime = ge.when - lastFish.birthTime;

		// String log = e.getKeyChar() + " " + responseTime / 1000000.0 + " " +
		// ge.correctResponse + " " + lastFish;
		// System.out.println(log);

		// now we write the result to the log
		this.writeToLog(ge.when, ge);

		// flash the lights, and modify the score
		// REFACTOR: move the goodclip/badclip playing into this file
		// so it will occur closer to the actual button press
		// it is current in GameView, all AudioClips should be played in the
		// same thread

		this.soundflash = true;
		this.soundIndicatorUpdate = System.nanoTime() + 50 * million;

		// update the summary data and send markers to the EEG
		if (ge.correctResponse) {
			sendEEGMarker(ge.when,"KEYG");
			this.wealth++;
			this.hits += 1;
		} else {
			sendEEGMarker(ge.when,"KEYB");
			this.wealth--;
			this.misses += 1;
		}

		// Finally, remove the last fish (should only be one!)
		// but don't remove until the end as update could be called as we are
		// pressing the key!!!
		// THIS NEEDS TO BE SYNCHRONIZED
		this.removeLastFish();
		return ge.correctResponse;
	}

	/**
	 * There are two modes, depending on whether we are showing a blank screen or not.
	 * For a blankscreen, we do nothing except check to see if the blankscreen timeout
	 * has occurred, if so then we set this.blankScreen to false, and return in all cases.
	 * update moves all actors one step check to see if a fish has become
	 * inactive if so, remove it and create a new fish (but don't launch right
	 * away!)
	 */
	public synchronized void update() {
		long now = System.nanoTime();


		/*
		 * if you are in EEG mode, then the session starts and ends with
		 * a blank screen, and the next two if statements handle those
		 * cases ... If you are not in EEG mode, these boolean variables
		 * will always be false.
		 */
		if (this.firstBlankScreen){
			if (now > this.blankScreenTimeout){
				//send ERES indicating that the initial rest period has ended.
				sendEEGMarker(now,"ERES");
				this.firstBlankScreen = false;
				//create fish for start of game
				createNextFish(System.nanoTime());
			}else {
				return;
			}
		}
		if(this.midBlankScreen){
			return;
		}
		
		if (this.secondBlankScreen){
			if (now > this.blankScreenTimeout){
				System.out.println("ending the 2nd blank screen");
				this.secondBlankScreen=false;
				this.paused=true;
				this.gameOver = true;	
				stopEEG();
			} else return;
	
		}
		
		if ( isGameOver()){
			if(gameSpec.Equity){
				writeEquityStat();
				
			}
			closeLogfile();
			return;
		}

		// these variables are used by the GameView class
		// REFACTOR: be careful about synchronization of these variables
		totalActorTime = (currentActorTime) / 1000000;
		timeRemaining = 100 - (totalActorTime / timePerTrial);

		/*
		 * There are two possibilities to consider. Either there is no fish on
		 * the screen or there is one fish If there is no fish, then we check to
		 * see if it is time to launch the next fish, and we do so If there is a
		 * fish on screen, then we update its state
		 */

		/*
		 * check to see if it time to create a new fish and we haven't yet
		 * created it... We will spawn a fish (so currentFish will be non null)
		 * but we won't create a new fish until this one disappears!
		 */
		if (currentFish == null) {
			if (now > this.nextFishTime) {
				currentActorTime = 0;
				spawnFish(now);
			}
		} else { // update the fish state
			Fish a = currentFish;
			a.update();
			keepOnBoard(a);

			if (!a.active) { // this is where we remove the fish if the user
								// didn't press a key and the lifespan has been
								// reached...
				removeFish(now, a);
				// these fields are used by GameView for gamification
				previousActorTime += a.lifeSpan;
				currentActorTime = 0;
				this.noKeyPress += 1;
			} else {
				this.currentActorTime = a.lifeSpan;
			}
		}

	}

	private void removeFish(long now, Fish a) {
		a.ct.stop();

		GameEvent missedFishEvent = new GameEvent(a);
		fishSoFar++;
		boolean timeForPause = checkForPause();
		if(!timeForPause) {
			createNextFish(now);
		}
		this.writeToLog(now, missedFishEvent);
		currentFish = null;
		sendEEGMarker(now,"EFIS");
		flash = true;
		indicatorUpdate = System.nanoTime() + 50000000l;
	}
	

	public void sendEEGMarker(long now,String code){

		if (this.usingDebgEgg)
			return;
		
		//this.writeToLog(now, "sendEEGMarker:"+ code);
		
		try {
			EEG.eventNS0(code, systimeToMillis(now), 1);
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Problem with EEG connection!!!\n Error is "+e);
		}
	}
	

	/**
	 * spawns a new fish using the data stored in the this.nextFish object
	 */
	public void spawnFish(long now) {

		if (this.isGameOver())
			return;

		
		
		// first we initialize its position and velocity
		double y = GameModel.HEIGHT / 2;
		double x = (nextFish.fromLeft) ? 1 : GameModel.WIDTH - 1;
		nextFish.x = x;
		nextFish.y = y;
		nextFish.vx = (nextFish.fromLeft) ? 10 : -10;
		nextFish.vy = 0;

		// next we initialize a few other fields
		// REFACTOR: some of these are redundant -- remove and simplify the
		// code!
		nextFish.active = true;
		nextFish.avmode = gameSpec.avmode;
		nextFish.birthTime = now;
		nextFish.lastUpdate = now;
		
		// now we set the seed for the random number generator for the fish
		// to depend on the trial number so that it is the same in every experiment
		// there will still be slight differences as the times since the fish updates
		// depend on the time since the last update and that will vary from run to run
		// If we want we could be more precise here and create a random path based on the
		// seed and have the fish follow that path
		
		nextFish.rand.setSeed(nextFish.trial);

		// REFACTOR: use the gameSpec.lifetime field instead of 2000L here
		nextFishTime = now + 2000L * million;
		
		// send marker to EEG
		sendEEGMarker(now,"GO--");

		 System.out.println("Spawning new fish: "+nextFish);

		/*
		 * this decides which sound file to play and starts it in a loop
		 * if we are in EEG mode this should delay 200ms ...
		 */
		playFishSound();

		// add the fish to the list of actors...
		currentFish = nextFish;

		// send a flash to the indicator
		flash = true;
		indicatorUpdate = System.nanoTime() + 50000000l;

		// log this event
		writeToLog(now, nextFish); // indicate that a was spawned

	}

	private void sendEEGTrialStartMarker(long now, int congruent, boolean fromLeft, Species species) {
		/* EEG code */
		// here we create a code of the form Txxx
		// where xxx is the trial number 
		// to be safe we handle the case more than 1000 trials
		// by taking the remainder of the trial number by 1000
		// and then making a number of the form 1xxx where xxx
		// is the trial number modulo 1000
		String trialString = Integer.toString(1000+(nextFish.trial % 1000 ));
		String code = "T" + trialString.substring(1);
		sendEEGMarker(now,code);
		
		switch (congruent) {
		case 0: code = "CONG";break;
		case 1: code = "INCO";break;
		case 2: code = "CONT";break;
		case 3: code = "AUDI";break;
		default: code="ERRO";break;
		}

		sendEEGMarker(now+million,code);
		
		if (fromLeft)
			code="DIRL";
		else
			code="DIRR";

		sendEEGMarker(now+2*million,code);
		
		// Yile wants the frequency encoded here too
		// but that is a little tricky as it depends on 
		// if they are focused on audio or visual
		// and if it is audio, then we only have a sound file
		// and we don't actually have an integer frequency....
		switch (species) {
		case good: code="GOOD";break;
		case bad: code="BAD "; break;
		default: code="ERRO"; break;
		}
		now = System.nanoTime();
		sendEEGMarker(now+3*million,code);		
	}

	private void playFishSound() {
		// this gives the location of the soundfile
		String clip;
		
		if (gameSpec.avmode == 0) { // visual
			if (nextFish.congruent == 0) { // audio/visual are congruent
				if (nextFish.species == Species.good){
					clip = gameSpec.good.soundFile;
				} else {
					clip = gameSpec.bad.soundFile;
				}
			} else if (nextFish.congruent == 1) { // audio/visual are not congruent
				if (nextFish.species == Species.good){
					clip = gameSpec.bad.soundFile;
				} else {
					clip = gameSpec.good.soundFile;
				}
			} else { // all sounds are non-modulated
				clip = gameSpec.nonModulatedSound;
			}
		} else { // auditory game mode (then the clip just depends on the species ...

				if (nextFish.species == Species.good){
					clip = gameSpec.good.soundFile;
				} else {
					clip = gameSpec.bad.soundFile;
				}
			
		}
		System.out.println("goodfile="+gameSpec.good.soundFile);
		System.out.println("badfile="+gameSpec.bad.soundFile);
		System.out.println("nonmodfile="+gameSpec.nonModulatedSound);
		
		System.out.println("congruent="+nextFish.congruent);
		System.out.println("species="+nextFish.species);
		System.out.println("Equity#= "+nextFish.equityN);
		System.out.println("clip="+clip);

		// set the appropriate AudioClip
		if (gameSpec.Equity==false || gameSpec.avmode==0)
		{
			if (!gameSpec.stereo)
				nextFish.ct = new AudioClip(clip + "/fish.wav");
			    
			else if (nextFish.fromLeft)
				nextFish.ct = new AudioClip(clip + "/fishL.wav");
			else
				nextFish.ct = new AudioClip(clip + "/fishR.wav");
		}
		else{
			if (!gameSpec.stereo)
				nextFish.ct = new AudioClip(clip + "/fish" + nextFish.equityN+".wav");
			    
			else if (nextFish.fromLeft)
				nextFish.ct = new AudioClip(clip + "/fishL" + nextFish.equityN+".wav");
			else
				nextFish.ct = new AudioClip(clip + "/fishR" + nextFish.equityN+".wav");
		}
		/*
		 * 
		 */
		
		
		
		//send marker that fish sound has started
		long now = System.nanoTime();
		sendEEGMarker(now,"SOUN");
		nextFish.ct.loop();
		
		
		
		soundflash = true;
		soundIndicatorUpdate = System.nanoTime() + 50000000l;
         
		
		// if fish is not silent play sound
		/*
		if (nextFish.congruent != 2 && gameSpec.avmode != 1) {
			nextFish.ct.loop();
			soundflash = true;
			soundIndicatorUpdate = System.nanoTime() + 50000000l;
		} else if (gameSpec.avmode == 1) {
			nextFish.ct.loop();
			soundflash = true;
			soundIndicatorUpdate = System.nanoTime() + 50000000l;
		}
		*/
		/*
		// if fish is not silent play sound
		if (nextFish.congruent != 2 && gameSpec.avmode != 1) {
			nextFish.ct.loop();
			soundflash = true;
			soundIndicatorUpdate = System.nanoTime() + 50000000l;
		} else if (gameSpec.avmode == 1) {
			nextFish.ct.loop();
			soundflash = true;
			soundIndicatorUpdate = System.nanoTime() + 50000000l;
		}
		*/
		
		
		
		
	}

	/**
	 * this reads the next line in the Script file property/value lines cause
	 * the system to update the GameSpec fish launches read the interval and use
	 * it to compute nextFishTime and read the species and side and store it in
	 * this.nextFish
	 * 
	 * This is called at the beginning of each trial. That is, at the beginning
	 * of the game and right after each fish is removed from the gameboard
	 * (either by a key press or by its exceeding its lifetime)
	 * 
	 * If there are no more fish, then the game ends.
	 * 
	 * This method sets the nextFishTime using the currentTime and the
	 * nextFish.interval field.
	 * 
	 * @return
	 */

	public void createNextFish(long now) {

		// initialize the scanner if its the first time we're reading a line
		if (scan == null)
			createScanner();
		if (scan == null)
			return;

		// this should never happen because there is a GAMEOVER event at the end
		// of every script file, but just in case, we'll end the game...
		if (!scan.hasNext()) {
			System.out
					.println("This should never happen!  Reached end of file!!");
			this.setGameOver(true);
			this.nextFishTime = this.nextFishTime + 10 * 1000000000L;
			return;
		}

		long interval = scan.nextLong();

		// process all the 0 interval commands (which set game properties)

		while (interval == -1) {
			interval = updateGameSpec();
		}

		// calculate the next FishTime and the basic characteristics of the
		// nextFish (species and side)
		if (interval == 0) {
			stop();
			return;
		}

		readNextFishData(now, interval);

	}

	private void readNextFishData(long now, long interval) {
		nextFishTime = interval * million + System.nanoTime(); // now;
		// the sound file and visual hertz in the input files are not used and
		// are just documentation....

		// REFACTOR: we should do a check to see that these values
		// all are consistent as congruent, sound, species and visualhz are
		// redundant...
		String sound = scan.next();
		int visualhz = scan.nextInt();
		/*
		if (visualhz != .visualhz) {
			
		}
		*/
		int congruent = scan.nextInt();
		int trialnum = scan.nextInt();
		String fromLeft = scan.next();

		String species = scan.next();
        String EquityN;
        if(gameSpec.Equity) {
        	EquityN = scan.next();
        } else {
        	EquityN = null;
        }
       
		/*
		 * System.out.println("Next fish release in "+interval+" milliseconds\n"
		 * + "from \n"+ System.nanoTime() + " at \n"+ nextFishTime
		 * +"\n  or in ms "+ "at "+(nextFishTime-gameStart)/million +
		 * " ms since start");
		 */

		scan.nextLine(); // skip over the rest of the line

		// create the next Fish to be launched
		nextFish.interval = interval;
		nextFish.avmode = gameSpec.avmode;
		nextFish.fromLeft = fromLeft.equals("left");
		nextFish.congruent = congruent;
		nextFish.trial = trialnum; 
		nextFish.species = (species.equals("good")) ? Species.good
				: Species.bad;
		nextFish.active = true;
		nextFish.lastUpdate = now;
        nextFish.equityN=EquityN;
		nextFishTime = now + interval * million;
		System.out.println(" Avmode" +gameSpec.avmode);
		if(gameSpec.avmode==0 && gameSpec.Equity==true)
		{
			nextFish.minBright=Integer.parseInt(minBrightness[Integer.parseInt(EquityN)-1]);
			nextFish.maxBright=Integer.parseInt(maxBrightness[Integer.parseInt(EquityN)-1]);
			nextFish.minSize=Integer.parseInt(minSize[Integer.parseInt(EquityN)-1]);
			nextFish.maxSize=Integer.parseInt(maxSize[Integer.parseInt(EquityN)-1]);
		}
		// this is the beginning of the trial so we can post some EEG markers
		sendEEGTrialStartMarker(now,nextFish.congruent,nextFish.fromLeft,nextFish.species);
	}

	/*
	 * this reads a property/value pair in the script file and uses that data to
	 * update the GameSpec for this model
	 * 
	 * REFACTOR: we allow files to end with a "-1 gameover .." line or a
	 * "0 gameover ..." line. Make a decision about which to use and stick with
	 * it!
	 */
	private long updateGameSpec() {
		long interval;
		long now = System.nanoTime();
		String prop = scan.next();
		String value = scan.next();
	
			if(prop.equals("minBrightnesslevels")|| prop.equals("maxBrightnesslevels")|| (prop.equals("minSizelevels")) || (prop.equals("maxSizelevels"))){
				while (value.indexOf(']')==-1){
					value+=scan.next();
				}
			
			scan.nextLine(); // skip over the rest of the line
			writeToLog(now, "0\t" + prop + "\t" + value);
			if (prop.equals("minBrightnesslevels")) 
				minBrightness=(value.replace("[", "").replace("]", "")).split(",");
			else if	(prop.equals("maxBrightnesslevels")) 
				maxBrightness=value.replace("[", "").replace("]", "").split(",");
			else  if	(prop.equals("minSizelevels")) 
				minSize=value.replace("[", "").replace("]", "").split(",");
			else if	(prop.equals("maxSizelevels")) 
				maxSize=value.replace("[", "").replace("]", "").split(",");
			
			interval = scan.nextLong();
			return interval;
		}
		scan.nextLine(); // skip over the rest of the line
		writeToLog(now, "0\t" + prop + "\t" + value);
		if (prop.equals("gameover")) {
		
			this.stop();
		
			this.nextFishTime = now + 10 * 1000000000L;
			return 0;
		}
		
		// System.out.println("interval="+interval+" prop="+prop+" value="+value);
		interval = scan.nextLong();
        
		gameSpec.update(prop, value);
		return interval;
	}
    private void writeEquityStat() 
    {
    	String logLine="Equity Statistics \n";
    	logLine+="Species \t Equity# \t accuracy \n";
    	for (int i=0; i<goodEquity.length;i++)
    		logLine+="Good   \t"+ (i+1)+"      \t" +((goodEquity[i])/(gameSpec.numNeutral/(2*5.0))) +"\n";
    	for (int i=0; i<badEquity.length;i++)
    		logLine+="bad    \t"+ (i+1)+"      \t" +(badEquity[i]/(gameSpec.numNeutral/(2*5.0))) +"\n";
    	try {
			this.logfile.write(logLine);
			System.out.println(logLine);
			this.logfile.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    }
	/**
	 * if no scanner exists yet, then create it using the inputScriptFileName
	 */
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

	/*****************************************************************************
	 * Fields used for the Graphical User Interface but that don't affect the
	 * model
	 * 
	 * REFACTOR: these should all be stored in their own object as they don't
	 * really effect the model at all and so should be fields of the model....
	 */

	/**
	 * The variables below are all used in the GUI and don't affect how the
	 * model evolves in time.
	 */
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

	public boolean flash;

	public long indicatorUpdate;

	public boolean soundflash;

	public long soundIndicatorUpdate;

	/** these variables record good/bad hits */
	private int hits, misses, noKeyPress;

	/**
	 * returns the number of hits so far
	 * 
	 * @return the hits
	 */
	public int getHits() {
		return hits;
	}

	/**
	 * @return the misses
	 */
	public int getMisses() {
		return misses;
	}

	/**
	 * @return the noKeyPress
	 */
	public int getNoKeyPress() {
		return noKeyPress;
	}

	/*****************************************************************************
	 * logging data
	 * 
	 * REFACTOR: we might want to make this a separate class too since it
	 * doesn't really affect the model.
	 */

	public void writeToLog(long now, Fish f) {
		String fish="";
        if (f.species.equals(Species.bad))
        		fish="bad ";
        else
        	fish="good";
		String logLine = "launch\t" +fish + "\t" + f.congruent + "\t"
				+ f.trial + "\t" + (f.fromLeft ? "left" : "right");
		if (gameSpec.Equity)
		{	 logLine+=" EquityN= " + f.equityN ;
        if (gameSpec.avmode==0)
    	   logLine+="  [minb= "+ f.minBright+"\t"+"maxb= "+f.maxBright+"\t"+"minSize="+f.minSize+"\t"+"maxSize="+f.maxSize + "]";
		}
		writeToLog(now, logLine);
	}

	public void writeToLog(long now, GameEvent e) {
		writeToLog(now, e.toString());
	}

	/**
	 * This writes a string to the log file and prefixes it with the number of
	 * milliseconds since the beginning of the session.
	 * 
	 * @param s
	 *            the string to be written to the log file
	 */
	public void writeToLog(long now, String s) {
		try {
			long theTime = (now - this.gameStart);

			long theInterval = theTime - lastLogEventTimeNano;
			lastLogEventTimeNano = theTime;

			int theSeconds = (int) Math.round(theInterval / 1000000.0);
			String logLine = theTime / 1000000 + " " + theSeconds
					+ GameEvent.sep + s + "\n";
			getLogFile(); // make sure the logfile is open!
			this.logfile.write(logLine);
			this.logfile.flush();
			System.out.println("log:" + logLine);
		} catch (Exception e) {
			System.out.println("Error writing to log " + e);
		}
	}

}
