package zeditor.windows;

import java.awt.Dimension;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import zeditor.windows.managers.ExplorerFrameManager;
import zildo.resource.Constantes;

/**
 * This code was edited or generated using CloudGarden's Jigloo SWT/Swing GUI Builder, which is free for non-commercial
 * use. If Jigloo is being used commercially (ie, by a corporation, company or business for any purpose whatever) then
 * you should purchase a license for each developer using Jigloo. Please visit www.cloudgarden.com for details. Use of
 * Jigloo implies acceptance of these licensing terms. A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR THIS MACHINE, SO
 * JIGLOO OR THIS CODE CANNOT BE USED LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
 */
public class ExplorerFrame {

	JFileChooser fileChooser;

	{
		// Set Look & Feel
		try {
			// javax.swing.UIManager.setLookAndFeel("com.jgoodies.looks.plastic.Plastic3DLookAndFeel");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private ExplorerFrameManager manager;

	public ExplorerFrame(MasterFrame frame, int mode) {
		int status;
		// Recréation du manager avec les objets en paramètre
		fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new FileNameExtensionFilter(
				"Cartes Zildo", "MAP"));
		fileChooser.setCurrentDirectory(new File(Constantes.DATA_PATH
				+ Constantes.MAP_PATH));
		fileChooser.setAcceptAllFileFilterUsed(false);

		manager = new ExplorerFrameManager(frame, fileChooser);
		fileChooser.setPreferredSize(new Dimension(600, 600));
		if (mode == ExplorerFrameManager.OPEN) {
			status = fileChooser.showOpenDialog(frame);
			if (status == JFileChooser.APPROVE_OPTION) {
				manager.open();
			}
		} else {
			status = fileChooser.showSaveDialog(frame);
			if (status == JFileChooser.APPROVE_OPTION) {
				manager.save();
			}
		}
	}
}
