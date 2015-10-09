package ch.winfor.monopoly;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Point;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ch.winfor.monopoly.Language.LanguageListener;
import ch.winfor.monopoly.ai.MonopolyAi;
import ch.winfor.monopoly.game.Board;
import ch.winfor.monopoly.game.BuyableField;
import ch.winfor.monopoly.game.Card;
import ch.winfor.monopoly.game.Card.KeepableCard;
import ch.winfor.monopoly.game.Game;
import ch.winfor.monopoly.game.GameListener;
import ch.winfor.monopoly.game.Player;
import ch.winfor.monopoly.gui.BoardPanel;
import ch.winfor.monopoly.gui.BrowseGamePanel;
import ch.winfor.monopoly.gui.GameControlPanel;

/**
 * a frame with a monopoly board and control panels on it
 * 
 * @author Nicolas Winkler
 * 
 */
public class MonopolyFrame extends JFrame implements LanguageListener,
		GameListener, Freeable, ChangeListener, ComponentListener {
	/** */
	private static final long serialVersionUID = 53108889100866260L;

	/** panel which displays the board */
	private BoardPanel boardPanel;

	/** the game */
	private Game game;

	private GameControlPanel[] gameControlPanels;

	private CardLayout cardLayout;

	private JPanel cardLayoutPanel;
	private JSplitPane upDownSplitPanel;

	/** panel, which allows the player to "browse" some game information */
	private BrowseGamePanel browseGamePanel;
	private JScrollPane boardContainer;
	private JMenuBar menuBar;
	private JMenu mnView;
	private JPanel panel;
	private JSlider slider;
	private JLabel lblZoom;

	private volatile boolean resizing;

	private ArrayList<MonopolyAi> ais;

	private static final int MIN_BOARD_SIZE = 500;
	private static final int MAX_BOARD_SIZE = 2500;

	/**
	 * creates the window from a {@link MonopolyGameConfiguration} structure
	 * 
	 * @param configuration
	 *            information about the settings
	 */
	public MonopolyFrame(MonopolyGameConfiguration configuration) {
		ais = new ArrayList<MonopolyAi>();
		Board board = configuration.getBoard();
		game = new Game(board, configuration.getNPlayers());
		boardPanel = new BoardPanel(game);

		gameControlPanels = new GameControlPanel[configuration.getNPlayers()];

		for (int i = 0; i < configuration.getNPlayers(); i++) {
			MonopolyGameConfiguration.Player p = configuration.getPlayer(i);
			if (p.getType() == MonopolyGameConfiguration.Player.Type.ARTIFICIAL_INTELLIGENCE) {
				ais.add(new MonopolyAi(game, i));
			}
			game.getPlayer(i).setName(p.getName());
			game.getPiece(i).setColor(p.getPieceColor());
		}

		for (int i = 0; i < configuration.getNPlayers(); i++) {
			MonopolyGameConfiguration.Player p = configuration.getPlayer(i);
			if (p.getType() == MonopolyGameConfiguration.Player.Type.ARTIFICIAL_INTELLIGENCE) {
				gameControlPanels[i] = new GameControlPanel(boardPanel, i,
						false);
			} else {
				gameControlPanels[i] = new GameControlPanel(boardPanel, i, true);
			}
		}

		game.addGameListener(this);

		initialize();
	}

	/**
	 * initializes a client side monopoly frame
	 * 
	 * @param game the game
	 */
	public MonopolyFrame(Game game, int playerIndex) {
		this.game = game;
		boardPanel = new BoardPanel(game);

		gameControlPanels = new GameControlPanel[game.getNPlayers()];

		for (int i = 0; i < game.getNPlayers(); i++) {
			if (i == playerIndex) {
				gameControlPanels[i] = new GameControlPanel(boardPanel, i,
						false);
			} else {
				gameControlPanels[i] = new GameControlPanel(boardPanel, i, true);
			}
		}

		game.addGameListener(this);

		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		setMinimumSize(new Dimension(850, 600));

		JSplitPane splitPane = new JSplitPane();
		splitPane.setBorder(null);
		splitPane.setResizeWeight(1.0);
		getContentPane().add(splitPane, BorderLayout.CENTER);

		boardPanel.setMinimumSize(new Dimension(500, 500));
		boardPanel.setPreferredSize(boardPanel.getMinimumSize());

		boardContainer = new JScrollPane(boardPanel);
		boardContainer.setMinimumSize(new Dimension(250, 250));
		boardContainer
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		boardContainer
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		boardPanel.addComponentListener(this);

		new MouseDragManager(boardPanel, boardContainer);

		splitPane.setLeftComponent(boardContainer);

		setLocationRelativeTo(null);
		splitPane.setDividerLocation(5.75 / 8);

		upDownSplitPanel = new JSplitPane();
		upDownSplitPanel.setResizeWeight(0.7);
		upDownSplitPanel.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane.setRightComponent(upDownSplitPanel);

		cardLayoutPanel = new JPanel();
		upDownSplitPanel.setLeftComponent(cardLayoutPanel);
		cardLayout = new CardLayout();
		cardLayoutPanel.setLayout(cardLayout);
		browseGamePanel = new BrowseGamePanel(game);
		upDownSplitPanel.setRightComponent(browseGamePanel);

		boardPanel.addBoardPanelListener(browseGamePanel);

		for (int i = 0; i < gameControlPanels.length; i++) {
			GameControlPanel gcp = gameControlPanels[i];
			cardLayoutPanel.add(gcp, Integer.toString(i));
		}

		menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		mnView = new JMenu("View");
		menuBar.add(mnView);

		panel = new JPanel();
		mnView.add(panel);

		lblZoom = new JLabel("zoom:");
		panel.add(lblZoom);

		slider = new JSlider();
		slider.addChangeListener(this);
		slider.setMaximum(100);
		slider.setValue(0);
		panel.add(slider);

		Language lang = Language.getInstance();
		lang.addLanguageListener(this);
		languageChanged(lang);
	}

	@Override
	public void free() {
		Language lang = Language.getInstance();
		lang.removeLanguageListener(this);

		for (int i = 0; i < gameControlPanels.length; i++) {
			GameControlPanel gcp = gameControlPanels[i];
			gcp.free();
		}

		for (MonopolyAi ai : ais) {
			ai.free();
		}
	}

	@Override
	public void languageChanged(Language sender) {
		mnView.setText(sender.get("view"));
		lblZoom.setText(sender.get("zoom") + ": ");
	}

	/**
	 * @return the game displayed in this frame
	 */
	public Game getGame() {
		return game;
	}

	@Override
	public void stateChanged(ChangeEvent ce) {
		if (ce.getSource() == slider) {
			resizing = true;
			int prefSize = MIN_BOARD_SIZE + (MAX_BOARD_SIZE - MIN_BOARD_SIZE)
					* slider.getValue() / slider.getMaximum();
			boardPanel.setPreferredSize(new Dimension(prefSize, prefSize));
			boardContainer.invalidate();
			boardContainer.validate();
			boardPanel.revalidate();
			boardContainer.repaint();
			resizing = false;
		}
	}

	@Override
	public void playingPieceMoved(Game sender, int pieceIndex, int oldPosition) {
	}

	@Override
	public void playerEndedTurn(Game sender, int playerIndex) {
		final int turn = sender.getTurn();
		if (gameControlPanels[turn] != null) {
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					cardLayout.show(cardLayoutPanel, Integer.toString(turn));
				}
			});
		}
	}

	@Override
	public void playerWealthChanged(Game sender, Player player,
			long wealthBefore) {
	}

	@Override
	public void playerWentBankrupt(Game sender, Player player) {
	}

	@Override
	public void playersJailStateChanged(Game sender, Player player,
			int jailStateBefore) {
	}

	@Override
	public void playerObtained(Game sender, Player player, BuyableField field) {
	}

	@Override
	public void playerKeepsCard(Game sender, Player player, KeepableCard card) {
	}

	@Override
	public void houseNumberChanged(Game game, int position, int oldNumber) {
	}

	@Override
	public void cardDrawn(Game sender, String deckName, Card c) {
	}

	@Override
	public void componentHidden(ComponentEvent arg0) {
	}

	@Override
	public void componentMoved(ComponentEvent arg0) {
	}

	@Override
	public void componentResized(ComponentEvent ce) {
		if (ce.getSource() == boardPanel && !resizing) {
			int actSize = boardPanel.getPreferredSize().width;
			int prefSize = MIN_BOARD_SIZE + (MAX_BOARD_SIZE - MIN_BOARD_SIZE)
					* slider.getValue() / slider.getMaximum();
			if (actSize != prefSize) {
				slider.setValue(actSize * (MAX_BOARD_SIZE - MIN_BOARD_SIZE)
						* slider.getMaximum() / actSize - MIN_BOARD_SIZE);
			}
		}
	}

	@Override
	public void componentShown(ComponentEvent arg0) {
	}

	private class MouseDragManager implements MouseListener,
			MouseMotionListener, MouseWheelListener {
		private int lastX;
		private int lastY;
		private JScrollPane scrollPane;
		private JComponent component;

		public MouseDragManager(JComponent component, JScrollPane scrollPane) {
			component.addMouseListener(this);
			component.addMouseMotionListener(this);
			component.addMouseWheelListener(this);
			this.component = component;
			this.scrollPane = scrollPane;
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			Point pt = scrollPane.getViewport().getViewPosition();
			pt.x += lastX - e.getX();
			pt.y += lastY - e.getY();
			if (pt.x < 0)
				pt.x = 0;
			if (pt.y < 0)
				pt.y = 0;
			if (pt.x >= component.getWidth() - scrollPane.getWidth())
				pt.x = component.getWidth() - scrollPane.getWidth();
			if (pt.y >= component.getHeight() - scrollPane.getHeight())
				pt.y = component.getHeight() - scrollPane.getHeight();

			scrollPane.getViewport().setViewPosition(pt);
		}

		@Override
		public void mouseMoved(MouseEvent e) {
		}

		@Override
		public void mouseClicked(MouseEvent e) {
		}

		@Override
		public void mousePressed(MouseEvent e) {
			lastX = e.getX();
			lastY = e.getY();
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

		@Override
		public void mouseWheelMoved(MouseWheelEvent mwe) {
			if (mwe.isControlDown() || mwe.isMetaDown()) {
				Dimension d = component.getPreferredSize();
				// d.height += 10;
				// d.width += 10;
				component.setPreferredSize(d);
				// component.revalidate();
				// component.repaint();
			}
		}
	}
}
