package ch.winfor.monopoly;

import java.awt.EventQueue;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import ch.winfor.monopoly.res.Ressources;

/**
 * main class of the application
 * 
 * @author Nicolas Winkler
 * 
 */
public class Monopoly {

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			String className = UIManager.getSystemLookAndFeelClassName();
			if (className.equals(UIManager
					.getCrossPlatformLookAndFeelClassName())) {
				LookAndFeelInfo[] lafi = UIManager.getInstalledLookAndFeels();
				UIManager.setLookAndFeel(lafi[1].getClassName());

				for (LookAndFeelInfo lf : lafi) {
					if (lf.getName().equals("Nimbus"))
						className = lf.getClassName();
				}
			}

			UIManager.setLookAndFeel(className);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Language language = Language.getInstance();
		language.loadLanguage(Ressources.getRessource("english.lng"));

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainMenuFrame window = new MainMenuFrame();
					window.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
