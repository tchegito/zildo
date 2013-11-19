package junit.script;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import zildo.monde.items.Item;
import zildo.monde.items.ItemKind;
import zildo.monde.items.SellingItem;

public class CheckSellingItems {

	@Test
	public void objetSimpleSerialize() {
		SellingItem item = new SellingItem(new Item(ItemKind.BOW, 1), 60, 2);
		
		Assert.assertEquals("[[BOW,1],60,2]", item.toString());
	}
	
	@Test
	public void objetSimpleDeserialize() {
		String entry = "[[SWORD,4],30,8]";
		
		SellingItem item = SellingItem.fromString(entry).get(0);
		
		Assert.assertSame(item.item.kind, ItemKind.SWORD);
		Assert.assertSame(item.item.level, 4);
		Assert.assertSame(item.price, 30);
		Assert.assertSame(item.quantity, 8);
	}
	
	@Test
	public void listeObjetsDeserialize() {
		String entry = "[[DYNAMITE,8],15,2], [[ROCK_BAG,3],12,7]";
		
		List<SellingItem> items = SellingItem.fromString(entry);
		
		Assert.assertSame(2, items.size());
	}
	
	@Test
	public void commandeComplete() {
		String entry = 
		""
		+"                    [[SWORD, 1], 100, 2],\n"
		+"                    [[FLASK_RED, 1], 15, -1],\n"
		+"                    [[DYNAMITE, 1], 60, 2]\n"
		+"                   ";
		
		List<SellingItem> items = SellingItem.fromString(entry);
		
		Assert.assertSame(3, items.size());
	}
}
