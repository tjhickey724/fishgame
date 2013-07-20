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
 * @author mike
 *
 */
public class ScriptWindow extends JFrame {
	JTextField expId,subId,scr;
	JPanel scriptpanel;
	JLabel scrip,currentactor,selectedFile;
	JButton start,restart,pause,sdone,runscript,openButton;
	GameModel gm;
    JFileChooser fc;
	
	public ScriptWindow(final ExperimenterWindow paramsui){
		super("Script Window");
		this.gm=paramsui.gm;
		setSize(300,250);
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
				paramsui.setVisible(true);
				setVisible(false);
				paramsui.gw.setVisible(false);
				gm.stop();
				System.exit(0); // this is harsh, but I'll work on it...
			}
		});
		
		start.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				
				GameActor.GAME_START = System.nanoTime();

				gm.inputScriptFileName=scr.getText();
				String SubjectID=subId.getText();
				String ExperimenterID=expId.getText();

				try {
					gm.writeToLog("Version:                "+RunGame.versionNum);
					gm.writeToLog("Experimenter:           "+ ExperimenterID);
					gm.writeToLog("Subject:                " + SubjectID);
					gm.writeToLog("Date:                   "+ (new java.util.Date()).toString());
					gm.logfile.flush();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				gm.start();
				GameLoop gl = new GameLoop(gm,paramsui.gameView.gameboard);
				Thread t = new Thread(gl);
				t.start();

			
			}
		});

		
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
		
		restart.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				gm.restart();
			}
		});
		
		
		
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
		scriptpanel.add(openButton);
		scriptpanel.add(selectedFile);
		scriptpanel.setBackground(Color.gray);
		this.add(scriptpanel);
	}

}
