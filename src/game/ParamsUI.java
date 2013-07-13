package game;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

public class ParamsUI extends JFrame {

			/**
	 * The script textbox could be bigger
	 */
	private static final long serialVersionUID = 1L;
		JLabel header,actorspecies,good,bad,good1,bad1,sound,visual,subjectid,experimenterid,soundType,videoType,mintime,maxtime,scrip;
		JLabel hit,miss,total,ghit,gmiss,bhit,bmiss,gtot,btot,htot,mtot,tot,nof,currentactor;
		JPanel matrix1,matrix2,matrix3,matrix4,matrix5,matrix6;
		JButton button1;
		JPanel col1,col2,col3,row1,row2,row3;
		int slow,fast,min,max,gh,gms,bh,bm,gt,bt,mt,ht,t;
		int[] times;
		String[] speeds,videotypes,soundtypes;
		JComboBox vidtype,stype;
		String SubjectID,ExperimenterID,script,type;
		JTextField expId,subId,scr,mintim,maxtim,numactors;
		JButton start,stop,pause,restart,gen,sdone,gdone,scripted,generate;
		GameModel gm;
		JScrollPane jsp;
		JFrame scriptedscreen,generatescreen;
		
		JTextField goodVisualHzTF = new JTextField("6");
		JTextField badVisualHzTF = new JTextField("8");
		
		
		JLabel minSizeLab = new JLabel("Min Size(%)");
		JTextField minSizeTF = new JTextField("100");
		JLabel maxSizeLab = new JLabel("Max Size(%)");
		JTextField maxSizeTF = new JTextField("120");
		
		JTextField goodSoundTF = new JTextField("sounds/fish6hz0p");
		JTextField badSoundTF = new JTextField("sounds/fish8hz0p");
		
	
		
	
	public ParamsUI(final GameModel gm, final DrawDemo gameView) {
		
		super("Parameters");
		this.gm=gm;
		this.setSize(500,500);
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		this.setLayout(new GridLayout(2,1));
		
		scriptedscreen=new JFrame("Run from Script");
		scriptedscreen.setSize(300,250);
		
		
		generatescreen=new JFrame("Generate Script");
		generatescreen.setLayout(new GridLayout(3,1));
		generatescreen.setSize(300,600);
		
		expId= new JTextField("Experimenter");
		subId = new JTextField("Subject");
		scr= new JTextField("scripts/demoscript15.txt");
		mintim = new JTextField("20");
		maxtim = new JTextField("60");
		numactors= new JTextField("20");
		
		header = new JLabel("Settings");
		actorspecies=new JLabel("Fish Type:");
		good=new JLabel("Good");
		bad=new JLabel("Bad");
		good1=new JLabel("Good");
		bad1=new JLabel("Bad");
		sound=new JLabel("Sound");
		visual=new JLabel("Visual");
		soundType=new JLabel("Sound Type: ");
		videoType=new JLabel("Video Type: ");
		mintime=new JLabel("Min Time: ");
		maxtime= new JLabel("Max Time: ");
		scrip = new JLabel("ScriptFile: ");	
		nof=new JLabel("Fish to generate:");
		currentactor=new JLabel("Current Fish #:");
		
		
		
		hit= new JLabel("Hits");
		miss = new JLabel("Misses:");
		ghit = new JLabel(gh+"");
		bhit = new JLabel(bh+"");
		gmiss = new JLabel(gms+"");
		bmiss = new JLabel(bm+"");
		gtot = new JLabel(gt+"");
		btot = new JLabel(bt+"");
		htot = new JLabel(ht+"");
		mtot = new JLabel(mt+"");
		tot = new JLabel(t+"");
		total = new JLabel("Total");
		
		
		min = 20;
		max = 60;

		
		
	

		
		speeds=new String[]{"none","slow","fast"};
		videotypes=new String[]{"Throb","Flicker"};
	
		vidtype = new JComboBox(videotypes);

		soundtypes=new String[]{"Stereo","Mono"};
		stype = new JComboBox(soundtypes);
		
		JTextArea textArea = new JTextArea(5, 20);
		jsp = new JScrollPane(textArea); 
		textArea.setEditable(true);
		
		
		pause=new JButton("Pause");
		restart=new JButton("Restart");
		scripted=new JButton("Run from Script");
		sdone=new JButton("Done");
		gdone=new JButton("Done");
		gen=new JButton("Generate");


		
		scripted.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				setVisible(false);
				scriptedscreen.setVisible(true);
				generatescreen.setVisible(false);
			}
		});
		
		generate=new JButton("Generate Script");
		generate.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				setVisible(false);
				scriptedscreen.setVisible(false);
				generatescreen.setVisible(true);
			}
		});
		

		gen.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){

			}
		});
		
		gdone.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				setVisible(true);
				scriptedscreen.setVisible(false);
				generatescreen.setVisible(false);
			}
		});
		
		sdone.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				setVisible(true);
				scriptedscreen.setVisible(false);
				generatescreen.setVisible(false);
			}
		});
		
		// the start button will look at the values of the params interface and change the game model accordingly
		start= new JButton("start");

		start.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				
				GameActor.GAME_START = System.nanoTime();
				
				gm.stereo = (stype.getSelectedItem().toString()=="Stereo");
				
				gm.mRate = (int) Double.parseDouble(mintim.getText());
				gm.sRate = (int) Double.parseDouble(maxtim.getText());
				
				gm.visualMin = (int) Double.parseDouble(minSizeTF.getText());
				gm.visualMax = (int) Double.parseDouble(maxSizeTF.getText());
			
				gm.inputScriptFileName=scr.getText();
				SubjectID=subId.getText();
				ExperimenterID=expId.getText();
				gm.scripted=true;
				gm.goodFishSounds = goodSoundTF.getText();
				gm.badFishSounds = badSoundTF.getText();
				
				
				gm.goodvisualhz = (int)Double.parseDouble(goodVisualHzTF.getText());
				gm.badvisualhz = (int)Double.parseDouble(badVisualHzTF.getText());
			
				
				System.out.println(gm.badaudiohz);
				System.out.println(gm.badvisualhz);
				System.out.println(gm.goodaudiohz);
				System.out.println(gm.goodvisualhz);
			
				System.out.println(script);
				System.out.println(gm.mRate);
				try {
					gm.logfile.write("Version:                "+"1.0 (7/11/2013)" + "\n" +
									 "Experimenter:           "+ ExperimenterID + "\n" + 
				                     "Subject:                " + SubjectID + "\n" + 
				                     "Date:                   "+ (new java.util.Date()).toString()+"\n"+
							         "Game Type:              "+ type + "\n"+
				                     "Script File:            " + gm.inputScriptFileName + "\n"+
				                     "Good Sounds:            "+ gm.goodFishSounds + "\n"+
							         "Bad Sounds:             " + gm.badFishSounds +"\n"+
				                     "Good Visual Hertz:      " +gm.goodvisualhz +"\n"+
				                     "Bad Visual Hertz:       " +gm.badvisualhz +"\n"+
				                     "Visual Min Scale:       "+ gm.visualMin + "\n"+ 
				                     "Visual Max Scale:       "+ gm.visualMin + "\n"+
				                     "Min Delay:              "+ gm.mRate+"\n"+
							         "Max Delay:              "+gm.sRate+"\n" + "\nstart\n");
					gm.logfile.flush();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				gm.start();
				GameLoop gl = new GameLoop(gm,gameView.gameboard);
				Thread t = new Thread(gl);
				t.start();
	
				/*
				// Finally, show the actual game window!
				gameView.frame.setVisible(true);
				GameLoop gl = new GameLoop(gm,gameView.gameboard);
				Thread t = new Thread(gl);
				System.out.println("gameloop");
				t.start();
				gameView.gameboard.requestFocus();
				*/
			
			}
		});

		stop = new JButton("stop");
		
		stop.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				gm.stop();
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
		
		matrix4 = new JPanel();
		matrix4.setLayout(new GridLayout(5,1));
		
		matrix5 = new JPanel();
		matrix5.setLayout(new GridLayout(4,4));
		
		matrix6 = new JPanel();
		
		JPanel blank=new JPanel();
		JPanel blank1=new JPanel();


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
		
		matrix4.setBorder(javax.swing.BorderFactory.createTitledBorder("Main Control") );
		matrix4.add(subId);
		matrix4.add(expId);
		matrix4.add(scrip);
		matrix4.add(scr);
		matrix4.add(currentactor);
		matrix4.add(start);
		matrix4.add(restart);
		matrix4.add(stop);
		matrix4.add(pause);
		matrix4.add(sdone);

		
		matrix5.add(blank);
		matrix5.add(hit);
		matrix5.add(miss);
		matrix5.add(total);
		matrix5.add(good1);
		matrix5.add(ghit);
		matrix5.add(gmiss);
		matrix5.add(gtot);
		matrix5.add(bad1);
		matrix5.add(bhit);
		matrix5.add(bmiss);
		matrix5.add(btot);
		matrix5.add(blank1);
		matrix5.add(htot);
		matrix5.add(mtot);
		matrix5.add(tot);
		
		matrix6.add(jsp);
		matrix1.setBackground(Color.red);
		matrix2.setBackground(Color.green);
		matrix3.setBackground(Color.blue);
		matrix4.setBackground(Color.gray);
		matrix5.setBackground(Color.pink);
		matrix6.setBackground(Color.yellow);
		matrix6.setBorder(javax.swing.BorderFactory.createTitledBorder("jsp") );
		
		generatescreen.add(matrix1);
		generatescreen.add(matrix3);
		generatescreen.add(matrix2);
		scriptedscreen.add(matrix4);
		this.add(scripted);
		this.add(generate);
		//this.add(matrix5);
		//this.add(matrix6);
	}
}
