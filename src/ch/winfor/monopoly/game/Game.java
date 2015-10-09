package ch.winfor.monopoly.game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

import ch.winfor.monopoly.game.Card.KeepableCard;
import ch.winfor.monopoly.game.HouseRegister.HouseRegisterListener;
import ch.winfor.monopoly.game.Player.PlayerListener;
import ch.winfor.monopoly.game.PlayingPiece.PlayingPieceListener;

/**
 * This class is the main part of the game. It connects {@link Board},
 * {@link PlayingPiece}s etc.
 * 
 * @author Nicolas Winkler
 * 
 */
public class Game implements PlayingPieceListener, PlayerListener,
		HouseRegisterListener, Xmlable, Serializable, Hashable {
	/** */
	private static final long serialVersionUID = -2837827570335148672L;

	/** the board, on which stuff would be placed in the real world */
	private Board board;

	/** pseudo-random number generator for the dice */
	private Random random;

	/** whose turn it is currently */
	private int turn;

	/** players of the game */
	private Player[] players;

	/** turn handler for the current turn */
	private TurnHandler turnHandler;

	/** listeners for game events from this game */
	private transient ArrayList<GameListener> gameListeners;

	/** holds information about where houses are */
	private HouseRegister houseRegister;

	/** the unordered card decks */
	private HashMap<String, ShuffledCardDeck> cardDecks;

	/**
	 * creates a new standard monopoly game
	 * 
	 * @param nPieces
	 *            the number of players
	 */
	public Game(int nPieces) {
		this(BoardFactory.createStandardBoard(), nPieces);
	}

	/**
	 * initializes the game with a board and a number of players
	 * 
	 * @param board
	 *            the board
	 * @param nPlayers
	 *            the number of players
	 */
	public Game(final Board board, int nPlayers) {
		this.board = board;
		random = new Random();
		gameListeners = new ArrayList<GameListener>();
		turn = 0;
		houseRegister = new HouseRegister(board);

		cardDecks = new HashMap<String, ShuffledCardDeck>();
		for (String deck : board.getDeckNames()) {
			cardDecks.put(deck, new ShuffledCardDeck(board.getCardStack(deck)));
		}

		this.players = new Player[nPlayers];
		for (int i = 0; i < this.players.length; i++) {
			this.players[i] = new Player("Player " + i);
			this.players[i].setWealth(board.getStartMoney());
		}
		addListeners();

		new Thread() {
			public void run() {
				while (true) {
					Scanner sc = new Scanner(System.in);
					while (sc.hasNextLine()) {
						String cmd = sc.nextLine();
						if (cmd.equalsIgnoreCase("mono")) {
							players[0].addPossession((BuyableField) board
									.getField(1));
							players[0].addPossession((BuyableField) board
									.getField(3));
						}
					}
					sc.close();

					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
	}

	/**
	 * @return the amount of players playing on the board i.e. the number of
	 *         players
	 */
	public int getNPlayers() {
		return players.length;
	}

	/**
	 * gets the {@code index}th player
	 * 
	 * @param index
	 *            the index of the player
	 * @return {@code index}th player
	 */
	public Player getPlayer(int index) {
		return players[index];
	}

	/**
	 * gets the playing piece of the {@code index}th player
	 * 
	 * @param index
	 *            the index of the piece
	 * @return the piece at the specified position
	 */
	public PlayingPiece getPiece(int index) {
		return players[index].getPiece();
	}

	/**
	 * adds listeners to the playing pieces and others, so the game will be
	 * notified about changes of these units
	 */
	private void addListeners() {
		for (int i = 0; i < this.players.length; i++) {
			this.players[i].getPiece().removePlayingPieceListener(this);
			this.players[i].getPiece().addPlayingPieceListener(this);

			this.players[i].removePlayerListener(this);
			this.players[i].addPlayerListener(this);
		}

		houseRegister.removeHouseRegisterListener(this);
		houseRegister.addHouseRegisterListener(this);
	}

	/**
	 * @return the board
	 */
	public Board getBoard() {
		return board;
	}

	/**
	 * @param board
	 *            the board to set
	 */
	public void setBoard(Board board) {
		this.board = board;
	}

	/**
	 * determines if a field is already bought or if it is still available
	 * 
	 * @param field
	 *            the field to determine if it is free
	 * @return <code>true</code> if the field can still be bought
	 */
	public boolean freeToBuy(BuyableField field) {
		for (int i = 0; i < players.length; i++) {
			if (players[i].possesses(field))
				return false;
		}
		return true;
	}

	public TurnHandler getTurnHandler() {
		if (turnHandler == null)
			turnHandler = new TurnHandler(this, turn);
		if (turnHandler.getGame() != this)
			turnHandler.setGame(this);
		return turnHandler;
	}

	/**
	 * finds the owner of a field
	 * 
	 * @param field
	 *            the field to find the owner
	 * @return the owner of the specified field or <code>null</code>, if the
	 *         field is not owned by anybody
	 */
	public Player getOwner(BuyableField field) {
		for (Player p : players) {
			if (p.possesses(field)) {
				return p;
			}
		}
		return null;
	}

	/**
	 * 
	 * @return an array containing the players
	 */
	public Player[] getPlayers() {
		return players;
	}

	/**
	 * @return an array containing the names of the players ordered by their
	 *         turn order
	 */
	public String[] getPlayerNames() {
		String[] names = new String[getNPlayers()];
		for (int i = 0; i < names.length; i++) {
			names[i] = getPlayer(i).getName();
		}
		return names;
	}

	/**
	 * counts the number of railroads a player owns
	 * 
	 * @param owner
	 *            the player whose railroads are counted
	 * @return the number of railroads the player owns
	 */
	public int getNRailroadsOwned(Player owner) {
		return countFields(owner, RailroadField.class);
	}

	/**
	 * counts the number of company fields a player owns
	 * 
	 * @param owner
	 *            the player whose company fields are counted
	 * @return the number of company fields the player owns
	 */
	public int getNCompaniesOwned(Player owner) {
		return countFields(owner, CompanyField.class);
	}

	/**
	 * counts a players possessions of a certain type
	 * 
	 * @param owner
	 *            the player whose possessions are counted
	 * @param type
	 *            the type of field that counts
	 * @return the number of fields of the specific type which belong to the
	 *         specified player
	 */
	private int countFields(Player owner, Class<? extends BuyableField> type) {
		int count = 0;

		int length = board.getAbsoluteLength();
		for (int i = 0; i < length; i++) {
			Field field = board.getField(i);
			if (field.getClass().equals(type)) {
				BuyableField buyableField = (BuyableField) field;
				if (getOwner(buyableField) == owner)
					count++;
			}
		}
		return count;
	}

	/**
	 * moves a playing piece
	 * 
	 * This function only moves a playing piece forward. It does not do any
	 * further action like paying start money etc.
	 * 
	 * @param whichTurn
	 *            the index of the player (whose playing piece is to move)
	 * @param diceValue
	 *            the number of fields to be moved
	 */
	public void movePiece(int whichTurn, int diceValue) {
		PlayingPiece piece = getPiece(whichTurn);
		int position = piece.getPosition();
		position += diceValue;
		position %= getBoard().getAbsoluteLength();
		piece.setPosition(position);
	}

	/**
	 * draws a card from a specified deck
	 * 
	 * @param deckName
	 *            the name of the deck
	 * @return the drawn card
	 */
	public Card drawCard(String deckName) {
		ShuffledCardDeck scd = cardDecks.get(deckName);
		if (scd != null) {
			Card c = scd.draw();
			fireCardDrawn(deckName, c);
			return c;
		} else {
			return null;
		}
	}

	/**
	 * finds out on which field a player is positioned
	 * 
	 * @param player
	 *            the player
	 * @return the field on which <code>player</code> stands
	 */
	public Field getFieldOfPlayer(Player player) {
		return getBoard().getField(player.getPiece().getPosition());
	}

	/**
	 * counts all the houses of a player
	 * 
	 * @param player
	 *            the player to count the houses
	 * @return the number of houses the player owns
	 */
	public int getTotalHouses(Player player) {
		int[] hAndH = getHousesAndHotels(player);
		return hAndH[0];
	}

	/**
	 * counts all the hotels of a player
	 * 
	 * @param player
	 *            the player to count the hotels
	 * @return the number of hotels the player owns
	 */
	public int getTotalHotels(Player player) {
		int[] hAndH = getHousesAndHotels(player);
		return hAndH[1];
	}

	/**
	 * counts the number of houses and hotels a specific player possesses
	 * 
	 * @param player
	 *            the player
	 * @return an array of length 2, which holds the number of houses at the
	 *         first index, the number of hotels at the second
	 */
	public int[] getHousesAndHotels(Player player) {
		Board board = getBoard();
		int houses = 0;
		int hotels = 0;
		for (int i = 0; i < board.getAbsoluteLength(); i++) {
			Field field = board.getField(i);
			if (field instanceof PropertyField) {
				PropertyField propertyField = (PropertyField) field;
				if (player.possesses(propertyField)) {
					int houseCount = houseRegister.getHouseCount(i);
					if (houseCount < board.getMaxHouses()) {
						houses += houseCount;
					} else {
						hotels++;
					}
				}
			}
		}
		return new int[] { houses, hotels };
	}

	/**
	 * counts the houses on a specific field
	 * 
	 * @param field
	 *            the field
	 * @return the number of houses on the field
	 */
	public int getHousesOn(Field field) {
		int fieldIndex = board.getFieldIndex(field);
		return getHousesOn(fieldIndex);
	}

	/**
	 * counts the houses on a specific field
	 * 
	 * @param fieldIndex
	 *            the index of the field
	 * @return the number of houses on the field
	 */
	public int getHousesOn(int fieldIndex) {
		return houseRegister.getHouseCount(fieldIndex);
	}

	/**
	 * set the number of houses on a specific field
	 * 
	 * @param fieldIndex
	 *            the index of the field
	 * @param newHouses
	 *            the new number of houses
	 */
	public void setHousesOn(int fieldIndex, int newHouses) {
		houseRegister.setHouseCount(fieldIndex, newHouses);
	}

	/**
	 * adds a house to the field at a specific index
	 * 
	 * @param fieldIndex
	 *            the index of the field to buy the house
	 * @return <code>true</code>, if the house was added; <code>false</code>, if
	 *         there were already too many houses
	 */
	public boolean addHouse(int fieldIndex) {
		int count = getHousesOn(fieldIndex);
		if (count < board.getMaxHouses()) {
			houseRegister.addHouse(fieldIndex);
			return true;
		} else {
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.winfor.monopoly.game.Xmlable#createXml()
	 */
	public String createXml() {
		return "";
	}

	/**
	 * finds all the monopolies a player owns
	 * 
	 * @param player
	 *            the player to search for
	 * @return an array containing all monopolies which the specified player
	 *         owns every property from
	 */
	public MonopolyGroup[] getMonopolies(Player player) {
		boolean[] ownership = new boolean[board.getNMonopolies()];
		for (int i = 0; i < ownership.length; i++)
			ownership[i] = true;

		int length = board.getAbsoluteLength();
		for (int i = 0; i < length; i++) {
			Field field = board.getField(i);
			if (field instanceof PropertyField) {
				PropertyField propertyField = (PropertyField) field;
				MonopolyGroup group = propertyField.getGroup();
				int monopolyIndex = board.getMonopolyIndex(group);
				if (getOwner(propertyField) != player) {
					ownership[monopolyIndex] = false;
				}
			}
		}

		int ownershipCount = 0;
		for (boolean o : ownership)
			if (o)
				ownershipCount++;

		MonopolyGroup[] owned = new MonopolyGroup[ownershipCount];
		int ownerIndex = 0;
		for (int i = 0; i < ownership.length; i++) {
			if (ownership[i]) {
				owned[ownerIndex++] = board.getMonopoly(i);
			}
		}

		return owned;
	}

	/**
	 * @return the pseudo-random number generator of this game
	 */
	Random getRandom() {
		return random;
	}

	void nextTurn() {
		int oldTurn = turn;

		turn++;
		turn = turn % players.length;
		turnHandler = null;

		firePlayerEndedTurn(oldTurn);
	}

	/**
	 * @return the index of the player whose turn it currently is
	 */
	public int getTurn() {
		return turn;
	}

	@Override
	public void updatedPosition(PlayingPiece piece, int position,
			int oldPosition) {
		int pieceIndex = 0;
		for (int i = getNPlayers() - 1; i >= 0; i--) {
			if (getPiece(i) == piece) {
				pieceIndex = i;
				break;
			}
		}
		firePlayingPieceMoved(pieceIndex, oldPosition);
	}

	@Override
	public void wealthChanged(Player sender, long oldWealth, long newWealth) {
		fireWealthChanged(sender, oldWealth);
	}

	@Override
	public void wentBankrupt(Player sender) {
		firePlayerWentBankrupt(sender);
	}

	@Override
	public void jailStateChanged(Player sender, int oldRounds) {
		firePlayerJailStateChanged(sender, oldRounds);
	}

	@Override
	public void addedPropertyPossession(Player sender, BuyableField property) {
		firePlayerObtained(sender, property);
	}

	@Override
	public void addedCardPossession(Player sender, KeepableCard card) {
		firePlayerKeeps(sender, card);
	}

	@Override
	public void houseNumberChanged(HouseRegister sender, int position,
			int oldNumber) {
		fireHouseNumberChanged(position, oldNumber);
	}

	/**
	 * sends a playing piece moved event to all subscribers
	 * 
	 * @param playerIndex
	 *            the index of the player whose piece was moved
	 * @param oldPosition
	 *            the old position of the piece on the board
	 */
	protected void firePlayingPieceMoved(int playerIndex, int oldPosition) {
		for (GameListener gl : gameListeners) {
			gl.playingPieceMoved(this, playerIndex, oldPosition);
		}
	}

	/**
	 * sends a wealth changed event
	 * 
	 * @param player
	 *            the concerned player
	 * @param wealthBefore
	 *            his wealth before the event
	 */
	protected void fireWealthChanged(Player player, long wealthBefore) {
		for (GameListener gl : gameListeners) {
			gl.playerWealthChanged(this, player, wealthBefore);
		}
	}

	/**
	 * sends a player went bankrupt event
	 * 
	 * @param player
	 *            the insolvent player
	 */
	protected void firePlayerWentBankrupt(Player player) {
		for (GameListener gl : gameListeners) {
			gl.playerWentBankrupt(this, player);
		}
	}

	/**
	 * sends an event that a player's jail state has changed
	 * 
	 * @param player
	 *            the concerned player
	 * @param jailStateBefore
	 *            the old jail state
	 */
	protected void firePlayerJailStateChanged(Player player, int jailStateBefore) {
		for (GameListener gl : gameListeners) {
			gl.playersJailStateChanged(this, player, jailStateBefore);
		}
	}

	/**
	 * sends an event that a player bought something
	 * 
	 * @param player
	 *            the player that bought something
	 * @param field
	 *            the field he bought
	 */
	protected void firePlayerObtained(Player player, BuyableField field) {
		for (GameListener gl : gameListeners) {
			gl.playerObtained(this, player, field);
		}
	}

	/**
	 * sends an event that a player can keep a card
	 * 
	 * @param player
	 *            the player that can keep a card
	 * @param card
	 *            the card he can keep
	 */
	protected void firePlayerKeeps(Player player, KeepableCard card) {
		for (GameListener gl : gameListeners) {
			gl.playerKeepsCard(this, player, card);
		}
	}

	/**
	 * sends an event that some houses were built or demolished
	 * 
	 * @param position
	 *            the position on the board
	 * @param oldNumber
	 *            the number of houses that were there before
	 */
	protected void fireHouseNumberChanged(int position, int oldNumber) {
		for (GameListener gl : gameListeners) {
			gl.houseNumberChanged(this, position, oldNumber);
		}
	}

	/**
	 * sends a player ended turn event to all subscribers
	 * 
	 * @param playerIndex
	 *            the index of the player that finished his turn
	 */
	protected void firePlayerEndedTurn(int playerIndex) {
		for (GameListener gl : gameListeners) {
			gl.playerEndedTurn(this, playerIndex);
		}
	}

	/**
	 * sends a card drawn event to all subscribers
	 * 
	 * @param deckName
	 *            the name of the deck which was drawn from
	 * @param c
	 *            the {@link Card} that was drawn
	 */
	protected void fireCardDrawn(String deckName, Card c) {
		for (GameListener gl : gameListeners) {
			gl.cardDrawn(this, deckName, c);
		}
	}

	/**
	 * adds a new listener to the game
	 * 
	 * @param gl
	 *            the listener
	 */
	public void addGameListener(GameListener gl) {
		if (gameListeners == null)
			gameListeners = new ArrayList<>();
		gameListeners.add(gl);
	}

	/**
	 * removes a listener from the game
	 * 
	 * @param gl
	 *            the listener
	 */
	public void removeGameListener(GameListener gl) {
		if (gameListeners == null)
			gameListeners = new ArrayList<>();
		gameListeners.remove(gl);
	}

	/**
	 * assigns another game value to this game
	 * 
	 * This method copies all data from the other game into this one, with the
	 * only exception being the game listeners
	 * 
	 * @param game
	 *            the game to copy from
	 */
	public void assignGame(Game game) {
		this.board = game.board;
		this.cardDecks = game.cardDecks;
		this.houseRegister = game.houseRegister;
		this.houseRegister.setBoard(getBoard());
		this.players = game.players;
		this.random = game.random;
		this.turn = game.turn;
		this.turnHandler = game.turnHandler;
		this.turnHandler.setGame(this);
		
		if (gameListeners == null)
			gameListeners = new ArrayList<>();
		
		addListeners();
	}

	@Override
	public long createHash() {
		final long prime = 435272656523449L;

		long hash = board.createHash();
		hash += random.hashCode();
		hash *= prime;
		hash += turn;
		hash *= prime;
		for (int i = 0; i < players.length; i++) {
			hash += players[i].createHash();
			hash *= prime;
		}

		if (turnHandler != null)
			hash += turnHandler.createHash();
		hash *= prime;
		hash += houseRegister.createHash();
		for (String deck : board.getDeckNames()) {
			ShuffledCardDeck scd = cardDecks.get(deck);
			if (scd != null)
				hash += scd.createHash() * prime;
		}

		return hash;
	}
}
