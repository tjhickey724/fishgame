/**
 * 
 */
package game;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import java.io.File;
import java.io.IOException;

import javax.swing.*;

/**
 * This generates a window that lets the experimenter fill in some data about
 * the experiment and then run the script and create a log. It also provides
 * options for controlling the experiment by pausing/restarting and ending the
 * session.
 * 
 * @author mike
 * 
 */

// Refactor: this has not been refactored yet


public class ScriptWindow extends JFrame {
	JPanel scriptpanel;
	GameModel gm;
	GameSpec gs;
	SubjectWindow sw;
	ScriptWindow thisSW;
	JButton openButton, pause;
	JTextField scr, subId, expId;
	JCheckBox eeg = new JCheckBox("EEG?");
	JCheckBox eeg2=new JCheckBox("debug EEG?");
	//JLabel eegLabel = new JLabel("EEG?");
	JFrame theWindow;

	JFileChooser fc;

	public ScriptWindow() {
		super("Script Window");
		thisSW = this;
		gs = new GameSpec();
		gm = new GameModel(gs);
		sw = new SubjectWindow(gm);
		sw.setVisible(true);
		setSize(300, 250);

		/*
		 * all of these initializations should be done above, where the
		 * variables are declared. It makes it easier to understand what the
		 * variables are and it simplifies the constructor, ... OR .... if
		 * possible don't even create a variable, just use the constructor to
		 * add the label or other non-readable/writeable widget into the layout
		 * below.
		 */

		expId = new JTextField("Experimenter");
		subId = new JTextField("Subject");
		scr = new JTextField("scripts/demoscriptv1.txt");
		JButton start = new JButton("start");
		pause = new JButton("pause");

		
		// stop = new JButton("Stop");
		JButton sdone = new JButton("Done");

		fc = new JFileChooser(System.getProperty("user.dir") + "/scripts");
		
		eeg.addItemListener(new ItemListener(){

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					gm.usingEEG = true;
					eeg2.setSelected(false);
				}else {
					gm.usingEEG=false;
				}
			}
			
		});
		eeg2.addItemListener(new ItemListener(){

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					
					gm.usingDebgEgg=true;
					eeg.setSelected(false);
				}else {
					
					gm.usingDebgEgg=false;
				}
			}
			
		});

		openButton = new JButton("Script File");
		openButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				// Handle open button action.
				int returnVal = fc.showOpenDialog(ScriptWindow.this);

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File scriptFile = fc.getSelectedFile();

					openButton.setText("scripts/" + scriptFile.getName());

				}
			}
		});

		pause.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (pause.getText().equals("pause")) {
					gm.pause();
					pause.setText("resume");
				} else {
					gm.resume();
					pause.setText("pause");
				}

			}
		});

		sdone.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (gm.usingEEG || gm.usingDebgEgg){
					JOptionPane.showMessageDialog(theWindow, "Please save the EEG data, then press OK");
				}
				gm.stop();
				thisSW.setVisible(false);
				sw.setVisible(false);
				// stop the background sound bgSound
				sw.gameboard.bgSound.stop();
			}
		});

		start.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// we use the static variable GAME_START of GameActor
				// to record when the game has begun, this is used to print out
				// time stamps in the log files
				Fish.GAME_START = System.nanoTime();

				// the GameModel is the object that reads the script

				gm.setInputScript(openButton.getText());

				// Now we write the header on the log file recording
				// the relevant information for this session
				String SubjectID = subId.getText();
				String ExperimenterID = expId.getText();
				long now = System.nanoTime();

				gm.writeToLog(now, "Version:                "
						+ RunGame.versionNum);
				gm.writeToLog(now, "Experimenter:           " + ExperimenterID);
				gm.writeToLog(now, "Subject:                " + SubjectID);
				gm.writeToLog(now, "Date:                   "
						+ (new java.util.Date()).toString());

				gm.writeToLog(now,
						"Scriptfile:             " + openButton.getText());

				// next we start the game model
				gm.start();
				// and start the game loop which will update the model
				// and view as fast as possible
				GameLoop gl = new GameLoop(gm, sw.gameboard);
				Thread t = new Thread(gl);
				t.start();

			}
		});

		// this pauses or resumes the game so the subject can take a break if
		// needed

		// Finally we do the layout of the components
		// we could also have moved all the labels and other info into here
		// and not given them names at all!
		scriptpanel = new JPanel();
		scriptpanel.setLayout(new GridLayout(6,2));

		scriptpanel.setBorder(javax.swing.BorderFactory
				.createTitledBorder("Main Control"));
		
		scriptpanel.add(this.eeg);
		scriptpanel.add(this.eeg2);
		
		scriptpanel.add(openButton);
		scriptpanel.add(new JLabel(""));
		
		scriptpanel.add(subId);
		scriptpanel.add(expId);
		
		
		scriptpanel.add(start);
		
		scriptpanel.add(pause);
		scriptpanel.add(new JLabel(""));
			
		scriptpanel.add(new JLabel(""));
		scriptpanel.add(sdone);
		
		scriptpanel.setBackground(Color.gray);
		this.add(scriptpanel);
		
		this.theWindow = this;
	}

}
