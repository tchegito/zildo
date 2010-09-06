package zeditor.core.tiles;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

import zeditor.core.Options;
import zeditor.core.exceptions.TileSetException;
import zeditor.core.exceptions.ZeditorException;
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
public class TileSet extends JPanel {
    private static final long serialVersionUID = 8712246788178837311L;

    private String tileName;
    private CorrespondanceGifDec bridge;
    private Point startPoint;
    private Point stopPoint;
    private Map<String, Image> tiles;
    private Image currentTile;
    private Integer tileWidth;
    private Integer tileHeight;
    private TileSelection currentSelection;

    boolean blockSet=false;    // If we are on a map region selected by user
   
    /**
     * Constructeur vide
     */
    public TileSet() {
    	tiles=new HashMap<String, Image>();
    }

    /**
     * Constructeur avec paramètres
     * @param p_tileName : Nom du set de tuiles en cours
     * @author Drakulo
     */
    public TileSet(String p_tileName) {
    	this();
        // Définition du layout afin d'afficher le Tile en haut du conteneur
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        tileName = p_tileName;

        // Construction du pont de correspondance
        bridge = new CorrespondanceGifDec();
        bridge.init();

        // On ajoute le mouseListene pour détecter les actions à la souris
        this.addMouseListener(new MouseListener() {
            public void mousePressed(MouseEvent e) {
                if(currentTile != null){
                    if(MouseEvent.BUTTON1 == e.getButton()){
                        // On réinitialise les points pour la nouvelle sélection
                        startPoint = null;
                        stopPoint = null;
   
                        int x = 16 * (e.getX() / 16);
                        int y = 16 * (e.getY() / 16);
                        if((x >= 0 && x <= tileWidth - 16) && (y >= 0 && y <= tileHeight - 16)){
                            if(startPoint == null){
                                startPoint = new Point(x,y);
                            }else{
                                startPoint.setLocation(x, y);
                            }
                            repaint();
                        }
                    }else if (MouseEvent.BUTTON3 == e.getButton()){
                        // Click droit
                    }
                }
            }
            public void mouseClicked(MouseEvent e) {
            }
            public void mouseEntered(MouseEvent arg0) {}
            public void mouseExited(MouseEvent arg0) {}
            public void mouseReleased(MouseEvent e) {
                if(currentTile != null){
                    if(MouseEvent.BUTTON1 == e.getButton()){
                        // Click gauche
                        int x = 16 * (e.getX() / 16);
                        int y = 16 * (e.getY() / 16);
                        if(x < 0){x = 0;}else if (x >= tileWidth){x = tileWidth - 16;}
                        if(y < 0){y = 0;}else if (y >= tileHeight){y = tileHeight - 16;}
                        if(stopPoint == null){
                            stopPoint = new Point(x,y);
                        }else{
                            stopPoint.setLocation(x, y);
                        }               
                       
                        if(startPoint != null){
                            // On trie les points si le startPoint est valide
                            sortPoints();
                            // On construit la nouvelle sélection
                            buildSelection();
                        }
                        repaint();
                       
                    }else if (MouseEvent.BUTTON3 == e.getButton()){
                        // Click droit
   
                    }
                }
            }
        });
        this.addMouseMotionListener(new MouseMotionListener(){
            public void mouseDragged(MouseEvent e) {
                if(currentTile != null){
                    int x = 16 * (e.getX() / 16);
                    int y = 16 * (e.getY() / 16);
                    if(x < 0){x = 0;}else if (x >= tileWidth){x = tileWidth - 16;}
                    if(y < 0){y = 0;}else if (y >= tileHeight){y = tileHeight - 16;}
                    if(stopPoint == null){
                        stopPoint = new Point(x,y);
                    }else{
                        stopPoint.setLocation(x, y);
                    }
                    // Repaint du Tile
                    repaint();
                }
            }
            public void mouseMoved(MouseEvent arg0) {}
        });
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
	        // On vérifie la largeur
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
        
        // On récupère le chemin paramétré (si il est paramétré)
        String path = OptionHelper.loadOption(Options.TILES_PATH.getValue());
        if ("".equals(path) || path == null) {
            path = "tiles/";
        }

        // On teste l'existance
        File f = new File(path);
        if (!f.exists()) {
            throw new TileSetException("Le chemin spécifié pour la banque de tiles n'existe pas");
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
     * Méthode de changement du tile
     * @param p_url chemin de l'image du Tile à charger
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
        // On supprime les points de sélection précédents
        startPoint = null;
        stopPoint = null;

        currentTile=getTileNamed(p_name);
        
        // Récupération de la taille de l'image
        
        tileWidth = currentTile.getWidth(imgObserver);
        tileHeight = currentTile.getHeight(imgObserver);
        // Si la hauteur n'est pas un multiple de 16, on tronque la taille au multiple inférieur
        if (tileHeight % 16 != 0){
            tileHeight -= tileHeight % 16;
        }

       
        // On repaint pour afficher le résultat
        repaint();
       
        blockSet=false;
    }

    /**
     * Méthode de chargement dynamique des tiles présents dans le dossier défini
     * des Tiles.
     * @return Object[] : Un tableau de String contenant le nom des fichiers images (sans l'extension)
     * @throws ZeditorException
     * @author Drakulo
     */
    public Object[] getTiles() throws ZeditorException {
        // Récupération des fichiers du dossier de tiles
        File[] files = null;
        List<String> list = new ArrayList<String>();
        String path = OptionHelper.loadOption(Options.TILES_PATH.getValue());
        if ("".equals(path) || path == null) {
            path = "tiles/";
        }
        path=path.trim();
        File dir = new File(path);
        if (!dir.exists()) {
            throw new TileSetException("Le chemin spécifié pour la banque de tiles n'existe pas");
        }
        files = dir.listFiles();
        if (files == null) {
            throw new TileSetException("Le dossier paramétré est vide");
        }
        int i = 0;
        for (File file : files) {
            String fileName = file.getName();
            // On ajoute à la liste que si l'élément est une image GIF
            if(fileName.endsWith(".gif") || fileName.endsWith(".GIF")){
            	String name=fileName.substring(0, (fileName.length() - 4));
            	getTileNamed(name);
                list.add(name);
            }
            i++;
        }
        if (list.isEmpty()) {
            throw new TileSetException(
            "Le dossier paramétré ne contient aucun tileSet");
        }
        list.add("*block*");
        return list.toArray();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void paint(Graphics g){
        // Redimensionnement de la taille du TileSet pour le Scroll automatique
        g.clearRect(0, 0, getWidth(), getHeight());
        if(currentTile != null){
            setPreferredSize(new Dimension(currentTile.getWidth(null), currentTile.getHeight(null)));
        }else{
            setPreferredSize(new Dimension(0, 0));
        }
        revalidate();
        
        // Repaint
        Graphics2D g2d = (Graphics2D) g;
        while (!g2d.drawImage(currentTile, 0, 0, Transparency.BANK_TRANSPARENCY, null)) {
        	// If image isn't ready yet, wait then retry
        	ZUtils.sleep(100);
        }
        
        if(currentTile != null && bridge != null && !blockSet){
            // Selon le paramétrage :
            if(Boolean.parseBoolean(OptionHelper.loadOption(Options.SHOW_TILES_UNMAPPED.getValue()))){
                showUnmappedTiles(g2d);
            }
            if(Boolean.parseBoolean(OptionHelper.loadOption(Options.SHOW_TILES_GRID.getValue()))){
                showGrid(g2d);
            }
        }
        // On dessine un cadre autour des tuiles sélectionnées en dernier pour qu'il soit au dessus
        if(stopPoint == null){
            // Le point stopDrag est null donc on met le cadre sur une simple case
            drawRectangle(g2d, Color.black, Color.white, startPoint, startPoint);
        }else{
            // Le point de stopDrag n'est pas null donc on doit tracer un rectangle sur plusieurs cases
            drawRectangle(g2d, Color.black, Color.white, startPoint, stopPoint);
        }
        g2d.dispose();

    }

    /**     * Méthode de tracé du cadre autour des tuiles sélectionnées
     * @param g : Graphics sur lequel on va dessiner
     * @param outer : Couleur de l'extérieur de cadre
     * @param inner : Couleur de l'intérieur de cadre
     * @param startPoint : Point de départ du cadre
     * @param stopPoint : Point de fin du cadre
     * @author Drakulo
     */
    private void drawRectangle(Graphics g, Color outer, Color inner, Point startPoint, Point stopPoint){
        if(startPoint != null && stopPoint != null){
            int xDep, yDep, xFin, yFin;
            if(startPoint.getX() < stopPoint.getX()){
                // On veut sélectionner de gauche à droite
                xDep = (int) startPoint.getX();
                xFin = (int) stopPoint.getX();
            }else{
                // On veut sélectionner de droite à gauche
                xDep = (int) stopPoint.getX();
                xFin = (int) startPoint.getX();
            }

            if(startPoint.getY() < stopPoint.getY()){
                // On veut sélectionner de haut en bas
                yDep = (int) startPoint.getY();
                yFin = (int) stopPoint.getY();
            }else{
                // On veut sélectionner de bas en haut
                yDep = (int) stopPoint.getY();
                yFin = (int) startPoint.getY();
            }

            g.setColor(outer);
            g.drawRect(xDep, yDep, xFin-xDep+16, yFin-yDep+16);
            g.setColor(inner);
            g.drawRect(xDep+1, yDep+1, xFin-xDep+14, yFin-yDep+14);
            g.setColor(outer);
            g.drawRect(xDep+2, yDep+2, xFin-xDep+12, yFin-yDep+12);
        }
    }

    /**
     * Méthode privée d'affichage des tuiles non mappées
     * @param g le Graphics concerné
     * @author Drakulo
     */
    private void showUnmappedTiles(Graphics g){
        for(int j = 0; j < currentTile.getHeight(null); j+=16){
            for(int i = 0; i < currentTile.getWidth(null); i+=16){
                if(bridge.getMotifParPoint(tileName, i, j) < 0){
                    g.setColor(Color.red);

                    // Lignes obliques montantes de gauche à droite (/)
                    g.drawLine(i+4, j, i, j+4);
                    g.drawLine(i+8, j, i, j+8);
                    g.drawLine(i+12, j, i, j+12);
                    g.drawLine(i+16, j, i, j+16);
                    g.drawLine(i+16, j+4, i+4, j+16);
                    g.drawLine(i+16, j+8, i+8, j+16);
                    g.drawLine(i+16, j+12, i+12, j+16);

                    // Lignes obliques descendantes de gauche à droite (\)
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
     * Méthode privée d'affichage de la grille sur le TileSet
     * @param g le Graphics concerné
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
     * Méthode privée de tri des Points de sélection. Le point en haut à gauche devient
     * le point de départ et le point en bas à droite devient le point de fin.
     */
    private void sortPoints(){
        // A partir des points de début et de fin, on recrée deux nouveaux
        // points afin d'avoir le point de début en haut à gauche et le point
        // de fin en bas à droite
        Point temp;
        if(startPoint.x > stopPoint.x){
            if(startPoint.y > stopPoint.y){
                temp = new Point(startPoint.x, startPoint.y);
                startPoint.setLocation(stopPoint.x, stopPoint.y);
                stopPoint.setLocation(temp);
            }else{
                temp = new Point(startPoint.x, stopPoint.y);
                startPoint.setLocation(stopPoint.x, startPoint.y);
                stopPoint.setLocation(temp);
            }
        }else{
            if(startPoint.y > stopPoint.y){
                temp = new Point(stopPoint.x, startPoint.y);
                startPoint.setLocation(startPoint.x, stopPoint.y);
                stopPoint.setLocation(temp);
            }
        }
    }
   
    /**
     * Méthode privée de construction de la sélection
     */
    private void buildSelection(){
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

                c=new Case();
                c.setN_banque(bank);
                c.setN_motif(bridge.getMotifParPoint(tileName, j, i));
                list.add(c);
                // On ne compte la largeur que pour la première ligne
                if(height == 0){
                    width ++;
                }
            }
            height ++;
        }
        currentSelection = new TileSelection(width, height, list);
        MasterFrameManager.setCurrentSelection(currentSelection);
    }
   
    public void buildSelection(int width, int height, List<Case> p_cases) {
           
        currentSelection = new TileSelection(width, height, p_cases);
        MasterFrameManager.getZildoCanvas().setCursorSize(width, height);

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
     * Getter de la sélection courante sur le TileSet
     * @return la sélection courante
     */
    public TileSelection getCurrentSelection() {
        return currentSelection;
    }
}