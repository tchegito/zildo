package zeditor.windows.subpanels;

import java.awt.Component;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import zildo.monde.dialog.MapDialog;
import zildo.monde.map.Area;
import zildo.server.EngineZildo;

@SuppressWarnings("serial")
public class StatsPanel extends JPanel {

    JLabel dim;
    JLabel nSpr;
    JLabel nPerso;
    JLabel nChainingPoint;
    JLabel nDialogs;
	JSpinner spinLimitX;
	JSpinner spinLimitY;
 
    public StatsPanel() {
	setLayout(new GridLayout(7,2));
	
	add(new JLabel("Dimension"));
	add(dim = new JLabel(""));
	add(new JLabel("Sprites"));
	add(nSpr = new JLabel(""));
	add(new JLabel("Personnages"));
	add(nPerso = new JLabel(""));
	add(new JLabel("Points d'enchainement"));
	add(nChainingPoint = new JLabel(""));
	add(new JLabel("Dialogues"));
	add(nDialogs = new JLabel(""));
	
	spinLimitX=new JSpinner(new SpinnerNumberModel(1, 1, 64, -1));
	spinLimitY=new JSpinner(new SpinnerNumberModel(1, 1, 64, -1));

	add(new JLabel("Taille X"));
	add(spinLimitX);
	
	add(new JLabel("Taille Y"));
	add(spinLimitY);

    }
    
    public void updateStats() {
		Area map=EngineZildo.mapManagement.getCurrentMap();
		int nbPerso=EngineZildo.persoManagement.tab_perso.size();
		int nbSpr=EngineZildo.spriteManagement.getSpriteEntities(null).size();
		int nbChPoint=map.getListPointsEnchainement().size();
		MapDialog dialogs=map.getMapDialog();
		int nbDial=dialogs == null ? 0 : dialogs.getN_phrases();
		dim.setText(map.getDim_x() + " x "+map.getDim_y());
		nPerso.setText(String.valueOf(nbPerso));
		nSpr.setText(String.valueOf(nbSpr - nbPerso));
		nChainingPoint.setText(String.valueOf(nbChPoint));
		nDialogs.setText(String.valueOf(nbDial));
		
		spinLimitX.setValue(map.getDim_x());
		spinLimitY.setValue(map.getDim_y());
		ChangeListener listener=new StatsFieldsListener();
		spinLimitX.addChangeListener(listener);
		spinLimitY.addChangeListener(listener);
    }
    
    @Override
    public void setVisible(boolean p_flag) {
        if (p_flag) {
            updateStats();
        }
        super.setVisible(p_flag);
    }

	class StatsFieldsListener implements ChangeListener {

		@Override
		public void stateChanged(ChangeEvent changeevent) {
			
			// If we are focusing on an existing sprite, then update his attributes
			Component comp=(Component) changeevent.getSource();
			if (comp instanceof JSpinner) {
				int val=(Integer) ((JSpinner) comp).getValue();
				Area map=EngineZildo.mapManagement.getCurrentMap();
				
				if (comp == spinLimitX) {
					map.setDim_x(val);
				} else if (comp == spinLimitY) {
					map.setDim_y(val);
				}
			}	
		}
	}
}
