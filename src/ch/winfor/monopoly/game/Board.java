package ch.winfor.monopoly.game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import ch.winfor.monopoly.game.Field.JailField;

/**
 * a monopoly board
 * 
 * @author Nicolas Winkler
 * 
 */
public class Board implements Hashable, Serializable {
	/** */
	private static final long serialVersionUID = -8659647362850198800L;

	/**
	 * the length of one edge (in fields) (Basically, this is one quarter of the
	 * total amount of fields on the board.)
	 */
	private int flankSize;

	/** the fields (which can be stepped on with pieces) */
	private Field[] fields;

	/** the startup capital on this board */
	private long startMoney;

	/** the maximum amount of houses possible on a field */
	private int maxHouses;

	/** the cards chance and community chest */
	private HashMap<String, CardCollection> cardStacks;

	/** the names of all card stacks */
	private ArrayList<String> cardStackNames;

	/** all the monopolies in the game */
	private MonopolyGroup[] monopolies;

	/** like Fr. for francs */
	private String currencyPrefix;

	/** like $ for dollars */
	private String currencySuffix;

	/** the standard length of a field */
	public static final int STANDARD_FLANK_SIZE = 10;

	/** the standard equivalent of one hotel in houses */
	public static final int STANDARD_MAX_HOUSES = 5;

	/** number of flanks on a board */
	public static final int N_FLANKS = 4;

	/**
	 * creates a new standard board with standard length (usually 10)
	 * 
	 * @see #STANDARD_FLANK_SIZE
	 */
	public Board() {
		this(STANDARD_FLANK_SIZE, STANDARD_MAX_HOUSES);
	}

	/**
	 * creates a new board with specified flank length
	 * 
	 * @param flankSize
	 *            the length of one edge of the board, measured in number of
	 *            fields
	 */
	public Board(int flankSize, int maxHouses) {
		this.flankSize = flankSize;
		this.maxHouses = maxHouses;
		fields = new Field[flankSize * 4];
		cardStacks = new HashMap<String, CardCollection>();
		cardStackNames = new ArrayList<String>();
	}

	/**
	 * @return the currency prefix
	 */
	public String getCurrencyPrefix() {
		return currencyPrefix;
	}

	/**
	 * @param currencyPrefix
	 *            the new currency prefix to set
	 */
	public void setCurrencyPrefix(String currencyPrefix) {
		this.currencyPrefix = currencyPrefix;
	}

	/**
	 * @return the currency postfix
	 */
	public String getCurrencySuffix() {
		return currencySuffix;
	}

	/**
	 * @param currencyPostfix
	 *            the new currency postfix to set
	 */
	public void setCurrencySuffix(String currencyPostfix) {
		this.currencySuffix = currencyPostfix;
	}

	/**
	 * generates a text out of a price value (e.g. {@code "Fr. 100.-"} out of
	 * {@code 100L})
	 * 
	 * @param price
	 *            the value to convert
	 * @return the new price string
	 */
	public String getCurrencyText(long price) {
		return getCurrencyPrefix() + price + getCurrencySuffix();
	}

	/**
	 * picks a field
	 * 
	 * @param flank
	 *            one of the four flanks of the board
	 * @param index
	 *            the index on the flank
	 * @return the field at the {@code index}th position on the {@code flank}th
	 *         flank
	 */
	public Field getField(int flank, int index) {
		return fields[(flank & 3) * flankSize + (index % (fields.length >> 2))];
	}

	/**
	 * get a field on the board
	 * 
	 * @param index
	 *            absolute index of the field (no flanks, absolute)
	 * @return the field at the specified index
	 */
	public Field getField(int index) {
		return fields[index];
	}

	/**
	 * set a field
	 * 
	 * @param index
	 *            absolute index of the field (no flanks, absolute)
	 * @param field
	 *            the new field value
	 */
	public void setField(int index, Field field) {
		fields[index] = field;
	}

	/**
	 * @return the absolute amount of fields
	 */
	public int getAbsoluteLength() {
		return fields.length;
	}

	/**
	 * @return the length of one flank
	 */
	public int getFlankLength() {
		return fields.length >> 2; // fields.length / 4
	}

	/**
	 * @return the startup capital
	 */
	public long getStartMoney() {
		return startMoney;
	}

	/**
	 * @param startMoney
	 *            the startup capital to set
	 */
	public void setStartMoney(long startMoney) {
		this.startMoney = startMoney;
	}

	/**
	 * searches for an instance of {@link JailField} and returns the first match
	 * 
	 * @return the first instance of {@link JailField} found or
	 *         <code>null</code> if no jail field found
	 */
	public JailField getJailField() {
		for (int i = 0; i < fields.length; i++) {
			if (fields[i] instanceof JailField) {
				return (JailField) fields[i];
			}
		}
		return null;
	}

	/**
	 * the maximum amount of houses is the equivalent of one hotel (normally it
	 * is 5)
	 * 
	 * @return the maximum amount of houses possible on this board
	 */
	public int getMaxHouses() {
		return maxHouses;
	}

	/**
	 * adds a stack of cards to the board
	 * 
	 * @param stackName
	 *            the name of the stack - if the board already has a stack with
	 *            the same name, the two stacks are put together
	 * @param cards
	 *            the array containing the cards in the stack
	 */
	public void addStack(String stackName, CardCollection cards) {
		if (cardStacks.containsKey(stackName)) {
			CardCollection cs = cardStacks.get(stackName);
			cs.merge(cards);
		} else {
			cardStacks.put(stackName, cards);
			cardStackNames.add(stackName);
		}
	}

	public Set<String> getDeckNames() {
		return cardStacks.keySet();
	}

	/**
	 * gets a specific {@link CardCollection}
	 * 
	 * @param stackName
	 *            the name of the desired stack
	 * @return the stack with the specified name
	 */
	public CardCollection getCardStack(String stackName) {
		return cardStacks.get(stackName);
	}

	/**
	 * @param index
	 *            the index of the monopoly
	 * @return the monopoly with the specified index
	 */
	public MonopolyGroup getMonopoly(int index) {
		return monopolies[index];
	}

	/**
	 * @return the number of monopolies in the game
	 */
	public int getNMonopolies() {
		return monopolies.length;
	}

	/**
	 * @param monopolies
	 *            the monopolies to set
	 */
	public void setMonopolies(MonopolyGroup[] monopolies) {
		this.monopolies = monopolies;
	}

	/**
	 * finds the next railroad field from a specified index on
	 * 
	 * @param position
	 *            the index to start looking from
	 * @return the index of the next railroad field
	 */
	public int getNextRailroadIndex(int position) {
		return getNextFieldIndex(position, RailroadField.class);
	}

	/**
	 * finds the next utility field from a specified index on
	 * 
	 * @param position
	 *            the index to start looking from
	 * @return the index of the next company field
	 */
	public int getNextUtilityIndex(int position) {
		return getNextFieldIndex(position, CompanyField.class);
	}

	/**
	 * finds the next field of a specific type from a specified index on
	 * 
	 * @param position
	 *            the index to start looking from
	 * @param fieldType
	 *            the desired type of the field
	 * @return the index of the next field of the desired type
	 */
	private int getNextFieldIndex(int position, Class<?> fieldType) {
		int cursor = position + 1;
		while (cursor != position) {
			if (getField(cursor).getClass().equals(fieldType)) {
				return cursor;
			}
			cursor++;
			cursor %= getAbsoluteLength();
		}
		return position;
	}

	public int getFieldIndex(Field field) {
		int length = getAbsoluteLength();
		for (int i = 0; i < length; i++) {
			if (fields[i] == field) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * finds the index of a monopoly
	 * 
	 * @param group
	 *            the monopoly
	 * @return the index if this monopoly, <code>-1</code> if the monopoly is
	 *         unknown to this board
	 */
	public int getMonopolyIndex(MonopolyGroup group) {
		for (int i = 0; i < monopolies.length; i++) {
			if (monopolies[i] == group)
				return i;
		}
		return -1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.winfor.monopoly.game.Hashable#createHash()
	 */
	public long createHash() {
		long hash = 0;
		hash += flankSize * 342343;
		for (int i = 0; i < fields.length; i++) {
			hash ^= fields[i].createHash();
			hash ^= hash >> 63;
			hash <<= 1;
		}
		hash ^= startMoney * 76363477;
		hash *= 27509;
		hash += maxHouses * 27493217;
		for (int i = 0; i < cardStackNames.size(); i++) {
			String name = cardStackNames.get(i);
			hash += cardStacks.get(name).createHash() << 16;
		}

		for (int i = 0; i < monopolies.length; i++) {
			hash ^= monopolies[i].createHash();
			hash *= 239;
		}
		return hash;
	}
}
