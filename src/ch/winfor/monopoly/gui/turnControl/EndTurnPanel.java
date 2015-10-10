package ch.winfor.monopoly.gui.turnControl;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import ch.winfor.monopoly.Language;
import ch.winfor.monopoly.Language.LanguageListener;
import ch.winfor.monopoly.game.Game;
import ch.winfor.monopoly.game.MonopolyGroup;
import ch.winfor.monopoly.game.Player;
import ch.winfor.monopoly.game.TurnHandler;

public class EndTurnPanel extends TurnActionPanel implements ActionListener,
        LanguageListener {

    /** */
    private static final long serialVersionUID = 8840307023350814421L;

    /** button to buy houses */
    private JButton btnBuyHouses;

    /** button to end the turn */
    private JButton btnEndTurn;

    /** text displayed on the top of this panel */
    private JLabel lblCaption;

    /** flag set to a sent {@link ActionEvent}, if the user wishes to buy houses */
    public static final int ACTION_ID_BUY_HOUSES = 0;

    /**
     * flag set to a sent {@link ActionEvent}, if the user wishes to end his
     * turn
     */
    public static final int ACTION_ID_END_TURN = 1;

    /**
     * create the panel
     */
    public EndTurnPanel(Game game) {
        super(game);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        lblCaption = new JLabel("You have taken your turn.");
        lblCaption.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblCaption.setHorizontalAlignment(SwingConstants.CENTER);
        add(lblCaption);

        btnBuyHouses = new JButton("Buy Houses & Hotels");
        btnBuyHouses.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(btnBuyHouses);

        btnEndTurn = new JButton("End Turn");
        btnEndTurn.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(btnEndTurn);

        btnBuyHouses.addActionListener(this);
        btnEndTurn.addActionListener(this);

        Language lang = Language.getInstance();
        lang.addLanguageListener(this);
        languageChanged(lang);
    }

    @Override
    public void refresh() {
        TurnHandler turnHandler = game.getTurnHandler();
        Player player = turnHandler.getPlayer();

        MonopolyGroup[] groups = game.getMonopolies(player);
        if (groups.length > 0)
            btnBuyHouses.setEnabled(true);
        else
            btnBuyHouses.setEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnBuyHouses) {
            ActionEvent ae = new ActionEvent(this, ACTION_ID_BUY_HOUSES,
                    "Buy Houses");
            fireActionEvent(ae);
        } else if (e.getSource() == btnEndTurn) {
            ActionEvent ae = new ActionEvent(this, ACTION_ID_END_TURN,
                    "End Turn");
            fireActionEvent(ae);
        }
    }

    @Override
    public void languageChanged(Language sender) {
        lblCaption.setText(sender.get("you have taken..."));
        btnBuyHouses.setText(sender.get("buy houses & hotels"));
        btnEndTurn.setText(sender.get("end turn"));
    }
}
