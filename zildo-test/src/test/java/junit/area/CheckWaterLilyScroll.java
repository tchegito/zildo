package junit.area;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import tools.EngineUT;
import zildo.monde.items.Item;
import zildo.monde.items.ItemKind;
import zildo.monde.sprites.Reverse;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.elements.Element;
import zildo.monde.sprites.persos.PersoPlayer;
import zildo.monde.util.Angle;
import zildo.monde.util.Point;
import zildo.monde.util.Vector2f;
import zildo.server.EngineZildo;

@Ignore
@RunWith(Parameterized.class)
public class CheckWaterLilyScroll extends EngineUT {

	@Parameterized.Parameters(name = "{index}: checkWaterLilyScroll(factor={0}, shiftX={1})")
	public static Iterable<Object[]> data() {
		List<Object[]> locations = new ArrayList<>();
		for (int shiftHeroX=10;shiftHeroX<13;shiftHeroX++) {
			for (float speedFactor=0.7f;speedFactor<=0.95;speedFactor += 0.02f) {
				locations.add(new Object[] { speedFactor, shiftHeroX });
			}
		}
		return locations;
	}
	
	float speedFactor;
	int shiftHeroX;
	
	public CheckWaterLilyScroll(float speedFactor, int shiftHeroX) {
		this.speedFactor = speedFactor;
		this.shiftHeroX = shiftHeroX;
	}
	
	SpriteEntity waterLily;
	PersoPlayer zildo;
	
	// Initialize a game on map 'igorlily', and place waterlily at given pos, with hero on it
	private void initIgorLily(Point waterLilyLoc) {
		mapUtils.loadMap("igorlily");
		EngineZildo.persoManagement.clearPersos(true);
		EngineZildo.spriteManagement.getNamedEntity("leaf").dying = true;
		
		// Spawn water lily
		waterLily = EngineZildo.spriteManagement.spawnSprite(
				ElementDescription.WATER_LEAF,
				waterLilyLoc.x, waterLilyLoc.y,
				false, Reverse.NOTHING, false); // 113,259
		waterLily.setName("leaf");
		
		zildo = spawnZildo(waterLilyLoc.x-12, waterLilyLoc.y-14);
		zildo.walkTile(false);
	}

	/**
	 * Bugs:
	 * 0.7 - 10
	 * 0.71 - 10
	 * 0.72 - 10
	 * 0.73 - 10
	 * 
	 * 0.7 - 11
	 * (...)
	 * 0.88 -11
	 * 
	 * 0.76 - 12
	 * 0.77 - 12
	 * 0.78 - 12
	 * 0.81 - 12
	 * 0.95 - 12
	 * 
	 * 0.84 - 13
	 * 0.85 - 13
	 * 0.89 - 13
	 * 0.9 - 13
	 * (...)
	 * 0.95 - 13
	 * 
	 */
	
	/** Issue 159 **/
	@Test
	public void npeOnceAgainOnLeafWithDoubleMapScroll() {
		initIgorLily(new Point(977, 267));
		Element wl = waterLily.getMover().getPlaceHolder();
		wl.x = 977.3744f;
		wl.y = 267.75107f;
		
		zildo.walkTile(false);
		
		// Wait end of scripts
		waitEndOfScripting();
		wl.vx = -0.14947365f;
		wl.vy = 0.12370436f;
 
		// v*0.96 et x -2.1 ==> 0
		// v*0.95 et x -2.1 ==> 0.1019355
		// v*0.9  et x -2.1 ==> 0.116020
		// v*0.85 et x -2.1 ==> 0.128090
		// v*0.95 et x +4   ==> 0.2785211
		// v*0.95 et x + 5  ==> 0.305170
		// v*0.95 et x + 11 ==> 0.4819107
		// v*0.95 et x + 12 ==> 0.5121 ==> crash!!! 
		wl.vx *=speedFactor;
		wl.vy *=speedFactor;
		
		zildo.setPos(new Vector2f(967.1378, 251.79767));
		zildo.x += shiftHeroX;
		zildo.walkTile(false);

		
		zildo.setWeapon(new Item(ItemKind.SWORD));
		Assert.assertTrue(zildo.isOnPlatform());
		zildo.setAngle(Angle.OUEST);
		zildo.attack();
		try {
			renderFrames(50*2*2*8);
		} catch (NullPointerException e) {
			System.out.println("Npe for coeff !!!");
		}
		waitEndOfScroll();
		mapUtils.assertCurrent("igorvillage");
	}

}
