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
	JPanel scriptpanel;
	GameModel gm;
	GameSpec gs;
	SubjectWindow sw;
	ScriptWindow thisSW;
	JButton pause=new JButton("pause"),
			openButton = new JButton("demoscriptv1.txt"),
			sdone=new JButton("Done"),
			start= new JButton("start");
	
	JTextField expId= new JTextField("Experimenter"),
		subId = new JTextField("Subject"),
		scr= new JTextField("scripts/demoscriptv1.txt");
	
    JFileChooser fc = new JFileChooser(System.getProperty("user.dir")+"/scripts");
	
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
		
		openButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {

		        //Handle open button action.
		            int returnVal = fc.showOpenDialog(ScriptWindow.this);

		            if (returnVal == JFileChooser.APPROVE_OPTION) {
		                File scriptFile = fc.getSelectedFile();
		                openButton.setText(scriptFile.getName());
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
				gm.setInputScript("scripts/"+openButton.getText());
				
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

		// this pauses or resumes the game so the subject can take a break if needed
		pause.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				if (pause.getText().equals("pause")) {
					gm.pause();
					pause.setText("resume");
				} else {
					gm.restart();
					pause.setText("pause");
				}
				
			}
		});

		// Finally we do the layout of the components
		scriptpanel = new JPanel();
		scriptpanel.setLayout(new GridLayout(3,1));
		scriptpanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Main Control") );
		scriptpanel.add(subId);
		scriptpanel.add(expId);
		//scriptpanel.add(new JLabel("ScriptFile: "));
		scriptpanel.add(openButton);
		scriptpanel.add(start);
		scriptpanel.add(pause);
		scriptpanel.add(sdone);
		
		scriptpanel.setBackground(Color.gray);
		this.add(scriptpanel);
	}

}
