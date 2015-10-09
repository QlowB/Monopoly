package ch.winfor.monopoly.network;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

/**
 * a basic server class
 * 
 * A {@link BasicServer} can be set up to automatically listen to new
 * connections. The accepted connections are stored an can be accessed.
 * 
 * @author Nicolas Winkler
 * 
 */
public class BasicServer {

	/** the port on which this server is running */
	private int port;

	/** server socket used to accept new connections */
	private ServerSocket acceptSocket;

	/** thread waiting for incoming connections */
	private Listener connectionListener;

	/** list of accepted connections */
	private List<Socket> acceptedConnections;

	/** list of subscribers that subscribed to this server */
	private List<ServerListener> serverListeners;

	/**
	 * initializes the server and makes it ready to listen
	 * 
	 * @param port
	 *            the port on which the server should run
	 * @throws BindException
	 *             if the port is already in use
	 */
	public BasicServer(int port) {
		this.port = port;
		acceptedConnections = new ArrayList<Socket>();
		serverListeners = new ArrayList<ServerListener>();
		setup();
	}

	/**
	 * sets the server socket
	 */
	private void setup() {
		try {
			acceptSocket = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return the port on which the server listens for connections
	 */
	public int getPort() {
		return port;
	}

	/**
	 * tests if all the connections are still in use and removes sockets that
	 * are either closed or not connected anymore
	 */
	public void testConnections() {
		synchronized (acceptedConnections) {
			for (int i = 0; i < acceptedConnections.size(); i++) {
				Socket s = acceptedConnections.get(i);
				if (!s.isConnected() || s.isClosed()) {
					acceptedConnections.remove(i);
					i--;
				}
			}
		}
	}

	/**
	 * @return the number of sockets connected to this server
	 */
	public int getNConnections() {
		return acceptedConnections.size();
	}

	/**
	 * gets a socket of a connection
	 * 
	 * @param i
	 *            the index of the desired connection socket
	 * @return the desired socket
	 */
	public Socket getConnection(int i) {
		Socket ret = null;
		synchronized (acceptedConnections) {
			ret = acceptedConnections.get(i);
		}
		return ret;
	}

	/**
	 * starts listening to connections and accepting anything incoming
	 */
	public void startListening() {
		if (acceptSocket != null) {
			connectionListener = new Listener();
			connectionListener.start();
			if (MonopolyClient.NETWORK_LOGS)
				System.out.println("started server on port " + getPort());
		}
	}

	/**
	 * stops the listening process
	 */
	public void stopListening() {
		if (connectionListener != null)
			connectionListener.pleaseStop();
		connectionListener = null;
		setup();
	}

	/**
	 * called when an incoming connection has been accepted
	 * 
	 * @param s
	 *            the socket for the connection
	 */
	private void acceptedSocket(Socket s) {
		synchronized (acceptedConnections) {
			acceptedConnections.add(s);
		}
		fireConnectionAccepted(s);
	}

	public void close() throws IOException {
		if (acceptSocket != null) {
			acceptSocket.close();
		}
		for (Socket s : acceptedConnections) {
			s.close();
		}
		acceptedConnections.clear();
	}

	/**
	 * thread that waits for incoming connections
	 * 
	 * @author Nicolas Winkler
	 * 
	 */
	private class Listener extends Thread {
		/** determines if the thread should continue running */
		private volatile boolean shouldRun;

		/**
		 * initializes the listener and perpares it ready to start
		 */
		public Listener() {
			shouldRun = true;
		}

		/**
		 * urges the listener to stop
		 * 
		 * After invoking this method, the listener should stop immediately and
		 */
		public void pleaseStop() {
			shouldRun = false;
			try {
				if (acceptSocket != null)
					acceptSocket.close();
				acceptSocket = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		/**
		 * runs a loop accepting connections
		 */
		public void run() {
			while (shouldRun && acceptSocket != null) {
				try {
					accept();
				} catch (SocketException e) {
					// probably exit thread
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		/**
		 * wait for clients to connect (blocking call)
		 * 
		 * @throws IOException
		 *             if an {@link IOException} occurred during the listening
		 * @throws SocketException
		 *             if the listening was stopped
		 */
		private void accept() throws IOException, SocketException {
			Socket s = acceptSocket.accept();
			acceptedSocket(s);
		}
	}

	/**
	 * subscribes an event listener to this server
	 * 
	 * @param serverListener
	 *            the new server listener
	 */
	public void addServerListener(ServerListener serverListener) {
		serverListeners.add(serverListener);
	}

	/**
	 * removes an event listener from this server
	 * 
	 * @param serverListener
	 *            the listener to remove
	 */
	public void removeServerListener(ServerListener serverListener) {
		serverListeners.remove(serverListener);
	}

	/**
	 * sends a message to all subscribers
	 * 
	 * @param s
	 *            the new connection socket
	 */
	protected void fireConnectionAccepted(Socket s) {
		for (ServerListener sl : serverListeners) {
			sl.connectionAccepted(this, s);
		}
	}

	/**
	 * interface for event handlers that catch server events
	 * 
	 * @author Nicolas Winkler
	 * 
	 */
	public static interface ServerListener {

		/**
		 * event fired when an incoming connection has been accepted
		 * 
		 * @param sender
		 *            the server that accepted the connection
		 * @param connection
		 *            socket to the new connection
		 */
		void connectionAccepted(BasicServer sender, Socket connection);
	}
}
