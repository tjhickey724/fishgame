/**
 * 
 */
package game;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.*;

/**
 * @author mike
 *
 */
public class ScriptWindow extends JFrame {
	JTextField expId,subId,scr;
	JPanel scriptpanel;
	JLabel scrip,currentactor;
	JButton start,restart,stop,pause,sdone,runscript;
	GameModel gm;
	
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
		pause=new JButton("Pause");
		restart=new JButton("Restart");
		stop = new JButton("Stop");
		sdone=new JButton("Done");
		
		sdone.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				paramsui.setVisible(true);
				setVisible(false);
				paramsui.gw.setVisible(false);
				gm.stop();
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
					paramsui.gm.logfile.write("Version:                "+"1.0 (7/11/2013)" + "\n" +
									 "Experimenter:           "+ ExperimenterID + "\n" + 
				                     "Subject:                " + SubjectID + "\n" + 
				                     "Date:                   "+ (new java.util.Date()).toString()+"\n"
				                 );
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
		stop.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				gm.stop();
			}
		});
		
		pause.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				gm.pause();
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
		scriptpanel.add(restart);
		scriptpanel.add(stop);
		scriptpanel.add(pause);
		scriptpanel.add(sdone);
		scriptpanel.setBackground(Color.gray);
		this.add(scriptpanel);
	}

}
