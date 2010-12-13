package zeditor.windows.subpanels;

import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import zildo.monde.map.Area;
import zildo.server.EngineZildo;

public class StatsPanel extends JPanel {

    JLabel dim;
    JLabel nSpr;
    JLabel nPerso;
    JLabel nChainingPoint;
    JLabel nDialogs;
    
    public StatsPanel() {
	setLayout(new GridLayout(5,2));
	
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
	
	// Set
    }
    
    public void updateStats() {
	Area map=EngineZildo.mapManagement.getCurrentMap();
	int nbPerso=EngineZildo.persoManagement.tab_perso.size();
	int nbSpr=EngineZildo.spriteManagement.getSpriteEntities(null).size();
	int nbChPoint=map.getListPointsEnchainement().size();
	int nbDial=map.getMapDialog().getN_phrases();
	dim.setText(map.getDim_x() + " x "+map.getDim_y());
	nPerso.setText(String.valueOf(nbPerso));
	nSpr.setText(String.valueOf(nbSpr - nbPerso));
	nChainingPoint.setText(String.valueOf(nbChPoint));
	nDialogs.setText(String.valueOf(nbDial));
    }
    
    @Override
    public void setVisible(boolean p_flag) {
        if (p_flag) {
            updateStats();
        }
        super.setVisible(p_flag);
    }

}
