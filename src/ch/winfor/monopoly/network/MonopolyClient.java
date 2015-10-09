package ch.winfor.monopoly.network;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import ch.winfor.monopoly.game.BuyableField;
import ch.winfor.monopoly.game.Card;
import ch.winfor.monopoly.game.Card.KeepableCard;
import ch.winfor.monopoly.game.Game;
import ch.winfor.monopoly.game.Player;
import ch.winfor.monopoly.network.RequestMessage.RequestFullGame;
import ch.winfor.monopoly.network.UpdateMessage.InvalidUpdateException;

/**
 * client part of a connection to a server running a monopoly game
 * 
 * @author Nicolas Winkler
 * 
 */
public class MonopolyClient extends MonopolyConnection {

	public MonopolyClient(InetAddress address, int port, Game game)
			throws IOException {
		super(new Socket(address, port), game);
	}

	public MonopolyClient(InetAddress address, int port) throws IOException {
		this(address, port, null);
	}

	public MonopolyClient(String address, int port, Game game)
			throws IOException {
		this(InetAddress.getByName(address), port, game);
	}

	public MonopolyClient(String address, int port) throws IOException {
		this(InetAddress.getByName(address), port, null);
	}

	public MonopolyClient() {
		super();
	}

	/**
	 * connects the socket and starts listening
	 * 
	 * @param address
	 *            the host name or ip
	 * @param port
	 *            the port
	 * @throws IOException
	 *             if there's an error during the connection
	 * @throws UnknownHostException
	 *             if the host is not found
	 */
	public void connect(String address, int port) throws UnknownHostException,
			IOException {
		super.connect(InetAddress.getByName(address), port);
	}

	/**
	 * asks the server to send the full game state
	 */
	public void requestFullGame() {
		RequestFullGame rfg = new RequestFullGame();
		sendMessage(rfg);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.winfor.monopoly.network.MonopolyConnection#messageReceived(ch.winfor
	 * .monopoly.network.NetworkMessage)
	 */
	@Override
	public void messageReceived(NetworkMessage message) {
		if (NETWORK_LOGS)
			System.out.println("Message received: " + message);

		if (message instanceof UpdateMessage) {
			UpdateMessage updateMessage = (UpdateMessage) message;

			removeListener(); // suspend listening to the game
			try {
				if (game != null)
					updateMessage.updateGame(game);
			} catch (InvalidUpdateException iue) {
				iue.printStackTrace();
			}
			addListener();

			if (game != null)
				if (!updateMessage.checkHash(game)) // hashes are not the same
					requestFullGame(); // error occurred, request the whole game
		}
	}

	@Override
	public void playerWealthChanged(Game sender, Player player,
			long wealthBefore) {
		// TODO Auto-generated method stub

	}

	@Override
	public void playerWentBankrupt(Game sender, Player player) {
		// TODO Auto-generated method stub

	}

	@Override
	public void playersJailStateChanged(Game sender, Player player,
			int jailStateBefore) {
		// TODO Auto-generated method stub

	}

	@Override
	public void playerObtained(Game sender, Player player, BuyableField field) {
		// TODO Auto-generated method stub

	}

	@Override
	public void playerKeepsCard(Game sender, Player player, KeepableCard card) {
		// TODO Auto-generated method stub

	}

	@Override
	public void houseNumberChanged(Game game, int position, int oldNumber) {
		// TODO Auto-generated method stub

	}

	@Override
	public void playingPieceMoved(Game sender, int pieceIndex, int oldPosition) {
		// TODO Auto-generated method stub

	}

	@Override
	public void playerEndedTurn(Game sender, int playerIndex) {
		// TODO Auto-generated method stub

	}

	@Override
	public void cardDrawn(Game sender, String deckName, Card c) {
	}
}
