package game;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

public class ExperimenterWindow extends JFrame {

		JButton scripted,generate;
		GameModel gm;
		ScriptWindow sw;
		GenerateWindow gw;
		SubjectWindow gameView;
		JPanel buttonPanel;
	
	public ExperimenterWindow(final GameModel gm, final SubjectWindow gameView) {
	
		super("Experimenter Window");
		this.gameView=gameView;
		this.gm=gm;
		this.setSize(500,500);
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
	
	
		sw = new ScriptWindow(this);
		gw=new GenerateWindow(this);
		buttonPanel = new JPanel();
		
		//Button to view script window
		scripted=new JButton("Run from Script");
		scripted.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				setVisible(false);
				sw.setVisible(true);
				gw.setVisible(false);
			}
		});
		//Button to view generate window
		generate=new JButton("Generate Script");
		generate.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				setVisible(false);
				sw.setVisible(false);
				gw.setVisible(true);
			}
		});
		buttonPanel.setLayout(new GridLayout(1,2));
		buttonPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Sekuler Lab"));
		buttonPanel.add(scripted);
		buttonPanel.add(generate);
		this.add(buttonPanel);
		this.pack();
	}

}
