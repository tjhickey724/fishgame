README

This is the behavioral version of the fishgame.
This program allows the experimenter to create scripts
and to run experiments using those scripts.
Scripts can be concatenated together to create longer runnable scripts.

The game proceeds in a sequence of trials
The first trial starts when the experimenter presses "start" on the ExperimenterWindow
Each trial consists of a delay period of variable length, called the Inter Fish Interval (IFI)
and these are typically chosen from a set of three possible delays.

Once the fish is launched it may oscillated visually and may be accompanied by an auditory cue
that also oscillates. There are four possible settings for the type of fish launched in a trial
Trials have two oscillation frequencies: slow (typically 6hz) and fast (typically 8hz)
   congruent - the visual and auditory cues have the same frequency
   incongruent - the visual and auditory cues have different frequencies
   visual-only - there is no auditory cue
   auditory-only - there is no visual cue
 Also, the experiment can be run in two modes, depending on what the
 subject is told to focus on for determining good/bad
   * avmode = 0  means use the   visual cue to determine good/bad
   * avmode = 1  means use the auditory cur to determine good/bad

The fish will stay on the screen for a fixed amount of time, typically 2 seconds,
unless the user presses a 'P' or an 'L'
  * 'P' indicates a good fish (with a slow oscillation)
  * 'L' indicates a bad fish (with a fast oscillation)
The subject will be told to focus on either the visual or the auditory cues to determine 
whether a fish is good or bad. 
Once a 'P' or 'L' is pressed, the fish disappears from the screen.
If no key is pressed, then the fish disappears after 2 seconds.

When the fish disappears the next trial is started, or if it was the last fish, the game ends.

The fishgame generates a logfile that records all important events:
* all UI properties of the game, e.g. the visual oscillation rates for good and bad fish,
  the sound files for good and bad fish, the lifetime of a fish, the subject ID, the experimenter ID,
  the date, whether stereo or mono sound is used, etc.
* the start of the game
* the launching of a fish
* the pressing of a key
* the end of the game
Moreover, if the EEG version is selected, then events are sent via TCP/IP connection to the NetStation
along with information about each important event.


The key technical issue is that when a fish is launched, the next fish info is read from the script
and the nextFishTime is determined assuming there will be no keypress.

If there is a keypress then the next fish time is set using the delay of the nextFish.

So we define a createFish operation that reads from the script 
and stores the info in the nextFish object
and updates the nextFishTime assuming no key will be pressed.

If a key is pressed, then the nextFishTime is updated to be the current time + the delay
for the next fish.

=================================================================
EEG Event Data Specifications

markers and data

marker with trial index before a fish is launched
T001 T002 T003 .... T999  for the key
this should happen half way before the next fish is launched....

Congruence marker is one of these
CONT
CONG
INCO

Direction marker is one of these
DIRL
DIRR

FishType marker is one of these
which encodes if it is good or bad and the frequency
G6__
B8__

When this fish is launched we need a marker
GO__

When they respond to a fish we need a marker
KEYP
KEYL

There will be a 200 ms delay in the feedback tone for EEG
and we need a marker for the feedback tone

FPOS
FNEG

===================================================================





