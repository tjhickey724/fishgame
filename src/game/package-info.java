/**
 * This package provides classes that implement the fMRI version of the fishgame.
 * The RunJava main method creates an Experimenter Window which gives the user two choices
 * <ul><li> Create a script</li> <li>Run a script</li></ul>
 * 
 * The script creator opens the GenerateWindow 
 * which offers several options for generating fish events 
 * using the ScriptGenerator class and 
 * the output of this generator goes into the scripts folder
 * 
 * The script runner opens the ScriptWindow which prompts the user for 
 * <ul><li>the script to use</li><li> the names of the subject and experimenter</li>
 * <li> and other info</li></ul> 
 * It also opens a GameView window which the subject will focus on during the experiment.
 * 
 * When a "=" character is typed (usually by the fMRI hardware through a USB keyboard)
 * the game starts and fish appear on the screen. The subject must press P or L to
 * indicate which kind of fish appeared. The script specifies the time the fish appears
 * as well as the side it appears from and the visual and audio features of the fish.
 * 
 * The game is implemented using a GameLoop which repeatedly updates the GameModel
 * and redraws the GameView. This loop operates in its own thread so as not to slow down
 * the user interface.
 * 
 * The GameModel keeps track of everything we need to know to draw the screen.
 * The GameView has the code for drawing the view (using the model) and for responding to
 * user input.  User input is given exclusively by key presses.
 * 
 * The interaction with the game is stored in a logfile which contains information about 
 * every relevant event including
 * <ul><li> the specifications in the script file</li>
 * <li> info about every fish released</li>
 * <li> info about every key press</li>
 * </li></ul>
 * The events in the log are tagged by an event-time since the beginning of the game in milliseconds.
 * 
 * More info about the game can be found in the headers of each of the classes and in the javadoc
 * for the methods and other members of these classes.
 */
package game;
/**
 * this is a test*****
 */
