package zeditor.windows.managers;

import java.awt.Color;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import zeditor.core.Options;
import zeditor.core.TileSet;
import zeditor.core.exceptions.ZeditorException;
import zeditor.helpers.OptionHelper;
import zeditor.windows.ExplorerFrame;
import zeditor.windows.OptionsFrame;
import zildo.fwk.awt.ZildoCanvas;

/**
 * Classe de management de la fenêtre principale de Zeditor (MasterFrame.class)
 * @author Drakulo
 */
public class MasterFrameManager {
	private JLabel systemDisplay;
	private TileSet tileSet;
	private JPanel masterPanel;
	private JFrame masterFrame;
	private JComboBox backgroundCombo;
	
	ZildoCanvas zildoCanvas;
	
	public final static int MESSAGE_ERROR = 1;
	public final static int MESSAGE_INFO = 2;
	
	/**
	 * Constructeur vide
	 * @author Drakulo
	 */
	public MasterFrameManager(){}
	
	/**
	 * Constructeur avec une liste d'objets correspondant aux différents objets de la MasterFrame
	 * @param p_sys Le JLabel Système de la MasterFrame
	 * @author Drakulo
	 */
	public MasterFrameManager(JLabel p_sys, TileSet p_tile, JPanel p_master, JFrame p_frame, JComboBox p_backgroundCombo, ZildoCanvas p_zildoCanvas){
		systemDisplay = p_sys;
		tileSet = p_tile;
		masterPanel = p_master;
		masterFrame = p_frame;
		backgroundCombo = p_backgroundCombo;
		zildoCanvas = p_zildoCanvas;
	}
	
	/**
	 * Ferme la fenêtre de Zeditor
	 * @author Drakulo
	 */
	public void exit(){
		// TODO Ajouter un test de vérification s'il y a eu une modification et demander une sauvegarde le cas échéant.
		System.exit(0);
	}

	/**
	 * Sauve la carte en cours dans la carte en cours. Si la carte en cours n'a pas encore été sauvegardée, on appelle 
	 * la méthode {@link MasterPanelManager.saveAs saveAs()}
	 * @author Drakulo
	 */
	public void save(){
		display("[A FAIRE] Sauvegarde effectuée.", MESSAGE_ERROR);
		// TODO
	}
	
	/**
	 * Ouvre l'explorateur afin de sélectionner le nom du fichier à sauvegarder pui lance (ou annule) la sauvegarde
	 */
	public void saveAs(){
		display("[A FAIRE] Enregistrer sous...", MESSAGE_ERROR);
		openFileExplorer(ExplorerFrameManager.SAVE);
		// TODO
	}
	
	/**
	 * Charge une nouvelle carte
	 * @author Drakulo
	 */
	public void load(){
		openFileExplorer(ExplorerFrameManager.OPEN);
		// TODO

	}
	
	public void loadMap(String p_mapName) {
		display("Ouverture du fichier : " +p_mapName, MasterFrameManager.MESSAGE_INFO);
		zildoCanvas.loadMap(p_mapName);	
		display("[A FAIRE] Chargement effectué.", MESSAGE_ERROR);
	}
	
	/**
	 * Crée une nouvelle carte
	 * @author Drakulo
	 */
	public void create(){
		// TODO
		changeTitle("Carte sans nom");
		display("[A FAIRE] Nouvelle carte", MESSAGE_ERROR);
	}
	
	/**
	 * Charge la liste des TileSets pour la combo de décors
	 * @return Un tableau de String des titres des TileSets 
	 * @author Drakulo
	 */
	public Object[] loadTileForCombo(){
		try{
			return TileSet.getTiles();
		}catch(ZeditorException e){
			display(e.getMessage(), MESSAGE_ERROR);
			return new Object[]{""};
		}
	}
	
	/**
	 * Charge le tileSet dont le nom est passé en paramètres
	 * @param name
	 * @author Drakulo
	 */
	public void changeTileSet(String p_name){
		try{
			tileSet.changeTile(p_name);
			display("TileSet '" + p_name + "' chargé.", MESSAGE_INFO);
		}catch(ZeditorException e){
			display(e.getMessage(), MESSAGE_ERROR);
		}
	}
	
	/**
	 * Affiche un message dans le label Système
	 * @param p_msg est le message à afficher
	 * @param p_type est le type de message
	 * @author Drakulo
	 */
	public void display(String p_msg, int p_type){
		if(systemDisplay != null){
			systemDisplay.setText(" " + p_msg);
			switch(p_type){
				case MESSAGE_ERROR :
					systemDisplay.setForeground(Color.red);
					break;
				case MESSAGE_INFO :
				default :
					systemDisplay.setForeground(Color.black);
					break;
			}
		}
	}
	
	/**
	 * Ouvre la fenêtre de paramétrage des options
	 * @author Drakulo
	 */
	public void openOptionsFrame(){
		OptionsFrame optFrame = new OptionsFrame();
		optFrame.setLocationRelativeTo(masterFrame);
		optFrame.setVisible(true);
		optFrame.addWindowListener(new WindowListener(){
			public void windowActivated(WindowEvent arg0) {}
			public void windowClosed(WindowEvent arg0) {}
			public void windowClosing(WindowEvent arg0) {}
			public void windowDeactivated(WindowEvent arg0) {
				updateTools();
				masterPanel.repaint();
			}
			public void windowDeiconified(WindowEvent arg0) {}
			public void windowIconified(WindowEvent arg0) {}
			public void windowOpened(WindowEvent arg0) {}
			
		});
	}
	
	/**
	 * Recharge les "petites" configurations
	 * @author Drakulo
	 */
	public void reloadConfig(){
		updateTools();
		masterPanel.repaint();
		display("Petite configuration rechargée.", MESSAGE_INFO);
	}
	
	/**
	 * Changement d'une option avec sauvegarde
	 * @param p_option : Entrée de l'énumération Options
	 * @param p_value : Valeur à attribuer
	 * @author Drakulo
	 */
	public void saveOption(String p_option, String p_value){
		OptionHelper.saveOption(p_option, p_value);
		masterPanel.repaint();
	}
	
	/**
	 * Chargement d'une option
	 * @param p_option : Entrée de l'énumération Options
	 * @return La valeur paramétrée de l'option
	 * @author Drakulo
	 */
	public String loadOption(String p_option){
		return OptionHelper.loadOption(p_option);
	}
	
	/**
	 * Met à jours les boutons de la ToolBar. Cette méthode est dépendante de la structure de la fenêtre.
	 * MasterPanel >> ToolbarContainer >> ToolBar
	 * @author Drakulo
	 */
	public void updateTools(){
		JToolBar toolBar = (JToolBar) ((JPanel) masterPanel.getComponent(0)).getComponent(0) ;
		
		// Bouton des tuiles non mappées
		JToggleButton unmapped = (JToggleButton) toolBar.getComponent(0);
		if(Boolean.valueOf(OptionHelper.loadOption(Options.SHOW_TILES_UNMAPPED.getValue()))){
			unmapped.setSelected(true);
		}else{
			unmapped.setSelected(false);
		}
		
		// Bouton d'affichage de la grille
		JToggleButton grid = (JToggleButton) toolBar.getComponent(1);
		if(Boolean.valueOf(OptionHelper.loadOption(Options.SHOW_TILES_GRID.getValue()))){
			grid.setSelected(true);
		}else{
			grid.setSelected(false);
		}
	}
	
	/**
	 * Change le titre de la fenêtre Zildo avec le texte : "Zeditor - [TITLE]"
	 * @param title
	 * @author Drakulo
	 */
	public void changeTitle(String title){
		masterFrame.setTitle("Zeditor - " + title);
	}
	
	/**
	 * Méthode de test pour afficher le numéro des tuiles sélectionnées
	 * @author Drakulo
	 */
	public void displaySelectedTiles(){
		if(tileSet.getCurrentSelection() != null){
			display(tileSet.getCurrentSelection().toString(), MESSAGE_INFO);
		}
	}
	
	/**
	 * Affiche ou masque la grille sur le TileSet suivant le paramètre
	 * @param flag true : afficher, false : masquer
	 * @author Drakulo
	 */
	public void showTileSetGrid(boolean flag){
		saveOption(Options.SHOW_TILES_GRID.getValue(), String.valueOf(flag));
		if(flag){
			display("Grille affichée.", MESSAGE_INFO);
		}else{
			display("Grille masquée.", MESSAGE_INFO);
		}
	}
	
	/**
	 * Affiche ou masque les tuiles non mappées sur le TileSet suivant le paramètre
	 * @param flag true : afficher, false : masquer
	 * @author Drakulo
	 */
	public void showTileSetUnmapped(boolean flag){
		saveOption(Options.SHOW_TILES_UNMAPPED.getValue(), String.valueOf(flag));
		if(flag){
			display("Tuiles non mappées mises en évidence.", MESSAGE_INFO);
		}else{
			display("Tuiles non mappées ignorées.", MESSAGE_INFO);
		}
	}
	
	/**
	 * Ouvre l'explorateur de fichier avec les paramètres
	 * @param mode est les mode (ouverture / sauvegarde) : 
	 * <p>{@link ExplorerFrameManager.OPEN} / {@link ExplorerFrameManager.SAVE}</p>
	 */
	public void openFileExplorer(int mode){
		ExplorerFrame explorer = new ExplorerFrame(masterFrame, mode);
		explorer.setLocationRelativeTo(masterFrame);
		explorer.setVisible(true);
	}
	/**
	 * Initialisation de la fenêtre
	 */
	public void init(){
		updateTools();
		changeTileSet(backgroundCombo.getSelectedItem().toString());
	}
}
