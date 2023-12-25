package de.fhkiel.eki.boffin.gamestate;

import de.fhkiel.eki.helper.ContinuousLogger;
import de.fhkiel.ki.cathedral.game.Game;
import de.fhkiel.ki.cathedral.game.Placement;

import java.io.PrintStream;
import java.util.Set;

public class GameStateManager {

    private static final int MAX_TURN_TIME = 9_000;
    private static final int MAX_TURN_TIME_BONUS = 120_000;
    private static GameStateManager instance;
    private Game game;
    private final PrintStream console;

    private GameState gameState;

    ContinuousLogger continuousLogger;


    /**
     * The remaining time in milliseconds at maximum 2 minutes.
     */
    private long remainingTime;

    /**
     * The time in milliseconds when the current turn started.
     */
    private long turnStartTime;


    private long calculatingStartTime;

    Set<Placement> possiblePlacementsInTurn;


    public GameStateManager(Game game, PrintStream console) {
        this.console = console;
        this.game = game;
        this.continuousLogger = new ContinuousLogger(console);
    }

    public static GameStateManager getInstance() {
        return instance;
    }


    /**
     * Initializes the game with the default values.
     */
    private void initGameState() {
        this.gameState = GameState.EarlyGame;
        this.remainingTime = MAX_TURN_TIME_BONUS;
        instance = this;
    }

    /**
     * Starts the timer and updates the Game object.
     *
     * @param game {@link Game} the updated Game object.
     */
    public void startTurn(Game game) {
        this.game = game;
        turnStartTime = System.currentTimeMillis();

        int turnNumber = game.lastTurn().getTurnNumber();

        // if a turn is started after gameOver (e.g. undo) reset to mid-game.
        if (gameState == GameState.GameOver) {
            gameState = GameState.MidGame;
        }

        // check if the game has been reset, if so, reinitialize the game state
        if (turnNumber <= 1) initGameState();
    }

    public void nrOfPossiblePlacementsCalculated(Set<Placement> possiblePlacementsInTurn) {
        this.possiblePlacementsInTurn = possiblePlacementsInTurn;
        int size = possiblePlacementsInTurn.size();
        if (size == 0) {
            console.println("I have no moves left.");
            console.println("It's your turn!");
        } else if (size == 1) {
            console.println("There is only one move to play...");
        } else {
            console.println("I have " + size + " possible moves.");
            console.println("I'm thinking...");
        }
    }

    public void finishGame() {
        gameState = GameState.GameOver;

        console.println("Game finished!\n");
        console.print("gg!\n");
    }

    public void startEvaluatingPlacements() {
        console.println("Evaluating placements...");
        calculatingStartTime = System.currentTimeMillis();

        Thread thread = new Thread(continuousLogger);
        thread.start();
    }

    public void endEvaluatingPlacements() {
        continuousLogger.doStop();
        console.println("Calculating placements took: " + (System.currentTimeMillis() - calculatingStartTime) + "ms");
    }

    public void switchGameState(GameState newState) {
        System.out.println("Switching to " + newState.name() + ".");
        gameState = newState;
    }

    public Set<Placement> switchToEndgameCalculations() {
        switchGameState(GameState.EndGame);
        // gameState is now EndGame, return the placements from that calculation
        return gameState.calculateTurn(game, possiblePlacementsInTurn);
    }

    public long getRemainingTurnTime() {
        return MAX_TURN_TIME + remainingTime;
    }

    public void startCalculatingFinalMoves() {
        console.println("Trying to find an optimal solution.");
        console.println("At maximum this turn is going to take: " + ((10000 + remainingTime) / 1000) + "s");
    }

    public void endCalculatingFinalMoves() {
        // check
        console.println("I found the best solution");
    }

    public Set<Placement> calculateTurn() {
        return gameState.calculateTurn(game, possiblePlacementsInTurn);
    }

    public void finishTurn() {
        long tookTime = System.currentTimeMillis() - turnStartTime;
        console.println("My turn took: " + tookTime + "ms");

        if (tookTime > 10_000) {
            long usedTimeBonus = tookTime - 10_000;
            remainingTime -= usedTimeBonus;
            console.println("I used " + tookTime / 1_000 + "s and " + tookTime % 1_000 + "ms.");
            console.println("I have: " + remainingTime / 1_000 + "s and " + (remainingTime % 1_000) + "ms buffer left.");
        }
        console.println("Its your turn!\n");
    }
}
