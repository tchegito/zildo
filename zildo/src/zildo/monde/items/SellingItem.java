package zildo.monde.items;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SellingItem {

	public Item item;
	public int price;
	public int quantity;
	
	public SellingItem(Item item, int price, int quantity) {
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
	 * @return List&lt;SellingItem&gt;
	 */
	public static List<SellingItem> fromString(String entry) {
		Scanner scan = new Scanner(entry.replace(" ", "").replace("\n", "")).useDelimiter("\\[+|\\]+,*|,");
		List<SellingItem> items = new ArrayList<SellingItem>();
		
		while (true) {
			Item it = Item.fromStrings(scan.next(), scan.next());
			SellingItem item = new SellingItem(it, scan.nextInt(), scan.nextInt());
			items.add(item);
			
			if (!scan.hasNext()) {
				break;
			}
			scan.next();	// Skip the separating comma before next item
		}
			
		return items;
	}
}
