package ch.winfor.monopoly.game;

import ch.winfor.monopoly.game.Card.KeepableCard;

/**
 * interface for catching events indicating any change to the game
 * 
 * @author Nicolas Winkler
 * 
 */
public interface GameListener extends EventListener {
    /**
     * invoked whenever a playing piece is moved on the board
     * 
     * @param sender
     *            the game that sent the event
     * @param pieceIndex
     *            the player index of the playing piece that was moved
     * @param oldPosition
     *            the position of the piece before it was moved
     */
    void playingPieceMoved(Game sender, int pieceIndex, int oldPosition);

    /**
     * invoked after a player ended his turn and the next player can start his
     * turn
     * 
     * @param sender
     *            the game that sent the event
     * @param playerIndex
     *            the index of the player that ended his turn
     */
    void playerEndedTurn(Game sender, int playerIndex);

    /**
     * invoked when a player's wealth changed
     * 
     * @param sender
     *            the game that sent the event
     * @param player
     *            the player whose wealth changed
     * @param wealthBefore
     *            the wealth of this player before the wealth changed
     */
    void playerWealthChanged(Game sender, Player player, long wealthBefore);

    /**
     * invoked after {@link #playerWealthChanged(Game, Player, long)} if the
     * wealth of the player is now negative
     * 
     * @param sender
     *            the sender game
     * @param player
     *            the player who went bankrupt
     */
    void playerWentBankrupt(Game sender, Player player);

    /**
     * invoked when a player's jail state is altered
     * 
     * @param sender
     *            the sender game
     * @param player
     *            the player whose state altered
     * @param jailStateBefore
     *            the jail state before it was altered
     */
    void playersJailStateChanged(Game sender, Player player, int jailStateBefore);

    /**
     * invoked when a player buys or in another way gets a new property
     * 
     * @param sender
     *            the sender game
     * @param player
     *            the player who has obtained something
     * @param field
     *            the field he has gotten
     */
    void playerObtained(Game sender, Player player, BuyableField field);

    /**
     * invoked when a player ca keep a card (normally these get-out-of-jail
     * cards)
     * 
     * @param sender
     *            the sender game
     * @param player
     *            the player who gets the card
     * @param card
     *            the card
     */
    void playerKeepsCard(Game sender, Player player, KeepableCard card);

    /**
     * invoked when some houses were built or demolished
     * 
     * @param game
     *            the sender game
     * @param position
     *            where the houses were built or demolished
     * @param oldNumber
     *            the number of houses that were there before the event happened
     */
    void houseNumberChanged(Game sender, int position, int oldNumber);

    /**
     * invoked when someone drew a card from a {@link ShuffledCardDeck}
     * 
     * @param sender
     *            the sender game
     * @param deckName
     *            the name of the deck that was drawn from
     * @param c
     *            the {@link Card} that was drawn
     */
    void cardDrawn(Game sender, String deckName, Card c);
}
