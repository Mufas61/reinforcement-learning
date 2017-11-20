package rl_sim.backend.environment;

import org.jetbrains.annotations.NotNull;

import java.util.Vector;

public interface Environment {

    /**
     * Adds the goal state to the maze.
     * If a goal already exists at that position then the goal is removed from that location.
     * This enables goal addition and deletion using the GUI.
     *
     * @param state Not null.
     */
    void addGoal(@NotNull State state);

    /**
     * Determines if the transition from current state to next state is a valid transition
     * a transition is invalid when there is a wall between the current state and next state OR
     * the next state lies outside the maze boundary
     */
    boolean isValidTransition(State curr, State state);

    /**
     * returns all the successors (valid successors as well as invalid successors)
     * of the state 'currState'
     */
    Vector<State> getSuccessors(State currState);

    /**
     * Adds a wall to the maze or removes it when already present.
     */
    void toggleWall(Wall newWall);

    /**
     * returns the reward which will result if there is an attempt to make a transition from
     * state 'curr' to state 'state'. returns penalty if the transition is invalid else returns zero
     */
    double getReward(State curr, State state);
}
