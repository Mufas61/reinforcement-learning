package rl_sim.backend.environment;

import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

public class Wall implements Serializable {
    public int x;
    public int y;
    public int dir;

    /**
     * Penalty for a step against this wall.
     */
    public int penalty;

    /**
     * Location of the wall.
     */
    public static final int UP = 0, RIGHT = 1, DOWN = 2, LEFT = 3;

    /**
     * @param x       x-axis.
     * @param y       y-axis.
     * @param dir     direction.
     * @param penalty for a step against this wall.
     */
    public Wall(int x, int y, int dir, int penalty) {
        this.x = x;
        this.y = y;
        this.dir = dir;
        this.penalty = penalty;
    }

    Wall(int x, int y, int dir) {
        this.x = x;
        this.y = y;
        this.dir = dir;
        this.penalty = 0;
    }

    /**
     * Walls are equals when {@link #x}, {@link #y} and {@link #dir} are equal.
     *
     * @param obj May be null.
     * @return true if objects are equal.
     */
    public boolean equals(@Nullable Object obj) {
        if (obj == null)
            return false;
        if (!(obj instanceof Wall))
            return false;

        Wall w = (Wall) obj;

        return (x == w.x && y == w.y && dir == w.dir);
    }

    @Override
    public String toString() {
        return "Wall{" +
                "x=" + x +
                ", y=" + y +
                ", dir=" + dir +
                ", penalty=" + penalty +
                '}';
    }
}