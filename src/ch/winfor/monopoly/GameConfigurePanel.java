package ch.winfor.monopoly;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import ch.winfor.monopoly.Language.LanguageListener;
import ch.winfor.monopoly.MonopolyGameConfiguration.Player;
import ch.winfor.monopoly.game.Board;
import ch.winfor.monopoly.game.BoardFactory;
import ch.winfor.monopoly.res.Ressources;

/**
 * panel to configure game settings
 * 
 * This component may fire an {@link ActionEvent} with parameter (gettable via
 * {@link ActionEvent#getActionCommand()}) "port updated" which indicates that
 * the content of the port field has been changed. There are other possibilities
 * such as "board updated".
 * 
 * @author Nicolas Winkler
 *
 */
public class GameConfigurePanel extends ActionPanel implements
		LanguageListener, ActionListener, FocusListener {
	/** */
	private static final long serialVersionUID = 7311417722895130839L;

	/** panel containing the player entries */
	protected PlayerListPanel centerContent;

	protected JComboBox<String> chooseBoardBox;

	protected JLabel lblChooseMap;

	protected ArrayList<BoardLink> boardLinks;
	protected JCheckBox chckbxOnline;
	private JTextField portField;
	private Component rigidArea;
	private JLabel lblPort;
	private Component rigidArea_1;

	public GameConfigurePanel() {
		setLayout(new BorderLayout(0, 0));
		centerContent = new PlayerListPanel();
		add(centerContent, BorderLayout.CENTER);

		boardLinks = getBoardNames();

		JPanel boardChoosePanel = new JPanel();
		boardChoosePanel.setBorder(new EmptyBorder(0, 5, 0, 5));
		add(boardChoosePanel, BorderLayout.NORTH);
		boardChoosePanel.setPreferredSize(new Dimension(0, 30));
		boardChoosePanel.setLayout(new BoxLayout(boardChoosePanel,
				BoxLayout.X_AXIS));

		lblChooseMap = new JLabel("Choose Board:");
		boardChoosePanel.add(lblChooseMap);
		chooseBoardBox = new JComboBox<String>();
		lblChooseMap.setLabelFor(chooseBoardBox);
		chooseBoardBox.addActionListener(this);
		initChooseMapBox();
		boardChoosePanel.add(chooseBoardBox);

		rigidArea = Box.createRigidArea(new Dimension(40, 20));
		boardChoosePanel.add(rigidArea);

		chckbxOnline = new JCheckBox("online");
		boardChoosePanel.add(chckbxOnline);

		rigidArea_1 = Box.createRigidArea(new Dimension(20, 20));
		boardChoosePanel.add(rigidArea_1);

		lblPort = new JLabel("port:");
		boardChoosePanel.add(lblPort);

		portField = new JTextField();
		lblPort.setLabelFor(portField);
		boardChoosePanel.add(portField);
		portField.setColumns(6);
		portField.setText(NetworkSetupPanel.DEFAULT_PORT);

		portField.addActionListener(this);
		portField.addFocusListener(this);

		chckbxOnline.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				portField.setEnabled(chckbxOnline.isSelected());
				portChanged();
			}
		});
		chckbxOnline.setSelected(false);
		portField.setEnabled(false);

		Language lang = Language.getInstance();
		lang.addLanguageListener(this);
		languageChanged(lang);
	}

	protected void initChooseMapBox() {
		String[] content = new String[boardLinks.size()];
		for (int i = 0; i < content.length; i++)
			content[i] = boardLinks.get(i).name;
		chooseBoardBox.setModel(new DefaultComboBoxModel<String>(content));
	}

	@Override
	public void languageChanged(Language sender) {
		lblChooseMap.setText(sender.get("choose board"));
		lblPort.setText(sender.get("port") + ":");
		chckbxOnline.setText(sender.get("online"));
	}

	/**
	 * @return the list panel in the center
	 */
	public PlayerListPanel getListPanel() {
		return centerContent;
	}

	/**
	 * @return a list with the available boards
	 */
	public ArrayList<BoardLink> getBoardNames() {
		ArrayList<BoardLink> links = new ArrayList<BoardLink>();
		InputStreamReader isr;
		try {
			isr = new InputStreamReader(Ressources.getRessource("boards.txt"),
					"UTF8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return links;
		}

		BufferedReader reader = new BufferedReader(isr);

		String line = "";
		try {
			while ((line = reader.readLine()) != null) {
				int index = line.indexOf(":");
				if (index > 0) {
					BoardLink bl = new BoardLink();
					bl.name = line.substring(0, index).trim();
					bl.path = line.substring(index + 1).trim();
					links.add(bl);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return links;
	}

	/**
	 * structure representing name and path of a board
	 * 
	 * @author Nicolas Winkler
	 * 
	 */
	protected static class BoardLink {
		/** the name of the board */
		public String name;

		/** it's path in the resource system */
		public String path;
	}

	/**
	 * @return the name of the selected board
	 */
	public String getBoardName() {
		int boardIndex = chooseBoardBox.getSelectedIndex();
		return boardLinks.get(boardIndex).name;
	}

	/**
	 * @return the path of the selected board
	 */
	public String getBoardPath() {
		int boardIndex = chooseBoardBox.getSelectedIndex();
		return boardLinks.get(boardIndex).path;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == portField) {
			portChanged();
		} else if (e.getSource() == chooseBoardBox) {
			fireActionEvent("board updated");
		}
	}

	@Override
	public void focusGained(FocusEvent e) {
	}

	@Override
	public void focusLost(FocusEvent e) {
		if (e.getSource() == portField) {
			portChanged();
		}
	}

	/**
	 * invoked on change in {@link #portField}
	 */
	private void portChanged() {
		int port = getPort();
		fireActionEvent(port, "port updated");
	}

	/**
	 * @return the chosen port
	 */
	public int getPort() {
		try {
			return Integer.parseInt(portField.getText());
		} catch (NumberFormatException nfe) {
			return -1;
		}
	}

	/**
	 * 
	 * @return weather the online checkbox is selected
	 */
	public boolean isOnline() {
		return this.chckbxOnline.isSelected();
	}

	/**
	 * sets a new port and writes it into {@link #portField}
	 * 
	 * @param port
	 *            the new port
	 */
	public void setPort(int port) {
		portField.setText(Integer.toString(port));
	}

	/**
	 * creates a {@link MonopolyGameConfiguration}, sets it to the chosen
	 * configuration and returns it
	 * 
	 * @param withBoard
	 *            if the board should be saved too
	 * 
	 * @return a {@link MonopolyGameConfiguration} corresponding to the chosen
	 *         preferences
	 */
	public MonopolyGameConfiguration getConfiguration(boolean withBoard) {
		MonopolyGameConfiguration mgc = new MonopolyGameConfiguration();

		if (isOnline())
			mgc.setPort(getPort());
		else
			mgc.setPort(-1);

		if (withBoard) {
			String boardPath = getBoardPath();
			InputStream ressource = Ressources.getRessource(boardPath);
			Board board = null;

			try {
				board = BoardFactory.createFromXml(ressource);
			} catch (Exception e) {
				e.printStackTrace();
			}

			mgc.setBoard(board);
		}

		mgc.setBoardName(getBoardName());

		PlayerListPanel list = getListPanel();
		for (int i = 0; i < list.getNPlayerEntries(); i++) {
			PlayerConfigureEntry entry = list.getPlayerEntry(i);
			MonopolyGameConfiguration.Player p = entry.getConfigurationPlayer();
			mgc.addPlayer(p);
		}

		return mgc;
	}

	/**
	 * models this panel to match the given {@link MonopolyGameConfiguration}
	 * 
	 * @param mgc
	 *            the {@link MonopolyGameConfiguration}
	 */
	public void applyConfiguration(MonopolyGameConfiguration mgc) {
		chooseBoardBox.setModel(new DefaultComboBoxModel<String>(
				new String[] { mgc.getBoardName() }));
		chooseBoardBox.setSelectedIndex(0);
		remove(centerContent);

		setPort(mgc.getPort());

		for (int i = 0; i < mgc.getNPlayers(); i++) {
			Player p = mgc.getPlayer(i);
			PlayerConfigureEntry pce = new PlayerConfigureEntry();
			centerContent.addPlayerEntrySilently(pce);
			pce.applyPlayer(p);
		}

		add(centerContent, BorderLayout.CENTER);
		revalidate();
		repaint();
	}
}
