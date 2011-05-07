package zeditor.windows;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import zeditor.windows.managers.ExplorerFrameManager;
import zildo.prefs.Constantes;

/**
* This code was edited or generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a corporation,
* company or business for any purpose whatever) then you
* should purchase a license for each developer using Jigloo.
* Please visit www.cloudgarden.com for details.
* Use of Jigloo implies acceptance of these licensing terms.
* A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
* THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
* LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
*/
public class ExplorerFrame extends javax.swing.JDialog {

	{
		//Set Look & Feel
		try {
			//javax.swing.UIManager.setLookAndFeel("com.jgoodies.looks.plastic.Plastic3DLookAndFeel");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private static final long serialVersionUID = 1621360929929363337L;
	private ExplorerFrameManager manager;
	private JButton actionButton;
	private JButton cancelButton;
	private JPanel buttonPanel;
	private JPanel controlPanel;
	private JFileChooser fileChooser;
	private AbstractAction actiondoAction;
	private AbstractAction actionCancel;
	//private String mode;
	//private String title;

	//private ExplorerFrameManager manager;

	/**
	* Auto-generated main method to display this JDialog
	*/
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFrame frame = new JFrame();
				ExplorerFrame inst = new ExplorerFrame(frame, -1);
				inst.setVisible(true);
			}
		});
	}
	
	public ExplorerFrame(JFrame frame, int mode) {
		super(frame);
		initGUI();
		manager.init(mode);
	}
	
	private void initGUI() {
		try {
			{
				manager = getManager();
				this.setTitle("Explorateur de fichiers");
				getContentPane().add(getFileChooser(), BorderLayout.CENTER);
				getContentPane().add(getControlPanel(), BorderLayout.SOUTH);
			}

			// Recréation du manager avec les objets en paramètre
			manager = new ExplorerFrameManager(this, actionButton, getFileChooser());
			
			this.setSize(600, 400);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private ExplorerFrameManager getManager() {
		if(manager == null) {
			manager = new ExplorerFrameManager();
		}
		return manager;
	}

	private AbstractAction getActionCancel() {
		if(actionCancel == null) {
			actionCancel = new AbstractAction("Annuler", null) {
				private static final long serialVersionUID = -707801490025709066L;

				public void actionPerformed(ActionEvent evt) {
					manager.close();
				}
			};
		}
		return actionCancel;
	}

	private AbstractAction getActiondoAction() {
		if(actiondoAction == null) {
			actiondoAction = new AbstractAction("ACTION", null) {
				private static final long serialVersionUID = -773821287619059041L;

				public void actionPerformed(ActionEvent evt) {
					manager.doAction();
				}
			};
		}
		return actiondoAction;
	}
	private JFileChooser getFileChooser() {
		if (fileChooser == null) {
			fileChooser = new JFileChooser();
			fileChooser.setFileFilter(new FileNameExtensionFilter("Cartes Zildo","MAP"));
			fileChooser.setCurrentDirectory(new File(Constantes.DATA_PATH+Constantes.MAP_PATH));
			fileChooser.setControlButtonsAreShown(false);
		}
		return fileChooser;
	}
	private JPanel getControlPanel() {
		if (controlPanel == null) {
			controlPanel = new JPanel();
			BoxLayout controlPanelLayout = new BoxLayout(controlPanel, javax.swing.BoxLayout.X_AXIS);
			controlPanel.setLayout(controlPanelLayout);
			controlPanel.add(getButtonPanel());
		}
		return controlPanel;
	}
	private JPanel getButtonPanel() {
		if (buttonPanel == null) {
			buttonPanel = new JPanel();
			FlowLayout buttonPanelLayout = new FlowLayout();
			buttonPanel.setLayout(buttonPanelLayout);
			buttonPanel.add(getCancelButton());
			buttonPanel.add(getActionButton());
		}
		return buttonPanel;
	}
	private JButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new JButton();
			cancelButton.setText("Annuler");
			cancelButton.setAction(getActionCancel());
		}
		return cancelButton;
	}
	private JButton getActionButton() {
		if (actionButton == null) {
			actionButton = new JButton();
			actionButton.setText("ACTION");
			actionButton.setAction(getActiondoAction());
		}
		return actionButton;
	}
}
