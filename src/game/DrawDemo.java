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
public class DrawDemo {
	private GameModel gm;
	private javax.swing.Timer  timer;
	public JFrame frame;
	public JFrame params;
	public GameView gameboard;
	private JLabel status;
	private JSlider speedSlider;
	private int timerDelay = 1000/30;
	// this will listen to timer events
	// and update the game and view every timestep
	private ActionListener stepButtonListener;

	public DrawDemo(GameModel gamemodel) throws UnsupportedAudioFileException, IOException, LineUnavailableException {

		JLabel header;
		JPanel buttonPanel;
		JButton stepButton;
		JButton runButton;
		JButton stopButton;
		JButton resetButton;
		
		this.gm=gamemodel;
		//this window will have the settings we can adjust
		params = new JFrame("Settings");
		params.setSize(500,500);
		params.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		params.setLayout(new BorderLayout());
		
		
		// first we create the Frame with a border layout
		frame = new JFrame("draw demo");
		frame.setSize(1000,800);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		
		// next we create the gameview
		gameboard = new GameView(gm);
		
		
		// here is the title of the game and the status bar
		header = new JLabel();
		header.setText("Press Q to clear a green fish on the left, P on the right. A for bad fish on the left, L for bad fish on the right.");
		status = new JLabel("Try to collect all the fish!");
		status.setForeground (Color.red);
		
		// Create the buttons and add actions
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(2,1));
		stepButton = new JButton();
		stepButton.setText("step");
		stepButtonListener = new StepButtonListener();
		stepButton.addActionListener(stepButtonListener);

		runButton = new JButton();
		runButton.setText("run");
		runButton.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent e){
						gm.start();
						gm.gameOver=false;
						gm.writeToLog("start session");
						status.setText("game in play");
					}
				});
		stopButton = new JButton("stop");
		stopButton.addActionListener(
			new ActionListener(){
				public void actionPerformed(ActionEvent e){
					gm.stop();
					status.setText("game paused");
					gm.writeToLog("end session");
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
		status = new JLabel("Try hit the correct button as a fish appears!");
		
		// add the buttons to a buttonPanel
	//	buttonPanel.add(stepButton);
		buttonPanel.add(runButton);
		buttonPanel.add(stopButton);
	//	buttonPanel.add(resetButton);
		
		// now create the speedSlider
		speedSlider = new JSlider(JSlider.VERTICAL,1,40,4);
		speedSlider.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent e){
			   int v = speedSlider.getValue();
			   //timer.setDelay(1000/v);
			   gm.herz=v;
			}
		});
	
		
		// put the frame components together with a border layout
		frame.add(header,BorderLayout.NORTH);
		frame.add(gameboard,BorderLayout.CENTER);
		frame.add(buttonPanel,BorderLayout.EAST);
		frame.add(status,BorderLayout.SOUTH);
		//frame.add(speedSlider,BorderLayout.WEST);
		params.add(speedSlider,BorderLayout.WEST);
		//frame.pack();

	}

	/**
	 * @param args ignored
	 */
	
	
	public static void timerVersion()throws UnsupportedAudioFileException, IOException, LineUnavailableException {
		GameModel gm = new GameModel(100,100);
		DrawDemo myDemo = new DrawDemo(gm);
		// now we create the StepButtonListener
		// and add it as a listener to the timer
		myDemo.frame.setVisible(true);

		myDemo.timer = new Timer(10,myDemo.stepButtonListener);
		myDemo.timer.setDelay(50);
		myDemo.timer.start();
		
	}
	/**
	 * a StepButtonListener object is called at every time step
	 * and it updates the model, redisplays the gameboard,
	 * and updates the status.
	 * @author tim
	 *
	 */
	public class StepButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e){
			try {
				gm.update();
			} catch (UnsupportedAudioFileException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (LineUnavailableException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			status.setText("fireflies remaining: "+gm.numActive+
					" current speed ="+1000/timer.getDelay()+" fps");
			gameboard.repaint();
			if (gm.gameOver){
				if (gm.numActive==0)
					status.setText("*** YOU WON ***");
				else
					status.setText("--- YOU LOST ---");
			}
		}
	}
}
