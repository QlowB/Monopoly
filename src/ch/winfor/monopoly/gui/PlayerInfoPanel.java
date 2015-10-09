package ch.winfor.monopoly.gui;

import java.awt.Component;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ch.winfor.monopoly.Freeable;
import ch.winfor.monopoly.Language;
import ch.winfor.monopoly.Language.LanguageListener;
import ch.winfor.monopoly.game.Board;
import ch.winfor.monopoly.game.BuyableField;
import ch.winfor.monopoly.game.Card.KeepableCard;
import ch.winfor.monopoly.game.Game;
import ch.winfor.monopoly.game.Player;
import ch.winfor.monopoly.game.Player.PlayerListener;

/**
 * info panel, which displays information about a specific player
 * 
 * @author Nicolas Winkler
 * 
 */
public class PlayerInfoPanel extends JPanel implements PlayerListener,
		Freeable, LanguageListener {
	/** */
	private static final long serialVersionUID = 8750813181164541570L;

	/** the player about whom information is displayed */
	private Player player;

	/** reference to the game */
	private Game game;

	/** description label "Money:" */
	private JLabel lblMoney;

	/**
	 * Create the panel.
	 */
	public PlayerInfoPanel(Player player, Game game) {
		this.player = player;
		this.game = game;
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		lblMoney = new JLabel("Money:");
		lblMoney.setAlignmentX(Component.CENTER_ALIGNMENT);
		add(lblMoney);

		player.addPlayerListener(this);

		Language lang = Language.getInstance();
		lang.addLanguageListener(this);
		languageChanged(lang);
	}

	@Override
	public void languageChanged(Language sender) {
		refreshMoneyCaption();
	}

	/**
	 * clears the panel and removes all listeners
	 */
	@Override
	public void free() {
		player.removePlayerListener(this);
		Language lang = Language.getInstance();
		lang.removeLanguageListener(this);
	}

	private void refreshMoneyCaption() {
		long wealth = player.getWealth();

		Board board = game.getBoard();
		String moneyPrefix = board.getCurrencyPrefix();
		String moneySuffix = board.getCurrencySuffix();
		
		Language lang = Language.getInstance();

		lblMoney.setText(lang.get("money") + ": " + moneyPrefix + wealth
				+ moneySuffix);
	}

	@Override
	public void wealthChanged(Player sender, long oldWealth, long newWealth) {
		refreshMoneyCaption();
	}

	@Override
	public void wentBankrupt(Player sender) {
	}

	@Override
	public void jailStateChanged(Player sender, int oldRounds) {
	}

	@Override
	public void addedPropertyPossession(Player sender, BuyableField property) {
	}

	@Override
	public void addedCardPossession(Player sender, KeepableCard card) {
	}
}
