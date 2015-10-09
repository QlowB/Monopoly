package ch.winfor.monopoly;

import java.util.Timer;
import java.util.TimerTask;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;

import ch.winfor.monopoly.network.MonopolyConnectionListener;
import ch.winfor.monopoly.network.MonopolyClient;
import ch.winfor.monopoly.network.MonopolyConnection;
import ch.winfor.monopoly.network.NetworkMessage;
import ch.winfor.monopoly.network.RequestMessage;

/**
 * panel handling the display of the server side configuration
 * 
 * It may fire an {@link ActionEvent} to all subscribers with parameter
 * {@code "timeout"} if the connection does not work.
 * 
 * @author Nicolas Winkler
 *
 */
public class ClientGameConfigurePanel extends GameConfigurePanel implements
		MonopolyConnectionListener {
	/** */
	private static final long serialVersionUID = 8342215279370420673L;

	/** the connection */
	private MonopolyClient client;

	/** timeout for game configuration requests */
	private static final long REQUEST_TIMEOUT_MILLISECONDS = 3000L;

	/** the received configuration */
	private MonopolyGameConfiguration receivedConfiguration;

	/**
	 * create the panel
	 */
	public ClientGameConfigurePanel() {
		super();
		chooseBoardBox.setEnabled(false);
		chckbxOnline.setEnabled(false);
		getListPanel().setEnabled(false);

	}

	/**
	 * sets the connection to the server
	 * 
	 * A {@link RequestGameConfiguration} will immediately be sent to the server
	 * and expects the server to answer.
	 * 
	 * @param client
	 *            the new connection
	 */
	public void setClientConnection(MonopolyClient client) {
		this.client = client;
		this.client.addConnectionListener(this);

		NetworkMessage nw = new RequestGameConfiguration();
		this.client.sendMessage(nw);
		Timer t = new Timer();
		t.schedule(new TimerTask() {
			@Override
			public void run() {
				if (receivedConfiguration == null) {
					timedOut(null);
				}
			}
		}, REQUEST_TIMEOUT_MILLISECONDS);
	}

	@Override
	public void messageReceived(MonopolyConnection sender,
			NetworkMessage message) {
		if (message instanceof GameConfigurationMessage) {
			receivedConfiguration = ((GameConfigurationMessage) message)
					.getGameConfiguration();
			applyConfiguration(receivedConfiguration);
		} else if (message instanceof PlayerEntryUpdateMessage) {
			PlayerEntryUpdateMessage peum = ((PlayerEntryUpdateMessage) message);
			if (peum.getPlayerIndex() < centerContent.getNPlayerEntries()) {
				PlayerConfigureEntry pce = centerContent.getPlayerEntry(peum
						.getPlayerIndex());
				pce.applyPlayer(peum.getNewPlayerState());
			} else {
				sender.sendMessage(new RequestGameConfiguration());
			}
		} else if (message instanceof ChosenBoardUpdateMessage) {
			chooseBoardBox.setModel(new DefaultComboBoxModel<String>(
					new String[] { ((ChosenBoardUpdateMessage) message)
							.getNewBoardName() }));
		} else if (message instanceof RemovePlayerUpdateMessage) {
			RemovePlayerUpdateMessage rpum = ((RemovePlayerUpdateMessage) message);
			if (rpum.getPlayerIndex() < centerContent.getNPlayerEntries()) {
				centerContent.removePlayerEntry(centerContent
						.getPlayerEntry(rpum.getPlayerIndex()));
			} else {
				sender.sendMessage(new RequestGameConfiguration());
			}
		} else if (message instanceof PlayerAddedUpdateMessage) {
			PlayerConfigureEntry pce = new PlayerConfigureEntry();
			pce.applyPlayer(((PlayerAddedUpdateMessage) message).getPlayer());
			centerContent.addPlayerEntrySilently(pce);
		} else if (message instanceof StartGameMessage) {
			fireActionEvent("start game");
		}
	}

	@Override
	public void timedOut(MonopolyConnection sender) {
		Language lang = Language.getInstance();
		JOptionPane.showMessageDialog(this, lang.get("err_connection"),
				lang.get("error"), JOptionPane.ERROR_MESSAGE);
		fireActionEvent("timeout");
	}

	/**
	 * requests the configuration of a game
	 * 
	 * @author Nicolas Winkler
	 *
	 */
	public static class RequestGameConfiguration extends RequestMessage {
		/** */
		private static final long serialVersionUID = 8133650150436062347L;

	}

	/**
	 * message transmitting the configuration of a game
	 * 
	 * @author Nicolas Winkler
	 *
	 */
	public static class GameConfigurationMessage extends NetworkMessage {
		/** */
		private static final long serialVersionUID = 2812204412893260197L;

		/** the actual game configuration */
		private MonopolyGameConfiguration gameConfiguration;

		public GameConfigurationMessage(MonopolyGameConfiguration mgc) {
			setGameConfiguration(mgc);
		}

		/**
		 * @return the gameConfiguration
		 */
		public MonopolyGameConfiguration getGameConfiguration() {
			return gameConfiguration;
		}

		/**
		 * @param gameConfiguration
		 *            the gameConfiguration to set
		 */
		public void setGameConfiguration(
				MonopolyGameConfiguration gameConfiguration) {
			this.gameConfiguration = gameConfiguration;
		}
	}

	/**
	 * message transmitting the state of a player
	 * 
	 * @author Nicolas Winkler
	 *
	 */
	public static class PlayerEntryUpdateMessage extends NetworkMessage {
		/** */
		private static final long serialVersionUID = 4124281477320907903L;

		/** index of the player */
		private int playerIndex;

		/** new state of the player */
		private MonopolyGameConfiguration.Player newPlayerState;

		public PlayerEntryUpdateMessage(int playerIndex,
				MonopolyGameConfiguration.Player newPlayerState) {
			this.playerIndex = playerIndex;
			this.newPlayerState = newPlayerState;
		}

		/**
		 * @return the index of the updated entry
		 */
		public int getPlayerIndex() {
			return playerIndex;
		}

		/**
		 * @return the new player state
		 */
		public MonopolyGameConfiguration.Player getNewPlayerState() {
			return newPlayerState;
		}
	}

	/**
	 * indicates that the board choice was altered
	 * 
	 * @author Nicolas Winkler
	 *
	 */
	public static class ChosenBoardUpdateMessage extends NetworkMessage {
		/** */
		private static final long serialVersionUID = -3166760965797766411L;
		private String newBoardName;

		public ChosenBoardUpdateMessage(String newBoardName) {
			this.newBoardName = newBoardName;
		}

		public String getNewBoardName() {
			return newBoardName;
		}
	}

	/**
	 * indicates that a {@link PlayerConfigureEntry} was removed
	 * 
	 * @author Nicolas Winkler
	 *
	 */
	public static class RemovePlayerUpdateMessage extends NetworkMessage {
		/** */
		private static final long serialVersionUID = 952239319930314555L;
		private int playerIndex;

		public RemovePlayerUpdateMessage(int playerIndex) {
			this.playerIndex = playerIndex;
		}

		public int getPlayerIndex() {
			return playerIndex;
		}
	}

	/**
	 * indicates that a {@link PlayerConfigureEntry} was added
	 * 
	 * @author Nicolas Winkler
	 *
	 */
	public static class PlayerAddedUpdateMessage extends NetworkMessage {
		/** */
		private static final long serialVersionUID = -3601719089784964895L;
		private MonopolyGameConfiguration.Player player;

		public PlayerAddedUpdateMessage(MonopolyGameConfiguration.Player player) {
			this.player = player;
		}

		public MonopolyGameConfiguration.Player getPlayer() {
			return player;
		}
	}

	/**
	 * message indicating that the game on the server has been started and that
	 * the client should start his game too, then sync it with the one of the
	 * server
	 * 
	 * @author Nicolas Winkler
	 *
	 */
	public static class StartGameMessage extends NetworkMessage {
		/** */
		private static final long serialVersionUID = -7736948572122254887L;
	}
}
