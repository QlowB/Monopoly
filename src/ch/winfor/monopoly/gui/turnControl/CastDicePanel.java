package ch.winfor.monopoly.gui.turnControl;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import ch.winfor.monopoly.Language;
import ch.winfor.monopoly.Language.LanguageListener;
import ch.winfor.monopoly.game.Game;
import ch.winfor.monopoly.game.TurnHandler;
import ch.winfor.monopoly.game.TurnHandler.TurnTask;

/**
 * Panel that is displayed when the player should cast the dice
 * 
 * @author Nicolas Winkler
 * 
 */
public class CastDicePanel extends TurnActionPanel implements ActionListener,
		LanguageListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6369666756818300879L;

	private JButton btnCastDice;

	/**
	 * Create the panel.
	 */
	public CastDicePanel(Game game) {
		super(game);
		btnCastDice = new JButton("Cast Dice");
		this.add(btnCastDice);
		btnCastDice.addActionListener(this);

		Language lang = Language.getInstance();
		lang.addLanguageListener(this);
		languageChanged(lang);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		TurnHandler turnHandler = game.getTurnHandler();
		if (source == btnCastDice) {
			if (turnHandler.getNextTask() == TurnTask.CAST_DICE) {
				turnHandler.castDice();
				turnHandler.movePiece();
				ActionEvent ae = new ActionEvent(this, 0, "");
				fireActionEvent(ae);
			} else {
				ActionEvent ae = new ActionEvent(this, -1, "");
				fireActionEvent(ae);
			}
		}
	}

	@Override
	public void languageChanged(Language sender) {
		btnCastDice.setText(sender.get("cast dice"));
	}
}
