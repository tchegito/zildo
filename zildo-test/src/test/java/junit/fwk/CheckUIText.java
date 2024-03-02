package junit.fwk;

import org.junit.Assert;
import org.junit.Test;

import zildo.fwk.ui.UIText;

public class CheckUIText {

	@Test
	public void checkHeroNameContainingSpace() {
		UIText.setCharacterName(" space ");
		String sentence = UIText.getGameText("necklace.action");
		System.out.println(sentence);
		Assert.assertFalse(sentence.contains("  "));
		Assert.assertFalse(sentence.contains(" ."));
	}
}
