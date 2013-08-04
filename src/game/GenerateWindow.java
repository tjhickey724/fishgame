/**
 * 
 */
package game;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
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
 * @author mike
 *
 */
public class GenerateWindow extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1635763426843293108L;
	
	
	JTextField 
	    mintim = new JTextField("30"),
	    maxtim = new JTextField("80");
	JComboBox soundtype,vol;
	JTextArea jtextarea=new JTextArea(5,20);	
	JTextField goodVisualHzTF = new JTextField("6");
	JTextField badVisualHzTF = new JTextField("8");
	
	
	JTextField minSizeTF = new JTextField("100");
	JTextField backgroundSpeed = new JTextField("0.1");
	JTextField cWidth = new JTextField("80");
	JTextField maxSizeTF = new JTextField("120");
	
	JButton goodSoundTF = new JButton("sounds/6hz");
	JButton badSoundTF = new JButton("sounds/8hz");
	JButton imageSelect = new JButton("images/stream.jpg");
	
	JTextField numactors = new JTextField("7");
	
	ScriptGenerator sgen = new ScriptGenerator();
	
	//filechooser for sounds
	JFileChooser soundfc;
	//filechooser for images
	JFileChooser imagefc;
	public GenerateWindow(){
		super("Generate Window");
		setLayout(new GridLayout(5,1));
		setSize(300,600);
		
		JPanel matrix1,matrix2,matrix3,matrix4;

		String currentDir = System.getProperty("user.dir");
        System.out.println("Current dir using System:" +currentDir);
		soundfc=new JFileChooser(currentDir+"/sounds");
		imagefc = new JFileChooser (currentDir+"/images");
		soundfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);


		/*
		 * all of these initializations should be done as the variables are being
		 * declared. That makes it clear what the variables are and it simplifies the
		 * body of the constructor.  REFACTOR....
		 */

		
		
		
		JScrollPane jscrollpane=new JScrollPane(jtextarea);
		
		String[] videotypes=new String[]{"Throb","Flicker"};
	
		JComboBox videotype = new JComboBox(videotypes);

		String[] soundtypes=new String[]{"Stereo","Mono"};
		soundtype = new JComboBox(soundtypes);
		
		String[] volumes=new String[]{"low","med","hi"};
		vol = new JComboBox(volumes);
		
		JButton gdone=new JButton("Done");
		JButton gen=new JButton("Generate");
		
		gen.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				// here we create a GameSpec and use the user input
				// to set the appropriate fields of the GameSpec object
				GameSpec gs = new GameSpec();
				gs.minFishRelease = (int) Integer.parseInt(mintim.getText());
				gs.maxFishRelease = (int) Integer.parseInt(maxtim.getText());
				gs.good.soundFile = goodSoundTF.getText();
				gs.bad.soundFile = badSoundTF.getText();
				gs.stereo = (soundtype.getSelectedItem().toString().equals("Stereo"));
				gs.good.throbRate = (int) Integer.parseInt(goodVisualHzTF.getText());
				gs.bad.throbRate = (int) Integer.parseInt(badVisualHzTF.getText());	
				gs.maxThrobSize = (int) Integer.parseInt(maxSizeTF.getText());
				gs.minThrobSize = (int) Integer.parseInt(minSizeTF.getText());
				gs.backgroundSpeed = (double) Double.parseDouble(backgroundSpeed.getText());
				gs.channelWidth = (int) Integer.parseInt(cWidth.getText());
				gs.backgroundImage = imageSelect.getText();
				String volumeLevel = (vol.getSelectedItem()).toString();
			    if (volumeLevel.equals("low")) {
			    	gs.bgSound = "water1.wav";
			    }else if (volumeLevel.equals("med")){
			    	gs.bgSound = "water2.wav";
			    } else {
			    	gs.bgSound = "water3.wav";
			    }
			    gs.bgSound = "sounds/background/" + gs.bgSound;
				

						
				int numberOfFish = Integer.parseInt(numactors.getText());
				System.out.println(gs.toScript());
				sgen.generate(gs,numberOfFish);
				
				// here we generate a report for the user, so they know what's happening
				jtextarea.append(gs.toScript());
				jtextarea.append("GENERATE "+numberOfFish+" events\n");
				

			}
		});
		
		gdone.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				sgen.close();
				setVisible(false);
			}
		});
		

		
		goodSoundTF.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {

		        //Handle open button action.
		            int returnVal = soundfc.showOpenDialog(GenerateWindow.this);

		            if (returnVal == JFileChooser.APPROVE_OPTION) {
		                File goodsoundfile = soundfc.getSelectedFile();
		                goodSoundTF.setText("sounds/"+goodsoundfile.getName());
		            }}});
		
		badSoundTF.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {

		        //Handle open button action.
		            int returnVal = soundfc.showOpenDialog(GenerateWindow.this);

		            if (returnVal == JFileChooser.APPROVE_OPTION) {
		                File badsoundfile = soundfc.getSelectedFile();
		                badSoundTF.setText("sounds/"+badsoundfile.getName());
		            }}});
		
		imageSelect.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {

		        //Handle open button action.
		            int returnVal = imagefc.showOpenDialog(GenerateWindow.this);

		            if (returnVal == JFileChooser.APPROVE_OPTION) {
		                File imagefile = imagefc.getSelectedFile();
		                imageSelect.setText("images/"+imagefile.getName());
		            }}});
		
		
		matrix1 = new JPanel();
		matrix1.setLayout(new GridLayout(3,3));
		matrix2 = new JPanel();
		matrix2.setLayout(new GridLayout(4,2));
		
		matrix3 = new JPanel();
		matrix3.setLayout(new GridLayout(5,2));
		
		matrix4 = new JPanel();
		matrix4.setLayout(new GridLayout(3,2));
		
		matrix1.add(new JLabel("Fish Type:"));
		matrix1.add(new JLabel("Sound"));
		matrix1.add(new JLabel("Visual"));
		
		matrix1.add(new JLabel("Good"));
		matrix1.add(this.goodSoundTF);
		matrix1.add(this.goodVisualHzTF);
		
		matrix1.add(new JLabel("Bad"));
		matrix1.add(this.badSoundTF);
		matrix1.add(this.badVisualHzTF);
		//col2.add(gs);
		//col2.add(bs);
		
	
		
		
		//col3.add(gv);
		//col3.add(bv);
		
		
		
		
		
		


		
		

		
		matrix3.add(new JLabel("Min Time: "));
		matrix3.add(mintim);
		matrix3.add(new JLabel("Max Time: "));
		matrix3.add(maxtim);
		matrix3.add(new JLabel("Min Size(%)"));
		matrix3.add(minSizeTF);
		matrix3.add(new JLabel("Max Size(%)"));
		matrix3.add(maxSizeTF);
		matrix3.add(new JLabel("Fish to generate:"));
		matrix3.add(numactors);
		

				
		matrix2.add(new JLabel("Sound Type: "));
		matrix2.add(soundtype);
		
		matrix2.add(new JLabel("Video Type: "));
		matrix2.add(videotype);
		matrix2.add(new JLabel("Volume:"));
		matrix2.add(vol);
		matrix2.add(gen);
		matrix2.add(gdone);
		
		
		matrix4.add(new JLabel("Background speed:"));
		matrix4.add(backgroundSpeed);
		matrix4.add(new JLabel("Channel Width:"));
		matrix4.add(cWidth);
		matrix4.add(new JLabel("Background:"));
		matrix4.add(imageSelect);
		
		matrix2.setBackground(Color.pink);
		matrix3.setBackground(Color.pink);
		matrix1.setBackground(Color.pink);
		matrix4.setBackground(Color.pink);

		
		add(matrix1);
		add(matrix3);
		add(matrix4);
		add(matrix2);
		add(jscrollpane);
	}
}
