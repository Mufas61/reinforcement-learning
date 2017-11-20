package rl_sim.gui.maze;

import org.jetbrains.annotations.NotNull;
import rl_sim.backend.environment.Maze;
import rl_sim.backend.environment.State;
import rl_sim.backend.environment.Wall;
import rl_sim.gui.GraphicsUtil;
import rl_sim.gui.Utility;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Vector;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static rl_sim.gui.GraphicsUtil.LOOK_AND_FEEL;

public class MazeEditor extends javax.swing.JFrame implements ActionListener {

    /**
     * Gets incremented for every new element.
     */
    private int yPosition = 0;

    /**
     * Path to the maze files.
     */
    private static final String MAZE_DIRECTORY_PATH = "./mazes/";

    private static final int BUTTON_HEIGHT = 25;
    private static final int MARGIN = 5;
    private static final int INPUT_BUTTON_POSITION = 65;
    private static final int TEXT_FIELD_WIDTH = 65;
    private static final int INPUT_BUTTON_WIDTH = 5 + 85;
    private static final int BUTTON_WIDTH = 5 + 70 + 80;

    /*all the components*/
    private JPanel jPanel;
    private JButton jHeight = new JButton();
    private JButton jWidth = new JButton();
    private JButton jWalls = new JButton();
    private JButton jPenalty = new JButton();
    private JButton jBoundaryPenalty = new JButton();
    private JButton jAddGoals = new JButton();
    private JButton jSaveMaze = new JButton();
    private JButton jLoadMaze = new JButton();
    private JButton jBoxSize = new JButton();
    private JButton jResetMaze = new JButton();
    private JTextField jHeightTextField = new JTextField();
    private JTextField jWidthTextField = new JTextField();
    private JTextField jPenaltyTextField = new JTextField();
    private JTextField jBoundaryPenaltyTextField = new JTextField();

    private JTextField jBoxSizeTextField = new JTextField();
    /*other objects and variables*/
    private Maze myMaze;
    private int penalty;    //the penalty to be set for the walls
    private int boundaryPenalty; //the penalty to be set for the boundary walls
    private int nodeLength = 40;    //indicates the size of the square box to be displayed in the GUI
    //by default it is 40 pixels. increase to increase magnification factor
    private boolean boundariesAdded = false;
    private int edit_state;
//    public static int EDIT_WALLS = 1, ADD_GOALS = 2, ADD_START = 3, ASSIGN_REWARD = 4;

    static {
        //Set Look & Feel
        try {
            javax.swing.UIManager.setLookAndFeel(LOOK_AND_FEEL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Main method.
     *
     * @param args arguments.
     */
    public static void main(String[] args) {
        MazeEditor inst = new MazeEditor();
        inst.setVisible(true);
    }

    /**
     * Constructor.
     */
    public MazeEditor() {
        super("RL-MDP:Maze Editor");
        initGUI();
    }

    /**
     * Initialize all gui elements.
     */
    private void initGUI() {
        try {
            myMaze = new Maze(2, 2);
            setSize(600, 600);
            this.setExtendedState(MAXIMIZED_BOTH);
            setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            final SymMouse aSymMouse = new SymMouse();

            jPanel = new JPanel();
            this.getContentPane().add(jPanel, BorderLayout.CENTER);
            jPanel.setBackground(new java.awt.Color(235, 241, 238));
            jPanel.setLayout(null);

            initInputButtonCombination(jHeightTextField, jHeight, "4", "Height");
            initInputButtonCombination(jWidthTextField, jWidth, "4", "Width");
            initInputButtonCombination(jPenaltyTextField, jPenalty, "50", "Set Penalty");
            initInputButtonCombination(jBoundaryPenaltyTextField, jBoundaryPenalty, "50", "Set Boundary Penalty");
            initButton(jWalls, "Add Walls");
            initButton(jAddGoals, "Add Goals");
            initButton(jSaveMaze, "Save Maze");
            initInputButtonCombination(jBoxSizeTextField, jBoxSize, "30", "Box Size");
            initButton(jResetMaze, "Reset Maze");
            initButton(jLoadMaze, "Load Maze");
            initSeparator();

            final JPanel jGridPanel = new GridPanel();
            jPanel.add(jGridPanel);
            jGridPanel.addMouseListener(aSymMouse);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initInputButtonCombination(@NotNull final JTextField textField,
                                            @NotNull final JButton button,
                                            @NotNull final String defaultValue,
                                            @NotNull final String name) {
        jPanel.add(textField);
        jPanel.add(button);

        textField.setText(defaultValue);
        textField.setBounds(MARGIN, yPosInPx(this.yPosition) + MARGIN, TEXT_FIELD_WIDTH, BUTTON_HEIGHT);

        button.setText(name);
        button.setBounds(MARGIN + INPUT_BUTTON_POSITION, yPosInPx(this.yPosition) + MARGIN, INPUT_BUTTON_WIDTH, BUTTON_HEIGHT);
        button.addActionListener(this);
        this.yPosition++;
    }

    private void initButton(@NotNull final JButton button,
                            @NotNull final String name) {
        jPanel.add(button);
        button.setText(name);
        button.setBounds(MARGIN, yPosInPx(this.yPosition) + MARGIN, BUTTON_WIDTH, BUTTON_HEIGHT);
        button.addActionListener(this);
        this.yPosition++;
    }

    /**
     * Initialize separator.
     */
    private void initSeparator() {
        final JSeparator jSeparator1 = new JSeparator();
        jPanel.add(jSeparator1);
        jSeparator1.setBounds(188, 2, 2, 363);
        jSeparator1.setBorder(BorderFactory.createTitledBorder(
                null,
                "",
                TitledBorder.LEADING,
                TitledBorder.TOP,
                new Font("MS Sans Serif", Font.PLAIN, 11),
                Color.BLACK));
    }

    /**
     * Updates the editor.
     *
     * @param graphics Not null.
     */
    private void updateGUI(@NotNull Graphics graphics) {
        Graphics2D g2 = (Graphics2D) graphics;
        setSize(myMaze.width * nodeLength + 4, myMaze.height * nodeLength + 4);
        drawGoals(graphics);
        plotMaze(g2);
        drawWalls(g2);
    }


    private class GridPanel extends JPanel {

        GridPanel() {
            GridLayout jGridPanelLayout = new GridLayout(15, 15);
            jGridPanelLayout.setRows(10);
            jGridPanelLayout.setColumns(10);
            setLayout(jGridPanelLayout);
            setLocation(230, 50);
            setSize(10, 10);
        }

        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            setSize(myMaze.width * nodeLength + 4, myMaze.height * nodeLength + 4);
            drawGoals(g);
            plotMaze(g2);
            drawWalls(g2);
        }
    }

    class SymMouse extends java.awt.event.MouseAdapter {
        public void mouseClicked(java.awt.event.MouseEvent event) {
            int clickX = event.getX();
            int clickY = (nodeLength * myMaze.height) - event.getY();
            int nodeX = (clickX / nodeLength);
            int nodeY = (clickY / nodeLength);

            switch (edit_state) {
                case 1:
                    int lowerLimX = (nodeX * nodeLength) + (int) (0.1 * nodeLength);
                    int upperLimX = ((nodeX + 1) * nodeLength) - (int) (0.1 * nodeLength);
                    if (clickX < lowerLimX) {
                        myMaze.toggleWall(new Wall(nodeX, nodeY, Wall.LEFT, penalty));
                        myMaze.toggleWall(new Wall(nodeX - 1, nodeY, Wall.RIGHT, penalty));
                    } else if (clickX > upperLimX) {
                        myMaze.toggleWall(new Wall(nodeX, nodeY, Wall.RIGHT, penalty));
                        myMaze.toggleWall(new Wall(nodeX + 1, nodeY, Wall.LEFT, penalty));
                    }

                    int lowerLimY = (nodeY * nodeLength) + (int) (0.1 * nodeLength);
                    int upperLimY = ((nodeY + 1) * nodeLength) - (int) (0.1 * nodeLength);
                    if (clickY < lowerLimY) {
                        myMaze.toggleWall(new Wall(nodeX, nodeY, Wall.DOWN, penalty));
                        myMaze.toggleWall(new Wall(nodeX, nodeY - 1, Wall.UP, penalty));
                    } else if (clickY > upperLimY) {
                        myMaze.toggleWall(new Wall(nodeX, nodeY, Wall.UP, penalty));
                        myMaze.toggleWall(new Wall(nodeX, nodeY + 1, Wall.DOWN, penalty));
                    }

                    repaint();
                    break;
                case 2:
                    System.out.print("trying to add goals at ");
                    System.out.println("nodeX,nodeY= " + nodeX + "," + nodeY);
                    myMaze.addGoal(new State(nodeX, nodeY));
                    myMaze.printGoals();
                    repaint();
                    break;
                case 3:
                    System.out.println("trying to add starts");
                    break;
                case 4:
                    System.out.println("trying to assign rewards");
                    break;
            }
        }
    }

    public void actionPerformed(ActionEvent evt) {
        System.out.println("event=" + evt);
        if (evt.getSource() == jLoadMaze) {
            loadFile();
            repaint();
        } else if (evt.getSource() == jHeight) {
            myMaze.height = Integer.parseInt(jHeightTextField.getText());
            repaint();
        } else if (evt.getSource() == jWidth) {
            myMaze.width = Integer.parseInt(jWidthTextField.getText());
            repaint();
        } else if (evt.getSource() == jWalls) {
            edit_state = 1;
            if (!boundariesAdded) {
                addBoundaries();
            }
            repaint();
        } else if (evt.getSource() == jPenalty) {
            penalty = Integer.parseInt(jPenaltyTextField.getText());
        } else if (evt.getSource() == jBoundaryPenalty) {
            boundaryPenalty = Integer.parseInt(jBoundaryPenaltyTextField.getText());
        } else if (evt.getSource() == jAddGoals) {
            edit_state = 2;
        } else if (evt.getSource() == jSaveMaze) {
            saveFile();
        } else if (evt.getSource() == jBoxSize) {
            nodeLength = Integer.parseInt(jBoxSizeTextField.getText());
            repaint();
        } else if (evt.getSource() == jResetMaze) {
            myMaze.height = 2;
            myMaze.width = 2;
            myMaze.walls.clear();
            myMaze.goals.clear();
            //	myMaze.starts.clear();
            boundariesAdded = false;
            repaint();
        }


    }

    private void plotMaze(Graphics2D g) {
        final int startX = 0;
        final int startY = 0;

        for (int i = 0; i <= nodeLength * myMaze.width; i = i + nodeLength)
            g.drawLine(i + startX, startY, i + startX, startY + (nodeLength * myMaze.height));

        for (int i = 0; i <= nodeLength * myMaze.height; i = i + nodeLength)
            g.drawLine(startX, i + startY, startX + (nodeLength * myMaze.width), i + startY);
    }

    private void drawWalls(Graphics g) {
        int aX, aY, bX, bY;    //start and end points of the wall
        Vector<Wall> wall = new Vector<>(myMaze.walls);
        Wall w;
        myMaze.printWalls();
        for (int i = 0; i < myMaze.walls.size(); i++) {
            w = wall.get(i);
            int nodeX = w.x;
            int nodeY = w.y;
            switch (w.dir) {
                case Wall.UP:
                    aX = nodeX * nodeLength;
                    bX = (nodeX + 1) * nodeLength;
                    aY = (myMaze.height - nodeY - 1) * nodeLength;
                    bY = (myMaze.height - nodeY - 1) * nodeLength;
                    // System.out.println("up wall (-" + w.penalty + ") ax,ay,bx,by= " + aX + "," + aY + "," + bX + "," + bY);
                    GraphicsUtil.drawLine(g, aX, aY, bX, bY, 5);
                    break;
                case Wall.DOWN:
                    aX = nodeX * nodeLength;
                    bX = (nodeX + 1) * nodeLength;
                    aY = (myMaze.height - nodeY) * nodeLength;
                    bY = (myMaze.height - nodeY) * nodeLength;
                    // System.out.println("down wall ax,ay,bx,by= " + aX + "," + aY + "," + bX + "," + bY);
                    GraphicsUtil.drawLine(g, aX, aY, bX, bY, 5);
                    break;
                case Wall.RIGHT:
                    aX = (nodeX + 1) * nodeLength;
                    bX = (nodeX + 1) * nodeLength;
                    aY = (myMaze.height - nodeY - 1) * nodeLength;
                    bY = (myMaze.height - nodeY) * nodeLength;
                    //System.out.println("right wall ax,ay,bx,by= " + aX + "," + aY + "," + bX + "," + bY);
                    GraphicsUtil.drawLine(g, aX, aY, bX, bY, 5);
                    break;
                case Wall.LEFT:
                    aX = (nodeX) * nodeLength;
                    bX = (nodeX) * nodeLength;
                    aY = (myMaze.height - nodeY) * nodeLength;
                    bY = (myMaze.height - nodeY - 1) * nodeLength;
                    //System.out.println("left wall ax,ay,bx,by= " + aX + "," + aY + "," + bX + "," + bY);
                    GraphicsUtil.drawLine(g, aX, aY, bX, bY, 5);
                    break;
            }
        }
    }

    private void drawGoals(Graphics g) {
        int left, top, height, width;
        State curr;
        for (int i = 0; i < myMaze.goals.size(); i++) {
            curr = (myMaze.goals.get(i));
            left = curr.x * nodeLength;
            top = (myMaze.height - curr.y - 1) * nodeLength;
            width = nodeLength;
            height = nodeLength;
            System.out.println("drawing at " + left + "," + top + "," + width + "," + height);
            GraphicsUtil.fillRect(g, left, top, width, height, Color.ORANGE);
        }
    }

    /**
     * Loads maze from file.
     */
    private void loadFile() {
        JFileChooser fc = new JFileChooser(MAZE_DIRECTORY_PATH);
        int returnVal = fc.showOpenDialog(MazeEditor.this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                File file = fc.getSelectedFile();
                FileInputStream fis = new FileInputStream(file);
                GZIPInputStream gzip = new GZIPInputStream(fis);
                ObjectInputStream in = new ObjectInputStream(gzip);
                myMaze = (Maze) in.readObject();
                in.close();
            } catch (Exception e) {
                Utility.show(e.getMessage());
            }
        }
    }

    /**
     * Save maze in file.
     */
    private void saveFile() {
        JFileChooser fc = new JFileChooser(MAZE_DIRECTORY_PATH);
        int returnVal = fc.showOpenDialog(MazeEditor.this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                File file = fc.getSelectedFile();
                FileOutputStream fos = new FileOutputStream(file);
                GZIPOutputStream gzip = new GZIPOutputStream(fos);
                ObjectOutputStream out = new ObjectOutputStream(gzip);
                out.writeObject(myMaze);
                out.flush();
                out.close();
            } catch (Exception e) {
                Utility.show(e.getMessage());
            }
        }
    }

    /**
     * Adds boundaries to the maze.
     */
    private void addBoundaries() {
        boundariesAdded = true;
        int j = 0;
        for (int i = 0; i < myMaze.width; i++) {
            System.out.println("adding up walls");
            myMaze.toggleWall(new Wall(i, j, Wall.DOWN, boundaryPenalty));
        }
        j = myMaze.height - 1;
        for (int i = 0; i < myMaze.width; i++) {
            System.out.println("adding down walls");
            myMaze.toggleWall(new Wall(i, j, Wall.UP, boundaryPenalty));
        }
        int i = 0;
        for (j = 0; j < myMaze.width; j++) {
            System.out.println("adding left walls");
            myMaze.toggleWall(new Wall(i, j, Wall.LEFT, boundaryPenalty));
        }
        i = myMaze.width - 1;
        for (j = 0; j < myMaze.width; j++) {
            System.out.println("adding right walls");
            myMaze.toggleWall(new Wall(i, j, Wall.RIGHT, boundaryPenalty));
        }
    }

    private int yPosInPx(int value) {
        return value * 30;
    }
}
