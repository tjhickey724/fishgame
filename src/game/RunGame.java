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
 * and starts up the game loop.
 * @author tim
 *
 */
public class RunGame {
	public static void main(String[] args) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
		GameModel gm = new GameModel(100,10);
		DrawDemo myDemo = new DrawDemo(gm);
		ParamsUI params= new ParamsUI(gm,myDemo);
		myDemo.frame.setVisible(true); 
		params.setVisible(true);

		GameLoop gl = new GameLoop(gm,myDemo.gameboard);
		Thread t = new Thread(gl);
		System.out.println("gameloop");
		t.start();
		myDemo.gameboard.requestFocus();

	}
}
