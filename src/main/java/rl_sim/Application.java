package rl_sim;

import rl_sim.gui.menu.MainUI;

public class Application {

    private Application() {
    }

    public static void main(String[] args) {
        MainUI inst = new MainUI();
        inst.setVisible(true);
    }

}
