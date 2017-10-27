package rl_sim.gui.state;/*
 * Created on Oct 23, 2004
 */


import java.awt.*;

public class State extends Point {

    public State(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /* used for debugging only...
     */
    public void printState() {
        System.out.print("rl_sim.gui.state.State=<" + x + "," + y + ">");
    }

    /*copies the newState object in to this state
     */
    public void copy(State newState) {
        x = newState.x;
        y = newState.y;
    }

    public boolean equals(Object Obj) {
        State st = (State) Obj;
        return (x == st.x && y == st.y);
    }

}
