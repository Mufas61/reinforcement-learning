package rl_sim.gui.state;/*
 * Created on Dec 21, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

/**
 * @author ryk
 * <p>
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

import java.util.Vector;

public class StateActionInfo {

    public int actionCounter;    //maintains the number of times this action has been taken
    Vector succs;        //maintains successors of this state and numofvisits to each successor
    public double totalReward;
    public double qsa;            //q-value associated with this this action

    //constructor for predecessor and successor
    StateActionInfo() {
        actionCounter = 0;
        succs = new Vector();
        qsa = 0;
        totalReward = 0;
    }

    void updateData(State succSt, double reward) {
        totalReward += reward;
        actionCounter += 1;
        addSuccessor(succSt);
    }

    private void addSuccessor(State succSt) {
        StateAndVisits snv = new StateAndVisits(succSt);
        int positionOfNext = succs.indexOf(snv);

        if (-1 == positionOfNext)
            succs.add(snv);
        else {
            snv = (StateAndVisits) succs.get(positionOfNext);//snv points to that particular element in the vector
            snv.incrementVisits();
        }
    }

    public double getReward() {
        if (totalReward == 0)
            return 0;

        return totalReward / actionCounter;
    }
}
