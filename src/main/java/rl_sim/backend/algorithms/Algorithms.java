package rl_sim.backend.algorithms;

public interface Algorithms {
    int ValueIter = 1, PolicyIter = 2;

    void initialize();

    boolean step();

    void setProperty(int name, String value);

    ValueFunction getValueFunction();

    int[][] getPolicy();

    int getNumOfIters();

    long getTime();
}