package ch.winfor.monopoly.game;

/**
 * one of those 'normal' property fields
 * 
 * @author Nicolas Winkler
 * 
 */
public class PropertyField extends BuyableField {

    /** */
    private static final long serialVersionUID = 7738281377581434568L;

    /** the monopoly this field belongs to */
    protected MonopolyGroup group;

    /** price for one house on this property */
    protected long housePrice;

    /**
     * how much rent has to be paid on this property (with respect to houses
     * built)
     */
    protected long[] rent;

    /**
     * initializes the field and automatically registers this field to a
     * {@link MonopolyGroup}
     * 
     * @param name
     *            name of the property
     * @param price
     *            what buying this field costs
     * @param group
     *            the monopoly this field belongs to
     * @param board
     *            the board on which the field is placed
     */
    public PropertyField(String name, long price, MonopolyGroup group,
            Board board) {
        super(name, price);
        this.group = group;
        group.registerField(this);
        rent = new long[board.getMaxHouses() + 1];
    }

    /**
     * @return the monopoly it belongs to
     */
    public MonopolyGroup getGroup() {
        return group;
    }

    /**
     * attatch a monopoly to this property
     * 
     * @param group
     *            the monopoly
     */
    public void setGroup(MonopolyGroup group) {
        this.group = group;
    }

    /**
     * @return the price per house on this property
     */
    public long getHousePrice() {
        return housePrice;
    }

    /**
     * set the price per house
     * 
     * @param housePrice
     *            the new price per house on this property
     */
    public void setHousePrice(long housePrice) {
        this.housePrice = housePrice;
    }

    /**
     * gets the price to spend a turn on this property
     * 
     * @param houses
     *            the number of houses built on the property
     * @return the rent on this property
     */
    public long getRent(int houses) {
        assert houses >= 0 && houses < rent.length : "Invalid number of houses";
        return rent[houses];
    }

    /**
     * sets the price to spend a turn on this property
     * 
     * @param houses
     *            the number of houses built on the property
     * @param rent
     *            the rent on this property
     */
    public void setRent(int houses, long rent) {
        assert houses >= 0 && houses < this.rent.length : "Invalid number of houses";
        this.rent[houses] = rent;
    }

    /**
     * @return the maximum number of houses supported
     */
    public int getMaxHouses() {
        return rent.length - 1;
    }
}
