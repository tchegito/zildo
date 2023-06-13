package zeditor.windows.managers;

import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import zeditor.core.Options;
import zeditor.core.exceptions.ZeditorException;
import zeditor.core.prefetch.Prefetch;
import zeditor.core.selection.CaseSelection;
import zeditor.core.selection.ChainingPointSelection;
import zeditor.core.selection.PersoSelection;
import zeditor.core.selection.Selection;
import zeditor.core.selection.SpriteSelection;
import zeditor.core.tiles.TileSelection;
import zeditor.fwk.awt.ZildoCanvas;
import zeditor.windows.ExplorerFrame;
import zeditor.windows.MasterFrame;
import zeditor.windows.OptionHelper;
import zeditor.windows.OptionsFrame;
import zeditor.windows.subpanels.ChainingPointPanel;
import zeditor.windows.subpanels.SelectionKind;
import zildo.client.ClientEngineZildo;
import zildo.monde.map.Area;
import zildo.monde.map.Case;
import zildo.monde.map.ChainingPoint;
import zildo.monde.map.accessor.SpecificFloorAreaAccesor;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.persos.Perso;
import zildo.server.EngineZildo;

/**
 * Classe de management de la fenêtre principale de Zeditor (MasterFrame.class)
 * 
 * @author Drakulo
 */
public class MasterFrameManager {
	public static JLabel systemDisplay;
	public static JLabel caseInfoDisplay;
	private static MasterFrame masterFrame;
	private JPanel masterPanel;

	private static ZildoCanvas zildoCanvas;

	private static Selection currentSelection;

	private String currentMapFile;

	boolean unsavedChanges = false;
	
	private byte currentFloor = 1;	// Default floor
	
	public final static int MESSAGE_ERROR = 1;
	public final static int MESSAGE_INFO = 2;
	public final static int MESSAGE_SUCCESS = 3;
	
	/**
	 * Constructeur vide
	 * 
	 * @author Drakulo
	 */
	public MasterFrameManager(MasterFrame p_frame) {
		masterFrame = p_frame;
		masterFrame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (!unsavedChanges || confirmAbandonChanges()) {
					System.exit(0);
				}
			}
		});
	}

	/**
	 * Constructeur avec une liste d'objets correspondant aux différents objets de la MasterFrame
	 * 
	 * @param p_sys
	 *            Le JLabel Système de la MasterFrame
	 * @author Drakulo
	 */
	public void initialize(JLabel p_sys, JLabel p_caseInfo, JPanel p_master,
			ZildoCanvas p_zildoCanvas) {
		systemDisplay = p_sys;
		caseInfoDisplay = p_caseInfo;
		masterPanel = p_master;
		zildoCanvas = p_zildoCanvas;
		zildoCanvas.setManager(this);

		// Make canvas get the focus whenever frame is activated.
		masterFrame.addWindowFocusListener(new WindowAdapter() {
			@Override
			public void windowGainedFocus(WindowEvent e) {
				zildoCanvas.requestFocusInWindow();
			}
		});

		updateTitle();
		updateChainingPoints(null);
		masterFrame.getStatsPanel().updateStats();
	}
	
	/**
	 * Ferme la fenêtre de Zeditor
	 * 
	 * @author Drakulo
	 */
	public void exit() {
		// TODO Ajouter un test de vérification s'il y a eu une modification et
		// demander une sauvegarde le cas échéant.
		System.exit(0);
	}

	/**
	 * Sauve la carte en cours dans la carte en cours. Si la carte en cours n'a pas encore été sauvegardée, on appelle
	 * la méthode {@link MasterPanelManager.saveAs saveAs()}
	 * 
	 * @author Drakulo
	 */
	public void save() {
		zildoCanvas.saveMapFile(currentMapFile);
		display("Sauvegarde effectuée.", MESSAGE_INFO);
		setUnsavedChanges(false);
	}

	public void saveAs(String newMapFile) {
		currentMapFile = newMapFile;

		save();
		updateTitle();
	}

	/**
	 * Ouvre l'explorateur afin de sélectionner le nom du fichier à sauvegarder pui lance (ou annule) la sauvegarde
	 */
	public void saveAs() {
		display("Enregistrer sous...", MESSAGE_ERROR);
		openFileExplorer(ExplorerFrameManager.SAVE);
	}

	/**
	 * Charge une nouvelle carte
	 * 
	 * @author Drakulo
	 */
	public void load() {
		openFileExplorer(ExplorerFrameManager.OPEN);

	}

	private boolean confirmAbandonChanges() {
		int answer = JOptionPane.showConfirmDialog(null, "You have unsaved changes on current map. Are you sure to loose them ?",
				"Zeditor", JOptionPane.YES_NO_OPTION);
		return answer == 0;
	}
	
	public void loadMap(String p_mapName, ChainingPoint p_fromChainingPoint) {

		if (unsavedChanges && !confirmAbandonChanges()) {
			return;	// Stay on current map
		}
		display("Ouverture du fichier : " + p_mapName, MESSAGE_INFO);
		try {
			ChainingPoint ch = zildoCanvas.loadMap(p_mapName,
					p_fromChainingPoint);
			display("Chargement effectué.", MESSAGE_INFO);
			currentMapFile = p_mapName;

			updateTitle();
			updateChainingPoints(ch);
			masterFrame.getStatsPanel().updateStats();
			masterFrame.getPersoPanel().focusPerso(null);
			setUnsavedChanges(false);
		} catch (RuntimeException e) {
			e.printStackTrace();
			display("Probleme !", MESSAGE_ERROR);
			JOptionPane.showMessageDialog(null, "Unable to load map "+p_mapName, "ZEditor", JOptionPane.ERROR_MESSAGE);
		}
	}

	public void updateTitle() {
		StringBuilder sb = new StringBuilder("ZEditor - ");
		Area map = EngineZildo.mapManagement.getCurrentMap();
		if (map != null) {
			sb.append(map.getName());
			sb.append(" - ");
			sb.append(map.getDim_x() + " x " + map.getDim_y());
		} else {
			sb.append("Nouvelle carte");
		}
		if (unsavedChanges) {
			sb.append(" *");
		}
		masterFrame.setTitle(sb.toString());
	}

	public void updateChainingPoints(ChainingPoint p_ch) {
		ChainingPointPanel chPanel = masterFrame.getChainingPointPanel();
		chPanel.updateList(getChainingPointsForCombo());
		if (p_ch != null) {
			chPanel.focusPoint(p_ch);
		}
	}

	/**
	 * Crée une nouvelle carte
	 * 
	 * @author Drakulo
	 */
	public void create() {
		display(" Nouvelle carte", MESSAGE_ERROR);
		zildoCanvas.clearMap();
		updateTitle();
		masterFrame.getStatsPanel().updateStats();
		updateChainingPoints(null);
	}

	/**
	 * Charge le tileSet dont le nom est passé en paramètres
	 * 
	 * @param name
	 * @author Drakulo
	 */
	public void changeTileSet(String p_name) {
		try {
			masterFrame.getBackgroundPanel().getTileSetPanel()
					.changeTile(p_name);
			display("TileSet '" + p_name + "' chargé.", MESSAGE_INFO);
		} catch (ZeditorException e) {
			display(e.getMessage(), MESSAGE_ERROR);
		}
	}

	public Object[] getPrefetchForCombo() {
		return Prefetch.getNames();
	}

	public ChainingPoint[] getChainingPointsForCombo() {
		if (EngineZildo.mapManagement == null) {
			return new ChainingPoint[] {};
		}
		List<ChainingPoint> names = new ArrayList<ChainingPoint>();
		List<ChainingPoint> points = EngineZildo.mapManagement.getCurrentMap()
				.getChainingPoints();
		for (ChainingPoint chp : points) {
			names.add(chp);
		}
		return names.toArray(new ChainingPoint[] {});
	}

	/**
	 * Affiche un message dans le label Système
	 * 
	 * @param p_msg
	 *            est le message à afficher
	 * @param p_type
	 *            est le type de message
	 * @author Drakulo
	 */
	public static void display(String p_msg, int p_type) {
		if (systemDisplay != null) {
			systemDisplay.setText(" " + p_msg);
			Color c = null;
			switch (p_type) {
			case MESSAGE_ERROR:
				c = Color.red;
				break;
			case MESSAGE_SUCCESS:
				c = Color.green;
				break;
			case MESSAGE_INFO:
			default:
				c = Color.black;
				break;
			}
			systemDisplay.setForeground(c);
		}
	}

	public static void display(String p_msg) {
		display(p_msg, MESSAGE_INFO);
	}
	
	public static void displayCaseInfo(String p_msg) {
		if (caseInfoDisplay != null) {
			caseInfoDisplay.setText(" " + p_msg);
			caseInfoDisplay.setForeground(Color.black);
		}
	}
	
	/**
	 * Ouvre la fenêtre de paramétrage des options
	 * 
	 * @author Drakulo
	 */
	public void openOptionsFrame() {
		OptionsFrame optFrame = new OptionsFrame();
		optFrame.setLocationRelativeTo(masterFrame);
		optFrame.setVisible(true);
		optFrame.addWindowListener(new WindowListener() {
			public void windowActivated(WindowEvent arg0) {
			}

			public void windowClosed(WindowEvent arg0) {
			}

			public void windowClosing(WindowEvent arg0) {

			}

			public void windowDeactivated(WindowEvent arg0) {
				updateTools();
				masterPanel.repaint();
			}

			public void windowDeiconified(WindowEvent arg0) {
			}

			public void windowIconified(WindowEvent arg0) {
			}

			public void windowOpened(WindowEvent arg0) {
			}

		});
	}

	/**
	 * Recharge les "petites" configurations
	 * 
	 * @author Drakulo
	 */
	public void reloadConfig() {
		updateTools();
		masterPanel.repaint();
		display("Petite configuration rechargée.", MESSAGE_INFO);
	}

	/**
	 * Changement d'une option avec sauvegarde
	 * 
	 * @param p_option
	 *            : Entrée de l'énumération Options
	 * @param p_value
	 *            : Valeur à attribuer
	 * @author Drakulo
	 */
	public void saveOption(Options p_option, String p_value) {
		OptionHelper.saveOption(p_option, p_value);
		masterFrame.getBackgroundPanel().repaint();
	}

	/**
	 * Chargement d'une option
	 * 
	 * @param p_option
	 *            : Entrée de l'énumération Options
	 * @return La valeur paramétrée de l'option
	 * @author Drakulo
	 */
	public String loadOption(String p_option) {
		return OptionHelper.loadOption(p_option);
	}

	/**
	 * Met à jours les boutons de la ToolBar. Cette méthode est dépendante de la structure de la fenêtre. MasterPanel >>
	 * ToolbarContainer >> ToolBar
	 * 
	 * @author Drakulo
	 */
	public void updateTools() {
		// Bouton des tuiles non mappées
		JToggleButton unmapped = masterFrame.getUnmappedTool();
		unmapped.setSelected(Boolean.valueOf(OptionHelper
				.loadOption(Options.SHOW_TILES_UNMAPPED.getValue())));

		// Bouton d'affichage de la grille
		JToggleButton grid = masterFrame.getGridTool();
		grid.setSelected(Boolean.valueOf(OptionHelper
				.loadOption(Options.SHOW_TILES_GRID.getValue())));

		// Bouton d'affichage de la grille
		JToggleButton collision = masterFrame.getCollisionTool();
		collision.setSelected(Boolean.valueOf(OptionHelper
				.loadOption(Options.SHOW_COLLISION.getValue())));
	}

	/**
	 * Change le titre de la fenêtre Zildo avec le texte : "Zeditor - [TITLE]"
	 * 
	 * @param title
	 * @author Drakulo
	 */
	public void changeTitle(String title) {
		masterFrame.setTitle("Zeditor - " + title);
	}

	/**
	 * Affiche ou masque la grille sur le TileSet suivant le paramètre
	 * 
	 * @param flag
	 *            true : afficher, false : masquer
	 * @author Drakulo
	 */
	public void showTileSetGrid(boolean flag) {
		saveOption(Options.SHOW_TILES_GRID, String.valueOf(flag));
		if (flag) {
			display("Grille affichée.", MESSAGE_INFO);
		} else {
			display("Grille masquée.", MESSAGE_INFO);
		}
	}

	public void showCollision(boolean flag) {
		saveOption(Options.SHOW_COLLISION, String.valueOf(flag));
		if (flag) {
			display("Collision activée.", MESSAGE_INFO);
		} else {
			display("Collision desactivée.", MESSAGE_INFO);
		}
	}

	/**
	 * Affiche ou masque les tuiles non mappées sur le TileSet suivant le paramètre
	 * 
	 * @param flag
	 *            true : afficher, false : masquer
	 * @author Drakulo
	 */
	public void showTileSetUnmapped(boolean flag) {
		saveOption(Options.SHOW_TILES_UNMAPPED, String.valueOf(flag));
		if (flag) {
			display("Tuiles non mappées mises en évidence.", MESSAGE_INFO);
		} else {
			display("Tuiles non mappées ignorées.", MESSAGE_INFO);
		}
	}

	/**
	 * Ouvre l'explorateur de fichier avec les paramètres
	 * 
	 * @param mode
	 *            est les mode (ouverture / sauvegarde) :
	 *            <p>
	 *            {@link ExplorerFrameManager.OPEN} / {@link ExplorerFrameManager.SAVE}
	 *            </p>
	 */
	public void openFileExplorer(int mode) {
		new ExplorerFrame(masterFrame, mode);
	}

	/**
	 * Initialisation de la fenêtre
	 */
	public void init() {
		updateTools();
		changeTileSet(masterFrame.getBackgroundPanel().getBackgroundCombo()
				.getSelectedItem().toString());
	}

	public SelectionKind getSelectionKind() {
		int sel = masterFrame.getTabsPane().getSelectedIndex();
		SelectionKind kind = SelectionKind.fromInt(sel);
		return kind;
	}

	public void switchTab(SelectionKind kind) {
		masterFrame.getTabsPane().setSelectedIndex(kind.ordinal());
	}
	
	public Selection getSelection() {
		SelectionKind kind = getSelectionKind();
		if (kind != null) {
			switch (kind) {
			case TILES:
				return masterFrame.getBackgroundPanel().getTileSetPanel()
						.getCurrentSelection();
			case PREFETCH:
			case CHAININGPOINT:
			case PERSOS:
			case SPRITES:
				return currentSelection;
			}
		}
		return null;
	}

	public ZildoCanvas getZildoCanvas() {
		return zildoCanvas;
	}

	/**
	 * Stop copy mode and switch to *block* tileset.
	 */
	public static void switchCopyTile(int p_width, int p_height,
			List<Case> p_cases) {
		if (p_width > 0 && p_height > 0) {
			masterFrame.getBackgroundPanel().switchCopyTile(p_width, p_height,
					p_cases);
		}
		masterFrame.getCopyPasteTool().setSelected(false);
	}

	public static void switchCopySprites(List<SpriteEntity> p_entities) {
		masterFrame.getManager().setSpriteSelection(
				new SpriteSelection<SpriteEntity>(p_entities));
		masterFrame.getCopyPasteTool().setSelected(false);
	}

	public void setCaseSelection(CaseSelection p_currentSelection) {
		currentSelection = p_currentSelection;
		if (currentSelection instanceof TileSelection) {
			TileSelection tileSel = (TileSelection) currentSelection;
			getZildoCanvas().setCursorSize(tileSel.width, tileSel.height);
		}
	}

	public void setChainingPointSelection(
			ChainingPointSelection p_currentSelection) {
		if (currentSelection == null
				|| !p_currentSelection.equals(currentSelection)) {
			// Chaining point changes : we hava to update the list
			masterFrame.getChainingPointPanel().focusPoint(
					p_currentSelection.getElement());
			currentSelection = p_currentSelection;
		}
	}

	public void setSpriteSelection(
			SpriteSelection<SpriteEntity> p_currentSelection) {
		if (currentSelection == null || p_currentSelection == null
				|| !p_currentSelection.equals(currentSelection)) {
			if (currentSelection != null) {
				currentSelection.unfocus();
			}
			// Focus the given sprite
			currentSelection = p_currentSelection;
			if (currentSelection != null) {
				currentSelection.focus();
			}
		}
		masterFrame.getSpritePanel().focusSprites(p_currentSelection);
	}

	/**
	 * Set the current Perso selection. Three possible situations:
	 * <ul>
	 * <li>user gain focus on a character on the map</li>
	 * <li>user pick a character from the library</li>
	 * <li>user remove the focuses perso</li>
	 * </ul>
	 * 
	 * @param p_currentSelection
	 */
	public void setPersoSelection(PersoSelection p_currentSelection) {
		if (currentSelection == null || p_currentSelection == null
				|| !p_currentSelection.equals(currentSelection)) {
			if (currentSelection != null) {
				currentSelection.unfocus();
			}
			// Focus the given perso (or focus NULL if selection is empty)
			if (p_currentSelection != null) {
				List<Perso> persos = p_currentSelection.getElement();
				for (Perso perso : persos) {
					masterFrame.getPersoPanel().focusPerso(perso);
				}
			}
			currentSelection = p_currentSelection;
			if (currentSelection != null) {
				currentSelection.focus();
			}
		}
	}

	public void updateTileSet() {
		masterFrame.getBackgroundPanel().repaint();
	}
	
	public byte getCurrentFloor() {
		return currentFloor;
	}
	
	public void setCurrentFloor(byte floor) {
		currentFloor = floor;
		ClientEngineZildo.tileEngine.setAreaAccessor(new SpecificFloorAreaAccesor(currentFloor));
	}
	
	public void reloadTileBanks() {
		masterFrame.getBackgroundPanel().getTileSetPanel().clearTiles();
		//masterFrame.getPersoPanel().reloadGraphics();
	}
	
	/** Inform that users have made changes on the current map **/
	public void setUnsavedChanges(boolean value) {
		boolean needToUpdate = value != unsavedChanges;
		unsavedChanges = value;
		if (needToUpdate) {
			updateTitle();
		}
	}

	public void doWhenTextureLoaded(Runnable sideEffect) {
		if (zildoCanvas != null && zildoCanvas.isTextureLoaded()) {
			sideEffect.run();
		} else {
			canvasWaitingCall.add(sideEffect);
		}
	}
	
	public ByteBuffer getSpriteTexture(int nTexture) {
		return zildoCanvas.getSpriteTexture(nTexture);
	}
	
	public ByteBuffer getTileTexture(int nTexture) {
		return zildoCanvas.getTileTexture(nTexture);
	}
	
	public final List<Runnable> canvasWaitingCall = new ArrayList<>();
}