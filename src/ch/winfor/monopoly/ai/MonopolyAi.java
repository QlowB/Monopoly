package ch.winfor.monopoly.ai;

import ch.winfor.monopoly.Freeable;
import ch.winfor.monopoly.game.Game;
import ch.winfor.monopoly.game.TurnHandler;

/**
 * basic monopoly playing intelligence
 * 
 * @author Nicolas Winkler
 * 
 */
public class MonopolyAi implements Freeable {
    /**
     * the players turn index
     */
    private int turn;

    /**
     * the game
     */
    private Game game;

    /** thread waiting for the turn */
    private TurnAwaiter turnAwaiter;

    public MonopolyAi(Game game, int turn) {
        this.game = game;
        this.turn = turn;
        turnAwaiter = new TurnAwaiter();
        turnAwaiter.start();
    }

    @SuppressWarnings("unused")
    public void takeTurn() {
        try {
            // make it look like the computer thinks
            // Thread.sleep(500);
            if (false)
                throw new InterruptedException();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        TurnHandler th = game.getTurnHandler();
        switch (th.getNextTask()) {
        case CAST_DICE:
            th.castDice();
            break;
        case MOVE_PLAYING_PIECE:
            th.movePiece();
            break;
        case BUY_PROPERTY:
            th.buyProperty(false);
            break;
        case PAY_RENT:
            th.payRent();
            break;
        case PAY_TAX:
            th.payTax();
            break;
        case END_TURN:
            th.endTurn();
            break;
        case DRAW_CARD:
            th.drawCard();
            break;
        case FOLLOW_CARD:
            th.followCard();
            break;
        default:
            th.endTurn();
            break;
        }
    }

    @Override
    public void free() {
        endGame();
    }

    public void endGame() {
        turnAwaiter.shouldStop();
        turnAwaiter = null;
    }

    /**
     * waits until its his turn
     * 
     * @author Nicolas Winkler
     * 
     */
    private class TurnAwaiter extends Thread {
        private boolean shouldRun;

        public TurnAwaiter() {
            shouldRun = true;
        }

        public void shouldStop() {
            shouldRun = false;
        }

        public void run() {
            while (shouldRun) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (game.getTurn() == turn) {
                    takeTurn();
                }
            }
        }
    }
}
