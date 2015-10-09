package ch.winfor.monopoly.game;

/**
 * a railroad field
 * 
 * @author Nicolas Winkler
 * 
 */
public class RailroadField extends BuyableField {

	/** */
	private static final long serialVersionUID = 5343309282231898485L;

	/** storing the rent dependant from the number of railroads owned */
	long[] rent;

	public RailroadField(String name, long price, int maxRailroads) {
		super(name, price);
		rent = new long[maxRailroads];
	}

	/**
	 * gets the price to spend a turn on this property
	 * 
	 * @param railroads
	 *            the number of railroads owned by the owner
	 * @return the rent on this property
	 */
	public long getRent(int railroads) {
		assert railroads - 1 >= 0 && railroads - 1 < rent.length : "Invalid number of railroads: "
				+ railroads;
		return rent[railroads - 1];
	}

	/**
	 * sets the price to spend a turn on this property
	 * 
	 * @param railroads
	 *            the number of railroads owned by the owner
	 * @param rent
	 *            the rent on this property
	 */
	public void setRent(int railroads, long rent) {
		assert railroads - 1 >= 0 && railroads - 1 < this.rent.length : "Invalid number of railroads";
		this.rent[railroads - 1] = rent;
	}

	/**
	 * @return the maximum number of railrodas that can be owned according to
	 *         this railroad field
	 */
	public int getMaxRailroads() {
		return rent.length;
	}
}
