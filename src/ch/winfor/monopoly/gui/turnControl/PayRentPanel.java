package ch.winfor.monopoly.gui.turnControl;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import ch.winfor.monopoly.Language;
import ch.winfor.monopoly.Language.LanguageListener;
import ch.winfor.monopoly.game.Game;
import ch.winfor.monopoly.game.TurnHandler;

public class PayRentPanel extends TurnActionPanel implements ActionListener {
    /**  */
    private static final long serialVersionUID = 2502618524815360040L;

    /**
     * create the panel
     */
    public PayRentPanel(Game game) {
        super(game);

        final JButton btnPayRent = new JButton("Pay Rent");
        btnPayRent.addActionListener(this);
        add(btnPayRent);

        LanguageListener ll = new LanguageListener() {

            @Override
            public void languageChanged(Language sender) {
                btnPayRent.setText(sender.get("pay rent"));
            }
        };
        Language lang = Language.getInstance();
        lang.addLanguageListener(ll);
        ll.languageChanged(lang);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ActionEvent ae = new ActionEvent(this, 0, "Rent Payed");
        TurnHandler th = game.getTurnHandler();
        th.payRent();
        fireActionEvent(ae);
    }
}
