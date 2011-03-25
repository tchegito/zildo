package zeditor.windows.managers;

import java.io.File;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;

import zeditor.windows.MasterFrame;

public class ExplorerFrameManager {
	public static final int SAVE = 0;
	public static final int OPEN = 1;
	
	private JDialog masterFrame;
	private JButton actionButton;
	private JFileChooser fileChooser;
	private MasterFrame parent;
	private int mode;
	
	/**
	 * Constructeur vide
	 */
	public ExplorerFrameManager(){}
	
	/**
	 * Constructeur avec paramètres
	 * @param p_frame
	 */
	public ExplorerFrameManager(JDialog p_frame, JButton p_actionButton, JFileChooser p_fileChooser) {
		masterFrame = p_frame;
		actionButton = p_actionButton;
		fileChooser = p_fileChooser;

		parent = (MasterFrame)masterFrame.getOwner();
	}

	/**
	 * Ferme la fenêtre
	 */
	public void close(){
		masterFrame.dispose();
	}
	
	/**
	 * Initialisation de certains champs en fonction du mode d'ouverture (OPEN ou SAVE)
	 * @param mode est le mode d'ouverture de la fenêtre
	 */
	public void init(int mode){
		this.mode = mode;
		if(mode == SAVE){
			masterFrame.setTitle("Enregistrer sous...");
			actionButton.setText("Enregistrer");
			
		}else if(mode == OPEN){
			masterFrame.setTitle("Ouvrir...");
			actionButton.setText("Ouvrir");
		}
	}
	
	/**
	 * Exécute l'action en fonction du mode d'ouverture : sauvegarde ou ouverture
	 */
	public void doAction(){
		if(mode == SAVE){
			save();
		}else if(mode == OPEN){
			open();
		}
	}

	/**
	 * Ouvre la carte sélectionnée
	 */
	private void open(){
		File f = fileChooser.getSelectedFile();
		// On teste la nature du fichier (si c'est un *.MAP)
		if(!f.getName().endsWith(".map")){
			// TODO Fenêtre d'erreur
			return;
		}
		String relativeFilename=f.getAbsolutePath().replace(zildo.prefs.Constantes.DATA_PATH, "");
		
		parent.getManager().loadMap(relativeFilename, null);
		close();

	}
	
	/**
	 * Sauvegarde la carte en cours dans le fichier sélectionné
	 */
	private void save(){
		if(fileChooser.getSelectedFile() == null){
			return;
		}
		//File f = new File(fileChooser.getSelectedFile().getAbsolutePath());

		parent.getManager().saveAs(fileChooser.getSelectedFile().getName());
		
		close();
	}
}
