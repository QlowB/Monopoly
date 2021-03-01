package ch.winfor.monopoly.game;

/**
 * a field that can be bought
 * 
 * @author Nicolas Winkler
 * 
 */
public class BuyableField extends Field {
    /** */
    private static final long serialVersionUID = 4211115957701118497L;

    /** the price to buy this property */
    protected long price;

    /** this property's mortgage value */
    protected long mortgageValue;

    public BuyableField(String name, long price) {
        super(name);
        this.price = price;
    }

    /**
     * @return the price to buy this property
     */
    public long getPrice() {
        return price;
    }

    /**
     * @param price
     *            the new price to set
     */
    public void setPrice(long price) {
        this.price = price;
    }

    /**
     * @return this property's mortgage value
     */
    public long getMortgageValue() {
        return mortgageValue;
    }

    /**
     * @param mortgageValue
     *            the mortgage value to set
     */
    public void setMortgageValue(long mortgageValue) {
        this.mortgageValue = mortgageValue;
    }
}
