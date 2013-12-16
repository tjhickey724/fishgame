package game;

import java.awt.CardLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class SplashMovie {
	public static  CardLayout cl;
	public static  JPanel cards = new JPanel(new CardLayout());
	public static String imagename="";
	public static void main(String[] args) throws InterruptedException {
		JFrame frame = new JFrame();
		frame.getContentPane().add(cards);
	    frame.setExtendedState(Frame.MAXIMIZED_BOTH); 
	    frame.setUndecorated(true);
	    frame.setLocationRelativeTo( null );
	    frame.setVisible(true);
		ArrayList<JPanel> x=new ArrayList<JPanel>();
		 for (int i=1; i<=100;i++)
		 {  
			 if( i<10)
				 imagename="000"+i+".jpg";
			 else if(i<100)
				 imagename="00"+i+".jpg";
			 else 
				 imagename="0"+i+".jpg"; 
			 System.out.println(imagename);
		 JPanel panel = new JPanel(null)
	        {   
	       
	            Image image = new ImageIcon("images/splashMovie/"+imagename).getImage();
	           
	            protected void paintComponent(Graphics g)
	            {
	                g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
	               
	                super.paintComponent(g);
	            }
	        };
	      panel.setOpaque(false);
	     
		  cards.add(panel,imagename)  ;  
		  cl = (CardLayout)(cards.getLayout());
		  cl.show(cards, imagename);
		  Thread.sleep(250);
		  panel.removeAll();
}
		
	}
}