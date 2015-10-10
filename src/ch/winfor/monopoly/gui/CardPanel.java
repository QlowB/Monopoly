package ch.winfor.monopoly.gui;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import ch.winfor.monopoly.game.Card;

/**
 * panel displaying the content of a card (chance, community chest)
 * 
 * @author Nicolas Winkler
 * 
 */
public class CardPanel extends JPanel {
    /** */
    private static final long serialVersionUID = 1477690741781584792L;

    /** the text panel displaying the cards content */
    private JLabel lblText;

    /**
     * Create the panel.
     */
    public CardPanel(Card c) {
        assert c != null : "card == null";

        setLayout(new BorderLayout(0, 0));

        lblText = new JLabel("<card text>");
        lblText.setHorizontalAlignment(SwingConstants.CENTER);
        add(lblText, BorderLayout.CENTER);
        if (c.getParentStack() != null)
            this.setBackground(c.getParentStack().getColor());
        lblText.setText("<html><body><div align=\"center\">" + c.getText()
                + "</div></body></html>");
    }
}
