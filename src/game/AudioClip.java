package game;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 audio file object with play and stop play methods
 */
public class AudioClip {
	public Clip clip;
	public String filename;
	public Float volumeControl;
	public AudioClip(String audiof, Float vc){
		filename = audiof;
		volumeControl = vc;
	}
	public AudioClip(String audiof) {
		filename = audiof;
		volumeControl = -10.0f;
	}
	public void play() {
	// play the sound clip 
		try {
			loadClip(filename);
		} catch (UnsupportedAudioFileException  e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 catch ( IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 catch (LineUnavailableException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		FloatControl gainControl = 
			    (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
			gainControl.setValue(volumeControl);
		clip.start();
		
		clip.addLineListener(new LineListener(){

			//checks if clip is finished playing
			public void update(LineEvent event) {
				if(!clip.isRunning()){
					clip.stop();
					clip.flush();
				}
			}
		});
	}
	//delay play of sound for science 200 milli seconds
	public synchronized void playDelay() {
		double current = System.nanoTime() / 1000000000.0;
		double start = current;
		while(current - start < .2) {
			//to test if delay is working
			//System.out.println("delaying");
			current = System.nanoTime() / 1000000000.0;
		}
		play();
	}
	public void loop() {
	// play the sound clip 
		try {
			loadClip(filename);
		} catch (UnsupportedAudioFileException  e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 catch ( IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 catch (LineUnavailableException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		//clip.start();
		clip.loop(Clip.LOOP_CONTINUOUSLY);  
		// REFACTOR -- do we need this LineListener?
		clip.addLineListener(new LineListener(){

			//checks if clip is finished playing
			public void update(LineEvent event) {
				if(!clip.isRunning()){
					clip.stop();
					clip.flush();
				}
			}
		});
	}
  
	public void stop() {
		if (clip != null){
			if (clip.isRunning())
				clip.stop();
		}else System.out.println("Trying to stop a null clip!!");
	}
	
	public void loadClip(String path) throws UnsupportedAudioFileException, IOException, LineUnavailableException{
		//find audio file
			File soundFile = new File(path);
			AudioInputStream sound = AudioSystem.getAudioInputStream(soundFile);

	    // load the sound into memory 
			DataLine.Info info = new DataLine.Info(Clip.class, sound.getFormat());
			clip = (Clip) AudioSystem.getLine(info);
			clip.open(sound);
	}
}        