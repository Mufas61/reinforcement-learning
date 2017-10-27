package rl_sim.gui.state;/*
 * Created on Jan 18, 2005
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
public class StateAndVisits {
    public State state;
    private int numVisits;

    public StateAndVisits(State _st) {
        state = new State(_st.x, _st.y);
//		state.copy(_st);
        numVisits = 1;
    }

    public void incrementVisits() {
        numVisits += 1;
    }

    public int getVisits() {
        return numVisits;
    }

    public boolean equals(Object Obj) {
        StateAndVisits snv = (StateAndVisits) Obj;
        return state.equals(snv.state);
    }

}
