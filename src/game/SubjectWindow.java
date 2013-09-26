package game;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import javax.swing.event.*;

/**
 * DrawDemo creates the GUI for the Psych Game which is designed to study the
 * effects of correlations between visual and auditory oscillation on response
 * time for motor activity among other features.
 * 
 * @author tim
 * 
 */
public class SubjectWindow extends JFrame {

	public JFrame frame;
	public GameView gameboard;
	private JLabel status;
	public JButton pause;
	public SubjectWindow(GameModel gamemodel) { // throws
												// UnsupportedAudioFileException,
												// IOException,
												// LineUnavailableException {

		// first we create the Frame with a border layout
		frame = this;
		this.setTitle("Subject Window");
		frame.setSize(500, 1000);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());

		// next we create the gameview
		gameboard = new GameView(gamemodel);
		
		status = new JLabel("");
		pause=new JButton("Pause");
		// put the frame components together with a border layout
		frame.add(pause, BorderLayout.NORTH);
		gameboard.header.setHorizontalAlignment(SwingConstants.CENTER);
		frame.add(gameboard, BorderLayout.CENTER);
		frame.add(status, BorderLayout.SOUTH);

		KeyAdapter kl = new KeyAdapter() {

			@Override
			public void keyTyped(KeyEvent e) {
				if (e.getKeyChar()=='='){
					gameboard.gm.started=true;
				}
			}
		};
		pause.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (pause.getText().equals("pause")) {
					gameboard.gm.pause();
					pause.setText("resume");
				} else {
					gameboard.gm.restart();
					pause.setText("pause");
				}

			}
		});
	}

}
