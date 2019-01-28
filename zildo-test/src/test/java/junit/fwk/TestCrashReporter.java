package junit.fwk;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import tools.EngineUT;
import zildo.client.gui.menu.SaveGameMenu;
import zildo.fwk.net.www.CrashReporter;
import zildo.server.EngineZildo;

public class TestCrashReporter extends EngineUT {

	// This test doesn't need EngineUT, but that's better to have all crash-related tests in the same class
	@Test
	public void ensureCompleteStack() {
		Throwable t = new RuntimeException("That Zorglub should have worked !");
		CrashReporter cr = new CrashReporter(t);
		Assert.assertTrue(cr.getMessage().contains("Zorglub"));
		String fullClassName = this.getClass().getCanonicalName();
		System.out.println(cr.getMessage());
		Assert.assertTrue("Full class name ("+fullClassName+" isn't found in stack trace !", cr.getMessage().contains(fullClassName));
	}
	
	@Test
	public void ensureContext() {
		String QUEST_NAME = "nonexistentQuest";
		CrashReporter cr = new CrashReporter(new IllegalArgumentException());
		EngineZildo.scriptManagement.accomplishQuest(QUEST_NAME, false);
		cr.addContext();
		Assert.assertTrue(cr.getMessage().contains(QUEST_NAME));
	}
	
	@Test
	public void checkErrorInGeneration() {
		Throwable t = new RuntimeException("That Zorglub should have worked !");
		CrashReporter cr = new CrashReporter(t);
		EngineZildo.scriptManagement = null;
		cr.addContext();
		System.out.println(cr.getMessage());
		Assert.assertTrue(cr.getMessage().contains(CrashReporter.class.getCanonicalName()));
	}
	
	@Test @Ignore	// This test works, but may overwhelm the database !
	public void transmitReport() {
		mapUtils.loadMap("polakyg");
		Throwable t = new RuntimeException("I want that Olivioh message !");
		new CrashReporter(t).addContext().sendReport();
	}
	
	/** Issue 171: exception when anti-crash feature tries to save although there isn't any hero present **/
	@Test
	public void saveOnCrash() {
		waitEndOfScripting();
		SaveGameMenu.saveOnCrash();
		// If we don't get an NPE at this point, we're good !
	}
}
