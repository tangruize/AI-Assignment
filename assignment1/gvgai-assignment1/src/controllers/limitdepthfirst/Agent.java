//package controllers.limitdepthfirst;
//
//import core.game.Observation;
//import core.game.StateObservation;
//import core.player.AbstractPlayer;
//import ontology.Types;
//import tools.ElapsedCpuTimer;
//import tools.Vector2d;
//
//import java.util.ArrayList;
//
//class tree {
//    ArrayList<tree> childs;
//    tree parent;
//    StateObservation state;
//    boolean searchable;
//    int badChildren;
//
//    void construct() {
//        final Types.ACTIONS[] actions = {
//                Types.ACTIONS.ACTION_UP, Types.ACTIONS.ACTION_DOWN,
//                Types.ACTIONS.ACTION_LEFT, Types.ACTIONS.ACTION_RIGHT
//        };
//
//        for (Types.ACTIONS a: actions) {
//            StateObservation s = state.copy();
//            s.advance(a);
//            childs.add(new tree(s, this));
//        }
//    }
//
//    private void check() {
//        if (state.getGameWinner() == Types.WINNER.PLAYER_LOSES)
//            searchable = false;
//        for (tree p = parent; p != null && searchable; p = p.parent) {
//            if (state.equalPosition(p.state))
//                searchable = false;
//        }
//        if (!searchable)
//            parent.badChildren++;
//    }
//
//    tree(StateObservation s, tree p) {
//        childs = new ArrayList<>();
//        state = s;
//        searchable = true;
//        parent = p;
//        badChildren = 0;
//        if (p == null)
//            construct();
//        else
//            check();
//    }
//}
//
//public class Agent extends AbstractPlayer {
//    private Vector2d goalPos, keyPos;
//    private final int LIMIT_DEPTH = 2;
//    private Vector2d getTypePos(ArrayList<Observation>[] positions, int category, int itype) {
//        for (ArrayList<Observation> i : positions) {
//            if (i.size() == 1) {
//                Observation ob = i.get(0);
//                if (ob.category == category && ob.itype == itype)
//                    return ob.position;
//            }
//        }
//        return new Vector2d(-1, -1);
//    }
//    private Vector2d getGoalPos(StateObservation stateObs) {
//        return getTypePos(stateObs.getImmovablePositions(), Types.TYPE_STATIC, 7);
//    }
//    private Vector2d getKeyPos(StateObservation stateObs) {
//        return getTypePos(stateObs.getMovablePositions(), Types.TYPE_MOVABLE ,6);
//    }
//
//    /**
//     * initialize all variables for the agent
//     * @param stateObs Observation of the current state.
//     * @param elapsedTimer Timer when the action returned is due.
//     */
//    public Agent(StateObservation stateObs, ElapsedCpuTimer elapsedTimer){
//        goalPos = getGoalPos(stateObs);
//        keyPos = getKeyPos(stateObs);
//    }
//
//    private ArrayList<tree> trees;
//    private tree goal = null;
//    private int avatarType;
//    private ArrayList<StateObservation> goalStates = new ArrayList<>();
//    private ArrayList<StateObservation> reachedStates = new ArrayList<>();
//
//    private boolean goalTest(StateObservation state) {
//        if (avatarType == 4)
//            return state.getGameWinner() == Types.WINNER.PLAYER_WINS;
//        return state.getAvatarPosition().equals(keyPos);
//    }
//
//    private boolean isGoodState(StateObservation state) {
//        ArrayList<Observation>[] movablePositions = state.getMovablePositions();
//        for (ArrayList<Observation> i: movablePositions) {
//            if (!i.isEmpty() && i.get(0).category == Types.TYPE_MOVABLE && i.get(0).itype == 8) {
//                for (Observation ob: i) {
//                    if (ob.position.x == keyPos.x && ob.position.y == keyPos.y)
//                        return false;
//                }
//                break;
//            }
//        }
//        return true;
//    }
//
////    private boolean isBoxMoved(StateObservation state1, StateObservation state2) {
////        ArrayList<Observation>[] ob1 = state1.getMovablePositions();
////        ArrayList<Observation>[] ob2 = state2.getMovablePositions();
////        ArrayList<Observation> obResult1 = ob1.length == 2 ? ob1[1] : ob1[0];
////        ArrayList<Observation> obResult2 = ob2.length == 2 ? ob2[1] : ob2[0];
////        return !obResult1.equals(obResult2);
////    }
//
//    private boolean isBoxMovable(StateObservation parent, StateObservation child) {
//        ArrayList<Observation>[] ob1 = parent.getMovablePositions();
//        ArrayList<Observation>[] ob2 = child.getMovablePositions();
//        ArrayList<Observation> obResult1 = ob1.length == 2 ? ob1[1] : ob1[0];
//        ArrayList<Observation> obResult2 = ob2.length == 2 ? ob2[1] : ob2[0];
//        if (obResult1.equals(obResult2))
//            return true;
//        ArrayList<Observation>[][] observationGrid = child.getObservationGrid();
//        final int[] X = {1, -1, 0, 0};
//        final int[] Y = {0, 0, 1, -1};
//        Vector2d pos = child.getAvatarPosition();
//        int x = (int)pos.x / 50;
//        int y = (int)pos.y / 50;
//        switch (child.getAvatarLastAction()) {
//            case ACTION_LEFT:
//                x -= 1;
//                break;
//            case ACTION_RIGHT:
//                x += 1;
//                break;
//            case ACTION_UP:
//                y -= 1;
//                break;
//            case ACTION_DOWN:
//                y += 1;
//                break;
//        }
//        int[] direction = new int[]{0, 0};
//        for (int i = 0; i < 4; ++i) {
//            int x1 = x + X[i], y1 = y + Y[i];
//            if (x1 < observationGrid.length && y1 < observationGrid[x1].length) {
//                if (observationGrid[x1][y1].size() > 0) {
//                    int itype = observationGrid[x1][y1].get(0).itype;
//                    if (itype == 0 || itype == 7 || itype == 8 || itype == 5)
//                        ++direction[i / 2];
//                }
//            }
//        }
//        if (direction[0] * direction[1] != 0)
//            return false;
//        return true;
//    }
//
//    private boolean isUnvisitedState(StateObservation next) {
//        if (next.getGameWinner() == Types.WINNER.PLAYER_LOSES)
//            return false;
//        for (int i = reachedStates.size() - 1; i >= 0; --i)
//            if (reachedStates.get(i).equalPosition(next))
//                return false;
//        return true;
//    }
//
//    private void DLS(tree t, int depth) {
//        if (goalTest(t.state)) {
//            goal = t;
//            return;
//        }
//        else if (depth == 0) {
////            if (t.searchable)
////                trees.add(t);
//            return;
//        }
//        for (tree child: t.childs) {
//            if (goal != null)
//                return;
//            if (child.searchable) {
//                if (isGoodState(child.state) && isUnvisitedState(child.state)/* && isBoxMovable(t.state, child.state)*/) {
//                    child.construct();
//                    trees.add(child);
//                    DLS(child, depth - 1);
//                }
//                else {
//                    child.searchable = false;
//                    if (child.parent != null)
//                        child.parent.badChildren++;
//                }
//            }
//        }
//    }
//
//    private StateObservation heuristic() {
//        if (trees.isEmpty()) {
//            System.out.println("heuristic no action!");
//            return null;
//        }
//        Vector2d xpos = avatarType == 4 ? goalPos : keyPos;
//        double min = Double.MAX_VALUE, val;
//        int index = -1;
//        for (int i = trees.size() - 1; i >= 0; --i) {
//            tree ti = trees.get(i);
//            if (ti.badChildren < 4) {
//                Vector2d pos = ti.state.getAvatarPosition();
//                val = Math.abs(xpos.x - pos.x) + Math.abs(xpos.y - pos.y);
//                if (val < min) {
//                    min = val;
//                    index = i;
//                }
//            }
//        }
//        if (index == -1) {
//            System.out.println("(2)heuristic no action!");
//            return null;
//        }
//        tree x = trees.get(index), y = x;
//        while (x.parent != null) {
//            y = x;
//            x = x.parent;
//        }
//        return y.state;
//    }
//
//    private StateObservation decision(StateObservation stateObs) {
//        if (!goalStates.isEmpty()) {
//            StateObservation ob = goalStates.get(goalStates.size() - 1);
//            goalStates.remove(goalStates.size() - 1);
//            System.out.println("find: " + ob.getAvatarLastAction());
//            return ob;
//        }
//
//        trees = new ArrayList<>();
//        avatarType = stateObs.getAvatarType();
//        tree x = new tree(stateObs, null);
//        DLS(x, LIMIT_DEPTH);
//        if (goal != null) {
//            while (goal.parent != null) {
//                goalStates.add(goal.state);
//                goal = goal.parent;
//            }
////            Types.ACTIONS a = goal.state.getAvatarLastAction();
//            StateObservation ob = goalStates.get(goalStates.size() - 1);
//            goalStates.remove(goalStates.size() - 1);
//            goal = null;
//            System.out.println("find: " + ob.getAvatarLastAction());
//            return ob;
//        }
//        StateObservation ob =  heuristic();
//        if (ob != null)
//            System.out.println("heuristic: " + ob.getAvatarLastAction());
//        return ob;
//    }
//
//    /**
//     * Use limited-DFS to find a path to win.
//     * @param stateObs Observation of the current state.
//     * @param elapsedTimer Timer when the action returned is due.
//     * @return next action
//     */
//    @Override
//    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
////        Vector2d move = Utils.processMovementActionKeys(Game.ki.getMask());
////        boolean useOn = Utils.processUseKey(Game.ki.getMask());
////
////        //In the keycontroller, move has preference.
////        Types.ACTIONS action = Types.ACTIONS.fromVector(move);
////
////        if(action == Types.ACTIONS.ACTION_NIL && useOn)
////            action = Types.ACTIONS.ACTION_USE;
////        Vector2d goalPos = getGoalPos(stateObs); // 目标的坐标
////        Vector2d keyPos = getKeyPos(stateObs); // 钥匙的坐标
////        if (action != Types.ACTIONS.ACTION_NIL) {
////            System.out.print(" Goal: (" + goalPos.x + ", " + goalPos.y
////                    + "); Key: (" + keyPos.x + ", " + keyPos.y + ")\n");
////        }
////        return action;
//
//        StateObservation a = decision(stateObs);
//        if (a != null) {
//            reachedStates.add(a);
//            return a.getAvatarLastAction();
//        }
//        return Types.ACTIONS.ACTION_NIL;
//    }
//}

package controllers.limitdepthfirst;

import controllers.Algorithms;
import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import tools.ElapsedCpuTimer;

public class Agent extends AbstractPlayer {
    private final Algorithms algorithms;

    public Agent(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        algorithms = new Algorithms(stateObs, Algorithms.heuristicAlgorithm, 43);
                //Algorithms.MIN_DEPTH);
    }

    @Override
    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        return algorithms.act(stateObs, elapsedTimer);
    }
}
