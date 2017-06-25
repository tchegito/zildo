package junit.perso;

import org.junit.Assert;
import org.junit.Test;

import testable.TestableIdGenerator;
import tools.EngineUT;
import zildo.fwk.collection.IdGenerator;
import zildo.fwk.db.Identified;
import zildo.fwk.script.context.SpriteEntityContext;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.PersoPlayer;
import zildo.server.EngineZildo;

public class CheckSpawnOverflow extends EngineUT {

	TestableIdGenerator seGenerator;
	
	@Test 
	public void sewer() {
		mapUtils.loadMap("dragon");
		PersoPlayer zildo = spawnZildo(279,280);
		zildo.setPv(40);
		waitEndOfScripting();
		
		retrieveCounter();
		
		int c1 = countEntities();
		Perso dragon = EngineZildo.persoManagement.getNamedPerso("dragon");
		Assert.assertNotNull(dragon);
		SpriteEntityContext context = new SpriteEntityContext(dragon);
		EngineZildo.scriptManagement.runPersoAction(dragon, "bossDragon", context);
		/*
		for (int i=0;i<200;i++) {
			zildo.setPv(40);
			renderFrames(50);
			System.out.println(countEntities()+ " hero: "+zildo.getPv()+"HP");
		} */
	}
	
	private int countEntities() {
		int countEntity = EngineZildo.spriteManagement.getSpriteEntities(null).size();
		int idAvailable = seGenerator.getAvailable();
		System.out.println("available: "+idAvailable+" total: "+(idAvailable+countEntity));
		return countEntity;		
	}
	
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
