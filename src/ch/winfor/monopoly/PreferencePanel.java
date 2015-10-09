package ch.winfor.monopoly;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ch.winfor.monopoly.Language.LanguageListener;
import ch.winfor.monopoly.res.Ressources;

/**
 * panel displaying changeable settings
 * 
 * An {@link ActionEvent} is sent to subscribers when the ok button was pressed
 * on this entry. The action command (can be viewed by calling
 * {@link ActionEvent#getActionCommand()}) for such events is "ok", "cancel" for
 * the cancel button.
 * 
 * @author Nicolas Winkler
 * 
 */
public class PreferencePanel extends ActionPanel implements LanguageListener,
		ActionListener {
	/** */
	private static final long serialVersionUID = -1325238529997300369L;

	/** description label of {@link #languageComboBox} */
	private JLabel lblLanguage;

	/** language select combo box */
	private JComboBox<String> languageComboBox;

	/** cancel button */
	private JButton cancelButton;

	/** apply button */
	private JButton applyButton;

	/** ok button */
	private JButton okButton;

	/** panel containing the upper part of the dialog */
	private final JPanel contentPanel;

	/**
	 * Create the dialog.
	 */
	public PreferencePanel() {
		setLayout(new BorderLayout());
		contentPanel = new JPanel();
		add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);

		languageComboBox = new JComboBox<String>();
		languageComboBox.setBounds(110, 20, 153, 20);
		languageComboBox.setModel(new DefaultComboBoxModel<String>(
				new String[] { "English", "Deutsch" }));
		contentPanel.add(languageComboBox);

		lblLanguage = new JLabel("Language:");
		lblLanguage.setLabelFor(languageComboBox);
		lblLanguage.setBounds(10, 23, 90, 14);
		contentPanel.add(lblLanguage);

		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		add(buttonPane, BorderLayout.SOUTH);

		okButton = new JButton("OK");
		okButton.addActionListener(this);
		okButton.setActionCommand("OK");
		buttonPane.add(okButton);
		// if (getRootPane() != null)
		// getRootPane().setDefaultButton(okButton);

		applyButton = new JButton("Apply");
		applyButton.addActionListener(this);
		buttonPane.add(applyButton);

		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(this);
		cancelButton.setActionCommand("Cancel");
		buttonPane.add(cancelButton);

		Language lang = Language.getInstance();
		languageChanged(lang);
		lang.addLanguageListener(this);
	}

	@Override
	public void languageChanged(Language sender) {
		lblLanguage.setText(sender.get("language") + ":");
		okButton.setText(sender.get("ok"));
		applyButton.setText(sender.get("apply"));
		cancelButton.setText(sender.get("cancel"));
	}

	private void apply() {
		String language = (String) languageComboBox.getSelectedItem();
		String filename = "";
		if (language.equals("English")) {
			filename = "english.lng";
		} else if (language.equals("Deutsch")) {
			filename = "german.lng";
		}

		Language mainLang = Language.getInstance();
		mainLang.loadLanguage(Ressources.getRessource(filename));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == okButton) {
			apply();
			fireActionEvent("ok");
		}
		if (e.getSource() == applyButton) {
			apply();
		} else if (e.getSource() == cancelButton) {
			setVisible(false);
			fireActionEvent("cancel");
		}
	}
	

	/**
	 * sets the "ok" button as the default
	 */
	public void setDefaultButton() {
		getRootPane().setDefaultButton(okButton);
	}
}
