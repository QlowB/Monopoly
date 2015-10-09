package ch.winfor.monopoly.game;

import java.io.Serializable;
import java.util.ArrayList;

import ch.winfor.monopoly.game.Card.KeepableCard;

/**
 * stores information about a player
 * 
 * @author Nicolas Winkler
 * 
 */
public class Player implements Serializable, Hashable {
	/** */
	private static final long serialVersionUID = 8867900086231318808L;

	/** money of this player */
	private long wealth;

	/** the playing piece */
	private PlayingPiece piece;

	/** the alias for this player */
	private String name;

	/** properties and other fields the player has bought */
	private ArrayList<BuyableField> possessions;

	/** cards that can be kept */
	private ArrayList<KeepableCard> cards;

	/**
	 * {@code > 0} if the player is currently in jail; defines how many rounds
	 * the player has to stay in jail
	 */
	private int inJailRounds;

	/** stores the subscribers to events from this player */
	private transient ArrayList<PlayerListener> playerListeners;

	public Player(String name) {
		this.setName(name);
		possessions = new ArrayList<BuyableField>();
		cards = new ArrayList<KeepableCard>();
		piece = new PlayingPiece();

		playerListeners = new ArrayList<PlayerListener>();
	}

	/**
	 * tests if the player possesses an item
	 * 
	 * @param field
	 *            the field to check
	 * @return if this player has bought this field
	 */
	public boolean possesses(BuyableField field) {
		return possessions.contains(field);
	}

	/**
	 * adds a property to the possessions of this player
	 * 
	 * @param field
	 *            the new property
	 */
	public void addPossession(BuyableField field) {
		possessions.add(field);
		fireAddedPropertyPossession(field);
	}

	/**
	 * adds a card (namely, one of the the get-out-of-jail cards) to the
	 * possessions of this player
	 * 
	 * @param card
	 *            the new card
	 */
	public void addPossession(KeepableCard card) {
		cards.add(card);
		fireAddedCardPossession(card);
	}

	/**
	 * @return the piece
	 */
	public PlayingPiece getPiece() {
		return piece;
	}

	/**
	 * @param piece
	 *            the piece to set
	 */
	public void setPiece(PlayingPiece piece) {
		this.piece = piece;
	}

	/**
	 * @return the wealth of the player
	 */
	public long getWealth() {
		return wealth;
	}

	/**
	 * @param wealth
	 *            the new wealth
	 */
	public void setWealth(long wealth) {
		long oldWealth = this.wealth;
		this.wealth = wealth;
		fireWealthChanged(oldWealth, wealth);
	}

	/**
	 * takes money from the player's wealth
	 * 
	 * This function does almost the same as {@link #pay(long)} except it does
	 * not add the money specified by the parameter but subtract it from the
	 * fortune of the player
	 * 
	 * @param amount
	 *            the amount of money to take
	 * 
	 * @see #pay(long)
	 */
	public void charge(long amount) {
		long oldWealth = wealth;
		wealth -= amount;
		fireWealthChanged(oldWealth, wealth);
	}

	/**
	 * adds money to the player's wealth
	 * 
	 * This function does almost the same as {@link #charge(long)} except it
	 * does not subtract the money specified by the parameter but add it to the
	 * fortune of the player
	 * 
	 * @param amount
	 *            the amount of money to pay
	 * @see #charge(long)
	 */
	public void pay(long amount) {
		long oldWealth = wealth;
		wealth += amount;
		fireWealthChanged(oldWealth, wealth);
	}

	/**
	 * @return if the player is currently in jail
	 */
	public boolean isInJail() {
		return inJailRounds > 0;
	}

	/**
	 * @return the number of rounds the player has to stay in jail
	 */
	public int getInJailRounds() {
		return inJailRounds;
	}

	/**
	 * @param rounds
	 *            the number of rounds the player has to stay in jail
	 */
	public void setInJailRounds(int rounds) {
		int oldRounds = this.inJailRounds;
		this.inJailRounds = rounds;
		fireJailStateChanged(oldRounds);
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * sends an event to all subscribers
	 * 
	 * @param oldWealth
	 *            the wealth before the change
	 * @param newWealth
	 *            the wealth after the change
	 */
	protected void fireWealthChanged(long oldWealth, long newWealth) {
		for (PlayerListener pl : playerListeners) {
			pl.wealthChanged(this, oldWealth, newWealth);
		}

		if (oldWealth >= 0 && newWealth < 0) {
			for (PlayerListener pl : playerListeners) {
				pl.wentBankrupt(this);
			}
		}
	}

	/**
	 * sends an jail state changed event to all subscribers
	 * 
	 * @param oldRounds
	 *            the old value of rounds
	 * @see PlayerListener#jailStateChanged(Player)
	 */
	protected void fireJailStateChanged(int oldRounds) {
		for (PlayerListener pl : playerListeners) {
			pl.jailStateChanged(this, oldRounds);
		}
	}

	/**
	 * sends an added-property-possession-event
	 * 
	 * @param property
	 *            the newly obtained property
	 */
	protected void fireAddedPropertyPossession(BuyableField property) {
		for (PlayerListener pl : playerListeners) {
			pl.addedPropertyPossession(this, property);
		}
	}

	/**
	 * sends an added-card-possession-event
	 * 
	 * @param card
	 *            the newly obtained card
	 */
	protected void fireAddedCardPossession(KeepableCard card) {
		for (PlayerListener pl : playerListeners) {
			pl.addedCardPossession(this, card);
		}
	}

	/**
	 * adds a new listener to the player
	 * 
	 * @param pl
	 *            the new listener
	 */
	public void addPlayerListener(PlayerListener pl) {
		if (playerListeners == null)
			playerListeners = new ArrayList<PlayerListener>();
		playerListeners.add(pl);
	}

	/**
	 * removes a listener
	 * 
	 * @param pl
	 *            the listener which should be removed
	 */
	public void removePlayerListener(PlayerListener pl) {
		if (playerListeners == null)
			playerListeners = new ArrayList<PlayerListener>();
		playerListeners.remove(pl);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.winfor.monopoly.game.Hashable#createHash()
	 */
	@Override
	public long createHash() {
		final long prime = 4938374592840923441L;
		final int prime2 = 493837501;

		long hash = wealth * prime;
		hash += piece.createHash();
		hash += name.hashCode() * prime2;

		hash += inJailRounds * prime;

		for (int i = 0; i < possessions.size(); i++) {
			hash += possessions.get(i).createHash() * prime;
		}

		for (int i = 0; i < cards.size(); i++) {
			hash += cards.get(i).createHash() * prime;
		}

		return hash;
	}

	/**
	 * listener to player events
	 * 
	 * @author Nicolas Winkler
	 * 
	 */
	public static interface PlayerListener extends EventListener {
		/**
		 * invokes always when the player's wealth has changed (e.g. he has
		 * payed something, he receives money etc.)
		 * 
		 * @param sender
		 *            the player who sent the event
		 * @param oldWealth
		 *            the wealth before the change
		 * @param newWealth
		 *            the wealth after the change
		 */
		void wealthChanged(Player sender, long oldWealth, long newWealth);

		/**
		 * invoked after
		 * {@link PlayerListener#wealthChanged(Player, long, long)}, if the new
		 * wealth is now negative.
		 * 
		 * @param sender
		 *            the player who sent the event
		 * @see #wealthChanged(Player, long, long)
		 */
		void wentBankrupt(Player sender);

		/**
		 * invoked when the player went to jail, passed a round in jail, or got
		 * out of jail
		 * 
		 * @param sender
		 *            the player, whose jail state changed
		 * @param oldRounds
		 *            the old value of {@link Player#getInJailRounds()} (before
		 *            the event happened)
		 */
		void jailStateChanged(Player sender, int oldRounds);

		/**
		 * invoked whenever the player obtains a new property
		 * 
		 * @param sender
		 *            the player who owns this stuff now
		 * @param property
		 *            the property he owns now
		 */
		void addedPropertyPossession(Player sender, BuyableField property);

		/**
		 * invoked when a player obtains a new card
		 * 
		 * @param sender
		 *            the player who has the card now
		 * @param card
		 *            the card he owns now
		 */
		void addedCardPossession(Player sender, KeepableCard card);
	}
}