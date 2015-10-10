package ch.winfor.monopoly.game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * provides functions to keep a record of all the possessions of the players
 * 
 * This class stores the amount of houses per field.
 * 
 * @author Nicolas Winkler
 * 
 */
public class HouseRegister implements Serializable, Hashable {
    /** */
    private static final long serialVersionUID = 2515315092721793060L;

    /** a link to the board */
    private transient Board board;

    /** stores the amount of houses per field */
    private int[] houseCount;

    /** list of subscribers */
    private transient List<HouseRegisterListener> houseRegisterListeners;

    public HouseRegister(Board board) {
        this.board = board;
        houseCount = new int[this.board.getAbsoluteLength()];
        for (int i = 0; i < houseCount.length; i++)
            houseCount[i] = 0;
        houseRegisterListeners = new ArrayList<HouseRegisterListener>();
    }

    /**
     * gets the amount of houses on a specific property
     * 
     * A hotel on the field is represented as the maximum number of houses
     * possible plus one.
     * 
     * @param fieldIndex
     *            the index of the property
     * @return the amount of houses on the specified property
     */
    public int getHouseCount(int fieldIndex) {
        assert fieldIndex >= 0 && fieldIndex < houseCount.length;
        return houseCount[fieldIndex];
    }

    /**
     * sets the amount of houses on a specific property
     * 
     * @param fieldIndex
     *            the index of the property
     * @param houseCount
     *            the amount of houses to set on the specified property
     */
    public void setHouseCount(int fieldIndex, int houseCount) {
        assert fieldIndex >= 0 && fieldIndex < this.houseCount.length;

        int oldNumber = this.houseCount[fieldIndex];
        this.houseCount[fieldIndex] = houseCount;
        fireHouseNumberChanged(fieldIndex, oldNumber);
    }

    /**
     * adds a house to a field
     * 
     * @param fieldIndex
     *            the index of the field to add the house
     */
    public void addHouse(int fieldIndex) {
        assert fieldIndex >= 0 && fieldIndex < houseCount.length;

        int oldNumber = houseCount[fieldIndex];
        houseCount[fieldIndex]++;
        fireHouseNumberChanged(fieldIndex, oldNumber);
    }

    @Override
    public long createHash() {
        final long prime = 3483890484920095279L;
        long hash = prime;

        for (int i = 0; i < houseCount.length; i++) {
            hash += houseCount[i];
            hash *= prime;
        }
        return hash;
    }

    /**
     * sends an event that an entry has changed
     * 
     * @param position
     *            the position of the change
     * @param oldNumber
     *            the old value
     */
    protected void fireHouseNumberChanged(int position, int oldNumber) {
        for (HouseRegisterListener hrl : houseRegisterListeners) {
            hrl.houseNumberChanged(this, position, oldNumber);
        }
    }

    /**
     * adds a subscriber to the list
     * 
     * @param hrl
     *            the new subscriber
     */
    public void addHouseRegisterListener(HouseRegisterListener hrl) {
        if (houseRegisterListeners == null)
            houseRegisterListeners = new ArrayList<HouseRegisterListener>();
        houseRegisterListeners.add(hrl);
    }

    /**
     * removes a subscriber from the list
     * 
     * @param hrl
     *            the subscriber to remove
     */
    public void removeHouseRegisterListener(HouseRegisterListener hrl) {
        if (houseRegisterListeners == null)
            houseRegisterListeners = new ArrayList<HouseRegisterListener>();
        houseRegisterListeners.remove(hrl);
    }

    /**
     * sets the board
     * 
     * @param board
     *            the board
     */
    public void setBoard(Board board) {
        this.board = board;
    }

    /**
     * listener interface to send events
     * 
     * @author Nicolas Winkler
     * 
     */
    public static interface HouseRegisterListener {
        /**
         * invoked when an entry has changed
         * 
         * @param sender
         *            the sending register
         * @param position
         *            the position where something changed
         * @param oldNumber
         *            the number of houses that were at this position before the
         *            event happened
         */
        void houseNumberChanged(HouseRegister sender, int position,
                int oldNumber);
    }
}
