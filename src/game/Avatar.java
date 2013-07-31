/**
 * 
 */
package game;

/**
 * @author Mike
 *
 */
public class Avatar extends GameActor {

	/**
	 * @param x
	 * @param y
	 * @param active
	 * @param spec
	 */

	public static int channelWidth;
	static int leftEdge;
	static int rightEdge;
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
		long now = System.nanoTime();
		double dt = (now -this.lastUpdate)/1000000000.0;
		this.lastUpdate = now;
		current(dt);
		if (!inMiddle()){
			System.out.println(x +" "+ leftEdge +" "+ channelWidth);
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
	public void current(double dt){
		double turnspeed = 0.1;
		if (x>rightEdge){
			x=rightEdge;
			vx=0;
		}
		if (x<leftEdge){
			x=leftEdge;
			vx=0;
		}
		vx += rand.nextDouble()*turnspeed -turnspeed/2;

		x += vx*speed*dt;
		
	}

	//this method checks if the avatar is in themiddle of the screen.
	public boolean inMiddle(){
		leftEdge = 50-channelWidth/2;
		rightEdge = 50+channelWidth/2;
		return (x<rightEdge && x>leftEdge);
	}

}
