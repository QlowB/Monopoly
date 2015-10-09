package ch.winfor.monopoly.game;

/**
 * 
 * @author Nicolas Winkler
 * 
 */
public class CompanyField extends BuyableField {

	/** */
	private static final long serialVersionUID = -9035762819793736950L;

	/**
	 * storing the rent multiplicator dependent from the number of companies
	 * owned
	 */
	long[] rentMultiplicator;

	public CompanyField(String name, long price, int nCompanies) {
		super(name, price);
		rentMultiplicator = new long[nCompanies];
	}

	/**
	 * gets the price multiplicator to spend a turn on this property
	 * 
	 * @param companies
	 *            the number of railroads owned by the owner
	 * @return the rent on this property
	 */
	public long getRentMultiplicator(int companies) {
		assert companies - 1 >= 0 && companies - 1 < rentMultiplicator.length : "Invalid number of railroads";
		return rentMultiplicator[companies - 1];
	}

	/**
	 * sets the price multiplicator to spend a turn on this property
	 * 
	 * @param companies
	 *            the number of companies owned by the owner
	 * @param rentMultiplicator
	 *            the rent on this property
	 */
	public void setRentMultiplicator(int companies, long rentMultiplicator) {
		assert companies - 1 >= 0
				&& companies - 1 < this.rentMultiplicator.length : "Invalid number of railroads";
		this.rentMultiplicator[companies - 1] = rentMultiplicator;
	}

	/**
	 * @return the maximum number of companies supported
	 */
	public int getMaxCompanies() {
		return rentMultiplicator.length;
	}
}
