package rl_sim.gui;

import rl_sim.gui.state.State;

public class Action {
    public final static int numActions = 4;
    public final static int UP = 0;
    public final static int RIGHT = 1;
    public final static int DOWN = 2;
    public final static int LEFT = 3;

    public Action() {
    }

    /**
     * Performs the specified action (UP, RIGHT, DOWN, LEFT) on the state 'st'
     * and returns the resulting new state
     */
    public static State performAction(State st, int action) {
        State newSt;
        switch (action) {
            case UP:
                newSt = new State(st.x, st.y + 1);
                break;
            case RIGHT:
                newSt = new State(st.x + 1, st.y);
                break;
            case DOWN:
                newSt = new State(st.x, st.y - 1);
                break;
            case LEFT:
                newSt = new State(st.x - 1, st.y);
                break;
            default:
                newSt = st;
        }
        return newSt;
    }

    public static State performAction(State st, int action, double pjog) {
        //Random randomGen = new Random();
        double rand = Math.random();
        State newSt;
        double randActProb = pjog / (Action.numActions);
        int choosenAction = action;

        for (int i = 0; i < Action.numActions; i++) {
            if (rand < (i + 1) * randActProb) {
                choosenAction = i;
                break;
            }
        }
        Utility.show("==================");
        Utility.show("rl_sim.gui.Action to be Taken:" + action);
        Utility.show("Rand" + rand);
        Utility.show("rl_sim.gui.Action Taken:" + choosenAction);
        Utility.show("==================");

        newSt = performAction(st, choosenAction);
        return newSt;
    }

}
