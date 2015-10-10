package ch.winfor.monopoly.game;

import java.io.Serializable;

import ch.winfor.monopoly.game.Field.GoToJailField;
import ch.winfor.monopoly.game.Field.JailField;
import ch.winfor.monopoly.game.Field.StartField;

/**
 * handler for a turn
 * 
 * @author Nicolas Winkler
 * 
 */
public class TurnHandler implements Serializable, Hashable {
    /** */
    private static final long serialVersionUID = -5094959225562941291L;

    /** reference to the game */
    private transient Game game;

    /** the index of the player whose turn it is */
    private int turn;

    /** the result of the throw (of the dices) */
    private int[] lastCast;

    /** the card that was drawn last */
    private Card drawnCard;

    /** the next task the player has to do */
    private TurnTask nextTask;

    /**
     * @param game
     *            the parent game
     */
    TurnHandler(Game game, int turn) {
        this.game = game;
        this.turn = turn;
        drawnCard = null;
        startTurn();
    }

    /**
     * initializes the turn handler at the start of the turn
     */
    private void startTurn() {
        Player player = getPlayer();
        if (player.isInJail()) {
            int rounds = player.getInJailRounds();
            rounds--;
            player.setInJailRounds(rounds);
        }
        nextTask = TurnTask.CAST_DICE;
    }

    /**
     * generates two random values from 1 to 6
     * 
     * @return an array containing two (always two) integers with random values
     *         from 1 to 6 or <code>null</code> if a player who shouldn't do
     *         anything throws the dice
     */
    public int[] castDice() {
        if (getNextTask() == TurnTask.CAST_DICE) {
            lastCast = new int[] { game.getRandom().nextInt(6) + 1,
                    game.getRandom().nextInt(6) + 1 };

            Player player = getPlayer();
            if (player.isInJail()) {
                if (doublesCast())
                    nextTask = TurnTask.MOVE_PLAYING_PIECE;
                else
                    nextTask = TurnTask.END_TURN;
            } else {
                nextTask = TurnTask.MOVE_PLAYING_PIECE;
            }
        }
        return lastCast;
    }

    /**
     * moves the piece of the current player
     */
    public void movePiece() {
        if (nextTask == TurnTask.MOVE_PLAYING_PIECE) {
            Player player = game.getPlayer(turn);
            PlayingPiece piece = player.getPiece();
            int positionBefore = piece.getPosition();
            game.movePiece(turn, getLastCastValue());
            int positionAfter = piece.getPosition();

            payStartMoney(positionBefore, positionAfter);

            int position = piece.getPosition();
            Field landed = game.getBoard().getField(position);
            landedOnField(landed);
        }
    }

    /**
     * checks if there was a start field in the interval and pays money to the
     * player if there was
     * 
     * @param positionBefore
     *            begin of the interval
     * @param positionAfter
     *            end of the interval
     */
    public void payStartMoney(int positionBefore, int positionAfter) {
        Board board = game.getBoard();
        Player player = game.getPlayer(turn);
        for (int i = positionBefore + 1; (i % board.getAbsoluteLength()) != positionAfter; i++) {
            int pos = i % board.getAbsoluteLength();
            Field field = board.getField(pos);
            if (field instanceof StartField) {
                StartField startField = (StartField) field;
                player.pay(startField.getPassMoney());
            }
        }
    }

    public void landedOnField(Field landed) {
        if (landed instanceof BuyableField) {
            BuyableField buyableLanded = (BuyableField) landed;
            if (game.freeToBuy(buyableLanded)) {
                nextTask = TurnTask.BUY_PROPERTY;
            } else {
                if (game.getOwner(buyableLanded) == getPlayer())
                    nextTask = TurnTask.END_TURN;
                else
                    nextTask = TurnTask.PAY_RENT;
            }
        } else if (landed instanceof DrawCardField) {
            nextTask = TurnTask.DRAW_CARD;
        } else if (landed instanceof TaxField) {
            nextTask = TurnTask.PAY_TAX;
        } else if (landed instanceof StartField) {
            Player player = game.getPlayer(turn);
            player.pay(((StartField) landed).getVisitMoney());
            nextTask = TurnTask.END_TURN;
        } else if (landed instanceof GoToJailField) {
            Board board = game.getBoard();
            JailField jail = board.getJailField();
            int jailIndex = board.getFieldIndex(jail);
            Player player = game.getPlayer(turn);
            player.setInJailRounds(JailField.STANDARD_STAY);
            PlayingPiece piece = game.getPiece(turn);
            piece.setPosition(jailIndex);
            nextTask = TurnTask.END_TURN;
        } else {
            nextTask = TurnTask.END_TURN;
        }
    }

    /**
     * If the player, whose turn it is currently, has landed on a property field
     * which does'nt belong to anybody, he has to either buy it or put it up to
     * auction.
     * 
     * @param buy
     *            <code>true</code> if the player wants to buy the property,
     *            <code>false</code> if he wants to auction it off
     * @return <code>true</code> if the property has been bought,
     *         <code>false</code> otherwise
     */
    public boolean buyProperty(boolean buy) {
        boolean bought = false;
        Player player = game.getPlayer(turn);
        PlayingPiece piece = player.getPiece();
        int position = piece.getPosition();
        Field f = game.getBoard().getField(position);
        if (f instanceof BuyableField && buy) {
            BuyableField buyableField = (BuyableField) f;
            if (player.getWealth() >= buyableField.getPrice()) {
                player.charge(buyableField.getPrice());
                player.addPossession(buyableField);
                bought = true;
            }
        }

        if (buy == bought) {
            nextTask = TurnTask.END_TURN;
        }

        return bought;
    }

    /**
     * @return the property the player can buy
     */
    public BuyableField getPropertyToBuy() {
        Player player = game.getPlayer(turn);
        PlayingPiece piece = player.getPiece();
        int position = piece.getPosition();
        Field field = game.getBoard().getField(position);
        if (field instanceof BuyableField) {
            BuyableField buyableField = (BuyableField) field;
            return buyableField;
        }
        return null;
    }

    /**
     * @return the price of the property the player could buy
     */
    public long propertyPrice() {
        BuyableField buyableField = getPropertyToBuy();
        if (buyableField != null)
            return buyableField.getPrice();
        else
            return 0L;
    }

    /**
     * the player can buy a house on a specific property field
     * 
     * @param propertyField
     *            the field on which the house should be built
     * @return <code>true</code> if the house was sucessfully built,
     *         <code>false</code> if the house was not built (this can have
     *         various reasons, either the player does not own this property,
     *         doesn't have enough money, can't build any more houses on this
     *         property, it's not even his turn...)
     */
    public boolean buyHouse(PropertyField propertyField) {
        Player player = game.getPlayer(turn);
        long housePrice = propertyField.getHousePrice();

        boolean bought = false;

        if (player.getWealth() >= housePrice) {
            Board board = getGame().getBoard();
            int fieldIndex = board.getFieldIndex(propertyField);
            if (fieldIndex != -1) {
                if (getGame().addHouse(fieldIndex)) {
                    player.charge(housePrice);
                    bought = true;
                }
            }
        }

        return bought;
    }

    /**
     * when a player needs to draw a card from a stack
     * 
     * @return a card drawn from the stack
     */
    public Card drawCard() {
        if (getNextTask() == TurnTask.DRAW_CARD) {
            Player player = game.getPlayer(turn);
            Field field = game.getFieldOfPlayer(player);

            nextTask = TurnTask.FOLLOW_CARD;
            if (field instanceof DrawCardField) {
                String deckName = field.getName();
                drawnCard = game.drawCard(deckName);
                return drawnCard;
            }
        }
        return null;
    }

    /**
     * @return the card that was drawn this turn, or <code>null</code> if no
     *         card was drawn
     */
    public Card getDrawnCard() {
        return drawnCard;
    }

    /**
     * follows the instructions on the drawn card
     */
    public void followCard() {
        if (drawnCard != null)
            drawnCard.execute(this);
        if (nextTask == TurnTask.FOLLOW_CARD) {
            nextTask = TurnTask.END_TURN;
        }
    }

    /**
     * pays a tax if the player is on a tax field
     * 
     * @return <code>true</code> if the player actually stands on a field where
     *         he has to pay a tax, <code>false</code> otherwise
     */
    public boolean payTax() {
        Player player = getPlayer();
        Field field = game.getFieldOfPlayer(player);
        if (field instanceof TaxField) {
            TaxField taxField = (TaxField) field;
            player.charge(taxField.getTaxAmount());
            nextTask = TurnTask.END_TURN;
            return true;
        }
        return false;
    }

    /**
     * pays the rent to a player if the current player landed on a field owned
     * by this player
     * 
     * @return <code>true</code>, if the rent was payed
     */
    public boolean payRent() {
        if (nextTask == TurnTask.PAY_RENT) {
            Player player = getPlayer();
            Field field = game.getFieldOfPlayer(player);
            if (field instanceof BuyableField) {
                Player owner = game.getOwner((BuyableField) field);
                if (owner != null) {
                    long rent = calculateRent();
                    player.charge(rent);
                    owner.charge(-rent);
                }
                nextTask = TurnTask.END_TURN;
                return true;
            }
        }
        return false;
    }

    /**
     * @return the rent the player has to pay
     */
    public long calculateRent() {
        long rent = 0;
        Player player = getPlayer();
        Field field = game.getFieldOfPlayer(player);
        if (field instanceof BuyableField) {
            Player owner = game.getOwner((BuyableField) field);
            if (field instanceof PropertyField && owner != null) {
                PropertyField propertyField = (PropertyField) field;
                int houses = game.getHousesOn(field);
                rent = propertyField.getRent(houses);
            } else if (field instanceof RailroadField) {
                RailroadField railroadField = (RailroadField) field;
                int railroadsOwned = game.getNRailroadsOwned(owner);
                if (railroadsOwned > 0
                        && railroadsOwned <= railroadField.getMaxRailroads()) {
                    rent = railroadField.getRent(railroadsOwned);
                }
            } else if (field instanceof CompanyField) {
                CompanyField companyField = (CompanyField) field;
                int companiesOwned = game.getNCompaniesOwned(owner);
                if (companiesOwned > 0
                        && companiesOwned <= companyField.getMaxCompanies()) {
                    rent = companyField.getRentMultiplicator(companiesOwned)
                            * getLastCastValue();
                }
            }
        }
        return rent;
    }

    /**
     * ends the current turn and prepares for the next player to play
     * 
     * @return <code>true</code> if the turn can be ended now,
     *         <code>false</code> otherwise
     */
    public boolean endTurn() {
        if (nextTask == TurnTask.END_TURN) {
            nextTask = TurnTask.TURN_FINISHED;
            game.nextTurn();
            return true;
        } else {
            return false;
        }
    }

    /**
     * @return the value of the two dice values combined
     */
    private int getLastCastValue() {
        if (lastCast != null)
            return lastCast[0] + lastCast[1];
        else
            return 0;
    }

    public boolean doublesCast() {
        return lastCast[0] == lastCast[1];
    }

    /**
     * @return the next thing to do
     */
    public TurnTask getNextTask() {
        return nextTask;
    }

    /**
     * @return the player, whose turn it is currently
     */
    public Player getPlayer() {
        return game.getPlayer(turn);
    }

    /**
     * @return the game
     */
    public Game getGame() {
        return game;
    }

    /**
     * @param game
     *            the game to set
     */
    public void setGame(Game game) {
        this.game = game;
    }

    /*
     * (non-Javadoc)
     * 
     * @see ch.winfor.monopoly.game.Hashable#createHash()
     */
    @Override
    public long createHash() {
        final long prime = 8298389382948293873L;
        long hash = turn * prime;
        if (lastCast != null) {
            for (int i = 0; i < lastCast.length; i++) {
                hash += lastCast[i];
                hash *= prime;
            }
        }
        if (drawnCard != null)
            hash += drawnCard.createHash() * prime;
        hash *= prime;
        if (nextTask != null)
            hash += nextTask.ordinal();
        hash *= prime;
        return hash;
    }

    /**
     * represents several 'tasks' a player has to do if it's his turn
     * 
     * @author Nicolas Winkler
     * 
     */
    public enum TurnTask {
        /** the player's next action has to be a cast of a dice */
        CAST_DICE,

        /** the player must now move his playing piece */
        MOVE_PLAYING_PIECE,

        /** the player has to determine if he will buy a property or not */
        BUY_PROPERTY,

        /** the player has to pay rent for landing on someones property */
        PAY_RENT,

        /** the player has to pay a tax */
        PAY_TAX,

        /** the player has to draw a card from a stack */
        DRAW_CARD,

        /** follow the instructions on a card */
        FOLLOW_CARD,

        /** the player can now either buy houses or end his turn */
        BUY_HOUSES,

        /** the player has now done everything necessary, he can end his turn */
        END_TURN,

        /** the player can't do anything anymore; he has to end his turn */
        TURN_FINISHED
    }
}
