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

    private boolean isInitialized = false;

    /**
     * Q(s,a) - immediate reward + discounted reward.
     * Q-value for every state-action-combination.
     */
    @NotNull
    private final Map<State, Map<Action, Double>> q_sa;

    /**
     * Holds all information for the gui that wouldn't been necessary for the algorithm.
     */
    @NotNull
    private InfoForGUI infoForGUI;

    /**
     * First state in every new round. Bottom left corner.
     */
    private State start;

    /**
     * Penalty per step.
     */
    private int costPerStep = 1;

    /**
     * Learning rate. // TODO Eng.: Neu erlerntes soll nicht alles zuvor gelernte überschreiben.
     */
    private double learning = 0.2;

    /**
     * Discounting rate. TODO Eng.: Jeder neue Schritt wird abgeschwächt.
     */
    private double discounting = 0.9;

    /**
     * Exploration rate. // TODO Eng.: Kleine Wahrscheinlichkeit eine andere Aktion als die beste zu wählen um vlt. bessere Wege zu entdecken.
     */
    private double exploration = 0.1;

    /**
     * Environment.
     */
    @NotNull
    private final Maze environment;

    /**
     * Algorithm has finished -> best way has been found.
     */
    private boolean converged;

    /**
     * Holds current state.
     */
    private State currState;

    /**
     * Holds next planed state.
     */
    private State nextState;

    public int episodes = 0;

    public MyQLearning(@NotNull final Maze environment) {
        this.environment = environment;
        this.infoForGUI = new InfoForGUI();
        this.q_sa = new HashMap<>(); // TODO maybe treemap to find keys faster
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

                q_sa.put(new State(width, height), actionValueMap); // init all states
            }
        }
        start = new State(0, 0); // bottom left corner.
        currState = start;
        isInitialized = true;
        converged = false;
        episodes = 0;
        discounting = 0.9; // because will be change for every episode
        // TODO update properties here
    }

    /**
     * Runs the algorithm.
     */
    public void compute() {
        Preconditions.checkState(isInitialized, "Algorithm has to be initialized!");
        do {
            computeEpisode();
        } while (converged); // Best way has been found
    }

    /**
     * Runs one episode (from start to goal) of the algorithm.
     */
    public void computeEpisode() {
        Preconditions.checkState(isInitialized, "Algorithm has to be initialized!");

        currState = start;
        do {
            computeStep();
        } while (reachedGoal()); //until s ist ein endzustand);
    }

    /**
     * One step in the algorithm. Updates {@link #currState}
     */
    public void computeStep() {
        Preconditions.checkState(isInitialized, "Algorithm has to be initialized!");
        LOG.debug("======== STEP ========");
        // break condition if not computed by episode
        if (reachedGoal()) {
            initForNewEpisode();
            return;
        }

        // TODO add do nothing
        // choose an action a TODO e-greedy-stuff
        final Action chosenAction = policy(currState);
        LOG.debug(String.format("Chosen action for %s: %s", currState, chosenAction));

        nextState = ActionHandler.performAction(currState, chosenAction);
        LOG.debug(String.format("Next State should be %s", nextState));

        double currQ = Q(currState, chosenAction);
        LOG.debug(String.format("Q-value for %s is %f", currState, currQ));

        //If not a valid transition stay in same state and add penalty
        double reward = handleTransition(currState, nextState);

        final double nextQ = minQ(nextState).getValue();
        LOG.debug(String.format("Min q-value for %s is %f", nextState, nextQ));

        LOG.debug(String.format("Reward/Penalty from %s to %s is %f", currState, nextState, reward));
        LOG.debug("currQ += learning * (r + (discounting * nextQ) - currQ)");
        LOG.debug(String.format("%f += %f * (%f + (%f * %f) - %f)", currQ, learning, reward, discounting, nextQ, currQ));

        // Q(s,a) <- Q(s,a) + a [r + y * max_a'(Q(s',a')) - Q(s,a)]
        // currQ += learning * (reward + (discounting * nextQ) - currQ);
        currQ += learning * (reward + discounting * nextQ - currQ); // (+ currQ) - because we calc with penalties and not rewards

        LOG.debug(String.format("Update Q(%s,%s) -> %f", currState, chosenAction, currQ));
        Q(currState, chosenAction, currQ); // update Q(s,a)

        currState = environment.isValidTransition(currState, nextState) ? nextState : currState;

        episodes++;
        infoForGUI.policy[currState.x][currState.y] = policy(currState).getValue();
    }

    /**
     * Reset after goal has reached.
     */
    private void initForNewEpisode() {
        currState = start;
        discounting = Math.pow(discounting, episodes);
        LOG.debug("new discounting is: " + discounting);
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
    private double handleTransition(@NotNull final State currState,
                                    @NotNull final State nextState) {
        if (environment.isValidTransition(currState, nextState)) {
            infoForGUI.receivedPenalty = false;
            return costPerStep; //transition cost = pathCost
        } else {
            infoForGUI.receivedPenalty = true;
            this.nextState = currState;
            return environment.getReward(currState, nextState) + costPerStep; //add reward or penalty
        }
    }

    /**
     * Q(s,a) - Q value for an given state and action.
     *
     * @param currState    Not null.
     * @param chosenAction Not null.
     * @return
     */
    private Double Q(@NotNull final State currState,
                     @NotNull final Action chosenAction) {
        final Double qValue = q_sa.get(currState).get(chosenAction);
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
    private void Q(@NotNull final State currState,
                   @NotNull final Action chosenAction,
                   @NotNull final Double qValue) {
        q_sa.get(currState).replace(chosenAction, qValue);
    }

    /**
     * Returns an chosen action by a policy.
     *
     * @param currState Not null.
     * @return
     */
    private Action policy(@NotNull final State currState) { // TODO test this
        LOG.debug("=> Policy");
        final Action bestAction = minQ(currState).getKey();
        double explor = exploration * (Action.capabilities());
        Action chosenAction = bestAction;

        final double random = Math.random();
        for (int i = 0; i < Action.capabilities(); i++) { // TODO here Don't understand
            if (random < (i + 1) * explor) {
                chosenAction = Action.valueOf(i);
                break;
            }
        }
        LOG.debug("BestAction:" + bestAction);
        LOG.debug("ChosenAction:" + chosenAction);
        infoForGUI.isBestAct = chosenAction == bestAction;

        return chosenAction;
    }

    /**
     * Returns the min q-value for an given state.
     *
     * @param currState Not null.
     * @return
     */
    private Map.Entry<Action, Double> minQ(@NotNull final State currState) { // TODO test this
        final Map<Action, Double> actionValueMap = q_sa.get(currState);
        final Set<Map.Entry<Action, Double>> entrySet = actionValueMap.entrySet();
        return entrySet
                .stream()
                .min(Comparator.comparingDouble(Map.Entry::getValue))
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
        return q_sa;
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
                costPerStep = Integer.parseInt(value);
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
                    stateValue[i][j] = minQ(new State(i, j)).getValue();
            return stateValue;
        }
    }

}
