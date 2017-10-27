package rl_sim.testing;

import rl_sim.backend.algorithms.QLearning;
import rl_sim.backend.algorithms.ValueFunction;
import rl_sim.backend.algorithms.ValueIteration;
import rl_sim.backend.algorithms.prioritized_sweeping.PrioritizedSweeping;
import rl_sim.backend.maze.Maze;
import rl_sim.gui.Utility;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.Date;
import java.util.Random;
import java.util.zip.GZIPInputStream;

public class DataLogger {
    private Maze myMaze;
    private double precision = 0.001;
    private double pjog = 0.3;

    private double learningRate = 0.7;
    private double epsilon = 0.1;
    private boolean decayingLR = true;

    private int maxBackups = 10;

    private double tinyThreshold = 0.01;

    private DataLogger() {
        JFileChooser fc = new JFileChooser("./mazes/");
        int returnVal = fc.showOpenDialog(new JFrame());
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                File file = fc.getSelectedFile();
                FileInputStream fis = new FileInputStream(file);
                GZIPInputStream gzis = new GZIPInputStream(fis);
                ObjectInputStream in = new ObjectInputStream(gzis);
                myMaze = (Maze) in.readObject();
                in.close();
            } catch (Exception e) {
                Utility.show(e.getMessage());
            }
        }
    }

    public void logValueIteration() {
        ValueIteration valItr = new ValueIteration(myMaze, pjog, precision);
        while (!valItr.step())
            ;
        ValueFunction valuefunc = valItr.getValueFunction();
        System.out.print(0 + "\t");
        valuefunc.displayValues();
    }

    public void logQLearning(int series, int cycles) {

        QLearning ql = new QLearning(myMaze, pjog, learningRate, epsilon, decayingLR);
        long startTime = new Date().getTime();
        for (int i = 0; i < series; i++) {
            for (int j = 0; j < cycles; j++) {
                while (!ql.step())
                    ;
                long endTime = new Date().getTime();
                System.out.print((endTime - startTime) + "\t");
                System.out.print(ql.evalPolicy() + "\n");
            }
        }
    }

    private PrioritizedSweeping ps;

    private void logPSweeping(int cycles) {
        long startTime = new Date().getTime();
        long endTime = new Date().getTime();
        System.out.print(0 + "\t");
        System.out.print((endTime - startTime) + "\t");
        System.out.print(ps.evalPolicy() + "\t");
        //System.out.print(ps.maxCounterLast+"\n");
        System.out.print(ps.averageBackups + "\n");
        for (int j = 0; j < cycles; j++) {
            while (!ps.step())
                ;
            endTime = new Date().getTime();
            System.out.print(j + 1 + "\t");
            System.out.print((endTime - startTime) + "\t");
            System.out.print(ps.evalPolicy() + "\t");
            //System.out.print(ps.maxCounterLast+"\n");
            System.out.print(ps.averageBackups + "\n");
            //if(value<=0.0)
            //    break;
        }
    }

    private void logPSweeping() {
        int i;
        maxBackups = 0;
        Random rand = new Random();
        int seed;
        ps = new PrioritizedSweeping(myMaze, pjog, epsilon, maxBackups, tinyThreshold);
        for (int trials = 0; trials < 10; trials++) {
            System.err.println("trial:" + trials);
            seed = rand.nextInt();
            for (i = 0; i < 6; i = i + 1) {
                //for(i=5;i<30;i=i+5) {
                //maxBackups = 50+i*150;
                maxBackups = (int) Math.pow(3, i) - 1;
                //if(maxBackups>myMaze.height*myMaze.width)
                //    maxBackups = myMaze.height*myMaze.width;
                System.err.println("i==>" + i + "|maxBackups=>" + maxBackups);
                ps.setProperty(PrioritizedSweeping.Properties.MaxBackups, "" + maxBackups);
                ps.initialize();
                ps.setSeed(seed);//Setting some seed based on trial
                logPSweeping(100);
            }
        }
    }

    public static void main(String[] args) {
        DataLogger dl = new DataLogger();

        //dl.logQLearning(1,100);

        dl.logPSweeping();
        System.exit(0);
    }
}
