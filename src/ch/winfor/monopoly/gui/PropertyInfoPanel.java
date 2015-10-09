package ch.winfor.monopoly.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import ch.winfor.monopoly.Freeable;
import ch.winfor.monopoly.Language;
import ch.winfor.monopoly.Language.LanguageListener;
import ch.winfor.monopoly.game.Board;
import ch.winfor.monopoly.game.BuyableField;
import ch.winfor.monopoly.game.CompanyField;
import ch.winfor.monopoly.game.PropertyField;
import ch.winfor.monopoly.game.RailroadField;

/**
 * a panel displaying information about a property (rent, mortgage value etc.)
 * 
 * @author Nicolas Winkler
 * 
 */
public class PropertyInfoPanel extends JPanel implements LanguageListener,
		Freeable {
	/** */
	private static final long serialVersionUID = -4067231683648188219L;

	/** coloured header with title etc. */
	private JPanel headerPanel;

	/** the field, which the info panel informs about */
	private BuyableField buyableField;

	/** displays the price of the property */
	private JLabel price;

	private Board board;

	private JPanel lower_panel;

	/**
	 * initialize the panel
	 */
	public PropertyInfoPanel(BuyableField buyableField, Board board) {
		this.buyableField = buyableField;
		this.board = board;
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 1.0, 5.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);

		headerPanel = new JPanel();
		if (buyableField instanceof PropertyField) {
			PropertyField propertyField = (PropertyField) buyableField;
			headerPanel.setBackground(propertyField.getGroup().getColor());
		} else {
			headerPanel.setBackground(Color.WHITE);
			headerPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		}
		GridBagConstraints gbc_headerPanel = new GridBagConstraints();
		gbc_headerPanel.weighty = 0.2;
		gbc_headerPanel.insets = new Insets(0, 0, 0, 0);
		gbc_headerPanel.fill = GridBagConstraints.BOTH;
		gbc_headerPanel.gridx = 0;
		gbc_headerPanel.gridy = 0;
		add(headerPanel, gbc_headerPanel);
		headerPanel.setLayout(new BorderLayout(0, 0));

		JLabel lblPropertyName = new JLabel(buyableField.getDescription());
		lblPropertyName.setHorizontalAlignment(SwingConstants.CENTER);
		lblPropertyName.setFont(lblPropertyName.getFont().deriveFont(20.0f));
		headerPanel.add(lblPropertyName);

		lower_panel = new JPanel();
		lower_panel.setBackground(Color.WHITE);
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.weighty = 0.8;
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 1;
		add(lower_panel, gbc_panel);
		lower_panel.setLayout(new BoxLayout(lower_panel, BoxLayout.Y_AXIS));

		/*
		 * JLabel lblRent = new JLabel("Rent: <$>"); lblRent.setEnabled(true);
		 * panel.add(lblRent); lblRent.setAlignmentX(CENTER_ALIGNMENT);
		 * 
		 * JLabel lblHouse = new JLabel("1 House: "); panel.add(lblHouse);
		 */

		Language lang = Language.getInstance();
		lang.addLanguageListener(this);
		languageChanged(lang);
	}

	private void initalize() {
		lower_panel.removeAll();
		price = new JLabel();

		lower_panel.add(price);
		price.setAlignmentX(CENTER_ALIGNMENT);

		JLabel[] rentLabels = getTable();

		for (int i = 0; i < rentLabels.length; i++) {
			lower_panel.add(rentLabels[i]);
			rentLabels[i].setAlignmentX(CENTER_ALIGNMENT);
		}
	}

	@Override
	public void languageChanged(Language sender) {
		initalize();
		String priceStr = board.getCurrencyText(buyableField.getPrice());
		price.setText(sender.get("price") + ": " + priceStr);
	}

	@Override
	public void free() {
		Language lang = Language.getInstance();
		lang.removeLanguageListener(this);
	}

	/**
	 * creates a list of labels which inform the reader about rent etc. of the
	 * property
	 * 
	 * @return the created list
	 */
	private JLabel[] getTable() {
		Language lang = Language.getInstance();
		if (buyableField instanceof PropertyField) {
			PropertyField propertyField = (PropertyField) buyableField;

			int nLabels = propertyField.getMaxHouses() + 1;
			JLabel[] rentLabels = new JLabel[nLabels];

			if (nLabels > 0) {
				rentLabels[0] = new JLabel(lang.get("rent") + ": "
						+ board.getCurrencyText(propertyField.getRent(0)));
			}

			for (int i = 1; i < nLabels - 1; i++) {
				rentLabels[i] = new JLabel(lang.get("with") + " " + i + " "
						+ lang.get("houses") + ": "
						+ board.getCurrencyText(propertyField.getRent(i)));
			}

			if (nLabels > 1) {
				rentLabels[nLabels - 1] = new JLabel(lang.get("with")
						+ " "
						+ lang.get("hotel")
						+ ": "
						+ board.getCurrencyText(propertyField
								.getRent(nLabels - 1)));
			}
			return rentLabels;
		} else if (buyableField instanceof RailroadField) {
			RailroadField railroadField = (RailroadField) buyableField;
			int nLabels = railroadField.getMaxRailroads();
			JLabel[] rentLabels = new JLabel[nLabels];

			if (nLabels > 0) {
				rentLabels[0] = new JLabel("Rent: "
						+ board.getCurrencyText(railroadField.getRent(1)));
			}

			for (int i = 2; i <= nLabels; i++) {
				rentLabels[i - 1] = new JLabel("If " + i + " railroads owned: "
						+ board.getCurrencyText(railroadField.getRent(i)));
			}
			return rentLabels;
		} else if (buyableField instanceof CompanyField) {
			CompanyField companyField = (CompanyField) buyableField;
			int nLabels = companyField.getMaxCompanies();
			JLabel[] rentLabels = new JLabel[nLabels];

			if (nLabels > 0) {
				rentLabels[0] = new JLabel("Rent: "
						+ companyField.getRentMultiplicator(1) + "x Dice Roll");
			}

			for (int i = 2; i <= nLabels; i++) {
				rentLabels[i - 1] = new JLabel("If " + i + " companies owned: "
						+ companyField.getRentMultiplicator(i) + "x Dice Roll");
			}

			return rentLabels;
		} else {
			return new JLabel[0];
		}
	}
}
