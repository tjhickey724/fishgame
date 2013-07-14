package game;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import javax.swing.event.*;

/**
 * DrawDemo creates the GUI for the Psych Game which is designed to study the effects of
 * correlations between visual and auditory oscillation on response time for motor activity
 * among other features.
 * @author tim
 *
 */
public class SubjectWindow {
	private GameModel gm;

	public JFrame frame;
	
	public GameView gameboard;
	private JLabel status;
	private JSlider speedSlider;

	// this will listen to timer events
	// and update the game and view every timestep
	private ActionListener stepButtonListener;
	

	public SubjectWindow(GameModel gamemodel) { // throws UnsupportedAudioFileException, IOException, LineUnavailableException {


		JPanel buttonPanel;
		JButton stepButton;
		JButton runButton;
		JButton stopButton;
		JButton resetButton;
		
		this.gm=gamemodel;
		//this window will have the settings we can adjust

		// first we create the Frame with a border layout
		frame = new JFrame("Subject Window");
		frame.setSize(500,1000);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		
		// next we create the gameview
		gameboard = new GameView(gm);
		
		
		// here is the title of the game and the status bar
		status = new JLabel("Try to collect all the fish!");
		status.setForeground (Color.red);
		
		// Create the buttons and add actions
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(2,1));
		stepButton = new JButton();
		stepButton.setText("step");

		stepButton.addActionListener(stepButtonListener);

		runButton = new JButton();
		runButton.setText("run");
		runButton.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent e){
						gm.start();
						gm.gameOver=false;
						gm.writeToLog(new GameEvent("startgame"));
						status.setText("game in play");
					}
				});
		stopButton = new JButton("stop");
		stopButton.addActionListener(
			new ActionListener(){
				public void actionPerformed(ActionEvent e){
					status.setText("game over");
					//gm.writeToLog(new GameEvent("stopgame"));
					//gm.stop();
				}
			});
		
		resetButton = new JButton("reset");
		resetButton.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent e){
	//					gm.initActors();
						status.setText("game reset!");
						gm.gameOver=false;
					}
				});
		status = new JLabel("");
		
		buttonPanel.add(runButton);
		buttonPanel.add(stopButton);

		

	
		
		// put the frame components together with a border layout
		frame.add(gameboard.header,BorderLayout.NORTH);
		frame.add(gameboard,BorderLayout.CENTER);
		frame.add(status,BorderLayout.SOUTH);


	}

	
}
