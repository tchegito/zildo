package zeditor.windows;

import java.awt.Color;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.SwingUtilities;

import zeditor.core.TileSet;
import zeditor.core.exceptions.ZeditorException;

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
@Deprecated
public class MasterFrameOld extends javax.swing.JFrame {
	private static final long serialVersionUID = 4003504688641071367L;

	{
		// Set Look & Feel
		try {
			javax.swing.UIManager.setLookAndFeel("com.jgoodies.looks.plastic.Plastic3DLookAndFeel");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private JMenuBar Menu;
	private JPanel PDecors;
	private JLabel systemMessage;
	private JPanel SystemPanel;
	private TileSet tileSetPanel;
	private AbstractAction openOptionsFrame;
	private AbstractAction reloadConfigAction;
	private JMenuItem ReloadConfig;
	private JMenuItem Options;
	private JMenu Parametrage;
	private JLabel tileInfos;
	private JMenuItem saveMap;
	private JMenuItem loadMap;
	private AbstractAction onChangeBackgroundCombo;
	private JPanel characterSet;
	private JPanel spriteSet;
	private JComboBox jComboBox2;
	private JComboBox jComboBox1;
	private JScrollPane tileSetScroll;
	private JComboBox decorCombo;
	private JPanel PPersonnage;
	private JPanel PSprite;
	private JTabbedPane Decors;
	private AbstractAction Close;
	private JPanel masterPanel;
	private JPanel leftPanel;
	private JPanel rightPanel;
	private JPanel Content;
	private JMenuItem Fichier_Quitter;
	private JMenu jMenu1;

	/**
	 * Auto-generated main method to display this JFrame
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				MasterFrameOld inst = new MasterFrameOld();
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}

	public MasterFrameOld() {
		super();
		initGUI();
	}

	private void initGUI() {
		try {
			BoxLayout thisLayout = new BoxLayout(getContentPane(), javax.swing.BoxLayout.Y_AXIS);
			getContentPane().setLayout(thisLayout);
			this.setTitle("Zeditor");
			this.setMaximumSize(new java.awt.Dimension(2147483647, 20));
			this.setIconImage(new ImageIcon(getClass().getClassLoader().getResource("zeditor/images/map.png")).getImage());
			this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			{
				Menu = new JMenuBar();
				setJMenuBar(Menu);
				{
					jMenu1 = new JMenu();
					Menu.add(jMenu1);
					Menu.add(getParametrage());
					jMenu1.setText("Fichier");
					jMenu1.setIcon(new ImageIcon(getClass().getClassLoader().getResource("zeditor/images/application_osx_terminal.png")));
					{
						Fichier_Quitter = new JMenuItem();
						jMenu1.add(getLoadMap());
						jMenu1.add(getSaveMap());
						jMenu1.add(Fichier_Quitter);
						Fichier_Quitter.setText("Quitter");
						Fichier_Quitter.setAction(getClose());
						Fichier_Quitter.setIcon(new ImageIcon(getClass().getClassLoader().getResource("zeditor/images/door.png")));
					}
				}
			}
			{
				masterPanel = new JPanel();
				getContentPane().add(masterPanel);
				getContentPane().add(getSystemPanel());
				BoxLayout MasterPanelLayout = new BoxLayout(masterPanel,
						javax.swing.BoxLayout.Y_AXIS);
				masterPanel.setLayout(MasterPanelLayout);
				masterPanel.setOpaque(false);
				{
					Content = new JPanel();
					BoxLayout ContentLayout = new BoxLayout(Content,
							javax.swing.BoxLayout.X_AXIS);
					masterPanel.add(Content);
					Content.setLayout(ContentLayout);
					Content.setBounds(0, 27, 792, 516);
					{
						leftPanel = new JPanel();
						Content.add(leftPanel);
						BoxLayout RightPanelLayout = new BoxLayout(leftPanel,
								javax.swing.BoxLayout.X_AXIS);
						leftPanel.setLayout(RightPanelLayout);
						leftPanel.setForeground(new java.awt.Color(255, 255,
								255));
						leftPanel.setBackground(new java.awt.Color(238, 238,
								238));
						leftPanel.setBounds(0, 0, 261, 543);
						leftPanel.setSize(344, 543);
						leftPanel.setPreferredSize(new java.awt.Dimension(344,
								543));
						leftPanel.add(getDecors());
						leftPanel.setFocusCycleRoot(true);
						leftPanel.setMaximumSize(new java.awt.Dimension(325,
								32767));
						leftPanel
								.setMinimumSize(new java.awt.Dimension(325, 10));
					}
					{
						rightPanel = new JPanel();
						Content.add(rightPanel);
						rightPanel.setBackground(new java.awt.Color(255, 255,
								255));
						rightPanel.setPreferredSize(new java.awt.Dimension(461,
								543));
						rightPanel.setSize(500, 543);
					}
				}
			}

			// Chargement manuel du tileSet
			tileSetScroll.setViewportView(getTileSetPanel());
			changeTileSet(decorCombo.getSelectedItem().toString());
			
			pack();
			this.setSize(800, 600);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private AbstractAction getClose() {
		if (Close == null) {
			Close = new AbstractAction("Quitter", null) {
				private static final long serialVersionUID = -5755410850857294096L;

				public void actionPerformed(ActionEvent evt) {
					System.exit(0);
				}
			};
		}
		return Close;
	}

	private JTabbedPane getDecors() {
		if (Decors == null) {
			Decors = new JTabbedPane();
			Decors.setBackground(new java.awt.Color(192, 192, 192));
			Decors.setSize(256, 566);
			Decors.setPreferredSize(new java.awt.Dimension(261, 488));
			Decors.addTab("Decors", null, getPDecors(), null);
			Decors.addTab("Sprites", null, getPSprite(), null);
			Decors.addTab("Personnages", null, getPPersonnage(), null);
		}
		return Decors;
	}

	private JPanel getPDecors() {
		if (PDecors == null) {
			PDecors = new JPanel();
			BoxLayout PDecorsLayout = new BoxLayout(PDecors, javax.swing.BoxLayout.Y_AXIS);
			PDecors.setLayout(PDecorsLayout);
			PDecors.setSize(256, 515);
			PDecors.setPreferredSize(new java.awt.Dimension(256, 10));
			PDecors.add(getDecorCombo());
			PDecors.add(getTileSetScroll());
			PDecors.add(getTileInfos());
		}
		return PDecors;
	}

	private JPanel getPSprite() {
		if (PSprite == null) {
			PSprite = new JPanel();
			BoxLayout PSpriteLayout = new BoxLayout(PSprite, javax.swing.BoxLayout.Y_AXIS);
			PSprite.setLayout(PSpriteLayout);
			PSprite.add(getJComboBox1());
			PSprite.add(getSpriteSet());
		}
		return PSprite;
	}

	private JPanel getPPersonnage() {
		if (PPersonnage == null) {
			PPersonnage = new JPanel();
			BoxLayout PPersonnageLayout = new BoxLayout(PPersonnage, javax.swing.BoxLayout.Y_AXIS);
			PPersonnage.setLayout(PPersonnageLayout);
			PPersonnage.setPreferredSize(new java.awt.Dimension(276, 317));
			PPersonnage.add(getJComboBox2());
			PPersonnage.add(getCharacterSet());
		}
		return PPersonnage;
	}

	private JComboBox getDecorCombo() {
		if (decorCombo == null) {

			decorCombo = new JComboBox();
			ComboBoxModel decorComboModel;
			try {
				decorComboModel = new DefaultComboBoxModel(TileSet.getTiles());
			} catch (ZeditorException e) {
				updateSysMsg(e.getMessage(), -1);
				decorComboModel = new DefaultComboBoxModel(new String[] { " " });
			}
			decorCombo.setModel(decorComboModel);
			decorCombo.setMaximumSize(new java.awt.Dimension(32767, 20));
			decorCombo.setAction(getOnChangeBackgroundCombo());
			decorCombo.setSize(256, 20);
			decorCombo.setPreferredSize(new java.awt.Dimension(256, 20));
		}
		return decorCombo;
	}

	private JScrollPane getTileSetScroll() {
		if (tileSetScroll == null) {
			tileSetScroll = new JScrollPane();
			tileSetScroll.setPreferredSize(new java.awt.Dimension(287, 495));
			tileSetScroll.setSize(256, 495);
			tileSetScroll.setAutoscrolls(true);
			tileSetScroll.setEnabled(false);
			tileSetScroll
					.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			tileSetScroll.add(getTileSetPanel());
		}
		return tileSetScroll;
	}

	private JComboBox getJComboBox1() {
		if (jComboBox1 == null) {
			ComboBoxModel jComboBox1Model = new DefaultComboBoxModel(
					new String[] { "Item One", "Item Two" });
			jComboBox1 = new JComboBox();
			jComboBox1.setModel(jComboBox1Model);
			jComboBox1.setMaximumSize(new java.awt.Dimension(32767, 20));
			jComboBox1.setPreferredSize(new java.awt.Dimension(272, 20));
			jComboBox1.setSize(272, 20);
		}
		return jComboBox1;
	}

	private JComboBox getJComboBox2() {
		if (jComboBox2 == null) {
			ComboBoxModel jComboBox2Model = new DefaultComboBoxModel(
					new String[] { "Item One", "Item Two" });
			jComboBox2 = new JComboBox();
			jComboBox2.setModel(jComboBox2Model);
			jComboBox2.setMaximumSize(new java.awt.Dimension(32767, 20));
			jComboBox2.setPreferredSize(new java.awt.Dimension(272, 20));
			jComboBox2.setSize(272, 20);
		}
		return jComboBox2;
	}

	private JPanel getSpriteSet() {
		if (spriteSet == null) {
			spriteSet = new JPanel();
		}
		return spriteSet;
	}

	private JPanel getCharacterSet() {
		if (characterSet == null) {
			characterSet = new JPanel();
		}
		return characterSet;
	}

	private AbstractAction getOnChangeBackgroundCombo() {
		if (onChangeBackgroundCombo == null) {
			onChangeBackgroundCombo = new AbstractAction(
					"onChangeBackgroundCombo", null) {
				private static final long serialVersionUID = 2877166435611205742L;

				public void actionPerformed(ActionEvent evt) {
					try {
						tileSetPanel.changeTile(decorCombo.getSelectedItem().toString());
						updateSysMsg();
					} catch (ZeditorException e) {
						updateSysMsg(e.getMessage(), -1);
					}
				}
			};
		}
		return onChangeBackgroundCombo;
	}

	private void changeTileSet(String p_name) {
		try {
			tileSetPanel.changeTile(p_name);
		} catch (ZeditorException e) {
			updateSysMsg(e.getMessage(), -1);
		}
	}

	private JMenuItem getLoadMap() {
		if (loadMap == null) {
			loadMap = new JMenuItem();
			loadMap.setText("Charger");
			loadMap.setEnabled(false);
			loadMap.setIcon(new ImageIcon(getClass().getClassLoader()
					.getResource("zeditor/images/folder.png")));
		}
		return loadMap;
	}

	private JMenuItem getSaveMap() {
		if (saveMap == null) {
			saveMap = new JMenuItem();
			saveMap.setText("Sauver");
			saveMap.setEnabled(false);
			saveMap.setIcon(new ImageIcon(getClass().getClassLoader()
					.getResource("zeditor/images/disk.png")));
		}
		return saveMap;
	}

	private TileSet getTileSetPanel() {
		if (tileSetPanel == null) {
			tileSetPanel = new TileSet();
		}
		return tileSetPanel;
	}

	private JPanel getSystemPanel() {
		if (SystemPanel == null) {
			SystemPanel = new JPanel();
			BoxLayout SystemPanelLayout = new BoxLayout(SystemPanel,
					javax.swing.BoxLayout.X_AXIS);
			SystemPanel.setLayout(SystemPanelLayout);
			SystemPanel.setSize(792, 24);
			SystemPanel.setPreferredSize(new java.awt.Dimension(216, 23));
			SystemPanel.add(getSystemMessage());
		}
		return SystemPanel;
	}

	private JLabel getSystemMessage() {
		if (systemMessage == null) {
			systemMessage = new JLabel();
			BoxLayout systemMessageLayout = new BoxLayout(systemMessage,
					javax.swing.BoxLayout.Y_AXIS);
			systemMessage.setLayout(systemMessageLayout);
			systemMessage.setText("");
			systemMessage.setBounds(5, 5, 782, 16);
			systemMessage.setPreferredSize(new java.awt.Dimension(217, 14));
			systemMessage.setHorizontalAlignment(SwingConstants.RIGHT);
		}
		return systemMessage;
	}

	private JLabel getTileInfos() {
		if (tileInfos == null) {
			tileInfos = new JLabel();
			tileInfos.setText("jLabel1");
		}
		return tileInfos;
	}

	private JMenu getParametrage() {
		if (Parametrage == null) {
			Parametrage = new JMenu();
			Parametrage.setText("Paramétrage");
			Parametrage.setIcon(new ImageIcon(getClass().getClassLoader()
					.getResource("zeditor/images/cog.png")));
			Parametrage.add(getOptions());
			Parametrage.add(getReloadConfig());
		}
		return Parametrage;
	}

	private JMenuItem getOptions() {
		if (Options == null) {
			Options = new JMenuItem();
			Options.setText("Options");
			Options.setAction(getOpenOptionsFrame());
			Options.setIcon(new ImageIcon(getClass().getClassLoader()
					.getResource("zeditor/images/bullet_wrench.png")));
		}
		return Options;
	}

	private AbstractAction getOpenOptionsFrame() {
		if (openOptionsFrame == null) {
			openOptionsFrame = new AbstractAction("Options", null) {
				private static final long serialVersionUID = 2504166520928298260L;

				public void actionPerformed(ActionEvent evt) {
					OptionsFrame optFrame = new OptionsFrame();
					optFrame.setLocationRelativeTo(null);
					optFrame.setVisible(true);
				}
			};
		}
		return openOptionsFrame;
	}

	/**
	 * Méthode d'update du message système en bas de la fenêtre
	 * 
	 * @param p_msg
	 *            String : Message à afficher
	 * @param p_type
	 *            int : code du niveau de message ( -1 : Erreur | 0 : Info | 1 :
	 *            Warning )
	 */
	private void updateSysMsg(String p_msg, Integer p_type) {
		JLabel sysLabel = getSystemMessage();
		switch (p_type) {
		case -1:
			sysLabel.setForeground(new Color(255, 0, 0));
			break;
		case 1:
			break;
		case 0:
			sysLabel.setForeground(new Color(0, 0, 0));
			break;
		}
		sysLabel.setText(p_msg);
	}

	private void updateSysMsg() {
		updateSysMsg("", 0);
	}

	/**
	 * Méthode de rechargement des paramètres
	 */
	private void reloadConfig() {
		//System.out.println("Rechargement de la configuration...");
		//this.removeAll();
		//initGUI();
		masterPanel = null;
		add(getContentPane());
		repaint();
	}

	private JMenuItem getReloadConfig() {
		if (ReloadConfig == null) {
			ReloadConfig = new JMenuItem();
			ReloadConfig.setText("Recharger le paramétrage");
			ReloadConfig.setAction(getReloadConfigAction());
			ReloadConfig.setIcon(new ImageIcon(getClass().getClassLoader()
					.getResource("zeditor/images/arrow_refresh.png")));
		}
		return ReloadConfig;
	}

	private AbstractAction getReloadConfigAction() {
		if (reloadConfigAction == null) {
			reloadConfigAction = new AbstractAction("Recharger la config.",	null) {
				private static final long serialVersionUID = -7877935671989785646L;

				public void actionPerformed(ActionEvent evt) {
					reloadConfig();
				}
			};
		}
		return reloadConfigAction;
	}
}
