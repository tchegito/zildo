package zeditor.windows.subpanels;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;

import zeditor.core.tiles.TileSet;
import zeditor.fwk.awt.ZildoCanvas.ZEditMode;
import zeditor.windows.managers.MasterFrameManager;
import zildo.monde.map.Case;

@SuppressWarnings("serial")
public class BackgroundPanel extends JPanel {

	MasterFrameManager manager;

	JComboBox backgroundCombo;
	JScrollPane backgroundScroll;
	AbstractAction actionChangeTileSet;
	TileSet tileSetPanel;
	
	JRadioButton radioDraw;
	JRadioButton radioReverse;
	JRadioButton radioRotate;
	JRadioButton radioRaise;
	JRadioButton radioLower;
	JRadioButton radioRemove;
	
	ButtonGroup group;
	JPanel line;
	Action radioAction;
	
	public BackgroundPanel(MasterFrameManager p_manager) {
		manager = p_manager;

		BoxLayout backgroundPanelLayout = new BoxLayout(this,
				javax.swing.BoxLayout.Y_AXIS);
		setLayout(backgroundPanelLayout);
		add(getPanelCheckBox());
		add(getBackgroundCombo());
		add(getBackgroundScroll());
	}

	public JComboBox getBackgroundCombo() {
		if (backgroundCombo == null) {
			ComboBoxModel backgroundComboModel = new DefaultComboBoxModel(
					loadTileForCombo());
			backgroundCombo = new JComboBox();
			backgroundCombo.setModel(backgroundComboModel);
			backgroundCombo.setSize(339, 21);
			backgroundCombo.setMaximumSize(new java.awt.Dimension(32767, 21));
			backgroundCombo.setAction(getActionChangeTileSet());
		}
		return backgroundCombo;
	}

	private JScrollPane getBackgroundScroll() {
		if (backgroundScroll == null) {
			backgroundScroll = new JScrollPane();
			backgroundScroll
					.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
			backgroundScroll.setViewportView(getTileSetPanel());
			backgroundScroll.setVerticalScrollBar(new TweekedScrollbar());
		}
		return backgroundScroll;
	}

	private AbstractAction getActionChangeTileSet() {
		if (actionChangeTileSet == null) {
			actionChangeTileSet = new AbstractAction("Changer le TileSet", null) {
				private static final long serialVersionUID = -7877935671989785646L;

				@Override
				public void actionPerformed(ActionEvent evt) {
					manager.changeTileSet(backgroundCombo.getSelectedItem()
							.toString());
				}
			};
		}
		return actionChangeTileSet;
	}

	private JPanel getPanelCheckBox() {
		JPanel panel = new JPanel();
		radioAction = new AbstractAction() {
			
			ZEditMode previous;

			@Override
			public void actionPerformed(ActionEvent e) {
				
				ZEditMode newMode;
				ZEditMode m = manager.getZildoCanvas().getMode();
				if (radioRotate.isSelected()) {
					newMode = ZEditMode.TILE_ROTATE_EDIT;
					if (!m.isTileAttributeLinked()) {
						previous = m;
					}
				} else if (radioReverse.isSelected()) {
					newMode = ZEditMode.TILE_REVERSE_EDIT;
					if (!m.isTileAttributeLinked()) {
						previous = m;
					}
				} else if (radioRaise.isSelected()) {
					newMode = ZEditMode.TILE_RAISE_EDIT;
					if (!m.isTileAttributeLinked()) {
						previous = m;
					}
				} else if (radioLower.isSelected()) {
					newMode = ZEditMode.TILE_LOWER_EDIT;
					if (!m.isTileAttributeLinked()) {
						previous = m;
					}
				} else if (radioRemove.isSelected()) {
					newMode = ZEditMode.TILE_REMOVE_EDIT;
					if (!m.isTileAttributeLinked()) {
						previous = m;
					}
				} else {
					newMode = previous;
				}
				if (newMode != null) {
					manager.getZildoCanvas().setMode(newMode);
				}
			}
		};
		radioDraw = new JRadioButton("Draw");
		radioReverse = new JRadioButton("Rev");
		radioRotate = new JRadioButton("Rot");
		radioRaise =new JRadioButton("Raise");
		radioLower =new JRadioButton("Lower");
		radioRemove =new JRadioButton("Remove");
		group = new ButtonGroup();
		line = new JPanel();
		addRadio(radioDraw);
		addRadio(radioReverse);
		addRadio(radioRotate);
		addRadio(radioRaise);
		addRadio(radioLower);
		addRadio(radioRemove);
		radioDraw.setSelected(true);
		
		panel.add(line);
		return panel;
	}
	
	private void addRadio(JRadioButton radio) {
		group.add(radio);
		line.add(radio);
		radio.addActionListener(radioAction);
	}
	
	/**
	 * Charge la liste des TileSets pour la combo de décors
	 * 
	 * @return Un tableau de String des titres des TileSets
	 * @author Drakulo
	 */
	public Object[] loadTileForCombo() {
		return getTileSetPanel().getTiles();
	}

	public TileSet getTileSetPanel() {
		if (tileSetPanel == null) {
			tileSetPanel = new TileSet("", manager);
		}
		return tileSetPanel;
	}

	public void switchCopyTile(int p_width, int p_height, List<Case> p_cases) {
		backgroundCombo.selectWithKeyChar('*');
		tileSetPanel.buildSelection(p_width, p_height, p_cases);
	}
}
