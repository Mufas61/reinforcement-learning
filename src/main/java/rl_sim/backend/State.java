package rl_sim.backend;

import java.awt.*;

public class State extends Point {

    public State(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Used for debugging only...
     */
    public void printState() {
        System.out.print("State=<" + x + "," + y + ">");
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

}
