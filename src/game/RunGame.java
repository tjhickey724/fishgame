package game;

import java.awt.event.ActionListener;
import java.io.IOException;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;

/**
 * This creates the experimenter's window, which offers the user a choice to run
 * from a script or generate a script. It also stores the versionNum
 * 
 * @author tim
 * 
 */
public class RunGame {
	/**
	 * version must have the form A.B.C where A,B,C are numbers It represents
	 * the Major/Minor/Bugfix versions of the code.
	 */
	public static String versionNum = "1.0.5";

	public static void main(String[] args) {

		ExperimenterWindow params = new ExperimenterWindow();
		params.setVisible(true);
	}
}
