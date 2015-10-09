package ch.winfor.monopoly;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;

import javax.swing.JButton;
import javax.swing.JPanel;

import ch.winfor.monopoly.Language.LanguageListener;

public class LoadingPanel extends ActionPanel implements LanguageListener,
		Freeable {
	/** */
	private static final long serialVersionUID = 352382309232480969L;

	/** the angle of the rotating thing */
	private double angle;

	/** angle for sinus argument responsible for pulsating look */
	private double angleSinValue;

	/** thread that rotates the stuff */
	private Rotater rotater;

	/** the text displayed at the bottom */
	private String text;

	/** number of characters of {@link #text} displayed currently */
	private int characters;

	/** panel containing the cancel button */
	private JPanel cancelPanel;

	/** cancel button */
	private JButton btnCancel;

	public LoadingPanel(String text) {
		this.text = text;
		setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		setLayout(new BorderLayout(0, 0));

		cancelPanel = new JPanel();
		add(cancelPanel, BorderLayout.SOUTH);
		cancelPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));

		btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				fireActionEvent("cancel");
			}
		});
		cancelPanel.add(btnCancel);

		btnCancel.setAlignmentX(Component.CENTER_ALIGNMENT);
		btnCancel.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		characters = 0;
		rotater = new Rotater();
		rotater.setRotateIncrement(0.11 * 0.7);
		rotater.setAngleSinIncrement(0.08 * 0.7);

		Language lang = Language.getInstance();
		lang.addLanguageListener(this);
		languageChanged(lang);
	}

	@Override
	public void languageChanged(Language sender) {
		btnCancel.setText(sender.get("cancel"));
	}

	@Override
	public void free() {
		Language lang = Language.getInstance();
		lang.removeLanguageListener(this);
	}

	public void activate() {
		rotater.start();
	}

	public void deactivate() {
		rotater.stop();
	}

	public void setVisible(boolean aFlag) {
		super.setVisible(aFlag);
		if (aFlag)
			activate();
		else
			deactivate();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;

		int drawWidth = getWidth();
		int drawHeight = getHeight() - 40 - cancelPanel.getHeight();

		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		int angleFill = (int) (300.0 + Math.sin(angleSinValue) * 50.0);

		AffineTransform at = g2d.getTransform();
		g2d.rotate(angle - (angleFill * Math.PI / 180) / 2, drawWidth * 0.5,
				drawHeight * 0.5);
		g2d.translate(drawWidth * 0.5, drawHeight * 0.5);
		g2d.setColor(Color.BLUE.darker());
		int wh = Math.min(drawWidth, drawHeight);
		wh = (int) (wh * 0.8);

		g2d.fillArc(-wh / 2, -wh / 2, wh, wh, 0, angleFill);

		g2d.setTransform(at);

		g2d.setColor(Color.BLACK);

		FontMetrics fm = g2d.getFontMetrics();
		int width = fm.stringWidth(text);
		g2d.drawString(text.substring(0, Math.min(characters, text.length())),
				(getWidth() - width) / 2, getHeight() - 35);

	}

	private class Rotater implements Runnable {
		private volatile boolean shouldRun;
		private volatile boolean isRunning;

		private double rotateIncrement;
		private double angleSinIncrement;

		public void start() {
			shouldRun = true;
			if (!isRunning) {
				isRunning = true;
				new Thread(this).start();
			}
		}

		/**
		 * @return the value by which the angle is incremented
		 */
		public double getRotateIncrement() {
			return rotateIncrement;
		}

		/**
		 * @param rotateIncrement
		 *            the the value by which the angle is incremented to set
		 */
		public void setRotateIncrement(double rotateIncrement) {
			this.rotateIncrement = rotateIncrement;
		}

		/**
		 * @return the angleSinIncrement
		 */
		public double getAngleSinIncrement() {
			return angleSinIncrement;
		}

		/**
		 * @param angleSinIncrement
		 *            the angleSinIncrement to set
		 */
		public void setAngleSinIncrement(double angleSinIncrement) {
			this.angleSinIncrement = angleSinIncrement;
		}

		public void stop() {
			shouldRun = false;
		}

		@Override
		public void run() {
			int nRuns = 0;
			while (shouldRun) {

				if ((nRuns % 20) == 0) {
					characters++;
					characters %= text.length() + 10;
				}

				angle += getRotateIncrement();
				angleSinValue += getAngleSinIncrement();
				repaint();
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				nRuns++;
			}
			isRunning = false;
		}
	}
}
