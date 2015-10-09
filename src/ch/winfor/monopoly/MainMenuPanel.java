package ch.winfor.monopoly;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import ch.winfor.monopoly.Language.LanguageListener;

/**
 * panel displaying the main menu
 * 
 * An {@link ActionEvent} is sent to the subscribers when the "start game"
 * button was pressed on this entry. The action command (can be viewed by
 * calling {@link ActionEvent#getActionCommand()}) for this event is
 * "start game"
 * 
 * @author Nicolas Winkler
 * 
 */
public class MainMenuPanel extends ActionPanel {

	/** */
	private static final long serialVersionUID = -4893226312103119244L;

	/** the button that starts the game */
	private JButton btnStartGame;

	/** preferences button */
	private JButton btnPreferences;

	/** exit game button */
	private JButton btnExit;

	/** welcome text to the left */
	private JLabel lblWelcometext;

	/** I hope this variable name is self-explanatory */
	private static final Color BACKGROUND_COLOR = new Color(255, 239, 213);

	/**
	 * create the panel
	 */
	public MainMenuPanel() {
		setBorder(new EmptyBorder(3, 5, 3, 3));
		setPreferredSize(new Dimension(500, 300));
		this.setBackground(BACKGROUND_COLOR);

		actionListeners = new ArrayList<ActionListener>();
		setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(200, 0));
		panel.setBackground(new Color(255, 239, 213));
		panel.setBorder(new LineBorder(new Color(25, 25, 112), 2));
		this.add(panel, BorderLayout.EAST);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		btnStartGame = new JButton("Start Game");
		btnStartGame.setBackground(getBackground());
		btnStartGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fireActionEvent("start game");
			}
		});

		btnExit = new JButton("Exit");
		btnExit.setBackground(getBackground());
		btnExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fireActionEvent("exit");
			}
		});

		btnPreferences = new JButton("Preferences");
		btnPreferences.setBackground(getBackground());
		btnPreferences.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				fireActionEvent("preferences");
			}
		});

		panel.add(Box.createRigidArea(new Dimension(0, 10)));
		panel.add(btnStartGame);

		panel.add(Box.createRigidArea(new Dimension(0, 5)));
		panel.add(btnPreferences);
		panel.add(Box.createRigidArea(new Dimension(0, 5)));
		panel.add(btnExit);
		btnStartGame.setAlignmentX(Component.CENTER_ALIGNMENT);
		btnPreferences.setAlignmentX(Component.CENTER_ALIGNMENT);
		btnExit.setAlignmentX(Component.CENTER_ALIGNMENT);

		lblWelcometext = new JLabel("Welcome!");

		this.add(lblWelcometext);

		Language lang = Language.getInstance();
		lang.addLanguageListener(new LanguageListener() {
			@Override
			public void languageChanged(Language sender) {
				setCaptions(sender);
			}
		});
		setCaptions(lang);
	}

	/**
	 * @return the button "start game"
	 */
	public JButton getStartGameButton() {
		return btnStartGame;
	}

	private void setCaptions(Language lang) {
		btnStartGame.setText(lang.get("start game"));
		btnPreferences.setText(lang.get("preferences"));
		btnExit.setText(lang.get("exit"));
		lblWelcometext.setText(lang.get("welcome_text"));
	}

	/**
	 * sets the "start game" button as the default
	 */
	public void setDefaultButton() {
		getRootPane().setDefaultButton(btnStartGame);
	}
}
