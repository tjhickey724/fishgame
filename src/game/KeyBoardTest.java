package game;
import java.util.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;

public class KeyBoardTest{
	
	JFrame frame;
	JTextArea inputText;
	JTextArea feedBackText;
	
	public static void main(String[] args){
		EventQueue.invokeLater(new Runnable()
			{
			@Override
			public void run(){
				new KeyBoardTest();
				}
			}
		);
			}
	
	public KeyBoardTest(){
		JFrame gui = new JFrame();
		gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gui.setTitle("Keyboard response test");
		gui.setSize(700,200);
		gui.setLocationRelativeTo(null);
		feedBackText = new JTextArea();
		JScrollPane scrollText = new JScrollPane(feedBackText);
		inputText = new JTextArea();
		inputText.addKeyListener(new KeyListener()
			{
				@Override
				public void keyPressed(KeyEvent e)
					{
						feedBackText.append("Key Pressed: " + e.getKeyChar() + "\n");
					}
			
				@Override
					public void keyReleased(KeyEvent e)
					{
					feedBackText.append("Key Released: " + e.getKeyChar() + "\n");
					}
			
				@Override
				public void keyTyped(KeyEvent e)
				{
					feedBackText.append("Key Typed: " + e.getKeyChar() +
						" " + KeyEvent.getKeyModifiersText(e.getModifiers()) + "\n");
				}
			});
	gui.add(inputText, BorderLayout.NORTH);
	gui.add(scrollText, BorderLayout.CENTER);
    gui.setVisible(true);
	
	}
}
	