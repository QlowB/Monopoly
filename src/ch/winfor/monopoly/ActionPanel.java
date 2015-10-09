package ch.winfor.monopoly;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JPanel;

/**
 * panel that accepts {@link ActionListener}s via the method
 * {@link ActionPanel#addActionListener(ActionListener)}
 * 
 * @author Nicolas Winkler
 * 
 */
public class ActionPanel extends JPanel {
	/**  */
	private static final long serialVersionUID = -4630990014858459318L;

	/** list with listeners */
	protected ArrayList<ActionListener> actionListeners;

	/**
	 * initialize the listener list
	 */
	public ActionPanel() {
		actionListeners = new ArrayList<ActionListener>();
	}

	/**
	 * adds a subscriber to this entry
	 * 
	 * @param al
	 *            the subscriber
	 */
	public void addActionListener(ActionListener al) {
		actionListeners.add(al);
	}

	/**
	 * removes a subscriber from this entry
	 * 
	 * @param al
	 *            the subscriber
	 */
	public boolean removeActionListener(ActionListener al) {
		return actionListeners.remove(al);
	}

	/**
	 * notifies all subscribers that something happened
	 * 
	 * @param parameter
	 *            the string parameter to the action event
	 */
	protected void fireActionEvent(String parameter) {
		for (ActionListener al : actionListeners) {
			ActionEvent ae = new ActionEvent(this, 0, parameter);
			al.actionPerformed(ae);
		}
	}

	/**
	 * notifies all subscribers that something happened
	 * 
	 * @param param1
	 *            the {@code int} parameter to the action event
	 * @param param2
	 *            the {@link String} parameter to the action event
	 */
	protected void fireActionEvent(int param1, String param2) {
		for (ActionListener al : actionListeners) {
			ActionEvent ae = new ActionEvent(this, param1, param2);
			al.actionPerformed(ae);
		}
	}
}
