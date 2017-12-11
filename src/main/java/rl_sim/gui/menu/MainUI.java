package rl_sim.gui.menu;

import rl_sim.gui.algorithms.MyQLSim;
import rl_sim.gui.algorithms.SARSASimulator;
import rl_sim.gui.maze.MazeEditor;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static rl_sim.gui.GraphicsUtil.LOOK_AND_FEEL;

public class MainUI extends javax.swing.JFrame implements ActionListener {

    static {
        //Set Look & Feel
        try {
            javax.swing.UIManager.setLookAndFeel(LOOK_AND_FEEL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public MainUI() {
        super("RL-MDP:Simulation");
        initGUI();
    }

    private void initGUI() {
        try {
            this.setSize(708, 484);
            this.setLocationRelativeTo(null);
            this.setUndecorated(true);
            //this.setExtendedState(MAXIMIZED_BOTH);
            setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            {
                JPanel jPanel = new JPanel();
                this.getContentPane().add(jPanel, BorderLayout.CENTER);
                jPanel.setLayout(null);
                jPanel.setBackground(new java.awt.Color(235, 241, 238));
                jPanel.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED, null, null));
                jPanel.setPreferredSize(new java.awt.Dimension(653, 466));
                {
                    initQLSimButton(jPanel);
                    initMyQLSimButton(jPanel);
                    initMazeEditorButton(jPanel);
                    initQuitButton(jPanel);

                    JPanel jInfoPanel = new JPanel();
                    BoxLayout jInfoPanelLayout = new BoxLayout(jInfoPanel, javax.swing.BoxLayout.X_AXIS);
                    jInfoPanel.setLayout(jInfoPanelLayout);
                    jPanel.add(jInfoPanel);
                    jInfoPanel.setBounds(80, 250, 540, 150);
                    {
                        JEditorPane jInfoPane = new JEditorPane();
                        jInfoPanel.add(jInfoPane);
                        //jInfoPane.setPage(helpURL);
                        jInfoPane.setBorder(new LineBorder(new java.awt.Color(0, 0, 0), 1, false));
                        jInfoPane.setBackground(new java.awt.Color(214, 214, 235));
                        jInfoPane.setAutoscrolls(false);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initQuitButton(JPanel jPanel) {
        JButton jQuitButton = new JButton();
        jPanel.add(jQuitButton);
        jQuitButton.setText("Enough of it !!");
        jQuitButton.setBounds(267, 182, 175, 30);
        jQuitButton.setActionCommand(GUICommand.QUIT.getValue());
        jQuitButton.addActionListener(this);
    }

    private void initMazeEditorButton(JPanel jPanel) {
        JButton jMazeEditorButton = new JButton();
        jPanel.add(jMazeEditorButton);
        jMazeEditorButton.setText("Create Maze");
        jMazeEditorButton.setBounds(267, 32, 175, 30);
        jMazeEditorButton.setActionCommand(GUICommand.EDIT_MAZE.getValue());
        jMazeEditorButton.addActionListener(this);
    }

    private void initQLSimButton(JPanel jPanel) {
        JButton jQLSimButton = new JButton();
        jPanel.add(jQLSimButton);
        jQLSimButton.setText("SARSA");
        jQLSimButton.setBounds(180, 134, 160, 30);
        jQLSimButton.setActionCommand(GUICommand.SARSA.getValue());
        jQLSimButton.addActionListener(this);
    }

    private void initMyQLSimButton(JPanel jPanel) {
        JButton jMyQLSimButton = new JButton();
        jPanel.add(jMyQLSimButton);
        jMyQLSimButton.setText("My Q Learning");
        jMyQLSimButton.setBounds(180, 80, 160, 30);
        jMyQLSimButton.setActionCommand(GUICommand.MY_Q_LEARNING_SIM.getValue());
        jMyQLSimButton.addActionListener(this);
    }

    public void actionPerformed(ActionEvent evt) {
        if (evt.getActionCommand().equals(GUICommand.QUIT.getValue())) {
            System.exit(0);
        } else if (evt.getActionCommand().equals(GUICommand.EDIT_MAZE.getValue())) {
            MazeEditor inst = new MazeEditor();
            inst.setVisible(true);
        } else if (evt.getActionCommand().equals(GUICommand.SARSA.getValue())) {
            SARSASimulator inst = new SARSASimulator();
            inst.setVisible(true);
        } else if (evt.getActionCommand().equals(GUICommand.MY_Q_LEARNING_SIM.getValue())) {
            MyQLSim inst = new MyQLSim();
            inst.setVisible(true);
        }
    }
}