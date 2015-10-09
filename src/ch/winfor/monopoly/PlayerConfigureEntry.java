package ch.winfor.monopoly;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import ch.winfor.monopoly.Language.LanguageListener;
import ch.winfor.monopoly.MonopolyGameConfiguration.Player;
import ch.winfor.monopoly.MonopolyGameConfiguration.Player.Type;

import javax.swing.border.BevelBorder;

/**
 * panel used to configure a game
 * 
 * An {@link ActionEvent} is sent to subscribers when the "remove" button was
 * pressed on this entry. The action command (can be viewed by calling
 * {@link ActionEvent#getActionCommand()}) for such events is "remove". Other
 * possibilities for the action command are: "color updated", "name updated" or
 * "type updated"
 * 
 * @author Nicolas Winkler
 * 
 */
public class PlayerConfigureEntry extends ActionPanel implements
		ActionListener, LanguageListener, Freeable, DocumentListener {
	/** */
	private static final long serialVersionUID = -7822005555617733410L;

	/** desired height of the panel */
	private static final int HEIGHT = 37;

	/** field to enter the player's name */
	private JTextField txtName;

	/** remove button */
	private JButton btnRemove;

	/** the color panel */
	private ChooseColorPanel colorChoice;

	/** choose whether the player is an ai, player or network player */
	private JComboBox<String> typeComboBox;
	private JPanel txtPanel;
	private JPanel infoPanel;

	/**
	 * create the panel
	 */
	public PlayerConfigureEntry() {
		setPreferredSize(new Dimension(436, 37));
		setMaximumSize(new Dimension(0x7FFFFFFF, HEIGHT));

		actionListeners = new ArrayList<ActionListener>();
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0 };
		gridBagLayout.rowWeights = new double[] { 0.0 };
		setLayout(gridBagLayout);

		txtPanel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.weightx = 0.4;
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.insets = new Insets(0, 5, 0, 5);
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 0;
		add(txtPanel, gbc_panel);
		txtPanel.setLayout(new BoxLayout(txtPanel, BoxLayout.X_AXIS));

		txtName = new JTextField();
		txtPanel.add(txtName);

		txtName.setMaximumSize(new Dimension(0x7FFFFFFF, txtName
				.getPreferredSize().height));
		txtName.setColumns(10);
		txtName.getDocument().addDocumentListener(this);

		colorChoice = new ChooseColorPanel();
		colorChoice.setMinimumSize(new Dimension(26, 26));
		colorChoice.setPreferredSize(new Dimension(26, 26));
		colorChoice.addActionListener(this);
		GridBagConstraints gbc_colorChoice = new GridBagConstraints();
		gbc_colorChoice.anchor = GridBagConstraints.NORTH;
		gbc_colorChoice.fill = GridBagConstraints.BOTH;
		gbc_colorChoice.insets = new Insets(5, 0, 5, 5);
		gbc_colorChoice.gridx = 1;
		gbc_colorChoice.gridy = 0;
		add(colorChoice, gbc_colorChoice);
		typeComboBox = new JComboBox<String>();
		typeComboBox.setModel(new DefaultComboBoxModel<String>(new String[] {
				"human", "computer", "network" }));
		typeComboBox.addActionListener(this);
		GridBagConstraints gbc_typeComboBox = new GridBagConstraints();
		gbc_typeComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_typeComboBox.insets = new Insets(0, 0, 0, 5);
		gbc_typeComboBox.gridx = 2;
		gbc_typeComboBox.gridy = 0;
		add(typeComboBox, gbc_typeComboBox);

		infoPanel = new JPanel();
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.weightx = 0.2;
		gbc_panel_1.insets = new Insets(0, 0, 0, 5);
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.gridx = 3;
		gbc_panel_1.gridy = 0;
		add(infoPanel, gbc_panel_1);

		btnRemove = new JButton("");
		btnRemove.setToolTipText("remove this player");
		btnRemove.setIcon(new ImageIcon(PlayerConfigureEntry.class
				.getResource("/ch/winfor/monopoly/res/cross_small.png")));
		GridBagConstraints gbc_btnRemove = new GridBagConstraints();
		gbc_btnRemove.fill = GridBagConstraints.BOTH;
		gbc_btnRemove.gridx = 4;
		gbc_btnRemove.gridy = 0;
		add(btnRemove, gbc_btnRemove);
		btnRemove.addActionListener(this);

		Language lang = Language.getInstance();
		lang.addLanguageListener(this);
		languageChanged(lang);
	}

	/**
	 * removes the language listener from the language
	 */
	public void free() {
		Language lang = Language.getInstance();
		lang.removeLanguageListener(this);
	}

	@Override
	public void languageChanged(Language sender) {
		btnRemove.setToolTipText(sender.get("remove this player"));
		typeComboBox.setModel(new DefaultComboBoxModel<String>(new String[] {
				sender.get("human"), sender.get("computer"),
				sender.get("network") }));
	}

	@Override
	public void setEnabled(boolean b) {
		super.setEnabled(b);
		txtName.setEnabled(b);
		btnRemove.setEnabled(b);
		colorChoice.setEnabled(b);
		typeComboBox.setEnabled(b);
	}

	/**
	 * @return the infoPanel (a panel where additional information can be
	 *         displayed)
	 */
	public JPanel getInfoPanel() {
		return infoPanel;
	}

	/**
	 * @param infoPanel
	 *            the infoPanel to set
	 */
	public void setInfoPanel(JPanel infoPanel) {
		this.infoPanel = infoPanel;
	}

	/**
	 * @return the textfield containing the player's name
	 */
	public JTextField getPlayerNameField() {
		return txtName;
	}

	/**
	 * @return the player's name
	 */
	public String getPlayerName() {
		return txtName.getText();
	}

	/**
	 * @return the chosen color for the playing piece
	 */
	public Color getPieceColor() {
		return colorChoice.getColor();
	}

	/**
	 * @return the type of this player
	 */
	public MonopolyGameConfiguration.Player.Type getPlayerType() {
		switch (typeComboBox.getSelectedIndex()) {
		case 0:
			return Type.HUMAN;
		case 1:
			return Type.ARTIFICIAL_INTELLIGENCE;
		case 2:
			return Type.NETWORK;
		default:
			return null;
		}
	}

	public void setPlayerType(MonopolyGameConfiguration.Player.Type type) {
		int index = 0;
		switch (type) {
		case HUMAN:
			index = 0;
			break;
		case ARTIFICIAL_INTELLIGENCE:
			index = 1;
			break;
		case NETWORK:
			index = 2;
			break;
		}

		typeComboBox.setSelectedIndex(index);
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		if (ae.getSource() == btnRemove) {
			fireActionEvent("remove");
		} else if (ae.getSource() == colorChoice) {
			fireActionEvent("color update");
		} else if (ae.getSource() == typeComboBox) {
			fireActionEvent("type update");
		}
	}

	public void setChosenColor(Color color) {
		colorChoice.setColor(color);
	}

	public void setPlayerName(String name) {
		txtName.setText(name);
	}

	/**
	 * sets the panel to the values of a
	 * {@link MonopolyGameConfiguration.Player}
	 * 
	 * @param p
	 *            the player
	 */
	public void applyPlayer(Player p) {
		setChosenColor(p.getPieceColor());
		setPlayerName(p.getName());
		setPlayerType(p.getType());
	}
	


	public Player getConfigurationPlayer() {
		Player p = new Player();
		p.setPieceColor(getPieceColor());
		p.setName(getPlayerName());
		p.setType(getPlayerType());
		return p;
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		if (e.getDocument() == txtName.getDocument()) {
			nameChanged();
		}
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		if (e.getDocument() == txtName.getDocument()) {
			nameChanged();
		}
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		if (e.getDocument() == txtName.getDocument()) {
			nameChanged();
		}
	}

	private void nameChanged() {
		fireActionEvent("name updated");
	}

	/**
	 * panel that displays a color and if clicked on, displays a color picker
	 * where a new color can be chosen
	 * 
	 * @author Nicolas Winkler
	 * 
	 */
	public static class ChooseColorPanel extends ActionPanel implements
			MouseListener, ChangeListener {
		/** */
		private static final long serialVersionUID = -8939641099886562896L;

		/** actual color picker component */
		private JColorChooser chooser;

		/** picker dialog */
		private JDialog colorChooser;

		/**
		 * initialize
		 */
		public ChooseColorPanel() {
			this.addMouseListener(this);
			colorChooser = new JDialog();
			chooser = new JColorChooser();
			chooser.getSelectionModel().addChangeListener(this);
			colorChooser.setContentPane(chooser);
			colorChooser.pack();
			colorChooser.setResizable(false);
			colorChooser.setLocationRelativeTo(null);
			setBackground(new Color(new Random().nextInt()));
			setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null,
					null));
		}

		/**
		 * @return the chosen color
		 */
		public Color getColor() {
			return getBackground();
		}

		/**
		 * sets the color
		 * 
		 * @param color
		 *            the new color
		 */
		public void setColor(Color color) {
			setBackground(color);
		}

		@Override
		public void mouseClicked(MouseEvent me) {
			if (isEnabled())
				colorChooser.setVisible(true);
		}

		@Override
		public void mouseEntered(MouseEvent me) {
		}

		@Override
		public void mouseExited(MouseEvent me) {
		}

		@Override
		public void mousePressed(MouseEvent me) {
			if (isEnabled())
				setBorder(new BevelBorder(BevelBorder.LOWERED, null, null,
						null, null));
		}

		@Override
		public void mouseReleased(MouseEvent me) {
			setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null,
					null));
		}

		@Override
		public void stateChanged(ChangeEvent ce) {
			if (ce.getSource() == chooser.getSelectionModel()) {
				setBackground(chooser.getColor());
				fireActionEvent("color updated");
			}
		}
	}
}
