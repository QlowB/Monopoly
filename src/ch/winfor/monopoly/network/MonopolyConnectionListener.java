package ch.winfor.monopoly.network;

/**
 * listener interface for catching events regarding a connection
 * 
 * @author Nicolas Winkler
 *
 */
public interface MonopolyConnectionListener {

    /**
     * invoked when a message has been received
     * 
     * @param sender
     *            the {@link MonopolyConnection} that sent the event
     * @param message
     *            the received message
     */
    void messageReceived(MonopolyConnection sender, NetworkMessage message);

    /**
     * invoked when the connection has stopped working
     * 
     * @param sender
     *            the {@link MonopolyConnection} that has stopped working
     */
    void timedOut(MonopolyConnection sender);
}
