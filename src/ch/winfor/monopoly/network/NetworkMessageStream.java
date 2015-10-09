package ch.winfor.monopoly.network;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

/**
 * stream wrapper that writes instances of {@link NetworkMessage} to a data
 * stream and also reads them from an incoming stream
 * 
 * @author Nicolas Winkler
 * 
 */
public class NetworkMessageStream {
	/** the data output stream */
	private OutputStream output;

	/** the data input stream */
	private InputStream input;

	/**
	 * initialize
	 */
	public NetworkMessageStream(OutputStream output, InputStream input) {
		this.output = output;
		this.input = input;
	}

	/**
	 * writes a {@link NetworkMessage} to a data output stream
	 * 
	 * @param message
	 *            the message to write
	 * @throws IOException
	 *             in case of an error during the sending
	 */
	public synchronized void writeMessage(NetworkMessage message)
			throws IOException {
		ObjectOutputStream oos = new ObjectOutputStream(output);

		oos.writeObject(message);

		if (MonopolyConnection.NETWORK_LOGS) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos2 = new ObjectOutputStream(baos);
			oos2.writeObject(message);
			System.out.println("Message sent (size = "
					+ baos.toByteArray().length + "): " + message);
		}
	}

	/**
	 * reads a {@link NetworkMessage} from a data input stream
	 * 
	 * @return the newly created message
	 * @throws IOException
	 *             in case of an error during the receiving
	 */
	public NetworkMessage readMessage() throws IOException {
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(input);
		} catch (IOException e1) {
			// connection closed
		}
		Object obj = null;

		try {
			if (ois != null)
				obj = ois.readObject();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		if (obj instanceof NetworkMessage) {
			NetworkMessage networkMessage = (NetworkMessage) obj;
			return networkMessage;
		} else {
			return null;
		}
	}

	/**
	 * @return the data output stream of this instance
	 */
	public OutputStream getOutput() {
		return output;
	}

	/**
	 * @param output
	 *            the new output stream
	 */
	public void setOutput(OutputStream output) {
		this.output = output;
	}

	/**
	 * @return the incoming data stream
	 */
	public InputStream getInput() {
		return input;
	}

	/**
	 * @param input
	 *            the new input stream to set
	 */
	public void setInput(InputStream input) {
		this.input = input;
	}
}
