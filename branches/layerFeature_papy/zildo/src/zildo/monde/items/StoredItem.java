package zildo.monde.items;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import zildo.fwk.ui.UIText;

public class StoredItem {

	public Item item;
	public int price;
	public int quantity;
	
	public StoredItem(Item item, int price, int quantity) {
		this.item = item;
		this.price = price;
		this.quantity = quantity;
	}
	
	@Override
	public String toString() {
		return "["+item.toString()+","+price+","+quantity+"]";
	}
	
	/**
	 * Parse the given string, with removing spaces and end-of-line characters.
	 * @param entry
	 * @return StringList&lt;SellingItem&gt;
	 */
	public static List<StoredItem> fromString(String entry) {
		Scanner scan = new Scanner(entry.replace(" ", "").replace("\n", "")).useDelimiter("\\[+|\\]+,*|,");
		List<StoredItem> items = new ArrayList<StoredItem>();
		
		while (true) {
			Item it = Item.fromStrings(scan.next(), scan.next());
			StoredItem item = new StoredItem(it, scan.nextInt(), scan.nextInt());
			items.add(item);
			
			if (!scan.hasNext()) {
				break;
			}
			scan.next();	// Skip the separating comma before next item
		}
			
		return items;
	}
	
	public void decrements() {
		if (quantity > 0) {
			quantity --;
		}
	}
	public String getName() {
		String str = item.kind.getName();
		str += "\n" + price + " "+UIText.getGameText("money");

		if (quantity != -1) {
			str += " ("+quantity+")";
		}
		return str;
	}
}
