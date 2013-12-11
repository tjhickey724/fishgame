package game;


import javax.imageio.ImageIO;
import javax.swing.JWindow;
import javax.swing.ImageIcon;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
 
 
public class SplashScreen extends JWindow {
	Image img=Toolkit.getDefaultToolkit().getImage("images/stream.jpg");
	ImageIcon imgicon=new ImageIcon(img);
	



public SplashScreen()
{
try
{
     
setSize(img.getWidth(null),img.getHeight(null));
setLocationRelativeTo(null);
show();
Thread.sleep(5000);
dispose();

ExperimenterWindow params = new ExperimenterWindow();
params.setVisible(true);
}
catch(Exception exception)
{
       javax.swing.JOptionPane.showMessageDialog((java.awt.Component)
               null,"Error"+exception.getMessage(), "Error:",
               javax.swing.JOptionPane.DEFAULT_OPTION);
}
}
 
public void paint(Graphics g)
{
g.drawImage(img,0,0,this);
drowbubble(g);
}
private void drowbubble(Graphics g){
	try{
	BufferedImage bubble=ImageIO.read(new File("images/bubble2.png"));
	BufferedImage bubbletitle=ImageIO.read(new File("images/bubbletrouble.png"));
	BufferedImage bubble2=ImageIO.read(new File("images/bubble2.png"));
	BufferedImage bubble3=ImageIO.read(new File("images/bubble2.png"));
	BufferedImage fishR=ImageIO.read(new File("images/fish/fishR.png"));
	BufferedImage fishL=ImageIO.read(new File("images/fish/fishL.png"));
	
	int x = (this.getWidth()- bubble.getWidth()/4)/2;
	int y = (this.getHeight()-bubble.getHeight()/4)/2;
	g.drawImage(bubbletitle,x-bubbletitle.getWidth()/2,0,bubbletitle.getWidth(),(bubbletitle.getHeight()),null);
	g.drawImage(bubble, 0, y, bubble.getWidth()/2, bubble.getHeight()/2, null);
	g.drawImage(bubble2, x*2-20, y/2, bubble.getWidth()/2, bubble.getHeight()/2, null);
	g.drawImage(bubble3, x+(bubble.getWidth()/2)+40, y*2-20, bubble.getWidth()/2, bubble.getHeight()/2, null);
	g.drawImage(fishR, x+2*(bubble.getWidth()/2)+40, y-20, fishR.getWidth()/4, fishR.getHeight()/3, null);
	}
	
	catch (Exception e){
		
	}
	
}
public static void main(String[]args)
{
SplashScreen sp=new SplashScreen();
}
}


