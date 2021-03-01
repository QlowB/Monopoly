package ch.winfor.monopoly.gui.turnControl;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import ch.winfor.monopoly.Language;
import ch.winfor.monopoly.Language.LanguageListener;
import ch.winfor.monopoly.game.Game;

public class TakenYourTurnPanel extends TurnActionPanel implements
        MouseListener {

    /** */
    private static final long serialVersionUID = 7761490899903232852L;

    /**
     * create the panel
     */
    public TakenYourTurnPanel(Game game) {
        super(game);
        setLayout(new BorderLayout(0, 0));

        final JLabel lblNewLabel = new JLabel("You have taken your turn.");
        lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(lblNewLabel);
        this.addMouseListener(this);

        LanguageListener ll = new LanguageListener() {

            @Override
            public void languageChanged(Language sender) {
                lblNewLabel.setText(sender.get("you have taken..."));
            }
        };
        Language lang = Language.getInstance();
        lang.addLanguageListener(ll);
        ll.languageChanged(lang);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        ActionEvent ae = new ActionEvent(this, 0, "Clicked");
        fireActionEvent(ae);
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
}
