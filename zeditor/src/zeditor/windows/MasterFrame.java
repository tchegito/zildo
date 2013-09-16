package zeditor.windows;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;

import zeditor.core.Options;
import zeditor.core.tiles.TileSet;
import zeditor.fwk.awt.ZildoScrollablePanel;
import zeditor.windows.managers.MasterFrameManager;
import zeditor.windows.subpanels.BackgroundPanel;
import zeditor.windows.subpanels.ChainingPointPanel;
import zeditor.windows.subpanels.PersoPanel;
import zeditor.windows.subpanels.PrefetchPanel;
import zeditor.windows.subpanels.ScriptPanel;
import zeditor.windows.subpanels.SpritePanel;
import zeditor.windows.subpanels.StatsPanel;
import zildo.Zildo;
import zildo.client.ClientEngineZildo;

import com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel;

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
@SuppressWarnings("serial")
public class MasterFrame extends javax.swing.JFrame {

	private JMenuBar menuBar;
	private MasterFrameManager manager;
	private JMenuItem mapCaptureItem;
	private JMenuItem builderMenuItem;
	private JMenuItem saveCollisionItem;
	private JMenu miscMenu;
	private JMenuItem reloadConfigItem;
	private JMenuItem optionsItem;
	private JMenu parametersMenu;
	private JMenuItem exitItem;
	private JSeparator fileSeparator1;
	private JMenuItem saveAsItem;
	private JMenuItem saveItem;
	private JMenuItem loadItem;
	private JMenuItem newItem;
	private JMenu fileMenu;
	private JLabel systemLabel;
	private JLabel caseInfoLabel;
	private JPanel systemPanel;
	private JPanel rightPanel;
	private PersoPanel persoPanel;
	private SpritePanel spritePanel;
	public TileSet tileSetPanel;
	private BackgroundPanel backgroundPanel;
	private ScriptPanel scriptPanel;
	private StatsPanel statsPanel;
	private JPanel prefetchPanel;
	private ChainingPointPanel chainingPointPanel;
	private JTabbedPane tabsPane;
	private JPanel leftPanel;
	private JPanel contentPanel;
	private JButton newMapTool;
	private JToggleButton gridTool;
	private JToggleButton collisionTool;
	private JToggleButton unmappedTool;
	private JToggleButton copyPasteTool;
	private JToggleButton backTileTool;
	private JToggleButton foreTileTool;
	private JToggleButton backSpriteTool;
	private JToggleButton foreSpriteTool;
	private JButton tileMaskTool;
	private JToggleButton spriteGridTool;
	private JToolBar toolBar;
	private JPanel toolBarContainer;
	private JPanel masterPanel;
	private AbstractAction actionSaveAs;
	private AbstractAction actionNew;
	private AbstractAction actionGridTool;
	private AbstractAction actionCollisionTool;
	private AbstractAction actionUnmappedTool;
	private AbstractAction actionReloadConfig;
	private AbstractAction actionOpenOptionsFrame;
	private AbstractAction actionExit;
	private AbstractAction actionSave;
	private AbstractAction actionLoad;
	private AbstractAction actionNewMapTool;
	private AbstractAction actionCopyPasteTool;
	private AbstractAction actionDisplayBackTileTool;
	private AbstractAction actionDisplayForeTileTool;
	private AbstractAction actionDisplayBackSpriteTool;
	private AbstractAction actionDisplayForeSpriteTool;
	private AbstractAction actionTileMask;
	private AbstractAction actionSpriteGrid;

	private ZildoScrollablePanel zildoPanel;

	/**
	 * Auto-generated main method to display this JFrame
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(new NimbusLookAndFeel());
				} catch (UnsupportedLookAndFeelException e) {

				}

				Zildo.screenX = 640;
				Zildo.screenY = 480;
				Zildo.viewPortX=Zildo.screenX;
				Zildo.viewPortY=Zildo.screenY + 26;
				MasterFrame inst = new MasterFrame();
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}

	public MasterFrame() {
		super();
		initGUI();
	}

	private void initGUI() {
		try {
			zildoPanel = new ZildoScrollablePanel("coucou");

			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			BorderLayout thisLayout = new BorderLayout();
			getContentPane().setLayout(thisLayout);
			this.setTitle("Zeditor");
			this.setMinimumSize(new java.awt.Dimension(1010, 600));
			this.setIconImage(new ImageIcon(getClass().getClassLoader()
					.getResource("zeditor/images/map.png")).getImage());
			getContentPane().add(getMasterPanel(), BorderLayout.CENTER);
			getContentPane().add(getSystemPanel(), BorderLayout.SOUTH);
			{
				menuBar = new JMenuBar();
				setJMenuBar(menuBar);
				menuBar.add(getFileMenu());
				menuBar.add(getParametersMenu());
				menuBar.add(getMiscMenu());
			}

			// On ajoute la carte

			getContentPane().add(zildoPanel, BorderLayout.EAST);

			// Initialisation de la fenêtre par le manager
			manager.init();

			pack();
			Dimension d = zildoPanel.getSize();
			this.setSize(640 + 350, d.height);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private AbstractAction getActionLoad() {
		if (actionLoad == null) {
			actionLoad = new AbstractAction("Ouvrir", null) {
				private static final long serialVersionUID = -7397130907033546971L;

				public void actionPerformed(ActionEvent evt) {
					manager.load();
				}
			};
		}
		return actionLoad;
	}

	private AbstractAction getActionSave() {
		if (actionSave == null) {
			actionSave = new AbstractAction("Enregistrer", null) {
				private static final long serialVersionUID = -4309279475377395081L;

				public void actionPerformed(ActionEvent evt) {
					manager.save();
				}
			};
		}
		return actionSave;
	}

	private AbstractAction getActionExit() {
		if (actionExit == null) {
			actionExit = new AbstractAction("Quitter", null) {
				private static final long serialVersionUID = 2504166520928298260L;

				public void actionPerformed(ActionEvent evt) {
					manager.exit();
				}
			};
		}
		return actionExit;
	}

	public MasterFrameManager getManager() {
		if (manager == null) {
			manager = new MasterFrameManager(this);
			manager.initialize(getSystemLabel(), getCaseInfoLabel(), getMasterPanel(),
					zildoPanel.getZildoCanvas());
		}
		return manager;
	}

	private AbstractAction getActionOpenOptionsFrame() {
		if (actionOpenOptionsFrame == null) {
			actionOpenOptionsFrame = new AbstractAction("Options", null) {
				private static final long serialVersionUID = -4764264927309844210L;

				@Override
				public void actionPerformed(ActionEvent evt) {
					manager.openOptionsFrame();
				}
			};
		}
		return actionOpenOptionsFrame;
	}

	private AbstractAction getActionReloadConfig() {
		if (actionReloadConfig == null) {
			actionReloadConfig = new AbstractAction("Recharger la conf.", null) {
				private static final long serialVersionUID = -3565192878529677669L;

				@Override
				public void actionPerformed(ActionEvent evt) {
					manager.reloadConfig();
				}
			};
		}
		return actionReloadConfig;
	}

	private AbstractAction getActionUnmappedTool() {
		if (actionUnmappedTool == null) {
			actionUnmappedTool = new AbstractAction("", null) {
				private static final long serialVersionUID = 7546834766049284479L;

				@Override
				public void actionPerformed(ActionEvent evt) {
					if (unmappedTool.isSelected()) {
						manager.showTileSetUnmapped(true);
					} else {
						manager.showTileSetUnmapped(false);
					}
				}
			};
		}
		return actionUnmappedTool;
	}

	private AbstractAction getActionGridTool() {
		if (actionGridTool == null) {
			actionGridTool = new AbstractAction("", null) {
				private static final long serialVersionUID = -416405688673820762L;

				@Override
				public void actionPerformed(ActionEvent evt) {
					if (gridTool.isSelected()) {
						manager.showTileSetGrid(true);
					} else {
						manager.showTileSetGrid(false);
					}
				}
			};
		}
		return actionGridTool;
	}

	private AbstractAction getActionCollisionTool() {
		if (actionCollisionTool == null) {
			actionCollisionTool = new AbstractAction("", null) {
				@Override
				public void actionPerformed(ActionEvent evt) {
					if (collisionTool.isSelected()) {
						manager.showCollision(true);
					} else {
						manager.showCollision(false);
					}
				}
			};
		}
		return actionCollisionTool;
	}

	private AbstractAction getActionCopyPasteTool() {
		if (actionCopyPasteTool == null) {
			actionCopyPasteTool = new AbstractAction(null) {
				@Override
				public void actionPerformed(ActionEvent evt) {
					zildoPanel.getZildoCanvas().switchCopyMode();
					if (manager.getSelection() != null) {
						manager.getSelection().unfocus();
					}
				}
			};
		}
		return actionCopyPasteTool;
	}

	private AbstractAction getActionDisplayBackTileTool() {
		if (actionDisplayBackTileTool == null) {
			actionDisplayBackTileTool = new AbstractAction(null) {
				@Override
				public void actionPerformed(ActionEvent evt) {
					ClientEngineZildo.mapDisplay.foreBackController
							.toggleDisplaySpecific(false);
				}
			};
		}
		return actionDisplayBackTileTool;
	}

	private AbstractAction getActionDisplayForeTileTool() {
		if (actionDisplayForeTileTool == null) {
			actionDisplayForeTileTool = new AbstractAction(null) {
				@Override
				public void actionPerformed(ActionEvent evt) {
					ClientEngineZildo.mapDisplay.foreBackController
							.toggleDisplaySpecific(true);
				}
			};
		}
		return actionDisplayForeTileTool;
	}

	private AbstractAction getActionDisplayBackSpriteTool() {
		if (actionDisplayBackSpriteTool == null) {
			actionDisplayBackSpriteTool = new AbstractAction("", null) {
				@Override
				public void actionPerformed(ActionEvent evt) {
					ClientEngineZildo.spriteDisplay.foreBackController
							.toggleDisplaySpecific(false);
				}
			};
		}
		return actionDisplayBackSpriteTool;
	}

	private AbstractAction getActionDisplayForeSpriteTool() {
		if (actionDisplayForeSpriteTool == null) {
			actionDisplayForeSpriteTool = new AbstractAction("", null) {
				@Override
				public void actionPerformed(ActionEvent evt) {
					ClientEngineZildo.spriteDisplay.foreBackController
							.toggleDisplaySpecific(true);
				}
			};
		}
		return actionDisplayForeSpriteTool;
	}

	private AbstractAction getActionTileMask() {
		if (actionTileMask == null) {
			actionTileMask = new AbstractAction("", null) {
				@Override
				public void actionPerformed(ActionEvent evt) {
					zildoPanel.getCanvas().switchTileLayer();
					String imgName="mask";
					switch (zildoPanel.getCanvas().getTileLayer()) {
					case 1:
						imgName="mask2";
						break;
					case 2:
						imgName="mask3";
						break;
					}
					getToggleTileMaskTool().setIcon(new ImageIcon(getClass().getClassLoader()
							.getResource("zeditor/images/"+imgName+".png")));
				}
			};
		}
		return actionTileMask;
	}
	
	private AbstractAction getActionSpriteGrid() {
		if (actionSpriteGrid == null) {
			actionSpriteGrid = new AbstractAction("", null) {
				@Override
				public void actionPerformed(ActionEvent evt) {
					zildoPanel.getCanvas().toggleGrid();
				}
			};
		}
		return actionSpriteGrid;
	}
	
	
	private AbstractAction getActionNewMapTool() {
		if (actionNewMapTool == null) {
			actionNewMapTool = new AbstractAction(null) {
				@Override
				public void actionPerformed(ActionEvent evt) {
					zildoPanel.getCanvas().clearMap();
				}
			};
		}
		return actionNewMapTool;
	}

	private AbstractAction getActionNew() {
		if (actionNew == null) {
			actionNew = new AbstractAction("Nouveau", null) {
				private static final long serialVersionUID = -5578117368337210024L;

				@Override
				public void actionPerformed(ActionEvent evt) {
					manager.create();
				}
			};
		}
		return actionNew;
	}

	private AbstractAction getActionSaveAs() {
		if (actionSaveAs == null) {
			actionSaveAs = new AbstractAction("Enregistrer sous ...", null) {
				private static final long serialVersionUID = 8663451078247792775L;

				@Override
				public void actionPerformed(ActionEvent evt) {
					manager.saveAs();
				}
			};
		}
		return actionSaveAs;
	}

	private JPanel getMasterPanel() {
		if (masterPanel == null) {
			masterPanel = new JPanel();
			BoxLayout masterPanelLayout = new BoxLayout(masterPanel,
					javax.swing.BoxLayout.Y_AXIS);
			masterPanel.setLayout(masterPanelLayout);
			masterPanel.add(getToolBarContainer());
			masterPanel.add(getContentPanel());
		}
		return masterPanel;
	}

	private JPanel getToolBarContainer() {
		if (toolBarContainer == null) {
			toolBarContainer = new JPanel();
			BorderLayout toolBarContainerLayout = new BorderLayout();
			toolBarContainer.setLayout(toolBarContainerLayout);
			toolBarContainer.setMaximumSize(new java.awt.Dimension(32767, 32));
			toolBarContainer.add(getToolBar(), BorderLayout.CENTER);
		}
		return toolBarContainer;
	}

	private JToolBar getToolBar() {
		if (toolBar == null) {
			toolBar = new JToolBar();
			toolBar.add(getNewMapTool());
			toolBar.add(new JToolBar.Separator());
			toolBar.add(getUnmappedTool());
			toolBar.add(getGridTool());
			toolBar.add(getCollisionTool());
			toolBar.add(new JToolBar.Separator());
			toolBar.add(getCopyPasteTool());
			toolBar.add(getToggleTileMaskTool());
			toolBar.add(getSpriteGridTool());
			toolBar.add(new JToolBar.Separator());
			toolBar.add(getToggleBackDisplayTool());
			toolBar.add(getToggleForeDisplayTool());
			toolBar.add(getToggleBackSpriteDisplayTool());
			toolBar.add(getToggleForeSpriteDisplayTool());

		}
		return toolBar;
	}

	public JToggleButton getUnmappedTool() {
		if (unmappedTool == null) {
			unmappedTool = new JToggleButton();
			unmappedTool.setToolTipText("Afficher les tuiles non mappées.");
			unmappedTool.setAction(getActionUnmappedTool());
			unmappedTool.setSelected(Boolean.parseBoolean(getManager()
					.loadOption(Options.SHOW_TILES_UNMAPPED.getValue())));
			unmappedTool.setIcon(new ImageIcon(getClass().getClassLoader()
					.getResource("zeditor/images/shading.png")));
		}
		return unmappedTool;
	}

	public JToggleButton getGridTool() {
		if (gridTool == null) {
			gridTool = new JToggleButton();
			gridTool.setAction(getActionGridTool());
			gridTool.setIcon(new ImageIcon(getClass().getClassLoader()
					.getResource("zeditor/images/text_columns.png")));
		}
		return gridTool;
	}

	public JToggleButton getCollisionTool() {
		if (collisionTool == null) {
			collisionTool = new JToggleButton();
			collisionTool.setAction(getActionCollisionTool());
			collisionTool.setIcon(new ImageIcon(getClass().getClassLoader()
					.getResource("zeditor/images/footstep.png")));
		}
		return collisionTool;
	}

	public JToggleButton getCopyPasteTool() {
		if (copyPasteTool == null) {
			copyPasteTool = new JToggleButton();
			copyPasteTool.setToolTipText("Copier une zone");
			copyPasteTool.setAction(getActionCopyPasteTool());
			copyPasteTool.setIcon(new ImageIcon(getClass().getClassLoader()
					.getResource("zeditor/images/copy.PNG")));
		}
		return copyPasteTool;
	}

	public JToggleButton getToggleBackDisplayTool() {
		if (backTileTool == null) {
			backTileTool = new JToggleButton();
			backTileTool.setToolTipText("Tiles d'arrière plan");
			backTileTool.setAction(getActionDisplayBackTileTool());
			backTileTool.setIcon(new ImageIcon(getClass().getClassLoader()
					.getResource("zeditor/images/backGround.PNG")));
		}
		return backTileTool;
	}

	public JToggleButton getToggleForeDisplayTool() {
		if (foreTileTool == null) {
			foreTileTool = new JToggleButton();
			foreTileTool.setToolTipText("Tiles de premier plan");
			foreTileTool.setAction(getActionDisplayForeTileTool());
			foreTileTool.setIcon(new ImageIcon(getClass().getClassLoader()
					.getResource("zeditor/images/foreGround.PNG")));
		}
		return foreTileTool;
	}

	public JToggleButton getToggleBackSpriteDisplayTool() {
		if (backSpriteTool == null) {
			backSpriteTool = new JToggleButton();
			backSpriteTool.setToolTipText("Sprites d'arrière plan");
			backSpriteTool.setAction(getActionDisplayBackSpriteTool());
			backSpriteTool.setIcon(new ImageIcon(getClass().getClassLoader()
					.getResource("zeditor/images/backGroundSprite.PNG")));
		}
		return backSpriteTool;
	}

	public JToggleButton getToggleForeSpriteDisplayTool() {
		if (foreSpriteTool == null) {
			foreSpriteTool = new JToggleButton();
			foreSpriteTool.setToolTipText("Sprites de premier plan plan");
			foreSpriteTool.setAction(getActionDisplayForeSpriteTool());
			foreSpriteTool.setIcon(new ImageIcon(getClass().getClassLoader()
					.getResource("zeditor/images/foreGroundSprite.PNG")));
		}
		return foreSpriteTool;
	}

	public JButton getToggleTileMaskTool() {
		if (tileMaskTool == null) {
			tileMaskTool = new JButton();
			tileMaskTool.setToolTipText("Edition du masque");
			tileMaskTool.setAction(getActionTileMask());
			tileMaskTool.setIcon(new ImageIcon(getClass().getClassLoader()
					.getResource("zeditor/images/mask.png")));
		}
		return tileMaskTool;
	}

	public JToggleButton getSpriteGridTool() {
		if (spriteGridTool == null) {
			spriteGridTool = new JToggleButton();
			spriteGridTool.setToolTipText("Sprite sur une grille de 8x8");
			spriteGridTool.setAction(getActionSpriteGrid());
			spriteGridTool.setIcon(new ImageIcon(getClass().getClassLoader()
					.getResource("zeditor/images/grid.png")));
		}
		return spriteGridTool;
	}
	
	public JButton getNewMapTool() {
		if (newMapTool == null) {
			newMapTool = new JButton();
			newMapTool.setToolTipText("Nouvelle carte");
			newMapTool.setAction(getActionNewMapTool());
			newMapTool.setIcon(new ImageIcon(getClass().getClassLoader()
					.getResource("zeditor/images/page.png")));
		}
		return newMapTool;
	}

	private JPanel getContentPanel() {
		if (contentPanel == null) {
			contentPanel = new JPanel();
			BoxLayout contentPanelLayout = new BoxLayout(contentPanel,
					javax.swing.BoxLayout.X_AXIS);
			contentPanel.setLayout(contentPanelLayout);
			contentPanel.add(getLeftPanel());
			contentPanel.add(getRightPanel());
		}
		return contentPanel;
	}

	private JPanel getLeftPanel() {
		if (leftPanel == null) {
			leftPanel = new JPanel();
			BoxLayout leftPanelLayout = new BoxLayout(leftPanel,
					javax.swing.BoxLayout.Y_AXIS);
			leftPanel.setLayout(leftPanelLayout);
			leftPanel.setPreferredSize(new java.awt.Dimension(344, 487));
			leftPanel.setSize(344, 539);
			leftPanel.setMaximumSize(new java.awt.Dimension(344, 32767));
			leftPanel.add(getTabsPane());
		}
		return leftPanel;
	}

	public JTabbedPane getTabsPane() {
		if (tabsPane == null) {
			tabsPane = new JTabbedPane(JTabbedPane.TOP,
					JTabbedPane.WRAP_TAB_LAYOUT);
			tabsPane.addTab("Décors", null, getBackgroundPanel(), null);
			tabsPane.addTab("Prefetch", null, getPrefetchPanel(), null);
			tabsPane.addTab("Sprites", null, getSpritePanel(), null);
			tabsPane.addTab("Personnages", null, getPersoPanel(), null);
			tabsPane.addTab("Enchainements", null, getChainingPointPanel(),
					null);
			tabsPane.addTab("Stats", null, getStatsPanel(), null);
			tabsPane.addTab("Scripts", null, getScriptPanel(), null);
		}
		return tabsPane;
	}

	public BackgroundPanel getBackgroundPanel() {
		if (backgroundPanel == null) {
			backgroundPanel = new BackgroundPanel(getManager());
		}
		return backgroundPanel;
	}

	private JPanel getPrefetchPanel() {
		if (prefetchPanel == null) {
			prefetchPanel = new PrefetchPanel(getManager());
		}
		return prefetchPanel;
	}

	public ChainingPointPanel getChainingPointPanel() {
		if (chainingPointPanel == null) {
			chainingPointPanel = new ChainingPointPanel(getManager());
		}
		return chainingPointPanel;
	}

	public ScriptPanel getScriptPanel() {
		if (scriptPanel == null) {
			scriptPanel = new ScriptPanel(getManager());
		}
		return scriptPanel;
	}

	public SpritePanel getSpritePanel() {
		if (spritePanel == null) {
			spritePanel = new SpritePanel(manager);
		}
		return spritePanel;
	}

	public PersoPanel getPersoPanel() {
		if (persoPanel == null) {
			persoPanel = new PersoPanel(manager);
		}
		return persoPanel;
	}

	public StatsPanel getStatsPanel() {
		if (statsPanel == null) {
			statsPanel = new StatsPanel();
		}
		return statsPanel;
	}

	private JPanel getRightPanel() {
		if (rightPanel == null) {
			rightPanel = new JPanel();
			rightPanel.setBackground(new java.awt.Color(255, 255, 255));
		}
		return rightPanel;
	}

	private JPanel getSystemPanel() {
		if (systemPanel == null) {
			systemPanel = new JPanel();
			BorderLayout systemPanelLayout = new BorderLayout();
			systemPanel.setLayout(systemPanelLayout);
			systemPanel.setPreferredSize(new java.awt.Dimension(10, 20));
			systemPanel.setSize(792, 20);
			systemPanel.setMaximumSize(new java.awt.Dimension(32767, 20));
			systemPanel.add(getSystemLabel(), BorderLayout.LINE_START);
			systemPanel.add(getCaseInfoLabel(), BorderLayout.LINE_END);
		}
		return systemPanel;
	}

	private JLabel getSystemLabel() {
		if (systemLabel == null) {
			systemLabel = new JLabel();
		}
		return systemLabel;
	}

	private JLabel getCaseInfoLabel() {
		if (caseInfoLabel == null) {
			caseInfoLabel = new JLabel();
		}
		return caseInfoLabel;
	}
	
	
	private JMenu getFileMenu() {
		if (fileMenu == null) {
			fileMenu = new JMenu();
			fileMenu.setText("Fichier");
			fileMenu.setIcon(new ImageIcon(getClass().getClassLoader()
					.getResource("zeditor/images/application_osx_terminal.png")));
			fileMenu.add(getNewItem());
			fileMenu.add(getLoadItem());
			fileMenu.add(getSaveItem());
			fileMenu.add(getSaveAsItem());
			fileMenu.add(getFileSeparator1());
			fileMenu.add(getExitItem());
		}
		return fileMenu;
	}

	private JMenuItem getNewItem() {
		if (newItem == null) {
			newItem = new JMenuItem();
			newItem.setText("Nouveau");
			newItem.setAction(getActionNew());
			newItem.setIcon(new ImageIcon(getClass().getClassLoader()
					.getResource("zeditor/images/page_white.png")));
		}
		return newItem;
	}

	private JMenuItem getLoadItem() {
		if (loadItem == null) {
			loadItem = new JMenuItem();
			loadItem.setText("Ouvrir");
			loadItem.setAction(getActionLoad());
			loadItem.setIcon(new ImageIcon(getClass().getClassLoader()
					.getResource("zeditor/images/folder.png")));
		}
		return loadItem;
	}

	private JMenuItem getSaveItem() {
		if (saveItem == null) {
			saveItem = new JMenuItem();
			saveItem.setText("Enregistrer");
			saveItem.setAction(getActionSave());
			saveItem.setIcon(new ImageIcon(getClass().getClassLoader()
					.getResource("zeditor/images/disk.png")));
		}
		return saveItem;
	}

	private JMenuItem getSaveAsItem() {
		if (saveAsItem == null) {
			saveAsItem = new JMenuItem();
			saveAsItem.setText("Enregistrer sous ...");
			saveAsItem.setAction(getActionSaveAs());
			saveAsItem.setIcon(new ImageIcon(getClass().getClassLoader()
					.getResource("zeditor/images/disk.png")));
		}
		return saveAsItem;
	}

	private JSeparator getFileSeparator1() {
		if (fileSeparator1 == null) {
			fileSeparator1 = new JSeparator();
		}
		return fileSeparator1;
	}

	private JMenuItem getExitItem() {
		if (exitItem == null) {
			exitItem = new JMenuItem();
			exitItem.setText("Quitter");
			exitItem.setAction(getActionExit());
			exitItem.setIcon(new ImageIcon(getClass().getClassLoader()
					.getResource("zeditor/images/door.png")));
		}
		return exitItem;
	}

	private JMenu getParametersMenu() {
		if (parametersMenu == null) {
			parametersMenu = new JMenu();
			parametersMenu.setText("Paramètres");
			parametersMenu.setIcon(new ImageIcon(getClass().getClassLoader()
					.getResource("zeditor/images/cog.png")));
			parametersMenu.add(getOptionsItem());
			parametersMenu.add(getReloadConfigItem());
		}
		return parametersMenu;
	}

	private JMenuItem getOptionsItem() {
		if (optionsItem == null) {
			optionsItem = new JMenuItem();
			optionsItem.setText("Options");
			optionsItem.setAction(getActionOpenOptionsFrame());
			optionsItem.setIcon(new ImageIcon(getClass().getClassLoader()
					.getResource("zeditor/images/wrench.png")));
			optionsItem.setBounds(-53, 21, 74, 19);
		}
		return optionsItem;
	}

	private JMenuItem getReloadConfigItem() {
		if (reloadConfigItem == null) {
			reloadConfigItem = new JMenuItem();
			reloadConfigItem.setText("Recharger la conf.");
			reloadConfigItem.setAction(getActionReloadConfig());
			reloadConfigItem.setIcon(new ImageIcon(getClass().getClassLoader()
					.getResource("zeditor/images/arrow_refresh.png")));
		}
		return reloadConfigItem;
	}

	private JMenu getMiscMenu() {
		if (miscMenu == null) {
			miscMenu = new JMenu();
			miscMenu.setText("Misc");
			miscMenu.setIcon(new ImageIcon(getClass().getClassLoader()
					.getResource("zeditor/images/bug.png")));
			miscMenu.add(getSaveCollisionItem());
			miscMenu.add(getBuilderItem());
			miscMenu.add(getMapCaptureItem());
		}
		return miscMenu;
	}

	private JMenuItem getSaveCollisionItem() {
		if (saveCollisionItem == null) {
			saveCollisionItem = new JMenuItem();
			saveCollisionItem.setAction(new AbstractAction("Save collision", null) {
				@Override
				public void actionPerformed(ActionEvent e) {
					getBackgroundPanel().getTileSetPanel().saveCurrentTileCollision();
				}
			});
			saveCollisionItem.setIcon(new ImageIcon(getClass().getClassLoader()
					.getResource("zeditor/images/package.png")));
		}
		return saveCollisionItem;
	}

	private JMenuItem getBuilderItem() {
		if (builderMenuItem == null) {
			builderMenuItem = new JMenuItem();
			builderMenuItem.setText("Builder");
			builderMenuItem.setAction(new AbstractAction("Builder") {
				@Override
				public void actionPerformed(ActionEvent e) {
					JDialog frame = new BuilderDialog();
					frame.setLocationRelativeTo(builderMenuItem.getParent().getParent());
					frame.setModal(true);
					frame.setVisible(true);
				}
			});
			builderMenuItem.setIcon(new ImageIcon(getClass()
					.getClassLoader().getResource("zeditor/images/user.png")));
		}
		return builderMenuItem;
	}
	
	private JMenuItem getMapCaptureItem() {
		if (mapCaptureItem == null) {
			mapCaptureItem = new JMenuItem();
			mapCaptureItem.setText("Capture map");
			mapCaptureItem.setAction(new AbstractAction("Capture map") {
				@Override
				public void actionPerformed(ActionEvent e) {
					zildoPanel.getZildoCanvas().askCapture();
				}
			});
			mapCaptureItem.setIcon(new ImageIcon(getClass()
					.getClassLoader().getResource("zeditor/images/user.png")));
		}
		return mapCaptureItem;
	}
	

}
