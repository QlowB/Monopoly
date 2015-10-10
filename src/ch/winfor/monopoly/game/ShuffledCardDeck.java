package ch.winfor.monopoly.game;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.Random;

/**
 * card stack where cards can be drawn from
 * 
 * @author Nicolas Winkler
 * 
 */
public class ShuffledCardDeck implements Serializable, Hashable {
    /** */
    private static final long serialVersionUID = 1703907219267597194L;

    /** the cards (in a fixed order) */
    private CardCollection cards;

    /** lifo structure containing the same cards as {@link #cards} */
    private LinkedList<Card> cardQueue;

    public ShuffledCardDeck(CardCollection cards) {
        this.cards = cards;
        cardQueue = new LinkedList<Card>();
        shuffle();
    }

    /**
     * shuffles the deck (puts it in random order)
     */
    public void shuffle() {
        cardQueue.clear();
        Card[] cardArr = cards.getCards();

        LinkedList<Card> list = new LinkedList<Card>();
        for (int i = 0; i < cardArr.length; i++) {
            list.add(cardArr[i]);
        }

        Random rand = new Random();

        for (int i = 0; i < cardArr.length; i++) {
            int index = rand.nextInt(list.size());
            cardQueue.add(list.get(index));
            list.remove(index);
        }
    }

    /**
     * draws the card on top of the deck
     * 
     * @return the card on top of the deck
     */
    public Card draw() {
        Card top = cardQueue.poll();
        putUnder(top);
        return top;
    }

    /**
     * puts a card under the stack
     * 
     * @param c
     *            the card to put
     */
    public void putUnder(Card c) {
        cardQueue.add(c);
    }

    @Override
    public long createHash() {
        final long prime = 472949829875983283L;
        long hash = 0;
        for (int i = 0; i < cardQueue.size(); i++) {
            hash += cardQueue.get(i).createHash();
            hash *= prime;
        }
        return hash;
    }
}
