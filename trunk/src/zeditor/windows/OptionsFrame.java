package zeditor.windows;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import zeditor.windows.managers.OptionsFrameManager;

/**
 * This code was edited or generated using CloudGarden's Jigloo SWT/Swing GUI
 * Builder, which is free for non-commercial use. If Jigloo is being used
 * commercially (ie, by a corporation, company or business for any purpose
 * whatever) then you should purchase a license for each developer using Jigloo.
 * Please visit www.cloudgarden.com for details. Use of Jigloo implies
 * acceptance of these licensing terms. A COMMERCIAL LICENSE HAS NOT BEEN
 * PURCHASED FOR THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED LEGALLY FOR
 * ANY CORPORATE OR COMMERCIAL PURPOSE.
 */
public class OptionsFrame extends javax.swing.JFrame {
	private static final long serialVersionUID = 95821990370940497L;

	{
		// Set Look & Feel
		try {
			// javax.swing.UIManager
			// .setLookAndFeel("com.jgoodies.looks.plastic.Plastic3DLookAndFeel");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private AbstractAction SaveAction;
	private AbstractAction CancelAction;
	private OptionsFrameManager manager;
	private JPanel CHaractersOptions;
	private JLabel infos;
	private JButton Save;
	private JButton Cancel;
	private JPanel ButtonsPanel;
	private JPanel ButtonsGroupPanel;
	private JPanel SpritesOptions;
	private JCheckBox gridCheckBox;
	private JCheckBox unmappedCheckBox;
	private JPanel boxesPanel;
	private JLabel star;
	private JLabel TilePathLabel;
	private JPanel TilePathPanel;
	private JPanel TilesOptions;
	private JTabbedPane TabbedPane;

	/**
	 * Auto-generated main method to display this JFrame
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				OptionsFrame inst = new OptionsFrame();
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}

	public OptionsFrame() {
		super();
		initGUI();
	}

	private void initGUI() {
		try {
			manager = getManager();
			this.setTitle("Options");
			this.setAlwaysOnTop(true);
			this.setResizable(false);
			BoxLayout thisLayout = new BoxLayout(getContentPane(),
					javax.swing.BoxLayout.Y_AXIS);
			getContentPane().setLayout(thisLayout);
			this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			this.setIconImage(new ImageIcon(getClass().getClassLoader()
					.getResource("zeditor/images/wrench.png")).getImage());
			getContentPane().add(getTabbedPane());
			getContentPane().add(getButtonsGroupPanel());

			// On recrée le manager avec les champs
			manager = new OptionsFrameManager(this, getUnmappedCheckBox(),
					getGridCheckBox());

			// On initialise la fenêtre avec le paramétrage actuel
			manager.init();

			pack();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private AbstractAction getCancelAction() {
		if (CancelAction == null) {
			CancelAction = new AbstractAction("Annuler", null) {
				private static final long serialVersionUID = 1446881783266983881L;

				@Override
				public void actionPerformed(ActionEvent evt) {
					manager.cancel();
				}
			};
		}
		return CancelAction;
	}

	private AbstractAction getSaveAction() {
		if (SaveAction == null) {
			SaveAction = new AbstractAction("Enregistrer", null) {
				private static final long serialVersionUID = 7216702886325874634L;

				@Override
				public void actionPerformed(ActionEvent evt) {
					manager.save();
				}
			};
		}
		return SaveAction;
	}

	private OptionsFrameManager getManager() {
		if (manager == null) {
			manager = new OptionsFrameManager();
		}
		return manager;
	}

	private JTabbedPane getTabbedPane() {
		if (TabbedPane == null) {
			TabbedPane = new JTabbedPane();
			TabbedPane.addTab("Décors", null, getTilesOptions(), null);
			TabbedPane.addTab("Sprites", null, getSpritesOptions(), null);
			TabbedPane
					.addTab("Personnages", null, getCHaractersOptions(), null);
		}
		return TabbedPane;
	}

	private JPanel getTilesOptions() {
		if (TilesOptions == null) {
			TilesOptions = new JPanel();
			BoxLayout TilesOptionsLayout = new BoxLayout(TilesOptions,
					javax.swing.BoxLayout.Y_AXIS);
			TilesOptions.setLayout(TilesOptionsLayout);
			TilesOptions.add(getTilePathPanel());
			TilesOptions.add(getBoxesPanel());
		}
		return TilesOptions;
	}

	private JPanel getTilePathPanel() {
		if (TilePathPanel == null) {
			TilePathPanel = new JPanel();
			BoxLayout TilePathPanelLayout = new BoxLayout(TilePathPanel,
					javax.swing.BoxLayout.X_AXIS);
			TilePathPanel.setLayout(TilePathPanelLayout);
			TilePathPanel.add(getTilePathLabel());
			TilePathPanel.add(getStar());
		}
		return TilePathPanel;
	}

	private JLabel getTilePathLabel() {
		if (TilePathLabel == null) {
			TilePathLabel = new JLabel();
			TilePathLabel.setText("Dossier des Tiles");
		}
		return TilePathLabel;
	}

	private JLabel getStar() {
		if (star == null) {
			star = new JLabel();
			star.setText(" * ");
		}
		return star;
	}

	private JPanel getBoxesPanel() {
		if (boxesPanel == null) {
			boxesPanel = new JPanel();
			BorderLayout UnmappedTilesPanelLayout = new BorderLayout();
			boxesPanel.setLayout(UnmappedTilesPanelLayout);
			boxesPanel.add(getUnmappedCheckBox(), BorderLayout.CENTER);
			boxesPanel.add(getGridCheckBox(), BorderLayout.NORTH);
		}
		return boxesPanel;
	}

	private JCheckBox getUnmappedCheckBox() {
		if (unmappedCheckBox == null) {
			unmappedCheckBox = new JCheckBox();
			unmappedCheckBox.setText("Afficher les tuiles non mappées");
		}
		return unmappedCheckBox;
	}

	private JCheckBox getGridCheckBox() {
		if (gridCheckBox == null) {
			gridCheckBox = new JCheckBox();
			gridCheckBox.setText("Afficher la grille");
		}
		return gridCheckBox;
	}

	private JPanel getSpritesOptions() {
		if (SpritesOptions == null) {
			SpritesOptions = new JPanel();
		}
		return SpritesOptions;
	}

	private JPanel getCHaractersOptions() {
		if (CHaractersOptions == null) {
			CHaractersOptions = new JPanel();
		}
		return CHaractersOptions;
	}

	private JPanel getButtonsGroupPanel() {
		if (ButtonsGroupPanel == null) {
			ButtonsGroupPanel = new JPanel();
			BorderLayout ButtonsGroupPanelLayout = new BorderLayout();
			ButtonsGroupPanel.setLayout(ButtonsGroupPanelLayout);
			ButtonsGroupPanel.setSize(517, 35);
			ButtonsGroupPanel.setPreferredSize(new java.awt.Dimension(517, 33));
			ButtonsGroupPanel.add(getButtonsPanel(), BorderLayout.EAST);
			ButtonsGroupPanel.add(getInfos(), BorderLayout.CENTER);
		}
		return ButtonsGroupPanel;
	}

	private JPanel getButtonsPanel() {
		if (ButtonsPanel == null) {
			ButtonsPanel = new JPanel();
			BoxLayout ButtonsPanelLayout = new BoxLayout(ButtonsPanel,
					javax.swing.BoxLayout.X_AXIS);
			ButtonsPanel.setPreferredSize(new java.awt.Dimension(120, 34));
			ButtonsPanel.setLayout(ButtonsPanelLayout);
			ButtonsPanel.add(getCancel());
			ButtonsPanel.add(getSave());
		}
		return ButtonsPanel;
	}

	private JButton getCancel() {
		if (Cancel == null) {
			Cancel = new JButton();
			Cancel.setText("Annuler");
			Cancel.setAction(getCancelAction());
		}
		return Cancel;
	}

	private JButton getSave() {
		if (Save == null) {
			Save = new JButton();
			Save.setText("Enregistrer");
			Save.setAction(getSaveAction());
		}
		return Save;
	}

	private JLabel getInfos() {
		if (infos == null) {
			infos = new JLabel();
			infos.setText(" * : Nécéssite un redémarrage de Zeditor pour être pris en compte");
		}
		return infos;
	}

}
