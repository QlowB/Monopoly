package ch.winfor.monopoly;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ch.winfor.monopoly.Language.LanguageListener;
import ch.winfor.monopoly.game.Game;
import ch.winfor.monopoly.network.MonopolyClient;
import ch.winfor.monopoly.network.MonopolyServer;

public class ConfigureOrJoinPanel extends ActionPanel implements
        LanguageListener, ActionListener {
    /** */
    private static final long serialVersionUID = -7223424381616908169L;

    /** start game button */
    private JButton btnStartGame;

    private JButton btnBack;

    private JTabbedPane tabbedPane;
    private GameConfigurePanel createGamePanel;
    private NetworkSetupPanel joinGamePanel;

    private LoadingPanel loadingPanel;

    /** if the player connects to a server, this is the connection */
    private MonopolyClient connectedClient;

    private Connecter connectingThread;

    /** the panel displayed when connected */
    private ClientGameConfigurePanel clientGameConfigurePanel;

    /** creates a server if needed */
    private ServerManager serverManager;

    /**
     * create the panel
     */
    public ConfigureOrJoinPanel() {
        setLayout(new BorderLayout(0, 0));

        JPanel bottomPanel = new JPanel();
        add(bottomPanel, BorderLayout.SOUTH);

        btnStartGame = new JButton("Start Game");
        bottomPanel.add(btnStartGame);
        btnStartGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                fireActionEvent("start game");
            }
        });

        btnBack = new JButton("back");
        bottomPanel.add(btnBack);
        btnBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (tabbedPane.getComponentAt(1) != joinGamePanel) {
                    resetConnected();
                } else
                    fireActionEvent("back");
            }
        });

        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        add(tabbedPane, BorderLayout.CENTER);

        createGamePanel = new GameConfigurePanel();
        tabbedPane.addTab("create game", null, createGamePanel, null);
        serverManager = new ServerManager(createGamePanel);

        loadingPanel = new LoadingPanel("Connecting...");
        loadingPanel.addActionListener(this);

        // centerContentPanel.setViewportView(playerConfigureEntry);
        clientGameConfigurePanel = new ClientGameConfigurePanel();
        clientGameConfigurePanel.addActionListener(this);

        joinGamePanel = new NetworkSetupPanel((Game) null);
        joinGamePanel.addActionListener(this);
        tabbedPane.addTab("join game", null, joinGamePanel, null);

        tabbedPane.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                if (tabbedPane.getSelectedIndex() == 0) {
                    setDefaultButton();
                    btnStartGame.setEnabled(true);
                }
                if (tabbedPane.getSelectedIndex() == 1) {
                    unsetDefaultButton();
                    btnStartGame.setEnabled(false);
                }
            }
        });

        PlayerListPanel list = createGamePanel.getListPanel();
        list.addPlayerEntry();
        list.addPlayerEntry();
        list.addPlayerEntry();

        Language lang = Language.getInstance();
        lang.addLanguageListener(this);
        languageChanged(lang);
    }

    public MonopolyServer getServer() {
        return serverManager.getServer();
    }

    @Override
    public void languageChanged(Language sender) {
        btnStartGame.setText(sender.get("start game"));
        btnBack.setText(sender.get("back"));

        tabbedPane.setTitleAt(0, sender.get("create game"));
        tabbedPane.setTitleAt(1, sender.get("join game"));
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == loadingPanel) {
            connectingThread.pleaseStop();
            connectingThread = null;
        } else if (ae.getSource() == joinGamePanel) {
            if (connectingThread != null && !connectingThread.isAlive())
                connectingThread = null;
            if (connectingThread == null) {
                tabbedPane.setComponentAt(1, loadingPanel);
                connectingThread = new Connecter();
                connectingThread.start();
            }
        } else if (ae.getSource() == clientGameConfigurePanel) {
            if (ae.getActionCommand().equals("timeout")) {
                resetConnected();
            } else if (ae.getActionCommand().equals("start game")) {
                fireActionEvent("start client");
            }
        }
    }

    /**
     * invoked when a connection is created
     * 
     * @param client
     *            the newly connected client
     */
    private void connected(MonopolyClient client) {
        tabbedPane.setComponentAt(1, clientGameConfigurePanel);
        clientGameConfigurePanel.revalidate();
        clientGameConfigurePanel.repaint();
        this.connectedClient = client;
        clientGameConfigurePanel.setClientConnection(client);
    }

    public void resetConnected() {
        if (connectedClient != null) {
            connectedClient.closeSocket();
            connectedClient = null;
        }
        tabbedPane.setComponentAt(1, joinGamePanel);
        joinGamePanel.revalidate();
        joinGamePanel.repaint();
    }

    /**
     * creates a {@link MonopolyGameConfiguration}, sets it to the chosen
     * configuration and returns it
     * 
     * @return a {@link MonopolyGameConfiguration} corresponding to the chosen
     *         preferences
     */
    public MonopolyGameConfiguration getConfiguration() {
        return createGamePanel.getConfiguration(true);
    }

    public MonopolyGameConfiguration getClientConfiguration() {
        return clientGameConfigurePanel.getConfiguration(false);
    }

    /**
     * sets the "start game" button as the default
     */
    public void setDefaultButton() {
        getRootPane().setDefaultButton(btnStartGame);
    }

    public void unsetDefaultButton() {
        getRootPane().setDefaultButton(null);
    }

    public MonopolyClient getClientConnection() {
        return connectedClient;
    }

    private class Connecter extends Thread {
        /** the client which is connected */
        private MonopolyClient client;

        @Override
        public void run() {
            client = new MonopolyClient();
            try {
                client.connect(joinGamePanel.getIp(), joinGamePanel.getPort());
                connected(client);
            } catch (UnknownHostException e) {
                // probably invalid ip
                e.printStackTrace();
                onError();
            } catch (IOException e) {
                // probably #pleaseStop() was invoked
                onError();
            }
        }

        /**
         * invoked on failure
         */
        private void onError() {
            tabbedPane.setComponentAt(1, joinGamePanel);
            joinGamePanel.revalidate();
            joinGamePanel.repaint();
            Language lang = Language.getInstance();
            JOptionPane.showMessageDialog(ConfigureOrJoinPanel.this,
                    lang.get("could_not_connect"), lang.get("error"),
                    JOptionPane.ERROR_MESSAGE);
        }

        /**
         * urges the {@link Thread} to cancel the connection and terminate
         */
        public void pleaseStop() {
            if (client != null) {
                client.closeSocket();
                client = null;
            }
        }
    }
}
