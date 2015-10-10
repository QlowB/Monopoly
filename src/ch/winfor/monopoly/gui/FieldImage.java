package ch.winfor.monopoly.gui;

import static java.lang.Math.min;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import ch.winfor.monopoly.game.BuyableField;
import ch.winfor.monopoly.game.Field;
import ch.winfor.monopoly.game.Game;
import ch.winfor.monopoly.game.MonopolyGroup;
import ch.winfor.monopoly.game.PropertyField;

/**
 * image of one Field
 * 
 * Images can be assigned a field, a position and a width and height. When
 * displayed, the image is drawn at the specified position but with negative
 * width and height. (This can be useful, because the top flank of the board
 * should be drawn upside down, and the other parts are rotated so they too are
 * displayed correctly.)
 * 
 * @author Nicolas Winkler
 * 
 */
public class FieldImage {

    /** the x coordinate to draw the field */
    private int xPos;

    /** the y coordinate to draw the field */
    private int yPos;

    /** reference to the actual field, which is displayed in this image */
    private Field field;

    /** image to draw stuff */
    private BufferedImage image;

    /** reference to the game */
    private Game game;

    /** the maximum font size possible */
    private static final int MAX_FONT_SIZE = 64;

    /** array of fonts for different font sizes */
    private static Font[] fonts;

    /**
     * creates the image based on width, height and the field which should be
     * displayed
     * 
     * @param width
     *            the width of the image in pixels
     * @param height
     *            the height of the image in pixels
     * @param field
     *            reference to the field which should be displayed
     * @param game
     *            the game which the field belongs to
     */
    public FieldImage(int width, int height, Field field, Game game) {
        this(0, 0, width, height, field, game);
    }

    /**
     * creates the image based on position, size and the field which should be
     * displayed
     * 
     * @param x
     *            the x-coordinate of the position in pixels
     * @param y
     *            the y-coordinate of the position in pixels
     * @param width
     *            the width of the image in pixels
     * @param height
     *            the height of the image in pixels
     * @param field
     *            reference to the field which should be displayed
     * @param game
     *            the game which the field belongs to
     */
    public FieldImage(int x, int y, int width, int height, Field field,
            Game game) {
        this.xPos = x;
        this.yPos = y;
        this.game = game;
        this.field = field;
        if (width <= 0 || height <= 0) {
            width = 1;
            height = 1;
        }
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        create();
    }

    /**
     * draws the content of the image
     */
    public void create() {
        if (getWidth() < 10)
            return;

        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        g.setBackground(BoardPanel.STANDARD_BACKGROUND_COLOR);
        g.clearRect(0, 0, image.getWidth(), image.getHeight());

        if (field instanceof PropertyField) {
            PropertyField propertyField = (PropertyField) field;
            drawColor(propertyField, g);
            drawCaption(g);
        } else if (field instanceof Field.CornerField) {
            // Field.CornerField cornerField = (Field.CornerField) field;
            drawTiltedCaption(g);
        } else {
            drawCaption(g);
        }
    }

    /**
     * draws the field's caption on the image rotated 45 degrees
     * 
     * @param g
     *            the graphics to draw onto
     */
    private void drawTiltedCaption(Graphics2D g) {
        int fontSize = Math.min(getWidth(), getHeight()) / 5;
        g.setFont(new Font("Arial", Font.BOLD, fontSize));
        AffineTransform at = g.getTransform();
        g.rotate(-Math.PI / 4, getWidth() / 2, getHeight() / 2);
        g.setColor(Color.BLACK);
        int textWidth = g.getFontMetrics().stringWidth(field.getName());
        int minWH = Math.min(getWidth(), getHeight());

        if (textWidth > minWH * Math.sqrt(2.0) * 0.8) {
            fontSize = (int) (fontSize * minWH * Math.sqrt(2.0) * 0.8 / textWidth);
            g.setFont(new Font("Arial", Font.BOLD, fontSize));
            textWidth = g.getFontMetrics().stringWidth(field.getName());
        }

        g.drawString(field.getName(), getWidth() / 2 - textWidth / 2,
                getHeight() / 2);
        g.setTransform(at);
    }

    /**
     * draws the upper color part of the field
     * 
     * @param propertyField
     *            the field whose color should be drawn
     * @param g
     *            the graphics to draw onto
     */
    private void drawColor(PropertyField propertyField, Graphics2D g) {
        int height = getHeight() / 4;
        MonopolyGroup group = propertyField.getGroup();
        if (group != null) {
            g.setColor(group.getColor());
        } else
            g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), height);
        g.setColor(Color.BLACK);

        Stroke s = g.getStroke();
        g.setStroke(new BasicStroke(2.0f));
        g.drawLine(1, height - 1, getWidth() - 1, height - 1);
        g.setStroke(s);

        drawHouses(g);
    }

    /**
     * draws the houses on the color part of the property field image
     * 
     * @param g
     *            the graphics to draw onto
     */
    private void drawHouses(Graphics2D g) {
        int houses = game.getHousesOn(field);
        final int hotel = game.getBoard().getMaxHouses();
        boolean drawHotel = false;

        if (houses == hotel) {
            drawHotel = true;
            houses = 1;
        }

        if (houses > 0) {
            final int spaceBetweenHouses = 2;

            int height = min(getHeight() / 6, getWidth() / houses - 2
                    * spaceBetweenHouses);

            int y = (getHeight() / 4 - height) / 2;
            int x = y;

            if (drawHotel)
                g.setColor(optimizeColor(Color.RED));
            else
                g.setColor(optimizeColor(Color.GREEN.darker()));
            for (int i = 0; i < houses; i++) {
                g.fillRect(x, y, height, height);
                x += height + spaceBetweenHouses;
            }
        }
    }

    private Color optimizeColor(Color color) {
        int maxDist = 0;
        if (field instanceof PropertyField) {
            PropertyField propertyField = (PropertyField) field;
            Color c = propertyField.getGroup().getColor();
            maxDist = Math.max(maxDist, Math.abs(color.getRed() - c.getRed()));
            maxDist = Math
                    .max(maxDist, Math.abs(color.getBlue() - c.getBlue()));
            maxDist = Math.max(maxDist,
                    Math.abs(color.getGreen() - c.getGreen()));

            if (maxDist < 20)
                color = color.darker();
        }
        return color;
    }

    /**
     * draws the field's caption on the image
     * 
     * @param g
     *            the graphics to draw onto
     */
    private void drawCaption(Graphics2D g) {
        int fontSize = Math.min(getWidth(), getHeight()) / 5;
        g.setFont(getFont(fontSize));
        g.setColor(Color.BLACK);

        String caption = field.getName();

        int y = getHeight() / 4 + fontSize + 3;

        String[] lines = getLines(caption, getWidth(), g.getFontMetrics());

        for (int i = 0; i < lines.length; i++) {
            int textWidth = g.getFontMetrics().stringWidth(lines[i]);
            if (textWidth >= getWidth() - 5) {
                fontSize = fontSize * (getWidth() - 8) / textWidth;
                g.setFont(getFont(fontSize));
            }
        }
        y = writeLines(lines, g, y);


        if (field instanceof BuyableField) {
            BuyableField buyableField = (BuyableField) field;
            String priceTag = game.getBoard().getCurrencyText(
                    buyableField.getPrice());
            lines = getLines(priceTag, getWidth(), g.getFontMetrics());
            y += getHeight() / 20;
            y = writeLines(lines, g, y);
        }
    }

    private int writeLines(String[] lines, Graphics g, int y) {
        int fontSize = g.getFont().getSize();
        for (int i = 0; i < lines.length; i++) {
            int textWidth = g.getFontMetrics().stringWidth(lines[i]);
            g.drawString(lines[i], image.getWidth() / 2 - textWidth / 2, y);
            y += fontSize;
        }
        return y;
    }

    /**
     * @param size
     *            the size in pixels of the desired font
     * @return the font
     */
    private synchronized Font getFont(int size) {
        assert size <= MAX_FONT_SIZE;

        size = Math.min(size, MAX_FONT_SIZE);

        if (fonts == null)
            fonts = new Font[MAX_FONT_SIZE + 1];

        if (fonts[size] == null) {
            fonts[size] = new Font("Arial", 0, size);
        }

        return fonts[size];
    }

    /**
     * splits a string into smaller segments, which can each be drawn on a
     * separate line; this allows us to use a font size which is still readable.
     * 
     * @param caption
     *            the caption text which has to be split into segments
     * @param width
     *            the maximum width of one line
     * @param fm
     *            the font metrics, which define type, size and style of the
     *            font
     * @return an array containing the separated lines
     */
    private String[] getLines(String caption, int width, FontMetrics fm) {
        int textWidth = fm.stringWidth(caption);
        if (textWidth >= getWidth() - 5) {
            String[] split = caption.split("\\s");
            String line = "";
            int i;
            for (i = 0; i < split.length; i++) {
                if (fm.stringWidth(line + " " + split[i]) < getWidth() - 5) {
                    line += " " + split[i];
                } else
                    break;
            }

            if (i <= 0) {
                i = 1;
                line = split[0];
            }

            assert split.length > 0;

            String newCap = "";
            for (int j = i; j < split.length; j++) {
                newCap += split[j] + " ";
            }

            String[] sub = getLines(newCap.trim(), textWidth, fm);
            String[] ret = new String[sub.length + 1];
            ret[0] = line;
            for (int j = 0; j < sub.length; j++) {
                ret[j + 1] = sub[j];
            }
            return ret;
        }
        return new String[] { caption };
    }

    public BufferedImage getImage() {
        return image;
    }

    /**
     * @return the x coordinate of this image
     */
    public int getX() {
        return xPos;
    }

    /**
     * @return the y coordinate of this image
     */
    public int getY() {
        return yPos;
    }

    /**
     * @return the width of the field image in pixels
     */
    public int getWidth() {
        return image.getWidth();
    }

    /**
     * @return the width of the field image in pixels
     */
    public int getHeight() {
        return image.getHeight();
    }

    /**
     * draws the image on a {@link Graphics2D} structure
     * 
     * @param g
     *            the graphics to draw onto
     */
    public void display(Graphics2D g) {
        g.drawImage(image, xPos, yPos, -image.getWidth(), -image.getHeight(),
                null);
    }

    /**
     * @return the x coordinate of the center point of this image
     */
    public float getCenterX() {
        return xPos - getWidth() / 2;
    }

    /**
     * @return the y coordinate of the center point of this image
     */
    public float getCenterY() {
        return yPos - getHeight() / 2;
    }
}
