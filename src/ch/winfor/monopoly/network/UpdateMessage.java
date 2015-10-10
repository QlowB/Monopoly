package ch.winfor.monopoly.network;

import ch.winfor.monopoly.game.Board;
import ch.winfor.monopoly.game.BuyableField;
import ch.winfor.monopoly.game.Card;
import ch.winfor.monopoly.game.Card.KeepableCard;
import ch.winfor.monopoly.game.CardCollection;
import ch.winfor.monopoly.game.Field;
import ch.winfor.monopoly.game.Game;
import ch.winfor.monopoly.game.Player;
import ch.winfor.monopoly.game.PlayingPiece;
import ch.winfor.monopoly.game.ShuffledCardDeck;

/**
 * network message sent when a part of the game is updated
 * 
 * @author Nicolas Winkler
 * 
 */
public abstract class UpdateMessage extends NetworkMessage {
    /** */
    private static final long serialVersionUID = -7758394851502953824L;

    /**
     * the game's hash on the other computer which should be the same on this
     * machine after applying the update's changes to the game
     */
    protected long hashAfter;

    /**
     * applies the changes to a game
     * 
     * @param game
     *            the game to apply changes to
     */
    public abstract void updateGame(Game game) throws InvalidUpdateException;

    /**
     * sets the hash which is compared to the game on the end computer
     * 
     * @param hash
     *            the new hash
     */
    public void setHash(long hash) {
        this.hashAfter = hash;
    }

    /**
     * tests if the sent hash is equal to the hash of the game
     * 
     * @param game
     *            the game to check the hash
     * @return {@code true} if the two hashes are equal, {@code false} otherwise
     */
    public boolean checkHash(Game game) {
        long gameHash = game.createHash();
        return gameHash == hashAfter;
    }

    /**
     * update messace concerning one player
     * 
     * @author Nicolas Winkler
     * 
     */
    public static abstract class PlayerUpdate extends UpdateMessage {
        /** */
        private static final long serialVersionUID = 3005459774796395556L;

        /** the index of the player who changed something */
        protected int playerIndex;
    }

    /**
     * sends the whole game and requests that this new game is treated as the
     * valid game
     * 
     * @author Nicolas Winkler
     * 
     */
    public static class FullGameUpdate extends UpdateMessage {
        /** */
        private static final long serialVersionUID = 5665432193162705538L;

        /** the actual game */
        private Game game;

        /**
         * inits the response
         * 
         * @param game
         *            the game to respond
         */
        public FullGameUpdate(Game game) {
            this.game = game;
        }

        @Override
        public void updateGame(Game game) throws InvalidUpdateException {
            game.assignGame(this.game);
        }

        /**
         * @return the game
         */
        public Game getGame() {
            return game;
        }
    }

    /**
     * an update message sent when a player moved its piece
     * 
     * @author Nicolas Winkler
     * 
     */
    public static class PlayerMovedUpdate extends PlayerUpdate {
        /**  */
        private static final long serialVersionUID = 1537183381588099268L;

        /** the new position of the playing piece */
        protected int piecePosition;

        /**
         * initializes the update message with player index and piece position
         * 
         * @param playerIndex
         *            index of the player that moved his piece
         * @param piecePosition
         *            position of the piece on the board now
         */
        public PlayerMovedUpdate(int playerIndex, int piecePosition) {
            this.playerIndex = playerIndex;
            this.piecePosition = piecePosition;
        }

        @Override
        public void updateGame(Game game) {
            PlayingPiece piece = game.getPiece(playerIndex);
            piece.setPosition(piecePosition);
        }
    }

    /**
     * message indicating that a player has paid or earned something
     * 
     * @author Nicolas Winkler
     * 
     */
    public static class PlayerWealthChangedUpdate extends PlayerUpdate {
        /** */
        private static final long serialVersionUID = -4884097641654771714L;

        /** the new wealth of the player */
        protected long newWealth;

        /**
         * initialize the update
         * 
         * @param newWealth
         *            the new wealth
         */
        public PlayerWealthChangedUpdate(long newWealth) {
            this.newWealth = newWealth;
        }

        @Override
        public void updateGame(Game game) {
            Player p = game.getPlayer(playerIndex);
            p.setWealth(newWealth);
        }
    }

    /**
     * message indicating that a player has finished his turn
     * 
     * @author Nicolas Winkler
     * 
     */
    public static class PlayerEndedTurn extends PlayerUpdate {
        /** */
        private static final long serialVersionUID = -6695417237909969555L;

        @Override
        public void updateGame(Game game) {
            game.getTurnHandler().endTurn();
        }
    }

    /**
     * message indicating that a player's jail state has changed
     * 
     * @author Nicolas Winkler
     * 
     */
    public static class PlayersJailStateChangedUpdate extends PlayerUpdate {
        /** */
        private static final long serialVersionUID = -1314417033202996862L;

        /** rounds that the player has to spend in jail left */
        protected int newJailState;

        /**
         * @param newJailState
         *            the new jail state
         */
        public PlayersJailStateChangedUpdate(int newJailState) {
            this.newJailState = newJailState;
        }

        @Override
        public void updateGame(Game game) {
            Player p = game.getPlayer(playerIndex);
            p.setInJailRounds(newJailState);
        }
    }

    /**
     * message indicating that a player bought something
     * 
     * @author Nicolas Winkler
     * 
     */
    public static class PlayerObtainedUpdate extends PlayerUpdate {
        /** */
        private static final long serialVersionUID = 6826071142446173483L;

        /** index of the field that the player bought */
        protected int fieldIndex;

        @Override
        public void updateGame(Game game) throws InvalidUpdateException {
            Player p = game.getPlayer(playerIndex);
            Field field = game.getBoard().getField(fieldIndex);
            if (field instanceof BuyableField)
                p.addPossession((BuyableField) field);
            else
                throw new InvalidUpdateException(
                        "player can only buy buyable fields.");
        }
    }

    /**
     * message indicating that a player can keep a card
     * 
     * @author Nicolas Winkler
     * 
     */
    public static class PlayerKeepsCardUpdate extends PlayerUpdate {
        /** */
        private static final long serialVersionUID = 7924617203181744983L;

        /** the name of the {@link CardCollection} the card belongs to */
        protected String cardCollectionName;

        /** index of the card in the {@link CardCollection} */
        protected int cardIndex;

        @Override
        public void updateGame(Game game) throws InvalidUpdateException {
            Player player = game.getPlayer(playerIndex);
            Board board = game.getBoard();
            CardCollection cc = board.getCardStack(cardCollectionName);
            Card card = cc.getCard(cardIndex);
            if (card instanceof KeepableCard)
                player.addPossession((KeepableCard) card);
            else
                throw new InvalidUpdateException(
                        "cannot keep un-keepable card.");
        }
    }

    /**
     * message indicating that a player can keep a card
     * 
     * @author Nicolas Winkler
     * 
     */
    public static class HousesNumberChangedUpdate extends UpdateMessage {
        /** */
        private static final long serialVersionUID = -7849121268205158225L;

        /** index of the field where the houses changed */
        protected int fieldIndex;

        /** number of houses on the field now */
        protected int newHouseNumbers;

        @Override
        public void updateGame(Game game) {
            game.setHousesOn(fieldIndex, newHouseNumbers);
        }
    }

    /**
     * message indicating that houses were built or demolished
     * 
     * @author Nicolas Winkler
     * 
     */
    public static class CardDrawnUpdate extends UpdateMessage {
        /** */
        private static final long serialVersionUID = -6300031422142747251L;

        /** the name of the {@link ShuffledCardDeck} the card belongs to */
        protected String deckName;

        @Override
        public void updateGame(Game game) {
            game.drawCard(deckName);
        }
    }

    /**
     * exception class describing an error caused by an non-valid
     * {@link UpdateMessage}.
     * 
     * @author Nicolas Winkler
     * 
     */
    public static class InvalidUpdateException extends Exception {
        /**  */
        private static final long serialVersionUID = -9107559441080633154L;

        /**
         * initializes the exception
         * 
         * @param message
         *            a short description of what went wrong
         */
        public InvalidUpdateException(String message) {
            super(message);
        }
    }
}
