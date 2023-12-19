package de.fhkiel.eki.boffin.Evaluations;

import de.fhkiel.ki.cathedral.game.Color;

public record AreaEvaluation(int areaBlack, int areaWhite) implements Evaluation {
    public int eval() {
        return areaWhite - areaBlack;
    }

    public int areaForColor(Color color) {
        if (color == Color.Black) {
            return areaBlack;
        } else {
            return areaWhite;
        }
    }

    @Override
    public String toString() {
        return "AreaEvaluation\t{ Black= " + areaBlack +
                ", White= " + areaWhite +
                "; Eval= " + this.eval() + '}';
    }
}
