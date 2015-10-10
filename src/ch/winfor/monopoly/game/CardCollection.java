package ch.winfor.monopoly.game;

import java.awt.Color;
import java.io.Serializable;

/**
 * stack of action cards
 * 
 * @author Nicolas Winkler
 * 
 */
public class CardCollection implements Hashable, Serializable {
    /** */
    private static final long serialVersionUID = 5355878966238371657L;

    /** the deck's name */
    private String name;

    /** the actual cards in the deck */
    private Card[] cards;

    /** the color of the cards */
    private Color color;

    public CardCollection() {
        this("", new Card[0], Color.WHITE);
    }

    public CardCollection(String name, Card[] cards, Color color) {
        setName(name);
        setCards(cards);
        setColor(color);
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the cards
     */
    public Card[] getCards() {
        return cards;
    }

    /**
     * gets one {@link Card} at a specific index
     * 
     * @param cardIndex
     *            the index of the desired {@link Card}
     * @return the {@link Card} at the specified index
     */
    public Card getCard(int cardIndex) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * sets the cards for this stack and sets this stack as the parent stack of
     * all the cards
     * 
     * @param cards
     *            the cards to set
     */
    public void setCards(Card[] cards) {
        this.cards = cards;
        for (Card c : cards) {
            c.setParentStack(this);
        }
    }

    /**
     * @return the color
     */
    public Color getColor() {
        return color;
    }

    /**
     * @param color
     *            the color to set
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * adds another stack to this stack
     * 
     * (The color of the other stack is lost, so is its name)
     * 
     * @param other
     *            the other stack
     */
    public void merge(CardCollection other) {
        Card[] arr = other.getCards();
        Card[] temp = new Card[arr.length + cards.length];
        for (int i = 0; i < arr.length; i++) {
            temp[i] = arr[i];
        }
        for (int i = 0; i < cards.length; i++) {
            temp[i + arr.length] = cards[i];
        }
        for (Card c : temp) {
            c.setParentStack(this);
        }
        cards = temp;
    }

    /*
     * (non-Javadoc)
     * 
     * @see ch.winfor.monopoly.game.Hashable#createHash()
     */
    public long createHash() {
        long hash = name.hashCode();
        hash += (long) color.getRGB() << 32;

        for (int i = 0; i < cards.length; i++) {
            Card c = cards[i];
            hash += c.createHash();
            hash *= 3;
        }

        return hash;
    }
}
