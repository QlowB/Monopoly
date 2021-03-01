package ch.winfor.monopoly.network;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

import ch.winfor.monopoly.game.Game;
import ch.winfor.monopoly.game.GameListener;

/**
 * a connection (server or client) to other players which play at the same game
 * as the user of this instance of the program
 * 
 * @author Nicolas Winkler
 * 
 */
public abstract class MonopolyConnection implements GameListener {
    /** the socket connection */
    protected Socket connection;

    /** the game belonging to the connection */
    protected Game game;

    /** the stream that manages network messages */
    protected NetworkMessageStream networkMessageStream;

    /** listening thread */
    private ConnectionListenerThread connectionListener;

    private ArrayList<MonopolyConnectionListener> listeners;

    /** defines if network logs are enabled (only for debug purposes) */
    public static boolean NETWORK_LOGS = true;

    /**
     * initialize the connection
     * 
     * @param connection
     *            the socket connection
     * @param game
     */
    public MonopolyConnection(Socket connection, Game game) {
        this.game = game;
        this.connection = connection;
        listeners = new ArrayList<MonopolyConnectionListener>();
        try {
            networkMessageStream = new NetworkMessageStream(
                    connection.getOutputStream(), connection.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        addListener();
        startListening();
    }

    public MonopolyConnection() {
        this.connection = new Socket();
        listeners = new ArrayList<MonopolyConnectionListener>();
    }

    /**
     * builds up a connection and starts listening
     * 
     * @param host
     *            the host to connect to
     * @param port
     *            the port to connect to
     * @throws IOException
     *             if an error occurs during the connection
     */
    protected void connect(InetAddress host, int port) throws IOException {
        InetSocketAddress isa = new InetSocketAddress(host, port);
        connection.connect(isa);
        try {
            if (networkMessageStream == null)
                networkMessageStream = new NetworkMessageStream(
                        connection.getOutputStream(),
                        connection.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        startListening();
    }

    /**
     * starts the listening thread
     */
    protected void startListening() {
        if (connectionListener == null) {
            connectionListener = new ConnectionListenerThread();
            connectionListener.start();
        }
    }

    /**
     * @return the game
     */
    public Game getGame() {
        return game;
    }

    /**
     * @param game
     *            the game to set
     */
    public void setGame(Game game) {
        if (game != null)
            game.removeGameListener(this);
        this.game = game;
        game.addGameListener(this);
    }

    /**
     * sends a message to the connected client
     * 
     * @param message
     *            the message to send
     * @return <code>true</code>, if the message has been successfully sent,
     *         <code>false</code> otherwise
     */
    public boolean sendMessage(NetworkMessage message) {
        if (networkMessageStream != null) {
            try {
                networkMessageStream.writeMessage(message);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                fireTimedOut();
            }
        }
        return false;
    }

    /**
     * waits for an incoming message (blocking call)
     * 
     * @return the message or <code>null</code> if the receiving failed
     * @throws IOException
     *             if there was an error while waiting for the message or
     *             receiving it
     */
    private synchronized NetworkMessage waitForMessage() throws IOException {
        if (networkMessageStream != null) {
            return networkMessageStream.readMessage();
        } else {
            return null;
        }
    }

    /**
     * forces the connection to close the socket resulting in cancellation of
     * all running operations e.g. connecting
     */
    public void closeSocket() {
        if (connection != null) {
            try {
                connection.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * adds a subscriber
     * 
     * @param cl
     *            the new subscriber
     */
    public void addConnectionListener(MonopolyConnectionListener cl) {
        listeners.add(cl);
    }

    /**
     * removes a subscriber
     * 
     * @param cl
     *            the old subscriber
     */
    public void removeConnectionListener(MonopolyConnectionListener cl) {
        listeners.remove(cl);
    }

    /**
     * notofies the subscribers that a message was received
     * 
     * @param message
     *            the received message
     */
    protected void fireMessageReceived(NetworkMessage message) {
        for (int i = 0; i < listeners.size(); i++) {
            MonopolyConnectionListener cl = listeners.get(i);
            cl.messageReceived(this, message);
        }
    }

    /**
     * notofies the subscribers that the connection doesn't work anymore
     * 
     * @param message
     *            the received message
     */
    public void fireTimedOut() {
        for (MonopolyConnectionListener cl : listeners) {
            cl.timedOut(this);
        }
    }

    /**
     * invoked when a message has been received
     * 
     * @param message
     *            the received message
     */
    public abstract void messageReceived(NetworkMessage message);

    /**
     * adds {@code this} as a {@link GameListener} to the game
     */
    protected void addListener() {
        if (game != null)
            game.addGameListener(this);
    }

    /**
     * removes {@code this} as a {@link GameListener} from the game
     */
    protected void removeListener() {
        if (game != null)
            game.removeGameListener(this);
    }

    /**
     * 
     * @return <code>true</code> if this connection can be used,
     *         <code>false</code> if not
     */
    public boolean isValid() {
        return connection != null && connection.isConnected()
                && !connection.isClosed();
    }

    private class ConnectionListenerThread extends Thread {
        /** if the thread should continue running */
        private boolean shouldRun;

        /**
		 * 
		 */
        public ConnectionListenerThread() {
            shouldRun = true;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Thread#run()
         */
        public void run() {
            NetworkMessage message = null;
            do {
                try {
                    message = waitForMessage();
                    if (message != null) {
                        messageReceived(message);
                        fireMessageReceived(message);
                    }
                } catch (IOException e) {
                    fireTimedOut();
                    shouldRun = false;
                } catch (Throwable t) { // we don't want to crash the whole
                                        // listening procedure because of errors
                                        // that happened while receiving a
                                        // message or processing the received
                                        // message
                    t.printStackTrace();
                }
            } while (shouldRun && message != null);
            connectionListener = null;
        }
    }
}
