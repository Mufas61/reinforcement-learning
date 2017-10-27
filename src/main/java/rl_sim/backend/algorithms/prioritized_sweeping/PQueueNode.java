package rl_sim.backend.algorithms.prioritized_sweeping;/*
 * Created on Jan 18, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

import rl_sim.gui.state.State;

/**
 * @author ryk
 * <p>
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PQueueNode {

    public State state;
    public double priority;

    public PQueueNode(State _st, double _priority) {
        state = new State(_st.x, _st.y);
//		state.copy(_st);
        priority = _priority;
    }

    public boolean equals(Object Obj) {
        PQueueNode node = (PQueueNode) Obj;
        return (state.equals(node.state));
    }

}
