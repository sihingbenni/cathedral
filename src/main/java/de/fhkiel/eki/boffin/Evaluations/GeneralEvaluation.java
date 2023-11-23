package de.fhkiel.eki.boffin.Evaluations;

public record GeneralEvaluation(ScoreEvaluation scoreEval, AreaEvaluation areaEval,
                                NextTurnEvaluation nextTurnEval) implements Evaluation {
    public int eval() {
        if (nextTurnEval != null) {
            return scoreEval.eval() + areaEval.eval() + nextTurnEval.eval();
        } else {
            return scoreEval.eval() + areaEval.eval();
        }
    }

    @Override
    public String toString() {
        // print the evaluation if desired

        String string = "==================\n";
        string += "----- State Eval -----\n";
        string += scoreEval + "\n";
        string += areaEval + "\n";
        string += nextTurnEval + "\n";
        string += "------------------------------\n";
        string += "Sum:\t" + this.eval() + "\n";
        string += "==================\n";

        return string;
    }
}
