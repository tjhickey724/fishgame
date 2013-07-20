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
	JComboBox vidtype,stype,vol;
	String[] speeds,videotypes,soundtypes,volumes;
	JButton gdone,gen;
	JScrollPane jscrollpane;
	JTextArea jtextarea;	
	JTextField goodVisualHzTF = new JTextField("6");
	JTextField badVisualHzTF = new JTextField("8");
	
	
	JLabel minSizeLab = new JLabel("Min Size(%)");
	JTextField minSizeTF = new JTextField("100");
	JLabel maxSizeLab = new JLabel("Max Size(%)");
	JTextField maxSizeTF = new JTextField("120");
	
	JButton goodSoundTF = new JButton("sounds/6hz");
	JButton badSoundTF = new JButton("sounds/8hz");
	JButton imageSelect = new JButton("Open");
	
	JTextField numactors = new JTextField("7");
	
	ScriptGenerator sgen = new ScriptGenerator();
	
	JFileChooser fc;
	public GenerateWindow(){
		super("Generate Window");
		setLayout(new GridLayout(5,1));
		setSize(300,600);
		
		JPanel matrix1,matrix2,matrix3,matrix4,col1,col2,col3,row1,row2,row3;
		JLabel actorspecies,good,bad,sound,visual,soundType,videoType,mintime,maxtime,nof;

		String currentDir = System.getProperty("user.dir");
        System.out.println("Current dir using System:" +currentDir);
		fc=new JFileChooser(currentDir+"/sounds");
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);


		/*
		 * all of these initializations should be done as the variables are being
		 * declared. That makes it clear what the variables are and it simplifies the
		 * body of the constructor.  REFACTOR....
		 */
		actorspecies=new JLabel("Fish Type:");
		good=new JLabel("Good");
		bad=new JLabel("Bad");
		sound=new JLabel("Sound");
		visual=new JLabel("Visual");
		soundType=new JLabel("Sound Type: ");
		videoType=new JLabel("Video Type: ");
		mintime=new JLabel("Min Time: ");
		maxtime= new JLabel("Max Time: ");
		nof=new JLabel("Fish to generate:");
		JLabel imagelabel = new JLabel("Select Image:");
		JLabel volLabel = new JLabel("Volume:");
		
		jtextarea=new JTextArea(5,20);
		jscrollpane=new JScrollPane(jtextarea);
		
		speeds=new String[]{"none","slow","fast"};
		videotypes=new String[]{"Throb","Flicker"};
	
		vidtype = new JComboBox(videotypes);

		soundtypes=new String[]{"Stereo","Mono"};
		stype = new JComboBox(soundtypes);
		
		volumes=new String[]{"low","med","hi"};
		vol = new JComboBox(volumes);
		
		gdone=new JButton("Done");
		gen=new JButton("Generate");
		
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
				gs.stereo = (stype.getSelectedItem().toString().equals("Stereo"));
				gs.good.throbRate = (int) Integer.parseInt(goodVisualHzTF.getText());
				gs.bad.throbRate = (int) Integer.parseInt(badVisualHzTF.getText());	
				gs.maxThrobSize = (int) Integer.parseInt(maxSizeTF.getText());
				gs.minThrobSize = (int) Integer.parseInt(minSizeTF.getText());
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
		            int returnVal = fc.showOpenDialog(GenerateWindow.this);

		            if (returnVal == JFileChooser.APPROVE_OPTION) {
		                File goodsoundfile = fc.getSelectedFile();
		                goodSoundTF.setText("sounds/"+goodsoundfile.getName());
		            }}});
		
		badSoundTF.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {

		        //Handle open button action.
		            int returnVal = fc.showOpenDialog(GenerateWindow.this);

		            if (returnVal == JFileChooser.APPROVE_OPTION) {
		                File badsoundfile = fc.getSelectedFile();
		                badSoundTF.setText("sounds/"+badsoundfile.getName());
		            }}});
		
		imageSelect.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {

		        //Handle open button action.
		            int returnVal = fc.showOpenDialog(GenerateWindow.this);

		            if (returnVal == JFileChooser.APPROVE_OPTION) {
		                File imagefile = fc.getSelectedFile();
		                imageSelect.setText("images/"+imagefile.getName());
		            }}});
		
		
		col1 = new JPanel();
		col1.setLayout(new GridLayout(3,1));
		col2 = new JPanel();
		col2.setLayout(new GridLayout(3,1));
		col3 = new JPanel();
		col3.setLayout(new GridLayout(3,1));
		
		row1 = new JPanel();
		row1.setLayout(new GridLayout(1,3));
		row2 = new JPanel();
		row2.setLayout(new GridLayout(1,3));
		row3 = new JPanel();
		row3.setLayout(new GridLayout(1,3));
		
		matrix1 = new JPanel();
		matrix1.setLayout(new GridLayout(1,3));
		matrix2 = new JPanel();
		matrix2.setLayout(new GridLayout(3,1));
		
		matrix3 = new JPanel();
		matrix3.setLayout(new GridLayout(5,2));
		
		matrix4 = new JPanel();
		matrix4.setLayout(new GridLayout(2,2));
		
		col1.add(actorspecies);
		col1.add(good);
		col1.add(bad);
		
		col2.add(sound);
		//col2.add(gs);
		//col2.add(bs);
		col2.add(this.goodSoundTF);
		col2.add(this.badSoundTF);
		
		col3.add(visual);
		//col3.add(gv);
		//col3.add(bv);
		col3.add(this.goodVisualHzTF);
		col3.add(this.badVisualHzTF);
		
		col1.setBackground(Color.cyan);
		col2.setBackground(Color.cyan);
		col3.setBackground(Color.cyan);
		
		
		row1.add(soundType);
		row1.add(stype);
		
		row2.add(videoType);
		row2.add(vidtype);
		
		row3.add(gen);
		row3.add(gdone);
		
		

		matrix1.add(col1);
		matrix1.add(col2);
		matrix1.add(col3);
		
		matrix2.add(row1);
		matrix2.add(row2);
		matrix2.add(row3);
		
		

		matrix3.setBorder(javax.swing.BorderFactory.createTitledBorder("Gen and Vis") );
		matrix3.add(mintime);
		matrix3.add(mintim);
		matrix3.add(maxtime);
		matrix3.add(maxtim);
		matrix3.add(minSizeLab);
		matrix3.add(minSizeTF);
		matrix3.add(maxSizeLab);
		matrix3.add(maxSizeTF);
		matrix3.add(nof);
		matrix3.add(numactors);
		matrix1.setBackground(Color.red);
		matrix2.setBackground(Color.green);
		matrix3.setBackground(Color.blue);
		
		//matrix4.add(imagelabel);
		//matrix4.add(imageSelect);
		matrix4.add(volLabel);
		matrix4.add(vol);
		
		
		add(matrix1);
		add(matrix3);
		add(matrix2);
		add(matrix4);
		add(jscrollpane);
	}
}
