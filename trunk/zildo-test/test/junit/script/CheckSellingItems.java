package junit.script;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import zildo.fwk.ZUtils;
import zildo.monde.items.Item;
import zildo.monde.items.ItemKind;
import zildo.monde.items.StoredItem;

public class CheckSellingItems {

	@Test
	public void objetSimpleSerialize() {
		StoredItem item = new StoredItem(new Item(ItemKind.BOW, 1), 60, 2);
		
		Assert.assertEquals("[[BOW,1],60,2]", item.toString());
	}
	
	@Test
	public void objetSimpleDeserialize() {
		String entry = "[[SWORD,4],30,8]";
		
		StoredItem item = StoredItem.fromString(entry).get(0);
		
		Assert.assertSame(item.item.kind, ItemKind.SWORD);
		Assert.assertSame(item.item.level, 4);
		Assert.assertSame(item.price, 30);
		Assert.assertSame(item.quantity, 8);
	}
	
	@Test
	public void listeObjetsDeserialize() {
		String entry = "[[DYNAMITE,8],15,2], [[ROCK_BAG,3],12,7]";
		
		List<StoredItem> items = StoredItem.fromString(entry);
		
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
		
		List<StoredItem> items = StoredItem.fromString(entry);
		
		Assert.assertSame(3, items.size());
		
		String toStr = ZUtils.listToString(items);
		
		Assert.assertEquals(toStr, entry.replaceAll(" ", "").replaceAll("\n", ""));
	}
}
