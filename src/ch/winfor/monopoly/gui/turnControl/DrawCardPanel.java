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
import ch.winfor.monopoly.game.Card;
import ch.winfor.monopoly.game.Game;

public class DrawCardPanel extends TurnActionPanel implements ActionListener {
    /**
	 * 
	 */
    private static final long serialVersionUID = -571553863754017889L;

    /**
     * Create the panel.
     */
    public DrawCardPanel(Game game) {
        super(game);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        final JLabel lblYouHaveTo = new JLabel(
                "<html><body><div align=\"center\">"
                        + "You have to draw a card from a stack.</div></body></html>");
        lblYouHaveTo.setHorizontalAlignment(SwingConstants.CENTER);
        lblYouHaveTo.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(lblYouHaveTo);

        final JButton btnDrawCard = new JButton("Draw Card");
        btnDrawCard.setAlignmentY(Component.TOP_ALIGNMENT);
        btnDrawCard.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(btnDrawCard);
        btnDrawCard.addActionListener(this);

        LanguageListener ll = new LanguageListener() {
            @Override
            public void languageChanged(Language sender) {
                lblYouHaveTo.setText("<html><body><div align=\"center\">"
                        + sender.get("prompt_draw") + "</div></body></html>");
                btnDrawCard.setText(sender.get("draw card"));
            }
        };
        Language lang = Language.getInstance();
        lang.addLanguageListener(ll);
        ll.languageChanged(lang);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ActionEvent ae = new ActionEvent(this, 0, "");
        Card card = game.getTurnHandler().drawCard();
        if (card != null) {
            /*
             * JFrame f = new JFrame(); f.setContentPane(new CardPanel(card));
             * f.pack(); f.setVisible(true);
             */
        }
        fireActionEvent(ae);
    }
}
