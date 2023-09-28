package de.fhkiel.eki;

import de.fhkiel.eki.agents.BiggestBuilding;
import de.fhkiel.ki.cathedral.gui.CathedralGUI;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        CathedralGUI.start(new BiggestBuilding());
    }
}