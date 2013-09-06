package game;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

/**
 * this creates two buttons one to open the "run from script" window the other
 * to open the "generate script" window
 * 
 * @author tim
 * 
 */
public class ExperimenterWindow extends JFrame {

	JButton scripted, generate;
	ScriptWindow sw;
	GenerateWindow gw;
	JPanel buttonPanel;

	public ExperimenterWindow() {

		super("Experimenter Window");
		this.setSize(500, 500);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		buttonPanel = new JPanel();

		// Button to view script window
		scripted = new JButton("Run from Script");
		scripted.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// setVisible(false);
				sw = new ScriptWindow();
				sw.setVisible(true);
			}
		});

		// Button to view generate window
		generate = new JButton("Generate Script");
		generate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				gw = new GenerateWindow();
				gw.setVisible(true);
			}
		});
		buttonPanel.setLayout(new GridLayout(1, 2));
		buttonPanel.setBorder(javax.swing.BorderFactory
				.createTitledBorder("Sekuler Lab"));
		buttonPanel.add(scripted);
		buttonPanel.add(generate);
		this.add(buttonPanel);
		this.pack();
	}

}
