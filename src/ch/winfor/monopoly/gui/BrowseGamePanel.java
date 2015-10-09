package ch.winfor.monopoly.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.border.LineBorder;

import ch.winfor.monopoly.Freeable;
import ch.winfor.monopoly.Language;
import ch.winfor.monopoly.Language.LanguageListener;
import ch.winfor.monopoly.game.Board;
import ch.winfor.monopoly.game.BuyableField;
import ch.winfor.monopoly.game.Field;
import ch.winfor.monopoly.game.Game;
import ch.winfor.monopoly.game.PropertyField;
import ch.winfor.monopoly.game.RailroadField;
import ch.winfor.monopoly.gui.BoardPanel.BoardPanelListener;

/**
 * panel allowing the user to scan through the properties etc.
 * 
 * @author Nicolas Winkler
 * 
 */
public class BrowseGamePanel extends JPanel implements ActionListener,
		BoardPanelListener, Freeable, LanguageListener {
	/** */
	private static final long serialVersionUID = 3014737613733371029L;

	/** the main game */
	private Game game;

	/** the lower panel that contains information */
	private JPanel displayPanel;

	/** the panel where we can browse for players */
	private JPanel browsePlayersPanel;

	/** the panel where we can browse for players */
	private JPanel browsePropertiesPanel;

	/** choose player box */
	private JComboBox<String> choosePlayer;

	/** choose field box */
	private JComboBox<BuyableField> chooseField;

	/** the player info panel, can sometimes be <code>null</code> */
	private PlayerInfoPanel playerInfoPanel;

	/** the tabbed pane containing everything */
	private JTabbedPane tabbedPane;
	private JScrollPane scrollPane;
	private JPanel propertiesDisplayPanel;

	/**
	 * Create the panel.
	 */
	public BrowseGamePanel(Game game) {
		setBorder(new LineBorder(Color.GREEN, 2));
		this.game = game;
		setLayout(new BorderLayout(0, 0));

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		add(tabbedPane, BorderLayout.CENTER);

		browsePlayersPanel = new JPanel();
		tabbedPane.addTab("Players", browsePlayersPanel);

		choosePlayer = new JComboBox<String>();
		choosePlayer.setModel(new DefaultComboBoxModel<String>(this.game
				.getPlayerNames()));
		choosePlayer.addActionListener(this);
		browsePlayersPanel.setLayout(new BorderLayout(0, 0));

		displayPanel = new JPanel();
		displayPanel.setBorder(null);
		browsePlayersPanel.add(choosePlayer, BorderLayout.NORTH);

		playerInfoPanel = new PlayerInfoPanel(game.getPlayer(0), game);
		displayPanel.add(playerInfoPanel);

		JScrollPane scroll = new JScrollPane(displayPanel);
		browsePlayersPanel.add(scroll);
		scroll.setBorder(null);
		displayPanel.setLayout(new BorderLayout(0, 0));

		browsePropertiesPanel = new JPanel();
		tabbedPane.addTab("Properties", browsePropertiesPanel);
		chooseField = new BuyableFieldComboBox();
		chooseField.setModel(new DefaultComboBoxModel<BuyableField>(
				findBuyableFields()));
		chooseField.addActionListener(this);
		browsePropertiesPanel.setLayout(new BorderLayout(0, 0));
		browsePropertiesPanel.add(chooseField, BorderLayout.NORTH);

		setPreferredSize(new Dimension(188, 238));

		propertiesDisplayPanel = new JPanel();
		propertiesDisplayPanel.setBorder(null);
		propertiesDisplayPanel.setLayout(new BorderLayout(0, 0));

		scrollPane = new JScrollPane(propertiesDisplayPanel);
		scrollPane.setBorder(null);
		browsePropertiesPanel.add(scrollPane, BorderLayout.CENTER);

		choosePlayer.setSelectedIndex(0);
		chooseField.setSelectedIndex(0);

		Language lang = Language.getInstance();
		lang.addLanguageListener(this);
		languageChanged(lang);
	}

	@Override
	public void languageChanged(Language sender) {
		tabbedPane.setTitleAt(0, sender.get("players"));
		tabbedPane.setTitleAt(1, sender.get("properties"));
	}

	@Override
	public void free() {
		Language lang = Language.getInstance();
		lang.removeLanguageListener(this);
	}

	/**
	 * lists all instances of {@link BuyableField} in a {@link Vector} -
	 * strucure
	 * 
	 * @return the vector - a list of all the instances of {@link BuyableField}
	 */
	private Vector<BuyableField> findBuyableFields() {
		Board board = game.getBoard();
		Vector<BuyableField> buyableFields = new Vector<BuyableField>();
		for (int i = 0; i < board.getAbsoluteLength(); i++) {
			Field field = board.getField(i);
			if (field instanceof BuyableField) {
				buyableFields.add((BuyableField) field);
			}
		}
		return buyableFields;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == chooseField) {
			BuyableField field = (BuyableField) chooseField.getSelectedItem();

			clearPanel(propertiesDisplayPanel);
			propertiesDisplayPanel.add(new PropertyInfoPanel(field, game.getBoard()));
		} else if (e.getSource() == choosePlayer) {
			int index = choosePlayer.getSelectedIndex();

			clearPanel(displayPanel);
			playerInfoPanel = new PlayerInfoPanel(game.getPlayer(index), game);
			displayPanel.add(playerInfoPanel);
		}
		displayPanel.revalidate();
		displayPanel.repaint();
		propertiesDisplayPanel.revalidate();
		propertiesDisplayPanel.repaint();
	}

	/**
	 * removes everything from the display panel and deletes the old panel
	 */
	private void clearPanel(JPanel panel) {
		for (int i = 0; i < panel.getComponentCount(); i++) {
			Component c = panel.getComponent(i);
			if (c instanceof Freeable)
				((Freeable) c).free();
		}
		panel.removeAll();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.winfor.monopoly.gui.BoardPanel.BoardPanelListener#clickedOnField(ch
	 * .winfor.monopoly.gui.BoardPanel, int)
	 */
	@Override
	public void clickedOnField(BoardPanel sender, int fieldIndex) {
		Board board = game.getBoard();
		Field field = board.getField(fieldIndex);
		if (field instanceof BuyableField) {
			chooseField.setSelectedItem(field);
			clearPanel(propertiesDisplayPanel);
			propertiesDisplayPanel.add(new PropertyInfoPanel(
					(BuyableField) field, board));

			propertiesDisplayPanel.revalidate();
			propertiesDisplayPanel.repaint();

			if (tabbedPane.getSelectedIndex() != 1)
				tabbedPane.setSelectedIndex(1);
		}
	}

	/**
	 * a combo box where instances of {@link BuyableField} can be placed and are
	 * displayed in the color corresponding to their monopoly
	 * 
	 * @author Nicolas Winkler
	 * 
	 */
	public static class BuyableFieldComboBox extends JComboBox<BuyableField> {
		/** */
		private static final long serialVersionUID = 1430659266292473554L;

		/**
		 * initialize the combo box
		 */
		public BuyableFieldComboBox() {
			setRenderer(new DefaultListCellRenderer() {
				/** */
				private static final long serialVersionUID = 552175687241041012L;

				@Override
				public Component getListCellRendererComponent(JList<?> list,
						Object value, int index, boolean isSelected,
						boolean cellHasFocus) {
					Component comp = super.getListCellRendererComponent(list,
							value, index, isSelected, cellHasFocus);

					if (value instanceof Field) {
						setText(((Field) value).getDescription());
					}

					if (value instanceof RailroadField) {
						Color c;
						if (!isSelected) {
							c = Color.GRAY;
						} else {
							c = Color.GRAY.darker();
						}
						comp.setBackground(c);
					}

					if (value instanceof PropertyField) {
						Color c;
						if (!isSelected) {
							c = ((PropertyField) value).getGroup().getColor();
						} else {
							c = ((PropertyField) value).getGroup().getColor()
									.darker();
						}
						comp.setBackground(c);
					}

					return comp;
				}
			});
		}

		@Override
		public Dimension getMaximumSize() {
			Dimension max = super.getMaximumSize();
			max.height = getPreferredSize().height;
			return max;
		}
	}
}
