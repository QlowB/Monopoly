package ch.winfor.monopoly;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

import ch.winfor.monopoly.Language.LanguageListener;
import ch.winfor.monopoly.game.Game;

/**
 * panel collecting the user input information necessary for building up a
 * connection (ip, port)
 * 
 * @author Nicolas Winkler
 * 
 */
public class NetworkSetupPanel extends ActionPanel implements ActionListener,
		LanguageListener, Freeable {
	/** */
	private static final long serialVersionUID = 1815991856615144038L;

	/** the default value of the port field */
	public static final String DEFAULT_PORT = "24283";

	/** field to enter ip address */
	private JTextField ipTextField;

	/** field to enter port number */
	private JTextField portTextField;

	/** ip description label */
	private JLabel lblIp;

	/** port description label */
	private JLabel lblPort;

	/** top description */
	private JLabel descriptionLabel;

	/** connect button */
	private JButton btnConnect;

	/** the game */
	@SuppressWarnings("unused")
	private Game game;

	/**
	 * create the panel
	 */
	public NetworkSetupPanel(Game game) {
		this.game = game;
		// setLayout(new BorderLayout(0, 0));

		GridBagLayout gbl_connectPanel = new GridBagLayout();
		gbl_connectPanel.columnWidths = new int[] { 1, 15 };
		gbl_connectPanel.rowHeights = new int[] { 1, 1, 1, 1 };
		gbl_connectPanel.columnWeights = new double[] { 0.0, 0.0 };
		gbl_connectPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0 };
		this.setLayout(gbl_connectPanel);

		descriptionLabel = new JLabel();
		GridBagConstraints gbc_lblPort_1_1 = new GridBagConstraints();
		gbc_lblPort_1_1.fill = GridBagConstraints.BOTH;
		gbc_lblPort_1_1.gridwidth = 2;
		gbc_lblPort_1_1.insets = new Insets(5, 5, 5, 5);
		gbc_lblPort_1_1.gridx = 0;
		gbc_lblPort_1_1.gridy = 0;
		this.add(descriptionLabel, gbc_lblPort_1_1);

		lblIp = new JLabel("IP:");
		lblIp.setAlignmentX(Component.CENTER_ALIGNMENT);
		GridBagConstraints gbc_lblIp = new GridBagConstraints();
		gbc_lblIp.anchor = GridBagConstraints.WEST;
		gbc_lblIp.insets = new Insets(5, 5, 5, 5);
		gbc_lblIp.gridx = 0;
		gbc_lblIp.gridy = 1;
		this.add(lblIp, gbc_lblIp);

		ipTextField = new JTextField();
		ipTextField.setMaximumSize(ipTextField.getPreferredSize());
		GridBagConstraints gbc_ipTextField = new GridBagConstraints();
		gbc_ipTextField.weightx = 1.0;
		gbc_ipTextField.fill = GridBagConstraints.BOTH;
		gbc_ipTextField.insets = new Insets(0, 0, 5, 0);
		gbc_ipTextField.gridx = 1;
		gbc_ipTextField.gridy = 1;
		this.add(ipTextField, gbc_ipTextField);

		lblPort = new JLabel("Port:");
		// lblNewLabel_1.setBounds(6, 33, 29, 16);
		lblPort.setAlignmentX(Component.CENTER_ALIGNMENT);
		GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
		gbc_lblNewLabel_1.fill = GridBagConstraints.BOTH;
		gbc_lblNewLabel_1.insets = new Insets(5, 5, 5, 5);
		gbc_lblNewLabel_1.gridx = 0;
		gbc_lblNewLabel_1.gridy = 2;
		this.add(lblPort, gbc_lblNewLabel_1);

		portTextField = new JTextField();
		portTextField.setText(DEFAULT_PORT);
		portTextField.setMaximumSize(portTextField.getPreferredSize());
		portTextField.setColumns(10);
		GridBagConstraints gbc_portTextField = new GridBagConstraints();
		gbc_portTextField.weightx = 1.0;
		gbc_portTextField.fill = GridBagConstraints.BOTH;
		gbc_portTextField.insets = new Insets(0, 0, 5, 0);
		gbc_portTextField.gridx = 1;
		gbc_portTextField.gridy = 2;
		this.add(portTextField, gbc_portTextField);

		btnConnect = new JButton("Connect");
		btnConnect.addActionListener(this);
		btnConnect.setAlignmentX(Component.CENTER_ALIGNMENT);
		GridBagConstraints gbc_btnConnect = new GridBagConstraints();
		gbc_btnConnect.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnConnect.anchor = GridBagConstraints.NORTH;
		gbc_btnConnect.gridx = 1;
		gbc_btnConnect.gridy = 3;
		this.add(btnConnect, gbc_btnConnect);

		Language lang = Language.getInstance();
		lang.addLanguageListener(this);
		languageChanged(lang);
	}

	@Override
	public void free() {
		Language lang = Language.getInstance();
		lang.removeLanguageListener(this);
	}

	@Override
	public void languageChanged(Language sender) {
		descriptionLabel.setText(getClientDescription());
		lblIp.setText(sender.get("ip") + ":");
		lblPort.setText(sender.get("port") + ":");
		btnConnect.setText(sender.get("connect"));
	}

	/**
	 * @return the port number specified in this panel or -1, if no valid port
	 *         number specified
	 */
	public int getPort() {
		try {
			return Integer.parseInt(portTextField.getText());
		} catch (NumberFormatException nfe) {
			return -1;
		}
	}

	public String getIp() {
		return ipTextField.getText();
	}

	/**
	 * @return the description on the client panel
	 */
	private String getClientDescription() {
		Language lang = Language.getInstance();
		return "<html><div align=\"center\">" + lang.get("enter_ip_etc")
				+ "</div></html>";
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnConnect) {
			fireActionEvent(getPort(), getIp());
		}
	}
}
