package de.fhkiel.eki.boffin.Evaluations;

public record NextTurnEvaluation(int nextTurnBlack, int nextTurnWhite) implements Evaluation {
    public int eval() {
        // as black potential area returns negative number, to keep it negative, we add instead of subtracting
        return nextTurnWhite + nextTurnBlack;
    }
}
