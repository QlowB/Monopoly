package ch.winfor.monopoly.game;

import java.io.Serializable;

import ch.winfor.monopoly.game.Field.JailField;

/**
 * a class representing an action card
 * 
 * @author Nicolas Winkler
 * 
 */
public class Card implements Hashable, Serializable {
    /** */
    private static final long serialVersionUID = -189625610959882402L;

    /** text on this card */
    private String text;

    /** card belongs to this stack */
    private CardCollection belongsTo;

    /**
     * initializes an empty card
     */
    public Card() {
        text = "";
    }

    /**
     * @return the text on this card
     */
    public String getText() {
        return text;
    }

    /**
     * @param text
     *            the new text to set
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * sets the stack this card belongs to
     * 
     * @param parentStack
     *            the stack
     */
    public void setParentStack(CardCollection parentStack) {
        this.belongsTo = parentStack;
    }

    /**
     * @return the stack this card belongs to
     */
    public CardCollection getParentStack() {
        return belongsTo;
    }

    /**
     * follows the instructions on the card
     * 
     * This method has to be overridden by every inheriting type of card.
     * 
     * @param turnHandler
     *            the handler for the current turn
     */
    public void execute(TurnHandler turnHandler) {
    }

    /**
     * card that tells the player to move forward to a field
     * 
     * @author Nicolas Winkler
     * 
     */
    public static class AdvanceToCard extends Card {
        /** */
        private static final long serialVersionUID = 5438704525726756593L;

        /** the position of the field to move forward to */
        private int position;

        /**
         * determines if the player should move forward i.e. if he gets start
         * money
         */
        private boolean moveForward;

        /**
         * initializes the card to point to a field
         * 
         * @param position
         *            the index of the field
         */
        public AdvanceToCard(int position) {
            this.position = position;
        }

        public AdvanceToCard(int position2, boolean moveForward) {
            this.moveForward = moveForward;
        }

        /**
         * @return if the player should move forward i.e. if he gets start money
         */
        public boolean isMoveForward() {
            return moveForward;
        }

        /**
         * @return the position of the field to move forward to
         */
        public int getPosition() {
            return position;
        }

        /**
         * @param position
         *            the new position to set
         */
        public void setPosition(int position) {
            this.position = position;
        }

        /*
         * (non-Javadoc)
         * 
         * @see monopoly.game.Card#execute(monopoly.game.TurnHandler)
         */
        public void execute(TurnHandler turnHandler) {
            Player player = turnHandler.getPlayer();
            PlayingPiece piece = player.getPiece();
            int oldPosition = piece.getPosition();
            piece.setPosition(this.position);

            if (isMoveForward())
                turnHandler.payStartMoney(oldPosition, piece.getPosition());

            Board board = turnHandler.getGame().getBoard();
            turnHandler.landedOnField(board.getField(piece.getPosition()));
        }
    }

    /**
     * advance to the nearest utility
     * 
     * This class is used for the following type of card: "Advance token to
     * nearest Utility. If unowned, you may buy it from the Bank. If owned,
     * throw dice and pay owner a total ten [not actually ten, but
     * {@link AdvanceToUtilityCard#multiplier}] times the amount thrown."
     * 
     * @author Nicolas Winkler
     * 
     */
    public static class AdvanceToUtilityCard extends Card {
        /** */
        private static final long serialVersionUID = 4501902001578856669L;
        /** multiplies the value of the cast dice */
        private long multiplier;

        /**
         * initializes the card
         * 
         * @param multiplier
         *            multiplies the value of the cast dice
         */
        public AdvanceToUtilityCard(long multiplier) {
            this.multiplier = multiplier;
        }

        /**
         * @return the multiplier
         */
        public long getMultiplier() {
            return multiplier;
        }

        /**
         * @param multiplier
         *            the multiplier to set
         */
        public void setMultiplier(long multiplier) {
            this.multiplier = multiplier;
        }

        /*
         * (non-Javadoc)
         * 
         * @see monopoly.game.Card#execute(monopoly.game.TurnHandler)
         */
        public void execute(TurnHandler turnHandler) {
            Player player = turnHandler.getPlayer();
            PlayingPiece piece = player.getPiece();
            Board board = turnHandler.getGame().getBoard();
            int position = piece.getPosition();
            int nextUtility = board.getNextUtilityIndex(position);
            int oldPosition = piece.getPosition();
            piece.setPosition(nextUtility);
            turnHandler.payStartMoney(oldPosition, piece.getPosition());

            turnHandler.landedOnField(board.getField(piece.getPosition()));
        }
    }

    /**
     * card that lets the player forward to the next railroad field
     * 
     * This class is used for the following type of card: "Advance token to the
     * nearest Railroad and pay owner twice the rental to which he/she is
     * otherwise entitled. If Railroad is unowned, you may buy it from the
     * Bank."
     * 
     * @author Nicolas Winkler
     * 
     */
    public static class AdvanceToRailroadCard extends Card {
        /** */
        private static final long serialVersionUID = 3463259447911610497L;
        /** the multiplier for the money to pay */
        private long multiplier;

        /**
         * initializes the card
         * 
         * @param multiplier
         *            the multiplier for the rent to pay on the field
         */
        public AdvanceToRailroadCard(long multiplier) {
            this.multiplier = multiplier;
        }

        /**
         * @return the multiplier
         */
        public long getMultiplier() {
            return multiplier;
        }

        /**
         * @param multiplier
         *            the multiplier to set
         */
        public void setMultiplier(long multiplier) {
            this.multiplier = multiplier;
        }

        /*
         * (non-Javadoc)
         * 
         * @see monopoly.game.Card#execute(monopoly.game.TurnHandler)
         */
        public void execute(TurnHandler turnHandler) {
            Player player = turnHandler.getPlayer();
            PlayingPiece piece = player.getPiece();
            Board board = turnHandler.getGame().getBoard();
            int position = piece.getPosition();
            int nextRailroad = board.getNextRailroadIndex(position);
            int oldPosition = piece.getPosition();
            piece.setPosition(nextRailroad);
            turnHandler.payStartMoney(oldPosition, piece.getPosition());
            turnHandler.landedOnField(board.getField(piece.getPosition()));
        }
    }

    /**
     * a card which sends the player to a field relative to his current position
     * 
     * @author Nicolas Winkler
     * 
     */
    public static class GoRelativeCard extends Card {
        /** */
        private static final long serialVersionUID = 3425723667884559471L;
        /** the position relative to the player's current position */
        private int relativePosition;

        public GoRelativeCard(int relativePosition) {
            this.relativePosition = relativePosition;
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * ch.winfor.monopoly.game.Card#execute(ch.winfor.monopoly.game.TurnHandler
         * )
         */
        public void execute(TurnHandler turnHandler) {
            Board board = turnHandler.getGame().getBoard();
            Player player = turnHandler.getPlayer();
            PlayingPiece piece = player.getPiece();
            int newPos = (piece.getPosition() + relativePosition)
                    % board.getAbsoluteLength();

            int oldPosition = piece.getPosition();
            piece.setPosition(newPos);
            if (relativePosition > 0)
                turnHandler.payStartMoney(oldPosition, piece.getPosition());
            turnHandler.landedOnField(board.getField(piece.getPosition()));
        }
    }

    /**
     * a card that simply makes the player get money or pay money
     * 
     * @author Nicolas Winkler
     * 
     */
    public static class GetMoneyCard extends Card {
        /** */
        private static final long serialVersionUID = -6975821401493822940L;
        /**
         * the money the player gets from the bank; if this is a negative value,
         * he has to pay the absolute value of this
         */
        private long money;

        public GetMoneyCard(long money) {
            this.money = money;
        }

        /**
         * @return the money
         */
        public long getMoney() {
            return money;
        }

        /**
         * @param money
         *            the money to set
         */
        public void setMoney(long money) {
            this.money = money;
        }

        /*
         * (non-Javadoc)
         * 
         * @see monopoly.game.Card#execute(monopoly.game.TurnHandler)
         */
        public void execute(TurnHandler turnHandler) {
            Player player = turnHandler.getPlayer();
            player.setWealth(player.getWealth() + money);
        }
    }

    /**
     * the player gets/pays money from/to every other player
     * 
     * @author Nicolas Winkler
     * 
     */
    public static class GetMoneyPerPlayerCard extends Card {
        /** */
        private static final long serialVersionUID = -1569944410208318879L;
        /**
         * the money the player gets from each other player; if a negative
         * value, he has to pay his fellow players the absolute value of it
         */
        private long money;

        /**
         * initializes the card
         * 
         * @param money
         *            the money the player gets from each other player; if a
         *            negative value, he has to pay his fellow players the
         *            absolute value of it
         */
        public GetMoneyPerPlayerCard(long money) {
            this.money = money;
        }

        /**
         * @return the money
         */
        public long getMoney() {
            return money;
        }

        /**
         * @param money
         *            the money to set
         */
        public void setMoney(long money) {
            this.money = money;
        }

        /*
         * (non-Javadoc)
         * 
         * @see monopoly.game.Card#execute(monopoly.game.TurnHandler)
         */
        public void execute(TurnHandler turnHandler) {
            Player player = turnHandler.getPlayer();
            Game game = turnHandler.getGame();
            for (int i = 0; i < game.getNPlayers(); i++) {
                Player fellowPlayer = game.getPlayer(i);
                if (player != fellowPlayer) {
                    fellowPlayer.setWealth(fellowPlayer.getWealth() - money);
                    player.setWealth(player.getWealth() + money);
                }
            }
        }
    }

    /**
     * a card that can be kept or/and traded after drawing it
     * 
     * @author Nicolas Winkler
     * 
     */
    public static class KeepableCard extends Card {

        /** */
        private static final long serialVersionUID = 8035127090016081973L;
    }

    /**
     * card that allows the user to get out of jail
     * 
     * @author Nicolas Winkler
     * 
     */
    public static class GetOutOfJailCard extends KeepableCard {
        /** */
        private static final long serialVersionUID = 1161790805550780277L;

        /*
         * (non-Javadoc)
         * 
         * @see monopoly.game.Card#execute(monopoly.game.TurnHandler)
         */
        public void execute(TurnHandler turnHandler) {
            Player player = turnHandler.getPlayer();
            player.setInJailRounds(0);
        }
    }

    /**
     * card that sends the player directly to jail
     * 
     * @author Nicolas Winkler
     * 
     */
    public static class GoToJailCard extends Card {
        /** */
        private static final long serialVersionUID = 2565869710513752069L;

        /*
         * (non-Javadoc)
         * 
         * @see monopoly.game.Card#execute(monopoly.game.TurnHandler)
         */
        public void execute(TurnHandler turnHandler) {
            Player player = turnHandler.getPlayer();
            Board board = turnHandler.getGame().getBoard();
            JailField jail = board.getJailField();
            int jailIndex = board.getFieldIndex(jail);
            player.setInJailRounds(JailField.STANDARD_STAY);
            player.getPiece().setPosition(jailIndex);
        }
    }

    /**
     * card that makes the player pay repair money for each house or hotel he
     * owns
     * 
     * @author Nicolas Winkler
     * 
     */
    public static class PayPerHouseCard extends Card {
        /** */
        private static final long serialVersionUID = 4780402567795443842L;

        /** the money the player has to pay per house he owns */
        private long perHouse;

        /** the money the player has to pay per hotel he owns */
        private long perHotel;

        /**
         * initializes the card
         * 
         * @param perHouse
         *            the repair costs per house
         * @param perHotel
         *            the repair costs per hotel
         * 
         */
        public PayPerHouseCard(long perHouse, long perHotel) {
            this.perHouse = perHouse;
            this.perHotel = perHotel;
        }

        /**
         * @return the perHouse
         */
        public long getPerHouse() {
            return perHouse;
        }

        /**
         * @param perHouse
         *            the perHouse to set
         */
        public void setPerHouse(long perHouse) {
            this.perHouse = perHouse;
        }

        /**
         * @return the perHotel
         */
        public long getPerHotel() {
            return perHotel;
        }

        /**
         * @param perHotel
         *            the perHotel to set
         */
        public void setPerHotel(long perHotel) {
            this.perHotel = perHotel;
        }

        /*
         * (non-Javadoc)
         * 
         * @see monopoly.game.Card#execute(monopoly.game.TurnHandler)
         */
        public void execute(TurnHandler turnHandler) {
            Game game = turnHandler.getGame();
            Player player = turnHandler.getPlayer();
            int nHouses = game.getTotalHouses(player);
            int nHotels = game.getTotalHotels(player);

            long houseCosts = perHouse * nHouses;
            long hotelCosts = perHotel * nHotels;

            player.charge(houseCosts + hotelCosts);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see ch.winfor.monopoly.game.Hashable#createHash()
     */
    public long createHash() {
        return text.hashCode();
    }
}
