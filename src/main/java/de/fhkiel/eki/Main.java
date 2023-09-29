package de.fhkiel.eki;

import de.fhkiel.eki.agents.Labor1Agent;
import de.fhkiel.eki.agents.Labor2Agent;
import de.fhkiel.ki.cathedral.gui.CathedralGUI;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        CathedralGUI.start(new Labor1Agent(), new Labor2Agent());
    }
}