package ch.winfor.monopoly.game;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import ch.winfor.monopoly.game.Field.StartField;
import ch.winfor.monopoly.res.Ressources;

/**
 * class providing functions to create boards
 * 
 * @author Nicolas Winkler
 * 
 */
public class BoardFactory {
	/**
	 * creates a new board (standard US-version)
	 * 
	 * @return the created board
	 */
	public static Board createStandardBoard() {
		try {
			return createFromXml(Ressources
					.getRessource("standard_edition.xml"));
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * loads a {@link Board} from an xml input stream
	 * 
	 * @param is
	 *            the xml strem
	 * @return the created board
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	public static Board createFromXml(InputStream is) throws SAXException,
			IOException, ParserConfigurationException {
		DocumentBuilder db = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder();
		Document d = db.parse(is);
		d.getDocumentElement().normalize();

		NodeList info = d.getElementsByTagName("Info");
		if (info.getLength() != 1)
			throw new BoardCreationException("More than one 'Info'-block");
		NodeList infos = info.item(0).getChildNodes();

		int flankSize = Board.STANDARD_FLANK_SIZE;
		long startupMoney = 0;
		int hotelVal = 5;
		String currencyPrefix = "";
		String currencySuffix = "$";

		for (int i = 0; i < infos.getLength(); i++) {
			Node node = infos.item(i);
			if (node.getNodeName().equals("Flank")) {
				String size = node.getAttributes().getNamedItem("size")
						.getNodeValue();
				flankSize = Integer.parseInt(size);
			} else if (node.getNodeName().equals("StartupMoney")) {
				String amount = node.getAttributes().getNamedItem("amount")
						.getNodeValue();
				startupMoney = Long.parseLong(amount);
			} else if (node.getNodeName().equals("Currency")) {
				currencyPrefix = node.getAttributes().getNamedItem("prefix")
						.getNodeValue();
				currencySuffix = node.getAttributes().getNamedItem("suffix")
						.getNodeValue();
			} else if (node.getNodeName().equals("Hotel")) {
				String houseValue = node.getAttributes()
						.getNamedItem("houseValue").getNodeValue();
				hotelVal = Integer.parseInt(houseValue);
			}
		}

		Board board = new Board(flankSize, hotelVal);
		board.setStartMoney(startupMoney);
		board.setCurrencyPrefix(currencyPrefix);
		board.setCurrencySuffix(currencySuffix);

		NodeList monopo = d.getElementsByTagName("Monopolies");
		if (monopo.getLength() != 1)
			throw new BoardCreationException(
					"There has to be exactly one 'Info'-block");

		NodeList monopolies = monopo.item(0).getChildNodes();

		MonopolyList mg = parseMonopolies(monopolies);

		board.setMonopolies(mg.toArray());

		NodeList fd = d.getElementsByTagName("Fields");
		if (fd.getLength() != 1)
			throw new BoardCreationException(
					"There has to be exactly one 'Fields'-block");
		Field[] fields = parseFields(fd.item(0).getChildNodes(),
				board.getAbsoluteLength(), board, mg);

		for (int i = 0; i < fields.length; i++) {
			if (i < board.getAbsoluteLength())
				board.setField(i, fields[i]);
		}

		NodeList cr = d.getElementsByTagName("Cards");
		if (cr.getLength() != 1)
			throw new BoardCreationException(
					"There has to be exactly one 'Cards'-block");
		NodeList stacks = cr.item(0).getChildNodes();

		for (int i = 0; i < stacks.getLength(); i++) {
			Node stack = stacks.item(i);
			if (stack.getNodeName().equals("Stack")) {
				String stackName = "";
				Color stackColor = Color.WHITE;
				try {
					stackName = stack.getAttributes().getNamedItem("name")
							.getNodeValue();
					stackColor = parseColor(stack.getAttributes()
							.getNamedItem("color").getNodeValue());
				} catch (Throwable t) {
				}
				Card[] cards = parseCards(stack.getChildNodes());

				CardCollection cardStack = new CardCollection(stackName, cards,
						stackColor);

				board.addStack(stackName, cardStack);
			}
		}

		return board;
	}

	/**
	 * parses xml nodes to a list of monopolies
	 * 
	 * @param monopolies
	 *            the xml nodes containing information about monopolies
	 * @return a {@link MonopolyList} containing the created monopolies
	 */
	private static MonopolyList parseMonopolies(NodeList monopolies) {
		MonopolyList monopo = new MonopolyList();

		for (int i = 0; i < monopolies.getLength(); i++) {
			Node n = monopolies.item(i);
			if (n.getNodeName().equals("Monopoly")) {
				Monopoly mg = new Monopoly();
				mg.setName(n.getAttributes().getNamedItem("name")
						.getNodeValue());
				String color = n.getAttributes().getNamedItem("color")
						.getNodeValue();
				mg.setColor(parseColor(color));
				monopo.add(mg);
			}
		}

		return monopo;
	}

	private static Field[] parseFields(NodeList fields, int nFields,
			Board board, MonopolyList monopolyList) {
		ArrayList<Field> parsed = new ArrayList<Field>();

		for (int i = 0; i < fields.getLength(); i++) {
			Node n = fields.item(i);
			if (n.getNodeName().equals("Field")) {
				Field field = parseField(n, board, monopolyList);
				parsed.add(field);
			}
		}

		Field[] ret = new Field[nFields];
		for (int i = 0; i < ret.length; i++) {
			if (i < parsed.size())
				ret[i] = parsed.get(i);
			else
				ret[i] = new Field("<empty>");
		}
		return ret;
	}

	private static Field parseField(Node n, Board board,
			MonopolyList monopolyList) {
		assert n.getNodeName().equals("Field");
		String type = n.getAttributes().getNamedItem("type").getNodeValue();
		String caption = n.getAttributes().getNamedItem("caption")
				.getNodeValue();

		Field field;

		if (type.equalsIgnoreCase("Start")) {
			field = parseStartField(caption, n);
		} else if (type.equalsIgnoreCase("Jail")) {
			field = new Field.JailField(caption);
		} else if (type.equalsIgnoreCase("FreeParking")) {
			field = new Field.FreeParkingField(caption);
		} else if (type.equalsIgnoreCase("GoToJail")) {
			field = new Field.GoToJailField(caption);
		} else if (type.equalsIgnoreCase("Property")) {
			field = parsePropertyField(caption, n, board, monopolyList);
		} else if (type.equalsIgnoreCase("Railroad")) {
			field = parseRailroadField(caption, n);
		} else if (type.equalsIgnoreCase("Company")) {
			field = parseCompanyField(caption, n);
		} else if (type.equalsIgnoreCase("DrawCard")) {
			field = new DrawCardField(caption);
		} else if (type.equalsIgnoreCase("Tax")) {
			long tax = 0;
			NodeList children = n.getChildNodes();
			for (int i = 0; i < children.getLength(); i++) {
				Node taxNode = children.item(i);
				if (taxNode.getNodeName().equals("Tax")) {
					try {
						tax = Long.parseLong(taxNode.getAttributes()
								.getNamedItem("value").getNodeValue());
					} catch (Throwable t) {
					}
				}
			}
			field = new TaxField(caption, tax);
		} else {
			field = new Field("<undefined>");
		}

		return field;
	}

	private static StartField parseStartField(String caption, Node n) {
		StartField sf = new StartField(caption);
		NodeList children = n.getChildNodes();
		long passMoney = 0;
		long visitMoney = 0;
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			try {
				if (child.getNodeName().equals("PassMoney")) {
					passMoney = Long.parseLong(child.getAttributes()
							.getNamedItem("amount").getNodeValue());
				} else if (child.getNodeName().equals("VisitMoney")) {
					visitMoney = Long.parseLong(child.getAttributes()
							.getNamedItem("amount").getNodeValue());
				}
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}

		sf.setPassMoney(passMoney);
		sf.setVisitMoney(visitMoney);
		return sf;
	}

	private static PropertyField parsePropertyField(String caption, Node n,
			Board board, MonopolyList monopolyList) {
		NodeList children = n.getChildNodes();
		long price = 0;
		long housePrice = 0;
		long mortgageValue = 0;
		long[] rent = null;
		MonopolyGroup group = null;

		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (child.getNodeName().equals("Price")) {
				Node value = child.getAttributes().getNamedItem("value");
				if (value != null) {
					try {
						price = Long.parseLong(value.getNodeValue());
					} catch (Throwable t) {
					}
				}
			} else if (child.getNodeName().equals("Monopoly")) {
				Node name = child.getAttributes().getNamedItem("name");
				if (name != null) {
					group = monopolyList.getMonopolyGroup(name.getNodeValue());
				}
			} else if (child.getNodeName().equals("HousePrice")) {
				Node value = child.getAttributes().getNamedItem("value");
				if (value != null) {
					try {
						housePrice = Long.parseLong(value.getNodeValue());
					} catch (Throwable t) {
					}
				}
			} else if (child.getNodeName().equals("Mortgage")) {
				Node value = child.getAttributes().getNamedItem("value");
				if (value != null) {
					try {
						mortgageValue = Long.parseLong(value.getNodeValue());
					} catch (Throwable t) {
					}
				}
			} else if (child.getNodeName().equals("RentTable")) {
				NodeList rentEntries = child.getChildNodes();
				rent = new long[board.getMaxHouses() + 1];
				for (int j = 0; j < rentEntries.getLength(); j++) {
					Node rentEntry = rentEntries.item(j);
					if (rentEntry.getNodeName().equals("Rent")) {
						Node houses = rentEntry.getAttributes().getNamedItem(
								"houses");
						Node value = rentEntry.getAttributes().getNamedItem(
								"value");
						if (value != null && houses != null) {
							try {
								int index = Integer.parseInt(houses
										.getNodeValue());
								long rentAtIndex = Long.parseLong(value
										.getNodeValue());
								if (rent.length > index) {
									rent[index] = rentAtIndex;
								}
							} catch (Throwable t) {
							}
						}
					}
				}
			}
		}

		PropertyField pf = new PropertyField(caption, price, group, board);
		pf.setHousePrice(housePrice);
		pf.setMortgageValue(mortgageValue);

		if (rent != null) {
			for (int i = 0; i < rent.length; i++) {
				pf.setRent(i, rent[i]);
			}
		}

		return pf;
	}

	private static RailroadField parseRailroadField(String caption, Node node) {
		long price = 0;
		long mortgageValue = 0;

		NodeList children = node.getChildNodes();

		Map<Integer, Long> rent = new HashMap<Integer, Long>();
		int rentMaxRailroads = 0;

		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (child.getNodeName().equals("Price")) {
				Node value = child.getAttributes().getNamedItem("value");
				if (value != null) {
					try {
						price = Long.parseLong(value.getNodeValue());
					} catch (Throwable t) {
					}
				}
			} else if (child.getNodeName().equals("Mortgage")) {
				Node value = child.getAttributes().getNamedItem("value");
				if (value != null) {
					try {
						mortgageValue = Long.parseLong(value.getNodeValue());
					} catch (Throwable t) {
					}
				}
			} else if (child.getNodeName().equals("RentTable")) {
				NodeList rentEntries = child.getChildNodes();

				for (int j = 0; j < rentEntries.getLength(); j++) {
					Node rentEntry = rentEntries.item(j);
					if (rentEntry.getNodeName().equals("Rent")) {
						Node railroads = rentEntry.getAttributes()
								.getNamedItem("railroads");
						Node value = rentEntry.getAttributes().getNamedItem(
								"value");
						if (value != null && railroads != null) {
							try {
								int index = Integer.parseInt(railroads
										.getNodeValue());
								long rentAtIndex = Long.parseLong(value
										.getNodeValue());
								rent.put(index, rentAtIndex);
								rentMaxRailroads = Math.max(index,
										rentMaxRailroads);
							} catch (Throwable t) {
							}
						}
					}
				}
			}
		}

		RailroadField rf = new RailroadField(caption, price, rentMaxRailroads);
		rf.setMortgageValue(mortgageValue);
		for (int i = 1; rent.containsKey(i) && i <= rentMaxRailroads; i++) {
			rf.setRent(i, rent.get(i));
		}

		return rf;
	}

	private static CompanyField parseCompanyField(String caption, Node node) {
		long price = 0;
		long mortgageValue = 0;

		NodeList children = node.getChildNodes();

		Map<Integer, Long> rent = new HashMap<Integer, Long>();
		int rentMaxCompanies = 0;

		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (child.getNodeName().equals("Price")) {
				Node value = child.getAttributes().getNamedItem("value");
				if (value != null) {
					try {
						price = Long.parseLong(value.getNodeValue());
					} catch (Throwable t) {
					}
				}
			} else if (child.getNodeName().equals("Mortgage")) {
				Node value = child.getAttributes().getNamedItem("value");
				if (value != null) {
					try {
						mortgageValue = Long.parseLong(value.getNodeValue());
					} catch (Throwable t) {
					}
				}
			} else if (child.getNodeName().equals("RentTable")) {
				NodeList rentEntries = child.getChildNodes();

				for (int j = 0; j < rentEntries.getLength(); j++) {
					Node rentEntry = rentEntries.item(j);
					if (rentEntry.getNodeName().equals("Rent")) {
						Node companies = rentEntry.getAttributes()
								.getNamedItem("companies");
						Node multiplicator = rentEntry.getAttributes()
								.getNamedItem("multiplicator");
						if (multiplicator != null && companies != null) {
							try {
								int index = Integer.parseInt(companies
										.getNodeValue());
								long rentAtIndex = Long.parseLong(multiplicator
										.getNodeValue());
								rent.put(index, rentAtIndex);
								rentMaxCompanies = Math.max(index,
										rentMaxCompanies);
							} catch (Throwable t) {
							}
						}
					}
				}
			}
		}

		CompanyField cf = new CompanyField(caption, price, rentMaxCompanies);
		cf.setMortgageValue(mortgageValue);
		for (int i = 1; rent.containsKey(i) && i <= rentMaxCompanies; i++) {
			cf.setRentMultiplicator(i, rent.get(i));
		}

		return cf;
	}

	/**
	 * parses action cards
	 * 
	 * @param cards
	 *            the list with xml nodes
	 * @return an array containing the generated cards
	 */
	private static Card[] parseCards(NodeList cards) {
		ArrayList<Card> cardList = new ArrayList<Card>();
		for (int i = 0; i < cards.getLength(); i++) {
			Node card = cards.item(i);
			if (card.getNodeName().equals("Card")) {

				Card c = parseCard(card);

				if (c != null)
					cardList.add(c);
			}
		}
		Card[] ret = new Card[cardList.size()];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = cardList.get(i);
		}
		return ret;
	}

	private static Card parseCard(Node cardNode) {
		String type = "";
		try {
			type = cardNode.getAttributes().getNamedItem("type").getNodeValue();
		} catch (Throwable t) {
		}

		Card card = null;

		// decide type
		try {
			if (type.equalsIgnoreCase("AdvanceTo")) { // AdvanceTo
				int position = Integer.parseInt(cardNode.getAttributes()
						.getNamedItem("fieldIndex").getNodeValue());
				card = new Card.AdvanceToCard(position);
			} else if (type.equalsIgnoreCase("BackTo")) { // BackTo
				int position = Integer.parseInt(cardNode.getAttributes()
						.getNamedItem("fieldIndex").getNodeValue());
				card = new Card.AdvanceToCard(position, false);
			} else if (type.equalsIgnoreCase("AdvanceToUtility")) { // AdvanceToUtility
				long multiplier = Long.parseLong(cardNode.getAttributes()
						.getNamedItem("multiplicator").getNodeValue());
				card = new Card.AdvanceToUtilityCard(multiplier);
			} else if (type.equalsIgnoreCase("AdvanceToRailroad")) { // AdvanceToRailroad
				long multiplier = Long.parseLong(cardNode.getAttributes()
						.getNamedItem("multiplicator").getNodeValue());
				card = new Card.AdvanceToRailroadCard(multiplier);
			} else if (type.equalsIgnoreCase("GetMoney")) { // GetMoney
				long arg = Long.parseLong(cardNode.getAttributes()
						.getNamedItem("value").getNodeValue());
				card = new Card.GetMoneyCard(arg);
			} else if (type.equalsIgnoreCase("PayMoney")) { // PayMoney
				long arg = Long.parseLong(cardNode.getAttributes()
						.getNamedItem("value").getNodeValue());
				card = new Card.GetMoneyCard(-arg);
			} else if (type.equalsIgnoreCase("GetMoneyPerPlayer")) { // GetMoneyPerPlayer
				long money = Long.parseLong(cardNode.getAttributes()
						.getNamedItem("value").getNodeValue());
				card = new Card.GetMoneyPerPlayerCard(money);
			} else if (type.equalsIgnoreCase("PayMoneyPerPlayer")) { // PayMoneyPerPlayer
				long money = Long.parseLong(cardNode.getAttributes()
						.getNamedItem("value").getNodeValue());
				card = new Card.GetMoneyPerPlayerCard(-money);
			} else if (type.equalsIgnoreCase("GetOutOfJail")) { // GetOutOfJail
				card = new Card.GetOutOfJailCard();
			} else if (type.equalsIgnoreCase("GoToJail")) { // GoToJail
				card = new Card.GoToJailCard();
			} else if (type.equalsIgnoreCase("PayPerHouse")) { // PayPerHouse
				long perHouse = Long.parseLong(cardNode.getAttributes()
						.getNamedItem("houses").getNodeValue());
				long perHotel = Long.parseLong(cardNode.getAttributes()
						.getNamedItem("hotels").getNodeValue());
				card = new Card.PayPerHouseCard(perHouse, perHotel);

			} else if (type.equalsIgnoreCase("GoRelative")) { // GoRelative
				int value = Integer.parseInt(cardNode.getAttributes()
						.getNamedItem("value").getNodeValue());
				card = new Card.GoRelativeCard(value);
			} else {
				card = new Card();
			}
		} catch (Throwable t) {
		}

		NodeList children = cardNode.getChildNodes();
		String caption = "";
		for (int i = 0; i < children.getLength(); i++) {
			if (children.item(i).getNodeType() == Node.TEXT_NODE) {
				String[] cap = children.item(i).getNodeValue().split("\\n");
				for (int j = 0; j < cap.length; j++) {
					if (!cap[j].isEmpty())
						caption += cap[j].trim() + " ";
				}
			}
		}

		if (card != null)
			card.setText(caption.trim());
		
		return card;
	}

	private static Color parseColor(String hex) {
		try {
			if (hex.startsWith("#")) {
				hex = hex.substring(1);
				return new Color(Integer.parseInt(hex, 16));
			} else {
				return new Color(Integer.parseInt(hex, 16));
			}
		} catch (NumberFormatException nfe) {
			return Color.WHITE;
		}
	}

	/**
	 * locally used data structure to hold information about a monopoly group
	 * 
	 * @author Nicolas Winkler
	 * 
	 */
	private static class Monopoly {
		private String name;
		private MonopolyGroup monopolyGroup;

		public Monopoly() {
			monopolyGroup = new MonopolyGroup();
		}

		public void setColor(Color c) {
			monopolyGroup.setColor(c);
		}

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		/**
		 * @param name
		 *            the name to set
		 */
		public void setName(String name) {
			this.name = name;
		}

		/**
		 * @return the monopolyGroup
		 */
		public MonopolyGroup getMonopolyGroup() {
			return monopolyGroup;
		}
	}

	/**
	 * locally used data structure to find monopoly groups by name
	 * 
	 * @author Nicolas Winkler
	 * 
	 */
	private static class MonopolyList extends ArrayList<Monopoly> {
		/**
		 * 
		 */
		private static final long serialVersionUID = 8408988503244772821L;

		public MonopolyGroup getMonopolyGroup(String name) {
			for (int i = 0; i < size(); i++) {
				if (get(i).getName().equals(name))
					return get(i).getMonopolyGroup();
			}
			return null;
		}

		public MonopolyGroup[] toArray() {
			MonopolyGroup[] ret = new MonopolyGroup[size()];
			for (int i = 0; i < ret.length; i++) {
				ret[i] = get(i).getMonopolyGroup();
			}
			return ret;
		}
	}

	/**
	 * exception raised when there is an error creating a board
	 * 
	 * @author Nicolas Winkler
	 * 
	 */
	public static class BoardCreationException extends RuntimeException {
		/**
		 * 
		 */
		private static final long serialVersionUID = -3855877232849966923L;

		public BoardCreationException(String errMsg) {
			super(errMsg);
		}
	}
}
