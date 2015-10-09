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
import ch.winfor.monopoly.game.BuyableField;
import ch.winfor.monopoly.game.Game;
import ch.winfor.monopoly.game.TurnHandler;

public class BuyPropertyPanel extends TurnActionPanel implements
		ActionListener, LanguageListener {
	/** */
	private static final long serialVersionUID = -3421108417250719211L;
	private JButton btnBuyProperty;
	private JButton btnDontBuyProperty;
	private JLabel lblYouCanBuy;

	private String beforeBuy;

	/**
	 * Create the panel.
	 */
	public BuyPropertyPanel(Game game) {
		super(game);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		lblYouCanBuy = new JLabel("", SwingConstants.CENTER);
		lblYouCanBuy.setAlignmentX(Component.CENTER_ALIGNMENT);
		add(lblYouCanBuy);

		btnBuyProperty = new JButton("Buy Property");
		add(btnBuyProperty);
		btnBuyProperty.setAlignmentX(Component.CENTER_ALIGNMENT);
		btnBuyProperty.addActionListener(this);
		btnDontBuyProperty = new JButton("Don't Buy Property");
		add(btnDontBuyProperty);
		btnDontBuyProperty.setAlignmentX(Component.CENTER_ALIGNMENT);
		btnDontBuyProperty.addActionListener(this);

		Language lang = Language.getInstance();
		lang.addLanguageListener(this);
		languageChanged(lang);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		TurnHandler turnHandler = game.getTurnHandler();
		if (e.getSource() == btnBuyProperty) {
			turnHandler.buyProperty(true);
		}
		if (e.getSource() == btnDontBuyProperty) {
			turnHandler.buyProperty(false);
		}

		ActionEvent ae = new ActionEvent(this, 0, "");
		fireActionEvent(ae);
	}

	/**
	 * recalculates the text displayed on the panel
	 */
	public void refresh() {
		TurnHandler th = game.getTurnHandler();
		BuyableField buyableField = th.getPropertyToBuy();

		String caption = "";
		if (buyableField != null)
			caption = "<html><div align=\"center\">" + beforeBuy
					+ buyableField.getDescription() + " ("
					+ buyableField.getPrice() + ")" + "</div></html>";
		lblYouCanBuy.setText(caption);
	}

	@Override
	public void languageChanged(Language sender) {
		btnBuyProperty.setText(sender.get("buy property"));
		btnDontBuyProperty.setText(sender.get("don't buy property"));
		beforeBuy = sender.get("prop_announce");
		refresh();
	}
}
