package game;

import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

/**
 * This creates the experimenter's window, which offers the user a choice to run
 * from a script or generate a script. It also stores the versionNum
 * 
 * @author tim
 * 
 */
// REFACTOR: we may need to call the setVisible method from inside the EventQueue....

public class RunGame {
	/**
	 * version must have the form A.B.C where A,B,C are numbers It represents
	 * the Major/Minor/Bugfix versions of the code.
	 */
	public static String versionNum = "3";

	public static void main(String[] args) throws InterruptedException {
		 JPanel panel = new JPanel(null)
	        {
	            Image image = new ImageIcon("images/stream.jpg").getImage();
	            Image Image2=new ImageIcon("Images/bubbleTrouble2.png").getImage();
	            protected void paintComponent(Graphics g)
	            {
	                g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
	                g.drawImage(Image2,(getWidth()-Image2.getWidth(this))/2,(getHeight()-Image2.getHeight(this))/2,Image2.getWidth(this),Image2.getHeight(this),null);
	                super.paintComponent(g);
	            }
	        };
	        panel.setOpaque(false);
	        AnimatedObject.play("sounds/water/bubble.wav");
//	      panel.add( new AnimationBackground(10, 10, 2, 3, 1, 1, 10) );
	        panel.add( new AnimatedObject(300, 100, 3, 2, -1, 1, 20) );
	        panel.add( new AnimatedObject(200, 200, 2, 3, 1, -1, 20) );
	        panel.add( new AnimatedObject(50, 50, 5, 5, -1, -1, 20) );
	        panel.add( new AnimatedObject(-200, 000, 3, 5, 1, 1, 20,"images/fish/smallfish.png"));
	        panel.add( new AnimatedObject(0, 200, 5, 0, 1, 1, 80) );
	        
	        JFrame frame = new JFrame();
	        frame.setContentPane(panel);
	        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
	        frame.setExtendedState(Frame.MAXIMIZED_BOTH); 
	        frame.setUndecorated(true);
	        frame.setLocationRelativeTo( null );
	        frame.setVisible(true);
	        
	        Thread.sleep(5000);
	      
	        frame.dispose();
            Instructions window=new Instructions();
		
	}
}
