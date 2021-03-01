package ch.winfor.monopoly.gui.turnControl;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;

import ch.winfor.monopoly.Language;
import ch.winfor.monopoly.Language.LanguageListener;
import ch.winfor.monopoly.game.Game;
import ch.winfor.monopoly.game.MonopolyGroup;
import ch.winfor.monopoly.game.Player;
import ch.winfor.monopoly.game.PropertyField;
import ch.winfor.monopoly.game.TurnHandler;
import ch.winfor.monopoly.gui.BrowseGamePanel.BuyableFieldComboBox;

public class BuyHousesPanel extends TurnActionPanel implements ActionListener,
        LanguageListener {

    /** */
    private static final long serialVersionUID = -8320663478289569278L;

    /** button to close the panel */
    private JButton btnNoMoreHouses;

    /** button to buy a house */
    private JButton btnBuyHouse;

    /** box to select the property to build on */
    private BuyableFieldComboBox comboBox;

    /**
     * create the panel
     */
    public BuyHousesPanel(Game game) {
        super(game);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        comboBox = new BuyableFieldComboBox();
        add(comboBox);

        btnBuyHouse = new JButton("Buy House");
        btnBuyHouse.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnBuyHouse.addActionListener(this);
        add(btnBuyHouse);

        Component rigidArea = Box.createRigidArea(new Dimension(20, 20));
        add(rigidArea);

        btnNoMoreHouses = new JButton("OK");
        btnNoMoreHouses.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(btnNoMoreHouses);
        btnNoMoreHouses.addActionListener(this);
        refresh();

        Language lang = Language.getInstance();
        lang.addLanguageListener(this);
        languageChanged(lang);
    }

    /**
     * refreshes the panel
     */
    public void refresh() {
        TurnHandler th = game.getTurnHandler();
        Player player = th.getPlayer();
        MonopolyGroup[] monopolies = game.getMonopolies(player);

        comboBox.removeAllItems();

        for (int i = 0; i < monopolies.length; i++) {
            MonopolyGroup group = monopolies[i];
            for (int j = 0; j < group.getNFields(); j++) {
                PropertyField field = group.getField(j);
                comboBox.addItem(field);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnNoMoreHouses) {
            ActionEvent ae = new ActionEvent(this, 0, btnNoMoreHouses.getText());
            fireActionEvent(ae);
        } else if (e.getSource() == btnBuyHouse) {
            TurnHandler th = game.getTurnHandler();
            Object selectedObject = comboBox.getSelectedItem();
            if (selectedObject instanceof PropertyField) {
                PropertyField propertyField = (PropertyField) selectedObject;
                th.buyHouse(propertyField);
            }
        }
    }

    @Override
    public void languageChanged(Language sender) {
        btnBuyHouse.setText(sender.get("buy house"));
        btnNoMoreHouses.setText(sender.get("ok"));
    }
}
