package rl_sim.backend.environment;

import org.jetbrains.annotations.NotNull;
import rl_sim.gui.Utility;

import java.io.Serializable;
import java.util.Vector;

public class Maze implements Environment, Serializable {

    public int height, width;
    public Vector<State> goals = new Vector<>();
    public Vector<Wall> walls = new Vector<>();

    /**
     * Constructor with the size of this maze.
     *
     * @param width
     * @param height
     */
    public Maze(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void addGoal(@NotNull State state) {
        //dont add goals if they lie outside the maze dimensions
        if (state.x < 0 || state.y < 0 || state.x >= width || state.y >= height)
            return;

        if (!goals.contains(state))
            goals.add(state);
        else goals.remove(state);
    }

    public boolean isValidTransition(State curr, State state) {
        Wall possibleWall = new Wall(curr.x, curr.y, getDirection(curr, state));

        if (walls.contains(possibleWall)) // A wall
            return false;

        // Niether wall not outside boundary
        return true;
    }

    public Vector<State> getSuccessors(State currState) {
        Vector<State> succs = new Vector<State>();

        for (int i = 0; i < Action.capabilities(); i++) {
            State newState = ActionHandler.performAction(currState, Action.valueOf(i));
            succs.add(newState);
        }
        return succs;
    }

    public void toggleWall(Wall newWall) {
        //dont add walls if they lie outside the maze dimensions
        if (newWall.x < 0 || newWall.y < 0 || newWall.x >= width || newWall.y >= height)
            return;

        if (!isWallPresent(newWall)) {
            walls.add(newWall);
            System.out.println("Added wall: " + newWall.toString() + "");
        } else {
            //remove the wall at that location
            walls.remove(newWall);
            System.out.println("Removed wall: " + newWall.toString() + "");
        }
    }

    public double getReward(State curr, State state) {
        Wall possibleWall = new Wall(curr.x, curr.y, getDirection(curr, state));
        int index = walls.indexOf(possibleWall);
        if (index != -1) {
            Wall w = walls.get(index);
            return w.penalty;
        }
        return 0;
    }

    /**
     * returns all possible valid successors of the state 'currState'
     */
    Vector<State> getValidSuccessors(State currState) {
        Vector<State> succs = new Vector<>();
        for (int i = 0; i < Action.capabilities(); i++) {
            State newState = ActionHandler.performAction(currState, Action.valueOf(i));
            if (isValidTransition(currState, newState))
                succs.add(newState);
        }
        return succs;
    }

    /**
     * returns the direction (UP=0, RIGHT=1, DOWN=2, LEFT=3) in which state 'st' lies with respect
     * to the state 'curr'.
     */
    private int getDirection(State curr, State st) {
        switch (curr.x - st.x) {
            case -1:
                return Wall.RIGHT;
            case 1:
                return Wall.LEFT;
        }
        switch (curr.y - st.y) {
            case -1:
                return Wall.UP;
            case 1:
                return Wall.DOWN;
        }
        return Wall.UP; //returning something by default, exception shud be used here
    }

    /**
     * returns true if a wall is present at the specified location else returns false.
     */
    private boolean isWallPresent(Wall newWall) {
        boolean wallPresent = false;
        Wall w;
        for (Object wall : walls) {
            w = (Wall) wall;
            if (w.x == newWall.x && w.y == newWall.y && w.dir == newWall.dir) {
                wallPresent = true;
                break;
            }
        }
        return wallPresent;
    }

    /**
     * only for debugging purposes...
     * prints on the console all the walls that are present in the maze
     */
    public void printWalls() {
        Wall w;
        Utility.show("------------printing walls------------");
        if (walls.isEmpty())
            Utility.show("no walls yet");

        for (Object wall : walls) {
            w = (Wall) wall;
            Utility.show("wall at " + w.x + " " + w.y + " " + w.dir + " "
                    + w.penalty);
        }
        Utility.show("------------done------------");
    }

    /**
     * only for debugging purposes...
     * prints on the console all the goals that are present in the maze
     */
    public void printGoals() {
        State st;
        Utility.show("-------------------------");
        if (goals.isEmpty())
            Utility.show("no goals yet");
        for (Object goal : goals) {
            st = (State) goal;
            Utility.show("goal at " + st.x + " " + st.y);
        }
    }
}