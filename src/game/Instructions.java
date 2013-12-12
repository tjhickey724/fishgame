package game;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Instructions {
	   public Instructions() throws InterruptedException{
		 JPanel panel = new JPanel(null)
	        {
	            Image image = new ImageIcon("images/menueback2.png").getImage();
	            Image image2=new ImageIcon("Images/stream.jpg").getImage();
	            protected void paintComponent(Graphics g)
	            {   g.drawImage(image2, 0, 0, getWidth(), getHeight(), null);
	                g.drawImage(image, (getWidth()-image.getWidth(null))/2, (getHeight()-image.getHeight(null))/2, image.getWidth(null), image.getHeight(null), null);
	                //g.drawImage(Image2,(getWidth()-Image2.getWidth(this))/2,(getHeight()-Image2.getHeight(this))/2,Image2.getWidth(this),Image2.getHeight(this),null);
	                super.paintComponent(g);
	            }
	        };
	        panel.setOpaque(false);
	        AnimatedObject.play("sounds/water/bubble.wav");
//	      panel.add( new AnimationBackground(10, 10, 2, 3, 1, 1, 10) );
	        panel.add( new AnimatedObject(300, 100, 3, 2, -1, 1, 20) );
	        panel.add( new AnimatedObject(200, 200, 2, 3, 1, -1, 20) );
	        panel.add( new AnimatedObject(50, 50, 5, 5, -1, -1, 20) );
	        panel.add( new AnimatedObject(0, 200, 5, 0, 1, 1, 80) );
	        
	        JFrame frame = new JFrame();
	        frame.setContentPane(panel);
	        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
	        frame.setExtendedState(Frame.MAXIMIZED_BOTH); 
	        frame.setUndecorated(true);
	        frame.setLocationRelativeTo( null );
	        frame.setVisible(true);
	        Thread.sleep(5000);
	        //frame.dispose();
	       
	        ExperimenterWindow params = new ExperimenterWindow();
			params.setVisible(true);
}
}
