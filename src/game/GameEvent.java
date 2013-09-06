package game;

/**
 * This class records game events which can be logged The events of interest are
 * generally keypresses after a fish is released, but sometimes there will be no
 * keypress for a fish and this is indicated using a 0 for the keyCharacter The
 * elements of interest are the response time
 * 
 * @author tim
 * 
 */
public class GameEvent {

	public static String sep = "\t";

	public String eventType = "none";

	/** the time the event occurred from System.nanoTime() **/
	public long when = System.nanoTime();

	/** the key pressed, use 0 for no key pressed **/
	public char keyPressed = 0;

	/**
	 * response time in milliseconds if key is pressed with no fish or fish is
	 * missed, this is 0
	 */
	public int responseTime = 0;

	/** the fish itself, possibly null if a key was pressed for no fish **/
	public GameActor fish = null;

	/** the fish species **/
	public Species species = Species.none;

	/** thie fish side **/
	public String side = "none";

	/** whether the response was correct or not **/
	public boolean correctResponse = false;

	/** when the fish was released, 0 for no fish **/
	public long fishRelease = 0;

	/** when the key was pressed, 0 for no key press **/
	public long keyPress = 0;

	// converts from System.nanoTime units to milliseconds from game start
	private int convertNanoToMSfromGS(long t) {
		int ms = (int) Math.round((t - GameActor.GAME_START) / 1000000.0);
		return ms;
	}

	/**
	 * this is called for other events
	 */
	public GameEvent(String eventType) {
		this.eventType = eventType;
	}

	/**
	 * this creates an event for a missed fish
	 * 
	 * @param keyPressed
	 */
	public GameEvent(char keyPressed) {
		this.eventType = "keypress  ";
		this.when = System.nanoTime();
		this.keyPressed = keyPressed;
		this.responseTime = 0;
		this.fish = null;
		this.species = Species.none;
		this.side = "none";
		this.correctResponse = false;
		this.fishRelease = this.when;
		this.keyPress = this.when;
	}

	/**
	 * this is called when a fish is missed, i.e. the user didn't press a key
	 * 
	 * @param fish
	 */
	public GameEvent(GameActor fish) {
		this.eventType = "missedfish";
		this.when = System.nanoTime();
		this.keyPressed = 0;
		this.responseTime = 0;
		this.fish = fish;
		this.species = fish.species;
		this.side = (fish.fromLeft) ? "left" : "right";
		this.correctResponse = false;
		this.fishRelease = fish.birthTime;
		this.keyPress = this.when;
	}

	/**
	 * this is used when the user responds to a fish while it is on the screen
	 * 
	 * @param keyPressed
	 * @param fish
	 */
	public GameEvent(char keyPressed, GameActor fish) {
		this.eventType = "hitfish   ";
		this.when = System.nanoTime();
		this.keyPressed = keyPressed;
		this.fish = fish;
		this.species = fish.species;
		this.side = (fish.fromLeft) ? "left" : "right";
		this.fishRelease = fish.birthTime;
		this.correctResponse = hitCorrectKey(keyPressed, fish);
		this.keyPress = this.when;
		this.responseTime = (int) Math
				.round((this.when - this.fishRelease) / 1000000.0);
	}

	/**
	 * returns true if the key press was correct
	 * 
	 * @param c
	 * @param lastFish
	 * @return
	 */
	private boolean hitCorrectKey(char c, GameActor lastFish) {
		Species s = lastFish.species;
		boolean onLeft = lastFish.origin == 0;
		if (s == Species.good)
			if (onLeft)
				return c == 'q';
			else
				return c == 'p';
		else if (onLeft)
			return c == 'a';
		else
			return c == 'l';
	}

	public String toString() {
		String response =
		// convertNanoToMSfromGS(this.when) + sep +
		this.eventType + sep + this.responseTime + sep + this.correctResponse
				+ sep + this.keyPressed + sep + this.species + sep + this.side
				+ sep
		// + convertNanoToMSfromGS(this.fishRelease) + sep
		// + this.when + sep
		// + convertNanoToMSfromGS(this.keyPress) + sep;
		;
		return response;
	}
}
