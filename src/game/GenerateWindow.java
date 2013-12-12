/**
 * 
 */
package game;

import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * This creates a window that allow the experimenter to create a session script
 * that will be used to run experiments.
 * 
 * @author mike
 * 
 */

// REFACTOR: this code has not been refactored yet.


public class GenerateWindow extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1635763426843293108L;
	public int interval[] = { 35, 45, 55 };
	JTextField int1 = new JTextField("" + interval[0]), int3 = new JTextField(
			"" + interval[2]), int2 = new JTextField("" + interval[1]),
			maxBrightnessTF = new JTextField("24"),
			minBrightnessTF = new JTextField("0"),
			numCongruent = new JTextField("10"),
			numInCongruent = new JTextField("10"),
			numNeutral = new JTextField("10"),
	        numConstatntVisual=new JTextField("10");
	Font font1 = new Font("SansSerif", Font.BOLD, 10);
	
	JComboBox soundtype, vol, tiedto;
	JTextArea jtextarea = new JTextArea(5, 20);
	JTextField goodVisualHzTF = new JTextField("6");
	JTextField badVisualHzTF = new JTextField("8");
	JTextField nonModulatedVisual = new JTextField("0");
	Checkbox hasAvatar = new Checkbox();

	JTextField minSizeTF = new JTextField("100");

	JTextField maxSizeTF = new JTextField("120");

	JButton goodSoundTF = new JButton("sounds/6hz");
	JButton badSoundTF = new JButton("sounds/8hz");
	JButton nonModulatedSound=new JButton("sounds/...");
	JButton imageSelect = new JButton("Open");

	ScriptGenerator sgen = new ScriptGenerator();

	JFileChooser fc;
  
	public GenerateWindow() {
		super("Generate Window");
		
		setLayout(new GridLayout(4, 1));
		setSize(300, 800);

		JPanel matrix1, matrix2, matrix3;

		String currentDir = System.getProperty("user.dir");
		System.out.println("Current dir using System:" + currentDir);
		fc = new JFileChooser(currentDir + "/sounds");
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		/*
		 * all of these initializations should be done as the variables are
		 * being declared. That makes it clear what the variables are and it
		 * simplifies the body of the constructor. REFACTOR....
		 */

		JLabel volLabel = new JLabel("Volume:");

		JScrollPane jscrollpane = new JScrollPane(jtextarea);

		String[] soundtypes = new String[] { "Stereo", "Mono" };
		soundtype = new JComboBox(soundtypes);

		String[] volumes = new String[] { "low", "med", "hi" };
		vol = new JComboBox(volumes);

		String[] modes = new String[] { "Visual", "Auditory" };
		tiedto = new JComboBox(modes);

		JButton gdone = new JButton("Done");
		JButton gen = new JButton("Generate");

		gen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// here we create a GameSpec and use the user input
				// to set the appropriate fields of the GameSpec object
				GameSpec gs = new GameSpec();
				interval[0] = Integer.parseInt(int1.getText());
				interval[1] = Integer.parseInt(int2.getText());
				interval[2] = Integer.parseInt(int3.getText());
				gs.interval = interval;
				gs.numCon = Integer.parseInt(numCongruent.getText());
				gs.numIncon = Integer.parseInt(numInCongruent.getText());
				gs.numNeutral = Integer.parseInt(numNeutral.getText());
				gs.good.update("soundFile", goodSoundTF.getText());
				gs.bad.update("soundFile", badSoundTF.getText());
				gs.update("nonModulatedSound",nonModulatedSound.getText());
				gs.stereo = (soundtype.getSelectedItem().toString()
						.equals("Stereo"));
				gs.good.throbRate = (int) Integer.parseInt(goodVisualHzTF
						.getText());
				gs.bad.throbRate = (int) Integer.parseInt(badVisualHzTF
						.getText());
				gs.maxThrobSize = (int) Integer.parseInt(maxSizeTF.getText());
				gs.minThrobSize = (int) Integer.parseInt(minSizeTF.getText());
				gs.maxBrightness = (int) Integer.parseInt(maxBrightnessTF
						.getText());
				gs.minBrightness = (int) Integer.parseInt(minBrightnessTF
						.getText());
				gs.avmode = (tiedto.getSelectedItem().toString()
						.equals("Visual") ? 0 : 1);
				gs.hasAvatar = hasAvatar.getState();

				String volumeLevel = (vol.getSelectedItem()).toString();
				if (volumeLevel.equals("low")) {
					gs.bgSound = "water1.wav";
				} else if (volumeLevel.equals("med")) {
					gs.bgSound = "water2.wav";
				} else {
					gs.bgSound = "water3.wav";
				}
				gs.bgSound = "sounds/background/" + gs.bgSound;

				System.out.println(gs.toScript());
				sgen.generate(gs);

				// here we generate a report for the user, so they know what's
				// happening
				jtextarea.append(gs.toScript());
				// jtextarea.append("GENERATE " + gs.totalTrials + " events\n");

			}
		});

		gdone.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sgen.close();
				setVisible(false);
			}
		});

		goodSoundTF.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				// Handle open button action.
				int returnVal = fc.showOpenDialog(GenerateWindow.this);

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File goodsoundfile = fc.getSelectedFile();
					goodSoundTF.setText("sounds/" + goodsoundfile.getName());
				}
			}
		});

		badSoundTF.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				// Handle open button action.
				int returnVal = fc.showOpenDialog(GenerateWindow.this);

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File badsoundfile = fc.getSelectedFile();
					badSoundTF.setText("sounds/" + badsoundfile.getName());
				}
			}
		});
		nonModulatedSound.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				// Handle open button action.
				int returnVal = fc.showOpenDialog(GenerateWindow.this);

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File nonmodulatedsound = fc.getSelectedFile();
					nonModulatedSound.setText("sounds/" + nonmodulatedsound.getName());
				}
			}
		});
		imageSelect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				// Handle open button action.
				int returnVal = fc.showOpenDialog(GenerateWindow.this);

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File imagefile = fc.getSelectedFile();
					imageSelect.setText("images/" + imagefile.getName());
				}
			}
		});

		matrix1 = new JPanel();
		matrix1.setLayout(new GridLayout(4, 3));
		matrix2 = new JPanel();
		matrix2.setLayout(new GridLayout(6, 2));

		matrix3 = new JPanel();

		matrix3.setLayout(new GridLayout(9, 2));

		matrix1.add(new JLabel("Fish Type:"));
		matrix1.add(new JLabel("Sound"));
		matrix1.add(new JLabel("Visual"));

		matrix1.add(new JLabel("Good"));
		matrix1.add(this.goodSoundTF);
		matrix1.add(this.goodVisualHzTF);

		matrix1.add(new JLabel("Bad"));
		matrix1.add(this.badSoundTF);
		matrix1.add(this.badVisualHzTF);
		matrix1.add(new JLabel("Neutral"));
		matrix1.add(this.nonModulatedSound);
		matrix1.add(nonModulatedVisual);
		
		// col2.add(gs);
		// col2.add(bs);

		// col3.add(gv);
		// col3.add(bv);

		matrix2.add(new JLabel("Sound Type: "));
		matrix2.add(soundtype);
		matrix2.add(new JLabel("Tied to"));
		matrix2.add(tiedto);

		matrix2.add(volLabel);
		matrix2.add(vol);
		matrix2.add(new JLabel("Max Brightness"));
		matrix2.add(maxBrightnessTF);
		matrix2.add(new JLabel("MainBrightness"));
		matrix2.add(minBrightnessTF);
		matrix2.add(gen);
		matrix2.add(gdone);

		matrix3.setBorder(javax.swing.BorderFactory
				.createTitledBorder("Gen and Vis"));
		matrix3.add(new JLabel("Interval 1"));
		matrix3.add(int1);
		matrix3.add(new JLabel("Interval 2"));
		matrix3.add(int2);
		matrix3.add(new JLabel("Interval 3"));
		matrix3.add(int3);
		matrix3.add(new JLabel("Min Size(%)"));
		matrix3.add(minSizeTF);
		matrix3.add(new JLabel("Max Size(%)"));
		matrix3.add(maxSizeTF);
		matrix3.add(new JLabel("NumCongruent"));
		matrix3.add(numCongruent);
		matrix3.add(new JLabel("NumIncongruent"));
		matrix3.add(this.numInCongruent);
		matrix3.add(new JLabel("Num Neutral"));
		matrix3.add(this.numNeutral);
       
		matrix3.add(new JLabel("Avatar?"));
		matrix3.add(hasAvatar);

		matrix2.setBackground(Color.green);
		matrix3.setBackground(Color.pink);
		matrix1.setBackground(Color.cyan);

		add(matrix1);
		add(matrix3);
		add(matrix2);
		add(jscrollpane);
	}
	
}
