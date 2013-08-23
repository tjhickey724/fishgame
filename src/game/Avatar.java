/**
 * 
 */
package game;

/**
 * The avatar reamins in the middle of the screen until a current is activated to push the boat to one side.
 * @author Mike
 *
 */
public class Avatar extends GameActor {

	/**
	 * @param x
	 * @param y
	 * @param active
	 * @param spec
	 * 
	 */
	//the width of the middle part that is safe for the boat to be in
	public static int channelWidth;
	//the left edge fo the middle 
	static int leftEdge;
	//the right edge of the middle
	static int rightEdge;
	//how fast the boat moves with keypresses. this is no longer used because there is now a single key used to 
	//stabilize the boat from any position
	public static double moveSpeed=1;
	//how strong the current is, and therefore how much time the player has to react to a current before the boat
	// hits the edge
	public static double currentSpeed=0.1;
	//how likely the current is to occur, at 1 it will always occur
	public static double currentProbability=1.0;
	//the key to stabilize the boat
	public static char moveKey = 'a';
	// if you want to determine which side the current will go, then currentType will NOT be random
	public static String currentType = "random";
	// this is the direction the current will be pushing the boat
	public static Side currentDirection = Side.right;
	//while the current is active, this is true
	public static boolean currentActive = false;
	//true while the boat is moving left or right
	private boolean movingLeft,movingRight;
	//the number of hits to the edge the boat can withstand
	public int health=3;
	//the number of times health can reach 0 before the game ends
	public int lives=5;
	//the picture file
	public String boatFileName = "images/boat.png";
	//checks if boat is still alive
	public boolean alive = true;
	
	public Avatar(double x, double y, boolean active, Species spec) {
		super(x, y, active, Species.avatar);
		speed=4;
	}

	/**
	 * @param x
	 * @param y
	 * @param active
	 * @param spec
	 * @param stereo
	 * @param goodFishSounds
	 * @param badFishSounds
	 */
	public Avatar(double x, double y, boolean active, Species spec,
			boolean stereo, String goodFishSounds, String badFishSounds) {
		super(x, y, active, Species.avatar, stereo, goodFishSounds, badFishSounds);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param x
	 * @param y
	 */
	public Avatar(double x, double y) {
		super(x, y);
		this.species=Species.avatar;
	}

	/**
	 * 
	 */
	public Avatar() {
		// TODO Auto-generated constructor stub
		this.species=Species.avatar;
	}
	
	
	public void update(){
		//check if the boat has health
		if (health==0){
			//lose a life if boat runs out of health
			lives--;
			//reset to full health
			health=3;
		}
		if (lives==0){
			System.out.println("boat has died");
			//set the status
			alive=false;
			
		}
		long now = System.nanoTime();
		double dt = (now -this.lastUpdate)/1000000000.0;
		this.lastUpdate = now;
		

		//if the boat is outside the edges of the center band, the current stops and the avatar is stabilized. 
		if (!inMiddle()){
			setCurrentActive(false);
			stabilize();
			health--;
		}
		
		if (currentActive){
			current();
			}
		if (movingLeft){
			moveLeft();
		}
		if(movingRight){
			moveRight();
		}
		//if the avatar is in the very center with an x near 50
		if (x<51 && x>49 && (movingLeft || movingRight)){
			movingLeft=false;
			movingRight=false;
			
		}
/*		if (species.toString().equals("good")){
			try {
				this.ct=new AudioClip("src/sound8.wav");
			} catch (UnsupportedAudioFileException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (LineUnavailableException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (species.toString().equals("bad")){
			try {
				this.ct=new AudioClip("src/bad.wav");
			} catch (UnsupportedAudioFileException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (LineUnavailableException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	} */
	} 

/*	private void current(double dt) {
			int currentSpeed =1;
			//if x is in the middle region
			x+=currentSpeed;
		if (x<50){
				x=currentSpeed;
			} else {
		if (x>30){
			x+=currentSpeed;
		}else{
			currentSpeed=-currentSpeed;
		}
			
		// y += vy*speed*dt;
		
	} */
	
	// pushes boat left
	public void leftCurrent(){
		vx=currentSpeed;
		x-=vx;
		}
	// pushes the boat right
	public void rightCurrent(){
		vx=currentSpeed;
		x+=vx;
		}
	// it is wise to use a method to set and get values if they are accessed by outside classes
	public static void setCurrentActive(boolean boo, Side s){
		currentActive = boo;
		currentDirection = s;
		
	}
	
	public static void setCurrentActive(boolean boo){
		if (Math.round(Math.random()) == 1){
			currentDirection = Side.left;
		}else{
			currentDirection = Side.right;
		}
		currentActive = boo;
		
	}
	
	public static void getCurrentActive(boolean boo){
		currentActive = boo;
	}
	
	
	public void current(){
		if (currentDirection == Side.right){
			rightCurrent();
		} else {
			leftCurrent();
		}

		}
	
	public void moveLeft(){
	
		x-=moveSpeed;
		this.movingLeft=true;
		}
	
	
	public void moveRight(){
		
		x+=moveSpeed;
		this.movingRight=true;
		}
	

	//this method checks if the avatar is inside the darker middle portion of the screen
	public boolean inMiddle(){
		leftEdge = 50-channelWidth/2;
		rightEdge = 50+channelWidth/2;
		return (x<rightEdge && x>leftEdge);
	}

	public void stabilize(){
		setCurrentActive(false);
		if (x==50) return;
		if (x>50){
			moveLeft();
		} else if (x<50){
			moveRight();
			}
	}
}
