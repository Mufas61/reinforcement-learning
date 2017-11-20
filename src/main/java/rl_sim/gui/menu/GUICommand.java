package rl_sim.gui.menu;

import org.jetbrains.annotations.NotNull;

/**
 * Commands for the gui.
 */
public enum GUICommand {

    /**
     * Shutdown the program.
     */
    QUIT("Quit"),

    /**
     * Open maze editor.
     */
    EDIT_MAZE("Edit Maze"),

    /**
     * Open p swepping simulator
     */
    P_SWEPPING_SIM("PSSim"),

    /**
     * Open q learning simulator.
     */
    Q_LEARNING_SIM("QSim"),

    /**
     * Open q learning simulator.
     */
    MY_Q_LEARNING_SIM("MyQSim"),

    /**
     * Open policy iteration.
     */
    POLICY_ITERATION("Policy"),

    /**
     * Open value iteration.
     */
    VALUE_ITERATION("Value");

    /**
     * Holds command as string.
     */
    @NotNull
    private String value;

    /**
     * Constructor.
     *
     * @param value Not null.
     */
    GUICommand(@NotNull String value) {
        this.value = value;
    }

    /**
     * Returns command as a string.
     *
     * @return Not null.
     */
    @NotNull
    public String getValue() {
        return value;
    }
}
