package game;
import java.awt.Color;
import java.io.IOException;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
/**
 * a GameActor has a position and a velocity and a speed
 * they also have a species 
 * and they keep track of whether they are active or not
 * @author tim
 *
 */
public class GameActor {
	// this is a bit of a hack, I need to refactor later REFACTOR!!
	public static long GAME_START = 0; //System.nanoTime();
	
	    String soundFolder = "fish_6_8_hz_pan0"; //"fish_3_5_hz_pan50";
	// size
		double radius = 10;
		// position
		double x;
		double y;
		// velocity
		double vx;
		double vy;
		// still on board?
		boolean active;
		// speed
		double speed=10;
		// species
		boolean fromLeft; // true if fish comes from left
		long birthTime;
		long lastUpdate;
		long gameStart=GameActor.GAME_START;
		Color 
			color1=new Color(150,0,0), 
			color2=new Color(200,0,0),
			color3=new Color(100,100,100), 
			color4=new Color(250,250,250);
		double colorHerz = 4;
		// Origin: 
		int origin;
		AudioClip ct,ctL,ctR;
		//AudioClip bt;
		Species species; 
		
		protected java.util.Random rand = new java.util.Random();

		public GameActor(double x, double y, boolean active, Species spec) {
			this(x,y,active,spec,true,"sounds/fish6hz0p","sounds/fish8hz0p");
		}
		

		
		public GameActor(double x, double y, boolean active, 
				         Species spec,
				         boolean stereo,
				         String goodFishSounds, String badFishSounds) {
			this.x=x; this.y=y; this.active=active;
			this.vx = speed*(rand.nextDouble()-0.5);
			this.vy = speed*(rand.nextDouble()-0.5);
			this.birthTime = System.nanoTime();
			this.lastUpdate = this.birthTime;
			this.gameStart=GameActor.GAME_START;
			this.species = spec;
			
			String fishSounds= 
				(species.equals(Species.good))?goodFishSounds:badFishSounds;
			
			try {
				this.ct=   new AudioClip(fishSounds+"/fish.wav");
				if (stereo){
					this.ctR = new AudioClip(fishSounds+"/fishR.wav", -10.0f);
					this.ctL = new AudioClip(fishSounds+"/fishL.wav", -10.0f);
					//(fishSounds+"/fishR.wav");
					//System.out.println(fishSounds+"/fishL.wav");
				} else {
					this.ctR = this.ct;
					this.ctL = this.ct;
				}
			} catch(Exception e){
				System.out.println("Audio problems!!:"+e);
			}
		}
		
		public GameActor(double x, double y) { //throws UnsupportedAudioFileException, IOException, LineUnavailableException {
			this(x,y,true,Species.good);
		}
		
		public GameActor() { //throws UnsupportedAudioFileException, IOException, LineUnavailableException {
			this(0,0,true,Species.good);
		}

		/**
		 * actors change their velocity slightly at every step
		 * but their speed remains the same. Update slightly modifies
		 * their velocity and uses that to compute their new position.
		 * Note that velocity is in units per update.
		 */
		public void update(){
			long now = System.nanoTime();
			double dt = (now -this.lastUpdate)/1000000000.0;
			this.lastUpdate = now;
			double turnspeed = 0.1;
			vx += rand.nextDouble()*turnspeed -turnspeed/2;
			vy += rand.nextDouble()*turnspeed -turnspeed/2;
			double tmpSpeed = Math.sqrt(vx*vx+vy*vy);
			vx /= tmpSpeed;
			vy /= tmpSpeed;
			x += vx*speed*dt;
			y += vy*speed*dt;
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
		
		public String toString(){
			int ix = (int)x;
			int iy = (int)y;
			//return "["+ix+","+iy+","+active+"]";
			return "["+this.species +","+this.origin+"]";
		}

		public String printStream(){
			String output = toString();
			output += "Fish is active: "+ active + " Birth Time:"+ " last updated: "+lastUpdate;
			return output;
		}

}
