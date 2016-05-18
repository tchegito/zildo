package tools;

import org.junit.Assert;
import org.junit.Test;

import tools.annotations.InfoPersos;
import tools.annotations.SpyHero;
import tools.annotations.SpyMapManagement;

public class AnnotationsTest extends EngineUT {

	@Test @SpyHero
	public void testSpyHero() {
		Assert.assertTrue("we should have spied hero !", spyHero);
		Assert.assertFalse("We shouldn't have spy map management !", spyMapManagement);
		Assert.assertFalse("We shouldn't have debug on characters !", debugInfosPersos);
	}
	
	@Test
	public void testNoSpyHero() {
		Assert.assertFalse("We shouldn't have spy hero !", spyHero);
		Assert.assertFalse("We shouldn't have spy map management !", spyMapManagement);
		Assert.assertFalse("We shouldn't have debug on characters !", debugInfosPersos);
	}
	
	@Test @SpyMapManagement
	public void testSpyMapManagement() {
		Assert.assertFalse("We shouldn't have spy hero !", spyHero);
		Assert.assertTrue("We should have spy map management !", spyMapManagement);
		Assert.assertFalse("We shouldn't have debug on characters !", debugInfosPersos);
	}
	
	@Test @SpyMapManagement @InfoPersos
	public void testCombo() {
		Assert.assertFalse("We shouldn't have spy hero !", spyHero);
		Assert.assertTrue("We should have spy map management !", spyMapManagement);
		Assert.assertTrue("We should have debug on characters !", debugInfosPersos);
	}
}
