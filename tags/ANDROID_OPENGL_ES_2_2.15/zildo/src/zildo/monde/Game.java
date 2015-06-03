/**
 * The Land of Alembrum
 * Copyright (C) 2006-2013 Evariste Boussaton
 * 
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package zildo.monde;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.BufferUnderflowException;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

import zildo.fwk.file.EasyBuffering;
import zildo.fwk.file.EasySerializable;
import zildo.fwk.script.xml.element.AdventureElement;
import zildo.fwk.script.xml.element.QuestElement;
import zildo.monde.items.Item;
import zildo.monde.items.ItemKind;
import zildo.monde.map.Area;
import zildo.monde.sprites.desc.ZildoOutfit;
import zildo.monde.sprites.persos.PersoPlayer;
import zildo.monde.util.Angle;
import zildo.monde.util.Point;
import zildo.server.EngineZildo;

/**
 * Modelizes a saved game, or start game. For now, it describes:<br/>
 * -simple game in a given map <br/>
 * -minimum management for map editing (ZEditor) <br/>
 * -deathmatch/cooperative nature<br/>
 * -current quest diary<br/>
 * @author tchegito
 */
public class Game implements EasySerializable {

	public boolean brandNew;	// TRUE if this game is a new game from the beginning (so, with intro)
    public boolean editing;
    public boolean multiPlayer;
    public boolean deathmatch; // Defines the game rules
    public String mapName;
    public String heroName;
    private int timeSpent;	// Number of seconds spent into the game
    
    private Date startPlay;
    
    public Game(String p_mapName, boolean p_editing) {
        mapName = p_mapName;
        editing = p_editing;
        multiPlayer = false;
        brandNew = true;
    }

    public Game(String p_mapName, String p_playerName) {
    	this(p_mapName, false);
    	heroName = p_playerName;
    	timeSpent = 0;
    	startPlay = new Date();
    }

    public Game(boolean p_editing) {
    	this(null, p_editing);
    }
    
	public void serialize(EasyBuffering p_buffer) {
		p_buffer.getAll().position(0);
		// 1: quest diary
		AdventureElement adventure=EngineZildo.scriptManagement.getAdventure();
		List<QuestElement> quests=adventure.getQuests();
		int nbQuest = 0;	// First pass to count the done quests
		for (QuestElement quest : quests) {
			if (quest.done) {
				nbQuest++;
			}
		}
		p_buffer.put(nbQuest);
		for (QuestElement quest : quests) {
			if (quest.done) {
				p_buffer.put(quest.name);
				p_buffer.put(quest.done);
			}
		}
		
		// 2: zildo's information
		PersoPlayer zildo=EngineZildo.persoManagement.getZildo();
		p_buffer.put((byte) zildo.getPv());
		p_buffer.put(zildo.getMaxpv() | (zildo.getMoonHalf() << 8));
		p_buffer.put(zildo.getCountArrow());
		p_buffer.put(zildo.getCountBomb());
		p_buffer.put((byte) zildo.getCountKey());
		p_buffer.put(zildo.getMoney());
		String encodedName = null;
		try {
			encodedName = URLEncoder.encode(heroName, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			encodedName = "Zildo";
		}
		p_buffer.put(encodedName);
		p_buffer.put((byte) zildo.getIndexSelection());
		
		// 3: inventory
		List<Item> items=zildo.getInventory();
		p_buffer.put(items.size());
		for (Item item : items) {
			p_buffer.put(item.kind.toString());
			p_buffer.put(item.level);
		}
		
        // 4: map (since 1.096)
        Area area = EngineZildo.mapManagement.getCurrentMap();
        p_buffer.put(area.getName());
        p_buffer.put((int) zildo.getX());
        p_buffer.put((int) zildo.getY());
        
        // 5: time spent
        p_buffer.put(getTimeSpent());
        
        // 6: map start location
        Point loc = EngineZildo.mapManagement.getStartLocation();
        if (loc == null) {	// When zildo has been placed by script (ex: intro)
        	loc = new Point(zildo.getX(), zildo.getY());
        }
        Angle a = EngineZildo.mapManagement.getStartAngle();
        if (a == null) {
        	a = Angle.SUD;
        }
        p_buffer.put(loc.x);
        p_buffer.put(loc.y);
        p_buffer.put((byte) a.value);
        
        // 7: variables
        for (Entry<String, String> entry : EngineZildo.scriptManagement.getVariables().entrySet()) {
        	p_buffer.put(entry.getKey());
        	p_buffer.put(entry.getValue());
        }
        
        // 8: floor
        p_buffer.put((byte) zildo.getFloor());
        
        // Backup quest state to restore if hero dies
        EngineZildo.setBackedUpGame(p_buffer);
	}

	/**
     * Create a game from a saved file. At this point, we assume that EngineZildo is already instantiated and
     * ScriptManagement is empty.
     * @param p_buffer
     * @param p_minimal TRUE=we just want minimal game, to display savegame title (mapname + timeSpent)
     * @return Game
     */
    public static Game deserialize(EasyBuffering p_buffer, boolean p_minimal) {
    	p_buffer.getAll().position(0);
        try {
            // 1: quest diary
            int questNumber = p_buffer.readInt();
            for (int i = 0; i < questNumber; i++) {
                String questName = p_buffer.readString();
                boolean questDone = p_buffer.readBoolean();
                if (questDone && !p_minimal) {
                    EngineZildo.scriptManagement.accomplishQuest(questName, false);
                }
            }
            
            // 2: Zildo
            int pv = p_buffer.readByte();
            int maxPvHeartQuarter = p_buffer.readInt();
            int countArrow = p_buffer.readInt();
            int countBomb = p_buffer.readInt();
            int countKey = p_buffer.readByte();
            int money = p_buffer.readInt();
            PersoPlayer zildo = null;
            List<Item> items = null;
            
            if (!p_minimal) {
                EngineZildo.spawnClient(ZildoOutfit.Zildo);
                zildo = EngineZildo.persoManagement.getZildo();
                zildo.setPv(pv);
	            zildo.setMaxpv(maxPvHeartQuarter & 255);
	            zildo.setMoonHalf(maxPvHeartQuarter >> 8);
	            zildo.setCountArrow(countArrow);
	            zildo.setCountBomb(countBomb);
	            zildo.setCountKey(countKey);
	            zildo.setMoney(money);
	            items = zildo.getInventory();
	            items.clear();
            }
            String heroName = URLDecoder.decode(p_buffer.readString(), "UTF-8");
            heroName = heroName.replaceAll(System.getProperty("line.separator"), "");
            byte indexSel = p_buffer.readByte();
           
            //zildo.setCountBomb(10);
            
            Game game = new Game(null, heroName);
           
            // 3: Inventory
            int itemNumber = p_buffer.readInt();
            for (int i = 0; i < itemNumber; i++) {
                String kind = p_buffer.readString();
                int level = p_buffer.readInt();
                if (zildo != null) {
                	Item item = new Item(ItemKind.fromString(kind), level);
	                items.add(item);
	                if (indexSel == i) {
	                    zildo.setWeapon(item);
	                }
                }
            }

            // 4: map (since 1.096)
            game.mapName = p_buffer.readString();
            Point loc = new Point(p_buffer.readInt(), p_buffer.readInt());
            if (!p_minimal) {
	            zildo.setX(loc.x);
	            zildo.setY(loc.y);
	            zildo.setFloor(1);
	            // Backup quest state to restore if hero dies
	            EngineZildo.setBackedUpGame(p_buffer);
            }
			
            // 5: time spent
            game.timeSpent = p_buffer.readInt();

            Angle a = Angle.NORD;
            if (!p_buffer.eof()) {
	            // 6: map start location
            	loc = new Point(p_buffer.readInt(), p_buffer.readInt());
            	a = Angle.fromInt(p_buffer.readByte());
            }
            
            if (p_minimal) {
            	// We got all we need, so, return from here before the end of file
            	return game;
            }
            
            // 7: variables
            int savePos = 0;
            try {
	            while (!p_buffer.eof()) {
	                savePos = p_buffer.getAll().position();
	            	String key = p_buffer.readString();
	            	String value = p_buffer.readString();
	            	EngineZildo.scriptManagement.getVariables().put(key, value);
	            }
            } catch (BufferUnderflowException e) {
            	// Yeah ! This is ugly, but we got to keep previous savegames, before floor was present
            	// in the game. So if the 'floor' is misinterpreted as a variable, so we get back and read it
            	// at what it is : a floor.
            	p_buffer.getAll().position(savePos);
            }
            
            // 8: floor (added at 2.09)
            if (!p_buffer.eof()) {
            	zildo.setFloor(p_buffer.readByte());
            }
            EngineZildo.mapManagement.setStartLocation(loc, a, zildo.getFloor());
            return game;
        } catch (Exception e) {
        	throw new RuntimeException("Unable to deserialize the game !");
        }
    }
    
    public int getTimeSpent() {
    	// Update time spent
        Date now = new Date();
        long diff = now.getTime() - startPlay.getTime();
        timeSpent += diff / 1000;
        startPlay = now;
        // Return it
    	return timeSpent;
    }
}