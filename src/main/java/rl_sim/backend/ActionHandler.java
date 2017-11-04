package rl_sim.backend;

import rl_sim.gui.Utility;

public class ActionHandler {

    /**
     * Performs the specified action (UP, RIGHT, DOWN, LEFT) on the state and returns the resulting new state.
     */
    public static State performAction(State state, Action action) {
        State newSt;
        switch (action) {
            case UP:
                newSt = new State(state.x, state.y + 1);
                break;
            case RIGHT:
                newSt = new State(state.x + 1, state.y);
                break;
            case DOWN:
                newSt = new State(state.x, state.y - 1);
                break;
            case LEFT:
                newSt = new State(state.x - 1, state.y);
                break;
            default:
                newSt = state;
        }
        return newSt;
    }

    public static State performAction(State st, Action action, double pjog) {
        //Random randomGen = new Random();
        double rand = Math.random();
        State newSt;
        double randActProb = pjog / (Action.capabilities());
        int choosenAction = action.getValue();

        for (int i = 0; i < Action.capabilities(); i++) {
            if (rand < (i + 1) * randActProb) {
                choosenAction = i;
                break;
            }
        }
        Utility.show("==================");
        Utility.show("Action to be Taken:" + action);
        Utility.show("Rand" + rand);
        Utility.show("Action Taken:" + choosenAction);
        Utility.show("==================");

        newSt = performAction(st, Action.valueOf(choosenAction));
        return newSt;
    }


}
