package rl_sim.backend.algorithms;

import com.google.common.base.Preconditions;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import rl_sim.backend.environment.Action;
import rl_sim.backend.environment.ActionHandler;
import rl_sim.backend.environment.Maze;

public class SARSA extends MyQLearning {
    private static final Logger LOG = Logger.getLogger(SARSA.class);

    private Action nextAction = null;

    /**
     * Constructor.
     *
     * @param environment Not null.
     */
    public SARSA(@NotNull Maze environment) {
        super(environment);
    }

    @Override
    public void computeStep() {
        Preconditions.checkState(isInitialized, "Algorithm has to be initialized!");

        LOG.debug("======== STEP ========");
        // break condition if not computed by episode
        if (reachedGoal()) {
            prepareNewEpisode();
            return;
        }

        // choose an action
        final Action currAction = (nextAction == null ? chosePolicy(currState) : nextAction);

        nextState = ActionHandler.performAction(currState, currAction);

        double currQ = getActionValue(currState, currAction);
        LOG.debug(String.format("Current Q(%s,%s) => %f", currState, currAction, currQ));

        //If not a valid transition stay in same state and add penalty
        double reward = handleTransition(currState, nextState);
        LOG.debug(String.format("Reward/Penalty for %s->%s => %f", currState, nextState, reward));

        //final double nextQ = bestQ(nextState).getValue();
        //LOG.debug(String.format("min_a(Q(%s,a)) => %f", nextState, nextQ));
        nextAction = chosePolicy(nextState);
        final double nextQ = getActionValue(nextState, nextAction);


        LOG.debug("currQ += learning * (r + (discounting * nextQ) - currQ)");
        // Q(s,a) <- Q(s,a) + a [r + y * max_a'(Q(s',a')) - Q(s,a)]
//        currQ = (1 - learning) * currQ + learning * (reward + discounting * nextQ); // TODO test this
        double newQ = currQ + learning * (reward + discounting * nextQ - currQ);
        LOG.debug(String.format("%f = %f + %f * (%f + (%f * %f) - %f)", newQ, currQ, learning, reward, discounting, nextQ, currQ));
        currQ = newQ;
        updateActionValue(currState, currAction, currQ); // update Q(s,a)

        infoForGUI.policy[currState.x][currState.y] = bestQ(currState).getKey().getValue(); // update for gui

        // s <- s'; a <- a'
        currState = environment.isValidTransition(currState, nextState) ? nextState : currState;
    }

}
