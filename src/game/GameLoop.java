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
				if (gm.isPaused()){
					try {
						Thread.sleep(100L);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					continue;
				}else if (gm.isGameOver()){
					// we need to clean up the old game
					// this should be a method inside the gm to reset
					gm.scan.close();
					gm.scan=null;
					System.out.println("ending game loop");
					return;
				}
				// update the model
				gm.update();
				
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
