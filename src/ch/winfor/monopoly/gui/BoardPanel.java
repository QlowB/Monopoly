package ch.winfor.monopoly.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import ch.winfor.monopoly.game.Board;
import ch.winfor.monopoly.game.BuyableField;
import ch.winfor.monopoly.game.Card;
import ch.winfor.monopoly.game.Card.KeepableCard;
import ch.winfor.monopoly.game.Game;
import ch.winfor.monopoly.game.GameListener;
import ch.winfor.monopoly.game.Player;
import ch.winfor.monopoly.game.PlayingPiece;

/**
 * panel displaying the whole playing board of a game
 * 
 * @author Nicolas Winkler
 * 
 */
public class BoardPanel extends JPanel implements GameListener, MouseListener {
    private static final long serialVersionUID = -7081424827169475469L;

    /** the standard background color of of the board */
    public static final Color STANDARD_BACKGROUND_COLOR = new Color(240, 240,
            200);

    /** the board that is displayed */
    private Game game;

    /** the width of the track i.e. height of the fields */
    private int trackWidth;

    /** the displayed images of the fields */
    private FieldImage[] fields;

    /** subscribers to events of this board panel */
    private List<BoardPanelListener> boardPanelListers;

    /**
     * Create the panel.
     * 
     * @param game
     *            the game which should be displayed
     */
    public BoardPanel(Game game) {
        super.setMinimumSize(new Dimension(550, 550));
        trackWidth = 90;
        this.game = game;
        game.addGameListener(this);
        fields = new FieldImage[game.getBoard().getAbsoluteLength()];

        boardPanelListers = new ArrayList<BoardPanelListener>();

        this.addMouseListener(this);
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
        if (game != null)
            game.removeGameListener(this);
        this.game = game;
        game.addGameListener(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        // g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
        // RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setBackground(STANDARD_BACKGROUND_COLOR);
        g2d.clearRect(0, 0, getWidth(), getHeight());

        trackWidth = (int) (Math.min(getWidth(), getHeight()) / 7.5);

        buildFields();
        displayFields(g2d);
        displayPlayingPieces(g2d);
    }

    private void buildFields() {
        for (int i = 0; i < Board.N_FLANKS; i++)
            buildFlank(i);
    }

    private void displayFields(Graphics2D g) {
        for (int i = 0; i < Board.N_FLANKS; i++)
            displayFlank(i, g);
    }

    private void buildFlank(int flankIndex) {
        assert flankIndex >= 0 && flankIndex < 4;

        int width = getWidth();
        int height = getHeight();

        if ((flankIndex & 1) != 0) { // uneven flank
            width = height;
            height = getWidth();
        }

        Board board = game.getBoard();

        int x = trackWidth;
        int y = trackWidth;

        int fieldsOffset = flankIndex * board.getFlankLength();

        assert fieldsOffset >= 0 && fieldsOffset <= fields.length;

        fields[fieldsOffset] = new FieldImage(x, y, trackWidth, trackWidth,
                board.getField(flankIndex, 0), game);

        float fieldWidth = (float) (width - 2 * trackWidth)
                / (board.getFlankLength() - 1);

        for (int i = 1; i < board.getFlankLength(); i++) {
            x = (int) (trackWidth + i * fieldWidth);

            // System.out.println(x + " --- " + (trackWidth + i * fieldWidth));
            fields[fieldsOffset + i] = new FieldImage(x, y, (int) fieldWidth,
                    trackWidth, board.getField(flankIndex, i), game);
        }
    }

    /**
     * displays one quarter of the board
     * 
     * @param flankIndex
     *            the index of the flank
     * @param g
     *            the graphics to draw onto
     */
    private void displayFlank(int flankIndex, Graphics2D g) {
        assert flankIndex >= 0 && flankIndex < 4;

        Board board = game.getBoard();
        AffineTransform at = g.getTransform();
        g.rotate((flankIndex + 2) * Math.PI / 2, getWidth() * 0.5,
                getHeight() * 0.5);
        if ((flankIndex & 1) != 0) {
            g.translate((getWidth() - getHeight()) * 0.5,
                    -(getWidth() - getHeight()) * 0.5);
        }

        int fieldsOffset = flankIndex * board.getFlankLength();
        for (int i = 0; i < board.getFlankLength(); i++) {
            FieldImage field = fields[fieldsOffset + i];
            field.display(g);
        }

        for (int i = 0; i < board.getFlankLength(); i++) {
            FieldImage field = fields[fieldsOffset + i];
            int x = field.getX();
            int y = field.getY();
            int width = field.getWidth();
            int height = field.getHeight();

            Stroke temp = g.getStroke();
            g.setStroke(new BasicStroke(2.0f));
            g.setColor(Color.BLACK);

            g.drawLine(x, y, x, y - height);
            g.drawLine(x, y, x - width, y);

            g.setStroke(temp);
        }

        g.setTransform(at);
    }

    /**
     * draws the playing pieces
     * 
     * @param g
     *            the graphics to draw onto
     */
    private void displayPlayingPieces(Graphics2D g) {
        int[] positions = new int[game.getBoard().getAbsoluteLength()];
        for (int i = 0; i < game.getNPlayers(); i++) {
            PlayingPiece piece = game.getPiece(i);
            int pos = piece.getPosition();

            displayPiecesOnField(piece, pos, positions[pos], g);
            positions[pos]++;
        }
    }

    /**
     * displays a playing piece on the field
     * 
     * @param piece
     *            the piece to draw
     * @param index
     *            the index of the field on which the piece stands
     * @param number
     *            displays the piece on the position for the numberth piece on
     *            this field
     * @param g
     *            the graphics to draw onto
     */
    private void displayPiecesOnField(PlayingPiece piece, int index,
            int number, Graphics2D g) {
        float x = fields[index].getCenterX();
        float y = fields[index].getCenterY();

        final int xMultiplier = 5;
        final int yMultiplier = 7;

        x -= number * xMultiplier;
        y -= number * yMultiplier;

        int flankLength = game.getBoard().getFlankLength();
        int flank = index / flankLength;
        index %= flankLength;

        AffineTransform at = g.getTransform();

        g.rotate((flank + 2) * Math.PI / 2, getWidth() * 0.5, getHeight() * 0.5);
        if ((flank & 1) != 0) {
            g.translate((getWidth() - getHeight()) * 0.5,
                    -(getWidth() - getHeight()) * 0.5);
        }
        g.setColor(piece.getColor());
        g.fillOval((int) x - 10, (int) y - 10, 20, 20);

        g.setTransform(at);
    }

    /**
     * finds the index of the field displayed at a specific position
     * 
     * @param x
     *            the x-coordinate of the position
     * @param y
     *            the y-coordinate of the position
     * @return the index of the field at this position, <code>-1</code> if no
     *         field is found
     */
    public int getFieldIndexAtPosition(int x, int y) {
        Board board = game.getBoard();
        for (int flank = 0; flank < Board.N_FLANKS; flank++) {
            int startIndex = board.getFlankLength() * flank;
            Point pt = new Point(x, y);
            rotatePoint(pt, flank);
            for (int i = 0; i < board.getFlankLength(); i++) {
                FieldImage fi = fields[startIndex + i];
                if ((pt.x <= fi.getX() && pt.x >= fi.getX() - fi.getWidth())
                        && (pt.y <= fi.getY() && pt.y >= fi.getY()
                                - fi.getHeight())) {
                    return startIndex + i;
                }
            }
        }
        return 0;
    }

    /**
     * transforms a point into the system of a specific flank
     * 
     * @param pt
     *            the point to transform
     * @param flank
     *            the desired flank
     */
    private void rotatePoint(Point pt, int flank) {

        AffineTransform at = new AffineTransform();

        at.rotate((flank + 2) * Math.PI / 2, getWidth() * 0.5,
                getHeight() * 0.5);

        if ((flank & 1) != 0) {
            at.translate((getWidth() - getHeight()) * 0.5,
                    -(getWidth() - getHeight()) * 0.5);
        }

        try {
            at.inverseTransform(pt, pt);
        } catch (NoninvertibleTransformException e) {
            e.printStackTrace();
        }
    }

    /**
     * adds an event subscriber to the panel
     * 
     * @param bpl
     *            the subscriber to add
     */
    public void addBoardPanelListener(BoardPanelListener bpl) {
        boardPanelListers.add(bpl);
    }

    /**
     * removes an event subscriber from the panel
     * 
     * @param bpl
     *            the subscriber to remove
     */
    public void removeBoardPanelListener(BoardPanelListener bpl) {
        boardPanelListers.remove(bpl);
    }

    /**
     * notifies the subscribers that a field has been clicked
     * 
     * @param fieldIndex
     *            the index of the clicked field
     */
    protected void fireClickedOnField(int fieldIndex) {
        for (BoardPanelListener bpl : boardPanelListers) {
            bpl.clickedOnField(this, fieldIndex);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();

        if (e.getButton() == MouseEvent.BUTTON1) {
            int field = getFieldIndexAtPosition(x, y);
            if (field != -1) {
                fireClickedOnField(field);
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
     */
    @Override
    public void mousePressed(MouseEvent e) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseReleased(MouseEvent e) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseEntered(MouseEvent e) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void playingPieceMoved(Game sender, int pieceIndex, int oldPosition) {
        repaint();
    }

    @Override
    public void playerEndedTurn(Game sender, int playerIndex) {
    }

    @Override
    public void playerWealthChanged(Game sender, Player player,
            long wealthBefore) {
    }

    @Override
    public void playerWentBankrupt(Game sender, Player player) {
    }

    @Override
    public void playersJailStateChanged(Game sender, Player player,
            int jailStateBefore) {
    }

    @Override
    public void playerObtained(Game sender, Player player, BuyableField field) {
    }

    @Override
    public void playerKeepsCard(Game sender, Player player, KeepableCard card) {
    }

    @Override
    public void houseNumberChanged(Game sender, int position, int oldValue) {
        repaint();
    }

    @Override
    public void cardDrawn(Game sender, String deckName, Card c) {
    }

    /**
     * listener interface for catching events sent from the board panel
     * 
     * @author Nicolas Winkler
     * 
     */
    public static interface BoardPanelListener {

        /**
         * invoked when a field on the panel is clicked on
         * 
         * @param sender
         *            the {@link BoardPanel} that sent the event
         * @param fieldIndex
         *            the index of the field that was clicked
         */
        public void clickedOnField(BoardPanel sender, int fieldIndex);
    }
}
