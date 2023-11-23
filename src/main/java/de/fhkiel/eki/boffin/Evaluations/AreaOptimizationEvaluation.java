package de.fhkiel.eki.boffin.Evaluations;

public record AreaOptimizationEvaluation(ScoreEvaluation scoreEvaluation) implements Evaluation {

    @Override
    public int eval() {
        return scoreEvaluation.eval();
    }
}
