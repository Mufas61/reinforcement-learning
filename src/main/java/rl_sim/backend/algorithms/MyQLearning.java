package rl_sim.backend.algorithms;

import com.google.common.base.Preconditions;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import rl_sim.backend.environment.Action;
import rl_sim.backend.environment.ActionHandler;
import rl_sim.backend.environment.Maze;
import rl_sim.backend.environment.State;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;

public class MyQLearning {
    private static final Logger LOG = Logger.getLogger(MyQLearning.class);

    boolean isInitialized = false;

    /**
     * Holds all information for the gui that wouldn't been necessary for the algorithm.
     */
    @NotNull
    InfoForGUI infoForGUI;

    /**
     * Penalty per step.
     */
    private double costPerStep = -0.5;

    /**
     * Percentage of how much of the new learned should be calculated to the overall result.
     */
    double learning = 0.7;

    /**
     * Discounting rate. Every new episode will be attenuated.
     */
    double discounting;

    /**
     * Percentage of exploration. Given chance that a random {@link Action} will be chosen and not the best.
     */
    private double exploration = 0.1;

    /**
     * Environment.
     */
    @NotNull
    final Maze environment;

    /**
     * First state in every new round. Bottom left corner.
     */
    private State start;

    /**
     * Holds current state.
     */
    State currState;

    /**
     * Holds next planed state.
     */
    State nextState;

    /**
     * Amount of done episodes (start -> goal).
     */
    private int episodes = 0;

    /**
     * Q(s,a) - immediate reward + discounted reward.
     * Q-value for every state-action-combination.
     */
    @NotNull
    private final Map<State, Map<Action, Double>> actionValueFunction;

    /**
     * Constructor.
     *
     * @param environment Not null.
     */
    public MyQLearning(@NotNull final Maze environment) {
        this.environment = environment;
        this.infoForGUI = new InfoForGUI();
        this.actionValueFunction = new HashMap<>(); // TODO maybe treemap to find keys faster
        initialize();
    }

    /**
     * For all s elem S, a elem A
     * Q(s,a) = 0
     */
    public void initialize() {
        for (int width = 0; width < environment.width * environment.height; width++) {
            for (int height = 0; height < environment.height; height++) {
                final HashMap<Action, Double> actionValueMap = new HashMap<>();

                for (Action action : Action.valuesWithoutNone()) {
                    actionValueMap.put(action, 0d); // init all actions for every state with zero
                }

                actionValueFunction.put(new State(width, height), actionValueMap); // init all states
            }
        }
        start = new State(0, 0); // bottom left corner.
        currState = start;
        isInitialized = true;
        episodes = 0;
        discounting = 0.9; // reinitialization because will be changed for every episode
    }

    /**
     * One step in the algorithm. Updates {@link #currState}
     */
    public void computeStep() {
        Preconditions.checkState(isInitialized, "Algorithm has to be initialized!");

        LOG.debug("======== STEP ========");
        // break condition if not computed by episode
        if (reachedGoal()) {
            prepareNewEpisode();
            return;
        }

        // choose an action a TODO e-greedy-stuff
        final Action action = chosePolicy(currState);

        nextState = ActionHandler.performAction(currState, action);

        double currQ = getActionValue(currState, action);
        LOG.debug(String.format("Current Q(%s,%s) => %f", currState, action, currQ));

        //If not a valid transition stay in same state and add penalty
        double reward = handleTransition(currState, nextState);
        LOG.debug(String.format("Reward/Penalty for %s->%s => %f", currState, nextState, reward));

        final double nextQ = bestQ(nextState).getValue();
        LOG.debug(String.format("min_a(Q(%s,a)) => %f", nextState, nextQ));

        LOG.debug("currQ += learning * (r + (discounting * nextQ) - currQ)");
        // Q(s,a) <- Q(s,a) + a [r + y * max_a'(Q(s',a')) - Q(s,a)]
//        currQ = (1 - learning) * currQ + learning * (reward + discounting * nextQ); // TODO test this
        double newQ = currQ + learning * (reward + discounting * nextQ - currQ);
        LOG.debug(String.format("%f = %f + %f * (%f + (%f * %f) - %f)", newQ, currQ, learning, reward, discounting, nextQ, currQ));
        currQ = newQ;
        updateActionValue(currState, action, currQ); // update Q(s,a)

        infoForGUI.policy[currState.x][currState.y] = bestQ(currState).getKey().getValue(); // update for gui
        currState = environment.isValidTransition(currState, nextState) ? nextState : currState;
    }

    /**
     * Reset after goal has reached.
     */
    void prepareNewEpisode() {
        LOG.info(">>> prepare for episode: " + (episodes + 1) + " <<<");
        episodes++;
        currState = start;
    }

    /**
     * Returns reward/penalty based on the transition.
     * If {@link Maze#isValidTransition(State, State)}
     * is true only {@link #costPerStep} will be calculated and
     * will stay in same state.
     *
     * @param currState Not null.
     * @param nextState Not null.
     * @return amount of reward/penalty for this transition.
     */
    double handleTransition(@NotNull final State currState,
                            @NotNull final State nextState) {
        if (environment.isValidTransition(currState, nextState)) {
            infoForGUI.receivedPenalty = false;
        } else {
            infoForGUI.receivedPenalty = true;
            this.nextState = currState;
        }
        return environment.getReward(currState, nextState) + costPerStep; //add reward or penalty
    }

    /**
     * Q(s,a) - Q value for an given state and action.
     *
     * @param currState    Not null.
     * @param chosenAction Not null.
     * @return TODO return
     */
    Double getActionValue(@NotNull final State currState,
                          @NotNull final Action chosenAction) {
        final Double qValue = actionValueFunction.get(currState).get(chosenAction);
        checkArgument(qValue != null);
        return qValue;
    }

    /**
     * Updates a given q-value.
     *
     * @param currState    Not null.
     * @param chosenAction Not null.
     * @param qValue       Not null.
     */
    void updateActionValue(@NotNull final State currState,
                           @NotNull final Action chosenAction,
                           @NotNull final Double qValue) {
        actionValueFunction.get(currState).replace(chosenAction, qValue);
    }

    /**
     * Returns an chosen action by a policy.
     *
     * @param currState Not null.
     * @return TODO return
     */
    Action chosePolicy(@NotNull final State currState) { // TODO test this
        LOG.debug(">>> Chose policy");
        final Action bestAction = bestQ(currState).getKey();
        Action chosenAction = bestAction;

        chosenAction = handleExploration(chosenAction);

        boolean hashChosenBestAction = chosenAction == bestAction;
        infoForGUI.isBestAct = hashChosenBestAction;
        LOG.debug("Chosen Action:" + chosenAction + ((hashChosenBestAction) ? "" : "-> best was:" + bestAction));
        LOG.debug("<<<");
        return chosenAction;
    }

    /**
     * Handles the chance for an exploration.
     *
     * @param chosenAction Not null.
     * @return a random {@link Action} by the chance of the given {@link #exploration} or the given object.
     */
    @NotNull
    private Action handleExploration(@NotNull Action chosenAction) {
        for (int i = 0; i < Action.capabilities(); i++) {
            final double chanceForEachAction = exploration / (Action.capabilities());
            final int rangeOfAction = i + 1;
            if (Math.random() <= rangeOfAction * chanceForEachAction) {
                chosenAction = Action.valueOf(i);
                break;
            }
        }
        return chosenAction;
    }

    /**
     * Returns the best q-value for an given state.
     *
     * @param currState Not null.
     * @return TODO return really Nullable?
     */
    Map.Entry<Action, Double> bestQ(@NotNull final State currState) { // TODO test this
        final Map<Action, Double> actionValueMap = actionValueFunction.get(currState);
        final Set<Map.Entry<Action, Double>> entrySet = actionValueMap.entrySet();
        return entrySet
                .stream()
                .max(Comparator.comparingDouble(Map.Entry::getValue))
                .orElse(null);

    }

    /**
     * @return {@link #currState}
     */
    public State getCurrState() {
        return currState;
    }

    /**
     * @return True, if goal is reached.
     */
    public boolean reachedGoal() {
        return environment.goals.contains(currState);
    }

    @NotNull
    public Map<State, Map<Action, Double>> getQsa() {
        return actionValueFunction;
    }

    /**
     * Sets a properties by using {@link Property}.
     *
     * @param property Not null.
     * @param value    Not null.
     */
    public void setProperty(@NotNull final Property property,
                            @NotNull final String value) {
        switch (property) {
            case Penalty:
                costPerStep = Double.parseDouble(value);
                break;
            case LearningRate:
                learning = Double.parseDouble(value);
                break;
            case ExplorationRate:
                exploration = Double.parseDouble(value);
                break;
            case DiscountingRate:
                discounting = Double.parseDouble(value);
                break;
            default:
                throw new IllegalArgumentException(property.name() + "not implemented!s");

        }
    }

    /**
     * Possible accessible field.
     */
    public enum Property {
        Penalty, LearningRate, ExplorationRate, DiscountingRate
    }

    /**
     * Returns an object for all information for the gui.
     *
     * @return Not null.
     */
    @NotNull
    public InfoForGUI getInfoForGUI() {
        return infoForGUI;
    }

    /**
     * Holds information for gui.
     */
    public class InfoForGUI {

        public boolean receivedPenalty = false;

        public boolean isBestAct = false;

        public int[][] policy = new int[environment.width][environment.height];

        private InfoForGUI() {
            //Initialise policy for all states as -1
            for (int i = 0; i < policy.length; i++)
                for (int j = 0; j < policy[i].length; j++)
                    policy[i][j] = -1;
        }

        public double[][] getStateValues() {
            double[][] stateValue = new double[environment.width][environment.height];
            for (int i = 0; i < environment.width; i++)
                for (int j = 0; j < environment.height; j++)
                    stateValue[i][j] = bestQ(new State(i, j)).getValue();
            return stateValue;
        }
    }

}
