package ch.winfor.monopoly.network;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import ch.winfor.monopoly.game.BuyableField;
import ch.winfor.monopoly.game.Card;
import ch.winfor.monopoly.game.Card.KeepableCard;
import ch.winfor.monopoly.game.Game;
import ch.winfor.monopoly.game.Player;
import ch.winfor.monopoly.network.BasicServer.ServerListener;
import ch.winfor.monopoly.network.RequestMessage.RequestFullGame;
import ch.winfor.monopoly.network.UpdateMessage.CardDrawnUpdate;
import ch.winfor.monopoly.network.UpdateMessage.FullGameUpdate;
import ch.winfor.monopoly.network.UpdateMessage.HousesNumberChangedUpdate;
import ch.winfor.monopoly.network.UpdateMessage.InvalidUpdateException;
import ch.winfor.monopoly.network.UpdateMessage.PlayerKeepsCardUpdate;
import ch.winfor.monopoly.network.UpdateMessage.PlayerObtainedUpdate;
import ch.winfor.monopoly.network.UpdateMessage.PlayerWealthChangedUpdate;
import ch.winfor.monopoly.network.UpdateMessage.PlayersJailStateChangedUpdate;

/**
 * the server part of a connection to several clients connected to the server
 * running in this program
 * 
 * @author Nicolas Winkler
 * 
 */
public class MonopolyServer implements MonopolyConnectionListener {
    /** the listening server */
    private BasicServer server;

    /** list of our clients */
    private List<ClientConnection> clients;

    /** list of our clients */
    private List<MonopolyServerListener> listeners;

    /** the server game */
    private Game game;

    public MonopolyServer(int port, Game game) {
        clients = new ArrayList<ClientConnection>();
        listeners = new ArrayList<MonopolyServerListener>();
        setGame(game);
        server = new BasicServer(port);
        server.startListening();
        server.addServerListener(new ServerListener() {
            @Override
            public void connectionAccepted(BasicServer sender, Socket connection) {
                ClientConnection cc = new ClientConnection(connection,
                        MonopolyServer.this.game);
                clients.add(cc);
                cc.addConnectionListener(MonopolyServer.this);
                fireConnectionAccepted(cc);
                if (MonopolyConnection.NETWORK_LOGS)
                    System.out.println("Connection accepted!");
            }
        });
    }

    public MonopolyServer(int port) {
        this(port, null);
    }

    /**
     * @return the game
     */
    public Game getGame() {
        return game;
    }

    /**
     * @param game
     *            the new game to set
     */
    public void setGame(Game game) {
        this.game = game;
        for (ClientConnection cc : clients) {
            cc.setGame(game);
        }
    }

    /**
     * class managing a connection to a client
     * 
     * @author Nicolas Winkler
     * 
     */
    public class ClientConnection extends MonopolyConnection {

        /**
         * wraps the client manager around the connection
         * 
         * @param connection
         *            the new connection
         */
        public ClientConnection(Socket connection, Game game) {
            super(connection, game);
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * ch.winfor.monopoly.network.MonopolyConnection#messageReceived(ch.
         * winfor.monopoly.network.NetworkMessage)
         */
        public void messageReceived(NetworkMessage message) {
            if (NETWORK_LOGS)
                System.out.println("Message received: " + message);

            if (message instanceof RequestFullGame) {
                sendFullGame();
            }
            if (message instanceof UpdateMessage) {
                UpdateMessage updateMessage = (UpdateMessage) message;

                removeListener(); // suspend listening to game
                try {
                    updateMessage.updateGame(game);
                } catch (InvalidUpdateException iue) {

                }
                addListener();

                if (!updateMessage.checkHash(game)) {
                    sendFullGame();
                }
            }
        }

        private void sendFullGame() {
            FullGameUpdate rfg = new FullGameUpdate(MonopolyServer.this.game);
            rfg.setHash(MonopolyServer.this.game.createHash());
            sendMessage(rfg);
        }

        private void sendUpdateMessage(UpdateMessage um) {
            um.setHash(game.createHash());
            sendMessage(um);
        }

        @Override
        public void playingPieceMoved(Game sender, int pieceIndex,
                int oldPosition) {
            int newPosition = sender.getPiece(pieceIndex).getPosition();
            UpdateMessage um = new UpdateMessage.PlayerMovedUpdate(pieceIndex,
                    newPosition);
            sendUpdateMessage(um);
        }

        @Override
        public void playerEndedTurn(Game sender, int playerIndex) {
            UpdateMessage um = new UpdateMessage.PlayerEndedTurn();
            sendUpdateMessage(um);
        }

        @Override
        public void playerWealthChanged(Game sender, Player player,
                long wealthBefore) {
            PlayerWealthChangedUpdate pwcu = new PlayerWealthChangedUpdate(
                    player.getWealth());
            sendUpdateMessage(pwcu);
        }

        @Override
        public void playerWentBankrupt(Game sender, Player player) {
        }

        @Override
        public void playersJailStateChanged(Game sender, Player player,
                int jailStateBefore) {
            PlayersJailStateChangedUpdate pjscu = new PlayersJailStateChangedUpdate(
                    player.getInJailRounds());
            sendUpdateMessage(pjscu);
        }

        @Override
        public void playerObtained(Game sender, Player player,
                BuyableField field) {
            PlayerObtainedUpdate pou = new PlayerObtainedUpdate();
            sendUpdateMessage(pou);
        }

        @Override
        public void playerKeepsCard(Game sender, Player player,
                KeepableCard card) {
            PlayerKeepsCardUpdate pkcu = new PlayerKeepsCardUpdate();
            sendUpdateMessage(pkcu);
        }

        @Override
        public void houseNumberChanged(Game game, int position, int oldNumber) {
            HousesNumberChangedUpdate hncu = new HousesNumberChangedUpdate();
            sendUpdateMessage(hncu);
        }

        @Override
        public void cardDrawn(Game sender, String deckName, Card c) {
            CardDrawnUpdate cdu = new CardDrawnUpdate();
            sendUpdateMessage(cdu);
        }
    }

    public void close() {
        try {
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return the port on which this server listens
     */
    public int getPort() {
        return server.getPort();
    }

    /**
     * adds a subscriber to the seprotected void
     * fireMessageReceived(MonopolyConnection sender, NetworkMessage message) {
     * for (MonopolyServerListener msl : listeners) { msl.messageReceived(this,
     * sender, message); } }rver
     * 
     * @param msl
     *            the new subscriber
     */
    public void addServerListener(MonopolyServerListener msl) {
        listeners.add(msl);
    }

    /**
     * removes a subscriber from the server
     * 
     * @param msl
     *            the old subscriber
     */
    public void removeServerListener(MonopolyServerListener msl) {
        listeners.remove(msl);
    }

    @Override
    public void messageReceived(MonopolyConnection sender,
            NetworkMessage message) {
        fireMessageReceived(sender, message);
    }

    protected void fireMessageReceived(MonopolyConnection sender,
            NetworkMessage message) {
        for (MonopolyServerListener msl : listeners) {
            msl.messageReceived(this, sender, message);
        }
    }

    protected void fireConnectionAccepted(MonopolyConnection sender) {
        for (MonopolyServerListener msl : listeners) {
            msl.connectionAccepted(this, sender);
        }
    }

    @Override
    public void timedOut(MonopolyConnection sender) {
        removeConnection(sender);
    }

    /**
     * removes a connection from the socket
     * 
     * @param connection
     *            the connection to remove
     */
    public void removeConnection(MonopolyConnection connection) {
        connection.closeSocket();
        clients.remove(connection);
    }

    /**
     * sends a message to all connections
     * 
     * @param nw
     *            the message
     */
    public void broadcast(final NetworkMessage nw) {
        for (int i = 0; i < clients.size(); i++) {
            final int index = i;
            new Thread() {
                public void run() {
                    clients.get(index).sendMessage(nw);
                }
            }.start();
        }
    }
}
