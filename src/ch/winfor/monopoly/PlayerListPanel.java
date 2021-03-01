package ch.winfor.monopoly;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import ch.winfor.monopoly.Language.LanguageListener;

/**
 * component which can list several instances of {@link PlayerConfigureEntry}
 * 
 * @author Nicolas Winkler
 *
 */
public class PlayerListPanel extends JScrollPane implements ActionListener,
        LanguageListener, Freeable {
    /** */
    private static final long serialVersionUID = 4715296390024504794L;

    /** filling content panel */
    private JPanel content;

    /** add player button */
    private JButton btnAddPlayer;

    private ArrayList<PlayerChangeListener> listeners;

    public PlayerListPanel() {
        content = new JPanel();
        super.getViewport().add(content);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        btnAddPlayer = new JButton("add player");
        btnAddPlayer.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnAddPlayer.addActionListener(this);
        content.add(btnAddPlayer);

        listeners = new ArrayList<PlayerChangeListener>();

        Language lang = Language.getInstance();
        lang.addLanguageListener(this);
        languageChanged(lang);
    }

    @Override
    public void languageChanged(Language sender) {
        btnAddPlayer.setText(sender.get("add player"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnAddPlayer) {
            addPlayerEntry();
        }
        if (e.getSource() instanceof PlayerConfigureEntry) {
            if (e.getActionCommand().equals("remove")) {
                removePlayerEntry((PlayerConfigureEntry) e.getSource());
            } else {
                firePlayerChanged(
                        getIndex((PlayerConfigureEntry) e.getSource()),
                        e.getActionCommand());
            }
        }
    }

    /**
     * adds a subscriber
     * 
     * @param pcl
     *            the new subscriber
     */
    public void addPlayerChangeListener(PlayerChangeListener pcl) {
        listeners.add(pcl);
    }

    /**
     * removes a subscriber
     * 
     * @param pcl
     *            the old subscriber
     */
    public void removePlayerChangeListener(PlayerChangeListener pcl) {
        listeners.remove(pcl);
    }

    protected void firePlayerChanged(int index, String changeCommand) {
        for (PlayerChangeListener pcl : listeners) {
            pcl.playerChanged(this, index, changeCommand);
        }
    }

    protected void firePlayerAdded() {
        for (PlayerChangeListener pcl : listeners) {
            pcl.playerAdded(this);
        }
    }

    protected void firePlayerRemoved(int index) {
        for (PlayerChangeListener pcl : listeners) {
            pcl.playerRemoved(this, index);
        }
    }

    private int getIndex(PlayerConfigureEntry source) {
        for (int i = 0; i < getNPlayerEntries(); i++) {
            if (source == getPlayerEntry(i))
                return i;
        }
        return 0;
    }

    @Override
    public void setEnabled(boolean b) {
        for (int i = 0; i < getNPlayerEntries(); i++) {
            getPlayerEntry(i).setEnabled(b);
        }
        btnAddPlayer.setEnabled(b);
        super.setEnabled(b);
    }

    /**
     * adds one player entry to the list
     * 
     * @return the added {@link PlayerListPanel}
     */
    public PlayerConfigureEntry addPlayerEntry() {
        PlayerConfigureEntry playerConfigureEntry = new PlayerConfigureEntry();

        int nComponents = content.getComponentCount();

        Language lang = Language.getInstance();
        String player = lang.get("player");
        playerConfigureEntry.getPlayerNameField().setText(
                player + " " + nComponents);

        playerConfigureEntry.setEnabled(this.isEnabled());

        content.add(playerConfigureEntry, nComponents - 1);
        content.revalidate();
        content.repaint();
        playerConfigureEntry.addActionListener(this);
        firePlayerAdded();
        return playerConfigureEntry;
    }

    public void addPlayerEntrySilently(PlayerConfigureEntry pce) {
        pce.setEnabled(this.isEnabled());
        int nComponents = content.getComponentCount();
        content.add(pce, nComponents - 1);
        pce.addActionListener(this);
        firePlayerAdded();
    }

    public void removePlayerEntry(PlayerConfigureEntry pce) {
        int index = getIndex(pce);
        content.remove(pce);
        pce.free();
        firePlayerRemoved(index);
        content.revalidate();
        content.repaint();
    }

    public void removeAllPlayerEntries() {
        int oldLength = getNPlayerEntries();
        content.removeAll();
        content.add(btnAddPlayer);
        for (int i = 0; i < oldLength; i++) {
            firePlayerRemoved(i);
        }
        content.revalidate();
        content.repaint();
    }

    /**
     * @return number of player entries
     */
    public int getNPlayerEntries() {
        return content.getComponentCount() - 1;
    }

    /**
     * gets one specific player entry
     * 
     * @param index
     *            index of the player entry
     * @return the player entry
     */
    public PlayerConfigureEntry getPlayerEntry(int index) {
        try {
            return (PlayerConfigureEntry) content.getComponent(index);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void free() {
        for (int i = 0; i < getNPlayerEntries(); i++) {
            PlayerConfigureEntry pce = getPlayerEntry(i);
            pce.free();
        }
        removeAllPlayerEntries();
    }

    /**
     * interface to catch events concerning the change of a player entry
     * 
     * @author Nicolas Winkler
     *
     */
    public interface PlayerChangeListener {
        /**
         * invoked when a player entry has changed
         * 
         * @param sender
         *            the {@link PlayerListPanel} that sent the event
         * @param i
         *            the index of the player entry
         * @param changeCommand
         *            what has changed
         */
        void playerChanged(PlayerListPanel sender, int i, String changeCommand);

        /**
         * invoke when a player is added
         * 
         * @param sender
         *            the {@link PlayerListPanel} that sent the event
         */
        void playerAdded(PlayerListPanel sender);

        /**
         * invoked when a {@link PlayerConfigureEntry} is removed
         * 
         * @param sender
         *            the {@link PlayerListPanel} that sent the event
         * @param playerIndex
         *            index of the removed panel
         */
        void playerRemoved(PlayerListPanel sender, int playerIndex);
    }
}
