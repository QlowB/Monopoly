package ch.winfor.monopoly.game;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

/**
 * 
 * @author Nicolas Winkler
 * 
 */
public class PlayingPiece implements Serializable, Hashable {

    /** */
    private static final long serialVersionUID = -2940361647635443351L;

    /** the piece's position on the board */
    private int position;

    /** the color of this piece */
    private Color color;

    /** list of listeners to be notified about events */
    private transient ArrayList<PlayingPieceListener> playingPieceListeners;

    /**
     * initializes the piece to position 0
     */
    public PlayingPiece() {
        position = 0;
        color = new Color(new Random().nextInt());
        playingPieceListeners = new ArrayList<PlayingPieceListener>();
    }

    /**
     * @return the absolute position of the playing piece
     */
    public int getPosition() {
        return position;
    }

    /**
     * @param position
     *            the position to set (absolute value)
     */
    public void setPosition(int position) {
        int oldPosition = this.position;
        this.position = position;
        firePositionUpdated(position, oldPosition);
    }

    /**
     * sends a position update event to every subscriber
     * 
     * @param position
     *            the new position
     * @param oldPosition
     *            tho position before the event happened
     */
    protected void firePositionUpdated(int position, int oldPosition) {
        if (playingPieceListeners == null)
            playingPieceListeners = new ArrayList<>();
        for (PlayingPieceListener ppl : playingPieceListeners) {
            ppl.updatedPosition(this, position, oldPosition);
        }
    }

    /**
     * adds a new listener to the playing piece
     * 
     * @param ppl
     *            the new listener
     */
    public void addPlayingPieceListener(PlayingPieceListener ppl) {
        if (playingPieceListeners == null)
            playingPieceListeners = new ArrayList<>();
        playingPieceListeners.add(ppl);
    }

    /**
     * removes a listener
     * 
     * @param ppl
     *            the listener which should be removed
     */
    public void removePlayingPieceListener(PlayingPieceListener ppl) {
        if (playingPieceListeners == null)
            playingPieceListeners = new ArrayList<>();
        playingPieceListeners.remove(ppl);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "PlayingPiece [position=" + position + "]";
    }

    /**
     * sets the color of this piece
     * 
     * @param color
     *            the new color
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * @return the color of this piece
     */
    public Color getColor() {
        return color;
    }

    /*
     * (non-Javadoc)
     * 
     * @see ch.winfor.monopoly.game.Hashable#createHash()
     */
    @Override
    public long createHash() {
        final long prime = 2778494590925926267L;

        long hash = position * prime;
        hash += color.getRGB() * prime;
        return hash;
    }

    /**
     * listener to playing piece events
     * 
     * @author Nicolas Winkler
     * 
     */
    public static interface PlayingPieceListener extends EventListener {
        /**
         * the position of the piece has been changed
         * 
         * @param piece
         *            the playing piece that has been moved
         * @param position
         *            the new position
         * @param oldPosition
         *            the old position
         */
        void updatedPosition(PlayingPiece piece, int position, int oldPosition);
    }
}
