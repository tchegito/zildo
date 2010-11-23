package zeditor.windows.subpanels;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import zeditor.core.tiles.TileSet;
import zeditor.windows.managers.MasterFrameManager;

public class BackgroundPanel extends JPanel {

    MasterFrameManager manager;
    
    JComboBox backgroundCombo;
    JScrollPane backgroundScroll;
    AbstractAction actionChangeTileSet;
    TileSet tileSetPanel;
    
    public BackgroundPanel(MasterFrameManager p_manager) {
	manager=p_manager;
	
	BoxLayout backgroundPanelLayout = new BoxLayout(this, javax.swing.BoxLayout.Y_AXIS);
	setLayout(backgroundPanelLayout);
	add(getBackgroundCombo());
	add(getBackgroundScroll());	
    }

    public JComboBox getBackgroundCombo() {
	if (backgroundCombo == null) {
	    ComboBoxModel backgroundComboModel = new DefaultComboBoxModel(
		    manager.loadTileForCombo());
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

		public void actionPerformed(ActionEvent evt) {
		    manager.changeTileSet(backgroundCombo.getSelectedItem()
			    .toString());
		}
	    };
	}
	return actionChangeTileSet;
    }
    

	public TileSet getTileSetPanel() {
		if (tileSetPanel == null) {
			tileSetPanel = new TileSet("", manager);
		}
		return tileSetPanel;
	}
}
