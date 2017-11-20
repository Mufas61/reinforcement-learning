package rl_sim.backend.algorithms;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;
import rl_sim.backend.environment.Action;
import rl_sim.backend.environment.Maze;
import rl_sim.backend.environment.State;

import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;

public class MyQLearning {
    private boolean isInitialized = false;
    private static final int PATH_COST = 1;

    /**
     * Q(s,a) - immediate reward + discounted reward
     */
    @NotNull
    private final Map<State, Map<Action, Double>> q_sa;

    /**
     * First state in every new round. Bottom left corner.
     */
    private State start;

    /**
     * Environment.
     */
    @NotNull
    private final Maze environment;

    /**
     * Algorithm has finished -> best way has been found.
     */
    private boolean converged;

    public MyQLearning(@NotNull final Maze environment) {
        this.environment = environment;
        this.q_sa = new HashMap<>(); // TODO maybe treemap tp find keys faster
    }


    public void initialize() {
        /*
         * For all s elem S, a elem A
         * Q(s,a) = 0
         */
        for (int width = 0; width < environment.width * environment.height; width++) {
            for (int height = 0; height < environment.height; height++) {
                for (Action action : Action.valuesWithoutNone()) {
                    final State state = new State(width, height);

                    final HashMap<Action, Double> actionValueMap = new HashMap<>();
                    actionValueMap.put(action, Double.NaN);
                    q_sa.put(state, actionValueMap); // init all states
                }
            }
        }
        start = new State(0, 0); // bottom left corner.
        isInitialized = true;
        converged = false;
    }

    public void compute() {
        Preconditions.checkState(isInitialized, "Algorithm has to be initialized!");
        Preconditions.checkState(!converged, "Algorithm has to be initialized!");

        do {
            final State currState;
            currState.copy(start);
            do {
                // TODO wähle eine aktion a und führe sie aus
                final Map.Entry<Action, Double> currAction = chooseAction(currState);
                // TODO erhalte belohnung r und neuen Zustand s*
                //
            } while (false); //TODO s ist ein endzustand);
        } while (converged);

    }

    private Map.Entry<Action, Double> chooseAction(final State currState) {
        final Map<Action, Double> actionValueMap = q_sa.get(currState);
        final Set<Map.Entry<Action, Double>> entrySet = actionValueMap.entrySet();
        final Map.Entry<Action, Double> bestAction = entrySet
                .stream()
                .max(Comparator.comparingDouble(Map.Entry::getValue))
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

}
