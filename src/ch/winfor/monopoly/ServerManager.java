package ch.winfor.monopoly;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import ch.winfor.monopoly.ClientGameConfigurePanel.RequestGameConfiguration;
import ch.winfor.monopoly.PlayerListPanel.PlayerChangeListener;
import ch.winfor.monopoly.network.MonopolyConnection;
import ch.winfor.monopoly.network.MonopolyServer;
import ch.winfor.monopoly.network.MonopolyServerListener;
import ch.winfor.monopoly.network.NetworkMessage;

/**
 * class handling the port changes of a {@link GameConfigurePanel}
 * 
 * @author Nicolas Winkler
 *
 */
public class ServerManager implements ActionListener, MonopolyServerListener,
		PlayerChangeListener {
	/** the panel */
	private GameConfigurePanel gameConfigurePanel;

	/** the server */
	private MonopolyServer server;

	public ServerManager(GameConfigurePanel gameConfigurePanel) {
		this.gameConfigurePanel = gameConfigurePanel;
		gameConfigurePanel.addActionListener(this);
		gameConfigurePanel.getListPanel().addPlayerChangeListener(this);
	}

	/**
	 * @return the server
	 */
	public MonopolyServer getServer() {
		return server;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == gameConfigurePanel) {
			if (e.getActionCommand().equals("port updated")) {
				if (server != null) {
					int serverPort = server.getPort();
					int textPort = gameConfigurePanel.getPort();
					if (textPort != serverPort) {
						server.close();
						newServer(textPort);
					}
				} else {
					int textPort = gameConfigurePanel.getPort();
					newServer(textPort);
				}
			} else if (e.getActionCommand().equals("board updated")) {
				if (server != null) {
					NetworkMessage nw = new ClientGameConfigurePanel.ChosenBoardUpdateMessage(
							gameConfigurePanel.getBoardName());
					server.broadcast(nw);
				}
			}
		}
	}

	/**
	 * starts a new server on a given port
	 * 
	 * @param textPort
	 *            the port
	 */
	private void newServer(int textPort) {
		server = new MonopolyServer(textPort);
		server.addServerListener(this);
	}

	@Override
	public void connectionAccepted(MonopolyServer sender,
			MonopolyConnection connection) {
	}

	@Override
	public void messageReceived(MonopolyServer sender,
			MonopolyConnection connection, NetworkMessage message) {
		if (message instanceof RequestGameConfiguration) {
			MonopolyGameConfiguration mgc = gameConfigurePanel
					.getConfiguration(false);
			NetworkMessage nm = new ClientGameConfigurePanel.GameConfigurationMessage(
					mgc);
			connection.sendMessage(nm);
		}
	}

	@Override
	public void playerChanged(PlayerListPanel sender, int i,
			String changeCommand) {
		PlayerConfigureEntry pce = gameConfigurePanel.getListPanel()
				.getPlayerEntry(i);
		if (pce != null && server != null) {
			NetworkMessage nw = new ClientGameConfigurePanel.PlayerEntryUpdateMessage(
					i, pce.getConfigurationPlayer());
			server.broadcast(nw);
		}
	}

	@Override
	public void playerAdded(PlayerListPanel sender) {
		if (server != null) {
			PlayerConfigureEntry pce = sender.getPlayerEntry(sender
					.getNPlayerEntries() - 1);
			NetworkMessage nw = new ClientGameConfigurePanel.PlayerAddedUpdateMessage(
					pce.getConfigurationPlayer());
			server.broadcast(nw);
		}
	}

	@Override
	public void playerRemoved(PlayerListPanel sender, int playerIndex) {
		if (server != null) {
			NetworkMessage nw = new ClientGameConfigurePanel.RemovePlayerUpdateMessage(
					playerIndex);
			server.broadcast(nw);
		}
	}
}
