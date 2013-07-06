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
			JLabel header,fish,good,bad,sound,visual,subjectid,experimenterid,soundType,videoType,gameType,mintime,maxtime,scrip;
		JPanel matrix1,matrix2,matrix3,matrix4;
		JButton button1;
		JPanel col1,col2,col3,row1,row2,row3;
		//JTextField gs,bs,gc,bc;
		int slow,fast,min,max;
		int[] times;
		String[] speeds,videotypes,gametypes,soundtypes;
		JSpinner gs,bs,gv,bv,vidtype,gamtype,stype,mintim,maxtim;
		String SubjectID,ExperimenterID,script,type;
		JTextField expId,subId,scr;
		JCheckBox ster;
		JFileChooser fc;
		JButton save;
		GameModel gm;
		SpinnerModel minmodel,maxmodel;
		
	
		
	
	public ParamsUI(final GameModel gm) {
		
		super("Parameters");
		this.gm=gm;
		this.setSize(500,500);
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		this.setLayout(new GridLayout(2,2));
		
		fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		
		expId= new JTextField("Experimenter");
		subId = new JTextField("Subject");
		scr= new JTextField("Script");
		
		header = new JLabel("Settings");
		fish=new JLabel("Fish Type:");
		good=new JLabel("Good");
		bad=new JLabel("Bad");
		sound=new JLabel("Sound");
		visual=new JLabel("Visual");
		soundType=new JLabel("Sound Type: ");
		videoType=new JLabel("Video Type: ");
		gameType=new JLabel("Game Type: ");
		mintime=new JLabel("Min Time: ");
		maxtime= new JLabel("Max Time: ");
		scrip = new JLabel("Script: ");	
		
		min = 0;
		max = 10;
		times = new int[max];
		for (int i=min; i<max; i++){
			times[i]=i;
		}
		
		final SpinnerModel minmodel = new SpinnerNumberModel(times[0],times[0],times[times.length-1],1);
		mintim = new JSpinner(minmodel);
		final SpinnerModel maxmodel = new SpinnerNumberModel(times[0],times[0],times[times.length-1],1);
		maxtim = new JSpinner(maxmodel);
	

		
		speeds=new String[]{"none","slow","fast"};
		
		final SpinnerModel speedmodel0 = new SpinnerListModel(speeds);
		final SpinnerModel speedmodel1 = new SpinnerListModel(speeds);
		final SpinnerModel speedmodel2 = new SpinnerListModel(speeds);
		final SpinnerModel speedmodel3 = new SpinnerListModel(speeds);
		
		videotypes=new String[]{"Flicker","Throb"};
		SpinnerModel videoTypeModel = new SpinnerListModel(videotypes);
		vidtype = new JSpinner(videoTypeModel);
		
		gametypes=new String[]{"Generated","Scripted"};
		final SpinnerModel gameTypeModel = new SpinnerListModel(gametypes);
		gamtype = new JSpinner(gameTypeModel);
		
		soundtypes=new String[]{"Stereo","Mono"};
		SpinnerModel soundTypeModel = new SpinnerListModel(soundtypes);
		stype = new JSpinner(soundTypeModel);
		
		gs=new JSpinner(speedmodel0);
		bs=new JSpinner(speedmodel1);
		gv=new JSpinner(speedmodel2);
		bv=new JSpinner(speedmodel3);
		
		// the Save button will look at the values of the params interface and change the game model accordingly
				save= new JButton("Save");
		save.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				gm.mRate =(Integer) minmodel.getValue();
				gm.sRate =(Integer) maxmodel.getValue();
				if (gameTypeModel.getValue().equals("Scripted")){
					gm.scripted=true;
					type = "Scripted";
				} else {
					gm.scripted=false;
					type = "Random";
				}
				if (speedmodel0.getValue().equals("fast")){
					gm.goodaudiohz=9;
				} else if (speedmodel0.getValue().equals("slow")){
					gm.goodaudiohz=4;
				}else if (speedmodel0.getValue().equals("none")){
					gm.goodaudiohz=0;
				}
				if (speedmodel1.getValue().equals("fast")){
					gm.badaudiohz=9;
				} else if (speedmodel1.getValue().equals("slow")){
					gm.badaudiohz=4;
				}else if (speedmodel1.getValue().equals("none")){
					gm.badaudiohz=0;
				}
				if (speedmodel2.getValue().equals("fast")){
					gm.goodvisualhz=9;
				} else if (speedmodel2.getValue().equals("slow")){
					gm.goodvisualhz=4;
				}else if (speedmodel2.getValue().equals("none")){
					gm.goodvisualhz=0;
				}
				if (speedmodel3.getValue().equals("fast")){
					gm.badvisualhz=9;
				} else if (speedmodel3.getValue().equals("slow")){
					gm.badvisualhz=4;
				}else if (speedmodel3.getValue().equals("none")){
					gm.badvisualhz=0;
				}
				script=scr.getText();
				SubjectID=subId.getText();
				ExperimenterID=expId.getText();
			
				
				System.out.println(gm.badaudiohz);
				System.out.println(gm.badvisualhz);
				System.out.println(gm.goodaudiohz);
				System.out.println(gm.goodvisualhz);
				
	//			slow = min;
				System.out.println(script);
				System.out.println(gm.mRate);
				try {
					gm.logfile.write("Experimenter: "+ ExperimenterID + "\n" + "Subject: " + SubjectID + "\n" + "Game Type: "+ type + "\n"+"Min Delay: "+ gm.mRate+"\n"+"Max Delay: "+gm.sRate+"\n");
					gm.logfile.flush();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
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
		matrix3.setLayout(new GridLayout(3,2));
		
		matrix4 = new JPanel();
		matrix4.setLayout(new GridLayout(3,1));
		
/*		button1 = new JButton();
		button1.setText("Push Me");
		button1.addActionListener(
				new ActionListener(){
			public void actionPerformed(ActionEvent e){
				System.out.println("Yay I love being pushed");
			}
		});
		
		*/
		
		col1.add(fish);
		col1.add(good);
		col1.add(bad);
		
		col2.add(sound);
		col2.add(gs);
		col2.add(bs);
		
		col3.add(visual);
		col3.add(gv);
		col3.add(bv);
		
		row1.add(soundType);
		row1.add(stype);
		
		row2.add(videoType);
		row2.add(vidtype);
		
		row3.add(gameType);
		row3.add(gamtype);
		
		
		matrix1.add(col1);
		matrix1.add(col2);
		matrix1.add(col3);
		
		matrix2.add(row1);
		matrix2.add(row2);
		matrix2.add(row3);
		
		matrix3.add(mintime);
		matrix3.add(mintim);
		matrix3.add(maxtime);
		matrix3.add(maxtim);
		matrix3.add(scrip);
		matrix3.add(scr);
		
		matrix4.add(subId);
		matrix4.add(expId);
		matrix4.add(save);
		
		this.add(matrix1);
		this.add(matrix2);
		this.add(matrix3);
		this.add(matrix4);
		
	}
}
