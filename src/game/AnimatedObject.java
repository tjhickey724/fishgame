package game;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;

public class AnimatedObject extends JLabel implements ActionListener
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int deltaX = 2;
    int deltaY = 3;
    int directionX = 1;
    int directionY = 1;

    public AnimatedObject(
        int startX, int startY,
        int deltaX, int deltaY,
        int directionX, int directionY,
        int delay)
    {
        this.deltaX = deltaX;
        this.deltaY = deltaY;
        this.directionX = directionX;
        this.directionY = directionY;

        setIcon( new ImageIcon("images/bubble2.png") );
        setSize( getPreferredSize() );
        setLocation(startX, startY);
        new javax.swing.Timer(delay, this).start();
    }
    public AnimatedObject(
            int startX, int startY,
            int deltaX, int deltaY,
            int directionX, int directionY,
            int delay,String image)
        {
            this.deltaX = deltaX;
            this.deltaY = deltaY;
            this.directionX = directionX;
            this.directionY = directionY;

            setIcon( new ImageIcon(image) );
            setSize( getPreferredSize() );
            setLocation(startX, startY);
            new javax.swing.Timer(delay, this).start();
        }

    public void actionPerformed(ActionEvent e)
    {
        Container parent = getParent();

        //  Determine next X position

        int nextX = getLocation().x + (deltaX * directionX);

        if (nextX < 0)
        {
            nextX = 0;
            directionX *= -1;
        }

        if ( nextX + getSize().width > parent.getSize().width)
        {
            nextX = parent.getSize().width - getSize().width;
            directionX *= -1;
        }

        //  Determine next Y position

        int nextY = getLocation().y + (deltaY * directionY);

        if (nextY < 0)
        {
            nextY = 0;
            directionY *= -1;
        }

        if ( nextY + getSize().height > parent.getSize().height)
        {
            nextY = parent.getSize().height - getSize().height;
            directionY *= -1;
        }

        //  Move the label

        setLocation(nextX, nextY);
    }
    public static synchronized void play(final String fileName) 
    {
        // Note: use .wav files
 
        new Thread(new Runnable() { 
            public void run() {
                try {
                    Clip clip = AudioSystem.getClip();
                    AudioInputStream inputStream = AudioSystem.getAudioInputStream(new File(fileName));
                    clip.open(inputStream);
                    clip.start(); 
                } catch (Exception e) {
                    System.out.println("play sound error: " + e.getMessage() + " for " + fileName);
                }
            }
        }).start();
    }

    	 
    
    
}