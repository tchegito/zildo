package zeditor.windows.subpanels;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import zeditor.core.tiles.TileSet;
import zeditor.windows.managers.MasterFrameManager;
import zildo.monde.map.Case;

@SuppressWarnings("serial")
public class BackgroundPanel extends JPanel {

	MasterFrameManager manager;

	JComboBox backgroundCombo;
	JScrollPane backgroundScroll;
	AbstractAction actionChangeTileSet;
	TileSet tileSetPanel;

	public BackgroundPanel(MasterFrameManager p_manager) {
		manager = p_manager;

		BoxLayout backgroundPanelLayout = new BoxLayout(this,
				javax.swing.BoxLayout.Y_AXIS);
		setLayout(backgroundPanelLayout);
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
					.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			backgroundScroll.setViewportView(getTileSetPanel());
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
