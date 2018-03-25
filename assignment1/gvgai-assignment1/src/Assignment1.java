
import controllers.Algorithms;
import core.ArcadeMachine;
import java.util.Random;
import core.competition.CompetitionParameters;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author yuy, tangruize
 */
public class Assignment1 {
 
    public static void main(String[] args)
    {
        //Available controllers:
    	String depthfirstController = "controllers.depthfirst.Agent";
    	String limitdepthfirstController = "controllers.limitdepthfirst.Agent";
        String AstarController = "controllers.Astar.Agent";
        String sampleMCTSController = "controllers.sampleMCTS.Agent";

        boolean visuals = true; // set to false if you don't want to see the game
        int seed = new Random().nextInt(); // seed for random

        /****** Task 1 ******/
        CompetitionParameters.ACTION_TIME = 10000; // set to the time that allow you to do the depth first search
        System.err.println("Using depth first algorithm, Max depth: " + Algorithms.MAX_DEPTH + ", Action time: " + CompetitionParameters.ACTION_TIME);
        ArcadeMachine.runOneGame("examples/gridphysics/bait.txt", "examples/gridphysics/bait_lvl0.txt", true, depthfirstController, null, seed, false);
        ArcadeMachine.runOneGame("examples/gridphysics/bait.txt", "examples/gridphysics/bait_lvl1.txt", true, depthfirstController, null, seed, false);
        ArcadeMachine.runOneGame("examples/gridphysics/bait.txt", "examples/gridphysics/bait_lvl2.txt", true, depthfirstController, null, seed, false);
        ArcadeMachine.runOneGame("examples/gridphysics/bait.txt", "examples/gridphysics/bait_lvl3.txt", true, depthfirstController, null, seed, false);
        ArcadeMachine.runOneGame("examples/gridphysics/bait.txt", "examples/gridphysics/bait_lvl4.txt", true, depthfirstController, null, seed, false);

        /****** Task 2 ******/
        CompetitionParameters.ACTION_TIME = 100; // no time for finding the whole path
        System.err.println("Using limited depth first with heuristic algorithm, Max depth: " + Algorithms.MIDDLE_DEPTH + ", Action time: " + CompetitionParameters.ACTION_TIME);
        ArcadeMachine.runOneGame("examples/gridphysics/bait.txt", "examples/gridphysics/bait_lvl0.txt", true, limitdepthfirstController, null, seed, false);
        ArcadeMachine.runOneGame("examples/gridphysics/bait.txt", "examples/gridphysics/bait_lvl1.txt", true, limitdepthfirstController, null, seed, false);
        ArcadeMachine.runOneGame("examples/gridphysics/bait.txt", "examples/gridphysics/bait_lvl2.txt", true, limitdepthfirstController, null, seed, false);
        ArcadeMachine.runOneGame("examples/gridphysics/bait.txt", "examples/gridphysics/bait_lvl3.txt", true, limitdepthfirstController, null, seed, false);
        ArcadeMachine.runOneGame("examples/gridphysics/bait.txt", "examples/gridphysics/bait_lvl4.txt", true, limitdepthfirstController, null, seed, false);

        /****** Task 3 ******/
        CompetitionParameters.ACTION_TIME = 100; // no time for finding the whole path
        System.err.println("Using A* algorithm, Max depth: " + Algorithms.MIN_DEPTH + ", Action time: " + CompetitionParameters.ACTION_TIME);
        ArcadeMachine.runOneGame("examples/gridphysics/bait.txt", "examples/gridphysics/bait_lvl0.txt", true, AstarController, null, seed, false);
        ArcadeMachine.runOneGame("examples/gridphysics/bait.txt", "examples/gridphysics/bait_lvl1.txt", true, AstarController, null, seed, false);
        ArcadeMachine.runOneGame("examples/gridphysics/bait.txt", "examples/gridphysics/bait_lvl2.txt", true, AstarController, null, seed, false);
        ArcadeMachine.runOneGame("examples/gridphysics/bait.txt", "examples/gridphysics/bait_lvl3.txt", true, AstarController, null, seed, false);
        ArcadeMachine.runOneGame("examples/gridphysics/bait.txt", "examples/gridphysics/bait_lvl4.txt", true, AstarController, null, seed, false);


        /****** Task 4 ******/
        CompetitionParameters.ACTION_TIME = 100; // no time for finding the whole path
        System.err.println("Using MCTS algorithm, Action time: " + CompetitionParameters.ACTION_TIME);
        ArcadeMachine.runOneGame("examples/gridphysics/bait.txt", "examples/gridphysics/bait_lvl0.txt", true, sampleMCTSController, null, seed, false);

    }   
}
