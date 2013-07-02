package game;
import java.awt.EventQueue;
import java.io.IOException;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
  The GameLoop repeatedly updates the model and draws a representation
  of the model on the screen.  Since it is running in its own thread
  it needs to use EventQueue.invokeLater to update the GUI components...
*/
public class GameLoop implements Runnable{
	private GameModel gm; 
	private GameView gameboard;
	
	public GameLoop(GameModel gm, GameView gameboard) {
		 this.gm=gm;
		 this.gameboard=gameboard;
		}

		public void run(){
			while(true){
				// update the model
				try {
					gm.update();
				} catch (UnsupportedAudioFileException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (LineUnavailableException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				// repaint the gameboard, safely
				EventQueue.invokeLater(new Runnable(){
					public void run(){
						gameboard.repaint();
						gameboard.requestFocus();
					}
				});

				// sleep for 0.001 seconds
				try{
					Thread.sleep(1l);
				}catch(Exception e){
					System.out.println("In game loop:"+ e);
				}
			}
		}

}
