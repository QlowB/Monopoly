package ch.winfor.monopoly.gui;

import java.awt.Component;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ch.winfor.monopoly.Freeable;
import ch.winfor.monopoly.Language;
import ch.winfor.monopoly.Language.LanguageListener;
import ch.winfor.monopoly.game.Board;
import ch.winfor.monopoly.game.BuyableField;
import ch.winfor.monopoly.game.Card.KeepableCard;
import ch.winfor.monopoly.game.Field;
import ch.winfor.monopoly.game.Game;
import ch.winfor.monopoly.game.Player;
import ch.winfor.monopoly.game.Player.PlayerListener;

import javax.swing.JList;

import java.awt.BorderLayout;

/**
 * info panel, which displays information about a specific player
 * 
 * @author Nicolas Winkler
 * 
 */
public class PlayerInfoPanel extends JPanel implements PlayerListener,
        Freeable, LanguageListener {
    /** */
    private static final long serialVersionUID = 8750813181164541570L;

    /** the player about whom information is displayed */
    private Player player;

    /** reference to the game */
    private Game game;

    /** description label "Money:" */
    private JLabel lblMoney;

    /** list model of {@link #propertyList} */
    private DefaultListModel<String> propertyListModel;

    /** list with all properties a player owns */
    private JList<String> propertyList;
    private JLabel propertiesLabel;

    /**
     * Create the panel.
     */
    public PlayerInfoPanel(Player player, Game game) {
        this.player = player;
        this.game = game;
        setLayout(new BorderLayout(0, 0));

        lblMoney = new JLabel("Money:");
        lblMoney.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(lblMoney, BorderLayout.NORTH);

        player.addPlayerListener(this);

        JPanel panel = new JPanel();
        add(panel, BorderLayout.CENTER);

        propertyListModel = new DefaultListModel<String>();
        propertyList = new JList<String>(propertyListModel);

        int nFields = game.getBoard().getAbsoluteLength();
        for (int i = 0; i < nFields; i++) {
            Field f = game.getBoard().getField(i);
            if (f instanceof BuyableField)
                if (player
                        .possesses((BuyableField) game.getBoard().getField(i)))
                    propertyListModel.addElement(f.getName());
        }

        panel.setLayout(new BorderLayout(0, 0));
        panel.add(propertyList);

        propertiesLabel = new JLabel("properties:");
        panel.add(propertiesLabel, BorderLayout.NORTH);

        Language lang = Language.getInstance();
        lang.addLanguageListener(this);
        languageChanged(lang);
    }

    @Override
    public void languageChanged(Language sender) {
        refreshMoneyCaption();
        propertiesLabel.setText(sender.get("properties") + ":");
    }

    /**
     * clears the panel and removes all listeners
     */
    @Override
    public void free() {
        player.removePlayerListener(this);
        Language lang = Language.getInstance();
        lang.removeLanguageListener(this);
    }

    private void refreshMoneyCaption() {
        long wealth = player.getWealth();

        Board board = game.getBoard();
        String moneyPrefix = board.getCurrencyPrefix();
        String moneySuffix = board.getCurrencySuffix();

        Language lang = Language.getInstance();

        lblMoney.setText(lang.get("money") + ": " + moneyPrefix + wealth
                + moneySuffix);
    }

    @Override
    public void wealthChanged(Player sender, long oldWealth, long newWealth) {
        refreshMoneyCaption();
    }

    @Override
    public void wentBankrupt(Player sender) {
    }

    @Override
    public void jailStateChanged(Player sender, int oldRounds) {
    }

    @Override
    public void addedPropertyPossession(Player sender, BuyableField property) {
        if (sender == player) {
            propertyListModel.addElement(property.getName());
        }
    }

    @Override
    public void addedCardPossession(Player sender, KeepableCard card) {
    }
}
