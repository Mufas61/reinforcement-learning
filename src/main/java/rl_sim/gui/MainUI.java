package rl_sim.gui;

import rl_sim.backend.algorithms.Algorithms;
import rl_sim.gui.algorithms.AlgoSimulator;
import rl_sim.gui.algorithms.PSSimulator;
import rl_sim.gui.algorithms.QLSimulator;
import rl_sim.gui.maze.MazeEditor;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainUI extends javax.swing.JFrame implements ActionListener {
    private JPanel jPanel;
    private JButton jPolicySimButton;
    private JPanel jInfoPanel;
    private JButton jQuitButton;
    private JButton jValueSimButton;
    private JButton jMazeEditorButton;
    private JEditorPane jInfoPane;
    private JButton jQLSimButton;
    private JButton jPSSimButton;

    static {
        //Set Look & Feel
        try {
            if (System.getProperty("os.name").contains("indows"))
                javax.swing.UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
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
                jPanel = new JPanel();
                this.getContentPane().add(jPanel, BorderLayout.CENTER);
                jPanel.setLayout(null);
                jPanel.setBackground(new java.awt.Color(235, 241, 238));
                jPanel.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED, null, null));
                jPanel.setPreferredSize(new java.awt.Dimension(653, 466));
                {
                    jValueSimButton = new JButton();
                    jPanel.add(jValueSimButton);
                    jValueSimButton.setText("Value Iteration");
                    jValueSimButton.setActionCommand("Value");
                    jValueSimButton.setBounds(180, 80, 160, 30);
                    jValueSimButton.addActionListener(this);
                }
                {
                    jPolicySimButton = new JButton();
                    jPanel.add(jPolicySimButton);
                    jPolicySimButton.setText("Policy Iteration");
                    jPolicySimButton.setBounds(368, 79, 160, 30);
                    jPolicySimButton.setActionCommand("Policy");
                    jPolicySimButton.addActionListener(this);
                }
                {
                    jQLSimButton = new JButton();
                    jPanel.add(jQLSimButton);
                    jQLSimButton.setText("Q Learning");
                    jQLSimButton.setBounds(180, 134, 160, 30);
                    jQLSimButton.setActionCommand("QSim");
                    jQLSimButton.addActionListener(this);
                }
                {
                    jPSSimButton = new JButton();
                    jPanel.add(jPSSimButton);
                    jPSSimButton.setText("P. Sweeping");
                    jPSSimButton.setBounds(368, 134, 160, 30);
                    jPSSimButton.setActionCommand("PSSim");
                    jPSSimButton.addActionListener(this);
                }
                {
                    jMazeEditorButton = new JButton();
                    jPanel.add(jMazeEditorButton);
                    jMazeEditorButton.setText("Create rl_sim.gui.Maze");
                    jMazeEditorButton.setBounds(267, 32, 175, 30);
                    jMazeEditorButton.setActionCommand("Edit rl_sim.gui.Maze");
                    jMazeEditorButton.addActionListener(this);
                }
                {
                    jQuitButton = new JButton();
                    jPanel.add(jQuitButton);
                    jQuitButton.setText("Enough of it !!");
                    jQuitButton.setBounds(267, 182, 175, 30);
                    jQuitButton.setActionCommand("Quit");
                    jQuitButton.addActionListener(this);
                }
                {
                    java.net.URL helpURL = MainUI.class.getResource("resources/info.html");

                    jInfoPanel = new JPanel();
                    BoxLayout jInfoPanelLayout = new BoxLayout(jInfoPanel, javax.swing.BoxLayout.X_AXIS);
                    jInfoPanel.setLayout(jInfoPanelLayout);
                    jPanel.add(jInfoPanel);
                    jInfoPanel.setBounds(80, 250, 540, 150);
                    {
                        jInfoPane = new JEditorPane();
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

    public void actionPerformed(ActionEvent evt) {
        if (evt.getActionCommand().equals("Quit")) {
            System.exit(0);
        } else if (evt.getActionCommand().equals("Edit rl_sim.gui.Maze")) {
            MazeEditor inst = new MazeEditor();
            inst.setVisible(true);
        } else if (evt.getActionCommand().equals("Value")) {
            AlgoSimulator inst = new AlgoSimulator(Algorithms.ValueIter);
            inst.setVisible(true);
        } else if (evt.getActionCommand().equals("Policy")) {
            AlgoSimulator inst = new AlgoSimulator(Algorithms.PolicyIter);
            inst.setVisible(true);
        } else if (evt.getActionCommand().equals("QSim")) {
            QLSimulator inst = new QLSimulator();
            inst.setVisible(true);
        } else if (evt.getActionCommand().equals("PSSim")) {
            PSSimulator inst = new PSSimulator();
            inst.setVisible(true);
        }
    }
}