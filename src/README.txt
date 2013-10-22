Notes on the fMRI version.

Concepts.
This game is designed to be used with an fMRI machine which will scan the subjects
brain in a sequence of scans each of which lasts 2.6 seconds.

An experiment consists of several runs
A run is a single session consisting of a sequence of blocks.
Each block is a sequence of trials, all of which have the same type which is one of
    Baseline, Congruent, Incongruent, Neutral
and each block also has the same "mood" which is either Active or Passive
Block typically have 8-10 trials.
Each trial consists of a contiguous sequence of 2.6 second scans.

There are four types of trials.
Each run begins and ends with a 10.4 second trial with only the background moving,
these are called baseline trials.
Fish trials all involve 
 * a delay of time t where t is chosen from a fixed set (e.g. 1.5, 2.0, 2.5 seconds) 
 * after which is fish is released and swims at a constant speed across the screen
 * the user may or may not press a key while the fish is on the screen
 * a good sound or bad sound will occur when the user presses a key
 * after being on the screen for 2.0 seconds the fish disappears
 * the next trial starts at the next multiple of 2.6 seconds.
 
 There are five types of trials all of which last some multiple of 2.6 seconds
 * Baseline trials (where no fish is released) that last 10.4 seconds
 * Congruent trials where the fish visual and audio oscillations are at the same hertz
 * Incongruent trials where the visual and audio osciallations are different (reversed)
 * Neutral trials where there is no audio at all
 * TextCue trial which simply displays the type of text for a certain amount of time
   over the moving background.
 
 In addition, trials can be either Active or Passive
 * Active trials ask the user to press 1 for a visually slow oscillation and 2 else
 * Passive trials ask the user to make no judgment (though they may be asked to press a key)

Each run will be controlled by a script which consists of a sequence of blocks of the four types,
e.g. PCNCIPNI  or NIPNCPICPNI
The entire run is begun and ended with a Baseline trial.
 
 The tool can be used to create scripts for each of the blocks:
   N, C, I  
 as well as Passive versions where P represents the passive congruent script
   n c i
 and a baseline script with just one baseline scan
   L
 These can then be concatenated together to form full scripts
  A_LCPINPINPL.txt   (cat L C P I N P I N P L > A_LCPINPINPL.txt)
  B_LNIPCIPCNL.txt
  C_LNCnICnINL.txt
  
  We can also create such a script directly all at once but it might be easier to do it
  this way using "cat" to form the final scripts...
  