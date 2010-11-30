package zeditor.core.tiles;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.ImageIcon;

import zeditor.core.Options;
import zeditor.core.exceptions.TileSetException;
import zeditor.core.exceptions.ZeditorException;
import zeditor.core.selection.CaseSelection;
import zeditor.helpers.OptionHelper;
import zeditor.tools.CorrespondanceGifDec;
import zeditor.tools.Transparency;
import zeditor.windows.managers.MasterFrameManager;
import zildo.client.ClientEngineZildo;
import zildo.fwk.ZUtils;
import zildo.monde.map.Case;

/**
 * Classe de gestion des Tuiles
 * @author Drakulo
 */
@SuppressWarnings("serial")
public class TileSet extends ImageSet {

    private CorrespondanceGifDec bridge;
    
    boolean blockSet=false;    // If we are on a map region selected by user

    /**
     * Constructeur avec param�tres
     * @param p_tileName : Nom du set de tuiles en cours
     * @author Drakulo
     */
    public TileSet(String p_tileName, MasterFrameManager p_manager) {
    	super(p_tileName, p_manager);
    	
        // Construction du pont de correspondance
        bridge = new CorrespondanceGifDec();
        bridge.init();

    }

    private Image getTileNamed(String p_name) {

        Image tile=tiles.get(p_name);
        if (tile == null) {
	        // On charge l'image
        	ImageIcon icon=null;
        	try {
        		icon = new ImageIcon(getPath() + p_name + ".gif");
        	} catch (Exception e) {
        		
        	}
	        // On v�rifie la largeur
	        if(icon.getIconWidth() != 320){
	            tile = null;
	            throw new RuntimeException("Le fichier de Tiles est trop large :" + icon.getIconWidth() + "px au lieu de 320px.");
	        }
	       
	        tile = icon.getImage();
	        
			
	        tile = Transparency.makeColorTransparent(tile, Transparency.BANK_TRANSPARENCY);
	        tiles.put(p_name, tile);
        }
        return tile;
    }
    
    private String getPath() throws TileSetException{
        
        // On r�cup�re le chemin param�tr� (si il est param�tr�)
        String path = OptionHelper.loadOption(Options.TILES_PATH.getValue());
        if ("".equals(path) || path == null) {
            path = "tiles/";
        }

        // On teste l'existance
        File f = new File(path);
        if (!f.exists()) {
            throw new TileSetException("Le chemin sp�cifi� pour la banque de tiles n'existe pas");
        }
        return path;
    }
    
    class CallbackImageObserver implements ImageObserver {
    	public boolean imageUpdate(Image img, int infoflags, int x, int y,
    			int width, int height) {
    		tileHeight=height;
    		tileWidth=width;
    		return false;
    	}
    }
    
    ImageObserver imgObserver=new CallbackImageObserver();
    
    /**
     * M�thode de changement du tile
     * @param p_url chemin de l'image du Tile � charger
     * @author Drakulo
     * @throws ZeditorException
     */
    public void changeTile(String p_name) throws ZeditorException {
        // On renomme le tile
        tileName = p_name;

        if (tileName.indexOf("*") != -1) {
            currentTile=null;
            repaint();
            return;
        }
        // On supprime les points de s�lection pr�c�dents
        startPoint = null;
        stopPoint = null;

        currentTile=getTileNamed(p_name);
        
        // R�cup�ration de la taille de l'image
        
        tileWidth = currentTile.getWidth(imgObserver);
        tileHeight = currentTile.getHeight(imgObserver);
        // Si la hauteur n'est pas un multiple de 16, on tronque la taille au multiple inf�rieur
        if (tileHeight % 16 != 0){
            tileHeight -= tileHeight % 16;
        }

       
        // On repaint pour afficher le r�sultat
        repaint();
       
        blockSet=false;
    }

    /**
     * M�thode de chargement dynamique des tiles pr�sents dans le dossier d�fini
     * des Tiles.
     * @return Object[] : Un tableau de String contenant le nom des fichiers images (sans l'extension)
     * @throws ZeditorException
     * @author Drakulo
     */
    public Object[] getTiles() throws ZeditorException {
        // R�cup�ration des fichiers du dossier de tiles
        File[] files = null;
        List<String> list = new ArrayList<String>();
        String path = OptionHelper.loadOption(Options.TILES_PATH.getValue());
        if ("".equals(path) || path == null) {
            path = "tiles/";
        }
        path=path.trim();
        File dir = new File(path);
        if (!dir.exists()) {
            throw new TileSetException("Le chemin sp�cifi� pour la banque de tiles n'existe pas");
        }
        files = dir.listFiles();
        if (files == null) {
            throw new TileSetException("Le dossier param�tr� est vide");
        }
        int i = 0;
        for (File file : files) {
            String fileName = file.getName();
            // On ajoute � la liste que si l'�l�ment est une image GIF
            if(fileName.endsWith(".gif") || fileName.endsWith(".GIF")){
            	String name=fileName.substring(0, (fileName.length() - 4));
            	getTileNamed(name);
                list.add(name);
            }
            i++;
        }
        if (list.isEmpty()) {
            throw new TileSetException(
            "Le dossier param�tr� ne contient aucun tileSet");
        }
        list.add("*block*");
        return list.toArray();
    }
    
    @Override
    protected void specificPaint(Graphics2D p_g2d) {
	    if(currentTile != null && bridge != null && !blockSet){
	        // Selon le param�trage :
	        if(Boolean.parseBoolean(OptionHelper.loadOption(Options.SHOW_TILES_UNMAPPED.getValue()))){
	            showUnmappedTiles(p_g2d);
	        }
	        if(Boolean.parseBoolean(OptionHelper.loadOption(Options.SHOW_TILES_GRID.getValue()))){
	            showGrid(p_g2d);
	        }
	    }
    }

    /**
     * M�thode priv�e d'affichage des tuiles non mapp�es
     * @param g le Graphics concern�
     * @author Drakulo
     */
    private void showUnmappedTiles(Graphics g){
        for(int j = 0; j < currentTile.getHeight(null); j+=16){
            for(int i = 0; i < currentTile.getWidth(null); i+=16){
                if(bridge.getMotifParPoint(tileName, i, j) < 0){
                    g.setColor(Color.red);

                    // Lignes obliques montantes de gauche � droite (/)
                    g.drawLine(i+4, j, i, j+4);
                    g.drawLine(i+8, j, i, j+8);
                    g.drawLine(i+12, j, i, j+12);
                    g.drawLine(i+16, j, i, j+16);
                    g.drawLine(i+16, j+4, i+4, j+16);
                    g.drawLine(i+16, j+8, i+8, j+16);
                    g.drawLine(i+16, j+12, i+12, j+16);

                    // Lignes obliques descendantes de gauche � droite (\)
                    g.drawLine(i, j, i+16, j+16);
                    g.drawLine(i+4, j, i+16, j+12);
                    g.drawLine(i+8, j, i+16, j+8);
                    g.drawLine(i+12, j, i+16, j+4);
                    g.drawLine(i, j+4, i+12, j+16);
                    g.drawLine(i, j+8, i+8, j+16);
                    g.drawLine(i, j+12, i+4, j+16);

                }
            }
        }
    }

    /**
     * M�thode priv�e d'affichage de la grille sur le TileSet
     * @param g le Graphics concern�
     * @author Drakulo
     */
    private void showGrid(Graphics g){
        g.setColor(Color.gray);
        // Grille horitontale
        for(int i = 0; i < currentTile.getHeight(null); i+=16){
            g.drawLine(0, i, currentTile.getWidth(null), i);
        }

        // Grille verticale
        for(int j = 0; j < currentTile.getWidth(null); j+=16){
            g.drawLine(j, 0, j, currentTile.getHeight(null));
        }
    }
   
    /**
     * M�thode priv�e de construction de la s�lection
     */
    protected void buildSelection(){
        if (blockSet) {
        	return;
        }
        int startX, startY, stopX, stopY, width, height;
        width = 0;
        height = 0;
        List<Case> list = new ArrayList<Case>();
       
        startX = startPoint.x;
        startY = startPoint.y;
        stopX = stopPoint.x;
        stopY = stopPoint.y;
       
        Case c;
        int bank=ClientEngineZildo.tileEngine.getBankFromName(tileName);
        for(int i = startY; i <= stopY; i+=16){
            for(int j = startX; j <= stopX; j+= 16){
            	int nMotif=bridge.getMotifParPoint(tileName, j, i);
            	if (nMotif == -1) {
            		list.add(null);
            	} else {
	                c=new Case();
	                c.setN_banque(bank);
	                c.setN_motif(bridge.getMotifParPoint(tileName, j, i));
	                list.add(c);
            	}
                // On ne compte la largeur que pour la premi�re ligne
                if(height == 0){
                    width ++;
                }
            }
            height ++;
        }
        currentSelection = new TileSelection(width, height, list);
        manager.setCaseSelection((CaseSelection) currentSelection);
    }
   
    /**
     * Build a selection from a Case's list (when user copy a section of the map)
     * @param width
     * @param height
     * @param p_cases
     */
    public void buildSelection(int width, int height, List<Case> p_cases) {
           
        currentSelection = new TileSelection(width, height, p_cases);
        manager.getZildoCanvas().setCursorSize(width, height);

        currentTile=new BufferedImage(width*16, height*16, BufferedImage.TYPE_INT_RGB);
        // We have to redraw the cases on the image
        Iterator<Case> itCase=p_cases.iterator();
        for (int j=0;j<height;j++) {
        	for (int i=0;i<width;i++) {
        		Case theCase=itCase.next();
        		int nBank=theCase.getN_banque();
        		drawMotif(i, j, nBank & 63, theCase.getN_motif(), false);
        		if (nBank>127) {
            		drawMotif(i, j, theCase.getN_banque_masque() & 63, theCase.getN_motif_masque(), true);
        		}
        	}
        }

        tileWidth=currentTile.getWidth(null);
        tileHeight=currentTile.getHeight(null);
        repaint();
        blockSet=true;
    }
   
    /**
     * Display tile, with or without mask
     * @param i
     * @param j
     * @param nBank
     * @param nMotif
     * @param masque
     */
    private void drawMotif(int i, int j, int nBank, int nMotif, boolean masque) {
		String bankName=ClientEngineZildo.tileEngine.getBankNameFromInt(nBank & 127);
		Image bankTile=getTileNamed(bankName);
		Point p=bridge.getPointParMotif(bankName, nMotif);
		Color col=masque ? null : Transparency.BANK_TRANSPARENCY;
	    Graphics g=currentTile.getGraphics();
	    while (false ==
			g.drawImage(bankTile, i*16, j*16, i*16+16, j*16+16,
					    p.x, p.y, p.x+16, p.y+16, col, null)) {
	    	// If image isn't ready yet, we wait for it then retry
	    	ZUtils.sleep(100);
	    }
    }
    
    /**
     * Getter de la s�lection courante sur le TileSet
     * @return la s�lection courante
     */
    public TileSelection getCurrentSelection() {
        return (TileSelection) currentSelection;
    }
}