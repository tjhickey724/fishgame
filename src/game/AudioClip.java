package game;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * this class is for playing small Audio Clips you can start, stop, and loop
 * clips
 */
public class AudioClip extends TimerTask {
	public Clip clip;
	public String filename;
	public GameModel gm = null;
	/**
	 * the 4 char code to be sent to the NetStation in EEG mode
	 * when this clip is played
	 */
	public String codeForEEG=""; 


	/**
	 * create an AudioClip based on the filename
	 * 
	 * @param audiof
	 */
	public AudioClip(String audiof) {
		filename = audiof;
	}
	
	public AudioClip(String audiof, GameModel gm){
		filename = audiof;
		this.gm=gm;
		
	}
	

	/**
	 * play the audio clip
	 */
	public void play() {
		// play the sound clip
		try {
			loadClip(filename);
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

		gm.writeToLog(System.nanoTime(), "playFeedback: "+this.filename);
		clip.start();

		if (this.codeForEEG != ""){
			gm.sendEEGMarker(System.nanoTime(), this.codeForEEG);
		}


		clip.addLineListener(new LineListener() {

			// checks if clip is finished playing
			public void update(LineEvent event) {
				if (!clip.isRunning()) {
					clip.stop();
					clip.flush();
				}
			}
		});
	}
	

	public void loop() {
		// play the sound clip
		try {
			loadClip(filename);
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
		// clip.start();
		clip.loop(Clip.LOOP_CONTINUOUSLY);
		// REFACTOR -- do we need this LineListener?
		clip.addLineListener(new LineListener() {

			// checks if clip is finished playing
			public void update(LineEvent event) {
				if (!clip.isRunning()) {
					clip.stop();
					clip.flush();
				}
			}
		});
	}

	public void stop() {
		if (clip != null) {
			if (clip.isRunning())
				clip.stop();
		} // else
			// System.out.println("Trying to stop a null clip!!");
	}

	public void loadClip(String path) throws UnsupportedAudioFileException,
			IOException, LineUnavailableException {
		// find audio file
		File soundFile = new File(path);
		AudioInputStream sound = AudioSystem.getAudioInputStream(soundFile);

		// load the sound into memory
		DataLine.Info info = new DataLine.Info(Clip.class, sound.getFormat());
		clip = (Clip) AudioSystem.getLine(info);
		clip.open(sound);
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		this.play();
		
	}
	
	
	
	public void playDelayed(NetStation ns, long delay){
		Timer theTimer = new Timer();
		AudioClip newSound = new AudioClip(this.filename,gm);
		theTimer.schedule(newSound, delay);


	}
}