package junit.perso;

import org.junit.Assert;
import org.junit.Test;

import testable.TestableIdGenerator;
import tools.EngineUT;
import zildo.fwk.db.Identified;
import zildo.fwk.script.context.SpriteEntityContext;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.PersoPlayer;
import zildo.server.EngineZildo;

/** Check that IdGenerator and SpriteEntities currently declared matches.
 * 
 * @author Tchegito
 *
 */
public class CheckSpawnOverflow extends EngineUT {

	TestableIdGenerator seGenerator;
	
	@Test 
	public void sewer() {
		System.gc();
		System.runFinalization();
		retrieveCounter();

		countEntities();
		mapUtils.loadMap("dragon");
		countEntities();
		PersoPlayer zildo = spawnZildo(279,280);
		countEntities();
		zildo.setPv(40);
		waitEndOfScripting();
		
		countEntities();
		Perso dragon = EngineZildo.persoManagement.getNamedPerso("dragon");
		Assert.assertNotNull(dragon);
		SpriteEntityContext context = new SpriteEntityContext(dragon);
		EngineZildo.scriptManagement.runPersoAction(dragon, "bossDragon", context);
		
		for (int i=0;i<200;i++) {
			zildo.setPv(40);
			renderFrames(150);
			System.out.println(countEntities()+ " hero: "+zildo.getPv()+"HP");
		}
	}
	
	/** Compare spawned entities, and availability in related IdGenerator buffer. **/
	private int countEntities() {
		int countEntity = EngineZildo.spriteManagement.getSpriteEntities(null).size();
		int idAvailable = seGenerator.getAvailable();
		int total = idAvailable+countEntity;
		System.out.println("available: "+idAvailable+" entities: "+countEntity+" total: "+total);
		// Reduce the condition because on Jenkins total equals 513 at some time (no explanation yet)
		Assert.assertEquals(Identified.DEFAULT_MAX_ID, total);
		return countEntity;		
	}
	
	/** Create a mocked IdGenerator, to get access to the availability buffer **/
	private void retrieveCounter() {
		new SpriteEntity() {
			protected void initializeId() {
				super.initializeId();
				seGenerator = new TestableIdGenerator(Identified.DEFAULT_MAX_ID);
				idsCounter.put(SpriteEntity.class, seGenerator);
			}
		}.initializeId();
		Assert.assertNotNull(seGenerator);
	}
}
