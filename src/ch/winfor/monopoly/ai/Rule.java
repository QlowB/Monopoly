package ch.winfor.monopoly.ai;

import ch.winfor.monopoly.game.TurnHandler;

/**
 * base class for a rule which is tested by the ai and also executed if needed.
 * 
 * @author Nicolas Winkler
 * 
 */
public interface Rule {
    /**
     * tests if the rule can be executed
     * 
     * @param turnHandler
     *            information about the current turn and game state
     * @see #execute(TurnHandler)
     * @return <code>true</code>, if the rule should be executed,
     *         <code>false</code> otherwise
     */
    abstract boolean test(TurnHandler turnHandler);

    /**
     * executes the rule
     * 
     * This method should only be invoked after {@link #test(TurnHandler)} has
     * been invoked and returned <code>true</code>.
     * 
     * @param turnHandler
     *            information about the current turn and game state
     */
    abstract void execute(TurnHandler turnHandler);

    /**
     * rule that determines weather the ai should buy some houses
     * 
     * @author Nicolas Winkler
     * 
     */
    public static class BuyHousesRule implements Rule {

        @Override
        public boolean test(TurnHandler turnHandler) {
            return false;
        }

        @Override
        public void execute(TurnHandler turnHandler) {
        }

    }
}
