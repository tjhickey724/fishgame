package game;

import java.awt.event.ActionListener;
import java.io.IOException;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;
/**
 * This creates the game window, initializes the game model,
 * and starts up the game loop.  On a Mac you can use GamePadCompanion.app
 *    https://www.macupdate.com/app/mac/6528/gamepad-companion
 * to map GamePad buttons to the appropriate keys
 * @author tim
 *
 */
public class RunGame {
	public static String versionNum = "0.5";
	
	public static void main(String[] args) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
		GameModel gm = new GameModel(100,10);
		SubjectWindow myDemo = new SubjectWindow(gm);
		ExperimenterWindow params= new ExperimenterWindow(gm,myDemo);
		myDemo.frame.setVisible(true); 
		params.setVisible(true);

		/*
		GameLoop gl = new GameLoop(gm,myDemo.gameboard);
		Thread t = new Thread(gl);
		t.start();
		//myDemo.gameboard.requestFocus();
		 * 
		 */
	

	}
}
