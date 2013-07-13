package game;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

public class ExperimenterWindow extends JFrame {

			/**
	 * 
	 * THIS NEEDS TO BE REFACTORED AS FOLLOWS
	 * CREATE TWO NEW Classes, 
	 *    ScriptWindow and GenerateWindow
	 * which both extend JFrame. The ParamsUI class then calls constructors
	 * to create these two windows (as you've done here) and hides/shows them
	 * as appropriate. You'll also need to pass it "this" to the ScriptWindow and GenerateWindow constructors
	 * so when the user presses done on those, they can call paramsUI.show();
	 * 
	 * The advantage of this approach is that it will break one very complex class into three simpler classes.
	 * Also, the only components which should be instqance variables are those that are read from or written to in a listener
	 * Otherwise, they should be local variables of the constructor so they don't clutter up the class.
	 * 
	 * 
	 */

		


	
		JButton scripted,generate;
		GameModel gm;
		JScrollPane jsp;
		ScriptWindow sw;
		GenerateWindow gw;
		DrawDemo gameView;

		
	
	public ExperimenterWindow(final GameModel gm, final DrawDemo gameView) {
	
		super("Parameters");
		this.gameView=gameView;
		this.gm=gm;
		this.setSize(500,500);
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		this.setLayout(new GridLayout(2,1));
	
		sw = new ScriptWindow(this);
		gw=new GenerateWindow(this);
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
		this.add(scripted);
		this.add(generate);
		this.pack();
	}
	
	public void startGame(){
		
	}
}
