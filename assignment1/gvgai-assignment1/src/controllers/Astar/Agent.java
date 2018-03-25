package controllers.Astar;

import controllers.Algorithms;
import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import tools.ElapsedCpuTimer;

public class Agent extends AbstractPlayer {
    private final Algorithms algorithms;

    public Agent(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        algorithms = new Algorithms(stateObs, Algorithms.aStarAlgorithm, Algorithms.MIN_DEPTH);
    }

    @Override
    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        return algorithms.act(stateObs, elapsedTimer);
    }
}
