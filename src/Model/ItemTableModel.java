package Model;

/**
 * Product item, price, and link for object creation
 * @author alvinkwan
 *
 */
public class ItemTableModel {

	String item, price, link;

	public ItemTableModel(String item, String price, String link) {
		super();
		this.item = item;
		this.price = price;
		this.link = link;
	}

	public ItemTableModel(String item, String price) {
		super();
		this.item = item;
		this.price = price;
	}

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}
	
	
}
