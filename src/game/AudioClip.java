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

/**
 * read and play an audio file
 */
public class AudioClip {
	/**
	 * the clip to be played
	 */
	public Clip clip;
	
	/**
	 * the file holding the clip
	 */
	public String filename;

	/**
	 * create a clip from the filename
	 * @param audiof
	 */
	public AudioClip(String audiof) {
		filename = audiof;
	}

	/**
	 * play the clip once
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
		clip.start();

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

	/**
	 * play the clip in a loop
	 */
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

	/**
	 * stop the clip
	 */
	public void stop() {
		if (clip != null) {
			if (clip.isRunning())
				clip.stop();
		}
	}

	/**
	 * load the clip from a filename
	 * @param path
	 * @throws UnsupportedAudioFileException
	 * @throws IOException
	 * @throws LineUnavailableException
	 */
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
}