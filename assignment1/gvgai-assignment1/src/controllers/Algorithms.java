package controllers;

import core.game.Observation;
import core.game.StateObservation;
import ontology.Types;
import tools.ElapsedCpuTimer;
import tools.Vector2d;

import java.util.*;

class StateEstimate {
    private static int sequenceCounter;
    private static Vector2d keyPosition, goalPosition;
    private static int keyGridX, keyGridY, goalGridX, goalGridY;
    static final int INFINITY = Integer.MAX_VALUE / 2;
    static boolean gotKey;

    /**
     * Three distance: has reached, to key/door and their sum.
     * i.e. g(), h(), f(); f() = g() + h()
     */
    int distanceSoFar, distanceToGoal, distanceTotal;
    StateObservation stateObservation;
    int sequence;
    Vector2d avatarPosition;
    StateEstimate previousState;

    StateEstimate(StateEstimate previousState, Types.ACTIONS action) {
        initialiseFields(previousState.stateObservation, previousState.distanceSoFar + 1, action);
        sequence = sequenceCounter++;
    }

    StateEstimate(StateObservation state, int distanceReached) {
        initialiseFields(state, distanceReached, Types.ACTIONS.ACTION_NIL);
        sequence = sequenceCounter++;
    }

    private void initialiseFields(StateObservation state, int distanceReached, Types.ACTIONS action) {
        stateObservation = state.copy();
        stateObservation.advance(action);
        if (stateObservation.getGameWinner() == Types.WINNER.PLAYER_WINS)
            avatarPosition = goalPosition;
        else
            avatarPosition = stateObservation.getAvatarPosition();
        distanceToGoal = calculateDistance();
        distanceSoFar = distanceReached;
        distanceTotal = distanceSoFar + distanceToGoal;
    }

    private int calculateDistance() {
        if (avatarPosition.x < 0)
            return INFINITY; // drop into hole
        ArrayList<Observation>[][] observationGrid = stateObservation.getObservationGrid();
        if (observationGrid[goalGridX][goalGridY].size() >= 2 || (!gotKey && observationGrid[keyGridX][keyGridY].size() >= 2))
            return INFINITY; // dead end
        Vector2d destination = gotKey ? goalPosition : keyPosition;
        return Math.abs((int)((avatarPosition.x - destination.x) / 50))
                + Math.abs((int)((avatarPosition.y - destination.y) / 50));
    }

    public static void setEnvironment(Vector2d key, Vector2d goal) {
        /* calling this function means that it's a new agent. */
        keyPosition = key;
        goalPosition = goal;
        gotKey = false;
        sequenceCounter = 0;
        keyGridX = (int) (keyPosition.x / 50);
        keyGridY = (int) (keyPosition.y / 50);
        goalGridX = (int) (goalPosition.x / 50);
        goalGridY = (int) (goalPosition.y / 50);
    }

    public static void setGotKey() {
        gotKey = true;
    }

    public boolean goalTest() {
        return distanceToGoal == 0;
    }

    private static final TreeSetComparator comparator = new TreeSetComparator();
    @Override
    public boolean equals(Object o) {
        return o instanceof StateEstimate && comparator.compare(this, (StateEstimate) o) == 0;
    }
}

class AStarComparator implements Comparator<StateEstimate> {

    @Override
    public int compare(StateEstimate stateEstimate, StateEstimate t1) {
        return stateEstimate.distanceTotal - t1.distanceTotal;
    }
}

class HeuristicComparator implements Comparator<StateEstimate> {

    @Override
    public int compare(StateEstimate stateEstimate, StateEstimate t1) {
        return stateEstimate.distanceToGoal - t1.distanceToGoal;
    }
}

class BreadthFirstComparator implements Comparator<StateEstimate> {

    @Override
    public int compare(StateEstimate stateEstimate, StateEstimate t1) {
//        if (stateEstimate.distanceToGoal >= StateEstimate.INFINITY || t1.distanceToGoal >= StateEstimate.INFINITY)
//            return stateEstimate.distanceToGoal - t1.distanceToGoal;
        int difference = stateEstimate.distanceSoFar - t1.distanceSoFar;
        return difference == 0 ? stateEstimate.sequence - t1.sequence : difference;
    }
}

class DepthFirstComparator implements Comparator<StateEstimate> {

    @Override
    public int compare(StateEstimate stateEstimate, StateEstimate t1) {
//        if (stateEstimate.distanceToGoal >= StateEstimate.INFINITY || t1.distanceToGoal >= StateEstimate.INFINITY)
//            return stateEstimate.distanceToGoal - t1.distanceToGoal;
        int difference =  t1.distanceSoFar - stateEstimate.distanceSoFar;
        return difference == 0 ? stateEstimate.sequence - t1.sequence : difference;
    }
}

class TreeSetComparator implements Comparator<StateEstimate> {

    @Override
    public int compare(StateEstimate t1, StateEstimate t2) {
        if      (t1.avatarPosition.x < t2.avatarPosition.x) return -1;
        else if (t1.avatarPosition.x > t2.avatarPosition.x) return 1;
        else if (t1.avatarPosition.y < t2.avatarPosition.y) return -2;
        else if (t1.avatarPosition.y > t2.avatarPosition.y) return 2;
        else if (t1.stateObservation.equalPosition(t2.stateObservation)) return 0;
        else if (t1.sequence < t2.sequence) return -3;
        else return 3;
    }
}

public class Algorithms {
    public static final DepthFirstComparator depthFirstAlgorithm = new DepthFirstComparator();
    public static final BreadthFirstComparator breadthFirstAlgorithm = new BreadthFirstComparator();
    public static final HeuristicComparator heuristicAlgorithm = new HeuristicComparator();
    public static final AStarComparator aStarAlgorithm = new AStarComparator();

    public static final int MIN_DEPTH = 11; // minimal depth for AStar. tested
    public static final int MIDDLE_DEPTH = 43; // minimal depth for Heuristic. tested
    public static final int MAX_DEPTH = Integer.MAX_VALUE; // search util find out. for depth first.

    private PriorityQueue<StateEstimate> openSet;
    private PriorityQueue<StateEstimate> depthSatisfied;

    private static final TreeSetComparator comparator = new TreeSetComparator();
    private TreeSet<StateEstimate> closedSet = new TreeSet<>(comparator);
    private TreeSet<StateEstimate> visitedSet = new TreeSet<>(comparator);

    // time out might occur while reconstructing path
    private Stack<Types.ACTIONS> path, tmpPath;
    private StateEstimate tmpCurrent;

    // TreeSet's time complexity is log(n), better than some operations of PriorityQueue's.
    private TreeSet<StateEstimate> openSetForTesting = new TreeSet<>(comparator);

    // left, right, up, down.
    private final ArrayList<Types.ACTIONS> availableActions;

    private enum StateMachine { FIRST_ACTION, RUNNING, DEAD, RECONSTRUCTING }
    private StateMachine stateMachine = StateMachine.FIRST_ACTION; // first action return nil to show window content.

    private static final int REMAINING_LIMIT = 5; // minimum action time to escape from game over.

    private int minDepth;
    private boolean shouldContinue = false;

    /*
     * itype:
     * 0: wall,    1: player without key, 2: hole,
     * 3: unknown, 4: player with key,    5: mushroom,
     * 6: key,     7:door,                8: box.
     */
    private enum IType {
        WALL, PLAYER_NO_KEY, HOLE, UNKNOWN, PLAYER_KEY, MUSHROOM, KEY, DOOR, BOX
    }

    private Vector2d getTypePos(ArrayList<Observation>[] positions, int itype) {
        for (ArrayList<Observation> i : positions) {
            if (i.size() == 1) {
                Observation observation = i.get(0);
                if (observation.itype == itype)
                    return observation.position;
            }
        }
        System.err.println("No position for itype: " + itype + "(" + IType.values()[itype] + ")");
        return new Vector2d(-1, -1);
    }
    private Vector2d getGoalPos(StateObservation stateObs) {
        return getTypePos(stateObs.getImmovablePositions(), IType.DOOR.ordinal());
    }
    private Vector2d getKeyPos(StateObservation stateObs) {
        return getTypePos(stateObs.getMovablePositions(), IType.KEY.ordinal());
    }

    public Algorithms(StateObservation stateObs, Comparator<StateEstimate> algorithm, int depth) {
        openSet = new PriorityQueue<>(algorithm);
        depthSatisfied = new PriorityQueue<>(algorithm);
        StateEstimate.setEnvironment(getKeyPos(stateObs), getGoalPos(stateObs));
        availableActions = stateObs.getAvailableActions();
        initSets(stateObs);
        minDepth = depth;
    }

    private void initSets(StateObservation stateObs) {
        openSet.clear();
        closedSet.clear();
        openSetForTesting.clear();
        depthSatisfied.clear();
        openSet.add(new StateEstimate(stateObs, 0));
        openSetForTesting.add(openSet.peek());
    }

    private boolean reconstructPath(StateEstimate current, ElapsedCpuTimer elapsedTimer) {
        path = null;
        if (tmpPath == null)
            tmpPath = new Stack<>();
        if (current != null)
            tmpCurrent = current;
        while (tmpCurrent.previousState != null && elapsedTimer.remainingTimeMillis() > REMAINING_LIMIT / 2) {
            tmpPath.push(tmpCurrent.stateObservation.getAvatarLastAction());
//            current = tmpCurrent;
            tmpCurrent = tmpCurrent.previousState;
        }
        if (tmpCurrent.previousState != null)
            return false;
        path = tmpPath;
        tmpPath = null;
//        visitedSet.add(current);
        visitedSet.add(tmpCurrent);
        tmpCurrent = null;
        return true;
    }

    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        switch (stateMachine) {
            case FIRST_ACTION:
                stateMachine = StateMachine.RUNNING;
            case DEAD:
                return Types.ACTIONS.ACTION_NIL;
            case RECONSTRUCTING:
                if (reconstructPath(null, elapsedTimer))
                    stateMachine = StateMachine.RUNNING;
                else
                    return Types.ACTIONS.ACTION_NIL;
        }
        if (path != null && !path.empty()) {
            return path.pop();
        }

        long remaining = elapsedTimer.remainingTimeMillis();
        double averageTime = 0, accumulatedTime = 0;
        int loopCounter = 0;

        if (shouldContinue)
            shouldContinue = false;
        else
            initSets(stateObs);

        while (!openSet.isEmpty() && remaining > 2 * averageTime && remaining > REMAINING_LIMIT) {
            ElapsedCpuTimer elapsedLoopTimer = new ElapsedCpuTimer();

            StateEstimate current = openSet.poll();
            openSetForTesting.remove(current);
            closedSet.add(current);

            if (current.goalTest()) {
                if (!StateEstimate.gotKey) {
                    StateEstimate.setGotKey();
//                    System.err.println("Got key! Steps: " + current.distanceSoFar);
                    initSets(current.stateObservation); // re-initialise
                    visitedSet.clear();
                }
//                else
//                    System.err.println("Reached destination! Steps: " + current.distanceSoFar);
                stateMachine = StateMachine.RECONSTRUCTING;
                if (!reconstructPath(current, elapsedTimer))
                    return Types.ACTIONS.ACTION_NIL;
                stateMachine = StateMachine.RUNNING;
                return path.pop();
            }

            if (current.distanceSoFar >= minDepth)
                depthSatisfied.add(current);

            for (Types.ACTIONS action: availableActions) {
                StateEstimate neighbor = new StateEstimate(current, action);
                if (neighbor.distanceTotal >= StateEstimate.INFINITY)
                    continue; // too far to reach
                if (neighbor.avatarPosition.equals(current.avatarPosition))
                    continue; // no action really performed. (e.g. against a wall)
                if (closedSet.contains(neighbor))
                    continue; // already evaluated.
                if (visitedSet.contains(neighbor))
                    continue; // visited.

                StateEstimate ceil = openSetForTesting.ceiling(neighbor);
                if (ceil != null && comparator.compare(ceil, neighbor) == 0) {
                    if (neighbor.distanceSoFar >= ceil.distanceSoFar)
                        continue; // not a better path
                    openSet.remove(neighbor);
                    openSetForTesting.remove(neighbor);
                }
                neighbor.previousState = current;
                openSet.add(neighbor);
                openSetForTesting.add(neighbor);
            }

            loopCounter++;
            accumulatedTime += elapsedLoopTimer.elapsedMillis();
            averageTime = accumulatedTime / loopCounter;
            remaining = elapsedTimer.remainingTimeMillis();
        }
        if (!depthSatisfied.isEmpty()) {
            if (reconstructPath(depthSatisfied.peek(), elapsedTimer)) {
                Types.ACTIONS action = Types.ACTIONS.ACTION_NIL;
                if (!path.isEmpty())
                    action = path.pop();
                path = null;
                return action;
            }
        }
        else if (openSet.isEmpty()) {
            System.err.println("Failed!");
            stateMachine = StateMachine.DEAD;
        }
        else
            shouldContinue = true;
        return Types.ACTIONS.ACTION_NIL;
    }
}
