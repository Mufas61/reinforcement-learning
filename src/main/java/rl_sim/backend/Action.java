package rl_sim.backend;

import org.jetbrains.annotations.NotNull;

public enum Action {
    NONE(-1), UP(0), RIGHT(1), DOWN(2), LEFT(3);

    /**
     * Value for the direction.
     */
    private int value;

    /**
     * Constructor.
     *
     * @param value Value for the direction.
     */
    Action(int value) {
        this.value = value;
    }

    /**
     * Value for the direction.
     *
     * @return Value for the direction.
     */
    public int getValue() {
        return value;
    }

    /**
     * Instance for direction value.
     *
     * @param value between -1 and 3.
     * @return Not null.
     */
    @NotNull
    public static Action valueOf(final int value) {
        switch (value) {
            case -1:
                return NONE;
            case 0:
                return UP;
            case 1:
                return RIGHT;
            case 2:
                return DOWN;
            case 3:
                return LEFT;
            default:
                throw new RuntimeException("Internal Error");

        }
    }

    /**
     * Returns amount of possible actions.
     *
     * @return amount of possible actions.
     */
    public static int capabilities() {
        return values().length - 1;
    }
}
