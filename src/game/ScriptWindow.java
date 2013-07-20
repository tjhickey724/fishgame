/**
 * 
 */
package game;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.*;

/**
 * This generates a window that lets the experimenter fill in some data about the experiment
 * and then run the script and create a log. It also provides options for controlling the
 * experiment by pausing/restarting and ending the session.
 * @author mike
 *
 */
public class ScriptWindow extends JFrame {
	JTextField expId,subId,scr;
	JPanel scriptpanel;
	JLabel scrip,currentactor,selectedFile;
	JButton start,restart,pause,sdone,runscript,openButton;
	GameModel gm;
	GameSpec gs;
	SubjectWindow sw;
	ScriptWindow thisSW;
	
    JFileChooser fc;
	
	public ScriptWindow(){
		super("Script Window");
		thisSW = this;
		gs = new GameSpec();
		gm = new GameModel(100,10,gs);
		sw = new SubjectWindow(gm);
		sw.setVisible(true);
		setSize(300,250);
		
		/*
		 * all of these initializations should be done above, where the variables
		 * are declared. It makes it easier to understand what the variables are
		 * and it simplifies the constructor, ... OR ....
		 * if possible don't even create a variable, just use the constructor to
		 * add the label or other non-readable/writeable widget into the layout below.
		 */
		
		expId= new JTextField("Experimenter");
		subId = new JTextField("Subject");
		scr= new JTextField("scripts/demoscriptv1.txt");
		currentactor = new JLabel("Current Fish #:");
		start= new JButton("start");
		scrip = new JLabel("ScriptFile: ");	
		pause=new JButton("pause");
		restart=new JButton("Restart");
		//stop = new JButton("Stop");
		sdone=new JButton("Done");
		selectedFile=new JLabel("Script File");
		
		
		fc = new JFileChooser();
		
		openButton = new JButton("Open a File...");
		openButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {

		        //Handle open button action.
		            int returnVal = fc.showOpenDialog(ScriptWindow.this);

		            if (returnVal == JFileChooser.APPROVE_OPTION) {
		                File scriptFile = fc.getSelectedFile();
		                selectedFile.setText(scriptFile.getName());
		            }}});
	
		
		sdone.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				gm.stop();
				thisSW.setVisible(false);
				sw.setVisible(false);
				// stop the background sound bgSound
				sw.gameboard.bgSound.stop();
			}
		});
		
		start.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				// we use the static variable GAME_START of GameActor
				// to record when the game has begun, this is used to print out
				// time stamps in the log files
				GameActor.GAME_START = System.nanoTime();

				// the GameModel is the object that reads the script
				gm.inputScriptFileName=scr.getText();
				
				// Now we write the header on the log file recording
				// the relevant information for this session
				String SubjectID=subId.getText();
				String ExperimenterID=expId.getText();

				try {
					gm.writeToLog("Version:                "+RunGame.versionNum);
					gm.writeToLog("Experimenter:           "+ ExperimenterID);
					gm.writeToLog("Subject:                " + SubjectID);
					gm.writeToLog("Date:                   "+ (new java.util.Date()).toString());
					gm.writeToLog("Scriptfile:             "+scr.getText());
					gm.logfile.flush();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				// next we start the game model
				gm.start();
				// and start the game loop which will update the model
				// and view as fast as possible
				GameLoop gl = new GameLoop(gm,sw.gameboard);
				Thread t = new Thread(gl);
				t.start();

			
			}
		});

		// this pauses or restarts the game so the subject can take a break if needed
		pause.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				if (pause.getText().equals("pause")) {
					gm.pause();
					pause.setText("restart");
				} else {
					gm.restart();
					pause.setText("pause");
				}
				
			}
		});
		

		// Finally we do the layout of the components
		// we could also have moved all the labels and other info into here
		// and not given them names at all! 
		scriptpanel = new JPanel();
		scriptpanel.setLayout(new GridLayout(5,1));
		
		scriptpanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Main Control") );
		scriptpanel.add(subId);
		scriptpanel.add(expId);
		scriptpanel.add(scrip);
		scriptpanel.add(scr);
		scriptpanel.add(currentactor);
		scriptpanel.add(start);
		//scriptpanel.add(restart);
		//scriptpanel.add(stop);
		scriptpanel.add(pause);
		scriptpanel.add(sdone);
		//scriptpanel.add(openButton);
		//scriptpanel.add(selectedFile);
		scriptpanel.setBackground(Color.gray);
		this.add(scriptpanel);
	}

}
