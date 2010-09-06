package zeditor.windows;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import zeditor.core.Options;
import zeditor.core.prefetch.Prefetch;
import zeditor.core.prefetch.PrefetchSelection;
import zeditor.core.tiles.TileSet;
import zeditor.windows.managers.MasterFrameManager;
import zildo.client.ClientEngineZildo;
import zildo.fwk.awt.ZildoScrollablePanel;


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
@SuppressWarnings("serial")
public class MasterFrame extends javax.swing.JFrame {



	private JMenuBar menuBar;
	private MasterFrameManager manager;
	private JMenu charactersMenuItem;
	private JMenu spriteMenuItem;
	private JMenuItem showSelectedTilesItem;
	private JMenu tileSetMenuItem;
	private JMenu debugMenu;
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
	private JPanel systemPanel;
	private JPanel rightPanel;
	private JPanel charactersPanel;
	private JPanel spritePanel;
	public TileSet tileSetPanel;
	private JScrollPane backgroundScroll;
	private JComboBox backgroundCombo;
	private JList prefetchCombo;
	private JPanel backgroundPanel;
	private JPanel prefetchPanel;
	private JTabbedPane tabsPane;
	private JPanel leftPanel;
	private JPanel contentPanel;
	private JButton newMapTool;
	private JToggleButton gridTool;
	private JToggleButton unmappedTool;
	private JToggleButton copyPasteTool;
	private JToggleButton backTileTool;
	private JToggleButton foreTileTool;
	private JToggleButton backSpriteTool;
	private JToggleButton foreSpriteTool;
	private JToolBar toolBar;
	private JPanel toolBarContainer;
	private JPanel masterPanel;
	private AbstractAction actionSaveAs;
	private AbstractAction actionTEST;
	private AbstractAction actionNew;
	private AbstractAction actionGridTool;
	private AbstractAction actionUnmappedTool;
	private AbstractAction actionReloadConfig;
	private AbstractAction actionOpenOptionsFrame;
	private AbstractAction actionChangeTileSet;
	private AbstractAction actionExit;
	private AbstractAction actionSave;
	private AbstractAction actionLoad;
	private AbstractAction actionNewMapTool;
	private AbstractAction actionCopyPasteTool;
	private AbstractAction actionDisplayBackTileTool;
	private AbstractAction actionDisplayForeTileTool;
	private AbstractAction actionDisplayBackSpriteTool;
	private AbstractAction actionDisplayForeSpriteTool;
	
	private ZildoScrollablePanel zildoPanel;

	/**
	* Auto-generated main method to display this JFrame
	*/
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
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
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			BorderLayout thisLayout = new BorderLayout();
			getContentPane().setLayout(thisLayout);
			this.setTitle("Zeditor");
			this.setMinimumSize(new java.awt.Dimension(800, 600));
			this.setIconImage(new ImageIcon(getClass().getClassLoader().getResource("zeditor/images/map.png")).getImage());
			getContentPane().add(getMasterPanel(), BorderLayout.CENTER);
			getContentPane().add(getSystemPanel(), BorderLayout.SOUTH);
			{
				menuBar = new JMenuBar();
				setJMenuBar(menuBar);
				menuBar.add(getFileMenu());
				menuBar.add(getParametersMenu());
				menuBar.add(getDebugMenu());
			}

			// On ajoute la carte
			zildoPanel=new ZildoScrollablePanel("polaky");
	        getContentPane().add(zildoPanel, BorderLayout.EAST);

            // Recréation du manager avec les objets en paramètre
			manager = new MasterFrameManager(getSystemLabel(), getTileSetPanel(), getMasterPanel(), this, getBackgroundCombo(), zildoPanel.getZildoCanvas());

			// Initialisation de la fenêtre par le manager
			manager.init();


			pack();
			Dimension d=zildoPanel.getSize();
			this.setSize(640 + 350, d.height);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private AbstractAction getActionLoad() {
		if(actionLoad == null) {
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
		if(actionSave == null) {
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
		if(actionExit == null) {
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
		if(manager == null) {
			manager = new MasterFrameManager(this);
		}
		return manager;
	}

	private AbstractAction getActionChangeTileSet() {
		if(actionChangeTileSet == null) {
			actionChangeTileSet = new AbstractAction("Changer le TileSet", null) {
				private static final long serialVersionUID = -7877935671989785646L;

				public void actionPerformed(ActionEvent evt) {
					manager.changeTileSet(backgroundCombo.getSelectedItem().toString());
				}
			};
		}
		return actionChangeTileSet;
	}
	private AbstractAction getActionOpenOptionsFrame() {
		if(actionOpenOptionsFrame == null) {
			actionOpenOptionsFrame = new AbstractAction("Options", null) {
				private static final long serialVersionUID = -4764264927309844210L;

				public void actionPerformed(ActionEvent evt) {
					manager.openOptionsFrame();
				}
			};
		}
		return actionOpenOptionsFrame;
	}

	private AbstractAction getActionReloadConfig() {
		if(actionReloadConfig == null) {
			actionReloadConfig = new AbstractAction("Recharger la conf.", null) {
				private static final long serialVersionUID = -3565192878529677669L;

				public void actionPerformed(ActionEvent evt) {
					manager.reloadConfig();
				}
			};
		}
		return actionReloadConfig;
	}

	private AbstractAction getActionUnmappedTool() {
		if(actionUnmappedTool == null) {
			actionUnmappedTool = new AbstractAction("", null) {
				private static final long serialVersionUID = 7546834766049284479L;

				public void actionPerformed(ActionEvent evt) {
					if(unmappedTool.isSelected()){
						manager.showTileSetUnmapped(true);
					}else{
						manager.showTileSetUnmapped(false);
					}
				}
			};
		}
		return actionUnmappedTool;
	}

	private AbstractAction getActionGridTool() {
		if(actionGridTool == null) {
			actionGridTool = new AbstractAction("", null) {
				private static final long serialVersionUID = -416405688673820762L;

				public void actionPerformed(ActionEvent evt) {
					if(gridTool.isSelected()){
						manager.showTileSetGrid(true);
					}else{
						manager.showTileSetGrid(false);
					}
				}
			};
		}
		return actionGridTool;
	}

	private AbstractAction getActionCopyPasteTool() {
	    if (actionCopyPasteTool == null) {
	    	actionCopyPasteTool = new AbstractAction(null) {
			    public void actionPerformed(ActionEvent evt) {
				zildoPanel.getZildoCanvas().switchCopyMode();
			    }
			};
	    }
	    return actionCopyPasteTool;
	}
	
	private AbstractAction getActionDisplayBackTileTool() {
	    if (actionDisplayBackTileTool == null) {
			actionDisplayBackTileTool = new AbstractAction(null) {
			    public void actionPerformed(ActionEvent evt) {
				ClientEngineZildo.mapDisplay.foreBackController.toggleDisplaySpecific(false);
			    }
			};
	    }
	    return actionDisplayBackTileTool;
	}
	
	private AbstractAction getActionDisplayForeTileTool() {
	    if (actionDisplayForeTileTool == null) {
			actionDisplayForeTileTool = new AbstractAction(null) {
			    public void actionPerformed(ActionEvent evt) {
				ClientEngineZildo.mapDisplay.foreBackController.toggleDisplaySpecific(true);
			    }
			};
	    }
	    return actionDisplayForeTileTool;
	}
	
	private AbstractAction getActionDisplayBackSpriteTool() {
	    if (actionDisplayBackSpriteTool == null) {
		actionDisplayBackSpriteTool = new AbstractAction("", null) {
		    public void actionPerformed(ActionEvent evt) {
			ClientEngineZildo.spriteDisplay.foreBackController.toggleDisplaySpecific(false);
		    }
		};
	    }
	    return actionDisplayBackSpriteTool;
	}
	
	private AbstractAction getActionDisplayForeSpriteTool() {
	    if (actionDisplayForeSpriteTool == null) {
		actionDisplayForeSpriteTool = new AbstractAction("", null) {
		    public void actionPerformed(ActionEvent evt) {
			ClientEngineZildo.spriteDisplay.foreBackController.toggleDisplaySpecific(true);
		    }
		};
	    }
	    return actionDisplayForeSpriteTool;
	}

	private AbstractAction getActionNewMapTool() {
	    if (actionNewMapTool == null) {
		actionNewMapTool = new AbstractAction(null) {
		    public void actionPerformed(ActionEvent evt) {
		    	zildoPanel.getCanvas().clearMap();
		    }
		};
	    }
	    return actionNewMapTool;
	}
	
	private AbstractAction getActionNew() {
		if(actionNew == null) {
			actionNew = new AbstractAction("Nouveau", null) {
				private static final long serialVersionUID = -5578117368337210024L;

				public void actionPerformed(ActionEvent evt) {
					manager.create();
				}
			};
		}
		return actionNew;
	}

	private AbstractAction getActionTEST() {
		if(actionTEST == null) {
			actionTEST = new AbstractAction("Afficher les tuiles sélectionnées", null) {
				private static final long serialVersionUID = 3319456644340391488L;

				public void actionPerformed(ActionEvent evt) {
					manager.displaySelectedTiles();
				}
			};
		}
		return actionTEST;
	}

	private AbstractAction getActionSaveAs() {
		if(actionSaveAs == null) {
			actionSaveAs = new AbstractAction("Enregistrer sous ...", null) {
				private static final long serialVersionUID = 8663451078247792775L;

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
			BoxLayout masterPanelLayout = new BoxLayout(masterPanel, javax.swing.BoxLayout.Y_AXIS);
			masterPanel.setLayout(masterPanelLayout);
			masterPanel.add(getToolBarContainer());
			masterPanel.add(getContentPanel());
		}
		return masterPanel;
	}
	private JPanel getToolBarContainer() {
		if(toolBarContainer == null) {
			toolBarContainer = new JPanel();
			BorderLayout toolBarContainerLayout = new BorderLayout();
			toolBarContainer.setLayout(toolBarContainerLayout);
			toolBarContainer.setMaximumSize(new java.awt.Dimension(32767,32));
			toolBarContainer.add(getToolBar(), BorderLayout.CENTER);
		}
		return toolBarContainer;
	}
	private JToolBar getToolBar() {
		if(toolBar == null) {
			toolBar = new JToolBar();
			toolBar.add(getNewMapTool());
			toolBar.add(new JToolBar.Separator());
			toolBar.add(getUnmappedTool());
			toolBar.add(getGridTool());
			toolBar.add(getCopyPasteTool());
			toolBar.add(new JToolBar.Separator());
			toolBar.add(getToggleBackDisplayTool());
			toolBar.add(getToggleForeDisplayTool());
			toolBar.add(getToggleBackSpriteDisplayTool());
			toolBar.add(getToggleForeSpriteDisplayTool());

		}
		return toolBar;
	}
	public JToggleButton getUnmappedTool() {
		if(unmappedTool == null) {
			unmappedTool = new JToggleButton();
			unmappedTool.setToolTipText("Afficher les tuiles non mappées.");
			unmappedTool.setAction(getActionUnmappedTool());
			unmappedTool.setSelected(Boolean.parseBoolean(getManager().loadOption(Options.SHOW_TILES_UNMAPPED.getValue())));
			unmappedTool.setIcon(new ImageIcon(getClass().getClassLoader().getResource("zeditor/images/shading.png")));
		}
		return unmappedTool;
	}
	public JToggleButton getGridTool() {
		if(gridTool == null) {
			gridTool = new JToggleButton();
			gridTool.setAction(getActionGridTool());
			gridTool.setIcon(new ImageIcon(getClass().getClassLoader().getResource("zeditor/images/text_columns.png")));
		}
		return gridTool;
	}
	
	public JToggleButton getCopyPasteTool() {
		if(copyPasteTool == null) {
		    copyPasteTool = new JToggleButton();
		    copyPasteTool.setToolTipText("Copier une zone");
		    copyPasteTool.setAction(getActionCopyPasteTool());
		    copyPasteTool.setIcon(new ImageIcon(getClass().getClassLoader().getResource("zeditor/images/copy.PNG")));
		}
		return copyPasteTool;
	}
	
	public JToggleButton getToggleBackDisplayTool() {
		if(backTileTool == null) {
		    backTileTool = new JToggleButton();
		    backTileTool.setToolTipText("Tiles d'arrière plan");
		    backTileTool.setAction(getActionDisplayBackTileTool());
		    backTileTool.setIcon(new ImageIcon(getClass().getClassLoader().getResource("zeditor/images/backGround.PNG")));
		}
		return backTileTool;
	}
	
	public JToggleButton getToggleForeDisplayTool() {
		if(foreTileTool == null) {
		    foreTileTool = new JToggleButton();
		    foreTileTool.setToolTipText("Tiles de premier plan plan");
		    foreTileTool.setAction(getActionDisplayForeTileTool());
		    foreTileTool.setIcon(new ImageIcon(getClass().getClassLoader().getResource("zeditor/images/foreGround.PNG")));
		}
		return foreTileTool;
	}
	
	public JToggleButton getToggleBackSpriteDisplayTool() {
		if(backSpriteTool == null) {
		    backSpriteTool = new JToggleButton();
		    backSpriteTool.setToolTipText("Sprites d'arrière plan");
		    backSpriteTool.setAction(getActionDisplayBackSpriteTool());
		    backSpriteTool.setIcon(new ImageIcon(getClass().getClassLoader().getResource("zeditor/images/backGroundSprite.PNG")));
		}
		return backSpriteTool;
	}
	
	public JToggleButton getToggleForeSpriteDisplayTool() {
		if(foreSpriteTool == null) {
		    foreSpriteTool = new JToggleButton();
		    foreSpriteTool.setToolTipText("Sprites de premier plan plan");
		    foreSpriteTool.setAction(getActionDisplayForeSpriteTool());
		    foreSpriteTool.setIcon(new ImageIcon(getClass().getClassLoader().getResource("zeditor/images/foreGroundSprite.PNG")));
		}
		return foreSpriteTool;
	}
	
	public JButton getNewMapTool() {
		if(newMapTool == null) {
		    newMapTool = new JButton();
		    newMapTool.setToolTipText("Nouvelle carte");
		    newMapTool.setAction(getActionNewMapTool());
		    newMapTool.setIcon(new ImageIcon(getClass().getClassLoader().getResource("zeditor/images/page.png")));
		}
		return newMapTool;
	}

	private JPanel getContentPanel() {
		if(contentPanel == null) {
			contentPanel = new JPanel();
			BoxLayout contentPanelLayout = new BoxLayout(contentPanel, javax.swing.BoxLayout.X_AXIS);
			contentPanel.setLayout(contentPanelLayout);
			contentPanel.add(getLeftPanel());
			contentPanel.add(getRightPanel());
		}
		return contentPanel;
	}
	private JPanel getLeftPanel() {
		if (leftPanel == null) {
			leftPanel = new JPanel();
			BoxLayout leftPanelLayout = new BoxLayout(leftPanel, javax.swing.BoxLayout.Y_AXIS);
			leftPanel.setLayout(leftPanelLayout);
			leftPanel.setPreferredSize(new java.awt.Dimension(344,487));
			leftPanel.setSize(344, 539);
			leftPanel.setMaximumSize(new java.awt.Dimension(344,32767));
			leftPanel.add(getTabsPane());
		}
		return leftPanel;
	}
	public JTabbedPane getTabsPane() {
		if (tabsPane == null) {
			tabsPane = new JTabbedPane();
			tabsPane.addTab("Décors", null, getBackgroundPanel(), null);
			tabsPane.addTab("Prefetch", null, getPrefetchPanel(), null);
			tabsPane.addTab("Sprites", null, getSpritePanel(), null);
			tabsPane.addTab("Personnages", null, getCharactersPanel(), null);
		}
		return tabsPane;
	}
	private JPanel getBackgroundPanel() {
		if (backgroundPanel == null) {
			backgroundPanel = new JPanel();
			BoxLayout backgroundPanelLayout = new BoxLayout(backgroundPanel, javax.swing.BoxLayout.Y_AXIS);
			backgroundPanel.setLayout(backgroundPanelLayout);
			backgroundPanel.add(getBackgroundCombo());
			backgroundPanel.add(getBackgroundScroll());
		}
		return backgroundPanel;
	}

	private JPanel getPrefetchPanel() {
		if (prefetchPanel == null) {
			prefetchPanel = new JPanel();
			BoxLayout prefetchPanelLayout = new BoxLayout(prefetchPanel, javax.swing.BoxLayout.Y_AXIS);
			prefetchPanel.setLayout(prefetchPanelLayout);
			prefetchPanel.add(getPrefetchCombo());
		}
		return prefetchPanel;
	}
	public JList getPrefetchCombo() {
		if (prefetchCombo == null) {
			ComboBoxModel prefetchComboModel = new DefaultComboBoxModel(getManager().getPrefetchForCombo());
			prefetchCombo = new JList();
			prefetchCombo.setModel(prefetchComboModel);
			prefetchCombo.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
			prefetchCombo.setLayoutOrientation(JList.VERTICAL);
			prefetchCombo.setVisibleRowCount(-1);
			prefetchCombo.setSize(500,300);
			prefetchCombo.addListSelectionListener(new ListSelectionListener() {
				
				public void valueChanged(ListSelectionEvent e) {
				    if (e.getValueIsAdjusting() == false) {
	
						int ind=prefetchCombo.getSelectedIndex();
				        if (ind != -1) {
							MasterFrameManager.setCurrentSelection(new PrefetchSelection(Prefetch.fromInt(ind)));
				        }
				    }
				}
			});
		}
		return prefetchCombo;
	}
	public JComboBox getBackgroundCombo() {
		if (backgroundCombo == null) {
			ComboBoxModel backgroundComboModel = new DefaultComboBoxModel(getManager().loadTileForCombo());
			backgroundCombo = new JComboBox();
			backgroundCombo.setModel(backgroundComboModel);
			backgroundCombo.setSize(339, 21);
			backgroundCombo.setMaximumSize(new java.awt.Dimension(32767,21));
			backgroundCombo.setAction(getActionChangeTileSet());
		}
		return backgroundCombo;
	}
	private JScrollPane getBackgroundScroll() {
		if (backgroundScroll == null) {
			backgroundScroll = new JScrollPane();
			backgroundScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			backgroundScroll.setViewportView(getTileSetPanel());
		}
		return backgroundScroll;
	}
	public TileSet getTileSetPanel() {
		if (tileSetPanel == null) {
			tileSetPanel = new TileSet("");
		}
		return tileSetPanel;
	}
	private JPanel getSpritePanel() {
		if (spritePanel == null) {
			spritePanel = new JPanel();
		}
		return spritePanel;
	}
	private JPanel getCharactersPanel() {
		if (charactersPanel == null) {
			charactersPanel = new JPanel();
		}
		return charactersPanel;
	}
	private JPanel getRightPanel() {
		if (rightPanel == null) {
			rightPanel = new JPanel();
			rightPanel.setBackground(new java.awt.Color(255,255,255));
		}
		return rightPanel;
	}
	private JPanel getSystemPanel() {
		if(systemPanel == null) {
			systemPanel = new JPanel();
			BorderLayout systemPanelLayout = new BorderLayout();
			systemPanel.setLayout(systemPanelLayout);
			systemPanel.setPreferredSize(new java.awt.Dimension(10,20));
			systemPanel.setSize(792, 20);
			systemPanel.setMaximumSize(new java.awt.Dimension(32767,20));
			systemPanel.add(getSystemLabel(), BorderLayout.CENTER);
		}
		return systemPanel;
	}
	private JLabel getSystemLabel() {
		if(systemLabel == null) {
			systemLabel = new JLabel();
		}
		return systemLabel;
	}
	private JMenu getFileMenu() {
		if (fileMenu == null) {
			fileMenu = new JMenu();
			fileMenu.setText("Fichier");
			fileMenu.setIcon(new ImageIcon(getClass().getClassLoader().getResource("zeditor/images/application_osx_terminal.png")));
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
		if(newItem == null) {
			newItem = new JMenuItem();
			newItem.setText("Nouveau");
			newItem.setAction(getActionNew());
			newItem.setIcon(new ImageIcon(getClass().getClassLoader().getResource("zeditor/images/page_white.png")));
		}
		return newItem;
	}
	private JMenuItem getLoadItem() {
		if (loadItem == null) {
			loadItem = new JMenuItem();
			loadItem.setText("Ouvrir");
			loadItem.setAction(getActionLoad());
			loadItem.setIcon(new ImageIcon(getClass().getClassLoader().getResource("zeditor/images/folder.png")));
		}
		return loadItem;
	}
	private JMenuItem getSaveItem() {
		if (saveItem == null) {
			saveItem = new JMenuItem();
			saveItem.setText("Enregistrer");
			saveItem.setAction(getActionSave());
			saveItem.setIcon(new ImageIcon(getClass().getClassLoader().getResource("zeditor/images/disk.png")));
		}
		return saveItem;
	}
	private JMenuItem getSaveAsItem() {
		if(saveAsItem == null) {
			saveAsItem = new JMenuItem();
			saveAsItem.setText("Enregistrer sous ...");
			saveAsItem.setAction(getActionSaveAs());
			saveAsItem.setIcon(new ImageIcon(getClass().getClassLoader().getResource("zeditor/images/disk.png")));
		}
		return saveAsItem;
	}
	private JSeparator getFileSeparator1() {
		if(fileSeparator1 == null) {
			fileSeparator1 = new JSeparator();
		}
		return fileSeparator1;
	}
	private JMenuItem getExitItem() {
		if (exitItem == null) {
			exitItem = new JMenuItem();
			exitItem.setText("Quitter");
			exitItem.setAction(getActionExit());
			exitItem.setIcon(new ImageIcon(getClass().getClassLoader().getResource("zeditor/images/door.png")));
		}
		return exitItem;
	}
	private JMenu getParametersMenu() {
		if(parametersMenu == null) {
			parametersMenu = new JMenu();
			parametersMenu.setText("Parmètres");
			parametersMenu.setIcon(new ImageIcon(getClass().getClassLoader().getResource("zeditor/images/cog.png")));
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
			optionsItem.setIcon(new ImageIcon(getClass().getClassLoader().getResource("zeditor/images/wrench.png")));
			optionsItem.setBounds(-53, 21, 74, 19);
		}
		return optionsItem;
	}
	private JMenuItem getReloadConfigItem() {
		if(reloadConfigItem == null) {
			reloadConfigItem = new JMenuItem();
			reloadConfigItem.setText("Recharger la conf.");
			reloadConfigItem.setAction(getActionReloadConfig());
			reloadConfigItem.setIcon(new ImageIcon(getClass().getClassLoader().getResource("zeditor/images/arrow_refresh.png")));
		}
		return reloadConfigItem;
	}
	private JMenu getDebugMenu() {
		if(debugMenu == null) {
			debugMenu = new JMenu();
			debugMenu.setText("Debug");
			debugMenu.setIcon(new ImageIcon(getClass().getClassLoader().getResource("zeditor/images/bug.png")));
			debugMenu.add(getTileSetMenuItem());
			debugMenu.add(getSpriteMenuItem());
			debugMenu.add(getCharactersMenuItem());
		}
		return debugMenu;
	}
	private JMenu getTileSetMenuItem() {
		if(tileSetMenuItem == null) {
			tileSetMenuItem = new JMenu();
			tileSetMenuItem.setText("TileSet");
			tileSetMenuItem.setIcon(new ImageIcon(getClass().getClassLoader().getResource("zeditor/images/photo.png")));
			tileSetMenuItem.add(getShowSelectedTilesItem());
		}
		return tileSetMenuItem;
	}
	private JMenuItem getShowSelectedTilesItem() {
		if(showSelectedTilesItem == null) {
			showSelectedTilesItem = new JMenuItem();
			showSelectedTilesItem.setText("Afficher les tuiles sélectionnées");
			showSelectedTilesItem.setAction(getActionTEST());
		}
		return showSelectedTilesItem;
	}
	private JMenu getSpriteMenuItem() {
		if(spriteMenuItem == null) {
			spriteMenuItem = new JMenu();
			spriteMenuItem.setText("Sprites");
			spriteMenuItem.setIcon(new ImageIcon(getClass().getClassLoader().getResource("zeditor/images/package.png")));
		}
		return spriteMenuItem;
	}
	private JMenu getCharactersMenuItem() {
		if(charactersMenuItem == null) {
			charactersMenuItem = new JMenu();
			charactersMenuItem.setText("Personnages");
			charactersMenuItem.setIcon(new ImageIcon(getClass().getClassLoader().getResource("zeditor/images/user.png")));
		}
		return charactersMenuItem;
	}

}
