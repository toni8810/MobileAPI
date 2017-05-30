package mobile.rest.api.domain;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;


@Entity
@Table(name="group_shopping_list")
public class GroupShoppingList {
	
	@EmbeddedId
	private GroupShoppingListCompositeKey gslck;

	public GroupShoppingListCompositeKey getGslck() {
		return gslck;
	}

	public void setGslck(GroupShoppingListCompositeKey gslck) {
		this.gslck = gslck;
	}

	@Override
	public String toString() {
		return "GroupShoppingList [gslck=" + gslck + "]";
	}

	
	
	
	
	
}
