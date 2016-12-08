import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch(Exception e) {
			System.err.println("No system look and feel found");
		}
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new Optimus();
			}
		});
	}

}
