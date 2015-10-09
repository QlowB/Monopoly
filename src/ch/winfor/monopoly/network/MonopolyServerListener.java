package ch.winfor.monopoly.network;

public interface MonopolyServerListener {
	/**
	 * invoked when a new connection to the server was created
	 * 
	 * @param sender
	 *            the server that accepted the connection
	 * @param connection
	 *            the new connection
	 */
	void connectionAccepted(MonopolyServer sender, MonopolyConnection connection);

	/**
	 * invoked whenever a client sends a message to this server
	 * 
	 * @param sender
	 *            the server who was sent something
	 * @param connection
	 *            the connection to the client that sent something
	 * @param message
	 *            the message that was sent
	 */
	void messageReceived(MonopolyServer sender, MonopolyConnection connection,
			NetworkMessage message);
}
