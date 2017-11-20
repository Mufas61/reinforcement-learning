package rl_sim.backend.algorithms;

import com.google.common.base.Preconditions;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rl_sim.backend.environment.Action;
import rl_sim.backend.environment.ActionHandler;
import rl_sim.backend.environment.Maze;
import rl_sim.backend.environment.State;

import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;

public class MyQLearning {
    private static final Logger LOG = Logger.getLogger(MyQLearning.class);

    private boolean isInitialized = false;
    private int numEpisodes;

    /**
     * Q(s,a) - immediate reward + discounted reward.
     * Q-value for every state-action-combination.
     */
    @NotNull
    private final Map<State, Map<Action, Double>> q_sa;

    private InfoForGUI infoForGUI;

    /**
     * First state in every new round. Bottom left corner.
     */
    private State start;

    /**
     * Penalty per step.
     */
    private int costPerStep = -1;

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
    private State currState;

    public MyQLearning(@NotNull final Maze environment) {
        this.environment = environment;
        this.infoForGUI = new InfoForGUI();
        this.q_sa = new HashMap<>(); // TODO maybe treemap to find keys faster
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
        isInitialized = true;
        converged = false;
        // TODO update properties here
    }

    /**
     * Runs the algorithm.
     */
    public void compute() {
        Preconditions.checkState(isInitialized, "Algorithm has to be initialized!");

        do {
            currState = start;
            do {
                doOneStep();
                numEpisodes++;
            } while (reachedGoal()); //until s ist ein endzustand);
        } while (converged); // Best way has been found
    }

    public State getCurrState() {
        return currState;
    }

    public static class Properties {
        public static int Penalty = 1;
        public static int LearningRate = 2;
        public static int ExplorationRate = 3;
        public static int DiscountingRate = 4;
    }

    public void setProperty(int name, String value) {
        if (name == Properties.Penalty) {
            costPerStep = Integer.parseInt(value);
        } else if (name == Properties.LearningRate) {
            learning = Double.parseDouble(value);
        } else if (name == Properties.ExplorationRate) {
            exploration = Double.parseDouble(value);
        } else if (name == Properties.DiscountingRate) {
            discounting = Double.parseDouble(value);
        }
    }

    /**
     * One step in the algorithm. Updates {@link #currState}
     */
    public void doOneStep() {
        // choose an action a TODO e-greedy-stuff
        final Action chosenAction = policy(currState);

        double currQ = Q(currState, chosenAction);
        State nextState = ActionHandler.performAction(currState, chosenAction);
        final double nextQ = minQ(nextState).getValue();

        //If not a valid transition stay in same state and add penalty;
        double reward = getReward(currState, nextState);

        LOG.debug(String.format("Reward/Penalty from %s to %s is %f", currState, nextState, reward));
        LOG.debug("currQ += learning * (r + (discounting * nextQ) - currQ)");
        LOG.debug(String.format("%f += %f * (%f + (%f * %f) - %f)", currQ, learning, reward, discounting, nextQ, currQ));

        // Q(s,a) <- Q(s,a) + a [r + y * max_a'(Q(s',a')) - Q(s,a)]
        // currQ += learning * (reward + (discounting * nextQ) - currQ);
        currQ += learning * (reward + (discounting * nextQ) + currQ); // + currQ - because we calc with penalties and not rewards

        LOG.debug(String.format("Update Q(%s,%s) -> %f", currState, chosenAction, currQ));
        Q(currState, chosenAction, currQ); // update Q(s,a)

        currState = environment.isValidTransition(currState, nextState) ? nextState : currState;
    }

    /**
     * Returns reward/penalty based on the transition.
     * If {@link Maze#isValidTransition(State, State)}
     * is true only {@link #costPerStep} will be calculated.
     *
     * @param currState Not null.
     * @param nextState Not null.
     * @return amount of reward/penalty for this transition.
     */
    private double getReward(@NotNull final State currState,
                             @NotNull final State nextState) {
        if (environment.isValidTransition(currState, nextState)) {
            infoForGUI.receivedPenalty = false;
            return costPerStep; //transition cost = pathCost
        } else {
            infoForGUI.receivedPenalty = true;
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
        final Action bestAction = minQ(currState).getKey();
        double exp = exploration * (Action.capabilities());
        Action chosenAction = bestAction;

        final double random = Math.random();
        for (int i = 0; i < Action.capabilities(); i++) { // TODO here Don't understand
            if (random < (i + 1) * exp) {
                chosenAction = Action.valueOf(i);
                break;
            }
        }
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
        final Map.Entry<Action, Double> bestAction = entrySet
                .stream()
                .min(Comparator.comparingDouble(Map.Entry::getValue))
                .orElse(null);

        if (bestAction != null) {
            return bestAction;
        } else {
            final Map.Entry<Action, Double> randomAction = entrySet
                    .stream()
                    .skip(new Random().nextInt(entrySet.size() - 1))
                    .findFirst()
                    .orElse(null);

            checkArgument(randomAction != null);

            return randomAction;
        }
    }

    /**
     * @return True, if goal is reached.
     */
    public boolean reachedGoal() {
        return environment.goals.contains(currState);
    }

    public int getNumEpisodes() {
        return numEpisodes;
    }

    public InfoForGUI getInfoForGUI() {
        return infoForGUI;
    }

    /**
     * Holds information for gui.
     */
    public class InfoForGUI {

        private boolean receivedPenalty;

        private int[][] policy;

        private InfoForGUI() {
            //Initialise policy for all states as -1
            for (int i = 0; i < policy.length; i++)
                for (int j = 0; j < policy[i].length; j++)
                    policy[i][j] = -1;
        }

        public int[][] getPolicy() {
            return policy;
        }

        public void setPolicy(int[][] policy) {
            this.policy = policy;
        }

        public boolean getReceivedPenalty() {
            return receivedPenalty;
        }

        public void setReceivedPenalty(@Nullable Boolean receivedPenalty) {
            this.receivedPenalty = receivedPenalty;
        }
    }
}
