package ch.winfor.monopoly;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;

import ch.winfor.monopoly.game.Board;

/**
 * a game configuration specifying settings for a game
 * 
 * @author Nicolas Winkler
 */
public class MonopolyGameConfiguration implements Serializable {
	/** */
	private static final long serialVersionUID = 7456438094905286237L;

	/**
	 * if it's an online game, the port on which the server runs, otherwise
	 * {@code -1}
	 */
	private int port;

	/** player list */
	private ArrayList<Player> players;

	/** name of the board */
	private String boardName;

	/** the board to play on */
	private Board board;

	/** if the game is hosted by a server */
	private transient boolean clientSide;

	/**
	 * initialize empty configuration
	 */
	public MonopolyGameConfiguration() {
		players = new ArrayList<Player>();
		port = -1;
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
	 * @return the name of the board
	 */
	public String getBoardName() {
		return boardName;
	}

	/**
	 * @param boardName
	 *            the new name of the board to set
	 */
	public void setBoardName(String boardName) {
		this.boardName = boardName;
	}

	/**
	 * @return the number of players in this configuration
	 */
	public int getNPlayers() {
		return players.size();
	}

	/**
	 * gets a player
	 * 
	 * @param i
	 *            the index of the player
	 * @return the player at the specified index
	 */
	public Player getPlayer(int i) {
		return players.get(i);
	}

	/**
	 * add a player to the configuration
	 * 
	 * @param player
	 *            the new player
	 */
	public void addPlayer(Player player) {
		players.add(player);
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @param port
	 *            the new port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * @return if the game is an online game
	 */
	public boolean isOnline() {
		return port != -1;
	}

	/**
	 * @return the clientSide
	 */
	public boolean isClientSide() {
		return clientSide;
	}

	/**
	 * @param clientSide
	 *            the clientSide to set
	 */
	public void setClientSide(boolean clientSide) {
		this.clientSide = clientSide;
	}

	/**
	 * template for a player
	 * 
	 * @author Nicolas Winkler
	 * 
	 */
	public static class Player implements Serializable {
		/** */
		private static final long serialVersionUID = -4993200477084337001L;

		/** name of the player */
		private String name;

		/** color of the playing piece */
		private Color pieceColor;

		/** if it is an human, ai etc. that plays for it */
		private Type type;

		/**
		 * @return the name of this player
		 */
		public String getName() {
			return name;
		}

		/**
		 * @param name
		 *            the new name to set
		 */
		public void setName(String name) {
			this.name = name;
		}

		/**
		 * @return the color of the playing piece
		 */
		public Color getPieceColor() {
			return pieceColor;
		}

		/**
		 * @param color
		 *            the new color of the playing piece to set
		 */
		public void setPieceColor(Color color) {
			this.pieceColor = color;
		}

		/**
		 * @return the type of this player
		 */
		public Type getType() {
			return type;
		}

		/**
		 * @param type
		 *            the new type of this player to set
		 */
		public void setType(Type type) {
			this.type = type;
		}

		/**
		 * type defining the type of a player
		 * 
		 * @author Nicolas Winkler
		 * 
		 */
		public static enum Type {
			/** a player that specifies its input via user interface */
			HUMAN,

			/** player simulated by the program */
			ARTIFICIAL_INTELLIGENCE,

			/** player connected through the network */
			NETWORK
		}
	}
}
