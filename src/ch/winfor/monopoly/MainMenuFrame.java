package ch.winfor.monopoly;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import ch.winfor.monopoly.game.Game;
import ch.winfor.monopoly.network.MonopolyClient;
import ch.winfor.monopoly.network.MonopolyConnection;
import ch.winfor.monopoly.network.MonopolyConnectionListener;
import ch.winfor.monopoly.network.MonopolyServer;
import ch.winfor.monopoly.network.NetworkMessage;
import ch.winfor.monopoly.network.RequestMessage.RequestFullGame;
import ch.winfor.monopoly.network.UpdateMessage.FullGameUpdate;

/**
 * the main menu frame
 * 
 * @author Nicolas Winkler
 * 
 */
public class MainMenuFrame extends JFrame implements ActionListener {
    /** */
    private static final long serialVersionUID = 2334568048555683030L;

    /** card layout */
    private CardLayout layout;

    /** the main menu panel */
    private MainMenuPanel mainMenuPanel;

    /** the game configuration panel */
    private ConfigureOrJoinPanel gameConfigurePanel;

    /** the preferences panel */
    private PreferencePanel preferencesPanel;

    /** the main panel */
    private JPanel contentPane;

    /** string constant for the {@link CardLayout} */
    private static final String MAIN_MENU = "main menu";

    /** string constant for the {@link CardLayout} */
    private static final String PREFERENCES = "preferences";

    /** string constant for the {@link CardLayout} */
    private static final String START_GAME = "start game";

    /**
     * create the frame
     */
    public MainMenuFrame() {
        // setType(Type.POPUP);
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainMenuPanel = new MainMenuPanel();
        gameConfigurePanel = new ConfigureOrJoinPanel();
        preferencesPanel = new PreferencePanel();

        mainMenuPanel.addActionListener(this);
        gameConfigurePanel.addActionListener(this);
        preferencesPanel.addActionListener(this);

        contentPane = new JPanel();
        layout = new CardLayout();
        contentPane.setLayout(layout);
        contentPane.setBorder(BorderFactory.createLineBorder(Color.ORANGE, 2));

        setContentPane(contentPane);
        add(mainMenuPanel, MAIN_MENU);
        add(preferencesPanel, PREFERENCES);
        add(gameConfigurePanel, START_GAME);

        mainMenuPanel.setDefaultButton();

        pack();
        setLocationRelativeTo(null);
    }

    /**
     * starts a monopoly game in a {@link MonopolyFrame}
     */
    private void startGame() {
        MonopolyGameConfiguration config = gameConfigurePanel
                .getConfiguration();

        if (config.getNPlayers() <= 0) {
            layout.show(contentPane, START_GAME);
            gameConfigurePanel.setDefaultButton();

            Language lang = Language.getInstance();
            JOptionPane.showMessageDialog(this,
                    lang.get("please_more_than_zero_players"),
                    lang.get("error"), JOptionPane.ERROR_MESSAGE);
            return;
        }

        MonopolyFrame mf = new MonopolyFrame(
                gameConfigurePanel.getConfiguration());

        if (config.isOnline()) {
            MonopolyServer ms = gameConfigurePanel.getServer();
            ms.setGame(mf.getGame());

            NetworkMessage nm = new ClientGameConfigurePanel.StartGameMessage();
            ms.broadcast(nm);
        }
        showGameFrame(mf);
    }

    private void startClientGame(final MonopolyClient monopolyClient) {
        monopolyClient.addConnectionListener(new MonopolyConnectionListener() {
            private int wrongMessages = 0;

            @Override
            public void timedOut(MonopolyConnection sender) {
                layout.show(contentPane, START_GAME);
                gameConfigurePanel.setDefaultButton();
            }

            @Override
            public void messageReceived(MonopolyConnection sender,
                    NetworkMessage message) {
                if (message instanceof FullGameUpdate) {
                    FullGameUpdate fgu = (FullGameUpdate) message;
                    Game game = fgu.getGame();
                    System.out.println(game);
                    monopolyClient.setGame(game);
                    MonopolyFrame mf = new MonopolyFrame(game, 2);
                    showGameFrame(mf);
                    /*
                     * new Timer().schedule(new TimerTask() {
                     * 
                     * @Override public void run() {
                     * monopolyClient.removeConnectionListener(getThis()); } },
                     * 1000);
                     */
                } else {
                    wrongMessages++;
                    if (wrongMessages > 5)
                        timedOut(sender);
                }
            }
        });
        monopolyClient.sendMessage(new RequestFullGame());
    }

    private void showGameFrame(MonopolyFrame mf) {
        this.setVisible(false);
        mf.setVisible(true);
        mf.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent arg0) {
            }

            @Override
            public void windowIconified(WindowEvent arg0) {
            }

            @Override
            public void windowDeiconified(WindowEvent arg0) {
            }

            @Override
            public void windowDeactivated(WindowEvent arg0) {
            }

            @Override
            public void windowClosing(WindowEvent arg0) {
                MainMenuFrame.this.setVisible(true);
            }

            @Override
            public void windowClosed(WindowEvent arg0) {
            }

            @Override
            public void windowActivated(WindowEvent arg0) {
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == mainMenuPanel) {
            if (ae.getActionCommand().equals("start game")) {
                layout.show(contentPane, START_GAME);
                gameConfigurePanel.setDefaultButton();
            } else if (ae.getActionCommand().equals("exit")) {
                System.exit(0);
            } else if (ae.getActionCommand().equals("preferences")) {
                layout.show(contentPane, PREFERENCES);
                preferencesPanel.setDefaultButton();
            }
        } else if (ae.getSource() == gameConfigurePanel) {
            if (ae.getActionCommand().equals("back")) {
                layout.show(contentPane, MAIN_MENU);
                mainMenuPanel.setDefaultButton();
            } else if (ae.getActionCommand().equals("start game")) {
                layout.show(contentPane, MAIN_MENU);
                mainMenuPanel.setDefaultButton();
                startGame();
            } else if (ae.getActionCommand().equals("start client")) {
                layout.show(contentPane, MAIN_MENU);
                mainMenuPanel.setDefaultButton();
                startClientGame(gameConfigurePanel.getClientConnection());
            }
        } else if (ae.getSource() == preferencesPanel) {
            if (ae.getActionCommand().equals("ok")
                    || ae.getActionCommand().equals("cancel")) {
                layout.show(contentPane, MAIN_MENU);
                mainMenuPanel.setDefaultButton();
            }
        }
    }
}
