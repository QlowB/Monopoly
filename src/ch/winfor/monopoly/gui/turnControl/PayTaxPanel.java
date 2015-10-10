package ch.winfor.monopoly.gui.turnControl;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import ch.winfor.monopoly.Language;
import ch.winfor.monopoly.Language.LanguageListener;
import ch.winfor.monopoly.game.Game;
import ch.winfor.monopoly.game.TurnHandler;

public class PayTaxPanel extends TurnActionPanel implements ActionListener {
    /**  */
    private static final long serialVersionUID = 2502618524815360040L;

    /**
     * create the panel
     */
    public PayTaxPanel(Game game) {
        super(game);

        final JButton btnPayTax = new JButton("Pay Tax");
        btnPayTax.addActionListener(this);
        add(btnPayTax);

        LanguageListener ll = new LanguageListener() {

            @Override
            public void languageChanged(Language sender) {
                btnPayTax.setText(sender.get("pay tax"));
            }
        };
        Language lang = Language.getInstance();
        lang.addLanguageListener(ll);
        ll.languageChanged(lang);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ActionEvent ae = new ActionEvent(this, 0, "Tax Payed");
        TurnHandler th = game.getTurnHandler();
        th.payTax();
        fireActionEvent(ae);
    }
}
