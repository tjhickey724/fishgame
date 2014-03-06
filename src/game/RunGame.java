package game;

import java.awt.CardLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JSlider;

/**
 * this create the splash screen for the game in addition 
 * it creates the experimenter's window, which offers the user a choice to run
 * from a script or generate a script. It also stores the versionNum
 * 
 * @author tim
 * 
 */
// REFACTOR: we may need to call the setVisible method from inside the EventQueue....

public class RunGame  {
	/**
	 * version must have the form A.B.C where A,B,C are numbers It represents
	 * the Major/Minor/Bugfix versions of the code.
	 */
	public static String versionNum = "3";
    public static final String filename="images/fish/smallfish.png";
    public static  CardLayout cl;
    public static  boolean endSplash=true;
    public static  JPanel cards = new JPanel(new CardLayout());
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
	        
//	      panel.add( new AnimationBackground(10, 10, 2, 3, 1, 1, 10) );
	        
	        JPanel panel2 = new JPanel(null)
	        {
	            Image image = new ImageIcon("images/menueback2.png").getImage();
	            Image image2=new ImageIcon("Images/stream.jpg").getImage();
	            protected void paintComponent(Graphics g)
	            {   g.drawImage(image2, 0, 0, getWidth(), getHeight(), null);
	                g.drawImage(image, (getWidth()-image.getWidth(null))/2, (getHeight()-image.getHeight(null))/2, image.getWidth(null), image.getHeight(null), null);
	               
	                super.paintComponent(g);
	            }
	        };
	        addAnimation(panel);
	        addAnimation(panel2);
	        panel2.setOpaque(false);
	        panel.setOpaque(false);
	        AnimatedObject.play("sounds/water/bubble.wav");
	        JFrame frame = new JFrame();
	      
	        cards.add(panel,"begin");
	        cards.add(panel2,"end");
	        frame.getContentPane().add(cards);
	       // frame.setExtendedState(Frame.MAXIMIZED_BOTH); 
	        //frame.setUndecorated(true);
	      //  frame.setLocationRelativeTo( null );
			frame.setSize(700, 1000);

	        frame.setVisible(true);
	        cl = (CardLayout)(cards.getLayout());
	        cl.show(cards, "begin");
	        frame.addKeyListener(new KeyListener() {
	            public void keyPressed(KeyEvent e) { 
	            	 System.out.print(e.getKeyCode());
	        		 if (e.getKeyCode()==KeyEvent.VK_SPACE){
	        			 cl.show(cards, "end");
	        			 endSplash=false;
	        		     ExperimenterWindow params = new ExperimenterWindow();
	        			 params.setVisible(true);
	        			 
	        		 }
	            	/* ... */ }

	            public void keyReleased(KeyEvent e) { 
			 } 

	            public void keyTyped(KeyEvent e) { /* ... */ }
	        });
	        
	        Thread.sleep(5000);
	        cl.show(cards, "end");
	        if(endSplash)
	        { ExperimenterWindow params = new ExperimenterWindow();
			params.setVisible(true);}
			
			
		
	}
	
	// this used to add animation to the background
	public static void addAnimation (JPanel panel){
		panel.add( new AnimatedObject(300, 100, 3, 2, -1, 1, 20) );
        panel.add( new AnimatedObject(200, 200, 2, 3, 1, -1, 20) );
        panel.add( new AnimatedObject(50, 50, 5, 5, -1, -1, 20) );
        panel.add( new AnimatedObject(-200, 000, 3, 5, 1, 1, 20,filename));
        panel.add( new AnimatedObject(0, 200, 5, 0, 1, 1, 80) );
	}

	
	

	
	
}
