package de.fhkiel.eki.boffin.Evaluations;

public record ScoreEvaluation(int scoreBlack, int scoreWhite) implements Evaluation {
    public int eval() {
        return scoreBlack - scoreWhite;
    }

    @Override
    public String toString() {
        return "ScoreEvaluation\t{ Black= " + scoreBlack +
                ", White= " + scoreWhite +
                "; Eval= " + this.eval() + '}';
    }
}
