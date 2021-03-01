package ch.winfor.monopoly.game;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * represents a monopoly
 * 
 * @author Nicolas Winkler
 * 
 */
public class MonopolyGroup implements Serializable {

    /** */
    private static final long serialVersionUID = 2193317783457664711L;

    /** the properties that this monopoly is based on */
    private ArrayList<PropertyField> fields;

    /** color of the property cards */
    private Color color;

    /**
     * initializes
     * 
     * @param color
     *            the color of the monopoly
     */
    public MonopolyGroup(Color color) {
        fields = new ArrayList<PropertyField>();
        this.color = color;
    }

    /**
     * creates a new white monopoly group
     */
    public MonopolyGroup() {
        this(Color.WHITE);
    }

    /**
     * @param i
     *            the index of the desired property
     * @return the property with the specified index
     */
    public PropertyField getField(int i) {
        return fields.get(i);
    }

    public int getNFields() {
        return fields.size();
    }

    /**
     * registers a {@link PropertyField} to this monopoly
     * 
     * This method will set the group of the field to this one.
     * 
     * @param field
     *            the field to register
     */
    public void registerField(PropertyField field) {
        field.setGroup(this);
        this.fields.add(field);
    }

    /**
     * determines if a property belongs to this monopoly
     * 
     * @param field
     *            the field to test
     * @return <code>true</code> if field belongs to this monopoly,
     *         <code>false</code> otherwise
     */
    public boolean hasField(PropertyField field) {
        return fields.contains(field);
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

    public long createHash() {
        long hash = color.getRGB();
        for (int i = 0; i < fields.size(); i++) {
            hash += fields.get(i).createHash();
            hash *= 3251;
        }
        return hash;
    }
}
