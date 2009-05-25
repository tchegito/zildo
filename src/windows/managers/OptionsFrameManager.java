package zeditor.windows.managers;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JTextField;

import zeditor.core.Options;
import zeditor.helpers.OptionHelper;

/**
 * Manager de la fenêtre OptionsFrame, la fenêtre d'options de Zeditor
 * @author Drakulo
 *
 */
public class OptionsFrameManager {
	private JFrame masterFrame;
	private JTextField tilesPath;
	private JCheckBox unmappedCheckBox;
	private JCheckBox gridCheckBox;
	
	/**
	 * Cionstructeur vide
	 */
	public OptionsFrameManager(){}
	
	/**
	 * Constructeur complet
	 * @param p_masterFrame
	 * @param p_tilesPath
	 * @param p_unmappedCheckBox
	 * @param p_gridCheckBox
	 */
	public OptionsFrameManager(JFrame p_masterFrame, JTextField p_tilesPath, JCheckBox p_unmappedCheckBox, JCheckBox p_gridCheckBox){
		masterFrame = p_masterFrame;
		tilesPath = p_tilesPath;
		unmappedCheckBox = p_unmappedCheckBox;
		gridCheckBox = p_gridCheckBox;
	}
	
	/**
	 * Sauvegarde de la configuration
	 */
	public void save(){
		Map<String, String> hm = new HashMap<String, String>();
		hm.put(Options.TILES_PATH.getValue(), tilesPath.getText());
		hm.put(Options.SHOW_TILES_UNMAPPED.getValue(), ((Boolean) unmappedCheckBox.isSelected()).toString());
		hm.put(Options.SHOW_TILES_GRID.getValue(), ((Boolean) gridCheckBox.isSelected()).toString());
		OptionHelper.save(hm);
		masterFrame.dispose();
	}
	
	/**
	 * Initialisation de la fenêtre
	 */
	public void init(){
		// Récupération du paramétrage
		Map<String, String> config = OptionHelper.load();

		// Initialisation des champs
		tilesPath.setText(config.get(Options.TILES_PATH.getValue()));
		unmappedCheckBox.setSelected(Boolean.parseBoolean(config.get(Options.SHOW_TILES_UNMAPPED.getValue())));
		gridCheckBox.setSelected(Boolean.parseBoolean(config.get(Options.SHOW_TILES_GRID.getValue())));
	}
	
	/**
	 * Clic sur le bouton Annuler (fermeture de la fenêtre sans sauvegarde)
	 */
	public void cancel(){
		masterFrame.dispose();
	}
}
