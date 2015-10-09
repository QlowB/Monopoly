package ch.winfor.monopoly.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;

import ch.winfor.monopoly.Freeable;
import ch.winfor.monopoly.Language;
import ch.winfor.monopoly.Language.LanguageListener;
import ch.winfor.monopoly.game.Game;
import ch.winfor.monopoly.game.TurnHandler;
import ch.winfor.monopoly.gui.turnControl.BuyHousesPanel;
import ch.winfor.monopoly.gui.turnControl.BuyPropertyPanel;
import ch.winfor.monopoly.gui.turnControl.CastDicePanel;
import ch.winfor.monopoly.gui.turnControl.DrawCardPanel;
import ch.winfor.monopoly.gui.turnControl.EndTurnPanel;
import ch.winfor.monopoly.gui.turnControl.PayRentPanel;
import ch.winfor.monopoly.gui.turnControl.PayTaxPanel;
import ch.winfor.monopoly.gui.turnControl.PlayCardPanel;
import ch.winfor.monopoly.gui.turnControl.TakenYourTurnPanel;
import ch.winfor.monopoly.gui.turnControl.TurnActionPanel;

/**
 * panel translating user input to changes in the game
 * 
 * @author Nicolas Winkler
 * 
 */
public class GameControlPanel extends JPanel implements ActionListener,
		LanguageListener, Freeable {
	/** */
	private static final long serialVersionUID = -1883884333624129256L;

	/** the height of the panel containing the controls */
	private static final int CONTROL_PANEL_HEIGHT = 150;

	/** reference to the game */
	private Game game;

	/** turn control panel which handles the throw of the dice */
	private CastDicePanel castDicePanel;

	/** turn control panel which asks the player if he wants to buy a property */
	private BuyPropertyPanel buyPropertyPanel;

	/** turn control panel which makes the player draw a card */
	private DrawCardPanel drawCardPanel;

	/** turn control panel which makes the player play a card */
	private PlayCardPanel playCardPanel;

	/** turn control panel which makes the player pay a tax */
	private PayTaxPanel payTaxPanel;

	/** turn control panel which makes the player pay rent */
	private PayRentPanel payRentPanel;

	/** turn control panel which asks the player if he wants to buy houses */
	private BuyHousesPanel buyHousesPanel;

	/** turn control panel which asks the player if he has finished his turn */
	private EndTurnPanel endTurnPanel;

	/**
	 * turn control panel which informs the player that he has finished his turn
	 */
	private TakenYourTurnPanel takenYourTurnPanel;

	/** info panel that displays info about the player */
	private PlayerInfoPanel playerInfoPanel;

	/** panel containing {@link TurnActionPanel}s to control the game */
	private JPanel upperPanel;

	/** thread waiting for the player to take his turn */
	private TurnAwaiter scanThread;

	/**
	 * specifies which turn this panel controls
	 */
	private int turn;
	private JPanel topPanel;
	private JLabel lblWhoseturn;

	/**
	 * determines if the user can actually do stuff with this panel or if it
	 * just displays stuff
	 */
	private boolean editable;

	/**
	 * Create the panel.
	 * 
	 * @param boardPanel
	 *            the board for the game
	 * @param turn
	 *            which turn the game control panel should control
	 * @param editable
	 *            determines if the user can actually do stuff with this panel
	 *            or if it just displays stuff
	 */
	public GameControlPanel(BoardPanel boardPanel, int turn, boolean editable) {
		this.editable = editable;
		this.setGame(boardPanel.getGame());
		this.turn = turn;

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 1.0, 1.0, 2.0 };
		setLayout(gridBagLayout);

		topPanel = new JPanel();
		GridBagConstraints gbc_topPanel = new GridBagConstraints();
		gbc_topPanel.insets = new Insets(0, 0, 5, 0);
		gbc_topPanel.fill = GridBagConstraints.BOTH;
		gbc_topPanel.gridx = 0;
		gbc_topPanel.gridy = 0;
		add(topPanel, gbc_topPanel);

		lblWhoseturn = new JLabel("whose_turn");
		lblWhoseturn.setFont(new Font("Tahoma", Font.PLAIN, 16));
		topPanel.add(lblWhoseturn);

		upperPanel = new JPanel();
		upperPanel.setBorder(null);
		upperPanel.setLayout(new BorderLayout());
		upperPanel.setPreferredSize(new Dimension(0, CONTROL_PANEL_HEIGHT));
		GridBagConstraints gbc_upperPanel = new GridBagConstraints();
		gbc_upperPanel.weighty = 0.5;
		gbc_upperPanel.anchor = GridBagConstraints.NORTH;
		gbc_upperPanel.insets = new Insets(0, 0, 5, 0);
		gbc_upperPanel.fill = GridBagConstraints.BOTH;
		gbc_upperPanel.gridx = 0;
		gbc_upperPanel.gridy = 1;
		add(upperPanel, gbc_upperPanel);

		if (editable) {
			castDicePanel = new CastDicePanel(game);
			buyPropertyPanel = new BuyPropertyPanel(game);
			drawCardPanel = new DrawCardPanel(game);
			playCardPanel = new PlayCardPanel(game);
			payTaxPanel = new PayTaxPanel(game);
			payRentPanel = new PayRentPanel(game);
			buyHousesPanel = new BuyHousesPanel(game);
			endTurnPanel = new EndTurnPanel(game);
			takenYourTurnPanel = new TakenYourTurnPanel(game);

			castDicePanel.addActionListener(this);
			buyPropertyPanel.addActionListener(this);
			drawCardPanel.addActionListener(this);
			playCardPanel.addActionListener(this);
			payTaxPanel.addActionListener(this);
			payRentPanel.addActionListener(this);
			buyHousesPanel.addActionListener(this);
			endTurnPanel.addActionListener(this);
			takenYourTurnPanel.addActionListener(this);
		}

		playerInfoPanel = new PlayerInfoPanel(getGame().getPlayer(turn), game);
		JPanel centerPanel = new JPanel();
		centerPanel.setBorder(new LineBorder(new Color(244, 164, 96), 2));
		centerPanel.setLayout(new BorderLayout(0, 0));
		centerPanel.add(playerInfoPanel, BorderLayout.CENTER);
		GridBagConstraints gbc_infoPanel = new GridBagConstraints();
		gbc_infoPanel.weighty = 0.5;
		gbc_infoPanel.insets = new Insets(0, 0, 5, 0);
		gbc_infoPanel.fill = GridBagConstraints.BOTH;
		gbc_infoPanel.gridx = 0;
		gbc_infoPanel.gridy = 2;
		add(centerPanel, gbc_infoPanel);

		waitForNextTurn();

		Language lang = Language.getInstance();
		lang.addLanguageListener(this);
		languageChanged(lang);
	}

	public void free() {
		Language lang = Language.getInstance();
		lang.removeLanguageListener(this);
	}

	@Override
	public void languageChanged(Language sender) {
		String before = sender.get("whose_turn_before");
		String after = sender.get("whose_turn_after");
		lblWhoseturn.setText(before + game.getPlayer(turn).getName() + after);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == endTurnPanel) {
			if (e.getID() == EndTurnPanel.ACTION_ID_END_TURN) {
				waitForNextTurn();
				setUpperPanelContent(takenYourTurnPanel);

				TurnHandler turnHandler = game.getTurnHandler();
				turnHandler.endTurn();
			} else {
				setUpperPanelContent(buyHousesPanel);
			}
		} else if (source instanceof TurnActionPanel) {
			updateTurnActionPanel();
		}
	}

	/**
	 * sets the content of the upper panel ({@link #upperPanel}) and refreshes
	 * it
	 * 
	 * @param comp
	 *            the new content of the panel
	 */
	private void setUpperPanelContent(TurnActionPanel comp) {
		comp.refresh();
		upperPanel.removeAll();
		upperPanel.add(comp);
		upperPanel.revalidate();
		upperPanel.repaint();
	}

	/**
	 * updates the turn control elements
	 */
	private void updateTurnActionPanel() {
		TurnHandler th = game.getTurnHandler();

		switch (th.getNextTask()) {
		case CAST_DICE:
			setUpperPanelContent(castDicePanel);
			break;
		case BUY_PROPERTY:
			setUpperPanelContent(buyPropertyPanel);
			break;
		case PAY_TAX:
			setUpperPanelContent(payTaxPanel);
			break;
		case PAY_RENT:
			setUpperPanelContent(payRentPanel);
			break;
		case DRAW_CARD:
			setUpperPanelContent(drawCardPanel);
			break;
		case FOLLOW_CARD:
			playCardPanel.setCard(th.getDrawnCard());
			setUpperPanelContent(playCardPanel);
			break;
		case END_TURN:
			setUpperPanelContent(endTurnPanel);
			break;
		default:
			break;
		}
	}

	/**
	 * @return the game
	 */
	public Game getGame() {
		return game;
	}

	/**
	 * @param game
	 *            the game to set
	 */
	public void setGame(Game game) {
		this.game = game;
	}

	/**
	 * sets {@link #scanThread}) to a new turn–awaiting thread
	 */
	private void waitForNextTurn() {
		if (!editable)
			return;
		if (scanThread != null)
			scanThread.shouldStop();
		scanThread = new TurnAwaiter();
		scanThread.start();
	}

	/**
	 * thread running a loop which checks if it's the player's turn
	 * 
	 * @author Nicolas Winkler
	 * 
	 */
	private class TurnAwaiter extends Thread {
		/** flag, if the thread should still run */
		private boolean shouldRun;

		public TurnAwaiter() {
			shouldRun = true;
		}

		/**
		 * urges the thread to stop
		 * 
		 * The thread should effectively stop after a maximum 100 milliseconds
		 * of delay have passed.
		 */
		public void shouldStop() {
			shouldRun = false;
		}

		/**
		 * main scanning routine
		 */
		public void run() {
			while (shouldRun) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (game.getTurn() == turn) {
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							updateTurnActionPanel();
						}
					});

					shouldRun = false;
				}
			}
		}
	}
}
