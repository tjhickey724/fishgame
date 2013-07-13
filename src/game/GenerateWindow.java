/**
 * 
 */
package game;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * @author mike
 *
 */
public class GenerateWindow extends JFrame {
	JPanel matrix1,matrix2,matrix3,col1,col2,col3,row1,row2,row3;
	JTextField mintim,maxtim,numactors;
	JLabel actorspecies,good,bad,sound,visual,soundType,videoType,mintime,maxtime,nof;
	JComboBox vidtype,stype;
	String[] speeds,videotypes,soundtypes;
	JButton gdone,gen;
	JScrollPane jscrollpane;
	JTextArea jtextarea;
	
	
	JTextField goodVisualHzTF = new JTextField("6");
	JTextField badVisualHzTF = new JTextField("8");
	
	
	JLabel minSizeLab = new JLabel("Min Size(%)");
	JTextField minSizeTF = new JTextField("100");
	JLabel maxSizeLab = new JLabel("Max Size(%)");
	JTextField maxSizeTF = new JTextField("120");
	
	JTextField goodSoundTF = new JTextField("sounds/fish6hz0p");
	JTextField badSoundTF = new JTextField("sounds/fish8hz0p");
	

	public GenerateWindow( final ExperimenterWindow paramsui){
		super("Generate Window");
		setLayout(new GridLayout(4,1));
		setSize(300,600);
		

		mintim = new JTextField("20");
		maxtim = new JTextField("60");
		numactors= new JTextField("20");
		
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
		
		jtextarea=new JTextArea(5,20);
		jscrollpane=new JScrollPane(jtextarea);
		
		speeds=new String[]{"none","slow","fast"};
		videotypes=new String[]{"Throb","Flicker"};
	
		vidtype = new JComboBox(videotypes);

		soundtypes=new String[]{"Stereo","Mono"};
		stype = new JComboBox(soundtypes);
		
		gdone=new JButton("Done");
		gen=new JButton("Generate");
		
		gen.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){

			}
		});
		
		gdone.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				paramsui.setVisible(true);
				paramsui.sw.setVisible(false);
				setVisible(false);
			}
		});
		
		
		
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
		
		add(matrix1);
		add(matrix3);
		add(matrix2);
		add(jscrollpane);
	}
}
