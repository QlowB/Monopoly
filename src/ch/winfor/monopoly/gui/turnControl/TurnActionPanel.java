package ch.winfor.monopoly.gui.turnControl;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import ch.winfor.monopoly.game.Game;

/**
 * base class for panels that can fire action events
 * 
 * These panels can be used to do a part of a turn and firing an action event
 * when finished.
 * 
 * @author Nicolas Winkler
 * 
 */
public class TurnActionPanel extends JPanel {
    /** */
    private static final long serialVersionUID = -5439262235010875147L;

    /** the color of the border of this panel */
    private static final Color BORDER_COLOR = new Color(170, 170, 255);

    /** the listeners */
    private ArrayList<ActionListener> actionListeners;

    /** reference to the game */
    protected Game game;

    /**
     * initializes the component
     * 
     * @param game
     */
    public TurnActionPanel(Game game) {
        this.game = game;
        actionListeners = new ArrayList<ActionListener>();
        this.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 2));
    }

    /**
     * fires an {@link ActionEvent} to every listener added to this component
     * 
     * @param ae
     *            the event to fire
     */
    protected void fireActionEvent(ActionEvent ae) {
        for (int i = 0; i < actionListeners.size(); i++) {
            actionListeners.get(i).actionPerformed(ae);
        }
    }

    /**
     * adds a new {@link ActionListener} that will be notified upon action of
     * this component
     * 
     * @param l
     *            the new {@link ActionListener}
     */
    public void addActionListener(ActionListener l) {
        actionListeners.add(l);
    }

    /**
     * removes an already added {@link ActionListener}
     * 
     * @param l
     *            the {@link ActionListener} to remove
     */
    public void removeActionListener(ActionListener l) {
        actionListeners.remove(l);
    }

    /**
     * recreates the panel and renews its contents
     */
    public void refresh() {
    }
}
