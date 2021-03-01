package ch.winfor.monopoly.gui.turnControl;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import ch.winfor.monopoly.Language;
import ch.winfor.monopoly.Language.LanguageListener;
import ch.winfor.monopoly.game.Card;
import ch.winfor.monopoly.game.Game;
import ch.winfor.monopoly.gui.CardPanel;

public class PlayCardPanel extends TurnActionPanel implements ActionListener {
    /**
	 * 
	 */
    private static final long serialVersionUID = -571553863754017889L;

    /** panel to display the drawn card */
    private JPanel cardPanel;

    /**
     * Create the panel.
     */
    public PlayCardPanel(Game game) {
        super(game);
        setLayout(new BorderLayout(0, 0));

        cardPanel = new JPanel();
        add(cardPanel, BorderLayout.SOUTH);
        cardPanel.setLayout(new BorderLayout(0, 0));
        cardPanel.setPreferredSize(new Dimension(0, 100));

        JPanel upperPanel = new JPanel();
        add(upperPanel, BorderLayout.CENTER);
        upperPanel.setLayout(new BoxLayout(upperPanel, BoxLayout.Y_AXIS));

        final JLabel lblYouHaveTo = new JLabel(
                "<html><div align=\"center\">Now you must follow the card's instructions.</div></html>");
        lblYouHaveTo.setHorizontalAlignment(SwingConstants.CENTER);
        upperPanel.add(lblYouHaveTo);
        lblYouHaveTo.setAlignmentY(Component.CENTER_ALIGNMENT);
        lblYouHaveTo.setAlignmentX(Component.CENTER_ALIGNMENT);

        final JButton btnDrawCard = new JButton("Play Card");
        upperPanel.add(btnDrawCard);
        btnDrawCard.setAlignmentY(Component.TOP_ALIGNMENT);
        btnDrawCard.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnDrawCard.addActionListener(this);

        LanguageListener ll = new LanguageListener() {

            @Override
            public void languageChanged(Language sender) {
                lblYouHaveTo.setText("<html><div align=\"center\">"
                        + sender.get("you_must_follow") + "</div></html>");
                btnDrawCard.setText(sender.get("play card"));
            }
        };
        Language lang = Language.getInstance();
        lang.addLanguageListener(ll);
        ll.languageChanged(lang);
    }

    /**
     * sets the card to display
     * 
     * @param card
     *            the card to display
     */
    public void setCard(Card card) {
        CardPanel panel = new CardPanel(card);
        cardPanel.removeAll();
        cardPanel.add(panel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ActionEvent ae = new ActionEvent(this, 0, "");
        game.getTurnHandler().followCard();
        fireActionEvent(ae);
    }
}
