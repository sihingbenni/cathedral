package de.fhkiel.eki;

import de.fhkiel.eki.agents.FirstReactAgent;
import de.fhkiel.eki.boffin.Baggins;
import de.fhkiel.eki.boffin.Boffin;
import de.fhkiel.ki.cathedral.gui.CathedralGUI;

public class Main {
    public static void main(String[] args) {
        CathedralGUI.start(new FirstReactAgent(), new Boffin(), new Baggins());
    }
}