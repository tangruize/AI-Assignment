//package controllers.depthfirst;
//
//import core.game.StateObservation;
//import core.player.AbstractPlayer;
//import ontology.Types;
//import tools.ElapsedCpuTimer;
//
//import java.util.ArrayList;
//
//public class Agent extends AbstractPlayer {
//    /**
//     * initialize all variables for the agent
//     * @param stateObs Observation of the current state.
//     * @param elapsedTimer Timer when the action returned is due.
//     */
//    public Agent(StateObservation stateObs, ElapsedCpuTimer elapsedTimer){}
//
//    /**
//     * judge if the next state has been reached
//     * @param states reached states
//     * @param next next states
//     * @return returns true if not reached
//     */
//    private boolean isValidOp(ArrayList<StateObservation> states, StateObservation next) {
//        if (next.getGameWinner() == Types.WINNER.PLAYER_LOSES)
//            return false;
//        for (int i = states.size() - 1; i >= 0; --i)
//            if (states.get(i).equalPosition(next))
//                return false;
//        return true;
//    }
//
//    /**
//     * win or not? if win, dfs will stop searching.
//     */
//    private boolean isWin = false;
//
//    /**
//     * reached states
//     */
//    private ArrayList<StateObservation> states = new ArrayList<>();
//
//    /**
//     * all actions the game can perform.
//     */
//    private ArrayList<Types.ACTIONS> availableActions;
//
//    /**
//     * Use DFS algorithm to find a path to win.
//     * @param states all reached states
//     */
//    private void dfs(ArrayList<StateObservation> states) {
//        int size = states.size();
//        StateObservation last = states.get(size - 1);
//        for (Types.ACTIONS action: availableActions) {
//            if (isWin)
//                return;
//            StateObservation copy = last.copy();
//            copy.advance(action);
//            if (isValidOp(states, copy)) {
//                while (states.size() > size)
//                    states.remove(size);
//                states.add(copy);
//                isWin = copy.getGameWinner() == Types.WINNER.PLAYER_WINS;
//                if (!isWin)
//                    dfs(states);
//            }
//        }
//    }
//
//    /**
//     * Use DFS to find a path to win.
//     * @param stateObs Observation of the current state.
//     * @param elapsedTimer Timer when the action returned is due.
//     * @return next action
//     */
//    @Override
//    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
//        if (!isWin) {
//            states.clear();
//            states.add(stateObs);
//            availableActions = stateObs.getAvailableActions();
//            dfs(states);
//        }
//
//        if (states.isEmpty())
//            return Types.ACTIONS.ACTION_NIL;
//
//        Types.ACTIONS action = states.get(0).getAvatarLastAction();
//        states.remove(0);
//        return action;
//    }
//}

package controllers.depthfirst;

import controllers.Algorithms;
import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import tools.ElapsedCpuTimer;

public class Agent extends AbstractPlayer {
    private final Algorithms algorithms;

    public Agent(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        algorithms = new Algorithms(stateObs, Algorithms.depthFirstAlgorithm, Algorithms.MAX_DEPTH);
    }

    @Override
    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        return algorithms.act(stateObs, elapsedTimer);
    }
}
