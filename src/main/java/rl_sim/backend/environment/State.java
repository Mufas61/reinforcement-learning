package rl_sim.backend.environment;

import java.awt.*;

public class State extends Point { // TODO cut gui dependency

    private static final long serialVersionUID = -5276940640259749850L;

    /**
     * Reward, if state is a goal.
     */
    public static final double REWARD_FOR_GOAL = 100;

    public State(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Copies the newState object in to this state
     */
    public void copy(State newState) {
        x = newState.x;
        y = newState.y;
    }

    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (!(obj instanceof State))
            return false;

        State st = (State) obj;
        return (x == st.x && y == st.y);
    }

    // TODO valueOf with cacheing

    @Override
    public String toString() {
        return String.format("(x:%d,y:%d)", x, y);
    }
}
