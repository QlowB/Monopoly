package ch.winfor.monopoly.game;

/**
 * one of the two fields where you have to pay a tax if you get on it
 * 
 * @author Nicolas Winkler
 * 
 */
public class TaxField extends Field {

	/** */
	private static final long serialVersionUID = -1072428948952950971L;

	/** amount of taxes to pay */
	protected long taxAmount;

	public TaxField(String name, long tax) {
		super(name);
		this.taxAmount = tax;
	}

	/**
	 * @return the tax amount
	 */
	public long getTaxAmount() {
		return taxAmount;
	}

	/**
	 * @param taxAmount
	 *            the new tax amount to set
	 */
	public void setTaxAmount(long taxAmount) {
		this.taxAmount = taxAmount;
	}
}
